package net.rim.device.apps.internal.explorer.MediaLibrary;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.util.ImageConverter;
import net.rim.device.apps.internal.explorer.MediaLibrary.util.MLUtilities;

public final class Album implements MediaInfo, PaintProvider, KeyProvider, VerbProvider {
   int _id;
   String _name;
   String[] _keywords;
   String[] _prefixedKeywords;
   int _trackCount = 0;
   EncodedImage _albumArt;

   @Override
   public final int paint(Graphics graphics, int x, int y, int width, int height, Object context) {
      Font font = graphics.getFont();
      int fontHeightInPt = font.getHeight(2);
      Font newFont = font.derive(0, fontHeightInPt - 1, 2);
      boolean twoLine = height >= font.getHeight() + newFont.getHeight();
      int alpha = graphics.getGlobalAlpha();
      if (twoLine) {
         EncodedImage image = this._albumArt;
         if (this._albumArt != null) {
            image = ImageConverter.scaleImage(this._albumArt, height, height, height, height);
         }

         if (image == null) {
            graphics.setGlobalAlpha(50);
            graphics.fillRect(x + 2, y + 2, height - 4, height - 4);
            graphics.setGlobalAlpha(alpha);
            graphics.drawRect(x + 2, y + 2, height - 4, height - 4);
            x += height;
         } else {
            int yLoc = y + (height - image.getScaledHeight() >> 1);
            graphics.drawImage(x, yLoc, height, height, image, 0, 0, 0);
            x += image.getScaledHeight();
         }

         x += 4;
      }

      int yOffset = height - font.getHeight() >> 1;
      graphics.drawText(this.toString(), x, y + yOffset, 64, width - x - 2);
      return 0;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      return null;
   }

   public final EncodedImage getArt() {
      return this._albumArt;
   }

   public final void addArtist(Artist artist) {
      if (artist != null) {
         String[] prefixedWords = artist.getPrefixedKeywords();
         if (!Arrays.contains(this._keywords, prefixedWords[0])) {
            Arrays.append(this._keywords, prefixedWords);
            MediaLibrary.getInstance().getAlbumCollection().fireElementUpdated(this, this);
         }
      }
   }

   public final void setArt(EncodedImage art) {
      this._albumArt = art;
   }

   public final void removeArtist(Artist artist) {
      String[] prefixedWords = artist.getPrefixedKeywords();

      for (int i = prefixedWords.length - 1; i >= 0; i--) {
         Arrays.remove(this._keywords, prefixedWords[i]);
      }

      MediaLibrary.getInstance().getAlbumCollection().fireElementUpdated(this, this);
   }

   public final void addGenre(Genre genre) {
      if (genre != null) {
         String[] prefixedWords = genre.getPrefixedKeywords();
         if (!Arrays.contains(this._keywords, prefixedWords[0])) {
            Arrays.append(this._keywords, prefixedWords);
            MediaLibrary.getInstance().getAlbumCollection().fireElementUpdated(this, this);
         }
      }
   }

   public final void removeGenre(Genre genre) {
      String[] prefixedWords = genre.getPrefixedKeywords();

      for (int i = prefixedWords.length - 1; i >= 0; i--) {
         Arrays.remove(this._keywords, prefixedWords[i]);
      }

      MediaLibrary.getInstance().getAlbumCollection().fireElementUpdated(this, this);
   }

   public final void addTrack(Track track) {
      this._trackCount++;
   }

   public final void removeTrack(Track track) {
      this._trackCount--;
      if (this._trackCount < 0) {
         this._trackCount = 0;
      }
   }

   public final int getTrackCount() {
      return this._trackCount;
   }

   public final void setName(String name) {
      if (name == null) {
         ResourceBundle rb = ResourceBundle.getBundle(349501092522026426L, "net.rim.device.apps.internal.resource.Explorer");
         this._keywords = StringUtilities.stringToKeywords(rb.getString(148));
         this._prefixedKeywords = new String[1];
         this._prefixedKeywords[0] = FilterConstants.generateKey('ￋ', FilterConstants.UNKNOWN_ID);
      } else {
         this._name = name.trim();
         this._keywords = StringUtilities.stringToKeywords(this._name);
         this._prefixedKeywords = new String[1];
         String hash = FilterConstants.generateKey('ￋ', Math.abs(this._name.toLowerCase().hashCode()));
         this._prefixedKeywords[0] = hash;
      }
   }

   @Override
   public final int getId() {
      return this._id;
   }

   @Override
   public final String[] getKeywords() {
      if (this._name == null) {
         ResourceBundle rb = ResourceBundle.getBundle(349501092522026426L, "net.rim.device.apps.internal.resource.Explorer");
         return StringUtilities.stringToKeywords(rb.getString(148));
      } else {
         return StringUtilities.stringToKeywords(this._name);
      }
   }

   @Override
   public final String[] getPrefixedKeywords() {
      return this._prefixedKeywords;
   }

   @Override
   public final void setPreloaded(boolean preloaded) {
   }

   @Override
   public final String getLocation() {
      return null;
   }

   @Override
   public final String getName() {
      return this._name;
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

   private final void setId(int id) {
      this._id = id;
   }

   public Album(int id) {
      this.setId(id);
   }

   @Override
   public final String toString() {
      String name = this.getName();
      if (name == null) {
         ResourceBundle rb = ResourceBundle.getBundle(349501092522026426L, "net.rim.device.apps.internal.resource.Explorer");
         return rb.getString(148);
      } else {
         return name;
      }
   }
}
