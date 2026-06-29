package net.rim.wica.runtime.metadata.internal.component;

import net.rim.wica.common.metadata.component.ComponentDef;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.component.Component;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;
import net.rim.wica.runtime.metadata.internal.def.ComponentDefAccess;
import net.rim.wica.runtime.metadata.internal.def.ComponentDefImpl;
import net.rim.wica.runtime.util.LongVector;

class ComponentImpl extends ComponentDefImpl implements Component, DataOwner {
   protected int[] _dataFields;
   protected long[] _longData;
   protected Object[] _refObjects;
   protected Wiclet _wiclet;
   protected long _handle;

   public void clean() {
      int numFields = this.getNumFields();

      for (int i = 0; i < numFields; i++) {
         switch (this.getFieldType(i)) {
            case 6:
               this.setReferenceField(i, -1);
               break;
            case 32774:
               LongVector data = (LongVector)this.getObjectFieldValue(i);
               data.removeAllElements();
         }
      }

      if (this._refObjects != null) {
         for (int i = this._refObjects.length - 1; i >= 0; i--) {
            this._refObjects[i] = null;
         }
      }
   }

   protected void setDefault(int field) {
      switch (this.getFieldType(field)) {
         case 0:
         case 1:
         case 5:
         default:
            this._dataFields[field] = this.getIntDefaultValue(field);
            return;
         case 2:
            this._longData[this._dataFields[field]] = Double.doubleToLongBits(this.getDoubleDefaultValue(field));
            return;
         case 3:
            this._refObjects[this._dataFields[field]] = this.getObjectDefaultValue(field);
         case -1:
         case 7:
            return;
         case 4:
            if (!this.getDefaultValues().hasDefaultValue(field)) {
               this._longData[this._dataFields[field]] = System.currentTimeMillis();
               return;
            }
         case 8:
            this._longData[this._dataFields[field]] = this.getLongDefaultValue(field);
            return;
         case 6:
            this._longData[this._dataFields[field]] = -1;
      }
   }

   protected void createDefaults() {
      int numFields = this.getNumFields();
      this._dataFields = new int[numFields];
      int numLongs = 0;
      int numObjects = 0;

      for (int i = 0; i < numFields; i++) {
         switch (this.getFieldType(i)) {
            case 2:
            case 4:
            case 6:
            case 8:
               this._dataFields[i] = numLongs++;
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
               this._dataFields[i] = numObjects++;
         }
      }

      if (numLongs > 0) {
         this._longData = new long[numLongs];
      }

      if (numObjects > 0) {
         this._refObjects = new Object[numObjects];
      }

      for (int i = 0; i < numFields; i++) {
         this.setDefault(i);
      }
   }

   @Override
   public void setDoubleFieldValue(int field, double value) {
      this.modified(this._handle, field);
      this._longData[this._dataFields[field]] = Double.doubleToLongBits(value);
   }

   @Override
   public void setBooleanFieldValue(int field, boolean value) {
      this.modified(this._handle, field);
      if (value) {
         this._dataFields[field] = 1;
      } else {
         this._dataFields[field] = 0;
      }
   }

   @Override
   public void setObjectFieldValue(int field, Object value) {
      this.modified(this._handle, field);
      int fieldType = this.getFieldType(field);
      if ((32768 & fieldType) != 0) {
         if (value != null) {
            BaseInnerVector to = (BaseInnerVector)this.getObjectFieldValue(field);
            to.clone(value);
         } else {
            BaseInnerVector to = (BaseInnerVector)this._refObjects[this._dataFields[field]];
            if (to != null) {
               to.removeAllElements();
            }

            this._refObjects[this._dataFields[field]] = null;
         }
      } else {
         this._refObjects[this._dataFields[field]] = value;
      }
   }

   @Override
   public Object getObjectFieldValue(int field) {
      Object o = this._refObjects[this._dataFields[field]];
      if (o == null) {
         int fieldType = this.getFieldType(field);
         if ((32768 & fieldType) != 0) {
            o = createArray(
               this._wiclet, fieldType, this, this._handle, field, this, this.getDefaultValues().getObjectDefaultValue(field), this.isPersistable()
            );
            this._refObjects[this._dataFields[field]] = o;
         }
      }

      return o;
   }

   @Override
   public int getIntFieldValue(int field) {
      return this._dataFields[field];
   }

   @Override
   public boolean getBooleanFieldValue(int field) {
      return this._dataFields[field] == 1;
   }

   @Override
   public double getDoubleFieldValue(int field) {
      return Double.longBitsToDouble(this._longData[this._dataFields[field]]);
   }

   @Override
   public long getLongFieldValue(int field) {
      return this._longData[this._dataFields[field]];
   }

   @Override
   public void setReferenceField(int field, long reference) {
      DataCollection dc = this._wiclet.getDataCollection(this.getFieldReferenceType(field));
      long oldRef = this.getLongFieldValue(field);
      if (oldRef != reference) {
         dc.addReference(reference, this.isPersistable());
         dc.removeReference(oldRef, this.isPersistable());
      }

      this.setLongFieldValue(field, reference);
   }

