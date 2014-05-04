package powercraft.api.village;

import java.util.List;
import java.util.Random;

import powercraft.api.reflect.PC_Reflection;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;


public class PC_Building implements IVillageCreationHandler {

	protected Class<?> pieceClass;
	
	protected int pieceWeight;
	
	protected int pieceLimit;
	
	public PC_Building(Class<?> pieceClass, int pieceWeight, int pieceLimit){
		PC_Buildings.addBuilding(this);
		this.pieceClass = pieceClass;
		this.pieceWeight = pieceWeight;
		this.pieceLimit = pieceLimit;
	}
	
	@SuppressWarnings("unused")
	public int getPieceWeight(Random random, int terrainType){
		return this.pieceWeight;
	}
	
	@SuppressWarnings("unused")
	public int getPieceLimit(Random random, int terrainType){
		return this.pieceLimit;
	}
	
	@Override
	public final PieceWeight getVillagePieceWeight(Random random, int terrainType) {
		return new PieceWeight(this.pieceClass, getPieceWeight(random, terrainType), getPieceLimit(random, terrainType));
	}

	@Override
	public final Class<?> getComponentClass() {
		return this.pieceClass;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object buildComponent(PieceWeight villagePiece, Start startPiece, List pieces, Random random, int startX, int startY, int startZ, int dir, int componentType) {
		return PC_Reflection.newInstance(this.pieceClass);
	}
	
	public void construct() {
		VillagerRegistry.instance().registerVillageCreationHandler(this);
	}
	
}
