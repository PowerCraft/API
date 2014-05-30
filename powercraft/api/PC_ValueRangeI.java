package powercraft.api;

import powercraft.api.gres.autoadd.PC_SortedList;


public class PC_ValueRangeI {
	
	static class Range implements Comparable<Range>{
		
		private int start;
		private int end;
		
		public Range(int start, int end){
			this.start = start;
			this.end = end;
		}
		
		boolean contains(int v) {
			if(v<this.start)
				return false;
			if(v>this.end)
				return false;
			return true;
		}
		
		@Override
		public int compareTo(Range o) {
			if(this.start>o.start)
				return 1;
			if(this.start<o.start)
				return -1;
			if(this.end>o.end)
				return 1;
			if(this.end<o.end)
				return -1;
			return 0;
		}
		
		@Override
		public String toString() {
			if(this.start==this.end)
				return Integer.toString(this.start);
			return "["+this.start+", "+this.end+"]";
		}

		public Range overlapping(Range v) {
			if(this.end>=v.start){
				if(this.end>=v.end){
					return this;
				}
				return new Range(this.start, v.end);
			}
			return null;
		}
		
	}
	
	private PC_SortedList<Range> ranges = new PC_SortedList<Range>();
	
	public void addValue(int value){
		addValue(value, true, value, true);
	}
	
	public void addValue(int start, boolean startIncluded, int end, boolean endIncluded){
		int s = start;
		if(!startIncluded){
			s++;
		}
		int e = end;
		if(!endIncluded){
			e--;
		}
		if(s<e)
			throw new IllegalArgumentException();
		this.ranges.add(new Range(s, e));
		checkOverlaps();
	}
	
	public void addRange(PC_ValueRangeI range){
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
	
	public boolean in(int value){
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
	
	public static PC_ValueRangeI pharseRange(String str){
		String[] splits = str.trim().split("\\s*,\\s*");
		PC_ValueRangeI range = new PC_ValueRangeI();
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
				int start = Integer.parseInt(ss[0].substring(1).trim());
				if(ss[1].isEmpty())
					throw new NumberFormatException();
				c = ss[1].charAt(ss[1].length()-1);
				int end = Integer.parseInt(ss[1].substring(0, ss[1].length()-1).trim());
				if(!(c=='[' || c==']' || c==')')){
					throw new NumberFormatException();
				}
				boolean endIncluded = c==']';
				range.addValue(start, startIncluded, end, endIncluded);
			}else{
				range.addValue(Integer.parseInt(s));
			}
		}
		return range;
	}
	
}
