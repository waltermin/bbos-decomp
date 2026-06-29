package net.rim.device.apps.internal.browser.page;

import javax.microedition.io.SecurityInfo;
import javax.microedition.pki.Certificate;
import net.rim.device.api.crypto.tls.TLSSecurityInfo;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class DialogShowInfo extends PopupScreen {
   private Certificate _javaCertificate;
   private net.rim.device.api.crypto.certificate.Certificate _rimCertificate;

   public DialogShowInfo(String text, SecurityInfo info) {
      super(new VerticalFieldManager(1153220571769602048L), 196608);
      VerticalFieldManager vfm = (VerticalFieldManager)this.getDelegate();
      vfm.add(new RichTextField(text));
      if (info != null) {
         if (info instanceof TLSSecurityInfo) {
            this._rimCertificate = ((TLSSecurityInfo)info).getRIMServerCertificate();
            return;
         }

         this._javaCertificate = info.getServerCertificate();
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int context) {
      super.makeMenu(menu, context);
      if (this._rimCertificate != null) {
         menu.add(new DialogShowInfo$1(this, BrowserResources.getString(744), 100, 1));
      } else {
         if (this._javaCertificate != null) {
            menu.add(new DialogShowInfo$2(this, BrowserResources.getString(744), 100, 1));
         }
      }
   }
}
