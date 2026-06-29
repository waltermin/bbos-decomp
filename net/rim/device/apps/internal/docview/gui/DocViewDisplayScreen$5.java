package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.apps.api.ui.PopupStatus;

class DocViewDisplayScreen$5 implements Runnable {
   private final DocViewDisplayField$ItemInfo val$finalItemToDisplay;
   private final Screen val$thisScreen;
   private final DocViewDisplayScreen this$0;

   DocViewDisplayScreen$5(DocViewDisplayScreen _1, DocViewDisplayField$ItemInfo _2, Screen _3) {
      this.this$0 = _1;
      this.val$finalItemToDisplay = _2;
      this.val$thisScreen = _3;
   }

   @Override
   public void run() {
      this.this$0.checkTitleField();
      this.this$0.displayRetrievedPercentage();

      try {
         this.this$0._displayField.checkArbItems();
         if (this.val$finalItemToDisplay != null
            && DocViewDisplayScreenInstance.getActivePartInstance(this.this$0._messageID, this.this$0._applicationID)._active) {
            Screen activeScreen = this.this$0.getUiEngine().getActiveScreen();

            while (activeScreen instanceof PopupStatus) {
               activeScreen = activeScreen.getScreenBelow();
            }

            if (ObjectUtilities.objEqual(activeScreen, this.val$thisScreen)
               && this.this$0._displayField.doSelectItem(this.val$finalItemToDisplay)
               && !this.this$0._displayField._fullDocState) {
               this.this$0._displayField.toggleDisplayMode();
               return;
            }
         }
      } finally {
         return;
      }
   }
}
