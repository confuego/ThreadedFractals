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
		
		/*for(Complex[] row : complexMatrix){
			for(Complex val : row){
				System.out.print(val + ", ");
			}
			System.out.println();
		}
		System.out.println();*/
		
	}
	
	public int escapeCountJ(Complex p){
		Complex z = p;
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
		notifyAll();
		return max;
	}
	
	public int escapeCountM(Complex p){
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
		/*synchronized(this){
			for(int[] row : this.calculatedEscapeCounts){
				for(int val : row){
					System.out.print(val + " ");
				}
				System.out.println();
			}
			System.out.println();
			System.out.println("Finished with " + Thread.currentThread().getName());
		}*/
	}
	
	public static int[][] combine(ArrayList<int[][]> matrices,int rowSize,int colSize){
		int [][] fullMatrix = new int[rowSize][colSize];
		int rowCount =0;
		for(int[][] matrix : matrices){
			for(int[] row : matrix){
				fullMatrix[rowCount] = row;
				rowCount++;
			}
			//rowCount = 0;
		}
		return fullMatrix;
	}
}