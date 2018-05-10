package task;

import org.apache.log4j.Logger;
import service.ExpDataClearService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ruanxin
 * @create 2018-04-22
 * @desc task center
 */
public class TaskCenter {

    private final static Logger logger = Logger.getLogger(TaskCenter.class);

    private ExpDataClearService expDataClearService;
    private AtomicInteger taskNum = new AtomicInteger(0);
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private static TaskCenter taskCenter = null;

    private TaskCenter () {

    }

    public static TaskCenter getInstance() {
        synchronized (TaskCenter.class) {
            if (taskCenter == null) {
                taskCenter = new TaskCenter();
            }
            return taskCenter;
        }
    }

    public void startExpDataClearTask(ExpDataClearService expDataClearService) {
        this.expDataClearService = expDataClearService;
        //exp data clear task
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                taskNum.incrementAndGet();
                expDataClearTask();
                taskNum.decrementAndGet();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    private void expDataClearTask () {
        try {
            logger.info("begin to clear exp data task!");
            expDataClearService.doTask();
            logger.info("finish clear exp data task!");
        } catch (Exception ex) {
            logger.error("exp data clear task occurs ex:", ex);
        }
    }

    public void stop () {
        executorService.shutdown();
    }

    public AtomicInteger getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(AtomicInteger taskNum) {
        this.taskNum = taskNum;
    }

    public static void main(String[] args) {
        AtomicInteger integer = new AtomicInteger(1);
        System.out.println(integer.incrementAndGet());
    }
}
