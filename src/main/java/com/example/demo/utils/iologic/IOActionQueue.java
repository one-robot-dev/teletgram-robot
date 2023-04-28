package com.example.demo.utils.iologic;

import com.example.demo.utils.ThrowableRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class IOActionQueue {
    private static final Logger logger = LoggerFactory.getLogger(IOActionQueue.class);

    private final ConcurrentLinkedQueue<ThrowableRunnable> requests = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean isDequeue = new AtomicBoolean(false);

    public IOActionQueue(){
    }

    public int getSize(){
        return requests.size();
    }

    public void enqueue(ThrowableRunnable cmd){
        requests.offer(cmd);
        if(!isRunning.get() && !isDequeue.get()){
            if (isDequeue.compareAndSet(false, true)) {
                IOLogicExecutors.executor.execute(this::dequeue);
                this.dequeue();
            }
        }
    }

    public void dequeue(){
        if (isRunning.compareAndSet(false, true)) {
            try {
                for(;;){
                    ThrowableRunnable run = requests.poll();
                    if (run == null) {
                        break;
                    }
                    try {
                        run.run();
                    } catch (Throwable e) {
                        logger.error("IO action queue run {} error, thread name is {}", run.getClass().getSimpleName(), Thread.currentThread().getName(), e);
                    }
                }
            }finally {
                isRunning.set(false);
                isDequeue.set(false);
            }
            if (!isRunning.get() && requests.peek() != null) {
                if (isDequeue.compareAndSet(false, true)) {
                    IOLogicExecutors.executor.execute(this::dequeue);
                    this.dequeue();
                }
            }
        }
    }

    public void clear() {
        requests.clear();
    }
}
