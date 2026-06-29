package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.ClientPersistentState;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.data.BrandingInfo;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.data.SecretQuestion;
import net.rim.device.apps.internal.bis.data.UserInfo;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.protocol.RestClient$GetSecretQuestionsCallResult;
import net.rim.device.apps.internal.bis.protocol.RestClient$GetUserInfoCallResult;
import net.rim.device.apps.internal.bis.protocol.RestClient$LoginCallResult;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;
import net.rim.device.apps.internal.bis.utils.system.DeviceIDs;

public class LoginCommand implements DomainCommand {
   @Override
   public DomainCommandResult run(Hashtable params) {
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      String brandName = configRecord.getBrandName();
      String userName = (String)params.get("userName");
      String password = (String)params.get("password");
      boolean rememberCredentials = "true".equals(params.get("rememberCredentials"));
      return login(brandName, userName, password, rememberCredentials, false);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static DomainCommandResult login(String brandName, String userName, String password, boolean rememberCredentials, boolean isAutoAuth) {
      String successResult = "success";
      String failedResult = "failed";
      if ("tmobile".equalsIgnoreCase(brandName)) {
         successResult = "tmobileSSOSuccess";
         failedResult = "tmobileSSOFailed";
      } else if (isAutoAuth) {
         successResult = "autoAuthSuccess";
         failedResult = "autoAuthFailed";
      }

      DomainCommandResult result = new DomainCommandResult(failedResult, ApplicationResources.getString(96), null);
      boolean createSession = false;

      try {
         RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");
         RestClient$LoginCallResult loginCallResult = restClient.login(
            brandName, userName, password, DeviceIDs.getInstance().getPIN(), DeviceIDs.getInstance().getIMEIESN(), null, isAutoAuth
         );
         if (loginCallResult.getRESTStatusCode() == 200) {
            result = new DomainCommandResult(successResult, null, null);
            createSession = true;
         } else if (loginCallResult.getRESTStatusCode() == 10006) {
            result = new DomainCommandResult("pinChangeDetected", null, null);
            ClientSessionState.getInstance().setTempPassword(password);
            ClientSessionState.getInstance().setTempUsername(userName);
            ClientSessionState.getInstance().setTempRememberCredentials(rememberCredentials);
            ClientSessionState.getInstance().setTempCurrentPin(loginCallResult.getCurrentPin());
            createSession = false;
         } else if (loginCallResult.getRESTStatusCode() == 10009) {
            result = new DomainCommandResult("termsNotAccepted", null, null);
            createSession = true;
         } else if (loginCallResult.getRESTStatusCode() == 10007) {
            result = new DomainCommandResult(failedResult, ApplicationResources.getString(218), null);
         } else if (!"tmobile".equalsIgnoreCase(brandName)) {
            BISEventLogger.logEvent(
               ((StringBuffer)(new Object("Login: Unhandled REST response code: "))).append(loginCallResult.getRESTStatusCode()).toString(), 0
            );
         } else if (loginCallResult.getRESTStatusCode() == 10003) {
            result = new DomainCommandResult("tmobileAccountSuspended", ApplicationResources.getString(221), null);
            createSession = false;
         } else if (loginCallResult.getRESTStatusCode() == 10105) {
            result = new DomainCommandResult("tmobileDeviceSuspended", ApplicationResources.getString(222), null);
            createSession = false;
         } else if (loginCallResult.getRESTStatusCode() == 10100) {
            result = new DomainCommandResult("tmobileDeviceInUse", ApplicationResources.getString(109), null);
            createSession = false;
         } else {
            BISEventLogger.logEvent(
               ((StringBuffer)(new Object("TMobile Login: Unhandled REST response code: "))).append(loginCallResult.getRESTStatusCode()).toString(), 0
            );
            result = new DomainCommandResult("error", null, null);
         }

         if (createSession) {
            if (!createSession(brandName, userName, password, rememberCredentials)) {
               result = new DomainCommandResult(failedResult, ApplicationResources.getString(96), null);
            } else if (successResult.equals(result.getResultName()) && isAutoAuth) {
               String alternateResultId = getAutoAuthSuccessTarget(result.getResultName());
               if (alternateResultId != null) {
                  result = new DomainCommandResult(alternateResultId, null, null);
               }
            }

            if (ClientSessionState.getInstance().getSecretQuestions() == null) {
               SecretQuestion[] secretQuestions = getSecretQuestions();
               if (secretQuestions == null) {
                  BISEventLogger.logEvent("Unable to get secret questions", 0);
                  result = new DomainCommandResult("error", null, null);
               } else {
                  ClientSessionState.getInstance().setSecretQuestions(secretQuestions);
               }
            }
         }
      } catch (Throwable var13) {
         BISEventLogger.logEvent(e.toString(), 0);
         result = new DomainCommandResult("error", null, null);
         return result;
      }

      return result;
   }

   protected static String getAutoAuthSuccessTarget(String originalTarget) {
      UserInfo user = ClientSessionState.getInstance().getUserInfo();
      BrandingInfo brand = ClientSessionState.getInstance().getBrandingInfo();
      if (user.hasHostedMailbox()) {
         Mailbox hosted = user.getHostedMailbox();
         if (!hosted.hasSecretQuestion()) {
            ClientSessionState.getInstance().setMailboxToModify(hosted);
            return "autoAuthHostedSecretQuestion";
         }
      }

      return user.isBBMail() && !user.hasHostedMailbox() ? "autoAuthBBMailCreateHosted" : null;
   }

   protected static boolean createSession(String brandName, String userName, String password, boolean rememberCredentials) {
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");
      RestClient$GetUserInfoCallResult userInfoCallResult = restClient.getUserInfo(brandName, userName);
      if (userInfoCallResult.getRESTStatusCode() == 200) {
         UserInfo userInfo = userInfoCallResult.getUserInfo();
         ClientSessionState.getInstance().setUserInfo(userInfo);
         if (userInfo.getLocale() != null) {
            ClientPersistentState.getInstance().setLocale(userInfo.getLocale());
         }

         if (rememberCredentials) {
            ClientPersistentState.getInstance().setUserName(userName);
            ClientPersistentState.getInstance().setPassword(password);
            return true;
         } else {
            ClientPersistentState.getInstance().clearCredentials();
            return true;
         }
      } else {
         BISEventLogger.logEvent(
            ((StringBuffer)(new Object("Create Session (Login): Unhandled REST response code: "))).append(userInfoCallResult.getRESTStatusCode()).toString(), 0
         );
         return false;
      }
   }

   public static SecretQuestion[] getSecretQuestions() {
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      ClientSessionState session = ClientSessionState.getInstance();
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");
      String userName = session.getUserInfo().getUsername();
      RestClient$GetSecretQuestionsCallResult questionsCallResult = restClient.getSecretQuestions(
         configRecord.getBrandName(), userName, session.getUserInfo().getLocale().toString()
      );
      if (questionsCallResult.getRESTStatusCode() == 200) {
         SecretQuestion[] questions = questionsCallResult.getSecretQuestions();
         SecretQuestion[] withCustom = new SecretQuestion[questions.length + 1];
         System.arraycopy(questions, 0, withCustom, 0, questions.length);
         withCustom[questions.length] = new SecretQuestion(-1, ApplicationResources.getString(255));
         return withCustom;
      } else {
         BISEventLogger.logEvent(
            ((StringBuffer)(new Object("Get Secret Questions: Unhandled REST response code: "))).append(questionsCallResult.getRESTStatusCode()).toString(), 0
         );
         return null;
      }
   }
}
