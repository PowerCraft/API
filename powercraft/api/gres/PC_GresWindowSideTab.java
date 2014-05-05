package powercraft.api.gres;

import java.text.DecimalFormat;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

import org.lwjgl.opengl.GL11;

import powercraft.api.PC_Direction;
import powercraft.api.PC_Rect;
import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2;
import powercraft.api.PC_Vec2I;
import powercraft.api.PC_Vec3;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.gres.PC_GresAlign.H;
import powercraft.api.gres.PC_GresAlign.V;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent.Event;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.history.PC_GresHistory;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.api.inventory.PC_ISidedInventory;
import powercraft.api.redstone.PC_RedstoneWorkType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresWindowSideTab extends PC_GresContainer {

	private static PC_GresWindowSideTab openSideTab;
	
	private final PC_Vec2I size = new PC_Vec2I(20, 20);
	
	private final PC_Vec3 color = new PC_Vec3(1, 1, 1);
	
	private PC_GresDisplayObject displayObject;
	
	private float time;
	
	public PC_GresWindowSideTab(String text){
		super(text);
		this.frame.x = 2;
		this.frame.y = 20;
		this.frame.width = 2;
		this.frame.height = 2;
	}
	
	public PC_GresWindowSideTab(String text, PC_GresDisplayObject displayObject) {
		this(text);
		setDisplayObject(displayObject);
	}

	public void setColor(PC_Vec3 color){
		this.color.setTo(color);
	}
	
	public void setDisplayObject(PC_GresDisplayObject displayObject){
		this.displayObject = displayObject;
	}
	
	public PC_GresDisplayObject getDisplayObject(){
		return this.displayObject;
	}
	
	@Override
	protected void setParent(PC_GresContainer parent) {
		if(parent instanceof PC_GresWindow){
			this.parent = parent;
			this.parentVisible = parent.isRecursiveVisible();
			this.parentEnabled = parent.isRecursiveEnabled();
		}else if (parent == null) {
			this.parent = null;
			this.parentVisible = true;
			this.parentEnabled = true;
		}
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return new PC_Vec2I(20, 20);
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(100, 100);
	}

	@Override
	protected PC_Vec2I calculatePrefSize() {
		return new PC_Vec2I(20, 20);
	}
	
	@Override
	public PC_Vec2 getRealLocation() {
		if (this.parent == null) {
			return this.rect.getLocationF();
		} 
		return this.rect.getLocationF().add(this.parent.getRealLocation());
	}

	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		GL11.glColor3d(this.color.x, this.color.y, this.color.z);
		drawTexture("Frame", -2, 0, this.rect.width+2, this.rect.height);
		GL11.glColor3f(1, 1, 1);
		if(this.displayObject!=null)
			this.displayObject.draw(2, 1, 16, 16);
		PC_Vec2 loc = getRealLocation();
		PC_Rect s = setDrawRect(scissor, new PC_Rect(loc.x+20, loc.y+2, this.rect.width-22, 16), scale, displayHeight, zoom);
		if(s!=null)
			drawString(this.text, 20, 2, 100, 16, H.LEFT, V.CENTER, false);
	}
	
	private boolean update=true;
	
	@Override
	protected void notifyChange() {
		if(this.update){
			updateMinSize();
			updatePrefSize();
			updateMaxSize();
		}
		notifyParentOfChange();
		if(this.update){
			this.rect.setSize(getPrefSize().max(new PC_Vec2I(fontRenderer.getStringSize(this.text).x+24, 20)));
			updateLayout();
		}
		this.rect.setSize(this.size);
	}

	@Override
	public PC_RectI getChildRect() {
		if(this.update){
			this.rect.setSize(getPrefSize().max(new PC_Vec2I(fontRenderer.getStringSize(this.text).x+24, 20)));
		}
		PC_RectI r = super.getChildRect();
		this.rect.setSize(this.size);
		return r;
	}

	@Override
	protected boolean handleMouseButtonClick(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {
		if(openSideTab == this){
			openSideTab = null;
		}else{
			openSideTab = this;
		}
		return true;
	}
	
	private static Object getTypeDisp(PC_RedstoneWorkType type){
		if(type==null)
			return PC_Gres.getGresTexture("I_OFF");
		switch(type){
		case ALWAYS:
			return PC_Gres.getGresTexture("I_ON");
		case ON_FLANK:
			return PC_Gres.getGresTexture("I_FL");
		case ON_HI_FLANK:
			return PC_Gres.getGresTexture("I_HFL");
		case ON_LOW_FLANK:
			return PC_Gres.getGresTexture("I_LFL");
		case ON_OFF:
			return PC_Gres.getGresTexture("Redstone_Torch_Off");
		case ON_ON:
			return Blocks.redstone_torch;
		default:
			return PC_Gres.getGresTexture("I_OFF");
		}
	}
	
	@Override
	protected void onDrawTick(float timeStamp) {
		this.time += timeStamp;
		int num = (int)(this.time/0.01);
		if(num>0){
			this.time -= num*0.01;
			if(openSideTab==this){
				this.size.setTo(this.size.add(num).min(getPrefSize().max(new PC_Vec2I(fontRenderer.getStringSize(this.text).x+24, 20))));
			}else{
				this.size.setTo(this.size.sub(num).max(20));
			}
			this.update=false;
			setSize(this.size);
			this.update=true;
		}
		super.onDrawTick(timeStamp);
	}

	public static PC_GresWindowSideTab createRedstoneSideTab(PC_TileEntity tileEntity){
		PC_GresWindowSideTab sideTab = new PC_GresWindowSideTab("Redstone", new PC_GresDisplayObject(Items.redstone));
		sideTab.setColor(new PC_Vec3(1.0, 0.2, 0.2));
		sideTab.setLayout(new PC_GresLayoutVertical());
		PC_RedstoneWorkType[] types = tileEntity.getAllowedRedstoneWorkTypes();
		if(types==null || types.length==0)
			return null;
		Object[] disps = new Object[types.length];
		int act = 0;
		for(int i=0; i<types.length; i++){
			disps[i] = getTypeDisp(types[i]);
			if(types[i]==tileEntity.getRedstoneWorkType()){
				act = i;
			}
		}
		PC_GresDisplayObject dO = new PC_GresDisplayObject(disps);
		PC_GresDisplay d = new PC_GresDisplay(dO);
		dO.setActiveDisplayObjectIndex(act);
		d.addEventListener(new RedstoneConfigEventListener(tileEntity, types));
		d.setBackground(new PC_GresDisplayObject(PC_Gres.getGresTexture("Slot")));
		d.setFrame(new PC_RectI(1, 1, 1, 1));
		sideTab.add(d);
		sideTab.add(new PC_GresLabel("State: ON"));
		return sideTab;
	}
	
	private static PC_Vec2I[] SIDE_POS = {new PC_Vec2I(18, 35), new PC_Vec2I(18, 1), new PC_Vec2I(18, 18), new PC_Vec2I(35, 35), new PC_Vec2I(35, 18), new PC_Vec2I(1, 18)};
	
	public static PC_GresWindowSideTab createIOConfigurationSideTab(PC_ISidedInventory inventory){
		PC_GresWindowSideTab sideTab = new PC_GresWindowSideTab("Configuration", new PC_GresDisplayObject(PC_Gres.getGresTexture("IO_CONF")));
		sideTab.setColor(new PC_Vec3(0.2, 1.0, 0.2));
		sideTab.setLayout(new PC_GresLayoutVertical());
		PC_GresFrame frame = new PC_GresFrame();
		frame.setMinSize(new PC_Vec2I(54, 54));
		sideTab.add(frame);
		Object[] obj = new Object[inventory.getGroupCount()+1];
		obj[0] = PC_Gres.getGresTexture("NULL");
		for(int i=1; i<obj.length; i++){
			obj[i] = PC_Gres.getGresTexture("F"+i);
		}
		PC_GresDisplay[] sides = new PC_GresDisplay[6];
		PC_GresDisplayObject dO;
		IOConfigEventListener eventListener = new IOConfigEventListener(inventory, sides);
		for(int i=0; i<6; i++){
			if(i==PC_Direction.NORTH.ordinal()){
				frame.add(sides[i] = new PC_GresDisplay(new PC_GresDisplayObject(inventory.getFrontIcon())));
			}else{
				frame.add(sides[i] = new PC_GresDisplay(dO = new PC_GresDisplayObject(obj)));
				dO.setActiveDisplayObjectIndex(inventory.getSideGroup(i)+1);
				sides[i].addEventListener(eventListener);
			}
			sides[i].setLocation(SIDE_POS[i]);
			sides[i].setSize(new PC_Vec2I(16, 16));
		}
		return sideTab;
	}
	
	public static PC_GresWindowSideTab createEnergySideTab(EnergyPerTick energy){
		PC_GresWindowSideTab sideTab = new PC_GresWindowSideTab("Energy", new PC_GresDisplayObject(PC_Gres.getGresTexture("Energy")));
		sideTab.setColor(new PC_Vec3(0.2, 0.2, 1.0));
		sideTab.setLayout(new PC_GresLayoutVertical());
		sideTab.add(energy.label = new PC_GresLabel("Energy: 0 E/T"));
		return sideTab;
	}
	
	public static class EnergyPerTick{
		
		PC_GresLabel label;
		
		public void setToValue(float value){
			if(this.label!=null){
				this.label.setText("Energy: "+new DecimalFormat("#.##").format(value)+" E/T");
			}
		}
		
	}
	
	private static class RedstoneConfigEventListener implements PC_IGresEventListener{
		
		private PC_TileEntity tileEntity;
		PC_RedstoneWorkType types[];
		
		public RedstoneConfigEventListener(PC_TileEntity tileEntity, PC_RedstoneWorkType types[]){
			this.tileEntity = tileEntity;
			this.types = types;
		}

		@Override
		public void onEvent(PC_GresEvent event) {
			if(event instanceof PC_GresMouseButtonEvent){
				PC_GresMouseButtonEvent bEvent = (PC_GresMouseButtonEvent) event;
				if(bEvent.getEvent()==Event.CLICK){
					PC_GresDisplay disp = (PC_GresDisplay) event.getComponent();
					PC_RedstoneWorkType rwt = this.types[disp.getDisplayObject().getActiveDisplayObjectIndex()];
					this.tileEntity.setRedstoneWorkType(rwt);
				}
			}
		}
		
	}
	
	private static class IOConfigEventListener implements PC_IGresEventListener{
		
		private PC_ISidedInventory inventory;
		private PC_GresDisplay[] sides;
		
		public IOConfigEventListener(PC_ISidedInventory inventory, PC_GresDisplay[] sides){
			this.inventory = inventory;
			this.sides = sides;
		}

		@Override
		public void onEvent(PC_GresEvent event) {
			if(event instanceof PC_GresMouseButtonEvent){
				PC_GresMouseButtonEvent bEvent = (PC_GresMouseButtonEvent) event;
				if(bEvent.getEvent()==Event.CLICK){
					for(int i=0; i<this.sides.length; i++){
						if(this.sides[i] == bEvent.getComponent()){
							this.inventory.setSideGroup(i, this.sides[i].getDisplayObject().getActiveDisplayObjectIndex()-1);
							break;
						}
					}
				}
			}
		}
		
	}
	
}
