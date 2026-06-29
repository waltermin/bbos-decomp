package net.rim.device.api.smartcard;

import net.rim.device.api.crypto.CryptoTokenCryptoSystemData;
import net.rim.device.api.crypto.CryptoTokenPrivateKeyData;
import net.rim.device.api.crypto.DSACryptoToken;

class SmartCardUserAuthenticator$AuthenticatorDSACryptoToken extends DSACryptoToken {
   DSACryptoToken _dsaCryptoToken;
   SmartCardSession _smartCardSession;

   public SmartCardUserAuthenticator$AuthenticatorDSACryptoToken(DSACryptoToken dsaCryptoToken, SmartCardSession smartCardSession) {
      this._smartCardSession = smartCardSession;
      this._dsaCryptoToken = dsaCryptoToken;
   }

   @Override
   public void signDSA(
      CryptoTokenCryptoSystemData cryptoSystemData,
      CryptoTokenPrivateKeyData privateKeyData,
      byte[] digest,
      int digestOffset,
      int digestLength,
      byte[] r,
      int rOffset,
      byte[] s,
      int sOffset
   ) {
      this._dsaCryptoToken.signDSA(cryptoSystemData, privateKeyData, digest, digestOffset, digestLength, r, rOffset, s, sOffset, this._smartCardSession);
   }

   @Override
   public int getDSAPrivateKeyLength(CryptoTokenCryptoSystemData cryptoTokenData) {
      return this._dsaCryptoToken.getDSAPrivateKeyLength(cryptoTokenData);
   }
}
