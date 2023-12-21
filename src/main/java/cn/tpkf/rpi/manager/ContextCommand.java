package cn.tpkf.rpi.manager;


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
     * @param context 上下文
     * @return 返回值
     */
    T execute(Context context);
}
