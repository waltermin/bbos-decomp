package net.rim.device.api.crypto.encoder;

import java.io.DataInputStream;
import java.io.InputStream;
import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.InvalidKeyEncodingException;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.RSAPublicKey;

final class WTLS_RIM_PublicKeyDecoder extends PublicKeyDecoder {
   @Override
   public final PublicKey decodeKey(InputStream encodedKey, CryptoSystem cryptoSystem, String keyAlgorithm) throws InvalidKeyEncodingException {
      if (keyAlgorithm == null) {
         throw new InvalidKeyEncodingException();
      }

      try {
         switch (keyAlgorithm.charAt(0)) {
            case '2':
               DataInputStream input = new DataInputStream(encodedKey);
               if (input.readUnsignedByte() != 0) {
                  throw new InvalidKeyEncodingException();
               }

               byte[] e = new byte[input.readUnsignedShort()];
               input.readFully(e);
               byte[] n = new byte[input.readUnsignedShort()];
               input.readFully(n);
               return new RSAPublicKey(new RSACryptoSystem(n.length * 8), e, n);
         }
      } finally {
         throw new InvalidKeyEncodingException();
      }

      throw new InvalidKeyEncodingException();
   }

   @Override
   public final String getEncodingAlgorithm() {
      return "WTLS";
   }

   @Override
   public final String[] getKeyAlgorithms() {
      return new String[]{"2"};
   }
}
