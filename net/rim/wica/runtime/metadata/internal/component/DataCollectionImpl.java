package net.rim.wica.runtime.metadata.internal.component;

import net.rim.device.api.collection.util.BigIntVector;
import net.rim.device.api.collection.util.BigLongVector;
import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.IntStack;
import net.rim.wica.common.metadata.component.DataComponentDef;
import net.rim.wica.common.metadata.component.DefaultValueDef;
import net.rim.wica.runtime.metadata.component.CompositeKey;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.metadata.internal.transaction.TransactionManager;
import net.rim.wica.runtime.metadata.internal.util.DataKeyHashtable;
import net.rim.wica.runtime.metadata.internal.util.PersistenceListener;
import net.rim.wica.runtime.persistence.PersDataCollectionStruct;
import net.rim.wica.runtime.persistence.WicletStore;

public class DataCollectionImpl implements DataCollection, DataOwner {
   protected IntIntHashtable _dataIndicies;
   protected BigIntVector _dataFields;
   protected BigLongVector _longData;
   protected BigVector _refObjects;
   protected BigVector _refVectors;
   protected boolean _save;
   protected int _idSource;
   protected int _numIntFields;
   protected int _numObjectFields;
   protected int _numLongFields;
   protected int _numVectorFields;
   protected int[] _fieldOffset;
   private IntStack _freeSlots;
   protected DataComponentDef _defs;
   protected WicletEx _wiclet;
   protected TransactionManager _transactions;
   protected boolean _isPersistable;
   protected PersistenceListener _persistenceListener;
   static Class class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener;
   static Class class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager;

   protected void save(PersDataCollectionStruct dcData) {
      this.defragment();
      if (this._isPersistable && this._save && this._persistenceListener.isPersistenceReadable()) {
         if (dcData == null) {
            dcData = new PersDataCollectionStruct();
         }

         dcData.setDataFields(this._dataFields);
         dcData.setLongData(this._longData);
         if (this._numVectorFields > 0) {
            for (int i = this._refVectors.size() - 1; i >= 0; i--) {
               BaseInnerVector vector = (BaseInnerVector)this._refVectors.elementAt(i);
               if (vector != null) {
                  vector.trimToSize();
               }
            }
         }

         dcData.setRefObjects(this._refObjects);
         dcData.setIdSource(this._idSource);
         WicletStore store = this._wiclet.getContext().getWicletStore();
         store.storeData(this._defs.getId(), dcData);
         this._save = false;
      }
   }

   protected void resetInternalCache() {
      this._dataIndicies.clear();
      this._dataFields.removeAll();
      if (this._longData != null) {
         this._longData.removeAll();
      }

      if (this._refObjects != null) {
         this._refObjects.removeAll();
      }

      if (this._refVectors != null) {
         this._refVectors.removeAll();
      }

      if (this._freeSlots != null) {
         this._freeSlots.removeAllElements();
      }
   }

   protected boolean loadExtraData(PersDataCollectionStruct dcData) {
      return true;
   }

   protected boolean equals(long data1, long data2, DataCollection dc2) {
      if (data1 >>> 32 != data2 >>> 32) {
         return false;
      }

      int numFields = this._defs.getNumFields();

      for (int i = 0; i < numFields; i++) {
         if (!this.equalsField(data1, data2, dc2, i)) {
            return false;
         }
      }

      return true;
   }

   public boolean equalsByFields(long data1, long data2, int[] fields) {
      if (data1 >>> 32 != data2 >>> 32) {
         return false;
      }

      int numFields = fields.length;

      for (int i = 0; i < numFields; i++) {
         if (!this.equalsField(data1, data2, this, fields[i])) {
            return false;
         }
      }

      return true;
   }

