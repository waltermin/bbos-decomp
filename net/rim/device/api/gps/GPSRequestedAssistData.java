package net.rim.device.api.gps;

public class GPSRequestedAssistData {
   private boolean _almanac;
   private boolean _utcModel;
   private boolean _ionosphericModel;
   private boolean _navigationModel;
   private int _gpsWeek;
   private int _gpsToe;
   private int _numSatellites;
   private int _tToeLimit;
   private byte[] _satID;
   private byte[] _iode;
   private boolean _dGpsCorrections;
   private boolean _referenceLocation;
   private boolean _referenceTime;
   private boolean _acquisitionAssistance;
   private boolean _realTimeIntegrity;
   private int MAX_NUM_GPS_SATELLITE_INFO = 15;

   public GPSRequestedAssistData() {
      this.reset();
   }

   public void reset() {
      this._almanac = false;
      this._utcModel = false;
      this._ionosphericModel = false;
      this._navigationModel = false;
      this._gpsWeek = 0;
      this._gpsToe = 0;
      this._numSatellites = 0;
      this._tToeLimit = 0;
      this._satID = new byte[this.MAX_NUM_GPS_SATELLITE_INFO];
      this._iode = new byte[this.MAX_NUM_GPS_SATELLITE_INFO];
      this._dGpsCorrections = false;
      this._referenceLocation = false;
      this._referenceTime = false;
      this._acquisitionAssistance = false;
      this._realTimeIntegrity = false;
   }

   public boolean isAlmanac() {
      return this._almanac;
   }

   public boolean isUtcModel() {
      return this._utcModel;
   }

   public boolean isIonosphericModel() {
      return this._ionosphericModel;
   }

   public boolean isNavigationModel() {
      return this._navigationModel;
   }

   public int getGpsWeek() {
      return this._gpsWeek;
   }

   public int getGpsToe() {
      return this._gpsToe;
   }

   public int getNumSatellites() {
      return this._numSatellites;
   }

   public int getTToeLimit() {
      return this._tToeLimit;
   }

   public boolean isDGpsCorrections() {
      return this._dGpsCorrections;
   }

   public boolean isReferenceLocation() {
      return this._referenceLocation;
   }

   public boolean isReferenceTime() {
      return this._referenceTime;
   }

   public boolean isAcquisitionAssistance() {
      return this._acquisitionAssistance;
   }

   public boolean isRealTimeIntegrity() {
      return this._realTimeIntegrity;
   }

   public int getSatelliteID(int index) {
      return this._satID[index] & 0xFF;
   }

   public int getSatelliteIode(int index) {
      return this._iode[index] & 0xFF;
   }
}
