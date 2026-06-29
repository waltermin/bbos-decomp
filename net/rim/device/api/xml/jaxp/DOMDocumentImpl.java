package net.rim.device.api.xml.jaxp;

import net.rim.device.api.util.IntVector;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

class DOMDocumentImpl extends DOMNodeImpl implements Document {
   DOMDocumentImpl(DOMInternalRepresentation ir, int node) {
      super(ir, node);
   }

   @Override
   public String getNodeName() {
      return "#document";
   }

   @Override
   public DocumentType getDoctype() {
      return super._ir.getDocumentType();
   }

   @Override
   public DOMImplementation getImplementation() {
      return DOMImplementationImpl.getInstance();
   }

   @Override
   public Element getDocumentElement() {
      for (int child = super._ir.getFirstChild(super._node); child != 0; child = super._ir.getNextChild(child)) {
         if (super._ir.getNodeType(child) == 1) {
            return (Element)super._ir.getNode(child);
         }
      }

      return null;
   }

   private Element appendDefaultAttributes(int element, IntVector defaultAttributes) {
      if (defaultAttributes != null) {
         for (int i = defaultAttributes.size() - 1; i >= 0; i--) {
            super._ir.insertAttribute(element, defaultAttributes.elementAt(i));
         }
      }

      return (Element)super._ir.getNode(element);
   }

   @Override
   public Element createElement(String tagName) {
      DOMInternalRepresentation.isNCName(tagName);
      IntVector defaultAttributes = super._ir.addDefaultAttributes(tagName);
      int element = super._ir.addElement("", "", tagName, tagName);
      return this.appendDefaultAttributes(element, defaultAttributes);
   }

   @Override
   public DocumentFragment createDocumentFragment() {
      return (DocumentFragment)super._ir.getNode(super._ir.addDocumentFragment());
   }

   @Override
   public Text createTextNode(String data) {
      char[] buff = data.toCharArray();
      return (Text)super._ir.getNode(super._ir.addText(3, buff, 0, buff.length));
   }

   @Override
   public Comment createComment(String data) {
      char[] buff = data.toCharArray();
      return (Comment)super._ir.getNode(super._ir.addText(8, buff, 0, buff.length));
   }

   @Override
   public CDATASection createCDATASection(String data) {
      char[] buff = data.toCharArray();
      return (CDATASection)super._ir.getNode(super._ir.addText(4, buff, 0, buff.length));
   }

   @Override
   public ProcessingInstruction createProcessingInstruction(String target, String data) {
      DOMInternalRepresentation.isQName(target);
      return (ProcessingInstruction)super._ir.getNode(super._ir.addPI(target, data));
   }

   @Override
   public Attr createAttribute(String name) {
      DOMInternalRepresentation.isQName(name);
      return (Attr)super._ir.getNode(super._ir.addAttribute("", "", name, name, "", "CDATA", false));
   }

   @Override
   public EntityReference createEntityReference(String name) {
      DOMInternalRepresentation.isNCName(name);
      String value = super._ir.getEntityValue(name);
      DOMTextImpl text = null;
      if (value != null) {
         text = (DOMTextImpl)this.createTextNode(value);
         super._ir.setReadOnly(text._node);
      }

      DOMNodeImpl er = super._ir.getNode(super._ir.addEntityReference(name, null, null));
      if (text != null) {
         er.appendChild(text);
         super._ir.setReadOnly(er._node);
      }

      return (EntityReference)er;
   }

   @Override
   public NodeList getElementsByTagName(String tagname) {
      return new DOMNodeListImpl(super._ir.getElementsByTagName(super._node, tagname));
   }

   @Override
   public Node importNode(Node importedNode, boolean deep) {
      throw new Object((short)9, "nyi");
   }

   @Override
   public Element createElementNS(String namespaceURI, String qName) {
      DOMInternalRepresentation.isQName(qName);
      IntVector defaultAttributes = super._ir.addDefaultAttributes(qName);
      int element = super._ir.addElement(namespaceURI, qName);
      return this.appendDefaultAttributes(element, defaultAttributes);
   }

   @Override
   public Attr createAttributeNS(String namespaceURI, String qualifiedName) {
      DOMInternalRepresentation.isQName(qualifiedName);
      return (Attr)super._ir.getNode(super._ir.addAttribute(namespaceURI, qualifiedName, "", "CDATA", false));
   }

   @Override
   public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
      return new DOMNodeListImpl(super._ir.getElementsByTagNameNS(super._node, namespaceURI, localName));
   }

   @Override
   public Element getElementById(String elementId) {
      return null;
   }
}