   public void copyFields(long copyTo, long copyFrom, DataCollection dcFrom, boolean deep) {
      for (int i = this._defs.getNumFields() - 1; i >= 0; i--) {
         switch (this._defs.getFieldType(i)) {
            case 0:
            case 1:
            case 5:
               this.setIntFieldValue(copyTo, i, dcFrom.getIntFieldValue(copyFrom, i));
               break;
            case 2:
               this.setDoubleFieldValue(copyTo, i, dcFrom.getDoubleFieldValue(copyFrom, i));
               break;
            case 3:
               this.setObjectFieldValue(copyTo, i, dcFrom.getObjectFieldValue(copyFrom, i));
               break;
            case 4:
            case 8:
               this.setLongFieldValue(copyTo, i, dcFrom.getLongFieldValue(copyFrom, i));
               break;
            case 6:
               DataCollection dc = this._wiclet.getDataCollection(this._defs.getFieldReferenceType(i));
               if (deep && !(dc instanceof KeyDataCollection)) {
                  long ref = this.getReferenceField(copyTo, i);
                  dc.copyFields(ref, dcFrom.getReferenceField(copyFrom, i), deep);
               } else {
                  long ref = dcFrom.getLongFieldValue(copyFrom, i);
                  long oldRef = this.getLongFieldValue(copyTo, i);
                  if (oldRef != ref) {
                     dc.addReference(ref, this._isPersistable);
                     dc.removeReference(oldRef, this._isPersistable);
                  }

                  this.setLongFieldValue(copyTo, i, ref);
               }
               break;
            case 32768:
            case 32769:
            case 32770:
            case 32771:
            case 32772:
            case 32773:
            case 32776: {
               BaseInnerVector to = (BaseInnerVector)this.getObjectFieldValue(copyTo, i);
               to.clone(dcFrom.getObjectFieldValue(copyFrom, i));
               break;
            }
            case 32774: {
               InnerDataVector to = (InnerDataVector)this.getObjectFieldValue(copyTo, i);
               to.clone(dcFrom.getObjectFieldValue(copyFrom, i), deep && !(to.getTypeHandler() instanceof KeyDataCollection));
            }
         }
      }
   }

   protected void onInitData(long handle) {
      int index = this.getSlot();
      int uid = this.getHandle(handle);
      this._dataIndicies.put(uid, index);
      this._dataFields.setElementAt(uid, this.getHandleIndex(index));
      this.newVersion(handle);
   }

   protected DataKeyHashtable buildKeyTable(int[] keyFields) {
      int numData = this._dataFields.size() / this._numIntFields;
      DataKeyHashtable keyToHandle = new DataKeyHashtable(this._wiclet, (KeyDataCollection)this, numData + (numData >> 1));
      int numberOfKeys = keyFields.length;
      if (numberOfKeys == 1) {
         for (int slot = 0; slot < numData; slot++) {
            Object key = this.getAsObject(slot, keyFields[0]);
            if (key != null) {
               keyToHandle.put(key, this._dataFields.elementAt(this.getHandleIndex(slot)));
            } else {
               int shortHandle = this._dataFields.elementAt(this.getHandleIndex(slot));
               long longHandle = (long)this._defs.getId() << 32 | 4294967295L & shortHandle;
               this.removeInternal(longHandle);
            }
         }
      } else {
         CompositeKey compositeKey = new CompositeKey(numberOfKeys);

         for (int slot = 0; slot < numData; slot++) {
            for (int i = 0; i < numberOfKeys; i++) {
               compositeKey.setPart(i, this.getAsObject(slot, keyFields[i]));
            }

            keyToHandle.put(compositeKey, this._dataFields.elementAt(this.getHandleIndex(slot)));
         }
      }

      return keyToHandle;
   }

   public void removeAll() {
      this.resetInternalCache();
      this._save = true;
   }

   protected int createSlot() {
      int numFields = this._defs.getNumFields();
      int index = this._dataFields.size() / this._numIntFields;
      DefaultValueDef defaults = this._defs.getDefaultValues();
      this._dataFields.addElement(-1);

      for (int i = 0; i < numFields; i++) {
         switch (this._defs.getFieldType(i)) {
            case 0:
            case 1:
            case 5:
               this._dataFields.addElement(defaults.getIntDefaultValue(i));
               break;
            case 2:
               this._longData.addElement(Double.doubleToLongBits(defaults.getDoubleDefaultValue(i)));
               break;
            case 3:
               this._refObjects.addElement(defaults.getObjectDefaultValue(i));
               break;
            case 4:
               long date = defaults.hasDefaultValue(i) ? defaults.getLongDefaultValue(i) : System.currentTimeMillis();
               this._longData.addElement(date);
               break;
            case 6:
               this._longData.addElement(-1);
               break;
            case 8:
               this._longData.addElement(defaults.getLongDefaultValue(i));
               break;
            case 32768:
            case 32769:
            case 32770:
            case 32771:
            case 32772:
            case 32773:
            case 32774:
            case 32776:
               this._refObjects.addElement(null);
               this._refVectors.addElement(null);
         }
      }

      return index;
   }

