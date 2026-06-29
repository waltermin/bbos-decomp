package net.rim.device.api.xml.jaxp;

import java.io.InputStream;

class DOMInternalRepresentation$StringStream extends InputStream {
   private int _index;
   private String _str;

   DOMInternalRepresentation$StringStream(String str) {
      this._str = str;
      this._index = 0;
   }

   @Override
   public int read() {
      try {
         return this._str.charAt(this._index++);
      } finally {
         ;
      }
   }
}
