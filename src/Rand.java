public class Rand {
	final static int a = 16807;
    final static int c = 0;
    final static int m = 2147483647;
    // setting the seed x0.
    long x;

    public Rand(int seed) {
    	x = seed;
    }

    public double uniform() {
    	// Calculate next value in sequence.
        x = ((a * x) + c) % m;
        // Return its 0-to-1 value.
        return (double)x/m ;
    }
    
    // integer in [0, upper)
    public int rand_in_range(int upper) {
    	return (int)(uniform()*m) % upper;
    }
    
    public double exponential(double mean) {
    	return -mean*Math.log(uniform());
    }

    public double triangular(double a, double b, double c) {
    	double R = uniform();
    	double x; 
    	if (R <= (b-a)/(c-a))
    		x = a + Math.sqrt((b-a)*(c-a)*R);
    	else
    		x = c - Math.sqrt((c-b)*(c-a)*(1-R));
    	return x;
    }
}