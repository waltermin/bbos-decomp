package net.rim.device.apps.internal.help;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RequestedResource;

final class RetrieveThread extends Thread {
   BrowserContent _browserField;
   RequestedResource _resource;
   HelpScreen _helpScreen;

   RetrieveThread(RequestedResource resource, BrowserContent referrer, HelpScreen helpScreen) {
      this._browserField = referrer;
      this._resource = resource;
      this._helpScreen = helpScreen;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      boolean var4 = false /* VF: Semaphore variable */;

      HttpConnection conn;
      try {
         var4 = true;
         String e = this._helpScreen.customLoadResource(this._resource.getUrl());
         conn = (HttpConnection)Connector.open(e);
         var4 = false;
      } finally {
         if (var4) {
            return;
         }
      }

      this._resource.setHttpConnection(conn);
      this._browserField.resourceReady(this._resource);
   }
}
