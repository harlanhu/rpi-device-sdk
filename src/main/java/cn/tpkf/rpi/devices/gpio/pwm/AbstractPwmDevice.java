package cn.tpkf.rpi.devices.gpio.pwm;

import cn.tpkf.rpi.devices.gpio.AbstractGpioDevice;
import cn.tpkf.rpi.enums.IBCMEnums;
import cn.tpkf.rpi.manager.DeviceManager;
import com.pi4j.io.pwm.*;
import com.pi4j.plugin.pigpio.provider.pwm.PiGpioPwmProvider;

import lombok.Getter;

/**
 * PWM 设备
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/6
 */
public abstract class AbstractPwmDevice extends AbstractGpioDevice {

    /**
     * The PWM instance.
     */
    protected final Pwm pwm;

    /**
     * The initial state.
     */
    @Getter
    protected final Number initial;

    /**
     * The shutdown state.
     */
    @Getter
    protected final Number shutdown;

    /**
     * The duty cycle.
     */
    @Getter
    protected final Number dutyCycle;

    /**
     * The frequency.
     */
    @Getter
    protected final Integer frequency;

    /**
     * The polarity.
     */
    @Getter
    protected final PwmPolarity polarity;

    /**
     * The PWM type.
     */
    @Getter
    protected final PwmType pwmType;

    /**
     * Creates an instance of the AbstractPwmDevice class.
     * @param deviceManager The DeviceManager instance.
     * @param id The ID of the device.
     * @param name The name of the device.
     * @param address The IBCMEnums address of the device.
     * @param pwmType The PWM type.
     * @param initial The initial state.
     * @param shutdown The shutdown state.
     * @param dutyCycle The duty cycle.
     * @param frequency The frequency.
     * @param polarity The polarity.
     */
    protected AbstractPwmDevice(DeviceManager deviceManager, String id, String name,
                                IBCMEnums address, PwmType pwmType, Number initial, Number shutdown,
                                Number dutyCycle, Integer frequency, PwmPolarity polarity) {
        super(deviceManager, id, name, address);
        this.initial = initial;
        this.shutdown = shutdown;
        this.dutyCycle = dutyCycle;
        this.frequency = frequency;
        this.polarity = polarity;
        this.pwmType = pwmType;
        this.pwm = deviceManager.execute(c -> {
            PwmConfig config = PwmConfigBuilder.newInstance(c)
                    .id(id)
                    .name(name)
                    .address(address.getValue())
                    .initial(initial)
                    .pwmType(pwmType)
                    .description(getDescription())
                    .dutyCycle(dutyCycle)
                    .frequency(frequency)
                    .polarity(polarity)
                    .shutdown(shutdown)
                    .provider(PiGpioPwmProvider.class)
                    .build();
            return c.create(config);
        });
        deviceManager.addDevice(this);
    }

    /**
     * on
     *
     * @param dutyCycle The duty cycle.
     * @param frequency The frequency.
     */
    public void on(Number dutyCycle, int frequency) {
        try {
            lock.lock();
            pwm.on(dutyCycle, frequency);
        } finally {
            lock.unlock();
        }
    }

    /**
     * on
     *
     * @param frequency The frequency.
     */
    public void on(int frequency) {
        try {
            lock.lock();
            pwm.on(frequency);
        } finally {
            lock.unlock();
        }
    }

    /**
     * off
     */
    public void off() {
        try {
            lock.lock();
            pwm.off();
        } finally {
            lock.unlock();
        }
    }

    /**
     * isOn
     *
     * @return true if the PWM is on.
     */
    public boolean isOn() {
        return pwm.isOn();
    }

    /**
     * isOff
     *
     * @return true if the PWM is off.
     */
    public boolean isOff() {
        return pwm.isOff();
    }
}
