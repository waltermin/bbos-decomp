package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.util.Comparator;

final class DocViewAttachmentPersist$ElementComparator implements Comparator {
   private DocViewAttachmentPersist$ElementComparator() {
   }

   @Override
   public final int compare(Object o1, Object o2) {
      return ((AttachmentElementInfo)o1).getElementPartID() - ((AttachmentElementInfo)o2).getElementPartID();
   }

   DocViewAttachmentPersist$ElementComparator(DocViewAttachmentPersist$1 x0) {
      this();
   }
}
