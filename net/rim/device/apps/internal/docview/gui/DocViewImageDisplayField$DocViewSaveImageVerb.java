package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.apps.internal.browser.verbs.SaveImageVerb;

final class DocViewImageDisplayField$DocViewSaveImageVerb extends SaveImageVerb {
   private final DocViewImageDisplayField this$0;

   DocViewImageDisplayField$DocViewSaveImageVerb(DocViewImageDisplayField _1, String url, EncodedImage image, boolean drmProtected) {
      super(url, image, drmProtected);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      switch (this.this$0._presentationValue) {
         case 0:
            return super.toString();
         case 1:
         default:
            return DocViewDisplayField._resources.getString(124);
         case 2:
            return DocViewDisplayField._resources.getString(125);
      }
   }
}
