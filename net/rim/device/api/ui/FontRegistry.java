package net.rim.device.api.ui;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;

public final class FontRegistry {
   private byte[][] _fontData = new byte[125][];
   private int _index;
   private String[] _typefaceNameTable = new String[64];
   private int _typefaceCount;
   private Font _defaultFont;
   private Hashtable _table = new Hashtable();
   private boolean isShufle = false;
   private IntHashtable _fontInfo = new IntHashtable();
   private int _handle;
   public static final int NO_FONT_DATA = -1;
   public static final int ALREADY_LOADED = -2;
   public static final int FONTS_ARRAY_FULL = -3;
   public static final int MISSING_TYPEFACE_NAME = -4;
   public static final int ILLEGAL_NUMBER_OF_FILES = -5;
   public static final int FAILED_TO_LOAD_FILE = -6;
   public static String DEFAULT_FAMILY;
   public static final int DEFAULT_SIZE;
   public static final int DEFAULT_STYLE;
   private static final long REGISTRY_NAME = -2659295168329133511L;
   private static final int MAX_FONTS = 125;
   private static final int MAX_TYPEFACE_NAMES = 64;
   private static final int CBTF_FONT_SIGNATURE = 1667396710;
   private static final int SFF4_FONT_SIGNATURE = 1936090676;
   private static final int TTF_FONT_SIGNATURE = 65536;
   private static FontRegistry _registry;

   public static final FontRegistry getInstance() {
      return _registry;
   }

   private final native void submit();

   public static final int loadFont(String font, String location, String typefaceName) {
      return loadFont(font, location, typefaceName, false);
   }

   public static final int loadFont(String font, String location, String typefaceName, boolean isPublic) {
      Resource resourceClass = Resource$Internal.getResourceClass(location);
      if (resourceClass == null) {
         return -1;
      }

      byte[] data = resourceClass.getResource(font);
      int handle = CodeModuleManager.getModuleHandle(location);
      return loadFont(handle, data, typefaceName, isPublic);
   }

   public static final int loadSplitFont(String aFileName, int aFileCount, String aLocation, String aTypefaceName, boolean aIsPublic) {
      return getInstance().loadSplitFontInternal(aFileName, aFileCount, aLocation, aTypefaceName, aIsPublic);
   }

