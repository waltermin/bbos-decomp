package net.rim.device.api.synchronization;

import net.rim.device.api.util.IntHashtable;

public final class SyncCollectionSchema {
   private int _recordTypeTag;
   private int _defaultRecordType;
   private IntHashtable _keyFieldIds = new IntHashtable(1);

   public final int getRecordTypeTag() {
      return this._recordTypeTag;
   }

   public final void setRecordTypeTag(int recordTypeTag) {
      this._recordTypeTag = recordTypeTag;
   }

   public final int getDefaultRecordType() {
      return this._defaultRecordType;
   }

   public final void setDefaultRecordType(int defaultRecordType) {
      if (defaultRecordType == 0) {
         throw new IllegalArgumentException();
      }

      this._defaultRecordType = defaultRecordType;
   }

   public final int[] getRecordTypes() {
      int numRecordTypes = this._keyFieldIds.size();
      if (numRecordTypes <= 0) {
         return new int[0];
      }

      int[] recordTypes = new int[numRecordTypes];
      this._keyFieldIds.keysToArray(recordTypes);
      return recordTypes;
   }

   public final int[] getKeyFieldIds(int recordType) {
      return (int[])this._keyFieldIds.get(recordType);
   }

   public final void setKeyFieldIds(int recordType, int[] keyFieldIds) {
      if (recordType == 0) {
         throw new IllegalArgumentException();
      }

      if (keyFieldIds != null && keyFieldIds.length != 0) {
         this._keyFieldIds.put(recordType, keyFieldIds);
      } else {
         this._keyFieldIds.remove(recordType);
      }
   }
}
