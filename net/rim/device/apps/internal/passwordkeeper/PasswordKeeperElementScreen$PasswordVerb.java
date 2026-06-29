package net.rim.device.apps.internal.passwordkeeper;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

final class PasswordKeeperElementScreen$PasswordVerb extends Verb {
   private int _type;
   private final PasswordKeeperElementScreen this$0;
   static final int RANDOM;
   static final int SAVE;
   static final int EDIT_LABEL;
   static final int DELETE;
   static final int CLOSE;

   PasswordKeeperElementScreen$PasswordVerb(PasswordKeeperElementScreen _1, int type, int ordering, ResourceBundleFamily rb, int rbKey) {
      super(ordering, rb, rbKey);
      this.this$0 = _1;
      this._type = type;
   }

   @Override
   public final Object invoke(Object parameter) {
      switch (this._type) {
         case 1:
         default:
            this.this$0.doRandom();
            return null;
         case 2:
            this.this$0.doSave();
            return null;
         case 3:
            this.this$0.doEditLabel();
            return null;
         case 4:
            PasswordKeeperOptions options = PasswordKeeperOptions.getOptions();
            if (options.getConfirmDelete()) {
               String title;
               try {
                  title = this.this$0._element.getTitle();
               } catch (DecryptionException e) {
                  title = PasswordKeeper.getString(3022);
               } catch (PasswordKeeperLockedException e) {
                  title = PasswordKeeper.getString(3022);
               }

               if (!SimpleChoiceDialog.askYesNoQuestion(PasswordKeeper.getString(3023), title)) {
                  return null;
               }
            }

            this.this$0.doDelete();
            this.this$0.doClose();
            return null;
         case 5:
            if (this.this$0.changed()) {
               this.this$0.promptForSave();
               return null;
            } else {
               this.this$0.doClose();
            }
         case 0:
            return null;
      }
   }
}
