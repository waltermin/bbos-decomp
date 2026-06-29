package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.data.UserInfo;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class EditCommand implements DomainCommand {
   @Override
   public final DomainCommandResult run(Hashtable params) {
      String description = (String)params.get("description");
      UserInfo userInfo = ClientSessionState.getInstance().getUserInfo();
      Mailbox mailboxToEdit = userInfo.getMailbox(description);
      if (mailboxToEdit == null) {
         throw new IllegalArgumentException("Unknown mailbox specified: " + description);
      }

      ClientSessionState.getInstance().setMailboxToModify(mailboxToEdit);
      int mailboxType = mailboxToEdit.getMailboxType();
      switch (mailboxType) {
         case 1:
            ClientSessionState.getInstance().setMailboxToModify(null);
            throw new IllegalStateException("Attempt to edit a mailbox that didn't contain enough type information");
         case 2:
         default:
            return new DomainCommandResult("editHosted", null, null);
         case 3:
            return new DomainCommandResult("editProprietary", null, null);
         case 4:
            return new DomainCommandResult("editISP", null, null);
         case 5:
            return new DomainCommandResult("editOWA", null, null);
         case 6:
            return new DomainCommandResult("editSMC", null, null);
      }
   }
}
