package powercraft.api.gres;


import net.minecraft.inventory.Slot;
import powercraft.api.PC_Rect;
import powercraft.api.PC_Vec2I;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresPlayerInventory extends PC_GresInventory {

	public PC_GresPlayerInventory(PC_GresBaseWithInventory base) {

		super(9, 4);
		for (int x = 0; x < this.slots.length; x++) {
			for (int y = 0; y < 3; y++) {
				setSlot(x, y, base.inventoryPlayerUpper[x][y]);
			}
		}
		for (int x = 0; x < this.slots.length; x++) {
			setSlot(x, 3, base.inventoryPlayerLower[x]);
		}
	}


	@Override
	protected PC_Vec2I calculatePrefSize() {

		return new PC_Vec2I(this.slots.length * this.slotWidth, this.slots[0].length * this.slotHeight + 4);
	}


	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {

		for (int x = 0; x < this.slots.length; x++) {
			for (int y = 0; y < 3; y++) {
				drawTexture(textureName, x * this.slotWidth, y * this.slotHeight, this.slotWidth, this.slotHeight);
			}
		}

		for (int x = 0; x < this.slots.length; x++) {
			drawTexture(textureName, x * this.slotWidth, 3 * this.slotHeight + 4, this.slotWidth, this.slotHeight);
		}

		PC_GresGuiHandler guiHandler = getGuiHandler();
		
		for (int x = 0, xp = 1+(this.slotWidth-18)/2; x < this.slots.length; x++, xp += this.slotWidth) {
			for (int y = 0, yp = 1+(this.slotHeight-18)/2; y < 3; y++, yp += this.slotHeight) {
				if (this.slots[x][y] != null) {
					Slot slot = this.slots[x][y];
					guiHandler.renderSlot(xp, yp, slot);
				}
			}
		}

		for (int x = 0, xp = 1+(this.slotWidth-18)/2; x < this.slots.length; x++, xp += this.slotWidth) {
			if (this.slots[x][3] != null) {
				Slot slot = this.slots[x][3];
				guiHandler.renderSlot(xp, 5 + this.slotHeight * 3 + (this.slotHeight-18)/2, slot);
			}
		}
	}


	@Override
	public Slot getSlotAtPosition(PC_Vec2I position) {

		if (position.y < this.slotHeight * 3) {
			return super.getSlotAtPosition(position);
		} else if (position.y >= this.slotHeight * 3 + 4) {
			return super.getSlotAtPosition(position.sub(0, 4));
		}
		return null;
	}

}
