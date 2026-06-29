package net.rim.wica.common.builtindata.componentdefn;

public class EmailCompDef extends DataComponentDefinition {
   public static final String MESSAGE_COMP_NAME = "Message";
   public static final int SUBJECT_ID = 0;
   public static final String SUBJECT = "subject";
   public static final int SUBJECT_TYPE = 3;
   public static final int CONTENT_ID = 1;
   public static final String CONTENT = "content";
   public static final int CONTENT_TYPE = 3;
   public static final int FROM_ID = 2;
   public static final String FROM = "from";
   public static final int FROM_TYPE = 3;
   public static final int REPLY_TO_ID = 3;
   public static final String REPLY_TO = "replyTo";
   public static final int REPLY_TO_TYPE = 3;
   public static final int TO_RECIPIENTS_ID = 4;
   public static final String TO_RECIPIENTS = "toRecipients";
   public static final int TO_RECIPIENTS_TYPE = 3;
   public static final int CC_RECIPIENTS_ID = 5;
   public static final String CC_RECIPIENTS = "ccRecipients";
   public static final int CC_RECIPIENTS_TYPE = 3;
   public static final int BCC_RECIPIENTS_ID = 6;
   public static final String BCC_RECIPIENTS = "bccRecipients";
   public static final int BCC_RECIPIENTS_TYPE = 3;
   public static final int FOLDER_ID = 7;
   public static final String FOLDER = "folder";
   public static final int FOLDER_TYPE = 5;
   public static final int PRIORITY_ID = 8;
   public static final String PRIORITY = "priority";
   public static final int PRIORITY_TYPE = 5;
   public static final int UID_ID = 9;
   public static final int[] KEYS = new int[]{9, -804651007, 19, 51};
   public static final String UID = "uid";
   public static final int UID_TYPE = 1;
   private static final int[] _fieldTypes = new int[]{
      3, 3, 3, 3, 3, 3, 3, 5, 5, 1, -804650981, 3, 3, 3, 6, 6, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 3, 3, 3, 3, 3, 3, 3, -804651005, 5
   };
   private static final String[] _fieldNames = new String[]{
      "subject", "content", "from", "replyTo", "toRecipients", "ccRecipients", "bccRecipients", "folder", "priority", "uid"
   };
   public static final int MESSAGE_NUM_OBJECT_FIELDS = 7;
   public static final int MESSAGE_NUM_INT_FIELDS = 3;
   private static EmailCompDef _instance;

   public static EmailCompDef getInstance() {
      if (_instance == null) {
         _instance = new EmailCompDef();
      }

      return _instance;
   }

   protected EmailCompDef() {
   }

   @Override
   public boolean hasKey() {
      return true;
   }

   @Override
   public int[] getKeyFields() {
      return KEYS;
   }

   @Override
   public String[] getFieldNames() {
      return _fieldNames;
   }

   @Override
   public String getComponentName() {
      return "Message";
   }

   @Override
   public int getNumFields() {
      return _fieldTypes.length;
   }

   @Override
   public int getFieldType(int fieldIndex) {
      return fieldIndex >= 0 && fieldIndex <= _fieldTypes.length - 1 ? _fieldTypes[fieldIndex] : -1;
   }

   @Override
   public int getId() {
      return 7;
   }

   @Override
   public int getFieldReferenceType(int fieldId) {
      int retValue = -1;
      switch (fieldId) {
         case 7:
         default:
            return 133;
         case 8:
            retValue = 130;
         case 6:
            return retValue;
      }
   }

   @Override
   public int getAccessType(int field) {
      int accessType = 268435456;
      switch (field) {
         case 2:
         case 3:
         case 7:
         case 9:
            accessType = 536870912;
         default:
            return accessType;
      }
   }

   @Override
   public int getIntDefaultValue(int fieldIndex) {
      int retValue = 0;
      switch (fieldIndex) {
         case 6:
            return super.getIntDefaultValue(fieldIndex);
         case 7:
         default:
            return 1;
         case 8:
            return 1;
      }
   }
}
