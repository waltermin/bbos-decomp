package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.system.TrackwheelListener;
import net.rim.device.api.ui.Trackball;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.internal.system.Events;

final class PreListenerScreen extends AppsMainScreen {
   private CalendarViewController _tl1;

   PreListenerScreen(CalendarViewController tl1) {
      super(562949953421312L);
      this.setDefaultClose(false);
      this._tl1 = tl1;
   }

   @Override
   public final boolean dispatchTrackwheelEvent(int event, int magnitude, int status, int time) {
      boolean handled = false;
      handled |= Events.dispatchTrackwheelEvent(event, magnitude, status, time, this._tl1);
      if (!handled) {
         handled |= super.dispatchTrackwheelEvent(event, magnitude, status, time);
      }

      return handled;
   }

   @Override
   protected final boolean trackwheelRoll(int amount, int status, int time) {
      boolean result = false;
      if (this._tl1 instanceof TrackwheelListener) {
         TrackwheelListener twl = this._tl1;
         result = twl.trackwheelRoll(amount, status, time);
      }

      if (!result) {
         result = super.trackwheelRoll(amount, status, time);
      }

      return result;
   }

   @Override
   public final boolean dispatchKeyEvent(int event, char key, int status, int time) {
      return key == '\uffff' ? true : super.dispatchKeyEvent(event, key, status, time);
   }

   @Override
   protected final ContextObject getMenuContextObject() {
      ContextObject context = new ContextObject();
      context.put(244, "calendar");
      return context;
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this._tl1.invokeAction(action);
            return true;
         default:
            return super.invokeAction(action);
      }
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      boolean result = false;
      if (Trackball.isSupported()) {
         result = this._tl1.navigationMovement(dx, dy, status, time);
      }

      if (!result) {
         result = super.navigationMovement(dx, dy, status, time);
      }

      return result;
   }

   @Override
   protected final Object invokeVerb(Verb verb, Object parameter) {
      return this._tl1.invokeVerb(verb, parameter);
   }

   @Override
   protected final boolean keyControl(char c, int status, int time) {
      if (!super.keyControl(c, status, time) && (status & 1) == 0) {
         long lastTimeViewed = this._tl1.getSelectedStartTime();
         switch (c) {
            case '\u0080':
               break;
            case '\u0081':
               return this._tl1.onUp(time, lastTimeViewed, this);
            case '\u0082':
               return this._tl1.onDown(time, lastTimeViewed, this);
            case '\u0083':
            default:
               return this._tl1.onLeft(time, lastTimeViewed, this);
            case '\u0084':
               return this._tl1.onRight(time, lastTimeViewed, this);
         }
      }

      return false;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      if (instance == 0) {
         this._tl1.populateMenu(menu);
      }
   }
}
