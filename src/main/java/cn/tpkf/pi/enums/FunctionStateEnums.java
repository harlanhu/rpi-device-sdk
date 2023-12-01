package cn.tpkf.pi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/22
 */
@Getter
@AllArgsConstructor
public enum FunctionStateEnums {

    /**
     * 初始化
     */
    INIT(0, "初始化"),


    /**
     * 运行中
     */
    RUNNING(1, "运行中"),


    /**
     * 已停止
     */
    STOPPED(2, "已停止");

    private final Integer value;

    private final String desc;
}
