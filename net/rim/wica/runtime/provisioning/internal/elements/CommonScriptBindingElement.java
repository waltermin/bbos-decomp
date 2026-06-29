package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public class CommonScriptBindingElement extends AbstractElement {
   protected String _params;
   protected ElementReference _script;

   public String getParams() {
      return this._params;
   }

   public ScriptElement getScript() {
      return this.hasScript() ? (ScriptElement)this._script.resolve() : null;
   }

   public boolean hasScript() {
      return this._script != null;
   }

   @Override
   public void setAttributes(Attributes attributes) {
      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attValue.length() != 0) {
            if (attName.equals("params")) {
               this._params = attValue;
            } else if (attName.equals("script")) {
               this._script = new ScriptElementReference(this, attValue);
            }
         }
      }
   }

   @Override
   public String toString() {
      StringBuffer buf = new StringBuffer(128);
      buf.append(ProvisioningHelper.getClassName(this));
      buf.append("[script=");
      buf.append(this._script);
      buf.append(']');
      return buf.toString();
   }
}
