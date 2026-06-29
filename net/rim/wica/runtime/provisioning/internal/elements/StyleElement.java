package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public final class StyleElement extends AbstractElement {
   private String _bgColor;
   private boolean _bold;
   private String _fgColor;
   private String _font;
   private boolean _italic;
   private String _size;
   private boolean _underline;
   private ElementReference _bgImage;

   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitStyleElement(this);
   }

   public final String getBgColor() {
      return this._bgColor;
   }

   @Override
   public final String getElementName() {
      return "style";
   }

   public final String getFgColor() {
      return this._fgColor;
   }

   public final String getFont() {
      return this._font;
   }

   public final String getSize() {
      return this._size;
   }

   public final boolean isBold() {
      return this._bold;
   }

   public final boolean isItalic() {
      return this._italic;
   }

   public final boolean isUnderline() {
      return this._underline;
   }

   public final ResourceElement getBgImage() {
      return this._bgImage != null ? (ResourceElement)this._bgImage.resolve() : null;
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attValue.length() != 0) {
            if (attName.equals("name")) {
               super._name = attValue;
            } else if (attName.equals("font")) {
               this._font = attValue;
            } else if (attName.equals("fgColor")) {
               this._fgColor = attValue;
            } else if (attName.equals("size")) {
               this._size = attValue;
            } else if (attName.equals("bgColor")) {
               this._bgColor = attValue;
            } else if (attName.equals("bold")) {
               this._bold = ProvisioningHelper.parseBoolean(attValue);
            } else if (attName.equals("underline")) {
               this._underline = ProvisioningHelper.parseBoolean(attValue);
            } else if (attName.equals("italic")) {
               this._italic = ProvisioningHelper.parseBoolean(attValue);
            } else if (attName.equals("bgImage")) {
               this._bgImage = new ResourceElementReference(this, attValue);
            }
         }
      }
   }

   @Override
   public final String toString() {
      String className = ProvisioningHelper.getClassName(this);
      StringBuffer buf = new StringBuffer(64);
      buf.append(className);
      buf.append("[name=");
      buf.append(super._name);
      buf.append(']');
      return buf.toString();
   }
}
