package runner;

public class RunThreadAdmin implements RunThreadAdminMBean {

	private RunThread run_thread = null;
	
	void setRunThread( RunThread run_thread )
	{
		this.run_thread = run_thread;
	}
	
	@Override
	public int getJobsRan() {
		int ret = -1;
		
		if( run_thread != null)
		{
			ret = run_thread.getJobsRan();
		}
		return ret;
	}

	@Override
	public String getName() {
		if( run_thread == null)
		{
			return null;
		}
		
		return run_thread.name;
	}

}
