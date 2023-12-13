package cn.tpkf.pi.devices.i2c;

import cn.tpkf.pi.devices.AbstractDevice;
import cn.tpkf.pi.manager.DeviceManager;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CConfigBuilder;
import com.pi4j.plugin.pigpio.provider.i2c.PiGpioI2CProvider;
import lombok.Getter;

/**
 * I2C 设备
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/6
 */
public abstract class AbstractI2cDevice extends AbstractDevice {

    /**
     * The I2C instance.
     */
    protected final I2C i2C;

    /**
     * The bus.
     */
    @Getter
    protected final Integer bus;

    /**
     * The device.
     */
    @Getter
    protected final Integer device;

    /**
     * Creates an instance of the AbstractI2cDevice class.
     * @param deviceManager The DeviceManager instance.
     * @param id The ID of the device.
     * @param name The name of the device.
     * @param bus The bus.
     * @param device The device.
     */
    protected AbstractI2cDevice(DeviceManager deviceManager, String id, String name, Integer bus, Integer device) {
        super(deviceManager, id, name);
        this.bus = bus;
        this.device = device;
        i2C = deviceManager.execute(c -> {
            I2CConfig config = I2CConfigBuilder.newInstance(c)
                    .id(id)
                    .name(name)
                    .bus(bus)
                    .device(device)
                    .description(getDescription())
                    .provider(PiGpioI2CProvider.class)
                    .build();
            return c.create(config);
        });
        deviceManager.addDevice(this);
    }

    @Override
    public String getDescription() {
        return id + "-I2C-BUS " + bus + " DEVICE " + device + "-" + name;
    }
}