   @Override
   public long getReferenceField(int field) {
      long ref = this.getLongFieldValue(field);
      DataCollection dc = this._wiclet.getDataCollection(this.getFieldReferenceType(field));
      if (ref == -1 || !dc.contains(ref)) {
         if (dc instanceof KeyDataCollection) {
            if (ref != -1) {
               ref = -1;
               this.setLongFieldValue(field, ref);
               return ref;
            }
         } else {
            if (dc instanceof KeylessDataCollection) {
               boolean isPersistable = this.isPersistable();
               ref = ((KeylessDataCollection)dc).create(isPersistable);
               dc.addReference(ref, isPersistable);
               this.setLongFieldValue(field, ref);
               return ref;
            }

            ref = dc.create();
            this.setLongFieldValue(field, ref);
         }
      }

      return ref;
   }

   @Override
   public long getExistingReferenceField(int field) {
      long ref = this.getLongFieldValue(field);
      DataCollection dc = this._wiclet.getDataCollection(this.getFieldReferenceType(field));
      if (ref != -1 && !dc.contains(ref)) {
         ref = -1;
         this.setLongFieldValue(field, ref);
      }

      return ref;
   }

   @Override
   public long getReferenceFieldAsIs(int field) {
      return this.getLongFieldValue(field);
   }

   @Override
   public Object getFieldValueAsObject(int field) {
      switch (this.getFieldType(field)) {
         case 0:
            return new Boolean(this.getBooleanFieldValue(field));
         case 1:
         case 5:
            return new Integer(this.getIntFieldValue(field));
         case 2:
            return new Double(this.getDoubleFieldValue(field));
         case 3:
         case 32768:
         case 32769:
         case 32770:
         case 32771:
         case 32772:
         case 32773:
         case 32774:
         case 32776:
            return this.getObjectFieldValue(field);
         case 4:
         case 8:
            return new Long(this.getLongFieldValue(field));
         case 6:
            return new Long(this.getReferenceField(field));
         default:
            throw new RuntimeException("Not recognized field type.");
      }
   }

   @Override
   public void setFieldValueFromObject(int field, Object value) {
      switch (this.getFieldType(field)) {
         case 0:
            this.setBooleanFieldValue(field, value == null ? false : (Boolean)value);
            return;
         case 1:
         case 5:
            this.setIntFieldValue(field, value == null ? 0 : (Integer)value);
            return;
         case 2:
            double dValue = (double)0L;
            if (!(value instanceof Double)) {
               if (!(value instanceof Integer)) {
                  if (value != null) {
                     throw new RuntimeException("Invalid value type for decimal variable or field");
                  }
               } else {
                  dValue = ((Integer)value).intValue();
               }
            } else {
               dValue = (Double)value;
            }

            this.setDoubleFieldValue(field, dValue);
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
            this.setObjectFieldValue(field, value);
            return;
         case 4:
            this.setLongFieldValue(field, value == null ? System.currentTimeMillis() : (Long)value);
            return;
         case 6:
            this.setReferenceField(field, value == null ? -1 : (Long)value);
            return;
         case 8:
            long lValue = 0;
            if (!(value instanceof Long)) {
               if (!(value instanceof Integer)) {
                  if (value != null) {
                     throw new RuntimeException("Invalid value type for long variable or field");
                  }
               } else {
                  lValue = ((Integer)value).intValue();
               }
            } else {
               lValue = (Long)value;
            }

            this.setLongFieldValue(field, lValue);
            return;
         default:
            throw new RuntimeException("Not recognized field type.");
      }
   }

   @Override
   public void setLongFieldValue(int field, long value) {
      this.modified(this._handle, field);
      this._longData[this._dataFields[field]] = value;
   }

   @Override
   public void setIntFieldValue(int field, int value) {
      this.modified(this._handle, field);
      this._dataFields[field] = value;
   }

   @Override
   public ComponentDef getDef() {
      return this;
   }

   @Override
   public void modified(long dataHandle, int field) {
   }

   ComponentImpl(int defId, Wiclet wiclet, ComponentDefAccess defs) {
      super(defId, defs);
      this._wiclet = wiclet;
      this._handle = (long)defId << 32;
   }

   protected static Object createArray(
      Wiclet wiclet, int type, DataOwner owner, long handle, int field, ComponentDef def, Object defaultValues, boolean allowPersistence
   ) {
      switch (type) {
         case 32767:
         case 32775:
            return null;
         case 32768:
         case 32769:
         case 32773:
         default:
            return new InnerIntVector(owner, handle, field, (int[])defaultValues);
         case 32770:
            return new InnerDoubleVector(owner, handle, field, (double[])defaultValues);
         case 32771:
            return new InnerVector(owner, handle, field, (Object[])defaultValues);
         case 32772:
         case 32776:
            return new InnerLongVector(owner, handle, field, (long[])defaultValues);
         case 32774:
            return new InnerDataVector(owner, handle, field, wiclet.getDataCollection(def.getFieldReferenceType(field)), allowPersistence);
      }
   }
}
