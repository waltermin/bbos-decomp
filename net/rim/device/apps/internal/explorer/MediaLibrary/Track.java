package net.rim.device.apps.internal.explorer.MediaLibrary;

import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.TextMetrics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.MediaLibrary.util.MLUtilities;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.vm.Array;

public final class Track implements MediaInfo, PaintProvider, KeyProvider, VerbProvider {
   int _id;
   String _title;
   Artist _artist;
   Album _album;
   Genre _genre;
   long _length;
   int _trackNumber = Integer.MAX_VALUE;
   String _location;
   String[] _keywords;

   @Override
   public final int paint(Graphics graphics, int x, int y, int width, int height, Object context) {
      boolean trackNumberNeeded = false;
      String trackStr = "";
      if (context instanceof ContextInfo) {
         ContextInfo contextInfo = (ContextInfo)context;
         if ((contextInfo.getType() & 4) != 0 && this._album.getName() != null) {
            trackNumberNeeded = true;
            if (this._trackNumber == Integer.MAX_VALUE) {
               trackNumberNeeded = false;
            } else if (this._trackNumber >= 10) {
               trackStr = String.valueOf(this._trackNumber);
            } else {
               trackStr = '0' + String.valueOf(this._trackNumber);
            }
         }
      }

      TextMetrics metrics = Ui.getTmpTextMetrics();
      DrawTextParam drawParam = Ui.getTmpDrawTextParam();
      int endPos = -1;
      String displayName = this.toString();
      String lengthString = "";
      if (this.toString() != null) {
         displayName = this.toString();
         endPos = displayName.length();
      }

      if (this._length >= 0) {
         int dur = (int)(this.getLength() / 1000000);
         lengthString = Integer.toString(dur / 60) + ':' + NumberUtilities.toString(dur % 60, 10, 2);
      }

      drawParam.iTruncateWithEllipsis = 2;
      int rightColWidth = graphics.getFont().getAdvance("44:44");
      int leftColWidth = graphics.getFont().getAdvance("44");
      int xPadding = 2;
      if (context instanceof ContextInfo && (((ContextInfo)context).getType() & 64) != 0) {
         xPadding = 5;
      }

      drawParam.iMaxAdvance = rightColWidth;
      drawParam.iAlignment = 53;
      graphics.drawText(lengthString, 0, lengthString.length(), width - rightColWidth - 2, y, drawParam, metrics);
      if (!trackNumberNeeded) {
         drawParam.iMaxAdvance = width - rightColWidth - 5;
         drawParam.iAlignment = 54;
         graphics.drawText(displayName, 0, endPos, xPadding, y, drawParam, metrics);
      } else {
         drawParam.iMaxAdvance = leftColWidth;
         drawParam.iAlignment = 54;
         graphics.drawText(trackStr, 0, trackStr.length(), 1, y, drawParam, metrics);
         drawParam.iMaxAdvance = width - rightColWidth - 2 - 5 - leftColWidth - 5 - 1;
         drawParam.iAlignment = 54;
         int xPos = leftColWidth + 5;
         graphics.drawText(displayName, 0, endPos, xPos, y, drawParam, metrics);
      }

      Font font = graphics.getFont();
      int fontHeightInPt = font.getHeight(2);
      Font newFont = font.derive(0, fontHeightInPt - 1, 2);
      if (height >= font.getHeight() + newFont.getHeight()) {
         graphics.setFont(newFont);
         int globalAlpha = graphics.getGlobalAlpha();
         graphics.setGlobalAlpha(210);
         int yPos = y + font.getHeight();
         int genreRightColWidth = 0;
         Genre genre = this.getGenre();
         Artist artist = this.getArtist();
         if (genre != null) {
            genreRightColWidth = newFont.getAdvance(genre.toString());
            rightColWidth = Math.max(genreRightColWidth + 2, rightColWidth);
            graphics.drawText(genre.toString(), width - genreRightColWidth - 2, yPos, 112, genreRightColWidth);
         }

         if (artist != null) {
            graphics.drawText(artist.toString(), x + 3, yPos, 112, width - rightColWidth - 3);
         }

         graphics.setFont(font);
         graphics.setGlobalAlpha(globalAlpha);
      }

      Ui.returnTmpDrawTextParam(drawParam);
      Ui.returnTmpTextMetrics(metrics);
      return 0;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      VerbRepository verbRepository = VerbRepository.getVerbRepository(-2843135760572915788L);
      Verb[] fileVerbs = verbRepository.getVerbs(8830038681865959882L);
      int insertIndex = verbs.length;
      Array.resize(verbs, verbs.length + fileVerbs.length);

      for (int i = 0; i < fileVerbs.length; i++) {
         verbs[insertIndex++] = fileVerbs[i];
      }

      return null;
   }

   public final void setAlbum(Album album) {
      this._album = album;
   }

   public final void setGenre(Genre genre) {
      this._genre = genre;
   }

   public final void setLength(long length) {
      this._length = length >= 0 ? length : 0;
   }

   public final void setTrackNumber(int trackNumber) {
      this._trackNumber = trackNumber;
   }

   public final int getTrackNumber() {
      return this._trackNumber;
   }

   public final void setArtist(Artist artist) {
      this._artist = artist;
   }

   public final void setTitle(String title) {
      if (title != null) {
         this._title = title.trim();
         if (this._title.length() == 0) {
            this._title = null;
         }
      }
   }

   public final Artist getArtist() {
      return this._artist;
   }

   public final Album getAlbum() {
      return this._album;
   }

   public final Genre getGenre() {
      return this._genre;
   }

   public final long getLength() {
      return this._length;
   }

   final void generateKeywords() {
      if (this._keywords == null) {
         this._keywords = new String[0];
      }

      if (this._artist != null) {
         Arrays.append(this._keywords, this._artist.getKeywords());
         Arrays.append(this._keywords, this._artist.getPrefixedKeywords());
      }

      if (this._album != null) {
         Arrays.append(this._keywords, this._album.getPrefixedKeywords());
      }

      if (this._genre != null) {
         Arrays.append(this._keywords, this._genre.getKeywords());
         Arrays.append(this._keywords, this._genre.getPrefixedKeywords());
      }

      if (this.getName() != null) {
         String[] titleKeywords = StringUtilities.stringToKeywords(this.getName());
         Arrays.append(this._keywords, titleKeywords);
      }
   }

   @Override
   public final String getLocation() {
      return this._location;
   }

   @Override
   public final void setPreloaded(boolean preloaded) {
      if (this._keywords == null) {
         this._keywords = new String[0];
      }

      if (preloaded) {
         Arrays.add(this._keywords, FilterConstants.PRELOADED_PREFIX);
      } else {
         Arrays.add(this._keywords, FilterConstants.USERLOADED_PREFIX);
      }
   }

   @Override
   public final String getName() {
      if (this._title == null && this._location != null) {
         String title = FileUtilities.getDisplayBaseName(this._location);
         this.setTitle(title);
         return title;
      } else {
         return this._title;
      }
   }

   @Override
   public final int getId() {
      return this._id;
   }

   @Override
   public final String[] getKeywords() {
      return null;
   }

   @Override
   public final String[] getPrefixedKeywords() {
      return null;
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

   private final void setLocation(String location) {
      if (location != null) {
         this._location = location.trim();
      }
   }

   public Track(int id, String location) {
      this.setId(id);
      this.setLocation(location);
   }

   private final void setId(int id) {
      this._id = id;
   }

   @Override
   public final String toString() {
      return this.getName();
   }
}
