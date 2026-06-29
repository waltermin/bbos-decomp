package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.text.NumericTextFilter;
import net.rim.device.api.util.AbstractString;

final class DocViewTextDisplayField$GoToDlg$MaxLimitNumericFilter extends NumericTextFilter {
   private final DocViewTextDisplayField$GoToDlg this$0;

   DocViewTextDisplayField$GoToDlg$MaxLimitNumericFilter(DocViewTextDisplayField$GoToDlg _1) {
      this.this$0 = _1;
   }

   @Override
   public final boolean validate(char character) {
      return super.validate(character) ? this.maxLimitValidate(character) : false;
   }

   @Override
   public final boolean validate(char character, AbstractString text, int position) {
      return super.validate(character, text, position) ? this.maxLimitValidate(character) : false;
   }

   private final boolean maxLimitValidate(char character) {
      try {
         int val = Integer.valueOf(((StringBuffer)(new Object())).append(this.this$0.getText()).append(character).toString());
         if (val > 0 && val <= this.this$0._maxLimit) {
            return true;
         }
      } finally {
         return false;
      }

      return false;
   }
}
