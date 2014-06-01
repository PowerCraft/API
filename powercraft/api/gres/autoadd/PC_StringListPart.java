package powercraft.api.gres.autoadd;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PC_StringListPart implements List<PC_StringWithInfo> {

	List<PC_StringWithInfo> sortedList;
	int start;
	int end;
	private String lastSearch;
	
	public PC_StringListPart(PC_SortedList<PC_StringWithInfo> sortedList){
		this.sortedList = sortedList;
		this.end = sortedList.size();
	}
	
	public void searchForAdd(String toAdd) {
		if(this.lastSearch==null){
			searchFor(toAdd);
		}else{
			searchFor(this.lastSearch+toAdd);
		}
	}
	
	public void searchFor(String s){
		if(this.lastSearch!=null && s.startsWith(this.lastSearch)){
			int oldStart = this.start;
			int oldEnd = this.end;
			this.start = -1;
			this.end = -1;
			for(int i=oldStart; i<oldEnd; i++){
				PC_StringWithInfo ss = this.sortedList.get(i);
				if(ss.startsWith(s)){
					if(this.start==-1)
						this.start = i;
					this.end = i+1;
				}else if(this.end!=-1)
					break;
			}
			if(this.start==-1)
				this.start = 0;
			if(this.end==-1)
				this.end = 0;
		}else if(this.lastSearch!=null && this.lastSearch.startsWith(s)){
			for(int i=0; i<this.start; i++){
				PC_StringWithInfo ss = this.sortedList.get(i);
				if(ss.startsWith(s)){
					this.start = i;
					break;
				}
			}
			for(int i=this.end; i<this.sortedList.size(); i++){
				PC_StringWithInfo ss = this.sortedList.get(i);
				if(ss.startsWith(s)){
					this.end = i+1;
				}else{
					break;
				}
			}
		}else{
			this.start = -1;
			this.end = -1;
			for(int i=0; i<this.sortedList.size(); i++){
				PC_StringWithInfo ss = this.sortedList.get(i);
				if(ss.startsWith(s)){
					if(this.start==-1)
						this.start = i;
					this.end = i+1;
				}else if(this.end!=-1)
					break;
			}
			if(this.start==-1)
				this.start = 0;
			if(this.end==-1)
				this.end = 0;
		}
		this.lastSearch = s;
	}
	
	@Override
	public boolean add(PC_StringWithInfo e) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void add(int index, PC_StringWithInfo element) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(Collection<? extends PC_StringWithInfo> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends PC_StringWithInfo> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean contains(Object o) {
		int index = this.sortedList.indexOf(o);
		return index>=this.start && index<this.end;
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		for(Object o:c){
			if(!contains(o)){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public PC_StringWithInfo get(int index) {
		return this.sortedList.get(index+this.start);
	}
	
	@Override
	public int indexOf(Object o) {
		int index = this.sortedList.indexOf(o);
		if(index>=this.start && index<this.end){
			return index;
		}
		return -1;
	}
	
	@Override
	public boolean isEmpty() {
		return this.start==this.end;
	}
	
	@Override
	public Iterator<PC_StringWithInfo> iterator() {
		return listIterator();
	}
	
	@Override
	public int lastIndexOf(Object o) {
		int index = this.sortedList.lastIndexOf(o);
		if(index>=this.start && index<this.end){
			return index;
		}
		return -1;
	}
	
	@Override
	public ListIterator<PC_StringWithInfo> listIterator() {
		return listIterator(0);
	}
	
	@Override
	public ListIterator<PC_StringWithInfo> listIterator(int index) {
		return new It(index+this.start);
	}
	
	private class It implements ListIterator<PC_StringWithInfo>{

		private int index;
		
		public It(int index) {
			this.index = index;
		}

		@Override
		public void add(PC_StringWithInfo e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext() {
			return this.index<PC_StringListPart.this.end;
		}

		@Override
		public boolean hasPrevious() {
			return this.index>PC_StringListPart.this.start;
		}

		@Override
		public PC_StringWithInfo next() {
			return PC_StringListPart.this.sortedList.get(this.index++);
		}

		@Override
		public int nextIndex() {
			return this.index;
		}

		@Override
		public PC_StringWithInfo previous() {
			return PC_StringListPart.this.sortedList.get(--this.index);
		}

		@Override
		public int previousIndex() {
			return this.index-1;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void set(PC_StringWithInfo e) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public PC_StringWithInfo remove(int index) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public PC_StringWithInfo set(int index, PC_StringWithInfo element) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int size() {
		return this.end-this.start;
	}
	
	@Override
	public List<PC_StringWithInfo> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Object[] toArray() {
		Object[] obj = new String[size()];
		for(int i=0; i<obj.length; i++){
			obj[i] = get(i);
		}
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		int size = size();
		T[] na = a;
		if(na.length<size){
			na = (T[]) Array.newInstance(na.getClass().getComponentType(), size);
		}
		for(int i=0; i<size; i++){
			na[i] = (T) get(i);
		}
		return na;
	}
	
}
