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

    private static final SystemInfo systemInfo = new SystemInfo();

    private static CpuTicksInfo cpuTicks;

    private static final ReentrantLock LOCK = new ReentrantLock(true);

    private static final PlatformInfo.CupInfo CPU_INFO = new PlatformInfo.CupInfo();

    private static final PlatformInfo.MemoryInfo MEMORY_INFO = new PlatformInfo.MemoryInfo();

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
                .setMaxFreq(processor.getMaxFreq());
        MEMORY_INFO.setTotalMemory(getTotalMemory());
        long[] oldTicks = getSystemCpuLoadTicks();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            log.error("init calculate system info error: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
        long[] newTicks = getSystemCpuLoadTicks();
        setCpuTicks(oldTicks, newTicks);
    }

    private SystemInfoUtils() {
        //DO NOTHING
    }

    private static void setCpuTicks(long[] oldTicks, long[] newTicks) {
        try {
            if (LOCK.tryLock(2500, TimeUnit.MICROSECONDS)) {
                cpuTicks = new CpuTicksInfo(oldTicks, newTicks);
            }
        } catch (InterruptedException e) {
            log.error("set cpu ticks info error: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            LOCK.unlock();
        }
    }

    private static CpuTicksInfo getCpuTicks() {
        try {
            if (LOCK.tryLock(2500, TimeUnit.MICROSECONDS)) {
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

    public static CentralProcessor getCentralProcess() {
        return systemInfo.getHardware().getProcessor();
    }

    public static HardwareAbstractionLayer getHardware() {
        return systemInfo.getHardware();
    }

    public static long getTotalMemory() {
        GlobalMemory globalMemory = getHardware().getMemory();
        return globalMemory.getTotal();
    }

    public static long getUsedMemory() {
        return getTotalMemory() - getFreeMemory();
    }

    public static long getFreeMemory() {
        return getHardware().getMemory().getAvailable();
    }

    public static long[] getSystemCpuLoadTicks() {
        return getCentralProcess().getSystemCpuLoadTicks();
    }

    public static double getCpuUsageRate() {
        return Objects.requireNonNull(getCpuTicks()).getUsageRate();
    }

    public static double getCpuTemperature() {
        return getHardware().getSensors().getCpuTemperature();
    }

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

        private long[] oldTicks;

        private long[] newTicks;

        private long nice;

        private long iRQ;

        private long softIRQ;

        private long steal;

        private long system;

        private long user;

        private long iOWait;

        private long idle;

        private long total;

        private double sysUsageRate;

        private double userUsageRate;

        private double waitUsageRate;

        private double usageRate;

        public CpuTicksInfo(long[] oldTicks, long[] newTicks) {
            this.nice = newTicks[CentralProcessor.TickType.NICE.getIndex()] - oldTicks[CentralProcessor.TickType.NICE.getIndex()];
            this.iRQ = newTicks[CentralProcessor.TickType.IRQ.getIndex()] - oldTicks[CentralProcessor.TickType.IRQ.getIndex()];
            this.softIRQ = newTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - oldTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
            this.steal = newTicks[CentralProcessor.TickType.STEAL.getIndex()] - oldTicks[CentralProcessor.TickType.STEAL.getIndex()];
            this.system = newTicks[CentralProcessor.TickType.SYSTEM.getIndex()] - oldTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
            this.user = newTicks[CentralProcessor.TickType.USER.getIndex()] - oldTicks[CentralProcessor.TickType.USER.getIndex()];
            this.iOWait = newTicks[CentralProcessor.TickType.IOWAIT.getIndex()] - oldTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
            this.idle = newTicks[CentralProcessor.TickType.IDLE.getIndex()] - oldTicks[CentralProcessor.TickType.IDLE.getIndex()];
            this.total = user + nice + system + idle + iOWait + iRQ + softIRQ + steal;
            this.sysUsageRate = rate(system, total);
            this.userUsageRate = rate(user, total);
            this.waitUsageRate = rate(iOWait, total);
            this.usageRate = rate(idle, total);
        }

        private double rate(long num, long divide) {
            return 1 - (double) num / (double) divide;
        }
    }
}
