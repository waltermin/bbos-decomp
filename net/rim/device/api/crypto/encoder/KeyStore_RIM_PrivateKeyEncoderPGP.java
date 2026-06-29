package net.rim.device.api.crypto.encoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.pgp.PGPPrivateKey;
import net.rim.device.internal.crypto.pgp.PGPUtilities;

final class KeyStore_RIM_PrivateKeyEncoderPGP extends PrivateKeyEncoder {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final EncodedKey encodeKey(PrivateKey key) throws CryptoTokenException, CryptoUnsupportedOperationException {
      if (!(key instanceof PGPPrivateKey)) {
         throw new CryptoUnsupportedOperationException();
      }

      PGPPrivateKey privateKey = (PGPPrivateKey)key;

      try {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         DataOutputStream output = new DataOutputStream(outputStream);
         output.writeInt(0);
         output.writeInt(6);
         output.write(privateKey.encode());
         output.close();
         outputStream.close();
         return new EncodedKey(outputStream.toByteArray(), PGPUtilities.ENCODING_ALGORITHM);
      } catch (Throwable var6) {
         throw new CryptoTokenException(e.toString());
      }
   }

   @Override
   public final String getEncodingAlgorithm() {
      return PGPUtilities.ENCODING_ALGORITHM;
   }

   @Override
   public final String[] getKeyAlgorithms() {
      return new String[]{PGPUtilities.PGP};
   }
}
