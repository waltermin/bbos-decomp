package net.rim.device.internal.i18n;

final class ResourceBundleFetcher$CompressedResourceHashtable$UpdateRunnable implements Runnable {
   private final ResourceBundleFetcher$CompressedResourceHashtable this$0;

   private ResourceBundleFetcher$CompressedResourceHashtable$UpdateRunnable(ResourceBundleFetcher$CompressedResourceHashtable _1) {
      this.this$0 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      boolean var12 = false /* VF: Semaphore variable */;

      label66: {
         try {
            var12 = true;
            this.this$0.updateBundleCache();
            var12 = false;
            break label66;
         } catch (Exception var16) {
            var12 = false;
         } finally {
            if (var12) {
               synchronized (this.this$0._lockObject) {
                  this.this$0._lockObject.notify();
               }
            }
         }

         synchronized (this.this$0._lockObject) {
            this.this$0._lockObject.notify();
            return;
         }
      }

      synchronized (this.this$0._lockObject) {
         this.this$0._lockObject.notify();
      }
   }

   ResourceBundleFetcher$CompressedResourceHashtable$UpdateRunnable(ResourceBundleFetcher$CompressedResourceHashtable x0, ResourceBundleFetcher$1 x1) {
      this(x0);
   }
}
