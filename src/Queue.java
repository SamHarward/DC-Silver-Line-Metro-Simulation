import java.util.Vector;  
public class Queue<T> {
	private Vector<T> all_data;
    
	public Queue() {
    	all_data = new Vector<T>();
    }
    
    public void enqueue(T e) {
    	all_data.addElement(e);
    }
    
    public T dequeue() {
    	T res = all_data.elementAt(0);
        all_data.removeElementAt(0);
        return res;
    }
    
    public T Get(int i){
    	return all_data.elementAt(i);
    }
}