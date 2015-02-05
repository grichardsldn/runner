package runner;

public interface RunnerAdminMBean {
	public int getInitalJobs();
	public int getRemainingJobs();
	public int getJobsRan();
	public int getNumThreads();
}
