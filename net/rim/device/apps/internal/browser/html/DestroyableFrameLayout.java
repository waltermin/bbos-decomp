package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.browser.field.Destroyable;
import net.rim.device.internal.ui.container.FrameLayout;

class DestroyableFrameLayout extends FrameLayout implements Destroyable {
   private Destroyable _item;

   public DestroyableFrameLayout(Destroyable destroyable) {
      this._item = destroyable;
   }

   public DestroyableFrameLayout(long style, Destroyable destroyable) {
      super(style);
      this._item = destroyable;
   }

   @Override
   public void destroy() {
      this._item.destroy();
   }

   @Override
   public void setDestroyMethod(int method) {
      this._item.setDestroyMethod(method);
   }
}
