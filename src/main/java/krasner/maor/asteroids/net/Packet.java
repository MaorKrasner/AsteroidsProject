package krasner.maor.asteroids.net;

public abstract class Packet {
    public static enum PacketTypes {
        INVALID(-1), LOGIN(00), DISCONNECT(01);

        private int packetId;

        private PacketTypes(int packetId) {
            this.packetId = packetId;
        }

        public int getPacketId() {
            return packetId;
        }
    }

    public byte packetID;

    public Packet (int packetId) {
        this.packetID = (byte) packetId;
    }

    public abstract void writeData(GameClient client);
    public abstract void writeData(GameServer server);

    public abstract byte[] getData();

    public String readData(byte[] data) {
        String message = new String(data).trim();
        return message.substring(2); // message begins after the number of packet type
    }

    public static PacketTypes lookupPacket(int id) {
        for (PacketTypes p : PacketTypes.values()) {
            if (p.getPacketId() == id)
                return p;
        }
        return PacketTypes.INVALID;
    }
}
