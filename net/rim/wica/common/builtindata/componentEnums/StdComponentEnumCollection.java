package net.rim.wica.common.builtindata.componentEnums;

import net.rim.wica.common.metadata.component.EnumCollection;

public class StdComponentEnumCollection implements EnumCollection {
   private static EnumDefinition[] _enumerations = new EnumDefinition[]{
      new StatusEnumDef(), new RptRuleFreqEnumDef(), new PriorityEnumDef(), new FreeBusyEnumDef(), new AttendeeTypeEnumDef(), new EmailFolderEnumDef()
   };
   private static StdComponentEnumCollection _instance;

   public static StdComponentEnumCollection getInstance() {
      if (_instance == null) {
         _instance = new StdComponentEnumCollection();
      }

      return _instance;
   }

   private StdComponentEnumCollection() {
   }

   public static EnumDefinition[] getEnumDefs() {
      return _enumerations;
   }

   @Override
   public String[] getEnum(int defId) {
      EnumDefinition enumDef = this.getEnumDefinitionByID(defId);
      return enumDef != null ? enumDef.getEnum() : null;
   }

   @Override
   public boolean isValidEnumValue(int defId, String value) {
      EnumDefinition enumDef = this.getEnumDefinitionByID(defId);
      return enumDef != null ? enumDef.isValidEnumValue(value) : false;
   }

   @Override
   public int getEnumValueAsInt(int defId, String enumValue) {
      EnumDefinition enumDef = this.getEnumDefinitionByID(defId);
      return enumDef != null ? enumDef.getEnumValueAsInt(enumValue) : -1;
   }

   private EnumDefinition getEnumDefinitionByID(int defId) {
      int arrayIndex = defId - 128;
      return arrayIndex >= 0 && arrayIndex < _enumerations.length ? _enumerations[arrayIndex] : null;
   }
}
