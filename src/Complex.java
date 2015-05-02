
public class Complex {
	double r,i;
	public Complex(double r,double i){
		this.r = r;
		this.i = i;
	}
	public synchronized static Complex add(Complex a,Complex b){
		return new Complex(a.r + b.r,a.i + b.i);
	}
	public synchronized static Complex sub(Complex a,Complex b){
		return new Complex(a.r-b.r,a.i-b.i);
	}
	public synchronized static Complex mul(Complex a,Complex b){
		double mult = a.r * b.r;
		double multi = a.r * b.i;
		double mult2 = a.i * b.r;
		double multi2 = -(a.i * b.i);
		return new Complex(mult + multi2,mult2 + multi);
		
	}
	public synchronized static double abs(Complex c){
		return Math.sqrt(Math.pow(c.r,2) + Math.pow(c.i,2));
	}
	@Override
	public String toString(){
		if(this.i<0)
			return this.r + " " + this.i + "i";
		return this.r + " + "+this.i + "i";
	}
	public synchronized Complex copy(){
		return this;
	}
	

}
