package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.explorer.Media.verbs.MediaVerb;
import net.rim.device.apps.internal.explorer.MediaLibrary.ContextInfo;
import net.rim.device.apps.internal.explorer.file.menu.OptionsMenuItem;
import net.rim.device.apps.internal.explorer.file.menu.ReceiveBluetooth;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.media.MediaPlayerState;

public class MediaLibraryScreen extends AppsMainScreen implements ListFieldCallback, ActionProvider {
   protected String[] ITEMS = new String[0];
   protected ListField _library;
   private int _nowPlaying;
   private int _mediaType;
   protected ContextInfo _context;

   protected void initialize() {
      throw null;
   }

   protected void addNowPlaying(int type) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   protected String[] getItems() {
      throw null;
   }

   protected String getTitle() {
      throw null;
   }

   protected int getNowPlaying() {
      return this._nowPlaying;
   }

   protected void invoke() {
      int index = this.getSelectedIndex();
      if (index == 0 && this._nowPlaying > 0) {
         MediaLauncher.showPlayer();
      }
   }

   protected int getSelectedIndex() {
      Field field = this.getLeafFieldWithFocus();
      return !(field instanceof ListField) ? -1 : this._library.getSelectedIndex();
   }

   protected void update() {
      Application app = this.getApplication();
      if (app != null) {
         app.invokeAndWait(new MediaLibraryScreen$ListUpdateRunnable(this));
      }
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return this.getPreferredWidth();
   }

   @Override
   public Object get(ListField listField, int index) {
      return this.ITEMS[index];
   }

   @Override
   public boolean perform(long actionId, Object context) {
      if (actionId == 6099736323056465049L) {
         this.invoke();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      graphics.drawText(this.ITEMS[index], 5, y + 4, 64, width);
   }

   @Override
   public void doLayout() {
      super.doLayout();
      int fontHeight = this._library.getFont().getHeight();
      this._library.setRowHeight(fontHeight + 8);
   }

   @Override
   protected boolean keyChar(char c, int status, int time) {
      if (c == 27) {
         this.close();
         return true;
      } else if (c == '\n') {
         this.invoke();
         return true;
      } else {
         return super.keyChar(c, status, time);
      }
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      MediaVerb view = new MediaVerb(6099736323056465049L, this, 569345, null, 157);
      menu.add(view);
      if (MediaUtilities.checkBluetoothPolicy()) {
         menu.add(new ReceiveBluetooth());
      }

      menu.add(new OptionsMenuItem());
      menu.setDefault(view);
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      if (super.navigationClick(status, time)) {
         return true;
      }

      if ((status & 1073741824) != 0) {
         return false;
      }

      this.invoke();
      return true;
   }

   @Override
   protected void onVisibilityChange(boolean visible) {
      super.onVisibilityChange(visible);
      this.internalVisibilityChange(visible);
   }

   private void internalVisibilityChange(boolean visible) {
      if (visible) {
         if (this.checkToAddNowPlaying()) {
            if (MediaLauncher.isPlayerRunning()) {
               Arrays.insertAt(this.ITEMS, ExplorerResources.getString(155), 0);
               this._nowPlaying = 1;
               this.update();
               return;
            }
         } else if (!MediaLauncher.isPlayerRunning() && this._nowPlaying == 1) {
            Arrays.removeAt(this.ITEMS, 0);
            this._nowPlaying = 0;
            this.update();
         }
      }
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this.internalVisibilityChange(true);
      }
   }

   private boolean checkToAddNowPlaying() {
      String url = MediaPlayerState.getMediaPlayerURL();
      if (url != null) {
         int mediaType = MIMETypeAssociations.getMediaType(url);
         return mediaType == this._mediaType && this._nowPlaying == 0;
      } else {
         return false;
      }
   }

   public MediaLibraryScreen(ContextInfo context) {
      super(562949953421312L);
      this.setTag(ThemeUtilities.SCREEN_TAG);
      Manager manager = this.getMainManager();
      if (manager != null) {
         manager.setTag(ThemeUtilities.SCREEN_TAG);
      }

      Arrays.append(this.ITEMS, this.getItems());
      this._library = new ListField(this.ITEMS.length);
      this._library.setCallback(this);
      this._library.setSearchable(false);
      this._library.setTag(ThemeUtilities.LIST_TAG);
      Field banner = ThemeUtilities.getTitleField(this.getTitle());
      VerticalFieldManager listManager = new VerticalFieldManager(281474976710656L);
      listManager.add(this._library);
      ListScrollbarManager scrollManager = new ListScrollbarManager(listManager);
      this.add(banner);
      this.add(scrollManager);
      this.setHelp(32247);
      this._context = context == null ? new ContextInfo() : context;
      this.initialize();
   }
}
