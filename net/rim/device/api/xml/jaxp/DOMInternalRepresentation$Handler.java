package net.rim.device.api.xml.jaxp;

import java.util.Hashtable;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

class DOMInternalRepresentation$Handler extends DefaultHandler implements RIMExtendedHandler {
   private Hashtable _entityReferences;
   private boolean _inDTD;
   private Hashtable _namespaceMap;
   private Vector _namespaceStack;
   private Vector _namespacePending;
   private RIMExtendedAttributes _noExtendedAttributes;
   private final DOMInternalRepresentation this$0;

   protected void fini(int node) {
      this.this$0.fixupAttributeValues(node);
      this.this$0._attributeValueLookup = null;
      this.this$0._parsing = false;
      this.this$0.shrink();
   }

   void addChildren(int parent) {
      int child = this.this$0.popChildren();

      while (child != 0) {
         int nextChild = this.this$0.getNextChild(child);
         this.this$0.setNextChild(child, 0);
         switch (this.this$0.getType(child)) {
            case 2:
               this.this$0.insertAttribute(parent, child);
               break;
            case 13:
               this.this$0._elementNamespaces.put(parent, child);
               break;
            default:
               this.this$0.insertChild(parent, child);
         }

         child = nextChild;
      }
   }

   @Override
   public void comment(char[] buff, int start, int length) {
      if (!this.this$0._ignoringComments) {
         int child = this.this$0.addText(8, buff, start, length);
         if (this._inDTD) {
            this.this$0.setInDTD(child);
         }

         this.this$0.pushChild(child);
      }
   }

   @Override
   public void cdataSection(char[] buff, int start, int length) {
      if (this.this$0._coalescing) {
         this.characters(buff, start, length);
      } else {
         this.this$0.pushChild(this.this$0.addText(4, buff, start, length));
      }
   }

   @Override
   public void startEntityReference(String name, String publicId, String systemId) {
      if (!this.this$0._expandingEntities) {
         SystemId id = new SystemId(publicId, systemId);
         this._entityReferences.put(name, id);
         this.this$0.pushChildren();
      }
   }

   @Override
   public void endEntityReference(String name) {
      if (!this.this$0._expandingEntities) {
         SystemId id = (SystemId)this._entityReferences.remove(name);
         int node = this.this$0.addEntityReference(name, id.publicId, id.systemId);
         this.addChildren(node);
         this.this$0.setTreeReadOnly(node);
         this.this$0.pushChild(node);
      }
   }

   @Override
   public void entityDecl(String name, String value) {
      this.this$0.addEntity(name, null, null, null, value);
   }

   @Override
   public void defaultAttribute(String element, String attribute, String defaultValue) {
      Hashtable attributeHash = (Hashtable)this.this$0._defaultAttributes.get(element);
      if (attributeHash == null) {
         attributeHash = (Hashtable)(new Object());
         this.this$0._defaultAttributes.put(element, attributeHash);
      }

      attributeHash.put(attribute, defaultValue);
   }

   @Override
   public void endDTD(String name, String publicId, String systemId, String body) {
      this.this$0._dtdName = name;
      this.this$0._dtdPublicId = publicId;
      this.this$0._dtdSystemId = systemId;
      this.this$0._dtdBody = body;
      this._inDTD = false;
   }

   @Override
   public void startDTD() {
      this._inDTD = true;
   }

   @Override
   public void startAndEndElement(String uri, String localName, String qName, Attributes attributes) {
      this.startElement(uri, localName, qName, attributes);
      this.endElement(uri, localName, qName);
   }

   @Override
   public void characters(char[] buff, int start, int length) {
      if (this.this$0._coalescing && this.this$0._currChild != 0 && this.this$0.getType(this.this$0._currChild) == 3) {
         this.this$0._text.append(buff, start, length);
         this.this$0.setTextLength(this.this$0._currChild, this.this$0.getTextLength(this.this$0._currChild) + length);
      } else {
         this.this$0.pushChild(this.this$0.addText(3, buff, start, length));
      }
   }

   @Override
   public void startDocument() {
      this.this$0._parsing = true;
      this.this$0._attributeValueLookup = new ObjectToIndex();
      this.this$0.pushChildren();
   }

   @Override
   public void endDocument() {
      throw null;
   }

