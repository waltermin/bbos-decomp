package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.util.Arrays;

public class AssociatedDataKeyStoreIndex implements KeyStoreIndex {
   private long _association;

   public AssociatedDataKeyStoreIndex(long association) {
      this._association = association;
   }

   @Override
   public void addToIndex(KeyStoreData data, KeyStoreDataMap dataMap) {
      byte[][] associatedData = data.getAssociatedData(this._association);
      if (associatedData != null) {
         int numAliases = associatedData.length;

         for (int i = 0; i < numAliases; i++) {
            dataMap.add(HashCodeCalculator.getCRC32(associatedData[i]), data);
         }
      }
   }

   @Override
   public int getHash(Object target) {
      if (!(target instanceof byte[])) {
         throw new IllegalArgumentException();
      } else {
         return HashCodeCalculator.getCRC32((byte[])target);
      }
   }

   @Override
   public boolean matches(KeyStoreData data, Object target) {
      if (target instanceof byte[]) {
         byte[] targetAssociation = (byte[])target;
         byte[][] association = data.getAssociatedData(this._association);
         if (association != null) {
            for (int i = 0; i < association.length; i++) {
               if (Arrays.equals(targetAssociation, association[i])) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   @Override
   public long getID() {
      return this._association;
   }
}
