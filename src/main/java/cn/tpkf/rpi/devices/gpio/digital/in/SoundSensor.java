package cn.tpkf.rpi.devices.gpio.digital.in;

import cn.tpkf.rpi.enums.IBCMEnums;
import cn.tpkf.rpi.manager.DeviceManager;

/**
 * Digital sound threshold sensor.
 *
 * @author Harlan
 */
public class SoundSensor extends AbstractDigitalSensor {

    public SoundSensor(DeviceManager deviceManager, String id, String name, IBCMEnums address) {
        super(deviceManager, id, name, address);
    }

    public SoundSensor(DeviceManager deviceManager, String id, String name, IBCMEnums address, boolean activeHigh) {
        super(deviceManager, id, name, address, activeHigh);
    }

    public SoundSensor(DeviceManager deviceManager, String id, String name, IBCMEnums address,
                       boolean activeHigh, long debounceMicSec,
                       Runnable onSoundTask, Runnable onQuietTask) {
        super(deviceManager, id, name, address, activeHigh, debounceMicSec, onSoundTask, onQuietTask);
    }

    public boolean isSoundDetected() {
        return isActive();
    }
}
