package powercraft.api.beam;


public class PC_LightFilter {
	
	private double[] frequencyBand;

	public PC_LightFilter(double...frequencyBand){
		if(frequencyBand.length%2!=0){
			throw new IllegalArgumentException("Need a frequency band start and end index");
		}
		this.frequencyBand = frequencyBand;
	}
	
	public double filter(double frequency, double intensity) {
		double diff = Double.POSITIVE_INFINITY;
		for(int i=0; i<this.frequencyBand.length; i+=2){
			double start = this.frequencyBand[i];
			double end = this.frequencyBand[i+1];
			double ldiff;
			if(frequency<start){
				ldiff = start-frequency;
			}else if(frequency>end){
				ldiff = frequency-end;
			}else{
				return intensity;
			}
			if(diff<ldiff){
				diff=ldiff;
			}
		}
		diff /= 10;
		return Math.max(1-(diff*diff), 0);
	}
	
}
