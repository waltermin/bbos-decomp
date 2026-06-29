package net.rim.wica.common.builtindata.componentdefn;

public class RepeatRuleCompDef extends BuiltinKeylessDataComponentDefinition {
   public static final String REPEAT_RULE_COMP_NAME;
   public static final int FREQUENCY_ID;
   public static final String FREQUENCY;
   public static final int FREQUENCY_TYPE;
   public static final int INTERVAL_ID;
   public static final String INTERVAL;
   public static final int INTERVAL_TYPE;
   public static final int END_ID;
   public static final String END;
   public static final int END_TYPE;
   private static final int[] _fieldTypes = new int[]{5, 1, 4, -804651006, 5, 3, -804651005, 5, 6, 7, -804651007, 9};
   private static final String[] _fieldNames = new String[]{"frequency", "interval", "end"};
   public static final int REPEAT_RULE_NUM_INT_FIELDS;
   public static final int REPEAT_RULE_NUM_LONG_FIELDS;
   private static RepeatRuleCompDef _instance;

   public static RepeatRuleCompDef getInstance() {
      if (_instance == null) {
         _instance = new RepeatRuleCompDef();
      }

      return _instance;
   }

   protected RepeatRuleCompDef() {
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
      return "RepeatRule";
   }

   @Override
   public int getId() {
      return 4;
   }

   @Override
   public int getFieldReferenceType(int fieldId) {
      int retValue = -1;
      switch (fieldId) {
         case 0:
            retValue = 129;
         default:
            return retValue;
      }
   }

   @Override
   public String[] getFieldNames() {
      return _fieldNames;
   }

   @Override
   public int getIntDefaultValue(int fieldIndex) {
      if (fieldIndex == 0) {
         return 0;
      } else {
         return fieldIndex == 1 ? 1 : super.getIntDefaultValue(fieldIndex);
      }
   }
}
