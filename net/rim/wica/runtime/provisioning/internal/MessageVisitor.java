package net.rim.wica.runtime.provisioning.internal;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.util.IntVector;
import net.rim.wica.runtime.lifecycle.Alert;
import net.rim.wica.runtime.metadata.internal.def.ComponentDefStruct;
import net.rim.wica.runtime.provisioning.internal.elements.AbstractElement;
import net.rim.wica.runtime.provisioning.internal.elements.AlertElement;
import net.rim.wica.runtime.provisioning.internal.elements.DataElement;
import net.rim.wica.runtime.provisioning.internal.elements.FieldElement;
import net.rim.wica.runtime.provisioning.internal.elements.GlobalElement;
import net.rim.wica.runtime.provisioning.internal.elements.MappedFieldElement;
import net.rim.wica.runtime.provisioning.internal.elements.MessageElement;
import net.rim.wica.runtime.provisioning.internal.elements.WicletElement;

public class MessageVisitor extends DefinitionVisitorAdapter {
   int _messageCount = 1;
   EncodingContext _msgDefs;
   EncodingContextOffset _msgDefsOffset;
   UniqueCodeGenerator _uniqueCodeGenerator;
   int _globalDataDefId;
   Vector _wicletAlerts;
   WicletElement _wicletElement;
   Hashtable _mappingResolvers;
   private static final int MAPPING_TO_GLOBAL_VAR_SIZE = 2;
   private static final int MAPPING_TO_DATA_VAR_SIZE = 1;

   public MessageVisitor(UniqueCodeGenerator ucg, int globalDataDefId, WicletElement we) {
      this._uniqueCodeGenerator = ucg;
      this._msgDefs = new EncodingContext();
      this._msgDefsOffset = new EncodingContextOffset();
      this._globalDataDefId = globalDataDefId;
      this._wicletAlerts = new Vector();
      this._wicletElement = we;
      this._mappingResolvers = new Hashtable();
   }

   public ComponentDefStruct getMessageDefs() {
      return this._msgDefs.toComponentDefStruct();
   }

   public Alert[] getAlerts() {
      Alert[] alerts = new Alert[this._wicletAlerts.size()];
      this._wicletAlerts.copyInto(alerts);
      return alerts;
   }

   private AlertElement findAlert(MessageElement me) {
      AlertElement result = null;
      AlertElement a = me.getAlert();
      if (a == null) {
         if (me.hasPrototype()) {
            return this.findAlert(me.getPrototype());
         }
      } else {
         result = a;
      }

      return result;
   }

   @Override
   public boolean visitMessageElement(MessageElement msg) {
      this.encodeMessageDefinition(msg);
      Vector fields = msg.getFields();
      this.expandVarDataArrray(fields);
      int varOffset = this._msgDefsOffset._varDataOffset;
      IntVector varData = this._msgDefs._varData;
      int numberOfFields = fields.size();
      varData.setElementAt(numberOfFields, varOffset + 0);
      varOffset++;
      AbstractElement mfe = null;
      int i = 0;

      for (int j = numberOfFields; i < j; i++) {
         mfe = (AbstractElement)fields.elementAt(i);
         this.encodeMessageField(mfe, varOffset + i * 7);
      }

      i = 0;
      int fieldMappingBegin = varOffset + numberOfFields * 7;
      int mappingOffset = varOffset + 6;
      AbstractElement ae = null;
      int ix = 0;

      for (int j = numberOfFields; ix < j; ix++) {
         ae = (AbstractElement)fields.elementAt(ix);
         i = mappingOffset + ix * 7;
         if (ae instanceof FieldElement) {
            varData.setElementAt(-1, i);
         } else {
            varData.setElementAt(fieldMappingBegin, i);
            fieldMappingBegin += this.encodeMapping((MappedFieldElement)ae, fieldMappingBegin);
         }
      }

      this._msgDefsOffset._varDataOffset = this._msgDefs._varData.size();
      this._msgDefsOffset._defsOffset = this._msgDefs._defs.size();
      AlertElement alert = this.findAlert(msg);
      if (alert != null) {
         Alert a = new Alert(msg.getMessageCode(), true, alert.isRibbon(), alert.getDialogText());
         this._wicletAlerts.addElement(a);
      }

      return true;
   }

   private int calculateTotalMappingSize(Vector allMfields) {
      int totalMappingLength = 0;
      int i = 0;

      for (int j = allMfields.size(); i < j; i++) {
         AbstractElement ae = (AbstractElement)allMfields.elementAt(i);
         if (!(ae instanceof MappedFieldElement)) {
            if (ae instanceof FieldElement) {
               totalMappingLength++;
            }
         } else {
            MappedFieldElement mfe = (MappedFieldElement)ae;
            MappingResolver mr = this.getMappingResolver(mfe.getMapping());
            if (mr.isMappedToFieldElement() && mr.rootReferenceStartsWithGlobalElement()) {
               totalMappingLength += mr.mappingTokenCount() + 2;
            } else if (mr.isMappedToGlobalElement()) {
               totalMappingLength += 3;
            } else if (mr.isMappedToDataElement()) {
               totalMappingLength += 2;
            } else {
               totalMappingLength += mr.mappingTokenCount() + 1;
            }
         }
      }

      return totalMappingLength;
   }

