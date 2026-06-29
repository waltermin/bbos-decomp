package net.rim.device.apps.internal.browser.html;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.IntLongHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.device.apps.internal.browser.util.RendererControl;
import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.html2.HTMLDocument;

public class HTMLDOMInternalRepresentation {
   private KnownKeysStringToIndex _names = new KnownKeysStringToIndex();
   private NameIdMultimap _nameIdMap = new NameIdMultimap();
   private IntIntHashtable _qnameToPrefix = (IntIntHashtable)(new Object());
   private IntIntHashtable _qnameToLocalName = (IntIntHashtable)(new Object());
   private ObjectToIndex _attrTypes = new ObjectToIndex();
   private ObjectToIndex _miscStringData = new ObjectToIndex();
   private ObjectToIndex _entityToIndex = new ObjectToIndex();
   private ObjectToIndex _dtdToIndex = new ObjectToIndex();
   private ResizableTextArray _text = new ResizableTextArray();
   private Vector _attrValues = (Vector)(new Object());
   private IntVector _attrIntValues = (IntVector)(new Object());
   private IntVector _notations = (IntVector)(new Object());
   private ToIntHashtable _notationHash = (ToIntHashtable)(new Object());
   private IntVector _entities = (IntVector)(new Object());
   private ToIntHashtable _entityHash = (ToIntHashtable)(new Object());
   private Hashtable _internalEntities = (Hashtable)(new Object());
   private Hashtable _defaultAttributes = (Hashtable)(new Object());
   private IntIntHashtable _elementNamespaces = (IntIntHashtable)(new Object());
   private IntHashtable _nodeList = (IntHashtable)(new Object());
   private int _dtdNode = 0;
   private int _document = 0;
   private boolean _expandingEntities = true;
   private boolean _coalescing = false;
   private ResizableIndexArrayWithFreeList _freeList = new ResizableIndexArrayWithFreeList();
   private ResizableIndexArray _parents = this._freeList;
   private ResizableIndexArray _types = new ResizableIndexArray();
   private ResizableIndexArray _firstChild = new ResizableIndexArray();
   private ResizableIndexArray _nextChild = new ResizableIndexArray();
   private ResizableIndexArray[] _data = new ResizableIndexArray[]{new ResizableIndexArray(), new ResizableIndexArray(), new ResizableIndexArray()};
   private ResizableIndexArray _entityReferenceNameData = this._data[0];
   private ResizableIndexArray _entityReferenceSystemIdData = this._data[1];
   private ResizableIndexArray _entityReferencePublicIdData = this._data[2];
   private ResizableIndexArray _entityData = this._data[0];
   private ResizableIndexArray _notationNameData = this._data[0];
   private ResizableIndexArray _notationPublicIdData = this._data[1];
   private ResizableIndexArray _notationSystemIdData = this._data[2];
   private ResizableIndexArray _documentTypeData = this._data[0];
   private ResizableIndexArray _processingInstructionTargetData = this._data[0];
   private ResizableIndexArray _processingInstructionDataData = this._data[1];
   private ResizableIndexArray _attributeQNameData = this._data[0];
   private ResizableIndexArray _attributeURIData = this._data[1];
   private ResizableIndexArray _attributeValueData = this._data[2];
   private ResizableIndexArray _textHandleData = this._data[0];
   private ResizableIndexArray _textLengthData = this._data[1];
   private ResizableIndexArray _namespaceURIData = this._data[0];
   private ResizableIndexArray _namespacePrefixData = this._data[1];
   private ResizableIndexArray _namespaceNextData = this._data[2];
   private ResizableIndexArray _elementQNameData = this._data[0];
   private ResizableIndexArray _elementURIData = this._data[1];
   private ResizableIndexArray _elementAttributesData = this._data[2];
   private IntLongHashtable _elementAttributeBitSet1 = (IntLongHashtable)(new Object());
   private IntLongHashtable _elementAttributeBitSet2 = (IntLongHashtable)(new Object());
   String _prefix;
   String _localName;
   int _numElementsDumped;
   int _numAttributesDumped;
   int _numTextsDumped;
   int _numPIsDumped;
   int _numDocFragsDumped;
   int _numDocumentsDumped;
   int _numDTDsDumped;
   int _numNotationsDumped;
   int _numEntitiesDumped;
   int _numEntRefsDumped;
   public static final boolean debug;
   private static final int TYPE;
   private static final int PARENT;
   private static final int FIRST_CHILD;
   private static final int NEXT_CHILD;
   private static final int DATA;
   static final int NO_HANDLE;
   private static final int READONLY;
   private static final int DEFAULT_ATTRIBUTE;
   private static final int IN_DTD;
   private static final int REACHABLE;
   private static final int FLAG_MASK;
   private static final int ATTRIBUTE_TYPE_MASK;
   private static final int ATTRIBUTE_TYPE_SHIFT;
   private static final int ELEMENT_NODE;
   private static final int ATTRIBUTE_NODE;
   private static final int TEXT_NODE;
   private static final int CDATA_SECTION_NODE;
   private static final int ENTITY_REFERENCE_NODE;
   private static final int ENTITY_NODE;
   private static final int PROCESSING_INSTRUCTION_NODE;
   private static final int COMMENT_NODE;
   private static final int DOCUMENT_NODE;
   private static final int DOCUMENT_TYPE_NODE;
   private static final int DOCUMENT_FRAGMENT_NODE;
   private static final int NOTATION_NODE;
   private static final int NAMESPACE_NODE;
   private static final int TYPE_MASK;

   HTMLDOMInternalRepresentation() {
      this._freeList.addParallelArray(this._types);
      this._freeList.addParallelArray(this._firstChild);
      this._freeList.addParallelArray(this._nextChild);
      this._freeList.addParallelArray(this._data[0]);
      this._freeList.addParallelArray(this._data[1]);
      this._freeList.addParallelArray(this._data[2]);
   }

   public void setCoalescing(boolean coalescing) {
      this._coalescing = coalescing;
   }

   public void setExpandingEntities(boolean expandingEntities) {
      this._expandingEntities = expandingEntities;
   }

   public boolean getCoalescing() {
      return this._coalescing;
   }

   public boolean getExpandingEntities() {
      return this._expandingEntities;
   }

   static boolean hasFeature(String feature, String version) {
      if (version == null || version.length() == 0 || version.equals("1.0") || version.equals("2.0")) {
         feature = StringUtilities.toLowerCase(feature, 1701707776);
         if (feature.equals("core") || feature.equals("xml")) {
            return true;
         }
      }

      return false;
   }

   int mapAttributeId(int attribId) {
      return this._names.mapAttribId(attribId);
   }

   void parseQName(String qName) {
      int colon = qName.indexOf(58);
      if (colon == -1) {
         this._prefix = "";
         this._localName = qName;
      } else {
         this._prefix = qName.substring(0, colon);
         this._localName = qName.substring(colon + 1);
      }
   }

   int newNode() {
      return this._freeList.findFreeSlot();
   }

   private void shrink() {
      this._freeList.shrink();
      this._text.shrink();
   }

