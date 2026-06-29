package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.protocol.RestClient$RESTCallResult;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ArgUtils;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;

public final class ForgotHostedPasswordCommand implements DomainCommand {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      String answer = ArgUtils.getStringValue(params, "answer");
      if (answer == null) {
         return new DomainCommandResult("forwardToScreen", null, null, params);
      }

      Mailbox mailbox = null;
      if (params.containsKey("mailboxesToValidate")) {
         Vector mailboxes = (Vector)params.get("mailboxesToValidate");
         mailbox = (Mailbox)mailboxes.firstElement();
      } else {
         mailbox = ClientSessionState.getInstance().getMailboxToModify();
      }

      if (mailbox == null) {
         BISEventLogger.logEvent("Forgot Hosted Password: no mailbox found", 0);
         return new DomainCommandResult("error", null, null);
      }

      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");

      try {
         RestClient$RESTCallResult callResult = restClient.sendForgotHostedPassword(
            configRecord.getBrandName(), ClientSessionState.getInstance().getUserInfo().getUsername(), answer
         );
         long restStatusCode = callResult.getRESTStatusCode();
         if (callResult.getRESTStatusCode() == 200) {
            return new DomainCommandResult("success", null, ApplicationResources.getString(100), params);
         } else if (callResult.getRESTStatusCode() == 10400) {
            handleAttempts(params, mailbox, restClient, configRecord);
            return new DomainCommandResult("failed", ApplicationResources.getString(248), null, params);
         } else {
            BISEventLogger.logEvent("Forgot Hosted Password: Unhandled REST response code: " + callResult.getRESTStatusCode(), 0);
            return new DomainCommandResult("error", null, null);
         }
      } catch (Throwable var10) {
         BISEventLogger.logEvent(e.toString(), 0);
         return new DomainCommandResult("error", null, null);
      }
   }

   public static final void handleAttempts(Hashtable params, Mailbox mailbox, RestClient restClient, BISClientConfigRecord configRecord) {
      Integer attempt = (Integer)params.get("validationAttempt");
      if (attempt == null) {
         attempt = new Integer(1);
      } else {
         attempt = new Integer(attempt + 1);
      }

      if (attempt >= ClientSessionState.getInstance().getBrandingInfo().getNumValidationAttempts() && mailbox.isValid()) {
         RestClient$RESTCallResult updateCallResult = restClient.updateAccount(
            configRecord.getBrandName(), ClientSessionState.getInstance().getUserInfo().getUsername(), mailbox, null
         );
         if (updateCallResult.getRESTStatusCode() != 200) {
            BISEventLogger.logEvent("Invalidate account: Unhandled REST response code: " + updateCallResult.getRESTStatusCode(), 0);
         } else {
            mailbox.setInvalid();
         }
      }

      params.put("validationAttempt", attempt);
   }
}
