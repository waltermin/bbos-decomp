package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.UiApplication;

final class QmRenderScreen$1 extends Thread {
   private final QmRenderScreen val$thisScreen;
   private final QmRenderScreen this$0;

   QmRenderScreen$1(QmRenderScreen _1, QmRenderScreen _2) {
      this.this$0 = _1;
      this.val$thisScreen = _2;
   }

   @Override
   public final void run() {
      QmRenderScreen.access$002(this.this$0, QmRenderScreen.access$200(this.this$0, QmRenderScreen.access$100(this.this$0)));
      if (QmRenderScreen.access$000(this.this$0) == null) {
         QmRenderScreen.access$002(this.this$0, QmRenderScreen.access$300(this.this$0));
      }

      Thread t = new QmRenderScreen$1$1(this);
      UiApplication.getUiApplication().invokeLater(t);
   }

   static final QmRenderScreen access$400(QmRenderScreen$1 x0) {
      return x0.this$0;
   }

   static final QmRenderScreen access$500(QmRenderScreen$1 x0) {
      return x0.val$thisScreen;
   }
}
