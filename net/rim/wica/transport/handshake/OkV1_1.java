package net.rim.wica.transport.handshake;

import net.rim.wica.transport.security.Key;
import net.rim.wica.transport.security.SecurityAlgorithm;
import net.rim.wica.transport.security.SecurityProvider;

public class OkV1_1 implements OkV1 {
   private long _deviceId;
   private long _serverId;
   private boolean _firstHandshake;
   private byte[] _serverVersions;
   private byte[] _nonce;
   private byte[] _textToSign;
   private byte[] _signature;
   private SecurityProvider _sp;

   public OkV1_1(HandshakeMessageBuffer hmb, SecurityProvider sp) {
      this._deviceId = hmb.readLong();
      this._serverId = hmb.readLong();
      this._firstHandshake = hmb.readByte() == 1;
      this._serverVersions = hmb.readBytes();
      this._nonce = hmb.readBytes();
      this._textToSign = hmb.getBytes();
      this._signature = hmb.readBytes();
      this._sp = sp;
   }

   public OkV1_1(SecurityProvider sp) {
      this._sp = sp;
   }

   @Override
   public long getDeviceId() {
      return this._deviceId;
   }

   @Override
   public void setDeviceId(long deviceId) {
      this._deviceId = deviceId;
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
   public boolean firstHandshake() {
      return this._firstHandshake;
   }

   @Override
   public void setFirstHandshake(boolean firstHandshake) {
      this._firstHandshake = firstHandshake;
   }

   @Override
   public byte[] getServerVersions() {
      return this._serverVersions;
   }

   @Override
   public void setServerVersions(byte[] serverVersions) {
      this._serverVersions = serverVersions;
   }

   @Override
   public byte[] getNonce() {
      return this._nonce;
   }

   @Override
   public void setNonce(byte[] nonce) {
      this._nonce = nonce;
   }

   @Override
   public void sign(Key key) {
      HandshakeMessageBuffer hmb = new HandshakeMessageBuffer(1024);
      hmb.writeByte((byte)0);
      hmb.writeByte((byte)this.getMessageVersion());
      hmb.writeByte(getCommand());
      hmb.writeLong(this._deviceId);
      hmb.writeLong(this._serverId);
      hmb.writeByte((byte)(this._firstHandshake ? 1 : 0));
      hmb.writeBytes(this._serverVersions);
      hmb.writeBytes(this._nonce);
      this._textToSign = hmb.getBytes();
      SecurityAlgorithm algorithm = SecurityAlgorithm.HMAC_SHA1;
      this._signature = this._sp.sign(this._textToSign, algorithm, key);
   }

   @Override
   public boolean verifySignature(Key key) {
      boolean ok = false;
      if (this._signature != null) {
         SecurityAlgorithm algorithm = SecurityAlgorithm.HMAC_SHA1;
         ok = this._sp.verifySignature(this._textToSign, this._signature, algorithm, key);
      }

      return ok;
   }

   @Override
   public byte[] serialize() {
      HandshakeMessageBuffer hmb = new HandshakeMessageBuffer(1024);
      hmb.writeByte((byte)0);
      hmb.writeByte((byte)this.getMessageVersion());
      hmb.writeByte(getCommand());
      hmb.writeLong(this._deviceId);
      hmb.writeLong(this._serverId);
      hmb.writeByte((byte)(this._firstHandshake ? 1 : 0));
      hmb.writeBytes(this._serverVersions);
      hmb.writeBytes(this._nonce);
      hmb.writeBytes(this._signature);
      return hmb.getBytes();
   }

   public static byte getCommand() {
      return 3;
   }

   @Override
   public int getMessageVersion() {
      return 2;
   }
}
