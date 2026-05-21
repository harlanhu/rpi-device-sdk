package cn.tpkf.rpi.devices.gpio.digital.in;

import cn.tpkf.rpi.enums.IBCMEnums;
import cn.tpkf.rpi.manager.DeviceManager;

/**
 * Digital flame sensor.
 *
 * @author Harlan
 */
public class FlameSensor extends AbstractDigitalSensor {

    public FlameSensor(DeviceManager deviceManager, String id, String name, IBCMEnums address) {
        super(deviceManager, id, name, address);
    }

    public FlameSensor(DeviceManager deviceManager, String id, String name, IBCMEnums address, boolean activeHigh) {
        super(deviceManager, id, name, address, activeHigh);
    }

    public FlameSensor(DeviceManager deviceManager, String id, String name, IBCMEnums address,
                       boolean activeHigh, long debounceMicSec,
                       Runnable onFlameTask, Runnable onClearTask) {
        super(deviceManager, id, name, address, activeHigh, debounceMicSec, onFlameTask, onClearTask);
    }

    public boolean isFlameDetected() {
        return isActive();
    }
}
