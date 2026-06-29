package net.rim.device.apps.internal.explorer.MediaLibrary;

import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.device.internal.io.file.FilteredFileListener;
import net.rim.device.internal.media.Playlist;

public class PlayListCollection extends MediaInfoCollection implements FilteredFileListener {
   private MediaLibrary _library;

   public void onRemoveMedia(MediaInfo media) {
      synchronized (this._library.getLock()) {
         for (int i = this.size() - 1; i >= 0; i--) {
            Object playlist = this.getAt(i);
            if (playlist instanceof PlaylistItem) {
               ((PlaylistItem)playlist).removeMediaFile(media);
            }
         }
      }
   }

   public void onMedia(MediaInfo media) {
      synchronized (this._library.getLock()) {
         for (int i = this.size() - 1; i >= 0; i--) {
            Object playlist = this.getAt(i);
            if (playlist instanceof PlaylistItem) {
               ((PlaylistItem)playlist).addMediaFile(media);
            }
         }
      }
   }

   @Override
   public void fileDeleted(String path) {
      synchronized (this._library.getLock()) {
         String decodedPath = URIDecoder.decode(path, "UTF-8");
         int id = StringUtilities.hashCodeIgnoreCase(decodedPath);
         Object item = this.find(id);
         if (item != null) {
            this.remove(item);
         }
      }
   }

   @Override
   public void fileAdded(String param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ldc_w "UTF-8"
      // 004: invokestatic net/rim/device/cldc/io/utility/URIDecoder.decode (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
      // 007: astore 2
      // 008: aload 2
      // 009: invokestatic net/rim/device/api/util/StringUtilities.hashCodeIgnoreCase (Ljava/lang/String;)I
      // 00c: istore 3
      // 00d: aload 0
      // 00e: getfield net/rim/device/apps/internal/explorer/MediaLibrary/PlayListCollection._library Lnet/rim/device/apps/internal/explorer/MediaLibrary/MediaLibrary;
      // 011: invokevirtual net/rim/device/apps/internal/explorer/MediaLibrary/MediaLibrary.getLock ()Ljava/lang/Object;
      // 014: dup
      // 015: astore 4
      // 017: monitorenter
      // 018: aload 1
      // 019: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMIMEType (Ljava/lang/String;)Ljava/lang/String;
      // 01c: ldc_w "application/vnd.rim.smartlist"
      // 01f: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 022: ifeq 05b
      // 025: aload 0
      // 026: iload 3
      // 027: invokevirtual net/rim/device/apps/internal/explorer/MediaLibrary/MediaInfoCollection.find (I)Ljava/lang/Object;
      // 02a: checkcast net/rim/device/apps/internal/explorer/MediaLibrary/SmartlistItem
      // 02d: astore 5
      // 02f: aload 5
      // 031: ifnull 03d
      // 034: aload 5
      // 036: invokevirtual net/rim/device/apps/internal/explorer/MediaLibrary/SmartlistItem.parse ()V
      // 039: aload 4
      // 03b: monitorexit
      // 03c: return
      // 03d: new net/rim/device/apps/internal/explorer/MediaLibrary/SmartlistItem
      // 040: dup
      // 041: iload 3
      // 042: aload 1
      // 043: invokestatic net/rim/device/internal/io/file/FileUtilities.getDisplayBaseName (Ljava/lang/String;)Ljava/lang/String;
      // 046: aload 1
      // 047: invokespecial net/rim/device/apps/internal/explorer/MediaLibrary/SmartlistItem.<init> (ILjava/lang/String;Ljava/lang/String;)V
      // 04a: astore 5
      // 04c: aload 5
      // 04e: invokevirtual net/rim/device/apps/internal/explorer/MediaLibrary/SmartlistItem.parse ()V
      // 051: aload 0
      // 052: aload 5
      // 054: invokevirtual net/rim/device/apps/internal/explorer/MediaLibrary/MediaInfoCollection.add (Ljava/lang/Object;)V
      // 057: aload 4
      // 059: monitorexit
      // 05a: return
      // 05b: aload 0
      // 05c: iload 3
      // 05d: invokevirtual net/rim/device/apps/internal/explorer/MediaLibrary/MediaInfoCollection.find (I)Ljava/lang/Object;
      // 060: checkcast net/rim/device/apps/internal/explorer/MediaLibrary/PlaylistItem
      // 063: astore 5
      // 065: aload 5
      // 067: ifnull 072
      // 06a: aload 5
      // 06c: invokevirtual net/rim/device/apps/internal/explorer/MediaLibrary/PlaylistItem.clear ()V
      // 06f: goto 08c
      // 072: new net/rim/device/apps/internal/explorer/MediaLibrary/PlaylistItem
      // 075: dup
      // 076: iload 3
      // 077: aload 1
      // 078: invokespecial net/rim/device/apps/internal/explorer/MediaLibrary/PlaylistItem.<init> (ILjava/lang/String;)V
      // 07b: astore 5
      // 07d: aload 5
      // 07f: aload 1
      // 080: invokestatic net/rim/device/internal/io/file/FileUtilities.getDisplayBaseName (Ljava/lang/String;)Ljava/lang/String;
      // 083: invokevirtual net/rim/device/apps/internal/explorer/MediaLibrary/PlaylistItem.setName (Ljava/lang/String;)V
      // 086: aload 0
      // 087: aload 5
      // 089: invokevirtual net/rim/device/apps/internal/explorer/MediaLibrary/MediaInfoCollection.add (Ljava/lang/Object;)V
      // 08c: aconst_null
      // 08d: astore 6
      // 08f: aconst_null
      // 090: astore 7
      // 092: aload 1
      // 093: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 096: checkcast java/lang/Object
      // 099: astore 6
      // 09b: aload 6
      // 09d: invokeinterface javax/microedition/io/file/FileConnection.openInputStream ()Ljava/io/InputStream; 1
      // 0a2: astore 7
      // 0a4: aload 7
      // 0a6: invokestatic com/sun/cldc/i18n/Helper.getDefaultEncoding ()Ljava/lang/String;
      // 0a9: invokestatic com/sun/cldc/i18n/Helper.getStreamReader (Ljava/io/InputStream;Ljava/lang/String;)Ljava/io/Reader;
      // 0ac: astore 8
      // 0ae: ldc_w "audio/x-mpegurl"
      // 0b1: aload 8
      // 0b3: invokestatic net/rim/device/internal/media/Playlist.getPlaylist (Ljava/lang/String;Ljava/io/Reader;)Lnet/rim/device/internal/media/Playlist;
      // 0b6: astore 9
      // 0b8: aload 1
      // 0b9: aload 5
      // 0bb: aload 9
      // 0bd: invokestatic net/rim/device/apps/internal/explorer/MediaLibrary/PlayListCollection.createPlaylist (Ljava/lang/String;Lnet/rim/device/apps/internal/explorer/MediaLibrary/PlaylistItem;Lnet/rim/device/internal/media/Playlist;)V
      // 0c0: aload 7
      // 0c2: ifnull 0ca
      // 0c5: aload 7
      // 0c7: invokevirtual java/io/InputStream.close ()V
      // 0ca: aload 6
      // 0cc: ifnull 11e
      // 0cf: aload 6
      // 0d1: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0d6: goto 11e
      // 0d9: astore 8
      // 0db: goto 11e
      // 0de: astore 8
      // 0e0: aload 7
      // 0e2: ifnull 0ea
      // 0e5: aload 7
      // 0e7: invokevirtual java/io/InputStream.close ()V
      // 0ea: aload 6
      // 0ec: ifnull 11e
      // 0ef: aload 6
      // 0f1: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0f6: goto 11e
      // 0f9: astore 8
      // 0fb: goto 11e
      // 0fe: astore 10
      // 100: aload 7
      // 102: ifnull 10a
      // 105: aload 7
      // 107: invokevirtual java/io/InputStream.close ()V
      // 10a: aload 6
      // 10c: ifnull 11b
      // 10f: aload 6
      // 111: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 116: goto 11b
      // 119: astore 11
      // 11b: aload 10
      // 11d: athrow
      // 11e: aload 4
      // 120: monitorexit
      // 121: return
      // 122: astore 12
      // 124: aload 4
      // 126: monitorexit
      // 127: aload 12
      // 129: athrow
      // try (92 -> 100): 101 null
      // try (73 -> 92): 103 null
      // try (104 -> 112): 113 null
      // try (73 -> 92): 115 null
      // try (103 -> 104): 115 null
      // try (116 -> 124): 125 null
      // try (115 -> 116): 115 null
      // try (13 -> 29): 131 null
      // try (30 -> 45): 131 null
      // try (46 -> 130): 131 null
      // try (131 -> 134): 131 null
   }

