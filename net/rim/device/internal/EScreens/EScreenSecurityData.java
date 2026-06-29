package net.rim.device.internal.EScreens;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.vm.Persistable;

class EScreenSecurityData implements Persistable {
   private long _unlockTime;
   private long _unlockLength;
   private boolean _allowAccess;
   public static final int FEATURE_ESCREEN_ACCESS = 0;
   private static final long ESCREEN_SECURITY_DATA = 2418345044197243008L;

   static EScreenSecurityData get(int feature) {
      PersistentObject persist = RIMPersistentStore.getPersistentObject(2418345044197243008L + feature);
      Object o = persist.getContents();
      if (!(o instanceof EScreenSecurityData)) {
         EScreenSecurityData var4 = new EScreenSecurityData();
         persist.setContents(var4, 51);
         persist.commit();
         return var4;
      } else {
         return (EScreenSecurityData)o;
      }
   }

   boolean isAccessAllowed() {
      this.checkForExpiry();
      return this._allowAccess;
   }

   void checkForExpiry() {
      if (this._allowAccess) {
         long currentTime = System.currentTimeMillis();
         if (currentTime < this._unlockTime || currentTime > this._unlockTime + this._unlockLength) {
            this._allowAccess = false;
            PersistentObject.commit(this);
         }
      }
   }

   void allowAccess(long length) {
      this._unlockTime = System.currentTimeMillis();
      this._unlockLength = length;
      this._allowAccess = true;
      PersistentObject.commit(this);
   }
}
