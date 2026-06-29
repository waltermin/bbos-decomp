package net.rim.device.apps.internal.manageconnections;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;

final class MyHorizontalFieldManager extends HorizontalFieldManager {
   private LabelField _status;
   private Field _indicator;
   private CheckboxField _toggle;

   public MyHorizontalFieldManager(CheckboxField toggle, LabelField status, Field indicator) {
      super(1152921504606846976L);
      if (toggle != null && status != null && indicator != null) {
         this._toggle = toggle;
         this._status = status;
         this._indicator = indicator;
         this.add(toggle);
         this.add(status);
         this.add(indicator);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   protected final void sublayout(int maxWidth, int maxHeight) {
      super.sublayout(maxWidth, maxHeight);
      int indicatorWidth = this._indicator.getPreferredWidth();
      int managerHeight = Math.max(Math.max(this._toggle.getPreferredHeight(), this._status.getPreferredHeight()), this._indicator.getPreferredHeight());
      int statusTop = (managerHeight - this._status.getPreferredHeight()) / 2;
      int toggleTop = (managerHeight - this._toggle.getPreferredHeight()) / 2;
      int indicatorTop = (managerHeight - this._indicator.getPreferredHeight()) / 2;
      int indicatorLeft = maxWidth - indicatorWidth;
      int padding = this._toggle.getPaddingRight() + this._toggle.getMarginRight() + this._status.getPaddingLeft() + this._status.getMarginLeft();
      if (padding < 5) {
         padding = 5;
      }

      int toggleWidth = this._toggle.getPreferredWidth();
      if (toggleWidth + padding > indicatorLeft) {
         toggleWidth = indicatorLeft - padding;
      }

      int statusLeft = toggleWidth + padding;
      int statusWidth = indicatorLeft - statusLeft;
      if (statusWidth > 0) {
         Font font = this.getFont();
         String statusString = this._status.getText();
         int minimalStatusWidth;
         if (statusString.length() > 4) {
            StringBuffer buffer = new StringBuffer(statusString.substring(0, 4));
            buffer.append('…');
            minimalStatusWidth = font.measureText(buffer, 0, buffer.length(), null, Ui.getTmpTextMetrics());
         } else {
            minimalStatusWidth = font.measureText(statusString, 0, statusString.length(), null, Ui.getTmpTextMetrics());
         }

         if (minimalStatusWidth > statusWidth) {
            statusWidth = 0;
            statusLeft = indicatorLeft;
         }
      } else {
         statusWidth = 0;
         statusLeft = indicatorLeft;
      }

      this.layoutChild(this._toggle, toggleWidth, maxHeight);
      this.setPositionChild(this._toggle, this._toggle.getLeft(), toggleTop);
      this.layoutChild(this._status, statusWidth, maxHeight);
      this.setPositionChild(this._status, statusLeft, statusTop);
      this.layoutChild(this._indicator, indicatorWidth, maxHeight);
      this.setPositionChild(this._indicator, maxWidth - indicatorWidth, indicatorTop);
      this.setExtent(maxWidth, Math.max(this._status.getHeight(), Math.max(this._toggle.getHeight(), this._indicator.getHeight())));
   }
}
