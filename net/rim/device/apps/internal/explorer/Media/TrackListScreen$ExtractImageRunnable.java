package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.component.BitmapField;

final class TrackListScreen$ExtractImageRunnable extends Thread {
   BitmapField _bitmap;
   private final TrackListScreen this$0;

   TrackListScreen$ExtractImageRunnable(TrackListScreen _1, BitmapField bitmap) {
      this.this$0 = _1;
      this._bitmap = bitmap;
      this.setPriority(3);
   }

   @Override
   public final void run() {
      EncodedImage image = this.this$0.extractImage();
      if (image != null) {
         synchronized (Application.getEventLock()) {
            this._bitmap.setImage(image);
         }
      }
   }
}
