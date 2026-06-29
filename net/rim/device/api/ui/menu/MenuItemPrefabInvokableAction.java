package net.rim.device.api.ui.menu;

import net.rim.device.api.ui.InvokableAction;

final class MenuItemPrefabInvokableAction extends MenuItemPrefab {
   private InvokableAction _action;

   MenuItemPrefabInvokableAction(InvokableAction action) {
      super(action.getActionId());
      this._action = action;
   }

   @Override
   public final void run() {
      this._action.actionPerformed(this);
   }
}
