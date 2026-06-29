package net.rim.wica.runtime.provisioning.internal.elements;

import java.util.Vector;
import net.rim.device.api.util.StringTokenizer;
import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public final class ScreenElement extends AbstractElement {
   private Vector _controls = (Vector)(new Object());
   private boolean _dialog;
   private String _layout;
   private MenuElement _menu;
   private OnInitElement _oninit;
   private OnShowElement _onshow;
   private Vector _params;
   private Vector _refreshMsgs;
   private ElementReference _style;
   private String _title;
   private Vector _vars;

   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      if (v.visitScreenElement(this)) {
         ProvisioningHelper.visitDefinitionElementsHelper(this._controls, v);
      }
   }

   @Override
   public final void addChild(AbstractElement parameter) {
      if (parameter instanceof VarElement) {
         this.addVar((VarElement)parameter);
      } else if (parameter instanceof ParamElement) {
         this.addParam((ParamElement)parameter);
      } else if (parameter instanceof OnInitElement) {
         this._oninit = (OnInitElement)parameter;
      } else if (parameter instanceof MenuElement) {
         this._menu = (MenuElement)parameter;
      } else if (parameter instanceof OnShowElement) {
         this._onshow = (OnShowElement)parameter;
      } else {
         this._controls.addElement(parameter);
      }
   }

   public final Vector getControls() {
      return this._controls;
   }

   @Override
   public final String getElementName() {
      return "screen";
   }

   public final String getLayout() {
      return this._layout;
   }

   public final MenuElement getMenu() {
      return this._menu;
   }

   public final OnInitElement getOninit() {
      return this._oninit;
   }

   public final OnShowElement getOnShow() {
      return this._onshow;
   }

   public final Vector getParams() {
      return this._params;
   }

   public final Vector getRefreshMsgs() {
      return this._refreshMsgs;
   }

   public final StyleElement getStyle() {
      return this._style != null ? (StyleElement)this._style.resolve() : null;
   }

   public final String getTitle() {
      return this._title;
   }

   public final Vector getVars() {
      return this._vars;
   }

   public final boolean isDialog() {
      return this._dialog;
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attValue.length() != 0) {
            if (attName.equals("name")) {
               super._name = attValue;
            } else if (attName.equals("title")) {
               this._title = attValue;
            } else if (attName.equals("layout")) {
               this._layout = attValue;
            } else if (attName.equals("dialog")) {
               this._dialog = ProvisioningHelper.parseBoolean(attValue);
            } else if (!attName.equals("refreshMsg")) {
               if (attName.equals("style")) {
                  this._style = new StyleElementReference(this, attValue);
               }
            } else {
               StringTokenizer tokenizer = (StringTokenizer)(new Object(attValue, ' '));
               Vector refreshMessages = (Vector)(new Object());
               String token = null;

               while (tokenizer.hasMoreTokens()) {
                  token = tokenizer.nextToken();
                  if (token.length() > 0) {
                     refreshMessages.addElement(new MessageElementReference(this, token));
                  }
               }

               if (refreshMessages.size() > 0) {
                  this._refreshMsgs = refreshMessages;
               }
            }
         }
      }
   }

   @Override
   public final String toString() {
      String className = ProvisioningHelper.getClassName(this);
      StringBuffer buf = (StringBuffer)(new Object(64));
      buf.append(className);
      buf.append("[name=");
      buf.append(super._name);
      buf.append(']');
      return buf.toString();
   }

   private final void addParam(ParamElement param) {
      if (this._params == null) {
         this._params = (Vector)(new Object());
      }

      this._params.addElement(param);
   }

   private final void addVar(VarElement var) {
      if (this._vars == null) {
         this._vars = (Vector)(new Object());
      }

      this._vars.addElement(var);
   }
}
