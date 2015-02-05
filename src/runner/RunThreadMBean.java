package runner;

import java.io.Serializable;

public interface RunThreadMBean
{
	public void setRunThread( RunThread run_thread);

	public String getName();

	public int getJobsRan();
}
