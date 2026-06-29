package net.rim.device.apps.internal.keystore.browser;

import net.rim.device.api.ui.Screen;
import net.rim.device.apps.api.ui.ProgressIndicator;

class KeyStoreBrowserOptionsItem$KeyStoreReloader implements Runnable {
   private int _myEpoch;
   private final KeyStoreBrowserOptionsItem this$0;

   KeyStoreBrowserOptionsItem$KeyStoreReloader(KeyStoreBrowserOptionsItem _1, int epoch) {
      this.this$0 = _1;
      this._myEpoch = epoch;
   }

   @Override
   public void run() {
      if (this._myEpoch == this.this$0._epoch) {
         Screen activeScreen = this.this$0._app.getActiveScreen();
         if (activeScreen == KeyStoreBrowserOptionsItem.access$200(this.this$0)) {
            ProgressIndicator indicator = (ProgressIndicator)(new Object(4));
            indicator.setProgressRunnable(new KeyStoreBrowserOptionsItem$LoadCertificatesThread(this.this$0, 1, this._myEpoch));
            indicator.run();
         } else {
            new KeyStoreBrowserOptionsItem$LoadCertificatesThread(this.this$0, 1, this._myEpoch).start();
         }
      }
   }
}
