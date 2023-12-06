package cn.tpkf.pi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Harlan
 * @email isharlan.hu@gmail.com
 * @date 2023 12 02 下午 08:28
 */
@Getter
@AllArgsConstructor
public enum BCMEnums {

    /**
     * gpio bcm number
     */
    SDA1(2),

    SCL1(2),

    D4(4),

    TXD(14),

    RXD(15),

    D17(17),

    PWM18(18),

    D27(27),

    D22(22),

    D23(23),

    D24(24),

    MOSI(10),

    MISO(9),

    D25(25),

    D11(11),

    CEO(8),

    CE1(7),

    D5(5),

    D6(6),

    D16(16),

    D26(26),

    D20(20),

    D21(21),

    PWM12(12),

    PWM13(13),

    PWM19(19);

    private final int vale;
}
