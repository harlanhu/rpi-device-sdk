package cn.tpkf.pi.devices.i2c;

import cn.tpkf.pi.manager.DeviceManager;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Harlan
 * @email isharlan.hu@gmail.com
 * @date 2023 12 07 下午 10:27
 */
public class Pcf8591 extends AbstractI2cDevice {

    public static final Integer DEFAULT_ADDRESS = 0x48;

    public static final Byte AIN_0 = 0x45;

    private static final Byte AIN_1 = 0x46;

    private static final Byte AIN_2 = 0x47;

    private static final Byte AIN_3 = 0x44;

    private final BigDecimal constantNum;

    @Getter
    private final Integer maxVoltage;

    public Pcf8591(DeviceManager deviceManager, String id, String name, Integer bus, Integer device, Integer maxVoltage) {
        super(deviceManager, id, name, bus, device);
        this.maxVoltage = maxVoltage;
        BigDecimal maxVoltageDecimal = new BigDecimal(maxVoltage);
        constantNum = maxVoltageDecimal.divide(new BigDecimal(255), 4, RoundingMode.HALF_UP);
    }

    public Pcf8591(DeviceManager deviceManager, String id, String name, Integer bus) {
        this(deviceManager, id, name, bus, DEFAULT_ADDRESS, 5);
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
