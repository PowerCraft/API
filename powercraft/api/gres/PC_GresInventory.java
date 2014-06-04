package powercraft.api.gres;


import java.util.List;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Rect;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.slot.PC_Slot;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresInventory extends PC_GresComponent {

	protected static final String textureName = "Slot";

	protected static final String[] colorTextureNames = {"F1", "F2", "F3", "F4", "F5", "F6"};
	
	protected static final String[] colorTextureNamesH = {"H1", "H2", "H3", "H4", "H5", "H6"};
	
	protected static final String[] colorTextureNamesV = {"V1", "V2", "V3", "V4", "V5", "V6"};
	
	protected Slot slots[][];

	protected int slotWidth = 0;

	protected int slotHeight = 0;


	public PC_GresInventory(int width, int height) {

		this.slotWidth = 18;
		this.slotHeight = 18;

		this.slots = new Slot[width][height];
	}


	public PC_GresInventory(int width, int height, int slotWidth, int slotHeight) {

		this.slotWidth = slotWidth;
		this.slotHeight = slotHeight;

		this.slots = new Slot[width][height];
	}


	public PC_GresInventory setSlot(int x, int y, Slot slot) {

		if (x >= 0 && x < this.slots.length && y >= 0 && y < this.slots[x].length) {
			this.slots[x][y] = slot;
		}
		return this;
	}
	
	public PC_GresInventory setSlots(Slot[] slot, int start) {

		int pos = start;
		boolean next = true;
		int y = 0;
		while(next){
			next = false;
			for(int x=0; x<this.slots.length; x++){
				if(this.slots[x].length>y){
					next = true;
					this.slots[x][y] = slot[pos++];
				}
			}
			y++;
		}
		
		return this;
	}


	public Slot getSlot(int x, int y) {

		if (x >= 0 && x < this.slots.length && y >= 0 && y < this.slots[x].length) {
			return this.slots[x][y];
		}
		return null;
	}


	@Override
	protected PC_Vec2I calculateMinSize() {

		return calculatePrefSize();
	}


	@Override
	protected PC_Vec2I calculateMaxSize() {

		return calculatePrefSize();
	}


	@Override
	protected PC_Vec2I calculatePrefSize() {

		return new PC_Vec2I(this.slots.length * this.slotWidth+2, this.slots[0].length * this.slotHeight+2);
	}


	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor3f(1, 1, 1);
		PC_GresTexture[] textures = new PC_GresTexture[6];
		PC_GresTexture[] textures1 = new PC_GresTexture[6];
		PC_GresTexture[] textures2 = new PC_GresTexture[6];
		for (int i = 0; i < 6; i++) {
			textures[i] = PC_Gres.getGresTexture(colorTextureNames[i]);
			textures1[i] = PC_Gres.getGresTexture(colorTextureNamesH[i]);
			textures2[i] = PC_Gres.getGresTexture(colorTextureNamesV[i]);
		}
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		for (int x = 0; x < this.slots.length; x++) {
			for (int y = 0; y < this.slots[x].length; y++) {
				drawTexture(textureName, x * this.slotWidth+1, y * this.slotHeight+1, this.slotWidth, this.slotHeight);
				Slot slot = this.slots[x][y];
				if(slot instanceof PC_Slot){
					int[] s = ((PC_Slot) slot).getAppliedSides();
					if(s!=null && s.length>0){
						float h = this.slotHeight/(float)s.length;
						float h2 = (this.slotHeight-2)/(float)s.length;
						int yp = 0;
						int xx = x * this.slotWidth + 1;
						int yy = (int) (y * this.slotHeight+2 + yp/(float)this.slotHeight);
						int w = this.slotWidth;
						if(y==0){
							textures1[s[0]].drawProzentual(xx, 0, w, 1, xx/(float)this.rect.width, 0, (w-2)/(float)this.rect.width, 1, 0);
						}
						if(y==this.slots[x].length-1){
							textures1[s[s.length-1]].drawProzentual(xx, this.slots[x].length*this.slotHeight+1, w, 1, xx/(float)this.rect.width, 0, (w-2)/(float)this.rect.width, 1, 0);
						}
						for(int i=0; i<s.length; i++){
							yp = (int) (h*i+0.5);
							int hh = (int) (h*(i+1)+0.5)-yp;
							int yp2 = (int) (h2*i+0.5);
							int hh2 = (int) (h2*(i+1)+0.5)-yp2;
							int yyy = yy + yp-1;
							int hhh = hh;
							if(i==0){
								hhh--;
								yyy++;
							}
							if(i==s.length-1){
								hhh--;
							}
							textures[s[i]].drawProzentual(xx+1, yyy, this.slotWidth-2, hhh, 0, yp2/(float)(this.slotHeight-2), 1, hh2/(float)(this.slotHeight-2), 0);
							hhh = hh + (y==0 && i==0?1:0) + (y==this.slots[x].length-1 && i==s.length-1?1:0);
							yyy = y==0 && i==0?0:yy + yp-1;
							if(x==0){
								textures2[s[i]].drawProzentual(0, yyy, 1, hhh, 0, yyy/(float)this.rect.height, 1, (hh-2)/(float)this.rect.height, 0);
							}
							if(x==this.slots.length-1){
								textures2[s[i]].drawProzentual(this.slots.length*this.slotWidth+1, yyy, 1, hhh, 0, yyy/(float)this.rect.height, 1, (hh-2)/(float)this.rect.height, 0);
							}
							
						}
					}
				}
			}
		}
		
		PC_GresGuiHandler guiHandler = getGuiHandler();
		for (int x = 0, xp = 2+(this.slotWidth-18)/2; x < this.slots.length; x++, xp += this.slotWidth) {
			for (int y = 0, yp = 2+(this.slotHeight-18)/2; y < this.slots[x].length; y++, yp += this.slotHeight) {
				if (this.slots[x][y] != null) {
					Slot slot = this.slots[x][y];
					guiHandler.renderSlot(xp, yp, slot);
				}
			}
		}
	}
	
	@Override
	public Slot getSlotAtPosition(PC_Vec2I position) {

		int x = position.x / this.slotWidth;
		int y = position.y / this.slotHeight;
		if (x >= 0 && y >= 0 && x < this.slots.length && y < this.slots[x].length) {
			return this.slots[x][y];
		}
		return null;
	}


	@Override
	public List<String> getTooltip(PC_Vec2I position) {

		Slot slot = getSlotAtPosition(position);
		if (slot != null) {
			ItemStack itemstack = null;

			if (slot.getHasStack()) itemstack = slot.getStack();

			//if (slot instanceof PC_Slot && ((PC_Slot) slot).getBackgroundStack() != null && ((PC_Slot) slot).renderTooltipWhenEmpty())
			//itemstack = ((PC_Slot) slot).getBackgroundStack();

			if (itemstack != null) {
				@SuppressWarnings("unchecked")
				List<String> l = itemstack.getTooltip(PC_ClientUtils.mc().thePlayer, PC_ClientUtils.mc().gameSettings.advancedItemTooltips);
				
				for (int k = 0; k < l.size(); ++k){
		            if (k == 0){
		                l.set(k, itemstack.getRarity().rarityColor + l.get(k));
		            }else{
		                l.set(k, EnumChatFormatting.GRAY + l.get(k));
		            }
		        }
				return l;
			}
		}
		return super.getTooltip(position);
	}

}
