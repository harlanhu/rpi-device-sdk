package cn.tpkf.pi.devices.gpio.w1;

import cn.tpkf.pi.devices.gpio.AbstractGpioDevice;
import cn.tpkf.pi.enums.IBCMEnums;
import cn.tpkf.pi.manager.DeviceManager;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutputProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 *
 */
public abstract class AbstractOneWireDevice extends AbstractGpioDevice {

    /**
     * The digitalOutput variable represents a digital output pin on a One Wire device.
     * It is used to set the state of the pin, indicating whether it should be high or low.
     * The value of the pin is set by calling the setValue() method defined in the DigitalOutput class.
     * This pin is associated with the DigitalOutput instance of the device.
     */
    protected final DigitalOutputConfig digitalOutputConfig;

    /**
     * The digital input variable represents a digital input pin on a One Wire device.
     * It is used to read the state of the pin, indicating whether it is high or low.
     * The value of the pin is obtained by calling the getValue() method defined in the IBCMEnums interface.
     * This pin is associated with the DigitalOutput instance of the device.
     */
    protected final DigitalInputConfig digitalInputConfig;

    @Getter
    protected WireState state;

    /**
     * The on state.
     */
    @Getter
    private final DigitalState onState;

    /**
     * The off state.
     */
    @Getter
    private final DigitalState offState;


    protected DigitalOutput digitalOutput;


    protected DigitalInput digitalInput;


    /**
     * Initializes an instance of the AbstractOneWireDevice class.
     *
     * @param deviceManager The DeviceManager instance.
     * @param id            The ID of the device.
     * @param name          The name of the device.
     * @param address       The IBCMEnums address of the device.
     * @param initial       The initial state of the digital output pin.
     * @param shutdown      The shutdown state of the digital output pin.
     */
    protected AbstractOneWireDevice(DeviceManager deviceManager, String id, String name, IBCMEnums address,
                                    DigitalState initial, DigitalState shutdown, WireState initState) {
        super(deviceManager, id, name, address);
        state = initState;
        if (DigitalState.LOW.equals(shutdown)) {
            onState = DigitalState.HIGH;
            offState = DigitalState.LOW;
        } else {
            onState = DigitalState.LOW;
            offState = DigitalState.HIGH;
        }
        digitalOutputConfig = deviceManager.execute(c -> DigitalOutputConfigBuilder.newInstance(c)
                .id(id)
                .name(name)
                .address(address.getValue())
                .initial(initial)
                .onState(onState)
                .shutdown(shutdown)
                .provider(PiGpioDigitalOutputProvider.class)
                .build());
        digitalInputConfig = deviceManager.execute(c -> DigitalInputConfigBuilder.newInstance(c)
                .id(id)
                .name(name)
                .address(address.getValue())
                .provider(PiGpioDigitalOutputProvider.class)
                .build());
        if (WireState.OUT == state) {
            digitalOutput = deviceManager.execute(c -> c.create(digitalOutputConfig));
        } else {
            digitalInput = deviceManager.execute(c -> c.create(digitalInputConfig));
        }
    }

    protected DigitalOutput getOutput() {
        try {
            lock.lock();
            if (WireState.OUT == state) {
                return digitalOutput;
            }
            digitalOutput = deviceManager.execute(c -> {
                digitalInput.shutdown(c);
                return c.create(digitalOutputConfig);
            });
            state = WireState.OUT;
            return digitalOutput;
        } finally {
            lock.unlock();
        }
    }

    protected DigitalInput getInput() {
        try {
            lock.lock();
            if (WireState.IN == state) {
                return digitalInput;
            }
            digitalInput = deviceManager.execute(c -> {
                digitalOutput.shutdown(c);
                return c.create(digitalInputConfig);
            });
            state = WireState.IN;
            return digitalInput;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Turns on the digital output.
     */
    protected void outOn() {
        try {
            lock.lock();
            DigitalOutput output = getOutput();
            output.on();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Turns off the digital output.
     */
    protected void outOff() {
        try {
            lock.lock();
            DigitalOutput output = getOutput();
            output.off();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Executes a pulse on the digital output pin for a given duration in the specified time unit.
     *
     * @param duration    The duration of the pulse.
     * @param timeUnit    The time unit of the duration.
     * @param digitalState The digital state of the pulse.
     */
    protected void outPulse(int duration, TimeUnit timeUnit, DigitalState digitalState) {
        try {
            lock.lock();
            DigitalOutput output = getOutput();
            output.pulse(duration, timeUnit, digitalState);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Executes a pulse on the digital output pin for a given duration in the specified time unit.
     *
     * @param duration    The duration of the pulse.
     * @param timeUnit    The time unit of the duration.
     */
    protected void outPulse(int duration, TimeUnit timeUnit) {
        outPulse(duration, timeUnit, onState);
    }

    protected DigitalState getInState() {
        return getInput().state();
    }

    protected boolean isInHigh() {
        return DigitalState.HIGH.equals(getInState());
    }

    protected boolean isInLow() {
        return DigitalState.LOW.equals(getInState());
    }

    @Getter
    @AllArgsConstructor
    public enum WireState {

        OUT(0),

        IN(1);

        private final Integer value;

    }
}
