package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.apps.api.framework.model.ContextObject;

final class GotoViewVerb extends CalendarViewerVerb {
   private int _view;
   private CalendarActions _calActions;
   private boolean _returnToPreviousView;

   public GotoViewVerb(int displayStringId, int order, int hotkeyResourceId, int view, CalendarActions calActions, boolean returnToPreviousView) {
      super(displayStringId, order, hotkeyResourceId);
      this._view = view;
      this._calActions = calActions;
      this._returnToPreviousView = returnToPreviousView;
   }

   @Override
   public final Object invoke(Object parameter) {
      boolean returnToMonthView = false;
      if (parameter instanceof ContextObject) {
         returnToMonthView = ((ContextObject)parameter).getFlag(87);
      }

      this._calActions.switchViews(this._view, returnToMonthView || this._returnToPreviousView);
      return null;
   }
}
