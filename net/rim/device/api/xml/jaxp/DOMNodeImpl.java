package net.rim.device.api.xml.jaxp;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMNodeImpl implements Node {
   protected DOMInternalRepresentation _ir;
   protected int _node;

   DOMNodeImpl(DOMInternalRepresentation ir, int node) {
      this._ir = ir;
      this._node = node;
   }

   @Override
   public String toString() {
      String value = this.getNodeValue();
      if (value != null) {
         return value;
      }

      String name = this.getNodeName();
      return value != null ? name : super.toString();
   }

   DOMInternalRepresentation getIR() {
      return this._ir;
   }

   int getNode() {
      return this._node;
   }

   @Override
   public String getNodeName() {
      throw null;
   }

   @Override
   public String getNodeValue() {
      return null;
   }

   @Override
   public void setNodeValue(String nodeValue) {
      this._ir.notReadOnly(this._node);
   }

   @Override
   public short getNodeType() {
      return (short)this._ir.getNodeType(this._node);
   }

   @Override
   public Node getParentNode() {
      return this._ir.getNode(this._ir.getParent(this._node));
   }

   @Override
   public NodeList getChildNodes() {
      return new DOMChildList(this);
   }

   @Override
   public Node getFirstChild() {
      return this._ir.getNode(this._ir.getFirstChild(this._node));
   }

   @Override
   public Node getLastChild() {
      return this._ir.getNode(this._ir.getLastChild(this._node));
   }

   @Override
   public Node getPreviousSibling() {
      return this._ir.getNode(this._ir.getPreviousChild(this._node));
   }

   @Override
   public Node getNextSibling() {
      return this._ir.getNode(this._ir.getNextChild(this._node));
   }

   @Override
   public NamedNodeMap getAttributes() {
      return null;
   }

   @Override
   public Document getOwnerDocument() {
      for (int node = this._ir.getParent(this._node); node != 0; node = this._ir.getParent(node)) {
         if (this._ir.getNodeType(node) == 9) {
            return (Document)this._ir.getNode(node);
         }
      }

      return null;
   }

   @Override
   public Node insertBefore(Node nNewChild, Node nRefChild) {
      DOMNodeImpl newChild = (DOMNodeImpl)nNewChild;
      DOMNodeImpl refChild = (DOMNodeImpl)nRefChild;
      int iNewChild = newChild._node;
      if (this._ir != newChild._ir) {
         throw new DOMException((short)4, "");
      }

      this._ir.notReadOnly(this._node);
      this._ir.notReadOnly(this._ir.getParent(iNewChild));
      if (refChild == null) {
         this.appendChild(newChild);
         return newChild;
      }

      int iRefChild = refChild._node;
      if (nNewChild == nRefChild) {
         return nNewChild;
      }

      if (this._ir.isLeaf(this._node) || this._ir.isParentOfOrSelf(iNewChild, this._node)) {
         throw new DOMException((short)3, "");
      }

      if (!this._ir.isParentOf(this._node, iRefChild)) {
         throw new DOMException((short)8, "");
      }

      if (!this._ir.allowsChild(this._node, iNewChild)) {
         throw new DOMException((short)3, "");
      }

      if (this._ir.getNodeType(iNewChild) != 11) {
         this._ir.unlink(iNewChild);
         this._ir.insertBefore(this._node, iNewChild, iRefChild);
         return newChild;
      }

      int child = this._ir.getFirstChild(iNewChild);

      while (child != 0) {
         int next = this._ir.getNextChild(child);
         this._ir.insertBefore(this._node, child, iRefChild);
         child = next;
      }

      this._ir.setFirstChild(iNewChild, 0);
      return newChild;
   }

   @Override
   public Node replaceChild(Node nNewChild, Node nOldChild) {
      DOMNodeImpl newChild = (DOMNodeImpl)nNewChild;
      DOMNodeImpl oldChild = (DOMNodeImpl)nOldChild;
      int iOldChild = oldChild._node;
      int iNewChild = newChild._node;
      if (this._ir != newChild._ir) {
         throw new DOMException((short)4, "");
      }

      this._ir.notReadOnly(this._node);
      this._ir.notReadOnly(this._ir.getParent(iNewChild));
      if (newChild == oldChild) {
         return newChild;
      }

      if (!this._ir.isParentOf(this._node, iOldChild)) {
         throw new DOMException((short)8, "");
      }

      if (this._ir.isLeaf(this._node) || this._ir.isParentOfOrSelf(iNewChild, this._node)) {
         throw new DOMException((short)3, "");
      }

      if (!this._ir.allowsChild(this._node, iNewChild)) {
         throw new DOMException((short)3, "");
      }

      if (this._ir.getNodeType(iNewChild) != 11) {
         this._ir.unlink(iNewChild);
         this._ir.replaceChild(this._node, iOldChild, iNewChild);
         return nOldChild;
      }

      int child = this._ir.getFirstChild(iNewChild);
      if (child == 0) {
         this._ir.unlink(iOldChild);
         return nOldChild;
      }

      int next = this._ir.getNextChild(child);
      this._ir.replaceChild(this._node, iOldChild, child);
      iOldChild = child;
      child = next;

      while (child != 0) {
         next = this._ir.getNextChild(child);
         this._ir.insertAfter(this._node, child, iOldChild);
         child = next;
      }

      this._ir.setFirstChild(iNewChild, 0);
      return nOldChild;
   }

   @Override
   public Node removeChild(Node nOldChild) {
      this._ir.notReadOnly(this._node);
      DOMNodeImpl oldChild = (DOMNodeImpl)nOldChild;
      if (!this._ir.isParentOf(this._node, oldChild._node)) {
         throw new DOMException((short)8, "");
      }

      this._ir.removeChild(this._node, oldChild._node);
      return oldChild;
   }

   @Override
   public Node appendChild(Node nNewChild) {
      DOMNodeImpl newChild = (DOMNodeImpl)nNewChild;
      int iNewChild = newChild._node;
      if (this._ir != newChild._ir) {
         throw new DOMException((short)4, "");
      }

      this._ir.notReadOnly(this._node);
      this._ir.notReadOnly(this._ir.getParent(iNewChild));
      if (!this._ir.allowsChild(this._node, iNewChild)) {
         throw new DOMException((short)3, "");
      }

      if (this._ir.isParentOfOrSelf(iNewChild, this._node)) {
         throw new DOMException((short)3, "");
      }

      if (this._ir.getNodeType(iNewChild) != 11) {
         this._ir.unlink(iNewChild);
         this._ir.appendChild(this._node, iNewChild);
         return newChild;
      }

      int child = this._ir.getFirstChild(iNewChild);

      while (child != 0) {
         int next = this._ir.getNextChild(child);
         this._ir.appendChild(this._node, child);
         child = next;
      }

      this._ir.setFirstChild(iNewChild, 0);
      return newChild;
   }

   @Override
   public boolean hasChildNodes() {
      return this._ir.getFirstChild(this._node) != 0;
   }

   @Override
   public Node cloneNode(boolean deep) {
      return this._ir.getNode(this._ir.cloneNode(this._node, deep));
   }

   @Override
   public void normalize() {
      this._ir.normalize(this._node);
   }

   @Override
   public boolean isSupported(String feature, String version) {
      return DOMInternalRepresentation.hasFeature(feature, version);
   }

   @Override
   public String getNamespaceURI() {
      return null;
   }

   @Override
   public String getPrefix() {
      return null;
   }

   @Override
   public void setPrefix(String prefix) {
      this._ir.notReadOnly(this._node);
      DOMInternalRepresentation.isNCName(prefix);
   }

   @Override
   public String getLocalName() {
      return null;
   }

   @Override
   public boolean hasAttributes() {
      return false;
   }
}
