package net.rim.device.api.crypto.pgp;

import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.keystore.KeyStoreCancelException;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.crypto.pgp.PGPUtilities;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.PasswordDialog;

public class PGPPasswordTicket implements Persistable {
   private int _promptType;
   private String[] _promptParameters;
   private byte[] _password;
   public static final int PROMPT_TYPE_DECRYPT_PRIVATE_KEY = 0;
   public static final int PROMPT_TYPE_DECRYPT_MESSAGE = 1;

   public PGPPasswordTicket(int promptType) {
      this(promptType, null);
   }

   public PGPPasswordTicket(int promptType, String[] promptParameters) {
      this._promptType = promptType;
      this._promptParameters = promptParameters;
   }

   public PGPPseudoRandomSource getPseudoRandomSource(int s2kType, byte[] salt, Digest digest, byte codedValue, boolean retry) throws KeyStoreCancelException {
      ResourceBundle rb = PGPUtilities.getResourceBundle();
      StringBuffer promptBuffer = new StringBuffer();
      if (retry) {
         promptBuffer.append(rb.getString(8036));
         this._password = null;
      }

      if (this._password == null) {
         switch (this._promptType) {
            case -1:
               throw new IllegalArgumentException();
            case 0:
            default:
               if (this._promptParameters == null) {
                  promptBuffer.append(rb.getString(8023));
               } else {
                  promptBuffer.append(MessageFormat.format(rb.getString(8032), this._promptParameters));
               }
               break;
            case 1:
               if (this._promptParameters == null) {
                  promptBuffer.append(rb.getString(8027));
               } else {
                  promptBuffer.append(MessageFormat.format(rb.getString(8034), this._promptParameters));
               }
         }

         PasswordDialog dialog = new PasswordDialog(promptBuffer.toString(), false, 255, 134217728);
         BackgroundDialog.show(dialog);
         if (dialog.getCloseReason() == -1) {
            throw new KeyStoreCancelException();
         }

         this._password = dialog.getPassword();
      }

      byte[] password = Arrays.copy(this._password);
      switch (s2kType) {
         case -1:
         case 2:
            throw new IllegalArgumentException();
         case 0:
         default:
            return new PGPSimpleKDFPseudoRandomSource(password, digest);
         case 1:
            return new PGPSaltedKDFPseudoRandomSource(password, salt, digest);
         case 3:
            return new PGPSaltedIteratedKDFPseudoRandomSource(codedValue, password, salt, digest);
      }
   }
}
