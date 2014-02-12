package powercraft.api.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import powercraft.api.PC_Api;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Side;
import powercraft.api.PC_Utils;
import powercraft.api.reflect.PC_Security;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class PC_PacketHandler extends SimpleChannelInboundHandler<PC_Packet> {

	private static boolean done;
	private static EnumMap<Side, FMLEmbeddedChannel> channels;
	private static List<Class<? extends PC_Packet>> packetList = new ArrayList<Class<? extends PC_Packet>>();
	@SuppressWarnings("unchecked")
	private static Class<? extends PC_Packet>[] packets = new Class[]{PC_PacketPacketResolve.class};
	private static HashMap<Class<? extends PC_Packet>, Integer> packetID = new HashMap<Class<? extends PC_Packet>, Integer>();

	static{
		packetID.put(PC_PacketPacketResolve.class, 0);
	}
	
	public static void register(){
		PC_Security.allowedCaller("PC_PacketHandler.register()", PC_Api.class);
		if(channels==null){
			channels = NetworkRegistry.INSTANCE.newChannel("PowerCraft", new Indexer());
			FMLEmbeddedChannel channel = channels.get(Side.CLIENT);
			channel.pipeline().addAfter(channel.findChannelHandlerNameForType(Indexer.class), PC_PacketHandler.class.getName(), new PC_PacketHandler(PC_Side.CLIENT));
			channel = channels.get(Side.SERVER);
			channel.pipeline().addAfter(channel.findChannelHandlerNameForType(Indexer.class), PC_PacketHandler.class.getName(), new PC_PacketHandler(PC_Side.SERVER));
			FMLCommonHandler.instance().bus().register(new Listener());
		}
	}
	
	public static class Listener{
		
		private Listener(){
			
		}
		
		@SubscribeEvent
		public void clientConnected(ServerConnectionFromClientEvent event){
			sendPacketResolveTo(((NetHandlerPlayServer)event.handler).playerEntity);
		}
		
	}
	
	@Sharable
	public static class Indexer extends MessageToMessageCodec<FMLProxyPacket, PC_Packet>{

		private Indexer(){
			
		}
		
		@Override
		protected void encode(ChannelHandlerContext ctx, PC_Packet msg, List<Object> out) throws Exception {
			 ByteBuf buffer = Unpooled.buffer();
			 writePacketToByteBuf(msg, buffer);
			 out.add(new FMLProxyPacket(buffer, "PowerCraft"));
		}

		@Override
		protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws Exception {
			out.add(getPacketFromByteBuf(msg.payload()));
		}
		
	}
	
	public static Packet getPacketFrom(PC_Packet packet){
        return channels.get(Side.SERVER).generatePacketFrom(packet);
    }
	
	public static void registerPacket(Class<? extends PC_Packet> packet){
		if(done){
			PC_Logger.severe("A packet want to register while startup is done");
		}else{
			packetList.add(packet);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void setupPackets(){
		PC_Security.allowedCaller("PC_PacketHandler.setupPackets()", PC_Api.class);
		done = true;
		packets = new Class[packetList.size()+1];
		packetID.clear();
		packets[0] = PC_PacketPacketResolve.class;
		packetID.put(PC_PacketPacketResolve.class, 0);
		for(int i=1; i<packets.length; i++){
			packets[i] = packetList.get(i-1);
			packetID.put(packets[i], i);
		}
	}
	
	private static void sendPacketResolveTo(EntityPlayerMP player){
		String[] packetClasses = new String[packets.length-1];
		for(int i=0; i<packetClasses.length; i++){
			packetClasses[i] = packets[i+1].getClass().getName();
		}
		sendTo(new PC_PacketPacketResolve(packetClasses), player);
	}

	static PC_Packet getPacketFromByteBuf(ByteBuf buf) {
		int id = buf.readInt();
		try {
			PC_Packet packet = packets[id].newInstance();
			packet.fromByteBuffer(buf);
			return packet;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	static void writePacketToByteBuf(PC_Packet packet, ByteBuf buf) {
		int id = packetID.get(packet.getClass());
		buf.writeInt(id);
		packet.toByteBuffer(buf);
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	static void setPackets(String[] packetClasses) {
		if(PC_Utils.mcs()!=null)
			return;
		packets = new Class[packetClasses.length+1];
		packetID.clear();
		packets[0] = PC_PacketPacketResolve.class;
		packetID.put(PC_PacketPacketResolve.class, 0);
		for(int i=1; i<packets.length; i++){
			try {
				packets[i] = (Class<? extends PC_Packet>) Class.forName(packetClasses[i-1]);
				packetID.put(packets[i], i);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void sendToAll(PC_Packet packet){
		checkServer(packet);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}

	public static void sendTo(PC_Packet packet, EntityPlayerMP player){
		checkServer(packet);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}

	public static void sendToAllAround(PC_Packet packet, int dimension, double x, double y, double z, double range){
		checkServer(packet);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(new TargetPoint(dimension, x, y, z, range));
        channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}

	public static void sendToDimension(PC_Packet packet, int dimension){
		checkServer(packet);
		 channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
	        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimension);
	        channels.get(Side.SERVER).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}
	
	public static void sendToServer(PC_Packet packet){
		checkClient(packet);
		channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
	    channels.get(Side.CLIENT).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}
	
	private static void checkServer(PC_Packet packet){
		if(!PC_Utils.isServer()){
			PC_Logger.severe("A server send not invoked form server");
		}else if(packet instanceof PC_PacketClientToServer){
			PC_Logger.severe("A server trys to send a client to server packet");
		}
	}
	
	private static void checkClient(PC_Packet packet){
		if(!PC_Utils.isClient()){
			PC_Logger.severe("A client send not invoked form client");
		}else if(packet instanceof PC_PacketServerToClient){
			PC_Logger.severe("A client trys to send a server to client packet");
		}
	}
	
	public PC_Side side;
	
	private PC_PacketHandler(PC_Side side){
		this.side = side;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PC_Packet msg) throws Exception {
		INetHandler iNetHandler = ctx.attr(NetworkRegistry.NET_HANDLER).get();
		PC_Packet result = msg.doAndReply(side, iNetHandler);
        if (result != null)
        {
            ctx.writeAndFlush(result).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
	}
	
}
