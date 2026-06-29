package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.wica.common.metadata.component.DataComponentDef;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.metadata.internal.component.KeylessDataCollection;

class AccessInnerDataCollection extends KeylessDataCollection implements BuiltinCollection {
   protected IntHashtable _owners = new IntHashtable();
   protected IntVector _modifiedItems;
   protected IntVector _loadedItems;
   protected IntHashtable _intFieldHandlers;
   protected IntHashtable _booleanFieldHandlers;
   protected IntHashtable _longFieldHandlers;
   protected IntHashtable _objectFieldHandlers;
   protected LoadItemHelper _loadHelper;

   protected void loadItem(long _1) {
      throw null;
   }

   public void modified(long dataHandle, int field) {
      if (!this.isItemLoaded(dataHandle)) {
         this.loadItemFromDB(dataHandle);
      }

      int handle = this.getHandle(dataHandle);
      if (!this._modifiedItems.contains(handle)) {
         this._modifiedItems.addElement(handle);
      }
   }

   public void setOwnerComponent(long innerHandle, long ownerHandle, int fieldID) {
      int handle = this.getHandle(innerHandle);
      this._owners.put(handle, new AccessInnerDataCollection$OwnerField(ownerHandle, fieldID));
   }

   protected int getHandle(long dataHandle) {
      return (int)(dataHandle & 4294967295L);
   }

   @Override
   public IntHashtable getObjectFieldHandlers() {
      return this._objectFieldHandlers;
   }

   @Override
   public IntHashtable getLongFieldHandlers() {
      return this._longFieldHandlers;
   }

   @Override
   public IntHashtable getIntFieldHandlers() {
      return this._intFieldHandlers;
   }

   @Override
   public boolean isItemLoaded(long dataHandle) {
      int handle = this.getHandle(dataHandle);
      return this.isPersistable(dataHandle) || this._modifiedItems.contains(handle) || this._loadedItems.contains(handle);
   }

   @Override
   public boolean loadItemFromDB(long dataHandle) {
      boolean ignoreTransactions = false;
      if (super._transactions != null) {
         ignoreTransactions = super._transactions.getIgnoreTransactions();
         super._transactions.setIgnoreTransactions(true);
      }

      this.loadItem(dataHandle);
      if (super._transactions != null) {
         super._transactions.setIgnoreTransactions(ignoreTransactions);
      }

      return true;
   }

   @Override
   public IntHashtable getBooleanFieldHandlers() {
      return this._booleanFieldHandlers;
   }

   @Override
   public Object getDBItemFromHandle(long _1) {
      throw null;
   }

   @Override
   public boolean equals(long data1, long data2) {
      if (data1 >>> 32 != data2 >>> 32) {
         return false;
      }

      int numFields = this.getDef().getNumFields();

      for (int i = 0; i < numFields; i++) {
         switch (this.getDef().getFieldType(i)) {
            case 0:
            case 1:
            case 5:
               if (this.getIntFieldValue(data1, i) != this.getIntFieldValue(data2, i)) {
                  return false;
               }
               break;
            case 3:
            case 32768:
            case 32769:
            case 32770:
            case 32771:
            case 32772:
            case 32773:
            case 32774:
            case 32776:
               Object o1 = this.getObjectFieldValue(data1, i);
               Object o2 = this.getObjectFieldValue(data2, i);
               if (o1 != o2 && (o1 == null || !o1.equals(o2))) {
                  return false;
               }
               break;
            case 4:
            case 8:
               if (this.getLongFieldValue(data1, i) != this.getLongFieldValue(data2, i)) {
                  return false;
               }
               break;
            case 6:
               DataCollection dc = super._wiclet.getDataCollection(this.getDef().getFieldReferenceType(i));
               if (!dc.equals(this.getReferenceField(data1, i), this.getReferenceField(data2, i))) {
                  return false;
               }
         }
      }

      return true;
   }

   public AccessInnerDataCollection(WicletEx wiclet, DataComponentDef defs) {
      super(wiclet, defs);
      this.resetCache();
      this._loadHelper = new LoadItemHelper(this);
   }

