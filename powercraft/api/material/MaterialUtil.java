package powercraft.api.material;

/**
 * @author James
 * Powercraft material
 */
public class MaterialUtil {
	
	/**
	 * Gets the material from a string
	 * @param name The name of the material (string)
	 * @return The material gotten from the name
	 */
	public static net.minecraft.block.material.Material getMaterialFromName(String name){
		switch(name){
		case "air":
			return net.minecraft.block.material.Material.air;
		case "anvil":
			return net.minecraft.block.material.Material.anvil;
		case "cactus":
			return net.minecraft.block.material.Material.cactus;
		case "cake":
			return net.minecraft.block.material.Material.cake;
		case "circuts":
			return net.minecraft.block.material.Material.circuits;
		case "clay":
			return net.minecraft.block.material.Material.clay;
		case "cloth":
			return net.minecraft.block.material.Material.cloth;
		case "coral":
			return net.minecraft.block.material.Material.coral;
		case "snow":
			return net.minecraft.block.material.Material.craftedSnow;
		case "dragon egg":
			return net.minecraft.block.material.Material.dragonEgg;
		case "fire":
			return net.minecraft.block.material.Material.fire;
		case "glass":
			return net.minecraft.block.material.Material.glass;
		case "grass":
			return net.minecraft.block.material.Material.grass;
		case "dirt":
			return net.minecraft.block.material.Material.ground;
		case "ice":
			return net.minecraft.block.material.Material.ice;
		case "iron":
			return net.minecraft.block.material.Material.iron;
		case "lava":
			return net.minecraft.block.material.Material.lava;
		case "leaves":
			return net.minecraft.block.material.Material.leaves;
		case "carpet":
			return net.minecraft.block.material.Material.materialCarpet;
		case "piston":
			return net.minecraft.block.material.Material.piston;
		case "plants":
			return net.minecraft.block.material.Material.plants;
		case "portal":
			return net.minecraft.block.material.Material.portal;
		case "pumpkin":
			return net.minecraft.block.material.Material.pumpkin;
		case "redstone":
			return net.minecraft.block.material.Material.redstoneLight;
		case "rock":
			return net.minecraft.block.material.Material.rock;
		case "sand":
			return net.minecraft.block.material.Material.sand;
		case "fallen snow":
			return net.minecraft.block.material.Material.snow;
		case "sponge":
			return net.minecraft.block.material.Material.sponge;
		case "tnt":
			return net.minecraft.block.material.Material.tnt;
		case "vine":
			return net.minecraft.block.material.Material.vine;
		case "water":
			return net.minecraft.block.material.Material.water;
		case "web":
			return net.minecraft.block.material.Material.web;
		case "wood":
			return net.minecraft.block.material.Material.wood;
		default:
			return null;
		}
	}
}
