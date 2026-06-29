package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.protocol.RestClient$RESTCallResult;
import net.rim.device.apps.internal.bis.utils.ArgUtils;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;

public final class ForgotPasswordCommand implements DomainCommand {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      String userName = ArgUtils.getStringValue(params, "userName");
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");

      try {
         RestClient$RESTCallResult callResult = restClient.sendForgotPassword(configRecord.getBrandName(), userName);
         long restStatusCode = callResult.getRESTStatusCode();
         if (callResult.getRESTStatusCode() == 200) {
            return new DomainCommandResult("success", null, ApplicationResources.getString(100));
         }

         if (restStatusCode == 404) {
            return new DomainCommandResult("failed", ApplicationResources.getString(98), null);
         }

         BISEventLogger.logEvent(
            ((StringBuffer)(new Object("Forgot Password: Unhandled REST response code: "))).append(callResult.getRESTStatusCode()).toString(), 0
         );
         return new DomainCommandResult("error", null, null);
      } catch (Throwable var9) {
         BISEventLogger.logEvent(e.toString(), 0);
         return new DomainCommandResult("error", null, null);
      }
   }
}
