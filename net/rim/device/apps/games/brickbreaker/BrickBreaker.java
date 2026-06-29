package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.ui.UiApplication;

final class BrickBreaker extends UiApplication implements HolsterListener {
   MenuScreen _menuScreen = new MenuScreen();
   Game _game;
   public static final String NAME = "BrickBreaker";
   public static final String VERSION = "4.1";

   public static final void main(String[] args) {
      BrickBreaker app = new BrickBreaker();
      app.enterEventDispatcher();
   }

   BrickBreaker() {
      this.pushScreen(this._menuScreen);
      this.invokeLater(new BrickBreaker$1(this));
      this.addHolsterListener(this);
   }

   @Override
   public final void inHolster() {
      this._game.pause();
      this.requestBackground();
   }

   @Override
   public final void outOfHolster() {
   }

   @Override
   public final void activate() {
   }

   @Override
   public final void deactivate() {
      this._game.pause();
   }
}
