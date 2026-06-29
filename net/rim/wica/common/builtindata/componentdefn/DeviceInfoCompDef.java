package net.rim.wica.common.builtindata.componentdefn;

public class DeviceInfoCompDef extends DataComponentDefinition {
   public static final String DEVICE_INFO_COMP_NAME = "DeviceInfo";
   public static final int IMEI_ID = 0;
   public static final String IMEI = "IMEI";
   public static final int IMEI_TYPE = 3;
   public static final int IMSI_ID = 1;
   public static final String IMSI = "IMSI";
   public static final int IMSI_TYPE = 3;
   public static final int PIN_ID = 2;
   public static final String PIN = "PIN";
   public static final int PIN_TYPE = 3;
   public static final int PHONE_NUMBER_ID = 3;
   public static final String PHONE_NUMBER = "phoneNumber";
   public static final int PHONE_NUMBER_TYPE = 3;
   public static final int DEVICE_INFO_NUM_OBJECT_FIELDS = 4;
   private static final int[] _fieldTypes = new int[]{3, 3, 3, 3, -804651002, 3, 3, 3, 3, 3, 3, -804650998, 3, 3, 3, 3};
   private static final String[] _fieldNames = new String[]{"IMEI", "IMSI", "PIN", "phoneNumber"};
   private static DeviceInfoCompDef _instance;

   public static DeviceInfoCompDef getInstance() {
      if (_instance == null) {
         _instance = new DeviceInfoCompDef();
      }

      return _instance;
   }

   protected DeviceInfoCompDef() {
   }

   @Override
   public String[] getFieldNames() {
      return _fieldNames;
   }

   @Override
   public String getComponentName() {
      return "DeviceInfo";
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
      return 8;
   }

   @Override
   public boolean hasKey() {
      return false;
   }

   @Override
   public int[] getKeyFields() {
      return null;
   }
}
