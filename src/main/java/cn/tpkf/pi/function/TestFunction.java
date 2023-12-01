package cn.tpkf.pi.function;

import java.util.List;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/22
 */
public class TestFunction extends AbstractFunction {

    public TestFunction(String id, String name, List<AbstractFunctionCommand> commands) {
        super(id, name, commands);
    }

    public TestFunction(String id, String name, List<AbstractFunctionCommand> commands, Integer commandIndex, Long executeTimeOut) {
        super(id, name, commands, commandIndex, executeTimeOut);
    }
}