   int addNamespace(String uri, String prefix, int next) {
      return this.addNamespace(this._names.append(uri), this._names.append(prefix), next);
   }

   int addNamespace(int uri, int prefix, int next) {
      int node = this.newNode();
      this._types.set(node, 13);
      this._parents.set(node, 0);
      this._firstChild.set(node, 0);
      this._nextChild.set(node, 0);
      this._namespaceURIData.set(node, uri);
      this._namespacePrefixData.set(node, prefix);
      this._namespaceNextData.set(node, next);
      return node;
   }

   String getNamespacePrefix(int ns) {
      return (String)this._names.get(this._namespacePrefixData.get(ns));
   }

   String getNamespaceURI(int ns) {
      return (String)this._names.get(this._namespaceURIData.get(ns));
   }

   int getNamespaceNext(int ns) {
      return this._namespaceNextData.get(ns);
   }

   int addElement(String uri, String qName) {
      this.parseQName(qName);
      return this.addElement(uri, this._prefix, this._localName, qName);
   }

   int addElement(String uri, String prefix, String localName, String qName) {
      if (qName.length() == 0) {
         qName = localName;
      }

      return this.addElement(this._names.append(uri), this._names.append(prefix), this._names.append(localName), this._names.append(qName));
   }

   int addElement(int tagId) {
      int nameIndex = this._names.mapTagId(tagId);
      return this.addElement(-2, -2, nameIndex, nameIndex);
   }

   int addElement(int uri, int prefix, int localName, int qName) {
      this._qnameToPrefix.put(qName, prefix);
      this._qnameToLocalName.put(qName, localName);
      int node = this.newNode();
      this._types.set(node, 1);
      this._parents.set(node, 0);
      this._firstChild.set(node, 0);
      this._nextChild.set(node, 0);
      this._elementQNameData.set(node, qName);
      this._elementURIData.set(node, uri);
      this._elementAttributesData.set(node, 0);
      return node;
   }

   String getElementLocalName(int node) {
      return (String)this._names.get(this._qnameToLocalName.get(this._elementQNameData.get(node)));
   }

   String getElementPrefix(int node) {
      return (String)this._names.get(this._qnameToPrefix.get(this._elementQNameData.get(node)));
   }

   String getElementQName(int node) {
      return (String)this._names.get(this._elementQNameData.get(node));
   }

   void setElementQName(int node, String qName) {
      this.setElementQName(node, this._names.append(qName));
   }

   void setElementQName(int node, int qName) {
      this._elementQNameData.set(node, qName);
   }

   String getElementURI(int node) {
      return (String)this._names.get(this._elementURIData.get(node));
   }

   int getElementAttributes(int node) {
      return this._elementAttributesData.get(node);
   }

   void setElementAttributes(int node, int attr) {
      this._elementAttributesData.set(node, attr);
   }

   int getElementNamespaces(int node) {
      int rc = this._elementNamespaces.get(node);
      return rc == -1 ? 0 : rc;
   }

   void setElementNamespaces(int node, int namespaces) {
      this._elementNamespaces.put(node, namespaces);
   }

   int getAttributes(int node) {
      return this.getType(node) != 1 ? 0 : this._elementAttributesData.get(node);
   }

   int addText(int type, char[] buff, int start, int length) {
      int data = this._text.newNode();
      this._text.append(buff, start, length);
      int node = this.newNode();
      this._types.set(node, type);
      this._parents.set(node, 0);
      this._firstChild.set(node, 0);
      this._nextChild.set(node, 0);
      this._textHandleData.set(node, data);
      this._textLengthData.set(node, length);
      return node;
   }

   int getTextHandle(int node) {
      return this._textHandleData.get(node);
   }

   void setTextHandle(int node, int handle) {
      this._textHandleData.set(node, handle);
   }

   int getTextLength(int node) {
      return this._textLengthData.get(node);
   }

   void setTextLength(int node, int length) {
      this._textLengthData.set(node, length);
   }

   String getTextString(int node) {
      return this._text.makeString(this.getTextHandle(node), this.getTextLength(node));
   }

   String getTextSubstring(int node, int offset, int count) {
      int len = this.getTextLength(node);
      if (offset >= 0 && offset <= len && count >= 0) {
         if (offset + count > len) {
            count = len - offset;
         }

         return count == 0 ? "" : this._text.makeSubstring(this.getTextHandle(node), offset, count);
      } else {
         throw new Object();
      }
   }

   int splitText(int handle, int splitIndex) {
      int textLength = this.getTextLength(handle);
      if (splitIndex > textLength) {
         return 0;
      }

      if (splitIndex < 0) {
         return 0;
      }

      this.setTextLength(handle, splitIndex);
      int newHandle = this.newNode();
      this._types.set(newHandle, this.getType(handle));
      this._parents.set(newHandle, 0);
      this._firstChild.set(newHandle, 0);
      this._nextChild.set(newHandle, 0);
      this._textHandleData.set(newHandle, this._text.splitHandle(this.getTextHandle(handle), splitIndex));
      this._textLengthData.set(newHandle, textLength - splitIndex);
      int parent = this.getParent(handle);
      if (parent == 0) {
         return newHandle;
      }

      this.insertAfter(parent, newHandle, handle);
      return newHandle;
   }

   void setTextData(int handle, String str) {
      this.replaceTextData(handle, 0, this.getTextLength(handle), str);
   }

   void appendTextData(int handle, String str) {
      this.replaceTextData(handle, this.getTextLength(handle), 0, str);
   }

   boolean insertTextData(int handle, int offset, String str) {
      return this.replaceTextData(handle, offset, 0, str);
   }

   boolean deleteTextData(int handle, int offset, int count) {
      return this.replaceTextData(handle, offset, count, null);
   }

   boolean replaceTextData(int handle, int offset, int count, String replacement) {
      int oldLength = this.getTextLength(handle);
      if (offset < 0 || offset > oldLength) {
         return false;
      }

      if (count < 0) {
         return false;
      }

      int endOffset = offset + count;
      if (endOffset > oldLength) {
         endOffset = oldLength;
      }

      int oldDataHandle = this.getTextHandle(handle);
      int newDataHandle = this._text.newNode();
      this._text.append(this._text.makeSubstring(oldDataHandle, 0, offset));
      int replacementLength = 0;
      if (replacement != null) {
         replacementLength = replacement.length();
         this._text.append(replacement);
      }

      this._text.append(this._text.makeSubstring(oldDataHandle, endOffset, oldLength - endOffset));
      this.setTextHandle(handle, newDataHandle);
      this.setTextLength(handle, offset + (oldLength - endOffset) + replacementLength);
      return true;
   }

   int addDocument(HTMLDocument doc) {
      int node = this._document = this.newNode();
      this._types.set(node, 9);
      this._parents.set(node, 0);
      this._firstChild.set(node, 0);
      this._nextChild.set(node, 0);
      this._nodeList.put(node, doc);
      return node;
   }

   void addNode(int id, HTMLNode node) {
      this._nodeList.put(id, node);
   }

   int getDocument() {
      return this._document;
   }

