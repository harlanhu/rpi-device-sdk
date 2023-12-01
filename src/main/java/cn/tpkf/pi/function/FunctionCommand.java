package cn.tpkf.pi.function;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/22
 */
public interface FunctionCommand {

    /**
     * 获取id
     * @return id
     */
    String getId();

    /**
     * 获取名称
     * @return 名称
     */
    String name();

    /**
     * 获取索引
     * @return 索引
     */
    Integer getIndex();

    /**
     * 执行
     */
    void execute();

    /**
     * 停止
     */
    void stop();
}
