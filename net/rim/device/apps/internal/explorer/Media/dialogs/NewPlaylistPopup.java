package net.rim.device.apps.internal.explorer.Media.dialogs;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.explorer.Media.SmartlistScreen;
import net.rim.device.apps.internal.explorer.MediaLibrary.ContextInfo;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaInfo;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaLibrary;
import net.rim.device.apps.internal.explorer.MediaLibrary.PlayListCollection;
import net.rim.device.apps.internal.explorer.MediaLibrary.PlaylistItem;
import net.rim.device.apps.internal.explorer.MediaLibrary.SmartlistItem;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.io.file.FileUtilities;

public final class NewPlaylistPopup extends PopupScreen {
   private int _type;
   private AutoTextEditField _name;
   private boolean _isModal;
   private MediaInfo _result;
   public static final int TYPE_PLAYLIST = 0;
   public static final int TYPE_SMARTLIST = 1;

   public NewPlaylistPopup(int type) {
      super(new VerticalFieldManager());
      this._type = type;
      this.initialize();
   }

   private final void initialize() {
      String title = null;
      if (this._type == 0) {
         title = ExplorerResources.getString(185);
      } else if (this._type == 1) {
         title = ExplorerResources.getString(186);
      }

      LabelField label = new LabelField(title);
      this._name = new AutoTextEditField(ExplorerResources.getString(187), "", Integer.MAX_VALUE, 2147483648L);
      this.add(label);
      this.add(new SeparatorField());
      this.add(this._name);
   }

   public final MediaInfo doModal() {
      this._isModal = true;
      UiApplication.getUiApplication().pushModalScreen(this);
      return this._result;
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      if (super.navigationClick(status, time)) {
         return true;
      }

      this.comit();
      return true;
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (super.keyChar(c, status, time)) {
         return true;
      } else if (c == '\n') {
         this.comit();
         return true;
      } else if (c == 27) {
         this.close();
         return true;
      } else {
         return false;
      }
   }

   private final void comit() {
      String location = FileUtilities.getDefaultPath(7);
      if (!location.startsWith("file://")) {
         location = "file://" + location;
      }

      String name = this._name.getText().trim();
      if (name != null && name.length() > 0) {
         if (this._type == 0) {
            location = location + name + ".m3u";
         } else if (this._type == 1) {
            location = location + name + ".rsp";
         }

         int id = StringUtilities.hashCodeIgnoreCase(location);
         PlayListCollection collection = MediaLibrary.getInstance().getPlaylistCollection();
         Object playlist = collection.find(id);
         if (playlist == null) {
            if (this._type == 1) {
               playlist = new SmartlistItem(id, name, location);
               ContextInfo context = new ContextInfo(16);
               context.setData(16, (SmartlistItem)playlist);
               UiApplication.getUiApplication().pushScreen(new SmartlistScreen(context, true));
               this.close();
               return;
            }

            if (this._type == 0) {
               try {
                  Object var9 = new PlaylistItem(id, location);
                  var9.setName(name);
                  if (this._isModal) {
                     this._result = var9;
                  } else {
                     var9.save();
                  }

                  this.close();
                  return;
               } finally {
                  return;
               }
            }
         } else {
            Dialog.alert(ExplorerResources.getString(188));
         }
      }
   }
}
