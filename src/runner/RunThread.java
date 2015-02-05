package runner;

import java.lang.management.ManagementFactory;
import java.util.*;

import javax.management.MBeanServer;
import javax.management.ObjectName;
public class RunThread implements Runnable {

	final Runner runner;
	int jobs_ran = 0;
	final String name;
	RunThreadAdmin mbean;
	
	public int getJobsRan()
	{
		return jobs_ran;
	}
	
	RunThread( Runner runner, String name)
	{
		this.runner = runner;
		this.name = name;
		
		mbean = new RunThreadAdmin();
		mbean.setRunThread( this);
		
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		try
		{
			server.registerMBean( mbean, 
				new ObjectName("com.avaya.runner:type=RunThread,thread=" + name));
		}
		catch (Exception e)
		{
			System.out.println("createMBean: Exception " + e);
		}
		
	}
	
	@Override
	public void run() 
	{
		System.out.println("RunThread "+ name +" started.");
		for (;;)
		{
			Job job = runner.getNextJob();
			
			if( job == null)
			{
				break;
				
				// wait 1 second and try again
				/*
				try
				{
					System.out.println("RunThread " + name + ": no job to run, sleeping");
					Thread.sleep(1000);
				}
				catch( InterruptedException e)
				{
					System.out.println("sleep: " + e);
				}
				*/
			}
			else
			{
				//System.out.println("RunThread " + name + ":");
				
				job.run();
				jobs_ran ++;
			}
		}
	}

}
