package cn.tpkf.rpi.devices.gpio.w1;

import cn.tpkf.rpi.devices.AbstractDevice;
import cn.tpkf.rpi.exception.DeviceException;
import cn.tpkf.rpi.manager.DeviceManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

/**
 * DS18B20 temperature sensor using the Linux 1-Wire sysfs interface.
 *
 * @author Harlan
 */
public class DS18B20 extends AbstractDevice {

    private static final Path DEFAULT_W1_DEVICES_PATH = Path.of("/sys/bus/w1/devices");

    private final Path deviceFile;

    public DS18B20(DeviceManager deviceManager, String id, String name) {
        this(deviceManager, id, name, findFirstDeviceFile(DEFAULT_W1_DEVICES_PATH));
    }

    public DS18B20(DeviceManager deviceManager, String id, String name, String sensorId) {
        this(deviceManager, id, name, DEFAULT_W1_DEVICES_PATH.resolve(sensorId).resolve("w1_slave"));
    }

    public DS18B20(DeviceManager deviceManager, String id, String name, Path deviceFile) {
        super(deviceManager, id, name);
        if (deviceFile == null) {
            throw new DeviceException("DS18B20 device file is null");
        }
        this.deviceFile = deviceFile;
        deviceManager.addDevice(this);
    }

    public double readTemperatureCelsius() {
        List<String> lines;
        try {
            lines = Files.readAllLines(deviceFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DeviceException("Fail to read DS18B20 data: " + deviceFile, e);
        }
        if (lines.size() < 2 || !lines.get(0).endsWith("YES")) {
            throw new DeviceException("Invalid DS18B20 crc data: " + deviceFile);
        }
        int temperatureIndex = lines.get(1).indexOf("t=");
        if (temperatureIndex < 0) {
            throw new DeviceException("Invalid DS18B20 temperature data: " + deviceFile);
        }
        String rawTemperature = lines.get(1).substring(temperatureIndex + 2);
        return Integer.parseInt(rawTemperature) / 1000.0;
    }

    public double readTemperatureFahrenheit() {
        return readTemperatureCelsius() * 9 / 5 + 32;
    }

    public Path getDeviceFile() {
        return deviceFile;
    }

    @Override
    public void shutdown() {
        deviceManager.removeDevice(id);
    }

    @Override
    public String getDescription() {
        return id + "-DS18B20-" + deviceFile + "-" + name;
    }

    private static Path findFirstDeviceFile(Path devicesPath) {
        try (var paths = Files.list(devicesPath)) {
            return paths
                    .filter(path -> path.getFileName().toString().startsWith("28-"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .map(path -> path.resolve("w1_slave"))
                    .filter(Files::isRegularFile)
                    .findFirst()
                    .orElseThrow(() -> new DeviceException("No DS18B20 sensor found in: " + devicesPath));
        } catch (IOException e) {
            throw new DeviceException("Fail to list 1-Wire devices: " + devicesPath, e);
        }
    }
}
