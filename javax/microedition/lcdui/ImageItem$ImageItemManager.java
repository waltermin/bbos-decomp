package javax.microedition.lcdui;

import net.rim.device.api.ui.container.VerticalFieldManager;

final class ImageItem$ImageItemManager extends VerticalFieldManager {
   ImageItem _img;

   public ImageItem$ImageItemManager(ImageItem img) {
      super(1152921504606846976L);
      this._img = img;
   }

   @Override
   protected final void sublayout(int width, int height) {
      if (this._img.getOwner() instanceof Form) {
         super.sublayout(width, this._img.getPreferredHeight());
      } else {
         super.sublayout(width, height);
      }
   }
}
