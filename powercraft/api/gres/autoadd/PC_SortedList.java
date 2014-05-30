package powercraft.api.gres.autoadd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PC_SortedList<T extends Comparable<T>> implements List<T> {

	private List<T> sortedList = new ArrayList<T>();
	
	@Override
	public boolean add(T e) {
		ListIterator<T> li = this.sortedList.listIterator();
		while(li.hasNext()){
			T s = li.next();
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
	public void add(int index, T element) {
		add(element);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		for(T s:c){
			add(s);
		}
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return addAll(c);
	}

	@Override
	public void clear() {
		this.sortedList.clear();
	}

	@Override
	public boolean contains(Object o) {
		return this.sortedList.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.sortedList.containsAll(c);
	}

	@Override
	public T get(int index) {
		return this.sortedList.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return this.sortedList.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return this.sortedList.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return this.sortedList.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return this.sortedList.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return new LI<T>(this.sortedList.listIterator());
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return new LI<T>(this.sortedList.listIterator(index));
	}

	private static class LI<T> implements ListIterator<T>{
		
		private ListIterator<T> li;

		LI(ListIterator<T> li){
			this.li = li;
		}
		
		@Override
		public void add(T e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext() {
			return this.li.hasNext();
		}

		@Override
		public boolean hasPrevious() {
			return this.li.hasPrevious();
		}

		@Override
		public T next() {
			return this.li.next();
		}

		@Override
		public int nextIndex() {
			return this.li.nextIndex();
		}

		@Override
		public T previous() {
			return this.li.previous();
		}

		@Override
		public int previousIndex() {
			return this.li.previousIndex();
		}

		@Override
		public void remove() {
			this.li.remove();
		}

		@Override
		public void set(T e) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	@Override
	public boolean remove(Object o) {
		return this.sortedList.remove(o);
	}

	@Override
	public T remove(int index) {
		return this.sortedList.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.sortedList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T set(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return this.sortedList.size();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		return this.sortedList.toArray();
	}

	@Override
	public <A> A[] toArray(A[] a) {
		return this.sortedList.toArray(a);
	}

}
