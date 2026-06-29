package net.rim.device.apps.internal.mms.model;

import net.rim.device.api.util.Persistable;
import net.rim.device.apps.internal.mms.api.MMSStatusReport;

class MMSStatusReportImpl implements MMSStatusReport, Persistable {
   private String _address;
   private long _date;
   private int _status;

   public MMSStatusReportImpl(String address, long date, int status) {
      this._address = address;
      this._date = date;
      this._status = status;
   }

   @Override
   public String getAddress() {
      return this._address;
   }

   @Override
   public long getDate() {
      return this._date;
   }

   @Override
   public int getStatus() {
      return this._status;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof MMSStatusReportImpl)) {
         return false;
      } else {
         MMSStatusReportImpl other = (MMSStatusReportImpl)obj;
         if (other._address == this._address) {
            return true;
         } else {
            return other._address == null ? false : other._address.equals(this._address);
         }
      }
   }
}
