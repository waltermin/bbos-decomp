package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;

final class QueuePage$DeleteRequestVerb extends Verb {
   private String _queue;
   private PageModel _pageModel;

   public QueuePage$DeleteRequestVerb(String queue, PageModel pageModel) {
      super(341248, -8414468493733347764L, "net.rim.device.apps.internal.resource.Common", 1000);
      this._queue = queue;
      this._pageModel = pageModel;
   }

   @Override
   public final Object invoke(Object context) {
      if (Dialog.ask(2, CommonResources.getString(3000)) == 3) {
         BrowserDaemonRegistry.getInstance().getOfflineQueue().removeItem(this._queue, this._pageModel);
      }

      return null;
   }
}
