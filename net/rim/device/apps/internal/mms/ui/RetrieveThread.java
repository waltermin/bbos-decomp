package net.rim.device.apps.internal.mms.ui;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RequestedResource;

class RetrieveThread implements Runnable {
   BrowserContent _browserContent;
   RequestedResource _resource;

   RetrieveThread(RequestedResource resource, BrowserContent referrer) {
      this._browserContent = referrer;
      this._resource = resource;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      boolean var4 = false /* VF: Semaphore variable */;

      Object conn;
      try {
         var4 = true;
         conn = Connector.open(this._resource.getUrl());
         var4 = false;
      } finally {
         if (var4) {
            return;
         }
      }

      this._resource.setHttpConnection((HttpConnection)conn);
      this._browserContent.resourceReady(this._resource);
   }
}
