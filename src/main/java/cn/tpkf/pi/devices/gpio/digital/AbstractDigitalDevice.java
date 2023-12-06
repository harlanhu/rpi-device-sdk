package cn.tpkf.pi.devices.gpio.digital;

import cn.tpkf.pi.devices.gpio.AbstractGpioDevice;
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
public abstract class AbstractDigitalDevice extends AbstractGpioDevice {

    protected AbstractDigitalDevice(DeviceManager deviceManager, String id, String name, BCMEnums address) {
        super(deviceManager, id, name, address);
    }

    protected abstract DigitalState getState();

    protected abstract boolean isHigh();

    protected abstract boolean isLow();
}
