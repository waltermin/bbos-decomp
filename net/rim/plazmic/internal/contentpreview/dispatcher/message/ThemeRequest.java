package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class ThemeRequest extends Model {
   private String _themeName;

   public ThemeRequest(String themeName) {
      this._themeName = themeName;
   }

   public final String getThemeName() {
      return this._themeName;
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.themeRequest(this._themeName);
   }

   @Override
   final String getClassName() {
      return "ThemeRequest";
   }

   @Override
   final String getProperties() {
      return this.toPropertyString("themeName", this._themeName);
   }
}
