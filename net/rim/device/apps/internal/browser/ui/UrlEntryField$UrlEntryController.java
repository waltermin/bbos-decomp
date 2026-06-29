package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.component.ComboFieldController;
import net.rim.device.api.util.StringUtilities;
import net.rim.tid.im.SLControlObject;

public class UrlEntryField$UrlEntryController extends ComboFieldController {
   private final UrlEntryField this$0;

   public UrlEntryField$UrlEntryController(UrlEntryField _1) {
      this.this$0 = _1;
   }

   @Override
   protected void textChanged(String newText, int context) {
      if ((context & -2147483648) == 0) {
         String[] variants = new Object[]{newText};
         if (this.this$0.getInputContext().isSureType()) {
            SLControlObject controlObject = (SLControlObject)this.this$0.getInputContext().getInputMethodControlObject();
            String[][][] vars = new Object[1][][];
            controlObject.actionPerformed(107, vars);
            if (vars[0] != null && vars[0].length > 0) {
               String base = newText.substring(0, newText.length() - vars[0][0].length());
               variants = new Object[vars[0].length];

               for (int i = 0; i < variants.length; i++) {
                  variants[i] = ((StringBuffer)(new Object())).append(base).append(vars[0][i]).toString();
               }
            }
         }

         this.this$0._indices = new int[2 * variants.length];
         this.this$0._firstEntryText = newText;
         int total = 1;

         for (int i = 0; i < variants.length; i++) {
            int[] matchBounds = this.this$0._urlStore.match(variants[i]);
            if (matchBounds[0] > -1 && matchBounds[1] > -1 && matchBounds[1] >= matchBounds[0]) {
               if (StringUtilities.strEqualIgnoreCase(this.this$0._urlStore.getElementAt(matchBounds[0]), newText)) {
                  matchBounds[0]++;
               }

               matchBounds[1]++;
               this.this$0._indices[2 * i] = matchBounds[0];
               this.this$0._indices[2 * i + 1] = matchBounds[1];
               total += matchBounds[1] - matchBounds[0];
            }
         }

         this.this$0.getList().setSize(total);
         if (total == 1) {
            this.this$0.hideDropList();
         } else {
            this.this$0.showDropList();
         }
      }
   }

   @Override
   protected void select(Object selection, int type) {
      if (selection instanceof Object) {
         this.this$0.setText((String)selection);
         super._comboField.hideDropList();
         UrlEntryField.access$300(this.this$0, 132388);
      }
   }

   @Override
   protected void escape() {
      this.this$0.setText("http://www.");
      this.this$0._indices = new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, -804651007, 5, -804651005, 5, 6, 7};
      super._comboField.hideDropList();
   }
}
