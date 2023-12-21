# rpi-device-sdk
> Based on [pi4j-v2](https://github.com/Pi4J/pi4j-v2) Raspberry Pi equipment application SDK.
> You can use the SDK to create common equipment and control the equipment for base functions easily.

[Note] The current tests only pass on the Raspberry Pi 4B

## Device List
- [x] Active Buzzer
- [x] Passive Buzzer
- [x] Button
- [x] LED Light
- [x] PCF8591
- [x] DHT11
- [x] DHT22

The equipment list will be updated continuously.
## How to use
### Maven
* Add dependency to pom.xml
```xml
<dependency>
    <groupId>com.github.harlanhu</groupId>
    <artifactId>rpi-device-sdk</artifactId>
    <version>${last.version}</version>
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
## Last
Thank [PI4J](https://github.com/Pi4J) very much for providing such a good raspberry pi I/O library.

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-white.svg)](https://sonarcloud.io/summary/new_code?id=harlanhu_pi-device-sdk)