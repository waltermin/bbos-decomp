package net.rim.device.apps.internal.bis.protocol;

import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.apps.internal.bis.utils.xml.XMLUtils;

final class ValidatePasswordsCall implements XMLCall {
   private Hashtable _passwords;

   ValidatePasswordsCall(Hashtable passwords) {
      this._passwords = passwords;
   }

   @Override
   public final void serialize(OutputStream ostream) {
      XMLUtils.startElement(ostream, "passwords");
      Enumeration ids = this._passwords.keys();

      while (ids.hasMoreElements()) {
         String id = (String)ids.nextElement();
         String password = (String)this._passwords.get(id);
         XMLUtils.startElement(ostream, "source");
         XMLUtils.writeSimpleElement(ostream, "srcMboxId", id);
         XMLUtils.writeSimpleElement(ostream, "password", password);
         XMLUtils.endElement(ostream, "source");
      }

      XMLUtils.endElement(ostream, "passwords");
   }
}
