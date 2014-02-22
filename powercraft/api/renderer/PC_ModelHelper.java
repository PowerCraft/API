package powercraft.api.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Vec3;

public class PC_ModelHelper {

	public static void drawBlockAsUsual(Block block, Tessellator tessellator, int meta) {
		for (PC_Direction side : PC_Direction.VALID_DIRECTIONS)
			renderBlockSide(side, tessellator, block.getIcon(side.ordinal(), meta));
	}

	public static void renderBlockSides(Tessellator tessellator, IIcon icon, PC_Direction... sides) {
		for (PC_Direction side : sides)
			renderBlockSide(side, tessellator, icon);
		// I am not able to type Tesselator correct...
	}

	public static void renderBlockSide(PC_Direction side, Tessellator tessellator, IIcon icon) {
		int x1 = 0;
		int y1 = 0;
		int z1 = 0;
		int x2 = 1;
		int y2 = 1;
		int z2 = 1;
		float u1 = icon.getMinU();
		float v1 = icon.getMinV();
		float u2 = icon.getMaxU();
		float v2 = icon.getMaxV();
		switch (side) {
		case DOWN:
			tessellator.addVertexWithUV(x1, y1, z2, u1, v1);
			tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
			tessellator.addVertexWithUV(x2, y1, z1, u2, v2);
			tessellator.addVertexWithUV(x2, y1, z2, u2, v1);
			break;
		case EAST:
			tessellator.addVertexWithUV(x1, y1, z2, u1, v1);
			tessellator.addVertexWithUV(x1, y2, z2, u1, v2);
			tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
			tessellator.addVertexWithUV(x1, y1, z1, u2, v1);
			break;
		case NORTH:
			tessellator.addVertexWithUV(x1, y1, z1, u1, v1);
			tessellator.addVertexWithUV(x1, y2, z1, u1, v2);
			tessellator.addVertexWithUV(x2, y2, z1, u2, v2);
			tessellator.addVertexWithUV(x2, y1, z1, u2, v1);
			break;
		case SOUTH:
			tessellator.addVertexWithUV(x2, y1, z2, u2, v1);
			tessellator.addVertexWithUV(x2, y2, z2, u2, v2);
			tessellator.addVertexWithUV(x1, y2, z2, u1, v2);
			tessellator.addVertexWithUV(x1, y1, z2, u1, v1);
			break;
		case UP:
			tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
			tessellator.addVertexWithUV(x1, y2, z2, u1, v2);
			tessellator.addVertexWithUV(x2, y2, z2, u2, v2);
			tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
			break;
		case WEST:
			tessellator.addVertexWithUV(x2, y1, z1, u2, v1);
			tessellator.addVertexWithUV(x2, y2, z1, u2, v2);
			tessellator.addVertexWithUV(x2, y2, z2, u1, v2);
			tessellator.addVertexWithUV(x2, y1, z2, u1, v1);
			break;
		default:
			break;
		}
	}

	public static void drawBox(PC_Vec3 bottomLeftFront, PC_Vec3 topRightBack,
			Tessellator tessellator, IIcon icon) {
		float x1 = (float) bottomLeftFront.x;
		float y1 = (float) bottomLeftFront.y;
		float z1 = (float) bottomLeftFront.z;
		float x2 = (float) topRightBack.x;
		float y2 = (float) topRightBack.y;
		float z2 = (float) topRightBack.z;
		float u1 = icon.getMinU();
		float v1 = icon.getMinV();
		float u2 = icon.getMaxU();
		float v2 = icon.getMaxV();
		// front
		tessellator.addVertexWithUV(x1, y1, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y2, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v1);
		// left
		tessellator.addVertexWithUV(x1, y1, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y2, z2, u1, v2);
		tessellator.addVertexWithUV(x1, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x1, y1, z1, u2, v1);
		// back
		tessellator.addVertexWithUV(x2, y1, z2, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v2);
		tessellator.addVertexWithUV(x1, y2, z2, u1, v2);
		tessellator.addVertexWithUV(x1, y1, z2, u1, v1);
		// right
		tessellator.addVertexWithUV(x2, y1, z1, u2, v1);
		tessellator.addVertexWithUV(x2, y2, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u1, v1);
		// up
		tessellator.addVertexWithUV(x1, y2, z1, u1, v1);
		tessellator.addVertexWithUV(x1, y2, z2, u1, v2);
		tessellator.addVertexWithUV(x2, y2, z2, u2, v2);
		tessellator.addVertexWithUV(x2, y2, z1, u2, v1);
		// down
		tessellator.addVertexWithUV(x1, y1, z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u1, v2);
		tessellator.addVertexWithUV(x2, y1, z1, u2, v2);
		tessellator.addVertexWithUV(x2, y1, z2, u2, v1);
	}
}
