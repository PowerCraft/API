package powercraft.api.gres.autoadd;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PC_StringListPart implements List<PC_StringWithInfo> {

	private List<PC_StringWithInfo> sortedList;
	private int start;
	private int end;
	private String lastSearch;
	
	public PC_StringListPart(List<PC_StringWithInfo> sortedList){
		this.sortedList = sortedList;
		end = sortedList.size();
	}
	
	public void searchForAdd(String toAdd) {
		searchFor(lastSearch+toAdd);
	}
	
	public void searchFor(String s){
		if(lastSearch!=null && s.startsWith(lastSearch)){
			int oldStart = start;
			int oldEnd = end;
			start = -1;
			end = -1;
			for(int i=oldStart; i<oldEnd; i++){
				PC_StringWithInfo ss = sortedList.get(i);
				if(ss.startsWith(s)){
					if(start==-1)
						start = i;
					end = i+1;
				}else if(end!=-1)
					break;
			}
			if(start==-1)
				start = 0;
			if(end==-1)
				end = 0;
		}else if(lastSearch!=null && lastSearch.startsWith(s)){
			for(int i=0; i<start; i++){
				PC_StringWithInfo ss = sortedList.get(i);
				if(ss.startsWith(s)){
					start = i;
					break;
				}
			}
			for(int i=end; i<sortedList.size(); i++){
				PC_StringWithInfo ss = sortedList.get(i);
				if(ss.startsWith(s)){
					end = i+1;
				}else{
					break;
				}
			}
		}else{
			start = -1;
			end = -1;
			for(int i=0; i<sortedList.size(); i++){
				PC_StringWithInfo ss = sortedList.get(i);
				if(ss.startsWith(s)){
					if(start==-1)
						start = i;
					end = i+1;
				}else if(end!=-1)
					break;
			}
			if(start==-1)
				start = 0;
			if(end==-1)
				end = 0;
		}
		lastSearch = s;
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
		int index = sortedList.indexOf(o);
		return index>=start && index<end;
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
		return sortedList.get(index+start);
	}
	
	@Override
	public int indexOf(Object o) {
		int index = sortedList.indexOf(o);
		if(index>=start && index<end){
			return index;
		}
		return -1;
	}
	
	@Override
	public boolean isEmpty() {
		return start==end;
	}
	
	@Override
	public Iterator<PC_StringWithInfo> iterator() {
		return listIterator();
	}
	
	@Override
	public int lastIndexOf(Object o) {
		int index = sortedList.lastIndexOf(o);
		if(index>=start && index<end){
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
		return new It(index+start);
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
			return index<end;
		}

		@Override
		public boolean hasPrevious() {
			return index>start;
		}

		@Override
		public PC_StringWithInfo next() {
			return sortedList.get(index++);
		}

		@Override
		public int nextIndex() {
			return index;
		}

		@Override
		public PC_StringWithInfo previous() {
			return sortedList.get(--index);
		}

		@Override
		public int previousIndex() {
			return index-1;
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
		return end-start;
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
		if(a.length<size){
			a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
		}
		for(int i=0; i<size; i++){
			a[i] = (T) get(i);
		}
		return a;
	}
	
}
