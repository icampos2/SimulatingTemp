/*
 * Keeps track of "time" to know when a minute is up to average the temp
 * which gets asked for to save in the ServerRack class
 * So this class saves information about the tasks it has and the time 
 * they have "left" 
 * Returning most tasks added (in slot 0) as candidates to move to another server
 * and removes them if told to
 * If in slot 3, they get kicked out in the next "second" so no need to move them
 * 
 * 
 * */

public class Server {
	private int total = 0;
	private int[] requests = {0, 0, 0};
	private int number ;
	
	
	
	public Server(int initial, int name) {
		requests[0] = initial;
		total = initial;
		number = name;
		
	}
	
	public int tasks() {
		return total;
	}
	
	
	
	public int avaliable() {
		return requests[0]/2;// extend further?
		
	}
	
	public void give(int moreTasks) {
		requests[0] += moreTasks;
		total += moreTasks;
	}
	
	
	
	
	public void remove() {
		int amount = avaliable();
		requests[0] -= amount;// tasks half the tasks away
		total -= amount;
		
	}
	
	public void update(int given) {
		// a second passed either got new requests or 0 for no new requests
		// "shift requests"
		int next;
		total -= requests[2];
		total += given;
		for (int i = 0; i <3; i++) {
			next = requests[i];
			requests[i] = given;
			given = next;
		}
		
		
	
		
		
	}
	
	

}
