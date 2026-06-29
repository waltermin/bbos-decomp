package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.data.SecretQuestion;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.internal.ui.container.FrameLayout;

public final class HostedSecretQuestionScreen extends UserSettingsScreen {
   private ObjectChoiceField _secretQuestions;
   private BasicEditField _customQuestion;
   private FrameLayout _customQuestionFrame;
   private BoldLabelField _customLabel;
   private BasicEditField _secretAnswer;
   private int _customEditPosition = -1;
   private int _customLabelPosition = -1;
   public static final String PARAM_QUESTION = "question";
   public static final String PARAM_QUESTION_ID = "questionId";
   public static final String PARAM_ANSWER = "answer";
   public static final String PARAM_NO_CANCEL = "secretQuestionNoCancel";

   public HostedSecretQuestionScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      Mailbox mailboxToEdit = ClientSessionState.getInstance().getMailboxToModify();
      boolean create = false;
      if (!mailboxToEdit.hasSecretQuestion()) {
         create = true;
      }

      if (create) {
         this.setMenuOptions(0);
      } else {
         this.setMenuOptions(31);
      }

      String titleText = null;
      if (create) {
         titleText = MessageFormat.format(ApplicationResources.getString(265), new String[]{mailboxToEdit.getDescription()});
      } else {
         titleText = MessageFormat.format(ApplicationResources.getString(257), new String[]{mailboxToEdit.getDescription()});
      }

      this.setTitle(titleText);
      this.addContentField(new BoldLabelField(ApplicationResources.getString(250)));
      this._secretQuestions = new ObjectChoiceField(null, ClientSessionState.getInstance().getSecretQuestions());
      this.addContentField(this._secretQuestions, true);
      this._secretQuestions.setChangeListener(new HostedSecretQuestionScreen$CustomSecretQuestionFieldListener(this, null));
      this.addContentField(new LabelField(ApplicationResources.getString(253)));
      this.addContentLineBreak();
      this._customLabel = new BoldLabelField(ApplicationResources.getString(251));
      this.addContentField(this._customLabel);
      this._customQuestion = new BasicEditField(null, null);
      this._customQuestionFrame = new FrameLayout();
      this._customQuestionFrame.add(this._customQuestion);
      this._customQuestionFrame.setBorder(0, 4, 0, 4);
      this._customQuestionFrame.setMargin(0, 0, 10, 0);
      this.addContentField(this._customQuestionFrame);
      boolean custom = setupSecretQuestionAndAnswer(this._secretQuestions, this._customQuestion);
      if (!custom) {
         this.hideCustomQuestion();
      }

      this.addContentField(new BoldLabelField(ApplicationResources.getString(252)));
      this._secretAnswer = new BasicEditField(null, null);
      this.addContentField(this._secretAnswer, true);
      String[] nextParams = null;
      if (!create) {
         nextParams = new String[]{"question", "questionId", "answer"};
      } else {
         nextParams = new String[]{"question", "questionId", "answer", "secretQuestionNoCancel"};
      }

      Button cancelButton = new Button(ApplicationResources.getString(28));
      Button saveButton = new Button(ApplicationResources.getString(29));
      if (!create) {
         this.addButtonBarButtons(new Button[]{cancelButton, saveButton}, false, 1);
         this.attachEventToField(cancelButton, new BackEvent(28));
      } else {
         this.addButtonBarButtons(new Button[]{saveButton}, false, 0);
      }

      CommandEvent saveEvent = new CommandEvent(29, 8, nextParams);
      this.attachEventToField(saveButton, saveEvent);
      this.setDefaultEvent(saveEvent);
      this.setHelp("231294.wml");
   }

   public static final boolean setupSecretQuestionAndAnswer(ObjectChoiceField questionsField, BasicEditField customField) {
      Mailbox mailboxToEdit = ClientSessionState.getInstance().getMailboxToModify();
      SecretQuestion[] questions = ClientSessionState.getInstance().getSecretQuestions();
      boolean custom = false;
      if (questions.length < 1) {
         return false;
      } else if (mailboxToEdit == null) {
         questionsField.setSelectedIndex(0);
         return false;
      } else {
         questionsField.setSelectedIndex(0);
         return false;
      }
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      Mailbox mailboxToEdit = ClientSessionState.getInstance().getMailboxToModify();
      String answer = this._secretAnswer.getText();
      if (answer != null && answer.length() != 0) {
         inputMap.put("answer", answer);
         SecretQuestion question = (SecretQuestion)this._secretQuestions.getChoice(this._secretQuestions.getSelectedIndex());
         if (question.isCustom()) {
            String customQuestion = this._customQuestion.getText();
            if (customQuestion == null || customQuestion.length() == 0) {
               this.setError(ApplicationResources.getString(256));
               return false;
            }

            inputMap.put("question", customQuestion);
         } else {
            inputMap.put("questionId", new Integer(question.getId()));
         }

         if (!mailboxToEdit.hasSecretQuestion()) {
            inputMap.put("secretQuestionNoCancel", new Boolean(true));
         }

         return true;
      } else {
         this.setError(ApplicationResources.getString(254));
         return false;
      }
   }

   protected final void showCustomQuestion() {
      if (this._customLabelPosition > -1) {
         this.insertAt(this._customLabel, this._customLabelPosition);
         this.insertAt(this._customQuestionFrame, this._customEditPosition);
         this._customLabelPosition = -1;
         this._customEditPosition = -1;
      }
   }

   protected final void hideCustomQuestion() {
      if (this._customLabel.getIndex() > -1) {
         this._customEditPosition = this.deleteField(this._customQuestionFrame);
         this._customLabelPosition = this.deleteField(this._customLabel);
      }
   }
}
