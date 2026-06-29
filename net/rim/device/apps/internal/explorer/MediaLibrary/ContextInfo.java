package net.rim.device.apps.internal.explorer.MediaLibrary;

import net.rim.device.api.util.IntHashtable;

public class ContextInfo {
   private int _type = 0;
   private boolean _external;
   private boolean _selectAll;
   private IntHashtable _table = new IntHashtable(5);
   private Object _object;
   public static final int UNKNOWN = 0;
   public static final int TRACK = 1;
   public static final int ARTIST = 2;
   public static final int ALBUM = 4;
   public static final int GENRE = 8;
   public static final int PLAYLIST = 16;
   public static final int VIDEO = 32;
   public static final int RINGTONE = 64;
   public static final int PICTURE = 128;
   public static final int VOICENOTE = 256;
   public static final int PRELOADED = 512;
   public static final int USERLOADED = 1024;
   public static final int ARTIST_ALBUM = 6;
   public static final int ARTIST_ALBUM_TRACK = 7;
   public static final int ARTIST_TRACK = 3;
   public static final int ALBUM_TRACK = 5;
   public static final int GENRE_TRACK = 9;
   public static final int GENRE_ARTIST = 10;
   public static final int GENRE_ARTIST_TRACK = 11;
   public static final int GENRE_ARTIST_ALBUM = 14;
   public static final int GENRE_ARTIST_ALBUM_TRACK = 14;
   public static final int GENRE_ALBUM = 12;
   public static final int GENRE_ALBUM_TRACK = 13;
   public static final int PLAYLIST_TRACK = 17;
   public static final int RINGTONE_TRACK = 65;
   public static final int RINGTONE_TRACK_PRELOADED = 577;
   public static final int RINGTONE_TRACK_USERLOADED = 1089;
   public static final int TRACK_PRELOADED = 513;
   public static final int VIDEO_PRELOADED = 544;
   public static final int VIDEO_USERLOADED = 1056;
   public static final int PICTURE_USERLOADED = 1152;
   public static final int PICTURE_PRELOADED = 640;

   public ContextInfo() {
      this(0);
   }

   public ContextInfo(int type) {
      this._type = type;
   }

   public int getType() {
      return this._type;
   }

   public void setType(int type) {
      this._type = type;
   }

   public void setData(int id, Object value) {
      this._table.put(id, value);
   }

   public Object getData(int id) {
      return this._table.containsKey(id) ? this._table.get(id) : null;
   }

   public void setItem(Object item) {
      this._object = item;
   }

   public Object getItem() {
      return this._object;
   }

   public void setExternal(boolean external) {
      this._external = external;
   }

   public boolean isExternal() {
      return this._external;
   }

   public void setSelectAll(boolean selectAll) {
      this._selectAll = selectAll;
   }

   public boolean isSelectAll() {
      return this._selectAll;
   }

   public static ContextInfo createCopy(ContextInfo source) {
      ContextInfo context = new ContextInfo();
      context._external = source.isExternal();
      context._selectAll = source.isSelectAll();
      context._object = source.getItem();
      return context;
   }
}
