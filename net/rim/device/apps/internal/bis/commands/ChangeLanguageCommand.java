package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import net.rim.device.api.i18n.Locale;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.ClientPersistentState;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.protocol.RestClient$RESTCallResult;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;

public final class ChangeLanguageCommand implements DomainCommand {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      String locale = (String)params.get("locale");
      if (null == locale || locale.length() < 2) {
         return new DomainCommandResult("failed", "Null or invalid value passed for locale", null);
      }

      if (!ApplicationResources.isAppLocalePresent(locale)) {
         return new DomainCommandResult("failed", "Locale not present on device", null);
      }

      ClientSessionState sessionState = ClientSessionState.getInstance();
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");
      String userName = sessionState.getUserInfo().getUsername();
      String language = locale.substring(0, 2);
      String country = null;
      if (locale.length() == 5) {
         country = locale.substring(3);
      }

      try {
         RestClient$RESTCallResult callResult = restClient.changeLanguage(configRecord.getBrandName(), userName, language, country);
         long restStatusCode = callResult.getRESTStatusCode();
         if (callResult.getRESTStatusCode() == 200) {
            Locale fullLocale = Locale.get(locale);
            sessionState.getUserInfo().setLocale(fullLocale);
            ClientPersistentState.getInstance().setLocale(fullLocale);
            return new DomainCommandResult("success", null, ApplicationResources.getString(215));
         }

         if (restStatusCode == 404) {
            return new DomainCommandResult("failed", ApplicationResources.getString(216), null);
         }

         if (callResult.getRESTStatusCode() == 401) {
            return DomainCommand.SESSION_TIMEOUT_RESULT;
         }

         BISEventLogger.logEvent("Change Language: Unhandled REST response code: " + callResult.getRESTStatusCode(), 0);
         return new DomainCommandResult("failed", ApplicationResources.getString(192), null);
      } catch (Throwable var14) {
         BISEventLogger.logEvent(e.toString(), 0);
         return new DomainCommandResult("error", null, null);
      }
   }
}
