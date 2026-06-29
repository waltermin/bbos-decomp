package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.data.UserInfo;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class ValidateSkipCommand implements DomainCommand {
   @Override
   public final DomainCommandResult run(Hashtable params) {
      UserInfo userInfo = ClientSessionState.getInstance().getUserInfo();
      Vector mailboxes = (Vector)params.get("mailboxesToValidate");
      if (mailboxes != null && mailboxes.size() != 0) {
         mailboxes.removeElementAt(0);
         String resultId = "success";
         if (mailboxes.size() > 0) {
            resultId = "successMore";
         } else {
            params = null;
         }

         return new DomainCommandResult(resultId, null, null, params);
      } else {
         BISEventLogger.logEvent("No mailbox found to skip", 0);
         return new DomainCommandResult("error", null, null);
      }
   }
}
