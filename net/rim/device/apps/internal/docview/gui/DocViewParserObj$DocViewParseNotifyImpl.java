package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;

final class DocViewParserObj$DocViewParseNotifyImpl extends DocViewParseNotify {
   private final DocViewParserObj this$0;

   DocViewParserObj$DocViewParseNotifyImpl(DocViewParserObj _1) {
      this.this$0 = _1;
   }

   @Override
   protected final void parseSucceeded(Field displayField) {
      UiApplication.getUiApplication().invokeLater(new DocViewParserObj$DocViewParseNotifyImpl$1(this, displayField), 450, false);
   }
}
