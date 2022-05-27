import java.util.HashMap;

public class Event {
	private double time;
	private HashMap<String, Integer> data; 
	private String type;
    
    public Event (String _type, HashMap<String, Integer> _data, double _time) {
    	type = _type;
        time = _time;
        data = _data;
    }

    public double get_time() {
    	return time;
    }
    
    public String get_type() {
    	return type;
    }
    
    public HashMap<String, Integer> get_data() {
    	return data;
    }
}