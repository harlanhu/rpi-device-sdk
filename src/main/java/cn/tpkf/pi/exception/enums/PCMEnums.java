package cn.tpkf.pi.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PCM (Pulse-code Modulation)
 *
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/12/14
 */
@Getter
@AllArgsConstructor
public enum PCMEnums implements IBCMEnums {

    FS(19),

    CLK(18),

    DIN(20),

    DOUT(21);

    private final Integer value;
}
