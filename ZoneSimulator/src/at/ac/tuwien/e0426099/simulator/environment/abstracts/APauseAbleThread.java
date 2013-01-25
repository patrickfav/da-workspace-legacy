package at.ac.tuwien.e0426099.simulator.environment.abstracts;

import at.ac.tuwien.e0426099.simulator.environment.G;
import at.ac.tuwien.e0426099.simulator.util.Log;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: PatrickF
 * Date: 21.01.13
 * Time: 22:39
 * To change this template use File | Settings | File Templates.
 */
public abstract class APauseAbleThread<T> extends Thread {
	private Log log = new Log(this, G.VERBOSE_LOG_MODE_GENERAL && G.VERBOSE_LOG_MODE_SYNCTHREAD);
    private BlockingDeque<T> workerQueue;
    private Semaphore pauseSemaphore;
    private volatile boolean isOnPause;
	private boolean workSwitch;
	private Lock workLock;

    public APauseAbleThread() {
        workerQueue = new LinkedBlockingDeque<T>();
        pauseSemaphore=new Semaphore(0,false);
		workSwitch=true;
		workLock = new ReentrantLock();
    }

    @Override
    public void run() {
        while (checkIfThereWillBeAnyWork()) {
            if (isOnPause) {
                log.d("[Sync] waiting in pause mode");
                pauseSemaphore.acquireUninterruptibly();
                log.d("[Sync] resuming");
            }
            log.d("[Sync] Waiting for dispatching next task");
			try {
				T obj = workerQueue.poll(G.THREAD_BLOCKING_TIMEOUT_SEC, TimeUnit.SECONDS);
				if(obj != null) {
					doTheWork(obj);
				} else {
					log.d("[Sync] Timeout");
				}
			} catch (InterruptedException e) {
				log.w("[Sync] interrupt called while waiting: "+e);
			}
        }
		onAllDone();
        log.d("All done.");
    }

    public synchronized void pause() {
		log.d("[Sync] pause called");
        isOnPause = true;
    }

    public synchronized void resumeExec() {
		log.d("[Sync] resume called");
        if(isOnPause) {
            isOnPause = false;
            pauseSemaphore.release();
        } else {
            log.w("Trying to resume, but not in pause.");
        }
    }

	public synchronized void stopExec() {
		workSwitch = false;
	}

    protected void addToWorkerQueue(T obj) {
        workerQueue.add(obj);
    }

	protected boolean checkIfThereWillBeAnyWork() {
		return workSwitch;
	}
	protected abstract void doTheWork(T input);
	protected abstract void onAllDone();

	public void waitForFinish() {
		log.d("[Sync] wait for task to finish");
		try {
			join();
		} catch (InterruptedException e) {
			log.w("[Sync] interrupt called while waitForFinish");
		}
	}

	protected Log getLog() {
		return log;
	}

	public Lock getWorkLock() {
		return workLock;
	}
}
