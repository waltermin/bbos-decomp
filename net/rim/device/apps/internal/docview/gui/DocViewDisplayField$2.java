package net.rim.device.apps.internal.docview.gui;

class DocViewDisplayField$2 implements Runnable {
   private final DocViewDisplayField this$0;

   DocViewDisplayField$2(DocViewDisplayField _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayField$2.this$0 Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayField;
      // 04: invokevirtual net/rim/device/api/ui/Field.getScreen ()Lnet/rim/device/api/ui/Screen;
      // 07: checkcast java/lang/Object
      // 0a: invokevirtual net/rim/device/apps/api/utility/framework/ModelScreen.leaveScreen ()V
      // 0d: goto 15
      // 10: astore 1
      // 11: goto 15
      // 14: astore 1
      // 15: aload 0
      // 16: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayField$2.this$0 Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayField;
      // 19: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayField._application Lnet/rim/device/api/ui/UiApplication;
      // 1c: bipush 0
      // 1d: invokevirtual net/rim/device/api/ui/UiApplication.suspendPainting (Z)V
      // 20: aload 0
      // 21: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayField$2.this$0 Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayField;
      // 24: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayField._application Lnet/rim/device/api/ui/UiApplication;
      // 27: invokevirtual net/rim/device/api/ui/UiApplication.doPainting ()V
      // 2a: return
      // try (0 -> 5): 6 null
      // try (0 -> 5): 8 null
   }
}
