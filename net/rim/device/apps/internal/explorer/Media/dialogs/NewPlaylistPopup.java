package net.rim.device.apps.internal.explorer.Media.dialogs;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.explorer.Media.SmartlistScreen;
import net.rim.device.apps.internal.explorer.MediaLibrary.ContextInfo;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaInfo;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaLibrary;
import net.rim.device.apps.internal.explorer.MediaLibrary.PlayListCollection;
import net.rim.device.apps.internal.explorer.MediaLibrary.PlaylistItem;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.io.file.FileUtilities;

public final class NewPlaylistPopup extends PopupScreen {
   private int _type;
   private AutoTextEditField _name;
   private boolean _isModal;
   private MediaInfo _result;
   public static final int TYPE_PLAYLIST;
   public static final int TYPE_SMARTLIST;

   public NewPlaylistPopup(int type) {
      super((Manager)(new Object()));
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

      LabelField label = (LabelField)(new Object(title));
      this._name = (AutoTextEditField)(new Object(ExplorerResources.getString(187), "", Integer.MAX_VALUE, 2147483648L));
      this.add(label);
      this.add((Field)(new Object()));
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
         location = ((StringBuffer)(new Object("file://"))).append(location).toString();
      }

      String name = this._name.getText().trim();
      if (name != null && name.length() > 0) {
         if (this._type == 0) {
            location = ((StringBuffer)(new Object())).append(location).append(name).append(".m3u").toString();
         } else if (this._type == 1) {
            location = ((StringBuffer)(new Object())).append(location).append(name).append(".rsp").toString();
         }

         int id = StringUtilities.hashCodeIgnoreCase(location);
         PlayListCollection collection = MediaLibrary.getInstance().getPlaylistCollection();
         Object playlist = collection.find(id);
         if (playlist == null) {
            if (this._type == 1) {
               playlist = new Object(id, name, location);
               ContextInfo context = (ContextInfo)(new Object(16));
               context.setData(16, playlist);
               UiApplication.getUiApplication().pushScreen(new SmartlistScreen(context, true));
               this.close();
               return;
            }

            if (this._type == 0) {
               try {
                  playlist = new Object(id, location);
                  ((PlaylistItem)playlist).setName(name);
                  if (this._isModal) {
                     this._result = (MediaInfo)playlist;
                  } else {
                     ((PlaylistItem)playlist).save();
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
