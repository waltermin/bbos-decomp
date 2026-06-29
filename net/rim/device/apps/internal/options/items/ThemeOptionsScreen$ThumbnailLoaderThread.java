package net.rim.device.apps.internal.options.items;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;

final class ThemeOptionsScreen$ThumbnailLoaderThread extends Thread {
   private final ThemeOptionsScreen this$0;

   ThemeOptionsScreen$ThumbnailLoaderThread(ThemeOptionsScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.setPriority(1);
      int numThemes = this.this$0._choices.length;

      for (int i = this.this$0._showNone ? 0 : 1; i < numThemes; i++) {
         String themeName = this.this$0._choices[i];
         synchronized (this.this$0._thumbnailCache) {
            if (this.this$0._thumbnailCache.get(themeName) != null) {
               continue;
            }
         }

         Bitmap thumbnailBitmap = this.loadPreviewBitmap(themeName);
         if (thumbnailBitmap != null) {
            synchronized (this.this$0._thumbnailCache) {
               this.this$0._thumbnailCache.put(themeName, thumbnailBitmap);
            }

            this.signalUpdatedThumbnail(i);
         }
      }
   }

   private final void signalUpdatedThumbnail(int index) {
      Application.getApplication().invokeLater(new ThemeOptionsScreen$ThumbnailLoaderThread$1(this, index));
   }

   private final Bitmap loadPreviewBitmap(String themeName) {
      EncodedImage image = null;
      Theme themeObject = null;

      label35:
      try {
         themeObject = ThemeManager.getTheme(themeName);
      } finally {
         break label35;
      }

      if (themeObject != null) {
         String imageName = themeObject.getThumbnailName();
         if (imageName != null) {
            image = themeObject.getImage(imageName);
         }
      }

      if (image != null) {
         int scale = this.determineImageScale(image.getWidth(), image.getHeight());
         image.setScaleX32(scale);
         image.setScaleY32(scale);
         return image.getBitmap();
      } else {
         return null;
      }
   }

   private final int determineImageScale(int imageWidth, int imageHeight) {
      int widthDiff = imageWidth - ThemeOptionsScreen._thumbnailWidth;
      int heightDiff = imageHeight - ThemeOptionsScreen._thumbnailHeight;
      if (widthDiff <= 0 && heightDiff <= 0) {
         return 65536;
      } else {
         return widthDiff > heightDiff
            ? Fixed32.div(Fixed32.toFP(imageWidth), Fixed32.toFP(ThemeOptionsScreen._thumbnailWidth))
            : Fixed32.div(Fixed32.toFP(imageHeight), Fixed32.toFP(ThemeOptionsScreen._thumbnailHeight));
      }
   }
}
