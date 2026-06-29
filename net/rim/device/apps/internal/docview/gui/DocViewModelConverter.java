package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.attachment.AttachmentViewerModelConverter;

final class DocViewModelConverter extends AttachmentViewerModelConverter {
   @Override
   public final boolean canConvert(Object parameters) {
      if (parameters instanceof Object) {
         String string = (String)parameters;
         if (StringUtilities.compareToIgnoreCase(string, "application/x-rimdeviceucs", 1701707776) == 0
            || StringUtilities.compareToIgnoreCase(string, "application/x-rimdevicezipucs", 1701707776) == 0) {
            return true;
         }
      }

      return super.canConvert(parameters);
   }
}
