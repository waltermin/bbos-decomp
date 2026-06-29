package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.EmailAddressEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.HeadingField;
import net.rim.device.apps.internal.bis.api.ui.NotificationMenuItem;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.data.UserInfo;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.InputValidationUtils;
import net.rim.device.apps.internal.bis.utils.system.DeviceUtils;

public final class GeneralSettingsEditScreen extends UserSettingsScreen {
   private EmailAddressEditField _accountNameEdit;
   private BasicEditField _yourNameEdit;
   private BasicEditField _signatureEdit;
   private EmailAddressEditField _emailAddressEdit;
   private PasswordEditField _passwordEdit;
   private EmailAddressEditField _autoBCCEdit;
   private CheckboxField _synchronizeDeletesCheckbox;
   private static final String PARAM_DESCRIPTION = "description";
   private static final String PARAM_REPLYTO = "replyTo";
   private static final String PARAM_FRIENDLYNAME = "friendlyName";
   private static final String PARAM_PASSWORD = "password";
   private static final String PARAM_SIGNATURE = "signature";
   private static final String PARAM_AUTOBCC = "autoBCC";
   private static final String PARAM_DELETESYNC = "deleteSync";

   public GeneralSettingsEditScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      Mailbox mailboxToEdit = ClientSessionState.getInstance().getMailboxToModify();
      UserInfo userInfo = ClientSessionState.getInstance().getUserInfo();
      String title = MessageFormat.format(ApplicationResources.getString(150), new Object[]{mailboxToEdit.getDescription()});
      this.setTitle(title);
      this.addContentField(new HeadingField(ApplicationResources.getString(170)));
      this.addContentLineBreak();
      boolean isOwa = mailboxToEdit.getMailboxType() == 5;
      String accountNameLabelText = null;
      if (isOwa) {
         accountNameLabelText = ApplicationResources.getString(52);
      } else {
         accountNameLabelText = ApplicationResources.getString(135);
      }

      this.addContentField(new BoldLabelField(accountNameLabelText));
      this._accountNameEdit = (EmailAddressEditField)(new Object(null, mailboxToEdit.getDescription()));
      this.addContentField(this._accountNameEdit, true);
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(136)));
      this.addContentField((Field)(new Object(mailboxToEdit.getEmail())));
      if (userInfo.isChangeReplyToEnabled() && !isOwa) {
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
      BoldLabelField userNameLabel = new BoldLabelField(ApplicationResources.getString(13));
      LabelField accountUserNameLabel = (LabelField)(new Object(mailboxToEdit.getUserName()));
      this.addContentFieldRow(new Object[]{userNameLabel, accountUserNameLabel});
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(14)));
      if (!userInfo.isAutoAuth()) {
         this._passwordEdit = (PasswordEditField)(new Object(null, mailboxToEdit.getPassword()));
      } else {
         this._passwordEdit = (PasswordEditField)(new Object(null, ""));
      }

      this.addContentField(this._passwordEdit, true);
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

      this.addContentLineBreak();
      this._synchronizeDeletesCheckbox = (CheckboxField)(new Object(null, mailboxToEdit.isDeleteSyncEnabled()));
      if (DeviceUtils.isDeleteSyncEnabled(mailboxToEdit.getDescription())) {
         this._synchronizeDeletesCheckbox.setLabel(ApplicationResources.getString(127));
      } else {
         this._synchronizeDeletesCheckbox.setLabel(ApplicationResources.getString(128));
         this._synchronizeDeletesCheckbox.setEditable(false);
      }

      this.addContentField(this._synchronizeDeletesCheckbox);
      Button cancel = new Button(ApplicationResources.getString(28));
      Button save = new Button(ApplicationResources.getString(29));
      this.addButtonBarButtons(new Button[]{cancel, save}, false, 1);
      LinkEvent cancelEvent = new LinkEvent(28, 7);
      this.attachEventToField(cancel, cancelEvent);
      this.setCloseEvent(cancelEvent);
      CommandEvent saveEvent = new CommandEvent(29, 8, new String[]{"description", "replyTo", "friendlyName", "password", "signature", "autoBCC", "deleteSync"});
      this.attachEventToField(save, saveEvent);
      this.setDefaultEvent(saveEvent);
      this.setHelp("index.wml");
   }

   @Override
   protected final void addCustomMenuItems(Menu menu, int instance) {
      super.addCustomMenuItems(menu, instance);
      Mailbox mailboxToEdit = ClientSessionState.getInstance().getMailboxToModify();
      boolean isOwa = mailboxToEdit.getMailboxType() == 5;
      int advancedScreenTarget = 35;
      if (!isOwa) {
         advancedScreenTarget = 36;
      }

      LinkEvent advancedLinkEvent = new LinkEvent(207, advancedScreenTarget);
      NotificationMenuItem advancedMenuItem = new NotificationMenuItem(advancedLinkEvent.getLabel(), 9998, 9998);
      menu.add(advancedMenuItem);
      this.attachEventToMenuItem(advancedMenuItem, advancedLinkEvent);
      menu.add(MenuItem.separator(9999));
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      Mailbox mailboxToEdit = ClientSessionState.getInstance().getMailboxToModify();
      boolean isOwa = mailboxToEdit.getMailboxType() == 5;
      UserInfo userInfo = ClientSessionState.getInstance().getUserInfo();
      if (userInfo.isChangeReplyToEnabled() && !isOwa) {
         String replyToAddress = this._emailAddressEdit.getText();
         boolean replyToIsValid = replyToAddress == null || replyToAddress.length() == 0 || InputValidationUtils.isValidEmailAddress(replyToAddress);
         if (!replyToIsValid) {
            this.setError(ApplicationResources.getString(188));
            return false;
         }

         inputMap.put("replyTo", replyToAddress);
      }

      if (userInfo.isAutoBCCEnabled()) {
         String autoBCCAddress = this._autoBCCEdit.getText();
         boolean autoBCCIsValid = autoBCCAddress == null || autoBCCAddress.length() == 0 || InputValidationUtils.isValidEmailAddress(autoBCCAddress);
         if (!autoBCCIsValid) {
            this.setError(ApplicationResources.getString(190));
            return false;
         }

         inputMap.put("autoBCC", autoBCCAddress);
      }

      String password = this._passwordEdit.getText();
      if ((password == null || password.length() == 0) && !userInfo.isAutoAuth()) {
         this.setError(ApplicationResources.getString(111));
         return false;
      } else {
         inputMap.put("description", this._accountNameEdit.getText());
         inputMap.put("friendlyName", this._yourNameEdit.getText());
         inputMap.put("password", password);
         inputMap.put("signature", this._signatureEdit.getText());
         inputMap.put("deleteSync", this._synchronizeDeletesCheckbox.getChecked() ? "true" : "false");
         return true;
      }
   }
}
