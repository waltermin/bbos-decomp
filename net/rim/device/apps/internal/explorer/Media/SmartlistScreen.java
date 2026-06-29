package net.rim.device.apps.internal.explorer.Media;

import javax.microedition.io.InputConnection;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.explorer.Media.verbs.MediaVerb;
import net.rim.device.apps.internal.explorer.Media.verbs.SelectMediaVerb;
import net.rim.device.apps.internal.explorer.MediaLibrary.ContextInfo;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaInfo;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaInfoCollection;
import net.rim.device.apps.internal.explorer.MediaLibrary.SmartlistItem;
import net.rim.device.apps.internal.explorer.file.menu.OptionsMenuItem;
import net.rim.device.apps.internal.explorer.file.menu.ReceiveBluetooth;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.media.M3UPlaylist;

public final class SmartlistScreen extends AppsMainScreen implements ActionProvider, FieldChangeListener {
   private SmartlistItem _smartlist;
   private VerticalFieldManager _artistsVfm = (VerticalFieldManager)(new Object());
   private VerticalFieldManager _albumsVfm = (VerticalFieldManager)(new Object());
   private VerticalFieldManager _genresVfm = (VerticalFieldManager)(new Object());
   private boolean _dirty;
   private static final String UTF8;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected final void invoke() {
      PopupScreen dialog = (PopupScreen)(new Object((Manager)(new Object())));
      dialog.add((Field)(new Object("Generating playlist...", 36028797086072832L)));
      UiApplication.getUiApplication().pushScreen(dialog);
      MediaInfoCollection tracks = this._smartlist.getTracks();
      if (tracks.size() == 0) {
         UiApplication.getUiApplication().popScreen(dialog);
         Dialog.alert("Your smart list criteria generated no tracks. Please modify your criteria.");
      } else {
         boolean var12 = false /* VF: Semaphore variable */;

         try {
            var12 = true;
            M3UPlaylist e = new Object();
            MediaInfo var15 = null;
            int size = tracks.size();

            for (int connection = 0; connection < size; connection++) {
               var15 = tracks.getAt(connection);
               ((M3UPlaylist)e).addUrl(((MediaInfo)var15).getLocation(), ((MediaInfo)var15).getName(), -1);
            }

            InputConnection connection = ((M3UPlaylist)e).openConnection("/");
            UiApplication.getUiApplication().popScreen(dialog);
            ContextObject context = (ContextObject)(new Object());
            ContextObject.put(context, -1477447097671931650L, this);
            MediaLauncher.launch(connection, context);
            synchronized (this) {
               this.wait(1500);
               var12 = false;
            }
         } finally {
            if (var12) {
               String msg = ExplorerResources.getString(202);
               Dialog.alert(msg);
               return;
            }
         }
      }
   }

