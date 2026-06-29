package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.cldc.impl.api.SoftToken;

class SoftTokensOptionsItem$OpenTokenVerb extends Verb {
   private final SoftTokensOptionsItem this$0;

   public SoftTokensOptionsItem$OpenTokenVerb(SoftTokensOptionsItem _1) {
      super(16986368);
      this.this$0 = _1;
   }

   @Override
   public String toString() {
      return this.this$0.getString(17);
   }

   @Override
   public Object invoke(Object parameter) {
      int selectedIndex = this.this$0._tokenListField.getSelectedIndex();
      if (selectedIndex < this.this$0._tokenList.size() && this.this$0._tokenList.size() > 0) {
         SoftToken token = (SoftToken)this.this$0._tokenList.elementAt(selectedIndex);
         this.this$0._softTokenMgr.showDialog(token.getSerialNum(), this.this$0.getString(18) + token.getSerialNum(), false, false, null);
      }

      return null;
   }
}
