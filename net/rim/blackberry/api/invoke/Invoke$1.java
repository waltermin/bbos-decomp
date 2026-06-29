package net.rim.blackberry.api.invoke;

import net.rim.device.apps.api.ribbon.RibbonLauncher;

class Invoke$1 extends Thread {
   @Override
   public void run() {
      try {
         RibbonLauncher.getInstance().launch("net_rim_bb_options_app.BluetoothConfig");
      } finally {
         return;
      }
   }
}
