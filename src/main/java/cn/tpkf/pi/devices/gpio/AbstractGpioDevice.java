package cn.tpkf.pi.devices.gpio;

import cn.tpkf.pi.devices.AbstractDevice;
import cn.tpkf.pi.enums.BCMEnums;
import cn.tpkf.pi.manager.DeviceManager;
import lombok.Getter;

/**
 * GPIO 设备
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/6
 */
@Getter
public abstract class AbstractGpioDevice extends AbstractDevice {

    /**
     * The BCMEnums address of the device.
     */
    protected final BCMEnums address;

    /**
     * Creates an instance of the AbstractGpioDevice class.
     *
     * @param deviceManager The DeviceManager instance.
     * @param id            The ID of the device.
     * @param name          The name of the device.
     * @param address       The BCMEnums address of the device.
     */
    protected AbstractGpioDevice(DeviceManager deviceManager, String id, String name, BCMEnums address) {
        super(deviceManager, id, name);
        this.address = address;
    }

    @Override
    public String getDescription() {
        return id + "-GPIO-BCM " + address.getVale() + "-" + name;
    }
}