   @Override
   public void remove(Object element) {
      if (element instanceof PlaylistItem) {
         PlaylistItem playlist = (PlaylistItem)element;
         playlist.removeAllMedia();
      }

      super.remove(element);
   }

   private static void createPlaylist(String path, PlaylistItem playlistItem, Playlist playlist) {
      if (path != null && playlistItem != null && playlist != null) {
         MediaInfoCollection trackCollection = MediaLibrary.getInstance().getTrackCollection();
         MediaInfoCollection videoCollection = MediaLibrary.getInstance().getVideoCollection();

         for (int i = 0; i < playlist.getNumberOfItems(); i++) {
            String encodedurl = playlist.getUrl(i);
            if (encodedurl != null) {
               encodedurl = URI.getAbsoluteURL(encodedurl, path);
               String url = URIDecoder.decode(encodedurl, "UTF-8");
               int id = StringUtilities.hashCodeIgnoreCase(url);
               int mediaType = MIMETypeAssociations.getMediaType(url);
               MediaInfo media = null;
               switch (mediaType) {
                  case 2:
                     media = (MediaInfo)trackCollection.find(id);
               }

               if (media != null) {
                  playlistItem.addMedia(media, false);
               } else {
                  playlistItem.addMediaURL(encodedurl);
               }
            }
         }
      } else {
         throw new Object();
      }
   }

   PlayListCollection(MediaLibrary library) {
      this._library = library;
   }
}
