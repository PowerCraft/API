package powercraft.api.beam;

import powercraft.api.PC_Vec3;


public interface PC_IBeam {
	
	public PC_Vec3 getDirection();
	
	public PC_Vec3 getPosition();
	
	public void setPosition(PC_Vec3 pos);
	
	public PC_Vec3 getColor();
	
	public PC_IBeam getNewBeam(PC_Vec3 startPos, PC_Vec3 newDirection, PC_Vec3 newColor);
	
}
