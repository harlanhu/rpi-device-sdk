package cn.tpkf.pi.devices.gpio.w1.dht;

import cn.tpkf.pi.enums.IBCMEnums;
import cn.tpkf.pi.manager.DeviceManager;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/18
 */
public class Dht22 extends AbstractDhtDevice {
    protected Dht22(DeviceManager deviceManager, String id, String name, IBCMEnums address) {
        super(deviceManager, id, name, address, 1, 80L, 130L);
    }

    @Override
    protected HumitureInfo processData(long[] data) {
        return null;
    }
}
