import java.io.FileNotFoundException;


public class Julia extends Fractal{

	public Julia(Complex iMG_LOW, Complex iMG_HIGH, int iMG_WIDTH,int iMG_HEIGHT, int maxIters, int numThreads, Complex iMG_c) {
		// TODO Auto-generated constructor stub
		super(iMG_LOW,iMG_HIGH,iMG_WIDTH,iMG_HEIGHT,maxIters, numThreads);
		this.c = iMG_c;
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		if(args.length == 11){
			Julia j = new Julia(new Complex(Double.parseDouble(args[0]),Double.parseDouble(args[2])),new Complex(Double.parseDouble(args[1]),Double.parseDouble(args[3])),Integer.parseInt(args[5]),Integer.parseInt(args[4]),Integer.parseInt(args[6]), Integer.parseInt(args[7]), new Complex(Double.parseDouble(args[8]),Double.parseDouble(args[9])));
			j.write(args[10]);
		}
		else
			System.out.println("Error: Either too many or too little args.\n");

	}
}