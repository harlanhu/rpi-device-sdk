package cn.tpkf.rpi.devices.gpio.pwm;

import cn.tpkf.rpi.enums.IBCMEnums;
import cn.tpkf.rpi.manager.DeviceManager;
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

    public PassiveBuzzer(DeviceManager deviceManager, String id, String name, IBCMEnums address, Integer frequency) {
        super(deviceManager, id, name, address, PwmType.HARDWARE, 0, 0, 50, frequency, PwmPolarity.NORMAL);
    }
}
