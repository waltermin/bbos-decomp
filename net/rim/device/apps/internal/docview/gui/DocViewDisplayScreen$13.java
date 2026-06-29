package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.util.ObjectUtilities;

class DocViewDisplayScreen$13 implements Runnable {
   private final Screen val$screen;
   private final DocViewParser val$docData;
   private final String val$domID;
   private final DocViewDisplayScreen this$0;

   DocViewDisplayScreen$13(DocViewDisplayScreen _1, Screen _2, DocViewParser _3, String _4) {
      this.this$0 = _1;
      this.val$screen = _2;
      this.val$docData = _3;
      this.val$domID = _4;
   }

   @Override
   public void run() {
      try {
         Screen activeScreen = this.this$0.getUiEngine().getActiveScreen();

         while (activeScreen instanceof Object) {
            activeScreen = activeScreen.getScreenBelow();
         }

         if (ObjectUtilities.objEqual(activeScreen, this.val$screen)) {
            this.this$0.displayEmbeddedObject(this.val$docData, this.val$domID, this.this$0._docData.getParsingData().getTrackChangesOnStatus());
            return;
         }
      } finally {
         return;
      }
   }
}
