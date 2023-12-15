package cn.tpkf.pi.devices.gpio.w1;

import cn.tpkf.pi.enums.IBCMEnums;
import cn.tpkf.pi.exception.DeviceException;
import cn.tpkf.pi.manager.DeviceManager;
import com.pi4j.io.gpio.digital.DigitalState;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
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
public class Dth11 extends AbstractOneWireDevice {

    private static final Integer DATA_LENGTH = 40;

    private static final Long DATA_WAIT_TIME = 2L;

    private AtomicReference<Double> temperature;

    private AtomicReference<Double> humidity;

    public Dth11(DeviceManager deviceManager, String id, String name, IBCMEnums address, WireState initState) {
        super(deviceManager, id, name, address, DigitalState.HIGH, DigitalState.HIGH, initState);
    }


    public double getTemperature() {
        return temperature.get();
    }

    public double getHumidity() {
        return humidity.get();
    }

    public void detection() {
        try {
            lock.lock();
            int[] data = new int[40];
            int dataIndex = 0;
            outPulse(2, TimeUnit.SECONDS);
            LocalDateTime waitFixTime = LocalDateTime.now();
            while (isInHigh()) {
                TimeUnit.MICROSECONDS.sleep(DATA_WAIT_TIME);
                if (Duration.between(waitFixTime, LocalDateTime.now()).getSeconds() > 1) {
                    throw new DeviceException("Time Out");
                }
            }
            waitFixTime = LocalDateTime.now();
            while (isInLow()) {
                TimeUnit.MICROSECONDS.sleep(DATA_WAIT_TIME);
                if (Duration.between(waitFixTime, LocalDateTime.now()).getSeconds() > 1) {
                    throw new DeviceException("Time Out");
                }
            }
            while (dataIndex < DATA_LENGTH) {
                while (isInLow()) {
                    TimeUnit.MICROSECONDS.sleep(DATA_WAIT_TIME);
                }
                int fixTime = LocalDateTime.now().getNano();
                while (isInHigh()) {
                    TimeUnit.MICROSECONDS.sleep(DATA_WAIT_TIME);
                }
                int now = LocalDateTime.now().getNano();
                if (now - fixTime > 28) {
                    data[dataIndex] = 1;
                } else {
                    data[dataIndex] = 0;
                }
                dataIndex ++;
            }
            int[] humidityIntegerBits = Arrays.copyOfRange(data, 0, 8);
            int[] humidityDecimalBits = Arrays.copyOfRange(data, 8, 16);
            int[] temperatureIntegerBits = Arrays.copyOfRange(data, 16, 24);
            int[] temperatureDecimalBits = Arrays.copyOfRange(data, 24, 32);
            int[] verifyBits = Arrays.copyOfRange(data, 32, 40);
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
                log.info("校验失败");
            }
            humidity.set(Double.valueOf(humidityInteger + "." + humidityDecimal));
            temperature.set(Double.valueOf(temperatureInteger + "." + temperatureDecimal));
        } catch (InterruptedException e) {
            log.info("Dth11 detection error: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    private int sysConvert(int num, int index) {
        return (int) (num * Math.pow(2, 7 - index));
    }
}
