package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.EmailAddressEditField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.HeadingField;
import net.rim.device.apps.internal.bis.api.ui.LinkField;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.data.UserInfo;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.InputValidationUtils;

public final class HostedAccountEditScreen extends UserSettingsScreen {
   private EmailAddressEditField _accountNameEdit;
   private BasicEditField _yourNameEdit;
   private BasicEditField _signatureEdit;
   private EmailAddressEditField _emailAddressEdit;
   private EmailAddressEditField _autoBCCEdit;
   private EmailAddressEditField _autoForwardEdit;
   private RadioButtonGroup _autoForwardRadioGroup;
   private RadioButtonField _allMessagesField;
   private RadioButtonField _onlyMessagesField;
   private static final String PARAM_DESCRIPTION;
   private static final String PARAM_REPLYTO;
   private static final String PARAM_FRIENDLY_NAME;
   private static final String PARAM_SIGNATURE;
   private static final String PARAM_AUTOBCC;
   private static final String PARAM_AUTOFORWARD;
   private static final String PARAM_AUTOFORWARDALL;

   public HostedAccountEditScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      Mailbox mailboxToEdit = ClientSessionState.getInstance().getMailboxToModify();
      UserInfo userInfo = ClientSessionState.getInstance().getUserInfo();
      String title = MessageFormat.format(ApplicationResources.getString(150), new Object[]{mailboxToEdit.getDescription()});
      this.setTitle(title);
      this.addContentField(new HeadingField(ApplicationResources.getString(142)));
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(135)));
      this._accountNameEdit = (EmailAddressEditField)(new Object(null, mailboxToEdit.getDescription()));
      this.addContentField(this._accountNameEdit, true);
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(136)));
      this.addContentField((Field)(new Object(mailboxToEdit.getEmail())));
      if (userInfo.isChangeReplyToEnabled()) {
         this.addContentLineBreak();
         this.addContentField(new BoldLabelField(ApplicationResources.getString(139)));
         this._emailAddressEdit = (EmailAddressEditField)(new Object(null, mailboxToEdit.getReplyTo()));
         this.addContentField(this._emailAddressEdit, true);
      }

      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(145)));
      this._yourNameEdit = (BasicEditField)(new Object(null, mailboxToEdit.getFriendlyName()));
      this.addContentField(this._yourNameEdit, true);
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(143)));
      this._signatureEdit = (BasicEditField)(new Object(null, mailboxToEdit.getSignature()));
      this.addContentField(this._signatureEdit, true);
      if (userInfo.isAutoBCCEnabled()) {
         this.addContentLineBreak();
         this.addContentField(new BoldLabelField(ApplicationResources.getString(132)));
         this._autoBCCEdit = (EmailAddressEditField)(new Object(null, mailboxToEdit.getAutoBCC()));
         this.addContentField(this._autoBCCEdit, true);
      }

      if (userInfo.isAutoForwardEnabled()) {
         this.addContentLineBreak();
         this.addContentField(new BoldLabelField(ApplicationResources.getString(133)));
         this._autoForwardRadioGroup = (RadioButtonGroup)(new Object());
         boolean autoForwardAll = mailboxToEdit.getAutoForwardAll();
         this._allMessagesField = (RadioButtonField)(new Object(ApplicationResources.getString(130), this._autoForwardRadioGroup, autoForwardAll));
         this._onlyMessagesField = (RadioButtonField)(new Object(ApplicationResources.getString(138), this._autoForwardRadioGroup, !autoForwardAll));
         this.addContentField(this._allMessagesField);
         this.addContentField(this._onlyMessagesField);
         this._autoForwardEdit = (EmailAddressEditField)(new Object(null, mailboxToEdit.getAutoForward()));
         this.addContentField(this._autoForwardEdit, true);
      }

      if (userInfo.isAutoAuth()) {
         this.addContentLineBreak();
         String changePassword = MessageFormat.format(ApplicationResources.getString(269), new Object[]{mailboxToEdit.getDescription()});
         LinkField changePasswordLink = new LinkField(changePassword);
         this.addContentField(changePasswordLink);
         this.attachEventToField(changePasswordLink, new LinkEvent(258, 52));
         LinkField changeSecretQuestionLink = new LinkField(ApplicationResources.getString(259));
         this.addContentField(changeSecretQuestionLink);
         this.attachEventToField(changeSecretQuestionLink, new LinkEvent(259, 51));
      }

      Button cancel = new Button(ApplicationResources.getString(28));
      Button save = new Button(ApplicationResources.getString(29));
      this.addContentLineBreak();
      this.addButtonBarButtons(new Button[]{cancel, save}, false, 1);
      this.attachEventToField(cancel, new BackEvent(28));
      CommandEvent saveEvent = new CommandEvent(
         29, 8, new String[]{"description", "replyTo", "friendlyName", "signature", "autoBCC", "autoForward", "autoForwardAll"}
      );
      this.attachEventToField(save, saveEvent);
      this.setDefaultEvent(saveEvent);
      this.setHelp("101111.wml");
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      UserInfo userInfo = ClientSessionState.getInstance().getUserInfo();
      if (userInfo.isChangeReplyToEnabled()) {
         String replyToAddress = this._emailAddressEdit.getText();
         boolean replyToIsValid = replyToAddress == null || replyToAddress.length() == 0 || InputValidationUtils.isValidEmailAddress(replyToAddress);
         if (!replyToIsValid) {
            this.setError(ApplicationResources.getString(188));
            return false;
         }

         inputMap.put("replyTo", replyToAddress);
      }

      inputMap.put("description", this._accountNameEdit.getText());
      inputMap.put("friendlyName", this._yourNameEdit.getText());
      inputMap.put("signature", this._signatureEdit.getText());
      if (userInfo.isAutoBCCEnabled()) {
         String autoBCCAddress = this._autoBCCEdit.getText();
         boolean autoBCCIsValid = autoBCCAddress == null || autoBCCAddress.length() == 0 || InputValidationUtils.isValidEmailAddress(autoBCCAddress);
         if (!autoBCCIsValid) {
            this.setError(ApplicationResources.getString(190));
            return false;
         }

         inputMap.put("autoBCC", autoBCCAddress);
      }

      if (userInfo.isAutoForwardEnabled()) {
         String autoForwardAddress = this._autoForwardEdit.getText();
         boolean autoForwardIsValid = autoForwardAddress == null
            || autoForwardAddress.length() == 0
            || InputValidationUtils.isValidEmailAddress(autoForwardAddress);
         if (!autoForwardIsValid) {
            this.setError(ApplicationResources.getString(189));
            return false;
         }

         inputMap.put("autoForward", autoForwardAddress);
         inputMap.put("autoForwardAll", this._allMessagesField.isSelected() ? "true" : "false");
      }

      return true;
   }
}
