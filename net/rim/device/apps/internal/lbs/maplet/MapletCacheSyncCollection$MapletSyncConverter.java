package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;

final class MapletCacheSyncCollection$MapletSyncConverter implements SyncConverter {
   private final MapletCacheSyncCollection this$0;

   MapletCacheSyncCollection$MapletSyncConverter(MapletCacheSyncCollection this$0) {
      this.this$0 = this$0;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean convert(SyncObject object, DataBuffer db, int version) {
      try {
         Maplet maplet = (Maplet)object;
         maplet.save(db);
         return true;
      } catch (Throwable var6) {
         this.this$0.log(e);
         return false;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final SyncObject convert(DataBuffer db, int version, int uid) {
      if (version == 1 || version >= 1 && version <= 1) {
         try {
            Maplet maplet = new Maplet((byte)0);
            maplet.load(db);
            return maplet;
         } catch (Throwable var6) {
            this.this$0.log(e);
            return null;
         }
      } else {
         return null;
      }
   }
}
