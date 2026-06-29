package net.rim.device.api.crypto.keystore;

public class LabelKeyStoreIndex implements KeyStoreIndex {
   public static final long ID = -2586671069390475682L;

   @Override
   public void addToIndex(KeyStoreData data, KeyStoreDataMap dataMap) {
      String label = data.getLabel();
      if (label != null) {
         dataMap.add(label.hashCode(), data);
      }
   }

   @Override
   public int getHash(Object target) {
      if (target instanceof Object) {
         return target.hashCode();
      } else {
         throw new Object();
      }
   }

   @Override
   public boolean matches(KeyStoreData data, Object target) {
      return !(target instanceof Object) ? false : target.equals(data.getLabel());
   }

   @Override
   public long getID() {
      return -2586671069390475682L;
   }
}