   int addAttribute(String uri, String qName, String value, String type, boolean isDefault) {
      this.parseQName(qName);
      return this.addAttribute(uri, this._prefix, this._localName, qName, value, type, isDefault);
   }

   private void fixupAttributeValues(int node) {
      IntIntHashtable attrMap = (IntIntHashtable)(new Object());
      this.fixupAttributeValues(node, attrMap);
   }

   private void fixupAttributeValues(int node, IntIntHashtable attrMap) {
      switch (this.getType(node)) {
         case 1:
            for (int child = this.getElementAttributes(node); child != 0; child = this.getNextChild(child)) {
               this.fixupAttributeValues(child, attrMap);
            }
         case 0:
            for (int child = this.getFirstChild(node); child != 0; child = this.getNextChild(child)) {
               this.fixupAttributeValues(child, attrMap);
            }

            return;
         case 2:
         default:
            int oldValue = this._attributeValueData.get(node);
            int newValue = attrMap.get(oldValue);
            if (newValue == -1) {
               newValue = this._attrValues.size();
               this._attrValues.addElement(this._attrValues.elementAt(oldValue));
               attrMap.put(oldValue, newValue);
            }

            this._attributeValueData.set(node, newValue);
      }
   }

   int addAttribute(String uri, String prefix, String localName, String qName, String value, String type, boolean isDefault) {
      return this.addAttribute(uri, prefix, this._names.append(localName), this._names.append(qName), value, this._attrTypes.append(type), isDefault);
   }

   int addAttribute(String uri, String prefix, String localName, String qName, String value, int typeId, boolean isDefault) {
      return this.addAttribute(uri, prefix, this._names.append(localName), this._names.append(qName), value, typeId, isDefault);
   }

   int addAttribute(String uri, String prefix, int localNameId, int qNameId, String value, int typeId, boolean isDefault) {
      int attrValue = this._attrValues.size();
      this._attrValues.addElement(value);
      int node = this.addAttribute(this._names.append(uri), this._names.append(prefix), localNameId, qNameId, attrValue, typeId);
      if (isDefault) {
         this.setAttributeIsDefault(node);
      }

      return node;
   }

   int addAttribute(String uri, String prefix, int localName, int qName, int value, int typeId, boolean isDefault) {
      this._attrIntValues.addElement(value);
      int attrValue = this._attrIntValues.size() - 1 | -2147483648;
      int node = this.addAttribute(this._names.append(uri), this._names.append(prefix), localName, qName, attrValue, typeId);
      if (isDefault) {
         this.setAttributeIsDefault(node);
      }

      return node;
   }

   int addAttribute(int uri, int prefix, int localName, int qName, int value, int type) {
      this._qnameToPrefix.put(qName, prefix);
      this._qnameToLocalName.put(qName, localName);
      int tag = 2;
      int node = this.newNode();
      tag |= type << 8;
      this._types.set(node, tag);
      this._parents.set(node, 0);
      this._firstChild.set(node, 0);
      this._nextChild.set(node, 0);
      this._attributeQNameData.set(node, qName);
      this._attributeURIData.set(node, uri);
      this._attributeValueData.set(node, value);
      return node;
   }

   int getAttribute(int node, String name) {
      int idName = this._names.append(name);
      return this.getAttribute(node, idName);
   }

   int getAttribute(int node, int idName) {
      if (idName >= 256 && idName < 320) {
         long attribsSet = this._elementAttributeBitSet1.get(node);
         if (attribsSet == -1 || (attribsSet >> idName - 256 & 1) == 0) {
            return 0;
         }
      } else if (idName >= 320 && idName < 384) {
         long attribsSet = this._elementAttributeBitSet2.get(node);
         if (attribsSet == -1 || (attribsSet >> idName - 256 - 64 & 1) == 0) {
            return 0;
         }
      }

      for (int child = this.getAttributes(node); child != 0; child = this.getNextChild(child)) {
         if (this._attributeQNameData.get(child) == idName) {
            return child;
         }
      }

      return 0;
   }

   int getAttributeNS(int node, String uri, String localName) {
      int idURI = this._names.append(uri);
      int idLocalName = this._names.append(localName);

      for (int child = this.getAttributes(node); child != 0; child = this.getNextChild(child)) {
         if (this.getType(child) != 2) {
            return 0;
         }

         if (this._attributeURIData.get(child) == idURI && this._qnameToLocalName.get(this._attributeQNameData.get(child)) == idLocalName) {
            return child;
         }
      }

      return 0;
   }

   IntVector addDefaultAttributes(String elementQName) {
      Hashtable attributeHash = (Hashtable)this._defaultAttributes.get(elementQName);
      if (attributeHash == null) {
         return null;
      }

      IntVector iv = (IntVector)(new Object());
      Enumeration e = attributeHash.keys();

      while (e.hasMoreElements()) {
         String attributeName = (String)e.nextElement();
         String attributeValue = (String)attributeHash.get(attributeName);
         iv.addElement(this.addAttribute("", "", attributeName, attributeName, attributeValue, -2, true));
      }

      return iv;
   }

   void replaceAttribute(int element, int oldAttr, int newAttr) {
      int currAttr = this.getElementAttributes(element);
      if (currAttr == oldAttr) {
         this.setElementAttributes(element, newAttr);
      } else {
         while (true) {
            int nextAttr = this.getNextChild(currAttr);
            if (nextAttr == oldAttr) {
               this.setNextChild(currAttr, newAttr);
               break;
            }

            currAttr = nextAttr;
         }
      }

      this.setNextChild(newAttr, this.getNextChild(oldAttr));
      this.setParent(newAttr, element);
      this.setParent(oldAttr, 0);
   }

   void removeAttribute(int element, int oldAttr) {
      int currAttr = this.getElementAttributes(element);
      if (currAttr == oldAttr) {
         this.setElementAttributes(element, this.getNextChild(oldAttr));
      } else {
         while (true) {
            int nextAttr = this.getNextChild(currAttr);
            if (nextAttr == oldAttr) {
               this.setNextChild(currAttr, this.getNextChild(oldAttr));
               break;
            }

            currAttr = nextAttr;
         }
      }

      this.setParent(oldAttr, 0);
   }

   int removeAttributeNode(int element, int oldAttr) {
      String defaultValue = null;
      Hashtable attributeHash = (Hashtable)this._defaultAttributes.get(this.getElementQName(element));
      if (attributeHash != null) {
         defaultValue = (String)attributeHash.get(this.getAttributeQName(oldAttr));
      }

      if (defaultValue != null) {
         int newAttr = this.cloneNode(oldAttr, false);
         this.setAttributeValue(newAttr, defaultValue);
         this.setAttributeIsDefault(newAttr);
         this.replaceAttribute(element, oldAttr, newAttr);
         return oldAttr;
      } else {
         this.removeAttribute(element, oldAttr);
         return oldAttr;
      }
   }

   int removeAttribute(int element, String name) {
      int attr = this.getAttribute(element, name);
      if (attr == 0) {
         return 0;
      }

      this.removeAttributeNode(element, attr);
      return attr;
   }

