package cn.tpkf.pi.devices.gpio.w1;

import cn.tpkf.pi.devices.gpio.AbstractGpioDevice;
import cn.tpkf.pi.enums.IBCMEnums;
import cn.tpkf.pi.manager.DeviceManager;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalInputProvider;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutputProvider;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * One Wire Device
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/14
 */
@Slf4j
public abstract class AbstractOneWireDevice extends AbstractGpioDevice {

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
                                    DigitalState initial, DigitalState shutdown, PullResistance pull) {
        super(deviceManager, id, name, address);
        if (DigitalState.LOW.equals(shutdown)) {
            onState = DigitalState.HIGH;
            offState = DigitalState.LOW;
        } else {
            onState = DigitalState.LOW;
            offState = DigitalState.HIGH;
        }
        digitalOutput = deviceManager.execute(c -> {
            DigitalOutputConfig config = DigitalOutputConfigBuilder.newInstance(c)
                    .id(id + "-OUT")
                    .name(name)
                    .address(address.getValue())
                    .initial(initial)
                    .onState(onState)
                    .shutdown(shutdown)
                    .provider(PiGpioDigitalOutputProvider.class)
                    .build();
            return c.create(config);
        });
        digitalInput = deviceManager.execute(c -> {
            DigitalInputConfig config = DigitalInputConfigBuilder.newInstance(c)
                    .id(id + "-IN")
                    .name(name)
                    .address(address.getValue())
                    .pull(pull)
                    .provider(PiGpioDigitalInputProvider.class)
                    .build();
            return c.create(config);
        });
        digitalOutput.off();
        deviceManager.addDevice(this);
    }

    protected boolean isHigh() {
        return digitalInput.isHigh();
    }

    protected boolean isLow() {
        return digitalInput.isLow();
    }

    @Override
    public void shutdown() {
        deviceManager.execute(context -> {
            digitalOutput.shutdown(context);
            digitalInput.shutdown(context);
            return null;
        });
        deviceManager.removeDevice(id);
    }
}
