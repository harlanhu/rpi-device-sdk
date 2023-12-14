package cn.tpkf.pi.devices.gpio.w1;

import cn.tpkf.pi.enums.IBCMEnums;
import cn.tpkf.pi.manager.DeviceManager;
import com.pi4j.io.gpio.digital.DigitalState;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * DTH11 温湿度传感器
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/14
 */
public class DTH11 extends AbstractOneWireDevice {

    private AtomicReference<Double> temperature;

    private AtomicReference<Double> humidity;

    /**
     * Initializes an instance of the AbstractOneWireDevice class.
     *
     * @param deviceManager The DeviceManager instance.
     * @param id            The ID of the device.
     * @param name          The name of the device.
     * @param address       The IBCMEnums address of the device.
     * @param initial       The initial state of the digital output pin.
     * @param shutdown      The shutdown state of the digital output pin.
     * @param initState
     */
    public DTH11(DeviceManager deviceManager, String id, String name, IBCMEnums address, DigitalState initial, DigitalState shutdown, WireState initState) {
        super(deviceManager, id, name, address, initial, shutdown, initState);
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
            outPulse(20, TimeUnit.MILLISECONDS);
        } finally {
            lock.unlock();
        }
    }
}
