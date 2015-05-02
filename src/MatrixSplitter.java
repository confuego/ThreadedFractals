import java.util.ArrayList;


public class MatrixSplitter extends Thread {
	Complex[][] complexMatrix;
	String fractalType;
	int maxIters;
	Complex c;
	int [][] calculatedEscapeCounts;
	double r_val,i_val;
	
	//c is null for Mandelbrot
	public MatrixSplitter(double r_val,double i_val,Complex low,int nrows,int ncols, double real_rate,double img_rate,int maxIters,String fractalType,Complex c){
		this.fractalType = fractalType;
		this.maxIters = maxIters;
		this.complexMatrix = new Complex[nrows][ncols];
		this.calculatedEscapeCounts = new int[nrows][ncols];
		this.c = c;
		for(int x=0;x<nrows;x++){
			for(int y=0;y<ncols;y++){
				complexMatrix[x][y] = new Complex(r_val,i_val);
				r_val = r_val + real_rate;
			}
			r_val = low.r;
			i_val  = i_val - img_rate;
			//System.out.println();
		}
		this.r_val = r_val;
		this.i_val = i_val;
		
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
			for(Complex[] row : complexMatrix){
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
			int j = 0;
			for(Complex[] row : complexMatrix){
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