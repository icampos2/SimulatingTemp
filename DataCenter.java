/*
 * This class sets up four rows of racks, seven racks per row,
 * 10 servers per rack. 
 * This class calls ServerRack (representing a rack) objects to return array lists of 
 * temperatures for a row of racks, for all servers in a chosen rack and 
 * history of temperature for a chosen server.
 * This class is given the number of requests per second to give to the racks,
 * and notify the remaining that time has passed if they don't get any requests.
 * 
 * Also has the algorithm to check for possible hot spots and move requests as needed
 * */

import java.util.ArrayList;
import java.util.HashMap;

public class DataCenter {
	private int racks = 7;
	private int rows = 4;
	private int servers = 10;
	public ArrayList<ArrayList<ServerRack>> layout;
	int row = 0;
	int rack = 0;
	private HashMap<Integer,ArrayList<Integer>> moving ;
	
	
	public DataCenter() {
		//System.out.println("initial requests");
		layout = new ArrayList<ArrayList<ServerRack>>();
		int name = 1;
		for (int i = 0; i < rows; i ++) {
			ArrayList<ServerRack> line = new ArrayList<ServerRack>();
			for (int j = 0; j < racks; j++) {
				ServerRack each = new ServerRack(name, servers);
				name += 1;
				line.add(each);
			}
			layout.add(line);
		}
		Simulate(840, false, 30);
	}

	private void redo () {
		moving = new HashMap<Integer, ArrayList< Integer>>();
		ArrayList< Integer> t;
		for (int i = 0; i < 28; i++) {
			t = new ArrayList< Integer>();
			t.add(-1);
			moving.put(i+1, t);
		}
	}
	
	public ArrayList<Integer> getRackChange(int r){
	
		return moving.get(r);
	}
	
	
	public void Simulate(int givenRequests, boolean move, int each) {
		
		//move determines if algorithm to move tasks among the servers is used
		//true for yes
		int i = 0;
		int perRack ;
		int num = 0;
		double save;
		int start = row;
		int end = rack;
		while ( i < givenRequests) {
			perRack = each;
			// give each ten and when it runs outs
			num = givenRequests - i;
			if (num< perRack) {
				perRack = num; 
			}
			save = layout.get(row).get(rack).spaceLeft();
			if (save < perRack) {
				perRack = (int) save;///?
			}
			//System.out.println(perRack + " requests done "+i +" "+ givenRequests );
			layout.get(row).get(rack).router(perRack);
			rack +=1;
			if (rack == 7) {
				row +=1;
				rack = 0;
				if (row == 4) {
					row = 0;
				}
			}
			i+= perRack;//more?
		}
		//notify remaining time has changed
		
		int tempRack = rack;
		int tempRow = row;
		boolean going = true;
		if(rack==end && row==start) {
			going =false;
		}
		while (going) {
			layout.get(tempRow).get(tempRack).router(0);
			tempRack+=1;
			if (tempRack == 7) {
				
				tempRack = 0;
				tempRow+= 1;
				if (tempRow == 4) {
					tempRow = 0;
				}
			}
			if (tempRow == start && tempRack == end) {
				going = false;
			}
		}
	
		redo();
		
		if (move) {
			change();
		}
		
		
	}
	
	// contains algorithm that looks for possible hotspots and new homes for tasks
	// it finds the "max" if neighbors are not different and gives it to "min" rack
	private void change() {
		int maxRack =0 ;
		int minRack = 0;
		double maxt = 60.0;
		double mint = 100.0;
		double num= 0;
		int save = 0;
		int save1 = 0;
		int a1 = 0;
		int a2 = 0;
		
		ArrayList<Double > arow;
		for (int i = 0; i < 4; i++) {
			arow = rowInfo(i);
			for (int k = 0; k < 7; k++) {
				num = arow.get(k);
				if (Double.compare(num, maxt)>0 ) {
					maxt = num;
					save = i;
					a1 = k;
					maxRack = layout.get(i).get(k).name();
				}else if (Double.compare(mint, num)>0) {
					mint= num;
					save1 = i;
					a2 = k;
					minRack = layout.get(i).get(k).name();
				}
			}
		}
		
		if (minRack != maxRack) {
			if ( (maxt-mint)>4.5) {
				int ser1 = layout.get(save).get(a1).findServer();
				int move = layout.get(save).get(a1).tasksServer(ser1);
				layout.get(save).get(a1).remove(ser1);
				layout.get(save1).get(a2).moveTo(move);
				moving.get(minRack).add(maxRack);
				moving.get(minRack).add(move);
				//System.out.println(" move "+ move + " to "+ minRack+ " f rom "+ maxRack);
			}
		}
	
	}
	
	// returns temperature of the racks in a chosen row
	//increased temperature of middle rows because they share a hot aisle
	//increased all racks to simulate the temperature being affected by neighbors
	public ArrayList<Double> rowInfo( int row){
		ArrayList<Double> tempRack = new ArrayList<Double>();
		double val;
		for (int i = 0; i< racks ; i++) {
			val = layout.get(row).get(i).TempRack(-1, 0, false);
			if ((row == 1) || (row == 2)) {
				val +=0.4; //to simulate that they share a hot aisle
			}
			if((i ==2) ||(i==3)||(i==4)) {
				val+=0.5; // to simulate inner racks along a given row
			}
			val+= 0.3; //to accommodate being next to each other
			tempRack.add(val);
		}
		
		return tempRack;
	}
	
	//returns temps of all server in the chosen rack
	public ArrayList<Double> rackInfo(int row, int rack){
		// returns list of temperatures for a rack to show servers temp
		ArrayList<Double> SinR = new ArrayList<Double>();
		double val;
		for (int i = 0; i< servers; i++) {
			val = layout.get(row).get(rack).TempServer(i, 0, false);
			SinR.add(val);
		}
		return SinR;
		
	}
	
	public int rackTask(int row, int rack) {
		return layout.get(row).get(rack).rackCount();
	}
	
	public int serverTask(int row, int rack, int ser) {
		return layout.get(row).get(rack).work(ser);
	}
	
	
	
	
	
}
