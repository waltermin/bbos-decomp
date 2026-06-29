package net.rim.device.apps.internal.browser.wml;

import net.rim.device.apps.internal.browser.stack.WAPInputStream;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.device.cldc.io.utility.URIEncoder;

final class WMLVariable {
   private WMLContextManager _wmlContextManager;
   private String _type;
   private boolean _composed;
   private String _name;
   private String _prefix;
   private byte[] _data;
   private WMLAttributeReader _reader;
   private String _encoding;
   static final String ESC;
   static final String UNESC;
   static final String NOESC;

   WMLVariable(byte[] data, WMLAttributeReader reader, WMLContextManager wmlContextManager) {
      this(wmlContextManager, "NoEsc", reader.getEncoding());
      this._data = data;
      this._reader = reader;
      this._composed = true;
   }

   WMLVariable(String name, WMLContextManager wmlContextManager, String type, String encoding) {
      this(wmlContextManager, type, encoding);
      this._composed = false;
      this._name = name;
   }

   private WMLVariable(WMLContextManager wmlContextManager, String type, String encoding) {
      this._wmlContextManager = wmlContextManager;
      this._type = type;
      this._encoding = encoding;
   }

   final String getValue() {
      String value = this._wmlContextManager.get(this.getName());
      if (this._type == null || this._type.equals("NoEsc")) {
         return value;
      } else if (this._type.equals("Esc")) {
         return URIEncoder.encode(null, value, this._encoding, true);
      } else {
         return this._type.equals("UnEsc") ? URIDecoder.decode(value, this._encoding) : null;
      }
   }

   final void set(String value) {
      this._wmlContextManager.put(this.getName(), value);
   }

   final String getName() {
      return this.getName(this._wmlContextManager);
   }

   final String getName(WMLContextManager wmlContextManager) {
      if (!this._composed) {
         return this._name;
      }

      try {
         return this._prefix == null
            ? this._reader.read(new WAPInputStream(this._data), wmlContextManager)
            : ((StringBuffer)(new Object())).append(this._prefix).append(this._reader.read(new WAPInputStream(this._data), wmlContextManager)).toString();
      } finally {
         ;
      }
   }

   final void setPrefix(String prefix) {
      this._prefix = prefix;
   }

   final boolean isComposed() {
      return this._composed;
   }
}
