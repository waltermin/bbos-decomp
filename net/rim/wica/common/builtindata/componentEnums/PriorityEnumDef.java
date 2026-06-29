package net.rim.wica.common.builtindata.componentEnums;

import net.rim.device.api.util.ToIntHashtable;

public final class PriorityEnumDef extends EnumDefinition {
   public static final int NUM_ENUM_VALUES;
   private static final String PRIORITY_HIGH;
   private static final String PRIORITY_NORMAL;
   private static final String PRIORITY_LOW;
   public static final int PRIORITY_HIGH_VALUE;
   public static final int PRIORITY_NORMAL_VALUE;
   public static final int PRIORITY_LOW_VALUE;
   public static final String PRIORITY_ENUM_NAME;

   public PriorityEnumDef() {
      super._enumNames = new Object[3];
      super._enumNames[0] = "HIGH";
      super._enumNames[1] = "NORMAL";
      super._enumNames[2] = "LOW";
      super._enumValues = (ToIntHashtable)(new Object(4));
      super._enumValues.put("HIGH", 0);
      super._enumValues.put("NORMAL", 1);
      super._enumValues.put("LOW", 2);
      super._enumName = "PRIORITY";
   }

   @Override
   public final int GetEnumID() {
      return 130;
   }
}
