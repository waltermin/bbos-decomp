package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.internal.qm.peer.common.Request;

final class ContactTreeField$NewRequestLeaf extends Field implements TreeItem, BranchLeaf {
   private Tag TAG;
   private final ContactTreeField this$0;

   ContactTreeField$NewRequestLeaf(ContactTreeField _1, Request request) {
      this.this$0 = _1;
      this.TAG = Tag.create("bbmessenger-newrequestleaf");
      this.setTag(this.TAG);
      this.setCookie(request);
   }

   @Override
   public final void doInvalidate(boolean andRelated) {
      this.invalidate();
   }

   @Override
   public final int getPreferredHeight() {
      return Math.max(ContactTreeField.access$2000(this.this$0), ContactTreeField.access$1600(this.this$0));
   }

   @Override
   protected final void layout(int width, int height) {
      height = this.getPreferredHeight();
      this.setExtent(width, height);
   }

   @Override
   public final boolean isFocusable() {
      return true;
   }

   @Override
   protected final void paint(Graphics graphics) {
      PeerRequest request = (PeerRequest)this.getCookie();
      this.this$0.paintNewRequest(graphics, request, this);
   }
}
