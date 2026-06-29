package net.rim.device.apps.internal.browser.html;

import java.util.Enumeration;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.SetHttpCookieEvent;
import net.rim.device.apps.internal.browser.markup.HTMLUtilities;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.html2.HTMLBodyElement;
import org.w3c.dom.html2.HTMLCollection;
import org.w3c.dom.html2.HTMLDocument;
import org.w3c.dom.html2.HTMLElement;
import org.w3c.dom.html2.HTMLFrameSetElement;

final class HTMLDocumentImpl extends HTMLNode implements HTMLDocument {
   private String _referrer;
   private String _host;
   private HTMLCollectionImpl _forms;
   private HTMLCollectionImpl _images;
   private HTMLCollectionImpl _anchors;
   private HTMLCollectionImpl _links;
   private HTMLCollectionImpl _applets;
   private HTMLBrowserContent _peer;

   final Element createElement(int tag) {
      int id = super._ir.addElement(tag);
      Element domObj;
      switch (tag) {
         case 4:
         case 6:
         case 7:
         case 8:
         case 11:
         case 14:
         case 15:
         case 16:
         case 21:
         case 22:
         case 23:
         case 25:
         case 26:
         case 28:
         case 32:
         case 33:
         case 48:
         case 54:
         case 62:
         case 63:
         case 70:
         case 73:
         case 74:
         case 77:
         case 78:
         case 79:
         case 80:
         case 82:
         case 83:
         case 93:
         case 94:
            domObj = new HTMLBaseGenericElement(tag, super._ir, id);
            break;
         case 5:
         default:
            domObj = new HTMLAnchor(super._ir, id);
            break;
         case 9:
            domObj = new HTMLApplet(super._ir, id);
            break;
         case 10:
            domObj = new HTMLArea(super._ir, id);
            break;
         case 12:
            domObj = new HTMLBase(super._ir, id);
            break;
         case 13:
            domObj = new HTMLBaseFont(super._ir, id);
            break;
         case 17:
            domObj = new HTMLBody(super._ir, id);
            break;
         case 18:
            domObj = new HTMLBr(super._ir, id);
            break;
         case 19:
            domObj = new HTMLButton(super._ir, id);
            break;
         case 20:
            domObj = new HTMLTableCaption(super._ir, id);
            break;
         case 24:
            domObj = new HTMLTableCol(super._ir, id);
            break;
         case 27:
         case 52:
            domObj = new HTMLMod(tag, super._ir, id);
            break;
         case 29:
            domObj = new HTMLDir(super._ir, id);
            break;
         case 30:
            domObj = new HTMLDiv(super._ir, id);
            break;
         case 31:
            domObj = new HTMLDl(super._ir, id);
            break;
         case 34:
            domObj = new HTMLFieldSet(super._ir, id);
            break;
         case 35:
            domObj = new HTMLFont(super._ir, id);
            break;
         case 36:
            domObj = new HTMLForm(super._ir, id);
            break;
         case 37:
            domObj = new HTMLFrame(super._ir, id);
            break;
         case 38:
            domObj = new HTMLFrameSet(super._ir, id);
            break;
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
            domObj = new HTMLHeading(tag, super._ir, id);
            break;
         case 45:
            domObj = new HTMLHead(super._ir, id);
            break;
         case 46:
            domObj = new HTMLHr(super._ir, id);
            break;
         case 47:
            domObj = new HTMLHtml(super._ir, id);
            break;
         case 49:
            domObj = new HTMLIFrame(super._ir, id);
            break;
         case 50:
            domObj = new HTMLImg(super._ir, id);
            break;
         case 51:
            domObj = new HTMLInput(super._ir, id);
            break;
         case 53:
            domObj = new HTMLIsIndex(super._ir, id);
            break;
         case 55:
            domObj = new HTMLLabel(super._ir, id);
            break;
         case 56:
            domObj = new HTMLLegend(super._ir, id);
            break;
         case 57:
            domObj = new HTMLLi(super._ir, id);
            break;
         case 58:
            domObj = new HTMLLink(super._ir, id);
            break;
         case 59:
            domObj = new HTMLMap(super._ir, id);
            break;
         case 60:
            domObj = new HTMLMenu(super._ir, id);
            break;
         case 61:
            domObj = new HTMLMeta(super._ir, id);
            break;
         case 64:
            domObj = new HTMLObject(super._ir, id);
            break;
         case 65:
            domObj = new HTMLOl(super._ir, id);
            break;
         case 66:
            domObj = new HTMLOptGroup(super._ir, id);
            break;
         case 67:
            domObj = new HTMLOption(super._ir, id);
            break;
         case 68:
            domObj = new HTMLParagraph(super._ir, id);
            break;
         case 69:
            domObj = new HTMLParam(super._ir, id);
            break;
         case 71:
            domObj = new HTMLPre(super._ir, id);
            break;
         case 72:
            domObj = new HTMLQuote(super._ir, id);
            break;
         case 75:
            domObj = new HTMLScript(super._ir, id);
            break;
         case 76:
            domObj = new HTMLSelect(super._ir, id);
            break;
         case 81:
            domObj = new HTMLStyle(super._ir, id);
            break;
         case 84:
            domObj = new HTMLTable(super._ir, id);
            break;
         case 85:
         case 88:
         case 90:
            domObj = new HTMLTableSection(tag, super._ir, id);
            break;
         case 86:
         case 89:
            domObj = new HTMLTableCell(tag, super._ir, id);
            break;
         case 87:
            domObj = new HTMLTextArea(super._ir, id);
            break;
         case 91:
            domObj = new HTMLTitle(super._ir, id);
            break;
         case 92:
            domObj = new HTMLTableRow(super._ir, id);
            break;
         case 95:
            domObj = new HTMLUl(super._ir, id);
      }

      super._ir.addNode(id, (HTMLNode)domObj);
      return domObj;
   }

