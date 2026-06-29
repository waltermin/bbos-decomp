package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public class PKCS12KDFPseudoRandomSource extends AbstractPseudoRandomSource implements PseudoRandomSource {
   private byte[] _salt;
   private int _iterationCount;
   private byte _ID;
   private byte[] _password;
   private Digest _digest;
   private int _u;
   private int _v;

   public PKCS12KDFPseudoRandomSource(byte[] password, byte[] salt, int iterationCount, byte ID) {
      this(password, salt, iterationCount, ID, (Digest)(new Object()));
   }

   public PKCS12KDFPseudoRandomSource(byte[] password, byte[] salt, int iterationCount, byte ID, Digest digest) {
      this._salt = salt;
      this._password = password;
      if (iterationCount > 0) {
         this._iterationCount = iterationCount;
         if (ID >= 1 && ID <= 3) {
            this._ID = ID;
            if (digest == null) {
               this._digest = (Digest)(new Object());
            } else {
               this._digest = digest;
            }

            if (this._digest instanceof Object) {
               this._u = 160;
               this._v = 512;
            } else {
               if (!(this._digest instanceof Object) && !(this._digest instanceof Object)) {
                  throw new Object();
               }

               this._u = 128;
               this._v = 512;
            }
         } else {
            throw new Object();
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public String getAlgorithm() {
      return "PKCS12KDF";
   }

   @Override
   public void xorBytes(byte[] buffer, int bufferOffset, int length) {
      if (buffer != null && bufferOffset >= 0 && length >= 0 && buffer.length - length >= bufferOffset) {
         int vBytes = this._v / 8;
         int diversifierByteLength = vBytes;
         byte[] diversifierBytes = new byte[diversifierByteLength];
         Arrays.fill(diversifierBytes, this._ID, 0, diversifierByteLength);
         int saltByteLength = this._salt == null ? 0 : this._salt.length;
         int saltBitLength = saltByteLength * 8;
         int SByteLength = this._v * ((saltBitLength + this._v - 1) / this._v) / 8;
         int passwordByteLength = this._password == null ? 0 : this._password.length;
         int passwordBitLength = passwordByteLength * 8;
         int PByteLength = this._v * ((passwordBitLength + this._v - 1) / this._v) / 8;
         byte[] IBytes = new byte[SByteLength + PByteLength];
         int offset = 0;
         int tempLength = 0;

         while (offset < SByteLength) {
            tempLength = Math.min(SByteLength - offset, saltByteLength);
            System.arraycopy(this._salt, 0, IBytes, offset, tempLength);
            offset += tempLength;
         }

         offset = 0;
         int var29 = 0;

         while (offset < PByteLength) {
            var29 = Math.min(PByteLength - offset, passwordByteLength);
            System.arraycopy(this._password, 0, IBytes, SByteLength + offset, var29);
            offset += var29;
         }

         int c = (length * 8 + this._u - 1) / this._u;
         byte[][][] A = new byte[c][][];
         int count = 0;
         byte[] B = new byte[vBytes];
         int k = (SByteLength * 8 + PByteLength * 8) / this._v;

         for (int i = 0; i < c; i++) {
            count = 1;
            A[i] = (byte[][])Arrays.copy(diversifierBytes);
            Arrays.append((byte[])A[i], IBytes);
            this._digest.reset();
            this._digest.update((byte[])A[i]);
            A[i] = (byte[][])this._digest.getDigest();

            while (count++ < this._iterationCount) {
               this._digest.update((byte[])A[i]);
               this._digest.getDigest((byte[])A[i], 0);
            }

            int ALength = A[i].length;
            offset = 0;
            int var31 = 0;

            while (offset < vBytes) {
               var31 = Math.min(vBytes - offset, ALength);
               System.arraycopy(A[i], 0, B, offset, var31);
               offset += var31;
            }

            int var26 = 0;

            for (int m = 0; m < k; m++) {
               var26 = m * vBytes;
               CryptoByteArrayArithmetic.add(IBytes, var26, vBytes, B, 0, vBytes, this._v, IBytes, var26, vBytes);
               CryptoByteArrayArithmetic.increment(IBytes, var26, vBytes, this._v, IBytes, var26, vBytes);
            }
         }

         byte[] finalA = new byte[0];

         for (int i = 0; i < c; i++) {
            Arrays.append(finalA, (byte[])A[i]);
         }

         for (int i = 0; i < length; i++) {
            buffer[bufferOffset + i] = (byte)(buffer[bufferOffset + i] ^ finalA[i]);
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public int getAvailable() {
      return Integer.MAX_VALUE;
   }

   @Override
   public int getMaxAvailable() {
      return Integer.MAX_VALUE;
   }

   public static void selfTest() {
      byte ID = 3;
      int iter = 1000;
      byte[] pass = new byte[]{0, 113, 0, 117, 0, 101, 0, 101, 0, 103, 0, 0};
      byte[] sa = new byte[]{38, 50, 22, -4, -62, -6, -77, 28};
      byte[] result = new byte[]{94, -60, -57, -88, 13, -10, 82, 41, 76, 57, 37, -74, 72, -102, 122, -72, 87, -56, 52, 118};

      try {
         if (ID == 3) {
            return;
         }

         byte[] key = Arrays.copy(SelfTestData.RANDOM_DATA);
         PKCS12KDFPseudoRandomSource source = new PKCS12KDFPseudoRandomSource(sa, pass, iter, ID);
         byte[] data = source.getBytes(32);
         if (Arrays.equals(data, result)) {
            System.out.println(" SUCCESS ");
            return;
         }

         if (ID == 3) {
            return;
         }
      } finally {
         throw new Object();
      }

      throw new Object();
   }

   static {
      long ID_TEST_PRS_PKCS12KDF = -492745862243622126L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      Object instance = appRegistry.getOrWaitFor(ID_TEST_PRS_PKCS12KDF);
      if (instance == null) {
         selfTest();
         appRegistry.put(ID_TEST_PRS_PKCS12KDF, appRegistry);
      }
   }
}
