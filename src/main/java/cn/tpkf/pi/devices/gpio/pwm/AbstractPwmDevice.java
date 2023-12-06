package cn.tpkf.pi.devices.gpio.pwm;

import cn.tpkf.pi.devices.gpio.AbstractGpioDevice;
import cn.tpkf.pi.enums.BCMEnums;
import cn.tpkf.pi.manager.DeviceManager;
import com.pi4j.io.pwm.*;
import com.pi4j.plugin.linuxfs.provider.pwm.LinuxFsPwmProvider;
import lombok.Getter;

/**
 * PWM 设备
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/6
 */
public abstract class AbstractPwmDevice extends AbstractGpioDevice {

    protected final Pwm pwm;

    @Getter
    protected final Number initial;

    @Getter
    protected final Number shutdown;

    @Getter
    protected final Number dutyCycle;

    @Getter
    protected final Integer frequency;

    @Getter
    protected final PwmPolarity polarity;

    @Getter
    protected final PwmType pwmType;

    protected AbstractPwmDevice(DeviceManager deviceManager, String id, String name,
                                BCMEnums address, PwmType pwmType, Number initial, Number shutdown,
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
                    .address(address.getVale())
                    .initial(initial)
                    .pwmType(pwmType)
                    .description(getDescription())
                    .dutyCycle(dutyCycle)
                    .frequency(frequency)
                    .polarity(polarity)
                    .shutdown(shutdown)
                    .provider(LinuxFsPwmProvider.class)
                    .build();
            return c.create(config);
        });
    }
}
