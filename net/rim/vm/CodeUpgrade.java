package net.rim.vm;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;

public class CodeUpgrade {
   private static final long MESSAGE_GUID = -4292848197358216020L;

   private static IntHashtable getMessages() {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      synchronized (appRegistry) {
         IntHashtable ht = (IntHashtable)appRegistry.get(-4292848197358216020L);
         if (ht == null) {
            ht = new IntHashtable();
            appRegistry.put(-4292848197358216020L, ht);
         }

         return ht;
      }
   }

   public static void setMessage(int id, byte[] msg) {
      getMessages().put(id, msg);
   }

   private static native void setMessage0(int var0, byte[] var1);

   public static void addPatch(long guid, String name, byte[] patch, int length) {
      FlashOutputStream newCode = new FlashOutputStream(guid, true);
      newCode.write(name.getBytes());
      newCode.write(0);
      newCode.write(length >>> 24 & 0xFF);
      newCode.write(length >>> 16 & 0xFF);
      newCode.write(length >>> 8 & 0xFF);
      newCode.write(length >>> 0 & 0xFF);
      newCode.write(patch, 0, length);
      newCode.close();
   }

   public static void start(boolean undo) {
      IntHashtable ht = getMessages();
      IntEnumeration ie = ht.keys();

      while (ie.hasMoreElements()) {
         int id = ie.nextElement();
         setMessage0(id, (byte[])ht.get(id));
      }

      start0(undo);
   }

   private static native void start0(boolean var0);

   public static boolean validatePatches() {
      Memory.maximizeContiguousRAM();
      return validateAllPatches();
   }

   static native boolean validateAllPatches();

   public static int getFlashRequired() {
      Memory.maximizeContiguousRAM();
      return getFlashRequired0();
   }

   static native int getFlashRequired0();

   public static native void eraseAll();

   public static native boolean isQuickFix();

   public static native boolean getModuleHash(int var0, byte[] var1);

   public static native String[] getPatchNames();

   public static native String[] getPatchNames(long var0);

   public static long getOSGUID(String str) {
      int len = str.length();
      byte[] name = new byte[len + 1];

      for (int i = len - 1; i >= 0; i--) {
         name[i] = (byte)str.charAt(i);
      }

      name[len] = 0;
      return getOSGUID(name);
   }

   private static native long getOSGUID(byte[] var0);

   public static native long getCodFileGUID();

   public static native long getNewPatchListGUID();

   public static native long getOldDeviceStateGUID();

   public static native String[] getOSSectionNames();

   public static native byte[] getOSSectionHash(String var0);

   private static void putBE(byte[] b, int at, int i) {
      b[at] = (byte)(i >> 8);
      b[at + 1] = (byte)i;
   }

   public static void setOTABitmap(int id, String text) {
      int displayWidth = Display.getWidth();
      int displayHeight = Display.getHeight() / 3;
      Font font = Font.getDefault();
      int textWidth = font.measureText(text, 0, text.length(), null, null);
      int textHeight = font.getHeight();
      int width;
      int height;
      Bitmap bitmap;
      if (textWidth <= displayWidth && textHeight <= displayHeight) {
         width = textWidth;
         height = textHeight;
         bitmap = new Bitmap(Bitmap.DEFAULT_TYPE, width, height);
         Graphics graphics = new Graphics(bitmap);
         graphics.drawText(text, 0, 0);
      } else {
         width = displayWidth;
         height = displayHeight;
         CodeUpgrade$MyTextRect tr = new CodeUpgrade$MyTextRect(new CodeUpgrade$DummyField(), text, 4);
         tr.layout(width, height);
         bitmap = new Bitmap(Bitmap.DEFAULT_TYPE, width, height);
         Graphics graphics = new Graphics(bitmap);
         tr.draw(graphics);
      }

      byte[] bits = new byte[]{1, 2, 4, 8, 16, 32, 64, -128};
      int[] argb = new int[width * height];
      bitmap.getARGB(argb, 0, width, 0, 0, width, height);
      int byteHigh = (height + 7) / 8;
      byte[] raw = new byte[width * byteHigh + 8];
      raw[0] = -1;
      raw[1] = -1;
      putBE(raw, 2, 1);
      putBE(raw, 4, width);
      putBE(raw, 6, height);

      for (int col = 0; col < width; col++) {
         for (int row = 0; row < height; row++) {
            if ((argb[row * width + col] & 16777215) == 0) {
               byte bit = bits[row & 7];
               int rowX = row / 8;
               raw[col * byteHigh + rowX + 8] = (byte)(raw[col * byteHigh + rowX + 8] | bit);
            }
         }
      }

      setMessage(id, raw);
   }
}
