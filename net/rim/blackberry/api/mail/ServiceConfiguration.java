package net.rim.blackberry.api.mail;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public class ServiceConfiguration {
   private String _uid;
   private String _cid;
   ServiceRecord _serviceRecord;
   public static String NO_SERVICE_BOOK = EmailResources.getString(85);

   public ServiceConfiguration() {
   }

   public ServiceConfiguration(String uid, String cid) {
      this._uid = uid;
      this._cid = cid;
      this._serviceRecord = ServiceBook.getSB().getRecordByUidAndCid(uid, cid);
      if (null == this._serviceRecord) {
         throw new NoSuchServiceException();
      }
   }

   public ServiceConfiguration(ServiceRecord sr) {
      this._uid = sr.getUid();
      this._cid = sr.getCid();
      this._serviceRecord = sr;
   }

   public String getUID() {
      return this._uid;
   }

   public String getCID() {
      return this._cid;
   }

   ServiceRecord getServiceRecord() {
      return this._serviceRecord;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (!(o instanceof ServiceConfiguration)) {
         return false;
      }

      ServiceConfiguration sc = (ServiceConfiguration)o;
      return StringUtilities.strEqualIgnoreCase(sc._uid, this._uid) && StringUtilities.strEqualIgnoreCase(sc._cid, this._cid);
   }

   @Override
   public int hashCode() {
      int result = 17;
      result = result * 37 + StringUtilities.hashCodeIgnoreCase(this._uid);
      return result * 37 + StringUtilities.hashCodeIgnoreCase(this._cid);
   }

   public String getName() {
      return this._serviceRecord.getName();
   }

   public String getEmailAddress() {
      return CMIMEUtilities.getEmailAddress(this._serviceRecord);
   }

   @Override
   public String toString() {
      return ((StringBuffer)(new Object())).append(this.getName()).append(':').append(this.getUID()).append(':').append(this.getCID()).toString();
   }
}
