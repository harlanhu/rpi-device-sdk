package cn.tpkf.pi.function;

import cn.tpkf.pi.enums.FunctionStateEnums;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/22
 */
public interface Function {

    /**
     * 获取当前状态
     *
     * @return 状态
     */
    FunctionStateEnums getCurrentState();

    /**
     * 获取当前命令
     * @return 命令
     */
    AbstractFunctionCommand getCurrentCommand();

    /**
     * 获取当前命令索引
     * @return 索引
     */
    Integer getCurrentCommandIndex();

    /**
     * 获取命令
     * @param index 索引
     * @return 命令
     */
    AbstractFunctionCommand getCommand(Integer index);


    /**
     * 获取下一个命令
     * @return 命令
     */
    AbstractFunctionCommand getNextCommand();

    /**
     * 获取命令数量
     * @return 数量
     */
    Integer getCommandSize();

    /**
     * 顺序执行命令
     */
    void sequenceExecute();

    /**
     * 顺序执行命令
     * @param index 索引
     */
    void sequenceExecute(Integer index);

    /**
     * 停止执行命令
     */
    void stopExecute();

    /**
     * 重新开始执行命令
     */
    void restartExecute();

    /**
     * 重新执行命令
     */
    void reExecute();

    /**
     * 是否正在运行
     * @return 是否正在运行
     */
    Boolean isRunning();
}
