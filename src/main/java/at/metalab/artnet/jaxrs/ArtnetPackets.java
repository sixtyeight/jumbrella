package at.metalab.artnet.jaxrs;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import artnet4j.ArtNet;
import artnet4j.ArtNetException;
import artnet4j.packets.ArtDmxPacket;

public class ArtnetPackets {

	private static ArtNet artNet = null;

	private static synchronized ArtNet getArtNet() throws SocketException, ArtNetException {
		if (artNet == null) {
			ArtNet a = new ArtNet();
			a.start();

			artNet = a;
		}
		return artNet;
	}

	public static void sendMainRoom(int r, int g, int b, int a, int w) throws SocketException, ArtNetException {
		List<Byte> umbrella = new ArrayList<>();
		umbrella.add((byte) r); // r
		umbrella.add((byte) g); // g
		umbrella.add((byte) b); // b
		umbrella.add((byte) a); // a
		umbrella.add((byte) w); // w

		List<Byte> allUmbrellas = new ArrayList<>();
		final int numUmbrellas = 12;
		for (int i = 0; i < numUmbrellas; i++) {
			allUmbrellas.addAll(umbrella);
		}

		byte[] payload = new byte[allUmbrellas.size()];
		{
			int i = 0;
			for (Byte data : allUmbrellas) {
				payload[i] = data;
				i++;
			}
		}

		final int artnetUniverse = 3;
		final int artnetSubnet = 0;
		final String ip = "10.20.255.255"; // n.b. broadcast
		final int resends = 20;

		// prepare the ArtDmxPacket which will be sent in the loop below
		ArtDmxPacket colorPacket = buildPacket(artnetSubnet, artnetUniverse, payload, payload.length);

		System.out.println("Sending color1 -> " + artnetSubnet + ":" + artnetUniverse + " (dmxDataLength="
				+ colorPacket.getDmxData().length + ")");

		// send multiple times due to possible packet loss
		ArtNet s = getArtNet();
		for (int i = 0; i < resends; i++) {
			s.unicastPacket(colorPacket, ip);
		}
	}

	private static ArtDmxPacket buildPacket(int artnetSubnet, int artnetUniverse, byte[] data, int numChannels) {
		ArtDmxPacket artDmxPacket = new ArtDmxPacket();
		artDmxPacket.setUniverse(artnetSubnet, artnetUniverse);
		artDmxPacket.setSequenceID(0);
		artDmxPacket.setDMX(data, numChannels);

		return artDmxPacket;
	}

	public static void main(String[] args) throws Exception {
		// sendMainRoom(0, 255, 255, 0, 0);
		sendMainRoom(0, 0, 0, 100, 100);
	}

}
