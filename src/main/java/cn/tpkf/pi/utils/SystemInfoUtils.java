package cn.tpkf.pi.utils;

import cn.tpkf.pi.pojo.PlatformInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/27
 */
@Slf4j
public class SystemInfoUtils {

    private static final SystemInfo SYSTEM_INFO = new SystemInfo();

    private static CpuTicksInfo cpuTicks;

    private static final ReentrantLock LOCK = new ReentrantLock(true);

    private static final PlatformInfo.CupInfo CPU_INFO = new PlatformInfo.CupInfo();

    private static final PlatformInfo.MemoryInfo MEMORY_INFO = new PlatformInfo.MemoryInfo();

    private static final long TIME_OUT = 2500;

    private static final long UPDATE_SEC = 3;

    static {
        CentralProcessor processor = getHardware().getProcessor();
        CentralProcessor.ProcessorIdentifier processorIdentifier = getHardware().getProcessor().getProcessorIdentifier();
        CPU_INFO.setName(processorIdentifier.getName())
                .setProcessorId(processorIdentifier.getProcessorID())
                .setFamily(processorIdentifier.getFamily())
                .setIs64Bit(processorIdentifier.isCpu64bit())
                .setVendor(processorIdentifier.getVendor())
                .setStepping(processorIdentifier.getStepping())
                .setMicroArchitecture(processorIdentifier.getMicroarchitecture())
                .setIdentifier(processorIdentifier.getIdentifier())
                .setModel(processorIdentifier.getModel())
                .setProcessorCount(processor.getLogicalProcessorCount())
                .setMaxFreq(processor.getMaxFreq());
        MEMORY_INFO.setTotalMemory(getTotalMemory());
        long[] oldTicks = getSystemCpuLoadTicks();
        long[] newTicks = getSystemCpuLoadTicks();
        setCpuTicks(oldTicks, newTicks);
        Thread cpuTicksUpdater = new Thread(() -> {
            while (true) {
                try {
                    updateCpuTicks();
                } catch (Exception e) {
                    log.warn("update cpu ticks error: {}", e.getMessage());
                }
            }
        });
        cpuTicksUpdater.setDaemon(true);
        cpuTicksUpdater.start();
    }

    private SystemInfoUtils() {
        //DO NOTHING
    }

