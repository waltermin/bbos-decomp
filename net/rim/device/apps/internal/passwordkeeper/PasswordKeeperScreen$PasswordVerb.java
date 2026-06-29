package net.rim.device.apps.internal.passwordkeeper;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.component.KeywordFilteredListFinder;
import net.rim.device.apps.api.framework.verb.Verb;

final class PasswordKeeperScreen$PasswordVerb extends Verb {
   private int _type;
   private final PasswordKeeperScreen this$0;
   private static final int NEW = 1;
   private static final int OPEN = 2;
   private static final int COPY_USERNAME = 3;
   private static final int COPY_PASSWORD = 4;
   private static final int DELETE = 5;
   private static final int CLEAR = 6;
   private static final int CHANGE = 7;
   private static final int OPTION = 8;
   private static final int CLOSE = 9;

   PasswordKeeperScreen$PasswordVerb(PasswordKeeperScreen _1, int type, int ordering, ResourceBundleFamily rb, int rbKey) {
      super(ordering, rb, rbKey);
      this.this$0 = _1;
      this._type = type;
   }

   @Override
   public final Object invoke(Object parameter) {
      switch (this._type) {
         case 1:
         default:
            KeywordFilteredListFinder finder = PasswordKeeperScreen.access$000(this.this$0);
            String searchPattern = finder.getSearchPattern();
            if (searchPattern == null || searchPattern.length() == 0) {
               searchPattern = PasswordKeeperScreen.access$100(this.this$0).getText();
            }

            PasswordKeeperElement element = null;
            if (searchPattern != null && searchPattern.length() > 0) {
               element = new PasswordKeeperElement(new String[0], new String[]{searchPattern, "", "", "", ""});
            }

            this.this$0.showScreen(element);
            return null;
         case 2:
            this.this$0.showScreen((PasswordKeeperElement)PasswordKeeperScreen.access$300(this.this$0));
            return null;
         case 3:
            this.this$0.copyUsername((PasswordKeeperElement)PasswordKeeperScreen.access$400(this.this$0));
            return null;
         case 4:
            this.this$0.copyPassword((PasswordKeeperElement)PasswordKeeperScreen.access$500(this.this$0));
            return null;
         case 5:
            this.this$0.deletePassword((PasswordKeeperElement)PasswordKeeperScreen.access$600(this.this$0));
            return null;
         case 6:
            this.this$0.cleanClipboard();
            return null;
         case 7:
            this.this$0.changePassword();
            return null;
         case 8:
            this.this$0.showOptions();
            return null;
         case 9:
            this.this$0.close();
         case 0:
            return null;
      }
   }
}
