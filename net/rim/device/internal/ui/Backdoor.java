package net.rim.device.internal.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;

public class Backdoor implements Runnable {
   private Menu _menu;
   private static final int DUMP_SCREEN = 1;
   private static final int KEYSTROKE_TIMING = 2;
   private static final int REPAINT = 3;
   private static final int TIME_REPAINT = 4;
   private static final int CACHE_STATS = 5;
   private static final int CLEAR_CACHE_STATS = 6;
   public static final int REPEAT_COUNT = 100;
   private static final String ui_package = "net.rim.device.api.ui.";
   private static String[] CACHE_STAT_NAMES = new String[]{
      "Hits: ", "Misses: ", "Cur Num Cached: ", "Flush Count: ", "Avg Bm Size: ", "Avg Bm Count when flushed: ", "Bms Added: ", "Cache size: ", "Free: "
   };

   @Override
   public void run() {
      this._menu = new Menu();
      this._menu.add(new Backdoor$MyMenuItem(this, "Dump Screen", 1));
      this._menu.add(new Backdoor$MyMenuItem(this, "Time Keystrokes", 2));
      this._menu.add(new Backdoor$MyMenuItem(this, "Repaint Screen", 3));
      this._menu.add(new Backdoor$MyMenuItem(this, "Time Repaint", 4));
      this._menu.add(new Backdoor$MyMenuItem(this, "Dump Cache Stats", 5));
      this._menu.add(new Backdoor$MyMenuItem(this, "Clear Cache Stats", 6));
      this._menu.show();
   }

   private void timeRepaint() {
      Screen scr = UiApplication.getUiApplication().getActiveScreen();
      if (scr != null) {
         long start = System.currentTimeMillis();

         for (int i = 0; i < 100; i++) {
            UiApplication.getUiApplication().repaint();
         }

         long end = System.currentTimeMillis();
         long elapsed = end - start;
         Dialog.inform("100 iterations in " + sayTime(elapsed) + "s. " + ' ' + sayTime(elapsed * 1000 / 100) + "ms per iteration.");
      }
   }

   private String styleToString(long style) {
      String str = "";
      if ((style & 1152921504606846976L) > 0) {
         str = str + "AW ";
      }

      if ((style & 2305843009213693952L) > 0) {
         str = str + "AH ";
      }

      if ((style & 18014398509481984L) > 0) {
         str = str + "FOCUS ";
      }

      if ((style & 281474976710656L) > 0) {
         str = str + "VS ";
      }

      if ((style & 17592186044416L) > 0) {
         str = str + "VSB ";
      }

      if ((style & 1125899906842624L) > 0) {
         str = str + "HS ";
      }

      if ((style & 70368744177664L) > 0) {
         str = str + "HSB ";
      }

      return str.trim();
   }

   private String formatClassName(Object obj) {
      String name = obj.getClass().getName();
      if (name.length() > 22 && name.substring(0, 22).equals("net.rim.device.api.ui.")) {
         name = name.substring(22);
      }

      return name;
   }

   private void dumpField(Field field, String indent, boolean focus) {
      String f = focus ? "-" : " ";
      f = f + (40 + field.getIndex()) + ") ";
      String name = this.formatClassName(field);
      String styles = this.styleToString(field.getStyle());
      String virtualExtent = "";
      if (field instanceof Manager) {
         Manager mgr = (Manager)field;
         virtualExtent = " (" + mgr.getVirtualWidth() + ',' + mgr.getVirtualHeight() + ')';
      }

      System.out
         .println(
            indent
               + f
               + name
               + " ("
               + field.getLeft()
               + ','
               + field.getTop()
               + ','
               + field.getWidth()
               + ','
               + field.getHeight()
               + ')'
               + virtualExtent
               + " ["
               + styles
               + ']'
         );
   }

   private void validateManager(Manager manager, String indent, boolean focusState) {
      this.dumpField(manager, indent, focusState);
      indent = indent + ' ';
      System.out.println(indent + "  (focus index is " + manager.getFieldWithFocusIndex() + ')');
      int fieldCount = manager.getFieldCount();
      Field focus = manager.getFieldWithFocus();

      for (int i = 0; i < fieldCount; i++) {
         Field field = manager.getField(i);
         if (!(field instanceof Manager)) {
            this.dumpField(field, indent, field == focus);
         } else {
            Manager m = (Manager)field;
            this.validateManager(m, indent, field == focus);
         }
      }
   }

   private void validate(Screen screen) {
      System.out.println("Dumping screen: " + this.formatClassName(screen));
      this.validateManager(screen.getDelegate(), "", true);
   }

   static String sayTime(long time) {
      int secs = (int)(time / 1000);
      int millis = (int)(time - secs * 1000);
      String suffix;
      if (millis < 10) {
         suffix = ".00" + millis;
      } else if (millis < 100) {
         suffix = ".0" + millis;
      } else {
         suffix = "." + millis;
      }

      return "" + secs + suffix;
   }

   private void dumpCacheStats() {
      int[] stats = UiInternal.getCacheStatistics();
      StringBuffer strBuf = new StringBuffer(128);
      strBuf.append("BM Cache Stats\n");
      if (stats != null) {
         for (int i = 0; i < CACHE_STAT_NAMES.length; i++) {
            strBuf.append(CACHE_STAT_NAMES[i]);
            strBuf.append(stats[i]);
            strBuf.append('\n');
         }
      } else {
         strBuf.append("Unable to retrieve");
      }

      Dialog.inform(strBuf.toString());
   }
}
