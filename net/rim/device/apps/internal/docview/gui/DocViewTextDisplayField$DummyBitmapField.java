package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.BitmapField;

public final class DocViewTextDisplayField$DummyBitmapField extends BitmapField {
   DocViewTextDisplayField$DummyBitmapField() {
      super(Bitmap.getPredefinedBitmap(1), 18014398509482020L);
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      if (!on) {
         try {
            super.drawFocus(graphics, on);
         } finally {
            return;
         }
      }
   }
}
