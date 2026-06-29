package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESHTMLDocumentPrototype extends ESObject {
   private static char[] CRLF = new char[]{'\r', '\n', '3', '\u0000'};

   public ESHTMLDocumentPrototype() {
      this.setGrowthIncrement(4);
      this.addHostFunction(new ESHTMLDocumentPrototype$1(this, Names.Document, "write", 0));
      this.addHostFunction(new ESHTMLDocumentPrototype$2(this, Names.Document, "writeln", 0));
      this.addHostFunction(new ESHTMLDocumentPrototype$3(this, Names.Document, "open", 0));
      this.addHostFunction(new ESHTMLDocumentPrototype$4(this, Names.Document, "close", 0));
      this.addHostFunction(new ESHTMLDocumentPrototype$5(this, Names.Document, "getElementById", 1));
      this.addHostFunction(new ESHTMLDocumentPrototype$6(this, Names.Document, "createElement", 1));
      this.addHostFunction(new ESHTMLDocumentPrototype$7(this, Names.Document, "createDocumentFragment", 0));
      this.addHostFunction(new ESHTMLDocumentPrototype$8(this, Names.Document, "createTextNode", 1));
      this.addHostFunction(new ESHTMLDocumentPrototype$9(this, Names.Document, "createComment", 1));
      this.addHostFunction(new ESHTMLDocumentPrototype$10(this, Names.Document, "createCDATASection", 1));
      this.addHostFunction(new ESHTMLDocumentPrototype$11(this, Names.Document, "createProcessingInstruction", 1));
      this.addHostFunction(new ESHTMLDocumentPrototype$12(this, Names.Document, "createAttribute", 1));
      this.addHostFunction(new ESHTMLDocumentPrototype$13(this, Names.Document, "createEntityReference", 1));
      this.addHostFunction(new ESHTMLDocumentPrototype$14(this, Names.Document, "getElementsByTagName", 1));
      this.addHostFunction(new ESHTMLDocumentPrototype$15(this, Names.Document, "importNode", 2));
      this.addHostFunction(new ESHTMLDocumentPrototype$16(this, Names.Document, "createElementNS", 2));
      this.addHostFunction(new ESHTMLDocumentPrototype$17(this, Names.Document, "createAttributeNS", 2));
      this.addHostFunction(new ESHTMLDocumentPrototype$18(this, Names.Document, "getElementsByTagNameNS", 2));
      this.addHostFunction(new ESHTMLDocumentPrototype$19(this, Names.Document, "getElementsByName", 1));
   }
}
