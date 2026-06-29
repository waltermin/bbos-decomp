package net.rim.wica.common.builtindata.componentdefn;

public class EventCompDef extends DataComponentDefinition {
   public static final String EVENT_COMP_NAME;
   public static final int UID_ID;
   public static final int[] KEYS = new int[]{0, -804651005, 1, 3};
   public static final String UID;
   public static final int UID_TYPE;
   public static final int SUMMARY_ID;
   public static final String SUMMARY;
   public static final int SUMMARY_TYPE;
   public static final int LOCATION_ID;
   public static final String LOCATION;
   public static final int LOCATION_TYPE;
   public static final int NOTE_ID;
   public static final String NOTE;
   public static final int NOTE_TYPE;
   public static final int START_ID;
   public static final String START;
   public static final int START_TYPE;
   public static final int END_ID;
   public static final String END;
   public static final int END_TYPE;
   public static final int ALARM_ID;
   public static final String ALARM;
   public static final int ALARM_TYPE;
   public static final int REPEAT_RULE_ID;
   public static final String REPEAT_RULE;
   public static final int REPEAT_RULE_TYPE;
   public static final int ATTENDEES_ID;
   public static final String ATTENDEES;
   public static final int ATTENDEES_TYPE;
   public static final int FREE_BUSY_ID;
   public static final String FREE_BUSY;
   public static final int FREE_BUSY_TYPE;
   public static final int ALLDAY_ID;
   public static final String ALLDAY;
   public static final int ALLDAY_TYPE;
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
   public static final int EVENT_NUM_OBJECT_FIELDS;
   public static final int EVENT_NUM_INT_FIELDS;
   public static final int EVENT_NUM_LONG_FIELDS;
   public static final int EVENT_NUM_BOOLEAN_FIELDS;
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
