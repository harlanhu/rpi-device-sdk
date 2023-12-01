package cn.tpkf.pi.function;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/22
 */
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractFunctionCommand {

    protected InnerFunctionCommand command;

    /**
     * 唯一标识
     */
    protected String id;

    /**
     * 名称
     */
    protected String name;

    /**
     * 索引
     */
    protected Integer index;

    /**
     * 执行
     */
    abstract void execute();

    void execute(InnerFunctionCommand command) {
        command.execute();
    }

    /**
     * 停止
     */
    abstract void stop();
}
