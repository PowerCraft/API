package powercraft.api;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class PC_ImmutableArrayList<E> implements List<E> {

	private E[] array;
	private int start;
	private int end;
	
	public PC_ImmutableArrayList(E...array){
		this.array = array;
		this.start = 0;
		this.end = array.length;
	}
	
	private PC_ImmutableArrayList(E[] array, int start, int end){
		this.array = array;
		this.start = start;
		this.end = end;
	}
	
	@Override
	public boolean add(E arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int arg0, E arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends E> arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object obj) {
		return indexOf(obj)!=-1;
	}

	@Override
	public boolean containsAll(Collection<?> obj) {
		for(Object o:obj){
			if(!contains(o))
				return false;
		}
		return true;
	}

	@Override
	public E get(int index) {
		return this.array[this.start+index];
	}

	@Override
	public int indexOf(Object obj) {
		for(int i=this.start; i<this.end; i++){
			if(this.array[i].equals(obj)){
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean isEmpty() {
		return size()==0;
	}

	@Override
	public Iterator<E> iterator() {
		return listIterator();
	}

	@Override
	public int lastIndexOf(Object obj) {
		for(int i=this.end-1; i>=this.start; i--){
			if(this.array[i].equals(obj)){
				return i;
			}
		}
		return -1;
	}

	@Override
	public ListIterator<E> listIterator() {
		return listIterator(0);
	}

	@SuppressWarnings("hiding")
	@Override
	public ListIterator<E> listIterator(int start) {
		return new ImmutableListIterator(start);
	}

	private class ImmutableListIterator implements ListIterator<E>{

		private int pos;
		
		ImmutableListIterator(int start){
			this.pos = start;
		}
		
		@Override
		public void add(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext() {
			return this.pos<size();
		}

		@Override
		public boolean hasPrevious() {
			return this.pos>0;
		}

		@Override
		public E next() {
			return get(this.pos++);
		}

		@Override
		public int nextIndex() {
			return this.pos;
		}

		@Override
		public E previous() {
			return get(--this.pos);
		}

		@Override
		public int previousIndex() {
			return this.pos-1;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void set(E e) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E remove(int arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E set(int arg0, E arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return this.end-this.start;
	}

	@SuppressWarnings("hiding")
	@Override
	public List<E> subList(int start, int end) {
		if(end>size())
			throw new IndexOutOfBoundsException();
		return new PC_ImmutableArrayList<E>(this.array, this.start+start, this.start+end);
	}
	
	@Override
	public Object[] toArray() {
		Object[] a = new Object[size()];
		System.arraycopy(this.array, this.start, a, 0, a.length);
		return a;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		T[] na = a;
		if(na.length<size()){
			na = (T[]) Array.newInstance(na.getClass().getComponentType(), size());
		}
		System.arraycopy(this.array, this.start, na, 0, na.length);
		return na;
	}

}
