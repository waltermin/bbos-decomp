package net.rim.device.apps.internal.explorer.MediaLibrary;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.MediaLibrary.util.MLUtilities;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.vm.Array;

public final class Video implements MediaInfo, PaintProvider, KeyProvider, VerbProvider {
   int _id;
   String _name;
   String _location;
   String[] _keywords;
   long _bookmark;

   @Override
   public final int paint(Graphics graphics, int x, int y, int width, int height, Object context) {
      int xPadding = 2;
      if (context instanceof ContextInfo && (((ContextInfo)context).getType() & 32) != 0) {
         xPadding = 5;
      }

      graphics.drawText(this.toString(), xPadding, y, 64, width);
      Font font = graphics.getFont();
      int fontHeightInPt = font.getHeight(2);
      Font newFont = font.derive(0, fontHeightInPt - 1, 2);
      if (height >= font.getHeight() + newFont.getHeight()) {
         graphics.setFont(newFont);
         int globalAlpha = graphics.getGlobalAlpha();
         graphics.setGlobalAlpha(204);
         int yPos = y + font.getHeight();
         ResourceBundle rb = ResourceBundle.getBundle(349501092522026426L, "net.rim.device.apps.internal.resource.Explorer");
         graphics.drawText(rb.getString(166), x + 3, yPos, 112, width - 3);
         graphics.setFont(font);
         graphics.setGlobalAlpha(globalAlpha);
      }

      return 0;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      VerbRepository verbRepository = VerbRepository.getVerbRepository(-2843135760572915788L);
      Verb[] fileVerbs = verbRepository.getVerbs(-7287235942111338224L);
      int insertIndex = verbs.length;
      Array.resize(verbs, verbs.length + fileVerbs.length);

      for (int i = 0; i < fileVerbs.length; i++) {
         verbs[insertIndex++] = fileVerbs[i];
      }

      return null;
   }

   public final void setName(String name) {
      if (name != null) {
         this._name = name.trim();
         this._keywords = StringUtilities.stringToKeywords(this._name);
      }
   }

   public final long getBookmark() {
      return this._bookmark;
   }

   public final void setBookmark(long position) {
      this._bookmark = position;
   }

   @Override
   public final String getLocation() {
      return this._location;
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
      return null;
   }

   @Override
   public final String[] getPrefixedKeywords() {
      return null;
   }

   @Override
   public final void setPreloaded(boolean preloaded) {
      if (this._keywords == null) {
         this._keywords = new Object[0];
      }

      if (preloaded) {
         Arrays.add(this._keywords, FilterConstants.PRELOADED_PREFIX);
      } else {
         Arrays.add(this._keywords, FilterConstants.USERLOADED_PREFIX);
      }
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

   public Video(int id, String location) {
      this.setId(id);
      this.setLocation(location);
   }

   private final void setId(int id) {
      this._id = id;
   }

   private final void setLocation(String location) {
      this._location = location;
      if (this._name == null && location != null) {
         String name = FileUtilities.getDisplayBaseName(location);
         this.setName(name);
      }
   }

   @Override
   public final String toString() {
      return this.getName();
   }
}
