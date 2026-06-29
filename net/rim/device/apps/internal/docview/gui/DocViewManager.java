package net.rim.device.apps.internal.docview.gui;

public final class DocViewManager {
   public static final void processData(
      String titleString, Object data, boolean pausable, int themeBgColor, int themeForeColor, boolean displayImageAsSlideshow, boolean isPresentation
   ) {
      byte presentationValue = 0;
      if (displayImageAsSlideshow) {
         presentationValue = (byte)(isPresentation ? 1 : 2);
      }

      AttachmentViewerFactory.processData(
         titleString,
         data,
         null,
         0,
         1,
         1,
         0,
         65535,
         themeBgColor,
         themeForeColor,
         false,
         pausable,
         null,
         null,
         true,
         0,
         displayImageAsSlideshow,
         presentationValue
      );
   }

   public static final void processData(
      Object data,
      boolean pausable,
      int themeBgColor,
      int themeForeColor,
      DocViewParseNotify parseNotify,
      DocViewNotify notifyObject,
      boolean displayImageAsSlideshow,
      boolean isPresentation
   ) {
      byte presentationValue = 0;
      if (displayImageAsSlideshow) {
         presentationValue = (byte)(isPresentation ? 1 : 2);
      }

      AttachmentViewerFactory.processData(
         null,
         data,
         null,
         0,
         1,
         1,
         0,
         65535,
         themeBgColor,
         themeForeColor,
         false,
         pausable,
         parseNotify,
         notifyObject,
         false,
         0,
         displayImageAsSlideshow,
         presentationValue
      );
   }
}
