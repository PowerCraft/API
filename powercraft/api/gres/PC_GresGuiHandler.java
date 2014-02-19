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
import powercraft.api.gres.layout.PC_IGresLayout;
import powercraft.api.gres.slot.PC_Slot;
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
	private long lastClickTime;
	private Slot lastSlotOver;
	private int lastClickButton;
	private boolean takeAll;
	private int scale;
	
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
		
	}

	public PC_IGresGui getClient() {

		return gui;
	}


	public void close() {

		mc.thePlayer.closeScreen();
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


	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {

		PC_GresRenderer.drawGradientRect(0, 0, rect.width, rect.height, -1072689136, -804253680);
	}


	protected void eventInitGui(int width, int height) {
		minSize.setTo(new PC_Vec2I(width, height));
		maxSize.setTo(minSize);
		prefSize.setTo(minSize);
		super.setSize(minSize);
		if (!initialized) {
			gui.initGui(this);
			initialized = true;
		}
		ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		int newScale = scaledresolution.getScaleFactor();
		if(newScale!=scale){
			scale = newScale;
			onScaleChanged(newScale);
		}
	}


	protected void eventUpdateScreen() {
		fireEvent(new PC_GresTickEvent(this, EventType.PRE));
		onTick();
		fireEvent(new PC_GresTickEvent(this, EventType.POST));
		if (!mc.thePlayer.isEntityAlive() || mc.thePlayer.isDead){
			close();
        }
	}


	protected void eventDrawScreen(PC_Vec2I mouse, float timeStamp) {
		ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		fireEvent(new PC_GresPaintEvent(this, EventType.PRE, timeStamp));
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		doPaint(new PC_Vec2I(0, 0), null, scaledresolution.getScaleFactor(), mc.displayHeight, timeStamp);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		drawMouseItemStack(mouse);
		fireEvent(new PC_GresPaintEvent(this, EventType.POST, timeStamp));
	}
	

	private void drawTooltip(PC_Vec2I mouse) {

		List<String> list = mouseOverComponent.getTooltip(mouse.sub(mouseOverComponent.getRealLocation()));
		if (list != null && !list.isEmpty()) {
			PC_GresRenderer.drawTooltip(mouse.x, mouse.y, rect.width, rect.height, list);
		}
	}


	protected void eventKeyTyped(char key, int keyCode) {

		PC_GresComponent c = focusedComponent;
		while(c!=null && !c.onKeyTyped(key, keyCode)){
			c = c.getParent();
		}
		if(c==null){
			PC_GresKeyEvent event = new PC_GresKeyEvent(this, key, keyCode);
			fireEvent(event);
			if (!event.isConsumed()) {
				if(!checkHotbarKeys(keyCode)){
					tryActionOnKeyTyped(key, keyCode);
				}
			}
		}
	}

	private boolean checkHotbarKeys(int keyCode){
		if (this.mc.thePlayer.inventory.getItemStack() == null && slotOver != null){
	    	for (int j = 0; j < 9; ++j){
	    		if (keyCode == 2 + j){
	    			sentMouseClickToServer(slotOver.slotNumber, j, 2);
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
		if (newMouseOverComponent != mouseOverComponent) {
			mouseOverComponent.onMouseLeave(mouse.sub(mouseOverComponent.getRealLocation()), buttons);
			newMouseOverComponent.onMouseEnter(mouse.sub(newMouseOverComponent.getRealLocation()), buttons);
			mouseOverComponent = newMouseOverComponent;
		}
	}


	protected void eventMouseButtonDown(PC_Vec2I mouse, int buttons, int eventButton) {

		setFocus(mouseOverComponent);
		inventoryMouseDown(mouse, buttons, eventButton);
		focusedComponent.onMouseButtonDown(mouse.sub(focusedComponent.getRealLocation()), buttons, eventButton);
	}


	protected void eventMouseButtonUp(PC_Vec2I mouse, int buttons, int eventButton) {

		inventoryMouseUp(mouse, buttons, eventButton);
		focusedComponent.onMouseButtonUp(mouse.sub(focusedComponent.getRealLocation()), buttons, eventButton);
	}


	protected void eventMouseMove(PC_Vec2I mouse, int buttons) {

		checkMouseOverComponent(mouse, buttons);
		inventoryMouseMove(mouse, buttons);
		mouseOverComponent.onMouseMove(mouse.sub(mouseOverComponent.getRealLocation()), buttons);
		if(mouseOverComponent!=focusedComponent)
			focusedComponent.onMouseMove(mouse.sub(focusedComponent.getRealLocation()), buttons);
	}


	protected void eventMouseWheel(PC_Vec2I mouse, int buttons, int wheel) {
		PC_GresComponent c = focusedComponent;
		while(c!=null && !c.onMouseWheel(mouse.sub(c.getRealLocation()), buttons, wheel)){
			c = c.getParent();
		}
	}


	@Override
	protected void notifyChange() {

		updateLayout();
	}


	public void setFocus(PC_GresComponent focusedComponent) {
		if(this.focusedComponent != focusedComponent){
			if(this.focusedComponent!=null){
				this.focusedComponent.onFocusLost(focusedComponent);
			}
			PC_GresComponent oldFocusedComponent = this.focusedComponent;
			this.focusedComponent = focusedComponent;
			if(focusedComponent!=null){
				focusedComponent.onFocusGot(oldFocusedComponent);
			}
		}
	}

	private ItemStack getMouseItemStack(){
		return mc.thePlayer.inventory.getItemStack();
	}
	
	private void drawMouseItemStack(PC_Vec2I mouse){
		ItemStack holdItemStack = getMouseItemStack();
		if (holdItemStack == null) {
			drawTooltip(mouse);
		}else{
			String text = null;
			holdItemStack = holdItemStack.copy();
			if(selectedSlots.size()>1){
				holdItemStack.stackSize = stackSize==-1?0:stackSize;
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
		slotOver = getSlotAtPosition(mouse);
		if(!takeAll && slotOver!=null && getMouseItemStack()!=null && stackSize!=-1 && slotClickButton!=-1 && isItemStacksCompatibleForSlot(getMouseItemStack(), slotOver) && canDragIntoSlot(slotOver)){
			if(!selectedSlots.contains(slotOver)){
				selectedSlots.add(slotOver);
				calcMouseStackSize();
			}
		}
	}
	
	private void calcMouseStackSize(){
		ItemStack itemStack = getMouseItemStack();
		if(itemStack==null){
			stackSize = 0;
		}else{
			stackSize = itemStack.stackSize;
			for(Slot slot:selectedSlots){
				int size = slot.getHasStack()?slot.getStack().stackSize:0;
				ItemStack is = itemStack.copy();
				is.stackSize = size+calcCount();
				if(is.stackSize>stackSize){
					is.stackSize = stackSize;
				}
				if (is.stackSize+size > is.getMaxStackSize()) {
					is.stackSize = is.getMaxStackSize()-size;
                }
                if (is.stackSize+size > slot.getSlotStackLimit()) {
                	is.stackSize = slot.getSlotStackLimit()-size;
                }
                stackSize -= is.stackSize;
			}
			if(selectedSlots.size()>=itemStack.stackSize){
				stackSize = -1;
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
		if(slotItemStack==null)
			return true;
		return itemStack.isItemEqual(slotItemStack) && ItemStack.areItemStackTagsEqual(itemStack, slotItemStack);
	}
	
	private void inventoryMouseDown(PC_Vec2I mouse, int buttons, int eventButton) {
		if(slotClickButton==-1){
			long clickTime = System.currentTimeMillis();
			boolean flag = lastSlotOver == slotOver && lastSlotOver!=null && clickTime - lastClickTime<250L && lastClickButton == eventButton;
			lastClickTime = clickTime;
			lastSlotOver = slotOver;
			if (slotOver!=null && slotOver.getHasStack() && eventButton == mc.gameSettings.keyBindPickBlock.getKeyCode() + 100){
				sentMouseClickToServer(slotOver.slotNumber, eventButton, 3);
            }else if(slotOver!=null && slotOver.getHasStack() && getMouseItemStack()==null){
				slotClickButton = eventButton;
				if(mc.gameSettings.touchscreen){
					onSlotClicked();
					if(getMouseItemStack()!=null)
						stackSize = getMouseItemStack().stackSize;
				}
			}else if(getMouseItemStack()!=null){
				if(slotOver!=null){
					takeAll = flag;
					slotClickButton = eventButton;
					selectedSlots.clear();
					inventoryMouseMove(mouse, buttons);
				}else if(mouseOverComponent==this){
					sentMouseClickToServer(-999, eventButton, 0);
				}
			}
		}
	}
	
	private void onSlotClicked(){
		if(GuiScreen.isShiftKeyDown()){
			sentMouseClickToServer(slotOver.slotNumber, slotClickButton, 1);
		}else{
			sentMouseClickToServer(slotOver.slotNumber, slotClickButton, 0);
		}
	}
	
	@SuppressWarnings("unused")
	private void inventoryMouseUp(PC_Vec2I mouse, int buttons, int eventButton){
		if(slotClickButton==eventButton && getMouseItemStack()==null){
			onSlotClicked();
		}else if(getMouseItemStack()!=null){
			onSlotFill();
			selectedSlots.clear();
		}
		takeAll = false;
		slotClickButton = -1;
		if(getMouseItemStack()!=null)
			stackSize = getMouseItemStack().stackSize;
	}
	
	private void onSlotFill(){
		if(takeAll){
			takeAll = false;
			sentMouseClickToServer(lastSlotOver.slotNumber, slotClickButton, 6);
		}else{
			if(selectedSlots.size()==1){
				sentMouseClickToServer(selectedSlots.get(0).slotNumber, slotClickButton, 0);
			}else if(selectedSlots.size()>0){
				sentMouseClickToServer(-999, Container.func_94534_d(0, slotClickButton), 5);
				for(Slot slot:selectedSlots){
		            sentMouseClickToServer(slot.slotNumber, Container.func_94534_d(1, slotClickButton), 5);
				}
				sentMouseClickToServer(-999, Container.func_94534_d(2, slotClickButton), 5);
			}
		}
	}
	
	private int calcCount(){
        switch (slotClickButton){
        case 0:
        	ItemStack itemStack = getMouseItemStack();
        	if(itemStack==null)
        		return 0;
        	return PC_MathHelper.floor_float(itemStack.stackSize / (float)selectedSlots.size());
        case 1:
            return 1;
		default:
			return 0;
        }
    }
	
	private void sentMouseClickToServer(int slotNumber, int mouseButton, int transfer){
		if(gui instanceof PC_GresBaseWithInventory){
			this.mc.playerController.windowClick(((PC_GresBaseWithInventory)gui).windowId, slotNumber, mouseButton, transfer, this.mc.thePlayer);
		}
	}
	
	protected void renderSlot(int x, int y, Slot slot) {
		boolean renderGray = false;
		String text = null;
		ItemStack itemStack = slot.getStack();
		ItemStack mouseItemStack = getMouseItemStack();
		if(selectedSlots.contains(slot) && selectedSlots.size()>1){
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
				selectedSlots.remove(slot);
			}
		}else if(slot==slotOver){
			renderGray = true;
		}
		renderGray &= slot.func_111238_b();
		if(slot instanceof PC_Slot){
			PC_Slot sSlot = (PC_Slot)slot;
			if(itemStack==null){
				itemStack = sSlot.getBackgroundStack();
				renderGray |= sSlot.renderGrayWhenEmpty();
			}
		}
		
		if(renderGray){
             PC_GresRenderer.drawGradientRect(x, y, 16, 16, -2130706433, -2130706433);
		}
		PC_GresRenderer.drawEasyItemStack(x, y, itemStack, text);
	}

	public void eventGuiClosed() {
		if (mc.thePlayer != null && gui instanceof PC_GresBaseWithInventory) {
            ((PC_GresBaseWithInventory)gui).onContainerClosed(this.mc.thePlayer);
        }
	}
	
}
