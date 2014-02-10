package powercraft.api.gres;

import java.lang.reflect.Array;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresDisplayObject implements PC_IGresEventListener {

	private final Object display;
	
	public PC_GresDisplayObject(Object display){
		if(display.getClass().isArray()){
			display = new ObjectChange(display);
		}
		if(display instanceof Item){
			display = new ItemStack((Item)display);
		}else if(display instanceof Block){
			display = new ItemStack((Block)display);
		}
		if(!(display instanceof IIcon || display instanceof ItemStack || display instanceof PC_GresTexture || display instanceof ObjectChange))
			throw new IllegalArgumentException("Unknow display object:"+display);
		this.display = display;
	}
	
	public PC_GresDisplayObject(Object...display){
		this(new ObjectChange(display));
	}
	
	public Object getDisplayObject(){
		if(display instanceof ObjectChange){
			return ((ObjectChange)display).display;
		}
		return display;
	}
	
	public Object getActiveDisplayObject(){
		if(display instanceof ObjectChange){
			return ((ObjectChange)display).getObject();
		}
		return display;
	}
	
	public int getActiveDisplayObjectIndex(){
		if(display instanceof ObjectChange){
			return ((ObjectChange)display).pos;
		}
		return 0;
	}
	
	public void setActiveDisplayObjectIndex(int index){
		if(display instanceof ObjectChange){
			((ObjectChange)display).pos = index;
		}
	}
	
	public PC_Vec2I getMinSize() {
		Object d = display;
		if(display instanceof ObjectChange){
			d = ((ObjectChange)display).getObject();
		}
		if(d instanceof IIcon){
			return new PC_Vec2I(16, 16);
		}else if(d instanceof ItemStack){
			return new PC_Vec2I(16, 16);
		}else if(d instanceof PC_GresTexture){
			return ((PC_GresTexture)d).getMinSize();
		}
		return new PC_Vec2I(-1, -1);
	}

	public PC_Vec2I getPrefSize() {
		Object d = display;
		if(display instanceof ObjectChange){
			d = ((ObjectChange)display).getObject();
		}
		if(d instanceof IIcon){
			return new PC_Vec2I(16, 16);
		}else if(d instanceof ItemStack){
			return new PC_Vec2I(16, 16);
		}else if(d instanceof PC_GresTexture){
			return ((PC_GresTexture)d).getDefaultSize();
		}
		return new PC_Vec2I(-1, -1);
	}

	public void draw(int x, int y, int width, int height) {
		Object d = display;
		if(display instanceof ObjectChange){
			d = ((ObjectChange)display).getObject();
		}
		if(d instanceof IIcon){
			PC_GresRenderer.drawTerrainIcon(x, y, width, height, (IIcon)d);
		}else if(d instanceof ItemStack){
			x += width/2-8;
			y -= height/2-8;
			PC_GresRenderer.drawItemStack(x, y, (ItemStack)d, null);
		}else if(d instanceof PC_GresTexture){
			((PC_GresTexture)d).draw(x, y, width, height, 0);
		}
	}
	
	@Override
	public void onEvent(PC_GresEvent event) {
		if(display instanceof ObjectChange){
			((ObjectChange) display).onEvent(event);
		}
	}
	
	private static class ObjectChange implements PC_IGresEventListener{

		private Object[] display;
		private int pos;
		
		private ObjectChange(Object[] display){
			this.display = new Object[display.length];
			for(int i=0; i<display.length; i++){
				Object o = display[i];
				if(o instanceof Item){
					o = new ItemStack((Item)o);
				}else if(o instanceof Block){
					o = new ItemStack((Block)o);
				}
				if(!(o instanceof IIcon || o instanceof ItemStack || o instanceof PC_GresTexture))
					throw new IllegalArgumentException("Unknow display object:"+o);
				this.display[i] = o;
			}
		}
		
		private ObjectChange(Object display){
			this.display = new Object[Array.getLength(display)];
			for(int i=0; i<this.display.length; i++){
				Object o = Array.get(display, i);
				if(o instanceof Item){
					o = new ItemStack((Item)o);
				}else if(o instanceof Block){
					o = new ItemStack((Block)o);
				}
				if(!(display instanceof IIcon || o instanceof ItemStack || o instanceof PC_GresTexture))
					throw new IllegalArgumentException("Unknow display object:"+o);
				this.display[i] = o;
			}
		}
		
		@Override
		public void onEvent(PC_GresEvent event) {
			if(event instanceof PC_GresMouseButtonEvent){
				if(((PC_GresMouseButtonEvent)event).getEvent()==PC_GresMouseButtonEvent.Event.UP && event.getComponent().mouseDown){
					pos++;
					pos %= display.length;
				}
			}
		}

		private Object getObject(){
			return display[pos];
		}
		
	}
	
}
