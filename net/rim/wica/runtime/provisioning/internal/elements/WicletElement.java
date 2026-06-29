package net.rim.wica.runtime.provisioning.internal.elements;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningEncodingException;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import net.rim.wica.runtime.provisioning.internal.StandardComponentResolver;
import net.rim.wica.runtime.provisioning.internal.UniqueCodeGenerator;
import net.rim.wica.runtime.util.Util;
import org.xml.sax.Attributes;

public final class WicletElement extends AbstractElement {
   private Hashtable _dataElements = new Hashtable();
   private Hashtable _dependencyElements;
   private String _description;
   private String _entry;
   private Hashtable _enumerationElements;
   private Vector _globalElements;
   private Hashtable _globalElementsTable;
   private String _icon;
   private String _hoverIcon;
   private String _messageDelivery;
   private Hashtable _messageElements;
   private String _persistence;
   private Hashtable _resourceElements = new Hashtable();
   private Hashtable _screenElements;
   private Hashtable _scriptElements;
   private Vector _sortedMessages;
   private StandardComponentResolver _standardComponentResolver;
   private Hashtable _styleElements;
   private String _uri;
   private String _vendor;
   private String _version;

   public WicletElement() {
      this._enumerationElements = new Hashtable();
      this._globalElements = new Vector();
      this._globalElementsTable = new Hashtable();
      this._dependencyElements = new Hashtable();
      this._messageElements = new Hashtable();
      this._styleElements = new Hashtable();
      this._screenElements = new Hashtable();
      this._scriptElements = new Hashtable();
      this._messageDelivery = "standard";
      this._persistence = "performant";
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      boolean var11 = false /* VF: Semaphore variable */;
      boolean var14 = false /* VF: Semaphore variable */;

      try {
         try {
            var14 = true;
            var11 = true;
            boolean thr = v.visitWicletElement(this);
            if (!thr) {
               var11 = false;
               var14 = false;
            } else {
               ProvisioningHelper.visitDefinitionElementsHelper(this._resourceElements, v);
               ProvisioningHelper.visitDefinitionElementsHelper(this._globalElements, v);
               ProvisioningHelper.visitDefinitionElementsHelper(this._dependencyElements, v);
               Enumeration e = this._enumerationElements.elements();
               EnumerationElement[] ee = new EnumerationElement[this._enumerationElements.size()];

               for (int i = 0; e.hasMoreElements(); i++) {
                  ee[i] = (EnumerationElement)e.nextElement();
               }

               int i = 0;

               for (int j = ee.length; i < j; i++) {
                  if (!this._standardComponentResolver.isStandardComponent(ee[i].getName())) {
                     ee[i].accept(v);
                  }
               }

               e = this._dataElements.elements();
               DataElement[] de = new DataElement[this._dataElements.size()];

               for (int ix = 0; e.hasMoreElements(); ix++) {
                  de[ix] = (DataElement)e.nextElement();
               }

               int ix = 0;

               for (int j = de.length; ix < j; ix++) {
                  if (!this._standardComponentResolver.isStandardComponent(de[ix].getName())) {
                     de[ix].accept(v);
                  }
               }

               ProvisioningHelper.visitDefinitionElementsHelper(this.getMessageElementsInSortedOrder(), v);
               ProvisioningHelper.visitDefinitionElementsHelper(this._styleElements, v);
               ProvisioningHelper.visitDefinitionElementsHelper(this._screenElements, v);
               ProvisioningHelper.visitDefinitionElementsHelper(this._scriptElements, v);
               var11 = false;
               var14 = false;
            }
         } finally {
            if (var14) {
               throw new ProvisioningEncodingException(v.getCurrentElementVisited());
            }
         }
      } finally {
         if (var11) {
            v.setCurrentElementVisited(null);
         }
      }

      v.setCurrentElementVisited(null);
   }

   @Override
   public final void addChild(AbstractElement parameter) {
      if (parameter instanceof DataElement) {
         this.addDataElement((DataElement)parameter);
      } else if (parameter instanceof MessageElement) {
         this.addMessageElement((MessageElement)parameter);
      } else if (parameter instanceof ResourceElement) {
         this.addResourceElement((ResourceElement)parameter);
      } else if (parameter instanceof DependencyElement) {
         this.addDependencyElement((DependencyElement)parameter);
      } else if (parameter instanceof GlobalElement) {
         this.addGlobalElement((GlobalElement)parameter);
      } else if (parameter instanceof StyleElement) {
         this.addStyleElement((StyleElement)parameter);
      } else if (parameter instanceof ScreenElement) {
         this.addScreenElement((ScreenElement)parameter);
      } else if (parameter instanceof ScriptElement) {
         this.addScriptElement((ScriptElement)parameter);
      } else {
         if (parameter instanceof EnumerationElement) {
            this.addEnumerationElement((EnumerationElement)parameter);
         }
      }
   }

   @Override
   public final void addElementBody(String elementName, String elementBodyText) {
      if (elementName.endsWith("desc")) {
         this._description = elementBodyText;
      }
   }

   public final void attachStandardComponentResolver(UniqueCodeGenerator ucg) {
      this._standardComponentResolver = new StandardComponentResolver(this, ucg);
   }

   public final DataElement getDataElement(String id) {
      return (DataElement)this._dataElements.get(id);
   }

   public final DependencyElement getDependencyElement(String id) {
      return (DependencyElement)this._dependencyElements.get(id);
   }

