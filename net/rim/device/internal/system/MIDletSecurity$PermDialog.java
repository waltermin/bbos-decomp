package net.rim.device.internal.system;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.PopupDialog;

class MIDletSecurity$PermDialog extends PopupDialog {
   private DialogFieldManager _dfm;
   private Field _yesButton;
   private Field _noButton;
   int _setting;

   MIDletSecurity$PermDialog(int perm, int setting, ApplicationDescriptor descriptor, String target) {
      super(new DialogFieldManager(), 33554432);
      this.setStatusPriority(-2147483643);
      String message = this.getMessage(perm, descriptor, target);
      String[] yesNo = CommonResource.getStringArray(10012);
      this._dfm = (DialogFieldManager)this.getDelegate();
      RichTextField label = new RichTextField(message, 45035996273704960L);
      this._dfm.setMessage(label);
      this._yesButton = new ButtonField(yesNo[0]);
      this._dfm.addCustomField(this._yesButton);
      this._noButton = new ButtonField(yesNo[1]);
      this._dfm.addCustomField(this._noButton);
      switch (setting) {
         case 2:
            this._yesButton.setFocus();
            break;
         default:
            this._noButton.setFocus();
      }

      this._setting = 0;
   }

   private String getMessage(int perm, ApplicationDescriptor descriptor, String target) {
      int group = MIDletSecurityConstants.MIDletPermissionGroups[perm];
      int resid = MIDletSecurityConstants.MIDletGroupResourceIds[group];
      String text = CommonResource.getString(resid);
      String moduleName = descriptor.getName();
      String formattedMsg = null;
      if (resid == 10057) {
         String displayTarget = target;
         if (displayTarget == null || displayTarget.trim().equals("") || StringUtilities.strEqualIgnoreCase(displayTarget.substring(0, 2), "//", 1701707776)) {
            displayTarget = CommonResource.getString(10178);
         }

         formattedMsg = MessageFormat.format(text, new String[]{moduleName, displayTarget});
      } else if (resid == 10058) {
         String displayTarget = target;
         if (displayTarget == null || displayTarget.trim().equals("") || displayTarget.substring(0, 2).equals("//")) {
            displayTarget = CommonResource.getString(10178);
         }

         formattedMsg = MessageFormat.format(text, new String[]{moduleName, displayTarget});
      } else {
         formattedMsg = MessageFormat.format(text, new String[]{moduleName});
      }

      return formattedMsg;
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      return this.doAction() ? true : super.trackwheelClick(status, time);
   }

   @Override
   protected boolean keyChar(char c, int status, int time) {
      return c == '\n' && this.doAction() ? true : super.keyChar(c, status, time);
   }

   private boolean doAction() {
      Field f = this._dfm.getLeafFieldWithFocus();
      if (f == this._yesButton) {
         this._setting = 6;
         this.close(0);
         return true;
      } else if (f == this._noButton) {
         this._setting = 0;
         this.close(0);
         return true;
      } else {
         return false;
      }
   }
}
