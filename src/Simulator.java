import java.util.HashMap;

public class Simulator {	
	public final static int n_stations = 28;
	public int total_arrived = 0;

	public final static double arr_rates_am[]      = {0.09861212563915267, 1.115702479338843, 1.5254237288135593, 0.8307692307692308, 0.6474820143884892, 0.1070154577883472, 0.062326869806094184, 0.14689880304678998, 0.14492753623188406, 0.0839030453697949, 0.06595017098192477, 0.11397214014352047, 0.19607843137254902, 0.17307692307692307, 0.17153748411689962, 1.560693641618497, 0.9440559440559441, 0.09930121368150055, 0.678391959798995, 0.31690140845070425, 0.12987012987012986, 0.14180672268907563, 0.24258760107816713, 0.18543956043956045, 0.23316062176165803, 0.14203051025775906, 0.2107728337236534, 0.08695652173913043};
	public final static double arr_rates_midday[]  = {0.48104956268221577, 2.519083969465649, 2.8947368421052633, 1.0576923076923077, 1.9760479041916168, 0.4903417533432392, 0.17045454545454544, 0.49327354260089684, 0.4225352112676056, 0.26128266033254155, 0.11262798634812286, 0.08711721224920803, 0.10125805461798097, 0.1373283395755306, 0.07542857142857143, 0.22964509394572025, 0.15827338129496402, 0.09874326750448834, 0.3179190751445087, 0.205607476635514, 0.25287356321839083, 0.39903264812575573, 0.5172413793103449, 0.4532967032967033, 0.7746478873239436, 0.497737556561086, 1.064516129032258, 0.46544428772919605};
	public final static double arr_rates_pm[]      = {0.2891566265060241, 1.0084033613445378, 0.6857142857142857, 0.27586206896551724, 0.5797101449275363, 0.41025641025641024, 0.06997084548104957, 0.24489795918367346, 0.22835394862036157, 0.12345679012345678, 0.0395908940943583, 0.024166750578995064, 0.01867704280155642, 0.027158537965372865, 0.016238159675236806, 0.041300980898296334, 0.035000729181857954, 0.018972332015810278, 0.06330783434450013, 0.05781739339918092, 0.16216216216216217, 0.42328042328042326, 0.38961038961038963, 0.5466970387243736, 1.0212765957446808, 0.7619047619047619, 1.2698412698412698, 0.5106382978723404};
	public final static double arr_rates_evening[] = {1.744186046511628, 4.166666666666667, 4.109589041095891, 0.78125, 5.454545454545454, 1.530612244897959, 0.3177966101694915, 1.0033444816053512, 0.43541364296081275, 0.5319148936170213, 0.2170767004341534, 0.0757193336698637, 0.09305210918114144, 0.15360983102918588, 0.05842259006815969, 0.3464203233256351, 0.29239766081871343, 0.20449897750511248, 0.7425742574257426, 0.3250270855904659, 0.379746835443038, 1.2195121951219512, 1.2048192771084338, 1.5706806282722514, 2.803738317757009, 1.7964071856287425, 2.4, 1.2096774193548387};
	
	public final static double station_service_time = 1;
	public final static int station_initial_customers[] = {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0};
	
	public final static int train_capacity = 175*8;
	public final static double avg_train_travel_time = 4;
	public final static double train_travel_time_range = 1;
	public final static int n_trains = 18;
	public final static int train_initial_stations[]   = {0, 1, 2, 3, 4, 5, 8, 10, 12, 14, 16, 18,
														20, 22, 23, 24, 26, 27};
	
	public final static int train_initial_directions[] = {1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1,
													      1, -1, 1, -1, 1, -1};
	
	public Station stations[] = new Station[n_stations];
	public Train trains[] = new Train[n_trains];
	public double clock;

    public EventList FutureEventList = new EventList();
    public Rand rs;
    
    public Simulator(int seed) {
    	clock = 0.0;
    	rs = new Rand(seed + 1);
    	for(int i = 0; i < n_stations; ++i) {
    		stations[i] = new Station(i, rs, n_stations,
    				station_initial_customers[i]);
    		FutureEventList.enqueue(new_customer(i));
    	}
    	
    	for(int i = 0; i < n_trains; ++i) {
    		trains[i] = new Train(i, train_initial_stations[i],
    				n_stations, train_initial_directions[i], train_capacity);
    		FutureEventList.enqueue(move_train(i));
    	}
    }
    
    public Event move_train(int train_id) {
    	double when = clock + avg_train_travel_time + rs.uniform() * train_travel_time_range
    			- (train_travel_time_range/2);
    	return trains[train_id].get_next_station(when);
    }
    
    public Event new_customer(int station_id) {
    	double when = 999999;
    	if(clock < 270) { //before 9:30 AM
    		when = clock + rs.exponential(arr_rates_am[station_id]);
    	} else if(clock < 600) { //before 3PM
    		when = clock + rs.exponential(arr_rates_midday[station_id]);
    	} else if(clock < 840) { //before 7PM
    		when = clock + rs.exponential(arr_rates_pm[station_id]);
    	} else { //before close (12AM)
    		when = clock + rs.exponential(arr_rates_evening[station_id]);
    	}
    	
    	HashMap<String, Integer> data = new HashMap<String, Integer>();
		data.put("station_id", station_id);
		total_arrived += 1;
		return new Event("Customer Arrival", data, when);
    }
}