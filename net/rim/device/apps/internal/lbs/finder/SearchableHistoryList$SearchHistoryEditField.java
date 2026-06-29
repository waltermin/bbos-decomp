package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BasicEditField;

final class SearchableHistoryList$SearchHistoryEditField extends BasicEditField {
   private final SearchableHistoryList this$0;

   public SearchableHistoryList$SearchHistoryEditField(SearchableHistoryList this$0, String label, String initialValue, int maxNumChars, long style) {
      super(label, initialValue, maxNumChars, style);
      this.this$0 = this$0;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         return this.invokeAction(1);
      }

      if (this.getText().endsWith(" ") && key == ' ') {
         return true;
      }

      if (!this.isFocus()) {
         this.setFocus();
         int pos = this.getTextLength() - 1;
         if (pos > 0) {
            this.moveFocus(pos, 0, 0);
         }
      }

      return super.keyChar(key, status, time);
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      return (status & 3) == 0 && (status & 536870912) != 0 && Ui.getTrackballClickAction() == 1 ? this.invokeAction(1) : super.navigationClick(status, time);
   }

   @Override
   public final void clear(int context) {
      super.clear(context);
      if (this.this$0._historyList != null) {
         this.this$0._historyList.reinit();
         this.this$0._historyList.getScreen().ensureRegionVisible(this.this$0._historyList, 0, 0, 0, 0);
      }
   }
}
