package net.rim.tid.im.spellcheck;

import net.rim.device.api.ui.component.Menu;

class SpellCheckInputMethodVariant$DelayedShowMenu implements Runnable {
   private long _halign;
   private long _valign;
   private int _mode;
   private Menu _menu;
   private final SpellCheckInputMethodVariant this$0;

   SpellCheckInputMethodVariant$DelayedShowMenu(SpellCheckInputMethodVariant _1, int mode, long halign, long valign) {
      this.this$0 = _1;
      this._halign = halign;
      this._valign = valign;
      this._mode = mode;
      this.init();
   }

   @Override
   public void run() {
      if (!this._menu.isDisplayed()) {
         this._menu.show();
      }
   }

   private void init() {
      this._menu = new Menu(0);
      this._menu.setAlignment(this._halign, this._valign);
      this.this$0.addMenuItems(this._mode, this._menu);
   }
}
