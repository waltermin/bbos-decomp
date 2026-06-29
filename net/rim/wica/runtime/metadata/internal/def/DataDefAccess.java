package net.rim.wica.runtime.metadata.internal.def;

import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.LongIntHashtable;

public class DataDefAccess implements ComponentDefAccess {
   Definitions _definitions;
   ComponentDefStruct _dataDefs;
   LongIntHashtable _fieldHandles;
   IntIntHashtable _idToDefIndex;

   public boolean isSingleton(int defId) {
      int dataIndex = this._idToDefIndex.get(defId);
      return (this._dataDefs._defs[dataIndex + 3] & 4) != 0;
   }

   public boolean isPersistable(int defId) {
      int dataIndex = this._idToDefIndex.get(defId);
      return (this._dataDefs._defs[dataIndex + 3] & 1) == 0;
   }

   public boolean isPersistable(int defId, int fieldId) {
      return (this.getFieldProperty(defId, fieldId, 1) & 1) == 0;
   }

   public IntEnumeration getDataDefIds() {
      return this._idToDefIndex.keys();
   }

   public ComponentDefStruct getDataDefs() {
      return this._dataDefs;
   }

   public int getType(int defId) {
      int dataIndex = this._idToDefIndex.get(defId);
      return this._dataDefs._defs[dataIndex + 0];
   }

   public int getEnumValueAsInt(int defId, String enumValue) {
      String[] values = this.getEnum(defId);

      for (int i = values.length - 1; i >= 0; i--) {
         if (enumValue.equals(values[i])) {
            return i;
         }
      }

      return -1;
   }

   public boolean isValidEnumValue(int defId, String enumValue) {
      return this.getEnumValueAsInt(defId, enumValue) != -1;
   }

   public boolean hasKey(int defId) {
      if (this.getPKeyField(defId) >= 0) {
         return true;
      }

      int dataIndex = this._idToDefIndex.get(defId);
      return (this._dataDefs._defs[dataIndex + 3] & 8) != 0;
   }

   public boolean isKeyField(int defId, int fieldId) {
      return this.getPKeyField(defId) >= 0 ? this.getPKeyField(defId) == fieldId : (this.getFieldProperty(defId, fieldId, 1) & 8) != 0;
   }

   public String[] getEnum(int defId) {
      int enumIndex = this._idToDefIndex.get(defId);
      int fieldsIndex = this._dataDefs._defs[enumIndex + 2];
      return (String[])this._dataDefs._objectData[fieldsIndex];
   }

   @Override
   public int getIntDefaultValue(int defId, int fieldId) {
      return !this.hasDefaultValue(defId, fieldId) ? 0 : this.getFieldProperty(defId, fieldId, 3);
   }

   @Override
   public boolean getBooleanDefaultValue(int defId, int fieldId) {
      return !this.hasDefaultValue(defId, fieldId) ? false : this.getIntDefaultValue(defId, fieldId) == 1;
   }

   @Override
   public long getLongDefaultValue(int defId, int fieldId) {
      if (!this.hasDefaultValue(defId, fieldId)) {
         return 0;
      }

      int dataIndex = this._idToDefIndex.get(defId);
      int fieldsIndex = this._dataDefs._defs[dataIndex + 4] + 1 + fieldId * 6;
      return (long)this._dataDefs._varData[fieldsIndex + 2] << 32 | 4294967295L & this._dataDefs._varData[fieldsIndex + 3];
   }

