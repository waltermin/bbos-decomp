package net.rim.wica.common.builtindata.componentdefn;

import net.rim.device.api.util.ToIntHashtable;
import net.rim.wica.common.metadata.component.DataComponentDef;
import net.rim.wica.common.metadata.component.DefaultValueDef;

public class DataComponentDefinition implements DataComponentDef, DefaultValueDef {
   private ToIntHashtable _fieldMapping;

   public String getComponentName() {
      throw null;
   }

   protected void initMappings() {
      String[] fieldNames = this.getFieldNames();
      if (fieldNames.length > 0) {
         this._fieldMapping = new ToIntHashtable(fieldNames.length + (fieldNames.length >> 1));

         for (int i = fieldNames.length - 1; i >= 0; i--) {
            this._fieldMapping.put(fieldNames[i], i);
         }
      }
   }

   public String[] getFieldNames() {
      throw null;
   }

   @Override
   public DefaultValueDef getDefaultValues() {
      return this;
   }

   @Override
   public int getNumFields() {
      throw null;
   }

   @Override
   public int getFieldType(int _1) {
      throw null;
   }

   @Override
   public boolean hasDefaultValue(int fieldIndex) {
      return false;
   }

   @Override
   public Object getObjectDefaultValue(int fieldIndex) {
      return null;
   }

   @Override
   public int getIntDefaultValue(int fieldIndex) {
      return 0;
   }

   @Override
   public boolean getBooleanDefaultValue(int fieldIndex) {
      return false;
   }

   @Override
   public double getDoubleDefaultValue(int fieldIndex) {
      return (double)0L;
   }

   @Override
   public long getLongDefaultValue(int fieldIndex) {
      return 0;
   }

   @Override
   public int getFieldReferenceType(int fieldId) {
      return -1;
   }

   @Override
   public boolean isPersistable() {
      return false;
   }

   @Override
   public boolean isPersistable(int fieldId) {
      return false;
   }

   @Override
   public boolean isKeyField(int field) {
      int[] keys = this.getKeyFields();
      return keys == null ? false : binarySearch(keys, field, 0, keys.length) >= 0;
   }

   @Override
   public int getAccessType(int field) {
      return this.isKeyField(field) ? 536870912 : 268435456;
   }

   @Override
   public int getFieldHandle(String field) {
      return this._fieldMapping.get(field);
   }

   @Override
   public boolean isBuiltinComponent() {
      return true;
   }

   @Override
   public int[] getKeyFields() {
      throw null;
   }

   @Override
   public boolean hasKey() {
      throw null;
   }

   @Override
   public int getId() {
      throw null;
   }

   public DataComponentDefinition() {
      this.initMappings();
   }

   private static int binarySearch(int[] a, int key, int fromIndex, int toIndex) {
      int low = fromIndex;
      int high = toIndex - 1;

      while (low <= high) {
         int mid = low + high >> 1;
         int midVal = a[mid];
         if (midVal < key) {
            low = mid + 1;
         } else {
            if (midVal <= key) {
               return mid;
            }

            high = mid - 1;
         }
      }

      return -(low + 1);
   }
}
