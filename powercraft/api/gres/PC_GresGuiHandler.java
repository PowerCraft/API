package powercraft.api.gres;


import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_MathHelper;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_GresPaintEvent;
import powercraft.api.gres.events.PC_GresPrePostEvent.EventType;
import powercraft.api.gres.events.PC_GresTickEvent;
import powercraft.api.gres.history.PC_GresHistory;
import powercraft.api.gres.layout.PC_IGresLayout;
import powercraft.api.gres.slot.PC_Slot;
import powercraft.api.gres.slot.PC_SlotPhantom;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresGuiHandler extends PC_GresContainer {
	
	private final PC_IGresGui gui;
	private final Minecraft mc;
	private boolean initialized;
	private PC_GresComponent focusedComponent = this;
	private PC_GresComponent mouseOverComponent = this;
	
	private final List<Slot> selectedSlots = new ArrayList<Slot>();
	private Slot slotOver;
	private int stackSize;
	private int slotClickButton = -1;
	private Slot lastSlotOver;
	private int lastClickButton;
	private boolean takeAll;
	private int scale;
	
	private long last = System.currentTimeMillis();
	
	private PC_GresHistory history = new PC_GresHistory(100);
	
	protected PC_GresGuiHandler(PC_IGresGui gui) {

		this.gui = gui;
		this.mc = PC_ClientUtils.mc();
		super.setLayout(new PC_IGresLayout() {
			
			@Override
			public PC_Vec2I getPreferredLayoutSize(PC_GresContainer container) {
				return container.getPrefSize();
			}
			
			@Override
			public PC_Vec2I getMinimumLayoutSize(PC_GresContainer container) {
				return container.getMaxSize();
			}
			
			@Override
			public void updateLayout(PC_GresContainer container) {
				for (PC_GresComponent component : container.children) {
					if(component.layoutData==null)
						component.putInRect(0, 0, container.rect.width, container.rect.height);
				}
			}
			
		});
	}
	
	@Override
	public void setLayout(PC_IGresLayout layout) {
		//
	}

	public PC_IGresGui getClient() {

		return this.gui;
	}


	public void close() {

		this.mc.thePlayer.closeScreen();
	}


	@Override
	public void setVisible(boolean visible) {

		if (!visible) close();
	}


	@Override
	protected void setParentVisible(boolean visible) {

		throw new IllegalArgumentException("GresGuiHandler can't have a parent");
	}


	@Override
	protected void setParent(PC_GresContainer parent) {

		throw new IllegalArgumentException("GresGuiHandler can't have a parent");
	}


	@Override
	public PC_GresGuiHandler getGuiHandler() {

		return this;
	}


	@Override
	public void setRect(PC_RectI rect) {

		throw new IllegalArgumentException("GresGuiHandler can't set rect");
	}


	@Override
	public void setSize(PC_Vec2I size) {

		throw new IllegalArgumentException("GresGuiHandler can't set size");
	}


	@Override
	public void setMinSize(PC_Vec2I minSize) {

		throw new IllegalArgumentException("GresGuiHandler can't set minsize");
	}


	@Override
	protected PC_Vec2I calculateMinSize() {

		return new PC_Vec2I();
	}


	@Override
	public void setMaxSize(PC_Vec2I maxSize) {

		throw new IllegalArgumentException("GresGuiHandler can't set maxsize");
	}


	@Override
	protected PC_Vec2I calculateMaxSize() {

		return new PC_Vec2I();
	}


	@Override
	public void setPrefSize(PC_Vec2I prefSize) {

		throw new IllegalArgumentException("GresGuiHandler can't set prefsize");
	}


	@Override
	protected PC_Vec2I calculatePrefSize() {

		return new PC_Vec2I();
	}


	@SuppressWarnings("hiding")
	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {

		PC_GresRenderer.drawGradientRect(0, 0, this.rect.width, this.rect.height, -1072689136, -804253680);
	}


	protected void eventInitGui(int width, int height) {
		this.minSize.setTo(new PC_Vec2I(width, height));
		this.maxSize.setTo(this.minSize);
		this.prefSize.setTo(this.minSize);
		super.setSize(this.minSize);
		if (!this.initialized) {
			this.gui.initGui(this);
			this.initialized = true;
		}
		ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
		int newScale = scaledresolution.getScaleFactor();
		if(newScale!=this.scale){
			this.scale = newScale;
			onScaleChanged(newScale);
		}
	}


	protected void eventUpdateScreen() {
		fireEvent(new PC_GresTickEvent(this, EventType.PRE));
		onTick();
		fireEvent(new PC_GresTickEvent(this, EventType.POST));
		if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead){
			close();
        }
	}


	@SuppressWarnings("unused")
	protected void eventDrawScreen(PC_Vec2I mouse, float timeStamp) {
		long t = System.currentTimeMillis();
		float ts = (t-this.last)/1000.0f;
		this.last = t;
		
		ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
		fireEvent(new PC_GresPaintEvent(this, EventType.PRE, ts));
		onDrawTick(ts);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		doPaint(new PC_Vec2I(0, 0), null, scaledresolution.getScaleFactor(), this.mc.displayHeight, ts);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		drawMouseItemStack(mouse);
		fireEvent(new PC_GresPaintEvent(this, EventType.POST, ts));
	}
	

	private void drawTooltip(PC_Vec2I mouse) {

		List<String> list = this.mouseOverComponent.onGetTooltip(mouse.sub(this.mouseOverComponent.getRealLocation()));
		if (list != null && !list.isEmpty()) {
			PC_GresRenderer.drawTooltip(mouse.x, mouse.y, this.rect.width, this.rect.height, list);
		}
	}


	protected void eventKeyTyped(char key, int keyCode, boolean repeat) {
		
		switch(keyCode){
		case Keyboard.KEY_Z:
			if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)){
				this.history.undo();
				return;
			}
			break;
		case Keyboard.KEY_Y:
			if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)){
				this.history.redo();
				return;
			}
			break;
		default:
			break;
		}
		
		PC_GresComponent c = this.focusedComponent;
		while(c!=null && !c.onKeyTyped(key, keyCode, repeat, this.history)){
			c = c.getParent();
		}
		if(c==null){
			PC_GresKeyEvent event = new PC_GresKeyEvent(this, key, keyCode, repeat, this.history);
			fireEvent(event);
			if (!event.isConsumed()) {
				if(!checkHotbarKeys(keyCode)){
					tryActionOnKeyTyped(key, keyCode, repeat, this.history);
				}
			}
		}
	}

	private boolean checkHotbarKeys(int keyCode){
		if (this.mc.thePlayer.inventory.getItemStack() == null && this.slotOver != null){
	    	for (int j = 0; j < 9; ++j){
	    		if (keyCode == 2 + j){
	    			sentMouseClickToServer(this.slotOver.slotNumber, j, 2);
	                return true;
	            }
	        }
	    }

	    return false;
	}
	

	private void checkMouseOverComponent(PC_Vec2I mouse, int buttons) {

		PC_GresComponent newMouseOverComponent = getComponentAtPosition(mouse);
		if (newMouseOverComponent == null) {
			newMouseOverComponent = this;
		}
		if (newMouseOverComponent != this.mouseOverComponent) {
			this.mouseOverComponent.onMouseLeave(mouse.sub(this.mouseOverComponent.getRealLocation()), buttons, this.history);
			newMouseOverComponent.onMouseEnter(mouse.sub(newMouseOverComponent.getRealLocation()), buttons, this.history);
			this.mouseOverComponent = newMouseOverComponent;
		}
	}


	protected void eventMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick) {

		setFocus(this.mouseOverComponent);
		inventoryMouseDown(mouse, buttons, eventButton, doubleClick);
		this.focusedComponent.onMouseButtonDown(mouse.sub(this.focusedComponent.getRealLocation()), buttons, eventButton, doubleClick, this.history);
	}


	protected void eventMouseButtonUp(PC_Vec2I mouse, int buttons, int eventButton) {

		inventoryMouseUp(mouse, buttons, eventButton);
		this.focusedComponent.onMouseButtonUp(mouse.sub(this.focusedComponent.getRealLocation()), buttons, eventButton, this.history);
	}


	protected void eventMouseMove(PC_Vec2I mouse, int buttons) {

		checkMouseOverComponent(mouse, buttons);
		inventoryMouseMove(mouse, buttons);
		this.mouseOverComponent.onMouseMove(mouse.sub(this.mouseOverComponent.getRealLocation()), buttons, this.history);
		if(this.mouseOverComponent!=this.focusedComponent)
			this.focusedComponent.onMouseMove(mouse.sub(this.focusedComponent.getRealLocation()), buttons, this.history);
	}


	protected void eventMouseWheel(PC_Vec2I mouse, int buttons, int wheel) {
		PC_GresComponent c = this.focusedComponent;
		while(c!=null && !c.onMouseWheel(mouse.sub(c.getRealLocation()), buttons, wheel, this.history)){
			c = c.getParent();
		}
	}


	@Override
	protected void notifyChange() {

		updateLayout();
	}


	public void setFocus(PC_GresComponent focusedComponent) {
		if(this.focusedComponent != focusedComponent){
			PC_GresComponent oldFocusedComponent = this.focusedComponent;
			this.focusedComponent = focusedComponent;
			if(oldFocusedComponent!=null){
				oldFocusedComponent.onFocusLost(focusedComponent, this.history);
			}
			if(focusedComponent!=null){
				focusedComponent.onFocusGot(oldFocusedComponent, this.history);
			}
			onFocusChaned(oldFocusedComponent, focusedComponent);
		}
	}

	private ItemStack getMouseItemStack(){
		return this.mc.thePlayer.inventory.getItemStack();
	}
	
	@SuppressWarnings("hiding")
	private void drawMouseItemStack(PC_Vec2I mouse){
		ItemStack holdItemStack = getMouseItemStack();
		if (holdItemStack == null) {
			drawTooltip(mouse);
		}else{
			String text = null;
			holdItemStack = holdItemStack.copy();
			if(this.selectedSlots.size()>1){
				holdItemStack.stackSize = this.stackSize==-1?0:this.stackSize;
				if(holdItemStack.stackSize==0){
					text = EnumChatFormatting.YELLOW+"0";
				}
			}
			GL11.glTranslated(0, 0, 100);
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			int k = 240;
			int i1 = 240;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k / 1.0F, i1 / 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			PC_GresRenderer.drawItemStack(mouse.x-8, mouse.y-8, holdItemStack, text);
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			RenderHelper.disableStandardItemLighting();
			GL11.glTranslated(0, 0, -100);
		}
	}
	
	@SuppressWarnings("unused")
	private void inventoryMouseMove(PC_Vec2I mouse, int buttons){
		this.slotOver = getSlotAtPosition(mouse);
		if(!this.takeAll && this.slotOver!=null && getMouseItemStack()!=null && this.stackSize!=-1 && this.slotClickButton!=-1 && isItemStacksCompatibleForSlot(getMouseItemStack(), this.slotOver) && canDragIntoSlot(this.slotOver)){
			if(!this.selectedSlots.contains(this.slotOver)){
				this.selectedSlots.add(this.slotOver);
				calcMouseStackSize();
			}
		}
	}
	
	private void calcMouseStackSize(){
		ItemStack itemStack = getMouseItemStack();
		if(itemStack==null){
			this.stackSize = 0;
		}else{
			this.stackSize = itemStack.stackSize;
			for(Slot slot:this.selectedSlots){
				int size = slot.getHasStack()?slot.getStack().stackSize:0;
				ItemStack is = itemStack.copy();
				is.stackSize = size+calcCount();
				if(is.stackSize>this.stackSize){
					is.stackSize = this.stackSize;
				}
				if (is.stackSize+size > is.getMaxStackSize()) {
					is.stackSize = is.getMaxStackSize()-size;
                }
                if (is.stackSize+size > slot.getSlotStackLimit()) {
                	is.stackSize = slot.getSlotStackLimit()-size;
                }
                this.stackSize -= is.stackSize;
			}
			if(this.selectedSlots.size()>=itemStack.stackSize){
				this.stackSize = -1;
			}
		}
	}
	
	private static boolean canDragIntoSlot(Slot slot){
		if(slot instanceof PC_Slot){
			return ((PC_Slot)slot).canDragIntoSlot();
		}
        return true;
	}
	
	private static boolean isItemStacksCompatibleForSlot(ItemStack itemStack, Slot slot){
		ItemStack slotItemStack = slot.getStack();
		if(itemStack==null){
			return false;
		}
		if(slotItemStack==null || slot instanceof PC_SlotPhantom)
			return true;
		return itemStack.isItemEqual(slotItemStack) && ItemStack.areItemStackTagsEqual(itemStack, slotItemStack);
	}
	
	private void inventoryMouseDown(PC_Vec2I mouse, int buttons, int eventButton, boolean doubleClick) {
		if(this.slotClickButton==-1){
			boolean flag = this.lastSlotOver == this.slotOver && this.lastSlotOver!=null && doubleClick && this.lastClickButton == eventButton;
			this.lastSlotOver = this.slotOver;
			if (this.slotOver!=null && this.slotOver.getHasStack() && eventButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100){
				sentMouseClickToServer(this.slotOver.slotNumber, eventButton, 3);
            }else if(this.slotOver!=null && this.slotOver.getHasStack() && getMouseItemStack()==null){
				this.slotClickButton = eventButton;
				if(this.mc.gameSettings.touchscreen){
					onSlotClicked();
					if(getMouseItemStack()!=null)
						this.stackSize = getMouseItemStack().stackSize;
				}
			}else if(getMouseItemStack()!=null){
				if(this.slotOver!=null){
					this.takeAll = flag;
					this.slotClickButton = eventButton;
					this.selectedSlots.clear();
					inventoryMouseMove(mouse, buttons);
				}else if(this.mouseOverComponent==this){
					sentMouseClickToServer(-999, eventButton, 0);
				}
			}
		}
	}
	
	private void onSlotClicked(){
		if(GuiScreen.isShiftKeyDown()){
			sentMouseClickToServer(this.slotOver.slotNumber, this.slotClickButton, 1);
		}else{
			sentMouseClickToServer(this.slotOver.slotNumber, this.slotClickButton, 0);
		}
	}
	
	@SuppressWarnings("unused")
	private void inventoryMouseUp(PC_Vec2I mouse, int buttons, int eventButton){
		if(this.slotClickButton==eventButton && getMouseItemStack()==null){
			if(this.slotOver==null)
				return;
			onSlotClicked();
		}else if(getMouseItemStack()!=null){
			onSlotFill();
			this.selectedSlots.clear();
		}
		this.takeAll = false;
		this.slotClickButton = -1;
		if(getMouseItemStack()!=null)
			this.stackSize = getMouseItemStack().stackSize;
	}
	
	private void onSlotFill(){
		if(this.takeAll){
			this.takeAll = false;
			sentMouseClickToServer(this.lastSlotOver.slotNumber, this.slotClickButton, 6);
		}else{
			if(this.selectedSlots.size()==1){
				sentMouseClickToServer(this.selectedSlots.get(0).slotNumber, this.slotClickButton, 0);
			}else if(this.selectedSlots.size()>0){
				sentMouseClickToServer(-999, Container.func_94534_d(0, this.slotClickButton), 5);
				for(Slot slot:this.selectedSlots){
		            sentMouseClickToServer(slot.slotNumber, Container.func_94534_d(1, this.slotClickButton), 5);
				}
				sentMouseClickToServer(-999, Container.func_94534_d(2, this.slotClickButton), 5);
			}
		}
	}
	
	private int calcCount(){
        switch (this.slotClickButton){
        case 0:
        	ItemStack itemStack = getMouseItemStack();
        	if(itemStack==null)
        		return 0;
        	return PC_MathHelper.floor_float(itemStack.stackSize / (float)this.selectedSlots.size());
        case 1:
            return 1;
		default:
			return 0;
        }
    }
	
	private void sentMouseClickToServer(int slotNumber, int mouseButton, int transfer){
		if(this.gui instanceof PC_GresBaseWithInventory){
			this.mc.playerController.windowClick(((PC_GresBaseWithInventory)this.gui).windowId, slotNumber, mouseButton, transfer, this.mc.thePlayer);
		}
	}
	
	@SuppressWarnings("hiding")
	protected void renderSlot(int x, int y, Slot slot) {
		boolean renderGray = false;
		String text = null;
		ItemStack itemStack = slot.getStack();
		ItemStack mouseItemStack = getMouseItemStack();
		if(this.selectedSlots.contains(slot) && this.selectedSlots.size()>1){
			if(isItemStacksCompatibleForSlot(mouseItemStack, slot) && canDragIntoSlot(slot)){
				int size = slot.getHasStack()?itemStack.stackSize:0;
				itemStack = mouseItemStack.copy();
				itemStack.stackSize = size+calcCount();
				renderGray = true;
				if (itemStack.stackSize > itemStack.getMaxStackSize()) {
					text = ""+EnumChatFormatting.YELLOW + itemStack.getMaxStackSize();
					itemStack.stackSize = itemStack.getMaxStackSize();
                }
                if (itemStack.stackSize > slot.getSlotStackLimit()) {
                	text = ""+EnumChatFormatting.YELLOW + slot.getSlotStackLimit();
                	itemStack.stackSize = slot.getSlotStackLimit();
                }
			}else{
				this.selectedSlots.remove(slot);
			}
		}else if(slot==this.slotOver){
			renderGray = true;
		}
		renderGray &= slot.func_111238_b();
		boolean renderGrayAfter = false;
		if(slot instanceof PC_Slot){
			PC_Slot sSlot = (PC_Slot)slot;
			if(itemStack==null){
				itemStack = sSlot.getBackgroundStack();
				renderGrayAfter = sSlot.renderGrayWhenEmpty();
			}
		}
		if(renderGray){
             PC_GresRenderer.drawGradientRect(x, y, 16, 16, -2130706433, -2130706433);
		}
		PC_GresRenderer.drawEasyItemStack(x, y, itemStack, text);
		if(renderGrayAfter){
            PC_GresRenderer.drawGradientRect(x, y, 16, 16, -2130706433, -2130706433);
		}
	}

	public void eventGuiClosed() {
		if (this.mc.thePlayer != null && this.gui instanceof PC_GresBaseWithInventory) {
            ((PC_GresBaseWithInventory)this.gui).onContainerClosed(this.mc.thePlayer);
        }
	}
	
	public PC_GresHistory getHistory(){
		return this.history;
	}

	public void onAdded(PC_GresComponent component) {
		component.onScaleChanged(this.scale);
	}
	
}
