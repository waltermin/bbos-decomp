package simulationservicebook;

import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;

class InsertInternetServiceBook$4 implements InsertInternetServiceBook$ApplicationDataProvider {
   private final InsertInternetServiceBook this$0;

   InsertInternetServiceBook$4(InsertInternetServiceBook _1) {
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public byte[] get() {
      try {
         BrowserConfigRecord tempRecord = BrowserConfigRecord.getNewConfig(BrowserConfigRecord.IPPP_SERVICE_CID, "S 00005", 1);
         tempRecord.setPropertyAsString(11, "Browser");
         return tempRecord.getEncodedData();
      } catch (Throwable var3) {
         System.err.println(e);
         return new byte[0];
      }
   }
}
