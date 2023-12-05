package cn.tpkf.pi.devices;

import cn.tpkf.pi.enums.PinEnums;
import cn.tpkf.pi.manager.DeviceManager;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.linuxfs.provider.gpio.digital.LinuxFsDigitalInputProvider;

import java.util.concurrent.TimeUnit;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/5
 */
public class Buzzer extends AbstractDevice {

    private final DigitalOutput digitalOutput;

    public Buzzer(DeviceManager deviceManager, String id, String name, PinEnums address) {
        super(id, name);
        digitalOutput = deviceManager.execute(context -> DigitalOutputBuilder.newInstance(context)
                .id(id)
                .name(name)
                .address(address.getVale())
                .initial(DigitalState.LOW)
                .shutdown(DigitalState.LOW)
                .provider(LinuxFsDigitalInputProvider.class)
                .build());
        deviceManager.addDevice(this);
    }

    public void on() {
        try {
            lock.lock();
            digitalOutput.high();
        } finally {
            lock.unlock();
        }
    }

    public void off() {
        try {
            lock.lock();
            digitalOutput.low();
        } finally {
            lock.unlock();
        }
    }

    public void toggle() {
        try {
            lock.lock();
            digitalOutput.toggle();
        } finally {
            lock.unlock();
        }
    }

    public void pulse(int interval, TimeUnit timeUnit, DigitalState digitalState) {
        try {
            lock.lock();
            digitalOutput.pulse(interval, timeUnit, digitalState);
        } finally {
            lock.unlock();
        }
    }

    public void blink(int delay, int duration, TimeUnit timeUnit, DigitalState digitalState) {
        try {
            lock.lock();
            digitalOutput.blink(delay, duration, timeUnit, digitalState);
        } finally {
            lock.unlock();
        }
    }
}
