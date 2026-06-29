package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.apps.internal.bis.ApplicationResources;

public class Event {
   private String _label;
   private boolean _onMenu = true;

   public Event() {
   }

   public Event(int rbID) {
      this._label = ApplicationResources.getString(rbID);
   }

   public String getLabel() {
      return this._label;
   }

   public boolean isOnMenu() {
      return this._onMenu;
   }

   public void setOnMenu(boolean onMenu) {
      this._onMenu = onMenu;
   }
}
