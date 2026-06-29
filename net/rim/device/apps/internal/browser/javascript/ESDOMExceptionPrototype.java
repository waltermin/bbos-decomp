package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.Value;

final class ESDOMExceptionPrototype extends ESObject {
   private static final String[] ERRORS = new String[]{
      "INDEX_SIZE_ERR",
      "DOMSTRING_SIZE_ERR",
      "HIERARCHY_REQUEST_ERR",
      "WRONG_DOCUMENT_ERR",
      "INVALID_CHARACTER_ERR",
      "NO_DATA_ALLOWED_ERR",
      "NO_MODIFICATION_ALLOWED_ERR",
      "NOT_FOUND_ERR",
      "NOT_SUPPORTED_ERR",
      "INUSE_ATTRIBUTE_ERR",
      "INVALID_STATE_ERR",
      "SYNTAX_ERR",
      "INVALID_MODIFICATION_ERR",
      "NAMESPACE_ERR",
      "INVALID_ACCESS_ERR"
   };

   ESDOMExceptionPrototype() {
      for (int i = 0; i < ERRORS.length; i++) {
         this.addField(ERRORS[i], 7, Value.makeIntegerValue(i + 1));
      }

      this.addHostFunction(new ESDOMExceptionPrototype$1(this, Names.DOMException, Names.toString));
   }
}
