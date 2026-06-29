package net.rim.device.apps.internal.browser.html;

import java.io.InputStream;

class HTMLDOMInternalRepresentation$StringStream extends InputStream {
   private int _index;
   private String _str;

   HTMLDOMInternalRepresentation$StringStream(String str) {
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
