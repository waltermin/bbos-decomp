package net.rim.device.api.crypto.encoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import net.rim.device.api.crypto.SymmetricKey;

final class KeyStore_RIM_SymmetricKeyEncoder1 extends SymmetricKeyEncoder {
   @Override
   protected final EncodedKey encodeKey(SymmetricKey key) {
      try {
         ByteArrayOutputStream output = (ByteArrayOutputStream)(new Object());
         DataOutputStream dataOut = (DataOutputStream)(new Object(output));
         dataOut.writeInt(0);
         int algIdentifier;
         if (key.getAlgorithm().equals("AES")) {
            algIdentifier = 1;
         } else if (key.getAlgorithm().equals("ARC4")) {
            algIdentifier = 2;
         } else if (key.getAlgorithm().equals("DES")) {
            algIdentifier = 4;
         } else if (key.getAlgorithm().equals("RC5")) {
            algIdentifier = 7;
         } else if (key.getAlgorithm().equals("TripleDES")) {
            algIdentifier = 9;
         } else {
            if (!key.getAlgorithm().equals("HMAC")) {
               throw new Object();
            }

            algIdentifier = 5;
         }

         dataOut.writeInt(algIdentifier);
         Utility.writeData(key.getData(), dataOut);
         dataOut.close();
         return new EncodedKey(output.toByteArray(), "KeyStore");
      } finally {
         throw new Object();
      }
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "KeyStore";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"AES", "ARC4", "DES", "HMAC", "RC5", "TripleDES"};
   }
}
