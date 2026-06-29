package net.rim.wica.runtime.provisioning.internal;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.util.IntVector;
import net.rim.wica.runtime.metadata.internal.def.ComponentDefStruct;
import net.rim.wica.runtime.provisioning.internal.elements.DataElement;
import net.rim.wica.runtime.provisioning.internal.elements.EnumerationElement;
import net.rim.wica.runtime.provisioning.internal.elements.FieldElement;
import net.rim.wica.runtime.provisioning.internal.elements.GlobalElement;
import net.rim.wica.runtime.provisioning.internal.elements.WicletElement;

public final class DataVisitor extends DefinitionVisitorAdapter {
   private EncodingContext _dataDefs;
   private EncodingContextOffset _dataDefsOffset;
   private int _globalDataDefId;
   private UniqueCodeGenerator _uniqueCodeGenerator;
   private static final String _GLOBALS_INTERNAL_NAME = "Global";

   public DataVisitor(UniqueCodeGenerator ucg) {
      this._uniqueCodeGenerator = ucg;
      this._dataDefs = new EncodingContext();
      this._dataDefsOffset = new EncodingContextOffset();
   }

   public final ComponentDefStruct getDataDefs() {
      return this._dataDefs.toComponentDefStruct();
   }

   public final int getGlobalDefId() {
      return this._globalDataDefId;
   }

   @Override
   public final boolean visitDataElement(DataElement data) {
      IntVector defs = this._dataDefs._defs;
      int defOffset = this._dataDefsOffset._defsOffset;
      int defSize = defs.size() + 5;
      defs.setSize(defSize);
      defs.setElementAt(6, defOffset + 0);
      int uniqueCode = this._uniqueCodeGenerator.generateCode(data.getName());
      defs.setElementAt(uniqueCode, defOffset + 1);
      defs.setElementAt(-1, defOffset + 2);
      Vector fields = data.getFields();
      int bits = 0;
      if (data.hasKey()) {
         bits |= 8;
         if (!data.isPersist()) {
            bits |= 1;
         }
      }

      defs.setElementAt(bits, defOffset + 3);
      IntVector varData = this._dataDefs._varData;
      int varOffset = this._dataDefsOffset._varDataOffset;
      defs.setElementAt(varOffset, defOffset + 4);
      int numberOfFields = fields.size();
      int varDataSize = varData.size() + 6 * numberOfFields + 1;
      varData.setSize(varDataSize);
      varData.setElementAt(numberOfFields, varOffset + 0);
      varOffset++;
      int i = 0;

      for (int j = numberOfFields; i < j; i++) {
         FieldElement df = (FieldElement)fields.elementAt(i);
         this.encodeFieldElement(data, df, varOffset + i * 6);
      }

      this._dataDefsOffset._varDataOffset = varDataSize;
      this._dataDefsOffset._defsOffset = defSize;
      return false;
   }

   @Override
   public final boolean visitEnumerationElement(EnumerationElement enumeration) {
      IntVector defs = this._dataDefs._defs;
      int defOffset = this._dataDefsOffset._defsOffset;
      int defsSize = defs.size() + 3;
      defs.setSize(defsSize);
      defs.setElementAt(5, defOffset + 0);
      int uniqueCode = this._uniqueCodeGenerator.generateCode(enumeration.getName());
      defs.setElementAt(uniqueCode, defOffset + 1);
      int objectOffset = this._dataDefs._objectData.size();
      defs.setElementAt(objectOffset, defOffset + 2);
      Vector objectData = this._dataDefs._objectData;
      Vector enums = enumeration.getValues();
      String[] enumValues = new String[enums.size()];
      enums.copyInto(enumValues);
      objectData.addElement(enumValues);
      this._dataDefsOffset._defsOffset = defsSize;
      return false;
   }

   @Override
   public final boolean visitWicletElement(WicletElement wiclet) {
      Vector globalElements = wiclet.getGlobalElements();
      if (globalElements != null && !globalElements.isEmpty()) {
         this.encodeGlobals(wiclet);
         return true;
      } else {
         this._globalDataDefId = -1;
         return true;
      }
   }

