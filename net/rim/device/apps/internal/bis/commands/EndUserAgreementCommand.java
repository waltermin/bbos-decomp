package net.rim.device.apps.internal.bis.commands;

import java.io.InputStream;
import java.util.Hashtable;
import net.rim.device.api.i18n.Locale;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.ClientPersistentState;
import net.rim.device.apps.internal.bis.api.io.http.HttpClient;
import net.rim.device.apps.internal.bis.api.io.http.HttpResponse;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.data.BrandingInfo;
import net.rim.device.apps.internal.bis.data.UserInfo;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.protocol.RestClient$RESTCallResult;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;
import net.rim.device.cldc.io.utility.URIEncoder;

public final class EndUserAgreementCommand implements DomainCommand {
   boolean _acceptAgreement;
   private static final String URL_BEFORE_ACCEPTED = "?c=10&a=44";
   private static final String URL_AFTER_ACCEPTED = "?c=1&a=109";
   private static final String WAP_CID = "WAP";

   public EndUserAgreementCommand(boolean acceptAgreement) {
      this._acceptAgreement = acceptAgreement;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");
      HttpClient httpClient = restClient.getHttpClient();
      ClientSessionState sessionState = ClientSessionState.getInstance();
      ClientPersistentState persistentState = ClientPersistentState.getInstance();
      boolean termsLoaded = sessionState.getTermsVersion() != null && sessionState.getTermsAndConditions() != null;
      if (!termsLoaded) {
         label71:
         try {
            termsLoaded = this.loadEndUserAgreement();
         } catch (Throwable var10) {
            BISEventLogger.logEvent(e.toString(), 0);
            break label71;
         }
      }

      if (!termsLoaded) {
         return new DomainCommandResult("error", null, null);
      }

      if (this._acceptAgreement && sessionState.getUserInfo() != null) {
         if (this.acceptEndUserAgreement(
            configRecord.getBrandName(), sessionState.getUserInfo().getUsername(), sessionState.getTermsVersion(), persistentState.getLocale()
         )) {
            sessionState.getUserInfo().setTimeStamp(System.currentTimeMillis());
            if (sessionState.getUserInfo().isAutoAuth() && !sessionState.getUserInfo().isBBMail()) {
               return new DomainCommandResult("autoAuthSuccess", null, null);
            } else {
               return sessionState.getUserInfo().isAutoAuth() && sessionState.getUserInfo().isBBMail()
                  ? new DomainCommandResult("bbmailAutoAuthSuccess", null, null)
                  : new DomainCommandResult("acceptLogin", null, null);
            }
         } else {
            return new DomainCommandResult("error", null, null);
         }
      } else {
         return new DomainCommandResult("success", null, null);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected final boolean acceptEndUserAgreement(String brandName, String userName, String tcversion, Locale locale) {
      try {
         RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");
         RestClient$RESTCallResult callResult = restClient.acceptEndUserAgreement(brandName, userName, tcversion, locale != null ? locale.toString() : null);
         long restStatusCode = callResult.getRESTStatusCode();
         if (restStatusCode == 200) {
            return true;
         }
      } catch (Throwable var10) {
         BISEventLogger.logEvent(e.toString(), 0);
         return false;
      }

      return false;
   }

   protected final boolean loadEndUserAgreement() {
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      String transportUID = configRecord.getTransportUID();
      long downloadTimeout = configRecord.getServiceTimeout();
      boolean useWapGateway = "WAP".equalsIgnoreCase(BISClientConfigRecord.getBISClientConfigRecord().getTransportCID());
      HttpClient httpClient = new HttpClient(transportUID, downloadTimeout, null, 0, useWapGateway);
      ClientSessionState sessionState = ClientSessionState.getInstance();
      BrandingInfo brandingInfo = sessionState.getBrandingInfo();
      UserInfo userInfo = sessionState.getUserInfo();
      String url = null;
      if (null != ClientSessionState.getInstance().getUserInfo() && ClientSessionState.getInstance().getUserInfo().getTimeStamp() > 0 && !this._acceptAgreement
         )
       {
         StringBuffer urlBuffer = (StringBuffer)(new Object());
         URIEncoder.encode(
            urlBuffer, ((StringBuffer)(new Object())).append(userInfo.getUsername()).append(":").append(configRecord.getBrandName()).toString(), "UTF-8", true
         );
         url = ((StringBuffer)(new Object()))
            .append(brandingInfo.getEndUserAgreementURL())
            .append("?c=1&a=109")
            .append("&")
            .append("externalId=")
            .append(urlBuffer.toString())
            .toString();
      } else {
         Locale locale = ClientPersistentState.getInstance().getLocale();
         url = ((StringBuffer)(new Object()))
            .append(brandingInfo.getEndUserAgreementURL())
            .append("?c=10&a=44")
            .append("&brand=")
            .append(configRecord.getBrandName())
            .append("&locale=")
            .append(locale != null ? locale.toString() : null)
            .toString();
      }

      EndUserAgreementCommand$TermsAndConditionsResponseHandler termsHandler = new EndUserAgreementCommand$TermsAndConditionsResponseHandler(this);
      HttpResponse response = httpClient.doXmlExchange(url, "GET", null, null, null, true);
      termsHandler.parse((InputStream)(new Object(response.getResponsePayload())));

      while (termsHandler.isRetry()) {
         int retryTimeInSeconds = Integer.valueOf(termsHandler.getRetry());
         synchronized (this) {
            this.wait(retryTimeInSeconds * 1000);
         }

         response = httpClient.doXmlExchange(url, "GET", null, null, null, true);
         termsHandler.parse((InputStream)(new Object(response.getResponsePayload())));
      }

      String terms = termsHandler.getTermsAndConditions();
      String version = termsHandler.getVersion();
      if (null != terms && null != version) {
         sessionState.setTermsAndConditions(terms);
         sessionState.setTermsVersion(version);
         return true;
      } else {
         return false;
      }
   }
}
