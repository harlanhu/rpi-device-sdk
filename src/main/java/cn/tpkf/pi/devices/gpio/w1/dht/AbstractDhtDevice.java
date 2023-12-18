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

    private static final Long SEND_DATA_TIME_MILLIS = 20L;

    @Getter
    private Double temperature;

    @Getter
    private Double humidity;

    FutureTask<long[]> readDataTask = new FutureTask<>(this::readData);

    private final Integer detectionInterval;

    private LocalDateTime lastDetectionTime;

    protected AbstractDhtDevice(DeviceManager deviceManager, String id, String name, IBCMEnums address, Integer detectionInterval,Long waitSignalTimeOutMicros, Long readDataTimeOutMicros) {
        super(deviceManager, id, name, address, DigitalState.HIGH, DigitalState.HIGH, PullResistance.OFF, WireState.OUT);
        waitSignalTimeOutNanos = waitSignalTimeOutMicros * 1000;
        readDataTimeOutNanos = readDataTimeOutMicros * 1000;
        this.detectionInterval = detectionInterval;
        detection();
    }

    @SneakyThrows
    public HumitureInfo detection() {
        try {
            lock.lock();
            if (Objects.nonNull(lastDetectionTime) && lastDetectionTime.plusSeconds(detectionInterval).isAfter(LocalDateTime.now())) {
                return new HumitureInfo(temperature, humidity);
            }
            // 发送开始信号
            digitalOutput.on();
            new Thread(readDataTask).start();
            TimeUnit.MILLISECONDS.sleep(SEND_DATA_TIME_MILLIS);
            digitalOutput.off();
            log.info(String.valueOf(digitalInput.state()));
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

    private long[] readData() {
        long[] data = new long[40];
        int dataIndex = 0;
        long eachReadEndTime = 0;
        long nanoTimer;
        // 等待主机电平拉高
        awaitSignal(SEND_DATA_TIME_MILLIS * 1000000, DigitalState.LOW);
        // 等待低电平
        awaitSignal(waitSignalTimeOutNanos, DigitalState.HIGH);
        // 等待高电平
        awaitSignal(waitSignalTimeOutNanos, DigitalState.LOW);
        // 开始读取数据
        while (dataIndex < DATA_LENGTH) {
            awaitSignal(waitSignalTimeOutNanos, DigitalState.LOW);
            nanoTimer = System.nanoTime();
            long validTime = readDataTimeOutNanos + nanoTimer;
            while (digitalInput.state() == DigitalState.HIGH) {
                if ((eachReadEndTime = System.nanoTime()) > validTime) {
                    throw new DeviceException("Read data time out: " + (eachReadEndTime - nanoTimer) + " ns, data size: " + dataIndex);
                }
            }
            log.info("dataIndex: {}", dataIndex);
            data[dataIndex] = (eachReadEndTime - nanoTimer);
            dataIndex ++;
        }
        return data;
    }

    protected abstract HumitureInfo processData(long[] data);

    private void awaitSignal(long timeOutNanos, DigitalState digitalState) {
        long endTime = System.nanoTime() + timeOutNanos;
        while (digitalInput.state() == digitalState) {
            if (System.nanoTime() > endTime) {
                throw new DeviceException("Wait " + digitalState.name() + " signal time out: " + (System.nanoTime() - endTime));
            }
        }
    }

    @Data
    @AllArgsConstructor
    public static class HumitureInfo implements Serializable {

        private Double temperature;

        private Double humidity;
    }
}
