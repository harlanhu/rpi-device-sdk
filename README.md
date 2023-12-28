# rpi-device-sdk
> Based on [pi4j-v2](https://github.com/Pi4J/pi4j-v2) Raspberry Pi equipment application SDK.
> You can use the SDK to create common equipment and control the equipment for base functions easily.

[Note] The current tests only pass on the Raspberry Pi 4B

## Device List
- [x] Active Buzzer
- [x] Passive Buzzer
- [x] Button
- [x] LED Light
- [x] Relay
- [x] HC-SR501 PIR Motion Sensor
- [x] SG90 Servo
- [x] L298N DC Motor Driver
- [x] DS18B20
- [x] PCF8591
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
    // Create Pi4J context
    Context context = Pi4J.newAutoContext();
    // Create a new Device Manager
    DeviceManager deviceManager = new DeviceManager(context);
    // Create a new device example LED and blink
    Led led = new Led(deviceManager, "led-4", "LED", BCMEnums.BCM_4);
    led.blink(1, 5, TimeUnit.SECONDS);
    //shutdown led
    led.shutdown();
    //shutdown device manager
    deviceManager.shutdown();
}
```

### More device examples
```java
// Relay, active-high by default
Relay relay = new Relay(deviceManager, "relay-17", "Relay", BCMEnums.BCM_17);
relay.on();
relay.off();

// HC-SR501 PIR motion sensor
HCSr501 pir = new HCSr501(deviceManager, "pir-27", "PIR", BCMEnums.BCM_27,
        false, 10_000L,
        () -> System.out.println("Motion detected"),
        () -> System.out.println("Motion cleared"));
boolean motionDetected = pir.isMotionDetected();

// SG90 servo
SG90Servo servo = new SG90Servo(deviceManager, "servo-18", "Servo", BCMEnums.BCM_18);
servo.center();
servo.setAngle(90);

// L298N single motor channel
L298N motor = new L298N(deviceManager, "motor-a", "Motor A", BCMEnums.BCM_23, BCMEnums.BCM_24, BCMEnums.BCM_25);
motor.setSpeed(60);
motor.forward();
motor.stop();

// DS18B20 temperature sensor, requires Linux 1-Wire sysfs support
DS18B20 ds18b20 = new DS18B20(deviceManager, "temp-1", "Temperature");
double temperature = ds18b20.readTemperatureCelsius();
```
## Last
Thank [PI4J](https://github.com/Pi4J) very much for providing such a good raspberry pi I/O library.

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-white.svg)](https://sonarcloud.io/summary/new_code?id=harlanhu_pi-device-sdk)
