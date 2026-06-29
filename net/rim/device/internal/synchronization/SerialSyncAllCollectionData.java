package net.rim.device.internal.synchronization;

import net.rim.device.api.synchronization.MultiServiceSyncCollection;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;

public class SerialSyncAllCollectionData extends SerialSyncCollectionData {
   private SyncAllCollection _sac;
   private static final byte COLLECTION_SID = 2;
   private static final byte RECORD_UID = 5;
   private static final byte RECORD_DATA = 10;
   private static final byte NULL_TERMINATOR = 0;

   public SerialSyncAllCollectionData(SyncAllCollection syncAllCollection) {
      super(syncAllCollection);
      this._sac = syncAllCollection;
   }

   @Override
   public boolean convertFromSyncObject(SyncObject object, DataBuffer buffer, int version) {
      boolean result = false;
      if (this._sac != null && object instanceof SyncAllCollectionSyncObject) {
         SyncAllCollectionSyncObject sacSyncObject = (SyncAllCollectionSyncObject)object;
         long sid = sacSyncObject.getSid();
         MultiServiceSyncCollection mssc = this._sac.getSyncCollectionBySid(sid);
         if (mssc != null) {
            SyncConverter c = mssc.getSyncConverter();
            if (c != null) {
               SyncObject so = sacSyncObject.getContainedSyncObject();
               DataBuffer recordData = (DataBuffer)(new Object());
               recordData.setBigEndian(false);
               result = c.convert(so, recordData, version);
               if (!result) {
                  SerialSyncLogger.logError("Error converting SyncObject to DataBuffer");
               }

               buffer.writeShort(8);
               buffer.writeByte(2);
               buffer.writeLong(sacSyncObject.getSid());
               buffer.writeShort(4);
               buffer.writeByte(5);
               buffer.writeInt(so.getUID());
               recordData.rewind();
               buffer.writeShort(recordData.getLength());
               buffer.writeByte(10);
               buffer.write(recordData, recordData.getLength());
            }
         }
      }

      return result;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public int convertToSyncObjectAndAdd(DataBuffer buffer, int version, int uniqueId, int dirty, SerialSyncDaemon$UidWrapper uidWrapper) {
      MultiServiceSyncCollection mssc = null;
      long sid = -1;
      int uid = 0;
      boolean sidFound = false;
      DataBuffer recordDataBuffer = null;
      if (this._sac == null) {
         return 2;
      }

      while (true) {
         boolean var17 = false /* VF: Semaphore variable */;

         try {
            var17 = true;
            if (buffer.eof()) {
               var17 = false;
               break;
            }

            short length = buffer.readShort();
            byte tag = buffer.readByte();
            switch (tag) {
               case 2:
                  if (length == 8) {
                     sid = buffer.readLong();
                     sidFound = true;
                  } else {
                     buffer.skipBytes(length);
                  }
                  break;
               case 5:
                  if (length == 4) {
                     uid = buffer.readInt();
                  } else {
                     buffer.skipBytes(length);
                  }
                  break;
               case 10:
                  byte[] recordData = new byte[length];
                  buffer.readFully(recordData);
                  recordDataBuffer = (DataBuffer)(new Object(recordData, 0, length, false));
                  break;
               default:
                  buffer.skipBytes(length);
            }
         } finally {
            if (var17) {
               SerialSyncLogger.logError("Converting record data: End of DataBuffer unexpectedly reached.");
               return 2;
            }
         }
      }

      if (sidFound && recordDataBuffer != null) {
         mssc = this._sac.getSyncCollectionBySid(sid);
         if (mssc == null) {
            mssc = this._sac.getDefaultCollection();
            if (mssc == null) {
               return 2;
            }
         }

         if (!SerialSyncDaemon.isWritable(mssc)) {
            return 2;
         }

         SyncConverter c = mssc.getSyncConverter();
         if (c == null) {
            return 2;
         }

         SyncObject so = c.convert(recordDataBuffer, version, uid);
         if (so != null && mssc.addSyncObject(so)) {
            uidWrapper.setUid(so.getUID());
            if (dirty == 0) {
               mssc.clearSyncObjectDirty(so);
            }
         }
      }

      return 0;
   }

   @Override
   public void endTransaction() {
      this._sac.endTransaction();
   }

   @Override
   public void beginTransaction() {
      this._sac.beginTransaction();
   }

   @Override
   public void collectionUpdatedSerially() {
      this._sac.collectionUpdatedSerially();
   }

   @Override
   public void cleanCollection() {
      this._sac.cleanAllCollections();
   }

   @Override
   public void dispose() {
   }
}
