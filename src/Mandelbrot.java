import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Mandelbrot extends Fractal{

	public Mandelbrot(Complex iMG_LOW, Complex iMG_HIGH, int iMG_WIDTH,int iMG_HEIGHT, int maxIters, int numThreads) {
		// TODO Auto-generated constructor stub
		super(iMG_LOW,iMG_HIGH,iMG_WIDTH,iMG_HEIGHT,maxIters,numThreads);
		this.c = new Complex(0,0);
	}

	
	public static void main(String[] args) throws FileNotFoundException, InterruptedException{
		//long start = System.nanoTime();
		if(args.length == 9){
			Mandelbrot m = new Mandelbrot(new Complex(Double.parseDouble(args[0]),Double.parseDouble(args[2])),new Complex(Double.parseDouble(args[1]),Double.parseDouble(args[3])),Integer.parseInt(args[5]),Integer.parseInt(args[4]),Integer.parseInt(args[6]), Integer.parseInt(args[7]));
			m.escapes();
			m.write(args[8]);
		}
		else
			System.out.println("Error: Either too many or too little args.\n");
		//long end =  System.nanoTime();
		//NumberFormat formatter = new DecimalFormat("#0.00000");
		//System.out.println("Execution Time is: " + formatter.format((end-start)) + " milliseconds.");

	}
}