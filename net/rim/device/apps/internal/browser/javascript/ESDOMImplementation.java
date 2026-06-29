package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.ThrownValue;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

final class ESDOMImplementation extends ESObject {
   private Document _doc;

   ESDOMImplementation(Document doc) {
      this._doc = doc;
      this.addHostFunction(new ESDOMImplementation$1(this, Names.DOMImplementation, "hasFeature", 2));
      this.addHostFunction(new ESDOMImplementation$2(this, Names.DOMImplementation, "createDocumentType", 3));
      this.addHostFunction(new ESDOMImplementation$3(this, Names.DOMImplementation, "createDocument", 3));
   }

   static final DocumentType getDocType(long paramValue) {
      Object obj = Convert.toObject(paramValue);
      if (!(obj instanceof ESDocumentType)) {
         throw ThrownValue.referenceError("Parm not a doctype");
      } else {
         return (DocumentType)((ESDocumentType)obj).getNode();
      }
   }
}
