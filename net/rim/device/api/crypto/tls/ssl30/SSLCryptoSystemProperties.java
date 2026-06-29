package net.rim.device.api.crypto.tls.ssl30;

import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.cldc.io.ssl.SSLConnectionOptions;

class SSLCryptoSystemProperties extends CryptoSystemProperties implements SSLRecordProtocolConstants {
   private byte _keyExchange;
   private SSLConnectionOptions _connectionOptions;

   SSLCryptoSystemProperties(byte keyExchange, SSLConnectionOptions connectionOptions) {
      this._keyExchange = keyExchange;
      this._connectionOptions = connectionOptions;
   }

   @Override
   public boolean isCryptoSystemStrong(CryptoSystem cryptoSystem) {
      return cryptoSystem == null ? false : this.isCryptoSystemStrong(this._keyExchange, this._connectionOptions, cryptoSystem);
   }

   private boolean isCryptoSystemStrong(byte keyExchange, SSLConnectionOptions connectionOptions, CryptoSystem cryptoSystem) {
      if (cryptoSystem != null && connectionOptions != null) {
         int bitLength = cryptoSystem.getBitLength();
         switch (keyExchange) {
            case 0:
            case 4:
            case 5:
            case 10:
            case 11:
               return cryptoSystem.isStrong();
            case 1:
            case 2:
            case 8:
            case 9:
            default:
               if (bitLength < connectionOptions.getMinimumStrongRSAKeySize()) {
                  return false;
               }

               return true;
            case 3:
               if (bitLength < connectionOptions.getMinimumStrongDHKeySize()) {
                  return false;
               }

               return true;
            case 6:
            case 7:
               if (bitLength < connectionOptions.getMinimumStrongDSAKeySize()) {
                  return false;
               }

               return true;
            case 12:
            case 13:
               return bitLength >= connectionOptions.getMinimumStrongECKeySize();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }
}
