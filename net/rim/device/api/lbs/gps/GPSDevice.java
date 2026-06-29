package net.rim.device.api.lbs.gps;

public class GPSDevice {
   protected byte _status = 0;
   protected Object _deviceID;
   protected String _name;
   protected String _friendlyName;
   protected GPSLocationData _locationDataInternal;
   protected static final long UID = 4560142210062134028L;
   public static final long INTERNAL_GPS_GUID = -5162649070632360034L;
   public static final byte NOT_REPORTING = 0;
   public static final byte REPORTING = 1;
   public static final byte WAITING = 2;
   public static final byte FORCED_STOP = 4;
   public static final byte RETRYING = 8;
   public static final byte ERROR = 10;
   public static final NoDevice NO_DEVICE = new NoDevice(null);

   private GPSDevice() {
   }

   protected GPSDevice(Object activationParam) {
   }

   @Override
   public final String toString() {
      return this._friendlyName != null ? this._friendlyName : this._name;
   }

   public final String getName() {
      return this._name;
   }

   public final Object getDeviceID() {
      return this._deviceID;
   }

   public final byte getDeviceState() {
      return this._status;
   }

   public void setDeviceToUse() {
      throw null;
   }

   public boolean messageDevice(Object _1) {
      throw null;
   }

   protected boolean startReporting() {
      throw null;
   }

   protected boolean stopReporting() {
      throw null;
   }

   protected GPSLocationData singleFix() {
      throw null;
   }

   protected String getChecksum(String sentence) {
      int checksum = 0;
      byte[] bytes = sentence.getBytes();
      int i = 0;

      while (i < bytes.length) {
         switch (bytes[i]) {
            case 42:
               return ((StringBuffer)(new Object())).append(checksum > 15 ? "" : "0").append(Integer.toHexString(checksum)).toString();
            default:
               if (checksum == 0) {
                  checksum = bytes[i];
               } else {
                  checksum ^= bytes[i];
               }
            case 36:
               i++;
         }
      }

      return "00";
   }

   protected final void updateData(boolean fireEvent) {
      GPSProvider.getInstance().updateLocationData(true);
   }

   public final void fireDeviceEvent(GPSDevice device, String message) {
      GPSProvider.getInstance().fireLocationDeviceEvent(device, message);
   }

   protected final void setGPSinvalid() {
      this._locationDataInternal._isValid = false;
      GPSProvider.getInstance().updateLocationData(true);
   }

   public final void setGPSDataValues(int lat, int lon, float bearing, float speed, boolean isValid) {
      this.setGPSDataValues(this._locationDataInternal, lat, lon, bearing, speed, isValid);
   }

   public final void setGPSDataValues(GPSLocationData data, int lat, int lon, float bearing, float speed, boolean isValid) {
      data._latitude = lat;
      data._longitude = lon;
      data._bearing = bearing;
      data._speed = speed;
      data._isValid = isValid;
   }

   public final void setAccuracy(float vertical, float horizontal) {
      this.setAccuracy(this._locationDataInternal, vertical, horizontal);
   }

   public final void setAccuracy(GPSLocationData data, float vertical, float horizontal) {
      data._vAccuracy = vertical;
      data._hAccuracy = horizontal;
   }

   protected final String getString(int resourceID) {
      return GPSProvider.getString(resourceID);
   }

   public boolean isInternalGPS() {
      throw null;
   }
}
