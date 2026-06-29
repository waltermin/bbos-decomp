package net.rim.wica.transport.handshake;

public class DefaultClientHello implements ClientHello {
   private int _deviceVersion;
   private int _messageVersion;

   public DefaultClientHello(HandshakeMessageBuffer hmb, int messageVersion) {
      this._messageVersion = messageVersion;
      this._deviceVersion = hmb.readByte();
   }

   public DefaultClientHello() {
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
   public int getMessageVersion() {
      return this._messageVersion;
   }

   @Override
   public byte[] serialize() {
      HandshakeMessageBuffer hmb = new HandshakeMessageBuffer(3);
      hmb.writeByte((byte)1);
      hmb.writeByte((byte)-1);
      hmb.writeByte((byte)this._deviceVersion);
      return hmb.getBytes();
   }
}
