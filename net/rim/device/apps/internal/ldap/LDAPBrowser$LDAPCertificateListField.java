package net.rim.device.apps.internal.ldap;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.apps.internal.api.crypto.certificate.CertificateListField;

class LDAPBrowser$LDAPCertificateListField extends CertificateListField {
   private final LDAPBrowser this$0;

   LDAPBrowser$LDAPCertificateListField(LDAPBrowser _1, int numRows, long style) {
      super(numRows, style);
      this.this$0 = _1;
   }

   @Override
   protected int getCheckState(int index) {
      LDAPBrowserContainer container = (LDAPBrowserContainer)this.this$0._containers.elementAt(index);
      Certificate certificate = container.getCertificate();
      if (certificate != null) {
         try {
            return this.this$0._context.isCertificateInKeyStore(certificate) ? 1 : 0;
         } catch (LDAPBrowserException var5) {
         }
      }

      return 0;
   }

   @Override
   protected String getText(int index) {
      return ((LDAPBrowserContainer)this.this$0._containers.elementAt(index)).getLabel();
   }

   @Override
   protected Font getFont(int index) {
      return this.this$0.isMatched(index) ? this.this$0._boldFont : this.this$0._font;
   }

   @Override
   public Object get(ListField listField, int index) {
      return ((LDAPBrowserContainer)this.this$0._containers.elementAt(index)).getLabel();
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return listField.indexOfList(prefix, start);
   }
}
