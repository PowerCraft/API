package powercraft.api.renderer.model;

import java.util.HashMap;

import net.minecraft.client.model.ModelBase;
import powercraft.api.PC_Module;
import powercraft.api.renderer.model.ms3d.PC_MS3DModel;


public class PC_Model extends ModelBase {

	private static interface ModelBuilder{
		
		PC_Model build(PC_Module module, String name);
		
	}
	
	private static HashMap<String, ModelBuilder> models = new HashMap<String, ModelBuilder>();
	
	static{
		models.put("ms3d", new ModelBuilder(){

			@Override
			public PC_Model build(PC_Module module, String name) {
				return new PC_MS3DModel(module, name);
			}
			
		});
	}
	
	public static PC_Model loadModel(PC_Module module, String name, String type){
		ModelBuilder model = models.get(type.toLowerCase());
		if(model==null)
			throw new IllegalArgumentException("Type "+type+" not supported");
		return model.build(module, name);
	}
	
}
