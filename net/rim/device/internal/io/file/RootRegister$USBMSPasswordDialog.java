package net.rim.device.internal.io.file;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.ui.component.SimplePasswordDialog;

final class RootRegister$USBMSPasswordDialog extends SimplePasswordDialog {
   private boolean _lookingForKnownPassword;
   private Security _security = Security.getInstance();
   private Dialog _messageDlg;

   public RootRegister$USBMSPasswordDialog() {
      super(null, 1, 32, false, 134217728);
      this.setStatusPriority(50);
      this.setMessage();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final void close(int closeReason) {
      if (this._messageDlg != null) {
         this._messageDlg.cancel();
      }

      boolean var5 = false /* VF: Semaphore variable */;

      label79: {
         label78: {
            label77: {
               try {
                  var5 = true;
                  if (closeReason == 0) {
                     if (this._lookingForKnownPassword) {
                        if (StringUtilities.compareToIgnoreCase(RootRegister._knownPassword, this.getText(), 1701707776) != 0) {
                           this.setRevealPassword(true);
                           var5 = false;
                           break label79;
                        }

                        this.setMessage();
                        var5 = false;
                        break label78;
                     }

                     String password = this.getText();
                     if (password == null || password.length() < 4 || !this._security.verifyStoredPasswordOnly(password)) {
                        this._messageDlg = new Dialog(0, CommonResource.getString(10047), 0, Bitmap.getPredefinedBitmap(0), 33554432);
                        this._messageDlg.show(49);
                        Proxy.getInstance().invokeLater(new RootRegister$USBMSPasswordDialog$1(this));
                        var5 = false;
                        break label77;
                     }
                  }

                  super.close(closeReason);
                  var5 = false;
               } finally {
                  if (var5) {
                     this.setText(null);
                  }
               }

               this.setText(null);
               return;
            }

            this.setText(null);
            return;
         }

         this.setText(null);
         return;
      }

      this.setText(null);
   }

   private final void setMessage() {
      StringBuffer message = new StringBuffer(CommonResource.getString(10118));
      int devicePasswordAttempt = this._security.getPasswordFailureCount() + 1;
      int devicePasswordMaxAttempts = this._security.getMaxPasswordAttempts();
      if (this._lookingForKnownPassword || devicePasswordAttempt != devicePasswordMaxAttempts / 2 + 1 && devicePasswordAttempt != devicePasswordMaxAttempts - 1
         )
       {
         this._lookingForKnownPassword = false;
         message.append(CommonResource.getString(10048));
         if (devicePasswordAttempt == devicePasswordMaxAttempts) {
            this._messageDlg = new Dialog(0, CommonResource.getString(10049), 0, Bitmap.getPredefinedBitmap(0), 33554432);
            this._messageDlg.show(49);
         }

         boolean revealDevicePassword = devicePasswordAttempt > this._security.getRevealPasswordAttempts(devicePasswordMaxAttempts);
         this.setRevealPassword(revealDevicePassword);
         if (devicePasswordAttempt > 1) {
            message.append(' ');
            message.append('(');
            message.append(devicePasswordAttempt);
            message.append('/');
            message.append(devicePasswordMaxAttempts);
            message.append(')');
         }

         message.append(':');
         message.append(' ');
         this.setPrompt(message.toString());
      } else {
         this._lookingForKnownPassword = true;
         this.setRevealPassword(true);
         message.append(MessageFormat.format(CommonResource.getString(10050), new Object[]{RootRegister._knownPassword}));
         this.setPrompt(message.toString());
      }
   }
}