   private MappingResolver getMappingResolver(String mapping) {
      MappingResolver result = null;
      if (!this._mappingResolvers.containsKey(mapping)) {
         result = new MappingResolver(this._wicletElement, mapping);
         this._mappingResolvers.put(mapping, result);
         return result;
      } else {
         return (MappingResolver)this._mappingResolvers.get(mapping);
      }
   }

   private void encodeMappingToFieldElement(MappedFieldElement mappedField, FieldElement mfield, int offset) {
      IntVector varData = this._msgDefs._varData;
      int msgFieldType = ProvisioningHelper.encodeType(mfield.getType(), mfield.isArray());
      varData.setElementAt(msgFieldType, offset + 0);
      int msgFieldBits = ProvisioningHelper.encodeMessageFieldBits(mfield);
      varData.setElementAt(msgFieldBits, offset + 1);
      long defaultValue = ProvisioningHelper.resolveDefaultValue(mfield, this._msgDefs._objectData);
      varData.setElementAt(ProvisioningHelper.encodeFieldDefaultHi(defaultValue), offset + 2);
      varData.setElementAt(ProvisioningHelper.encodeFieldDefaultLo(defaultValue), offset + 3);
      int uniqueCode = 0;
      if (mappedField != null) {
         uniqueCode = this._uniqueCodeGenerator.generateCode(mappedField.getName());
      } else {
         uniqueCode = this._uniqueCodeGenerator.generateCode(mfield.getName());
      }

      varData.setElementAt(uniqueCode, offset + 4);
      if (mfield.hasComponent()) {
         uniqueCode = this._uniqueCodeGenerator.generateCode(mfield.getComponentName());
         varData.setElementAt(uniqueCode, offset + 5);
      } else {
         varData.setElementAt(-1, offset + 5);
      }
   }

   private void encodeMappingToGlobalElement(MappedFieldElement mappedField, GlobalElement global, int offset) {
      IntVector varData = this._msgDefs._varData;
      varData.setElementAt(ProvisioningHelper.encodeType(global.getType(), global.isArray()), offset + 0);
      varData.setElementAt(ProvisioningHelper.encodeMessageFieldBits(global), offset + 1);
      long defaultValue = ProvisioningHelper.resolveDefaultValue(global, this._msgDefs._objectData);
      varData.setElementAt(ProvisioningHelper.encodeFieldDefaultHi(defaultValue), offset + 2);
      varData.setElementAt(ProvisioningHelper.encodeFieldDefaultLo(defaultValue), offset + 3);
      int uniqueCode = this._uniqueCodeGenerator.generateCode(mappedField.getName());
      varData.setElementAt(uniqueCode, offset + 4);
      if (global.hasComponent()) {
         uniqueCode = this._uniqueCodeGenerator.generateCode(global.getComponentName());
         varData.setElementAt(uniqueCode, offset + 5);
      } else {
         varData.setElementAt(-1, offset + 5);
      }
   }

   private int encodeMapping(MappedFieldElement mfield, int varDataOffset) {
      int mappingLength = 0;
      IntVector varData = this._msgDefs._varData;
      MappingResolver resolver = this.getMappingResolver(mfield.getMapping());
      if (resolver.isMappedToDataElement()) {
         mappingLength = 1;
         int uniqueCode = this._uniqueCodeGenerator.generateCode(mfield.getMapping());
         varData.setElementAt(mappingLength, varDataOffset);
         varData.setElementAt(uniqueCode, varDataOffset + 1);
      } else if (resolver.isMappedToGlobalElement()) {
         mappingLength = 2;
         GlobalElement global = (GlobalElement)resolver.resolveMapping();
         WicletElement wiclet = (WicletElement)global.getParent();
         int indexOfGlobalField = wiclet.indexOf(global);
         varData.setElementAt(mappingLength, varDataOffset);
         varData.setElementAt(this._globalDataDefId, varDataOffset + 1);
         varData.setElementAt(indexOfGlobalField, varDataOffset + 2);
      } else if (resolver.isMappedToFieldElement()) {
         Vector mappingPath = resolver.resolveFieldMapping();
         mappingLength = mappingPath.size();
         if (!resolver.rootReferenceStartsWithGlobalElement()) {
            if (resolver.mappingTokenCount() != mappingPath.size()) {
            }

            DataElement de = (DataElement)mappingPath.elementAt(0);
            int mIndex = 0;
            int uniqueCode = this._uniqueCodeGenerator.generateCode(de.getName());
            varData.setElementAt(mappingLength, varDataOffset + mIndex++);
            varData.setElementAt(uniqueCode, varDataOffset + mIndex++);
            int i = 1;

            for (int j = mappingPath.size(); i < j; i++) {
               FieldElement dfield = (FieldElement)mappingPath.elementAt(i);
               DataElement dparent = (DataElement)dfield.getParent();
               int fieldIndex = dparent.getFieldIndex(dfield.getName());
               varData.setElementAt(fieldIndex, varDataOffset + mIndex++);
            }
         } else {
            GlobalElement ge = (GlobalElement)mappingPath.elementAt(0);
            WicletElement we = (WicletElement)ge.getParent();
            int indexOfGlobal = we.indexOf(ge);
            int mIndex = 0;
            varData.setElementAt(++mappingLength, varDataOffset + mIndex++);
            varData.setElementAt(this._globalDataDefId, varDataOffset + mIndex++);
            varData.setElementAt(indexOfGlobal, varDataOffset + mIndex++);
            int i = 1;

            for (int j = mappingPath.size(); i < j; i++) {
               FieldElement dfield = (FieldElement)mappingPath.elementAt(i);
               DataElement dparent = (DataElement)dfield.getParent();
               int fieldIndex = dparent.getFieldIndex(dfield.getName());
               varData.setElementAt(fieldIndex, varDataOffset + mIndex++);
            }
         }
      }

      return mappingLength == 0 ? 0 : mappingLength + 1;
   }

