package cn.tpkf.pi.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Pulse-width Modulation
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/14
 */
@Getter
@AllArgsConstructor
public enum PWMEnums implements IBCMEnums {

    PWM_13(13),

    PWM_19(19),

    PWM_18(18),

    PWM_12(12);

    private final Integer value;
}
