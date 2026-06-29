package net.rim.device.internal.EScreens;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.container.MainScreen;

public final class EScreenVMStats extends MainScreen {
   private ObjectListField _list;
   private EScreenMemStatsRefresher[] _refreshers;
   private static final String[] _titles = new String[]{
      "Processes", "RAM Usage", "Flash Usage", "Object Usage", "Code Modules", "Transient Objects in flash", "Persistent Objects in flash"
   };
   private static final int Processes = 0;
   private static final int RAM = 1;
   private static final int Flash = 2;
   private static final int Object = 3;
   private static final int Code = 4;
   private static final int Transient = 5;
   private static final int Persistent = 6;
   private static final int NUM_STATS = 7;

   public EScreenVMStats(Font font) {
      this.setFont(font);
      this._list = (ObjectListField)(new Object());
      this._list.set(_titles);
      this._list.setSelectedIndex(0);
      this.add((Field)(new Object("Bundle: 199")));
      this.add(this._list);
      this.setTitle("Java VM Engineering Screens");
      this._refreshers = new EScreenMemStatsRefresher[7];
      this._refreshers[1] = new EScreenVMStats$1(this);
      this._refreshers[2] = new EScreenVMStats$2(this);
      this._refreshers[3] = new EScreenVMStats$3(this);
      this._refreshers[4] = new EScreenVMStats$4(this);
      this._refreshers[5] = new EScreenVMStats$5(this);
      this._refreshers[6] = new EScreenVMStats$6(this);
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      boolean result = super.keyChar(c, status, time);
      return !result && c == '\n' ? this.chooseScreen() : result;
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      return this.chooseScreen();
   }

   private final boolean chooseScreen() {
      int index = this._list.getSelectedIndex();
      switch (index) {
         case -1:
            return false;
         case 0:
         default:
            Ui.getUiEngine().pushScreen(new EScreenVMProcesses(this.getFont()));
            return true;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
            Ui.getUiEngine().pushScreen(new EScreenVMMemStats(_titles[index], this._refreshers[index], this.getFont()));
            return true;
      }
   }
}
