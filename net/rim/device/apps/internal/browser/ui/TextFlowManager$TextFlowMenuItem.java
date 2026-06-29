package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class TextFlowManager$TextFlowMenuItem extends MenuItem {
   private int _resid;
   private final TextFlowManager this$0;

   public TextFlowManager$TextFlowMenuItem(TextFlowManager _1, int resid, int ordinal, int priority) {
      super(BrowserResources.getResourceBundle(), resid, ordinal, priority);
      this.this$0 = _1;
      this._resid = resid;
   }

   @Override
   public final void run() {
      switch (this._resid) {
         case 655:
            if (this.this$0.usingCursor()) {
               Graphics g = TextFlowManager.access$3700(this.this$0);
               g.setOverlay(
                  4,
                  null,
                  this.this$0._cursorXPos - this.this$0.getDefaultCursor().getOriginX(),
                  this.this$0._cursorYPos - this.this$0.getDefaultCursor().getOriginY()
               );
            }

            this.this$0._focusField.position(true);
            if (this.this$0.getLeafFieldWithFocus() != this.this$0._focusField) {
               TextFlowManager.access$3800(this.this$0, 0, this.this$0.getCurrentScroll(), 0, 0);
               return;
            }
            break;
         case 656:
            if (this.this$0.usingCursor()) {
               Graphics g = TextFlowManager.access$3900(this.this$0);
               g.setOverlay(
                  4,
                  this.this$0.getDefaultCursor().getBitmap(),
                  this.this$0._cursorXPos - this.this$0.getDefaultCursor().getOriginX(),
                  this.this$0._cursorYPos - this.this$0.getDefaultCursor().getOriginY()
               );
               this.this$0._cursorFocusRect.set(0, 0, 0, 0);
            }

            this.this$0._focusField.position(false);
            return;
         case 853:
         case 854:
            this.this$0.toggleViewMode();
      }
   }
}
