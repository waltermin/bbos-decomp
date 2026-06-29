package net.rim.device.apps.internal.explorer.MediaLibrary;

import java.io.DataOutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.MediaLibrary.util.MLUtilities;
import net.rim.vm.Array;

public final class SmartlistItem implements MediaInfo, KeyProvider, PaintProvider, VerbProvider {
   int _id;
   String _name;
   String _location;
   String[] _keywords;
   String[] _prefixedKeywords;
   String[] _artists = new Object[0];
   String[] _albums = new Object[0];
   String[] _genres = new Object[0];
   String _unknown;
   private static Bitmap _image = Bitmap.getBitmapResource("lightbulbsmall.png");
   private static final int VERSION;
   private static final int END_OF_FILE;

   @Override
   public final int paint(Graphics graphics, int x, int y, int width, int height, Object context) {
      int iwidth = _image.getWidth();
      int iheight = _image.getHeight();
      graphics.drawBitmap(x, y - 3 + (height - iheight >> 1), iwidth, iheight, _image, 0, 0);
      graphics.drawText(this.toString(), x + iwidth + 2, y, 64, width);
      return 0;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      return null;
   }

   final void parse() {
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
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/explorer/MediaLibrary/SmartlistItem._artists [Ljava/lang/String;
      // 04: bipush 0
      // 05: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 08: aload 0
      // 09: getfield net/rim/device/apps/internal/explorer/MediaLibrary/SmartlistItem._albums [Ljava/lang/String;
      // 0c: bipush 0
      // 0d: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 10: aload 0
      // 11: getfield net/rim/device/apps/internal/explorer/MediaLibrary/SmartlistItem._genres [Ljava/lang/String;
      // 14: bipush 0
      // 15: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 18: aconst_null
      // 19: astore 1
      // 1a: aconst_null
      // 1b: astore 2
      // 1c: aload 0
      // 1d: invokevirtual net/rim/device/apps/internal/explorer/MediaLibrary/SmartlistItem.getLocation ()Ljava/lang/String;
      // 20: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 23: checkcast java/lang/Object
      // 26: astore 1
      // 27: aload 1
      // 28: invokeinterface javax/microedition/io/InputConnection.openDataInputStream ()Ljava/io/DataInputStream; 1
      // 2d: astore 2
      // 2e: aload 2
      // 2f: invokevirtual java/io/DataInputStream.readByte ()B
      // 32: bipush 1
      // 33: if_icmpeq 41
      // 36: new java/lang/Object
      // 39: dup
      // 3a: ldc_w "version of playlist file is unsupported"
      // 3d: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 40: athrow
      // 41: bipush 0
      // 42: istore 3
      // 43: aconst_null
      // 44: astore 4
      // 46: iload 3
      // 47: ifne b7
      // 4a: aload 2
      // 4b: invokevirtual java/io/DataInputStream.readByte ()B
      // 4e: sipush 255
      // 51: iand
      // 52: istore 5
      // 54: aload 2
      // 55: invokevirtual java/io/DataInputStream.readInt ()I
      // 58: istore 6
      // 5a: iload 5
      // 5c: lookupswitch 80 4 0 78 2 44 4 44 8 44
      // 88: iload 6
      // 8a: newarray 8
      // 8c: astore 4
      // 8e: aload 2
      // 8f: aload 4
      // 91: invokevirtual java/io/DataInputStream.readFully ([B)V
      // 94: aload 0
      // 95: iload 5
      // 97: new java/lang/Object
      // 9a: dup
      // 9b: aload 4
      // 9d: ldc_w "UTF-8"
      // a0: invokespecial java/lang/String.<init> ([BLjava/lang/String;)V
      // a3: invokevirtual net/rim/device/apps/internal/explorer/MediaLibrary/SmartlistItem.addItem (ILjava/lang/String;)Z
      // a6: pop
      // a7: goto 46
      // aa: bipush 1
      // ab: istore 3
      // ac: aload 2
      // ad: iload 6
      // af: i2l
      // b0: invokevirtual java/io/DataInputStream.skip (J)J
      // b3: pop2
      // b4: goto 46
      // b7: aload 1
      // b8: ifnull c1
      // bb: aload 1
      // bc: invokeinterface javax/microedition/io/Connection.close ()V 1
      // c1: aload 2
      // c2: ifnull fe
      // c5: aload 2
      // c6: invokevirtual java/io/DataInputStream.close ()V
      // c9: return
      // ca: astore 3
      // cb: return
      // cc: astore 3
      // cd: aload 1
      // ce: ifnull d7
      // d1: aload 1
      // d2: invokeinterface javax/microedition/io/Connection.close ()V 1
      // d7: aload 2
      // d8: ifnull fe
      // db: aload 2
      // dc: invokevirtual java/io/DataInputStream.close ()V
      // df: return
      // e0: astore 3
      // e1: return
      // e2: astore 7
      // e4: aload 1
      // e5: ifnull ee
      // e8: aload 1
      // e9: invokeinterface javax/microedition/io/Connection.close ()V 1
      // ee: aload 2
      // ef: ifnull fb
      // f2: aload 2
      // f3: invokevirtual java/io/DataInputStream.close ()V
      // f6: goto fb
      // f9: astore 8
      // fb: aload 7
      // fd: athrow
      // fe: return
      // try (73 -> 81): 82 null
      // try (16 -> 73): 84 null
      // try (85 -> 93): 94 null
      // try (16 -> 73): 96 null
      // try (84 -> 85): 96 null
      // try (97 -> 105): 106 null
      // try (96 -> 97): 96 null
   }

   public final boolean addItem(int type, String item) {
      item = item.trim();
      String[] array = null;
      switch (type) {
         case 2:
            array = this._artists;
            break;
         case 4:
            array = this._albums;
            break;
         case 8:
            array = this._genres;
            break;
         default:
            return false;
      }

      if (array != null && !Arrays.contains(array, item)) {
         Arrays.add(array, item);
         return true;
      } else {
         return false;
      }
   }

   public final boolean removeItem(int type, String item) {
      item = item.trim();
      String[] array = null;
      switch (type) {
         case 2:
            array = this._artists;
            break;
         case 4:
            array = this._albums;
            break;
         case 8:
            array = this._genres;
            break;
         default:
            return false;
      }

      if (array != null && Arrays.contains(array, item)) {
         Arrays.remove(array, item);
         return true;
      } else {
         return false;
      }
   }

   public final String[] getItems(int type) {
      switch (type) {
         case 2:
            return this._artists;
         case 4:
            return this._albums;
         case 8:
            return this._genres;
         default:
            return null;
      }
   }

   public final MediaInfoCollection getTracks() {
      long mills = System.currentTimeMillis();
      MediaInfoCollection tracks = new MediaInfoCollection();
      MediaInfoCollection trackc = MediaLibrary.getInstance().getTrackCollection();
      KeywordFilterList list = trackc.getKeywordFilterList();
      String[] criteria = new Object[0];
      String temp = null;
      int id = 0;
      if (this._artists.length == 0 && this._albums.length == 0 && this._genres.length == 0) {
         return trackc;
      }

      for (int i = 0; i <= this._artists.length; i++) {
         if (i != 0 || this._artists.length != 0) {
            if (i == this._artists.length) {
               break;
            }

            temp = this._artists[i];
            if (this.isUnknownString(temp)) {
               id = FilterConstants.UNKNOWN_ID;
            } else {
               id = StringUtilities.hashCodeIgnoreCase(temp);
            }

            Artist artist = (Artist)MediaLibrary.getInstance().getArtistCollection().find(id);
            if (artist != null) {
               Arrays.append(criteria, artist.getPrefixedKeywords());
            }
         }

         int artistlength = criteria.length;

         for (int j = 0; j <= this._albums.length; j++) {
            if (j != 0 || this._albums.length != 0) {
               if (j == this._albums.length) {
                  break;
               }

               temp = this._albums[j];
               if (this.isUnknownString(temp)) {
                  id = FilterConstants.UNKNOWN_ID;
               } else {
                  id = StringUtilities.hashCodeIgnoreCase(temp);
               }

               Album album = (Album)MediaLibrary.getInstance().getAlbumCollection().find(id);
               if (album != null) {
                  Arrays.append(criteria, album.getPrefixedKeywords());
               }
            }

            int albumlength = criteria.length;

            for (int k = 0; k <= this._genres.length; k++) {
               if (k != 0 || this._genres.length != 0) {
                  if (k == this._genres.length) {
                     break;
                  }

                  temp = this._genres[k];
                  if (this.isUnknownString(temp)) {
                     id = FilterConstants.UNKNOWN_ID;
                  } else {
                     id = StringUtilities.hashCodeIgnoreCase(temp);
                  }

                  Genre genre = (Genre)MediaLibrary.getInstance().getGenreCollection().find(id);
                  if (genre != null) {
                     Arrays.append(criteria, genre.getPrefixedKeywords());
                  }
               }

               trackc.addCriteria(null, null);
               trackc.addCriteria(null, criteria);
               int size = list.size();

               for (int idx = 0; idx < size; idx++) {
                  tracks.add((Track)list.getAt(idx));
               }

               Array.resize(criteria, albumlength);
            }

            Array.resize(criteria, artistlength);
         }

         Array.resize(criteria, 0);
      }

      trackc.addCriteria(null, null);
      System.out.println(((StringBuffer)(new Object("Creating playlist took: "))).append(System.currentTimeMillis() - mills).append("ms").toString());
      return tracks;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void save() {
      FileConnection fc = null;
      DataOutputStream out = null;
      boolean var21 = false /* VF: Semaphore variable */;

      try {
         try {
            var21 = true;
            StreamConnection ioe = Connector.open(this.getLocation(), 3);
            if (!(ioe instanceof Object)) {
               throw new Object(((StringBuffer)(new Object("Smartlist "))).append(this.getName()).append(" cannot be saved").toString());
            }

            fc = (FileConnection)ioe;
            if (!fc.exists()) {
               fc.create();
            }

            fc.truncate(0);
            out = fc.openDataOutputStream();
            String temp = null;
            byte[] bytes = null;
            out.writeByte(1);

            for (int i = 0; i < this._artists.length; i++) {
               temp = this._artists[i];
               bytes = temp.getBytes("utf-8");
               out.write(2);
               out.writeInt(bytes.length);
               out.write(bytes);
            }

            for (int i = 0; i < this._albums.length; i++) {
               temp = this._albums[i];
               bytes = temp.getBytes("utf-8");
               out.write(4);
               out.writeInt(bytes.length);
               out.write(bytes);
            }

            for (int i = 0; i < this._genres.length; i++) {
               temp = this._genres[i];
               bytes = temp.getBytes("utf-8");
               out.write(8);
               out.writeInt(bytes.length);
               out.write(bytes);
            }

            out.write(0);
            out.writeInt(0);
            var21 = false;
         } finally {
            ;
         }
      } finally {
         if (var21) {
            label236:
            try {
               if (fc != null) {
                  fc.close();
               }

               if (out != null) {
                  out.close();
               }
            } finally {
               break label236;
            }
         }
      }

      try {
         if (fc != null) {
            fc.close();
         }

         if (out != null) {
            out.close();
         }
      } finally {
         return;
      }
   }

   @Override
   public final String getName() {
      return this._name;
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
   public final int getId() {
      return this._id;
   }

   @Override
   public final String getLocation() {
      return this._location;
   }

   private final void setLocation(String location) {
      this._location = location;
   }

   private final boolean isUnknownString(String string) {
      if (this._unknown == null) {
         ResourceBundle rb = ResourceBundle.getBundle(349501092522026426L, "net.rim.device.apps.internal.resource.Explorer");
         this._unknown = rb.getString(148);
      }

      return string.equals(this._unknown);
   }

   public SmartlistItem(int id, String name, String location) {
      this.setId(id);
      this.setName(name);
      this.setLocation(location);
   }

   private final void setId(int id) {
      this._id = id;
   }

   @Override
   public final String toString() {
      return this.getName();
   }

   private final void setName(String name) {
      if (name != null) {
         this._name = name.trim();
         this._keywords = StringUtilities.stringToKeywords(this._name);
         this._prefixedKeywords = new Object[1];
         this._prefixedKeywords[0] = FilterConstants.SMARTLIST_PREFIX;
         Arrays.append(this._keywords, this._prefixedKeywords);
      }
   }
}
