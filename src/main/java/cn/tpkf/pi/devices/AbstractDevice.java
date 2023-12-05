package cn.tpkf.pi.devices;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/22
 */
@Data
@AllArgsConstructor
public abstract class AbstractDevice implements Device {

    protected String id;

    protected String name;

    /**
     * 设备锁
     */
    protected final ReentrantLock lock;

    protected AbstractDevice(String id, String name) {
        this.id = id;
        this.name = name;
        this.lock = new ReentrantLock();
    }

    @Override
    public <T> T execute(DeviceCommand<T> command) {
        // 获取锁
        lock.lock();
        try {
            // 执行命令
            return command.execute();
        } finally {
            // 释放锁
            lock.unlock();
        }
    }
}
