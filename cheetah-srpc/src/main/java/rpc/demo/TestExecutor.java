package rpc.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ruanxin
 * @create 2018-03-27
 * @desc
 */
public class TestExecutor {

    private TaskTest taskTest;

    public TestExecutor(TaskTest taskTest) {
        this.taskTest = taskTest;
    }

    public void execute() {
        taskTest.test();
    }

    public static void main(String[] args) {
        TaskTest taskTest = new TaskTestImpl();
        final TestExecutor testExecutor = new TestExecutor(taskTest);
        ExecutorService executorService = Executors.newScheduledThreadPool(3);
        executorService.execute(new Runnable() {
            public void run() {
                testExecutor.execute();
            }
        });

    }
}
