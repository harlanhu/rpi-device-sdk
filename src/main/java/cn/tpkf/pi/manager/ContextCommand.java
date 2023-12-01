package cn.tpkf.pi.manager;


import com.pi4j.context.Context;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/21
 */
@FunctionalInterface
public interface ContextCommand<T> {

    /**
     * 执行命令
     */
    T execute(Context context);
}
