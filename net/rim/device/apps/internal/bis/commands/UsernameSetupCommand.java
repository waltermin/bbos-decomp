package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.data.UserInfo;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.protocol.RestClient$ChangeToUsernameCallResult;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;

public final class UsernameSetupCommand implements DomainCommand {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");
      ClientSessionState sessionState = ClientSessionState.getInstance();
      String userName = (String)params.get("userName");
      String password = (String)params.get("password");
      String failedIntegrationType = (String)params.get("failedIntegrationType");
      String failedIntegrationEmail = null;
      if (failedIntegrationType != null) {
         failedIntegrationEmail = sessionState.getIntegrationEmail();
      }

      String commandResultID = null;
      String commandResultErrorMsg = null;
      String commandResultStatusMsg = null;
      Hashtable pendingParams = null;

      try {
         RestClient$ChangeToUsernameCallResult callResult = restClient.changeToUsername(
            configRecord.getBrandName(), sessionState.getUserInfo().getUsername(), userName, password, failedIntegrationType, failedIntegrationEmail
         );
         long restStatusCode = callResult.getRESTStatusCode();
         if (restStatusCode != 200) {
            if (callResult.getRESTStatusCode() == 10000) {
               Hashtable paramsToForward = new Hashtable();
               paramsToForward.put("suggestions", callResult.getSuggestions());
               paramsToForward.put("userName", userName);
               return new DomainCommandResult("suggestions", null, null, paramsToForward);
            }

            if (restStatusCode == 401) {
               return DomainCommand.SESSION_TIMEOUT_RESULT;
            }

            BISEventLogger.logEvent("Change Device: Uhandled REST response code: " + callResult.getRESTStatusCode(), 0);
            commandResultID = "error";
         } else {
            commandResultID = "success";
            commandResultStatusMsg = ApplicationResources.getString(234);
            UserInfo userInfo = ClientSessionState.getInstance().getUserInfo();
            userInfo.setUsername(userName);
            userInfo.setAutoAuth(false);
            sessionState.setAutoAuth(false);
            DomainCommandResult loginResult = LoginCommand.login(configRecord.getBrandName(), userName, password, false, false);
            if (!"success".equals(loginResult.getResultName())) {
               commandResultID = "loginFailed";
               commandResultErrorMsg = ApplicationResources.getString(235);
               commandResultStatusMsg = null;
            } else {
               Mailbox[] arrMailboxes = sessionState.getUserInfo().getMailboxes();
               if (arrMailboxes != null && arrMailboxes.length > 0) {
                  pendingParams = new Hashtable();
                  Vector mailboxes = new Vector();

                  for (int i = 0; i < arrMailboxes.length; i++) {
                     arrMailboxes[i].setInvalid();
                     mailboxes.addElement(arrMailboxes[i]);
                  }

                  pendingParams.put("mailboxesToValidate", mailboxes);
               } else {
                  commandResultID = "noAccountsToValidate";
               }

               if (failedIntegrationType != null) {
                  commandResultID = "emailSentConfirmation";
                  commandResultStatusMsg = null;
               }
            }
         }
      } catch (Throwable var22) {
         BISEventLogger.logEvent(e.toString(), 0);
         commandResultID = "error";
         return new DomainCommandResult(commandResultID, commandResultErrorMsg, commandResultStatusMsg, pendingParams);
      }

      return new DomainCommandResult(commandResultID, commandResultErrorMsg, commandResultStatusMsg, pendingParams);
   }
}
