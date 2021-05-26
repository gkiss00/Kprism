package helper;

import java.util.concurrent.Semaphore;

public class TimeChecker implements Runnable {
    private long timeout;
    private Semaphore semaphore;

    public TimeChecker(int timeout, Semaphore semaphore){
        this.timeout = timeout * 1000;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        long now = System.currentTimeMillis();
        while(now - start < timeout) {
            try{
                Thread.sleep(1000);
            } catch (Exception e) {

            }
            now = System.currentTimeMillis();
        }
        try {
            semaphore.acquire();
            System.out.println("Timeout");
            System.exit(0);
        } catch (Exception e){
            System.exit(1);
        }
        
    }
}
