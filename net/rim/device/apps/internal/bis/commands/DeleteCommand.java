package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.data.UserInfo;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class DeleteCommand implements DomainCommand {
   @Override
   public final DomainCommandResult run(Hashtable params) {
      String description = (String)params.get("description");
      UserInfo userInfo = ClientSessionState.getInstance().getUserInfo();
      Mailbox mailboxToDelete = userInfo.getMailbox(description);
      if (mailboxToDelete == null) {
         throw new Object(((StringBuffer)(new Object("Unknown mailbox specified: "))).append(description).toString());
      }

      ClientSessionState.getInstance().setMailboxToModify(mailboxToDelete);
      return new DomainCommandResult("success", null, null);
   }
}
