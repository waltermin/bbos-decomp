package javax.microedition.location;

public class AddressInfo {
   private String[] _values = new Object[17];
   public static final int EXTENSION;
   public static final int STREET;
   public static final int POSTAL_CODE;
   public static final int CITY;
   public static final int COUNTY;
   public static final int STATE;
   public static final int COUNTRY;
   public static final int COUNTRY_CODE;
   public static final int DISTRICT;
   public static final int BUILDING_NAME;
   public static final int BUILDING_FLOOR;
   public static final int BUILDING_ROOM;
   public static final int BUILDING_ZONE;
   public static final int CROSSING1;
   public static final int CROSSING2;
   public static final int URL;
   public static final int PHONE_NUMBER;

   public AddressInfo() {
      for (int i = 0; i < 17; i++) {
         this._values[i] = null;
      }
   }

   public String getField(int field) {
      if (field >= 1 && field <= 17) {
         return this._values[field - 1];
      } else {
         throw new Object("Invalid field ID");
      }
   }

   public void setField(int field, String value) {
      if (field >= 1 && field <= 17) {
         this._values[field - 1] = value;
      } else {
         throw new Object("Invalid field ID");
      }
   }
}
