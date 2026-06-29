package net.rim.device.internal.ui.component;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.RichTextFieldUtilities;
import net.rim.device.internal.ui.container.FrameLayout;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

public class PasswordDialog extends PopupDialog implements FieldChangeListener {
   private byte[] _passwordBytes;
   protected ButtonField _ok;
   protected ButtonField _cancel;
   protected BasicEditField _passwordField;
   protected BasicEditField _confirmField;
   private boolean _mismatchIndicationDisplayed;

   protected boolean accept() {
      String passwordString = this._passwordField.getText();
      int passwordStringLength = passwordString.length();
      String confirmString = this._confirmField != null ? this._confirmField.getText() : null;
      int confirmStringLength = confirmString != null ? confirmString.length() : 0;
      if (passwordStringLength == 0 && confirmStringLength == 0) {
         if (this._mismatchIndicationDisplayed) {
            Manager manager = this.getDelegate();
            manager.deleteRange(0, 2);
            this._mismatchIndicationDisplayed = false;
         }

         this._passwordField.setFocus();
         return false;
      } else if (this._confirmField != null && !StringUtilities.strEqual(passwordString, confirmString)) {
         synchronized (Application.getEventLock()) {
            if (!this._mismatchIndicationDisplayed) {
               RichTextField mismatchIndicationField = new RichTextField(CommonResource.getString(10013));
               Manager manager = this.getDelegate();
               manager.insert(mismatchIndicationField, 0);
               manager.insert(new SeparatorField(), 1);
               this._mismatchIndicationDisplayed = true;
            }

            this._passwordField.setText(null);
            this._confirmField.setText(null);
            this._passwordField.setFocus();
            return false;
         }
      } else {
         this._passwordBytes = passwordString.getBytes();
         this.close(0);
         return true;
      }
   }

   public byte[] getPassword() {
      return this._passwordBytes;
   }

   protected boolean cancel() {
      if (!this.isCancelAllowed()) {
         return false;
      }

      this._passwordBytes = null;
      this.close(-1);
      return true;
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field == this._ok) {
         this.accept();
      } else {
         if (field == this._cancel) {
            this.cancel();
         }
      }
   }

   public PasswordDialog(String passwordLabel, String confirmLabel) {
      this(passwordLabel, confirmLabel, 32);
   }

   public PasswordDialog(String passwordLabel, String confirmLabel, int maxPasswordLength) {
      this(passwordLabel, confirmLabel, maxPasswordLength, 0);
   }

   public PasswordDialog(String passwordLabel, String confirmLabel, int maxPasswordLength, int style) {
      this(passwordLabel, confirmLabel, false, maxPasswordLength, style);
   }

   public PasswordDialog(String passwordLabel, String passwordLabelRetry, boolean confirm, String confirmLabel, boolean revealPassword, int style) {
      this(passwordLabel, confirmLabel, revealPassword, 32, style);
   }

   private PasswordDialog(String passwordLabel, String confirmLabel, boolean revealPassword, int maxPasswordLength, int style) {
      super(new VerticalIndentFieldManager(1153220571769602048L), style);
      VerticalIndentFieldManager vifm = (VerticalIndentFieldManager)this.getDelegate();
      RichTextField passwordLabelField = RichTextFieldUtilities.getBoldFormattedRichTextField(passwordLabel, 45035996273704960L);
      vifm.add(passwordLabelField);
      this._passwordField = this.createPasswordEditField(revealPassword, maxPasswordLength);
      FrameLayout layout = new FrameLayout(1);
      layout.add(this._passwordField);
      vifm.add(layout);
      if (confirmLabel != null) {
         RichTextField confirmLabelField = RichTextFieldUtilities.getBoldFormattedRichTextField(confirmLabel, 45035996273704960L);
         vifm.add(confirmLabelField);
         this._confirmField = this.createPasswordEditField(revealPassword, maxPasswordLength);
         layout = new FrameLayout(1);
         layout.add(this._confirmField);
         vifm.add(layout);
      }

      HorizontalFieldManager buttonManager = new HorizontalFieldManager(12884901888L);
      this._ok = new ButtonField(CommonResource.getString(100));
      this._ok.setChangeListener(this);
      buttonManager.add(this._ok);
      this._cancel = new ButtonField(CommonResource.getString(10005));
      this._cancel.setChangeListener(this);
      buttonManager.add(this._cancel);
      vifm.add(buttonManager);
   }

   private BasicEditField createPasswordEditField(boolean revealPassword, int maxPasswordLength) {
      return revealPassword ? new BasicEditField(null, null, maxPasswordLength, 1073741824) : new PasswordEditField(null, null, maxPasswordLength, 1073741824);
   }

   public PasswordDialog(String passwordLabel) {
      this(passwordLabel, false);
   }

   public PasswordDialog(String passwordLabel, boolean revealPassword, int maxPasswordLength, int style) {
      this(passwordLabel, null, revealPassword, maxPasswordLength, style);
   }

   public PasswordDialog(String passwordLabel, boolean revealPassword, int maxPasswordLength) {
      this(passwordLabel, revealPassword, maxPasswordLength, 0);
   }

   public PasswordDialog(String passwordLabel, boolean revealPassword) {
      this(passwordLabel, revealPassword, 32);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         return this.cancel();
      }

      if (key == '\n') {
         Field field = this.getLeafFieldWithFocus();
         if (field == this._cancel) {
            return this.cancel();
         } else if (field == this._passwordField && this._confirmField != null) {
            this._confirmField.setFocus();
            return true;
         } else {
            this.accept();
            return true;
         }
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      if (!super.trackwheelClick(status, time)) {
         Field field = this.getLeafFieldWithFocus();
         if (field == this._cancel) {
            return this.cancel();
         } else if (field == this._passwordField && this._confirmField != null) {
            this._confirmField.setFocus();
            return true;
         } else {
            this.accept();
            return true;
         }
      } else {
         return true;
      }
   }
}
