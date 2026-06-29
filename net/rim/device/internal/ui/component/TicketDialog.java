package net.rim.device.internal.ui.component;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.container.FrameLayout;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;
import net.rim.device.internal.ui.security.component.VendorModuleStackDialog;

public class TicketDialog extends VendorModuleStackDialog implements FieldChangeListener {
   private boolean _isClosed;
   private byte[] _passwordBytes;
   private ButtonField _ok;
   private ButtonField _cancel;
   private BasicEditField _passwordField;
   private boolean _numericPassword;
   private boolean _revealPassword;
   private HorizontalFieldManager _passwordHFM;

   public byte[] getPassword() {
      return this.getCloseReason() == -1 ? null : this._passwordBytes;
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

   public TicketDialog(
      RichTextField label, boolean promptForPassword, String promptForPasswordString, boolean revealPassword, long style, boolean numericPasswordEntry
   ) {
      super(new VerticalIndentFieldManager(1153220571769602048L), style);
      VerticalIndentFieldManager vifm = (VerticalIndentFieldManager)this.getDelegate();
      if (label != null) {
         vifm.add(label);
      }

      this._revealPassword = revealPassword;
      if (promptForPassword) {
         if (promptForPasswordString != null) {
            vifm.add(new VerticalSpacerField(4));
            RichTextField passwordPrompt = new RichTextField(promptForPasswordString, 9007199254740992L);
            vifm.add(passwordPrompt);
         }

         if (numericPasswordEntry) {
            this._passwordHFM = new HorizontalFieldManager();
            vifm.add(this._passwordHFM);
            this._numericPassword = true;
            this.resetPasswordField(false);
         } else {
            FrameLayout layout = this.createPasswordField();
            vifm.add(layout);
         }
      }

      HorizontalFieldManager buttonManager = new HorizontalFieldManager(12884901888L);
      this._ok = new ButtonField(CommonResource.getString(100));
      this._ok.setChangeListener(this);
      buttonManager.add(this._ok);
      this._cancel = new ButtonField(CommonResource.getString(10005));
      this._cancel.setChangeListener(this);
      buttonManager.add(this._cancel);
      vifm.add(buttonManager);
      VendorModuleStackDialog.populateVendorApplicationModulesStack(vifm);
   }

   private FrameLayout createPasswordField() {
      if (this._revealPassword) {
         this._passwordField = new BasicEditField((this._numericPassword ? 16777216 : 1073741824) | 2147483648L);
      } else {
         this._passwordField = new PasswordEditField(null, "", 1000000, this._numericPassword ? 16777216 : 0);
      }

      FrameLayout layout = new FrameLayout(1);
      layout.add(this._passwordField);
      return layout;
   }

   private void resetPasswordField(boolean focusOnPasswordField) {
      this._passwordHFM.deleteAll();
      FrameLayout layout = this.createPasswordField();
      Bitmap bitmap;
      if (this._numericPassword) {
         bitmap = Bitmap.getBitmapResource("net_rim_bb_framework_api", "numericinput.gif");
      } else {
         bitmap = Bitmap.getBitmapResource("net_rim_bb_framework_api", "alphanumericinput.gif");
      }

      BitmapField bitmapField = new BitmapField(bitmap, 51539607552L);
      TicketDialog$WidthRestrictedHorizontalFieldManager wrHFM = new TicketDialog$WidthRestrictedHorizontalFieldManager(bitmapField.getPreferredWidth());
      wrHFM.add(layout);
      this._passwordHFM.add(wrHFM);
      this._passwordHFM.add(bitmapField);
      if (focusOnPasswordField) {
         this._passwordField.setFocus();
      }
   }

   @Override
   public void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         if (this._passwordField != null) {
            this._passwordField.setFocus();
            return;
         }

         if (this._ok != null) {
            this._ok.setFocus();
         }
      }
   }

   public TicketDialog(RichTextField label, boolean promptForPassword, String promptForPasswordString, boolean revealPassword, long style) {
      this(label, promptForPassword, promptForPasswordString, revealPassword, style, false);
   }

   private synchronized boolean accept() {
      if (!this._isClosed) {
         this._isClosed = true;
         this._passwordBytes = this._passwordField == null ? null : this._passwordField.getText().getBytes();
         this.close(0);
         return true;
      } else {
         return true;
      }
   }

   private synchronized boolean cancel() {
      if (!this.isCancelAllowed()) {
         return false;
      } else if (!this._isClosed) {
         this._isClosed = true;
         this.close(-1);
         return true;
      } else {
         return true;
      }
   }

   public TicketDialog(RichTextField label, boolean promptForPassword, String promptForPasswordString, boolean revealPassword) {
      this(label, promptForPassword, promptForPasswordString, revealPassword, 0);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         if (this._cancel != null) {
            this.cancel();
         }

         return true;
      } else {
         if (key == '\n') {
            Field field = this.getLeafFieldWithFocus();
            if (field == this._cancel) {
               return this.cancel();
            }

            if (this._passwordHFM != null && field == this._passwordField && this._passwordField.getTextLength() == 0) {
               this._numericPassword = !this._numericPassword;
               this.resetPasswordField(true);
               return true;
            }

            if (this._ok != null) {
               return this.accept();
            }
         }

         return super.keyChar(key, status, time);
      }
   }
}
