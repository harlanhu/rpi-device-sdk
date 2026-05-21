package cn.tpkf.rpi.devices.gpio.digital.in;

import cn.tpkf.rpi.enums.IBCMEnums;
import cn.tpkf.rpi.manager.DeviceManager;

/**
 * Digital line tracking sensor.
 *
 * @author Harlan
 */
public class LineTrackingSensor extends AbstractDigitalSensor {

    public LineTrackingSensor(DeviceManager deviceManager, String id, String name, IBCMEnums address) {
        this(deviceManager, id, name, address, false);
    }

    public LineTrackingSensor(DeviceManager deviceManager, String id, String name, IBCMEnums address,
                              boolean activeHigh) {
        super(deviceManager, id, name, address, activeHigh);
    }

    public LineTrackingSensor(DeviceManager deviceManager, String id, String name, IBCMEnums address,
                              boolean activeHigh, long debounceMicSec,
                              Runnable onLineTask, Runnable offLineTask) {
        super(deviceManager, id, name, address, activeHigh, debounceMicSec, onLineTask, offLineTask);
    }

    public boolean isLineDetected() {
        return isActive();
    }
}
