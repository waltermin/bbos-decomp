package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ActiveRichTextField$RegionQueue;

final class DocViewTextDisplayField$TextControlInfo$TextScreenField extends DocViewRichTextField {
   private final Runnable _scan;
   private final DocViewTextDisplayField$TextControlInfo this$1;

   @Override
   protected final void customExecuteBgScan() {
      try {
         this.this$1.this$0._application.invokeLater(this._scan);
      } finally {
         return;
      }
   }

   DocViewTextDisplayField$TextControlInfo$TextScreenField(DocViewTextDisplayField$TextControlInfo _1) {
      super(0);
      this.this$1 = _1;
      this._scan = new DocViewTextDisplayField$TextControlInfo$TextScreenField$1(this);
   }

   @Override
   protected final void onFocus(int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo$TextScreenField.this$1 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo;
      // 04: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo.doProcessLinks ()V
      // 07: goto 0b
      // 0a: astore 2
      // 0b: aload 0
      // 0c: iload 1
      // 0d: invokespecial net/rim/device/api/ui/component/TextField.onFocus (I)V
      // 10: return
      // 11: astore 2
      // 12: return
      // 13: astore 2
      // 14: return
      // try (0 -> 3): 4 null
      // try (5 -> 8): 9 null
      // try (5 -> 8): 11 null
   }

   final void doInvalidate() {
      this.invalidate();
   }

   final int getControlFontsSize() {
      if (this.getTextLength() > 0) {
         Font[] fonts = this.getFonts();
         if (fonts != null) {
            return fonts.length;
         }
      }

      return 0;
   }