   protected void initSlot(int index) {
      this._dataFields.setElementAt(-1, this.getHandleIndex(index));
      int numFields = this._defs.getNumFields();
      DefaultValueDef defaults = this._defs.getDefaultValues();

      for (int i = 0; i < numFields; i++) {
         switch (this._defs.getFieldType(i)) {
            case -1:
            case 7:
               break;
            case 0:
            case 1:
            case 5:
            default:
               int intIndex = this.getIntFieldIndex(index, i);
               this._dataFields.setElementAt(defaults.getIntDefaultValue(i), intIndex);
               break;
            case 2: {
               int longIndex = this.getLongFieldIndex(index, i);
               this._longData.setElementAt(Double.doubleToLongBits(defaults.getDoubleDefaultValue(i)), longIndex);
               break;
            }
            case 3:
               int dataIndex = this.getObjectFieldIndex(index, i);
               this._refObjects.setElementAt(defaults.getObjectDefaultValue(i), dataIndex);
               break;
            case 4: {
               long date = defaults.hasDefaultValue(i) ? defaults.getLongDefaultValue(i) : System.currentTimeMillis();
               int longIndex = this.getLongFieldIndex(index, i);
               this._longData.setElementAt(date, longIndex);
               break;
            }
            case 6: {
               int longIndex = this.getLongFieldIndex(index, i);
               this._longData.setElementAt(-1, longIndex);
               break;
            }
            case 8: {
               int longIndex = this.getLongFieldIndex(index, i);
               this._longData.setElementAt(defaults.getLongDefaultValue(i), longIndex);
            }
         }
      }
   }

   protected void defragment() {
      if (this._freeSlots != null && this._freeSlots.size() > 0) {
         int minSlot = this._freeSlots.elementAt(0);

         while (this._freeSlots.size() > 0) {
            int slot = this._freeSlots.pop();
            int index = slot * this._numIntFields;

            for (int i = index + this._numIntFields - 1; i >= index; i--) {
               this._dataFields.removeElementAt(i);
            }

            index = slot * this._numLongFields;

            for (int i = index + this._numLongFields - 1; i >= index; i--) {
               this._longData.removeElementAt(i);
            }

            index = slot * this._numObjectFields;

            for (int i = index + this._numObjectFields - 1; i >= index; i--) {
               this._refObjects.removeElementAt(i);
            }

            index = slot * this._numVectorFields;

            for (int i = index + this._numVectorFields - 1; i >= index; i--) {
               this._refVectors.removeElementAt(i);
            }
         }

         this._dataFields.optimize();
         if (this._longData != null) {
            this._longData.optimize();
         }

         if (this._refObjects != null) {
            this._refObjects.optimize();
         }

         if (this._refVectors != null) {
            this._refVectors.optimize();
         }

         int numData = this._dataFields.size() / this._numIntFields;

         for (int slot = minSlot; slot < numData; slot++) {
            this._dataIndicies.put(this._dataFields.elementAt(this.getHandleIndex(slot)), slot);
         }
      }
   }

   public void freeSlot(int index) {
      this.addFreeSlot(index);
      this.cleanSlot(index);
   }

   public int getSlot() {
      int index = -1;
      if (this._freeSlots != null && this._freeSlots.size() != 0) {
         index = this._freeSlots.pop();
         this.initSlot(index);
         return index;
      } else {
         return this.createSlot();
      }
   }

   protected int getVectorFieldIndex(int index, int field) {
      return index * this._numVectorFields + this._fieldOffset[(field << 1) + 1];
   }

   protected int getObjectFieldIndex(int index, int field) {
      return index * this._numObjectFields + this._fieldOffset[field << 1];
   }

   protected int getHandle(long dataHandle) {
      return (int)(dataHandle & 4294967295L);
   }