   int removeAttributeNS(int element, String uri, String localName) {
      int attr = this.getAttributeNS(element, uri, localName);
      if (attr == 0) {
         return 0;
      }

      this.removeAttributeNode(element, attr);
      return attr;
   }

   void insertAttribute(int element, int attr, int idName) {
      this.setNextChild(attr, this.getElementAttributes(element));
      this.setElementAttributes(element, attr);
      this.setParent(attr, element);
      if (idName >= 256 && idName < 320) {
         long attribsSet = this._elementAttributeBitSet1.get(element);
         if (attribsSet == -1) {
            attribsSet = 0;
         }

         this._elementAttributeBitSet1.put(element, attribsSet | (long)1 << idName - 256);
      } else {
         if (idName >= 320 && idName < 384) {
            long attribsSet = this._elementAttributeBitSet2.get(element);
            if (attribsSet == -1) {
               attribsSet = 0;
            }

            this._elementAttributeBitSet2.put(element, attribsSet | (long)1 << idName - 256 - 64);
         }
      }
   }

   void setAttribute(int node, String name, String value) {
      this.setAttribute(node, this._names.append(name), value);
   }

   void setAttribute(int node, int idName, int value) {
      int attr = this.getAttribute(node, idName);
      if (attr != 0) {
         this.setAttributeValue(attr, value);
         this.clearAttributeIsDefault(attr);
      } else {
         attr = this.addAttribute("", "", idName, idName, value, -2, false);
         this.insertAttribute(node, attr, idName);
         if (idName == -4 || idName == -3) {
            this._nameIdMap.put(Integer.toString(value), node);
         }
      }
   }

   void setAttribute(int node, int idName, String value) {
      int attr = this.getAttribute(node, idName);
      if (attr != 0) {
         this.setAttributeValue(attr, value);
         this.clearAttributeIsDefault(attr);
      } else {
         attr = this.addAttribute("", "", idName, idName, value, -2, false);
         this.insertAttribute(node, attr, idName);
         if (idName == -4 || idName == -3) {
            this._nameIdMap.put(value, node);
         }
      }
   }

   void setAttributeNS(int node, String uri, String qName, String value) {
      this.parseQName(qName);
      int attr = this.getAttributeNS(node, uri, this._localName);
      if (attr != 0) {
         this.setAttributeValue(attr, value);
         this.clearAttributeIsDefault(attr);
      } else {
         int idName = this._names.append(this._localName);
         attr = this.addAttribute(uri, this._prefix, idName, this._names.append(qName), value, -2, false);
         this.insertAttribute(node, attr, idName);
         if (idName == -4 || idName == -3) {
            this._nameIdMap.put(value, node);
         }
      }
   }

   int setAttributeNode(int element, int attr) {
      int oldAttr = this.getAttributeNS(element, this.getAttributeURI(attr), this.getAttributeLocalName(attr));
      if (oldAttr == 0) {
         this.insertAttribute(element, attr, 0);
         return 0;
      } else {
         this.replaceAttribute(element, oldAttr, attr);
         return oldAttr;
      }
   }

   String getAttributeLocalName(int node) {
      return (String)this._names.get(this._qnameToLocalName.get(this._attributeQNameData.get(node)));
   }

   String getAttributeURI(int node) {
      return (String)this._names.get(this._attributeURIData.get(node));
   }

   String getAttributeType(int node) {
      return (String)this._attrTypes.get(this._types.get(node) >> 8 & 3840);
   }

   String getAttributeQName(int node) {
      return (String)(node == 0 ? "" : this._names.get(this._attributeQNameData.get(node)));
   }

   String getAttributePrefix(int node) {
      return (String)this._names.get(this._qnameToPrefix.get(this._attributeQNameData.get(node)));
   }

   void setAttributeQName(int node, String qName) {
      this.setAttributeQName(node, this._names.append(qName));
   }

   void setAttributeQName(int node, int qName) {
      this._attributeQNameData.set(node, qName);
   }

   String getAttributeValue(int node) {
      if (node == 0) {
         return "";
      }

      int valueIndex = this._attributeValueData.get(node);
      return (String)((valueIndex & -2147483648) != 0
         ? Integer.toString(this._attrIntValues.elementAt(valueIndex & 2147483647))
         : this._attrValues.elementAt(valueIndex));
   }

   int getAttributeValueAsInt(int node) {
      if (node == 0) {
         return -1;
      }

      int valueIndex = this._attributeValueData.get(node);
      return (valueIndex & -2147483648) != 0
         ? this._attrIntValues.elementAt(valueIndex & 2147483647)
         : RendererControl.valueAsInt((String)this._attrValues.elementAt(this._attributeValueData.get(node)), -1);
   }

   int getAttributeValueAsPixels(int node) {
      if (node == 0) {
         return -1;
      }

      int valueIndex = this._attributeValueData.get(node);
      return (valueIndex & -2147483648) != 0
         ? this._attrIntValues.elementAt(valueIndex & 2147483647)
         : RendererControl.valueAsPixels((String)this._attrValues.elementAt(this._attributeValueData.get(node)), -1);
   }

   void setAttributeValue(int node, String value) {
      int valueNode = this._attrValues.size();
      this._attrValues.addElement(value);
      this._attributeValueData.set(node, valueNode);
   }

   void setAttributeValue(int node, int value) {
      this._attrIntValues.addElement(value);
      int valueNode = this._attrIntValues.size() - 1 | -2147483648;
      this._attributeValueData.set(node, valueNode);
   }

   boolean getAttributeIsSpecified(int node) {
      return (this._types.get(node) & 16384) == 0;
   }

   void setAttributeIsDefault(int node) {
      this._types.set(node, this._types.get(node) | 16384);
   }

   void clearAttributeIsDefault(int node) {
      this._types.set(node, this._types.get(node) & -16385);
   }

   int addPI(String target, String data) {
      return this.addPI(this._names.append(target), this._miscStringData.append(data));
   }

   int addPI(int target, int data) {
      int node = this.newNode();
      this._types.set(node, 7);
      this._parents.set(node, 0);
      this._firstChild.set(node, 0);
      this._nextChild.set(node, 0);
      this._processingInstructionTargetData.set(node, target);
      this._processingInstructionDataData.set(node, data);
      return node;
   }

   String getPITarget(int node) {
      return (String)this._names.get(this._processingInstructionTargetData.get(node));
   }

   String getPIData(int node) {
      return (String)this._miscStringData.get(this._processingInstructionDataData.get(node));
   }

   void setPIData(int node, String data) {
      this._processingInstructionDataData.set(node, this._miscStringData.append(data));
   }

   int addDocumentType(String qName, String publicId, String systemId, String body) {
      int node = this._dtdNode = this.newNode();
      HTMLDOMInternalRepresentation$DocumentTypeData dtd = new HTMLDOMInternalRepresentation$DocumentTypeData();
      dtd.name = this._names.append(qName);
      dtd.publicId = this._names.append(publicId);
      dtd.systemId = this._names.append(systemId);
      dtd.body = this._miscStringData.append(body);
      this._types.set(node, 10);
      this._parents.set(node, 0);
      this._firstChild.set(node, 0);
      this._nextChild.set(node, 0);
      this._documentTypeData.set(node, this._dtdToIndex.append(dtd));
      return this._dtdNode;
   }

