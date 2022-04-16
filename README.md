# SimulatingTemp
CS552 project 
Simulate uses DataCenter and Workload to produce a text file of information 
  Simulate asks the workload for the number of requests per second (treated as minute), tells the Datacenter how many requests it got
   Datacenter handles the racks, the ServerRack handles the servers
There's two parameters to change inside Simulate
1) boolean optimization: if false, does not modify the workload and writes to "individual.txt"
                      if true, moves workloads in attempt to lower highest temp, writes to "second.txt"
2) int time: indictes the hour we want to look at, 24 total hours, for first hour, time = 1 for second time =2 ........
