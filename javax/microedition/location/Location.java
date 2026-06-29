package javax.microedition.location;

public class Location {
   boolean _valid = true;
   private AddressInfo _addressInfo;
   private float _course = (float)2143289344;
   private QualifiedCoordinates _qualifiedCoordinates;
   private float _speed = (float)2143289344;
   private long _timeStamp;
   private int _locationMethod;
   private int _satellites;
   public static final int MTE_SATELLITE = 1;
   public static final int MTE_TIMEDIFFERENCE = 2;
   public static final int MTE_TIMEOFARRIVAL = 4;
   public static final int MTE_CELLID = 8;
   public static final int MTE_SHORTRANGE = 16;
   public static final int MTE_ANGLEOFARRIVAL = 32;
   public static final int MTY_TERMINALBASED = 65536;
   public static final int MTY_NETWORKBASED = 131072;
   public static final int MTA_ASSISTED = 262144;
   public static final int MTA_UNASSISTED = 524288;
   private static final String SAT_STRING = "satellites";

   protected Location() {
   }

   public boolean isValid() {
      return this._valid;
   }

   void setValid(boolean valid) {
      this._valid = valid;
   }

   public long getTimestamp() {
      return this._timeStamp;
   }

   void setTimestamp(long timeStamp) {
      this._timeStamp = timeStamp;
   }

   public QualifiedCoordinates getQualifiedCoordinates() {
      return this._qualifiedCoordinates;
   }

   void setCoordinates(QualifiedCoordinates coordinates) {
      this._qualifiedCoordinates = coordinates;
   }

   public float getSpeed() {
      return this._speed;
   }

   void setSpeed(float speed) {
      this._speed = speed;
   }

   public float getCourse() {
      return this._course;
   }

   void setCourse(float course) {
      this._course = course;
   }

   public int getLocationMethod() {
      return this._locationMethod;
   }

   void setLocationMethod(int locationMethod) {
      this._locationMethod = locationMethod;
   }

   public AddressInfo getAddressInfo() {
      return this._addressInfo;
   }

   void setAddressInfo(AddressInfo addressInfo) {
      this._addressInfo = addressInfo;
   }

   void setSatellites(int satellites) {
      this._satellites = satellites;
   }

   public String getExtraInfo(String mimetype) {
      if (mimetype.equals("satellites")) {
         return String.valueOf(this._satellites);
      }

      StringBuffer info = new StringBuffer();
      if (!mimetype.equals("application/X-jsr179-location-nmea")) {
         if (!mimetype.equals("application/X-jsr179-location-lif")) {
            if (mimetype.equals("text/plain")) {
               return this._valid ? "Valid Location." : "Invalid Location.";
            } else {
               return null;
            }
         } else {
            if (!this._valid) {
               return null;
            }

            info.append("lif:<pd><time>");
            info.append(this._timeStamp);
            info.append("</time><shape><Point><coord><X>");
            info.append(this._qualifiedCoordinates.getLongitude());
            info.append("</X><Y>");
            info.append(this._qualifiedCoordinates.getLatitude());
            info.append("</Y></coord></Point></shape><alt>");
            info.append(this._qualifiedCoordinates.getAltitude());
            info.append("</alt><alt_acc>");
            info.append(this._qualifiedCoordinates.getVerticalAccuracy());
            info.append("</alt_acc><speed>");
            info.append(this._speed);
            info.append("</speed><direction>");
            info.append(this._course);
            info.append("</direction></pd>");
            return info.toString();
         }
      } else {
         if (!this._valid) {
            return null;
         }

         String lonString = Coordinates.convert(this._qualifiedCoordinates.getLongitude(), 2);
         String latString = Coordinates.convert(this._qualifiedCoordinates.getLatitude(), 2);
         info.append("$GPGGA,");
         info.append(this._timeStamp);
         info.append(',');
         info.append(latString);
         info.append(',');
         if (latString.startsWith("-")) {
            info.append('S');
         } else {
            info.append('N');
         }

         info.append(',');
         info.append(lonString);
         info.append(',');
         if (lonString.startsWith("-")) {
            info.append('W');
         } else {
            info.append('E');
         }

         info.append(',');
         info.append('1');
         info.append(',');
         info.append(this._satellites);
         info.append(',');
         info.append(',');
         info.append(this._qualifiedCoordinates.getAltitude());
         info.append(',');
         info.append('M');
         info.append(",,");
         info.append("$GPGLL,");
         info.append(latString);
         info.append(',');
         if (latString.startsWith("-")) {
            info.append('S');
         } else {
            info.append('N');
         }

         info.append(',');
         info.append(lonString);
         info.append(',');
         if (lonString.startsWith("-")) {
            info.append('W');
         } else {
            info.append('E');
         }

         info.append(',');
         info.append(this._timeStamp);
         info.append(',');
         info.append('A');
         return info.toString();
      }
   }

   Location clone() {
      Location location = new Location();
      location._addressInfo = this._addressInfo;
      location._course = this._course;
      location._locationMethod = this._locationMethod;
      location._qualifiedCoordinates = this._qualifiedCoordinates;
      location._satellites = this._satellites;
      location._speed = this._speed;
      location._valid = this._valid;
      return location;
   }
}
