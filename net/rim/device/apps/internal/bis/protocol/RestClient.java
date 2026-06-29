package net.rim.device.apps.internal.bis.protocol;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.apps.internal.bis.api.io.http.HttpClient;
import net.rim.device.apps.internal.bis.api.io.http.HttpResponse;
import net.rim.device.apps.internal.bis.api.io.http.HttpStatusUtils;
import net.rim.device.apps.internal.bis.data.AuthInfo;
import net.rim.device.apps.internal.bis.data.BrandingInfo;
import net.rim.device.apps.internal.bis.data.Filter;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.data.SecretQuestion;
import net.rim.device.apps.internal.bis.data.UserInfo;
import net.rim.device.apps.internal.bis.utils.system.DeviceIDs;

public final class RestClient {
   private HttpClient _httpClient;
   private String _serverURL;
   private static final String SCHEME_HTTP = "http";
   private static final String SCHEME_HTTPS = "https";
   private static final String DEFAULT_SCHEME = "https";
   private static final String REST_HOST_URL_PATTERN = "{0}://{1}";
   private static final String BRAND_URL_PATTERN = "{0}://{1}/brand/{2}";
   private static final String USER_URL_PATTERN = "{0}://{1}/{2}/user";
   private static final String SPECIFIC_USER_URL_PATTERN = "{0}://{1}/{2}/user/{3}";
   private static final String SEND_FORGOT_PASSWORD_URL_PATTERN = "{0}://{1}/{2}/user/{3}/sfp";
   private static final String SEND_FORGOT_HOSTED_PASSWORD_URL_PATTERN = "{0}://{1}/{2}/user/{3}/sfhp";
   private static final String SESSION_URL_PATTERN = "{0}://{1}/{2}/user/{3}/session";
   private static final String CHANGE_HANDHELD_URL_PATTERN = "{0}://{1}/{2}/user/{3}/device";
   private static final String CHANGE_PASSWORD_URL_PATTERN = "{0}://{1}/{2}/user/{3}/password";
   private static final String SEND_SERVICE_BOOKS_URL_PATTERN = "{0}://{1}/{2}/user/{3}/sendSb";
   private static final String LOG_URL_PATTERN = "{0}://{1}/{2}/user/{3}/log";
   private static final String EMAIL_ACCOUNT_URL_PATTERN = "{0}://{1}/{2}/user/{3}/emailAccount";
   private static final String SPECIFIC_EMAIL_ACCOUNT_URL_PATTERN = "{0}://{1}/{2}/user/{3}/emailAccount/{4}";
   private static final String VALIDATE_SRCMBOX_URL_PATTERN = "{0}://{1}/{2}/user/{3}/emailAccount/{4}/validate";
   private static final String CLIENT_APP_STATUS_URL_PATTERN = "{0}://{1}/{2}/clientApp/{3}";
   private static final String CORE_STATUS_URL_PATTERN = "{0}://{1}/{2}/clientApp/{3}/core/status";
   private static final String LANG_STATUS_URL_PATTERN = "{0}://{1}/{2}/clientApp/{3}/language/{4}/{5}/status";
   private static final String ACCEPT_EUA_URL_PATTERN = "{0}://{1}/{2}/user/{3}/acceptTerms";
   private static final String PRE_AUTH_PATTERN = "{0}://{1}/{2}/preAuth";
   private static final String VALIDATE_PASSWORDS_URL_PATTERN = "{0}://{1}/{2}/user/{3}/validatePasswords";
   private static final String CHANGE_TO_USERNAME_URL_PATTERN = "{0}://{1}/{2}/user/{3}/changeToUsername";
   private static final String SECRET_QUESTIONS_URL_PATTERN = "{0}://{1}/{2}/secretQuestions/{3}";
   private static final String REST_REQUEST_METHOD = "RESTRequestMethod";
   private static final String FILTERS_URL_PATTERN = "{0}://{1}/{2}/user/{3}/emailAccount/{4}";
   private static final String FILTERS_RETRIEVE_URL_PATTERN = "{0}://{1}/{2}/user/{3}/emailAccount/{4}/filters";
   private static final String FILTER_ADD_URL_PATTERN = "{0}://{1}/{2}/user/{3}/emailAccount/{4}/filter";
   private static final String FILTER_UPDATE_DELETE_URL_PATTERN = "{0}://{1}/{2}/user/{3}/emailAccount/{4}/filter/{5}";
   private static final String REST_RESPONSECODE_PROPERTY = "RESTResponseCode";
   private static final String[] REQUIRED_REST_RESPONSE_PROPERTIES = new String[]{"RESTResponseCode"};

