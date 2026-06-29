package net.rim.device.apps.internal.secureemail.encodings.smime;

import java.util.Vector;
import net.rim.device.api.crypto.cms.CMSReceiptData;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;

public final class SignedReceiptCache {
   private static final long ID;
   private static PersistentObject _persistentObject = RIMPersistentStore.getPersistentObject(8463082725501068133L);
   private static Vector _receipts;

   public static final void addReceipts(CMSReceiptData[] receiptData) {
      if (receiptData != null && receiptData.length != 0) {
         int numReceipts = receiptData.length;

         for (int i = 0; i < numReceipts; i++) {
            _receipts.addElement(receiptData[i]);
            if (_receipts.size() > 50) {
               _receipts.removeElementAt(0);
            }
         }

         _persistentObject.commit();
      }
   }

   public static final CMSReceiptData[] getReceipts() {
      CMSReceiptData[] result = new Object[_receipts.size()];
      _receipts.copyInto(result);
      return result;
   }

   static {
      synchronized (_persistentObject) {
         if (_persistentObject.getContents() == null) {
            _persistentObject.setContents(new Object(), 4801362);
            _persistentObject.commit();
         }
      }

      _receipts = (Vector)_persistentObject.getContents();
   }
}
