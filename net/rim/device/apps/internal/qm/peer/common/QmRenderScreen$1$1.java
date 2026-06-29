package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.UiApplication;

final class QmRenderScreen$1$1 extends Thread {
   private final QmRenderScreen$1 this$1;

   QmRenderScreen$1$1(QmRenderScreen$1 _1) {
      this.this$1 = _1;
   }

   @Override
   public final void run() {
      QmRenderScreen$1.access$400(this.this$1).getDelegate().add(QmRenderScreen.access$000(QmRenderScreen$1.access$400(this.this$1)));
      UiApplication.getUiApplication().pushModalScreen(QmRenderScreen$1.access$500(this.this$1));
   }
}
