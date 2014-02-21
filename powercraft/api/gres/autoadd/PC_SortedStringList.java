package powercraft.api.gres.autoadd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PC_SortedStringList implements List<PC_StringWithInfo> {

	private List<PC_StringWithInfo> sortedList = new ArrayList<PC_StringWithInfo>();
	
	@Override
	public boolean add(PC_StringWithInfo e) {
		ListIterator<PC_StringWithInfo> li = sortedList.listIterator();
		while(li.hasNext()){
			PC_StringWithInfo s = li.next();
			int comp = s.compareTo(e);
			if(comp==0){
				return false;
			}else if(comp>0){
				li.previous();
				li.add(e);
				return true;
			}
		}
		li.add(e);
		return true;
	}

	@Override
	public void add(int index, PC_StringWithInfo element) {
		add(element);
	}

	@Override
	public boolean addAll(Collection<? extends PC_StringWithInfo> c) {
		for(PC_StringWithInfo s:c){
			add(s);
		}
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends PC_StringWithInfo> c) {
		return addAll(c);
	}

	@Override
	public void clear() {
		sortedList.clear();
	}

	@Override
	public boolean contains(Object o) {
		return sortedList.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return sortedList.containsAll(c);
	}

	@Override
	public PC_StringWithInfo get(int index) {
		return sortedList.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return sortedList.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return sortedList.isEmpty();
	}

	@Override
	public Iterator<PC_StringWithInfo> iterator() {
		return sortedList.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return sortedList.lastIndexOf(o);
	}

	@Override
	public ListIterator<PC_StringWithInfo> listIterator() {
		return new LI(sortedList.listIterator());
	}

	@Override
	public ListIterator<PC_StringWithInfo> listIterator(int index) {
		return new LI(sortedList.listIterator(index));
	}

	private static class LI implements ListIterator<PC_StringWithInfo>{
		
		private ListIterator<PC_StringWithInfo> li;

		private LI(ListIterator<PC_StringWithInfo> li){
			this.li = li;
		}
		
		@Override
		public void add(PC_StringWithInfo e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext() {
			return li.hasNext();
		}

		@Override
		public boolean hasPrevious() {
			return li.hasPrevious();
		}

		@Override
		public PC_StringWithInfo next() {
			return li.next();
		}

		@Override
		public int nextIndex() {
			return li.nextIndex();
		}

		@Override
		public PC_StringWithInfo previous() {
			return li.previous();
		}

		@Override
		public int previousIndex() {
			return li.previousIndex();
		}

		@Override
		public void remove() {
			li.remove();
		}

		@Override
		public void set(PC_StringWithInfo e) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	@Override
	public boolean remove(Object o) {
		return sortedList.remove(o);
	}

	@Override
	public PC_StringWithInfo remove(int index) {
		return sortedList.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return sortedList.removeAll(c);
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
		return sortedList.size();
	}

	@Override
	public List<PC_StringWithInfo> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		return sortedList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return sortedList.toArray(a);
	}

}
