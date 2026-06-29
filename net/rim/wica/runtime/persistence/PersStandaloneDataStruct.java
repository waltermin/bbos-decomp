package net.rim.wica.runtime.persistence;

import net.rim.device.api.util.Persistable;

public final class PersStandaloneDataStruct implements Persistable, Recryptable {
   private int[] _dataFields;
   private long[] _longData;
   private Object[] _refObjects;

   public final int[] getDataFields() {
      return this._dataFields;
   }

   public final void setDataFields(int[] fields) {
      this._dataFields = fields;
   }

   public final long[] getLongData() {
      return this._longData;
   }

   public final void setLongData(long[] data) {
      this._longData = data;
   }

   public final Object[] getRefObjects() {
      return this._refObjects == null ? null : (Object[])PersisProtectionHelper.decode(this._refObjects);
   }

   public final void initRefObjects(int size) {
      this._refObjects = new Object[size];
   }

   public final void setRefObjectAt(Object object, int i) {
      this._refObjects[i] = PersisProtectionHelper.encode(object);
   }

   public final void setRefObjects(Object[] objects) {
      this._refObjects = (Object[])PersisProtectionHelper.encode(objects);
   }

   @Override
   public final void recrypt() {
      this._refObjects = (Object[])PersisProtectionHelper.reEncode(this._refObjects);
   }
}
