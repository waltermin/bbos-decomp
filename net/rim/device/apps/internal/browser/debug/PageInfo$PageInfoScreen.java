package net.rim.device.apps.internal.browser.debug;

import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.ui.VerbMenuItem;

class PageInfo$PageInfoScreen extends MainScreen {
   private final PageInfo this$0;

   public PageInfo$PageInfoScreen(PageInfo _1) {
      this.this$0 = _1;
   }

   public PageInfo$PageInfoScreen(PageInfo _1, long style) {
      super(style);
      this.this$0 = _1;
   }

   @Override
   protected void makeMenu(Menu menu, int context) {
      super.makeMenu(menu, context);
      DebugInfoSendVerb sendVerb = new DebugInfoSendVerb(this.this$0._data, "BSM Debug Info: " + this.this$0._url);
      menu.add(new VerbMenuItem(sendVerb, 100));
   }
}
