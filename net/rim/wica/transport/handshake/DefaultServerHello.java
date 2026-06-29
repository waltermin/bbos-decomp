package net.rim.wica.transport.handshake;

public class DefaultServerHello implements ServerHello {
   private int _serverVersion;
   private int _messageVersion;

   public DefaultServerHello(HandshakeMessageBuffer hmb, int messageVersion) {
      this._messageVersion = messageVersion;
      this._serverVersion = hmb.readByte();
   }

   public DefaultServerHello() {
   }

   @Override
   public int getServerVersion() {
      return this._serverVersion;
   }

   @Override
   public void setServerVersion(int serverVersion) {
      this._serverVersion = serverVersion;
   }

   @Override
   public int getMessageVersion() {
      return this._messageVersion;
   }

   @Override
   public byte[] serialize() {
      HandshakeMessageBuffer hmb = new HandshakeMessageBuffer(3);
      hmb.writeByte((byte)2);
      hmb.writeByte((byte)-1);
      hmb.writeByte((byte)this._serverVersion);
      return hmb.getBytes();
   }
}
