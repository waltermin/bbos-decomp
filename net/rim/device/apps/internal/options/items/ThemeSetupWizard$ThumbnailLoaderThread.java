package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.Bitmap;

final class ThemeSetupWizard$ThumbnailLoaderThread extends Thread {
   private final ThemeSetupWizard this$0;

   ThemeSetupWizard$ThumbnailLoaderThread(ThemeSetupWizard _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      int numThemes = this.this$0._themeNamesLocalized.length;

      for (int i = this.this$0._hideNone ? 1 : 0; i < numThemes; i++) {
         String resourceName = ThemeSetupWizard.getPreviewResourceName(this.this$0._themeNamesLocalized[i]);
         if (resourceName != null) {
            synchronized (this.this$0._thumbCache) {
               if (this.this$0._thumbCache.get(resourceName) != null) {
                  continue;
               }
            }

            Bitmap thumbnailBitmap = ThemeSetupWizard.loadPreviewBitmap(resourceName);
            if (thumbnailBitmap == null) {
               thumbnailBitmap = this.this$0._emptyThumbnail;
            }

            synchronized (this.this$0._thumbCache) {
               this.this$0._thumbCache.put(resourceName, thumbnailBitmap);
            }

            this.this$0.signalUpdatedThumbnail(i, thumbnailBitmap);
         }
      }
   }
}
