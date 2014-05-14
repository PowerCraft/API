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
		Object d = display;
		if(d.getClass().isArray()){
			d = new ObjectChange(d);
		}
		if(d instanceof Item){
			d = new ItemStack((Item)d);
		}else if(d instanceof Block){
			d = new ItemStack((Block)d);
		}
		if(!(d instanceof IIcon || d instanceof ItemStack || d instanceof PC_GresTexture || d instanceof ObjectChange))
			throw new IllegalArgumentException("Unknow display object:"+d);
		this.display = d;
	}
	
	public PC_GresDisplayObject(Object...display){
		this(new ObjectChange(display));
	}
	
	public Object getDisplayObject(){
		if(this.display instanceof ObjectChange){
			return ((ObjectChange)this.display).display;
		}
		return this.display;
	}
	
	public Object getActiveDisplayObject(){
		if(this.display instanceof ObjectChange){
			return ((ObjectChange)this.display).getObject();
		}
		return this.display;
	}
	
	public int getActiveDisplayObjectIndex(){
		if(this.display instanceof ObjectChange){
			return ((ObjectChange)this.display).pos;
		}
		return 0;
	}
	
	public void setActiveDisplayObjectIndex(int index){
		if(this.display instanceof ObjectChange){
			((ObjectChange)this.display).pos = index;
		}
	}
	
	public PC_Vec2I getMinSize() {
		Object d = this.display;
		if(this.display instanceof ObjectChange){
			d = ((ObjectChange)this.display).getObject();
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
		Object d = this.display;
		if(this.display instanceof ObjectChange){
			d = ((ObjectChange)this.display).getObject();
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
		Object d = this.display;
		if(this.display instanceof ObjectChange){
			d = ((ObjectChange)this.display).getObject();
		}
		if(d instanceof IIcon){
			PC_GresRenderer.drawTerrainIcon(x, y, width, height, (IIcon)d);
		}else if(d instanceof ItemStack){
			int nx = x + width/2-8;
			int ny = y + height/2-8;
			PC_GresRenderer.drawItemStackAllreadyLighting(nx, ny, (ItemStack)d, null);
		}else if(d instanceof PC_GresTexture){
			((PC_GresTexture)d).draw(x, y, width, height, 0);
		}
	}
	
	@Override
	public void onEvent(PC_GresEvent event) {
		if(this.display instanceof ObjectChange){
			((ObjectChange) this.display).onEvent(event);
		}
	}
	
	private static class ObjectChange implements PC_IGresEventListener{

		Object[] display;
		int pos;
		
		ObjectChange(Object[] display){
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
		
		ObjectChange(Object display){
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
					this.pos++;
					this.pos %= this.display.length;
				}
			}
		}

		Object getObject(){
			return this.display[this.pos];
		}
		
	}
	
}
