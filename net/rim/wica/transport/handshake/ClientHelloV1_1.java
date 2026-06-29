package net.rim.wica.transport.handshake;

public class ClientHelloV1_1 implements ClientHelloV1 {
   private int _deviceVersion;

   public ClientHelloV1_1(HandshakeMessageBuffer hmb) {
      this._deviceVersion = hmb.readByte();
   }

   public ClientHelloV1_1() {
   }

   @Override
   public int getDeviceVersion() {
      return this._deviceVersion;
   }

   @Override
   public void setDeviceVersion(int deviceVersion) {
      this._deviceVersion = deviceVersion;
   }

   @Override
   public byte[] serialize() {
      HandshakeMessageBuffer hmb = new HandshakeMessageBuffer(3);
      hmb.writeByte((byte)1);
      hmb.writeByte((byte)this.getMessageVersion());
      hmb.writeByte((byte)this._deviceVersion);
      return hmb.getBytes();
   }

   public static byte getCommand() {
      return 0;
   }

   @Override
   public int getMessageVersion() {
      return 2;
   }
}
