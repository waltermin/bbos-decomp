package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.apps.internal.browser.verbs.SaveImageVerb;

final class DocViewSlideshowField$DocViewSaveImageVerb extends SaveImageVerb {
   private final DocViewSlideshowField this$0;

   DocViewSlideshowField$DocViewSaveImageVerb(DocViewSlideshowField _1, String url, EncodedImage image, boolean drmProtected) {
      super(url, image, drmProtected);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return this.this$0._isPresentation ? DocViewSlideshowField._resources.getString(124) : super.toString();
   }
}
