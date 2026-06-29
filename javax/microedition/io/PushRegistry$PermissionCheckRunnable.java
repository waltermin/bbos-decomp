package javax.microedition.io;

class PushRegistry$PermissionCheckRunnable implements Runnable {
   boolean _failed;
   boolean _done;
   int _moduleHandle;
   String _permission;
   String _url;
   RuntimeException _re;

   public PushRegistry$PermissionCheckRunnable(int moduleHandle, String permission, String url) {
      this._moduleHandle = moduleHandle;
      this._permission = permission;
      this._url = url;
   }

   @Override
   public void run() {
      synchronized (this) {
         try {
            PushRegistry.checkPermissionPrimitive(this._moduleHandle, this._permission, this._url);
         } catch (Exception e) {
            this._failed = true;
            if (e instanceof RuntimeException) {
               this._re = (RuntimeException)e;
            }
         }

         this._done = true;
         throw new PushRegistry$PushRegistryPermissionCheckExitEvent(null);
      }
   }
}
