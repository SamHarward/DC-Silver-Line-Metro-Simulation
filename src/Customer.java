public class Customer {
	private int start;
	private int end;
	public double time_start;
	public Customer(int _start, int _end, double _time_start) {
	    start = _start;
	    end = _end;
	    time_start = _time_start;
	}
	
	public int get_start() {
		return start;
	}
	public int get_end() {
		return end;
	}
}