package utils.code;

/**
 * @author ruanxin
 * @create 2018-04-01
 * @desc
 */
public class Errors {
    public static IllegalStateException notExpected() {
        return new IllegalStateException("The operation is not supposed to be called!");
    }
}