   private final synchronized int loadSplitFontInternal(String aFileName, int aFileCount, String aLocation, String aTypefaceName, boolean aIsPublic) {
      if (aFileCount > 0 && aFileCount <= 999) {
         int start_index = this._index;
         int i = 0;
         if (125 - start_index < aFileCount) {
            start_index = 0;
            boolean found = false;

            while (start_index + aFileCount <= 125) {
               for (i = 0; i < aFileCount; i++) {
                  if (this._fontData[start_index + i] != null) {
                     start_index = start_index + i + 1;
                     break;
                  }
               }

               if (i == aFileCount) {
                  found = true;
                  break;
               }
            }

            if (!found) {
               long LOGWORTHY_REPORT_REQUEST = 2888237357036234703L;
               RIMGlobalMessagePoster.postGlobalEvent(LOGWORTHY_REPORT_REQUEST, 0, 0, aTypefaceName + " exceeds number of allowed fonts in the system.", null);
               return -3;
            }
         }

         Resource resource_class = Resource$Internal.getResourceClass(aLocation);
         if (resource_class == null) {
            return -1;
         }

         int cod_file_handle = CodeModuleManager.getModuleHandle(aLocation);
         byte[] first_block = resource_class.getResource(aFileName + ".001");

         for (int var16 = 0; var16 < 125; var16++) {
            if (probablyIdentical(this._fontData[var16], first_block)) {
               Enumeration e = this._fontInfo.elements();
               IntEnumeration ien = this._fontInfo.keys();
               FontRegistry$FontInfo fi = null;

               while (e.hasMoreElements()) {
                  fi = (FontRegistry$FontInfo)e.nextElement();
                  int fi_index = ien.nextElement();
                  if (fi._index == var16) {
                     if (aTypefaceName.equals(fi._typefaceName)) {
                        this._fontInfo.put(this._handle, fi);
                        return this._handle++;
                     }

                     return -131072 | fi_index;
                  }
               }
            }
         }

         for (int var17 = 0; var17 < aFileCount; var17++) {
            int index = var17 + 1;
            String file_name = aFileName;
            if (index < 10) {
               file_name = file_name + ".00" + index;
            } else if (index < 100) {
               file_name = file_name + ".0" + index;
            } else {
               file_name = file_name + "." + index;
            }

            this._fontData[start_index + var17] = resource_class.getResource(file_name);
            if (this._fontData[start_index + var17] == null) {
               for (int j = 0; j < var17; j++) {
                  this._fontData[start_index + j] = null;
               }

               return -1;
            }
         }

         if (this._index == start_index) {
            this._index += aFileCount;
         }

         int return_value = 0;
         int re_handle = loadSplitFontResource(this._fontData, aTypefaceName, aIsPublic, start_index, aFileCount);
         if (re_handle != 0) {
            this._fontInfo.put(this._handle, new FontRegistry$FontInfo(start_index, aFileCount, aIsPublic, cod_file_handle, re_handle, aTypefaceName));
            return_value = this._handle++;
            if (aIsPublic) {
               int k = 0;
               k = 0;

               while (k < this._typefaceCount && !aTypefaceName.equals(this._typefaceNameTable[k])) {
                  k++;
               }

               if (k == this._typefaceCount) {
                  this._typefaceNameTable[this._typefaceCount++] = aTypefaceName;
               }
            }

            this.reload();
         } else {
            return_value = -6;

            for (int var18 = 0; var18 < aFileCount; var18++) {
               this._fontData[start_index + var18] = null;
            }

            while (this._index > 0 && this._fontData[this._index - 1] == null) {
               this._index--;
            }
         }

         return return_value;
      } else {
         return -5;
      }
   }

   public static final int loadFont(byte[] data, String typefaceName, boolean isPublic) {
      return getInstance().loadFont0(-1, data, typefaceName, isPublic);
   }

   public static final int loadFont(int codFileHandle, byte[] data, String typefaceName, boolean isPublic) {
      return getInstance().loadFont0(codFileHandle, data, typefaceName, isPublic);
   }

   public static final String getTypefaceName(int fontHandle) {
      return getInstance().getTypefaceName0(fontHandle);
   }

   private final String getTypefaceName0(int fontHandle) {
      Object obj;
      if (fontHandle >= 0 && (obj = this._fontInfo.get(fontHandle)) != null) {
         FontRegistry$FontInfo fi = (FontRegistry$FontInfo)obj;
         return fi._typefaceName;
      } else {
         return null;
      }
   }

