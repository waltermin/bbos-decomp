package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.InputHintLabelField;
import net.rim.device.apps.internal.bis.api.ui.RefreshableScreen;
import net.rim.device.apps.internal.bis.data.SecretQuestion;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.InputValidationUtils;
import net.rim.device.internal.ui.container.FrameLayout;

public final class NewBBEmailAddressScreen extends UserSettingsScreen {
   private LabelField _userNameTakenLabel;
   private RadioButtonField[] _suggestionChoices;
   private RadioButtonField _customUserNameChoice;
   private RadioButtonGroup _suggestionRadioGroup;
   private BasicEditField _userNameEdit;
   private PasswordEditField _passwordEdit;
   private PasswordEditField _passwordConfirmEdit;
   private ObjectChoiceField _secretQuestions;
   private BasicEditField _customQuestion;
   private FrameLayout _customQuestionFrame;
   private BoldLabelField _customLabel;
   private BasicEditField _secretAnswer;
   private int _customEditPosition = -1;
   private int _customLabelPosition = -1;
   public static final String PARAM_SUGGESTIONS = "suggestions";
   public static final String PARAM_USERNAME = "userName";
   public static final String PARAM_PASSWORD = "password";
   public static final String PARAM_QUESTION = "question";
   public static final String PARAM_QUESTION_ID = "questionId";
   public static final String PARAM_ANSWER = "answer";

   public NewBBEmailAddressScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this._customEditPosition = -1;
      this._customLabelPosition = -1;
      this.setTitle(ApplicationResources.getString(11));
      String mailDomain = ClientSessionState.getInstance().getBrandingInfo().getHostedMailDomain();
      if (screenParams != null && screenParams.containsKey("suggestions")) {
         this._userNameEdit = (BasicEditField)(new Object(null, null));
         this._userNameEdit.setChangeListener(new NewBBEmailAddressScreen$CustomUsernameEditFieldListener(this, null));
         this._userNameTakenLabel = (LabelField)(new Object());
         this.addContentField(this._userNameTakenLabel);
         String userName = (String)screenParams.get("userName");
         String userNameTakenText = MessageFormat.format(ApplicationResources.getString(36), new Object[]{userName});
         this._userNameTakenLabel.setText(userNameTakenText);
         this._suggestionRadioGroup = (RadioButtonGroup)(new Object());
         this._suggestionChoices = new Object[3];
         this._suggestionChoices[0] = (RadioButtonField)(new Object(null, this._suggestionRadioGroup, true));
         this._suggestionChoices[1] = (RadioButtonField)(new Object(null, this._suggestionRadioGroup, false));
         this._suggestionChoices[2] = (RadioButtonField)(new Object(null, this._suggestionRadioGroup, false));
         this._customUserNameChoice = (RadioButtonField)(new Object(ApplicationResources.getString(65), this._suggestionRadioGroup, false));
         this.addContentField(this._suggestionChoices[0]);
         this.addContentField(this._suggestionChoices[1]);
         this.addContentField(this._suggestionChoices[2]);
         this.addContentField(this._customUserNameChoice);
         this.addContentField(this._userNameEdit, true);
         this.addContentField(new BoldLabelField(((StringBuffer)(new Object("@"))).append(mailDomain).toString()));
         String[] suggestions = (Object[])screenParams.get("suggestions");
         this._suggestionChoices[0].setLabel(suggestions[0]);
         this._suggestionChoices[1].setLabel(suggestions[1]);
         this._suggestionChoices[2].setLabel(suggestions[2]);
      } else {
         this.addContentField((Field)(new Object(ApplicationResources.getString(86))));
         this.addContentLineBreak();
         this.addContentField(new BoldLabelField(ApplicationResources.getString(13)));
         if (ClientSessionState.getInstance().getBrandingInfo().isForceUserIDEqualHostedAddress()) {
            String hostedAddress = ClientSessionState.getInstance().getUserInfo().getUsername();
            this.addContentField((Field)(new Object(hostedAddress)));
         } else {
            this._userNameEdit = (BasicEditField)(new Object(null, null));
            this.addContentField(this._userNameEdit, true);
         }

         this.addContentField(new BoldLabelField(((StringBuffer)(new Object("@"))).append(mailDomain).toString()));
      }

