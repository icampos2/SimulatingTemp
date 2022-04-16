/*
 * This class sets up 10 servers represented by Server objects, 
 * initialized with three tasks.
 * This class assigns requests among the servers, and allows for removing
 * the most recently assigned tasks. 
 * 
 * There's a function to compute the overall rack temperature and a function
 * to compute the temperature for a single server both taking parameters to 
 * allow for a change in task amount to check how the temperature will change
 * 
 * Also saves history of a server (all of it) but returns the most recent hour of 
 * information (average temperature over a minute) 
 * 
 * The number of tasks a server can have is restricted to 30 and 
 * for the whole rack 300 tasks 
 * */

import java.util.ArrayList;
import java.util.HashMap;

public class ServerRack {
	

	
	private int scount ;
	private int racknum;
	private int p0 = 100;
	private int pw = 200;
	private double serverL = 30.0; // limit of request rate on a given server 30 per sec
	private double rackL = 900.0;
	private double tc = 1/180;
	private double[] effect=  {0.00025, 0.00024, 0.00023, 0.00022, 0.00021, 0.00020, 0.00019, 0.00018, 0.00017,0.00016 };
	private ArrayList<Server> info ;
	private double inlet = 68; //68 degrees F for incoming temp
	
	
	
	// parameters are for the rack number/name and the number of servers on the rack (10)
	public ServerRack(int name, int snum) {
		racknum = name;
		scount = snum;
		
		info = new ArrayList<Server>();
		for (int i = 0; i < scount; i++) {
			info.add(new Server(3, i+1));// initialized with 3 requests, numbered 1 to 10 
		}
		//sets up hashmap to save history for each server in this rack
		double each = 0.0;
		for (int i = 0; i < scount; i++) {
			each = TempServer(i, 0, false);
			
		}
	}
	
	public int name () {
		return racknum;
	}
	// limit set on how many tasks per rack can be handled
	// returns limit - current tasks 
	public double spaceLeft(){
		return rackL - rackCount();
	}
	
	// computes the overall rack temperature using information from server temps
	public double TempRack(int change, int val, boolean diff) {
		//parameters allow for changing requests on one server
		double Tout =  ( inlet)*(10.0/180.0); // Temp from calculation 
		int total;
		double each = 0.0;
		double out;
		double v;
		for (int i = 0 ; i< scount; i++) {
			if (i == change) {
				total = val;
			}else {
				total = info.get(i).tasks();
			}
			double w = total/serverL;
			double P = p0 + (pw*w);
			out = ((effect[i]*P)); // amount of temp contribution given workload
			v = TempServer(i, total, false);
			Tout += out ;
			each += v ;
			
		}
		each= each/10.0; //average temp
		//System.out.println(Tout + " other "+ each + " overall? "+ (each+Tout));
		Tout+=each;
		return Tout;
	}
	
	
	//computes  the temperature of the given server based on number of tasks (also given)
	// if not given the number of tasks, based on diff, it gets looked up
	public double TempServer(int i, int work, boolean diff) {
		double out = 0.0;
		if (diff == false) {
			work = info.get(i).tasks();
		}
		double w = work/serverL;
		double P = p0 + (pw*w);
		out = inlet + ((effect[i]*P)*180.0);
		return out;
	}
	
	// returns the sum of tasks (from all servers) 
	//tasks for this rack
	public int rackCount() {
		int num=0;
		for (int i =0; i< scount;i++) {
			num +=info.get(i).tasks();
		}
		return num;
	}
	
	// chosen to move requests to this rack
	//needs to find the server that can handle requests if not divide up requests
	public void moveTo(int given) {
		int smallest = 0;
		int save = 0;
		while (given > 0) {
			save = 0;
			smallest = 5;
			for (int i = 0; i< scount; i++) {
				if((info.get(i).tasks()+ given) <= serverL) {
					info.get(i).give(given);
					return;
				}
				if (info.get(i).tasks()< smallest ) {
					smallest = smallest - info.get(i).tasks();
					save = i;
				}
		}
		
			info.get(save).give(smallest);
			given -= smallest;
		}
	
		
	}
	
	// Requests are given to this rack and need to be evenly distributed 
	//among the ten servers
	public void router(int howMany) {
		int per = howMany/scount;
		
			for (int i = 0; i< scount; i ++) {
				info.get(i).update(per);
				
			}
		
	}

	
	//removes most recent requests from the chosen server
	//if there's a problem with the temperature move most recently adding tasks
	public void remove(int i) {
		double amount = info.get(i).tasks();
		if (amount != 0) {
			info.get(i).remove();
	
		}
	}
	
	//find a server with the largest number of tasks to give away
	public int findServer() {
		int found = 0;
		int num = 0;
		
		int check;
		for ( int i = 0; i < scount; i++) {
			check = info.get(i).avaliable();
			if (check > num) {
				found = i;
				num = check;
			}
			
		}
		return found;
	}
	
	public int tasksServer(int i) {
		return info.get(i).avaliable();
	}
	
	//task count for a server
	public int work(int i) {
		return info.get(i).tasks();
	}
	
	
	
	
}
