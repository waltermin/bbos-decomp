package net.rim.wica.runtime.metadata.internal.def;

import net.rim.device.api.util.IntVector;
import net.rim.wica.common.metadata.component.DataComponentDef;

public class DataDef extends ComponentDefImpl implements DataComponentDef {
   private int[] _keyFieldIndexes;

   public DataDef(int defId, DataDefAccess defs) {
      super(defId, defs);
      if (defs.hasKey(defId)) {
         int size = defs.getNumFields(defId);
         IntVector keys = new IntVector();

         for (int i = 0; i < size; i++) {
            if (defs.isKeyField(defId, i)) {
               keys.addElement(i);
            }
         }

         this._keyFieldIndexes = keys.toArray();
      }
   }

   @Override
   public boolean hasKey() {
      return this._keyFieldIndexes != null;
   }

   @Override
   public int[] getKeyFields() {
      return this._keyFieldIndexes;
   }

   @Override
   public boolean isKeyField(int field) {
      return ((DataDefAccess)super._defs).isKeyField(super._defId, field);
   }

   @Override
   public boolean isPersistable() {
      return ((DataDefAccess)super._defs).isPersistable(super._defId);
   }

   @Override
   public boolean isPersistable(int fieldId) {
      return ((DataDefAccess)super._defs).isPersistable(super._defId, fieldId);
   }

   @Override
   public int getAccessType(int field) {
      return this.isKeyField(field) ? 536870912 : 268435456;
   }

   @Override
   public boolean isBuiltinComponent() {
      return false;
   }
}
