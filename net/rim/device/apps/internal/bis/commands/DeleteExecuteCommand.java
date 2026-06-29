package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.protocol.RestClient$RESTCallResult;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;

public final class DeleteExecuteCommand implements DomainCommand {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      Mailbox mailboxToDelete = ClientSessionState.getInstance().getMailboxToModify();
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");

      try {
         String bisUserName = ClientSessionState.getInstance().getUserInfo().getUsername();
         RestClient$RESTCallResult callResult = restClient.deleteAccount(configRecord.getBrandName(), bisUserName, mailboxToDelete.getSrcMboxID());
         if (callResult.getRESTStatusCode() != 200) {
            if (callResult.getRESTStatusCode() == 401) {
               return DomainCommand.SESSION_TIMEOUT_RESULT;
            }

            BISEventLogger.logEvent("Delete: Unhandled REST response code: " + callResult.getRESTStatusCode(), 0);
            return new DomainCommandResult("failed", ApplicationResources.getString(192), null);
         }

         ClientSessionState.getInstance().getUserInfo().removeMailbox(mailboxToDelete);
      } catch (Throwable var8) {
         BISEventLogger.logEvent(e.toString(), 0);
         return new DomainCommandResult("error", null, null);
      }

      return new DomainCommandResult("success", null, null);
   }
}
