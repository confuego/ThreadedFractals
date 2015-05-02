import java.util.ArrayList;


public class MatrixSplitter extends Thread {
	Complex[] complexRow;
	String fractalType;
	int maxIters;
	Complex c;
	int [] calculatedEscapeCounts;
	
	//c is null for Mandelbrot
	public MatrixSplitter(Complex[] complexRow,String fractalType,int maxIters,Complex c){
		this.complexRow = complexRow;
		this.fractalType = fractalType;
		this.maxIters = maxIters;
		this.c = c;
	}
	
	public int escapeCountJ(Complex p){
		Complex z = new Complex(p.r,p.i);
		int escape=0;
		for (int i=0;i<=maxIters;i++){
			
			Complex znext = new Complex(0,0);	//create new z
			
			
			znext.r=(z.r*z.r)-(z.i*z.i);	//calculate z^2
			znext.i=(z.r*z.i)+(z.i*z.r);
			
			
			znext.r+=c.r;	//add p to z
			znext.i+=c.i;

			escape=i;
			if (Math.abs(Math.sqrt(znext.r*znext.r+znext.i*znext.i))>=2){
				return escape;		//if magnitude of z > 2
			}
			z=znext;
			
		}
		return escape;
	}
	
	public int escapeCountM(Complex p){
		Complex z = new Complex(0,0);
		int escape=0;
		for (int i=0;i<=maxIters;i++){

			Complex znext = new Complex(0,0);		//create new z
			
			znext.r= (z.r*z.r)-(z.i*z.i);		//calculate z^2
			znext.i= (z.r*z.i)+(z.i*z.r);
			
			znext.r+=p.r;		//add p to z
			znext.i+=p.i;
			
			escape=i;
			if (Math.abs(Math.sqrt(znext.r*znext.r+znext.i*znext.i))>2){			//instead of 2
				return escape;		//is magnitude of z >2
			}
			z=znext;
		}
		return escape;
	}
	
	public void run(){
		if(fractalType.equals("Mandelbrot")){
			int i = 0;
			for(Complex c : complexRow){
				calculatedEscapeCounts[i] = escapeCountM(c);
				i++;
			}
		}
		else if(fractalType.equals("Julia")){
			int i=0;
			for(Complex c : complexRow){
				calculatedEscapeCounts[i] = escapeCountJ(c);
				i++;
			}
		}
		else
			System.out.println("Not a configured Fractal type.");
	}
	
	public int[][] combine(ArrayList<int[]> rows,int colSize){
		int [][] fullMatrix = new int[rows.size()][colSize];
		int i =0;
		for(int[] row : rows){
			fullMatrix[i] = row;
			i++;
		}
		return fullMatrix;
	}
}