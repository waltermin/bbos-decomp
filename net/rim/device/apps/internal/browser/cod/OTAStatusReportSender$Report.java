package net.rim.device.apps.internal.browser.cod;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;

final class OTAStatusReportSender$Report implements Persistable {
   private Object _url;
   public int _status;
   public String _transportServiceCID;
   public int _numAttempts;

   public OTAStatusReportSender$Report(String url, int status, String transportServiceCID) {
      this._url = PersistentContent.encode(url);
      this._status = status;
      this._transportServiceCID = transportServiceCID;
   }

   public final String getURL() {
      return PersistentContent.decodeString(this._url);
   }
}
