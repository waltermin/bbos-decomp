package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.Node;

class ESNode extends RedirectedObject {
   private Node _node;

   ESNode(Node node) {
      this(node, Names.Node);
   }

   ESNode(Node node, String name) {
      this(node, name, JavaScriptEngine.getInstance()._nodePrototype);
   }

   ESNode(Node node, String name, ESObject prototype) {
      this(node, name, prototype, false);
   }

   ESNode(Node node, String name, ESObject prototype, boolean nullOk) {
      super(name, prototype, nullOk);
      this._node = node;
   }

   Node getNode() {
      return this._node;
   }

   static Node getNode(long paramValue) throws ThrownValue {
      Object obj = Convert.toObject(paramValue);
      if (!(obj instanceof ESNode)) {
         throw ThrownValue.referenceError("Parm not a node");
      } else {
         return ((ESNode)obj).getNode();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long requestFieldValue(String name) throws ThrownValue {
      if (name == Names.nodeName) {
         return JavaScriptEngine.makeStringValue(this._node.getNodeName());
      }

      if (name == Names.nodeValue) {
         try {
            return JavaScriptEngine.makeStringValue(this._node.getNodeValue());
         } catch (Throwable var4) {
            throw ESDOMException.createThrownValue(e);
         }
      } else if (name == Names.nodeType) {
         return Value.makeIntegerValue(this._node.getNodeType());
      } else if (name == Names.parentNode) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this._node.getParentNode());
      } else if (name == Names.childNodes) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this._node.getChildNodes());
      } else if (name == Names.firstChild) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this._node.getFirstChild());
      } else if (name == Names.lastChild) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this._node.getLastChild());
      } else if (name == Names.previousSibling) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this._node.getPreviousSibling());
      } else if (name == Names.nextSibling) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this._node.getNextSibling());
      } else if (name == Names.attributes) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this._node.getAttributes());
      } else if (name == Names.ownerDocument) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this._node.getOwnerDocument());
      } else if (name == Names.namespaceURI) {
         return JavaScriptEngine.makeStringValue(this._node.getNamespaceURI());
      } else if (name == Names.prefix) {
         return JavaScriptEngine.makeStringValue(this._node.getPrefix());
      } else {
         return name == Names.localName ? JavaScriptEngine.makeStringValue(this._node.getLocalName()) : Value.DEFAULT;
      }
   }
}
