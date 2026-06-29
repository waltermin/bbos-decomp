package net.rim.wica.common.builtindata.componentEnums;

import net.rim.device.api.util.ToIntHashtable;

public final class StatusEnumDef extends EnumDefinition {
   public static final int NUM_ENUM_VALUES;
   private static final String STATUS_NOT_STARTED;
   private static final String STATUS_IN_PROGRESS;
   private static final String STATUS_COMPLETED;
   private static final String STATUS_WAITING;
   private static final String STATUS_DEFERRED;
   public static final int STATUS_NOT_STARTED_VALUE;
   public static final int STATUS_IN_PROGRESS_VALUE;
   public static final int STATUS_COMPLETED_VALUE;
   public static final int STATUS_WAITING_VALUE;
   public static final int STATUS_DEFERRED_VALUE;
   public static final String STATUS_ENUM_NAME;

   public StatusEnumDef() {
      super._enumNames = new Object[5];
      super._enumNames[0] = "NOT_STARTED";
      super._enumNames[1] = "IN_PROGRESS";
      super._enumNames[2] = "COMPLETED";
      super._enumNames[3] = "WAITING";
      super._enumNames[4] = "DEFERRED";
      super._enumValues = (ToIntHashtable)(new Object(7));
      super._enumValues.put("NOT_STARTED", 0);
      super._enumValues.put("IN_PROGRESS", 1);
      super._enumValues.put("COMPLETED", 2);
      super._enumValues.put("WAITING", 3);
      super._enumValues.put("DEFERRED", 4);
      super._enumName = "STATUS";
   }

   @Override
   public final int GetEnumID() {
      return 128;
   }
}
