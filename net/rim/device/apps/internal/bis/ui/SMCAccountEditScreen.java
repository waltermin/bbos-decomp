package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class SMCAccountEditScreen extends UserSettingsScreen {
   public SMCAccountEditScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      Mailbox mailboxToEdit = ClientSessionState.getInstance().getMailboxToModify();
      String title = MessageFormat.format(ApplicationResources.getString(150), new String[]{mailboxToEdit.getDescription()});
      this.setTitle(title);
      this.addContentField(new LabelField(ApplicationResources.getString(220)));
      Button ok = new Button(ApplicationResources.getString(39));
      this.addButtonBarButtons(new Button[]{ok}, false);
      LinkEvent okEvent = new LinkEvent(39, 7);
      this.attachEventToField(ok, okEvent);
      this.setDefaultEvent(okEvent);
      this.setHelp("index.wml");
   }
}