   protected void newVersion(long dataHandle) {
      this._save = true;
   }

   @Override
   public int getIntFieldValue(long dataHandle, int field) {
      int dataIndex = this.getIntFieldIndex(this._dataIndicies.get(this.getHandle(dataHandle)), field);
      return this._dataFields.elementAt(dataIndex);
   }

   @Override
   public Object getObjectFieldValue(long dataHandle, int field) {
      int fieldType = this._defs.getFieldType(field);
      if ((32768 & fieldType) == 0) {
         int dataIndex = this.getObjectFieldIndex(this._dataIndicies.get(this.getHandle(dataHandle)), field);
         return this._refObjects.elementAt(dataIndex);
      }

      int vectorIndex = this.getVectorFieldIndex(this._dataIndicies.get(this.getHandle(dataHandle)), field);
      Object o = this._refVectors.elementAt(vectorIndex);
      if (o == null) {
         if (this._isPersistable) {
            int dataIndex = this.getObjectFieldIndex(this._dataIndicies.get(this.getHandle(dataHandle)), field);
            Object array = this._refObjects.elementAt(dataIndex);
            if (array == null) {
               o = ComponentImpl.createArray(this._wiclet, fieldType, this, dataHandle, field, this._defs, null, this._isPersistable);
               this._refObjects.setElementAt(((BaseInnerVector)o).getArrayRef(), dataIndex);
            } else {
               o = ComponentImpl.createArray(
                  this._wiclet, fieldType, this, dataHandle, field, this._defs, this._defs.getDefaultValues().getObjectDefaultValue(field), this._isPersistable
               );
               ((BaseInnerVector)o).setArrayRef(array);
            }
         } else {
            o = ComponentImpl.createArray(
               this._wiclet, fieldType, this, dataHandle, field, this._defs, this._defs.getDefaultValues().getObjectDefaultValue(field), this._isPersistable
            );
         }

         this._refVectors.setElementAt(o, vectorIndex);
      }

      return o;
   }

   @Override
   public boolean getBooleanFieldValue(long dataHandle, int field) {
      return this.getIntFieldValue(dataHandle, field) == 1;
   }

   @Override
   public double getDoubleFieldValue(long dataHandle, int field) {
      return Double.longBitsToDouble(this.getLongFieldValue(dataHandle, field));
   }

   @Override
   public long getLongFieldValue(long dataHandle, int field) {
      int dataIndex = this.getLongFieldIndex(this._dataIndicies.get(this.getHandle(dataHandle)), field);
      return this._longData.elementAt(dataIndex);
   }

   @Override
   public long getReferenceFieldAsIs(long dataHandle, int field) {
      return this.getLongFieldValue(dataHandle, field);
   }

   @Override
   public boolean isModified() {
      return this._save;
   }

   @Override
   public boolean isSupported(String feature) {
      return false;
   }

   @Override
   public void modified(long dataHandle, int field) {
      if (this._transactions != null) {
         this._transactions.modified(dataHandle, field);
      }

      if (this._persistenceListener != null) {
         this._persistenceListener.modified(dataHandle, field);
      }

      this.newVersion(dataHandle);
   }

   @Override
   public long getExistingReferenceField(long dataHandle, int field) {
      long ref = this.getLongFieldValue(dataHandle, field);
      DataCollection dc = this._wiclet.getDataCollection(this._defs.getFieldReferenceType(field));
      if (ref != -1 && !dc.contains(ref)) {
         ref = -1;
      }

      return ref;
   }

   @Override
   public long getReferenceField(long dataHandle, int field) {
      long ref = this.getLongFieldValue(dataHandle, field);
      DataCollection dc = this._wiclet.getDataCollection(this._defs.getFieldReferenceType(field));
      if (ref == -1 || !dc.contains(ref)) {
         if (dc instanceof KeyDataCollection) {
            if (ref != -1) {
               ref = -1;
               this.setLongFieldValue(dataHandle, field, ref);
            }
         } else if (!(dc instanceof KeylessDataCollection)) {
            ref = dc.create();
            this.setLongFieldValue(dataHandle, field, ref);
         } else {
            ref = ((KeylessDataCollection)dc).create(this._isPersistable);
            dc.addReference(ref, this._isPersistable);
            this.setLongFieldValue(dataHandle, field, ref);
         }
      }

      return ref;
   }

