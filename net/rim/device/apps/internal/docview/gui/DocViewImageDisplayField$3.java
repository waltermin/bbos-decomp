package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.util.ObjectUtilities;

class DocViewImageDisplayField$3 implements Runnable {
   private final DocViewImageDisplayField$ImageState val$state;
   private final DocViewImageDisplayField this$0;

   DocViewImageDisplayField$3(DocViewImageDisplayField _1, DocViewImageDisplayField$ImageState _2) {
      this.this$0 = _1;
      this.val$state = _2;
   }

   @Override
   public void run() {
      try {
         if (ObjectUtilities.objEqual(this.val$state, this.this$0.getCurrentImageState())) {
            Field fld = this.val$state.getActiveImageField();
            if (fld != null) {
               this.this$0.delete(fld);
            }

            this.this$0.add(this.val$state._enlargeAllImageField);
            return;
         }
      } finally {
         return;
      }
   }
}
