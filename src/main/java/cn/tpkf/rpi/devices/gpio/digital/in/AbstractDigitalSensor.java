package cn.tpkf.rpi.devices.gpio.digital.in;

import cn.tpkf.rpi.enums.IBCMEnums;
import cn.tpkf.rpi.manager.DeviceManager;

/**
 * Base class for simple digital sensors with active/inactive states.
 *
 * @author Harlan
 */
public abstract class AbstractDigitalSensor extends AbstractDIDevice {

    private static final Runnable NOOP = () -> { };

    private final boolean activeHigh;

    protected AbstractDigitalSensor(DeviceManager deviceManager, String id, String name, IBCMEnums address) {
        this(deviceManager, id, name, address, true);
    }

    protected AbstractDigitalSensor(DeviceManager deviceManager, String id, String name, IBCMEnums address,
                                    boolean activeHigh) {
        this(deviceManager, id, name, address, activeHigh, 10_000L, NOOP, NOOP);
    }

    protected AbstractDigitalSensor(DeviceManager deviceManager, String id, String name, IBCMEnums address,
                                    boolean activeHigh, long debounceMicSec,
                                    Runnable onActiveTask, Runnable onInactiveTask) {
        super(deviceManager, id, name, address, !activeHigh, debounceMicSec,
                activeHigh ? defaultTask(onActiveTask) : defaultTask(onInactiveTask),
                activeHigh ? defaultTask(onInactiveTask) : defaultTask(onActiveTask));
        this.activeHigh = activeHigh;
    }

    public boolean isActive() {
        return activeHigh ? isHigh() : isLow();
    }

    public boolean isInactive() {
        return !isActive();
    }

    protected boolean isActiveHigh() {
        return activeHigh;
    }

    private static Runnable defaultTask(Runnable task) {
        return task == null ? NOOP : task;
    }
}
