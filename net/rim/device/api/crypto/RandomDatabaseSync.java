package net.rim.device.api.crypto;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.synchronization.AlwaysSyncCollection;
import net.rim.device.internal.synchronization.NoProtectedContentInCollection;

final class RandomDatabaseSync implements SyncCollection, SyncConverter, AlwaysSyncCollection, NoProtectedContentInCollection {
   private String _name;
   private static final int RANDOM_LENGTH = 1024;
   private static final int RANDOM_DATA_TYPE = 1;
   private static final int UID = 1;

   public RandomDatabaseSync(String name) {
      this._name = name;
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      if (!(object instanceof RandomDatabaseSync$RandomSync)) {
         return false;
      }

      RandomDatabaseSync$RandomSync randomSync = (RandomDatabaseSync$RandomSync)object;
      RandomSource.add(randomSync.getData());
      return true;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return true;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      return true;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      return true;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      return new SyncObject[]{new RandomDatabaseSync$RandomSync()};
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      return uid == 1 ? new RandomDatabaseSync$RandomSync() : null;
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return false;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final int getSyncObjectCount() {
      return 1;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final String getSyncName() {
      return this._name;
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
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
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (!(object instanceof RandomDatabaseSync$RandomSync)) {
         return false;
      }

      RandomDatabaseSync$RandomSync randomSync = (RandomDatabaseSync$RandomSync)object;
      ConverterUtilities.writeByteArray(buffer, 1, randomSync.getData());
      return true;
   }

   @Override
   public final SyncObject convert(DataBuffer buffer, int version, int uid) {
      try {
         if (ConverterUtilities.findType(buffer, 1)) {
            byte[] randomData = ConverterUtilities.readByteArray(buffer);
            return new RandomDatabaseSync$RandomSync(randomData);
         }
      } finally {
         return null;
      }

      return null;
   }
}
