package net.rim.device.api.ui;

import net.rim.device.api.system.Display;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

class UiEngineImpl$ScreenList {
   private Screen[] _screens;
   private XYRect[] _extents;
   private XYRect[] _opaqueRegions;
   private int _localScreenCount;
   private int _globalScreenCount;
   private Screen[] _hiddenGlobalScreens;
   private final UiEngineImpl this$0;

   public UiEngineImpl$ScreenList(UiEngineImpl _1) {
      this.this$0 = _1;
      this._screens = new Screen[0];
      this._extents = new XYRect[0];
      this._opaqueRegions = new XYRect[0];
      this._hiddenGlobalScreens = new Screen[0];
   }

   public synchronized void copyGlobalScreens() {
      GlobalScreenManager.copyGlobalScreens(this._screens, this._localScreenCount, this._hiddenGlobalScreens);
      this._globalScreenCount = this._screens.length - this._localScreenCount;
      if (this._globalScreenCount < 0) {
         throw new IllegalStateException("copyGlobalScreens: Local screens were overwritten");
      }

      this.wrapOutOfProcessGlobalScreens();
      int screenCount = this.getScreenCount();
      Array.resize(this._extents, screenCount);
      Array.resize(this._opaqueRegions, screenCount);

      for (int i = this._localScreenCount; i < screenCount; i++) {
         this._extents[i] = new XYRect(0, 0, 0, 0);
         this._opaqueRegions[i] = new XYRect(0, 0, 0, 0);
         this.updateExtent(this._screens[i]);
         if (i != screenCount) {
            this._screens[i].setClearBackingStore(true);
         }
      }
   }

   public synchronized Screen[] getHiddenGlobalScreens() {
      return this._hiddenGlobalScreens;
   }

   public synchronized int getScreenCount() {
      return this._localScreenCount + this._globalScreenCount;
   }

   public synchronized int getLocalScreenCount() {
      return this._localScreenCount;
   }

   public synchronized int getGlobalScreenCount() {
      return this._globalScreenCount;
   }

   public synchronized int getInProcessGlobalScreenCount() {
      int count = 0;
      int screenCount = this.getScreenCount();

      for (int i = screenCount - 1; i >= screenCount - this._globalScreenCount; i--) {
         Screen next = this.getScreen(i);
         if (this.this$0.isInProcess(next) || next.getUiEngineImpl() == null && next.isDismissing() && !(next instanceof UiEngineImpl$ProxyScreen)) {
            count++;
         }
      }

      return count;
   }

   public synchronized Screen getScreen(int index) {
      if (index >= 0 && index < this._screens.length) {
         return this._screens[index];
      } else {
         throw new IllegalArgumentException("Illegal screen index: " + index);
      }
   }

   public synchronized Screen getLocalScreen(int index) {
      return index >= this._localScreenCount ? null : this.getScreen(index);
   }

   public synchronized XYRect getExtent(int index) {
      if (index >= 0 && index < this._extents.length) {
         return this._extents[index];
      } else {
         throw new IllegalArgumentException("Illegal extent index: " + index);
      }
   }

   public synchronized XYRect getOpaqueRegion(int index) {
      if (index >= 0 && index < this._opaqueRegions.length) {
         return this._opaqueRegions[index];
      } else {
         throw new IllegalArgumentException("Illegal opaque region index: " + index);
      }
   }

   public synchronized Screen getScreenInNonEventThread(int index) {
      try {
         return this._screens[index];
      } catch (ArrayIndexOutOfBoundsException var3) {
         return null;
      }
   }

   public synchronized int getIndex(Screen screen) {
      return Arrays.getIndex(this._screens, screen);
   }

   public synchronized int getLocalIndex(Screen screen) {
      int index = this.getIndex(screen);
      return index >= this._localScreenCount ? -1 : index;
   }

   public synchronized Screen getTopmostScreen() {
      int count = this._screens.length;
      return count == 0 ? null : this._screens[count - 1];
   }

   public synchronized Screen getTopmostLocalScreen() {
      if (this._localScreenCount == 0) {
         return null;
      }

      int i = this._localScreenCount - 1;

      while (i >= 0 && this._screens[i] instanceof UiEngineImpl$ProxyScreen) {
         i--;
      }

      return i < 0 ? null : this._screens[i];
   }

   public synchronized Screen getTopmostGlobalScreen() {
      return this._globalScreenCount == 0 ? null : this.getTopmostScreen();
   }

   public synchronized int highestOpaqueRegionContaining(XYRect region) {
      for (int i = this._opaqueRegions.length - 1; i >= 0; i--) {
         if (this._opaqueRegions[i].contains(region)) {
            return i;
         }
      }

      return -1;
   }

   public synchronized boolean isTopmost(Screen screen) {
      if (screen == null) {
         throw new IllegalArgumentException("null screen passed to isTopmost");
      } else {
         return this.getTopmostScreen() == screen;
      }
   }

   public synchronized boolean isTopmostLocal(Screen screen) {
      if (screen == null) {
         throw new IllegalArgumentException("null screen passed to isTopmostLocal");
      } else {
         return this.getTopmostLocalScreen() == screen;
      }
   }

   public synchronized void push(Screen screen) {
      if (screen.isGlobal()) {
         throw new IllegalArgumentException("Attempted to push a global screen");
      }

      Arrays.insertAt(this._screens, screen, this._localScreenCount);
      Arrays.insertAt(this._extents, new XYRect(0, 0, 0, 0), this._localScreenCount);
      Arrays.insertAt(this._opaqueRegions, new XYRect(0, 0, 0, 0), this._localScreenCount);
      this._localScreenCount++;
   }

