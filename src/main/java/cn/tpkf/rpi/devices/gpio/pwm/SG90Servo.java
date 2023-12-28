package cn.tpkf.rpi.devices.gpio.pwm;

import cn.tpkf.rpi.enums.IBCMEnums;
import cn.tpkf.rpi.manager.DeviceManager;
import com.pi4j.io.pwm.PwmPolarity;
import com.pi4j.io.pwm.PwmType;
import lombok.Getter;

/**
 * SG90 compatible PWM servo.
 *
 * @author Harlan
 */
public class SG90Servo extends AbstractPwmDevice {

    public static final int DEFAULT_FREQUENCY = 50;

    public static final int DEFAULT_MIN_PULSE_MICROS = 500;

    public static final int DEFAULT_MAX_PULSE_MICROS = 2500;

    public static final int DEFAULT_MAX_ANGLE = 180;

    @Getter
    private final int minPulseMicros;

    @Getter
    private final int maxPulseMicros;

    @Getter
    private final int maxAngle;

    @Getter
    private double angle;

    public SG90Servo(DeviceManager deviceManager, String id, String name, IBCMEnums address) {
        this(deviceManager, id, name, address, DEFAULT_MIN_PULSE_MICROS, DEFAULT_MAX_PULSE_MICROS, DEFAULT_MAX_ANGLE);
    }

    public SG90Servo(DeviceManager deviceManager, String id, String name, IBCMEnums address,
                     int minPulseMicros, int maxPulseMicros, int maxAngle) {
        super(deviceManager, id, name, address, PwmType.HARDWARE, 0, 0, 0, DEFAULT_FREQUENCY, PwmPolarity.NORMAL);
        this.minPulseMicros = minPulseMicros;
        this.maxPulseMicros = maxPulseMicros;
        this.maxAngle = maxAngle;
    }

    public void setAngle(double angle) {
        if (angle < 0 || angle > maxAngle) {
            throw new IllegalArgumentException("Servo angle must be between 0 and " + maxAngle);
        }
        this.angle = angle;
        setPulseWidthMicros(angleToPulseWidthMicros(angle));
    }

    public void center() {
        setAngle(maxAngle / 2.0);
    }

    public void setPulseWidthMicros(double pulseWidthMicros) {
        if (pulseWidthMicros < minPulseMicros || pulseWidthMicros > maxPulseMicros) {
            throw new IllegalArgumentException("Pulse width must be between " + minPulseMicros + " and " + maxPulseMicros);
        }
        on(pulseWidthToDutyCycle(pulseWidthMicros), DEFAULT_FREQUENCY);
    }

    public double angleToPulseWidthMicros(double angle) {
        return minPulseMicros + (maxPulseMicros - minPulseMicros) * angle / maxAngle;
    }

    private double pulseWidthToDutyCycle(double pulseWidthMicros) {
        double periodMicros = 1_000_000.0 / DEFAULT_FREQUENCY;
        return pulseWidthMicros / periodMicros * 100.0;
    }
}
