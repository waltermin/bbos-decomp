package net.rim.device.apps.internal.browser.img;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.ui.ZoomBitmapField;

final class ImageRenderer$MyZoomBitmapField extends ZoomBitmapField {
   public ImageRenderer$MyZoomBitmapField(long style) {
      super(style);
   }

   @Override
   protected final void paintImage(Graphics param1, int param2, int param3, int param4, int param5, EncodedImage param6, int param7, int param8, int param9) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: aload 1
      // 02: iload 2
      // 03: iload 3
      // 04: iload 4
      // 06: iload 5
      // 08: aload 6
      // 0a: iload 7
      // 0c: iload 8
      // 0e: iload 9
      // 10: invokespecial net/rim/device/apps/api/ui/ZoomBitmapField.paintImage (Lnet/rim/device/api/ui/Graphics;IIIILnet/rim/device/api/system/EncodedImage;III)V
      // 13: return
      // 14: astore 10
      // 16: aload 1
      // 17: bipush 0
      // 18: bipush 0
      // 19: aload 0
      // 1a: invokevirtual net/rim/device/api/ui/Field.getContentWidth ()I
      // 1d: aload 0
      // 1e: invokevirtual net/rim/device/api/ui/Field.getContentHeight ()I
      // 21: getstatic net/rim/device/apps/internal/browser/ui/BrowserBitmapField._brokenImage Lnet/rim/device/api/system/Bitmap;
      // 24: bipush 0
      // 25: bipush 0
      // 26: invokevirtual net/rim/device/api/ui/Graphics.drawBitmap (IIIILnet/rim/device/api/system/Bitmap;II)V
      // 29: return
      // 2a: astore 10
      // 2c: aload 1
      // 2d: bipush 0
      // 2e: bipush 0
      // 2f: aload 0
      // 30: invokevirtual net/rim/device/api/ui/Field.getContentWidth ()I
      // 33: aload 0
      // 34: invokevirtual net/rim/device/api/ui/Field.getContentHeight ()I
      // 37: getstatic net/rim/device/apps/internal/browser/ui/BrowserBitmapField._brokenImage Lnet/rim/device/api/system/Bitmap;
      // 3a: bipush 0
      // 3b: bipush 0
      // 3c: invokevirtual net/rim/device/api/ui/Graphics.drawBitmap (IIIILnet/rim/device/api/system/Bitmap;II)V
      // 3f: return
      // try (0 -> 11): 12 null
      // try (0 -> 11): 25 null
   }
}
