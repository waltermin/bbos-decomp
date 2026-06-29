package net.rim.device.apps.internal.browser.store;

import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.SyncBuffer;

final class BrowserURLSyncObject implements SyncObject, ConversionProvider {
   private String _url;

   public BrowserURLSyncObject(String url) {
      this._url = url;
   }

   public final String getURL() {
      return this._url;
   }

   @Override
   public final int getUID() {
      return this._url.hashCode();
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (!(target instanceof Object)) {
         return false;
      }

      SyncBuffer syncBuffer = (SyncBuffer)target;
      DataBuffer dataBuffer = (DataBuffer)(new Object(false));
      dataBuffer.setLength(0);
      dataBuffer.writeCompressedInt(BrowserURLCollection.SYNC_VERSION);

      try {
         dataBuffer.writeUTF(this._url);
      } finally {
         ;
      }

      syncBuffer.addBytes(25, dataBuffer.toArray());
      return true;
   }
}
