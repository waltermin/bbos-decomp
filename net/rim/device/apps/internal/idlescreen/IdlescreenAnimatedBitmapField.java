package net.rim.device.apps.internal.idlescreen;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.internal.ui.component.AnimatedBitmapField;

final class IdlescreenAnimatedBitmapField extends AnimatedBitmapField implements Runnable {
   IdlescreenAnimatedBitmapField(EncodedImage image) {
      this(image, 2305843009213693988L);
   }

   IdlescreenAnimatedBitmapField(EncodedImage image, long style) {
      super(image, 2, style);
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      if (visible) {
         UiApplication.getUiApplication().invokeLater(this, 500, false);
      } else {
         this.stopAnimation();
      }

      super.onVisibilityChange(visible);
   }

   @Override
   public final void run() {
      this.reset();
      this.startAnimation();
   }
}
