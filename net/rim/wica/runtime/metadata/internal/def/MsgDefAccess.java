package net.rim.wica.runtime.metadata.internal.def;

import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.LongIntHashtable;

public class MsgDefAccess implements ComponentDefAccess {
   private Definitions _definitions;
   private ComponentDefStruct _msgDefs;
   IntIntHashtable _idToDefIndex;
   private LongHashtable _fieldMapping;
   private LongIntHashtable _fieldHandles;
   private IntIntHashtable _codeToDefIndex;
   private static final int[] _emptyArray = new int[0];

   public IntEnumeration getMsgDefIds() {
      return this._idToDefIndex.keys();
   }

   public boolean hasDefs() {
      return this._msgDefs._defs.length > 0;
   }

   public int getMsgDefByCode(int code) {
      return this._codeToDefIndex.get(code);
   }

   public int getMsgCode(int defId) {
      int msgIndex = this._idToDefIndex.get(defId);
      return this._msgDefs._defs[msgIndex + 6];
   }

   public String getMsgName(int defId) {
      int msgIndex = this._idToDefIndex.get(defId);
      int msgNameIndex = this._msgDefs._defs[msgIndex + 4];
      return msgNameIndex == -1 ? null : (String)this._msgDefs._objectData[msgNameIndex];
   }

   public int getScript(int defId) {
      int msgIndex = this._idToDefIndex.get(defId);
      return this._msgDefs._defs[msgIndex + 3];
   }

   public int[] getFieldMapping(int defId, int fieldId) {
      if (this._fieldMapping == null) {
         this._fieldMapping = new LongHashtable();
      }

      long key = (long)defId << 32 | 4294967295L & fieldId;
      int[] mapping = (int[])this._fieldMapping.get(key);
      if (mapping == null) {
         int msgIndex = this._idToDefIndex.get(defId);
         int fieldsIndex = this._msgDefs._defs[msgIndex + 5];
         int mapIndex = this._msgDefs._varData[fieldsIndex + 1 + fieldId * 7 + 6];
         if (mapIndex != -1) {
            int size = this._msgDefs._varData[mapIndex + 0];
            mapIndex++;
            mapping = new int[size];

            for (int i = 0; i < size; i++) {
               mapping[i] = this._msgDefs._varData[mapIndex + i];
            }
         } else {
            mapping = _emptyArray;
         }

         this._fieldMapping.put(key, mapping);
      }

      return mapping;
   }

   public ComponentDefStruct getMsgDefs() {
      return this._msgDefs;
   }

   public boolean isSynchronous(int defId) {
      int msgIndex = this._idToDefIndex.get(defId);
      return (this._msgDefs._defs[msgIndex + 2] & 2) != 0;
   }

   public boolean isSecure(int defId) {
      int msgIndex = this._idToDefIndex.get(defId);
      return (this._msgDefs._defs[msgIndex + 2] & 1) != 0;
   }

   @Override
   public int getFieldReferenceType(int defId, int fieldId) {
      return this.getFieldProperty(defId, fieldId, 5);
   }

   @Override
   public int getFieldType(int defId, int fieldId) {
      return this.getFieldProperty(defId, fieldId, 0);
   }

   @Override
   public int getNumFields(int defId) {
      int msgIndex = this._idToDefIndex.get(defId);
      int fieldsIndex = this._msgDefs._defs[msgIndex + 5];
      return fieldsIndex == -1 ? -1 : this._msgDefs._varData[fieldsIndex + 0];
   }

   @Override
   public boolean hasDefaultValue(int defId, int fieldId) {
      return (this.getFieldProperty(defId, fieldId, 1) & 1) != 0;
   }

   @Override
   public Object getObjectDefaultValue(int defId, int fieldId) {
      if (!this.hasDefaultValue(defId, fieldId)) {
         return null;
      }

      int objIndex = this.getFieldProperty(defId, fieldId, 3);
      return this._msgDefs._objectData[objIndex];
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
      int fieldsIndex = this._msgDefs._defs[dataIndex + 5] + 1 + fieldId * 7;
      return (long)this._msgDefs._varData[fieldsIndex + 2] << 32 | 4294967295L & this._msgDefs._varData[fieldsIndex + 3];
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
      // 03: invokevirtual net/rim/wica/runtime/metadata/internal/def/MsgDefAccess.hasDefaultValue (II)Z
      // 06: ifne 0f
      // 09: nop
      // 0a: ldc2_w 0
      // 0d: nop
      // 0e: dreturn
      // 0f: aload 0
      // 10: iload 1
      // 11: iload 2
      // 12: invokevirtual net/rim/wica/runtime/metadata/internal/def/MsgDefAccess.getLongDefaultValue (II)J
      // 15: invokestatic java/lang/Double.longBitsToDouble (J)D
      // 18: nop
      // 19: dreturn
   }

   @Override
   public boolean hasDefinition(int defId) {
      return this._idToDefIndex.containsKey(defId);
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
      int msgIndex = this._idToDefIndex.get(defId);
      int fieldsIndex = this._msgDefs._defs[msgIndex + 5];
      return this._msgDefs._varData[fieldsIndex + 1 + fieldId * 7 + valueOffset];
   }

   MsgDefAccess(Definitions defs, ComponentDefStruct msgDefs) {
      this._definitions = defs;
      this._msgDefs = msgDefs;
      this._fieldHandles = new LongIntHashtable();
      int size = this._msgDefs._defs.length;
      int msgCount = size / 7;
      msgCount += msgCount >> 1;
      this._idToDefIndex = new IntIntHashtable(msgCount);
      this._codeToDefIndex = new IntIntHashtable(msgCount);

      for (int i = 0; i < size; i += 7) {
         int defId = this._msgDefs._defs[i + 1];
         this._idToDefIndex.put(defId, i);
         this._codeToDefIndex.put(this._msgDefs._defs[i + 6], defId);
      }
   }
}
