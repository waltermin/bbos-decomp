package net.rim.device.internal.synchronization.ota.util;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Persistable;
import net.rim.vm.Persistence;

public final class ReferenceGenerator implements Persistable {
   private short _positiveRefId;
   private short _negativeRefId;
   private static final long GUID = -8042806557911297349L;
   private static ReferenceGenerator theReferenceGenerator;

   public static final ReferenceGenerator getSingletonInstance() {
      if (theReferenceGenerator == null) {
         ApplicationRegistry theApplicationRegistry = ApplicationRegistry.getApplicationRegistry();
         theReferenceGenerator = (ReferenceGenerator)theApplicationRegistry.getOrWaitFor(-8042806557911297349L);
         if (theReferenceGenerator == null) {
            PersistentObject xPersistentObject = RIMPersistentStore.getPersistentObject(-8042806557911297349L);
            theReferenceGenerator = (ReferenceGenerator)xPersistentObject.getContents();
            if (theReferenceGenerator == null) {
               theReferenceGenerator = new ReferenceGenerator();
               xPersistentObject.setContents(theReferenceGenerator, 51);
               xPersistentObject.commit();
            }

            theApplicationRegistry.put(-8042806557911297349L, theReferenceGenerator);
         }
      }

      return theReferenceGenerator;
   }

   private ReferenceGenerator() {
   }

   public final int getPositiveRefID() {
      if (this._positiveRefId > 32767) {
         this._positiveRefId = 1;
      } else {
         this._positiveRefId++;
      }

      Persistence.commit(this, true);
      return this._positiveRefId;
   }

   public final int getNegativeRefID() {
      if (this._negativeRefId < -32768) {
         this._negativeRefId = -1;
      } else {
         this._negativeRefId--;
      }

      Persistence.commit(this, true);
      return this._negativeRefId;
   }
}
