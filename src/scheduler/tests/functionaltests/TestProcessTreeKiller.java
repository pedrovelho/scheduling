/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionaltests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.ow2.proactive.scheduler.common.job.JobId;
import org.ow2.proactive.scheduler.common.job.JobResult;
import org.ow2.proactive.scheduler.common.job.JobStatus;
import org.ow2.proactive.scheduler.common.job.TaskFlowJob;
import org.ow2.proactive.scheduler.common.task.NativeTask;

import functionalTests.FunctionalTest;


/**
 * @author ProActive team
 *
 * this test check whether ProcessTreeKiller kill properly detached processes launched
 * by a native Task, and only processes launched by this native task.
 *
 */
public class TestProcessTreeKiller extends FunctionalTest {

    private static String nativeLinuxExecLauncher = TestProcessTreeKiller.class.getResource(
            "/functionaltests/executables/PTK_launcher.sh").getPath();

    private static String unixPTKProcessName = "PTK_process.sh";

    private static String nativeLinuxDetachedProcess = TestProcessTreeKiller.class.getResource(
            "/functionaltests/executables/" + unixPTKProcessName).getPath();

    private static int detachedProcNumber = 4;

    private final static int wait_time = 6000;

    /**
     * Tests start here.
     *
     * @throws Throwable any exception that can be thrown during the test.
     */
    @org.junit.Test
    public void run() throws Throwable {
        killAll(unixPTKProcessName);

        SchedulerTHelper.log("Test 1 : Creating jobs...");

        //create job 1
        TaskFlowJob job1 = new TaskFlowJob();
        job1.setName("Test PTK1");
        job1.setDescription("a command that launches detached commands");

        NativeTask task1 = new NativeTask();
        String task1Name = "TestPTK1";
        task1.setName(task1Name);
        task1.setCommandLine(new String[] { nativeLinuxExecLauncher });
        job1.addTask(task1);

        //create job 2
        TaskFlowJob job2 = new TaskFlowJob();
        job2.setName("Test PTK2");
        job2.setDescription("a command that launches detached commands");

        NativeTask task2 = new NativeTask();
        String task2Name = "TestPTK2";
        task2.setName(task2Name);
        task2.setCommandLine(new String[] { nativeLinuxExecLauncher });
        job2.addTask(task2);

        SchedulerTHelper.setExecutable(nativeLinuxExecLauncher + " " + nativeLinuxDetachedProcess);

        //submit two jobs
        JobId id1 = SchedulerTHelper.submitJob(job1);
        SchedulerTHelper.waitForEventTaskRunning(id1, task1Name);
        JobId id2 = SchedulerTHelper.submitJob(job2);
        SchedulerTHelper.waitForEventTaskRunning(id2, task2Name);

        Thread.sleep(wait_time);

        int runningDetachedProcNumber = getProcessNumber(unixPTKProcessName);

        //we should have 2 times (2 jobs) number of detached processes
        SchedulerTHelper.log("number of processes : " + runningDetachedProcNumber);
        Assert.assertEquals(detachedProcNumber * 2, runningDetachedProcNumber);

        //kill the first job
        SchedulerTHelper.getSchedulerInterface().killJob(id1);
        SchedulerTHelper.waitForEventJobFinished(id1);

        //we should have 1 time number of detached processes
        runningDetachedProcNumber = getProcessNumber(unixPTKProcessName);
        SchedulerTHelper.log("number of processes : " + runningDetachedProcNumber);
        Assert.assertEquals(detachedProcNumber, runningDetachedProcNumber);

        //kill the second job
        SchedulerTHelper.getSchedulerInterface().killJob(id2);
        SchedulerTHelper.waitForEventJobFinished(id2);

        //we should have 0 detached processes
        runningDetachedProcNumber = getProcessNumber(unixPTKProcessName);
        SchedulerTHelper.log("number of processes : " + runningDetachedProcNumber);
        Assert.assertEquals(0, runningDetachedProcNumber);

        JobResult res = SchedulerTHelper.getJobResult(id1);
        Assert.assertEquals(JobStatus.KILLED, res.getJobInfo().getStatus());

        res = SchedulerTHelper.getJobResult(id2);
        Assert.assertEquals(JobStatus.KILLED, res.getJobInfo().getStatus());
    }

    private void killAll(String processName) throws Throwable {
        byte[] out = new byte[1024];
        //get PIDs of processName
        Process p = Runtime.getRuntime().exec("pidof " + processName);
        int n = p.getInputStream().read(out);
        //contains PIDs separated with spaces
        if (n > 0) {
            String pids = new String(out, 0, n);
            if (pids != null && pids.length() > 1) {
                //kill this processes
                Runtime.getRuntime().exec("kill " + pids);
            }
        }
    }

    private int getProcessNumber(String executableName) throws IOException {
        int toReturn = 0;
        String line;
        Process p = Runtime.getRuntime().exec("ps -e");
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = input.readLine()) != null) {
            if (line.contains(executableName)) {
                toReturn++;
            }
        }
        input.close();
        return toReturn;
    }
}