package cn.tpkf.pi;

import cn.tpkf.pi.devices.digital.out.Buzzer;
import cn.tpkf.pi.function.AbstractFunctionCommand;
import cn.tpkf.pi.manager.DeviceManager;
import cn.tpkf.pi.pojo.PlatformInfo;
import com.alibaba.fastjson2.JSON;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
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
        System.out.println(JSON.toJSONString(platformInfo));
        Buzzer buzzer = new Buzzer(manager, "1", "蜂鸣器", null);
        AbstractFunctionCommand abstractFunctionCommand = new AbstractFunctionCommand("", "", 1);
        abstractFunctionCommand.execute();
    }
}
