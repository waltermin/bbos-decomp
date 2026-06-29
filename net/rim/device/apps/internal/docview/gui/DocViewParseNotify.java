package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;

public class DocViewParseNotify {
   protected void parseSucceeded(Field displayField) {
   }

   protected void parseFailed(byte errorCode) {
      int errorID = AttachmentViewerFactory.getParsingErrorID(errorCode);
      if (errorID != -1) {
         UiApplication.getUiApplication()
            .invokeLater(new ErrorDlg(ResourceBundle.getBundle(-4603212010799374808L, "net.rim.device.apps.internal.resource.DocView").getString(errorID)));
      }
   }
}
