package javax.microedition.lcdui;

import javax.microedition.midlet.MIDletStateChangeException;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.lcdui.Lcdui;
import net.rim.device.internal.lcdui.MIDletInterface;
import net.rim.device.internal.ui.MIDletApplication;

class MIDPScreen extends net.rim.device.api.ui.Screen implements Comparator {
   private Displayable _displayable;
   protected Ticker _ticker;
   private LabelField _title;
   private SeparatorField _separator;
   private boolean _fullScreenMode;
   private XYRect _displayableAreaExtent = new XYRect(0, 0, net.rim.device.api.system.Display.getWidth(), net.rim.device.api.system.Display.getHeight());
   private SimpleSortingVector _commands = new SimpleSortingVector();
   private CommandListener _commandListener;
   private Command _escapeCommand;
   private static Runnable _tickerRunnable = new MIDPScreen$TickerRunnable();
   private static boolean _pendingTimer = false;
   private static boolean _idleTimer = false;

   Displayable getDisplayable() {
      return this._displayable;
   }

   final void setDisplayable(Displayable displayable) {
      this._displayable = displayable;
   }

   final void setDisplay(Display display) {
   }

   void setFullScreenMode(boolean mode) {
      if (this._fullScreenMode != mode) {
         this._fullScreenMode = mode;
         if (mode) {
            if (this._title != null) {
               this.deleteRange(0, 2);
            }
         } else if (this._title != null) {
            this.insert(this._title, 0);
            this.insert(this._separator, 1);
            this._title.setDirty(true);
         }

         this.recalcDisplayableAreaHeight();
         this.invalidate();
      }
   }

   XYRect getDisplayableAreaExtent() {
      return this._displayableAreaExtent;
   }

   String getTitle() {
      synchronized (Application.getEventLock()) {
         return this._title == null ? null : this._title.getText();
      }
   }

   void setTitle(String s) {
      synchronized (Application.getEventLock()) {
         if (s == null) {
            if (this._title != null) {
               if (!this._fullScreenMode) {
                  this.deleteRange(0, 2);
               }

               this._title = null;
               this._separator = null;
            }
         } else if (this._title == null) {
            this._separator = new SeparatorField();
            this._title = new LabelField(s, 1152921504606846976L);
            if (!this._fullScreenMode) {
               this.insert(this._title, 0);
               this.insert(this._separator, 1);
            }
         } else {
            this._title.setText(s);
         }

         this.recalcDisplayableAreaHeight();
      }
   }

   public void init() {
   }

   public final void setTicker(Ticker ticker) {
      boolean update = false;
      if (ticker != null) {
         if (this._ticker == null && this.isValidLayout()) {
            update = true;
            scheduleTickerTimer(0);
         }

         this._ticker = ticker;
         ticker.setStuff(this.getFont());
      } else if (this._ticker != null) {
         update = true;
         this._ticker = ticker;
      }

      if (update) {
         this.recalcDisplayableAreaHeight();
         this.invalidate();
      }
   }

   public final Ticker getTicker() {
      return this._ticker;
   }

   boolean advanceTicker() {
      if (this._ticker != null) {
         this._ticker.advanceTicker();
         boolean updateVisuals = !this._fullScreenMode;
         if (updateVisuals) {
            int tickerHeight = this._ticker.getHeight();
            net.rim.device.api.ui.Graphics graphics = this.getGraphics();
            graphics.clear(0, this.getHeight() - tickerHeight, this.getWidth(), tickerHeight);
            this._ticker.draw(graphics, this.getHeight() - tickerHeight);
            this.updateDisplay();
         }

         return true;
      } else {
         return false;
      }
   }

   void restartTickerTimer() {
      if (_idleTimer) {
         scheduleTickerTimer(0);
      }
   }

