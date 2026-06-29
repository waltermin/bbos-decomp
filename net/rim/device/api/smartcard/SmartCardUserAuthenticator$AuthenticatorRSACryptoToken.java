package net.rim.device.api.smartcard;

import net.rim.device.api.crypto.CryptoTokenPrivateKeyData;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.RSACryptoToken;

class SmartCardUserAuthenticator$AuthenticatorRSACryptoToken extends RSACryptoToken {
   RSACryptoToken _rsaCryptoToken;
   SmartCardSession _smartCardSession;

   public SmartCardUserAuthenticator$AuthenticatorRSACryptoToken(RSACryptoToken rsaCryptoToken, SmartCardSession smartCardSession) {
      this._smartCardSession = smartCardSession;
      this._rsaCryptoToken = rsaCryptoToken;
   }

   @Override
   public void signRSA(RSACryptoSystem cryptoSystem, CryptoTokenPrivateKeyData privateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset) {
      this._rsaCryptoToken.signRSA(cryptoSystem, privateKeyData, input, inputOffset, output, outputOffset, this._smartCardSession);
   }
}
