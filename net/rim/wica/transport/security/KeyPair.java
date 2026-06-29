package net.rim.wica.transport.security;

public class KeyPair {
   private Key _privateKey;
   private Key _publicKey;

   public KeyPair(Key privateKey, Key publicKey) {
      this._privateKey = privateKey;
      this._publicKey = publicKey;
   }

   public Key getPrivateKey() {
      return this._privateKey;
   }

   public Key getPublicKey() {
      return this._publicKey;
   }
}
