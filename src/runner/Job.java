package runner;

public class Job implements Runnable
{
	JobState state = JobState.CREATED;
	double work_units = 1.0;
	String name;
	
	private String command_line;
	
	public Job( String command_line, String name, double work_units)
	{
		this.command_line = command_line;
		this.work_units = work_units;
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName( String new_name )
	{
		name = new_name;
	}

	public JobState getState()
	{
		return state;
	}
	
	public void setState( JobState new_state)
	{
		state = new_state;
	}
	
	public void run()
	{
		System.out.println( name + ": running " + command_line);
		setState(JobState.STARTED);
		try
		{
			Process process = Runtime.getRuntime().exec(command_line);
			process.waitFor();
			if (process.exitValue() != 0)
			{
				//System.out.println("GDR: Process returned non-zero");
				setState(JobState.FAILED);
			}
		}
		catch (Exception e)
		{
			setState(JobState.FAILED);
			System.out.println("Exception:" + e.toString());
		}

		if( state != JobState.FAILED)
		{
			setState(JobState.COMPLETED);
		}
	}
}
