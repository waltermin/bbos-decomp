package net.rim.device.api.gps;

final class GPSRegistry$PDEInfoStatus {
   GPS$GPSPDEInfo _pdeInfo;
   boolean _pdeStatus;
   boolean _credentialStatus;

   private GPSRegistry$PDEInfoStatus(GPS$GPSPDEInfo info, boolean pStatus, boolean credStatus) {
      this._pdeInfo = info;
      this._pdeStatus = pStatus;
      this._credentialStatus = credStatus;
   }

   public final void setCredStatus(boolean status) {
      this._credentialStatus = status;
   }

   public final boolean getCredStatus() {
      return this._credentialStatus;
   }

   public final boolean getPDEStatus() {
      return this._pdeStatus;
   }

   public final GPS$GPSPDEInfo getPDEInfo() {
      return this._pdeInfo;
   }

   GPSRegistry$PDEInfoStatus(GPS$GPSPDEInfo x0, boolean x1, boolean x2, GPSRegistry$1 x3) {
      this(x0, x1, x2);
   }
}
