package cn.tpkf.rpi.devices.gpio.digital.in;

import cn.tpkf.rpi.enums.IBCMEnums;
import cn.tpkf.rpi.manager.DeviceManager;

/**
 * SW-420 compatible digital vibration sensor.
 *
 * @author Harlan
 */
public class VibrationSensor extends AbstractDigitalSensor {

    public VibrationSensor(DeviceManager deviceManager, String id, String name, IBCMEnums address) {
        super(deviceManager, id, name, address);
    }

    public VibrationSensor(DeviceManager deviceManager, String id, String name, IBCMEnums address, boolean activeHigh) {
        super(deviceManager, id, name, address, activeHigh);
    }

    public VibrationSensor(DeviceManager deviceManager, String id, String name, IBCMEnums address,
                           boolean activeHigh, long debounceMicSec,
                           Runnable onVibrationTask, Runnable onStableTask) {
        super(deviceManager, id, name, address, activeHigh, debounceMicSec, onVibrationTask, onStableTask);
    }

    public boolean isVibrationDetected() {
        return isActive();
    }
}