   @Override
   public final boolean perform(long actionId, Object context) {
      if (actionId == 3712387911241450002L) {
         this.invoke();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field instanceof Object) {
         Object cookie = field.getCookie();
         if (cookie instanceof SmartlistScreen$ButtonInfo) {
            SmartlistScreen$ButtonInfo info = (SmartlistScreen$ButtonInfo)cookie;
            Manager manager = null;
            switch (info.getType()) {
               case 2:
                  manager = this._artistsVfm;
                  break;
               case 4:
                  manager = this._albumsVfm;
                  break;
               case 8:
                  manager = this._genresVfm;
                  break;
               default:
                  return;
            }

            if (info.isRemove()) {
               boolean deleteFile = Dialog.ask(2) == 3;
               if (!deleteFile) {
                  return;
               }

               manager.delete(field.getManager().getManager());
               this._smartlist.removeItem(info.getType(), info.getString());
               if (manager.getFieldCount() == 0) {
                  manager.add(this.createAnyField());
               }

               this._dirty = true;
               return;
            }

            SelectMediaVerb select = new SelectMediaVerb(SelectMediaVerb.translateContextInfoToSelectType(info.getType()), 0);
            select.setExternal(false);
            select.setSelectAll(true);
            Object[] selections = select.invokeSelect(null);
            if (selections != null) {
               for (int i = 0; i < selections.length; i++) {
                  if (this._smartlist.addItem(info.getType(), selections[i].toString())) {
                     if (manager.getFieldCount() == 1) {
                        Field field0 = manager.getField(0);
                        if (field0 instanceof Object) {
                           manager.deleteAll();
                        }
                     }

                     manager.add(this.createButtonField(info.getType(), selections[i].toString(), true, null, null));
                     this._dirty = true;
                  }
               }
            }
         }
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      MediaVerb play = new MediaVerb(3712387911241450002L, this, 569345, null, 92);
      menu.add(play);
      if (MediaUtilities.checkBluetoothPolicy()) {
         menu.add(new ReceiveBluetooth());
      }

      menu.add(new OptionsMenuItem());
      MediaUtilities.addDownloadTunesMenuItems(menu);
      menu.setDefault(play);
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      Field field = this.getLeafFieldWithFocus();
      if (c == 27) {
         this.close();
         return true;
      } else if ((c == ' ' || c == '\n') && field instanceof CollapseToggleField) {
         ((CollapseToggleField)field).toggleCollapse();
         return true;
      } else {
         return super.keyChar(c, status, time);
      }
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      if (super.navigationClick(status, time)) {
         return true;
      } else if ((status & 1073741824) != 0) {
         return false;
      } else {
         Field field = this.getLeafFieldWithFocus();
         if (!(field instanceof Object)) {
            this.invoke();
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (!attached && this._dirty) {
         try {
            this._smartlist.save();
         } finally {
            return;
         }
      }
   }

   private final void initialize() {
      Field banner = ThemeUtilities.getTitleField(((StringBuffer)(new Object("Smartlist: "))).append(this._smartlist.getName()).toString());
      this.add(banner);
      VerticalFieldManager manager = (VerticalFieldManager)(new Object(281474976710656L));
      ListScrollbarManager scrollingManager = new ListScrollbarManager(manager);
      VerticalFieldManager verticalTempManager = null;
      Manager tempManager = null;
      VerticalFieldManager var8 = new Object(281474976710656L);
      tempManager = this.createButtonField(2, "Tracks by the following artists", false, (Manager)var8, this._artistsVfm);
      ((Manager)var8).add(tempManager);
      ((Manager)var8).add(this._artistsVfm);
      manager.add((Field)var8);
      String[] items = this._smartlist.getItems(2);
      if (items.length > 0) {
         for (int i = 0; i < items.length; i++) {
            tempManager = this.createButtonField(2, items[i], true, null, null);
            this._artistsVfm.add(tempManager);
         }
      } else {
         this._artistsVfm.add(this.createAnyField());
      }

      manager.add(new VerticalSpacerField());
      var8 = new Object(281474976710656L);
      tempManager = this.createButtonField(4, "That are in the following albums", false, (Manager)var8, this._albumsVfm);
      ((Manager)var8).add(tempManager);
      ((Manager)var8).add(this._albumsVfm);
      manager.add((Field)var8);
      items = this._smartlist.getItems(4);
      if (items.length > 0) {
         for (int i = 0; i < items.length; i++) {
            tempManager = this.createButtonField(4, items[i], true, null, null);
            this._albumsVfm.add(tempManager);
         }
      } else {
         this._albumsVfm.add(this.createAnyField());
      }

      manager.add(new VerticalSpacerField());
      var8 = new Object(281474976710656L);
      tempManager = this.createButtonField(8, "And are of the following genres", false, (Manager)var8, this._genresVfm);
      ((Manager)var8).add(tempManager);
      ((Manager)var8).add(this._genresVfm);
      manager.add((Field)var8);
      items = this._smartlist.getItems(8);
      if (items.length > 0) {
         for (int i = 0; i < items.length; i++) {
            tempManager = this.createButtonField(8, items[i], true, null, null);
            this._genresVfm.add(tempManager);
         }
      } else {
         this._genresVfm.add(this.createAnyField());
      }

      this.add(scrollingManager);
   }

   public SmartlistScreen(ContextInfo context, boolean newList) {
      super(562949953421312L);
      this.setTag(ThemeUtilities.SCREEN_TAG);
      Manager manager = this.getMainManager();
      if (manager != null) {
         manager.setTag(ThemeUtilities.SCREEN_TAG);
      }

      this._smartlist = (SmartlistItem)context.getData(16);
      this._dirty = newList;
      this.initialize();
      this.setHelp(32247);
   }

   private final Manager createButtonField(int type, String string, boolean isRemove, Manager manager, Field field) {
      String text = null;
      if (isRemove) {
         text = "Remove";
      } else {
         text = "Add";
      }

      ButtonField button = (ButtonField)(new Object(text, 65536));
      SmartlistScreen$ButtonInfo info = new SmartlistScreen$ButtonInfo(type, string, isRemove);
      button.setCookie(info);
      button.setChangeListener(this);
      RichTextField rtf = null;
      RichTextField var13 = new Object(string, 36028799166447616L);
      ((Field)var13).setTag(ThemeUtilities.SELECTABLE_TEXT_TAG);
      RightAlightButtonManager rbm = new RightAlightButtonManager(!isRemove);
      if (isRemove) {
         rbm.add(new HorizontalSpacerField());
      } else {
         rbm.add(new CollapseToggleField(manager, field));
      }

      rbm.add((Field)var13);
      rbm.add(button);
      if (isRemove) {
         VerticalFieldManager vfm = (VerticalFieldManager)(new Object());
         vfm.add(rbm);
         vfm.add(new MySeparatorField());
         return vfm;
      } else {
         return rbm;
      }
   }

   private final RichTextField createAnyField() {
      RichTextField rtf = (RichTextField)(new Object("(Any)", 36028797019226112L));
      rtf.setTag(ThemeUtilities.SELECTABLE_TEXT_TAG);
      return rtf;
   }
}
