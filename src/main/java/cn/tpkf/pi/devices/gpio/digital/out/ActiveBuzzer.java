package cn.tpkf.pi.devices.gpio.digital.out;

import cn.tpkf.pi.enums.BCMEnums;
import cn.tpkf.pi.manager.DeviceManager;
import com.pi4j.io.gpio.digital.DigitalState;

/**
 * 有源蜂鸣器
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/5
 */
public class ActiveBuzzer extends AbstractDoDevice {

    public ActiveBuzzer(DeviceManager deviceManager, String id, String name, BCMEnums address) {
        super(deviceManager, id, name, address, DigitalState.LOW, DigitalState.LOW);
    }
}
