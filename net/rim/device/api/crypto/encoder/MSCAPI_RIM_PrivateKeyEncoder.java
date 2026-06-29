package net.rim.device.api.crypto.encoder;

import java.io.ByteArrayOutputStream;
import net.rim.device.api.crypto.CryptoUtilities;
import net.rim.device.api.crypto.DHCryptoSystem;
import net.rim.device.api.crypto.DHPrivateKey;
import net.rim.device.api.crypto.DSACryptoSystem;
import net.rim.device.api.crypto.DSAPrivateKey;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.RSAPrivateKey;

final class MSCAPI_RIM_PrivateKeyEncoder extends PrivateKeyEncoder {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final EncodedKey encodeKey(PrivateKey k) {
      try {
         if (k.getAlgorithm().equals("DSA")) {
            DSAPrivateKey key = (DSAPrivateKey)k;
            ByteArrayOutputStream output = (ByteArrayOutputStream)(new Object());
            CryptoUtilities.writeBytes(output, 7, 2, 0, 0);
            CryptoUtilities.writeBytes(output, 0, 34, 0, 0);
            CryptoUtilities.writeBytes(output, 68, 83, 83, 52);
            DSACryptoSystem cryptoSystem = key.getDSACryptoSystem();
            int bitlen = cryptoSystem.getP().length * 8;
            CryptoUtilities.writeIntegerLittleEndian(output, bitlen);
            bitlen = cryptoSystem.getQ().length * 8;
            CryptoUtilities.writeIntegerLittleEndian(output, bitlen);
            CryptoUtilities.writeBytes(output, 0, 0, 0, 0);
            bitlen = cryptoSystem.getPrivateKeyLength() * 8;
            CryptoUtilities.writeIntegerLittleEndian(output, bitlen);
            CryptoUtilities.writeBytes(output, 255, 255, 255, 255);

            for (int i = 0; i < 20; i++) {
               output.write(0);
            }

            output.write(CryptoUtilities.flipArray(cryptoSystem.getP()));
            output.write(CryptoUtilities.flipArray(cryptoSystem.getQ()));
            output.write(CryptoUtilities.flipArray(cryptoSystem.getG()));
            output.write(CryptoUtilities.flipArray(key.getPublicKeyData()));
            output.write(CryptoUtilities.flipArray(key.getPrivateKeyData()));
            return (EncodedKey)(new Object(output.toByteArray(), "MSCAPI"));
         }

         if (k.getAlgorithm().equals("DH")) {
            DHPrivateKey key = (DHPrivateKey)k;
            ByteArrayOutputStream output = (ByteArrayOutputStream)(new Object());
            CryptoUtilities.writeBytes(output, 7, 2, 0, 0);
            CryptoUtilities.writeBytes(output, 2, 170, 0, 0);
            CryptoUtilities.writeBytes(output, 0, 68, 72, 52);
            DHCryptoSystem cryptoSystem = key.getDHCryptoSystem();
            int bitlen = cryptoSystem.getP().length * 8;
            CryptoUtilities.writeIntegerLittleEndian(output, bitlen);
            byte[] q = cryptoSystem.getQ();
            bitlen = q == null ? 0 : q.length * 8;
            CryptoUtilities.writeIntegerLittleEndian(output, bitlen);
            CryptoUtilities.writeBytes(output, 0, 0, 0, 0);
            byte[] privateKeyData = key.getPrivateKeyData();
            bitlen = privateKeyData.length * 8;
            CryptoUtilities.writeIntegerLittleEndian(output, bitlen);
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
            output.write(CryptoUtilities.flipArray(privateKeyData));
            byte[] var24 = null;
            return (EncodedKey)(new Object(output.toByteArray(), "MSCAPI"));
         }

         if (k.getAlgorithm().equals("RSA")) {
            RSAPrivateKey key = (RSAPrivateKey)k;
            ByteArrayOutputStream output = (ByteArrayOutputStream)(new Object());
            CryptoUtilities.writeBytes(output, 7, 2, 0, 0);
            CryptoUtilities.writeBytes(output, 0, 164, 0, 0);
            CryptoUtilities.writeBytes(output, 82, 83, 65, 50);
            int bitlen = key.getRSACryptoSystem().getBitLength();
            CryptoUtilities.writeIntegerLittleEndian(output, bitlen);
            CryptoUtilities.writeByteArray4BigEndian(output, key.getE());
            output.write(CryptoUtilities.flipArray(key.getN()));
            output.write(CryptoUtilities.flipArray(key.getP()));
            output.write(CryptoUtilities.flipArray(key.getQ()));
            output.write(CryptoUtilities.flipArray(key.getDModPm1()));
            output.write(CryptoUtilities.flipArray(key.getDModQm1()));
            output.write(CryptoUtilities.flipArray(key.getQInvModP()));
            output.write(CryptoUtilities.flipArray(key.getD()));
            byte[] data = output.toByteArray();
            if (data.length > 20 + 2 * bitlen / 8 + 5 * bitlen / 16) {
               throw new Object();
            }

            return (EncodedKey)(new Object(output.toByteArray(), "MSCAPI"));
         }
      } catch (Throwable var10) {
         throw new Object(e.toString());
      }

      throw new Object();
   }

   @Override
   protected final String getEncodingAlgorithm() {
      return "MSCAPI";
   }

   @Override
   protected final String[] getKeyAlgorithms() {
      return new String[]{"DSA", "DH", "RSA"};
   }
}
