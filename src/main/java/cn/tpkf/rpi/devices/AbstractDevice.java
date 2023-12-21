package cn.tpkf.rpi.devices;

import cn.tpkf.rpi.manager.DeviceManager;
import lombok.Getter;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/22
 */
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
        this.id = id;
        this.name = name;
        this.deviceManager = deviceManager;
        this.lock = new ReentrantLock();
    }

    @Override
    public void shutdown() {
        deviceManager.removeDevice(id);
        deviceManager.execute(context -> context.shutdown(id));
    }
}