   public RestClient(String url, String transportUID, long timeout, boolean useWapGateway) {
      this._serverURL = url;
      this._httpClient = new HttpClient(transportUID, timeout, useWapGateway);
   }

   public final HttpClient getHttpClient() {
      return this._httpClient;
   }

   public final RestClient$GetBrandingInfoCallResult getBrandingInfo(String siteName) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName};
      String url = MessageFormat.format("{0}://{1}/brand/{2}", urlArgs);
      HttpResponse response = this._httpClient.doXmlExchange(url, "GET", null, null, REQUIRED_REST_RESPONSE_PROPERTIES, true);
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      if (restResponseCode != 200) {
         return new RestClient$GetBrandingInfoCallResult(restResponseCode, null);
      }

      BrandingInfoHandler brandingInfoHandler = new BrandingInfoHandler();
      if (response.getResponsePayload() == null) {
         throw new RESTException("Response missing XML body");
      }

      try {
         BrandingInfo loadedBrandingInfo = brandingInfoHandler.loadFromXML((InputStream)(new Object(response.getResponsePayload())));
         return new RestClient$GetBrandingInfoCallResult(restResponseCode, loadedBrandingInfo);
      } finally {
         throw new RESTException("Could not parse response XML");
      }
   }

   public final RestClient$RESTCallResult sendForgotPassword(String siteName, String userName) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/sfp", urlArgs);
      HttpResponse response = this.doStateUpdatingCall(url, new SimpleCall("sfp", Boolean.TRUE.toString()));
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      return new RestClient$RESTCallResult(restResponseCode);
   }

   public final RestClient$RESTCallResult sendForgotHostedPassword(String siteName, String userName, String answer) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/sfhp", urlArgs);
      HttpResponse response = this.doStateUpdatingCall(url, new SendForgotHostedPasswordCall(answer));
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      return new RestClient$RESTCallResult(restResponseCode);
   }

   public final RestClient$RESTCallResult updatePassword(String siteName, String userName, String oldPassword, String newPassword) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/password", urlArgs);
      HttpResponse response = this.doStateUpdatingCall(url, new UpdatePasswordCall(oldPassword, newPassword));
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      return new RestClient$RESTCallResult(restResponseCode);
   }

   public final RestClient$RESTCallResult sendServiceBooks(String siteName, String userName) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/sendSb", urlArgs);
      HttpResponse response = this.doStateUpdatingCall(url, new SimpleCall("sendSb", Boolean.TRUE.toString()));
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      return new RestClient$RESTCallResult(restResponseCode);
   }

   public final RestClient$RESTCallResult changeHandheld(String siteName, String userName, String newPin, String newImei) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/device", urlArgs);
      HttpResponse response = this.doStateUpdatingCall(url, new ChangeHandheldCall(newPin, newImei));
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      return new RestClient$RESTCallResult(restResponseCode);
   }

   public final RestClient$RESTCallResult changeLanguage(String siteName, String userName, String language, String country) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}", urlArgs);
      HttpResponse response = this.doStateUpdatingCall(url, new ChangeLanguageCall(language, country));
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      return new RestClient$RESTCallResult(restResponseCode);
   }

   public final RestClient$RESTCallResult acceptEndUserAgreement(String siteName, String userName, String tcversion, String tclocale) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/acceptTerms", urlArgs);
      HttpResponse response = this.doStateUpdatingCall(url, new AcceptEndUserAgreementCall(tcversion, tclocale));
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      return new RestClient$RESTCallResult(restResponseCode);
   }

   public final RestClient$SignupUserCallResult signupUser(
      String siteName, String pin, String imei, String userName, String password, String language, String tcVersion, String[] suggestions
   ) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName};
      String url = MessageFormat.format("{0}://{1}/{2}/user", urlArgs);
      HttpResponse response = this.doStateAddingCall(url, new SignupUserCall(pin, imei, language, userName, password, tcVersion));
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      UserInfoHandler userInfoHandler = new UserInfoHandler();
      if (response.getResponsePayload() == null) {
      }

      try {
         if (restResponseCode == 200) {
            UserInfo loadedUserInfo = userInfoHandler.loadFromXML((InputStream)(new Object(response.getResponsePayload())));
            return new RestClient$SignupUserCallResult(restResponseCode, loadedUserInfo);
         }

         if (restResponseCode == 10000) {
            SuggestionsHandler suggestionsHandler = new SuggestionsHandler();
            String[] suggestionsList = suggestionsHandler.loadFromXML((InputStream)(new Object(response.getResponsePayload())));
            suggestions[0] = suggestionsList[0];
            suggestions[1] = suggestionsList[1];
            suggestions[2] = suggestionsList[2];
         }

         return new RestClient$SignupUserCallResult(restResponseCode, null);
      } finally {
         throw new RESTException("Could not parse response XML");
      }
   }

   public final RestClient$LoginCallResult login(
      String siteName, String userName, String password, String pin, String imei, String acceptPinChange, boolean autoAuth
   ) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/session", urlArgs);
      HttpResponse response = this.doStateAddingCall(url, new LoginCall(password, pin, imei, acceptPinChange, autoAuth));
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      String currentPin = null;
      if (restResponseCode == 10006) {
         currentPin = (String)(new Object(response.getResponsePayload()));
      }

      return new RestClient$LoginCallResult(restResponseCode, currentPin);
   }

   public final RestClient$GetUserInfoCallResult getUserInfo(String siteName, String userName) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}", urlArgs);
      HttpResponse response = this._httpClient.doXmlExchange(url, "GET", null, null, REQUIRED_REST_RESPONSE_PROPERTIES, true);
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      UserInfoHandler userInfoHandler = new UserInfoHandler();
      if (response.getResponsePayload() == null) {
      }

      try {
         UserInfo loadedUserInfo = userInfoHandler.loadFromXML((InputStream)(new Object(response.getResponsePayload())));
         return new RestClient$GetUserInfoCallResult(restResponseCode, loadedUserInfo);
      } finally {
         throw new RESTException("Could not parse response XML");
      }
   }

   public final RestClient$AddMailboxCallResult addSimpleAccount(
      String siteName, String userName, String emailAddress, String password, Boolean aolIntegrationPermitted
   ) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/emailAccount", urlArgs);
      long restResponseCode = 0;
      HttpResponse response = this.doStateAddingCall(url, new AddSimpleAccountCall(emailAddress, password, aolIntegrationPermitted));
      this.checkResponseCode(response.getHttpResponseCode());
      restResponseCode = this.getAndCheckRESTResponseCode(response);
      if (restResponseCode != 200) {
         return new RestClient$AddMailboxCallResult(restResponseCode, null);
      }

      try {
         MailboxHandler mailboxHandler = new MailboxHandler();
         mailboxHandler.loadFromXML((InputStream)(new Object(response.getResponsePayload())));
         Mailbox loadedMailbox = (Mailbox)mailboxHandler.getResult();
         return new RestClient$AddMailboxCallResult(restResponseCode, loadedMailbox);
      } finally {
         throw new RESTException("Could not parse response XML");
      }
   }

   public final RestClient$AddMailboxCallResult addOWAAccount(
      String siteName, String userName, String email, String server, String description, String owaUserName, String owaPassword
   ) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/emailAccount", urlArgs);
      long restResponseCode = 0;
      HttpResponse response = this.doStateAddingCall(url, new AddOWAAccountCall(email, server, description, owaUserName, owaPassword));
      this.checkResponseCode(response.getHttpResponseCode());
      restResponseCode = this.getAndCheckRESTResponseCode(response);
      if (response.getHttpResponseCode() == 200) {
      }

      try {
         MailboxHandler mailboxHandler = new MailboxHandler();
         mailboxHandler.loadFromXML((InputStream)(new Object(response.getResponsePayload())));
         Mailbox loadedMailbox = (Mailbox)mailboxHandler.getResult();
         return new RestClient$AddMailboxCallResult(restResponseCode, loadedMailbox);
      } finally {
         throw new RESTException("Could not parse response XML");
      }
   }

   public final RestClient$AddMailboxCallResult addHostedAccount(
      String siteName,
      String userName,
      String email,
      String password,
      String secretQuestion,
      Integer secretQuestionId,
      String secretAnswer,
      String[] suggestions
   ) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/emailAccount", urlArgs);
      long restResponseCode = 0;
      HttpResponse response = this.doStateAddingCall(url, new AddHostedAccountCall(email, password, secretQuestion, secretQuestionId, secretAnswer));
      this.checkResponseCode(response.getHttpResponseCode());
      restResponseCode = this.getAndCheckRESTResponseCode(response);
      if (response.getHttpResponseCode() == 200) {
      }

      try {
         if (restResponseCode == 200) {
            MailboxHandler mailboxHandler = new MailboxHandler();
            mailboxHandler.loadFromXML((InputStream)(new Object(response.getResponsePayload())));
            Mailbox loadedMailbox = (Mailbox)mailboxHandler.getResult();
            return new RestClient$AddMailboxCallResult(restResponseCode, loadedMailbox);
         }

         if (restResponseCode == 10210) {
            SuggestionsHandler suggestionsHandler = new SuggestionsHandler();
            String[] suggestionsList = suggestionsHandler.loadFromXML((InputStream)(new Object(response.getResponsePayload())));
            suggestions[0] = suggestionsList[0];
            suggestions[1] = suggestionsList[1];
            suggestions[2] = suggestionsList[2];
         }

         return new RestClient$AddMailboxCallResult(restResponseCode, null);
      } finally {
         throw new RESTException("Could not parse response XML");
      }
   }

   public final RestClient$AddMailboxCallResult addISPAccount(
      String siteName, String userName, String email, String server, String ispUserName, String ispPassword
   ) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/emailAccount", urlArgs);
      long restResponseCode = 0;
      HttpResponse response = this.doStateAddingCall(url, new AddISPAccountCall(email, server, ispUserName, ispPassword));
      this.checkResponseCode(response.getHttpResponseCode());
      restResponseCode = this.getAndCheckRESTResponseCode(response);
      if (response.getHttpResponseCode() == 200) {
      }

      try {
         MailboxHandler mailboxHandler = new MailboxHandler();
         mailboxHandler.loadFromXML((InputStream)(new Object(response.getResponsePayload())));
         Mailbox loadedMailbox = (Mailbox)mailboxHandler.getResult();
         return new RestClient$AddMailboxCallResult(restResponseCode, loadedMailbox);
      } finally {
         throw new RESTException("Could not parse response XML");
      }
   }

   public final RestClient$RESTCallResult updateAccount(String siteName, String userName, Mailbox updatedMailbox, String oldPassword) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName, updatedMailbox.getSrcMboxID()};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/emailAccount/{4}", urlArgs);
      long restResponseCode = 0;
      HttpResponse response = this.doStateUpdatingCall(url, new UpdateAccountCall(updatedMailbox, oldPassword));
      this.checkResponseCode(response.getHttpResponseCode());
      restResponseCode = this.getAndCheckRESTResponseCode(response);
      return new RestClient$RESTCallResult(restResponseCode);
   }

   public final RestClient$RESTCallResult deleteAccount(String siteName, String userName, String srcMboxID) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName, srcMboxID};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/emailAccount/{4}", urlArgs);
      long restResponseCode = 0;
      HttpResponse response = this.doStateDeletingCall(url, null);
      this.checkResponseCode(response.getHttpResponseCode());
      restResponseCode = this.getAndCheckRESTResponseCode(response);
      return new RestClient$RESTCallResult(restResponseCode);
   }

   public final RestClient$AddFilterCallResult addFilter(
      String siteName,
      String userName,
      String srcMboxId,
      String filterName,
      Boolean sendAlert,
      Boolean levelOne,
      Boolean headersOnly,
      String filterOperator,
      String filterValue
   ) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName, srcMboxId};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/emailAccount/{4}/filter", urlArgs);
      HttpResponse response = this.doStateAddingCall(url, new AddFilterCall(filterName, sendAlert, levelOne, headersOnly, filterOperator, filterValue));
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      FilterHandler filterHandler = new FilterHandler();
      if (response.getResponsePayload() == null) {
      }

      try {
         Filter filter = filterHandler.loadFromXML((InputStream)(new Object(response.getResponsePayload())));
         return new RestClient$AddFilterCallResult(restResponseCode, filter);
      } finally {
         throw new RESTException("Could not parse response XML");
      }
   }

   public final RestClient$RESTCallResult updateFilter(
      String siteName,
      String userName,
      String srcMboxId,
      String filterId,
      String filterName,
      Boolean sendAlert,
      Boolean levelOne,
      Boolean headersOnly,
      String filterOperator,
      String filterValue
   ) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName, srcMboxId, filterId};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/emailAccount/{4}/filter/{5}", urlArgs);
      HttpResponse response = this.doStateUpdatingCall(url, new AddFilterCall(filterName, sendAlert, levelOne, headersOnly, filterOperator, filterValue));
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      return new RestClient$RESTCallResult(restResponseCode);
   }

   public final RestClient$RESTCallResult deleteFilter(String siteName, String userName, String srcMboxId, String filterId) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName, srcMboxId, filterId};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/emailAccount/{4}/filter/{5}", urlArgs);
      HttpResponse response = this.doStateDeletingCall(url, null);
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      return new RestClient$RESTCallResult(restResponseCode);
   }

   public final RestClient$GetFiltersCallResult getFilters(String siteName, String userName, String srcMboxId) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName, srcMboxId};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/emailAccount/{4}/filters", urlArgs);
      HttpResponse response = this._httpClient.doXmlExchange(url, "GET", null, null, REQUIRED_REST_RESPONSE_PROPERTIES, true);
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      FiltersHandler filtersHandler = new FiltersHandler();
      if (response.getResponsePayload() == null) {
      }

      try {
         Vector filters = filtersHandler.loadFromXML((InputStream)(new Object(response.getResponsePayload())));
         return new RestClient$GetFiltersCallResult(restResponseCode, filters);
      } finally {
         throw new RESTException("Could not parse response XML");
      }
   }

   public final RestClient$PreAuthenticationCallResult preAuthenticate(String siteName, String pin, String imei) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName};
      String url = MessageFormat.format("{0}://{1}/{2}/preAuth", urlArgs);
      DeviceIDs ids = DeviceIDs.getInstance();
      HttpResponse response = this.doStateUpdatingCall(url, new PreAuthenticationCall(pin, imei, ids.getIMSI(), ids.getICCID(), ids.getMSISDN()));
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      AuthInfoHandler authInfoHandler = new AuthInfoHandler();
      if (response.getResponsePayload() != null || restResponseCode != 10302 && restResponseCode != 10303 && restResponseCode != 10304) {
         try {
            AuthInfo loadedAuthInfo = null;
            if (restResponseCode == 10302 || restResponseCode == 10303 || restResponseCode == 10304) {
               loadedAuthInfo = authInfoHandler.loadFromXML((InputStream)(new Object(response.getResponsePayload())));
            }

            return new RestClient$PreAuthenticationCallResult(restResponseCode, loadedAuthInfo);
         } finally {
            throw new RESTException("Could not parse response XML");
         }
      } else {
         throw new RESTException("Response missing XML body");
      }
   }

   public final RestClient$ValidatePasswordsCallResult validatePasswords(String siteName, String userName, Hashtable mboxPasswords) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, userName};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/validatePasswords", urlArgs);
      DeviceIDs ids = DeviceIDs.getInstance();
      HttpResponse response = this.doStateUpdatingCall(url, new ValidatePasswordsCall(mboxPasswords));
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      FailedValidationsHandler failedHandler = new FailedValidationsHandler();
      if (response.getResponsePayload() == null) {
         throw new RESTException("Response missing XML body");
      }

      try {
         int[] failedMboxes = failedHandler.loadFromXML((InputStream)(new Object(response.getResponsePayload())));
         return new RestClient$ValidatePasswordsCallResult(restResponseCode, failedMboxes);
      } finally {
         throw new RESTException("Could not parse response XML");
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final RestClient$ChangeToUsernameCallResult changeToUsername(
      String siteName, String oldUsername, String userName, String password, String failedIntegrationType, String failedIntegrationEmail
   ) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, oldUsername};
      String url = MessageFormat.format("{0}://{1}/{2}/user/{3}/changeToUsername", urlArgs);
      HttpResponse response = this.doStateUpdatingCall(url, new ChangeToUsernameCall(userName, password, failedIntegrationType, failedIntegrationEmail));
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      String[] suggestions = null;
      if (restResponseCode == 10000 && response.getResponsePayload() == null) {
         throw new RESTException("Response missing XML body");
      }

      if (restResponseCode == 10000) {
         boolean var15 = false /* VF: Semaphore variable */;

         try {
            var15 = true;
            SuggestionsHandler e = new SuggestionsHandler();
            suggestions = e.loadFromXML((InputStream)(new Object(response.getResponsePayload())));
            var15 = false;
         } finally {
            if (var15) {
               throw new RESTException("Could not parse response XML");
            }
         }
      }

      return new RestClient$ChangeToUsernameCallResult(restResponseCode, suggestions);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final RestClient$GetSecretQuestionsCallResult getSecretQuestions(String siteName, String userName, String locale) {
      Object[] urlArgs = new Object[]{"https", this._serverURL, siteName, locale};
      String url = MessageFormat.format("{0}://{1}/{2}/secretQuestions/{3}", urlArgs);
      HttpResponse response = this._httpClient.doXmlExchange(url, "GET", null, null, REQUIRED_REST_RESPONSE_PROPERTIES, true);
      this.checkResponseCode(response.getHttpResponseCode());
      long restResponseCode = this.getAndCheckRESTResponseCode(response);
      SecretQuestion[] questions = null;
      if (response.getResponsePayload() == null) {
         throw new RESTException("Response missing XML body");
      }

      boolean var12 = false /* VF: Semaphore variable */;

      try {
         var12 = true;
         SecretQuestionsHandler e = new SecretQuestionsHandler();
         questions = e.loadFromXML((InputStream)(new Object(response.getResponsePayload())));
         var12 = false;
      } finally {
         if (var12) {
            throw new RESTException("Could not parse response XML");
         }
      }

      return new RestClient$GetSecretQuestionsCallResult(restResponseCode, questions);
   }

   private final void checkResponseCode(int responseCode) {
      if (HttpStatusUtils.isAuthenticationErrorStatus(responseCode)) {
         throw new UserUnauthorizedException(responseCode);
      }

      if (responseCode != 200) {
         throw new HttpCommException(responseCode);
      }
   }

   private final HttpResponse doStateUpdatingCall(String url, XMLCall xmlCall) {
      return this.doXMLCall(url, xmlCall, "PUT");
   }

   private final HttpResponse doStateDeletingCall(String url, XMLCall xmlCall) {
      return this.doXMLCall(url, xmlCall, "DELETE");
   }

   private final HttpResponse doStateAddingCall(String url, XMLCall xmlCall) {
      return this.doXMLCall(url, xmlCall, "POST");
   }

   private final HttpResponse doXMLCall(String url, XMLCall xmlCall, String requestMethod) {
      byte[] requestPayload = null;
      if (xmlCall != null) {
         ByteArrayOutputStream baos = (ByteArrayOutputStream)(new Object());
         xmlCall.serialize(baos);
         requestPayload = baos.toByteArray();
      }

      Hashtable requestProperties = (Hashtable)(new Object());
      requestProperties.put("RESTRequestMethod", requestMethod);
      return this._httpClient.doXmlExchange(url, "POST", requestProperties, requestPayload, REQUIRED_REST_RESPONSE_PROPERTIES, true);
   }

   private final long getAndCheckRESTResponseCode(HttpResponse response) {
      String restResponseCodeString = response.getResponseProperty("RESTResponseCode");
      if (restResponseCodeString == null) {
         throw new RESTException("HTTP response missing REST response code");
      }

      try {
         return Long.parseLong(restResponseCodeString);
      } finally {
         throw new RESTException(((StringBuffer)(new Object("REST response code invalid : "))).append(restResponseCodeString).toString());
      }
   }
}
