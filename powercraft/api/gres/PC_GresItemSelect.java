package powercraft.api.gres;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Rect;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresTextEdit.PC_GresInputType;
import powercraft.api.gres.dialog.PC_GresDialogBasic;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEventResult;
import powercraft.api.gres.events.PC_GresMouseEventResult;
import powercraft.api.gres.history.PC_GresHistory;


public class PC_GresItemSelect extends PC_GresComponent {
	
	private ItemStack itemStack;
	
	public PC_GresItemSelect(){
		
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return new PC_Vec2I(18, 18);
	}
	
	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(18, 18);
	}
	
	@Override
	protected PC_Vec2I calculatePrefSize() {
		return new PC_Vec2I(18, 18);
	}
	
	@Override
	protected void paint(PC_Rect scissor, double scale, int displayHeight, float timeStamp, float zoom) {
		if(this.itemStack==null){
			drawTexture("ItemSelectEmpty", 1, 1, 16, 16);
		}else{
			drawTexture("ItemSelectFull", 1, 1, 16, 16);
		}
		PC_GresRenderer.drawItemStackAllreadyLighting(1, 1, this.itemStack, null);
	}

	@Override
	protected boolean handleKeyTyped(char key, int keyCode, boolean repeat, PC_GresHistory history) {
		if(keyCode==Keyboard.KEY_DELETE){
			this.itemStack = null;
		}else if(keyCode==Keyboard.KEY_S){
			select();
		}else if(keyCode==Keyboard.KEY_E){
			edit();
		}
		return super.handleKeyTyped(key, keyCode, repeat, history);
	}

	@Override
	protected boolean handleMouseButtonClick(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {
		if(eventButton==0){
			select();
		}else if(eventButton==1){
			edit();
		}
		return super.handleMouseButtonClick(mouse, buttons, eventButton, history);
	}
	
	private void select(){
		SelectDialog selectDialog = new SelectDialog(this);
		addToBase(selectDialog);
		selectDialog.takeFocus();
	}
	
	private void edit(){
		if(this.itemStack!=null){
			boolean fuzzy = this.itemStack.stackTagCompound!=null && this.itemStack.stackTagCompound.getBoolean("isFuzzyCalc");
			EditDialog editDialog = new EditDialog(this, this.itemStack.stackSize, this.itemStack.getItemDamage(), fuzzy);
			addToBase(editDialog);
			editDialog.takeFocus();
		}
	}
	
	public void setEditData(int count, int damage, boolean fuzzy) {
		this.itemStack = new ItemStack(this.itemStack.getItem(), count, damage);
		if(fuzzy){
			if(this.itemStack.stackTagCompound == null){
				this.itemStack.stackTagCompound = new NBTTagCompound();
			}
			this.itemStack.stackTagCompound.setBoolean("isFuzzyCalc", true);
		}
	}
	
	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}
	
	private static class EditDialog extends PC_GresDialogBasic{
		
		private PC_GresItemSelect itemSelect;
		
		private PC_GresTextEdit count;
		
		private PC_GresCheckBox fuzzy;
		
		private PC_GresTextEdit damage;
		
		public EditDialog(PC_GresItemSelect itemSelect, int stackSize, int d, boolean fuzzy) {
			super("Edit Item", "Apply", new Object[]{Integer.valueOf(stackSize), Integer.valueOf(d), Boolean.valueOf(fuzzy)});
			this.itemSelect = itemSelect;
		}

		@Override
		protected void init(PC_GresWindow window, Object[] data) {
			window.add((this.count = new PC_GresTextEdit(""+data[0], 5, PC_GresInputType.INT)).addEventListener(this));
			window.add((this.fuzzy = new PC_GresCheckBox("fuzzy")).addEventListener(this));
			this.fuzzy.check(data[2]==Boolean.TRUE);
			window.add((this.damage = new PC_GresTextEdit(""+data[1], 5, PC_GresInputType.INT)).addEventListener(this));
			this.damage.setEnabled(!this.fuzzy.isChecked());
		}

		@Override
		protected void doButtonClicked() {
			this.itemSelect.setEditData(parse(this.count.getText()), parse(this.damage.getText()), this.fuzzy.isChecked());
		}
		
		private static int parse(String text){
			try{
				return Integer.parseInt(text);
			}catch(NumberFormatException e){
				return 0;
			}
		}
		
		@Override
		public void onEvent(PC_GresEvent event) {
			if(event instanceof PC_GresMouseEventResult){
				PC_GresMouseEventResult mer = (PC_GresMouseEventResult)event;
				if(mer.getComponent()==this.fuzzy){
					this.damage.setEnabled(!this.fuzzy.isChecked());
				}
			}
			super.onEvent(event);
		}
		
	}
	
	private static class SelectDialog extends PC_GresDialogBasic{

		private PC_GresItemSelect itemSelect;
		
		private PC_GresTextEdit textEdit;
		
		private PC_GresInventory inventory;
		
		private Search search;
		
		List<ItemStack> list;
		
		public SelectDialog(PC_GresItemSelect itemSelect) {
			super("Item Select", null, null);
			this.itemSelect = itemSelect;
		}

		@Override
		protected void init(PC_GresWindow window, Object[] data) {
			window.add((this.textEdit = new PC_GresTextEdit("", 10)).addEventListener(this));
			window.add((this.inventory = new PC_GresInventory(9, 6)).addEventListener(this));
			int index = 0;
			for(int i=0; i<6; i++){
				for(int j=0; j<9; j++){
					this.inventory.setSlot(j, i, new S(index++));
				}
			}
			this.list = new ArrayList<ItemStack>();
			searchItems();
		}

		private void searchItems(){
			if(this.search!=null){
				this.search.doStop();
			}
			this.list.clear();
			this.search = new Search(this.textEdit.getText());
		}
		
		@Override
		protected void doButtonClicked() {
			//
		}
		
		void addItemStack(ItemStack is){
			this.list.add(is);
		}
		
		@Override
		public void onEvent(PC_GresEvent event) {
			if(event instanceof PC_GresMouseEventResult){
				PC_GresMouseEventResult mer = (PC_GresMouseEventResult)event;
				if(mer.getComponent()==this.inventory){
					Slot slot = this.inventory.getSlotAtPosition(mer.getMouse());
					if(slot!=null){
						ItemStack is = slot.getStack();
						if(is!=null){
							is = is.copy();
							is.stackSize = is.getMaxStackSize();
							this.itemSelect.setItemStack(is);
							close();
						}
					}
				}
			}else if(event instanceof PC_GresKeyEventResult){
				if(event.getComponent()==this.textEdit){
					searchItems();
				}
			}
			super.onEvent(event);
		}
		
		private class S extends Slot{

			public S(int index) {
				super(null, index, 0, 0);
			}

			@Override
			public ItemStack getStack() {
				return SelectDialog.this.list.size()>getSlotIndex()?SelectDialog.this.list.get(getSlotIndex()):null;
			}
			
		}
		
		private class Search extends Thread{

			private Object sync = new Object();
			
			private boolean stop;
			
			private String s;
			
			private boolean isRunning;
			
			public Search(String search) {
				setDaemon(true);
				this.s = search.toLowerCase();
				this.isRunning = true;
				start();
			}

			@Override
			@SuppressWarnings("unchecked")
			public void run(){
				Iterator<Item> iterator = Item.itemRegistry.iterator();

				List<ItemStack> l = new ArrayList<ItemStack>();
				
		        while (iterator.hasNext()){
		            Item item = iterator.next();

		            if (item != null){
		            	item.getSubItems(item, null, l);
		            }
		            if(this.stop)
		        		break;
		        }
		        
		        Minecraft mc = PC_ClientUtils.mc();
		        if(!this.stop){
			        for(ItemStack itemStack:l){
			        	List<String> tooltips = itemStack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
			        	for(String tooltip:tooltips){
			        		if(tooltip.toLowerCase().contains(this.s)){
			        			addItemStack(itemStack);
			        			break;
			        		}
			        		if(this.stop)
				        		break;
			        	}
			        	if(this.stop)
			        		break;
			        }
		        }
		        
		        synchronized(this.sync){
		        	this.isRunning = false;
		        	this.sync.notify();
		        }
		        
			}
			
			public void doStop() {
				synchronized(this.sync){
					this.stop = true;
					if(this.isRunning){
						try {
							this.sync.wait();
						} catch (InterruptedException e) {/**/}
					}
				}
			}
			
		}
		
	}
	
}
