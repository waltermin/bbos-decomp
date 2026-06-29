package net.rim.wica.runtime.authentication.protocols;

import net.rim.device.api.crypto.DESEncryptorEngine;
import net.rim.device.api.crypto.DESKey;

public final class NTLM {
   private static final String _LMPWD = "KGS!@#$%";

   public static final byte[] getLMResponse(String password, byte[] challenge) {
      byte[] lmHash = getLMHash(password);
      return getLMResponse(lmHash, challenge);
   }

   public static final byte[] getLMResponse(byte[] lmHash, byte[] challenge) {
      byte[] lmResponse = new byte[24];
      byte[] keyBytes = new byte[21];
      System.arraycopy(lmHash, 0, keyBytes, 0, 16);
      DESEncryptorEngine lowEncryptor = new DESEncryptorEngine(createDESKey(keyBytes, 0));
      DESEncryptorEngine middleEncryptor = new DESEncryptorEngine(createDESKey(keyBytes, 7));
      DESEncryptorEngine highEncryptor = new DESEncryptorEngine(createDESKey(keyBytes, 14));
      lowEncryptor.encrypt(challenge, 0, lmResponse, 0);
      middleEncryptor.encrypt(challenge, 0, lmResponse, 8);
      highEncryptor.encrypt(challenge, 0, lmResponse, 16);
      return lmResponse;
   }

   public static final byte[] getLMHash(String password) {
      byte[] lmHash = new byte[16];
      byte[] keyBytes = new byte[14];
      byte[] magicConstant = "KGS!@#$%".getBytes("US-ASCII");
      byte[] oemPassword = password.toUpperCase().getBytes("US-ASCII");
      int length = Math.min(oemPassword.length, 14);
      System.arraycopy(oemPassword, 0, keyBytes, 0, length);
      DESEncryptorEngine lowEncryptor = new DESEncryptorEngine(createDESKey(keyBytes, 0));
      DESEncryptorEngine highEncryptor = new DESEncryptorEngine(createDESKey(keyBytes, 7));
      lowEncryptor.encrypt(magicConstant, 0, lmHash, 0);
      highEncryptor.encrypt(magicConstant, 0, lmHash, 8);
      return lmHash;
   }

   private static final DESKey createDESKey(byte[] bytes, int offset) {
      byte[] keyBytes = new byte[7];
      System.arraycopy(bytes, offset, keyBytes, 0, 7);
      byte[] material = new byte[]{
         keyBytes[0],
         (byte)(keyBytes[0] << 7 | (keyBytes[1] & 255) >>> 1),
         (byte)(keyBytes[1] << 6 | (keyBytes[2] & 255) >>> 2),
         (byte)(keyBytes[2] << 5 | (keyBytes[3] & 255) >>> 3),
         (byte)(keyBytes[3] << 4 | (keyBytes[4] & 255) >>> 4),
         (byte)(keyBytes[4] << 3 | (keyBytes[5] & 255) >>> 5),
         (byte)(keyBytes[5] << 2 | (keyBytes[6] & 255) >>> 6),
         (byte)(keyBytes[6] << 1)
      };
      return new DESKey(material);
   }
}
