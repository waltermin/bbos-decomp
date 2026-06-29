package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.data.UserInfo;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.protocol.RestClient$GetFiltersCallResult;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;

public final class FilterSummaryCommand implements DomainCommand {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      String description = (String)params.get("description");
      UserInfo userInfo = ClientSessionState.getInstance().getUserInfo();
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      Mailbox mailbox = userInfo.getMailbox(description);
      if (mailbox == null) {
         throw new IllegalArgumentException("Unknown mailbox specified: " + description);
      }

      if (mailbox.getFilters() != null) {
         ClientSessionState.getInstance().setMailboxToModify(mailbox);
         return new DomainCommandResult("success", null, null);
      }

      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");

      try {
         RestClient$GetFiltersCallResult callResult = restClient.getFilters(configRecord.getBrandName(), userInfo.getUsername(), mailbox.getSrcMboxID());
         if (callResult.getRESTStatusCode() != 200) {
            BISEventLogger.logEvent("Filters Summary: Unhandled REST response code: " + callResult.getRESTStatusCode(), 0);
            return new DomainCommandResult("error", null, null);
         }

         mailbox.setFilters(callResult.getFilters());
      } catch (Throwable var9) {
         BISEventLogger.logEvent(e.getMessage(), 0);
         return new DomainCommandResult("error", null, null);
      }

      ClientSessionState.getInstance().setMailboxToModify(mailbox);
      return new DomainCommandResult("success", null, null);
   }
}
