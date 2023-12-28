package cn.tpkf.rpi.devices.other;

import cn.tpkf.rpi.devices.AbstractDevice;
import cn.tpkf.rpi.enums.BCMEnums;
import cn.tpkf.rpi.manager.DeviceManager;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutputProvider;

/**
 * RGY LED
 * Red Green Yellow LED
 * GND and 3 GPIO pins
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/28
 */
public class RgyLed extends AbstractDevice {

    private final DigitalOutput redOutput;

    private final DigitalOutput greenOutput;

    private final DigitalOutput yellowOutput;

    private final DigitalState onState;

    /**
     * Represents an abstract device.
     *
     * @param deviceManager the DeviceManager instance
     * @param id            the ID of the device
     * @param name          the name of the device
     */
    public RgyLed(DeviceManager deviceManager, String id, String name,
                     BCMEnums redAddress, BCMEnums greenAddress, BCMEnums yellowAddress,
                     DigitalState init, DigitalState shutdown) {
        super(deviceManager, id, name);
        if (DigitalState.LOW.equals(shutdown)) {
            onState = DigitalState.HIGH;
        } else {
            onState = DigitalState.LOW;
        }
        redOutput = deviceManager.execute(context -> {
            DigitalOutputConfig config = DigitalOutputConfig.newBuilder(context)
                    .id(id + " RED")
                    .address(redAddress.getValue())
                    .initial(init)
                    .shutdown(shutdown)
                    .onState(onState)
                    .provider(PiGpioDigitalOutputProvider.class)
                    .build();
            return context.create(config);
        });
        greenOutput = deviceManager.execute(context -> {
            DigitalOutputConfig config = DigitalOutputConfig.newBuilder(context)
                    .id(id + " GREEN")
                    .address(greenAddress.getValue())
                    .initial(init)
                    .shutdown(shutdown)
                    .onState(onState)
                    .provider(PiGpioDigitalOutputProvider.class)
                    .build();
            return context.create(config);
        });
        yellowOutput = deviceManager.execute(context -> {
            DigitalOutputConfig config = DigitalOutputConfig.newBuilder(context)
                    .id(id + " YELLOW")
                    .address(yellowAddress.getValue())
                    .initial(init)
                    .shutdown(shutdown)
                    .onState(onState)
                    .provider(PiGpioDigitalOutputProvider.class)
                    .build();
            return context.create(config);
        });
        deviceManager.addDevice(this);
    }

    public void on(LightColor color) {
        switch (color) {
            case NONE -> {
                redOutput.off();
                greenOutput.off();
                yellowOutput.off();
            }
            case ALL -> {
                redOutput.on();
                greenOutput.on();
                yellowOutput.on();
            }
            case RED -> {
                redOutput.on();
                greenOutput.off();
                yellowOutput.off();
            }
            case GREEN -> {
                redOutput.off();
                greenOutput.on();
                yellowOutput.off();
            }
            case YELLOW -> {
                redOutput.off();
                greenOutput.off();
                yellowOutput.on();
            }
        }
    }

    public void colorOn(LightColor color) {
        switch (color) {
            case RED -> redOutput.on();
            case GREEN -> greenOutput.on();
            case YELLOW -> yellowOutput.on();
            default -> throw new IllegalStateException("Unexpected value: " + color);
        }
    }

    @Override
    public void shutdown() {
        deviceManager.execute(context -> {
            redOutput.shutdown(context);
            greenOutput.shutdown(context);
            yellowOutput.shutdown(context);
            return null;
        });
        deviceManager.removeDevice(id);
    }

    @Override
    public String getDescription() {
        return id + "-RgyLed " + name;
    }

    public enum LightColor {

        RED, GREEN, YELLOW, NONE, ALL
    }
}
