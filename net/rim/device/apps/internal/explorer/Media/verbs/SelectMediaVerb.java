package net.rim.device.apps.internal.explorer.Media.verbs;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.Media.AlbumListScreen;
import net.rim.device.apps.internal.explorer.Media.ArtistListScreen;
import net.rim.device.apps.internal.explorer.Media.GenreListScreen;
import net.rim.device.apps.internal.explorer.Media.MusicLibraryScreen;
import net.rim.device.apps.internal.explorer.Media.TrackListScreen;
import net.rim.device.apps.internal.explorer.MediaLibrary.ContextInfo;
import net.rim.device.apps.internal.explorer.file.SelectionListener;

public final class SelectMediaVerb extends Verb implements SelectionListener {
   private int _type;
   private int _flags;
   private Object[] _selection;
   private boolean _external = true;
   private boolean _selectAll = true;
   public static final int FLAG_USERLOADED = 1;
   public static final int FLAG_PRELOADED = 2;
   public static final int FLAG_LOCATION = 4;
   public static final int TYPE_VOICENOTE = 1;
   public static final int TYPE_RINGTONE = 2;
   public static final int TYPE_VIDEO = 3;
   public static final int TYPE_MUSIC = 4;
   public static final int TYPE_ALL_SONGS = 5;
   public static final int TYPE_GENRE = 6;
   public static final int TYPE_ARTIST = 7;
   public static final int TYPE_ALBUM = 8;

   public final void setExternal(boolean external) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void setSelectAll(boolean selectAll) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final Object[] invokeSelect(Object parameter) {
      ContextInfo context = new ContextInfo();
      context.setExternal(this._external);
      context.setSelectAll(this._selectAll);
      context.setItem(this);
      Screen screen = null;
      switch (this._type) {
         case 0:
            return null;
         case 1:
         default:
            context.setType(256);
            screen = new TrackListScreen(context);
            break;
         case 2:
            if (this.isUserloaded()) {
               context.setType(1089);
            } else if (this.isPreloaded()) {
               context.setType(577);
            } else {
               context.setType(65);
            }

            screen = new TrackListScreen(context);
            break;
         case 3:
            if (this.isUserloaded()) {
               context.setType(1056);
            } else if (this.isPreloaded()) {
               context.setType(544);
            } else {
               context.setType(32);
            }

            screen = new TrackListScreen(context);
            break;
         case 4:
            screen = new MusicLibraryScreen(context);
            break;
         case 5:
            if (this.isPreloaded()) {
               context.setType(513);
            } else {
               context.setType(1);
            }

            screen = new TrackListScreen(context);
            break;
         case 6:
            context.setType(8);
            screen = new GenreListScreen(context);
            break;
         case 7:
            context.setType(2);
            screen = new ArtistListScreen(context);
            break;
         case 8:
            context.setType(4);
            screen = new AlbumListScreen(context);
      }

      if (screen != null) {
         UiApplication.getUiApplication().pushModalScreen(screen);
      }

      return this._selection;
   }

   public final boolean isFlagSet(int flag) {
      return (this._flags & flag) != 0;
   }

   @Override
   public final void selected(Object[] items) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void selected(Object item) {
      this._selection = new Object[1];
      this._selection[0] = item;
   }

   public SelectMediaVerb(int type, int flags, int ordering, ResourceBundleFamily rbf, int titleKey) {
      super(ordering, rbf, titleKey);
      this._type = type;
      this._flags = flags;
   }

   @Override
   public final Object invoke(Object parameter) {
      boolean temp = this._selectAll;
      this._selectAll = false;
      Object[] selection = this.invokeSelect(parameter);
      this._selectAll = temp;
      return selection != null && selection.length >= 1 ? selection[0] : null;
   }

   public SelectMediaVerb(int type, int flags) {
      super(0);
      this._type = type;
      this._flags = flags;
   }

   private final boolean isUserloaded() {
      return this.isFlagSet(1);
   }

   private final boolean isPreloaded() {
      return this.isFlagSet(2);
   }

   public static final int translateContextInfoToSelectType(int contextInfoType) {
      switch (contextInfoType) {
         case 1:
            return 5;
         case 2:
            return 7;
         case 4:
            return 8;
         case 8:
            return 6;
         case 32:
            return 3;
         case 64:
            return 2;
         case 256:
            return 1;
         default:
            return -1;
      }
   }
}
