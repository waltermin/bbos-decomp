package net.rim.device.apps.internal.calendar.viewer;

final class CalendarOptionsScreen$ViewChoice {
   private String _viewName;
   private int _view;

   public CalendarOptionsScreen$ViewChoice(String viewName, int view) {
      this._viewName = viewName;
      this._view = view;
   }

   @Override
   public final String toString() {
      return this._viewName;
   }

   public final int getView() {
      return this._view;
   }
}
