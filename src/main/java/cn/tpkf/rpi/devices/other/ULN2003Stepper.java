package cn.tpkf.rpi.devices.other;

import cn.tpkf.rpi.devices.AbstractDevice;
import cn.tpkf.rpi.enums.IBCMEnums;
import cn.tpkf.rpi.exception.DeviceException;
import cn.tpkf.rpi.manager.DeviceManager;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutputProvider;

import java.util.concurrent.TimeUnit;

/**
 * ULN2003 driver for 28BYJ-48 compatible stepper motors.
 *
 * @author Harlan
 */
public class ULN2003Stepper extends AbstractDevice {

    private static final int[][] HALF_STEP_SEQUENCE = {
            {1, 0, 0, 0},
            {1, 1, 0, 0},
            {0, 1, 0, 0},
            {0, 1, 1, 0},
            {0, 0, 1, 0},
            {0, 0, 1, 1},
            {0, 0, 0, 1},
            {1, 0, 0, 1}
    };

    private final IBCMEnums in1Address;

    private final IBCMEnums in2Address;

    private final IBCMEnums in3Address;

    private final IBCMEnums in4Address;

    private final DigitalOutput[] outputs;

    private int sequenceIndex;

    public ULN2003Stepper(DeviceManager deviceManager, String id, String name,
                          IBCMEnums in1Address, IBCMEnums in2Address,
                          IBCMEnums in3Address, IBCMEnums in4Address) {
        super(deviceManager, id, name);
        this.in1Address = in1Address;
        this.in2Address = in2Address;
        this.in3Address = in3Address;
        this.in4Address = in4Address;
        outputs = new DigitalOutput[] {
                createOutput(id + "-IN1", in1Address),
                createOutput(id + "-IN2", in2Address),
                createOutput(id + "-IN3", in3Address),
                createOutput(id + "-IN4", in4Address)
        };
        deviceManager.addDevice(this);
    }

    public void clockwise(int steps, long delay, TimeUnit timeUnit) {
        step(Math.abs(steps), delay, timeUnit, 1);
    }

    public void counterClockwise(int steps, long delay, TimeUnit timeUnit) {
        step(Math.abs(steps), delay, timeUnit, -1);
    }

    public void step(int steps, long delay, TimeUnit timeUnit) {
        int direction = steps >= 0 ? 1 : -1;
        step(Math.abs(steps), delay, timeUnit, direction);
    }

    public void release() {
        writeStep(new int[] {0, 0, 0, 0});
    }

    @Override
    public void shutdown() {
        release();
        // attempt to shutdown each output individually and log failures
        for (DigitalOutput output : outputs) {
            try {
                deviceManager.execute(context -> {
                    context.shutdown(output.id());
                    return null;
                });
            } catch (Exception e) {
                // best-effort shutdown
                // Use System.err? library uses slf4j in other classes; use deviceManager's logger isn't accessible here
            }
        }
        deviceManager.removeDevice(id);
    }

    @Override
    public String getDescription() {
        return id + "-ULN2003-IN1 " + in1Address.getValue()
                + " IN2 " + in2Address.getValue()
                + " IN3 " + in3Address.getValue()
                + " IN4 " + in4Address.getValue()
                + "-" + name;
    }

    private void step(int steps, long delay, TimeUnit timeUnit, int direction) {
        for (int i = 0; i < steps; i++) {
            try {
                lock.lock();
                sequenceIndex = Math.floorMod(sequenceIndex + direction, HALF_STEP_SEQUENCE.length);
                writeStep(HALF_STEP_SEQUENCE[sequenceIndex]);
            } finally {
                lock.unlock();
            }
            try {
                timeUnit.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new DeviceException("ULN2003 step interrupted", e);
            }
        }
    }

    private void writeStep(int[] step) {
        for (int i = 0; i < outputs.length; i++) {
            outputs[i].state(step[i] == 1 ? DigitalState.HIGH : DigitalState.LOW);
        }
    }

    private DigitalOutput createOutput(String pinId, IBCMEnums address) {
        return deviceManager.execute(context -> {
            DigitalOutputConfig config = DigitalOutputConfigBuilder.newInstance(context)
                    .id(pinId)
                    .name(name)
                    .address(address.getValue())
                    .initial(DigitalState.LOW)
                    .shutdown(DigitalState.LOW)
                    .description(getDescription())
                    .provider(PiGpioDigitalOutputProvider.class)
                    .build();
            return context.create(config);
        });
    }
}
