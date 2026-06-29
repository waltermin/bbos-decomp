package net.rim.device.cldc.impl.datarecovery;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.cldc.io.datarecovery.DataRecovery;

public final class Registration {
   public static final void DataRecoveryMain(String[] args) {
      DataRecovery.getInstance();
      switch (RadioInfo.getNetworkType()) {
         case 2:
         case 6:
            return;
         case 3:
         default:
            new OffOnThread(0);
            return;
         case 4:
         case 5:
            new ContextRecovery();
            return;
         case 7:
            new OffOnThread(1);
      }
   }
}
