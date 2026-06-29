package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.protocol.RestClient$AddMailboxCallResult;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ArgUtils;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;

public final class ExistingEmailSetupCommand implements DomainCommand {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      String email = ArgUtils.getStringValue(params, "email");
      String password = ArgUtils.getStringValue(params, "password");
      String reentered = ArgUtils.getStringValue(params, "reentered");
      String aolTermsAccepted = ArgUtils.getStringValue(params, "aolTermsAccepted");
      boolean credentialsReentered = "true".equals(reentered);
      Boolean aolIntegrationPermitted = aolTermsAccepted == null ? null : new Boolean("true".equals(aolTermsAccepted));
      String commandResultID = null;
      String commandResultErrorMsg = null;
      String commandResultStatusMsg = null;
      ClientSessionState sessionState = ClientSessionState.getInstance();
      sessionState.setIntegrationEmail(email);
      sessionState.setIntegrationPassword(password);
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");
      String userName = sessionState.getUserInfo().getUsername();

      try {
         RestClient$AddMailboxCallResult callResult = restClient.addSimpleAccount(
            configRecord.getBrandName(), userName, email, password, aolIntegrationPermitted
         );
         long restStatusCode = callResult.getRESTStatusCode();
         if (restStatusCode == 200) {
            Mailbox mailbox = callResult.getMailbox();
            sessionState.getUserInfo().addMailbox(mailbox);
            commandResultID = "success";
         } else if (restStatusCode != 10202 && restStatusCode != 10209 && restStatusCode != 500) {
            if (restStatusCode == 10200) {
               commandResultID = "promptAOLTerms";
            } else if (restStatusCode == 10207) {
               commandResultID = "promptHotmailError";
            } else {
               if (callResult.getRESTStatusCode() == 401) {
                  return DomainCommand.SESSION_TIMEOUT_RESULT;
               }

               BISEventLogger.logEvent("Existing Mail Setup: Unhandled REST response code: " + callResult.getRESTStatusCode(), 0);
               commandResultID = "failed";
               commandResultErrorMsg = ApplicationResources.getString(192);
            }
         } else if (credentialsReentered) {
            commandResultID = "selectAccount";
         } else {
            commandResultID = "reenter";
         }
      } catch (Throwable var20) {
         BISEventLogger.logEvent(e.toString(), 0);
         commandResultID = "error";
         return new DomainCommandResult(commandResultID, commandResultErrorMsg, commandResultStatusMsg);
      }

      return new DomainCommandResult(commandResultID, commandResultErrorMsg, commandResultStatusMsg);
   }
}
