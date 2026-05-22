package cn.tpkf.rpi.devices;

import cn.tpkf.rpi.exception.DeviceException;
import cn.tpkf.rpi.manager.DeviceManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/22
 */
@Slf4j
public abstract class AbstractDevice implements Device {

    /**
     * 设备ID
     */
    @Getter
    protected final String id;

    /**
     * 设备名称
     */
    @Getter
    protected final String name;

    /**
     * 设备管理器
     */
    protected final DeviceManager deviceManager;

    /**
     * 设备锁
     */
    protected final ReentrantLock lock;

    /**
     * Represents an abstract device.
     *
     * @param deviceManager the DeviceManager instance
     * @param id the ID of the device
     * @param name the name of the device
     */
    protected AbstractDevice(DeviceManager deviceManager, String id, String name) {
        if (StringUtils.isBlank(id)) {
            throw new DeviceException("Device id must not be blank");
        }
        if (StringUtils.isBlank(name)) {
            throw new DeviceException("Device name must not be blank");
        }
        this.id = id;
        this.name = name;
        this.deviceManager = Objects.requireNonNull(deviceManager, "deviceManager must not be null");
        this.lock = new ReentrantLock();
    }

    @Override
    public void shutdown() {
        // Try to shutdown resources in the context first, but do not fail the caller if shutdown fails
        if (deviceManager.isRunning()) {
            try {
                deviceManager.execute(context -> {
                    context.shutdown(id);
                    return null;
                });
            } catch (Exception e) {
                log.warn("Failed to shutdown device context for id: {}", id, e);
            }
        }
        // Always remove from manager to avoid leaks
        deviceManager.removeDevice(id);
    }
}
