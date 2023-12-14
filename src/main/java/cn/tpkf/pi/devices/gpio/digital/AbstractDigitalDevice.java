package cn.tpkf.pi.devices.gpio.digital;

import cn.tpkf.pi.devices.gpio.AbstractGpioDevice;
import cn.tpkf.pi.exception.enums.IBCMEnums;
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
public abstract class AbstractDigitalDevice extends AbstractGpioDevice {

    /**
     * Initializes an instance of the AbstractDigitalDevice class.
     *
     * @param deviceManager The DeviceManager instance.
     * @param id            The ID of the device.
     * @param name          The name of the device.
     * @param address       The IBCMEnums address of the device.
     */
    protected AbstractDigitalDevice(DeviceManager deviceManager, String id, String name, IBCMEnums address) {
        super(deviceManager, id, name, address);
    }

    /**
     * 获取当前状态
     * @return 状态
     */
    protected abstract DigitalState getState();

    /**
     * 是否为高电平
     *
     * @return 是否为高电平
     */
    protected abstract boolean isHigh();

    /**
     * 是否为低电平
     *
     * @return 是否为低电平
     */
    protected abstract boolean isLow();
}
