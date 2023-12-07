package cn.tpkf.pi.devices.i2c;

import cn.tpkf.pi.enums.BCMEnums;
import cn.tpkf.pi.manager.DeviceManager;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * @author Harlan
 * @email isharlan.hu@gmail.com
 * @date 2023 12 07 下午 10:27
 */
public class Pcf8591 extends AbstractI2cDevice {

    public static final Integer DEFAULT_BUS = 0x48;

    public static final Byte AIN_0 = 0x00;

    private static final Byte AIN_1 = 0x01;

    private static final Byte AIN_2 = 0x02;

    private static final Byte AIN_3 = 0x03;

    private final BigDecimal constantNum;

    @Getter
    private final Integer maxVoltage;

    public Pcf8591(DeviceManager deviceManager, String id, String name, Integer bus, Integer device, Integer maxVoltage) {
        super(deviceManager, id, name, bus, device);
        this.maxVoltage = maxVoltage;
        constantNum = BigDecimal.valueOf(maxVoltage / 255);
    }

    public Pcf8591(DeviceManager deviceManager, String id, String name, Integer device) {
        this(deviceManager, id, name, DEFAULT_BUS, device, 5);
    }

    public double readAin0() {
        return read(AIN_0);
    }

    public double readAin1() {
        return read(AIN_1);
    }

    public double readAin2() {
        return read(AIN_2);
    }

    public double readAin3() {
        return read(AIN_3);
    }

    public double read(Byte aIn) {
        int value = i2C.readRegister(aIn);
        return constantNum.multiply(new BigDecimal(value)).doubleValue();
    }
}
