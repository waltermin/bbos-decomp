package net.rim.device.internal.synchronization.ota.service;

import net.rim.device.internal.synchronization.ota.api.Logger;

public class DataSourceDatabaseFields$UnmappedTagException extends Exception {
   private int _tag;
   private int _occurCount;

   public DataSourceDatabaseFields$UnmappedTagException(int tag, int occurCount) {
      this._tag = tag;
      this._occurCount = occurCount;
   }

   public void log(String collection) {
      Logger.logUnmappedTagError(this._tag, this._occurCount, collection);
   }
}
