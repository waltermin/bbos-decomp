package net.rim.wica.common.builtindata.componentdefn;

public class AttendeeCompDef extends BuiltinKeylessDataComponentDefinition {
   public static final String ATTENDEE_COMP_NAME;
   public static final int TYPE_ID;
   public static final String TYPE;
   public static final int TYPE_TYPE;
   public static final int ADDRESS_ID;
   public static final String ADDRESS;
   public static final int ADDRESS_TYPE;
   private static final int[] _fieldTypes = new int[]{5, 3, -804651005, 5, 6, 7, -804651007, 9};
   private static final String[] _fieldNames = new String[]{"type", "address"};
   public static final int ATTENDEE_NUM_OBJECT_FIELDS;
   public static final int ATTENDEE_NUM_INT_FIELDS;
   private static AttendeeCompDef _instance;

   public static AttendeeCompDef getInstance() {
      if (_instance == null) {
         _instance = new AttendeeCompDef();
      }

      return _instance;
   }

   protected AttendeeCompDef() {
   }

   @Override
   public String[] getFieldNames() {
      return _fieldNames;
   }

   @Override
   public int getIntDefaultValue(int fieldIndex) {
      return fieldIndex == 0 ? 1 : super.getIntDefaultValue(fieldIndex);
   }

   @Override
   public String getComponentName() {
      return "Attendee";
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
      return 6;
   }

   @Override
   public int getFieldReferenceType(int fieldId) {
      return fieldId == 0 ? 132 : super.getFieldReferenceType(fieldId);
   }

   @Override
   public int getAccessType(int field) {
      return field == 0 ? 536870912 : super.getAccessType(field);
   }
}
