package powercraft.api;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.util.RegistrySimple;


public class PC_RegistryIterator<E> implements ListIterator<E> {

	private RegistrySimple registry;
	private List<String> keys;
	private int pos;
	
	@SuppressWarnings("unchecked")
	public PC_RegistryIterator(RegistrySimple registry){
		this.registry = registry;
		this.keys = new ArrayList<String>(registry.getKeys());
	}
	
	@Override
	public void add(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasNext() {
		return this.keys.size()>this.pos;
	}

	@Override
	public boolean hasPrevious() {
		return this.pos>0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E next() {
		return (E) this.registry.getObject(this.keys.get(this.pos++));
	}

	@Override
	public int nextIndex() {
		return this.pos+1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E previous() {
		return (E) this.registry.getObject(this.keys.get(--this.pos));
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
