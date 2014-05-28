package powercraft.api.gres;

import java.util.List;


public class PC_GresListBoxElement {

	public final Object id;
	
	public final String name;
	
	public final String key;
	
	public final List<?> nextLayer;
	
	public PC_GresListBoxElement(Object id, String name) {
		this.id = id;
		this.name = name;
		this.key = null;
		this.nextLayer = null;
	}
	
	public PC_GresListBoxElement(Object id, String name, String key) {
		this.id = id;
		this.name = name;
		this.key = key;
		this.nextLayer = null;
	}
	
	public PC_GresListBoxElement(String name, List<?> nextLayer) {
		this.id = null;
		this.name = name;
		this.key = null;
		this.nextLayer = nextLayer;
	}
	
	public PC_GresListBoxElement(String name, String key, List<?> nextLayer) {
		this.id = null;
		this.name = name;
		this.key = key;
		this.nextLayer = nextLayer;
	}
	
}
