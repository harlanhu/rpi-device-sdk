package cn.tpkf.rpi.devices.i2c;

import cn.tpkf.rpi.manager.DeviceManager;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Harlan
 * @email isharlan.hu@gmail.com
 * @date 2023 12 07 下午 10:27
 */
public class Pcf8591 extends AbstractI2cDevice {

    /**
     * The default address.
     */
    public static final Integer DEFAULT_ADDRESS = 0x48;

    /**
     * The AIN_0 address.
     */
    public static final Byte AIN_0 = 0x45;

    /**
     * The AIN_1 address.
     */
    private static final Byte AIN_1 = 0x46;

    /**
     * The AIN_2 address.
     */
    private static final Byte AIN_2 = 0x47;

    /**
     * The AIN_3 address.
     */
    private static final Byte AIN_3 = 0x44;

    /**
     * The constant number.
     */
    private final BigDecimal constantNum;

    /**
     * The max voltage.
     */
    @Getter
    private final Integer maxVoltage;

    /**
     * Creates an instance of the AbstractI2cDevice class.
     * @param deviceManager The DeviceManager instance.
     * @param id The ID of the device.
     * @param name The name of the device.
     * @param bus The bus.
     * @param device The device.
     * @param maxVoltage The max voltage.
     */
    public Pcf8591(DeviceManager deviceManager, String id, String name, Integer bus, Integer device, Integer maxVoltage) {
        super(deviceManager, id, name, bus, device);
        this.maxVoltage = maxVoltage;
        BigDecimal maxVoltageDecimal = new BigDecimal(maxVoltage);
        constantNum = maxVoltageDecimal.divide(new BigDecimal(255), 4, RoundingMode.HALF_UP);
    }

    public Pcf8591(DeviceManager deviceManager, String id, String name, Integer bus) {
        this(deviceManager, id, name, bus, DEFAULT_ADDRESS, 5);
    }

    /**
     * Reads the value of the AIN_0 address.
     *
     * @return The value of the AIN_0 address.
     */
    public double readAin0() {
        return read(AIN_0);
    }

    /**
     * Reads the value of the AIN_1 address.
     *
     * @return The value of the AIN_1 address.
     */
    public double readAin1() {
        return read(AIN_1);
    }

    /**
     * Reads the value of the AIN_2 address.
     *
     * @return The value of the AIN_2 address.
     */
    public double readAin2() {
        return read(AIN_2);
    }

    /**
     * Reads the value of the AIN_3 address.
     *
     * @return The value of the AIN_3 address.
     */
    public double readAin3() {
        return read(AIN_3);
    }

    /**
     * Reads the value of the specified address.
     *
     * @param aIn The address.
     * @return The value of the specified address.
     */
    public double read(Byte aIn) {
        int value = i2C.readRegister(aIn);
        return constantNum.multiply(new BigDecimal(value)).doubleValue();
    }
}
