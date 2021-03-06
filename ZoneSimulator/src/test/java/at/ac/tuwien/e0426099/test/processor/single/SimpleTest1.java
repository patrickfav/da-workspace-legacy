package at.ac.tuwien.e0426099.test.processor.single;

import at.ac.tuwien.e0426099.simulator.environment.Env;
import at.ac.tuwien.e0426099.simulator.environment.zone.ZoneId;
import at.ac.tuwien.e0426099.simulator.environment.task.Task;
import at.ac.tuwien.e0426099.simulator.environment.task.interfaces.ITask;
import at.ac.tuwien.e0426099.test.processor.AProcessorTest;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * @author PatrickF
 * @since 13.12.12
 */
public class SimpleTest1 extends AProcessorTest{

	@Test(timeout = 60000)
	public void testSubTaskStartShouldFinish() {
		ZoneId id = Env.get().addZone("local", defaultDualCoreUnit);

		ITask task1 = new Task("No1");

		task1.addSubTask(subTask1);
		task1.addSubTask(subTask3);
		task1.addSubTask(subTask4);

		ITask task2 = new Task("No2");
		task2.addSubTask(subTask2);
		task2.addSubTask(subTask5);

		Env.get().getZone(id).addTask(task1);
		Env.get().getZone(id).addTask(task2);

        Env.get().start();

		assertTrue(Env.get().waitForFinish());
	}
}
