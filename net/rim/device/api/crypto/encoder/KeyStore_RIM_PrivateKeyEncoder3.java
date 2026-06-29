package net.rim.device.api.crypto.encoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import net.rim.device.api.crypto.ECCryptoSystem;
import net.rim.device.api.crypto.ECPrivateKey;
import net.rim.device.api.crypto.KEACryptoSystem;
import net.rim.device.api.crypto.KEAPrivateKey;
import net.rim.device.api.crypto.PrivateKey;

final class KeyStore_RIM_PrivateKeyEncoder3 extends PrivateKeyEncoder {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final EncodedKey encodeKey(PrivateKey key) {
      try {
         ByteArrayOutputStream output = (ByteArrayOutputStream)(new Object());
         DataOutputStream dataOut = (DataOutputStream)(new Object(output));
         dataOut.writeInt(0);
         if (key.getAlgorithm().equals("EC")) {
            ECPrivateKey k = (ECPrivateKey)key;
            ECCryptoSystem cryptoSystem = k.getECCryptoSystem();
            dataOut.writeInt(3);
            String curveName = k.getECCryptoSystem().getName();
            Utility.writeData(curveName.getBytes(), dataOut);
            Utility.writeData(k.getPrivateKeyData(), dataOut);
         } else {
            if (!key.getAlgorithm().equals("KEA")) {
               throw new Object();
            }

            KEAPrivateKey k = (KEAPrivateKey)key;
            KEACryptoSystem cryptoSystem = k.getKEACryptoSystem();
            dataOut.writeInt(4);
            Utility.writeData(cryptoSystem.getP(), dataOut);
            Utility.writeData(cryptoSystem.getQ(), dataOut);
            Utility.writeData(cryptoSystem.getG(), dataOut);
            Utility.writeData(k.getPrivateKeyData(), dataOut);
         }

         dataOut.close();
         return (EncodedKey)(new Object(output.toByteArray(), "KeyStore"));
      } catch (Throwable var8) {
         throw new Object(e.toString());
      }
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "KeyStore";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"EC", "KEA"};
   }
}
