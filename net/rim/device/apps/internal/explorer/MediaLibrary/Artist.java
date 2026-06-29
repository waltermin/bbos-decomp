package net.rim.device.apps.internal.explorer.MediaLibrary;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.MediaLibrary.util.MLUtilities;

public final class Artist implements MediaInfo, PaintProvider, KeyProvider, VerbProvider {
   int _id;
   String _name;
   String[] _keywords;
   String[] _prefixedKeywords;
   int _trackCount = 0;

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
      if (name == null) {
         ResourceBundle rb = ResourceBundle.getBundle(349501092522026426L, "net.rim.device.apps.internal.resource.Explorer");
         this._keywords = StringUtilities.stringToKeywords(rb.getString(148));
         this._prefixedKeywords = new Object[1];
         this._prefixedKeywords[0] = FilterConstants.generateKey('ￊ', FilterConstants.UNKNOWN_ID);
      } else {
         this._name = name.trim();
         this._keywords = StringUtilities.stringToKeywords(this._name);
         this._prefixedKeywords = new Object[1];
         String hash = FilterConstants.generateKey('ￊ', Math.abs(this._name.toLowerCase().hashCode()));
         this._prefixedKeywords[0] = hash;
      }
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

   @Override
   public final String getLocation() {
      return null;
   }

   @Override
   public final int getId() {
      return this._id;
   }

   @Override
   public final String getName() {
      return this._name;
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
   public final String toString() {
      String name = this.getName();
      if (name == null) {
         ResourceBundle rb = ResourceBundle.getBundle(349501092522026426L, "net.rim.device.apps.internal.resource.Explorer");
         return rb.getString(148);
      } else {
         return name;
      }
   }

   public Artist(int id) {
      this.setId(id);
   }

   private final void setId(int id) {
      this._id = id;
   }
}
