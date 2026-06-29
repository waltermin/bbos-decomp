package net.rim.device.apps.internal.secureemail.encodings.pgp.server;

import java.io.IOException;
import net.rim.device.api.xml.XMLHashtable;

public class PGPUniversalServerException extends IOException {
   private XMLHashtable _hashtable;

   protected PGPUniversalServerException(XMLHashtable hashtable) {
      this._hashtable = hashtable;
   }

   public XMLHashtable getXMLHashtable() {
      return this._hashtable;
   }
}
