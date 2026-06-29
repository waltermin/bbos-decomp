package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.apps.api.ui.VerbMenuItem;

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
                  ? DocViewSlideshowField._resources.getString(90) + ' ' + (this.this$0._currentIndex + 1)
                  : DocViewSlideshowField._resources.getString(44) + ' ' + (this.this$0._currentIndex + 1),
               img,
               this.isProtected()
            );
            contextMenu.addItem(new VerbMenuItem(saveImageVb, saveImageVb.getOrdering()));
         }
      }
   }
}
