package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.internal.mms.verbs.ChangeRecipientVerb;
import net.rim.device.apps.internal.mms.verbs.DeleteRecipientVerb;

final class RecipientField extends HorizontalFieldManager {
   public RecipientField(String label, Object recipient, Object context) {
      this.add((Field)(new Object(label)));
      Object ctx = ContextObject.clone(context);
      Object obj = ContextObject.get(ctx, 3696141428889703675L);
      if (obj != null) {
         ContextObject.put(ctx, 252, obj);
      }

      Field recipientField = ((FieldProvider)recipient).getField(ctx);
      if (recipientField != null) {
         this.add(recipientField);
      }

      this.setCookie(recipient);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      Object mgr = this.getManager();
      if (mgr instanceof RecipientFieldManager) {
         RecipientFieldManager rlf = (RecipientFieldManager)mgr;
         if (rlf._editable) {
            ChangeRecipientVerb verb = new ChangeRecipientVerb(this, rlf);
            menu.add((MenuItem)(new Object(verb, 500)));
         }
      }
   }

   @Override
   protected final boolean keyChar(char character, int status, int time) {
      if (character == 127 || character == '\b') {
         Object mgr = this.getManager();
         if (mgr instanceof RecipientFieldManager) {
            RecipientFieldManager rfm = (RecipientFieldManager)mgr;
            if (rfm._editable) {
               if (Dialog.ask(2) == 3) {
                  new DeleteRecipientVerb(this, rfm).invoke(null);
               }

               return true;
            }
         }
      }

      return super.keyChar(character, status, time);
   }
}
