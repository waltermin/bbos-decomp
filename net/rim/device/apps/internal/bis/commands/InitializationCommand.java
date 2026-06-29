package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.bis.BISClient;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.data.BrandingInfo;
import net.rim.device.apps.internal.bis.invoke.BISClientInvoke;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.protocol.RestClient$GetBrandingInfoCallResult;
import net.rim.device.apps.internal.bis.protocol.RestClient$PreAuthenticationCallResult;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;
import net.rim.device.apps.internal.bis.utils.system.DeviceIDs;

public final class InitializationCommand implements DomainCommand {
   public static final String TMOBILE_BRAND = "tmobile";

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      BISClient app = (BISClient)UiApplication.getUiApplication();
      boolean ribbonLaunch = app.getUiMode() == 0;
      BISClientInvoke.destroyLaunchProcess(ribbonLaunch);
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");

      try {
         RestClient$GetBrandingInfoCallResult brandCallResult = restClient.getBrandingInfo(configRecord.getBrandName());
         if (brandCallResult.getRESTStatusCode() == 200) {
            BrandingInfo brandingInfo = brandCallResult.getBrandingInfo();
            ClientSessionState.getInstance().setBrandingInfo(brandingInfo);
            if ("tmobile".equalsIgnoreCase(configRecord.getBrandName())) {
               return LoginCommand.login(configRecord.getBrandName(), "UNKNOWN", "FAKEPASSWORD", false, false);
            } else {
               RestClient$PreAuthenticationCallResult authCallResult = restClient.preAuthenticate(
                  configRecord.getBrandName(), DeviceIDs.getInstance().getPIN(), DeviceIDs.getInstance().getIMEIESN()
               );
               if (authCallResult.getRESTStatusCode() == 10302 || authCallResult.getRESTStatusCode() == 10303 || authCallResult.getRESTStatusCode() == 10304) {
                  ClientSessionState.getInstance().setAutoAuth(true);
                  return LoginCommand.login(configRecord.getBrandName(), authCallResult.getAuthInfo().getUsername(), "FAKEPASSWORD", false, true);
               } else if (authCallResult.getRESTStatusCode() == 10301) {
                  BISEventLogger.logEvent("Failed pre-auth call, got status code " + authCallResult.getRESTStatusCode(), 0);
                  return new DomainCommandResult("autoAuthFailed", null, null);
               } else {
                  return brandCallResult.getBrandingInfo().isAutoLoginEnabled()
                     ? new DomainCommandResult("autoLogin", null, null)
                     : new DomainCommandResult("success", null, null);
               }
            }
         } else {
            return new DomainCommandResult("failed", null, null);
         }
      } catch (Throwable var9) {
         BISEventLogger.logEvent(e.toString(), 0);
         return new DomainCommandResult("failed", null, null);
      }
   }
}
