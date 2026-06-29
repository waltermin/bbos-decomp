package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public class ColumnElement extends AbstractElement implements TableStyling {
   private ElementReference _evenRowStyle;
   private String _format;
   private boolean _frozen;
   private ElementReference _headerStyle;
   private String _inValue;
   private ElementReference _oddRowStyle;
   private ElementReference _style;
   private String _title;
   private String _type = "text";
   private boolean _visible = true;
   private String _width;
   private String _name;

   public boolean isVisible() {
      return this._visible;
   }

   public String getFormat() {
      return this._format;
   }

   public boolean isFrozen() {
      return this._frozen;
   }

   public String getInValue() {
      return this._inValue;
   }

   public String getWidth() {
      return this._width;
   }

   public StyleElement getStyle() {
      return this._style != null ? (StyleElement)this._style.resolve() : null;
   }

   public String getTitle() {
      return this._title;
   }

   public String getType() {
      return this._type;
   }

   @Override
   public StyleElement getOddRowStyle() {
      return this._oddRowStyle != null ? (StyleElement)this._oddRowStyle.resolve() : null;
   }

   @Override
   public StyleElement getHeaderStyle() {
      return this._headerStyle != null ? (StyleElement)this._headerStyle.resolve() : null;
   }

   @Override
   public StyleElement getEvenRowStyle() {
      return this._evenRowStyle != null ? (StyleElement)this._evenRowStyle.resolve() : null;
   }

   @Override
   public void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitColumnElement(this);
   }

   @Override
   public String getElementName() {
      return "column";
   }

   @Override
   public String getName() {
      return this._name;
   }

   @Override
   public void setAttributes(Attributes attributes) {
      super.setAttributes(attributes);

      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attValue.length() != 0) {
            if (attName.equals("name")) {
               this._name = attValue;
            } else if (attName.equals("title")) {
               this._title = attValue;
            } else if (attName.equals("inValue")) {
               this._inValue = attValue;
            } else if (attName.equals("type")) {
               this._type = attValue;
            } else if (attName.equals("format")) {
               this._format = attValue;
            } else if (attName.equals("width")) {
               this._width = attValue;
            } else if (attName.equals("visible")) {
               this._visible = ProvisioningHelper.parseBoolean(attValue);
            } else if (attName.equals("frozen")) {
               this._frozen = ProvisioningHelper.parseBoolean(attValue);
            } else if (attName.equals("style")) {
               this._style = new StyleElementReference(this, attValue);
            } else if (attName.equals("headerStyle")) {
               this._headerStyle = new StyleElementReference(this, attValue);
            } else if (attName.equals("oddRowStyle")) {
               this._oddRowStyle = new StyleElementReference(this, attValue);
            } else if (attName.equals("evenRowStyle")) {
               this._evenRowStyle = new StyleElementReference(this, attValue);
            }
         }
      }
   }
}
