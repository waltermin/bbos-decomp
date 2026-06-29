package net.rim.device.apps.internal.docview.gui;

import net.rim.device.internal.ui.component.SimpleInputDialog;

final class DocViewSheetDisplayField$GoToCellDlg extends SimpleInputDialog {
   public DocViewSheetDisplayField$GoToCellDlg(String prompt) {
      super(0, prompt);
      this.getEditField().setFilter(new DocViewSheetDisplayField$GoToCellDlg$AlphaNumericFilter());
   }
}
