package net.rim.device.apps.internal.browser.store;

import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.vm.WeakReference;

final class BrowserFolderConverter implements SyncConverter {
   public static final int VERSION_SUPPORTED = 1;
   private static WeakReference _tmpWriteDataBufferWR = new WeakReference(null);
   private static WeakReference _tmpReadDataBufferWR = new WeakReference(null);
   private static int FOLDER_FIELD_ID = 0;
   private static BrowserFolderSyncObject _returnSyncObject = new BrowserFolderSyncObject();

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (object == null) {
         return false;
      }

      BrowserFolderSyncObject browserSyncObject = (BrowserFolderSyncObject)object;
      SyncBuffer syncBuffer = new SyncBuffer(buffer, version, browserSyncObject.getUID());
      DataBuffer _tmpWriteDataBuffer = WeakReferenceUtilities.getDataBuffer(_tmpWriteDataBufferWR, false);
      _tmpWriteDataBuffer.setLength(0);

      try {
         _tmpWriteDataBuffer.writeCompressedInt(browserSyncObject.getParentFolderUID());
         _tmpWriteDataBuffer.writeUTF(browserSyncObject.getFriendlyName());
      } finally {
         ;
      }

      syncBuffer.addBytes(FOLDER_FIELD_ID, _tmpWriteDataBuffer.toArray());
      return true;
   }

   @Override
   public final SyncObject convert(DataBuffer dataBuffer, int version, int uid) {
      if (version != 1) {
         return null;
      }

      SyncBuffer syncBuffer = new SyncBuffer(dataBuffer, version, uid);
      if (syncBuffer.getFieldType() != FOLDER_FIELD_ID) {
         return null;
      }

      int parentFolderUID = 0;
      String friendlyName = null;

      try {
         int bufLen = dataBuffer.readShort();
         dataBuffer.readByte();
         DataBuffer _tmpReadDataBuffer = WeakReferenceUtilities.getDataBuffer(_tmpReadDataBufferWR, false);
         _tmpReadDataBuffer.setData(dataBuffer.getArray(), dataBuffer.getArrayPosition(), bufLen);
         parentFolderUID = _tmpReadDataBuffer.readCompressedInt();
         friendlyName = _tmpReadDataBuffer.readUTF();
      } finally {
         ;
      }

      _returnSyncObject.setParams(uid, parentFolderUID, friendlyName);
      return _returnSyncObject;
   }
}