   protected final void addElement(String name, String id, HTMLElement element) {
      if (element instanceof HTMLForm) {
         this._forms.addItem(name != null ? name : id, element);
      } else if (element instanceof HTMLImg) {
         this._images.addItem(name != null ? name : id, element);
      } else {
         if (element instanceof HTMLAnchor) {
            if (name != null) {
               this._anchors.addItem(name, element);
            } else if (id != null) {
               this._anchors.addItem(id, element);
            }

            if (element.hasAttribute("href")) {
               this._links.addItem(name != null ? name : id, element);
               return;
            }
         } else if (element instanceof HTMLArea) {
            this._links.addItem(name != null ? name : id, element);
         }
      }
   }

   final Enumeration getAllElementsEnumeration() {
      return super._ir.getAllElementsEnumeration();
   }

   public final HTMLBrowserContent getUiPeer() {
      return this._peer;
   }

   @Override
   public final String getURL() {
      return this._peer.getURL();
   }

   @Override
   public final String getReferrer() {
      return this._referrer;
   }

   @Override
   public final String getDomain() {
      return this._host;
   }

   @Override
   public final String getCookie() {
      RenderingApplication app = this._peer.getRenderingApplication();
      return app != null ? app.getHTTPCookie(this._peer.getURL()) : null;
   }

   @Override
   public final void setCookie(String cookie) {
      RenderingApplication app = this._peer.getRenderingApplication();
      if (cookie != null && app != null) {
         SetHttpCookieEvent event = (SetHttpCookieEvent)(new Object(this._peer, this.getURL(), cookie));
         app.eventOccurred(event);
      }
   }

   @Override
   public final Element getElementById(String id) {
      return (Element)super._ir.getElementById(super._node, id);
   }

   @Override
   public final DOMImplementation getImplementation() {
      return HTMLDOMImplementation.getInstance();
   }

   @Override
   public final Element getDocumentElement() {
      for (int child = super._ir.getFirstChild(super._node); child != 0; child = super._ir.getNextChild(child)) {
         if (super._ir.getNodeType(child) == 1) {
            return (Element)super._ir.getNode(child);
         }
      }

      return null;
   }

   @Override
   public final String getTitle() {
      return this._peer.getTitle();
   }

   @Override
   public final void setTitle(String title) {
      this._peer.setTitle(title);
   }

   @Override
   public final Element createElement(String tagName) {
      int tag = HTMLUtilities.resolveTag(tagName);
      return this.createElement(tag);
   }

   @Override
   public final DocumentFragment createDocumentFragment() {
      return null;
   }

   @Override
   public final Text createTextNode(String data) {
      char[] buff = data.toCharArray();
      return (Text)super._ir.getNode(super._ir.addText(3, buff, 0, buff.length));
   }

