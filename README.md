# rpi-device-sdk
> Based on [pi4j-v2](https://github.com/Pi4J/pi4j-v2) Raspberry Pi equipment application SDK.
> You can use the SDK to create common equipment and control the equipment for base functions easily.

[Note] The current tests only pass on the Raspberry Pi 4B

## Device List
- [x] Active Buzzer
- [x] Passive Buzzer
- [x] Button
- [x] Flame Sensor
- [x] Sound Sensor
- [x] Touch Sensor
- [x] Vibration Sensor
- [x] Line Tracking Sensor
- [x] LED Light
- [x] Relay
- [x] HC-SR501 PIR Motion Sensor
- [x] SG90 Servo
- [x] L298N DC Motor Driver
- [x] ULN2003 Stepper Motor Driver
- [x] DS18B20
- [x] PCF8591
- [x] LCD1602 I2C
- [x] HCSR04
- [ ] DHT11
- [ ] DHT22

The equipment list will be updated continuously.
## How to use
### Maven
* Add dependency to pom.xml
```xml
<dependency>
    <groupId>cn.tpkf.rpi</groupId>
    <artifactId>rpi-device-sdk</artifactId>
    <version>0.0.1</version>
</dependency>
```
* A simple example
```java
public static void main(String[] args) {
    Led led = Rpi.led(BCMEnums.BCM_4);
    led.blink(1, 5, TimeUnit.SECONDS);
}
```

The `Rpi` facade lazily creates and reuses a shared `DeviceManager`, and registers a JVM shutdown hook to release devices.
Use `DeviceManager.create()` when you want explicit lifecycle control:

```java
try (DeviceManager deviceManager = DeviceManager.create()) {
    Led led = deviceManager.led(BCMEnums.BCM_4);
    led.blink(1, 5, TimeUnit.SECONDS);
}
```

Factory methods reuse registered devices with the same id and type. You can still create devices with their constructors when you need full control over id, name, and advanced options.

### More device examples
```java
// Relay, active-high by default
Relay relay = deviceManager.relay(BCMEnums.BCM_17);
relay.on();
relay.off();

// HC-SR501 PIR motion sensor
HCSr501 pir = new HCSr501(deviceManager, "pir-27", "PIR", BCMEnums.BCM_27,
        false, 10_000L,
        () -> System.out.println("Motion detected"),
        () -> System.out.println("Motion cleared"));

// Example: prefer using SLF4J for logging. See DemoApp in src/main/java/cn/tpkf/rpi/DemoApp.java for a CI-friendly example.
// Replace System.out calls with a logger in your application, e.g.:
// private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(YourClass.class);
// () -> log.info("Motion detected")
// () -> log.info("Motion cleared")
boolean motionDetected = pir.isMotionDetected();

// Digital sensors
FlameSensor flame = new FlameSensor(deviceManager, "flame-22", "Flame", BCMEnums.BCM_22, false);
SoundSensor sound = new SoundSensor(deviceManager, "sound-5", "Sound", BCMEnums.BCM_5);
TouchSensor touch = new TouchSensor(deviceManager, "touch-6", "Touch", BCMEnums.BCM_6);
VibrationSensor vibration = new VibrationSensor(deviceManager, "vibration-13", "Vibration", BCMEnums.BCM_13);
LineTrackingSensor line = new LineTrackingSensor(deviceManager, "line-19", "Line", BCMEnums.BCM_19);

// SG90 servo
SG90Servo servo = deviceManager.servo(BCMEnums.BCM_18);
servo.center();
servo.setAngle(90);

// L298N single motor channel
L298N motor = new L298N(deviceManager, "motor-a", "Motor A", BCMEnums.BCM_23, BCMEnums.BCM_24, BCMEnums.BCM_25);
motor.setSpeed(60);
motor.forward();
motor.stop();

// ULN2003 stepper motor
ULN2003Stepper stepper = new ULN2003Stepper(deviceManager, "stepper", "Stepper",
        BCMEnums.BCM_12, BCMEnums.BCM_16, BCMEnums.BCM_20, BCMEnums.BCM_21);
stepper.clockwise(512, 2, TimeUnit.MILLISECONDS);
stepper.release();

// DS18B20 temperature sensor, requires Linux 1-Wire sysfs support
DS18B20 ds18b20 = deviceManager.ds18b20();
double temperature = ds18b20.readTemperatureCelsius();

// LCD1602 with I2C backpack
Lcd1602I2c lcd = deviceManager.lcd1602(1);
lcd.printLine(0, "Hello RPi");
lcd.printLine(1, "Temp: " + temperature);
```
## Last
Thank [PI4J](https://github.com/Pi4J) very much for providing such a good raspberry pi I/O library.

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-white.svg)](https://sonarcloud.io/summary/new_code?id=harlanhu_pi-device-sdk)
