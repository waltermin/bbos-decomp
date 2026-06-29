package net.rim.device.apps.internal.explorer.MediaLibrary;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.i18n.DateFormat;
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

public final class VoiceNote implements MediaInfo, PaintProvider, KeyProvider, VerbProvider {
   int _id;
   String _name;
   String _location;
   String[] _keywords;
   long _length;
   long _modifiedTime = -1;

   @Override
   public final int paint(Graphics graphics, int x, int y, int width, int height, Object context) {
      TextMetrics metrics = Ui.getTmpTextMetrics();
      DrawTextParam drawParam = Ui.getTmpDrawTextParam();
      int endPos = -1;
      String displayName = this.toString();
      String lengthString = "0:00";
      if (this.toString() != null) {
         displayName = this.toString();
         endPos = displayName.length();
      }

      if (this._length >= 0) {
         int dur = (int)(this._length / 1000000);
         lengthString = ((StringBuffer)(new Object()))
            .append(Integer.toString(dur / 60))
            .append(':')
            .append(NumberUtilities.toString(dur % 60, 10, 2))
            .toString();
      }

      drawParam.iTruncateWithEllipsis = 2;
      int rightColWidth = graphics.getFont().getAdvance("44:44");
      drawParam.iMaxAdvance = rightColWidth;
      drawParam.iAlignment = 53;
      graphics.drawText(lengthString, 0, lengthString.length(), width - rightColWidth - 2, y, drawParam, metrics);
      drawParam.iMaxAdvance = width - rightColWidth - 5;
      drawParam.iAlignment = 54;
      graphics.drawText(displayName, 0, endPos, 2, y, drawParam, metrics);
      Font font = graphics.getFont();
      int fontHeightInPt = font.getHeight(2);
      Font newFont = font.derive(0, fontHeightInPt - 1, 2);
      if (height >= font.getHeight() + newFont.getHeight()) {
         graphics.setFont(newFont);
         int globalAlpha = graphics.getGlobalAlpha();
         graphics.setGlobalAlpha(210);
         int yPos = y + font.getHeight();
         int timeRightColWidth = 0;
         String date = "****-*-**";
         String time = "*:**";
         if (this._modifiedTime >= 0) {
            date = DateFormat.getInstance(48).formatLocal(this._modifiedTime);
            time = DateFormat.getInstance(7).formatLocal(this._modifiedTime);
         }

         timeRightColWidth = newFont.getAdvance(time);
         rightColWidth = Math.max(timeRightColWidth + 2, rightColWidth);
         graphics.drawText(date, x + 3, yPos, 112, width - rightColWidth - 3);
         graphics.drawText(time, width - timeRightColWidth - 2, yPos, 112, timeRightColWidth);
         graphics.setFont(font);
         graphics.setGlobalAlpha(globalAlpha);
      }

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

   public final void setName(String name) {
      if (name != null) {
         this._name = name.trim();
         this._keywords = StringUtilities.stringToKeywords(this._name);
      }
   }

   public final void setLength(long length) {
      this._length = length;
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

   @Override
   public final String toString() {
      return this.getName();
   }

   private final void updateLastModifiedTime() {
      try {
         FileConnection file = (FileConnection)Connector.open(this.getLocation());
         this._modifiedTime = file.lastModified();
      } finally {
         return;
      }
   }

   public VoiceNote(int id, String pathURL) {
      this.setId(id);
      this.setLocation(pathURL);
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

      this.updateLastModifiedTime();
   }
}
