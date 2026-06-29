package net.rim.device.internal.EScreens;

import net.rim.device.api.system.MemoryStats;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.container.MainScreen;

class EScreenVMMemStats extends MainScreen {
   protected MemoryStats _stats;
   private ObjectListField _list;
   EScreenMemStatsRefresher _refresher;
   private static final int STAT_ALLOC = 0;
   private static final int STAT_FREE = 1;
   private static final int STAT_OBJECTS = 2;
   private static final int STAT_OBJECT_SIZE = 3;
   private static final int NUM_STATS = 4;
   private static final int MENU_QUICK_GC = 1;
   private static final int MENU_FULL_GC = 2;
   private static final int MENU_THOROUGH_GC = 3;

   public EScreenVMMemStats(String title, EScreenMemStatsRefresher refresher, Font font) {
      this.setFont(font);
      this._refresher = refresher;
      this._stats = new MemoryStats();
      this._list = new ObjectListField();
      this.add(this._list);
      this.setTitle(title);
   }

   int addDigits(int i, int originalNumber, int magnitude, StringBuffer sb) {
      if (i > magnitude) {
         int digits = i / magnitude;
         if (i != originalNumber) {
            sb.append(',');
            if (digits < 10) {
               sb.append("00");
            } else if (digits < 100) {
               sb.append("0");
            }
         }

         i -= digits * magnitude;
         sb.append(Integer.toString(digits));
         return i;
      } else {
         return i;
      }
   }

   String fmt(String prefix, int i) {
      StringBuffer sb = new StringBuffer();
      sb.append(prefix);
      if (i <= 0) {
         sb.append(Integer.toString(i));
      } else {
         int j = i;
         j = this.addDigits(j, i, 1000000, sb);
         j = this.addDigits(j, i, 1000, sb);
         j = this.addDigits(j, i, 1, sb);
      }

      return sb.toString();
   }

   public void refresh() {
      this._refresher.refresh(this._stats);
      String[] data = new String[]{
         this.fmt("Allocated: ", this._stats.getAllocated()),
         this.fmt("Free: ", this._stats.getFree()),
         this.fmt("Objects: ", this._stats.getObjectCount()),
         this.fmt("Object Size: ", this._stats.getObjectSize())
      };
      this._list.set(data);
      this._list.invalidate();
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      menu.add(new EScreenVMMemStats$MyMenu(this, "Quick GC", 1));
      menu.add(new EScreenVMMemStats$MyMenu(this, "Full GC", 2));
      menu.add(new EScreenVMMemStats$MyMenu(this, "Thorough GC", 3));
      super.makeMenu(menu, instance);
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this.refresh();
      }
   }

   @Override
   protected boolean keyChar(char c, int status, int time) {
      boolean result = super.keyChar(c, status, time);
      if (!result && c == 'r') {
         this.refresh();
         return true;
      } else {
         return result;
      }
   }
}
