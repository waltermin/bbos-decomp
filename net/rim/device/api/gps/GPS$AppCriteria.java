package net.rim.device.api.gps;

public class GPS$AppCriteria {
   int _prefRespTime;
   int _verticalAccuracy;
   int _horizontalAccuracy;
   boolean _costAllowed;
   int _powerUsage;

   public GPS$AppCriteria() {
   }

   public GPS$AppCriteria(int respTime, int vacc, int hacc, boolean cost, int power) {
      this._prefRespTime = respTime;
      this._verticalAccuracy = vacc;
      this._horizontalAccuracy = hacc;
      this._costAllowed = cost;
      this._powerUsage = power;
   }
}
