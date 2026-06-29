package net.rim.device.api.system;

import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

class PersistentContent$ResetPasswordData implements Persistable {
   private byte[] _B;
   private byte[] _digestOfB;
   private byte[] _D;
   private byte[] _passwordCiphertext;
   private static final long ID;

   private PersistentContent$ResetPasswordData(byte[] B, byte[] D, byte[] passwordCiphertext) {
      this._B = B;
      this._D = D;
      this._passwordCiphertext = passwordCiphertext;
      Digest digest = new SHA1Digest();
      digest.update(B);
      this._digestOfB = digest.getDigest();
   }

   byte[] getD() {
      return this._D;
   }

   byte[] getDigestOfB() {
      return this._digestOfB;
   }

   boolean isSameB(byte[] B) {
      return Arrays.equals(this._B, B);
   }

   byte[] getPasswordCiphertext() {
      return this._passwordCiphertext;
   }

   static void createInstance(byte[] B, byte[] D, byte[] passwordCiphertext) {
      PersistentObject po = PersistentStore.getPersistentObject(-4896566637383887503L);
      po.setContents(new PersistentContent$ResetPasswordData(B, D, passwordCiphertext), 51);
   }

   static PersistentContent$ResetPasswordData getInstance() {
      PersistentObject po = PersistentStore.getPersistentObject(-4896566637383887503L);
      return (PersistentContent$ResetPasswordData)po.getContents();
   }

   static void clearInstance() {
      PersistentObject po = PersistentStore.getPersistentObject(-4896566637383887503L);
      po.setContents(null, 51);
   }
}
