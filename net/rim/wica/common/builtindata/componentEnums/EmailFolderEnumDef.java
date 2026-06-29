package net.rim.wica.common.builtindata.componentEnums;

import net.rim.device.api.util.ToIntHashtable;

public final class EmailFolderEnumDef extends EnumDefinition {
   public static final int NUM_ENUM_VALUES;
   private static final String INBOX;
   private static final String SENT;
   public static final int INBOX_VALUE;
   public static final int SENT_VALUE;
   public static final String FOLDER_ENUM_NAME;

   public EmailFolderEnumDef() {
      super._enumNames = new Object[2];
      super._enumNames[0] = "INBOX";
      super._enumNames[1] = "SENT";
      super._enumValues = (ToIntHashtable)(new Object(3));
      super._enumValues.put("INBOX", 0);
      super._enumValues.put("SENT", 1);
      super._enumName = "FOLDER";
   }

   @Override
   public final int GetEnumID() {
      return 133;
   }
}
