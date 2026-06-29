package simulationservicebook;

import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;

class InsertInternetServiceBook$SBEntry {
   private ServiceBook _sb;
   private ServiceRecord _rec = (ServiceRecord)(new Object());

   public InsertInternetServiceBook$SBEntry(
      String uid,
      String cid,
      String name,
      int type,
      int encryptionmode,
      int compressionmode,
      String description,
      InsertInternetServiceBook$ApplicationDataProvider appdataprovider,
      HostRoutingTable hrtEntry
   ) {
      this._rec.setType(type);
      this._rec.setName(name);
      this._rec.setUid(uid);
      this._rec.setCid(cid);
      this._rec.setEncryptionMode(encryptionmode);
      this._rec.setCompressionMode(compressionmode);
      this._rec.setDescription(description);
      this._rec.setApplicationData(appdataprovider.get());
      this._rec.setAttachedHrt(hrtEntry);
      this._sb = ServiceBook.getSB();
   }

   public void setDSID(String dsid) {
      this._rec.setDataSourceId(dsid);
   }

   public boolean add(boolean skipIfCidPresent) {
      if (skipIfCidPresent) {
         ServiceRecord[] srs = this._sb.findRecordsByCid(this._rec.getCid());
         if (srs.length > 0) {
            return false;
         }
      }

      System.out.println("InsertInternetServiceBook: Adding SBEntry");
      boolean retval = false;
      if (null != this._sb.addRecord(this._rec)) {
         System.out.println("Service Book entry accepted!");
         this._sb.commit();
         return true;
      } else {
         System.out.println("Service Book update FAILED!");
         return retval;
      }
   }
}
