package cn.tpkf.pi.devices;

import cn.tpkf.pi.manager.DeviceManager;
import lombok.Getter;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/22
 */
public abstract class AbstractDevice implements Device {

    @Getter
    protected final String id;

    @Getter
    protected final String name;

    protected final DeviceManager deviceManager;

    /**
     * 设备锁
     */
    protected final ReentrantLock lock;

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