   public synchronized void pop(Screen screen) {
      if (screen.isGlobal()) {
         throw new IllegalArgumentException("Attempted to pop a global screen");
      }

      int index = Arrays.getIndex(this._screens, screen);
      if (index < 0) {
         throw new IllegalStateException("Popping non-displayed screen");
      }

      Arrays.removeAt(this._screens, index);
      Arrays.removeAt(this._extents, index);
      Arrays.removeAt(this._opaqueRegions, index);
      this._localScreenCount--;
   }

   public synchronized void updateExtent(Screen screen) {
      int index = this.getIndex(screen);
      XYRect extent = this.this$0.getScreenExtent(screen);
      XYRect cachedExtent = this.getExtent(index);
      cachedExtent.set(extent);
      XYRect opaqueRegion = this.getOpaqueRegion(index);
      if (this.this$0.isScreenTransparent(screen)) {
         opaqueRegion.set(0, 0, 0, 0);
      } else if (this.this$0.isScreenTransparentBorder(screen)) {
         opaqueRegion.set(extent);
         XYEdges tmpEdges = Ui._tmpEdges;
         if (!(screen instanceof UiEngineImpl$ProxyScreen)) {
            screen.getBorder(tmpEdges);
         } else {
            ((UiEngineImpl$ProxyScreen)screen).getWrappedScreen().getBorder(tmpEdges);
         }

         opaqueRegion.subtract(tmpEdges);
      } else {
         opaqueRegion.set(extent);
      }
   }

   public synchronized Screen getScreenAbove(Screen screen) {
      int index = this.this$0.getScreenIndex(screen);
      if (index < 0) {
         throw new IllegalArgumentException("getScreenAbove couldn't find the screen");
      } else {
         return index < this.getScreenCount() - 1 ? this.getScreen(index + 1) : null;
      }
   }

   public synchronized Screen getLocalScreenAbove(Screen screen) {
      int index = this.this$0.getScreenIndex(screen);
      if (index < 0) {
         throw new IllegalArgumentException("getLocalScreenAbove couldn't find the screen");
      } else {
         return index < this._localScreenCount - 1 ? this.getScreen(index + 1) : null;
      }
   }

   public synchronized Screen getScreenBelow(Screen screen) {
      int index = this.getIndex(screen);
      if (index < 0) {
         throw new IllegalArgumentException("getScreenBelow couldn't find the screen");
      } else {
         return index > 0 ? this.getScreen(index - 1) : null;
      }
   }

   public synchronized Screen getLocalScreenBelow(Screen screen) {
      int index = this.this$0.getScreenIndex(screen);
      if (index < 0) {
         throw new IllegalArgumentException("getLocalScreenBelow couldn't find the screen");
      } else {
         return index < this._localScreenCount && index > 0 ? this.getScreen(index - 1) : null;
      }
   }

   public synchronized XYRect[] getOpaqueRegionsArray() {
      return this._opaqueRegions;
   }

   private void wrapOutOfProcessGlobalScreens() {
      for (int i = 0; i < this._globalScreenCount; i++) {
         this._screens[i + this._localScreenCount] = this.wrapGlobalScreen(this._screens[i + this._localScreenCount], false);
      }

      for (int i = 0; i < this._hiddenGlobalScreens.length; i++) {
         this._hiddenGlobalScreens[i] = this.wrapGlobalScreen(this._hiddenGlobalScreens[i], true);
      }
   }

   private Screen wrapGlobalScreen(Screen screen, boolean hidden) {
      if (screen instanceof UiEngineImpl$ProxyScreen) {
         return screen;
      }

      if (this.this$0.isInProcess(screen)) {
         return screen;
      }

      screen = new UiEngineImpl$ProxyScreen(screen);
      screen.setUiEngine(this.this$0);
      screen.doLayoutNoSynch();
      return screen;
   }

   private Screen[] wrapLocalScreens() {
      Screen[] wrappedLocalScreens = new Screen[0];

      for (int i = this._localScreenCount - 1; i >= 0; i--) {
         Screen local = this._screens[i];
         if (!(local instanceof UiEngineImpl$ProxyScreen)) {
            Arrays.insertAt(wrappedLocalScreens, this.wrapLocalScreen(local), 0);
            if (local.getExtent().contains(0, 0, Display.getWidth(), Display.getHeight())
               && !this.this$0.isScreenTransparent(local)
               && !this.this$0.isScreenTransparentBorder(local)) {
               return wrappedLocalScreens;
            }

            if (this.this$0.isScreenTransparent(local) && i == 0) {
               Arrays.insertAt(wrappedLocalScreens, this.wrapLocalScreen(this.this$0._bottomScreen), 0);
            }
         }
      }

      return wrappedLocalScreens;
   }

   private Screen wrapLocalScreen(Screen screen) {
      if (screen instanceof UiEngineImpl$ProxyScreen) {
         throw new IllegalStateException("Trying to wrap a local wrapped screen.");
      } else {
         return new UiEngineImpl$ProxyScreen(screen);
      }
   }

   private String getScreenListDebugging() {
      StringBuffer buffer = new StringBuffer();

      for (int i = 0; i < this._screens.length; i++) {
         Screen next = this._screens[i];
         if (!(next instanceof UiEngineImpl$ProxyScreen)) {
            if (next.isGlobal()) {
               buffer.append("G,");
            } else {
               buffer.append("L,");
            }
         } else {
            next = ((UiEngineImpl$ProxyScreen)next).getWrappedScreen();
            if (next.isGlobal()) {
               buffer.append("PG,");
            } else {
               buffer.append("PL,");
            }
         }
      }

      return buffer.toString();
   }
}
