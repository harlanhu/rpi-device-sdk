package cn.tpkf.pi.devices;

import com.pi4j.io.gpio.digital.DigitalOutput;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/4
 */
public class DigitalOutputDevice extends AbstractDevice {

    private final DigitalOutput digitalOutput;

    public DigitalOutputDevice(DigitalOutput digitalOutput) {
        super(new ReentrantLock());
        this.digitalOutput = digitalOutput;
    }

    @Override
    public String getId() {
        return digitalOutput.getId();
    }

    @Override
    public String getName() {
        return digitalOutput.getName();
    }
}
