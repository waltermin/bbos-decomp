package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.Value;

class ESNodePrototype extends RedirectedObject {
   private String[] CLASSES = new String[]{
      "ELEMENT_NODE",
      "ATTRIBUTE_NODE",
      "TEXT_NODE",
      "CDATA_SECTION_NODE",
      "ENTITY_REFERENCE_NODE",
      "ENTITY_NODE",
      "PROCESSING_INSTRUCTION_NODE",
      "COMMENT_NODE",
      "DOCUMENT_NODE",
      "DOCUMENT_TYPE_NODE",
      "DOCUMENT_FRAGMENT_NODE",
      "NOTATION_NODE"
   };

   public ESNodePrototype() {
      for (int i = 0; i < this.CLASSES.length; i++) {
         this.addField(this.CLASSES[i], 7, Value.makeIntegerValue(i + 1));
      }

      this.addHostFunction(new ESNodePrototype$1(this, Names.Node, "insertBefore", 2));
      this.addHostFunction(new ESNodePrototype$2(this, Names.Node, "replaceChild", 2));
      this.addHostFunction(new ESNodePrototype$3(this, Names.Node, "removeChild", 1));
      this.addHostFunction(new ESNodePrototype$4(this, Names.Node, "appendChild", 1));
      this.addHostFunction(new ESNodePrototype$5(this, Names.Node, "hasChildNodes", 0));
      this.addHostFunction(new ESNodePrototype$6(this, Names.Node, "cloneNode", 1));
      this.addHostFunction(new ESNodePrototype$7(this, Names.Node, "normalize", 0));
      this.addHostFunction(new ESNodePrototype$8(this, Names.Node, "isSupported", 2));
      this.addHostFunction(new ESNodePrototype$9(this, Names.Node, "hasAttributes", 0));
   }
}
