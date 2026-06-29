package net.rim.wica.transport.handshake;

import net.rim.wica.transport.security.Key;
import net.rim.wica.transport.security.SecurityAlgorithm;
import net.rim.wica.transport.security.SecurityProvider;

public class RegisterV1_1 implements RegisterV1 {
   private int _deviceVersion;
   private byte[] _pin;
   private byte[] _reVersion;
   private byte[] _deviceVersions;
   private boolean _resetState;
   private byte[] _ciphertextRSK;
   private byte[] _plaintextRSK;
   private byte[] _ciphertextRK;
   private byte[] _plaintextRK;
   private byte[] _nonce;
   private byte[] _textToSign;
   private byte[] _signature;
   private SecurityProvider _sp;

   public RegisterV1_1(HandshakeMessageBuffer hmb, SecurityProvider sp) {
      this._deviceVersion = hmb.readByte();
      this._pin = hmb.readBytes();
      this._reVersion = hmb.readBytes();
      this._deviceVersions = hmb.readBytes();
      this._resetState = hmb.readByte() == 1;
      this._ciphertextRSK = hmb.readBytes();
      this._ciphertextRK = hmb.readBytes();
      this._nonce = hmb.readBytes();
      this._textToSign = hmb.getBytes();
      this._signature = hmb.readBytes();
      this._sp = sp;
   }

   public RegisterV1_1(SecurityProvider sp) {
      this._sp = sp;
   }

   @Override
   public String getPIN() {
      return (String)(this._pin == null ? null : new Object(this._pin));
   }

   @Override
   public void setPIN(String pin) {
      this._pin = pin == null ? null : pin.getBytes();
   }

   @Override
   public String getReVersion() {
      return (String)(this._reVersion == null ? null : new Object(this._reVersion));
   }

   @Override
   public void setReVersion(String reVersion) {
      this._reVersion = reVersion == null ? null : reVersion.getBytes();
   }

   @Override
   public byte[] getDeviceVersions() {
      return this._deviceVersions;
   }

   @Override
   public void setDeviceVersions(byte[] deviceVersions) {
      this._deviceVersions = deviceVersions;
   }

   @Override
   public boolean resetState() {
      return this._resetState;
   }

   @Override
   public void setResetState(boolean rs) {
      this._resetState = rs;
   }

   @Override
   public byte[] getResetKey() {
      return this._plaintextRSK;
   }

   @Override
   public void setResetKey(byte[] resetKey) {
      this._plaintextRSK = resetKey;
   }

   @Override
   public byte[] getRK() {
      return this._plaintextRK;
   }

   @Override
   public void setRK(byte[] rk) {
      this._plaintextRK = rk;
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
   public void setDeviceVersion(int deviceVersion) {
      this._deviceVersion = deviceVersion;
   }

   @Override
   public int getDeviceVersion() {
      return this._deviceVersion;
   }

   @Override
   public void sign(Key key) {
      HandshakeMessageBuffer hmb = new HandshakeMessageBuffer(2048);
      hmb.writeByte((byte)0);
      hmb.writeByte((byte)this.getMessageVersion());
      hmb.writeByte(getCommand());
      hmb.writeByte((byte)this._deviceVersion);
      hmb.writeBytes(this._pin);
      hmb.writeBytes(this._reVersion);
      hmb.writeBytes(this._deviceVersions);
      hmb.writeByte((byte)(this._resetState ? 1 : 0));
      hmb.writeBytes(this._ciphertextRSK);
      hmb.writeBytes(this._ciphertextRK);
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
   public void secure(Key key) {
      SecurityAlgorithm algorithm = SecurityAlgorithm.RSA_ECB_PKCS1;
      if (this._plaintextRK != null) {
         this._ciphertextRK = this._sp.encrypt(this._plaintextRK, algorithm, key, null);
         this._plaintextRK = null;
      } else {
         this._ciphertextRK = null;
      }

      if (this._plaintextRSK != null) {
         this._ciphertextRSK = this._sp.encrypt(this._plaintextRSK, algorithm, key, null);
         this._plaintextRSK = null;
      } else {
         this._ciphertextRSK = null;
      }
   }

   @Override
   public void unsecure(Key key) {
      SecurityAlgorithm algorithm = SecurityAlgorithm.RSA_ECB_PKCS1;
      if (this._ciphertextRK != null) {
         this._plaintextRK = this._sp.decrypt(this._ciphertextRK, algorithm, key, null);
         this._ciphertextRK = null;
      } else {
         this._plaintextRK = null;
      }

      if (this._ciphertextRSK != null) {
         this._plaintextRSK = this._sp.decrypt(this._ciphertextRSK, algorithm, key, null);
         this._ciphertextRSK = null;
      } else {
         this._plaintextRSK = null;
      }
   }

   @Override
   public byte[] serialize() {
      HandshakeMessageBuffer hmb = new HandshakeMessageBuffer(2048);
      hmb.writeByte((byte)0);
      hmb.writeByte((byte)this.getMessageVersion());
      hmb.writeByte(getCommand());
      hmb.writeByte((byte)this._deviceVersion);
      hmb.writeBytes(this._pin);
      hmb.writeBytes(this._reVersion);
      hmb.writeBytes(this._deviceVersions);
      hmb.writeByte((byte)(this._resetState ? 1 : 0));
      hmb.writeBytes(this._ciphertextRSK);
      hmb.writeBytes(this._ciphertextRK);
      hmb.writeBytes(this._nonce);
      hmb.writeBytes(this._signature);
      return hmb.getBytes();
   }

   public static byte getCommand() {
      return 2;
   }

   @Override
   public int getMessageVersion() {
      return 2;
   }
}
