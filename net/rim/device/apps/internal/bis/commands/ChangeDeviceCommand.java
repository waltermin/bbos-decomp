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

public final class ChangeDeviceCommand implements DomainCommand {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      String pin = ArgUtils.getStringValue(params, "pin");
      String imei = ArgUtils.getStringValue(params, "imei");
      String commandResultID = null;
      String commandResultErrorMsg = null;
      String commandResultStatusMsg = null;
      ClientSessionState sessionState = ClientSessionState.getInstance();
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");
      String userName = sessionState.getUserInfo().getUsername();
      Hashtable pendingParams = null;

      try {
         RestClient$RESTCallResult callResult = restClient.changeHandheld(configRecord.getBrandName(), userName, pin, imei);
         long restStatusCode = callResult.getRESTStatusCode();
         if (restStatusCode == 200) {
            sessionState.getUserInfo().setPIN(pin);
            if (!sessionState.getUserInfo().isAutoAuth()) {
               commandResultID = "success";
            } else {
               commandResultID = "autoAuthSuccess";
               pendingParams = (Hashtable)(new Object());
               Vector mailboxes = (Vector)(new Object());
               Mailbox[] arrMailboxes = sessionState.getUserInfo().getMailboxes();

               for (int i = 0; i < arrMailboxes.length; i++) {
                  arrMailboxes[i].setInvalid();
                  mailboxes.addElement(arrMailboxes[i]);
               }

               pendingParams.put("mailboxesToValidate", mailboxes);
            }

            commandResultStatusMsg = ApplicationResources.getString(113);
         } else {
            if (callResult.getRESTStatusCode() == 401) {
               return DomainCommand.SESSION_TIMEOUT_RESULT;
            }

            BISEventLogger.logEvent(
               ((StringBuffer)(new Object("Change Device: Uhandled REST response code: "))).append(callResult.getRESTStatusCode()).toString(), 0
            );
            commandResultErrorMsg = ApplicationResources.getString(114);
            commandResultID = "failed";
         }
      } catch (Throwable var19) {
         BISEventLogger.logEvent(e.toString(), 0);
         commandResultID = "error";
         return new DomainCommandResult(commandResultID, commandResultErrorMsg, commandResultStatusMsg, pendingParams);
      }

      return new DomainCommandResult(commandResultID, commandResultErrorMsg, commandResultStatusMsg, pendingParams);
   }
}
