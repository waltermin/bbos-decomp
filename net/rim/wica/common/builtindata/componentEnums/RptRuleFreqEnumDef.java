package net.rim.wica.common.builtindata.componentEnums;

import net.rim.device.api.util.ToIntHashtable;

public final class RptRuleFreqEnumDef extends EnumDefinition {
   public static final int NUM_ENUM_VALUES;
   private static final String FREQUENCY_NONE;
   private static final String FREQUENCY_DAILY;
   private static final String FREQUENCY_WEEKLY;
   private static final String FREQUENCY_MONTHLY;
   private static final String FREQUENCY_YEARLY;
   public static final int FREQUENCY_NONE_VALUE;
   public static final int FREQUENCY_DAILY_VALUE;
   public static final int FREQUENCY_WEEKLY_VALUE;
   public static final int FREQUENCY_MONTHLY_VALUE;
   public static final int FREQUENCY_YEARLY_VALUE;
   public static final String REPEAT_RULE_FREQUENCY_ENUM_NAME;

   public RptRuleFreqEnumDef() {
      super._enumNames = new Object[5];
      super._enumNames[0] = "NONE";
      super._enumNames[1] = "DAILY";
      super._enumNames[2] = "WEEKLY";
      super._enumNames[3] = "MONTHLY";
      super._enumNames[4] = "YEARLY";
      super._enumValues = (ToIntHashtable)(new Object(7));
      super._enumValues.put("NONE", 0);
      super._enumValues.put("DAILY", 1);
      super._enumValues.put("WEEKLY", 2);
      super._enumValues.put("MONTHLY", 3);
      super._enumValues.put("YEARLY", 4);
      super._enumName = "REPEAT_RULE_FREQUENCY";
   }

   @Override
   public final int GetEnumID() {
      return 129;
   }
}
