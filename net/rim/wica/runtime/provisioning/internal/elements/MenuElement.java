package net.rim.wica.runtime.provisioning.internal.elements;

import java.util.Vector;

public final class MenuElement extends AbstractElement {
   private Vector _items = (Vector)(new Object());
   private OnShowElement _onshow;

   @Override
   public final void addChild(AbstractElement parameter) {
      if (parameter instanceof OnShowElement) {
         this._onshow = (OnShowElement)parameter;
      } else {
         if (parameter instanceof MenuItemElement) {
            this._items.addElement(parameter);
         }
      }
   }

   @Override
   public final String getElementName() {
      return "menu";
   }

   public final Vector getItems() {
      return this._items;
   }

   public final OnShowElement getOnShow() {
      return this._onshow;
   }
}
