package net.rim.device.internal.io.file;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.cldc.io.file.FileSystemEncryption;
import net.rim.device.cldc.io.file.MasterKeyFile;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.ui.component.SimplePasswordDialog;

final class RootRegister$MasterKeyPasswordDialog extends SimplePasswordDialog {
   private Dialog _messageDlg;
   private MasterKeyFile _masterKeyFile;
   private int _attempt;
   private int _maxAttempts;
   private final RootRegister this$0;

   public RootRegister$MasterKeyPasswordDialog(RootRegister _1) {
      super(null, 1, 32, false, 134217728);
      this.this$0 = _1;
      this._attempt = 1;
      this._maxAttempts = 5;
      this.setStatusPriority(50);
      this.setMessage();
      this._masterKeyFile = FileSystemEncryption.getKeyFile();
   }

   public final MasterKeyFile getKeyFile() {
      return this._masterKeyFile;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final void close(int closeReason) {
      if (this._messageDlg != null) {
         this._messageDlg.cancel();
      }

      boolean var5 = false /* VF: Semaphore variable */;

      label54: {
         try {
            var5 = true;
            if (closeReason == 0) {
               this._attempt++;
               String password = this.getText();
               if (password == null || !this._masterKeyFile.checkPassword(password.getBytes())) {
                  this._messageDlg = new Dialog(0, CommonResource.getString(10047), 0, Bitmap.getPredefinedBitmap(0), 33554432);
                  this._messageDlg.show(49);
                  Proxy.getInstance().invokeLater(new RootRegister$MasterKeyPasswordDialog$1(this));
                  if (this._attempt > this._maxAttempts) {
                     this._messageDlg = new Dialog(0, CommonResource.getString(10124), 0, Bitmap.getPredefinedBitmap(0), 33554432);
                     this._messageDlg.show(49);
                     super.close(-1);
                     var5 = false;
                  } else {
                     var5 = false;
                  }
                  break label54;
               }

               this.this$0.setCachedMasterKey(this._masterKeyFile.getMasterKey(), this._masterKeyFile.getTimestamp(), this._masterKeyFile.getLockType());
               this.this$0._tempPwd = password;
            }

            super.close(closeReason);
            var5 = false;
         } finally {
            if (var5) {
               this.getEditField().setText(null);
            }
         }

         this.getEditField().setText(null);
         return;
      }

      this.getEditField().setText(null);
   }

   private final void setMessage() {
      StringBuffer message = new StringBuffer(CommonResource.getString(10121));
      if (this._attempt > 1) {
         message.append(' ');
         message.append('(');
         message.append(this._attempt);
         message.append('/');
         message.append(this._maxAttempts);
         message.append(')');
      }

      message.append(':');
      message.append(' ');
      this.setPrompt(message.toString());
   }
}
