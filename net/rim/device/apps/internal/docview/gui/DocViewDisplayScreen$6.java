package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.internal.ui.Image;

class DocViewDisplayScreen$6 implements Runnable {
   private final Screen val$screen;
   private final DocViewParser val$docData;
   private final Image val$iconFinal;
   private final DocViewDisplayScreen this$0;

   DocViewDisplayScreen$6(DocViewDisplayScreen _1, Screen _2, DocViewParser _3, Image _4) {
      this.this$0 = _1;
      this.val$screen = _2;
      this.val$docData = _3;
      this.val$iconFinal = _4;
   }

   @Override
   public void run() {
      Screen activeScreen = null;

      label66:
      try {
         activeScreen = this.this$0.getUiEngine().getActiveScreen();

         while (activeScreen instanceof Object) {
            activeScreen = activeScreen.getScreenBelow();
         }
      } finally {
         break label66;
      }

      label72: {
         if (this.this$0._executeObj == null
            || !ObjectUtilities.objEqual(activeScreen, this.val$screen) && !(activeScreen instanceof DocViewTextDisplayField$DocViewPlayScreen)) {
            if (!(activeScreen instanceof DocViewDisplayScreen)) {
               break label72;
            }

            if (!((DocViewDisplayScreen)activeScreen)._forceAllowDocInfo) {
               break label72;
            }
         }

         if (this.this$0._displayField != null) {
            this.this$0._displayField.showDocInfo(this.val$docData.getParsingData(), this.val$iconFinal);
         }
      }

      this.this$0._executeObj = null;
   }
}
