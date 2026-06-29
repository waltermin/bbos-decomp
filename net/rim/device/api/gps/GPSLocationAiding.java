package net.rim.device.api.gps;

public class GPSLocationAiding {
   int _status;
   int _latitude;
   int _longitude;
   int _altitude;
   int _horUncertainty;
   int _altUncertainty;

   public void setStatus(int status) {
      this._status = status;
   }

   public void setLatitude(float latitude) {
      int value = (int)latitude * 1000000;
      if (value >= 0) {
         this._latitude = value;
      } else {
         value = 0 - value;
         this._latitude = -2147483648 | value;
      }
   }

   public void setLongitude(float longitude) {
      int value = (int)longitude * 1000000;
      if (value >= 0) {
         this._longitude = value;
      } else {
         value = 0 - value;
         this._longitude = -2147483648 | value;
      }
   }

   public void setAltitude(int altitude) {
      int value = altitude & 32767;
      if (altitude >= 0) {
         this._altitude = 32768 | value;
      } else {
         this._altitude = value;
      }
   }

   public void setHorUncertainty(float horUncertainty) {
      this._horUncertainty = (int)(horUncertainty * 1120403456);
   }

   public void setAltUncertainty(float altUncertainty) {
      this._altUncertainty = (int)(altUncertainty * 1120403456);
   }
}
