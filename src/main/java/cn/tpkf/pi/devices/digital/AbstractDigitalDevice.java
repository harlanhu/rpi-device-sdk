package cn.tpkf.pi.devices.digital;

import cn.tpkf.pi.devices.AbstractDevice;
import cn.tpkf.pi.enums.BCMEnums;
import cn.tpkf.pi.manager.DeviceManager;
import com.pi4j.io.gpio.digital.DigitalState;
import lombok.Getter;

/**
 * 数字信号设备
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/6
 */
@Getter
public abstract class AbstractDigitalDevice extends AbstractDevice {

    private final BCMEnums address;

    protected AbstractDigitalDevice(DeviceManager deviceManager, String id, String name, BCMEnums address) {
        super(deviceManager, id, name);
        this.address = address;
    }

    protected abstract DigitalState getState();

    protected abstract boolean isHigh();

    protected abstract boolean isLow();

    @Override
    public String getDescription() {
        return id + "-DO-BCM " + address.getVale() + "-" + name;
    }
}
