package cn.tpkf.pi;

import com.alibaba.fastjson2.JSON;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;

import cn.tpkf.pi.devices.gpio.digital.out.Led;
import cn.tpkf.pi.enums.BCMEnums;
import cn.tpkf.pi.manager.DeviceManager;
import cn.tpkf.pi.pojo.PlatformInfo;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/27
 */
@Slf4j
public class Main {

    public static void main(String[] args) throws InterruptedException {
        Context context = Pi4J.newAutoContext();
        DeviceManager manager = new DeviceManager(context);
        PlatformInfo platformInfo = manager.getPlatformInfo();
        log.info(JSON.toJSONString(platformInfo));
        Led led = new Led(manager, "null", "null", BCMEnums.D4);
        led.on();
        log.info("on");
        led.off();
    }
}
