package junit.test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

class ZPC {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> queue = new LinkedBlockingDeque<String>();
        queue.put("测试");
        System.out.println(queue.poll(5000 , TimeUnit.MILLISECONDS));

    }

}