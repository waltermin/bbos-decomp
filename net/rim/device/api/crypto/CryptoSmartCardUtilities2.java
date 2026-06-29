package net.rim.device.api.crypto;

public class CryptoSmartCardUtilities2 {
   private CryptoSmartCardUtilities2() {
   }

   public static PrivateKey createPrivateKey(
      SmartCardRSACryptoToken smartCardRSACryptoToken, int modulusBitLength, CryptoTokenPrivateKeyData cryptoTokenPrivateKeyData
   ) {
      return (PrivateKey)(new Object((RSACryptoSystem)(new Object(smartCardRSACryptoToken, modulusBitLength)), cryptoTokenPrivateKeyData));
   }

   public static PrivateKey createPrivateKey(
      SmartCardDSACryptoToken smartCardDSACryptoToken, CryptoTokenCryptoSystemData cryptoTokenSystemData, CryptoTokenPrivateKeyData cryptoTokenPrivateKeyData
   ) {
      return (PrivateKey)(new Object((DSACryptoSystem)(new Object(smartCardDSACryptoToken, cryptoTokenSystemData)), cryptoTokenPrivateKeyData));
   }
}
