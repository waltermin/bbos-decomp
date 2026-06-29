package net.rim.device.internal.synchronization.ota.api;

import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.Persistable;

public final class SyncAgentUrl implements Persistable {
   private boolean _dirty;
   private int _hashCode;
   private String[] _urlItems = new String[3];
   private byte _version;
   private boolean _externalToSync;
   private static final byte SID = 0;
   private static final byte DATASOURCENAME = 1;
   private static final byte DATABASENAME = 2;

   public SyncAgentUrl() {
   }

   public SyncAgentUrl(long sid, String aDataSourceName, String aDatabaseName) {
      this.setSid(sid);
      this.setDataSourceName(aDataSourceName);
      this.setDatabaseName(aDatabaseName);
   }

   public SyncAgentUrl(long sid, String aDataSourceName, String aDatabaseName, boolean externalToSync) {
      this(sid, aDataSourceName, aDatabaseName);
      this.setExternalToSync(externalToSync);
   }

   public final void setVersion(int ver) {
      this._version = (byte)ver;
   }

   public final int getVersion() {
      return this._version;
   }

   public final void setExternalToSync(boolean externalToSync) {
      this._externalToSync = externalToSync;
   }

   public final boolean getExternalToSync() {
      return this._externalToSync;
   }

   public final void setSid(long sid) {
      this._urlItems[0] = String.valueOf(sid);
      this._dirty = true;
   }

   public final long getSid() {
      return Long.parseLong(this._urlItems[0]);
   }

   public final void setDataSourceName(String aDataSourceName) {
      this._urlItems[1] = aDataSourceName.trim();
      this._dirty = true;
   }

   public final String getDataSourceName() {
      return this._urlItems[1];
   }

   public final void setDatabaseName(String aDatabaseName) {
      this._urlItems[2] = aDatabaseName.trim();
      this._dirty = true;
   }

   public final String getDatabaseName() {
      return this._urlItems[2];
   }

   @Override
   public final boolean equals(Object anObject) {
      if (this == anObject) {
         return true;
      } else {
         return anObject instanceof SyncAgentUrl ? this.hashCode() == anObject.hashCode() : false;
      }
   }

   @Override
   public final int hashCode() {
      if (this._dirty) {
         this._hashCode = -1;

         for (int xIndex = 0; xIndex < this._urlItems.length; xIndex++) {
            this._hashCode = CRC32.update(this._hashCode, this._urlItems[xIndex].getBytes());
         }

         this._dirty = false;
      }

      return this._hashCode;
   }
}
