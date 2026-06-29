package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.data.Filter;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.protocol.RestClient$RESTCallResult;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;

public final class DeleteFilterExecuteCommand implements DomainCommand {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      Filter filter = ClientSessionState.getInstance().getFilterToModify();
      Mailbox mailbox = ClientSessionState.getInstance().getMailboxToModify();
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");

      try {
         String bisUserName = ClientSessionState.getInstance().getUserInfo().getUsername();
         RestClient$RESTCallResult callResult = restClient.deleteFilter(configRecord.getBrandName(), bisUserName, mailbox.getSrcMboxID(), filter.getId());
         if (callResult.getRESTStatusCode() != 200) {
            if (callResult.getRESTStatusCode() == 401) {
               return DomainCommand.SESSION_TIMEOUT_RESULT;
            }

            BISEventLogger.logEvent(((StringBuffer)(new Object("Delete: Unhandled REST response code: "))).append(callResult.getRESTStatusCode()).toString(), 0);
            return new DomainCommandResult("failed", ApplicationResources.getString(192), null);
         }

         ClientSessionState.getInstance().setFilterToModify(null);
         mailbox.removeFilter(filter.getId());
      } catch (Throwable var9) {
         BISEventLogger.logEvent(e.toString(), 0);
         return new DomainCommandResult("error", null, null);
      }

      return new DomainCommandResult("success", null, null);
   }
}
