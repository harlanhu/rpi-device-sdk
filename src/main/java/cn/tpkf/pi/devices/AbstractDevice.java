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

    /**
     * 设备锁
     */
    protected final ReentrantLock lock;

    protected AbstractDevice() {
        this.lock = new ReentrantLock();
    }
}
