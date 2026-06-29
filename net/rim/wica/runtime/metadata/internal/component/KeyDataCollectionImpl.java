package net.rim.wica.runtime.metadata.internal.component;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntEnumeration;
import net.rim.wica.common.metadata.component.DataComponentDef;
import net.rim.wica.runtime.metadata.component.CompositeKey;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.metadata.internal.util.DataKeyHashtable;
import net.rim.wica.runtime.metadata.util.ValueResolver;
import net.rim.wica.runtime.util.LongVector;

public class KeyDataCollectionImpl extends DataCollectionImpl implements KeyDataCollection {
   protected DataKeyHashtable _keyTable;
   private DataComponentDef _keyDefs;

   public KeyDataCollectionImpl(WicletEx owner, DataComponentDef defs) {
      super(owner, defs, defs.isPersistable());
      this._keyDefs = this.getKeyDef(defs);
      this._keyTable = this.buildKeyTable(super._defs.getKeyFields());
      if (super._persistenceListener != null) {
         super._persistenceListener.created(this);
      }
   }

   @Override
   public long create(Object pKey) {
      if (pKey == null) {
         throw new IllegalArgumentException("Null PK");
      }

      long handle = this.find(pKey);
      int[] keyFields = super._defs.getKeyFields();
      if (handle != -1) {
         if (keyFields.length == 1 && super._defs.getFieldType(keyFields[0]) == 6) {
            KeyDataCollection dc = (KeyDataCollection)super._wiclet.getDataCollection(super._defs.getFieldReferenceType(keyFields[0]));
            if (dc != null && !dc.contains(this.getLongFieldValue(handle, keyFields[0]))) {
               this.setLongFieldValue(handle, keyFields[0], dc.create(pKey));
            }
         }
      } else {
         handle = this.create();
         this._keyTable.put(pKey, (int)(handle & 4294967295L));
         if (keyFields.length == 1) {
            int singleKeyField = keyFields[0];
            if (6 == super._defs.getFieldType(singleKeyField)) {
               KeyDataCollection dc = (KeyDataCollection)super._wiclet.getDataCollection(super._defs.getFieldReferenceType(singleKeyField));
               this.setLongFieldValue(handle, singleKeyField, dc.create(pKey));
               return handle;
            }

            this.setFieldValueFromObject(handle, singleKeyField, pKey);
            return handle;
         }

         if (!(pKey instanceof CompositeKey)) {
            throw new IllegalArgumentException("Incompatible key type");
         }

         CompositeKey key = (CompositeKey)pKey;

         for (int i = keyFields.length - 1; i >= 0; i--) {
            this.setFieldValueFromObject(handle, keyFields[i], key.getPart(i));
         }
      }

      return handle;
   }

   @Override
   public Object getPKey(long dataHandle) {
      return this._keyTable.getKey((int)(dataHandle & 4294967295L));
   }

   @Override
   public long find(Object pKey) {
      if (pKey == null) {
         throw new IllegalArgumentException("Null PK");
      }

      int handle = this._keyTable.get(pKey);
      return handle == -1 ? -1 : (long)super._defs.getId() << 32 | 4294967295L & handle;
   }

   @Override
   public long[] retrieveAll(boolean bSort) {
      int i = 0;
      long[] all = new long[this._keyTable.size()];
      int defId = super._defs.getId();

      for (IntEnumeration e = this._keyTable.elements(); e.hasMoreElements(); i++) {
         all[i] = (long)defId << 32 | 4294967295L & e.nextElement();
      }

      if (bSort && all != null && all.length != 0) {
         Arrays.sort(all, 0, all.length - 1);
      }

      return all;
   }

   @Override
   public void removeAll() {
      if (super._transactions != null) {
         long[] handles = this.retrieveAll(false);

         for (int i = 0; i < handles.length; i++) {
            super._transactions.deleted(handles[i]);
         }
      }

      this._keyTable.clear();
      super.removeAll();
   }

   @Override
   public int getCount() {
      return this._keyTable.size();
   }

   @Override
   public void remove(long dataHandle) {
      if (super._transactions != null) {
         super._transactions.deleted(dataHandle);
      }

      if (super._persistenceListener != null) {
         super._persistenceListener.deleted(dataHandle);
      }

      int intHandle = this.getHandle(dataHandle);
      this._keyTable.remove(intHandle);
      super.remove(dataHandle);
   }

   @Override
   public long[] findWhere(String expression, ValueResolver resolver) {
      LongVector findWhereResults = new LongVector();
      super._wiclet.findWhere(findWhereResults, this, this.retrieveAll(true), expression, resolver);
      findWhereResults.trimToSize();
      return findWhereResults.getArray();
   }

   @Override
   public boolean equals(long data1, long data2) {
      return data1 == data2 || this.equalsByFields(data1, data2, super._defs.getKeyFields());
   }

   @Override
   public void restoreKey(long handle, Object key) {
      this._keyTable.put(key, (int)(handle & 4294967295L));
   }

   @Override
   public DataComponentDef getKeyDef() {
      return this._keyDefs;
   }

   private DataComponentDef getKeyDef(DataComponentDef def) {
      int[] keys = def.getKeyFields();
      if (keys.length == 1 && def.getFieldType(keys[0]) == 6) {
         def = (DataComponentDef)super._wiclet.getComponentDef(def.getFieldReferenceType(keys[0]));
         return this.getKeyDef(def);
      } else {
         return def;
      }
   }
}
