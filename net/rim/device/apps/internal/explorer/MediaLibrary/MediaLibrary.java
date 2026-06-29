package net.rim.device.apps.internal.explorer.MediaLibrary;

import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.device.internal.io.file.FileIndexService;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.io.file.MetaDataFileInfo;
import net.rim.device.internal.io.file.MetaDataListener;

public class MediaLibrary implements MetaDataListener {
   private MediaInfoCollection _trackCollection;
   private MediaInfoCollection _artistCollection;
   private MediaInfoCollection _albumCollection;
   private MediaInfoCollection _genreCollection;
   private PlayListCollection _playlistCollection;
   private MediaInfoCollection _ringtoneCollection;
   private VideoCollection _videoCollection;
   private MediaInfoCollection _voiceNotesCollection;
   private MediaInfoCollection _preloadedTrackCollection;
   private final Object _lock = new Object();
   private boolean _backdoor;
   private static final long GUID = -4618605792851823899L;
   private static final String[] _ringtonePaths = new String[]{
      "file:///SDCard/BlackBerry/ringtones/", "file:///store/samples/ringtones/", "file:///store/home/user/ringtones/"
   };
   private static final String[] _voiceNotePaths = new String[]{"file:///SDCard/BlackBerry/voicenotes/", "file:///store/home/user/voicenotes/"};
   private static final String _preloadedSamplesPath = "file:///store/samples/";
   private static final String _preloadedRingtonePath = "file:///store/samples/ringtones/";
   private static final String _preloadedTrackPath = "file:///store/samples/music/";

   public Object getLock() {
      return this._lock;
   }

   public MediaInfoCollection getTrackCollection() {
      return this._trackCollection;
   }

   public MediaInfoCollection getPreloadedTrackCollection() {
      return this._preloadedTrackCollection;
   }

   public MediaInfoCollection getArtistCollection() {
      return this._artistCollection;
   }

   public MediaInfoCollection getAlbumCollection() {
      return this._albumCollection;
   }

   public MediaInfoCollection getGenreCollection() {
      return this._genreCollection;
   }

   public PlayListCollection getPlaylistCollection() {
      return this._playlistCollection;
   }

   public MediaInfoCollection getRingtoneCollection() {
      return this._ringtoneCollection;
   }

   public MediaInfoCollection getVideoCollection() {
      return this._videoCollection;
   }

   public MediaInfoCollection getVoiceNotesCollection() {
      return this._voiceNotesCollection;
   }

   void onRingtone(MetaDataFileInfo info) {
      synchronized (this._lock) {
         String filename = info.getPath() + info.getFileName();
         String decodedFilename = URIDecoder.decode(filename, "UTF-8");
         int id = StringUtilities.hashCodeIgnoreCase(decodedFilename);
         Track track = new Track(id, filename);
         Object title = info.getMetaData(1);
         Object length = info.getMetaData(2);
         String temp = null;
         if (title instanceof String) {
            temp = (String)title;
            if (temp.length() > 0) {
               track.setTitle(temp);
            }
         }

         if (length instanceof Long) {
            Long len = (Long)length;
            track.setLength(len);
         }

         track.generateKeywords();
         if (filename.regionMatches(true, 0, "file:///store/samples/ringtones/", 0, 32)) {
            track.setPreloaded(true);
         } else {
            track.setPreloaded(false);
         }

         this._ringtoneCollection.add(track);
      }
   }

