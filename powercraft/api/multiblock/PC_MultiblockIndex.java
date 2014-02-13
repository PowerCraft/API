package powercraft.api.multiblock;


import powercraft.api.PC_Direction;


public enum PC_MultiblockIndex {

	CENTER(PC_MultiblockType.CENTER), FACENORTH(PC_MultiblockType.FACE), FACEEAST(PC_MultiblockType.FACE), FACESOUTH(PC_MultiblockType.FACE), FACEWEST(
			PC_MultiblockType.FACE), FACETOP(PC_MultiblockType.FACE), FACEBOTTOM(PC_MultiblockType.FACE), CORNERTOPNORTHEAST(PC_MultiblockType.CORNER), CORNERTOPSOUTHEAST(
			PC_MultiblockType.CORNER), CORNERTOPSOUTHWEST(PC_MultiblockType.CORNER), CORNERTOPNORTHWEST(PC_MultiblockType.CORNER), CORNERBOTTOMNORTHEAST(
			PC_MultiblockType.CORNER), CORNERBOTTOMSOUTHEAST(PC_MultiblockType.CORNER), CORNERBOTTOMSOUTHWEST(PC_MultiblockType.CORNER), CORNERBOTTOMNORTHWEST(
			PC_MultiblockType.CORNER), EDGETOPNORTH(PC_MultiblockType.CORNER), EDGETOPEAST(PC_MultiblockType.CORNER), EDGETOPSOUTH(
			PC_MultiblockType.CORNER), EDGETOPWEST(PC_MultiblockType.CORNER), EDGEBOTTOMNORTH(PC_MultiblockType.CORNER), EDGEBOTTOMEAST(
			PC_MultiblockType.CORNER), EDGEBOTTOMSOUTH(PC_MultiblockType.CORNER), EDGEBOTTOMWEST(PC_MultiblockType.CORNER), EDGENORTHEAST(
			PC_MultiblockType.CORNER), EDGESOUTHEAST(PC_MultiblockType.CORNER), EDGESOUTHWEST(PC_MultiblockType.CORNER), EDGENORTHWEST(
			PC_MultiblockType.CORNER);

	private static final PC_Direction FACEDIRS[] = { PC_Direction.NORTH, PC_Direction.EAST, PC_Direction.SOUTH, PC_Direction.WEST, PC_Direction.UP,
			PC_Direction.DOWN };
	public static final PC_MultiblockIndex FACEINDEXFORDIR[] = { PC_MultiblockIndex.FACEBOTTOM, PC_MultiblockIndex.FACETOP,
			PC_MultiblockIndex.FACENORTH, PC_MultiblockIndex.FACESOUTH, PC_MultiblockIndex.FACEWEST, PC_MultiblockIndex.FACEEAST };

	public final PC_MultiblockType type;


	PC_MultiblockIndex(PC_MultiblockType type) {

		this.type = type;
	}


	public static PC_Direction getFaceDir(PC_MultiblockIndex index) {

		if (index.type == PC_MultiblockType.FACE) {
			return FACEDIRS[index.ordinal() - 1];
		}
		return PC_Direction.UNKNOWN;
	}

	public static PC_MultiblockIndex getFromDir(PC_Direction dir) {
		switch(dir){
		case DOWN:
			return FACEBOTTOM;
		case EAST:
			return FACEEAST;
		case NORTH:
			return FACENORTH;
		case SOUTH:
			return FACESOUTH;
		case UP:
			return FACETOP;
		case WEST:
			return FACEWEST;
		case UNKNOWN:
		default:
			return null;
		}
	}
	
}
