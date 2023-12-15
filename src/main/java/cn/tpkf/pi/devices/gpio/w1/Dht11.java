package cn.tpkf.pi.devices.gpio.w1;

import cn.tpkf.pi.enums.IBCMEnums;
import cn.tpkf.pi.exception.DeviceException;
import cn.tpkf.pi.manager.DeviceManager;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * DTH11 温湿度传感器
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/14
 */
@Slf4j
public class Dht11 extends AbstractOneWireDevice {

    private static final Integer DATA_LENGTH = 40;

    private static final Integer SEND_SIGNAL_TIME = 20;

    private static final Long WAIT_TIME_OUT_NANOS = 10000000L;

    private static final Long READ_TIME_OUT_NANOS = 130000L;

    private final AtomicReference<Double> temperature;

    private final AtomicReference<Double> humidity;

    public Dht11(DeviceManager deviceManager, String id, String name, IBCMEnums address) {
        super(deviceManager, id, name, address, DigitalState.HIGH, DigitalState.HIGH, PullResistance.OFF, WireState.OUT);
        temperature = new AtomicReference<>(0.0);
        humidity = new AtomicReference<>(0.0);
    }

    public double getTemperature() {
        return temperature.get();
    }

    public double getHumidity() {
        return humidity.get();
    }

    @SneakyThrows
    public void detection() {
        try {
            lock.lock();
            long[] data = new long[40];
            int dataIndex = 0;
            long eachReadEndTime = 0;
            long nanoTimer;
            // 发送开始信号
            digitalOutput.on();
            TimeUnit.MILLISECONDS.sleep(SEND_SIGNAL_TIME);
            digitalOutput.off();
            long waitStartTime = System.nanoTime();
            // 等待低电平
            awaitSignal(waitStartTime, DigitalState.LOW);
            // 等待高电平
            awaitSignal(waitStartTime, DigitalState.HIGH);
            // 开始读取数据
            while (dataIndex < DATA_LENGTH) {
                awaitSignal(waitStartTime, DigitalState.LOW);
                nanoTimer = System.nanoTime();
                long validTime = READ_TIME_OUT_NANOS + nanoTimer;
                while (digitalInput.state() == DigitalState.HIGH) {
                    if ((eachReadEndTime = System.nanoTime()) > validTime) {
                        throw new DeviceException("Read data time out: " + (eachReadEndTime - nanoTimer) + " ns, data size: " + dataIndex + 1);
                    }
                }
                log.info("dataIndex: {}", dataIndex);
                data[dataIndex] = (eachReadEndTime - nanoTimer);
                dataIndex ++;
            }
            // 处理数据
            processAndSetData(data);
        } finally {
            lock.unlock();
        }
    }

    @SneakyThrows
    private void awaitSignal(long startNanoTime, DigitalState digitalState) {
        long endTime = startNanoTime + WAIT_TIME_OUT_NANOS;
        while (digitalInput.state() == digitalState) {
            if (System.nanoTime() > endTime) {
                throw new DeviceException("Wait " + digitalState.name() + " signal time out: " + (System.nanoTime() - startNanoTime));
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
    private int sysConvert(long num, int index) {
        return (int) (num * Math.pow(2, 7d - index));
    }

    /**
     * Process the given data and set the humidity and temperature values accordingly.
     *
     * @param data the data to be processed, an array of integers representing the bits of the data
     */
    private void processAndSetData(long[] data) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] > 28000) {
                data[i] = 1;
            } else {
                data[i] = 0;
            }
        }
        long[] humidityIntegerBits = Arrays.copyOfRange(data, 0, 8);
        long[] humidityDecimalBits = Arrays.copyOfRange(data, 8, 16);
        long[] temperatureIntegerBits = Arrays.copyOfRange(data, 16, 24);
        long[] temperatureDecimalBits = Arrays.copyOfRange(data, 24, 32);
        long[] verifyBits = Arrays.copyOfRange(data, 32, 40);
        int humidityInteger = 0;
        int humidityDecimal = 0;
        int temperatureInteger = 0;
        int temperatureDecimal = 0;
        int verifyNum = 0;
        for (int i = 0; i < 8; i++) {
            humidityInteger += sysConvert(humidityIntegerBits[i], i);
            humidityDecimal += sysConvert(humidityDecimalBits[i], i);
            temperatureInteger += sysConvert(temperatureIntegerBits[i], i);
            temperatureDecimal += sysConvert(temperatureDecimalBits[i], i);
            verifyNum += sysConvert(verifyBits[i], i);
        }
        int sum = humidityInteger + humidityDecimal + temperatureInteger + temperatureDecimal;
        if (sum != verifyNum) {
            log.warn("Dht11 data verify error");
            return;
        }
        humidity.set(Double.valueOf(humidityInteger + "." + humidityDecimal));
        temperature.set(Double.valueOf(temperatureInteger + "." + temperatureDecimal));
    }
}
