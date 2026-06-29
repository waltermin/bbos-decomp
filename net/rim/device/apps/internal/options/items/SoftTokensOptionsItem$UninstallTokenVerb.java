package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.cldc.impl.api.SoftToken;

class SoftTokensOptionsItem$UninstallTokenVerb extends Verb {
   private final SoftTokensOptionsItem this$0;

   public SoftTokensOptionsItem$UninstallTokenVerb(SoftTokensOptionsItem _1) {
      super(16986368, SoftTokensOptionsItem._rb.getFamily(), 1);
      this.this$0 = _1;
   }

   @Override
   public Object invoke(Object parameter) {
      int selectedIndex = this.this$0._tokenListField.getSelectedIndex();
      if (selectedIndex < this.this$0._tokenList.size() && this.this$0._tokenList.size() > 0) {
         SoftToken token = (SoftToken)this.this$0._tokenList.elementAt(selectedIndex);
         if (this.this$0._softTokenMgr.delete(token.getSerialNum(), true)) {
            this.this$0._tokenListField.setSize(this.this$0._tokenList.size());
            Dialog.inform(this.this$0.getString(20));
            return null;
         }

         Dialog.alert(this.this$0.getString(21));
      }

      return null;
   }
}