   @Override
   public void setReferenceField(long dataHandle, int field, long reference) {
      long oldRef = this.getLongFieldValue(dataHandle, field);
      if (oldRef != reference) {
         DataCollection dc = this._wiclet.getDataCollection(this._defs.getFieldReferenceType(field));
         dc.addReference(reference, this._isPersistable);
         dc.removeReference(oldRef, this._isPersistable);
      }

      this.setLongFieldValue(dataHandle, field, reference);
   }

   @Override
   public void setLongFieldValue(long dataHandle, int field, long value) {
      int handle = this.getHandle(dataHandle);
      this.modified(dataHandle, field);
      int dataIndex = this.getLongFieldIndex(this._dataIndicies.get(handle), field);
      this._longData.setElementAt(value, dataIndex);
   }

   @Override
   public void setDoubleFieldValue(long dataHandle, int field, double value) {
      this.setLongFieldValue(dataHandle, field, Double.doubleToLongBits(value));
   }

   @Override
   public void setBooleanFieldValue(long dataHandle, int field, boolean value) {
      this.setIntFieldValue(dataHandle, field, value ? 1 : 0);
   }

   @Override
   public void setIntFieldValue(long dataHandle, int field, int value) {
      int handle = this.getHandle(dataHandle);
      this.modified(dataHandle, field);
      int dataIndex = this.getIntFieldIndex(this._dataIndicies.get(handle), field);
      this._dataFields.setElementAt(value, dataIndex);
   }

   @Override
   public void setObjectFieldValue(long dataHandle, int field, Object value) {
      int handle = this.getHandle(dataHandle);
      this.modified(dataHandle, field);
      int fieldType = this._defs.getFieldType(field);
      if ((32768 & fieldType) != 0) {
         int vectorIndex = this.getVectorFieldIndex(this._dataIndicies.get(this.getHandle(dataHandle)), field);
         BaseInnerVector to;
         if (value != null) {
            to = (BaseInnerVector)this.getObjectFieldValue(dataHandle, field);
            to.clone(value);
         } else {
            to = (BaseInnerVector)this._refVectors.elementAt(vectorIndex);
            if (to != null) {
               to.removeAllElements();
            }
         }

         if (this._isPersistable && to != null) {
            to.trimToSize();
            int dataIndex = this.getObjectFieldIndex(this._dataIndicies.get(this.getHandle(dataHandle)), field);
            this._refObjects.setElementAt(to.getArrayRef(), dataIndex);
            return;
         }
      } else {
         int dataIndex = this.getObjectFieldIndex(this._dataIndicies.get(handle), field);
         this._refObjects.setElementAt(value, dataIndex);
      }
   }

   @Override
   public void remove(long dataHandle) {
      this.removeInternal(dataHandle);
   }

   @Override
   public void copyFields(long copyTo, long copyFrom, boolean deep) {
      this.copyFields(copyTo, copyFrom, this, deep);
   }

   @Override
   public long create() {
      long handle = (long)this._defs.getId() << 32 | 4294967295L & this._idSource++;
      this.onInitData(handle);
      if (this._transactions != null) {
         this._transactions.created(handle);
      }

      if (this._persistenceListener != null) {
         this._persistenceListener.created(handle);
      }

      return handle;
   }

   @Override
   public Object getFieldValueAsObject(long dataHandle, int field) {
      switch (this._defs.getFieldType(field)) {
         case 0:
            return new Object(this.getBooleanFieldValue(dataHandle, field));
         case 1:
         case 5:
            return new Object(this.getIntFieldValue(dataHandle, field));
         case 2:
            return new Object(this.getDoubleFieldValue(dataHandle, field));
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
            return new Object(this.getLongFieldValue(dataHandle, field));
         case 6:
            return new Object(this.getReferenceField(dataHandle, field));
         default:
            throw new Object("Not recognized field type.");
      }
   }

