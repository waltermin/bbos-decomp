package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.apps.api.ui.AppsMainScreen;

public class QmMainScreen extends AppsMainScreen {
   public static final int MENU_INSTANCE_DEFAULT = 0;
   public static final int QM_ACTION_INVOKE = 1;

   public QmMainScreen(long style) {
      super(style);
   }

   public void invokeVerb(int _1) {
      throw null;
   }

   public void invokeVerbSpecial(int _1) {
      throw null;
   }

   boolean internalTrackwheelClick(int status, int time) {
      return this.trackwheelClick(status, time);
   }

   boolean internalTrackwheelRoll(int amount, int status, int time) {
      return this.trackwheelRoll(amount, status, time);
   }
}