    /**
     * 更新CPU信息
     */
    private static void updateCpuTicks() {
        long[] oldTicks = getSystemCpuLoadTicks();
        try {
            TimeUnit.SECONDS.sleep(UPDATE_SEC);
        } catch (InterruptedException e) {
            log.error("init calculate system info error: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
        long[] newTicks = getSystemCpuLoadTicks();
        setCpuTicks(oldTicks, newTicks);
    }

    /**
     * 设置CPU信息
     * @param oldTicks oldTicks
     * @param newTicks newTicks
     */
    private static void setCpuTicks(long[] oldTicks, long[] newTicks) {
        try {
            if (LOCK.tryLock(TIME_OUT, TimeUnit.MICROSECONDS)) {
                cpuTicks = new CpuTicksInfo(oldTicks, newTicks);
            }
        } catch (InterruptedException e) {
            log.error("set cpu ticks info error: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * 获取CPU信息
     *
     * @return CpuTicksInfo
     */
    private static CpuTicksInfo getCpuTicks() {
        try {
            if (LOCK.tryLock(TIME_OUT, TimeUnit.MICROSECONDS)) {
                return cpuTicks;
            }
        } catch (InterruptedException e) {
            log.error("get cpu ticks info error: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            LOCK.unlock();
        }
        return null;
    }

    /**
     * 获取CPU信息
     *
     * @return CentralProcessor
     */
    public static CentralProcessor getCentralProcess() {
        return SYSTEM_INFO.getHardware().getProcessor();
    }

    /**
     * 获取硬件信息
     *
     * @return HardwareAbstractionLayer
     */
    public static HardwareAbstractionLayer getHardware() {
        return SYSTEM_INFO.getHardware();
    }

    /**
     * 获取总内存
     *
     * @return 总内存
     */
    public static long getTotalMemory() {
        GlobalMemory globalMemory = getHardware().getMemory();
        return globalMemory.getTotal();
    }

    /**
     * 获取已使用内存
     *
     * @return 已使用内存
     */
    public static long getUsedMemory() {
        return getTotalMemory() - getFreeMemory();
    }

    /**
     * 获取空闲内存
     *
     * @return 空闲内存
     */
    public static long getFreeMemory() {
        return getHardware().getMemory().getAvailable();
    }

    /**
     * 获取系统CPU负载
     *
     * @return 系统CPU负载
     */
    public static long[] getSystemCpuLoadTicks() {
        return getCentralProcess().getSystemCpuLoadTicks();
    }

    /**
     * 获取CPU使用率
     *
     * @return CPU使用率
     */
    public static double getCpuUsageRate() {
        CpuTicksInfo cpuInfo = getCpuTicks();
        if (Objects.isNull(cpuInfo)) {
            return 0;
        }
        return cpuInfo.getUsageRate();
    }

    /**
     * 获取CPU温度
     *
     * @return CPU温度
     */
    public static double getCpuTemperature() {
        return getHardware().getSensors().getCpuTemperature();
    }

    /**
     * 获取平台信息
     *
     * @return 平台信息
     */
    public static PlatformInfo getPlatformInfo() {
        CPU_INFO.setUsageRate(getCpuUsageRate());
        CPU_INFO.setTemperature(getCpuTemperature());
        MEMORY_INFO.setUsedMemory(getUsedMemory());
        MEMORY_INFO.setFreeMemory(getFreeMemory());
        PlatformInfo platformInfo = new PlatformInfo();
        platformInfo.setCupInfo(CPU_INFO);
        platformInfo.setMemoryInfo(MEMORY_INFO);
        platformInfo.setCurrentTime(LocalDateTime.now());
        return platformInfo;
    }

    @Data
    @NoArgsConstructor
    private static class CpuTicksInfo implements Serializable {

        private long nice;

        private long irq;

        private long softIrq;

        private long steal;

        private long system;

        private long user;

        private long ioWait;

        private long idle;

        private long total;

        private double sysUsageRate;

        private double userUsageRate;

        private double waitUsageRate;

        private double usageRate;

        public CpuTicksInfo(long[] oldTicks, long[] newTicks) {
            this.nice = newTicks[CentralProcessor.TickType.NICE.getIndex()] - oldTicks[CentralProcessor.TickType.NICE.getIndex()];
            this.irq = newTicks[CentralProcessor.TickType.IRQ.getIndex()] - oldTicks[CentralProcessor.TickType.IRQ.getIndex()];
            this.softIrq = newTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - oldTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
            this.steal = newTicks[CentralProcessor.TickType.STEAL.getIndex()] - oldTicks[CentralProcessor.TickType.STEAL.getIndex()];
            this.system = newTicks[CentralProcessor.TickType.SYSTEM.getIndex()] - oldTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
            this.user = newTicks[CentralProcessor.TickType.USER.getIndex()] - oldTicks[CentralProcessor.TickType.USER.getIndex()];
            this.ioWait = newTicks[CentralProcessor.TickType.IOWAIT.getIndex()] - oldTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
            this.idle = newTicks[CentralProcessor.TickType.IDLE.getIndex()] - oldTicks[CentralProcessor.TickType.IDLE.getIndex()];
            this.total = user + nice + system + idle + ioWait + irq + softIrq + steal;
            this.sysUsageRate = divide(system, total);
            this.userUsageRate = divide(user, total);
            this.waitUsageRate = divide(ioWait, total);
            this.usageRate = divide(idle, total);
        }

        private double divide(long num, long divide) {
            BigDecimal result  = new BigDecimal(num).divide(new BigDecimal(divide), 4, RoundingMode.HALF_UP);
            return new BigDecimal(1).subtract(result, new MathContext(4)).doubleValue();
        }
    }
}
