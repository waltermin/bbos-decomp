package net.rim.device.apps.internal.passwordkeeper;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.verb.Verb;

final class PasswordKeeperOptionsScreen$PasswordVerb extends Verb {
   private int _type;
   private final PasswordKeeperOptionsScreen this$0;
   private static final int SAVE = 1;
   private static final int CLOSE = 2;

   PasswordKeeperOptionsScreen$PasswordVerb(PasswordKeeperOptionsScreen _1, int type, int ordering, ResourceBundleFamily rb, int rbKey) {
      super(ordering, rb, rbKey);
      this.this$0 = _1;
      this._type = type;
   }

   @Override
   public final Object invoke(Object parameter) {
      switch (this._type) {
         case 0:
            break;
         case 1:
         default:
            try {
               if (this.this$0.isDataValid()) {
                  this.this$0.save();
                  this.this$0.close();
                  return null;
               }
               break;
            } finally {
               ;
            }
         case 2:
            if (this.this$0.hasChanged()) {
               this.this$0.promptForSave();
               return null;
            }

            this.this$0.close();
      }

      return null;
   }
}
