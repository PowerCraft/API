package powercraft.api;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fluids.IFluidBlock;
import powercraft.api.block.PC_AbstractBlockBase;

/**
 * Temperature handling for blocks
 * @author XOR
 *
 */
public final class PC_BlockTemperatures {

	/**
	 * Temperature of 0°C in Kelvin
	 */
	public static final int CELCIUS0_TEMPERATURE = 273;
	/**
	 * Temperature of 24°C in Kelvin
	 */
	public static final int DEFAULT_TEMPERATURE = 24+CELCIUS0_TEMPERATURE;
	/**
	 * Some Minecraft block temperatures
	 */
	private static HashMap<Block, Integer> blockTemperatures = new HashMap<Block, Integer>();
	
	/**
	 * Get the temperature of an block
	 * @param world the world
	 * @param x x-coord
	 * @param y y-coord
	 * @param z z-coord
	 * @return the temperature in Kelvin
	 */
	public static int getTemperature(World world, int x, int y, int z){
		int temperature = getTemperatureForBiomeAndHeight2(world, x, y, z);
		int blockTemperature = getTemperature2(world, x, y, z);
		return blockTemperature+temperature-DEFAULT_TEMPERATURE;
	}
	
	/**
	 * Get the temperature of an block not connected to the hight-temperature
	 * @param world the world
	 * @param x x-coord
	 * @param y y-coord
	 * @param z z-coord
	 * @return the temperature in Kelvin
	 */
	public static int getTemperature2(World world, int x, int y, int z){
		Block block = PC_Utils.getBlock(world, x, y, z);
		if(block instanceof PC_AbstractBlockBase){
    		return ((PC_AbstractBlockBase)block).getTemperature(world, x, y, z);
    	}else if(block instanceof IFluidBlock){
    		return ((IFluidBlock)block).getFluid().getTemperature(world, x, y, z);
    	}
    	return getTemperatureFallback(block);
	}
	
	/**
	 * Get the temperature of an block
	 * @param block the block
	 * @return the temperature in Kelvin
	 */
	public static int getTemperature(Block block) {
		if(block instanceof PC_AbstractBlockBase){
    		return ((PC_AbstractBlockBase)block).getTemperature();
    	}else if(block instanceof IFluidBlock){
    		return ((IFluidBlock)block).getFluid().getTemperature();
    	}
		return getTemperatureFallback(block);
	}
	
	/**
	 * Temperature of Minecraft blocks
	 * @param block the Minecraft block
	 * @return the temperature in Kelvin
	 */
	public static int getTemperatureFallback(Block block){
		Integer temperature = blockTemperatures.get(block);
		return temperature==null?DEFAULT_TEMPERATURE:temperature.intValue();
	}
	
	/**
	 * Get the temperature for a specific height and biome
	 * @param world the world
	 * @param x x-coord
	 * @param y y-coord
	 * @param z z-coord
	 * @return the temperature in Kelvin
	 */
	public static int getTemperatureForBiomeAndHeight(World world, int x, int y, int z){
		BiomeGenBase biome = PC_Utils.getBiome(world, x, z);
		return (int) (biome.getFloatTemperature(x, y, z)-0.1)*35+CELCIUS0_TEMPERATURE;
	}
	
	/**
	 * Get the temperature for a specific height and biome and weather
	 * @param world the world
	 * @param x x-coord
	 * @param y y-coord
	 * @param z z-coord
	 * @return the temperature in Kelvin
	 */
	public static int getTemperatureForBiomeAndHeight2(World world, int x, int y, int z){
		BiomeGenBase biome = PC_Utils.getBiome(world, x, z);
		int temperature = (int) ((biome.getFloatTemperature(x, y, z)-0.2)*32);
		if(world.isRaining() && (biome.getEnableSnow() || biome.canSpawnLightningBolt())){
			temperature -= biome.rainfall*10;
		}
		int height = world.getHeightValue(x, z);
		if(height<y+10 && height>=y){
			temperature = (int) (temperature*(1-(height-y)/10.0f));
		}
		if(y<40){
			temperature += 40-y;
		}
		return temperature+CELCIUS0_TEMPERATURE;
	}
	
	/**
	 * Set a temperature for a Minecraft block
	 * @param block the block which should have a temperature
	 * @param temperature the temperature for the block
	 */
	public static void setTemperatureFor(Block block, int temperature){
		if(block instanceof PC_AbstractBlockBase || block instanceof IFluidBlock){
			PC_Logger.warning("PowerCraft Blocks or IFluidBlock's have a funktion for temperature");
		}else{
			blockTemperatures.put(block, Integer.valueOf(temperature));
		}
	}
	
	/**
	 * convert °C to Kelvin
	 * @param temperature temperature in °C
	 * @return temperature in Kelvin
	 */
	public static int celciusToKelvin(int temperature){
		return temperature+CELCIUS0_TEMPERATURE;
	}
	
	/**
	 * convert Kelvin to °C
	 * @param temperature temperature in Kelvin
	 * @return temperature in °C
	 */
	public static int kelvinToCelcius(int temperature){
		return temperature-CELCIUS0_TEMPERATURE;
	}
	
	static{
		setTemperatureFor(Blocks.lava, 1000+CELCIUS0_TEMPERATURE);
		setTemperatureFor(Blocks.lit_furnace, 100+CELCIUS0_TEMPERATURE);
		setTemperatureFor(Blocks.torch, 50+CELCIUS0_TEMPERATURE);
		setTemperatureFor(Blocks.ice, -10+CELCIUS0_TEMPERATURE);
		setTemperatureFor(Blocks.packed_ice, -30+CELCIUS0_TEMPERATURE);
		setTemperatureFor(Blocks.snow, -10+CELCIUS0_TEMPERATURE);
		setTemperatureFor(Blocks.snow_layer, -10+CELCIUS0_TEMPERATURE);
		setTemperatureFor(Blocks.fire, 100+CELCIUS0_TEMPERATURE);
		setTemperatureFor(Blocks.lit_redstone_lamp, 50+CELCIUS0_TEMPERATURE);
		setTemperatureFor(Blocks.lit_pumpkin, 40+CELCIUS0_TEMPERATURE);
		setTemperatureFor(Blocks.redstone_torch, 40+CELCIUS0_TEMPERATURE);
	}
	
	private PC_BlockTemperatures(){
		PC_Utils.staticClassConstructor();
	}
	
}
