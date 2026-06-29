package net.rim.device.apps.internal.supl;

import net.rim.device.api.gps.LCS;
import net.rim.device.api.system.Application;

public final class SUPLMain extends Application {
   public static final void main(String[] args) {
      SUPLMain supl = new SUPLMain();
      System.out.println("In SUPL Main");
      supl.enterEventDispatcher();
   }

   private SUPLMain() {
      new ULPPushlet().register();
      LCS.addListener(Application.getApplication(), new SETInitiateListener());
      LCS.addListener(Application.getApplication(), new LCSControlPlaneListener());
      new SMSListenForNet();
   }
}
