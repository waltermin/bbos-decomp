package net.rim.device.apps.internal.explorer.MediaLibrary;

import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;

public class Playlistable implements MediaInfo, PaintProvider, KeyProvider, VerbProvider {
   private MediaInfo _media;
   private int _index;

   @Override
   public int paint(Graphics graphics, int x, int y, int width, int height, Object context) {
      if (this._media instanceof PaintProvider) {
         ((PaintProvider)this._media).paint(graphics, x, y, width, height, context);
      }

      return 0;
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      return !(this._media instanceof VerbProvider) ? null : ((VerbProvider)this._media).getVerbs(context, verbs);
   }

   void setPlaylistIndex(int index) {
      this._index = index;
   }

   public int getPlaylistIndex() {
      return this._index;
   }

   public MediaInfo getMediaInfo() {
      return this._media;
   }

   @Override
   public String getName() {
      return this._media.getName();
   }

   @Override
   public String[] getKeywords() {
      return this._media.getKeywords();
   }

   @Override
   public String[] getPrefixedKeywords() {
      return this._media.getPrefixedKeywords();
   }

   @Override
   public void setPreloaded(boolean preloaded) {
      this._media.setPreloaded(preloaded);
   }

   @Override
   public int getId() {
      return this._media.getId();
   }

   @Override
   public String getLocation() {
      return this._media.getLocation();
   }

   @Override
   public int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return !(this._media instanceof KeyProvider) ? 0 : ((KeyProvider)this._media).getKeys(context, keyArray, index, keyRequested);
   }

   @Override
   public int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      return 0;
   }

   public Playlistable(MediaInfo media, int index) {
      this._media = media;
      this._index = index;
   }
}
