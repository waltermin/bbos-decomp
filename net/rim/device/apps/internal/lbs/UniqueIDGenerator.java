package net.rim.device.apps.internal.lbs;

import net.rim.device.api.synchronization.UIDGenerator;

public final class UniqueIDGenerator {
   public static final int generateID() {
      LocationDocumentCollection ldc = LocationDocumentCollection.getInstance();

      int uid;
      do {
         uid = UIDGenerator.getUID();
      } while (ldc.getSyncObject(uid) != null);

      return uid;
   }
}
