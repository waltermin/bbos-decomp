package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import org.xml.sax.Attributes;

public class SingleChoiceElement extends CommonControlElement {
   protected String _format;
   protected String _mapping;
   protected OnChangeElement _onChange;
   protected String _type = "radio";
   protected String _visibleRows;

   @Override
   public void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitSingleChoiceElement(this);
   }

   @Override
   public void addChild(AbstractElement parameter) {
      if (parameter instanceof OnChangeElement) {
         this._onChange = (OnChangeElement)parameter;
      } else {
         super.addChild(parameter);
      }
   }

   @Override
   public String getElementName() {
      return "singleChoice";
   }

   public String getFormat() {
      return this._format;
   }

   public String getMapping() {
      return this._mapping;
   }

   public OnChangeElement getOnChange() {
      return this._onChange;
   }

   public String getType() {
      return this._type;
   }

   public String getVisibleRows() {
      return this._visibleRows;
   }

   @Override
   public void setAttributes(Attributes attributes) {
      super.setAttributes(attributes);

      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attValue.length() != 0) {
            if (attName.equals("format")) {
               this._format = attValue;
            } else if (attName.equals("type")) {
               this._type = attValue;
            } else if (attName.equals("visibleRows")) {
               this._visibleRows = attValue;
            } else if (attName.equals("mapping")) {
               this._mapping = attValue;
            }
         }
      }
   }
}
