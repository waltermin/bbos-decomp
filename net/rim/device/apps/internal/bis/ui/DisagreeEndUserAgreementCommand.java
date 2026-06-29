package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;

public final class DisagreeEndUserAgreementCommand implements DomainCommand {
   @Override
   public final DomainCommandResult run(Hashtable params) {
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");
      restClient.getHttpClient().clearCookie();
      boolean autoAuth = false;
      if (ClientSessionState.getInstance().getUserInfo() != null && ClientSessionState.getInstance().getUserInfo().isAutoAuth()) {
         autoAuth = true;
      }

      ClientSessionState.getInstance().setUserInfo(null);
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      String brandName = configRecord.getBrandName();
      if ("tmobile".equalsIgnoreCase(brandName)) {
         return new DomainCommandResult("tmobileSuccess", null, null);
      } else {
         return autoAuth ? new DomainCommandResult("autoAuthSuccess", null, null) : new DomainCommandResult("success", null, null);
      }
   }
}