   private void encodeMappingToDataElement(MappedFieldElement data, int offset) {
      IntVector varData = this._msgDefs._varData;
      varData.setElementAt(6, offset + 0);
      varData.setElementAt(0, offset + 1);
      varData.setElementAt(0, offset + 2);
      varData.setElementAt(-1, offset + 3);
      int uniqueCode = this._uniqueCodeGenerator.generateCode(data.getName());
      varData.setElementAt(uniqueCode, offset + 4);
      uniqueCode = this._uniqueCodeGenerator.generateCode(data.getMapping());
      varData.setElementAt(uniqueCode, offset + 5);
   }

   private void encodeMessageDefinition(MessageElement msg) {
      IntVector defs = this._msgDefs._defs;
      int defOffset = this._msgDefsOffset._defsOffset;
      int defsSize = defs.size() + 7;
      defs.setSize(defsSize);
      defs.setElementAt(9, defOffset + 0);
      int uniqueCode = this._uniqueCodeGenerator.generateCode(msg.getName());
      defs.setElementAt(uniqueCode, defOffset + 1);
      int isSecure = ProvisioningHelper.parseBoolean(msg.isSecure());
      defs.setElementAt(isSecure, defOffset + 2);
      if (msg.hasScript()) {
         uniqueCode = this._uniqueCodeGenerator.generateCode(msg.getScript().getName());
         defs.setElementAt(uniqueCode, defOffset + 3);
      } else {
         defs.setElementAt(-1, defOffset + 3);
      }

      int objectOffset = this._msgDefs._objectData.size();
      Vector objectData = this._msgDefs._objectData;
      objectData.addElement(msg.getName());
      defs.setElementAt(objectOffset, defOffset + 4);
      int varOffset = this._msgDefsOffset._varDataOffset;
      defs.setElementAt(varOffset, defOffset + 5);
      msg.setMessageCode(this._messageCount++);
      defs.setElementAt(msg.getMessageCode(), defOffset + 6);
   }

   private void encodeMessageField(AbstractElement ae, int offset) {
      if (!(ae instanceof MappedFieldElement)) {
         if (ae instanceof FieldElement) {
            this.encodeMappingToFieldElement(null, (FieldElement)ae, offset);
         }
      } else {
         MappedFieldElement mfe = (MappedFieldElement)ae;
         MappingResolver mr = this.getMappingResolver(mfe.getMapping());
         AbstractElement mappingTarget = mr.resolveMapping();
         if (mr.isMappedToDataElement()) {
            this.encodeMappingToDataElement(mfe, offset);
            return;
         }

         if (mr.isMappedToGlobalElement()) {
            this.encodeMappingToGlobalElement(mfe, (GlobalElement)mappingTarget, offset);
            return;
         }

         if (mr.isMappedToFieldElement()) {
            this.encodeMappingToFieldElement(mfe, (FieldElement)mappingTarget, offset);
            return;
         }
      }
   }

   private void expandVarDataArrray(Vector allMfields) {
      int totalMappingLength = this.calculateTotalMappingSize(allMfields);
      int numberOfFields = allMfields.size();
      int neededVarDataSize = 7 * numberOfFields + totalMappingLength;
      IntVector varData = this._msgDefs._varData;
      int expandedVarDataSize = varData.size() + neededVarDataSize + 1;
      varData.setSize(expandedVarDataSize);
   }
}
