import java.util.ArrayList;


public class MatrixSplitter extends Thread {
	Complex[][] complexRows;
	String fractalType;
	int maxIters;
	Complex c;
	int [][] calculatedEscapeCounts;
	
	//c is null for Mandelbrot
	public MatrixSplitter(Complex[][] complexRows,String fractalType,int maxIters,Complex c){
		this.complexRows = complexRows;
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
			int j =0;
			for(Complex[] row : complexRows){
				for(Complex val : row){
					calculatedEscapeCounts[i][j] = escapeCountM(val);
					j++;
				}
				j=0;
				i++;
			}
		}
		else if(fractalType.equals("Julia")){
			int i = 0;
			int j =0;
			for(Complex[] row : complexRows){
				for(Complex val : row){
					calculatedEscapeCounts[i][j] = escapeCountJ(val);
					j++;
				}
				j=0;
				i++;
			}
		}
		else
			System.out.println("Not a configured Fractal type.");
	}
	
	public static int[][] combine(ArrayList<int[][]> matrices,int rowSize,int colSize){
		int [][] fullMatrix = new int[rowSize][colSize];
		int rowCount =0;
		for(int[][] matrix : matrices){
			for(int[] row : matrix){
				fullMatrix[rowCount] = row;
			}
			rowCount++;
		}
		return fullMatrix;
	}
}