package net.rim.device.apps.internal.browser.stack;

import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;

public final class CacheModel implements PersistableRIMModel, SyncObject, ConversionProvider {
   CacheNode _cacheNode;
   boolean _sticky;
   static final int AVAILABLE_OFFLINE;
   private static final int VERSION;

   public final long getCreationDate() {
      return this._cacheNode.getCreationDate();
   }

   public final long getAccessedDate() {
      return this._cacheNode.getAccessedDate();
   }

   public final CacheResult getCacheResult() {
      return this._cacheNode.getContents();
   }

   @Override
   public final int getUID() {
      return this.getCacheResult().getURLWithoutFragment().hashCode();
   }

   public final boolean isSticky() {
      return this._sticky;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (!(target instanceof Object)) {
         return false;
      }

      SyncBuffer syncBuffer = (SyncBuffer)target;
      return this.writeCacheModel(syncBuffer);
   }

   public final CacheNode getCacheNode() {
      return this._cacheNode;
   }

   public final boolean writeCacheModel(SyncBuffer syncBuffer) {
      DataBuffer dataBuffer = (DataBuffer)(new Object(false));
      dataBuffer.setLength(0);
      dataBuffer.writeByte(this.isSticky() ? (this._cacheNode.getAvailableOffline() ? 2 : 1) : 0);
      dataBuffer.writeCompressedLong(this.getCreationDate());
      dataBuffer.writeCompressedLong(this.getAccessedDate());
      dataBuffer.writeCompressedInt(1);
      syncBuffer.addBytes(22, dataBuffer.toArray());
      CacheResult cacheResult = this.getCacheResult();
      if (cacheResult == null) {
         return true;
      }

      try {
         return cacheResult.writeCacheResult(syncBuffer);
      } finally {
         ;
      }
   }

   public CacheModel(CacheNode cacheNode, boolean sticky) {
      this._cacheNode = cacheNode;
      this._sticky = sticky;
   }
}
