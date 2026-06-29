package net.rim.device.apps.internal.docview.gui;

import net.rim.device.internal.ui.component.SimpleInputDialog;

public final class DocViewTextDisplayField$GoToDlg extends SimpleInputDialog {
   private final int _maxLimit;

   public DocViewTextDisplayField$GoToDlg(String prompt, int maxLimit) {
      super(0, prompt);
      if (maxLimit <= 0) {
         throw new IllegalArgumentException("Invalid maximum limit.");
      }

      this._maxLimit = maxLimit;
      this.getEditField().setFilter(new DocViewTextDisplayField$GoToDlg$MaxLimitNumericFilter(this));
   }

   final int getPositiveNumberText() {
      String txt = this.getText();

      try {
         int val = Integer.valueOf(txt);
         if (val > 0) {
            return val;
         }
      } finally {
         return -1;
      }

      return -1;
   }
}