   String getDocumentTypeName(int node) {
      return (String)this._names.get(((HTMLDOMInternalRepresentation$DocumentTypeData)this._dtdToIndex.get(this._documentTypeData.get(node))).name);
   }

   String getDocumentTypePublicId(int node) {
      return (String)this._names.get(((HTMLDOMInternalRepresentation$DocumentTypeData)this._dtdToIndex.get(this._documentTypeData.get(node))).publicId);
   }

   String getDocumentTypeSystemId(int node) {
      return (String)this._names.get(((HTMLDOMInternalRepresentation$DocumentTypeData)this._dtdToIndex.get(this._documentTypeData.get(node))).systemId);
   }

   String getDocumentTypeBody(int node) {
      return (String)this._miscStringData.get(((HTMLDOMInternalRepresentation$DocumentTypeData)this._dtdToIndex.get(this._documentTypeData.get(node))).body);
   }

   int addNotation(int name, int publicId, int systemId) {
      int node = this.newNode();
      this._notations.addElement(node);
      this._notationHash.put(this._names.get(name), node);
      this._types.set(node, 12);
      this._parents.set(node, 0);
      this._firstChild.set(node, 0);
      this._nextChild.set(node, 0);
      this._notationNameData.set(node, name);
      this._notationPublicIdData.set(node, publicId);
      this._notationSystemIdData.set(node, systemId);
      return node;
   }

   String getNotationName(int node) {
      return (String)this._names.get(this._notationNameData.get(node));
   }

   String getNotationPublicId(int node) {
      return (String)this._names.get(this._notationPublicIdData.get(node));
   }

   String getNotationSystemId(int node) {
      return (String)this._names.get(this._notationSystemIdData.get(node));
   }

   ToIntHashtable getNotationHash() {
      return this._notationHash;
   }

   IntVector getNotations() {
      return this._notations;
   }

   int addEntity(String name, String publicId, String systemId, String notationName, String value) {
      if (value != null) {
         this._internalEntities.put(name, value);
      }

      return this.addEntity(
         this._names.append(name),
         this._names.append(publicId),
         this._names.append(systemId),
         this._names.append(notationName),
         this._miscStringData.append(value)
      );
   }

   String getEntityValue(String name) {
      return (String)this._internalEntities.get(name);
   }

   int addEntity(int name, int publicId, int systemId, int notationName, int value) {
      int node = this.newNode();
      this._entities.addElement(node);
      HTMLDOMInternalRepresentation$EntityData data = new HTMLDOMInternalRepresentation$EntityData();
      data.name = name;
      data.publicId = publicId;
      data.systemId = systemId;
      data.notationName = notationName;
      data.value = value;
      this._entityHash.put(this._names.get(name), node);
      this._types.set(node, 6);
      this._parents.set(node, 0);
      this._firstChild.set(node, 0);
      this._nextChild.set(node, 0);
      this._entityData.set(node, this._entityToIndex.append(data));
      return node;
   }

   String getEntityName(int node) {
      return (String)this._names.get(((HTMLDOMInternalRepresentation$EntityData)this._entityToIndex.get(this._entityData.get(node))).name);
   }

   String getEntityValue(int node) {
      return (String)this._names.get(((HTMLDOMInternalRepresentation$EntityData)this._entityToIndex.get(this._entityData.get(node))).value);
   }

   String getEntityPublicId(int node) {
      return (String)this._names.get(((HTMLDOMInternalRepresentation$EntityData)this._entityToIndex.get(this._entityData.get(node))).publicId);
   }

   String getEntitySystemId(int node) {
      return (String)this._names.get(((HTMLDOMInternalRepresentation$EntityData)this._entityToIndex.get(this._entityData.get(node))).systemId);
   }

   String getEntityNotationName(int node) {
      return (String)this._names.get(((HTMLDOMInternalRepresentation$EntityData)this._entityToIndex.get(this._entityData.get(node))).notationName);
   }

   ToIntHashtable getEntityHash() {
      return this._entityHash;
   }

   IntVector getEntities() {
      return this._entities;
   }

   int addEntityReference(String name, String publicId, String systemId) {
      return this.addEntityReference(this._names.append(name), this._names.append(publicId), this._names.append(systemId));
   }

   int addEntityReference(int name, int publicId, int systemId) {
      int node = this.newNode();
      this._types.set(node, 5);
      this._parents.set(node, 0);
      this._firstChild.set(node, 0);
      this._nextChild.set(node, 0);
      this._entityReferenceNameData.set(node, name);
      this._entityReferenceSystemIdData.set(node, publicId);
      this._entityReferencePublicIdData.set(node, systemId);
      return node;
   }

   String getEntityReferenceName(int node) {
      return (String)this._names.get(this._entityReferenceNameData.get(node));
   }

   String getEntityReferenceSystemId(int node) {
      return (String)this._names.get(this._entityReferenceSystemIdData.get(node));
   }

   String getEntityReferencePublicId(int node) {
      return (String)this._names.get(this._entityReferencePublicIdData.get(node));
   }

   int cloneNode(int handle, boolean deep) {
      return 0;
   }

   int getLastChild(int handle) {
      int child = this.getFirstChild(handle);
      if (child == 0) {
         return 0;
      }

      while (true) {
         int nextChild = this.getNextChild(child);
         if (nextChild == 0) {
            return child;
         }

         child = nextChild;
      }
   }

   int getPreviousChild(int child) {
      return this.getPreviousChild(this.getParent(child), child);
   }

   int getPreviousChild(int parent, int child) {
      if (parent == 0) {
         return 0;
      }

      int currChild = this.getFirstChild(parent);
      if (currChild == child) {
         return 0;
      }

      while (true) {
         int nextChild = this.getNextChild(currChild);
         if (nextChild == child) {
            return currChild;
         }

         currChild = nextChild;
      }
   }

   boolean allowsChild(int parent, int child) {
      switch (this.getType(parent)) {
         case 1:
         case 5:
            switch (this.getType(child)) {
               case 0:
               case 2:
               case 6:
               case 9:
               case 10:
                  return false;
               case 1:
               case 3:
               case 4:
               case 5:
               case 7:
               case 8:
               case 11:
               default:
                  return true;
            }
         case 9:
         case 11:
            switch (this.getType(child)) {
               case 1:
               case 3:
               case 4:
               case 7:
               case 8:
               case 11:
                  return true;
               default:
                  return false;
            }
         default:
            return false;
      }
   }

   boolean isLeaf(int handle) {
      switch (this.getType(handle)) {
         case 1:
         case 5:
         case 9:
         case 11:
            return false;
         case 2:
         case 3:
         case 4:
         case 6:
         case 7:
         case 8:
         case 10:
         case 12:
         default:
            return true;
      }
   }

   HTMLDocumentType getDocumentType() {
      return (HTMLDocumentType)this.getNode(this._dtdNode);
   }

