package cn.tpkf.rpi.devices.gpio.digital.out;

import cn.tpkf.rpi.enums.IBCMEnums;
import cn.tpkf.rpi.manager.DeviceManager;
import com.pi4j.io.gpio.digital.DigitalState;

/**
 * LED 灯
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/6
 */
public class Led extends AbstractDoDevice {

    public Led(DeviceManager deviceManager, String id, String name, IBCMEnums address) {
        super(deviceManager, id, name, address, DigitalState.LOW, DigitalState.LOW);
    }
}