   DOMInternalRepresentation$Handler(DOMInternalRepresentation _1) {
      this.this$0 = _1;
      this._entityReferences = (Hashtable)(new Object());
      this._namespaceMap = (Hashtable)(new Object());
      this._namespaceStack = (Vector)(new Object());
      this._namespacePending = (Vector)(new Object());
      this._noExtendedAttributes = new DOMInternalRepresentation$Handler$1(this);
   }

   private String getPrefix(String uri) {
      return (String)(uri.length() == 0 ? "" : this._namespaceMap.get(uri));
   }

   @Override
   public void ignorableWhitespace(char[] buff, int start, int length) {
      if (!this.this$0._ignoringWhitespace) {
         this.characters(buff, start, length);
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) {
      this.this$0.pushChildren();
      int nAttributes = attributes.getLength();
      RIMExtendedAttributes ext;
      if (!(attributes instanceof RIMExtendedAttributes)) {
         ext = this._noExtendedAttributes;
      } else {
         ext = (RIMExtendedAttributes)attributes;
      }

      int pendingLength = this._namespacePending.size();
      int namespaceNext = 0;

      for (int i = 0; i < pendingLength; i += 2) {
         namespaceNext = this.this$0.addNamespace((String)this._namespacePending.elementAt(i), (String)this._namespacePending.elementAt(i + 1), namespaceNext);
      }

      if (namespaceNext != 0) {
         this._namespacePending = (Vector)(new Object());
         this.this$0.pushChild(namespaceNext);
      }

      for (int i = 0; i < nAttributes; i++) {
         String attrURI = attributes.getURI(i);
         int node = this.this$0
            .addAttribute(
               attrURI,
               this.getPrefix(attrURI),
               attributes.getLocalName(i),
               attributes.getQName(i),
               attributes.getValue(i),
               attributes.getType(i),
               ext.isDefault(i)
            );
         this.this$0.pushChild(node);
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      int node = this.this$0.addElement(uri, this.getPrefix(uri), localName, qName);
      this.addChildren(node);
      this.this$0.pushChild(node);
   }

   @Override
   public void startPrefixMapping(String prefix, String uri) {
      String oldPrefix = (String)this._namespaceMap.get(uri);
      if (oldPrefix == null) {
         oldPrefix = "";
      }

      this._namespaceStack.addElement(uri);
      this._namespaceStack.addElement(oldPrefix);
      this._namespaceMap.put(uri, prefix);
      this._namespacePending.addElement(uri);
      this._namespacePending.addElement(prefix);
   }

   @Override
   public void endPrefixMapping(String prfix) {
      int len = this._namespaceStack.size() - 1;
      String oldPrefix = (String)this._namespaceStack.elementAt(len);
      this._namespaceStack.removeElementAt(len);
      String uri = (String)this._namespaceStack.elementAt(--len);
      this._namespaceStack.removeElementAt(len);
      if (oldPrefix.length() == 0) {
         this._namespaceMap.remove(uri);
      } else {
         this._namespaceMap.put(uri, oldPrefix);
      }
   }

   @Override
   public void notationDecl(String name, String publicId, String systemId) {
      this.this$0.addNotation(this.this$0._names.append(name), this.this$0._names.append(publicId), this.this$0._names.append(systemId));
   }

   @Override
   public void processingInstruction(String target, String data) {
      int child = this.this$0.addPI(target, data);
      if (this._inDTD) {
         this.this$0.setInDTD(child);
      }

      this.this$0.pushChild(child);
   }

   @Override
   public InputSource resolveEntity(String publicId, String systemId) {
      return this.this$0._entityResolver != null ? this.this$0._entityResolver.resolveEntity(publicId, systemId) : null;
   }

   @Override
   public void setDocumentLocator(Locator locator) {
   }

   @Override
   public void skippedEntity(String name) {
      this.this$0.pushChild(this.this$0.addEntityReference(name, null, null));
   }

   @Override
   public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) {
      this.this$0.addEntity(name, publicId, systemId, notationName, null);
   }

   @Override
   public void warning(SAXParseException exception) {
      if (this.this$0._errorHandler != null) {
         this.this$0._errorHandler.warning(exception);
      }
   }

   @Override
   public void error(SAXParseException exception) {
      if (this.this$0._errorHandler != null) {
         this.this$0._errorHandler.error(exception);
      }
   }

   @Override
   public void fatalError(SAXParseException exception) throws SAXParseException {
      if (this.this$0._errorHandler != null) {
         this.this$0._errorHandler.fatalError(exception);
      }

      throw exception;
   }
}
