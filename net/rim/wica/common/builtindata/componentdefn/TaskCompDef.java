package net.rim.wica.common.builtindata.componentdefn;

public class TaskCompDef extends DataComponentDefinition {
   public static final String TASK_COMP_NAME = "Task";
   public static final int NOTE_ID = 0;
   public static final String NOTE = "note";
   public static final int NOTE_TYPE = 3;
   public static final int SUMMARY_ID = 1;
   public static final String SUMMARY = "summary";
   public static final int SUMMARY_TYPE = 3;
   public static final int UID_ID = 2;
   public static final int[] KEYS = new int[]{2, -804651002, 3, 3};
   public static final String UID = "uid";
   public static final int UID_TYPE = 1;
   public static final int PRIORITY_ID = 3;
   public static final String PRIORITY = "priority";
   public static final int PRIORITY_TYPE = 5;
   public static final int DUE_ID = 4;
   public static final String DUE = "due";
   public static final int DUE_TYPE = 4;
   public static final int STATUS_ID = 5;
   public static final String STATUS = "status";
   public static final int STATUS_TYPE = 5;
   public static final int TASK_NUM_OBJECT_FIELDS = 2;
   public static final int TASK_NUM_INT_FIELDS = 3;
   public static final int TASK_NUM_LONG_FIELDS = 1;
   private static final int[] _fieldTypes = new int[]{3, 3, 1, 5, 4, 5, -804651004, 3, 3, 3, 3, -804651002, 3, 3, 3, 3, 3, 3, -804650998, 3, 3, 3, 3, 3};
   private static final String[] _fieldNames = new String[]{"note", "summary", "uid", "priority", "due", "status"};
   private static TaskCompDef _instance;

   public static TaskCompDef getInstance() {
      if (_instance == null) {
         _instance = new TaskCompDef();
      }

      return _instance;
   }

   protected TaskCompDef() {
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
   public String getComponentName() {
      return "Task";
   }

   @Override
   public int getId() {
      return 5;
   }

   @Override
   public int getFieldReferenceType(int fieldId) {
      int retValue = -1;
      switch (fieldId) {
         case 3:
            return 130;
         case 5:
            retValue = 128;
         default:
            return retValue;
      }
   }

   @Override
   public String[] getFieldNames() {
      return _fieldNames;
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
   public int getIntDefaultValue(int fieldIndex) {
      int retValue = 0;
      switch (fieldIndex) {
         case 3:
            return 1;
         case 5:
            return 0;
         default:
            return super.getIntDefaultValue(fieldIndex);
      }
   }

   @Override
   public int getAccessType(int field) {
      return field == 2 ? 536870912 : super.getAccessType(field);
   }
}
