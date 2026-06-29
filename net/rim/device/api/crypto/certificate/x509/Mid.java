package net.rim.device.api.crypto.certificate.x509;

import net.rim.device.api.util.Persistable;
import net.rim.vm.UnGroupable;

final class Mid implements Persistable, UnGroupable {
   protected Content _content;

   public Mid(byte[] encoding) {
      this._content = new Content(encoding);
   }
}
