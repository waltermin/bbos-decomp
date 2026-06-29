package net.rim.device.apps.internal.ribbon.skin.svg.eventprovider;

import net.rim.device.apps.internal.ribbon.indicators.UnreadCountComponent;

class CountEventProvider implements CountChangeListener {
   protected SkinEventProvider _skinProvider;
   private boolean _exposed;
   private boolean _visible;
   private boolean _new;
   private int _count;

   public void dispatchNew() {
      throw null;
   }

   public void dispatchNoNew() {
      throw null;
   }

   public void dispatchNewVisible() {
      throw null;
   }

   public void dispatchNoNewVisible() {
      throw null;
   }

   public void onExposed() {
      this._exposed = true;
      this.considerNewVisible();
   }

   public void onVisibilityChange(boolean visible) {
      this._visible = visible;
      this.considerNewVisible();
   }

   public void onObscured() {
      this._exposed = false;
   }

   @Override
   public void countChanged(UnreadCountComponent component) {
      int newCount = component.getCount();
      if (newCount > this._count && component.hasNewStatus()) {
         this.dispatchNew();
         this._new = true;
         this.considerNewVisible();
      } else if (!component.hasNewStatus()) {
         this.dispatchNoNew();
         this.dispatchNoNewVisible();
         this._new = false;
      }

      this._count = newCount;
   }

   CountEventProvider(SkinEventProvider skinProvider) {
      this._skinProvider = skinProvider;
   }

   private void considerNewVisible() {
      if (this._new && this._visible && this._exposed) {
         this.dispatchNewVisible();
         this._new = false;
      }
   }
}
