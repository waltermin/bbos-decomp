package net.rim.device.cldc.io.ssl;

import javax.microedition.pki.Certificate;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.internal.ui.component.TitledScrollingDialog;

public final class CertificateDisplayDialog extends TitledScrollingDialog {
   private ButtonField _viewCert;
   private Certificate[] _certChain;
   private int _offset;
   private static ResourceBundle _rb = ResourceBundle.getBundle(-320500590281765934L, "net.rim.device.internal.resource.SSL");

   public CertificateDisplayDialog(Certificate[] certChain, int offset) {
      this(certChain, offset, 0);
   }

   public CertificateDisplayDialog(Certificate[] certChain, int offset, long style) {
      super(style);
      this._certChain = certChain;
      this._offset = offset;
      this.setTitle(this.formatDNForTitle(certChain[offset].getSubject()));
   }

   @Override
   protected final void populateDialog() {
      boolean notBeforeAdded = false;
      Certificate var10000 = this._certChain[this._offset];
      if (this._certChain[this._offset] instanceof ProxyCertificate) {
         String notBeforeString = ((ProxyCertificate)var10000).getNotBeforeString();
         if (notBeforeString != null) {
            notBeforeAdded = true;
            this.addScrollingLabelAndValue(_rb.getString(7), notBeforeString);
         }
      }

      if (!notBeforeAdded) {
         long notBefore = this._certChain[this._offset].getNotBefore();
         this.addScrollingLabelAndValue(_rb.getString(7), this.getTimeString(notBefore));
      }

      boolean notAfterAdded = false;
      var10000 = this._certChain[this._offset];
      if (this._certChain[this._offset] instanceof ProxyCertificate) {
         String notAfterString = ((ProxyCertificate)var10000).getNotAfterString();
         if (notAfterString != null) {
            notAfterAdded = true;
            this.addScrollingLabelAndValue(_rb.getString(6), notAfterString);
         }
      }

      if (!notAfterAdded) {
         long notAfter = this._certChain[this._offset].getNotAfter();
         this.addScrollingLabelAndValue(_rb.getString(6), this.getTimeString(notAfter));
      }

      this.addScrollingLabelAndValue(_rb.getString(0), this.formatSubjectDN(this._certChain[this._offset].getSubject()));
      this.addScrollingLabelAndValue(_rb.getString(8), this._certChain[this._offset].getType());
      this.addScrollingLabelAndValue(_rb.getString(1), this._certChain[this._offset].getVersion());
      this.addScrollingLabelAndValue(_rb.getString(2), this._certChain[this._offset].getIssuer());
      this.addScrollingLabelAndValue(_rb.getString(3), this._certChain[this._offset].getSerialNumber());
      if (this._offset < this._certChain.length - 1) {
         this._viewCert = new ButtonField(_rb.getString(10), 12884901888L);
         this._viewCert.setChangeListener(this);
         this.addScrollingField(this._viewCert);
      }

      super.populateDialog();
   }

   private final void getString(String subject, StringBuffer buffer, String prefix) {
      int startIndex = subject.indexOf(prefix);
      if (startIndex >= 0) {
         int endIndex = subject.indexOf(";", startIndex);
         if (endIndex == -1) {
            endIndex = subject.length();
         }

         buffer.append(subject.substring(startIndex + 3, endIndex));
      }
   }

   private final String formatDNForTitle(String subject) {
      StringBuffer buffer = new StringBuffer();
      this.getString(subject, buffer, "CN=");
      if (buffer.length() > 0) {
         return buffer.toString();
      }

      this.getString(subject, buffer, "SN=");
      if (buffer.length() > 0) {
         return buffer.toString();
      }

      this.getString(subject, buffer, "OU=");
      if (buffer.length() > 0) {
         return buffer.toString();
      }

      this.getString(subject, buffer, "O=");
      if (buffer.length() > 0) {
         return buffer.toString();
      }

      this.getString(subject, buffer, "E=");
      return buffer.length() > 0 ? buffer.toString() : null;
   }

   private final String formatSubjectDN(String subject) {
      StringBuffer buffer = new StringBuffer();
      this.getString(subject, buffer, "CN=");
      return buffer.length() > 0 ? buffer.toString() : null;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      this.handleFieldChanged(field, context);
   }

   @Override
   protected final boolean handleFieldChanged(Field field, int context) {
      if (super.handleFieldChanged(field, context)) {
         return true;
      } else if (field == this._viewCert) {
         CertificateDisplayDialog dialog = new CertificateDisplayDialog(this._certChain, this._offset + 1);
         dialog.show();
         return true;
      } else {
         return false;
      }
   }

   private final String getTimeString(long date) {
      return DateFormat.getInstance(45).formatLocal(date);
   }
}