   @Override
   public void setFieldValueFromObject(long dataHandle, int field, Object value) {
      switch (this._defs.getFieldType(field)) {
         case 0:
            this.setBooleanFieldValue(dataHandle, field, value == null ? false : value);
            return;
         case 1:
         case 5:
            this.setIntFieldValue(dataHandle, field, value == null ? 0 : value);
            return;
         case 2:
            double dValue = (double)0L;
            if (!(value instanceof Object)) {
               if (!(value instanceof Object)) {
                  if (value != null) {
                     throw new Object("Invalid value type for decimal variable or field");
                  }
               } else {
                  dValue = ((Integer)value).intValue();
               }
            } else {
               dValue = value;
            }

            this.setDoubleFieldValue(dataHandle, field, dValue);
            return;
         case 3:
         case 32768:
         case 32769:
         case 32770:
         case 32771:
         case 32772:
         case 32773:
         case 32774:
         case 32776:
            this.setObjectFieldValue(dataHandle, field, value);
            return;
         case 4:
            this.setLongFieldValue(dataHandle, field, value == null ? System.currentTimeMillis() : value);
            return;
         case 6:
            this.setReferenceField(dataHandle, field, value == null ? -1 : value);
            return;
         case 8:
            long lValue = 0;
            if (!(value instanceof Object)) {
               if (!(value instanceof Object)) {
                  if (value != null) {
                     throw new Object("Invalid value type for long variable or field");
                  }
               } else {
                  lValue = ((Integer)value).intValue();
               }
            } else {
               lValue = value;
            }

            this.setLongFieldValue(dataHandle, field, lValue);
            return;
         default:
            throw new Object("Not recognized field type.");
      }
   }

   @Override
   public void restoreHandle(long handle) {
      this.onInitData(handle);
      if (this._transactions != null) {
         this._transactions.created(handle);
      }
   }

   @Override
   public boolean equals(long data1, long data2) {
      return this.equals(data1, data2, this);
   }

   @Override
   public boolean contains(long dataHandle) {
      return this._defs.getId() == (int)(dataHandle >>> 32) && this._dataIndicies.containsKey(this.getHandle(dataHandle));
   }

   @Override
   public DataComponentDef getDef() {
      return this._defs;
   }

   @Override
   public boolean equalsField(long data, int field, Object o) {
      switch (this._defs.getFieldType(field)) {
         case 0:
         case 1:
         case 5:
            if (this.getIntFieldValue(data, field) == o) {
               return true;
            }

            return false;
         case 2:
            if (this.getLongFieldValue(data, field) == Double.doubleToLongBits(o)) {
               return true;
            }

            return false;
         case 3:
         case 32768:
         case 32769:
         case 32770:
         case 32771:
         case 32772:
         case 32773:
         case 32774:
         case 32776:
            Object o1 = this.getObjectFieldValue(data, field);
            return o == o1 || o != null && o.equals(o1);
         case 4:
         case 8:
            if (this.getLongFieldValue(data, field) == o) {
               return true;
            }

            return false;
         case 6:
            DataCollection dc = this._wiclet.getDataCollection(this._defs.getFieldReferenceType(field));
            return dc.equals(this.getReferenceField(data, field), o);
         default:
            throw new Object("Not supported type");
      }
   }

   @Override
   public void addReference(long dataHandle, boolean persistableContainer) {
   }

   @Override
   public void removeReference(long dataHandle, boolean persistableContainer) {
   }

   @Override
   public boolean isPersistable(long dataHandle) {
      return this._isPersistable;
   }

