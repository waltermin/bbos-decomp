package net.rim.device.apps.internal.browser.plugin.media.field;

import net.rim.plazmic.internal.mediaengine.service.node.RectNode;
import net.rim.plazmic.internal.mediaengine.service.node.ViewportNode;
import net.rim.plazmic.internal.mediaengine.service.node.VisualNode;

final class SeekablePositionField$ResizableVisualNode {
   private final RectNode _rectNode;
   private final ViewportNode _viewNode;

   private SeekablePositionField$ResizableVisualNode(VisualNode visualNode) {
      if (!(visualNode instanceof RectNode)) {
         if (!(visualNode instanceof ViewportNode)) {
            this._viewNode = null;
            this._rectNode = null;
         } else {
            ViewportNode viewNode = (ViewportNode)visualNode;
            this._viewNode = viewNode;
            this._rectNode = null;
         }
      } else {
         RectNode rectNode = (RectNode)visualNode;
         this._rectNode = rectNode;
         this._viewNode = null;
      }

      this.setAspectRatio(0);
   }

   private final void setX(int x) {
      if (this._rectNode != null) {
         this._rectNode.setX(x);
      } else {
         if (this._viewNode != null) {
            this._viewNode.setX(x);
         }
      }
   }

   private final void setWidth(int w) {
      if (this._rectNode != null) {
         this._rectNode.setWidth(w);
      } else {
         if (this._viewNode != null) {
            this._viewNode.setWidth(w);
         }
      }
   }

   private final int getWidth() {
      if (this._rectNode != null) {
         return this._rectNode.getWidth();
      } else {
         return this._viewNode != null ? this._viewNode.getWidth() : 0;
      }
   }

   private final void setAspectRatio(int r) {
      if (this._viewNode != null) {
         this._viewNode.setAspectRatio(r);
      }
   }

   SeekablePositionField$ResizableVisualNode(VisualNode x0, SeekablePositionField$1 x1) {
      this(x0);
   }
}
