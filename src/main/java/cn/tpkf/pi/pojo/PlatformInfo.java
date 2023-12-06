package cn.tpkf.pi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlatformInfo implements Serializable {

    private LocalDateTime currentTime;

    private CupInfo cupInfo;

    private MemoryInfo memoryInfo;

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CupInfo implements Serializable {

        /**
         * 供应商
         */
        private String vendor;

        /**
         * 名称
         */
        private String name;

        /**
         * 系列
         */
        private String family;

        /**
         * 型号
         */
        private String model;

        /**
         * 步进
         */
        private String stepping;

        /**
         * 处理器ID
         */
        private String processorId;

        /**
         * 标识符
         */
        private String identifier;

        /**
         * 是否64位
         */
        private Boolean is64Bit;

        /**
         * 最大频率
         */
        private Long maxFreq;

        /**
         * 当前使用率
         */
        private Double usageRate;

        /**
         * 微处理结构
         */
        private String microArchitecture;

        /**
         * 温度
         */
        private Double temperature;
    }

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemoryInfo implements Serializable {

        /**
         * 总内存
         */
        private Long totalMemory;

        /**
         * 已使用内存
         */
        private Long usedMemory;

        /**
         * 空闲内存
         */
        private Long freeMemory;
    }
}
