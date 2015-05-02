import java.io.FileNotFoundException;


public class Mandelbrot extends Fractal{

	public Mandelbrot(Complex iMG_LOW, Complex iMG_HIGH, int iMG_WIDTH,int iMG_HEIGHT, int maxIters, int numThreads) {
		// TODO Auto-generated constructor stub
		super(iMG_LOW,iMG_HIGH,iMG_WIDTH,iMG_HEIGHT,maxIters,numThreads);
		this.c = new Complex(0,0);
	}

	@Override
	public int escapeCount(Complex p) {
		this.c = p;
		Complex z = new Complex(0,0);
		int max = 0;// this is just in case it goes above two and then back under two.
		int count=0;
		while(count<=this.maxIters){
			double val = Complex.abs(z);
			if(val <= 2)
				max=count;
			if(val > 2)
				break;
			z = Complex.add(Complex.mul(z, z), c);
			//System.out.printf("Z is: %s\n",z);
			count++;
		}
		// TODO Auto-generated method stub
		this.c = new Complex(0,0);
		return max;
	}
	
	public static void main(String[] args) throws FileNotFoundException, InterruptedException{
		
		if(args.length == 8){
			Mandelbrot m = new Mandelbrot(new Complex(Double.parseDouble(args[0]),Double.parseDouble(args[2])),new Complex(Double.parseDouble(args[1]),Double.parseDouble(args[3])),Integer.parseInt(args[5]),Integer.parseInt(args[4]),Integer.parseInt(args[6]), Integer.parseInt(args[7]));
			//m.write(args[7]);
		}
		else
			System.out.println("Error: Either too many or too little args.\n");

	}
}