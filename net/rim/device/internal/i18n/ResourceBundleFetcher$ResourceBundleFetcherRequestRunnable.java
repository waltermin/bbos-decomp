package net.rim.device.internal.i18n;

import net.rim.device.api.i18n.ResourceBundle;

final class ResourceBundleFetcher$ResourceBundleFetcherRequestRunnable implements Runnable {
   private ResourceBundle _bundle;
   private String _requestName;
   private int _requestHandle;
   private Object _lockObject = new Object();

   private ResourceBundleFetcher$ResourceBundleFetcherRequestRunnable() {
   }

   private final void reset(String name, int handle) {
      this._requestName = name;
      this._requestHandle = handle;
      this._bundle = null;
   }

   private final Object getLock() {
      return this._lockObject;
   }

   private final ResourceBundle getResult() {
      return this._bundle;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      if (this._requestName != null) {
         boolean var12 = false /* VF: Semaphore variable */;

         label72: {
            try {
               var12 = true;
               this._bundle = ResourceBundleFetcher.fetchResourceBundleInternal(this._requestName, this._requestHandle);
               var12 = false;
               break label72;
            } catch (Exception var16) {
               var12 = false;
            } finally {
               if (var12) {
                  synchronized (this._lockObject) {
                     this._lockObject.notify();
                  }
               }
            }

            synchronized (this._lockObject) {
               this._lockObject.notify();
               return;
            }
         }

         synchronized (this._lockObject) {
            this._lockObject.notify();
         }
      }
   }

   ResourceBundleFetcher$ResourceBundleFetcherRequestRunnable(ResourceBundleFetcher$1 x0) {
      this();
   }
}
