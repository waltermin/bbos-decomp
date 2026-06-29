package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.data.UserInfo;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class ValidateCommand implements DomainCommand {
   @Override
   public final DomainCommandResult run(Hashtable params) {
      String description = (String)params.get("description");
      UserInfo userInfo = ClientSessionState.getInstance().getUserInfo();
      Mailbox mailboxToValidate = userInfo.getMailbox(description);
      if (mailboxToValidate == null) {
         throw new IllegalArgumentException("Unknown mailbox specified: " + description);
      }

      Vector mailboxes = new Vector();
      mailboxes.addElement(mailboxToValidate);
      Hashtable forwardParams = new Hashtable();
      forwardParams.put("mailboxesToValidate", mailboxes);
      return new DomainCommandResult("success", null, null, forwardParams);
   }
}
