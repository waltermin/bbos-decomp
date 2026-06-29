package net.rim.device.api.ui;

import net.rim.device.api.util.MathUtilities;

class Screen$ViewFocusSelector implements Screen$FocusSelector {
   private int _amount;
   private int _status;
   private int _time;
   private Screen _screen;
   private boolean _success;
   private static Screen$ViewFocusSelector _selector = new Screen$ViewFocusSelector();

   @Override
   public void select() {
      Field fieldWithFocus = this._screen.getLeafFieldWithFocus();
      XYRect nextFocus = Ui.getTmpXYRect();
      XYRect nextFocusVisible = Ui.getTmpXYRect();
      fieldWithFocus.getNextFocus(this._amount > 0 ? 512 : 256, nextFocus);
      Manager manager = this._screen._delegate;

      while (!manager.isStyle(281474976710656L)) {
         Field field = manager.getFieldWithFocus();
         if (!(field instanceof Manager)) {
            break;
         }

         manager = (Manager)field;
      }

      nextFocusVisible.set(nextFocus);
      fieldWithFocus.transformToScreen(nextFocusVisible);
      if (nextFocusVisible.height < this._screen.getFont().getHeight() && (nextFocusVisible.height != nextFocus.height || nextFocus.height == 0)) {
         int scroll = manager.getVerticalScroll();
         scroll = MathUtilities.clamp(
            0, scroll + 2 * this._amount * Font.getDefault().getHeight(), Math.max(scroll, manager.getVirtualHeight() - manager.getHeight())
         );
         this._success = scroll != manager.getVerticalScroll();
         manager.setVerticalScroll(scroll);
         this._success = true;
         nextFocusVisible.set(nextFocus);
         fieldWithFocus.transformToScreen(nextFocusVisible);
         if (nextFocusVisible.height >= this._screen.getFont().getHeight() || nextFocusVisible.height == nextFocus.height && nextFocus.height != 0) {
            int remaining = this._screen._delegate.moveFocus(this._amount, this._status, this._time);
            if (remaining != 0) {
               System.out.println("Failed to move focus to a reported focus point.");
               this._success = false;
            }
         }
      } else {
         int remaining = this._screen._delegate.moveFocus(this._amount, this._status, this._time);
         this._success = true;
         if (remaining != 0) {
            System.out.println("Failed to move focus to a reported focus point.");
            this._success = false;
         }
      }

      Ui.returnTmpXYRect(nextFocus);
      Ui.returnTmpXYRect(nextFocusVisible);
   }

   public final boolean getSuccess() {
      return this._success;
   }

   public static Screen$ViewFocusSelector getSelector(Screen screen, int amount, int status, int time) {
      _selector._screen = screen;
      _selector._amount = amount;
      _selector._status = status;
      _selector._time = time;
      _selector._success = false;
      return _selector;
   }
}
