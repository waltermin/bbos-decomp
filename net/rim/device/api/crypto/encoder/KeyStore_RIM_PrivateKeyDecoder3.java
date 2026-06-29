package net.rim.device.api.crypto.encoder;

import java.io.DataInputStream;
import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.ECCryptoSystem;
import net.rim.device.api.crypto.ECPrivateKey;
import net.rim.device.api.crypto.KEACryptoSystem;
import net.rim.device.api.crypto.KEAPrivateKey;
import net.rim.device.api.crypto.PrivateKey;

final class KeyStore_RIM_PrivateKeyDecoder3 extends KeyStore_PrivateKeyDecoder {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final PrivateKey decodeKey(DataInputStream input, CryptoSystem cryptoSystem, String algorithm) {
      try {
         switch (algorithm.charAt(0)) {
            case '2':
               throw new Object();
            case '3':
            default: {
               byte[] cryptoSys = Utility.readData(input);
               String curveName = (String)(new Object(cryptoSys));
               if (curveName == null) {
                  throw new Object();
               }

               ECCryptoSystem eccCryptoSystem = new ECCryptoSystem(curveName);
               byte[] keyData = Utility.readData(input);
               return new ECPrivateKey(eccCryptoSystem, keyData);
            }
            case '4': {
               byte[] p = Utility.readData(input);
               byte[] q = Utility.readData(input);
               byte[] g = Utility.readData(input);
               KEACryptoSystem keaCryptoSystem = new KEACryptoSystem(p, q, g);
               byte[] keyData = Utility.readData(input);
               return new KEAPrivateKey(keaCryptoSystem, keyData);
            }
         }
      } catch (Throwable var10) {
         throw new Object(e.toString());
      }
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "KeyStore";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"3", "4"};
   }
}
