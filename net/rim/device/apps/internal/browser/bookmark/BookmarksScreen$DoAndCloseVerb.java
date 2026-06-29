package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.ui.Screen;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.channel.OpenChannelVerb;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.verbs.FollowLinkVerb;

final class BookmarksScreen$DoAndCloseVerb extends Verb {
   private Verb _verb;
   private Screen _screen;
   private RIMModel _model;
   private final BookmarksScreen this$0;

   public BookmarksScreen$DoAndCloseVerb(BookmarksScreen _1, Verb verb, Screen screen, RIMModel model) {
      super(verb);
      this.this$0 = _1;
      this._verb = verb;
      this._screen = screen;
      this._model = model;
   }

   @Override
   public final String toString() {
      return this._verb.toString();
   }

   @Override
   public final Object invoke(Object parameter) {
      if (this._verb instanceof FollowLinkVerb || this._verb instanceof OpenChannelVerb) {
         if (this._model instanceof PageModel) {
            PageModel pageModel = (PageModel)this._model;
            pageModel.changeStatus(4);
            pageModel.setLastAccessedTime(System.currentTimeMillis());
            BrowserDaemonRegistry.broadCastEvent(102, pageModel);
         }

         this.this$0.setConfigFromModelOrCurrentFolder(this._model);
      }

      if (this._verb.invoke(parameter) != null || !(this._verb instanceof FollowLinkVerb)) {
         this._screen.close();
      }

      return null;
   }
}
