package cn.tpkf.pi.devices.gpio.w1.dht;

import cn.tpkf.pi.devices.gpio.w1.AbstractOneWireDevice;
import cn.tpkf.pi.enums.IBCMEnums;
import cn.tpkf.pi.exception.DeviceException;
import cn.tpkf.pi.manager.DeviceManager;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * DHT系列
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/18
 */
@Slf4j
public abstract class AbstractDhtDevice extends AbstractOneWireDevice {

    /**
     * The length of the data.
     */
    private static final Integer DATA_LENGTH = 40;

    /**
     * The timeout duration in nanoseconds for waiting for a signal.
     * This variable represents the duration in nanoseconds that will be used
     * when waiting for a signal. If the signal is not received within this
     * duration, the waiting process will time out.
     * It is recommended to set a reasonable and appropriate value for this
     * timeout to prevent waiting indefinitely.
     */
    private final Long waitSignalTimeOutNanos;

    /**
     * This variable represents the maximum time in nanoseconds to wait for reading data before timing out.
     */
    private final Long readDataTimeOutNanos;

    /**
     * The time duration, in milliseconds, after which the data should be sent.
     * This variable is used to specify how long to wait sending data.
     */
    private static final Long SEND_DATA_TIME_MILLIS = 20L;

    @Getter
    private Double temperature;

    @Getter
    private Double humidity;

    /**
     * A FutureTask for reading data from a source.
     */
    FutureTask<long[]> readDataTask = new FutureTask<>(this::readData);

    /**
     * The interval, in seconds, between two consecutive detections.
     */
    private final Integer detectionInterval;

    /**
     * The time of the last detection.
     */
    private LocalDateTime lastDetectionTime;

    /**
     * Indicates whether to keep the signal high.
     */
    private volatile boolean keepHighSignal = true;

    protected AbstractDhtDevice(DeviceManager deviceManager, String id, String name, IBCMEnums address, Integer detectionInterval,Long waitSignalTimeOutMicros, Long readDataTimeOutMicros) {
        super(deviceManager, id, name, address, DigitalState.HIGH, DigitalState.HIGH, PullResistance.OFF);
        waitSignalTimeOutNanos = waitSignalTimeOutMicros * 1000;
        readDataTimeOutNanos = readDataTimeOutMicros * 1000;
        this.detectionInterval = detectionInterval;
    }

    /**
     * Performs a detection of temperature and humidity using a sensor.
     *
     * @return The updated temperature and humidity information.
     */
    @SneakyThrows
    public HumitureInfo detection() {
        try {
            lock.lock();
            if (Objects.nonNull(lastDetectionTime) && lastDetectionTime.plusSeconds(detectionInterval).isAfter(LocalDateTime.now())) {
                return new HumitureInfo(temperature, humidity);
            }
            if (Objects.isNull(lastDetectionTime)) {
                TimeUnit.SECONDS.sleep(2);
            }
            // 发送开始信号
            digitalOutput.on();
            new Thread(readDataTask).start();
            TimeUnit.MILLISECONDS.sleep(SEND_DATA_TIME_MILLIS);
            while (keepHighSignal) {
                Thread.onSpinWait();
            }
            digitalOutput.off();
            // 读取数据
            long[] data = readDataTask.get();
            // 处理数据
            HumitureInfo humitureInfo = processData(data);
            temperature = humitureInfo.getTemperature();
            humidity = humitureInfo.getHumidity();
            lastDetectionTime = LocalDateTime.now();
            return humitureInfo;
        } finally {
            lock.unlock();
            digitalOutput.off();
        }
    }

    /**
     * Reads data from a device and returns an array of long values.
     *
     * @return an array of long values representing the read data
     * @throws DeviceException if there is a timeout while reading the data
     */
    private long[] readData() {
        try {
            long[] data = new long[40];
            int dataIndex = 0;
            long eachReadEndTime = 0;
            long nanoTimer;
            // 等待主机电平拉高
            keepHighSignal = false;
            awaitSignalOff(SEND_DATA_TIME_MILLIS * 2000000, DigitalState.LOW);
            // 等待低电平
            awaitSignalOff(waitSignalTimeOutNanos , DigitalState.HIGH);
            // 等待高电平
            awaitSignalOff(waitSignalTimeOutNanos, DigitalState.LOW);
            // 等待低电平
            awaitSignalOff(waitSignalTimeOutNanos, DigitalState.HIGH);
            // 开始读取数据
            while (dataIndex < DATA_LENGTH) {
                awaitSignalOff(waitSignalTimeOutNanos, DigitalState.LOW);
                nanoTimer = System.nanoTime();
                long validTime = readDataTimeOutNanos + nanoTimer;
                while (digitalInput.state() == DigitalState.HIGH) {
                    if ((eachReadEndTime = System.nanoTime()) > validTime) {
                        throw new DeviceException("Read data time out: " + (eachReadEndTime - nanoTimer) + " ns, data size: " + dataIndex);
                    }
                }
                data[dataIndex] = (eachReadEndTime - nanoTimer);
                dataIndex ++;
            }
            return data;
        } finally {
            keepHighSignal = true;
        }
    }

    /**
     * Processes the given data and returns a HumitureInfo object.
     *
     * @param data An array of long values representing the data to be processed.
     * @return A HumitureInfo object containing the processed data.
     */
    protected abstract HumitureInfo processData(long[] data);

    /**
     * Waits for the specified amount of time for the given digital state to be off.
     *
     * @param timeOutNanos the timeout period in nanoseconds
     * @param digitalState the desired digital state to wait for
     * @throws DeviceException if the digital state is not off within the timeout period
     */
    private void awaitSignalOff(long timeOutNanos, DigitalState digitalState) {
        long endTime = System.nanoTime() + timeOutNanos;
        while (digitalInput.state() == digitalState) {
            if (System.nanoTime() > endTime) {
                throw new DeviceException("Keep " + digitalState.name() + " signal time out: " + (System.nanoTime() - endTime));
            }
        }
    }

    /**
     * Converts a given number to a binary system based on the provided index.
     *
     * @param num The number to be converted.
     * @param index The index indicating the position in the binary system.
     * @return The converted number in the binary system.
     */
    protected int sysConvert(long num, int index) {
        return (int) (num * Math.pow(2, 7d - index));
    }

    @Data
    @AllArgsConstructor
    public static class HumitureInfo implements Serializable {

        private Double temperature;

        private Double humidity;
    }
}
