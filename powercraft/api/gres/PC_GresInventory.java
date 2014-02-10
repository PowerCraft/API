package powercraft.api.gres;


import java.util.List;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_RectI;
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

		slotWidth = 18;
		slotHeight = 18;

		slots = new Slot[width][height];
	}


	public PC_GresInventory(int width, int height, int slotWidth, int slotHeight) {

		this.slotWidth = slotWidth;
		this.slotHeight = slotHeight;

		slots = new Slot[width][height];
	}


	public PC_GresInventory setSlot(int x, int y, Slot slot) {

		if (x >= 0 && x < this.slots.length && y >= 0 && y < this.slots[x].length) {
			this.slots[x][y] = slot;
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

		return new PC_Vec2I(slots.length * slotWidth+2, slots[0].length * slotHeight+2);
	}


	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {

		PC_GresTexture[] textures = new PC_GresTexture[6];
		PC_GresTexture[] textures1 = new PC_GresTexture[6];
		PC_GresTexture[] textures2 = new PC_GresTexture[6];
		for (int i = 0; i < 6; i++) {
			textures[i] = PC_Gres.getGresTexture(colorTextureNames[i]);
			textures1[i] = PC_Gres.getGresTexture(colorTextureNamesH[i]);
			textures2[i] = PC_Gres.getGresTexture(colorTextureNamesV[i]);
		}
		for (int x = 0; x < slots.length; x++) {
			for (int y = 0; y < slots[x].length; y++) {
				drawTexture(textureName, x * slotWidth+1, y * slotHeight+1, slotWidth, slotHeight);
				Slot slot = slots[x][y];
				if(slot instanceof PC_Slot){
					int[] s = ((PC_Slot) slot).getAppliedSides();
					if(s!=null && s.length>0){
						float h = (slotHeight-2.0f)/s.length;
						int yp = 0;
						int xx = x * slotWidth + 2;
						int yy = (int) (y * slotHeight+2 + yp/(float)slotHeight);
						if(y==0){
							int w = slotWidth + (x==0?1:0) + (x==slots[x].length-1?1:0)-2;
							int xxx = x==0?1:xx;
							textures1[s[0]].drawProzentual(xxx, 0, w, 1, xxx/(float)rect.width, 0, w/(float)rect.width, 1, 0);
						}
						if(y==slots[x].length-1){
							int w = slotWidth + (x==0?1:0) + (x==slots[x].length-1?1:0)-2;
							int xxx = x==0?1:xx;
							textures1[s[s.length-1]].drawProzentual(xxx, slots[x].length*slotHeight+1, w, 1, xxx/(float)rect.width, 0, w/(float)rect.width, 1, 0);
						}
						for(int i=0; i<s.length; i++){
							yp = (int) (h*i+0.5);
							int hh = (int) (h*(i+1)+0.5)-yp;
							float tp = yp/(slotHeight-2.0f);
							textures[s[i]].drawProzentual(xx, yy + yp, slotWidth-2, hh, 0, tp, 1, hh/(slotHeight-2.0f), 0);
							if(x==0){
								int hhh = hh + (y==0 && i==0?2:0) + (y==slots[x].length-1 && i==s.length-1?2:0);
								int yyy = y==0 && i==0?0:yy + yp;
								textures2[s[i]].drawProzentual(0, yyy, 1, hhh, 0, yyy/(float)rect.height, 1, hhh/(float)rect.height, 0);
							}
							if(x==slots.length-1){
								int hhh = hh + (y==0 && i==0?2:0) + (y==slots[x].length-1 && i==s.length-1?2:0);
								int yyy = y==0 && i==0?0:yy + yp;
								textures2[s[i]].drawProzentual(slots.length*slotWidth+1, yyy, 1, hhh, 0, yyy/(float)rect.height, 1, hhh/(float)rect.height, 0);
							}
							
						}
					}
				}
			}
		}
		
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		int k = 240;
		int i1 = 240;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k / 1.0F, i1 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		PC_GresGuiHandler guiHandler = getGuiHandler();

		for (int x = 0, xp = 2+(slotWidth-18)/2; x < slots.length; x++, xp += slotWidth) {
			for (int y = 0, yp = 2+(slotHeight-18)/2; y < slots[x].length; y++, yp += slotHeight) {
				if (slots[x][y] != null) {
					Slot slot = slots[x][y];
					guiHandler.renderSlot(xp, yp, slot);
				}
			}
		}
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
	}
	
	@Override
	protected Slot getSlotAtPosition(PC_Vec2I position) {

		int x = position.x / slotWidth;
		int y = position.y / slotHeight;
		if (x >= 0 && y >= 0 && x < slots.length && y < slots[x].length) {
			return slots[x][y];
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
				l.set(0,
						(new StringBuilder()).append("\247").append(itemstack.getRarity().rarityColor).append(l.get(0))
								.append("\2477").toString());
				return l;
			}
		}
		return super.getTooltip(position);
	}

}
