package powercraft.api.beam;

import java.util.ArrayList;
import java.util.List;

import powercraft.api.PC_Vec3;


public class PC_LightValue {

	public static final double THz = 1e12;
	private double[] frequencyAndIntensity;
	
	public PC_LightValue(double...frequencyAndIntensity){
		if(frequencyAndIntensity.length==0 || frequencyAndIntensity.length%2!=0){
			throw new IllegalArgumentException("Need always a frequency and intensity");
		}
		for(int i=0; i<frequencyAndIntensity.length; i+=2){
			if(frequencyAndIntensity[i+1]<=0){
				throw new IllegalArgumentException("No negative or 0 intensity");
			}
		}
		this.frequencyAndIntensity = frequencyAndIntensity;
	}
	
	public PC_Vec3 toColor(){
		PC_Vec3 color = new PC_Vec3();
		for(int i=0; i<this.frequencyAndIntensity.length; i+=2){
			color = color.add(getColorForFrequencyAndIntensity(this.frequencyAndIntensity[i], this.frequencyAndIntensity[i+1]));
		}
		return color;
	}
	
	public double getIntensity(){
		double intensity = 0;
		for(int i=1; i<this.frequencyAndIntensity.length; i+=2){
			intensity += this.frequencyAndIntensity[i];
		}
		return intensity;
	}
	
	private static double adjust(double color, double factor){
		final float GAMMA = 0.8f;
		if(color==0){
			return 0;
		}
		return Math.pow(color*factor, GAMMA);
	}
	
	private static PC_Vec3 getColorForFrequencyAndIntensity(double frequency, double intensity){
		double red;
		double green;
		double blue;
		double factor;
		double wavelength = 3e17/frequency;
		if(wavelength>=380 && wavelength<440){
			red = -(wavelength - 440) / (440 - 380);
			green = 0.0;
			blue = 1.0;
		}else if(wavelength>=440 && wavelength<490){
			red = 0.0;
			green = (wavelength - 440) / (490 - 440);
			blue = 1.0;
		}else if(wavelength>=490 && wavelength<510){
			red = 0.0;
			green = 1.0;
			blue = -(wavelength - 510) / (510 - 490);
		}else if(wavelength>=510 && wavelength<580){
			red = (wavelength - 510) / (580 - 510);
			green = 1.0;
			blue = 0.0;
		}else if(wavelength>=580 && wavelength<645){
			red = 1.0;
			green = -(wavelength - 645) / (645 - 580);
			blue = 0.0;
		}else if(wavelength>=645 && wavelength<=780){
			red = 1.0;
			green = 0.0;
			blue = 0.0;
		}else{
			red = 0.0;
			green = 0.0;
			blue = 0.0;
		}
		if(wavelength>=380 && wavelength<420){
			factor = 0.3 + 0.7*(wavelength - 380) / (420 - 380);
		}else if(wavelength>=420 && wavelength<701){
			factor = 1.0;
		}else if(wavelength>=701 && wavelength<780){
			factor = 0.3 + 0.7*(780 - wavelength) / (780 - 700);
		}else{
			factor = 0.0;
		}
		PC_Vec3 color = new PC_Vec3(adjust(red, factor), adjust(green, factor), adjust(blue, factor));
		return color.mul(intensity);
	}
	
	public PC_LightValue filterBy(PC_LightFilter filter){
		if(filter==null)
			return this;
		List<Double> list = new ArrayList<Double>();
		for(int i=0; i<this.frequencyAndIntensity.length; i+=2){
			double frequency = this.frequencyAndIntensity[i];
			double intensity = filter.filter(frequency, this.frequencyAndIntensity[i+1]);
			if(intensity>0){
				list.add(Double.valueOf(frequency));
				list.add(Double.valueOf(intensity));
			}
		}
		if(list.isEmpty())
			return null;
		double[] d = new double[list.size()];
		for(int i=0; i<d.length; i++){
			d[i] = list.get(i).doubleValue();
		}
		return new PC_LightValue(d);
	}
	
}
