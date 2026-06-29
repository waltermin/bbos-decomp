package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public class SmartCardRSACryptoToken extends RSACryptoToken implements Persistable {
   protected SmartCardRSACryptoToken() {
   }

   @Override
   public boolean isSupportedDecryptRSA(RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData) {
      return this.isSupportedDecryptRSASmartCardImpl(cryptoSystem, privateKeyData);
   }

   protected boolean isSupportedDecryptRSASmartCardImpl(CryptoSystem _1, CryptoTokenPrivateKeyData _2) {
      throw null;
   }

   @Override
   public void decryptRSA(
      RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset
   ) {
      this.decryptRSASmartCardImpl(cryptoSystem, privateKeyData, input, inputOffset, output, outputOffset);
   }

   protected void decryptRSASmartCardImpl(CryptoSystem _1, CryptoTokenPrivateKeyData _2, byte[] _3, int _4, byte[] _5, int _6) {
      throw null;
   }

   @Override
   public void signRSA(RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset) {
      this.signRSASmartCardImpl(cryptoSystem, privateKeyData, input, inputOffset, output, outputOffset);
   }

   protected void signRSASmartCardImpl(CryptoSystem _1, CryptoTokenPrivateKeyData _2, byte[] _3, int _4, byte[] _5, int _6) {
      throw null;
   }
}