   @Override
   public boolean isInnerVectorInitialized(long dataHandle, int field) {
      if ((this._defs.getFieldType(field) & 32768) == 32768) {
         int vectorIndex = this.getVectorFieldIndex(this._dataIndicies.get(this.getHandle(dataHandle)), field);
         if (this._refVectors.elementAt(vectorIndex) != null) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void save() {
      this.save(null);
   }

   private int getIntFieldIndex(int index, int field) {
      return index * this._numIntFields + this._fieldOffset[field << 1];
   }

   private void addFreeSlot(int index) {
      if (this._freeSlots == null) {
         this._freeSlots = (IntStack)(new Object());
      }

      int low = 0;
      int high = this._freeSlots.size() - 1;

      while (low <= high) {
         int mid = low + high >> 1;
         int midValue = this._freeSlots.elementAt(mid);
         if (index == midValue) {
            throw new Object("Slot was already marked as free slot!");
         }

         if (index > midValue) {
            low = mid + 1;
         } else {
            high = mid - 1;
         }
      }

      this._freeSlots.insertElementAt(index, low);
   }

   private void cleanSlot(int index) {
      this._dataFields.setElementAt(-1, this.getHandleIndex(index));
      int objIndex = index * this._numObjectFields;

      for (int i = 0; i < this._numObjectFields; i++) {
         this._refObjects.setElementAt(null, objIndex + i);
      }

      int vecIndex = index * this._numVectorFields;

      for (int i = 0; i < this._numVectorFields; i++) {
         this._refVectors.setElementAt(null, vecIndex + i);
      }
   }

   private boolean load() {
      if (this._isPersistable && this._persistenceListener.isPersistenceReadable()) {
         WicletStore store = this._wiclet.getContext().getWicletStore();
         PersDataCollectionStruct dcData = (PersDataCollectionStruct)store.loadData(this._defs.getId());
         if (dcData != null) {
            this._dataFields = dcData.getDataFields();
            this._longData = dcData.getLongData();
            this._refObjects = dcData.getRefObjects();
            this._idSource = dcData.getIdSource();
            if (this._numVectorFields > 0) {
               int size = this._numVectorFields * (this._dataFields.size() / this._numIntFields);
               if (size > 0) {
                  this._refVectors = (BigVector)(new Object(size));

                  for (int i = 0; i < size; i++) {
                     this._refVectors.addElement(null);
                  }
               } else {
                  this._refVectors = (BigVector)(new Object());
               }
            }

            return this.loadExtraData(dcData);
         }
      }

      return false;
   }

   private int getHandleIndex(int index) {
      return index * this._numIntFields;
   }

   private int getLongFieldIndex(int index, int field) {
      return index * this._numLongFields + this._fieldOffset[field << 1];
   }

   private boolean equalsField(long data1, long data2, DataCollection dc2, int i) {
      switch (this._defs.getFieldType(i)) {
         case 0:
         case 1:
         case 5:
            if (this.getIntFieldValue(data1, i) == dc2.getIntFieldValue(data2, i)) {
               return true;
            }

            return false;
         case 2:
         case 4:
         case 8:
            if (this.getLongFieldValue(data1, i) == dc2.getLongFieldValue(data2, i)) {
               return true;
            }

            return false;
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
            Object o2 = dc2.getObjectFieldValue(data2, i);
            return o2 == o1 || o2 != null && o2.equals(o1);
         case 6:
            DataCollection dc = this._wiclet.getDataCollection(this._defs.getFieldReferenceType(i));
            return dc.equals(this.getReferenceField(data1, i), dc2.getReferenceField(data2, i));
         default:
            throw new Object("Not supported type");
      }
   }

   private void buildOffsets() {
      int fieldCount = this._defs.getNumFields();
      this._fieldOffset = new int[fieldCount << 1];
      this._numIntFields = 1;

      for (int i = 0; i < fieldCount; i++) {
         switch (this._defs.getFieldType(i)) {
            case 0:
            case 1:
            case 5:
               this._fieldOffset[i << 1] = this._numIntFields++;
               break;
            case 2:
            case 4:
            case 6:
            case 8:
               this._fieldOffset[i << 1] = this._numLongFields++;
               break;
            case 32768:
            case 32769:
            case 32770:
            case 32771:
            case 32772:
            case 32773:
            case 32774:
            case 32776:
               this._fieldOffset[(i << 1) + 1] = this._numVectorFields++;
            case 3:
               this._fieldOffset[i << 1] = this._numObjectFields++;
         }
      }
   }

   private Object getAsObject(int slot, int keyField) {
      int keyType = this._defs.getFieldType(keyField);
      switch (keyType) {
         case 1:
         case 5:
         default:
            return new Object(this._dataFields.elementAt(this.getIntFieldIndex(slot, keyField)));
         case 2:
            return new Object(Double.longBitsToDouble(this._longData.elementAt(this.getLongFieldIndex(slot, keyField))));
         case 3:
            return this._refObjects.elementAt(this.getObjectFieldIndex(slot, keyField));
         case 4:
         case 8:
            return new Object(this._longData.elementAt(this.getLongFieldIndex(slot, keyField)));
         case 6:
            KeyDataCollection dc = (KeyDataCollection)this._wiclet.getDataCollection(this._defs.getFieldReferenceType(keyField));
            long ref = this._longData.elementAt(this.getLongFieldIndex(slot, keyField));
            if (dc.contains(ref)) {
               return dc.getPKey(ref);
            }
         case 0:
         case 7:
            return null;
      }
   }

   private void prepareData() {
      int numData = this._dataFields.size() / this._numIntFields;
      this._dataIndicies = (IntIntHashtable)(new Object(numData + (numData >> 1)));

      for (int slot = numData - 1; slot >= 0; slot--) {
         int handle = this._dataFields.elementAt(this.getHandleIndex(slot));
         if (handle != -1) {
            if (handle > this._idSource) {
               this._idSource = handle + 1;
            }

            this._dataIndicies.put(handle, slot);
         } else {
            int index = slot * this._numIntFields;

            for (int i = index + this._numIntFields - 1; i >= index; i--) {
               this._dataFields.removeElementAt(i);
            }

            index = slot * this._numLongFields;
            if (this._longData != null && this._longData.size() > index) {
               for (int i = index + this._numLongFields - 1; i >= index; i--) {
                  this._longData.removeElementAt(i);
               }
            }

            index = slot * this._numObjectFields;
            if (this._refObjects != null && this._refObjects.size() > index) {
               for (int i = index + this._numObjectFields - 1; i >= index; i--) {
                  this._refObjects.removeElementAt(i);
               }
            }

            index = slot * this._numVectorFields;
            if (this._refVectors != null && this._refVectors.size() > index) {
               for (int i = index + this._numVectorFields - 1; i >= index; i--) {
                  this._refVectors.removeElementAt(i);
               }
            }
         }
      }
   }

   private void removeInternal(long dataHandle) {
      if (this.contains(dataHandle)) {
         int handle = this.getHandle(dataHandle);

         for (int i = this._defs.getNumFields() - 1; i >= 0; i--) {
            if (this._defs.getFieldType(i) == 6) {
               long innerHandle = this.getLongFieldValue(dataHandle, i);
               if (innerHandle != -1) {
                  DataCollection dc = this._wiclet.getDataCollection(this._defs.getFieldReferenceType(i));
                  dc.removeReference(innerHandle, this._isPersistable);
               }
            } else if (this._defs.getFieldType(i) == 32774) {
               int vectorIndex = this.getVectorFieldIndex(this._dataIndicies.get(this.getHandle(dataHandle)), i);
               InnerDataVector v = (InnerDataVector)this._refVectors.elementAt(vectorIndex);
               if (v != null) {
                  v.removeAllElements();
               }
            }
         }

         this.freeSlot(this._dataIndicies.get(handle));
         this._dataIndicies.remove(handle);
         this.newVersion(dataHandle);
      }
   }

   public DataCollectionImpl(WicletEx wiclet, DataComponentDef defs, boolean allowPersistence) {
      this._wiclet = wiclet;
      this._defs = defs;
      this.buildOffsets();
      this._isPersistable = allowPersistence && this._defs.isPersistable();
      this._persistenceListener = (PersistenceListener)this._wiclet
         .getRuntime()
         .getService(
            class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener == null
               ? (
                  class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener = class$(
                     "net.rim.wica.runtime.metadata.internal.util.PersistenceListener"
                  )
               )
               : class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener
         );
      if (!allowPersistence || !this.load()) {
         this._dataFields = (BigIntVector)(new Object());
         if (this._numLongFields > 0) {
            this._longData = (BigLongVector)(new Object());
         }

         if (this._numObjectFields > 0) {
            this._refObjects = (BigVector)(new Object());
         }

         if (this._numVectorFields > 0) {
            this._refVectors = (BigVector)(new Object());
         }
      }

      this.prepareData();
      this._transactions = (TransactionManager)this._wiclet
         .getRuntime()
         .getService(
            class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager == null
               ? (
                  class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager = class$(
                     "net.rim.wica.runtime.metadata.internal.transaction.TransactionManager"
                  )
               )
               : class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager
         );
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
