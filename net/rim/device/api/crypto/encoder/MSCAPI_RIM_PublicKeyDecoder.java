package net.rim.device.api.crypto.encoder;

import java.io.InputStream;
import net.rim.device.api.crypto.CryptoUtilities;
import net.rim.device.api.crypto.DHCryptoSystem;
import net.rim.device.api.crypto.DSACryptoSystem;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

final class MSCAPI_RIM_PublicKeyDecoder extends MSCAPI_PublicKeyDecoder {
   @Override
   public final PublicKey decodeKey(InputStream input, String algorithm) {
      switch (algorithm.charAt(0)) {
         case '1':
            byte[] header = CryptoUtilities.readByteArrayFromInputStream(input, 4);
            boolean version3 = true;
            if (!Arrays.equals(header, "DSS3".getBytes())) {
               if (!Arrays.equals(header, "DSS1".getBytes())) {
                  throw new Object();
               }

               version3 = false;
            }

            int bitlenP = CryptoUtilities.readIntegerLittleEndian(input);
            int bitlenQ = 160;
            int bitlenJ = 0;
            if (version3) {
               bitlenQ = CryptoUtilities.readIntegerLittleEndian(input);
               bitlenJ = CryptoUtilities.readIntegerLittleEndian(input);
               CryptoUtilities.readIntegerLittleEndian(input);
               CryptoUtilities.readByteArrayFromInputStream(input, 20);
            }

            byte[] p = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenP + 7) / 8);
            byte[] q = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenQ + 7) / 8);
            byte[] g = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenP + 7) / 8);
            byte[] j = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenJ + 7) / 8);
            byte[] y = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenP + 7) / 8);
            if (!version3) {
               CryptoUtilities.readIntegerLittleEndian(input);
               CryptoUtilities.readByteArrayFromInputStream(input, 20);
            }

            CryptoUtilities.flipArray(p);
            CryptoUtilities.flipArray(g);
            CryptoUtilities.flipArray(q);
            DSACryptoSystem cryptoSystem = (DSACryptoSystem)(new Object(p, q, g));
            return (PublicKey)(new Object(cryptoSystem, CryptoUtilities.flipArray(y)));
         case '2':
            CryptoUtilities.verifyKeyBytes(input, 82, 83, 65, 49);
            int bitlen = CryptoUtilities.readIntegerLittleEndian(input);
            RSACryptoSystem cryptoSystem = (RSACryptoSystem)(new Object(bitlen));
            byte[] e = CryptoUtilities.readInteger4BigEndianAsByteArray(input);
            int i = 4;

            while (e[i - 1] == 0 && i > 0) {
               i--;
            }

            Array.resize(e, i);
            byte[] n = CryptoUtilities.readByteArrayFromInputStream(input, (bitlen + 7) / 8);
            CryptoUtilities.flipArray(n);
            return (PublicKey)(new Object(cryptoSystem, e, n));
         case '5':
            CryptoUtilities.verifyKeyBytes(input, 0, 68, 72, 51);
            int bitlenP = CryptoUtilities.readIntegerLittleEndian(input);
            int bitlenQ = CryptoUtilities.readIntegerLittleEndian(input);
            int bitlenJ = CryptoUtilities.readIntegerLittleEndian(input);
            int counter = CryptoUtilities.readIntegerLittleEndian(input);
            byte[] seed = CryptoUtilities.readByteArrayFromInputStream(input, 20);
            byte[] p = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenP + 7) / 8);
            byte[] q = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenQ + 7) / 8);
            byte[] g = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenP + 7) / 8);
            CryptoUtilities.readByteArrayFromInputStream(input, (bitlenJ + 7) / 8);
            byte[] y = CryptoUtilities.readByteArrayFromInputStream(input, (bitlenP + 7) / 8);
            DHCryptoSystem cryptoSystem;
            if (bitlenQ == 0) {
               CryptoUtilities.flipArray(p);
               CryptoUtilities.flipArray(g);
               cryptoSystem = (DHCryptoSystem)(new Object(p, g));
            } else {
               CryptoUtilities.flipArray(p);
               CryptoUtilities.flipArray(g);
               CryptoUtilities.flipArray(q);
               cryptoSystem = (DHCryptoSystem)(new Object(p, q, g));
            }

            return (PublicKey)(new Object(cryptoSystem, CryptoUtilities.flipArray(y)));
         default:
            throw new Object();
      }
   }

   @Override
   public final String getEncodingAlgorithm() {
      return "MSCAPI";
   }

   @Override
   public final String[] getKeyAlgorithms() {
      return new String[]{"5", "1", "2"};
   }
}
