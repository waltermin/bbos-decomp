package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.certificate.CertificateServerInfo;
import net.rim.device.apps.api.framework.verb.Verb;

class CertificateServersAttachmentField$ServerVerb extends Verb {
   private int _type;
   private final CertificateServersAttachmentField this$0;
   public static final int DISPLAY = 1;
   public static final int IMPORT = 2;

   public CertificateServersAttachmentField$ServerVerb(CertificateServersAttachmentField _1, int type, int ordering) {
      super(ordering);
      this.this$0 = _1;
      this._type = type;
   }

   @Override
   public Object invoke(Object context) {
      switch (this._type) {
         case 0:
            throw new IllegalArgumentException();
         case 1:
         default:
            CertificateServerInfo serverInfox = this.this$0._attachmentModel.getCertificateServerInfo();
            if (serverInfox != null) {
               CertificateServerInfo.display(serverInfox);
               return null;
            }
            break;
         case 2:
            CertificateServerInfo serverInfo = this.this$0._attachmentModel.getCertificateServerInfo();
            if (serverInfo != null && !this.this$0._servers.contains(serverInfo)) {
               this.this$0._servers.addServer(serverInfo);
               this.this$0._listField.invalidate();
               return null;
            }
      }

      return null;
   }

   @Override
   public String toString() {
      switch (this._type) {
         case 0:
            throw new IllegalArgumentException();
         case 1:
         default:
            return CertificateServersAttachmentField._rb.getString(107);
         case 2:
            return CertificateServersAttachmentField._rb.getString(108);
      }
   }
}
