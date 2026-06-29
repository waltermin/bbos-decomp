package net.rim.wica.common.builtindata.componentdefn;

public class EmailCompDef extends DataComponentDefinition {
   public static final String MESSAGE_COMP_NAME;
   public static final int SUBJECT_ID;
   public static final String SUBJECT;
   public static final int SUBJECT_TYPE;
   public static final int CONTENT_ID;
   public static final String CONTENT;
   public static final int CONTENT_TYPE;
   public static final int FROM_ID;
   public static final String FROM;
   public static final int FROM_TYPE;
   public static final int REPLY_TO_ID;
   public static final String REPLY_TO;
   public static final int REPLY_TO_TYPE;
   public static final int TO_RECIPIENTS_ID;
   public static final String TO_RECIPIENTS;
   public static final int TO_RECIPIENTS_TYPE;
   public static final int CC_RECIPIENTS_ID;
   public static final String CC_RECIPIENTS;
   public static final int CC_RECIPIENTS_TYPE;
   public static final int BCC_RECIPIENTS_ID;
   public static final String BCC_RECIPIENTS;
   public static final int BCC_RECIPIENTS_TYPE;
   public static final int FOLDER_ID;
   public static final String FOLDER;
   public static final int FOLDER_TYPE;
   public static final int PRIORITY_ID;
   public static final String PRIORITY;
   public static final int PRIORITY_TYPE;
   public static final int UID_ID;
   public static final int[] KEYS = new int[]{9, -804651007, 19, 51};
   public static final String UID;
   public static final int UID_TYPE;
   private static final int[] _fieldTypes = new int[]{
      3, 3, 3, 3, 3, 3, 3, 5, 5, 1, -804650981, 3, 3, 3, 6, 6, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 3, 3, 3, 3, 3, 3, 3, -804651005, 5
   };
   private static final String[] _fieldNames = new String[]{
      "subject", "content", "from", "replyTo", "toRecipients", "ccRecipients", "bccRecipients", "folder", "priority", "uid"
   };
   public static final int MESSAGE_NUM_OBJECT_FIELDS;
   public static final int MESSAGE_NUM_INT_FIELDS;
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
