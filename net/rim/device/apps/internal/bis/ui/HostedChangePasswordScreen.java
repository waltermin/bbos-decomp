package net.rim.device.apps.internal.bis.ui;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class HostedChangePasswordScreen extends ChangePasswordScreen {
   public HostedChangePasswordScreen() {
      super._saveCommandId = 8;
      super._helpFile = "231293.wml";
      super._forgotCommandId = 34;
   }

   @Override
   protected final String getTitle() {
      Mailbox mailboxToEdit = ClientSessionState.getInstance().getMailboxToModify();
      return MessageFormat.format(ApplicationResources.getString(266), new String[]{mailboxToEdit.getDescription()});
   }
}
