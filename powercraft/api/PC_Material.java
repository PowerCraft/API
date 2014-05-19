package powercraft.api;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;


public final class PC_Material extends Material {
	
	public static final Material MACHINES = new PC_Material(MapColor.ironColor).setImmovableMobility();

	private boolean liquid;
	private boolean solid = true;
	private boolean canBlockGrass = true;
	private boolean blocksMovement = true;
	private boolean translucent;	
	
	public PC_Material(MapColor color){
		super(color);
	}
	
	@Override
	public boolean isLiquid() {
		return this.liquid;
	}
	
	protected PC_Material setLiquid(){
		this.liquid = true;
		return this;
	}

	@Override
	public boolean isSolid() {
		return this.solid;
	}

	protected PC_Material setNotSolid(){
		this.solid = false;
		return this;
	}
	
	@Override
	public boolean getCanBlockGrass() {
		return this.canBlockGrass;
	}
	
	protected PC_Material setNotBlockGrass(){
		this.canBlockGrass = false;
		return this;
	}

	@Override
	public boolean blocksMovement() {
		return this.blocksMovement;
	}
	
	protected PC_Material setNotBlocksMovement(){
		this.blocksMovement = false;
		return this;
	}

	@Override
	public boolean isOpaque(){
        return this.translucent ? false : this.blocksMovement();
    }
	
	protected PC_Material setTranslucent() {
        this.translucent = true;
        return this;
    }
	
	@Override
	protected Material setRequiresTool(){
		super.setRequiresTool();
        return this;
    }

    @Override
	protected PC_Material setBurning(){
        super.setBurning();
        return this;
    }

    @Override
	public PC_Material setReplaceable(){
        super.setReplaceable();
        return this;
    }

    @Override
	protected PC_Material setNoPushMobility(){
        super.setNoPushMobility();
        return this;
    }

    @Override
	protected PC_Material setImmovableMobility(){
    	super.setImmovableMobility();
        return this;
    }

    @Override
	protected PC_Material setAdventureModeExempt(){
        super.setAdventureModeExempt();
        return this;
    }
	
}
