package net.rim.wica.transport.handshake;

import net.rim.wica.transport.security.Key;
import net.rim.wica.transport.security.SecurityAlgorithm;
import net.rim.wica.transport.security.SecurityProvider;

public class FailureV1_1 implements FailureV1 {
   private byte[] _reason;
   private byte[] _nonce;
   private byte[] _textToSign;
   private byte[] _signature;
   private SecurityProvider _sp;

   public FailureV1_1(HandshakeMessageBuffer hmb, SecurityProvider sp) {
      this._reason = hmb.readBytes();
      this._nonce = hmb.readBytes();
      this._textToSign = hmb.getBytes();
      this._signature = hmb.readBytes();
      this._sp = sp;
   }

   public FailureV1_1(SecurityProvider sp) {
      this._sp = sp;
   }

   @Override
   public String getReason() {
      return (String)(this._reason == null ? null : new Object(this._reason));
   }

   @Override
   public void setReason(String reason) {
      this._reason = reason == null ? null : reason.getBytes();
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
      hmb.writeBytes(this._reason);
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
      hmb.writeBytes(this._reason);
      hmb.writeBytes(this._nonce);
      hmb.writeBytes(this._signature);
      return hmb.getBytes();
   }

   public static byte getCommand() {
      return 4;
   }

   @Override
   public int getMessageVersion() {
      return 2;
   }
}
