package net.rim.wica.runtime.metadata.internal.def;

import net.rim.wica.common.metadata.component.ComponentDef;
import net.rim.wica.common.metadata.component.DefaultValueDef;

public class ComponentDefImpl implements ComponentDef, DefaultValueDef {
   protected int _defId;
   protected ComponentDefAccess _defs;

   public ComponentDefImpl(int defId, ComponentDefAccess defs) {
      this._defId = defId;
      this._defs = defs;
   }

   @Override
   public boolean isPersistable() {
      return false;
   }

   @Override
   public DefaultValueDef getDefaultValues() {
      return this;
   }

   @Override
   public int getId() {
      return this._defId;
   }

   @Override
   public int getNumFields() {
      return this._defs.getNumFields(this._defId);
   }

   @Override
   public int getFieldType(int fieldId) {
      return this._defs.getFieldType(this._defId, fieldId);
   }

   @Override
   public int getFieldReferenceType(int fieldId) {
      return this._defs.getFieldReferenceType(this._defId, fieldId);
   }

   @Override
   public int getFieldHandle(String field) {
      return this._defs.getFieldHandle(this._defId, field);
   }

   @Override
   public int getAccessType(int field) {
      return 268435456;
   }

   @Override
   public boolean hasDefaultValue(int fieldIndex) {
      return this._defs.hasDefaultValue(this._defId, fieldIndex);
   }

   @Override
   public Object getObjectDefaultValue(int fieldIndex) {
      return this._defs.getObjectDefaultValue(this._defId, fieldIndex);
   }

   @Override
   public int getIntDefaultValue(int fieldIndex) {
      return this._defs.getIntDefaultValue(this._defId, fieldIndex);
   }

   @Override
   public boolean getBooleanDefaultValue(int fieldIndex) {
      return this._defs.getBooleanDefaultValue(this._defId, fieldIndex);
   }

   @Override
   public double getDoubleDefaultValue(int fieldIndex) {
      return this._defs.getDoubleDefaultValue(this._defId, fieldIndex);
   }

   @Override
   public long getLongDefaultValue(int fieldIndex) {
      return this._defs.getLongDefaultValue(this._defId, fieldIndex);
   }
}
