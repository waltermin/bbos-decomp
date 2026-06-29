package net.rim.wica.common.builtindata.componentdefn;

public class EventCompDef extends DataComponentDefinition {
   public static final String EVENT_COMP_NAME = "Event";
   public static final int UID_ID = 0;
   public static final int[] KEYS = new int[]{0, -804651005, 1, 3};
   public static final String UID = "uid";
   public static final int UID_TYPE = 1;
   public static final int SUMMARY_ID = 1;
   public static final String SUMMARY = "summary";
   public static final int SUMMARY_TYPE = 3;
   public static final int LOCATION_ID = 2;
   public static final String LOCATION = "location";
   public static final int LOCATION_TYPE = 3;
   public static final int NOTE_ID = 3;
   public static final String NOTE = "note";
   public static final int NOTE_TYPE = 3;
   public static final int START_ID = 4;
   public static final String START = "start";
   public static final int START_TYPE = 4;
   public static final int END_ID = 5;
   public static final String END = "end";
   public static final int END_TYPE = 4;
   public static final int ALARM_ID = 6;
   public static final String ALARM = "alarm";
   public static final int ALARM_TYPE = 4;
   public static final int REPEAT_RULE_ID = 7;
   public static final String REPEAT_RULE = "repeatRule";
   public static final int REPEAT_RULE_TYPE = 6;
   public static final int ATTENDEES_ID = 8;
   public static final String ATTENDEES = "attendees";
   public static final int ATTENDEES_TYPE = 32774;
   public static final int FREE_BUSY_ID = 9;
   public static final String FREE_BUSY = "free_busy";
   public static final int FREE_BUSY_TYPE = 5;
   public static final int ALLDAY_ID = 10;
   public static final String ALLDAY = "allday";
   public static final int ALLDAY_TYPE = 0;
   private static final int[] _fieldTypes = new int[]{
      1,
      3,
      3,
      3,
      4,
      4,
      4,
      6,
      32774,
      5,
      0,
      -804651007,
      2,
      -804651002,
      3,
      3,
      1,
      5,
      4,
      5,
      -804651004,
      3,
      3,
      3,
      3,
      -804651002,
      3,
      3,
      3,
      3,
      3,
      3,
      -804650998,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      5,
      5,
      1,
      -804650981
   };
   private static final String[] _fieldNames = new String[]{
      "uid", "summary", "location", "note", "start", "end", "alarm", "repeatRule", "attendees", "free_busy", "allday"
   };
   public static final int EVENT_NUM_OBJECT_FIELDS = 4;
   public static final int EVENT_NUM_INT_FIELDS = 2;
   public static final int EVENT_NUM_LONG_FIELDS = 3;
   public static final int EVENT_NUM_BOOLEAN_FIELDS = 1;
   private static EventCompDef _instance;

   public static EventCompDef getInstance() {
      if (_instance == null) {
         _instance = new EventCompDef();
      }

      return _instance;
   }

   protected EventCompDef() {
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
      return "Event";
   }

   @Override
   public int getId() {
      return 3;
   }

   @Override
   public int getFieldReferenceType(int fieldId) {
      int retValue = -1;
      switch (fieldId) {
         case 7:
         default:
            return 4;
         case 8:
            return 6;
         case 9:
            retValue = 131;
         case 6:
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
      return fieldIndex == 9 ? 2 : super.getIntDefaultValue(fieldIndex);
   }
}
