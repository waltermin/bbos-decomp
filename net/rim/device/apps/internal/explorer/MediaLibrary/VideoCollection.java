package net.rim.device.apps.internal.explorer.MediaLibrary;

import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.device.internal.io.file.MetaDataFileInfo;
import net.rim.device.internal.io.file.MetaDataListener;

public class VideoCollection extends MediaInfoCollection implements MetaDataListener {
   private MediaLibrary _library;
   private static final String _preloadedVideoPath = "file:///store/samples/videos/";

   public VideoCollection(MediaLibrary library) {
      this._library = library;
   }

   @Override
   public void metaDataAdded(MetaDataFileInfo info) {
      String pathURL = ((StringBuffer)(new Object())).append(info.getPath()).append(info.getFileName()).toString();
      String decodedURL = URIDecoder.decode(pathURL, "UTF-8");
      int id = StringUtilities.hashCodeIgnoreCase(decodedURL);
      synchronized (this._library.getLock()) {
         Video video = (Video)this.find(id);
         if (video == null) {
            video = new Video(id, pathURL);
            if (pathURL.regionMatches(true, 0, "file:///store/samples/videos/", 0, 29)) {
               video.setPreloaded(true);
            } else {
               video.setPreloaded(false);
            }

            this.add(video);
            MediaLibrary.getInstance().getPlaylistCollection().onMedia(video);
         }

         Object bookmark = info.getMetaData(7);
         if (bookmark instanceof Object) {
            video.setBookmark(bookmark);
         }
      }
   }

   @Override
   public void metaDataDeleted(String path) {
      String decodedPath = URIDecoder.decode(path, "UTF-8");
      int id = StringUtilities.hashCodeIgnoreCase(decodedPath);
      Video video = (Video)this.find(id);
      if (video != null) {
         this.remove(video);
         MediaLibrary.getInstance().getPlaylistCollection().onRemoveMedia(video);
      }
   }

   @Override
   public void metaDataFileUnavailable(String folderPath) {
      synchronized (this._library.getLock()) {
         for (int i = this.size() - 1; i >= 0; i--) {
            Video video = (Video)this.getAt(i);
            if (video.getLocation().regionMatches(true, 0, folderPath, 0, folderPath.length())) {
               this.remove(video);
            }
         }
      }
   }
}
