package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.cldc.util.CalendarExtensions;

final class InPlaceEventDialog extends Screen {
   private HorizontalFieldManager _hfm;
   private InPlaceEventDialog$StartAndDurationField _startDur = new InPlaceEventDialog$StartAndDurationField();
   private InPlaceEventDialog$InPlaceEventDialogVerticalFieldManager _vfm = new InPlaceEventDialog$InPlaceEventDialogVerticalFieldManager(1152921504606846976L);
   private VerticalFieldManager _vfmEdit = (VerticalFieldManager)(new Object(1153202979583557632L));
   private AutoTextEditField _edit = (AutoTextEditField)(new Object(null, null));
   private LabelField _label = (LabelField)(new Object(null, 1152921504606846976L));
   private Calendar _cal = Calendar.getInstance();
   private CalendarExtensions _calEx = (CalendarExtensions)this._cal;
   private DateFormat _df = DateFormat.getInstance(48);
   private StringBuffer _otherDateString = (StringBuffer)(new Object());
   private boolean _userCancelled = true;
   private int _xTopLeft;
   private int _yTopLeft;
   private int _initialKeycode;
   private static final int OUTSIDE_SPACE = 1;
   private static final int INSIDE_SPACE = 2;
   private static final int TOTAL_BORDER = 3;
   private static final int START_TIME_INCREMENT = 900000;
   private static final int BIG_DURATION_INCREMENT = 3600000;
   private static final int SMALL_DURATION_INCREMENT = 900000;
   private static final int DURATION_BOUNDARY = 7200000;

   public InPlaceEventDialog(int xTopLeft, int yTopLeft) {
      super((Manager)(new Object(0)));
      this._xTopLeft = xTopLeft;
      this._yTopLeft = yTopLeft;
      this._hfm = (HorizontalFieldManager)this.getDelegate();
      this._hfm.add(this._startDur);
      this._hfm.add(this._vfm);
      this._vfm.add(this._vfmEdit);
      this._vfmEdit.add(this._edit);
      this._vfm.add(this._label);
      this._edit.setFocus();
   }

   @Override
   protected final void onFocusNotify(boolean focus) {
      super.onFocusNotify(focus);
      if (focus && this._initialKeycode != 0) {
         this.processKeyEvent(513, '\u0000', this._initialKeycode, 0);
         this._initialKeycode = 0;
      }
   }

   final void setStart(long start) {
      this._startDur.setStart(start);
   }

   final long getStart() {
      return this._startDur.getStart();
   }

   final void setDuration(long dur) {
      this._startDur.setDuration(dur);
   }

   final long getDuration() {
      return this._startDur.getDuration();
   }

   final void seedEventDescription(int initialKeycode, char initialChar) {
      this._initialKeycode = initialKeycode;
   }

   final String getEventDescription() {
      return this._edit.getText();
   }

   private final void updateLabel() {
      Calendar cal = this._cal;
      CalendarExtensions calEx = this._calEx;
      calEx.setTimeLong(this._startDur.getStart());
      int sYear = cal.get(1);
      int sDOY = cal.get(6);
      calEx.setTimeLong(this._startDur.getStart() + this._startDur.getDuration());
      int eYear = cal.get(1);
      int eDOY = cal.get(6);
      this._otherDateString.setLength(0);
      if (sYear != eYear || sDOY != eDOY) {
         this._df.format(cal, this._otherDateString, null);
      }

      this._label.setText(this._otherDateString);
   }

   final boolean userCancelled() {
      return this._userCancelled;
   }

   @Override
   protected final void sublayout(int width, int height) {
      int startDurWidth = this._startDur.getPreferredWidth();
      int xPopup = Math.max(this._xTopLeft - startDurWidth - 3, 0);
      int clientWidth = width - 6 - xPopup;
      this._vfm.doLayout(clientWidth - startDurWidth, height);
      int childHeight = this._vfm.getHeight();
      this.layoutDelegate(clientWidth, childHeight);
      this.setPositionDelegate(3, 3);
      this.setExtent(this._hfm.getWidth() + 6, this._hfm.getHeight() + 6);
      this.setPosition(xPopup, Math.min(this._yTopLeft - 3, height - this.getHeight()));
   }

   @Override
   protected final void paint(Graphics graphics) {
      graphics.drawRoundRect(1, 1, this.getWidth() - 2, this.getHeight() - 2, 6, 6);
      super.paint(graphics);
   }

   @Override
   protected final boolean keyChar(char character, int status, int time) {
      if (character == '\n') {
         if (this._edit.getTextLength() > 0) {
            this._userCancelled = false;
         }

         UiApplication.getUiApplication().popScreen(this);
         return true;
      } else if (super.keyChar(character, status, time)) {
         return true;
      } else if (character == 27) {
         UiApplication.getUiApplication().popScreen(this);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      if (super.trackwheelClick(status, time)) {
         return true;
      }

      if (this._edit.getTextLength() > 0) {
         this._userCancelled = false;
      }

      UiApplication.getUiApplication().popScreen(this);
      return true;
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      boolean result = false;
      if (Trackball.isSupported() && dx == 0 && dy != 0) {
         int oldStatus = status;
         if ((status & 1) > 0) {
            status |= 2;
            status &= -2;
         }

         result = this.trackwheelRoll(dy, status, time);
         status = oldStatus;
      }

      if (!result) {
         result = super.navigationMovement(dx, dy, status, time);
      }

      return result;
   }

   @Override
   public final boolean trackwheelRoll(int amount, int status, int time) {
      if ((status & 8) != 0) {
         return super.trackwheelRoll(amount, status, time);
      }

      if ((status & 1) != 0) {
         if (this._edit.getTextLength() <= 0) {
            this._startDur.setStart(this._startDur.getStart() + amount * 900000);
            this.updateLabel();
            return true;
         } else {
            return super.trackwheelRoll(amount, status, time);
         }
      } else {
         if ((status & 2) != 0) {
            this._startDur.setStart(this._startDur.getStart() + amount * 900000);
            this.updateLabel();
            return true;
         }

         long duration = this._startDur.getDuration();
         duration += amount * 900000;
         if (duration < 0) {
            duration = 0;
         }

         this._startDur.setDuration(duration);
         this.updateLabel();
         return true;
      }
   }

   @Override
   protected final boolean stylusTap(int x, int y, int status, int time) {
      XYRect extent = this.getExtent();
      if (!extent.contains(extent.x, extent.y, x, y)) {
         UiApplication.getUiApplication().popScreen(this);
         return true;
      } else {
         return false;
      }
   }
}
