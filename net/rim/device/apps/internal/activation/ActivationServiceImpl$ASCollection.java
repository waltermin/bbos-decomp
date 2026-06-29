package net.rim.device.apps.internal.activation;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.provisioning.ActivationService;

public final class ActivationServiceImpl$ASCollection implements SyncCollection, SyncConverter, SyncObject {
   private static final int CURRENT_VERSION = 3;
   private static final int THE_UID = 1;
   private static final int TAG_KEY_VALUE = 16;
   private static final int TAG_COMPLETED = 17;
   private static final int TAG_ACTIVATION_RECORD = 18;

   @Override
   public final SyncObject[] getSyncObjects() {
      SyncObject[] objs = new Object[1];
      objs[0] = this;
      return objs;
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      return true;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      return true;
   }

   @Override
   public final int getUID() {
      return 1;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      return true;
   }

   @Override
   public final int getSyncVersion() {
      return 3;
   }

   @Override
   public final String getSyncName() {
      return "Activated Services";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return this.getSyncName();
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public final void beginTransaction() {
   }

   @Override
   public final void endTransaction() {
      ((ActivationServiceImpl)ActivationService.getInstance()).iconRefresh();
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return true;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      return null;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return false;
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (version != 3) {
         return false;
      } else if (object instanceof ActivationServiceImpl$ASCollection) {
         this.serializeActivatedServicesHashtable(buffer);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final SyncObject convert(DataBuffer data, int version, int UID) {
      if (UID != 1) {
         return null;
      }

      try {
         if (version == 3) {
            while (true) {
               int type;
               try {
                  type = ConverterUtilities.getType(data);
               } finally {
                  ;
               }

               switch (type) {
                  case 18:
                     this.readActivationRecord(data);
                     break;
                  default:
                     ConverterUtilities.skipField(data);
               }
            }
         }
      } finally {
         return null;
      }

      return null;
   }

   @Override
   public final int getSyncObjectCount() {
      return 1;
   }

   ActivationServiceImpl$ASCollection() {
   }

   private final void readActivationRecord(DataBuffer buffer) {
      try {
         long[] activationRecord = ConverterUtilities.readLongArray(buffer);
         long sid = -1;
         if (activationRecord.length == 2) {
            sid = activationRecord[0];
            if (sid != -1) {
               ActivationServiceImpl.access$000(activationRecord[1], sid);
               return;
            }
         }
      } finally {
         return;
      }
   }

   private final void serializeActivatedServicesHashtable(DataBuffer buffer) {
      long[] activatedServices = ActivationService.getActivatedServices();

      for (int i = activatedServices.length - 1; i >= 0; i--) {
         long activated = ActivationService.getLastSuccessfulActivationDate(activatedServices[i]);
         if (activated >= 0) {
            ConverterUtilities.writeLongArray(buffer, 18, new long[]{activatedServices[i], activated});
         }
      }
   }
}
