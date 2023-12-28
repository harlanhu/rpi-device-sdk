package cn.tpkf.rpi.devices.other;

import cn.tpkf.rpi.devices.AbstractDevice;
import cn.tpkf.rpi.enums.IBCMEnums;
import cn.tpkf.rpi.manager.DeviceManager;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.io.pwm.PwmPolarity;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutputProvider;
import com.pi4j.plugin.pigpio.provider.pwm.PiGpioPwmProvider;

import java.util.Objects;

/**
 * L298N single DC motor channel.
 *
 * @author Harlan
 */
public class L298N extends AbstractDevice {

    public static final int DEFAULT_PWM_FREQUENCY = 1000;

    private final IBCMEnums in1Address;

    private final IBCMEnums in2Address;

    private final IBCMEnums enableAddress;

    private final DigitalOutput in1;

    private final DigitalOutput in2;

    private final Pwm enable;

    private int speed;

    public L298N(DeviceManager deviceManager, String id, String name, IBCMEnums in1Address, IBCMEnums in2Address) {
        this(deviceManager, id, name, in1Address, in2Address, null);
    }

    public L298N(DeviceManager deviceManager, String id, String name, IBCMEnums in1Address, IBCMEnums in2Address,
                 IBCMEnums enableAddress) {
        super(deviceManager, id, name);
        this.in1Address = in1Address;
        this.in2Address = in2Address;
        this.enableAddress = enableAddress;
        in1 = createOutput(id + "-IN1", in1Address);
        in2 = createOutput(id + "-IN2", in2Address);
        enable = Objects.isNull(enableAddress) ? null : createPwm(id + "-EN", enableAddress);
        deviceManager.addDevice(this);
    }

    public void forward() {
        setDirection(DigitalState.HIGH, DigitalState.LOW);
    }

    public void backward() {
        setDirection(DigitalState.LOW, DigitalState.HIGH);
    }

    public void stop() {
        setDirection(DigitalState.LOW, DigitalState.LOW);
        if (Objects.nonNull(enable)) {
            enable.off();
        }
    }

    public void brake() {
        setDirection(DigitalState.HIGH, DigitalState.HIGH);
    }

    public void setSpeed(int speed) {
        if (speed < 0 || speed > 100) {
            throw new IllegalArgumentException("Motor speed must be between 0 and 100");
        }
        this.speed = speed;
        if (Objects.nonNull(enable)) {
            if (speed == 0) {
                enable.off();
            } else {
                enable.on(speed, DEFAULT_PWM_FREQUENCY);
            }
        }
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public void shutdown() {
        deviceManager.removeDevice(id);
        deviceManager.execute(context -> {
            context.shutdown(in1.id());
            context.shutdown(in2.id());
            if (Objects.nonNull(enable)) {
                context.shutdown(enable.id());
            }
            return null;
        });
    }

    @Override
    public String getDescription() {
        return id + "-L298N-IN1 " + in1Address.getValue() + " IN2 " + in2Address.getValue() + "-" + name;
    }

    private void setDirection(DigitalState in1State, DigitalState in2State) {
        try {
            lock.lock();
            in1.state(in1State);
            in2.state(in2State);
        } finally {
            lock.unlock();
        }
    }

    private DigitalOutput createOutput(String pinId, IBCMEnums address) {
        return deviceManager.execute(context -> {
            DigitalOutputConfig config = DigitalOutputConfigBuilder.newInstance(context)
                    .id(pinId)
                    .name(name)
                    .address(address.getValue())
                    .initial(DigitalState.LOW)
                    .shutdown(DigitalState.LOW)
                    .description(getDescription())
                    .provider(PiGpioDigitalOutputProvider.class)
                    .build();
            return context.create(config);
        });
    }

    private Pwm createPwm(String pinId, IBCMEnums address) {
        return deviceManager.execute(context -> {
            PwmConfig config = PwmConfigBuilder.newInstance(context)
                    .id(pinId)
                    .name(name)
                    .address(address.getValue())
                    .initial(0)
                    .shutdown(0)
                    .pwmType(PwmType.SOFTWARE)
                    .dutyCycle(0)
                    .frequency(DEFAULT_PWM_FREQUENCY)
                    .polarity(PwmPolarity.NORMAL)
                    .description(getDescription())
                    .provider(PiGpioPwmProvider.class)
                    .build();
            return context.create(config);
        });
    }
}
