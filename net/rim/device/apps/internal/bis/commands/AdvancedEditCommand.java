package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.data.UserInfo;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.protocol.RestClient$RESTCallResult;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ArgUtils;
import net.rim.device.apps.internal.bis.utils.ArgValidationUtils;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;

public final class AdvancedEditCommand implements DomainCommand {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      Mailbox mailboxToEdit = ClientSessionState.getInstance().getMailboxToModify();
      String password = ArgUtils.getStringValue(params, "password");
      String server = ArgUtils.getStringValue(params, "server");
      String useSSL = ArgUtils.getStringValue(params, "ssl");
      Mailbox tempMailbox = new Mailbox();
      tempMailbox.copy(mailboxToEdit);
      boolean dirty = false;
      if (ArgValidationUtils.hasStringValueChanged(tempMailbox.getServer(), server)) {
         tempMailbox.setServer(server);
         dirty = true;
      }

      if (ArgValidationUtils.hasBooleanValueChanged(tempMailbox.getUseSSL(), useSSL)) {
         tempMailbox.setUseSSL("true".equals(useSSL));
         dirty = true;
      }

      if (ClientSessionState.getInstance().getUserInfo().isAutoAuth()) {
         tempMailbox.setPassword(password);
      }

      if (!dirty) {
         return new DomainCommandResult("success", null, null);
      }

      if ((password == null || password.length() == 0) && ClientSessionState.getInstance().getUserInfo().isAutoAuth()) {
         return new DomainCommandResult("passwordValidation", null, null, params);
      }

      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");
      UserInfo userInfo = ClientSessionState.getInstance().getUserInfo();
      if (configRecord != null && restClient != null && userInfo != null) {
         try {
            String bisUserName = userInfo.getUsername();
            RestClient$RESTCallResult callResult = restClient.updateAccount(configRecord.getBrandName(), bisUserName, tempMailbox, null);
            if (callResult.getRESTStatusCode() != 200) {
               if (callResult.getRESTStatusCode() == 401) {
                  return DomainCommand.SESSION_TIMEOUT_RESULT;
               }

               if (callResult.getRESTStatusCode() == 10202) {
                  String errorMsg = MessageFormat.format(ApplicationResources.getString(228), new Object[]{mailboxToEdit.getDescription()});
                  return new DomainCommandResult("validationFailed", errorMsg, null);
               }

               BISEventLogger.logEvent(
                  ((StringBuffer)(new Object("Advanced Edit: Unhandled REST response code: "))).append(callResult.getRESTStatusCode()).toString(), 0
               );
               String failureForward = "failed";
               if (mailboxToEdit.getMailboxType() == 5) {
                  failureForward = "failedOWA";
               }

               return new DomainCommandResult(failureForward, ApplicationResources.getString(192), null);
            }

            userInfo.removeMailbox(mailboxToEdit);
            userInfo.addMailbox(tempMailbox);
            ClientSessionState.getInstance().setMailboxToModify(tempMailbox);
         } catch (Throwable var15) {
            BISEventLogger.logEvent(e.toString(), 0);
            return new DomainCommandResult("error", null, null);
         }

         return new DomainCommandResult("success", null, ApplicationResources.getString(112));
      } else {
         return new DomainCommandResult("error", null, null);
      }
   }
}
