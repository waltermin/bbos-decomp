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
import net.rim.device.apps.internal.bis.utils.ArgUtils;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;

public final class ChangePasswordCommand implements DomainCommand {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      String password = ArgUtils.getStringValue(params, "password");
      String oldPassword = ArgUtils.getStringValue(params, "oldPassword");
      String commandResultID = null;
      String commandResultErrorMsg = null;
      String commandResultStatusMsg = null;
      ClientSessionState sessionState = ClientSessionState.getInstance();
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");
      String userName = sessionState.getUserInfo().getUsername();

      try {
         RestClient$RESTCallResult callResult = restClient.updatePassword(configRecord.getBrandName(), userName, oldPassword, password);
         long restStatusCode = callResult.getRESTStatusCode();
         if (restStatusCode == 200) {
            commandResultID = "success";
            commandResultStatusMsg = ApplicationResources.getString(115);
         } else if (restStatusCode == 10005) {
            commandResultErrorMsg = ApplicationResources.getString(203);
            commandResultID = "failed";
         } else {
            if (callResult.getRESTStatusCode() == 401) {
               return DomainCommand.SESSION_TIMEOUT_RESULT;
            }

            BISEventLogger.logEvent(
               ((StringBuffer)(new Object("Change Password: Unhandled REST response code: "))).append(callResult.getRESTStatusCode()).toString(), 0
            );
            commandResultErrorMsg = ApplicationResources.getString(192);
            commandResultID = "failed";
         }
      } catch (Throwable var15) {
         BISEventLogger.logEvent(e.toString(), 0);
         commandResultID = "error";
         return new DomainCommandResult(commandResultID, commandResultErrorMsg, commandResultStatusMsg);
      }

      return new DomainCommandResult(commandResultID, commandResultErrorMsg, commandResultStatusMsg);
   }
}