   void onTrack(MetaDataFileInfo info) {
      synchronized (this._lock) {
         String filename = info.getPath() + info.getFileName();
         String decodedFilename = URIDecoder.decode(filename, "UTF-8");
         int id = StringUtilities.hashCodeIgnoreCase(decodedFilename);
         Track track = null;
         Artist artist = null;
         Album album = null;
         Genre genre = null;
         boolean preloaded = false;
         if (filename.regionMatches(true, 0, "file:///store/samples/music/", 0, 28)) {
            preloaded = true;
         } else if (filename.regionMatches(true, 0, "file:///store/samples/", 0, 22)) {
            return;
         }

         track = new Track(id, filename);
         Object titleStr = info.getMetaData(1);
         Object artistStr = info.getMetaData(3);
         Object albumStr = info.getMetaData(4);
         Object genreStr = info.getMetaData(5);
         Object length = info.getMetaData(2);
         Object trackNumStr = info.getMetaData(6);
         String temp = null;
         int var25 = -1;
         if (length instanceof Long) {
            Long len = (Long)length;
            track.setLength(len);
         }

         if (titleStr instanceof String) {
            temp = (String)titleStr;
            if (temp.length() > 0) {
               track.setTitle(temp);
            }
         }

         temp = this.castOrCreateTitle(artistStr);
         if (temp != null) {
            var25 = StringUtilities.hashCodeIgnoreCase(temp);
         } else {
            var25 = FilterConstants.UNKNOWN_ID;
         }

         if (preloaded) {
            Artist preloadedArtist = new Artist(var25);
            preloadedArtist.setName(temp);
            track.setArtist(preloadedArtist);
            track.generateKeywords();
            this._preloadedTrackCollection.add(track);
         } else {
            artist = (Artist)this._artistCollection.find(var25);
            if (artist == null) {
               artist = new Artist(var25);
               artist.setName(temp);
            }

            track.setArtist(artist);
            artist.addTrack(track);
            temp = this.castOrCreateTitle(albumStr);
            if (temp != null) {
               var25 = StringUtilities.hashCodeIgnoreCase(temp);
            } else {
               var25 = FilterConstants.UNKNOWN_ID;
            }

            album = (Album)this._albumCollection.find(var25);
            if (album == null) {
               album = new Album(var25);
               album.setName(temp);
            }

            track.setAlbum(album);
            album.addArtist(artist);
            album.addTrack(track);
            temp = this.castOrCreateTitle(genreStr);
            if (temp != null) {
               var25 = StringUtilities.hashCodeIgnoreCase(temp);
            } else {
               var25 = FilterConstants.UNKNOWN_ID;
            }

            genre = (Genre)this._genreCollection.find(var25);
            if (genre == null) {
               genre = new Genre(var25);
               genre.setName(temp);
            }

            track.setGenre(genre);
            genre.addTrack(track);
            if (artist != null) {
               artist.addGenre(genre);
            }

            if (album != null) {
               album.addGenre(genre);
            }

            if (trackNumStr instanceof String) {
               temp = (String)trackNumStr;

               label136:
               try {
                  int index = temp.indexOf(47);
                  if (index >= 0) {
                     temp = temp.substring(0, index);
                  }

                  int trackNumber = Integer.parseInt(temp);
                  track.setTrackNumber(trackNumber);
               } finally {
                  break label136;
               }
            }

            track.generateKeywords();
            this._trackCollection.add(track);
            if (artist.getName() == null) {
               this._artistCollection.add(artist, 0);
            } else {
               this._artistCollection.add(artist);
            }

            if (album.getName() == null) {
               this._albumCollection.add(album, 0);
            } else {
               this._albumCollection.add(album);
            }

            if (genre.getName() == null) {
               this._genreCollection.add(genre, 0);
            } else {
               this._genreCollection.add(genre);
            }

            this.getPlaylistCollection().onMedia(track);
         }
      }
   }

   void onVoiceNote(MetaDataFileInfo info) {
      synchronized (this._lock) {
         String filename = info.getPath() + info.getFileName();
         String decodedFilename = URIDecoder.decode(filename, "UTF-8");
         int id = StringUtilities.hashCodeIgnoreCase(decodedFilename);
         VoiceNote voicenote = new VoiceNote(id, filename);
         Object length = info.getMetaData(2);
         if (length instanceof Long) {
            Long len = (Long)length;
            voicenote.setLength(len);
         }

         this._voiceNotesCollection.add(voicenote);
      }
   }

   public boolean getBackdoor() {
      return this._backdoor;
   }

   public void setBackdoor(boolean backdoor) {
      this._backdoor = backdoor;
   }

   @Override
   public void metaDataFileUnavailable(String folderPath) {
      synchronized (this._lock) {
         MediaInfo media = null;
         Track track = null;
         String location = null;
         VoiceNote voiceNote = null;

         for (int i = this._playlistCollection.size() - 1; i >= 0; i--) {
            media = (MediaInfo)this._playlistCollection.getAt(i);
            location = media.getLocation();
            if (location.regionMatches(true, 0, folderPath, 0, folderPath.length())) {
               this._playlistCollection.remove(media);
            }
         }

         for (int i = this._trackCollection.size() - 1; i >= 0; i--) {
            track = (Track)this._trackCollection.getAt(i);
            location = track.getLocation();
            if (location.regionMatches(true, 0, folderPath, 0, folderPath.length())) {
               this.onRemoveTrack(track);
            }
         }

         for (int i = this._ringtoneCollection.size() - 1; i >= 0; i--) {
            track = (Track)this._ringtoneCollection.getAt(i);
            location = track.getLocation();
            if (location.regionMatches(true, 0, folderPath, 0, folderPath.length())) {
               this._ringtoneCollection.remove(track);
            }
         }

         this._videoCollection.metaDataFileUnavailable(folderPath);

         for (int i = this._voiceNotesCollection.size() - 1; i >= 0; i--) {
            voiceNote = (VoiceNote)this._voiceNotesCollection.getAt(i);
            location = voiceNote.getLocation();
            if (location.regionMatches(true, 0, folderPath, 0, folderPath.length())) {
               this._voiceNotesCollection.remove(voiceNote);
            }
         }
      }
   }

   @Override
   public void metaDataDeleted(String filePath) {
      int mediaType = MIMETypeAssociations.getMediaType(filePath);
      switch (mediaType) {
         case 2:
         default:
            if (determineVoiceNotePath(filePath)) {
               this.onRemoveVoiceNote(filePath);
               return;
            } else {
               if (determineRingTonePath(FileUtilities.getPathURL(filePath), FileUtilities.getName(filePath))) {
                  this.onRemoveRingtone(filePath);
                  return;
               }

               this.onRemoveTrack(filePath);
               return;
            }
         case 3:
            this._videoCollection.metaDataDeleted(filePath);
         case 1:
      }
   }

