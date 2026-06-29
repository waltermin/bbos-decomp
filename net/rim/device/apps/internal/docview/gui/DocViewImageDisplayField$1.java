package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.util.ObjectUtilities;

class DocViewImageDisplayField$1 implements Runnable {
   private final DocViewImageDisplayField$ImageState val$state;
   private final DocViewImageField val$imgFld;
   private final DocViewImageDisplayField this$0;

   DocViewImageDisplayField$1(DocViewImageDisplayField _1, DocViewImageDisplayField$ImageState _2, DocViewImageField _3) {
      this.this$0 = _1;
      this.val$state = _2;
      this.val$imgFld = _3;
   }

   @Override
   public void run() {
      label26:
      try {
         if (ObjectUtilities.objEqual(this.val$state, this.this$0.getCurrentImageState())) {
            Field fld = this.val$state.getActiveImageField();
            if (fld != null) {
               this.this$0.delete(fld);
            }

            this.this$0.add(this.val$imgFld);
         }
      } finally {
         break label26;
      }

      this.val$state._enlargedImageField = this.val$imgFld;
   }
}
