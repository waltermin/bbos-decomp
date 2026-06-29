package net.rim.wica.common.builtindata.componentdefn;

public class AddressCompDef extends BuiltinKeylessDataComponentDefinition {
   public static final String ADDRESS_COMP_NAME;
   public static final int ADDR_COUNTRY_ID;
   public static final String ADDR_COUNTRY;
   public static final int ADDR_COUNTRY_TYPE;
   public static final int ADDR_EXTRA_ID;
   public static final String ADDR_EXTRA;
   public static final int ADDR_EXTRA_TYPE;
   public static final int ADDR_LOCALITY_ID;
   public static final String ADDR_LOCALITY;
   public static final int ADDR_LOCALITY_TYPE;
   public static final int ADDR_POSTALCODE_ID;
   public static final String ADDR_POSTALCODE;
   public static final int ADDR_POSTALCODE_TYPE;
   public static final int ADDR_REGION_ID;
   public static final String ADDR_REGION;
   public static final int ADDR_REGION_TYPE;
   public static final int ADDR_STREET_ID;
   public static final String ADDR_STREET;
   public static final int ADDR_STREET_TYPE;
   public static final int ADDRESS_NUM_OBJECT_FIELDS;
   private static final int[] _fieldTypes = new int[]{3, 3, 3, 3, 3, 3, -804650998, 3, 3, 3, 3, 3, 3, 3, 5, 5, 1, -804650981, 3, 3, 3, 6, 6, 3};
   private static final String[] _fieldNames = new String[]{"addrCountry", "addrExtra", "addrLocality", "addrPostalcode", "addrRegion", "addrStreet"};
   private static AddressCompDef _instance;

   public static AddressCompDef getInstance() {
      if (_instance == null) {
         _instance = new AddressCompDef();
      }

      return _instance;
   }

   protected AddressCompDef() {
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
      return "Address";
   }

   @Override
   public int getId() {
      return 1;
   }

   @Override
   public String[] getFieldNames() {
      return _fieldNames;
   }
}
