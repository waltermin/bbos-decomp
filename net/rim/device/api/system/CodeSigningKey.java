package net.rim.device.api.system;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.Persistable;

public final class CodeSigningKey implements Persistable {
   private int _signerId;
   private byte[] _publicKey;
   private String _description;
   public static final int RRI_SIGNER_ID = 51;
   public static final int RRT_SIGNER_ID = 5526098;
   public static final int RCC_SIGNER_ID = 4408146;
   public static final int RCI_SIGNER_ID = 4801362;
   public static final int RCR_SIGNER_ID = 5391186;
   public static final int RBB_SIGNER_ID = 4342354;
   public static final int RBA_SIGNER_ID = 4276818;
   public static final int RATT_SIGNER_ID = 1414807890;
   private static IntHashtable _builtInKeys = ApplicationRegistry.getApplicationRegistry().getIntHashtable(491444893097860105L);

   private CodeSigningKey() {
   }

   public CodeSigningKey(String signerId, byte[] publicKey, String description) {
      this(convert(signerId), publicKey, description);
   }

   public CodeSigningKey(int signerId, byte[] publicKey, String description) {
      this._signerId = signerId;
      this._publicKey = publicKey;
      this._description = description;
   }

   public final String getSignerId() {
      return convert(this._signerId);
   }

   public final int getSignerIdAsInt() {
      return this._signerId;
   }

   public final byte[] getPublicKey() {
      return Arrays.copy(this._publicKey);
   }

   final byte[] getPublicKeyInternal() {
      return this._publicKey;
   }

   public final String getDescription() {
      return this._description;
   }

   @Override
   public final boolean equals(Object obj) {
      if (!(obj instanceof CodeSigningKey)) {
         return false;
      }

      CodeSigningKey other = (CodeSigningKey)obj;
      return this._signerId == other._signerId && Arrays.equals(this._publicKey, other._publicKey);
   }

   public static final native CodeSigningKey get(Object var0);

   public static final native CodeSigningKey get(int var0, int var1);

   public static final CodeSigningKey get(int moduleHandle, String signerId) {
      return get(moduleHandle, convert(signerId));
   }

   public static final CodeSigningKey getBuiltInKey(int signerId) {
      CodeSigningKey key = (CodeSigningKey)_builtInKeys.get(signerId);
      if (key == null) {
         key = getBuiltInKey2(signerId);
         _builtInKeys.put(signerId, key);
      }

      return key;
   }

   private static final native CodeSigningKey getBuiltInKey2(int var0);

   public static final int convert(String s) {
      if (s == null) {
         throw new IllegalArgumentException();
      }

      int length = s.length();
      if (length != 0 && length <= 4) {
         int id = 0;

         for (int i = length - 1; i >= 0; i--) {
            id <<= 8;
            id |= s.charAt(i) & 255;
         }

         return id;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static final String convert(int i) {
      byte[] b = new byte[]{(byte)i, (byte)(i >> 8), (byte)(i >> 16), (byte)(i >> 24)};
      int length = 0;

      while (length < 4 && b[length] != 0) {
         length++;
      }

      return new String(b, 0, length);
   }
}
