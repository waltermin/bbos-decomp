package net.rim.device.apps.internal.browser.page;

import net.rim.device.apps.internal.browser.verbs.FollowLinkWithContextVerb;

final class QueuePage$ViewRequestVerb extends FollowLinkWithContextVerb {
   private PageModel _pageModel;

   public QueuePage$ViewRequestVerb(PageModel pageModel) {
      super(pageModel);
      this._pageModel = pageModel;
   }

   @Override
   public final Object invoke(Object context) {
      if (this._pageModel.getStatus() == 3) {
         this._pageModel.changeStatus(4);
      }

      return super.invoke(context);
   }
}
