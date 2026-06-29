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

public final class EditSaveCommand implements DomainCommand {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      Mailbox mailboxToEdit = ClientSessionState.getInstance().getMailboxToModify();
      Mailbox tempMailbox = new Mailbox();
      tempMailbox.copy(mailboxToEdit);
      boolean dirty = this.updateMailbox(params, tempMailbox);
      if (!dirty) {
         return new DomainCommandResult("success", null, null);
      }

      String password = ArgUtils.getStringValue(params, "password");
      String oldPassword = ArgUtils.getStringValue(params, "oldPassword");
      if ((password == null || password.length() == 0) && ClientSessionState.getInstance().getUserInfo().isAutoAuth()) {
         return new DomainCommandResult("passwordValidation", null, null, params);
      }

      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");
      UserInfo userInfo = ClientSessionState.getInstance().getUserInfo();

      try {
         String bisUserName = userInfo.getUsername();
         RestClient$RESTCallResult callResult = restClient.updateAccount(configRecord.getBrandName(), bisUserName, tempMailbox, oldPassword);
         if (callResult.getRESTStatusCode() == 200) {
            userInfo.removeMailbox(mailboxToEdit);
            userInfo.addMailbox(tempMailbox);
            String message = this.getSuccessMessage(params);
            return new DomainCommandResult("success", null, message);
         }

         if (callResult.getRESTStatusCode() == 10202) {
            ForgotHostedPasswordCommand.handleAttempts(params, tempMailbox, restClient, configRecord);
            String errorMsg = MessageFormat.format(ApplicationResources.getString(228), new Object[]{mailboxToEdit.getDescription()});
            return new DomainCommandResult("validationFailed", errorMsg, null);
         }

         if (callResult.getRESTStatusCode() == 10005) {
            String errorMsg = MessageFormat.format(ApplicationResources.getString(228), new Object[]{mailboxToEdit.getDescription()});
            return new DomainCommandResult("hostedValidationFailed", errorMsg, null);
         }

         if (callResult.getRESTStatusCode() == 401) {
            return DomainCommand.SESSION_TIMEOUT_RESULT;
         }

         BISEventLogger.logEvent(((StringBuffer)(new Object("Save: Unhandled REST response code: "))).append(callResult.getRESTStatusCode()).toString(), 0);
         return new DomainCommandResult("failed", null, ApplicationResources.getString(192));
      } catch (Throwable var14) {
         BISEventLogger.logEvent(e.toString(), 0);
         return new DomainCommandResult("error", null, null);
      }
   }

   protected final boolean updateMailbox(Hashtable params, Mailbox tempMailbox) {
      boolean dirty = false;
      String password = ArgUtils.getStringValue(params, "password");
      String friendlyName = ArgUtils.getStringValue(params, "friendlyName");
      String replyTo = ArgUtils.getStringValue(params, "replyTo");
      String autoBCC = ArgUtils.getStringValue(params, "autoBCC");
      String autoForward = ArgUtils.getStringValue(params, "autoForward");
      String autoForwardAllString = ArgUtils.getStringValue(params, "autoForwardAll");
      String userName = ArgUtils.getStringValue(params, "userName");
      String server = ArgUtils.getStringValue(params, "server");
      String useSSLString = ArgUtils.getStringValue(params, "useSSL");
      String description = ArgUtils.getStringValue(params, "description");
      String deleteSyncString = ArgUtils.getStringValue(params, "deleteSync");
      String signature = ArgUtils.getStringValue(params, "signature");
      String defaultRule = ArgUtils.getStringValue(params, "defaultRuleSend");
      String secretQuestion = ArgUtils.getStringValue(params, "question");
      Integer secretQuestionId = (Integer)params.get("questionId");
      String secretAnswer = ArgUtils.getStringValue(params, "answer");
      String oldPassword = ArgUtils.getStringValue(params, "oldPassword");
      if (ArgValidationUtils.hasStringValueChanged(tempMailbox.getPassword(), password) && password.length() > 0) {
         tempMailbox.setPassword(password);
         dirty = true;
      }

      if (ArgValidationUtils.hasStringValueChanged(tempMailbox.getFriendlyName(), friendlyName)) {
         tempMailbox.setFriendlyName(friendlyName);
         dirty = true;
      }

      if (ArgValidationUtils.hasStringValueChanged(tempMailbox.getReplyTo(), replyTo)) {
         tempMailbox.setReplyTo(replyTo);
         dirty = true;
      }

      if (ArgValidationUtils.hasStringValueChanged(tempMailbox.getAutoBCC(), autoBCC)) {
         tempMailbox.setAutoBCC(autoBCC);
         dirty = true;
      }

      if (ArgValidationUtils.hasStringValueChanged(tempMailbox.getAutoForward(), autoForward)) {
         tempMailbox.setAutoForward(autoForward);
         dirty = true;
      }

      if (ArgValidationUtils.hasBooleanValueChanged(tempMailbox.getAutoForwardAll(), autoForwardAllString)) {
         tempMailbox.setAutoForwardAll("true".equals(autoForwardAllString));
         dirty = true;
      }

      if (ArgValidationUtils.hasStringValueChanged(tempMailbox.getUserName(), userName)) {
         tempMailbox.setUserName(userName);
         dirty = true;
      }

      if (ArgValidationUtils.hasStringValueChanged(tempMailbox.getServer(), server)) {
         tempMailbox.setServer(server);
         dirty = true;
      }

      if (ArgValidationUtils.hasBooleanValueChanged(tempMailbox.getUseSSL(), useSSLString)) {
         tempMailbox.setUseSSL("true".equals(useSSLString));
         dirty = true;
      }

      if (ArgValidationUtils.hasStringValueChanged(tempMailbox.getDescription(), description)) {
         tempMailbox.setDescription(description);
         dirty = true;
      }

      if (ArgValidationUtils.hasBooleanValueChanged(tempMailbox.getDeleteSync(), deleteSyncString)) {
         tempMailbox.setDeleteSync("true".equals(deleteSyncString));
         dirty = true;
      }

      if (ArgValidationUtils.hasStringValueChanged(tempMailbox.getSignature(), signature)) {
         tempMailbox.setSignature(signature);
         dirty = true;
      }

      if (defaultRule != null) {
         tempMailbox.setForwardMessages("true".equals(defaultRule));
         dirty = true;
      }

      if (ArgValidationUtils.hasStringValueChanged(tempMailbox.getSecretQuestion(), secretQuestion)) {
         tempMailbox.setSecretQuestion(secretQuestion);
         tempMailbox.setSecretQuestionId(null);
         dirty = true;
      }

      if (secretQuestionId != null && !secretQuestionId.equals(tempMailbox.getSecretQuestionId())) {
         tempMailbox.setSecretQuestionId(secretQuestionId);
         tempMailbox.setSecretQuestion(null);
         dirty = true;
      }

      if (ArgValidationUtils.hasStringValueChanged(tempMailbox.getSecretAnswer(), secretAnswer)) {
         tempMailbox.setSecretAnswer(secretAnswer);
         dirty = true;
      }

      return dirty;
   }

   protected final String getSuccessMessage(Hashtable params) {
      String mailbox = ClientSessionState.getInstance().getMailboxToModify().getDescription();
      if (params.containsKey("oldPassword")) {
         return MessageFormat.format(ApplicationResources.getString(260), new Object[]{mailbox});
      } else {
         return !params.containsKey("answer") || !params.containsKey("question") && !params.containsKey("questionId")
            ? ApplicationResources.getString(112)
            : MessageFormat.format(ApplicationResources.getString(261), new Object[]{mailbox});
      }
   }
}
