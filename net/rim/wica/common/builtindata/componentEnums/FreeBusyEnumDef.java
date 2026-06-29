package net.rim.wica.common.builtindata.componentEnums;

import net.rim.device.api.util.ToIntHashtable;

public final class FreeBusyEnumDef extends EnumDefinition {
   public static final int NUM_ENUM_VALUES = 4;
   private static final String FREEBUSY_FREE = "FREE";
   private static final String FREEBUSY_TENTATIVE = "TENTATIVE";
   private static final String FREEBUSY_BUSY = "BUSY";
   private static final String FREEBUSY_OUT_OF_OFFICE = "OUT_OF_OFFICE";
   public static final int FREEBUSY_FREE_VALUE = 0;
   public static final int FREEBUSY_TENTATIVE_VALUE = 1;
   public static final int FREEBUSY_BUSY_VALUE = 2;
   public static final int FREEBUSY_OUT_OF_OFFICE_VALUE = 3;
   public static final String FREEBUSY_ENUM_NAME = "FREEBUSY";

   public FreeBusyEnumDef() {
      super._enumNames = new Object[4];
      super._enumNames[0] = "FREE";
      super._enumNames[1] = "TENTATIVE";
      super._enumNames[2] = "BUSY";
      super._enumNames[3] = "OUT_OF_OFFICE";
      super._enumValues = (ToIntHashtable)(new Object(6));
      super._enumValues.put("FREE", 0);
      super._enumValues.put("TENTATIVE", 1);
      super._enumValues.put("BUSY", 2);
      super._enumValues.put("OUT_OF_OFFICE", 3);
      super._enumName = "FREEBUSY";
   }

   @Override
   public final int GetEnumID() {
      return 131;
   }
}
