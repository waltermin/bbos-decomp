package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BasicScreen;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.data.Filter;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class DeleteFilterConfirmationScreen extends BasicScreen {
   @Override
   public final void refresh(Hashtable screenParams) {
      Filter filter = ClientSessionState.getInstance().getFilterToModify();
      Mailbox mailboxToDelete = ClientSessionState.getInstance().getMailboxToModify();
      this.setTitle(ApplicationResources.getString(5));
      String deleteConfirmText = MessageFormat.format(ApplicationResources.getString(296), new String[]{filter.getName()});
      this.addContentField(new LabelField(deleteConfirmText));
      Button noButton = new Button(ApplicationResources.getString(31));
      Button yesButton = new Button(ApplicationResources.getString(32));
      this.addButtonBarButtons(new Button[]{noButton, yesButton}, false, 1);
      this.attachEventToField(yesButton, new CommandEvent(32, 25, new String[]{"filterid", "filtername"}));
      BackEvent backEvent = new BackEvent(31);
      this.attachEventToField(noButton, backEvent);
      this.setDefaultEvent(backEvent);
   }
}
