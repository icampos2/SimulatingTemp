/*
 * Creates DataCenter object and Workload object to simulate incoming tasks
 * and assigning them to racks (and then to servers).
 * 
 * 
 * boolean optimization indicates whether or not the tasks will get moved if their server/rack gets too hot
 * if optimization = false , information is saved in file "individual.txt"
 * if optimization = true, information is saved in "second.txt"
 * 
 * //row 1 is racks 1-7
	//row 2 is racks 8 -14
	//row 3 is racks 15-21
	//row 4 is racks 22-28
 * It saves history of each server from each rack, the rack is named 1 to 28
 * and servers are marked 1 to 10 within its given rack
 * 
 * 
 * runs an "hour" of requests saves every temp in the text file (mentioned above)
 * Format:
 * row number
 * rack1 temp work fromRackNum givenWork
 * serverNum temp work
 * ....nine more servers
 * rack2 ...
 * ....
 * ...... 5 more racks
 * row2
 * ... two more rows
 * 
 * -> when it loops back to row 1, another minute has started
 * 
 * 
 * Currently saves info for one hour in minutes so 60 data entries in the arraylists of temps
 * There are 24 hours to go through, to get a different hourL change the parameter time
 * Current does the first hour, time = 1
 * Second hour would be time =2 and so on
 * 
 * 
 * work hash map has same structure as data but holds the number tasks instead of temps
 * */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Simulate {
	static String[] nums = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
			"11", "12", "13", "14", "15", "16", "17", "18", "19", "20", 
			"21", "22", "23", "24", "25", "26", "27", "28"};
	
	//outer is rack#
		//inner is server #
		// "server" 0 is the list of rack temps 
		// server 1-10 is the temps for that given server
		// arraylist contains temp 
	static HashMap<Integer, HashMap<Integer, ArrayList<Double>>> data;
	static HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> work;
	
	
	 public static void saveInfo(FileWriter wr, int time, DataCenter dc) throws IOException{
		 
		 int name =0;
		 ArrayList<Double > row;
		 ArrayList<Double> rack;
		 String label ="";
		 String change="";
		 String work ="";
		 int rack2 =0;
		 int wo = 0;
		 ArrayList<Integer> more;
			for (int i = 0; i< 4; i++) {
				// write row
				label= nums[i];
				wr.append(label+ "\n");
				//get info
				row= dc.rowInfo(i);
				for (int j = 0 ; j < 7; j++) {
					//write rack temp from row array
				
					more = dc.getRackChange(name+1);
					if (more.size()>1) {
						rack2 = more.get(1);
						wo = more.get(2);
					}else {
						rack2 = 0;
						wo = 0;	
					}
					
					label =nums[name];
					change = Double.toString(row.get(j));
					work = Integer.toString(dc.rackTask(i, j));
					// new addition to end of line for rack info, rack it got work from and the amount
					wr.append(label + " "+ change +" "+work+" "+rack2+ " "+wo+"\n");
					name+=1;
					
					rack = dc.rackInfo(i, j);
					
					for (int a = 0; a < 10; a++) {
						// write server temp from  rack
						label = nums[a];
						change = Double.toString(rack.get(a));
						work = Integer.toString(dc.serverTask(i, j, a));
						wr.append(label + " "+change +" "+work+ "\n");
					}
				}
			}
	 }
	 
	 
	public static void main(String[] args) {
		int time = 1;// which hour of information
		boolean optimization = false; // modification of workloads 
		
		DataCenter room = new DataCenter();
		Workload info = new Workload(time);
		File alone ;
		if (!optimization) {
			//false
			alone = new File("individual.txt");
			
		}else {
			//true
			alone = new File("second.txt");
		}
		try {
			if(!alone.createNewFile()) {
				alone.delete();
				alone.createNewFile();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		FileWriter fw;
		try {
			fw = new FileWriter(alone);
			int seconds =0;
			saveInfo(fw, seconds, room);

			int requests = 0;
			
			for (int k = 0; k< 60; k++) { // minutes
				
				if (k%5 == 0) { // every five minutes requests amounts change
					requests = info.requests();
				}
				
				seconds+=1;
				room.Simulate(requests*28, optimization,250);////300
				saveInfo(fw, seconds, room);
			}
		
		
		fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//collectData();
		
	}
	
	public static void collectData() {
		data =new HashMap<Integer, HashMap<Integer, ArrayList<Double>>>();
		HashMap<Integer, ArrayList<Double>> inner;
		
		work =new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		HashMap<Integer, ArrayList<Integer>> inw;
		
		ArrayList<Double> ti;
		ArrayList<Integer> wi;
		for ( int i =0; i < 28; i++) {
			inner = new HashMap<Integer, ArrayList<Double>>();
			inw = new HashMap<Integer, ArrayList<Integer>>();
			for (int k = 0; k < 11; k++) {
				ti = new ArrayList<Double>();
				inner.put(k, ti);
				wi = new ArrayList<Integer>();
				inw.put(k, wi);
			}
			data.put(i+1, inner);
			work.put(i+1, inw);
		}
		
		//open file and place data together
		File f = new File("individual.txt");
		try {
			String[] info;
			int name;
			int task;
			double tp;
			int count =0;
			int rack=1;
			Scanner sc = new Scanner(f);
			while(sc.hasNextLine()) {
				info = sc.nextLine().split(" ");
				if (info.length < 3) {
					//row label
					//skip
					count =0;
					info=sc.nextLine().split(" ");
					//System.out.println("row");
				}
				name = Integer.valueOf(info[0]);
				tp = Double.valueOf(info[1]);
				task = Integer.valueOf(info[2]);
				//System.out.println("name == "+name);
				if ((count%11) ==0) {
					//rack
					rack = name;
					data.get(rack).get(0).add(tp);
					work.get(rack).get(0).add(task);
				}else  {
					// server
					//System.out.println("server "+name + " for rack "+rack);
					data.get(rack).get(name).add(tp);
					work.get(rack).get(name).add(task);
				}
				count +=1;
				
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
