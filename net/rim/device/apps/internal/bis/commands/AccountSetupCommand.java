package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import net.rim.device.api.i18n.Locale;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.ClientPersistentState;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.protocol.RestClient$RESTCallResult;
import net.rim.device.apps.internal.bis.protocol.RestClient$SignupUserCallResult;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;
import net.rim.device.apps.internal.bis.utils.system.DeviceIDs;

public final class AccountSetupCommand implements DomainCommand {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      String userName = (String)params.get("userName");
      String password = (String)params.get("password");
      DomainCommandResult result = null;
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      ClientSessionState.getInstance().setAccountSetupUserName(userName);
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");

      try {
         String[] userNameSuggestions = new String[3];
         String pin = DeviceIDs.getInstance().getPIN();
         String imei = DeviceIDs.getInstance().getIMEIESN();
         Locale locale = ClientPersistentState.getInstance().getLocale();
         RestClient$SignupUserCallResult callResult = restClient.signupUser(
            configRecord.getBrandName(),
            pin,
            imei,
            (String)params.get("userName"),
            (String)params.get("password"),
            locale.toString(),
            ClientSessionState.getInstance().getTermsVersion(),
            userNameSuggestions
         );
         if (callResult.getRESTStatusCode() != 200) {
            if (callResult.getRESTStatusCode() == 10000) {
               ClientSessionState.getInstance().setAccountSetupSuggestions(userNameSuggestions);
               return new DomainCommandResult("showAccountSuggestions", null, null);
            }

            if (callResult.getRESTStatusCode() == 10100) {
               return new DomainCommandResult("deviceInUse", ApplicationResources.getString(109), null);
            }

            BISEventLogger.logEvent("Account Setup: Unhandled REST response code: " + callResult.getRESTStatusCode(), 0);
            return new DomainCommandResult("error", null, null);
         }

         ClientSessionState.getInstance().setUserInfo(callResult.getUserInfo());
         RestClient$RESTCallResult loginResult = restClient.login(
            configRecord.getBrandName(), callResult.getUserInfo().getUsername(), password, pin, imei, null, false
         );
         if (loginResult.getRESTStatusCode() == 200) {
            if (ClientSessionState.getInstance().getUserInfo().isBBMail()) {
               Mailbox[] mailboxes = ClientSessionState.getInstance().getUserInfo().getMailboxes();
               if (mailboxes != null && mailboxes.length != 0) {
                  ClientSessionState.getInstance().setIntegrationEmail(mailboxes[0].getDescription());
                  return new DomainCommandResult("successBBMail", null, null);
               }

               return new DomainCommandResult("error", null, null);
            }

            return new DomainCommandResult("success", null, null);
         }
      } catch (Throwable var15) {
         BISEventLogger.logEvent(e.getMessage(), 0);
         return new DomainCommandResult("error", null, null);
      }

      return new DomainCommandResult("success", null, null);
   }
}
