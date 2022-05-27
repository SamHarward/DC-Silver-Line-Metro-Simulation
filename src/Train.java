import java.util.HashMap;
import java.util.Vector;

public class Train {
	private int max_customers;
	public int current_station;
	private int n_stations;
	public int direction; // 1 or -1
	public int train_id;
	
	public Vector<Customer> in_train = new Vector<Customer>();
	
	public Train(int _train_id, int initial_station, int _n_stations, int _direction, int _max_customers) {
		n_stations = _n_stations;
		current_station = initial_station;
		direction = _direction;
		max_customers = _max_customers;
		train_id = _train_id;
	}
	
	public Event get_next_station(double time) {
		if((current_station == n_stations - 1 && direction > 0) ||
		   (current_station == 0              && direction < 0)) {
			
			// time to turn around
			direction *= -1;
		}
		
		int new_station = current_station + direction;
		
		HashMap<String, Integer> data = new HashMap<String, Integer>();
		data.put("train_id", train_id);
		data.put("station_id", new_station);
		
		return new Event("Train Arrival", data, time);
	}
	
	public Vector<Customer> set_station(int station) {
		current_station = station;
		Vector<Customer> departed = new Vector<Customer>();

		// delete any customers getting off here
		for(int i = in_train.size() - 1; i >= 0; --i) {
			if(in_train.get(i).get_end() == station) {
				departed.add(in_train.get(i));
				in_train.remove(i);
			}
		}
		return departed;
	}
	
	public Vector<Customer> board(Vector<Customer> to_board) {
		// return overflow if any
		Vector<Customer> overflow = new Vector<Customer>();
		for(int i = 0; i < to_board.size(); ++i) {
			if(in_train.size() < max_customers) {
				in_train.add(to_board.get(i));
			} else {
				overflow.add(to_board.get(i));
			}
		}
		return overflow;
	}
	
	public double get_utilization() {
		return (double)in_train.size()/(double)max_customers;
	}
}