package cn.tpkf.pi.devices.pwm;

import cn.tpkf.pi.devices.AbstractDevice;
import cn.tpkf.pi.enums.BCMEnums;
import cn.tpkf.pi.manager.DeviceManager;
import com.pi4j.io.pwm.*;
import com.pi4j.plugin.linuxfs.provider.gpio.digital.LinuxFsDigitalInputProvider;
import lombok.Getter;

/**
 * PWM 设备
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/6
 */
@Getter
public abstract class AbstractPwmDevice extends AbstractDevice {

    private final Pwm pwm;

    private final BCMEnums address;

    private final Number initial;

    private final Number shutdown;

    private final Number dutyCycle;

    private final Integer frequency;

    private final PwmPolarity polarity;

    private final PwmType pwmType;

    protected AbstractPwmDevice(DeviceManager deviceManager, String id, String name,
                                BCMEnums address, PwmType pwmType, Number initial, Number shutdown,
                                Number dutyCycle, Integer frequency, PwmPolarity polarity) {
        super(deviceManager, id, name);
        this.address = address;
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
                    .provider(LinuxFsDigitalInputProvider.class)
                    .build();
            return c.create(config);
        });
    }

    @Override
    public String getDescription() {
        return id + "-PWM-BCM " + address.getVale() + "-" + name;
    }
}
