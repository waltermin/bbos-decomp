package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.protocol.RestClient$RESTCallResult;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;

public final class SendSBCommand extends NetCommand {
   @Override
   final DomainCommandResult runInternal(Hashtable params) {
      String commandResult = null;
      String commandErrorMsg = null;
      String commandStatusMsg = null;
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");
      String bisUserName = ClientSessionState.getInstance().getUserInfo().getUsername();
      RestClient$RESTCallResult callResult = restClient.sendServiceBooks(configRecord.getBrandName(), bisUserName);
      if (callResult.getRESTStatusCode() == 200) {
         commandResult = "success";
         commandStatusMsg = ApplicationResources.getString(116);
      } else {
         if (callResult.getRESTStatusCode() == 401) {
            return DomainCommand.SESSION_TIMEOUT_RESULT;
         }

         BISEventLogger.logEvent("Send SB: Unhandled REST response code: " + callResult.getRESTStatusCode(), 0);
         commandErrorMsg = ApplicationResources.getString(192);
         commandResult = "failed";
      }

      return new DomainCommandResult(commandResult, commandErrorMsg, commandStatusMsg);
   }
}
