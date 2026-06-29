package net.rim.wica.runtime.provisioning.internal.elements;

import java.util.Vector;
import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public class FieldElement extends AbstractElement {
   protected boolean _array;
   protected ElementReference _component;
   protected String _defaultValue;
   protected String _type = "string";
   protected boolean _key;

   @Override
   public void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitFieldElement(this);
   }

   public AbstractElement getComponent() {
      return this.hasComponent() ? this._component.resolve() : null;
   }

   public String getComponentName() {
      String name = null;
      if (this.hasComponent()) {
         AbstractElement ae = this._component.resolve();
         name = ae.getName();
      }

      return name;
   }

   public String getDefaultValue() {
      return this._defaultValue;
   }

   @Override
   public String getElementName() {
      return "field";
   }

   public String getType() {
      return this._type;
   }

   public boolean hasComponent() {
      return this._component != null;
   }

   public boolean hasDefaultValue() {
      return this._defaultValue != null;
   }

   public boolean isArray() {
      return this._array;
   }

   @Override
   public void setAttributes(Attributes attributes) {
      super.setAttributes(attributes);

      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attName.equals("name")) {
            super._name = attValue;
         } else if (attName.equals("type")) {
            this._type = attValue;
         } else if (attName.equals("component")) {
            EnumElementReference eer = new EnumElementReference(this, attValue);
            this._component = new DataElementReference(eer, this, attValue);
         } else if (attName.equals("array")) {
            this._array = ProvisioningHelper.parseBoolean(attValue);
         } else if (attName.equals("default")) {
            this._defaultValue = attValue;
         }
      }
   }

   @Override
   public String toString() {
      StringBuffer buf = (StringBuffer)(new Object(120));
      buf.append("DfieldElement[name=")
         .append(super._name)
         .append(",type=")
         .append(this._type)
         .append(",component=")
         .append(this._component)
         .append(",array=")
         .append(this._array)
         .append(",defaultValue=")
         .append(this._defaultValue)
         .append(']');
      return buf.toString();
   }

   void resolvePath(String[] st, Vector pathElements, int currentToken) {
      boolean hasMoreTokens = currentToken < st.length;
      if (!hasMoreTokens) {
         pathElements.addElement(this);
      } else {
         if (this.hasComponent() && hasMoreTokens && "data".equals(this._type)) {
            DataElement element = (DataElement)this.getComponent();
            element.resolvePath(st, pathElements, currentToken);
         }
      }
   }
}
