import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public abstract class Fractal{
	double img_rate = 0;
	double real_rate = 0;
	Complex low,high;	// lower-left and upper-right coordinates
	int nrows,ncols;	// pixel counting
	int maxIters;	// how many iterations to consider
	int numThreads; // how many threads there are
	Complex[][] complexMatrix; // matrix of the complex numbers.
	ArrayList<MatrixSplitter> numOfThreadedMatrices = new ArrayList<MatrixSplitter>();
	ArrayList<int[][]> splitEscapeVals = new ArrayList<int[][]>();
	int[][] escapeVals;	// cached answers for each point's iterations to escape
	Complex c;	// what is the c value of the iteration function? (boring for Mendelbrot)
	
	public Fractal(Complex low, Complex high, int ncols,int nrows, int maxIters, int numThreads){
		this.low = low;
		this.high = high;
		this.ncols = ncols;
		this.nrows = nrows;
		this.maxIters = maxIters;
		this.escapeVals = new int[nrows][ncols];
		this.complexMatrix = new Complex[nrows][ncols];
		this.numThreads = numThreads;
		double real_dist = 0;
		double img_dist = 0;
		if(high.r > 0)
			real_dist = Math.abs(high.r) + Math.abs(low.r);
		else
			real_dist = Math.abs(low.r) - Math.abs(high.r);
		if(high.i > 0)
			img_dist = Math.abs(high.i) + Math.abs(low.i);
		else
			img_dist = Math.abs(low.i) - Math.abs(high.i);
		//System.out.printf("Real dist: %f Img dist: %f\n",real_dist,img_dist);
		this.img_rate = img_dist/(nrows-1);
		this.real_rate = real_dist/(ncols-1);
		
	}
	
	
	
	/*
	 * Given one point p,how many iterations can you follow, up	to	maxIters,	before	it	escapes?	
	 * This	means all points in	the	set	will return	maxIters.
	 */
	public abstract int escapeCount(Complex p);
	
	/*
	 * Calculate	escape	counts	for	each	point	indicated	by	current	instance variables;	
	 * return	this	array	of	escape	counts.	First	row	is	the	HIGHEST	imaginary	values	row, from low real values to high values	
	 * as	you	go	through	the	first	row	of	this	array.	Starting	with	range	low=-2-2i,	high=+2+2i,	
	 * this	means	you	should	have answer[0][0]	@	(-2+2i),	answer[0][ncols-1]	@	(+2+2i),	answer[nrows-1][0]	@	(-2-2i),	and	answer[nrows-1][ncols-1]	@	(+2-2i).	
	 */
	public int[][] escapes(){
		/*
		 * Calculate distance between the imaginaries and the real #'s
		 * rate = divide img_distance/nrows && divide real_distance/ncols 
		 * Loop through array
		 * at every change in row, subtract the imaginary portion of the Complex number by the rate calculated - start @ high.i 
		 * at every change in column, add to the real portion of the Complex number by the rate calculated - start @ low.r
		 * pass that Complex number into the escapeCount() function
		 * return matrix
		 */
		//System.out.printf("Real rate: %f Img rate: %f\n",real_rate,img_rate);
		//System.out.println(numThreads);
		int rowsPerThread = nrows / numThreads;
		double r_val = low.r;
		double i_val = high.i;
		System.out.println(rowsPerThread);
		for(int x=0;x<numThreads;x++){
			MatrixSplitter m = new MatrixSplitter(r_val,i_val,low,rowsPerThread,ncols,real_rate,img_rate,maxIters,"Mandelbrot",c);
			this.numOfThreadedMatrices.add(m);
			m.start();
			r_val = m.r_val;
			i_val = m.i_val;
		}
		try{for(MatrixSplitter m : this.numOfThreadedMatrices){m.join();}}catch(InterruptedException e){e.printStackTrace();}
		for(MatrixSplitter m : this.numOfThreadedMatrices){
			this.splitEscapeVals.add(m.calculatedEscapeCounts);
		}
		escapeVals = MatrixSplitter.combine(splitEscapeVals, nrows, ncols);
		return escapeVals;
	}
	
	
	/*
	 * Given	a	file	name,	calculate	all	escape	counts	as	needed,	
	 * and	create	a	file	containing	these	lines.	(See	example	below)
	 * nrows	ncols	maxIters
	 * lowRealVal		highRealVal		lowImaginaryVal		highImaginaryVal
	 * realC		imaginaryC
	 * <a	blank	line>
	 * each	row	from	escapeVals	as	a	line,	with	its	values	separated	by	whitespace	(please	pad	all	items	to	the	same	minimal	spacing	width	as	shown	below)
	 */
	public void write(String filename) throws FileNotFoundException{
		PrintWriter writer = new PrintWriter(filename);
		writer.println(this.nrows +" "+ this.ncols +" "+ this.maxIters);
		writer.println(low.r +" "+ high.r +" "+ low.i +" "+ high.i);
		writer.println(c.r +" "+ c.i);
		writer.println();
		for(int x=0;x<nrows;x++){
			for(int y=0;y<ncols;y++){
				if(escapeVals[x][y] >= 10)
					writer.print(escapeVals[x][y] + " ");
				else
					writer.print(escapeVals[x][y] + "  ");
			}
			writer.println();
		}
		
		writer.close();
		System.out.println("File written to.");
	}
	
	/*
	 * Given	a	factor,	stay	centered	on	the	same	spot	but	scale	it	
	 * by	factor	so	that	each	dimension	sees	1/factor	of	its	previous	range.	For	instance,	zooming	in	by	factor=2,	
	 * we	should	see	half	the	distance	of	the	previous	real	range	and	half	the	distance	of	the	previous	imaginary	range.	
	 * This	doesn't	means	we're	seeing	half	the	original	image,	by	the	way.
	 */
	public	void zoom(double factor){
		Complex scale = new Complex(1/factor,0);
		high = Complex.mul(high,scale);
		low = Complex.mul(low, scale);
		//this.escapes();
		
	}
	
	/*
	 * Straight-up	reset	low	and	high,	
	 * and	recalculate	other	stale	state	(escape	counts)	as	necessary	.
	 */
	public	void updateDimensions(Complex low,Complex high){
		this.low = low;
		this.high = high;
		//this.escapes();
		
	}
	
	@Override
	public String toString(){
		return null;
		
	}
	
}
