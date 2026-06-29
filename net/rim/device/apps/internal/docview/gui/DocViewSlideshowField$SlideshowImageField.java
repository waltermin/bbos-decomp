package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.MenuItem;

final class DocViewSlideshowField$SlideshowImageField extends SlideshowBitmapField {
   private final DocViewSlideshowField this$0;

   DocViewSlideshowField$SlideshowImageField(DocViewSlideshowField _1, boolean isInitialRotated) {
      super(isInitialRotated);
      this.this$0 = _1;
   }

   @Override
   protected final void makeContextMenu(ContextMenu contextMenu, int instance) {
      super.makeContextMenu(contextMenu, instance);
      if (instance == 0) {
         EncodedImage img = this.getImage();
         if (img != null) {
            DocViewSlideshowField$DocViewSaveImageVerb saveImageVb = new DocViewSlideshowField$DocViewSaveImageVerb(
               this.this$0,
               this.this$0._isPresentation
                  ? ((StringBuffer)(new Object()))
                     .append(DocViewSlideshowField._resources.getString(90))
                     .append(' ')
                     .append(String.valueOf(this.this$0._currentIndex + 1))
                     .toString()
                  : ((StringBuffer)(new Object()))
                     .append(DocViewSlideshowField._resources.getString(44))
                     .append(' ')
                     .append(String.valueOf(this.this$0._currentIndex + 1))
                     .toString(),
               img,
               this.isProtected()
            );
            contextMenu.addItem((MenuItem)(new Object(saveImageVb, saveImageVb.getOrdering())));
         }
      }
   }
}