   public void addCommand(Command cmd) {
      int n = this._commands.size();

      for (int i = 0; i < n; i++) {
         if (this._commands.elementAt(i) == cmd) {
            return;
         }
      }

      this._commands.addElement(cmd);
      this.updateKeyMappings(cmd);
   }

   public void removeCommand(Command cmd) {
      if (this._commands != null) {
         int n = this._commands.size();

         for (int i = 0; i < n; i++) {
            if (this._commands.elementAt(i) == cmd) {
               this._commands.removeElementAt(i);
               break;
            }
         }

         if (cmd == this._escapeCommand) {
            this.updateEscapeCommand();
         }
      }
   }

   public void setCommandListener(CommandListener l) {
      this._commandListener = l;
   }

   @Override
   public int compare(Object o1, Object o2) {
      return ((Command)o1).getPriority() - ((Command)o2).getPriority();
   }

   @Override
   protected void sublayout(int width, int height) {
      this.setPosition(0, 0);
      this.setExtent(width, height);
      boolean reserveTickerSpace = !this._fullScreenMode && this._ticker != null;
      if (reserveTickerSpace) {
         this._ticker.setStuff(this.getFont());
         height -= this._ticker.getHeight();
      }

      this.setPositionDelegate(0, 0);
      this.layoutDelegate(width, height);
   }

   @Override
   protected void paint(net.rim.device.api.ui.Graphics graphics) {
      super.paint(graphics);
      boolean paintTicker = !this._fullScreenMode && this._ticker != null;
      if (paintTicker) {
         this._ticker.draw(graphics, this.getHeight() - this._ticker.getHeight());
      }
   }

   private final void destroyMIDlet() {
      try {
         ((MIDletInterface)Application.getApplication()).destroyApp(true);
      } catch (MIDletStateChangeException var2) {
      }

      MIDletApplication ma = (MIDletApplication)Application.getApplication();
      ma.exit();
   }

   @Override
   protected boolean keyChar(char c, int status, int time) {
      if (super.keyChar(c, status, time)) {
         return true;
      } else if (c == 27 && this._escapeCommand != null) {
         Lcdui.setCommandActionCallback(this._commandListener, this._escapeCommand, this.getDisplayable());
         return true;
      } else {
         return false;
      }
   }

   private long getEscapeKeyPriority(Command cmd) {
      if (cmd == null) {
         return Long.MAX_VALUE;
      }

      long prio;
      switch (cmd.getCommandType()) {
         case 2:
            prio = 8589934592L;
            break;
         case 3:
            prio = 4294967296L;
            break;
         case 7:
            prio = 12884901888L;
            break;
         default:
            return Long.MAX_VALUE;
      }

      return prio + cmd.getPriority();
   }

