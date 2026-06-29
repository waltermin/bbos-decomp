package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.internal.proxy.Proxy;

public class WaitForUnlock extends Thread {
   private String _string;
   private Object _cookie;
   private Object _ticket;

   public WaitForUnlock(String string) {
      this._string = string;
   }

   @Override
   public void start() {
      this.start(null);
   }

   public void start(Object cookie) {
      this._cookie = cookie;
      this._ticket = PersistentContent.getTicket();
      if (this._ticket == null) {
         super.start();
      } else {
         this.performWork(this._cookie);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      this._ticket = PersistentContent.waitForTicket();
      PopupScreen pleaseWaitScreen = (PopupScreen)(new Object((Manager)(new Object())));
      DialogFieldManager manager = (DialogFieldManager)pleaseWaitScreen.getDelegate();
      manager.setMessage((RichTextField)(new Object(this._string, 36028797018963968L)));
      manager.setIcon((BitmapField)(new Object(Bitmap.getPredefinedBitmap(3), 51539607552L)));
      Proxy.getInstance().invokeLater(new WaitForUnlock$1(this, pleaseWaitScreen));
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         this.performWork(this._cookie);
         var5 = false;
      } finally {
         if (var5) {
            Proxy.getInstance().invokeLater(new WaitForUnlock$2(this, pleaseWaitScreen));
         }
      }

      Proxy.getInstance().invokeLater(new WaitForUnlock$2(this, pleaseWaitScreen));
   }

   public void performWork(Object _1) {
      throw null;
   }
}
