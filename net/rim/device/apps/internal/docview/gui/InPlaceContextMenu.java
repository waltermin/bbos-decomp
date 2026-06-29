package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.container.InPlaceScreen;

final class InPlaceContextMenu extends InPlaceScreen {
   private CustomMenuField _choice;
   private int _xpos;
   private int _ypos;

   InPlaceContextMenu(int xpos, int ypos, CustomMenuField choice) {
      super(null, choice, 1);
      this._choice = choice;
      this._xpos = xpos;
      this._ypos = ypos;
   }

   @Override
   protected final void sublayout(int width, int height) {
      this.layoutDelegate(DocViewGUIInternalConstants.SCREEN_WIDTH, DocViewGUIInternalConstants.SCREEN_HEIGHT);
      this.setPosition(this._xpos, this._ypos);
      this.setExtent(this._choice.getWidth(), this._choice.getHeight());
   }
}