   private final synchronized int loadFont0(int codFile, byte[] data, String typefaceName, boolean isPublic) {
      int rc = -1;
      if (typefaceName == null || typefaceName.length() == 0) {
         return -4;
      }

      if (data != null && data.length != 0) {
         if (this._index == 125) {
            this.isShufle = true;
         }

         int i = 0;
         boolean emptySpotFound = false;

         while (i < 125 && !probablyIdentical(this._fontData[i], data)) {
            if (this.isShufle && this._fontData[i] == null) {
               this._index = i;
               emptySpotFound = true;
            }

            i++;
         }

         if (i == 125 && this.isShufle && !emptySpotFound) {
            long LOGWORTHY_REPORT_REQUEST = 2888237357036234703L;
            RIMGlobalMessagePoster.postGlobalEvent(LOGWORTHY_REPORT_REQUEST, 0, 0, typefaceName + " exceeds number of allowed fonts in the system.", null);
            return -3;
         }

         if (i < 125) {
            Enumeration e = this._fontInfo.elements();
            IntEnumeration ien = this._fontInfo.keys();
            int fiIndex = 0;
            FontRegistry$FontInfo fi = null;

            while (e.hasMoreElements()) {
               fi = (FontRegistry$FontInfo)e.nextElement();
               fiIndex = ien.nextElement();
               if (fi._index == i) {
                  if (typefaceName.equals(fi._typefaceName)) {
                     this._fontInfo.put(this._handle, fi);
                     rc = this._handle++;
                  } else {
                     rc = -131072 | fiIndex;
                  }
                  break;
               }
            }
         } else {
            this._fontData[this._index] = data;
            int hd;
            if ((hd = loadFontResource(data, typefaceName, isPublic, this._index)) == 0) {
               this._fontData[this._index] = null;
            } else {
               rc = this._handle;
               this._fontInfo.put(this._handle, new FontRegistry$FontInfo(this._index, 1, isPublic, codFile, hd, typefaceName));
               this._index++;
               this._handle++;
               if (isPublic) {
                  boolean loaded = false;

                  for (int k = 0; k < this._typefaceCount; k++) {
                     if (typefaceName.equals(this._typefaceNameTable[k])) {
                        loaded = true;
                        break;
                     }
                  }

                  if (!loaded) {
                     this._typefaceNameTable[this._typefaceCount++] = typefaceName;
                  }
               }
            }

            this.reload();
         }

         return rc;
      } else {
         return rc;
      }
   }