   @Override
   public void setBooleanFieldValue(long dataHandle, int field, boolean value) {
      this.modified(dataHandle, field);
      super.setBooleanFieldValue(dataHandle, field, value);
   }

   @Override
   public void setDoubleFieldValue(long dataHandle, int field, double value) {
      this.modified(dataHandle, field);
      super.setDoubleFieldValue(dataHandle, field, value);
   }

   @Override
   public void setFieldValueFromObject(long dataHandle, int field, Object value) {
      this.modified(dataHandle, field);
      super.setFieldValueFromObject(dataHandle, field, value);
   }

   @Override
   public void setIntFieldValue(long dataHandle, int field, int value) {
      this.modified(dataHandle, field);
      super.setIntFieldValue(dataHandle, field, value);
   }

   @Override
   public void setLongFieldValue(long dataHandle, int field, long value) {
      this.modified(dataHandle, field);
      super.setLongFieldValue(dataHandle, field, value);
   }

   @Override
   public void setObjectFieldValue(long dataHandle, int field, Object value) {
      this.modified(dataHandle, field);
      super.setObjectFieldValue(dataHandle, field, value);
   }

   @Override
   public void setReferenceField(long dataHandle, int field, long reference) {
      this.modified(dataHandle, field);
      super.setReferenceField(dataHandle, field, reference);
   }

   @Override
   public boolean getBooleanFieldValue(long dataHandle, int field) {
      return this.isItemLoaded(dataHandle) ? super.getBooleanFieldValue(dataHandle, field) : this._loadHelper.getBooleanFieldValue(dataHandle, field);
   }

   @Override
   public int getIntFieldValue(long dataHandle, int field) {
      return this.isItemLoaded(dataHandle) ? super.getIntFieldValue(dataHandle, field) : this._loadHelper.getIntFieldValue(dataHandle, field);
   }

   @Override
   public long getLongFieldValue(long dataHandle, int field) {
      return this.isItemLoaded(dataHandle) ? super.getLongFieldValue(dataHandle, field) : this._loadHelper.getLongFieldValue(dataHandle, field);
   }

   @Override
   public Object getFieldValueAsObject(long dataHandle, int field) {
      if (this.isItemLoaded(dataHandle)) {
         return super.getFieldValueAsObject(dataHandle, field);
      }

      switch (this.getDef().getFieldType(field)) {
         case 0:
            return new Boolean(this.getBooleanFieldValue(dataHandle, field));
         case 1:
         case 5:
            return new Integer(this.getIntFieldValue(dataHandle, field));
         case 2:
            return new Double(this.getDoubleFieldValue(dataHandle, field));
         case 3:
         case 32768:
         case 32769:
         case 32770:
         case 32771:
         case 32772:
         case 32773:
         case 32774:
         case 32776:
            return this.getObjectFieldValue(dataHandle, field);
         case 4:
         case 8:
            return new Long(this.getLongFieldValue(dataHandle, field));
         case 6:
            return new Long(this.getReferenceField(dataHandle, field));
         default:
            throw new RuntimeException("Not recognized field type.");
      }
   }

   @Override
   public Object getObjectFieldValue(long dataHandle, int field) {
      Object retValue;
      if (this.isItemLoaded(dataHandle)) {
         boolean initialize = false;
         if (this.getDef().getFieldType(field) == 32774) {
            initialize = !this.isInnerVectorInitialized(dataHandle, field);
         }

         retValue = super.getObjectFieldValue(dataHandle, field);
         if (initialize) {
            this._loadHelper.initVector(retValue);
         }
      } else {
         retValue = this._loadHelper.getObjectFieldValue(dataHandle, field);
      }

      return retValue;
   }

   @Override
   public void copyFields(long copyTo, long copyFrom, boolean deep) {
      this.modified(copyTo, 0);
      super.copyFields(copyTo, copyFrom, deep);
   }

   private void resetCache() {
      this._modifiedItems = new IntVector(10, 5);
      this._loadedItems = new IntVector(10, 5);
   }
}
