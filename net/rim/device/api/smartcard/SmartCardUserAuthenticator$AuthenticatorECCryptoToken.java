package net.rim.device.api.smartcard;

import net.rim.device.api.crypto.CryptoTokenCryptoSystemData;
import net.rim.device.api.crypto.CryptoTokenPrivateKeyData;
import net.rim.device.api.crypto.ECCryptoToken;

class SmartCardUserAuthenticator$AuthenticatorECCryptoToken extends ECCryptoToken {
   ECCryptoToken _ecCryptoToken;
   SmartCardSession _smartCardSession;

   public SmartCardUserAuthenticator$AuthenticatorECCryptoToken(ECCryptoToken ecCryptoToken, SmartCardSession smartCardSession) {
      this._smartCardSession = smartCardSession;
      this._ecCryptoToken = ecCryptoToken;
   }

   @Override
   public void signECDSA(
      CryptoTokenCryptoSystemData cryptoTokenCryptoSystemData,
      CryptoTokenPrivateKeyData cryptoTokenPrivateKeyData,
      byte[] digest,
      int digestOffset,
      int digestLength,
      byte[] r,
      int rOffset,
      byte[] s,
      int sOffset
   ) {
      this._ecCryptoToken
         .signECDSA(cryptoTokenCryptoSystemData, cryptoTokenPrivateKeyData, digest, digestOffset, digestLength, r, rOffset, s, sOffset, this._smartCardSession);
   }
}
