package net.rim.device.apps.internal.explorer.MediaLibrary;

import java.io.PrintStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.MediaLibrary.util.MLUtilities;
import net.rim.device.apps.internal.explorer.MediaLibrary.util.PlaylistIndexComparator;
import net.rim.device.cldc.io.utility.URIDecoder;

public final class PlaylistItem implements MediaInfo, KeyProvider, PaintProvider, VerbProvider {
   int _id;
   String _name;
   String _location;
   String[] _keywords;
   String[] _prefixedKeywords;
   String[] _urls;
   IntIntHashtable _urlHashes;
   MediaInfoCollection _collection;

   @Override
   public final int paint(Graphics graphics, int x, int y, int width, int height, Object context) {
      graphics.drawText(this.toString(), x, y, 64, width);
      return 0;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      return null;
   }

   public final void setName(String name) {
      if (name != null) {
         this._name = name.trim();
         this._keywords = StringUtilities.stringToKeywords(this._name);
         this._prefixedKeywords = new String[1];
         this._prefixedKeywords[0] = FilterConstants.PLAYLIST_PREFIX;
         Arrays.append(this._keywords, this._prefixedKeywords);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void save() {
      FileConnection playlistFile = null;
      PrintStream writer = null;
      boolean var18 = false /* VF: Semaphore variable */;

      try {
         try {
            var18 = true;
            playlistFile = (FileConnection)Connector.open(this.getLocation(), 3);
            if (!playlistFile.exists()) {
               playlistFile.create();
            }

            playlistFile.truncate(0);
            writer = new PrintStream(playlistFile.openOutputStream(Integer.MAX_VALUE));
            if (this._urls != null) {
               for (int i = 0; i < this._urls.length; i++) {
                  writer.println(this._urls[i]);
               }

               var18 = false;
            } else {
               var18 = false;
            }
         } finally {
            ;
         }
      } finally {
         if (var18) {
            label174:
            try {
               if (writer != null) {
                  writer.close();
               }

               if (playlistFile != null) {
                  playlistFile.close();
               }
            } finally {
               break label174;
            }
         }
      }

      try {
         if (writer != null) {
            writer.close();
         }

         if (playlistFile != null) {
            playlistFile.close();
         }
      } finally {
         return;
      }
   }

   public final MediaInfoCollection getCollection() {
      return this._collection;
   }

   public final void moveMedia(Playlistable moving, int desiredIndex, boolean save) {
      int newIndex = -1;
      int movingIndex = moving.getPlaylistIndex();
      Playlistable previous = null;
      if (desiredIndex == 0) {
         newIndex = 0;
      } else if (desiredIndex == this._collection.size() - 1) {
         newIndex = this._urls.length - 1;
         previous = (Playlistable)this._collection.getAt(this._collection.size() - 1);
      } else {
         previous = (Playlistable)this._collection.getAt(desiredIndex);
         newIndex = previous.getPlaylistIndex();
      }

      Arrays.removeAt(this._urls, movingIndex);
      Arrays.insertAt(this._urls, moving.getLocation(), newIndex);
      movingIndex = this._collection.getIndex(moving);
      Playlistable item = null;
      if (desiredIndex < movingIndex) {
         for (int i = desiredIndex; i < movingIndex; i++) {
            item = (Playlistable)this._collection.getAt(i);
            item.setPlaylistIndex(item.getPlaylistIndex() + 1);
         }
      } else if (desiredIndex > movingIndex) {
         for (int i = movingIndex; i <= desiredIndex; i++) {
            item = (Playlistable)this._collection.getAt(i);
            item.setPlaylistIndex(item.getPlaylistIndex() - 1);
         }
      }

      this._collection.remove(moving);
      moving.setPlaylistIndex(newIndex);
      this._collection.fastAdd(moving, -1);
      this._collection.fireReset();
      if (save) {
         try {
            this.save();
         } finally {
            return;
         }
      }
   }

   final int addMediaURL(String url) {
      if (this._urls == null) {
         this._urls = new String[0];
         this._urlHashes = new IntIntHashtable(5);
      }

      Arrays.add(this._urls, url);
      int urlHash = StringUtilities.hashCodeIgnoreCase(URIDecoder.decode(url, "UTF-8"));
      int count = this._urlHashes.get(urlHash);
      if (count < 0) {
         count = 0;
      }

      this._urlHashes.put(urlHash, ++count);
      return this._urls.length - 1;
   }

   public final void addMedia(MediaInfo media, boolean save) {
      if (media instanceof Track) {
         String location = media.getLocation();
         if (location != null && location.length() > 0) {
            int index = this.addMediaURL(location);
            if (save) {
               try {
                  this.save();
               } finally {
                  return;
               }
            } else {
               Playlistable item = new Playlistable(media, index);
               this._collection.fastAdd(item, -1);
            }
         }
      }
   }

   public final void removeMedia(Playlistable media, boolean save) {
      int index = media.getPlaylistIndex();
      if (this._urls != null) {
         int urlHash = StringUtilities.hashCodeIgnoreCase(URIDecoder.decode(this._urls[index], "UTF-8"));
         if (this._urlHashes != null) {
            int count = this._urlHashes.get(urlHash);
            if (count >= 0) {
               if (--count <= 0) {
                  this._urlHashes.remove(urlHash);
               } else {
                  this._urlHashes.put(urlHash, count);
               }
            }
         }

         Arrays.removeAt(this._urls, index);
      }

      index = this._collection.getIndex(media);
      if (index >= 0) {
         for (int i = index + 1; i < this._collection.size(); i++) {
            Playlistable item = (Playlistable)this._collection.getAt(i);
            int itemIndex = item.getPlaylistIndex() - 1;
            item.setPlaylistIndex(itemIndex);
         }
      }

      this._collection.remove(media);
      if (save) {
         try {
            this.save();
         } finally {
            return;
         }
      }
   }

   final void addMediaFile(MediaInfo media) {
      if (media instanceof Track) {
         int id = media.getId();
         String url = URIDecoder.decode(media.getLocation(), "UTF-8");
         int urlHash = StringUtilities.hashCodeIgnoreCase(url);
         if (this._urlHashes != null) {
            if (this._urlHashes.get(urlHash) > 0) {
               if (this._collection.find(id) == null) {
                  if (this._urls != null) {
                     for (int i = 0; i < this._urls.length; i++) {
                        String comaprisonURL = URIDecoder.decode(this._urls[i], "UTF-8");
                        if (url.equalsIgnoreCase(comaprisonURL)) {
                           Playlistable item = new Playlistable(media, i);
                           this._collection.fastAdd(item, -1);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   final void removeMediaFile(MediaInfo media) {
      for (int i = this._collection.size() - 1; i >= 0; i--) {
         Playlistable item = (Playlistable)this._collection.getAt(i);
         if (media == item.getMediaInfo()) {
            this._collection.remove(item);
         }
      }
   }

   final void removeAllMedia() {
      this._collection.removeAll();
   }

   final void clear() {
      this._urls = null;
      if (this._urlHashes != null) {
         this._urlHashes.clear();
         this._urlHashes = null;
      }

      this.removeAllMedia();
   }

   public final int size() {
      return this._collection != null ? this._collection.size() : 0;
   }

   @Override
   public final int getId() {
      return this._id;
   }

   @Override
   public final String getLocation() {
      return this._location;
   }

   @Override
   public final String[] getKeywords() {
      return this._keywords;
   }

   @Override
   public final String[] getPrefixedKeywords() {
      return this._prefixedKeywords;
   }

   @Override
   public final void setPreloaded(boolean preloaded) {
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return MLUtilities.getKeys(this._keywords, context, keyArray, index, keyRequested);
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final String getName() {
      return this._name;
   }

   private final void setId(int id) {
      this._id = id;
   }

   @Override
   public final String toString() {
      return this.getName();
   }

   public PlaylistItem(int id, String location) {
      this.setId(id);
      this.setLocation(location);
      this._collection = new MediaInfoCollection(PlaylistIndexComparator.getInstance());
   }

   private final void setLocation(String location) {
      this._location = location;
   }
}
