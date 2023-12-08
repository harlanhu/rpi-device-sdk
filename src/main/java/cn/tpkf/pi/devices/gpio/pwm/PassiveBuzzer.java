package cn.tpkf.pi.devices.gpio.pwm;

import cn.tpkf.pi.enums.BCMEnums;
import cn.tpkf.pi.manager.DeviceManager;
import com.pi4j.io.pwm.PwmPolarity;
import com.pi4j.io.pwm.PwmType;

/**
 * 无源蜂鸣器
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/8
 */
public class PassiveBuzzer extends AbstractPwmDevice {

    protected PassiveBuzzer(DeviceManager deviceManager, String id, String name, BCMEnums address, PwmType pwmType, Number initial, Number shutdown, Number dutyCycle, Integer frequency, PwmPolarity polarity) {
        super(deviceManager, id, name, address, pwmType, initial, shutdown, dutyCycle, frequency, polarity);
    }
}
