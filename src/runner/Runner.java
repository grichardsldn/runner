package runner;
import java.util.*;
import java.lang.management.*;

import javax.management.*;

public class Runner {

	Set<Job> jobs_to_run = new HashSet<Job>();
	List<Thread> thread_list = new ArrayList<Thread>();
	private RunnerAdmin mbean;
	
	public int getNumJobsRemaining()
	{
		synchronized( jobs_to_run)
		{
			return jobs_to_run.size();
		}
	}

	/** Take a job from the jobs_to_run set.
	 * 
	 * @return The Job that was removed or null if none left.
	 */
	public Job getNextJob()
	{
		synchronized(jobs_to_run)
		{
			if( jobs_to_run.isEmpty())
			{
				return null;
			}
			Iterator<Job> i = jobs_to_run.iterator();
			Job job = i.next();
			
			jobs_to_run.remove( job);
			
			return job;
		}
	}
	
	public Runner()
	{
		mbean = new RunnerAdmin();
		mbean.setRunner( this);
		
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		try
		{
			server.registerMBean( mbean, 
				new ObjectName("com.avaya.runner:type=RunAdmin"));
		}
		catch (Exception e)
		{
			System.out.println("createMBean: Exception " + e);
		}
	}
	public void AddJob(String command_line, String name, double work_units)
	{
		Job job = new Job(command_line, name, work_units);
		
		synchronized(jobs_to_run)
		{
			jobs_to_run.add(job);
		}
	}
	
	public void AddRunThread( String name)
	{

		
		System.out.println("Adding RunThread " + name);
		
		RunThread runthread = new RunThread(this, name);
		Thread thread = new Thread(runthread);
		thread.start();
		
		thread_list.add(thread);
	}
	
	public void WaitToComplete()
	{
		System.out.println("Waiting to complete");
		
		for(;;)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch( InterruptedException e)
			{
				break;
			}
			synchronized( jobs_to_run)
			{
				if( jobs_to_run.size() == 0)
				{
					break;
				}
			}
		}
	}
	
		
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{	
		Runner runner = new Runner();
		
		for( int i = 1 ; i <= 6000 ; i++)
		{
			String num = String.format("%06d", i);
			runner.AddJob("convert /tmp/hurt/Main/main_"+ num+ ".jpg "+
					"-sharpen 0x0.2 -contrast-stretch 10%x90% /tmp/hurt/Main/out/main_" +
					num + ".jpg", "processing "+ num, 1.0);
		}
		
		runner.AddRunThread("Thread1");
		runner.AddRunThread("Thread2");
		runner.AddRunThread("Thread3");
		runner.AddRunThread("Thread4");
		
		runner.WaitToComplete();
		System.out.println("All Jobs ran");
	}

}
