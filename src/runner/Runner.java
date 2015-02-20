package runner;
import java.util.*;
import java.lang.management.*;
import java.io.*;

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
		try {
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
    if( args.length != 1 ) {
      System.err.println("usage: <command-list_file>");
      System.exit(1);
    } 

		Runner runner = new Runner();

    try {
	    BufferedReader br = 
        new BufferedReader(new FileReader( new File( args[0])));	
  
        String command = null;
        int command_num = 1;
        while(( command = br.readLine()) != null ) {
          runner.AddJob( command, "command #" + command_num ++, 1.0 );
        }    
        br.close();
    } catch ( Exception e ) {
      System.err.println("Exception reading file: " + e.toString() );
      System.exit(1);
    }

		runner.AddRunThread("Thread1");
		runner.AddRunThread("Thread2");
		runner.AddRunThread("Thread3");
		runner.AddRunThread("Thread4");
		
		runner.WaitToComplete();
		System.out.println("All Jobs ran");
	}

}
