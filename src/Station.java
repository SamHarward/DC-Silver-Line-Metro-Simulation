import java.util.Vector;  

public class Station {
	public Vector<Customer> on_platform = new Vector<Customer>();
	private int n_stations;
	public int station_id;
	
	public Station(int _station_id, Rand rs, int _n_stations, int initial_customer_count) {
		n_stations = _n_stations;
		station_id = _station_id;
		
		for(int i = 0; i < initial_customer_count; ++i) {
			on_platform.add(gen_customer(rs, 0));
		}
	}
	
	public Customer gen_customer(Rand rs, double time) {
		int destination = rs.rand_in_range(n_stations);
		while(destination == station_id) {
			//why are you trying to get on a train??
			destination = rs.rand_in_range(n_stations);
		}
		return new Customer(station_id, destination, time);		
	}
	
	public Vector<Customer> get_boarders(int direction) {
		Vector<Customer> to_board = new Vector<Customer>();
		for(int i = on_platform.size() - 1; i >= 0; --i) {
			// traveling in the right direction?
			if((on_platform.get(i).get_end() > station_id) == (direction > 0)) {
				to_board.add(on_platform.get(i));
				on_platform.remove(i);
			}
		}
		return to_board;
	}
}