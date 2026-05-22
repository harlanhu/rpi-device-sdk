package cn.tpkf.rpi.config;

import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.io.pwm.PwmProvider;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalInputProvider;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutputProvider;
import com.pi4j.plugin.pigpio.provider.i2c.PiGpioI2CProvider;
import com.pi4j.plugin.pigpio.provider.pwm.PiGpioPwmProvider;

import java.util.Objects;

/**
 * SDK-level configuration shared by device factories.
 *
 * @author Harlan
 */
public final class RpiConfig {

    public static final long DEFAULT_LOCK_TIMEOUT_MILLIS = 2000L;

    public static final long DEFAULT_DEBOUNCE_MICROS = 10_000L;

    private final long lockTimeoutMillis;

    private final long debounceMicros;

    private final boolean registerShutdownHook;

    private final Class<? extends DigitalInputProvider> digitalInputProvider;

    private final Class<? extends DigitalOutputProvider> digitalOutputProvider;

    private final Class<? extends PwmProvider> pwmProvider;

    private final Class<? extends I2CProvider> i2cProvider;

    // metrics configuration
    private final boolean metricsEnabled;

    private final int metricsPort;

    private RpiConfig(Builder builder) {
        this.lockTimeoutMillis = builder.lockTimeoutMillis;
        this.debounceMicros = builder.debounceMicros;
        this.registerShutdownHook = builder.registerShutdownHook;
        this.digitalInputProvider = builder.digitalInputProvider;
        this.digitalOutputProvider = builder.digitalOutputProvider;
        this.pwmProvider = builder.pwmProvider;
        this.i2cProvider = builder.i2cProvider;
        this.metricsEnabled = builder.metricsEnabled;
        this.metricsPort = builder.metricsPort;
    }

    public static RpiConfig defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public long getLockTimeoutMillis() {
        return lockTimeoutMillis;
    }

    public long getDebounceMicros() {
        return debounceMicros;
    }

    public boolean isRegisterShutdownHook() {
        return registerShutdownHook;
    }

    public Class<? extends DigitalInputProvider> getDigitalInputProvider() {
        return digitalInputProvider;
    }

    public Class<? extends DigitalOutputProvider> getDigitalOutputProvider() {
        return digitalOutputProvider;
    }

    public Class<? extends PwmProvider> getPwmProvider() {
        return pwmProvider;
    }

    public Class<? extends I2CProvider> getI2cProvider() {
        return i2cProvider;
    }

    public boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    public int getMetricsPort() {
        return metricsPort;
    }

    public static final class Builder {

        private long lockTimeoutMillis = DEFAULT_LOCK_TIMEOUT_MILLIS;

        private long debounceMicros = DEFAULT_DEBOUNCE_MICROS;

        private boolean registerShutdownHook = true;

        private Class<? extends DigitalInputProvider> digitalInputProvider = PiGpioDigitalInputProvider.class;

        private Class<? extends DigitalOutputProvider> digitalOutputProvider = PiGpioDigitalOutputProvider.class;

        private Class<? extends PwmProvider> pwmProvider = PiGpioPwmProvider.class;

        private Class<? extends I2CProvider> i2cProvider = PiGpioI2CProvider.class;

        // metrics defaults
        private boolean metricsEnabled = false;
        private int metricsPort = 9091;

        private Builder() {
        }

        public Builder lockTimeoutMillis(long lockTimeoutMillis) {
            if (lockTimeoutMillis <= 0) {
                throw new IllegalArgumentException("lockTimeoutMillis must be greater than 0");
            }
            this.lockTimeoutMillis = lockTimeoutMillis;
            return this;
        }

        public Builder debounceMicros(long debounceMicros) {
            if (debounceMicros < 0) {
                throw new IllegalArgumentException("debounceMicros must be greater than or equal to 0");
            }
            this.debounceMicros = debounceMicros;
            return this;
        }

        public Builder registerShutdownHook(boolean registerShutdownHook) {
            this.registerShutdownHook = registerShutdownHook;
            return this;
        }

        public Builder digitalInputProvider(Class<? extends DigitalInputProvider> digitalInputProvider) {
            this.digitalInputProvider = Objects.requireNonNull(digitalInputProvider, "digitalInputProvider must not be null");
            return this;
        }

        public Builder digitalOutputProvider(Class<? extends DigitalOutputProvider> digitalOutputProvider) {
            this.digitalOutputProvider = Objects.requireNonNull(digitalOutputProvider, "digitalOutputProvider must not be null");
            return this;
        }

        public Builder pwmProvider(Class<? extends PwmProvider> pwmProvider) {
            this.pwmProvider = Objects.requireNonNull(pwmProvider, "pwmProvider must not be null");
            return this;
        }

        public Builder i2cProvider(Class<? extends I2CProvider> i2cProvider) {
            this.i2cProvider = Objects.requireNonNull(i2cProvider, "i2cProvider must not be null");
            return this;
        }

        public Builder metricsEnabled(boolean enabled) {
            this.metricsEnabled = enabled;
            return this;
        }

        public Builder metricsPort(int port) {
            if (port <= 0 || port > 65535) {
                throw new IllegalArgumentException("metricsPort must be a valid port number");
            }
            this.metricsPort = port;
            return this;
        }

        public RpiConfig build() {
            return new RpiConfig(this);
        }
    }
}
