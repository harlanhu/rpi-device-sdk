package cn.tpkf.pi.exception;

/**
 * @author Harlan
 * @email isharlan.hu@gmali.com
 * @date 2023/11/22
 */
public class FunctionException extends RuntimeException {

        public FunctionException(String message) {
            super(message);
        }

        public FunctionException(String message, Throwable e) {
            super(message, e);
        }
}
