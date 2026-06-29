package net.rim.wica.common.builtindata.componentdefn;

public class MemoCompDef extends DataComponentDefinition {
   public static final String MEMO_COMP_NAME = "Memo";
   public static final int UID_ID = 0;
   public static final int[] KEYS = new int[]{0, -804651005, 1, 3};
   public static final String UID = "uid";
   public static final int UID_TYPE = 1;
   public static final int TITLE_ID = 1;
   public static final String TITLE = "title";
   public static final int TITLE_TYPE = 3;
   public static final int BODY_ID = 2;
   public static final String BODY = "body";
   public static final int BODY_TYPE = 3;
   private static final int[] _fieldTypes = new int[]{1, 3, 3, -804650997, 1, 3, 3, 3, 4, 4, 4, 6};
   private static final String[] _fieldNames = new String[]{"uid", "title", "body"};
   public static final int CONTACT_NUM_OBJECT_FIELDS = 2;
   public static final int CONTACT_NUM_INT_FIELDS = 1;
   private static MemoCompDef _instance;

   public static MemoCompDef getInstance() {
      if (_instance == null) {
         _instance = new MemoCompDef();
      }

      return _instance;
   }

   protected MemoCompDef() {
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
      return "Memo";
   }

   @Override
   public int getId() {
      return 9;
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
}