   private void updateKeyMappings(Command newCommand) {
      switch (newCommand.getCommandType()) {
         case 2:
         case 3:
         case 7:
            if (this.getEscapeKeyPriority(newCommand) < this.getEscapeKeyPriority(this._escapeCommand)) {
               this._escapeCommand = newCommand;
            }

            return;
      }
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      Command exitCommand = null;
      int n = this._commands.size();

      for (int i = 0; i < n; i++) {
         Command cmd = (Command)this._commands.elementAt(i);
         if (cmd.getCommandType() == 7) {
            exitCommand = cmd;
            break;
         }
      }

      this.updateEscapeCommand();
      boolean defaultSet = false;
      boolean midpCommandsAdded = false;
      long minPriority = Long.MAX_VALUE;
      MenuItem menuItem = null;

      for (int i = 0; i < n; i++) {
         Command cmd = (Command)this._commands.elementAt(i);
         if (cmd != exitCommand) {
            menuItem = new MIDPScreen$MyMenuItem(this, cmd.getMenuLabel(), new MIDPScreen$CommandCookie(cmd, null));
            menu.add(menuItem);
            midpCommandsAdded = true;
            if (cmd.getPriority() < minPriority) {
               minPriority = cmd.getPriority();
               menu.setDefault(menuItem);
               defaultSet = true;
            }
         }
      }

      if (midpCommandsAdded) {
         menu.addSeparator();
      }

      midpCommandsAdded = false;
      Item selectedItem = null;
      Field selectedField = this.getLeafFieldWithFocus();
      if (selectedField != null) {
         try {
            selectedItem = (Item)selectedField.getCookie();
         } catch (ClassCastException var15) {
         }
      }

      if (selectedItem != null) {
         SimpleSortingVector commands = selectedItem.getCommands();
         n = commands.size();

         for (int i = 0; i < n; i++) {
            Command cmd = (Command)commands.elementAt(i);
            menuItem = new MIDPScreen$MyMenuItem(this, cmd.getMenuLabel(), new MIDPScreen$CommandCookie(cmd, selectedItem));
            menu.add(menuItem);
            midpCommandsAdded = true;
            if (!defaultSet) {
               menu.setDefault(menuItem);
               defaultSet = true;
            }
         }
      }

      if (midpCommandsAdded) {
         menu.addSeparator();
      }

      if (exitCommand != null) {
         menuItem = new MIDPScreen$MyMenuItem(this, exitCommand.getMenuLabel(), new MIDPScreen$CommandCookie(exitCommand, null));
         menu.add(menuItem);
      } else {
         menuItem = new MIDPScreen$MyMenuItem(this, CommonResource.getString(9), null);
         menu.add(menuItem);
      }

      if (!defaultSet) {
         menu.setDefault(menuItem);
      }
   }

   private static final void scheduleTickerTimer(long delay) {
      synchronized (Application.getEventLock()) {
         if (!_pendingTimer) {
            if (delay == 0) {
               Application.getApplication().invokeLater(_tickerRunnable);
            } else if (Application.getApplication().invokeLater(_tickerRunnable, delay, false) == -1) {
               throw new RuntimeException("Unable to allocate a ticker timer.");
            }

            _pendingTimer = true;
         }
      }
   }

   MIDPScreen() {
      super(new VerticalFieldManager(3458764513820540928L), 65536);
      this._commands.setSortComparator(this);
      this._commands.setSort(true);
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached && this._ticker != null) {
         scheduleTickerTimer(0);
      }
   }

   private void updateEscapeCommand() {
      long minPriority = Long.MAX_VALUE;
      this._escapeCommand = null;
      int n = this._commands.size();

      for (int i = 0; i < n; i++) {
         Command cmd = (Command)this._commands.elementAt(i);
         if (this.getEscapeKeyPriority(cmd) < minPriority) {
            minPriority = this.getEscapeKeyPriority(cmd);
            this._escapeCommand = cmd;
         }
      }
   }

   MIDPScreen(Manager delegate) {
      super(delegate);
      this._commands.setSortComparator(this);
      this._commands.setSort(true);
   }

   private void recalcDisplayableAreaHeight() {
      int newHeight = net.rim.device.api.system.Display.getHeight();
      int titleAndSeparatorHeight = 0;
      if (!this._fullScreenMode) {
         if (this._title != null) {
            titleAndSeparatorHeight = this._title.getPreferredHeight() + this._separator.getPreferredHeight();
            newHeight -= titleAndSeparatorHeight;
         }

         if (this._ticker != null) {
            newHeight -= this._ticker.getHeight();
         }
      }

      this._displayableAreaExtent.set(0, titleAndSeparatorHeight, net.rim.device.api.system.Display.getWidth(), newHeight);
   }

   @Override
   public boolean dispatchKeyEvent(int event, char key, int status, int time) {
      this.restartTickerTimer();
      return event == 32768 && key == 0 ? false : super.dispatchKeyEvent(event, key, status, time);
   }

   @Override
   public boolean dispatchTrackwheelEvent(int event, int magnitude, int status, int time) {
      this.restartTickerTimer();
      return super.dispatchTrackwheelEvent(event, magnitude, status, time);
   }
}
