package powercraft.api;

import powercraft.api.gres.autoadd.PC_SortedList;


public class PC_ValueRange {
	
	static class Range implements Comparable<Range>{
		
		private double start;
		private boolean startIncluded;
		private double end;
		private boolean endIncluded;
		
		public Range(double start, boolean startIncluded, double end, boolean endIncluded){
			this.start = start;
			this.startIncluded = startIncluded;
			this.end = end;
			this.endIncluded = endIncluded;
		}
		
		boolean contains(double v) {
			if(this.startIncluded?v<this.start:v<=this.start)
				return false;
			if(this.endIncluded?v>this.end:v>=this.end)
				return false;
			return true;
		}
		
		@Override
		public int compareTo(Range o) {
			if(this.start>o.start)
				return 1;
			if(this.start<o.start)
				return -1;
			if(this.startIncluded && !o.startIncluded)
				return -1;
			if(!this.startIncluded && o.startIncluded)
				return 1;
			if(this.end>o.end)
				return 1;
			if(this.end<o.end)
				return -1;
			if(this.endIncluded && !o.endIncluded)
				return 1;
			if(!this.endIncluded && o.endIncluded)
				return -1;
			return 0;
		}
		
		@Override
		public String toString() {
			if(this.start==this.end)
				return Double.toString(this.start);
			return (this.startIncluded?"[":"(")+this.start+", "+this.end+(this.endIncluded?"]":")");
		}

		public Range overlapping(Range v) {
			if(this.end>v.start || (this.end==v.start && (this.endIncluded || v.startIncluded))){
				if(this.end>v.end || (this.end==v.end && this.endIncluded)){
					return this;
				}
				return new Range(this.start, this.startIncluded, v.end, v.endIncluded);
			}
			return null;
		}
		
	}
	
	private PC_SortedList<Range> ranges = new PC_SortedList<Range>();
	
	public void addValue(double value){
		addValue(value, true, value, true);
	}
	
	public void addValue(double start, boolean startIncluded, double end, boolean endIncluded){
		if(end<start)
			throw new IllegalArgumentException();
		if(start==end && (!startIncluded || !endIncluded))
			throw new IllegalArgumentException();
		this.ranges.add(new Range(start, startIncluded, end, endIncluded));
		checkOverlaps();
	}
	
	public void addRange(PC_ValueRange range){
		this.ranges.addAll(range.ranges);
		checkOverlaps();
	}
	
	private void checkOverlaps(){
		int i=0;
		while(this.ranges.size()>i+1){
			Range v1 = this.ranges.get(i);
			Range v2 = this.ranges.get(i+1);
			Range overlapping = v1.overlapping(v2);
			if(overlapping==null){
				i++;
			}else{
				this.ranges.remove(i);
				this.ranges.remove(i);
				this.ranges.add(overlapping);
			}
		}
	}
	
	public boolean in(double value){
		for(Range range:this.ranges){
			if(range.contains(value)){
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		String out = "";
		if(!this.ranges.isEmpty()){
			out += this.ranges.get(0);
			for(int i=1; i<this.ranges.size(); i++){
				out += ", "+this.ranges.get(i);
			}
		}
		return out;
	}
	
	public static PC_ValueRange pharseRange(String str){
		String[] splits = str.trim().split("\\s*,\\s*");
		PC_ValueRange range = new PC_ValueRange();
		for(int i=0; i<splits.length; i++){
			String s = splits[i];
			if(s.isEmpty())
				throw new NumberFormatException();
			char c = s.charAt(0);
			if(c=='[' || c==']' || c=='('){
				boolean startIncluded = c=='[';
				String[] ss = s.split(";", 2);
				if(ss.length==1)
					throw new NumberFormatException();
				double start = Double.parseDouble(ss[0].substring(1).trim());
				if(ss[1].isEmpty())
					throw new NumberFormatException();
				c = ss[1].charAt(ss[1].length()-1);
				double end = Double.parseDouble(ss[1].substring(0, ss[1].length()-1).trim());
				if(!(c=='[' || c==']' || c==')')){
					throw new NumberFormatException();
				}
				boolean endIncluded = c==']';
				range.addValue(start, startIncluded, end, endIncluded);
			}else{
				range.addValue(Double.parseDouble(s));
			}
		}
		return range;
	}
	
}
