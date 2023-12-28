package cn.tpkf.rpi.devices.gpio.digital.out;

import cn.tpkf.rpi.enums.IBCMEnums;
import cn.tpkf.rpi.manager.DeviceManager;
import com.pi4j.io.gpio.digital.DigitalState;

/**
 * Relay module.
 *
 * @author Harlan
 */
public class Relay extends AbstractDoDevice {

    public Relay(DeviceManager deviceManager, String id, String name, IBCMEnums address) {
        this(deviceManager, id, name, address, true);
    }

    public Relay(DeviceManager deviceManager, String id, String name, IBCMEnums address, boolean activeHigh) {
        super(deviceManager, id, name, address,
                activeHigh ? DigitalState.LOW : DigitalState.HIGH,
                activeHigh ? DigitalState.LOW : DigitalState.HIGH);
    }
}