   HTMLNode allocNode(int handle) {
      switch (this.getType(handle)) {
         case 1:
         case 10:
            return new HTMLDocumentType(this, handle);
         default:
            return null;
      }
   }

   HTMLNode getNode(int handle) {
      if (handle < 0) {
         return null;
      }

      if (handle == 0) {
         return null;
      }

      HTMLNode node = (HTMLNode)this._nodeList.get(handle);
      if (node != null) {
         return node;
      }

      node = this.allocNode(handle);
      this._nodeList.put(handle, node);
      return node;
   }

   HTMLNode getElementById(int handle, String idName) {
      int nameStr = this._names.append("NAME");
      int idStr = this._names.append("ID");

      for (int child = this.getFirstChild(handle); child != 0; child = this.getNextChild(child)) {
         if (this.getType(child) == 1) {
            for (int attrib = this.getAttributes(child); attrib != 0; attrib = this.getNextChild(attrib)) {
               int attribQName = this._attributeQNameData.get(attrib);
               if ((attribQName == nameStr || attribQName == idStr) && StringUtilities.strEqual(this.getAttributeValue(attrib), idName)) {
                  return this.getNode(child);
               }
            }
         }

         HTMLNode node = this.getElementById(child, idName);
         if (node != null) {
            return node;
         }
      }

      return null;
   }

   void getElementsByName(Vector v, int handle, String idName, int nameStr, int idStr) {
      for (int child = this.getFirstChild(handle); child != 0; child = this.getNextChild(child)) {
         if (this.getType(child) == 1) {
            for (int attrib = this.getAttributes(child); attrib != 0; attrib = this.getNextChild(attrib)) {
               int attribQName = this._attributeQNameData.get(attrib);
               if ((attribQName == nameStr || attribQName == idStr) && StringUtilities.strEqual(this.getAttributeValue(attrib), idName)) {
                  v.addElement(this.getNode(child));
                  break;
               }
            }
         }

         this.getElementsByName(v, child, idName, nameStr, idStr);
      }
   }

   void getElementsByTagName(Vector v, int handle, int id) {
      for (int child = this.getFirstChild(handle); child != 0; child = this.getNextChild(child)) {
         if (this.getType(child) == 1 && this._elementQNameData.get(child) == id) {
            v.addElement(this.getNode(child));
         }

         this.getElementsByTagName(v, child, id);
      }
   }

   Enumeration getAllElementsEnumeration() {
      return this._nodeList.elements();
   }

   void getAllElements(Vector v, int handle) {
      for (int child = this.getFirstChild(handle); child != 0; child = this.getNextChild(child)) {
         if (this.getType(child) == 1) {
            v.addElement(this.getNode(child));
         }

         this.getAllElements(v, child);
      }
   }

   Vector getElementsByName(int handle, String name) {
      if (handle != this._document) {
         int nameStr = this._names.append("NAME");
         int idStr = this._names.append("ID");
         Vector v = (Vector)(new Object());
         this.getElementsByName(v, handle, name, nameStr, idStr);
         return v;
      }

      int item = this._nameIdMap.getSingle(name);
      if (item != -1) {
         Vector v = (Vector)(new Object());
         v.addElement(this.getNode(item));
         return v;
      }

      IntVector items = this._nameIdMap.getMultiple(name);
      if (items == null) {
         return (Vector)(new Object());
      }

      Vector v = (Vector)(new Object());
      int size = items.size();

      for (int i = 0; i < size; i++) {
         v.addElement(this.getNode(items.elementAt(i)));
      }

      return v;
   }

   Vector getElementsByTagName(int handle, String tagName) {
      Vector v = (Vector)(new Object());
      if (tagName.length() == 1 && tagName.charAt(0) == '*') {
         this.getAllElements(v, handle);
         return v;
      } else {
         int tagNameId = this._names.append(tagName);
         this.getElementsByTagName(v, handle, tagNameId);
         return v;
      }
   }

   void getElementsByTagNameNS(Vector v, int handle, String uri, String localName) {
      int idURI = this._names.append(uri);
      int idName = this._names.append(localName);

      for (int child = this.getFirstChild(handle); child != 0; child = this.getNextChild(child)) {
         if (this.getType(child) == 1 && this._elementURIData.get(child) == idURI && this._qnameToLocalName.get(this._elementQNameData.get(child)) == idName) {
            v.addElement(this.getNode(child));
            this.getElementsByTagNameNS(v, child, uri, localName);
         }
      }
   }

   Vector getElementsByTagNameNS(int handle, String uri, String localName) {
      Vector v = (Vector)(new Object());
      this.getElementsByTagNameNS(v, handle, uri, localName);
      return v;
   }

   int getParent(int handle) {
      return this._parents.get(handle);
   }

   void setParent(int handle, int parent) {
      this._parents.set(handle, parent);
   }

   int getFirstChild(int handle) {
      return this._firstChild.get(handle);
   }

   void setFirstChild(int handle, int child) {
      this._firstChild.set(handle, child);
   }

   int getNextChild(int handle) {
      return this._nextChild.get(handle);
   }

   void setNextChild(int handle, int child) {
      this._nextChild.set(handle, child);
   }

   boolean isParentOfOrSelf(int parent, int child) {
      if (this.getType(child) == 2) {
         return false;
      }

      if (parent == child) {
         return true;
      }

      if (this.getFirstChild(parent) == 0) {
         return false;
      }

      while (parent != child) {
         child = this.getParent(child);
         if (child == 0) {
            return false;
         }
      }

      return true;
   }

   boolean isParentOf(int parent, int child) {
      if (this.getType(child) == 2) {
         return false;
      }

      do {
         child = this.getParent(child);
         if (child == 0) {
            return false;
         }
      } while (parent != child);

      return true;
   }

   void unlink(int child) {
      int parent = this.getParent(child);
      if (parent != 0) {
         this.removeChild(parent, child);
      }
   }

   void insertBefore(int parent, int newChild, int oldChild) {
      int previousChild = this.getPreviousChild(oldChild);
      if (previousChild == 0) {
         this.setNextChild(newChild, this.getFirstChild(parent));
         this.setFirstChild(parent, newChild);
      } else {
         this.setNextChild(newChild, oldChild);
         this.setNextChild(previousChild, newChild);
      }

      this.setParent(newChild, parent);
   }

   void insertAfter(int parent, int newChild, int oldChild) {
      if (oldChild == 0) {
         this.appendChild(parent, newChild);
      } else {
         this.setNextChild(newChild, this.getNextChild(oldChild));
         this.setNextChild(oldChild, newChild);
         this.setParent(newChild, parent);
      }
   }

   void replaceChild(int parent, int oldChild, int newChild) {
      this.insertBefore(parent, newChild, oldChild);
      this.unlink(oldChild);
   }

   void insertChild(int parent, int child) {
      this.setNextChild(child, this.getFirstChild(parent));
      this.setFirstChild(parent, child);
      this.setParent(child, parent);
   }

