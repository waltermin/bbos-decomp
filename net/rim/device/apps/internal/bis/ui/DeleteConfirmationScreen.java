package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class DeleteConfirmationScreen extends UserSettingsScreen {
   public DeleteConfirmationScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      Mailbox mailboxToDelete = ClientSessionState.getInstance().getMailboxToModify();
      this.setTitle(ApplicationResources.getString(5));
      String email = mailboxToDelete.getEmail();
      String deleteConfirmText = MessageFormat.format(ApplicationResources.getString(30), new String[]{email});
      this.addContentField(new LabelField(deleteConfirmText));
      Button noButton = new Button(ApplicationResources.getString(31));
      Button yesButton = new Button(ApplicationResources.getString(32));
      this.addButtonBarButtons(new Button[]{noButton, yesButton}, false, 1);
      this.attachEventToField(yesButton, new CommandEvent(32, 10, null));
      BackEvent backEvent = new BackEvent(31);
      this.attachEventToField(noButton, backEvent);
      this.setDefaultEvent(backEvent);
      if (mailboxToDelete.getHosted()) {
         this.setHelp("221726.wml");
      } else {
         this.setHelp("221700.wml");
      }
   }
}
