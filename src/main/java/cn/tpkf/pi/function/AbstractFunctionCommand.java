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
public abstract class AbstractFunctionCommand implements FunctionCommand {

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


    void execute(InnerFunctionCommand command) {
        command.execute();
    }
}
