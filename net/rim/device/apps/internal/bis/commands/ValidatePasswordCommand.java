package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.protocol.RestClient$ValidatePasswordsCallResult;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;

public final class ValidatePasswordCommand implements DomainCommand {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");
      String userName = ClientSessionState.getInstance().getUserInfo().getUsername();
      Vector mailboxes = (Vector)params.get("mailboxesToValidate");
      if (mailboxes == null) {
         BISEventLogger.logEvent("No mailbox found for validation", 0);
         return new DomainCommandResult("error", null, null);
      }

      Mailbox origMailbox = (Mailbox)mailboxes.firstElement();
      Mailbox mailboxToValidate = new Mailbox();
      mailboxToValidate.copy(origMailbox);
      String commandResultID = null;
      String commandResultErrorMsg = null;
      String commandResultStatusMsg = null;
      String newPassword = (String)params.get("password");
      Hashtable idToPassword = new Hashtable();
      idToPassword.put(mailboxToValidate.getSrcMboxID(), newPassword);

      try {
         RestClient$ValidatePasswordsCallResult callResult = restClient.validatePasswords(configRecord.getBrandName(), userName, idToPassword);
         long restStatusCode = callResult.getRESTStatusCode();
         if (restStatusCode == 200 && callResult.getFailedMboxes().length == 0) {
            mailboxes.removeElementAt(0);
            if (mailboxes.size() == 0) {
               commandResultID = "success";
            } else {
               commandResultID = "successMore";
            }

            commandResultStatusMsg = ApplicationResources.getString(229);
            mailboxToValidate.setValid();
            ClientSessionState.getInstance().getUserInfo().removeMailbox(origMailbox);
            ClientSessionState.getInstance().getUserInfo().addMailbox(mailboxToValidate);
         } else if (restStatusCode == 200 && callResult.getFailedMboxes().length == 1) {
            commandResultErrorMsg = MessageFormat.format(ApplicationResources.getString(228), new String[]{mailboxToValidate.getDescription()});
            commandResultID = "validationFailed";
            ForgotHostedPasswordCommand.handleAttempts(params, mailboxToValidate, restClient, configRecord);
         } else {
            if (restStatusCode == 401) {
               return DomainCommand.SESSION_TIMEOUT_RESULT;
            }

            BISEventLogger.logEvent("Change Device: Uhandled REST response code: " + callResult.getRESTStatusCode(), 0);
            commandResultID = "error";
         }
      } catch (Throwable var17) {
         BISEventLogger.logEvent(e.toString(), 0);
         commandResultID = "error";
         return new DomainCommandResult(commandResultID, commandResultErrorMsg, commandResultStatusMsg, params);
      }

      return new DomainCommandResult(commandResultID, commandResultErrorMsg, commandResultStatusMsg, params);
   }
}
