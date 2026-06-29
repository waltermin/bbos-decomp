package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.data.SecretQuestion;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class ForgotHostedPasswordScreen extends UserSettingsScreen {
   private BasicEditField _answerEdit;
   private Hashtable _pendingParams;
   public static final String PARAM_ANSWER = "answer";
   public static final String PARAM_ATTEMPT = "validationAttempt";

   public ForgotHostedPasswordScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this._pendingParams = screenParams;
      this.setTitle(ApplicationResources.getString(8));
      this.addContentField(new LabelField(ApplicationResources.getString(245)));
      this.addContentLineBreak();
      Mailbox mailbox = null;
      if (screenParams.containsKey("mailboxesToValidate")) {
         Vector mailboxes = (Vector)screenParams.get("mailboxesToValidate");
         mailbox = (Mailbox)mailboxes.firstElement();
      } else {
         mailbox = ClientSessionState.getInstance().getMailboxToModify();
      }

      String secretQuestion = null;
      if (mailbox.getSecretQuestion() != null && mailbox.getSecretQuestion().length() > 0) {
         secretQuestion = mailbox.getSecretQuestion();
      } else {
         SecretQuestion[] questions = ClientSessionState.getInstance().getSecretQuestions();

         for (int i = 0; i < questions.length; i++) {
            if (new Integer(questions[i].getId()).equals(mailbox.getSecretQuestionId())) {
               secretQuestion = questions[i].getText();
               break;
            }
         }
      }

      this.addContentField(new LabelField(secretQuestion));
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(246)));
      this._answerEdit = new BasicEditField(null, null);
      this.addContentField(this._answerEdit, true);
      Button cancelButton = new Button(ApplicationResources.getString(28));
      Button okButton = new Button(ApplicationResources.getString(39));
      this.addButtonBarButtons(new Button[]{cancelButton, okButton}, false, 1);
      String[] okParams = new String[]{"answer", "validationAttempt"};
      if (screenParams.containsKey("mailboxesToValidate")) {
         okParams = new String[]{"answer", "validationAttempt", "mailboxesToValidate"};
      }

      CommandEvent okEvent = new CommandEvent(39, 34, okParams);
      this.attachEventToField(okButton, okEvent);
      this.setDefaultEvent(okEvent);
      this.attachEventToField(cancelButton, new BackEvent());
      this.setHelp("231291.wml");
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      if (this._pendingParams != null && this._pendingParams.containsKey("validationAttempt")) {
         inputMap.put("validationAttempt", this._pendingParams.get("validationAttempt"));
      }

      if (this._pendingParams != null && this._pendingParams.containsKey("mailboxesToValidate")) {
         inputMap.put("mailboxesToValidate", this._pendingParams.get("mailboxesToValidate"));
      }

      String answer = this._answerEdit.getText();
      if (answer != null && answer.length() != 0) {
         inputMap.put("answer", answer);
         return true;
      } else {
         this.setError(ApplicationResources.getString(249));
         return false;
      }
   }
}
