package cn.tpkf.pi.devices.gpio.w1;

import cn.tpkf.pi.devices.gpio.w1.dht.Dht11;
import cn.tpkf.pi.enums.BCMEnums;
import cn.tpkf.pi.manager.DeviceManager;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/15
 */
@Slf4j
class Dht11Test {

    private final Context context = Pi4J.newAutoContext();

    @Test
    void detection() {
        DeviceManager deviceManager = new DeviceManager(context);
        Dht11 dht11 = new Dht11(deviceManager, "dht11", "温湿度传感器", BCMEnums.BCM_4);
        assertNotNull(dht11.detection());
        assertNotEquals(0.0, dht11.getHumidity());
        assertNotEquals(0.0, dht11.getTemperature());
    }
}