      if (ClientSessionState.getInstance().isAutoAuth()) {
         this.addContentLineBreak();
         this._passwordEdit = (PasswordEditField)(new Object(null, null));
         this._passwordConfirmEdit = (PasswordEditField)(new Object(null, null));
         this.addContentField(new BoldLabelField(ApplicationResources.getString(14)));
         this.addContentField(this._passwordEdit, true);
         this.addContentField(new InputHintLabelField(ApplicationResources.getString(21)));
         this.addContentLineBreak();
         this.addContentField(new BoldLabelField(ApplicationResources.getString(19)));
         this.addContentField(this._passwordConfirmEdit, true);
         this.addContentLineBreak();
         this.addContentField(new BoldLabelField(ApplicationResources.getString(250)));
         this._secretQuestions = (ObjectChoiceField)(new Object(null, ClientSessionState.getInstance().getSecretQuestions()));
         this.addContentField(this._secretQuestions, true);
         this._secretQuestions.setChangeListener(new NewBBEmailAddressScreen$CustomSecretQuestionFieldListener(this, null));
         this.addContentField((Field)(new Object(ApplicationResources.getString(253))));
         this.addContentLineBreak();
         this._customLabel = new BoldLabelField(ApplicationResources.getString(251));
         this.addContentField(this._customLabel);
         this._customQuestion = (BasicEditField)(new Object(null, null));
         this._customQuestionFrame = (FrameLayout)(new Object());
         this._customQuestionFrame.add(this._customQuestion);
         this._customQuestionFrame.setBorder(0, 4, 0, 4);
         this._customQuestionFrame.setMargin(0, 0, 10, 0);
         this.addContentField(this._customQuestionFrame);
         boolean custom = HostedSecretQuestionScreen.setupSecretQuestionAndAnswer(this._secretQuestions, this._customQuestion);
         if (!custom) {
            this.hideCustomQuestion();
         }

         this.addContentField(new BoldLabelField(ApplicationResources.getString(252)));
         this._secretAnswer = (BasicEditField)(new Object(null, null));
         this.addContentField(this._secretAnswer, true);
      }

      String[] nextParams = null;
      if (ClientSessionState.getInstance().isAutoAuth()) {
         nextParams = new String[]{"userName", "password", "question", "questionId", "answer"};
      } else {
         nextParams = new String[]{"userName"};
      }

      boolean showBack = true;
      if (screenParams != null) {
         RefreshableScreen prevScreen = (RefreshableScreen)screenParams.get("previousScreen");
         if (prevScreen != null && !prevScreen.canComeBack()) {
            showBack = false;
         }
      }

      Button closeButton = new Button(ApplicationResources.getString(15));
      Button backButton = null;
      if (showBack) {
         backButton = new Button(ApplicationResources.getString(16));
      }

      Button nextButton = new Button(ApplicationResources.getString(17));
      if (showBack) {
         this.addButtonBarButtons(new Button[]{closeButton, backButton, nextButton}, false, 2);
         this.attachEventToField(backButton, new BackEvent());
      } else {
         this.addButtonBarButtons(new Button[]{closeButton, nextButton}, false, 1);
      }

      CommandEvent nextEvent = new CommandEvent(17, 3, nextParams);
      this.attachEventToField(nextButton, nextEvent);
      this.setDefaultEvent(nextEvent);
      this.attachEventToField(closeButton, new CloseEvent());
      this.setHelp("221685.wml");
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      String hostedAddress = null;
      if (ClientSessionState.getInstance().getBrandingInfo().isForceUserIDEqualHostedAddress()) {
         hostedAddress = ClientSessionState.getInstance().getUserInfo().getUsername();
      } else if (this._customUserNameChoice != null && !this._customUserNameChoice.isSelected()) {
         hostedAddress = this._suggestionChoices[this._suggestionRadioGroup.getSelectedIndex()].getLabel();
      } else {
         hostedAddress = this._userNameEdit.getText();
         if (!InputValidationUtils.isValidUsername(hostedAddress)) {
            if (InputValidationUtils.hasTooFewUsernameCharacters(hostedAddress)) {
               this.setError(ApplicationResources.getString(102));
               return false;
            }

            if (InputValidationUtils.hasTooManyUsernameCharacters(hostedAddress)) {
               this.setError(ApplicationResources.getString(101));
               return false;
            }

            this.setError(ApplicationResources.getString(103));
            return false;
         }

         hostedAddress = ((StringBuffer)(new Object()))
            .append(hostedAddress)
            .append("@")
            .append(ClientSessionState.getInstance().getBrandingInfo().getHostedMailDomain())
            .toString();
      }

      inputMap.put("userName", hostedAddress);
      if (ClientSessionState.getInstance().getUserInfo().isAutoAuth()) {
         String password = this._passwordEdit.getText();
         if (!InputValidationUtils.validatePassword(this, password)) {
            return false;
         }

         String passwordConfirm = this._passwordConfirmEdit.getText();
         if (!password.equals(passwordConfirm)) {
            this.setError(ApplicationResources.getString(108));
            return false;
         }

         inputMap.put("password", password);
         String answer = this._secretAnswer.getText();
         if (answer == null || answer.length() == 0) {
            this.setError(ApplicationResources.getString(254));
            return false;
         }

         inputMap.put("answer", answer);
         SecretQuestion question = (SecretQuestion)this._secretQuestions.getChoice(this._secretQuestions.getSelectedIndex());
         if (question.isCustom()) {
            String customQuestion = this._customQuestion.getText();
            if (customQuestion != null && customQuestion.length() != 0) {
               inputMap.put("question", customQuestion);
               return true;
            }

            this.setError(ApplicationResources.getString(256));
            return false;
         }

         inputMap.put("questionId", new Object(question.getId()));
      }

      return true;
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
      if (this._customLabelPosition == -1) {
         this._customEditPosition = this.deleteField(this._customQuestionFrame);
         this._customLabelPosition = this.deleteField(this._customLabel);
      }
   }
}