   private final void encodeFieldElement(DataElement data, FieldElement field, int offset) {
      IntVector varData = this._dataDefs._varData;
      varData.setElementAt(ProvisioningHelper.encodeType(field.getType(), field.isArray()), offset + 0);
      varData.setElementAt(ProvisioningHelper.encodeDataFieldBits(data, field), offset + 1);
      long defaultValue = ProvisioningHelper.resolveDefaultValue(field, this._dataDefs._objectData);
      varData.setElementAt(ProvisioningHelper.encodeFieldDefaultHi(defaultValue), offset + 2);
      varData.setElementAt(ProvisioningHelper.encodeFieldDefaultLo(defaultValue), offset + 3);
      int uniqueCode = this._uniqueCodeGenerator.generateCode(field.getName());
      varData.setElementAt(uniqueCode, offset + 4);
      if (field.hasComponent()) {
         uniqueCode = this._uniqueCodeGenerator.generateCode(field.getComponentName());
         varData.setElementAt(uniqueCode, offset + 5);
      } else {
         varData.setElementAt(-1, offset + 5);
      }
   }

   private final void encodeGlobalElement(GlobalElement global, int offset) {
      IntVector varData = this._dataDefs._varData;
      varData.setElementAt(ProvisioningHelper.encodeType(global.getType(), global.isArray()), offset + 0);
      varData.setElementAt(ProvisioningHelper.encodeDataFieldBits(global), offset + 1);
      long defaultValue = ProvisioningHelper.resolveDefaultValue(global, this._dataDefs._objectData);
      varData.setElementAt(ProvisioningHelper.encodeFieldDefaultHi(defaultValue), offset + 2);
      varData.setElementAt(ProvisioningHelper.encodeFieldDefaultLo(defaultValue), offset + 3);
      int uniqueCode = this._uniqueCodeGenerator.generateCode(global.getName());
      varData.setElementAt(uniqueCode, offset + 4);
      if (global.hasComponent()) {
         uniqueCode = this._uniqueCodeGenerator.generateCode(global.getComponentName());
         varData.setElementAt(uniqueCode, offset + 5);
      } else {
         varData.setElementAt(-1, offset + 5);
      }
   }

   private final void encodeGlobals(WicletElement wiclet) {
      IntVector defs = this._dataDefs._defs;
      int defOffset = this._dataDefsOffset._defsOffset;
      int defSize = defs.size() + 5;
      defs.setSize(defSize);
      defs.setElementAt(6, defOffset + 0);
      this._globalDataDefId = this._uniqueCodeGenerator.generateCode("Global");
      defs.setElementAt(this._globalDataDefId, defOffset + 1);
      defs.setElementAt(-1, defOffset + 2);
      int dataBits = 4;
      dataBits |= this.areAllGlobalsNonPersistable(wiclet) ? 1 : 0;
      defs.setElementAt(dataBits, defOffset + 3);
      int varOffset = this._dataDefsOffset._varDataOffset;
      IntVector varData = this._dataDefs._varData;
      defs.setElementAt(varOffset, defOffset + 4);
      Vector globals = wiclet.getGlobalElements();
      int numberOfGlobals = globals.size();
      int newVarSize = varData.size() + 6 * numberOfGlobals + 1;
      varData.setSize(newVarSize);
      varData.setElementAt(numberOfGlobals, varOffset + 0);
      varOffset++;
      Enumeration enumeration = globals.elements();

      for (int i = 0; enumeration.hasMoreElements(); i++) {
         GlobalElement global = (GlobalElement)enumeration.nextElement();
         this.encodeGlobalElement(global, varOffset + i * 6);
      }

      this._dataDefsOffset._varDataOffset = newVarSize;
      this._dataDefsOffset._defsOffset = defSize;
   }

   private final boolean areAllGlobalsNonPersistable(WicletElement wiclet) {
      Vector globals = wiclet.getGlobalElements();
      GlobalElement ge = null;
      int i = 0;

      for (int j = globals.size(); i < j; i++) {
         ge = (GlobalElement)globals.elementAt(i);
         if (ge.isPersist()) {
            return false;
         }
      }

      return true;
   }
}
