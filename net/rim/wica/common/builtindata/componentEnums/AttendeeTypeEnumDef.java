package net.rim.wica.common.builtindata.componentEnums;

import net.rim.device.api.util.ToIntHashtable;

public final class AttendeeTypeEnumDef extends EnumDefinition {
   public static final int NUM_ENUM_VALUES = 5;
   private static final String ORGANIZER = "ORGANIZER";
   private static final String INVITED = "INVITED";
   private static final String ACCEPTED = "ACCEPTED";
   private static final String DECLINED = "DECLINED";
   private static final String TENTATIVE = "TENTATIVE";
   public static final int ORGANIZER_VALUE = 0;
   public static final int INVITED_VALUE = 1;
   public static final int ACCEPTED_VALUE = 2;
   public static final int DECLINED_VALUE = 3;
   public static final int TENTATIVE_VALUE = 4;
   public static final String ATTENDEE_TYPE_ENUM_NAME = "ATTENDEE_TYPE";

   public AttendeeTypeEnumDef() {
      super._enumNames = new Object[5];
      super._enumNames[0] = "ORGANIZER";
      super._enumNames[1] = "INVITED";
      super._enumNames[2] = "ACCEPTED";
      super._enumNames[3] = "DECLINED";
      super._enumNames[4] = "TENTATIVE";
      super._enumValues = (ToIntHashtable)(new Object(7));
      super._enumValues.put("ORGANIZER", 0);
      super._enumValues.put("INVITED", 1);
      super._enumValues.put("ACCEPTED", 2);
      super._enumValues.put("DECLINED", 3);
      super._enumValues.put("TENTATIVE", 4);
      super._enumName = "ATTENDEE_TYPE";
   }

   @Override
   public final int GetEnumID() {
      return 132;
   }
}