   private static final boolean probablyIdentical(byte[] data1, byte[] data2) {
      if (data1 != null && data2 != null) {
         if (data1.length != data2.length) {
            return false;
         }

         int range = 10;
         if (data1.length < 30) {
            range = 1;
         }

         for (int i = 0; i < range; i++) {
            if (data1[0 + i] != data2[0 + i]) {
               return false;
            }

            if (data1[data1.length / 2 + i] != data2[data1.length / 2 + i]) {
               return false;
            }

            if (data1[data1.length - 1 - i] != data2[data1.length - 1 - i]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private final FontRegistry$FontInfo getFontInfo(String typefaceName, int occurance) {
      int index = 0;
      Enumeration e = this._fontInfo.elements();
      FontRegistry$FontInfo fi = null;

      while (e.hasMoreElements()) {
         fi = (FontRegistry$FontInfo)e.nextElement();
         if (typefaceName.equals(fi._typefaceName)) {
            if (++index == occurance) {
               return fi;
            }
         }
      }

      return null;
   }

   public static final int unloadFont(int aHandle) {
      return aHandle < 0 ? -1 : getInstance().unloadFont0(aHandle);
   }

   public static final int unloadFont(String typefaceName) {
      return typefaceName != null && typefaceName.length() != 0 ? getInstance().unloadFont0(typefaceName) : -1;
   }

   private final int unloadFont0(String typefaceName) {
      int rc = -1;
      synchronized (this.getClass()) {
         IntEnumeration e = this._fontInfo.keys();
         FontRegistry$FontInfo fi = null;

         while (e.hasMoreElements()) {
            int key = e.nextElement();
            fi = (FontRegistry$FontInfo)this._fontInfo.get(key);
            if (typefaceName.equals(fi._typefaceName) && (rc = this.unloadFont0(key)) != 0) {
               break;
            }
         }

         return rc;
      }
   }

   private final int unloadFont0(int aHandle) {
      int rc = -1;
      synchronized (this.getClass()) {
         if (aHandle < 0) {
            return rc;
         } else {
            Object obj;
            if ((obj = this._fontInfo.get(aHandle)) != null) {
               this._fontInfo.remove(aHandle);
               return this.unloadFont0((FontRegistry$FontInfo)obj);
            } else {
               return rc;
            }
         }
      }
   }

   private final int unloadFont0(FontRegistry$FontInfo fi) {
      int rc = -1;
      Enumeration e = this._fontInfo.elements();
      int i = 0;
      FontRegistry$FontInfo tfi = null;

      while (e.hasMoreElements()) {
         tfi = (FontRegistry$FontInfo)e.nextElement();
         if (fi._renderingEngineHandle == tfi._renderingEngineHandle) {
            i++;
         }
      }

      if (i > 0) {
         return 0;
      }

      rc = unloadFontResource(fi._renderingEngineHandle);
      if (rc != 0) {
         return rc;
      }

      for (int j = 0; j < fi._count; j++) {
         this._fontData[fi._index + j] = null;
      }

      if (fi._codFileHandle != -1) {
         CodeModuleManager.deleteModuleEx(fi._codFileHandle, false);
      }

      this.reload();
      return rc;
   }

   public final synchronized int getTypefaceType(String aTypeface) {
      if (aTypeface != null && aTypeface.length() != 0) {
         FontRegistry$FontInfo fi = this.getFontInfo(aTypeface, 1);
         if (fi == null) {
            return FontFamily.UNKNOWN_FONT;
         }

         byte[] data = this._fontData[fi._index];
         if (data.length < 4) {
            return FontFamily.UNKNOWN_FONT;
         }

         int type = data[0] << 24 | data[1] << 16 | data[2] << 8 | data[3];
         switch (type) {
            case 65536:
               return FontFamily.SCALABLE_FONT;
            case 1667396710:
               return FontFamily.MONO_BITMAP_FONT;
            case 1936090676:
               return FontFamily.SCALABLE_FONT;
            default:
               return FontFamily.UNKNOWN_FONT;
         }
      } else {
         return FontFamily.UNKNOWN_FONT;
      }
   }

   public final synchronized int[] getHeightsForTypeface(String aTypeface) {
      int[] rc = null;
      if (aTypeface != null && aTypeface.length() != 0) {
         int id = -1;
         int size = 0;

         FontRegistry$FontInfo fi;
         for (int index = 1; (fi = this.getFontInfo(aTypeface, index)) != null; index++) {
            if (size < this._fontData[fi._index].length) {
               id = fi._index;
               size = this._fontData[fi._index].length;
            }
         }

         if (id < 0) {
            return rc;
         }

         byte[] data = this._fontData[id];

         try {
            DataInputStream stream = new DataInputStream(new ByteArrayInputStream(data));
            if (stream.readInt() != 1667396710) {
               return rc;
            }

            stream.skip(20);
            int tableID = stream.readUnsignedShort();
            if (tableID != 2) {
               System.err.println("tableID != 2");
            }

            int tableOffset = stream.readInt();
            stream.reset();
            stream.skip(tableOffset);
            int skiping = stream.readUnsignedShort();
            int sizes = stream.readUnsignedByte();
            rc = new int[sizes];

            for (int j = 0; j < sizes; j++) {
               rc[j] = stream.readUnsignedByte();
               stream.skip(6);
               rc[j] += stream.readUnsignedByte();
               stream.skip(skiping - 8);
            }

            stream.close();
            return rc;
         } catch (Exception e) {
            return null;
         }
      } else {
         return rc;
      }
   }

   public static final FontFamily get(String name) {
      Object result = null;
      FontRegistry registry = getInstance();
      synchronized (registry) {
         result = registry._table.get(name);
         if (result == null) {
            result = new FontFamily(name);
            registry._table.put(name, result);
         }
      }

      return (FontFamily)result;
   }

   public static final String getTypefaceNameByIndex(int id) {
      return getInstance()._typefaceNameTable[id];
   }

   public final int getTypefaceNameIndex(String typeface) {
      for (int i = 0; i < this._typefaceCount; i++) {
         if (this._typefaceNameTable[i].equals(typeface)) {
            return i;
         }
      }

      this._typefaceNameTable[this._typefaceCount++] = typeface;
      return this._typefaceCount - 1;
   }

   static final Font _getDefaultFont() {
      FontRegistry registry = getInstance();
      if (_registry._defaultFont == null) {
         _registry._defaultFont = FontPersist.getDefaultFont();
         if (_registry._defaultFont == null) {
            return null;
         }
      }

      return _registry._defaultFont;
   }

   public static final Font getDefaultFont() {
      synchronized (getInstance()) {
         if (_registry._defaultFont == null) {
            _registry._defaultFont = FontPersist.getDefaultFont();
            if (_registry._defaultFont == null) {
               _registry._defaultFont = get(DEFAULT_FAMILY).getFont(DEFAULT_STYLE, DEFAULT_SIZE, 3);
            }
         }
      }

      return _registry._defaultFont;
   }

   public static final boolean isDefaultFontSet() {
      return FontPersist.getDefaultFont() != null;
   }

   public static final int getDefaultHeight(int units) {
      int dsize = DEFAULT_SIZE;
      if (getDefaultFont() != null) {
         dsize = getDefaultFont().getHeight();
      }

      return Ui.convertSize(dsize, 0, units);
   }

   public static final String[] getFontFamilies() {
      FontRegistry registry = getInstance();
      synchronized (registry) {
         Hashtable t = new Hashtable();
         Enumeration en = registry._fontInfo.elements();

         while (en.hasMoreElements()) {
            FontRegistry$FontInfo fi = (FontRegistry$FontInfo)en.nextElement();
            if (fi._isPublic) {
               t.put(fi._typefaceName, fi._typefaceName);
            }
         }

         String[] rc = new String[t.size()];
         Enumeration e = t.keys();
         int j = 0;

         while (e.hasMoreElements()) {
            rc[j++] = (String)e.nextElement();
         }

         Arrays.sort(rc, new FontRegistry$1());
         return rc;
      }
   }

   public static final void setDefaultFont(Font defaultFont) {
      synchronized (getInstance()) {
         if (_registry._defaultFont == defaultFont) {
            return;
         }

         _registry._defaultFont = defaultFont;
         FontPersist.setDefaultFont(defaultFont);
      }

      ThemeManager.onSystemFontChangeInternal();
      RIMGlobalMessagePoster.postGlobalEvent(-4394903006263251010L);
   }

   public static final void setDefaultFont(String family, int style, int height, int units) {
      synchronized (getInstance()) {
         if (family != null && family.length() != 0 && height > 0) {
            getInstance();
            FontFamily fontf = get(family);
            Font font = fontf.getFont(style, height, units);
            setDefaultFont(font);
         }
      }
   }

   private final synchronized void reload() {
      Enumeration e = this._table.elements();

      while (e.hasMoreElements()) {
         ((FontFamily)e.nextElement()).reload();
      }
   }

   private static final native int loadFontResource(byte[] var0, String var1, boolean var2, int var3);

   private static final native int loadSplitFontResource(byte[][] var0, String var1, boolean var2, int var3, int var4);

   private static final native int unloadFontResource(int var0);

   private static final native void setPreferredFallback(String var0, int var1);

   static {
      if (Graphics.isColor()) {
         DEFAULT_FAMILY = "BBMillbank";
         DEFAULT_SIZE = 10;
         DEFAULT_STYLE = 0;
      } else {
         DEFAULT_FAMILY = "System";
         DEFAULT_SIZE = 10;
         DEFAULT_STYLE = 0;
      }

      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _registry = (FontRegistry)ar.getOrWaitFor(-2659295168329133511L);
      if (_registry == null) {
         _registry = new FontRegistry();
         _registry.submit();
         ar.put(-2659295168329133511L, _registry);
         loadFont("System.cbtf", "net_rim_font_system", "System", true);
         loadFont("RIM_latin_millbank.sff4", "net_rim_font_european_sff", "BBMillbank", true);
         setPreferredFallback(DEFAULT_FAMILY, 1);
      }
   }
}
