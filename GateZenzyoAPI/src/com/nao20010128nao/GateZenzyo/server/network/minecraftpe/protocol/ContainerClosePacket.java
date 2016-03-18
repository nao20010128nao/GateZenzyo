package com.nao20010128nao.GateZenzyo.server.network.minecraftpe.protocol;

/**
 * author: MagicDroidX Nukkit Project
 */
public class ContainerClosePacket extends DataPacket {
	public static final byte NETWORK_ID = ProtocolInfo.CONTAINER_CLOSE_PACKET;

	@Override
	public byte pid() {
		return NETWORK_ID;
	}

	public int windowid;

	@Override
	public void decode() {
		this.windowid = this.getByte();
	}

	@Override
	public void encode() {
		this.reset();
		this.putByte((byte) this.windowid);
	}
}