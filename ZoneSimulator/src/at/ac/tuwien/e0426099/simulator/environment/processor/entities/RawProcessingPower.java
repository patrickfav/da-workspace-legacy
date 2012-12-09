package at.ac.tuwien.e0426099.simulator.environment.processor.entities;

/**
 * @author PatrickF
 * @since 07.12.12
 */
public class RawProcessingPower {
	private long computationsPerMs;

	public RawProcessingPower(long computationsPerMs) {
		this.computationsPerMs = computationsPerMs;
	}

	public long getComputationsPerMs() {
		return computationsPerMs;
	}

	/**
	 * Returns the processing power with given penalty.
	 * Penalty is a double between 0.0 and 1.0 where
	 * 1.0 means 100% meaning 0 processing power and
	 * 0.0 means full power.
	 *
	 * @param penalty
	 * @return
	 */
	public double getComputationsPerMs(double penalty) {
		if(penalty >= 1) {
			return 0;
		} else if(penalty <= 0) {
			return getComputationsPerMs();
		} else {
			return Math.floor(((double) computationsPerMs)*penalty);
		}
	}

	public long getComputationsDone(long timeInMs) {
		return computationsPerMs * timeInMs;
	}

	public long getEstimatedTimeInMsToFinish(ProcessingRequirements requirements) {
		long computationsPerMs = Math.min(requirements.getMaxComputationalUtilization().getComputationsPerMs(),this.computationsPerMs);
		return (long) Math.ceil(requirements.getComputationNeedForCompletion() / computationsPerMs);
	}

	public long getEstimatedTimeInMsToFinish(long computationsNeeded) {
		return (long) Math.ceil(computationsNeeded / computationsPerMs);
	}
}
