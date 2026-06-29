package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.data.SecretQuestion;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.protocol.RestClient$RESTCallResult;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;
import net.rim.device.apps.internal.bis.utils.system.DeviceIDs;

public final class AcceptPinChangeCommand extends LoginCommand {
   String _acceptPinChange;

   public AcceptPinChangeCommand(String acceptPinChange) {
      this._acceptPinChange = acceptPinChange;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      String successMessage = null;
      String failedMessage = null;
      if (this._acceptPinChange != null && this._acceptPinChange.equals("yes")) {
         successMessage = ApplicationResources.getString(113);
         failedMessage = ApplicationResources.getString(204);
      }

      try {
         BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
         String brandName = configRecord.getBrandName();
         ClientSessionState sessionState = ClientSessionState.getInstance();
         String userName = sessionState.getTempUsername();
         String password = sessionState.getTempPassword();
         boolean rememberCredentials = sessionState.getTempRememberCredentials();
         RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");
         RestClient$RESTCallResult loginCallResult = restClient.login(
            brandName,
            userName,
            password,
            DeviceIDs.getInstance().getPIN(),
            DeviceIDs.getInstance().getIMEIESN(),
            this._acceptPinChange,
            sessionState.isAutoAuth()
         );
         if (loginCallResult.getRESTStatusCode() != 200) {
            BISEventLogger.logEvent(
               ((StringBuffer)(new Object("Accept Pin Change: Unhandled REST response code: "))).append(loginCallResult.getRESTStatusCode()).toString(), 0
            );
            return new DomainCommandResult("failed", failedMessage, null);
         }

         if (!LoginCommand.createSession(brandName, userName, password, rememberCredentials)) {
            return new DomainCommandResult("failed", ApplicationResources.getString(96), null);
         }

         if (sessionState.getSecretQuestions() == null) {
            SecretQuestion[] secretQuestions = LoginCommand.getSecretQuestions();
            if (secretQuestions == null) {
               BISEventLogger.logEvent("Unable to get secret questions", 0);
               return new DomainCommandResult("error", null, null);
            }

            sessionState.setSecretQuestions(secretQuestions);
         }

         if (sessionState.isAutoAuth() && this._acceptPinChange.equals("yes")) {
            Hashtable pendingParams = (Hashtable)(new Object());
            Vector mailboxes = (Vector)(new Object());
            Mailbox[] arrMailboxes = sessionState.getUserInfo().getMailboxes();

            for (int i = 0; i < arrMailboxes.length; i++) {
               arrMailboxes[i].setInvalid();
               mailboxes.addElement(arrMailboxes[i]);
            }

            pendingParams.put("mailboxesToValidate", mailboxes);
            return new DomainCommandResult("autoAuthSuccess", null, successMessage, pendingParams);
         } else {
            return new DomainCommandResult("success", null, successMessage);
         }
      } catch (Throwable var17) {
         BISEventLogger.logEvent(e.getMessage(), 0);
         return new DomainCommandResult("error", null, null);
      }
   }
}
