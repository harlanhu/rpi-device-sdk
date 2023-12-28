package cn.tpkf.rpi.devices.gpio.digital.in;

import cn.tpkf.rpi.enums.IBCMEnums;
import cn.tpkf.rpi.manager.DeviceManager;

/**
 * HC-SR501 PIR motion sensor.
 *
 * @author Harlan
 */
public class HCSr501 extends AbstractDIDevice {

    private static final Runnable NOOP = () -> { };

    private final boolean inverted;

    public HCSr501(DeviceManager deviceManager, String id, String name, IBCMEnums address) {
        this(deviceManager, id, name, address, false);
    }

    public HCSr501(DeviceManager deviceManager, String id, String name, IBCMEnums address, boolean inverted) {
        this(deviceManager, id, name, address, inverted, 10_000L, () -> { }, () -> { });
    }

    public HCSr501(DeviceManager deviceManager, String id, String name, IBCMEnums address, boolean inverted,
                   long debounceMicSec, Runnable onMotionTask, Runnable onIdleTask) {
        super(deviceManager, id, name, address, inverted, debounceMicSec,
                inverted ? defaultTask(onIdleTask) : defaultTask(onMotionTask),
                inverted ? defaultTask(onMotionTask) : defaultTask(onIdleTask));
        this.inverted = inverted;
    }

    public boolean isMotionDetected() {
        return inverted ? isLow() : isHigh();
    }

    public boolean isIdle() {
        return !isMotionDetected();
    }

    private static Runnable defaultTask(Runnable task) {
        return task == null ? NOOP : task;
    }
}
