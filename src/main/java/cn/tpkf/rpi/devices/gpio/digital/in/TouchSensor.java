package cn.tpkf.rpi.devices.gpio.digital.in;

import cn.tpkf.rpi.enums.IBCMEnums;
import cn.tpkf.rpi.manager.DeviceManager;

/**
 * TTP223 compatible digital touch sensor.
 *
 * @author Harlan
 */
public class TouchSensor extends AbstractDigitalSensor {

    public TouchSensor(DeviceManager deviceManager, String id, String name, IBCMEnums address) {
        super(deviceManager, id, name, address);
    }

    public TouchSensor(DeviceManager deviceManager, String id, String name, IBCMEnums address, boolean activeHigh) {
        super(deviceManager, id, name, address, activeHigh);
    }

    public TouchSensor(DeviceManager deviceManager, String id, String name, IBCMEnums address,
                       boolean activeHigh, long debounceMicSec,
                       Runnable onTouchedTask, Runnable onReleasedTask) {
        super(deviceManager, id, name, address, activeHigh, debounceMicSec, onTouchedTask, onReleasedTask);
    }

    public boolean isTouched() {
        return isActive();
    }
}