   public final String getDescription() {
      return this._description;
   }

   @Override
   public final String getElementName() {
      return "wiclet";
   }

   public final String getEntry() {
      return this._entry;
   }

   public final EnumerationElement getEnumerationElement(String id) {
      return (EnumerationElement)this._enumerationElements.get(id);
   }

   public final GlobalElement getGlobalElement(String name) {
      return (GlobalElement)this._globalElementsTable.get(name);
   }

   public final Vector getGlobalElements() {
      return this._globalElements;
   }

   public final String getIcon() {
      return this._icon;
   }

   public final String getHoverIcon() {
      return this._hoverIcon;
   }

   public final String getMessageDelivery() {
      return this._messageDelivery;
   }

   public final MessageElement getMessageElement(String id) {
      return (MessageElement)this._messageElements.get(id);
   }

   public final String getPersistence() {
      return this._persistence;
   }

   public final ResourceElement getResourceElement(String id) {
      return (ResourceElement)this._resourceElements.get(id);
   }

   public final Hashtable getResourceElements() {
      return this._resourceElements;
   }

   public final ScreenElement getScreenElement(String id) {
      return (ScreenElement)this._screenElements.get(id);
   }

   public final ScriptElement getScriptElement(String id) {
      return (ScriptElement)this._scriptElements.get(id);
   }

   public final StandardComponentResolver getStandardComponentResolver() {
      return this._standardComponentResolver;
   }

   public final StyleElement getStyleElement(String id) {
      return (StyleElement)this._styleElements.get(id);
   }

   public final String getUri() {
      return this._uri;
   }

   public final String getVendor() {
      return this._vendor;
   }

   public final String getVersion() {
      return this._version;
   }

   public final int indexOf(GlobalElement global) {
      return this._globalElements.indexOf(global);
   }

   @Override
   public final String toString() {
      StringBuffer buf = new StringBuffer(256);
      buf.append("WicletElement");
      buf.append("[name=");
      buf.append(super._name);
      buf.append("[uri=");
      buf.append(this._uri);
      buf.append("[vendor=");
      buf.append(this._vendor);
      buf.append("[version=");
      buf.append(this._version);
      buf.append(']');
      return buf.toString();
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      super.setAttributes(attributes);

      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attValue.length() != 0) {
            if (attName.equals("name")) {
               super._name = attValue;
            } else if (attName.equals("uri")) {
               this._uri = attValue;
            } else if (attName.equals("entry")) {
               this._entry = attValue;
            } else if (attName.equals("version")) {
               this._version = attValue;
            } else if (attName.equals("vendor")) {
               this._vendor = attValue;
            } else if (attName.equals("icon")) {
               this._icon = attValue;
            } else if (attName.equals("hoverIcon")) {
               this._hoverIcon = attValue;
            } else if (attName.equals("messageDelivery")) {
               this._messageDelivery = attValue;
            } else if (attName.equals("persistence")) {
               this._persistence = attValue;
            }
         }
      }
   }

   public final Vector resolvePath(String mapping) {
      Vector path = new Vector();
      String[] tokens = Util.split(mapping, '.');
      int currentToken = 0;
      String root = tokens[currentToken++];
      int tokenCount = tokens.length;
      GlobalElement ge = (GlobalElement)this._globalElementsTable.get(root);
      DataElement de = (DataElement)this._dataElements.get(root);
      if (ge != null) {
         path.addElement(ge);
         if (currentToken < tokenCount) {
            ge.resolvePath(tokens, path, currentToken);
            return path;
         }
      } else if (de != null) {
         path.addElement(de);
         if (currentToken < tokenCount) {
            de.resolvePath(tokens, path, currentToken);
         }
      }

      return path;
   }

   private final void addDataElement(DataElement data) {
      this._dataElements.put(data.getName(), data);
   }

   private final void addDependencyElement(DependencyElement dependency) {
      this._dependencyElements.put(dependency.getType(), dependency);
   }

   private final void addEnumerationElement(EnumerationElement enumeration) {
      this._enumerationElements.put(enumeration.getName(), enumeration);
   }

   private final void addGlobalElement(GlobalElement global) {
      this._globalElements.addElement(global);
      this._globalElementsTable.put(global.getName(), global);
   }

   private final void addMessageElement(MessageElement message) {
      this._messageElements.put(message.getName(), message);
   }

   private final void addResourceElement(ResourceElement resource) {
      this._resourceElements.put(resource.getName(), resource);
   }

   private final void addScreenElement(ScreenElement screen) {
      this._screenElements.put(screen.getName(), screen);
   }

   private final void addScriptElement(ScriptElement script) {
      this._scriptElements.put(script.getName(), script);
   }

   private final void addStyleElement(StyleElement style) {
      this._styleElements.put(style.getName(), style);
   }

   private final Vector getMessageElementsInSortedOrder() {
      if (this._sortedMessages == null) {
         SimpleSortingVector sv = new SimpleSortingVector();
         sv.setSortComparator(MessageElement.getComparator());
         if (this._messageElements != null) {
            Enumeration enumeration = this._messageElements.elements();

            while (enumeration.hasMoreElements()) {
               MessageElement message = (MessageElement)enumeration.nextElement();
               sv.add(message);
            }
         }

         sv.reSort();
         this._sortedMessages = sv.getVector();
      }

      return this._sortedMessages;
   }
}