   @Override
   public final Comment createComment(String data) {
      char[] buff = data.toCharArray();
      return (Comment)super._ir.getNode(super._ir.addText(8, buff, 0, buff.length));
   }

   @Override
   public final CDATASection createCDATASection(String data) {
      throw new Object((short)9, null);
   }

   @Override
   public final ProcessingInstruction createProcessingInstruction(String target, String data) {
      throw new Object((short)9, null);
   }

   @Override
   public final Attr createAttribute(String name) {
      HTMLDOMInternalRepresentation.isQName(name);
      return (Attr)super._ir.getNode(super._ir.addAttribute("", "", name, name, "", "CDATA", false));
   }

   @Override
   public final EntityReference createEntityReference(String name) {
      HTMLDOMInternalRepresentation.isNCName(name);
      String value = super._ir.getEntityValue(name);
      HTMLNode text = null;
      if (value != null) {
         text = (HTMLNode)this.createTextNode(value);
         super._ir.setReadOnly(text._node);
      }

      HTMLNode er = super._ir.getNode(super._ir.addEntityReference(name, null, null));
      if (text != null) {
         er.appendChild(text);
         super._ir.setReadOnly(er._node);
      }

      return (EntityReference)er;
   }

   @Override
   public final Node importNode(Node importedNode, boolean deep) {
      throw new Object((short)9, null);
   }

   @Override
   public final Element createElementNS(String namespaceURI, String qualifiedName) {
      int tag = HTMLUtilities.resolveTag(qualifiedName);
      return this.createElement(tag);
   }

   @Override
   public final Attr createAttributeNS(String namespaceURI, String qualifiedName) {
      HTMLDOMInternalRepresentation.isQName(qualifiedName);
      return (Attr)super._ir.getNode(super._ir.addAttribute(namespaceURI, qualifiedName, "", "CDATA", false));
   }

   @Override
   public final NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
      return new HTMLNodeList(super._ir.getElementsByTagNameNS(super._node, namespaceURI, localName));
   }

   @Override
   public final HTMLElement getBody() {
      for (int child = super._ir.getFirstChild(super._node); child != 0; child = super._ir.getNextChild(child)) {
         if (super._ir.getNodeType(child) == 1) {
            for (int var3 = super._ir.getFirstChild(child); var3 != 0; var3 = super._ir.getNextChild(var3)) {
               if (super._ir.getNodeType(var3) == 1) {
                  Node node = super._ir.getNode(var3);
                  if (node instanceof HTMLBodyElement || node instanceof HTMLFrameSetElement) {
                     return (HTMLElement)node;
                  }
               }
            }
            break;
         }
      }

      return null;
   }

   @Override
   public final void setBody(HTMLElement body) {
   }

   @Override
   public final HTMLCollection getImages() {
      return this._images;
   }

   @Override
   public final HTMLCollection getApplets() {
      return this._applets;
   }

   @Override
   public final HTMLCollection getLinks() {
      return this._links;
   }

   @Override
   public final HTMLCollection getForms() {
      return this._forms;
   }

   @Override
   public final HTMLCollection getAnchors() {
      return this._anchors;
   }

   @Override
   public final void open() {
   }

   @Override
   public final void close() {
   }

   @Override
   public final void write(String text) {
   }

   @Override
   public final void writeln(String text) {
   }

   @Override
   public final NodeList getElementsByName(String elementName) {
      return new HTMLNodeList(super._ir.getElementsByName(super._node, elementName));
   }

   @Override
   public final NodeList getElementsByTagName(String tagName) {
      return new HTMLNodeList(super._ir.getElementsByTagName(super._node, tagName));
   }

   @Override
   public final DocumentType getDoctype() {
      return super._ir.getDocumentType();
   }

   @Override
   public final String getNodeName() {
      return "#document";
   }

   HTMLDocumentImpl(String referrer, String host, HTMLBrowserContent peer) {
      super(new HTMLDOMInternalRepresentation(), 0);
      this._referrer = referrer;
      this._host = host;
      super._node = super._ir.addDocument(this);
      this._forms = new HTMLCollectionImpl();
      this._images = new HTMLCollectionImpl();
      this._anchors = new HTMLCollectionImpl();
      this._links = new HTMLCollectionImpl();
      this._applets = new HTMLCollectionImpl();
      this._peer = peer;
   }
}
