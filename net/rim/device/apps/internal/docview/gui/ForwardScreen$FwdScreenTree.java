package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.TreeField;
import net.rim.device.api.ui.component.TreeFieldCallback;
import net.rim.device.apps.api.messaging.MessageIcons;

final class ForwardScreen$FwdScreenTree extends TreeField {
   private final ForwardScreen this$0;

   public ForwardScreen$FwdScreenTree(ForwardScreen _1, TreeFieldCallback callback, long style) {
      super(callback, style);
      this.this$0 = _1;
   }

   final void redrawTree() {
      this.invalidate();
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      int retValue = super.moveFocus(amount, status, time);
      if (amount != 0) {
         this.this$0.checkTitleInformation(this.getCurrentNode(), false);
      }

      return retValue;
   }

   private final boolean defaultAction(boolean includeTreeRoots) {
      int currentNode = this.getCurrentNode();
      if (currentNode != -1) {
         if (this.getFirstChild(currentNode) == -1) {
            AttachmentElementInfo elemInfo = (AttachmentElementInfo)this.getCookie(currentNode);
            if (elemInfo.getElementPartID() != -2) {
               this.this$0.doActionOnSelectedNode(false);
               return true;
            }
         } else if (includeTreeRoots) {
            this.setExpanded(currentNode, !this.getExpanded(currentNode));
            return true;
         }
      }

      return false;
   }

   @Override
   protected final boolean keyChar(char character, int status, int time) {
      return character == '\n' && this.defaultAction(false) ? true : super.keyChar(character, status, time);
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            if (this.defaultAction(true)) {
               return true;
            }
         default:
            return false;
      }
   }

   @Override
   public final void setCurrentNode(int node) {
      super.setCurrentNode(node);
      this.this$0.checkTitleInformation(node, true);
   }

   @Override
   public final void getFocusRect(XYRect rect) {
      super.getFocusRect(rect);
      int focusNode = this.getCurrentNode();
      if (focusNode != -1) {
         boolean isChildSingleDoc = false;
         if (this.getFirstChild(focusNode) == -1) {
            if (this.getNodeDepth(focusNode) == 0) {
               rect.x = 0;
            } else {
               isChildSingleDoc = true;
            }
         } else {
            rect.x += 2;
         }

         boolean hasIcon = false;
         AttachmentElementInfo info = (AttachmentElementInfo)this.getCookie(focusNode);
         if (info.getElementPartID() != -2 && info.getElementState() != 0) {
            hasIcon = true;
         }

         rect.width = hasIcon ? this.getWidth() - rect.x - MessageIcons.getIcons().getWidth(this.getFont()) - (isChildSingleDoc ? 1 : 0) : this.getWidth();
      }
   }

   final int findNode(int iMorePartID, String archiveIndicator, int iAttachmentPartID) {
      for (int crtNode = this.nextNode(0, 0, true); crtNode != -1; crtNode = this.nextNode(crtNode, 0, true)) {
         AttachmentElementInfo info = (AttachmentElementInfo)this.getCookie(crtNode);
         if (info._morePartID == iMorePartID && info.matchArchiveIndicator(archiveIndicator) && info.getElementPartID() == iAttachmentPartID) {
            return crtNode;
         }
      }

      return -1;
   }

   private final int getNodeDepth(int node) {
      int depth = 0;

      while (true) {
         node = this.getParent(node);
         if (node == 0) {
            return depth;
         }

         depth++;
      }
   }
}