   void appendChild(int parent, int newChild) {
      this.setNextChild(newChild, 0);
      int currChild = this.getFirstChild(parent);
      if (currChild == 0) {
         this.setFirstChild(parent, newChild);
      } else {
         while (true) {
            int nextChild = this.getNextChild(currChild);
            if (nextChild == 0) {
               this.setNextChild(currChild, newChild);
               break;
            }

            currChild = nextChild;
         }
      }

      this.setParent(newChild, parent);
   }

   void removeChild(int parent, int oldChild) {
      int currChild = this.getFirstChild(parent);
      if (currChild == oldChild) {
         this.setFirstChild(parent, this.getNextChild(oldChild));
      } else {
         while (true) {
            int nextChild = this.getNextChild(currChild);
            if (nextChild == oldChild) {
               this.setNextChild(currChild, this.getNextChild(oldChild));
               break;
            }

            currChild = nextChild;
         }
      }

      this.setParent(oldChild, 0);
   }

   int getType(int node) {
      return this._types.get(node) & 15;
   }

   int getNodeType(int node) {
      switch (this.getType(node)) {
         case 0:
            return -1;
         case 1:
         default:
            return 1;
         case 2:
            return 2;
         case 3:
            return 3;
         case 4:
            return 4;
         case 5:
            return 5;
         case 6:
            return 6;
         case 7:
            return 7;
         case 8:
            return 8;
         case 9:
            return 9;
         case 10:
            return 10;
         case 11:
            return 11;
         case 12:
            return 12;
      }
   }

   void notReadOnly(int node) {
      if (node != 0) {
         if ((this._types.get(node) & 32768) != 0) {
            throw new Object((short)7, "");
         }
      }
   }

   static void isNCName(String prefix) {
      if (!isValidName(prefix, false)) {
         throw new Object((short)5, "");
      }
   }

   static boolean isValidName(String name, boolean allowColon) {
      return true;
   }

   static void isQName(String name) {
      if (!isValidName(name, true)) {
         throw new Object((short)5, "");
      }
   }

   void setTreeReadOnly(int node) {
      this.setReadOnly(node);

      for (int child = this.getFirstChild(node); child != 0; child = this.getNextChild(child)) {
         this.setTreeReadOnly(child);
      }

      for (int child = this.getAttributes(node); child != 0; child = this.getNextChild(child)) {
         this.setTreeReadOnly(child);
      }
   }

   void setReadOnly(int node) {
      this._types.set(node, this._types.get(node) | 32768);
   }

   void setInDTD(int node) {
      this._types.set(node, this._types.get(node) | 8192);
   }

   boolean getInDTD(int node) {
      return (this._types.get(node) & 8192) != 0;
   }

   public static HTMLDOMInternalRepresentation getIR(Node node) {
      return ((HTMLNode)node).getIR();
   }

   private void markReachable(int handle) {
      int type = this._types.get(handle);
      if ((type & 4096) == 0) {
         type |= 4096;
         this._types.set(handle, type);
         if ((type & 15) == 1) {
            for (int attr = this.getElementAttributes(handle); attr != 0; attr = this.getNextChild(attr)) {
               this.markReachable(attr);
            }
         }

         for (int child = this.getFirstChild(handle); child != 0; child = this.getNextChild(child)) {
            this.markReachable(child);
         }
      }
   }

   public static void compress(Node n) {
      ((HTMLNode)n).getIR().gc();
   }

   private void gc() {
      for (int i = this._notations.size() - 1; i >= 0; i--) {
         this.markReachable(this._notations.elementAt(i));
      }

      for (int i = this._entities.size() - 1; i >= 0; i--) {
         this.markReachable(this._entities.elementAt(i));
      }

      if (this._dtdNode != 0) {
         this.markReachable(this._dtdNode);
      }

      if (this._document != 0) {
         this.markReachable(this._document);
      }

      Enumeration e = this._nodeList.elements();

      while (e.hasMoreElements()) {
         HTMLNode node = (HTMLNode)e.nextElement();
         if (node != null) {
            this.markReachable(node.getNode());
         }
      }

      IntEnumeration ie = this._elementNamespaces.keys();
      IntVector deadNamespaces = null;

      while (ie.hasMoreElements()) {
         int element = ie.nextElement();
         if ((this._types.get(element) & 4096) != 0) {
            for (int nameSpace = this._elementNamespaces.get(element); nameSpace != 0; nameSpace = this.getNamespaceNext(nameSpace)) {
               this.markReachable(nameSpace);
            }
         } else {
            if (deadNamespaces == null) {
               deadNamespaces = (IntVector)(new Object());
            }

            deadNamespaces.addElement(element);
         }
      }

      if (deadNamespaces != null) {
         for (int i = deadNamespaces.size() - 1; i >= 0; i--) {
            this._elementNamespaces.remove(deadNamespaces.elementAt(i));
         }
      }

      for (int i = this._freeList.getFirstFree(); i != 0; i = this._freeList.getNextFree(i)) {
         this._types.set(i, this._types.get(i) | 4096);
      }

      for (int i = this._types.getLength() - 1; i > 0; i--) {
         int type = this._types.get(i);
         if ((type & 4096) != 0) {
            this._types.set(i, type & -4097);
         } else {
            this._nodeList.remove(i);
            this._freeList.freeSlot(i);
         }
      }
   }

   public static void dump(Node n) {
      HTMLNode node = (HTMLNode)n;
      node.getIR().dumpAll(0, node);
   }

   void dump(NodeList l) {
      this.dump(0, l);
   }

   void printIndent(int indent) {
      for (int i = 0; i < indent; i++) {
         System.out.print("  ");
      }
   }

   void printlnIndent(int indent, String str) {
      this.printIndent(indent);
      System.out.println(str);
   }

   void dumpChildren(int indent, Node n) {
      NodeList l = n.getChildNodes();

      for (int i = 0; i < l.getLength(); i++) {
         this.dump(indent, l.item(i));
      }
   }

   void dump(int indent, NodeList l) {
      for (int i = 0; i < l.getLength(); i++) {
         this.dump(indent, l.item(i));
      }
   }

   void dump(int indent, NamedNodeMap l) {
      for (int i = 0; i < l.getLength(); i++) {
         this.dump(indent, l.item(i));
      }
   }

   void dumpAttributes(int indent, Element e) {
      this.dump(indent, e.getAttributes());
   }

   void dumpNamespaces(int indent, Element e) {
      int element = ((HTMLNode)e).getNode();

      for (int ns = this.getElementNamespaces(element); ns != 0; ns = this.getNamespaceNext(ns)) {
         this.printlnIndent(
            indent, ((StringBuffer)(new Object("xmlns:"))).append(this.getNamespacePrefix(ns)).append("=").append(this.getNamespaceURI(ns)).toString()
         );
      }
   }

   void dump(int indent, Element e) {
      if (e.getNamespaceURI().length() == 0) {
         this.printlnIndent(indent, ((StringBuffer)(new Object("<"))).append(e.getNodeName()).append(">").toString());
      } else {
         this.printlnIndent(
            indent,
            ((StringBuffer)(new Object("<")))
               .append(e.getPrefix())
               .append(":")
               .append(e.getNodeName())
               .append("(")
               .append(e.getNamespaceURI())
               .append(")")
               .append(">")
               .toString()
         );
      }

      this.dumpNamespaces(indent + 1, e);
      this.dumpAttributes(indent + 1, e);
      this.dumpChildren(indent + 1, e);
      this.printlnIndent(indent, ((StringBuffer)(new Object("</"))).append(e.getNodeName()).append(">").toString());
   }

