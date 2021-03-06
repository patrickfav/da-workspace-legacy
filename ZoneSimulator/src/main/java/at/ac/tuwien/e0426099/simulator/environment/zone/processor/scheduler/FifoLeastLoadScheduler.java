package at.ac.tuwien.e0426099.simulator.environment.zone.processor.scheduler;

import at.ac.tuwien.e0426099.simulator.environment.EnvConst;
import at.ac.tuwien.e0426099.simulator.environment.zone.processor.entities.CoreDestination;
import at.ac.tuwien.e0426099.simulator.environment.zone.processor.entities.ProcessingCoreInfo;
import at.ac.tuwien.e0426099.simulator.helper.comparators.ProcessingInfoLoadComparator;
import at.ac.tuwien.e0426099.simulator.environment.task.entities.SubTaskId;
import at.ac.tuwien.e0426099.simulator.helper.Log;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author PatrickF
 * @since 07.12.12
 */
public class FifoLeastLoadScheduler implements IScheduler{
	private Log log = new Log(this, EnvConst.VERBOSE_LOG_MODE_GENERAL && EnvConst.VERBOSE_LOG_MODE_SCHEDULER);
	private ConcurrentLinkedQueue<SubTaskId> taskQueue;

	public FifoLeastLoadScheduler() {
		taskQueue=new ConcurrentLinkedQueue<SubTaskId>();
	}

	@Override
	public void addToQueue(SubTaskId subTaskId) {
		taskQueue.offer(subTaskId);
	}

	@Override
	public CoreDestination getNext(List<ProcessingCoreInfo> coreInfos) {
		log.v("Schedule next task");
		if(coreInfos != null && !coreInfos.isEmpty() && !taskQueue.isEmpty()) {
			Collections.sort(coreInfos,new ProcessingInfoLoadComparator());

			for(ProcessingCoreInfo info:coreInfos) {
				if(info.getLoad() < 1 && info.getCurrentRunningTasks() < info.getMaxConcurrentTasks()) {
					log.v("Schedule next task to: "+info);
					return new CoreDestination(info.getId(),taskQueue.poll());
				}
			}
		}
		log.v("Nothing more to schedule. Tasks leaving in queue: "+taskQueue.size());
		return null;
	}

    @Override
    public boolean hasTaskLeftToSchedule() {
        return !taskQueue.isEmpty();
    }

	@Override
	public String toString() {
		return "[FifoScheduler]";
	}
}