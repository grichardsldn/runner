package runner;

public class RunnerAdmin implements RunnerAdminMBean {

	private Runner runner = null;
	
	protected void setRunner( Runner runner)
	{
		this.runner = runner;
	}
	
	@Override
	public int getInitalJobs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getJobsRan() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumThreads() 
	{
		return runner.thread_list.size();
	}

	@Override
	public int getRemainingJobs() 
	{
		return runner.getNumJobsRemaining();
	}

}