   void dump(int indent, Attr a) {
      if (a.getSpecified()) {
         if (a.getNamespaceURI().length() == 0) {
            this.printlnIndent(indent, ((StringBuffer)(new Object())).append(a.getNodeName()).append("=").append(a.getValue()).toString());
         } else {
            this.printlnIndent(
               indent,
               ((StringBuffer)(new Object()))
                  .append(a.getPrefix())
                  .append(":")
                  .append(a.getName())
                  .append("(")
                  .append(a.getNamespaceURI())
                  .append(")")
                  .append("=")
                  .append(a.getValue())
                  .toString()
            );
         }
      }
   }

   void dump(int indent, CharacterData c) {
      this.printlnIndent(indent, ((StringBuffer)(new Object("'"))).append(c.getData()).append("'").toString());
   }

   void dump(int indent, Comment c) {
      this.printlnIndent(indent, ((StringBuffer)(new Object("<!--"))).append(c.getData()).append("-->").toString());
   }

   void dump(int indent, ProcessingInstruction p) {
      this.printlnIndent(indent, ((StringBuffer)(new Object("<?"))).append(p.getTarget()).append(" ").append(p.getData()).append("?>").toString());
   }

   void dump(int indent, Document d) {
      DocumentType dt = d.getDoctype();
      if (dt != null) {
         this.dump(indent, dt);
      }

      this.printlnIndent(indent, "<DOCUMENT>");
      this.dumpChildren(indent + 1, d);
      this.printlnIndent(indent, "</DOCUMENT>");
   }

   void dump(int indent, DocumentFragment d) {
      this.printlnIndent(indent, "<DOCUMENT_FRAGMENT>");
      this.dumpChildren(indent + 1, d);
      this.printlnIndent(indent, "</DOCUMENT_FRAGMENT>");
   }

   void dump(int indent, DocumentType dtd) {
      this.printlnIndent(indent, ((StringBuffer)(new Object("<DTD> "))).append(dtd.getName()).toString());
      if (dtd.getSystemId() != null) {
         this.printlnIndent(indent, ((StringBuffer)(new Object("SYSID: "))).append(dtd.getSystemId()).toString());
      }

      if (dtd.getPublicId() != null) {
         this.printlnIndent(indent, ((StringBuffer)(new Object("PUBID: "))).append(dtd.getPublicId()).toString());
      }

      this.printlnIndent(indent, dtd.getInternalSubset());
      this.printlnIndent(indent, "</DTD>");
      this.printlnIndent(indent, "<NOTATIONS>");
      this.dump(indent, dtd.getNotations());
      this.printlnIndent(indent, "</NOTATIONS>");
      this.printlnIndent(indent, "<ENTITIES>");
      this.dump(indent, dtd.getEntities());
      this.printlnIndent(indent, "</ENTITIES>");
   }

   void dump(int indent, Notation note) {
      this.printlnIndent(indent, "<NOTATION>");
      this.printlnIndent(indent, note.getNodeName());
      this.printlnIndent(indent, note.getPublicId());
      this.printlnIndent(indent, note.getSystemId());
      this.printlnIndent(indent, "</NOTATION>");
   }

   void dump(int indent, Entity ent) {
      this.printlnIndent(indent, "<ENTITY>");
      this.printlnIndent(indent, ent.getNodeName());
      this.printlnIndent(indent, ent.getPublicId());
      this.printlnIndent(indent, ent.getSystemId());
      this.printlnIndent(indent, ent.getNotationName());
      this.printlnIndent(indent, "</ENTITY>");
   }

   void dump(int indent, EntityReference ent) {
      this.printlnIndent(indent, ((StringBuffer)(new Object("&"))).append(ent.getNodeName()).append(";").toString());
      this.dumpChildren(indent + 1, ent);
   }

   void dumpAll(int indent, Node n) {
      this.dump(indent, n);
      this.printlnIndent(indent, ((StringBuffer)(new Object("_numElementsDumped="))).append(this._numElementsDumped).toString());
      this.printlnIndent(indent, ((StringBuffer)(new Object("_numAttributesDumped="))).append(this._numAttributesDumped).toString());
      this.printlnIndent(indent, ((StringBuffer)(new Object("_numTextsDumped="))).append(this._numTextsDumped).toString());
      this.printlnIndent(indent, ((StringBuffer)(new Object("_numPIsDumped="))).append(this._numPIsDumped).toString());
      this.printlnIndent(indent, ((StringBuffer)(new Object("_numDocFragsDumped="))).append(this._numDocFragsDumped).toString());
      this.printlnIndent(indent, ((StringBuffer)(new Object("_numDocumentsDumped="))).append(this._numDocumentsDumped).toString());
      this.printlnIndent(indent, ((StringBuffer)(new Object("_numDTDsDumped="))).append(this._numDTDsDumped).toString());
      this.printlnIndent(indent, ((StringBuffer)(new Object("_numNotationsDumped="))).append(this._numNotationsDumped).toString());
      this.printlnIndent(indent, ((StringBuffer)(new Object("_numEntitiesDumped="))).append(this._numEntitiesDumped).toString());
      this.printlnIndent(indent, ((StringBuffer)(new Object("_numEntRefsDumped="))).append(this._numEntRefsDumped).toString());
   }

   void dump(int indent, Node n) {
      if (((HTMLNode)n)._ir != this) {
         this.printlnIndent(indent, "Node does not belong to this document!");
      } else {
         switch (n.getNodeType()) {
            case 0:
               System.out.println("***UNKNOWN NODE***");
               return;
            case 1:
            default:
               this.dump(indent, (Element)n);
               this._numElementsDumped++;
               return;
            case 2:
               this.dump(indent, (Attr)n);
               this._numAttributesDumped++;
               return;
            case 3:
            case 4:
               this.dump(indent, (CharacterData)n);
               this._numTextsDumped++;
               return;
            case 5:
               this._numEntRefsDumped++;
               this.dump(indent, (EntityReference)n);
               return;
            case 6:
               this._numEntitiesDumped++;
               this.dump(indent, (Entity)n);
               return;
            case 7:
               this.dump(indent, (ProcessingInstruction)n);
               this._numPIsDumped++;
               return;
            case 8:
               this.dump(indent, (Comment)n);
               this._numTextsDumped++;
               return;
            case 9:
               this._numDocumentsDumped++;
               this.dump(indent, (Document)n);
               return;
            case 10:
               this._numDTDsDumped++;
               this.dump(indent, (DocumentType)n);
               return;
            case 11:
               this._numDocFragsDumped++;
               this.dump(indent, (DocumentFragment)n);
               return;
            case 12:
               this._numNotationsDumped++;
               this.dump(indent, (Notation)n);
         }
      }
   }
}
