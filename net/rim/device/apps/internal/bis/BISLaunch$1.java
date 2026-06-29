package net.rim.device.apps.internal.bis;

import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.invoke.BISClientInvoke;
import net.rim.device.apps.internal.bis.launch.protocol.RestClient;
import net.rim.device.apps.internal.bis.launch.protocol.UpdateInfo;

final class BISLaunch$1 implements Runnable {
   private final BISLaunch this$0;

   BISLaunch$1(BISLaunch _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._updatesCheckSuccessful = false;

      try {
         String brandName = BISClientConfigRecord.getBISClientConfigRecord().getBrandName();
         String version = BISClientInvoke.getBISClientVersion();
         UpdateInfo updateInfo = RestClient.getAppStatus2(brandName);
         this.this$0._updatesAvailable = !updateInfo.getUpToDateStatus();
         this.this$0._updatesRequired = updateInfo.isMandatory();
         this.this$0._urls = updateInfo.getDownloadUrls();
         this.this$0._digests = updateInfo.getDigests();
         this.this$0._size = updateInfo.getDownloadSize();
         this.this$0._updatesCheckSuccessful = true;
      } finally {
         return;
      }
   }
}
