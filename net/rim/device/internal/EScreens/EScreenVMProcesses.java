package net.rim.device.internal.EScreens;

import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.vm.Process;

public final class EScreenVMProcesses extends MainScreen implements Comparator, Runnable, ListFieldCallback {
   private long _recentTimeWindow;
   private long _totalUtilizedTime;
   private int _timerId = -1;
   private ListField _list;
   private EScreenVMProcesses$EScreenProcess[] _processes = new EScreenVMProcesses$EScreenProcess[0];
   private EScreenVMProcesses$EScreenProcess[] _drawnProcesses;
   private static final int MENU_REFRESH = 1;
   private static final int MENU_KILL_PROCESS = 2;
   private static final int MENU_NO_CPU = 3;
   private static final int MENU_TOP_CPU = 4;
   private static final int MENU_TOTAL_CPU = 5;
   private static final int MENU_AUTO_REFRESH = 6;
   private static int _displayType = 4;

   @Override
   public final int compare(Object p1, Object p2) {
      return ((EScreenVMProcesses$EScreenProcess)p1).compareTo((EScreenVMProcesses$EScreenProcess)p2);
   }

   public EScreenVMProcesses(Font font) {
      this.setFont(font);
      this._list = (ListField)(new Object());
      this._list.setSize(0);
      this._list.setCallback(this);
      this.add(this._list);
      this.startTimer();
      this.refresh();
      this.setTitle("VM Processes");
   }

   private final void refresh() {
      int drawnLength = this._processes.length;
      this._drawnProcesses = new EScreenVMProcesses$EScreenProcess[this._processes.length];

      for (int i = drawnLength - 1; i >= 0; i--) {
         this._drawnProcesses[i] = this._processes[i];
      }

      ApplicationProcess[] processes = ((ApplicationManagerInternal)ApplicationManager.getApplicationManager()).getProcesses();
      int numProcesses = processes.length;
      int pid = -1;
      int sel = this._list.getSelectedIndex();
      if (sel != -1) {
         pid = this._processes[sel].getProcessId();
      }

      this._processes = new EScreenVMProcesses$EScreenProcess[numProcesses + 1];
      this._processes[numProcesses] = new EScreenVMProcesses$EScreenProcess(this, null);
      this._recentTimeWindow = Process.getRecentCPUTime((Process)((Object)null));
      this._totalUtilizedTime = 0;

      for (int i = numProcesses - 1; i >= 0; i--) {
         EScreenVMProcesses$EScreenProcess p = new EScreenVMProcesses$EScreenProcess(this, processes[i]);
         this._totalUtilizedTime = this._totalUtilizedTime + p.getTime();
         this._processes[i] = p;
      }

      if (_displayType == 4) {
         this._processes[numProcesses].setTime(this._totalUtilizedTime);
      }

      Arrays.sort(this._processes, this);
      if (drawnLength != this._processes.length) {
         this._list.setSize(this._processes.length);
      }

      if (pid != -1) {
         for (int i = this._processes.length - 1; i >= 0; i--) {
            if (this._processes[i].getProcessId() == pid) {
               this._list.setSelectedIndex(i);
               break;
            }
         }
      }

      if (this._processes.length != drawnLength) {
         this._list.invalidate();
      } else {
         for (int i = drawnLength - 1; i >= 0; i--) {
            if (this._drawnProcesses[i].compareTo(this._processes[i]) != 0) {
               this._list.invalidate(i);
            }
         }
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      menu.add(new EScreenVMProcesses$MyMenu(this, "Refresh", 1));
      menu.add(new EScreenVMProcesses$MyMenu(this, this.autoRefreshText(), 6));
      menu.addSeparator();
      menu.add(new EScreenVMProcesses$MyMenu(this, "No CPU", 3));
      menu.add(new EScreenVMProcesses$MyMenu(this, "Top CPU", 4));
      menu.add(new EScreenVMProcesses$MyMenu(this, "Total CPU", 5));
      menu.addSeparator();
      menu.add(new EScreenVMProcesses$MyMenu(this, "Kill Process", 2));
      menu.addSeparator();
      super.makeMenu(menu, instance);
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this.refresh();
      }
   }

   private final String autoRefreshText() {
      return this._timerId == -1 ? "Auto Refresh On" : "Auto Refresh Off";
   }

   private final synchronized void toggleTimer() {
      if (this._timerId == -1) {
         this.startTimer();
      } else {
         this.stopTimer();
      }
   }

   private final synchronized void startTimer() {
      if (this._timerId == -1) {
         this._timerId = UiApplication.getUiApplication().invokeLater(this, 1000, true);
      }
   }

   private final synchronized void stopTimer() {
      if (this._timerId != -1) {
         UiApplication.getUiApplication().cancelInvokeLater(this._timerId);
         this._timerId = -1;
      }
   }

   @Override
   public final synchronized void run() {
      this.refresh();
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      String s;
      if (index < this._processes.length && index >= 0) {
         s = this._processes[index].toString();
      } else {
         s = "";
      }

      graphics.drawText(s, 0, s.length(), 0, y, 0, width);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return this.getWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      return "";
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }
}
