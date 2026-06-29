package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;

public class ElementReference {
   protected ElementReference _chained;
   protected AbstractElement _elementSource;
   protected AbstractElement _reference;
   protected String _targetName;

   public ElementReference(AbstractElement elementSource, String targetName) {
      if (elementSource == null || targetName == null) {
         new Object(
            ((StringBuffer)(new Object("ElementReference cannot be created with element=")))
               .append(elementSource)
               .append(" with name ")
               .append(targetName)
               .toString()
         );
      }

      this._elementSource = elementSource;
      this._targetName = targetName;
   }

   public ElementReference(ElementReference er, AbstractElement elementSource, String targetName) {
      this(elementSource, targetName);
      this._chained = er;
   }

   public boolean isResolved() {
      return this.getReference() != null;
   }

   public AbstractElement resolve() {
      AbstractElement ae = null;
      if (this._chained != null) {
         ae = this._chained.resolve();
      }

      return ae;
   }

   @Override
   public String toString() {
      StringBuffer buf = (StringBuffer)(new Object(ProvisioningHelper.getClassName(this)));
      buf.append("[targetName=");
      buf.append(this._targetName);
      buf.append(']');
      return buf.toString();
   }

   protected AbstractElement getReference() {
      return this._reference;
   }

   protected String getTargetName() {
      return this._targetName;
   }

   protected WicletElement resolveWicletElement() {
      return (WicletElement)ProvisioningHelper.findAncestor(this._elementSource, "wiclet");
   }

   protected void setReference(AbstractElement ref) {
      this._reference = ref != null ? ref : (this._chained != null ? this._chained.resolve() : null);
   }
}
