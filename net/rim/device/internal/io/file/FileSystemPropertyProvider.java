package net.rim.device.internal.io.file;

import net.rim.device.internal.system.SystemPropertyProvider;

public final class FileSystemPropertyProvider implements SystemPropertyProvider {
   @Override
   public final String getProperty(String property) {
      if (property.equals("fileconn.dir.photos")) {
         return "file:///store/home/user/pictures/";
      } else if (property.equals("fileconn.dir.photos.name")) {
         return "Pictures";
      } else if (property.equals("fileconn.dir.videos")) {
         return "file:///store/home/user/videos/";
      } else if (property.equals("fileconn.dir.videos.name")) {
         return "Videos";
      } else if (property.equals("fileconn.dir.tones")) {
         return "file:///store/home/user/ringtones/";
      } else if (property.equals("fileconn.dir.tones.name")) {
         return "Ringtones";
      } else if (property.equals("fileconn.dir.music")) {
         return "file:///store/home/user/ringtones/";
      } else if (property.equals("fileconn.dir.music.name")) {
         return "Music";
      } else if (property.equals("fileconn.dir.memorycard")) {
         return "file:///SDCard/";
      } else if (property.equals("fileconn.dir.memorycard.name")) {
         return "Media Card";
      } else if (property.equals("fileconn.dir.memorycard.photos")) {
         return "file:///SDCard/BlackBerry/pictures/";
      } else if (property.equals("fileconn.dir.memorycard.photos.name")) {
         return "Pictures ";
      } else if (property.equals("fileconn.dir.memorycard.videos")) {
         return "file:///SDCard/BlackBerry/videos/";
      } else if (property.equals("fileconn.dir.memorycard.videos.name")) {
         return "Videos";
      } else if (property.equals("fileconn.dir.memorycard.tones")) {
         return "file:///SDCard/BlackBerry/ringtones/";
      } else if (property.equals("fileconn.dir.memorycard.tones.name")) {
         return "Ringtones";
      } else if (property.equals("fileconn.dir.memorycard.music")) {
         return "file:///SDCard/BlackBerry/ringtones/";
      } else {
         return property.equals("fileconn.dir.memorycard.music.name") ? "Music" : null;
      }
   }
}
