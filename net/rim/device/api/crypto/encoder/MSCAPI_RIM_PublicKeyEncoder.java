package net.rim.device.api.crypto.encoder;

import java.io.ByteArrayOutputStream;
import net.rim.device.api.crypto.CryptoUtilities;
import net.rim.device.api.crypto.DHCryptoSystem;
import net.rim.device.api.crypto.DHPublicKey;
import net.rim.device.api.crypto.DSACryptoSystem;
import net.rim.device.api.crypto.DSAPublicKey;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.RSAPublicKey;

final class MSCAPI_RIM_PublicKeyEncoder extends PublicKeyEncoder {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final EncodedKey encodeKey(PublicKey k, long flags) {
      try {
         if (k.getAlgorithm().equals("DSA")) {
            DSAPublicKey key = (DSAPublicKey)k;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            CryptoUtilities.writeBytes(output, 6, 2, 0, 0);
            CryptoUtilities.writeBytes(output, 0, 34, 0, 0);
            CryptoUtilities.writeBytes(output, 68, 83, 83, 51);
            DSACryptoSystem cryptoSystem = key.getDSACryptoSystem();
            int bitlen = cryptoSystem.getP().length * 8;
            CryptoUtilities.writeIntegerLittleEndian(output, bitlen);
            bitlen = cryptoSystem.getQ().length * 8;
            CryptoUtilities.writeIntegerLittleEndian(output, bitlen);
            CryptoUtilities.writeBytes(output, 0, 0, 0, 0);
            CryptoUtilities.writeBytes(output, 255, 255, 255, 255);

            for (int i = 0; i < 20; i++) {
               output.write(0);
            }

            output.write(CryptoUtilities.flipArray(cryptoSystem.getP()));
            output.write(CryptoUtilities.flipArray(cryptoSystem.getQ()));
            output.write(CryptoUtilities.flipArray(cryptoSystem.getG()));
            output.write(CryptoUtilities.flipArray(key.getPublicKeyData()));
            byte[] data = output.toByteArray();
            return new EncodedKey(output.toByteArray(), "MSCAPI");
         }

         if (k.getAlgorithm().equals("DH")) {
            DHPublicKey key = (DHPublicKey)k;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            CryptoUtilities.writeBytes(output, 6, 2, 0, 0);
            CryptoUtilities.writeBytes(output, 2, 170, 0, 0);
            CryptoUtilities.writeBytes(output, 0, 68, 72, 51);
            DHCryptoSystem cryptoSystem = key.getDHCryptoSystem();
            int bitlen = cryptoSystem.getP().length * 8;
            CryptoUtilities.writeIntegerLittleEndian(output, bitlen);
            byte[] q = cryptoSystem.getQ();
            bitlen = q == null ? 0 : q.length * 8;
            CryptoUtilities.writeIntegerLittleEndian(output, bitlen);
            CryptoUtilities.writeBytes(output, 0, 0, 0, 0);
            CryptoUtilities.writeBytes(output, 255, 255, 255, 255);

            for (int i = 0; i < 20; i++) {
               output.write(0);
            }

            output.write(CryptoUtilities.flipArray(cryptoSystem.getP()));
            if (q != null) {
               output.write(CryptoUtilities.flipArray(q));
            }

            output.write(CryptoUtilities.flipArray(cryptoSystem.getG()));
            output.write(CryptoUtilities.flipArray(key.getPublicKeyData()));
            byte[] data = output.toByteArray();
            return new EncodedKey(data, "MSCAPI");
         }

         if (k.getAlgorithm().equals("RSA")) {
            RSAPublicKey key = (RSAPublicKey)k;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            CryptoUtilities.writeBytes(output, 6, 2, 0, 0);
            CryptoUtilities.writeBytes(output, 0, 164, 0, 0);
            CryptoUtilities.writeBytes(output, 82, 83, 65, 49);
            int bitlen = key.getRSACryptoSystem().getBitLength();
            CryptoUtilities.writeIntegerLittleEndian(output, bitlen);
            CryptoUtilities.writeByteArray4BigEndian(output, key.getE());
            output.write(CryptoUtilities.flipArray(key.getN()));
            byte[] data = output.toByteArray();
            if (data.length > 20 + bitlen / 8) {
               throw new IllegalArgumentException();
            }

            return new EncodedKey(output.toByteArray(), "MSCAPI");
         }
      } catch (Throwable var11) {
         throw new RuntimeException(e.toString());
      }

      throw new IllegalArgumentException();
   }

   @Override
   public final String getEncodingAlgorithm() {
      return "MSCAPI";
   }

   @Override
   public final String[] getKeyAlgorithms() {
      return new String[]{"DSA", "RSA", "DH"};
   }
}
