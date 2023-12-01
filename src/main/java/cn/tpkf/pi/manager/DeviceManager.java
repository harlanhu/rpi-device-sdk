package cn.tpkf.pi.manager;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.tpkf.pi.exception.DeviceManagerException;
import cn.tpkf.pi.pojo.PlatformInfo;
import cn.tpkf.pi.utils.SystemInfoUtils;
import com.alibaba.fastjson2.JSON;
import com.pi4j.common.Descriptor;
import com.pi4j.context.Context;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

import java.time.LocalDateTime;
import java.util.Objects;
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

    private final Context context;

    private final Long timeOutMillis;

    private final ReentrantLock lock;

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
        Descriptor contextDescribe = context.describe();
        HardwareAbstractionLayer hardware = SystemInfoUtils.getHardware();
        CentralProcessor.ProcessorIdentifier processorIdentifier = hardware.getProcessor().getProcessorIdentifier();
        PlatformInfo platformInfo = new PlatformInfo();
        PlatformInfo.CupInfo cupInfo = new PlatformInfo.CupInfo();
        cupInfo.setName(processorIdentifier.getName())
                .setModel(processorIdentifier.getModel())
                .setFamily(processorIdentifier.getFamily())
                .setIdentifier(processorIdentifier.getIdentifier())
                .setVendor(processorIdentifier.getVendor())
                .setMicroArchitecture(processorIdentifier.getMicroarchitecture())
                .setStepping(processorIdentifier.getStepping())
                .setProcessorId(processorIdentifier.getProcessorID())
                .setIs64Bit(processorIdentifier.isCpu64bit())
                .setVendorFreq(processorIdentifier.getVendorFreq())
                .setMaxFreq(hardware.getProcessor().getMaxFreq())
                .setUsageRate(SystemInfoUtils.getCpuUsageRate())
                .setTemperature(SystemInfoUtils.getCpuTemperature());
        platformInfo.setCupInfo(cupInfo);
        PlatformInfo.MemoryInfo memoryInfo = new PlatformInfo.MemoryInfo();
        memoryInfo.setFreeMemory(SystemInfoUtils.getFreeMemory())
                .setTotalMemory(SystemInfoUtils.getTotalMemory())
                .setUsedMemory(SystemInfoUtils.getUsedMemory());
        platformInfo.setMemoryInfo(memoryInfo);
        PlatformInfo.ContextInfo contextInfo = new PlatformInfo.ContextInfo();
        contextInfo.setName(contextDescribe.name())
                .setValue(JSON.toJSONString(contextDescribe.value()))
                .setCategory(contextDescribe.category())
                .setParent(JSON.toJSONString(contextDescribe.parent()))
                .setDescription(contextDescribe.description())
                .setId(contextDescribe.id())
                .setQuantity(contextDescribe.quantity());
        platformInfo.setContextInfo(contextInfo);
        platformInfo.setCurrentTime(LocalDateTime.now());
        return platformInfo;
    }
}
