package powercraft.api.gres.autoadd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PC_SortedStringList implements List<String> {

	private List<String> sortedList = new ArrayList<String>();
	
	@Override
	public boolean add(String e) {
		ListIterator<String> li = sortedList.listIterator();
		while(li.hasNext()){
			String s = li.next();
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
	public void add(int index, String element) {
		add(element);
	}

	@Override
	public boolean addAll(Collection<? extends String> c) {
		for(String s:c){
			add(s);
		}
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends String> c) {
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
	public String get(int index) {
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
	public Iterator<String> iterator() {
		return sortedList.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return sortedList.lastIndexOf(o);
	}

	@Override
	public ListIterator<String> listIterator() {
		return new LI(sortedList.listIterator());
	}

	@Override
	public ListIterator<String> listIterator(int index) {
		return new LI(sortedList.listIterator(index));
	}

	private static class LI implements ListIterator<String>{
		
		private ListIterator<String> li;

		private LI(ListIterator<String> li){
			this.li = li;
		}
		
		@Override
		public void add(String e) {
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
		public String next() {
			return li.next();
		}

		@Override
		public int nextIndex() {
			return li.nextIndex();
		}

		@Override
		public String previous() {
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
		public void set(String e) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	@Override
	public boolean remove(Object o) {
		return sortedList.remove(o);
	}

	@Override
	public String remove(int index) {
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
	public String set(int index, String element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return sortedList.size();
	}

	@Override
	public List<String> subList(int fromIndex, int toIndex) {
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
