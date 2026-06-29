package net.rim.device.api.crypto.encoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import net.rim.device.api.crypto.DHCryptoSystem;
import net.rim.device.api.crypto.DHPrivateKey;
import net.rim.device.api.crypto.DSACryptoSystem;
import net.rim.device.api.crypto.DSAPrivateKey;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.RSAPrivateKey;

final class KeyStore_RIM_PrivateKeyEncoder1 extends PrivateKeyEncoder {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final EncodedKey encodeKey(PrivateKey key) {
      try {
         ByteArrayOutputStream output = (ByteArrayOutputStream)(new Object());
         DataOutputStream dataOut = (DataOutputStream)(new Object(output));
         dataOut.writeInt(0);
         if (key.getAlgorithm().equals("RSA")) {
            RSAPrivateKey k = (RSAPrivateKey)key;
            dataOut.writeInt(5);
            Utility.writeData(k.getN(), dataOut);
            Utility.writeData(k.getE(), dataOut);
            Utility.writeData(k.getD(), dataOut);
            Utility.writeData(k.getP(), dataOut);
            Utility.writeData(k.getQ(), dataOut);
            Utility.writeData(k.getDModPm1(), dataOut);
            Utility.writeData(k.getDModQm1(), dataOut);
            Utility.writeData(k.getQInvModP(), dataOut);
         } else if (key.getAlgorithm().equals("DH")) {
            DHPrivateKey k = (DHPrivateKey)key;
            DHCryptoSystem cryptoSystem = k.getDHCryptoSystem();
            dataOut.writeInt(1);
            Utility.writeData(cryptoSystem.getP(), dataOut);
            Utility.writeData(cryptoSystem.getG(), dataOut);
            Utility.writeData(cryptoSystem.getQ(), dataOut);
            Utility.writeData(k.getPrivateKeyData(), dataOut);
         } else {
            if (!key.getAlgorithm().equals("DSA")) {
               throw new Object();
            }

            DSAPrivateKey k = (DSAPrivateKey)key;
            DSACryptoSystem cryptoSystem = k.getDSACryptoSystem();
            dataOut.writeInt(2);
            Utility.writeData(cryptoSystem.getP(), dataOut);
            Utility.writeData(cryptoSystem.getQ(), dataOut);
            Utility.writeData(cryptoSystem.getG(), dataOut);
            Utility.writeData(k.getPrivateKeyData(), dataOut);
         }

         dataOut.close();
         return new EncodedKey(output.toByteArray(), "KeyStore");
      } catch (Throwable var7) {
         throw new Object(e.toString());
      }
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "KeyStore";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"RSA", "DH", "DSA"};
   }
}
