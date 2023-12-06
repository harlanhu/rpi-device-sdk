package cn.tpkf.pi.manager;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.tpkf.pi.devices.Device;
import cn.tpkf.pi.exception.DeviceManagerException;
import cn.tpkf.pi.pojo.PlatformInfo;
import cn.tpkf.pi.utils.SystemInfoUtils;
import com.pi4j.common.Descriptor;
import com.pi4j.context.Context;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/21
 */
@Slf4j
@AllArgsConstructor
public class DeviceManager {

    /**
     * 上下文
     */
    private final Context context;

    /**
     * 获取锁超时时间
     */
    private final Long timeOutMillis;

    /**
     * 设备锁
     */
    private final ReentrantLock lock;

    private Map<String, Device> devices = new ConcurrentHashMap<>();

    public DeviceManager(Context context) {
        this(context, 2000L);
    }

    public DeviceManager(Context context, Long timeOutMillis) {
        this.context = context;
        this.timeOutMillis = timeOutMillis;
        this.lock = new ReentrantLock();
    }

    /**
     * 打印横幅
     */
    public void printBanner() {
        // 读取横幅文本
        String banner = ResourceUtil.readUtf8Str("static/banner.txt");
        // 获取当前上下文的平台描述
        Descriptor describe = context.platform().describe();
        // 打印横幅和平台描述信息
        log.info("\n{}\nId: {}\nName: {}\nCategory: {}\nQuantity: {}\nParent: {}\nValue: {}\n",
                banner, describe.id(), describe.name(), describe.category(), describe.quantity(), describe.parent(),
                describe.value());
    }

    /**
     * 执行ContextCommand命令
     *
     * @param command 要执行的ContextCommand命令
     * @return 执行结果，成功返回true，失败返回false
     */
    public <T> T execute(ContextCommand<T> command) {
        if (!isRunning()) {
            throw new DeviceManagerException("Context is not running");
        }
        try {
            // 尝试获取锁
            boolean isLocked = lock.tryLock(timeOutMillis, TimeUnit.MILLISECONDS);
            if (isLocked) {
                // 获取锁后执行命令
                return command.execute(context);
            }
            throw new DeviceManagerException("Acquire lock timeout");
        } catch (InterruptedException e) {
            // 被中断时设置当前线程状态为中断，并抛出DeviceManagerException异常
            Thread.currentThread().interrupt();
            throw new DeviceManagerException("Fail to acquire lock", e);
        } finally {
            // 如果当前线程持有锁，则释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 添加设备
     * @param device 设备
     */
    public void addDevice(Device device) {
        if (StringUtils.isBlank(device.getId())) {
            throw new DeviceManagerException("Device id or name is blank");
        }
        devices.put(device.getId(), device);
    }

    /**
     * 移除设备
     * @param id 设备id
     */
    public void removeDevice(String id) {
        devices.remove(id);
    }

    /**
     * 判断应用程序是否正在运行中
     *
     * @return 如果应用程序正在运行中则返回true，否则返回false
     */
    public boolean isRunning() {
        return Objects.nonNull(context) && !context.isShutdown();
    }

    /**
     * 关闭方法
     *
     * @return 关闭状态，如果成功返回true，否则返回false
     */
    public boolean shutdown() {
        if (isRunning()) {
            context.shutdown();
        }
        return true;
    }

    public PlatformInfo getPlatformInfo() {
        return SystemInfoUtils.getPlatformInfo();
    }
}
