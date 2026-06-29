package net.rim.wica.common.builtindata.componentdefn;

public class RepeatRuleCompDef extends BuiltinKeylessDataComponentDefinition {
   public static final String REPEAT_RULE_COMP_NAME = "RepeatRule";
   public static final int FREQUENCY_ID = 0;
   public static final String FREQUENCY = "frequency";
   public static final int FREQUENCY_TYPE = 5;
   public static final int INTERVAL_ID = 1;
   public static final String INTERVAL = "interval";
   public static final int INTERVAL_TYPE = 1;
   public static final int END_ID = 2;
   public static final String END = "end";
   public static final int END_TYPE = 4;
   private static final int[] _fieldTypes = new int[]{5, 1, 4, -804651006, 5, 3, -804651005, 5, 6, 7, -804651007, 9};
   private static final String[] _fieldNames = new String[]{"frequency", "interval", "end"};
   public static final int REPEAT_RULE_NUM_INT_FIELDS = 2;
   public static final int REPEAT_RULE_NUM_LONG_FIELDS = 1;
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
