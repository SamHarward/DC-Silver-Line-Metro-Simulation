import java.util.HashMap;
import java.util.Vector;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

public class MainClass {
	public static void main(String argv[]) {
		String output_path = "output.csv";
		String station_output_path = "station_output.csv";
		boolean is_output_run = true;
		int runs = 500;
		try {
			Files.deleteIfExists(Paths.get(output_path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Files.deleteIfExists(Paths.get(station_output_path));
		} catch (IOException e) {
			e.printStackTrace();
		}

		PrintWriter output = null;
		try {
			output = new PrintWriter(new FileWriter(output_path, true));
		} catch (IOException e) {
			System.out.print(e.getStackTrace());
			return;
		}

		PrintWriter station_output = null;
		try {
			station_output = new PrintWriter(new FileWriter(station_output_path, true));
		} catch (IOException e) {
			System.out.print(e.getStackTrace());
			output.close();
			return;
		}
		
		for(int run_id = 0; run_id < runs; ++run_id) {
			Simulator ss = new Simulator(run_id);
			Vector<Double> travel_times = new Vector<Double>();
			
			output.println("Clock,Event Type,Start Station,End Station,Customer Start Time,Train ID,Train Utilization");
			station_output.println("Clock,Wiehle-Reston East,Spring Hill,Greensboro,Tysons Corner,McLean,East Falls Church,Ballston-MU,Virginia Sq-GMU,Clarendon,Court House,Rosslyn,Foggy Bottom,Farragut West,McPherson Sq,Metro Center,Federal Triangle,Smithsonian,L'Enfant Plaza,Federal Center SW,Capitol South,Eastern Market,Potomac Ave,Stadium-Armory,Benning Rd,Capitol Heights,Addison Road,Morgan Boulevard,Largo Town Center");
	
			while(ss.clock <= 1110) {
				Event e = ss.FutureEventList.getMin();  //get imminent event
				if(e != null) {
					ss.FutureEventList.dequeue();           //delete the event
					ss.clock = e.get_time();                //advance in time
					if (e.get_type() == "Train Arrival") {
						HashMap<String, Integer> d = e.get_data(); 
						Train   t = ss.trains[d.get("train_id")];
						Station s = ss.stations[d.get("station_id")];
						if(is_output_run) {
							output.println(String.valueOf(ss.clock) + ","
									+ "Train Arrival"
									+ ",,,,"
									+ t.train_id + ","
									+ String.valueOf(t.get_utilization()));
						}
						// customers who reached their destination
						Vector<Customer> departed = t.set_station(s.station_id);
						for(int i = 0; i < departed.size(); ++i) {
							Customer c = departed.get(i);
							travel_times.add(ss.clock - c.time_start);
							if(is_output_run) {
								output.println(String.valueOf(ss.clock) + ","
										+ "Customer Departure,"
										+ c.get_start() + ","
										+ c.get_end()   + ","
										+ c.time_start + ",,");
							}
						}
						HashMap<String, Integer> nd = new HashMap<String, Integer>(); 
						nd.put("train_id", t.train_id);
						nd.put("station_id", s.station_id);
	
						Event depart = new Event("Train Departure",
											nd,
											ss.clock + Simulator.station_service_time);
						
						ss.FutureEventList.enqueue(depart);
						int[] stations_occupied = new int[Simulator.n_stations];
						for(int i = 0; i < Simulator.n_trains; ++i) {
							int station = ss.trains[i].current_station;
							stations_occupied[station] = 1;
						}
						if(is_output_run) {
							station_output.print(String.valueOf(ss.clock) + ",");
						}
						for(int i = 0; i < Simulator.n_stations; ++i) {
							if(i == Simulator.n_stations - 1) {
								station_output.print(stations_occupied[i] + "\n");
							} else {
								station_output.print(stations_occupied[i] + ",");
							}
						}
					}
					else if(e.get_type() == "Train Departure") {
						HashMap<String, Integer> d = e.get_data(); 
						Train   t = ss.trains[d.get("train_id")];
						Station s = ss.stations[d.get("station_id")];
	
						// customers who are boarding (as many as can fit on the train)
						Vector<Customer> boarders = s.get_boarders(t.direction);
						Vector<Customer> overflow = t.board(boarders);
						s.on_platform.addAll(overflow);
						
						ss.FutureEventList.enqueue(ss.move_train(t.train_id));
					}
					else if(e.get_type() == "Customer Arrival") {
						HashMap<String, Integer> d = e.get_data(); 
						Station s = ss.stations[d.get("station_id")];
						// add a new customer as planned
						s.on_platform.add(s.gen_customer(ss.rs, ss.clock));
	
						// line up the next arrival
						ss.FutureEventList.enqueue(ss.new_customer(d.get("station_id")));
					}
				} else {
					// no more events; might as well quit
					break;
				}
			}
			is_output_run = false;

			double max = 0;
			double avg = 0;
			for(int i = 0; i < travel_times.size(); ++i) {
				avg += travel_times.get(i);
				if(travel_times.get(i) > max) {
					max = travel_times.get(i);
				}
		
			}
			avg /= travel_times.size();
			System.out.println(run_id + "," + avg + "," + max);
			
			output.close();
			station_output.close();
		}
    }
}