   private final void super_setText(String text, ActiveRichTextField$RegionQueue rq) {
      boolean optimize = false;
      if (!this.this$1.this$0._duringLayoutActivity) {
         this.this$1.this$0._duringLayoutActivity = true;
         optimize = true;
      }

      super.setText(text, rq);
      if (optimize) {
         this.this$1.this$0._duringLayoutActivity = false;
         if (this.this$1.this$0._incLayoutRequested) {
            this.updateLayout();
            this.this$1.this$0._incLayoutRequested = false;
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final void setText(String text, ActiveRichTextField$RegionQueue rq) {
      if (this.getTextLength() > 0) {
         int[] backupOffsets = this.getOffsets();
         byte[] backupAttributes = this.getAttributes();
         if (backupOffsets != null && backupAttributes != null) {
            if (rq == null || rq.offsets == null || rq.attributes == null) {
               return;
            }

            if (rq.offsets.length == backupOffsets.length && rq.attributes.length == backupAttributes.length) {
               super._cookieIDs = rq.cookieID;
               return;
            }
         }
      }

      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         if (this.this$1.this$0._application == Application.getApplication()) {
            this.super_setText(text, rq);
            return;
         }

         this.this$1.this$0._application.invokeLater(new DocViewTextDisplayField$TextControlInfo$TextScreenField$2(this, text, rq));
         var6 = false;
      } finally {
         if (var6) {
            super.setText(text, rq);
            return;
         }
      }
   }

   @Override
   public final void setCursorPosition(int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 0: aload 0
      // 1: iload 1
      // 2: invokespecial net/rim/device/api/ui/component/RichTextField.setCursorPosition (I)V
      // 5: return
      // 6: astore 2
      // 7: return
      // 8: astore 2
      // 9: return
      // a: astore 2
      // b: return
      // try (0 -> 3): 4 null
      // try (0 -> 3): 6 null
      // try (0 -> 3): 8 null
   }

   @Override
   public final void getFocusRect(XYRect param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 0: aload 0
      // 1: aload 1
      // 2: invokespecial net/rim/device/api/ui/component/ActiveRichTextField.getFocusRect (Lnet/rim/device/api/ui/XYRect;)V
      // 5: return
      // 6: astore 2
      // 7: return
      // 8: astore 2
      // 9: return
      // a: astore 2
      // b: return
      // try (0 -> 3): 4 null
      // try (0 -> 3): 6 null
      // try (0 -> 3): 8 null
   }

   final int[] getControlOffsets() {
      return this.getOffsets();
   }

   final int[] getControlForegroundColors() {
      return this.getForegroundColors();
   }

   final int[] getControlBackgroundColors() {
      return this.getBackgroundColors();
   }

   final void notifyChangeFonts() {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   @Override
   public final int moveFocus(int param1, int param2, int param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: iload 1
      // 001: ifne 007
      // 004: goto 16f
      // 007: iload 2
      // 008: bipush 1
      // 009: iand
      // 00a: ifeq 038
      // 00d: iload 2
      // 00e: ldc_w 131072
      // 011: iand
      // 012: ifeq 038
      // 015: invokestatic net/rim/device/api/ui/Ui.getUiEngine ()Lnet/rim/device/api/ui/UiEngine;
      // 018: invokeinterface net/rim/device/api/ui/UiEngine.getActiveScreen ()Lnet/rim/device/api/ui/Screen; 1
      // 01d: ldc_w 32768
      // 020: bipush 32
      // 022: bipush 32
      // 024: iload 1
      // 025: ifle 02c
      // 028: bipush 0
      // 029: goto 02e
      // 02c: bipush 2
      // 02e: invokestatic net/rim/device/api/ui/Keypad.keycode (CI)I
      // 031: iload 3
      // 032: invokevirtual net/rim/device/api/ui/Screen.dispatchKeyEvent (ICII)Z
      // 035: pop
      // 036: iload 1
      // 037: ireturn
      // 038: aload 0
      // 039: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo$TextScreenField.this$1 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo;
      // 03c: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo._ctrlFullDocumentState Z
      // 03f: ifne 045
      // 042: goto 146
      // 045: iload 1
      // 046: ifgt 04c
      // 049: goto 146
      // 04c: aload 0
      // 04d: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo$TextScreenField.this$1 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo;
      // 050: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo.this$0 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField;
      // 053: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewDisplayField.isMoreAvailable ()Z
      // 056: ifne 05c
      // 059: goto 146
      // 05c: invokestatic net/rim/device/apps/internal/docview/gui/AttachmentViewerFactory.isAutoMoreEnabled ()Z
      // 05f: ifne 065
      // 062: goto 146
      // 065: aload 0
      // 066: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo$TextScreenField.this$1 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo;
      // 069: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo.this$0 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField;
      // 06c: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewDisplayField.isMoreRequestSent ()Z
      // 06f: ifeq 075
      // 072: goto 146
      // 075: aload 0
      // 076: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo$TextScreenField.this$1 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo;
      // 079: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo.this$0 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField;
      // 07c: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField._skippedDataFields Lnet/rim/device/api/util/SimpleSortingVector;
      // 07f: ifnull 0d7
      // 082: aload 0
      // 083: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo$TextScreenField.this$1 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo;
      // 086: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo.this$0 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField;
      // 089: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField._skippedDataFields Lnet/rim/device/api/util/SimpleSortingVector;
      // 08c: invokevirtual java/util/Vector.size ()I
      // 08f: ifle 0d7
      // 092: aload 0
      // 093: invokevirtual net/rim/device/api/ui/Field.getIndex ()I
      // 096: aload 0
      // 097: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo$TextScreenField.this$1 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo;
      // 09a: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo.this$0 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField;
      // 09d: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField._skippedDataFields Lnet/rim/device/api/util/SimpleSortingVector;
      // 0a0: aload 0
      // 0a1: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo$TextScreenField.this$1 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo;
      // 0a4: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo.this$0 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField;
      // 0a7: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField._skippedDataFields Lnet/rim/device/api/util/SimpleSortingVector;
      // 0aa: invokevirtual java/util/Vector.size ()I
      // 0ad: bipush 1
      // 0ae: isub
      // 0af: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 0b2: checkcast net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$SkippedStatusField
      // 0b5: invokevirtual net/rim/device/api/ui/Field.getIndex ()I
      // 0b8: if_icmpge 0d7
      // 0bb: aload 0
      // 0bc: iload 1
      // 0bd: iload 2
      // 0be: iload 3
      // 0bf: invokespecial net/rim/device/api/ui/component/RichTextField.moveFocus (III)I
      // 0c2: istore 4
      // 0c4: iload 4
      // 0c6: iload 1
      // 0c7: if_icmpeq 0d4
      // 0ca: aload 0
      // 0cb: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo$TextScreenField.this$1 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo;
      // 0ce: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo.this$0 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField;
      // 0d1: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewDisplayField.checkTrackChanges ()V
      // 0d4: iload 4
      // 0d6: ireturn
      // 0d7: aload 0
      // 0d8: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo$TextScreenField.this$1 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo;
      // 0db: invokespecial net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo.getControlIndex ()I
      // 0de: istore 4
      // 0e0: aload 0
      // 0e1: invokevirtual net/rim/device/api/ui/component/RichTextField.getTextLength ()I
      // 0e4: bipush 1
      // 0e5: isub
      // 0e6: istore 5
      // 0e8: iload 4
      // 0ea: aload 0
      // 0eb: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo$TextScreenField.this$1 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo;
      // 0ee: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo.this$0 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField;
      // 0f1: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField._FullDocFieldVector [Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo;
      // 0f4: arraylength
      // 0f5: bipush 1
      // 0f6: isub
      // 0f7: if_icmpge 124
      // 0fa: aload 0
      // 0fb: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo$TextScreenField.this$1 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo;
      // 0fe: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo.this$0 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField;
      // 101: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField._FullDocFieldVector [Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo;
      // 104: iinc 4 1
      // 107: iload 4
      // 109: aaload
      // 10a: astore 6
      // 10c: aload 6
      // 10e: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo._richTextField Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo$TextScreenField;
      // 111: ifnull 0e8
      // 114: iload 5
      // 116: aload 6
      // 118: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo._richTextField Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo$TextScreenField;
      // 11b: invokevirtual net/rim/device/api/ui/component/RichTextField.getTextLength ()I
      // 11e: iadd
      // 11f: istore 5
      // 121: goto 0e8
      // 124: iload 5
      // 126: aload 0
      // 127: invokevirtual net/rim/device/api/ui/component/RichTextField.getCursorPosition ()I
      // 12a: isub
      // 12b: aload 0
      // 12c: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo$TextScreenField.this$1 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo;
      // 12f: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo.this$0 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField;
      // 132: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField.AUTOMORE_TRIGGER I
      // 135: if_icmpge 146
      // 138: aload 0
      // 139: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo$TextScreenField.this$1 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo;
      // 13c: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo.this$0 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField;
      // 13f: aconst_null
      // 140: bipush 1
      // 141: bipush 0
      // 142: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewDisplayField.executeMore (Lnet/rim/device/apps/internal/docview/gui/MoreDescriptor;ZZ)Z
      // 145: pop
      // 146: iload 1
      // 147: istore 4
      // 149: aload 0
      // 14a: iload 1
      // 14b: iload 2
      // 14c: iload 3
      // 14d: invokespecial net/rim/device/api/ui/component/RichTextField.moveFocus (III)I
      // 150: istore 4
      // 152: goto 162
      // 155: astore 5
      // 157: bipush 0
      // 158: istore 4
      // 15a: goto 162
      // 15d: astore 5
      // 15f: bipush 0
      // 160: istore 4
      // 162: aload 0
      // 163: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo$TextScreenField.this$1 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo;
      // 166: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$TextControlInfo.this$0 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField;
      // 169: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewDisplayField.checkTrackChanges ()V
      // 16c: iload 4
      // 16e: ireturn
      // 16f: aload 0
      // 170: iload 1
      // 171: iload 2
      // 172: iload 3
      // 173: invokespecial net/rim/device/api/ui/component/RichTextField.moveFocus (III)I
      // 176: ireturn
      // try (148 -> 154): 155 null
      // try (148 -> 154): 159 null
   }

   static final void access$5800(DocViewTextDisplayField$TextControlInfo$TextScreenField x0) {
      x0.executeBackgroundScan();
   }
}
