package powercraft.api;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class PC_ImmutableList<E> implements List<E> {

	private List<E> list;
	private int start;
	private int end;
	
	public PC_ImmutableList(List<E> list){
		this.list = list;
		start = 0;
		end = list.size();
	}
	
	private PC_ImmutableList(List<E> list, int start, int end){
		this.list = list;
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
		return list.get(start+index);
	}

	@Override
	public int indexOf(Object obj) {
		int index = list.indexOf(obj)-start;
		if(index<0 || index>=end){
			return -1;
		}
		return index;
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
		int index = list.lastIndexOf(obj)-start;
		if(index<0 || index>=end){
			return -1;
		}
		return index;
	}

	@Override
	public ListIterator<E> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<E> listIterator(int start) {
		return new ImmutableListIterator(start);
	}

	private class ImmutableListIterator implements ListIterator<E>{

		private int pos;
		
		private ImmutableListIterator(int start){
			pos = start;
		}
		
		@Override
		public void add(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext() {
			return pos<size();
		}

		@Override
		public boolean hasPrevious() {
			return pos>0;
		}

		@Override
		public E next() {
			return get(pos++);
		}

		@Override
		public int nextIndex() {
			return pos;
		}

		@Override
		public E previous() {
			return get(--pos);
		}

		@Override
		public int previousIndex() {
			return pos-1;
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
		return end-start;
	}

	@Override
	public List<E> subList(int start, int end) {
		if(end>size())
			throw new IndexOutOfBoundsException();
		return new PC_ImmutableList<E>(list, this.start+start, this.start+end);
	}
	
	@Override
	public Object[] toArray() {
		Object[] a = new Object[size()];
		for(int i=0; i<a.length; i++){
			a[i] = list.get(i+start);
		}
		return a;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if(a.length<size()){
			a = (T[]) Array.newInstance(a.getClass().getComponentType(), size());
		}
		for(int i=0; i<a.length; i++){
			a[i] = (T) list.get(i+start);
		}
		return a;
	}

}
