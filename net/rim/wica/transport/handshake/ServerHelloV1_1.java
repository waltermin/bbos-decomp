package net.rim.wica.transport.handshake;

public class ServerHelloV1_1 implements ServerHelloV1 {
   private int _serverVersion;
   private long _serverId;
   private EncodedCertificate[] _serverCertificateChain;

   public ServerHelloV1_1(HandshakeMessageBuffer hmb) {
      this._serverVersion = hmb.readByte();
      this._serverId = hmb.readLong();
      int certificateChainLength = hmb.readInt();
      if (certificateChainLength != -1) {
         this._serverCertificateChain = new EncodedCertificate[certificateChainLength];

         for (int i = 0; i < certificateChainLength; i++) {
            this._serverCertificateChain[i] = new EncodedCertificate(hmb.readBytes());
         }
      }
   }

   public ServerHelloV1_1() {
   }

   @Override
   public long getServerId() {
      return this._serverId;
   }

   @Override
   public void setServerId(long serverId) {
      this._serverId = serverId;
   }

   @Override
   public EncodedCertificate[] getServerCertificateChain() {
      return this._serverCertificateChain;
   }

   @Override
   public void setServerCertificateChain(EncodedCertificate[] certificateChain) {
      this._serverCertificateChain = certificateChain;
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
   public byte[] serialize() {
      HandshakeMessageBuffer hmb = new HandshakeMessageBuffer(16384);
      hmb.writeByte((byte)2);
      hmb.writeByte((byte)this.getMessageVersion());
      hmb.writeByte((byte)this._serverVersion);
      hmb.writeLong(this._serverId);
      hmb.writeInt(this._serverCertificateChain == null ? -1 : this._serverCertificateChain.length);
      if (this._serverCertificateChain != null) {
         for (int i = 0; i < this._serverCertificateChain.length; i++) {
            hmb.writeBytes(this._serverCertificateChain[i].getCertificate());
         }
      }

      return hmb.getBytes();
   }

   public static byte getCommand() {
      return 1;
   }

   @Override
   public int getMessageVersion() {
      return 2;
   }
}
