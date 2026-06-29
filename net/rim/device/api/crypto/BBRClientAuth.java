package net.rim.device.api.crypto;

import net.rim.device.api.util.Arrays;

public class BBRClientAuth {
   private byte[] _h = new byte[66];
   private byte[] _RD = new byte[67];
   private byte[] _RB;
   private byte[] _rD = new byte[66];
   private byte[] _eB;
   private static final String CRYPTO_SYSTEM = "EC521R1";
   private static final int COMPRESSED_PUBLIC_KEY_LENGTH = 67;
   private static final int PRIVATE_KEY_LENGTH = 66;

   public BBRClientAuth(byte[] secret) {
      NativeEC.generateKeyPair("EC521R1", this._rD, this._RD);
      this._eB = RandomSource.getBytes(66);
      this._eB[0] = 0;
      Digest digest = new SHA512Digest();
      digest.update(secret);
      byte[] h = digest.getDigest();
      System.arraycopy(h, 0, this._h, 66 - h.length, h.length);
   }

   public byte[] getRD() {
      return this._RD;
   }

   public byte[] getYD(byte[] RB, byte[] eD) {
      if (RB != null && RB.length == 67 && eD != null && eD.length == 66) {
         while (CryptoByteArrayArithmetic.isZero(RB) || Arrays.equals(RB, this._RD)) {
            NativeEC.generatePublicKey("EC521R1", RandomSource.getBytes(66), RB);
         }

         this._RB = RB;

         while (CryptoByteArrayArithmetic.isZero(eD)) {
            eD = RandomSource.getBytes(66);
         }

         byte[] groupOrder = new byte[66];
         NativeEC.getGroupOrder("EC521R1", groupOrder);
         byte[] eDrD = new byte[66];
         CryptoByteArrayArithmetic.multiply(eD, this._rD, groupOrder, eDrD);
         byte[] yD = new byte[66];
         CryptoByteArrayArithmetic.subtract(this._h, eDrD, groupOrder, yD);
         return yD;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public byte[] getEB() {
      return this._eB;
   }

   public boolean verify(byte[] yB) throws VerificationException {
      if (yB == null || yB.length != 66) {
         throw new IllegalArgumentException();
      }

      if (CryptoByteArrayArithmetic.isZero(yB)) {
         throw new VerificationException("yB was zero");
      }

      byte[] P = new byte[67];
      NativeEC.getBasePoint("EC521R1", P);
      byte[] addResult = new byte[67];
      NativeEC.multiplyAndAdd("EC521R1", yB, this._eB, this._RB, addResult);
      byte[] hP = new byte[67];
      NativeEC.multiply("EC521R1", this._h, P, hP);
      return Arrays.equals(addResult, hP);
   }
}
