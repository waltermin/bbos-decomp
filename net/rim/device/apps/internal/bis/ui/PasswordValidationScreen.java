package net.rim.device.apps.internal.bis.ui;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.api.ui.Event;
import net.rim.device.apps.internal.bis.api.ui.LinkField;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class PasswordValidationScreen extends UserSettingsScreen {
   private PasswordEditField _passwordEdit;
   private Hashtable _pendingParams;
   private Button _cancel;
   private Button _save;
   public static final String PARAM_MAILBOXES = "mailboxesToValidate";
   public static final String PARAM_PASSWORD = "password";

   public PasswordValidationScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this._pendingParams = screenParams;
      boolean allowCancel = true;
      if (screenParams.containsKey("secretQuestionNoCancel")) {
         Boolean noCancel = (Boolean)screenParams.get("secretQuestionNoCancel");
         allowCancel = !noCancel;
      }

      if (!allowCancel) {
         this.setMenuOptions(0);
      } else {
         this.setMenuOptions(31);
      }

      int targetCommand = 8;
      Event cancelEvent = null;
      String[] saveParamNames = null;
      String[] linkParamNames = null;
      if (this._pendingParams != null) {
         saveParamNames = new String[this._pendingParams.size() + 1];
         linkParamNames = new String[this._pendingParams.size()];
         Enumeration keys = this._pendingParams.keys();

         int curIndex;
         for (curIndex = 0; keys.hasMoreElements(); curIndex++) {
            String key = (String)keys.nextElement();
            saveParamNames[curIndex] = key;
            linkParamNames[curIndex] = key;
         }

         saveParamNames[curIndex] = "password";
      } else {
         saveParamNames = new String[]{"password"};
         linkParamNames = new String[0];
      }

      Mailbox mailboxToValidate = null;
      if (this._pendingParams.containsKey("mailboxesToValidate")) {
         Vector mailboxes = (Vector)this._pendingParams.get("mailboxesToValidate");
         mailboxToValidate = (Mailbox)mailboxes.firstElement();
         targetCommand = 31;
         cancelEvent = new CommandEvent(28, 33, linkParamNames);
      } else {
         mailboxToValidate = ClientSessionState.getInstance().getMailboxToModify();
         cancelEvent = new LinkEvent(28, 7);
      }

      if (mailboxToValidate == null) {
         BISEventLogger.logEvent("No mailbox found to validate", 0);
      }

      String title = MessageFormat.format(ApplicationResources.getString(226), new String[]{mailboxToValidate.getDescription()});
      this.setTitle(title);
      String topText = MessageFormat.format(ApplicationResources.getString(225), new String[]{mailboxToValidate.getDescription()});
      LabelField topLabel = new LabelField(topText);
      topLabel.setMargin(0, 0, 10, 0);
      this.addContentField(topLabel);
      String accountText = MessageFormat.format(ApplicationResources.getString(227), new String[]{mailboxToValidate.getDescription()});
      LabelField accountLabel = new LabelField(accountText);
      this.addContentField(accountLabel);
      if (mailboxToValidate.getHosted() && mailboxToValidate.hasSecretQuestion()) {
         LinkField forgotPasswordLinkField = new LinkField(ApplicationResources.getString(79));
         this.addContentField(forgotPasswordLinkField);
         this.attachEventToField(forgotPasswordLinkField, new CommandEvent(247, 34, linkParamNames));
      }

      this._passwordEdit = new PasswordEditField(null, "");
      this.addContentField(this._passwordEdit, true);
      this._save = new Button(ApplicationResources.getString(39));
      if (allowCancel) {
         this._cancel = new Button(ApplicationResources.getString(28));
         this.addButtonBarButtons(new Button[]{this._cancel, this._save}, false, 1);
         this.attachEventToField(this._cancel, cancelEvent);
      } else {
         this.addButtonBarButtons(new Button[]{this._save}, false, 0);
      }

      CommandEvent saveEvent = new CommandEvent(39, targetCommand, saveParamNames);
      this.attachEventToField(this._save, saveEvent);
      this.setDefaultEvent(saveEvent);
      this.setHelp("98732.wml");
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      Enumeration keys = this._pendingParams.keys();

      while (keys.hasMoreElements()) {
         Object key = keys.nextElement();
         inputMap.put(key, this._pendingParams.get(key));
      }

      if (this._save.isFocus()) {
         String password = this._passwordEdit.getText();
         if (password == null || password.length() == 0) {
            this.setError(ApplicationResources.getString(111));
            return false;
         }

         inputMap.put("password", password);
      }

      return true;
   }
}
