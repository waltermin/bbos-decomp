package net.rim.device.apps.internal.explorer.MediaLibrary;

import net.rim.device.api.util.IntHashtable;

public class ContextInfo {
   private int _type = 0;
   private boolean _external;
   private boolean _selectAll;
   private IntHashtable _table = (IntHashtable)(new Object(5));
   private Object _object;
   public static final int UNKNOWN;
   public static final int TRACK;
   public static final int ARTIST;
   public static final int ALBUM;
   public static final int GENRE;
   public static final int PLAYLIST;
   public static final int VIDEO;
   public static final int RINGTONE;
   public static final int PICTURE;
   public static final int VOICENOTE;
   public static final int PRELOADED;
   public static final int USERLOADED;
   public static final int ARTIST_ALBUM;
   public static final int ARTIST_ALBUM_TRACK;
   public static final int ARTIST_TRACK;
   public static final int ALBUM_TRACK;
   public static final int GENRE_TRACK;
   public static final int GENRE_ARTIST;
   public static final int GENRE_ARTIST_TRACK;
   public static final int GENRE_ARTIST_ALBUM;
   public static final int GENRE_ARTIST_ALBUM_TRACK;
   public static final int GENRE_ALBUM;
   public static final int GENRE_ALBUM_TRACK;
   public static final int PLAYLIST_TRACK;
   public static final int RINGTONE_TRACK;
   public static final int RINGTONE_TRACK_PRELOADED;
   public static final int RINGTONE_TRACK_USERLOADED;
   public static final int TRACK_PRELOADED;
   public static final int VIDEO_PRELOADED;
   public static final int VIDEO_USERLOADED;
   public static final int PICTURE_USERLOADED;
   public static final int PICTURE_PRELOADED;

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
