package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.BasicScreen;
import net.rim.device.apps.internal.bis.api.ui.BoldLabelField;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.utils.InputValidationUtils;

public final class ForgotPasswordScreen extends BasicScreen {
   private BasicEditField _userNameEdit;
   private static final String PARAM_USERNAME = "userName";

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(8));
      this.addContentField(new LabelField(ApplicationResources.getString(76)));
      this.addContentLineBreak();
      this.addContentField(new BoldLabelField(ApplicationResources.getString(13)));
      this._userNameEdit = new BasicEditField(null, null);
      this.addContentField(this._userNameEdit, true);
      Button cancelButton = new Button(ApplicationResources.getString(28));
      Button okButton = new Button(ApplicationResources.getString(39));
      this.addButtonBarButtons(new Button[]{cancelButton, okButton}, false, 1);
      CommandEvent okEvent = new CommandEvent(39, 11, new String[]{"userName"});
      this.attachEventToField(okButton, okEvent);
      this.setDefaultEvent(okEvent);
      this.attachEventToField(cancelButton, new BackEvent(28));
      this.setHelp("231240.wml");
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      String userName = this._userNameEdit.getText();
      if (!InputValidationUtils.isValidUsername(userName)) {
         this.setError(ApplicationResources.getString(97));
         return false;
      } else {
         inputMap.put("userName", userName);
         return true;
      }
   }
}