   @Override
   public void metaDataAdded(MetaDataFileInfo info) {
      int mediaType = MIMETypeAssociations.getMediaType(info.getFileName());
      switch (mediaType) {
         case 2:
         default:
            if (determineVoiceNotePath(info.getPath())) {
               this.onVoiceNote(info);
               return;
            } else {
               if (determineRingTonePath(info.getPath(), info.getFileName())) {
                  this.onRingtone(info);
                  return;
               }

               this.onTrack(info);
               return;
            }
         case 3:
            this._videoCollection.metaDataAdded(info);
         case 1:
      }
   }

   private String castOrCreateTitle(Object string) {
      String temp = null;
      if (string instanceof String) {
         temp = (String)string;
         temp = temp.trim();
         if (temp.length() <= 0) {
            temp = null;
         }
      }

      return temp;
   }

   public static MediaLibrary getInstance() {
      MediaLibrary library = (MediaLibrary)ApplicationRegistry.getApplicationRegistry().get(-4618605792851823899L);
      if (library == null) {
         library = new MediaLibrary();
         ApplicationRegistry.getApplicationRegistry().replace(-4618605792851823899L, library);
      }

      return library;
   }

   private void onRemoveTrack(Object data) {
      synchronized (this._lock) {
         Track track = null;
         if (!(data instanceof String)) {
            if (data instanceof Track) {
               track = (Track)data;
            }
         } else {
            String decodedPath = URIDecoder.decode((String)data, "UTF-8");
            int id = StringUtilities.hashCodeIgnoreCase(decodedPath);
            track = (Track)this._trackCollection.find(id);
         }

         if (track != null) {
            Artist artist = track.getArtist();
            Album album = track.getAlbum();
            Genre genre = track.getGenre();
            if (artist != null) {
               artist.removeTrack(track);
               if (artist.getTrackCount() <= 0) {
                  this._artistCollection.remove(artist);
                  if (album != null) {
                     album.removeArtist(artist);
                  }
               }
            }

            if (album != null) {
               album.removeTrack(track);
               if (album.getTrackCount() <= 0) {
                  this._albumCollection.remove(album);
               }
            }

            if (genre != null) {
               genre.removeTrack(track);
               if (genre.getTrackCount() <= 0) {
                  this._genreCollection.remove(genre);
                  if (artist != null) {
                     artist.removeGenre(genre);
                  }

                  if (album != null) {
                     album.removeGenre(genre);
                  }
               }
            }

            this._trackCollection.remove(track);
            this.getPlaylistCollection().onRemoveMedia(track);
         }
      }
   }

   private void onRemoveRingtone(String path) {
      synchronized (this._lock) {
         String decodedPath = URIDecoder.decode(path, "UTF-8");
         int id = StringUtilities.hashCodeIgnoreCase(decodedPath);
         Track tone = (Track)this._ringtoneCollection.find(id);
         if (tone != null) {
            this._ringtoneCollection.remove(tone);
         }
      }
   }

   private void onRemoveVoiceNote(String path) {
      synchronized (this._lock) {
         String decodedPath = URIDecoder.decode(path, "UTF-8");
         int id = StringUtilities.hashCodeIgnoreCase(decodedPath);
         VoiceNote vn = (VoiceNote)this._voiceNotesCollection.find(id);
         if (vn != null) {
            this._voiceNotesCollection.remove(vn);
         }
      }
   }

   private static final boolean determineVoiceNotePath(String path) {
      int i = _voiceNotePaths.length;

      while (--i >= 0) {
         String voiceNotePath = _voiceNotePaths[i];
         if (path.regionMatches(true, 0, voiceNotePath, 0, voiceNotePath.length())) {
            return true;
         }
      }

      return false;
   }

   public static void libMain(String[] args) {
      FileIndexService indexService = FileIndexService.getService();
      if (indexService != null) {
         MediaLibrary library = getInstance();
         indexService.addMetaDataListener(library);
         indexService.addFilteredFileListener(library.getPlaylistCollection(), 7);
         indexService.requestScan();
      }
   }

   private static final boolean determineRingTonePath(String path, String name) {
      int i = _ringtonePaths.length;

      while (--i >= 0) {
         String ringtonePath = _ringtonePaths[i];
         if (path.regionMatches(true, 0, ringtonePath, 0, ringtonePath.length())) {
            return true;
         }
      }

      return name != null && "audio/amr".equals(MIMETypeAssociations.getMIMEType(name));
   }

   private MediaLibrary() {
      this._trackCollection = new MediaInfoCollection();
      this._artistCollection = new MediaInfoCollection();
      this._albumCollection = new MediaInfoCollection();
      this._genreCollection = new MediaInfoCollection();
      this._playlistCollection = new PlayListCollection(this);
      this._ringtoneCollection = new MediaInfoCollection();
      this._videoCollection = new VideoCollection(this);
      this._voiceNotesCollection = new MediaInfoCollection();
      this._preloadedTrackCollection = new MediaInfoCollection();
   }
}
