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

public final class HostedEmailSetupCommand implements DomainCommand {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      String hostedMailUserName = ArgUtils.getStringValue(params, "userName");
      String password = ArgUtils.getStringValue(params, "password");
      String secretQuestion = ArgUtils.getStringValue(params, "question");
      Integer secretQuestionId = (Integer)params.get("questionId");
      String secretAnswer = ArgUtils.getStringValue(params, "answer");
      String commandResultID = null;
      String commandResultErrorMsg = null;
      String commandResultStatusMsg = null;
      Hashtable forwardParams = null;
      ClientSessionState sessionState = ClientSessionState.getInstance();
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");
      String userName = sessionState.getUserInfo().getUsername();
      sessionState.setIntegrationEmail(hostedMailUserName);

      try {
         String[] bbmailSuggestions = new String[3];
         RestClient$AddMailboxCallResult callResult = restClient.addHostedAccount(
            configRecord.getBrandName(), userName, hostedMailUserName, password, secretQuestion, secretQuestionId, secretAnswer, bbmailSuggestions
         );
         long restStatusCode = callResult.getRESTStatusCode();
         if (restStatusCode == 200) {
            Mailbox mailbox = callResult.getMailbox();
            sessionState.getUserInfo().addMailbox(mailbox);
            commandResultID = "success";
         } else if (callResult.getRESTStatusCode() == 10210) {
            forwardParams = params;
            forwardParams.put("suggestions", bbmailSuggestions);
            commandResultID = "showHostedMailSuggestions";
         } else {
            if (callResult.getRESTStatusCode() == 401) {
               return DomainCommand.SESSION_TIMEOUT_RESULT;
            }

            BISEventLogger.logEvent("Hosted Mail Setup: Unhandled REST response code: " + callResult.getRESTStatusCode(), 0);
            commandResultID = "error";
            commandResultErrorMsg = ApplicationResources.getString(174);
         }
      } catch (Throwable var21) {
         BISEventLogger.logEvent(e.toString(), 0);
         commandResultID = "error";
         return new DomainCommandResult(commandResultID, commandResultErrorMsg, commandResultStatusMsg, forwardParams);
      }

      return new DomainCommandResult(commandResultID, commandResultErrorMsg, commandResultStatusMsg, forwardParams);
   }
}
