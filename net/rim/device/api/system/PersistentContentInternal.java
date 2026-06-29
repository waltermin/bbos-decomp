package net.rim.device.api.system;

public final class PersistentContentInternal {
   private PersistentContentInternal() {
   }

   public static final void lock() {
      PersistentContent.getInstance().lock();
   }

   public static final void unlock(String password) {
      PersistentContent.getInstance().unlock(password);
   }

   public static final void setContentProtection(String password, boolean encrypt, int strength) {
      PersistentContent.getInstance().setContentProtection(password, encrypt, strength);
   }

   public static final void changePassword(String oldPassword, String newPassword) {
      PersistentContent.getInstance().changePassword(oldPassword, newPassword);
   }

   public static final boolean doesEncryptionKeyExist() {
      return PersistentContent.getInstance().doesEncryptionKeyExist();
   }

   public static final void setContentCompression(boolean compress) {
      PersistentContent.getInstance().setContentCompression(compress);
   }

   public static final void registerPersistentContentIndicator(PersistentContentListener listener) {
      PersistentContent.getInstance().registerPersistentContentIndicator(listener);
   }

   public static final byte[] decodeByteArray(Object content, boolean firstBlockOnly, boolean keepPlaintextInRAM) {
      return content instanceof char[] ? (byte[])PersistentContent.getInstance().decode((char[])content, firstBlockOnly, keepPlaintextInRAM) : (byte[])content;
   }

   public static final byte[] getD() {
      return PersistentContent.getInstance().getD();
   }

   public static final byte[] getBChecksum() {
      return PersistentContent.getInstance().getBChecksum();
   }

   public static final void clearK() {
      PersistentContent.getInstance().clearK();
   }

   public static final boolean setK(byte[] K, byte[] checksum) {
      return PersistentContent.getInstance().setK(K, checksum);
   }
}
