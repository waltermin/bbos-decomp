package net.rim.device.apps.internal.profiles;

import net.rim.device.api.ui.component.ListField;

final class ProfilesScreen$UpdateRunnable implements Runnable {
   private int _listSize;
   private int _selectedIndex;
   private ListField _runnableListField;

   final synchronized void setAction(ListField listField, int listSizeInt, int selectedIndexInt) {
      this._runnableListField = listField;
      this._listSize = listSizeInt;
      this._selectedIndex = selectedIndexInt;
   }

   @Override
   public final void run() {
      if (this._runnableListField != null) {
         this._runnableListField.setSize(this._listSize, this._selectedIndex);
      }
   }
}
