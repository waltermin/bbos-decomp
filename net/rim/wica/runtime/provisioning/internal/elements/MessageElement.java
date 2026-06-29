package net.rim.wica.runtime.provisioning.internal.elements;

import java.util.Vector;
import net.rim.device.api.util.Comparator;
import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public final class MessageElement extends AbstractElement {
   private AlertElement _alert;
   private int _messageCode;
   private Vector _fields = (Vector)(new Object());
   private MessageElementReference _prototype;
   private ScriptElementReference _script;
   private boolean _secure;
   private String _type;
   private NotificationElement _notification;

   public static final Comparator getComparator() {
      return new MessageElement$MessageElementComparator(null);
   }

   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      boolean traverseChildren = v.visitMessageElement(this);
      if (traverseChildren) {
         ProvisioningHelper.visitDefinitionElementsHelper(this._fields, v);
         if (this.hasAlert()) {
            this._alert.accept(v);
         }

         if (this.hasNotification()) {
            this._notification.accept(v);
         }
      }
   }

   @Override
   public final void addChild(AbstractElement parameter) {
      if (parameter instanceof MappedFieldElement || parameter instanceof FieldElement) {
         this._fields.addElement(parameter);
      } else if (parameter instanceof AlertElement) {
         this._alert = (AlertElement)parameter;
      } else {
         if (parameter instanceof NotificationElement) {
            this._notification = (NotificationElement)parameter;
         }
      }
   }

   public final AlertElement getAlert() {
      return this._alert;
   }

   public final NotificationElement getNotification() {
      return this._notification;
   }

   @Override
   public final String getElementName() {
      return "message";
   }

   public final Vector getFields() {
      return !this.hasPrototype() ? this._fields : this.getPrototypeFieldElementHierarchy();
   }

   public final int getMessageCode() {
      return this._messageCode;
   }

   public final MessageElement getPrototype() {
      return this.hasPrototype() ? (MessageElement)this._prototype.resolve() : null;
   }

   public final ScriptElement getScript() {
      return this.hasScript() ? (ScriptElement)this._script.resolve() : null;
   }

   public final boolean hasAlert() {
      return this._alert != null;
   }

   public final boolean hasPrototype() {
      return this._prototype != null;
   }

   public final boolean hasNotification() {
      return this._notification != null;
   }

   public final boolean hasScript() {
      return this._script != null;
   }

   public final boolean isSecure() {
      return this._secure;
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attValue.length() != 0) {
            if (attName.equals("name")) {
               super._name = attValue;
            } else if (attName.equals("secure")) {
               this._secure = ProvisioningHelper.parseBoolean(attValue);
            } else if (attName.equals("prototype")) {
               this._prototype = new MessageElementReference(this, attValue);
            } else if (attName.equals("script") && attValue.length() > 0) {
               this._script = new ScriptElementReference(this, attValue);
            } else if (attName.equals("type")) {
               this._type = attValue;
            }
         }
      }
   }

   public final void setMessageCode(int code) {
      this._messageCode = code;
   }

   @Override
   public final String toString() {
      StringBuffer buf = (StringBuffer)(new Object(120));
      buf.append("MessageElement[name=")
         .append(super._name)
         .append(",prototype=")
         .append(this._prototype)
         .append(",script=")
         .append(this._script)
         .append(",secure=")
         .append(this._secure)
         .append(",type=")
         .append(this._type)
         .append(']');
      return buf.toString();
   }

   private final void buildFieldHierarchy(Vector fields, MessageElement d) {
      if (d.hasPrototype()) {
         this.buildFieldHierarchy(fields, d.getPrototype());
      }

      Vector f = d._fields;
      int i = 0;

      for (int j = f.size(); i < j; i++) {
         fields.addElement(f.elementAt(i));
      }
   }

   private final Vector getPrototypeFieldElementHierarchy() {
      Vector fieldHierarchy = (Vector)(new Object());
      this.buildFieldHierarchy(fieldHierarchy, this);
      return fieldHierarchy;
   }
}