   @Override
   public double getDoubleDefaultValue(int param1, int param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: No common supertype for ternary expression
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getExprType(FunctionExprent.java:224)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.checkExprTypeBounds(FunctionExprent.java:372)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExpr(VarTypeProcessor.java:156)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExprent(VarTypeProcessor.java:132)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.lambda$processVarTypes$2(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.DirectGraph.iterateExprents(DirectGraph.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.processVarTypes(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.calculateVarTypes(VarTypeProcessor.java:44)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionsProcessor.setVarVersions(VarVersionsProcessor.java:68)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarVersions(VarProcessor.java:47)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:241)
      //
      // Bytecode:
      // 00: aload 0
      // 01: iload 1
      // 02: iload 2
      // 03: invokevirtual net/rim/wica/runtime/metadata/internal/def/DataDefAccess.hasDefaultValue (II)Z
      // 06: ifne 0f
      // 09: nop
      // 0a: ldc2_w 0
      // 0d: nop
      // 0e: dreturn
      // 0f: aload 0
      // 10: iload 1
      // 11: iload 2
      // 12: invokevirtual net/rim/wica/runtime/metadata/internal/def/DataDefAccess.getLongDefaultValue (II)J
      // 15: invokestatic java/lang/Double.longBitsToDouble (J)D
      // 18: nop
      // 19: dreturn
   }

   @Override
   public Object getObjectDefaultValue(int defId, int fieldId) {
      if (!this.hasDefaultValue(defId, fieldId)) {
         return this.getFieldType(defId, fieldId) == 3 ? "" : null;
      }

      int objIndex = this.getFieldProperty(defId, fieldId, 3);
      return this._dataDefs._objectData[objIndex];
   }

   @Override
   public boolean hasDefaultValue(int defId, int fieldId) {
      return (this.getFieldProperty(defId, fieldId, 1) & 2) != 0;
   }

   @Override
   public int getFieldReferenceType(int defId, int fieldId) {
      return this.getFieldProperty(defId, fieldId, 5);
   }

   @Override
   public boolean hasDefinition(int defId) {
      return this._idToDefIndex.containsKey(defId);
   }

   @Override
   public int getFieldType(int defId, int fieldId) {
      return this.getFieldProperty(defId, fieldId, 0);
   }

   @Override
   public int getNumFields(int defId) {
      int dataIndex = this._idToDefIndex.get(defId);
      int fieldsIndex = this._dataDefs._defs[dataIndex + 4];
      return fieldsIndex == -1 ? -1 : this._dataDefs._varData[fieldsIndex + 0];
   }

   @Override
   public int getFieldHandle(int defId, String field) {
      int fieldHash = this._definitions.getCodeByName(field);
      int fieldHandle = this._fieldHandles.get((long)defId << 32 | 4294967295L & fieldHash);
      if (fieldHandle == -1 && !this._fieldHandles.containsKey((long)defId << 32 | 4294967295L)) {
         this._fieldHandles.put((long)defId << 32 | 4294967295L, 0);
         int numFields = this.getNumFields(defId);
         long defIdKey = (long)defId << 32;

         for (int i = 0; i < numFields; i++) {
            long fieldKey = defIdKey | 4294967295L & this.getFieldProperty(defId, i, 4);
            this._fieldHandles.put(fieldKey, i);
         }

         fieldHandle = this._fieldHandles.get(defIdKey | 4294967295L & fieldHash);
      }

      return fieldHandle;
   }

   private int getFieldProperty(int defId, int fieldId, int valueOffset) {
      int dataIndex = this._idToDefIndex.get(defId);
      int fieldsIndex = this._dataDefs._defs[dataIndex + 4];
      return this._dataDefs._varData[fieldsIndex + 1 + fieldId * 6 + valueOffset];
   }

   DataDefAccess(Definitions definitions, ComponentDefStruct dataDefs) {
      this._definitions = definitions;
      this._dataDefs = dataDefs;
      this._fieldHandles = new LongIntHashtable();
      int size = this._dataDefs._defs.length;
      int dataCount = size / 5;
      this._idToDefIndex = new IntIntHashtable(dataCount + (dataCount >> 1));
      int i = 0;

      while (i < size) {
         this._idToDefIndex.put(this._dataDefs._defs[i + 1], i);
         switch (this._dataDefs._defs[i + 0]) {
            case 4:
               throw new RuntimeException("Illegal definition in data defs");
            case 5:
               i += 3;
               break;
            case 6:
            default:
               i += 5;
         }
      }
   }

   private int getPKeyField(int defId) {
      int dataIndex = this._idToDefIndex.get(defId);
      return this._dataDefs._defs[dataIndex + 2];
   }
}
