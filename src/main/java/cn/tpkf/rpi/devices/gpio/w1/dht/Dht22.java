package cn.tpkf.rpi.devices.gpio.w1.dht;

import cn.tpkf.rpi.enums.IBCMEnums;
import cn.tpkf.rpi.exception.DeviceException;
import cn.tpkf.rpi.manager.DeviceManager;

import java.util.Arrays;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/18
 */
public class Dht22 extends AbstractDhtDevice {

    protected Dht22(DeviceManager deviceManager, String id, String name, IBCMEnums address) {
        super(deviceManager, id, name, address, 1, 80L, 130L);
    }

    @Override
    protected HumitureInfo processData(long[] data) {
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
            throw new DeviceException("Dht22 data verify error");
        }
        if (temperatureDecimalBits[0] == 1) {
            temperatureInteger = -temperatureInteger;
        }
        return new HumitureInfo(Double.valueOf(temperatureInteger + "." + temperatureDecimal), Double.valueOf(humidityInteger + "." + humidityDecimal));
    }
}
