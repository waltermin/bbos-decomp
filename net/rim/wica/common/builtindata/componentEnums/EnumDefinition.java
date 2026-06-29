package net.rim.wica.common.builtindata.componentEnums;

import net.rim.device.api.util.ToIntHashtable;

public class EnumDefinition {
   protected String[] _enumNames;
   protected String _enumName;
   ToIntHashtable _enumValues;

   public int GetEnumID() {
      throw null;
   }

   public String getEnumName() {
      return this._enumName;
   }

   public String[] getEnum() {
      return this._enumNames;
   }

   public boolean isValidEnumValue(String value) {
      return this._enumValues.containsKey(value);
   }

   public int getEnumValueAsInt(String enumValue) {
      return this._enumValues.get(enumValue);
   }
}
