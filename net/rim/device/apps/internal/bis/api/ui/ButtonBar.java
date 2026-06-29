package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.container.HorizontalFieldManager;

final class ButtonBar extends HorizontalFieldManager {
   private Button _firstFocusButton;
   public static final int CLOSE_BUTTON = 1;
   public static final int BACK_BUTTON = 2;
   public static final int NEXT_BUTTON = 4;
   private static final int BUTTON_FONT_SIZE_PT = 5;

   public ButtonBar() {
      super(12884901888L);
      this.resetFonts();
   }

   public final void resetFonts() {
      int fontSize = Ui.convertSize(5, 3, 0);
      this.setFont(Font.getDefault().derive(1, fontSize));
   }

   public final void setFirstFocusButton(Button firstFocusButton) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected final int firstFocus(int direction) {
      int focusIndex = 0;
      if (this._firstFocusButton != null && this._firstFocusButton.getIndex() > -1) {
         focusIndex = this._firstFocusButton.getIndex();
      }

      return focusIndex;
   }
}
