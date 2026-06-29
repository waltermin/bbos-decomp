package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html2.HTMLAnchorElement;

public final class SimpleHTMLAnchorElement implements HTMLAnchorElement {
   private String _url;

   public SimpleHTMLAnchorElement(String url) {
      this._url = url;
   }

   @Override
   public final String getAccessKey() {
      return null;
   }

   @Override
   public final void setAccessKey(String accessKey) {
   }

   @Override
   public final String getCharset() {
      return null;
   }

   @Override
   public final void setCharset(String charset) {
   }

   @Override
   public final String getCoords() {
      return null;
   }

   @Override
   public final void setCoords(String coords) {
   }

   @Override
   public final String getHref() {
      return this._url;
   }

   @Override
   public final void setHref(String href) {
      this._url = href;
   }

   @Override
   public final String getHreflang() {
      return null;
   }

   @Override
   public final void setHreflang(String hreflang) {
   }

   @Override
   public final String getName() {
      return null;
   }

   @Override
   public final void setName(String name) {
   }

   @Override
   public final String getRel() {
      return null;
   }

   @Override
   public final void setRel(String rel) {
   }

   @Override
   public final String getRev() {
      return null;
   }

   @Override
   public final void setRev(String rev) {
   }

   @Override
   public final String getShape() {
      return null;
   }

   @Override
   public final void setShape(String shape) {
   }

   @Override
   public final int getTabIndex() {
      return -1;
   }

   @Override
   public final void setTabIndex(int tabIndex) {
   }

   @Override
   public final String getTarget() {
      return null;
   }

   @Override
   public final void setTarget(String target) {
   }

   @Override
   public final String getType() {
      return null;
   }

   @Override
   public final void setType(String type) {
   }

   @Override
   public final void blur() {
   }

   @Override
   public final void focus() {
   }

   @Override
   public final String getId() {
      return null;
   }

   @Override
   public final void setId(String id) {
   }

   @Override
   public final String getTitle() {
      return null;
   }

   @Override
   public final void setTitle(String title) {
   }

   @Override
   public final String getLang() {
      return null;
   }

   @Override
   public final void setLang(String lang) {
   }

   @Override
   public final String getDir() {
      return null;
   }

   @Override
   public final void setDir(String dir) {
   }

   @Override
   public final String getClassName() {
      return null;
   }

   @Override
   public final void setClassName(String className) {
   }

   @Override
   public final String getTagName() {
      return "a";
   }

   @Override
   public final String getAttribute(String name) {
      return "href".equals(name) ? this.getHref() : null;
   }

   @Override
   public final void setAttribute(String name, String value) {
      if ("href".equals(name)) {
         this.setHref(value);
      }
   }

   @Override
   public final void removeAttribute(String name) {
      if ("href".equals(name)) {
         this.setHref(null);
      }
   }

   @Override
   public final Attr getAttributeNode(String name) {
      return null;
   }

   @Override
   public final Attr setAttributeNode(Attr newAttr) {
      return null;
   }

   @Override
   public final Attr removeAttributeNode(Attr oldAttr) {
      return null;
   }

   @Override
   public final NodeList getElementsByTagName(String name) {
      return null;
   }

   @Override
   public final String getAttributeNS(String namespaceURI, String localName) {
      return null;
   }

   @Override
   public final void setAttributeNS(String namespaceURI, String qualifiedName, String value) {
   }

   @Override
   public final void removeAttributeNS(String namespaceURI, String localName) {
   }

   @Override
   public final Attr getAttributeNodeNS(String namespaceURI, String localName) {
      return null;
   }

   @Override
   public final Attr setAttributeNodeNS(Attr newAttr) {
      return null;
   }

   @Override
   public final NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
      return null;
   }

   @Override
   public final boolean hasAttribute(String name) {
      return this.getHref() != null && "href".equals(name);
   }

   @Override
   public final boolean hasAttributeNS(String namespaceURI, String localName) {
      return false;
   }

   @Override
   public final String getNodeName() {
      return this.getTagName();
   }

   @Override
   public final String getNodeValue() {
      return null;
   }

   @Override
   public final void setNodeValue(String nodeValue) {
   }

   @Override
   public final short getNodeType() {
      return 1;
   }

   @Override
   public final Node getParentNode() {
      return null;
   }

   @Override
   public final NodeList getChildNodes() {
      return null;
   }

   @Override
   public final Node getFirstChild() {
      return null;
   }

   @Override
   public final Node getLastChild() {
      return null;
   }

   @Override
   public final Node getPreviousSibling() {
      return null;
   }

   @Override
   public final Node getNextSibling() {
      return null;
   }

   @Override
   public final NamedNodeMap getAttributes() {
      return null;
   }

   @Override
   public final Document getOwnerDocument() {
      return null;
   }

   @Override
   public final Node insertBefore(Node newChild, Node refChild) {
      return null;
   }

   @Override
   public final Node replaceChild(Node newChild, Node oldChild) {
      return null;
   }

   @Override
   public final Node removeChild(Node oldChild) {
      return null;
   }

   @Override
   public final Node appendChild(Node newChild) {
      return null;
   }

   @Override
   public final boolean hasChildNodes() {
      return false;
   }

   @Override
   public final Node cloneNode(boolean deep) {
      return null;
   }

   @Override
   public final void normalize() {
   }

   @Override
   public final boolean isSupported(String feature, String version) {
      return false;
   }

   @Override
   public final String getNamespaceURI() {
      return null;
   }

   @Override
   public final String getPrefix() {
      return null;
   }

   @Override
   public final void setPrefix(String prefix) {
   }

   @Override
   public final String getLocalName() {
      return null;
   }

   @Override
   public final boolean hasAttributes() {
      return false;
   }
}
