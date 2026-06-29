package net.rim.device.cldc.io.ippp;

import net.rim.device.api.servicebook.ServiceRecord;

class SocketTransportBase$SocketTransportBaseConnection {
   SBApplicationData _applicationData;
   String _uid;
   private final SocketTransportBase this$0;

   public SocketTransportBase$SocketTransportBaseConnection(SocketTransportBase _1, ServiceRecord rec) {
      this.this$0 = _1;
      this._uid = rec.getUid();
      this._applicationData = new SBApplicationData(rec);
   }

   public Cache getCache() {
      return this.this$0._cache;
   }

   public int getCacheSize() {
      int cacheSize = 100;
      if (this._applicationData != null) {
         cacheSize = this._applicationData.getValueAsInt(3);
         if (cacheSize == 0) {
            cacheSize = 100;
         }
      }

      return cacheSize;
   }

   public int getQueueSize() {
      int queueSize = 100;
      if (this._applicationData != null) {
         queueSize = this._applicationData.getValueAsInt(4);
         if (queueSize == 0) {
            queueSize = 100;
         }
      }

      return queueSize;
   }

   public int getPendingSize() {
      int pendingSize = 10;
      if (this._applicationData != null) {
         pendingSize = this._applicationData.getValueAsInt(5);
         if (pendingSize == 0) {
            pendingSize = 10;
         }
      }

      return pendingSize;
   }

   public long getTimeout() {
      long timeout = 130000;
      if (this._applicationData != null) {
         timeout = this._applicationData.getValueAsInt(2);
         if (timeout == 0) {
            timeout = 130000;
         }
      }

      return timeout;
   }

   public int getDataSize() {
      int dataSize = 1024;
      if (this._applicationData != null) {
         dataSize = this._applicationData.getValueAsInt(1);
         if (dataSize == 0) {
            dataSize = 1024;
         }
      }

      return dataSize;
   }

   public int getIpppType() {
      int type = 0;
      if (this._applicationData != null) {
         type = this._applicationData.getValueAsInt(6);
      }

      return type;
   }

   public void update() {
      if (this._applicationData != null) {
         this._applicationData.update();
      }
   }
}
