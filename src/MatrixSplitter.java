import java.text.DecimalFormat;
import java.text.NumberFormat;


public class MatrixSplitter extends Thread{
	Fractal f;
	int startrows,stoprows,ncols;
	public MatrixSplitter(Fractal f,int startrows,int stoprows,int ncols){
		this.f = f;
		this.startrows =startrows;
		this.stoprows = stoprows;
		this.ncols = ncols;
	}
	
	
	
	public void run(){
		long start = System.currentTimeMillis();
		
		
		for(int i = this.startrows;i<this.stoprows;i++){
			for(int j =0; j<this.ncols;j++){
				f.escapeVals[i][j] = f.escapeCount(f.complexMatrix[i][j]);
				//System.out.println("Thread " + Thread.currentThread().getId() + " i is: " + i + " j is: " + j);
			}
		}
		
		
		long end =  System.currentTimeMillis();
		NumberFormat formatter = new DecimalFormat("#0.00000");
		System.out.println("Thread Execution Time is: " + formatter.format((end-start)) + " milliseconds.");
	}
}