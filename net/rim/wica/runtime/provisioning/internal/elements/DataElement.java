package net.rim.wica.runtime.provisioning.internal.elements;

import java.util.Vector;
import net.rim.device.api.util.StringTokenizer;
import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public class DataElement extends AbstractElement {
   private Vector _fields = (Vector)(new Object());
   private Vector _keyFields;
   private String _persist;
   private ElementReference _prototype;
   private Vector _stringedFields = (Vector)(new Object());

   @Override
   public void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      boolean traverseChildren = v.visitDataElement(this);
      if (traverseChildren) {
         ProvisioningHelper.visitDefinitionElementsHelper(this._fields, v);
      }
   }

   @Override
   public void addChild(AbstractElement parameter) {
      if (parameter instanceof FieldElement) {
         this.addFieldElement((FieldElement)parameter);
      }
   }

   public int fieldCount() {
      return this._fields.size();
   }

   @Override
   public String getElementName() {
      return "data";
   }

   public FieldElement getField(String fieldName) {
      FieldElement result = null;
      int fieldIndex = this._stringedFields.indexOf(fieldName);
      if (fieldIndex != -1) {
         return (FieldElement)this._fields.elementAt(fieldIndex);
      }

      if (this.hasPrototype()) {
         DataElement parent = this.getPrototype();
         result = parent.getField(fieldName);
      }

      return result;
   }

   public int getFieldIndex(String fieldName) {
      return this.getFieldIndexHelper(fieldName);
   }

   public Vector getFields() {
      return !this.hasPrototype() ? this._fields : this.buildHierarchy(this);
   }

   public DataElement getPrototype() {
      return this.hasPrototype() ? (DataElement)this._prototype.resolve() : null;
   }

   public boolean hasKey() {
      return this._keyFields != null && this._keyFields.size() > 0;
   }

   public boolean isKeyField(FieldElement f) {
      return this._keyFields != null && this._keyFields.indexOf(f.getName()) != -1;
   }

   public boolean hasPrototype() {
      return this._prototype != null;
   }

   public boolean isPersist() {
      boolean result = false;
      if (this.hasPersistFlag()) {
         return ProvisioningHelper.parseBoolean(this._persist);
      }

      for (DataElement e = this; e != null; e = e.getPrototype()) {
         if (e.hasPersistFlag()) {
            return ProvisioningHelper.parseBoolean(e._persist);
         }
      }

      return result;
   }

   public void resolvePath(String[] st, Vector pathElements, int currentToken) {
      FieldElement resolvedElement = this.getField(st[currentToken++]);
      if (resolvedElement != null) {
         pathElements.addElement(resolvedElement);
         if (currentToken < st.length) {
            resolvedElement.resolvePath(st, pathElements, currentToken);
         }
      }
   }

   @Override
   public void setAttributes(Attributes attributes) {
      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attValue.length() != 0) {
            if (attName.equals("name")) {
               super._name = attValue;
            } else if (attName.equals("persist")) {
               this._persist = attValue;
            } else if (attName.equals("prototype")) {
               this._prototype = new DataElementReference(this, attValue);
            } else if (attName.equals("key")) {
               if (this._keyFields == null) {
                  this._keyFields = (Vector)(new Object());
               } else {
                  this._keyFields.removeAllElements();
               }

               StringTokenizer tokenizer = (StringTokenizer)(new Object(attValue, ' '));
               String token = null;

               while (tokenizer.hasMoreTokens()) {
                  token = tokenizer.nextToken();
                  if (token.length() > 0) {
                     this._keyFields.addElement(token);
                  }
               }
            }
         }
      }
   }

   @Override
   public String toString() {
      StringBuffer buf = (StringBuffer)(new Object(120));
      buf.append("DataElement[name=")
         .append(super._name)
         .append(",prototype=")
         .append(this._prototype)
         .append(",persist=")
         .append(this._persist)
         .append(",key=");
      int numKeys = this._keyFields != null ? this._keyFields.size() : 0;

      for (int i = 0; i < numKeys; i++) {
         buf.append(this._keyFields.elementAt(i)).append(' ');
      }

      buf.append(']');
      return buf.toString();
   }

   private void addFieldElement(FieldElement field) {
      this._fields.addElement(field);
      this._stringedFields.addElement(field.getName());
   }

   private Vector buildHierarchy(DataElement d) {
      Vector hier = (Vector)(new Object());
      this.buildFieldHierarchy(hier, d);
      return hier;
   }

   private void addFieldsToHierarchy(Vector hierarchyOfFields, DataElement d) {
      Vector f = d._fields;
      int i = 0;

      for (int j = f.size(); i < j; i++) {
         hierarchyOfFields.addElement(f.elementAt(i));
      }
   }

   private boolean hasPersistFlag() {
      return this._persist != null;
   }

   private void buildFieldHierarchy(Vector fields, DataElement d) {
      if (d.hasPrototype()) {
         this.buildFieldHierarchy(fields, d.getPrototype());
      }

      this.addFieldsToHierarchy(fields, d);
   }

   private int getFieldIndexHelper(String fieldName) {
      int index = this._stringedFields.indexOf(fieldName);
      if (index != -1 && !this.hasPrototype()) {
         return index;
      } else if (index != -1 && this.hasPrototype()) {
         DataElement parent = this.getPrototype();
         return index + parent.prototypeFieldCount();
      } else if (this.hasPrototype()) {
         DataElement parent = this.getPrototype();
         return parent.getFieldIndexHelper(fieldName);
      } else {
         return -1;
      }
   }

   private int prototypeFieldCount() {
      int count = 0;
      if (!this.hasPrototype()) {
         return this.fieldCount();
      }

      DataElement parent = this.getPrototype();
      return this.fieldCount() + parent.prototypeFieldCount();
   }
}
