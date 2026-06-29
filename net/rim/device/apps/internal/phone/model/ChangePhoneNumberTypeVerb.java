package net.rim.device.apps.internal.phone.model;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.apps.api.framework.verb.Verb;

class ChangePhoneNumberTypeVerb extends Verb {
   PopupScreen _dialog;
   private static final int _id = 414;
   private static ResourceBundle _resources = ResourceBundle.getBundle(2699923441625099942L, "net.rim.device.apps.internal.resource.Phone");

   ChangePhoneNumberTypeVerb() {
      super(623872);
   }

   @Override
   public String toString() {
      return _resources.getString(414);
   }

   @Override
   public Object invoke(Object param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/api/ui/UiApplication.getUiApplication ()Lnet/rim/device/api/ui/UiApplication;
      // 03: invokevirtual net/rim/device/api/ui/UiApplication.getActiveScreen ()Lnet/rim/device/api/ui/Screen;
      // 06: invokevirtual net/rim/device/api/ui/Screen.getLeafFieldWithFocus ()Lnet/rim/device/api/ui/Field;
      // 09: checkcast net/rim/device/apps/internal/phone/model/PhoneNumberModelEditField
      // 0c: astore 2
      // 0d: getstatic net/rim/device/apps/internal/phone/model/ChangePhoneNumberTypeVerb._resources Lnet/rim/device/api/i18n/ResourceBundle;
      // 10: sipush 601
      // 13: invokevirtual net/rim/device/api/i18n/ResourceBundle.getObject (I)Ljava/lang/Object;
      // 16: checkcast [Ljava/lang/Object;
      // 19: checkcast [Ljava/lang/Object;
      // 1c: astore 3
      // 1d: aload 2
      // 1e: invokevirtual net/rim/device/api/ui/component/BasicEditField.getLabel ()Ljava/lang/String;
      // 21: astore 4
      // 23: aload 4
      // 25: bipush 0
      // 26: aload 4
      // 28: invokevirtual java/lang/String.length ()I
      // 2b: bipush 2
      // 2d: isub
      // 2e: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 31: astore 4
      // 33: bipush 0
      // 34: istore 5
      // 36: bipush 0
      // 37: istore 6
      // 39: iload 6
      // 3b: aload 3
      // 3c: arraylength
      // 3d: if_icmpge 59
      // 40: aload 4
      // 42: aload 3
      // 43: iload 6
      // 45: aaload
      // 46: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 49: ifeq 53
      // 4c: iload 6
      // 4e: istore 5
      // 50: goto 59
      // 53: iinc 6 1
      // 56: goto 39
      // 59: new java/lang/Object
      // 5c: dup
      // 5d: getstatic net/rim/device/apps/internal/phone/model/ChangePhoneNumberTypeVerb._resources Lnet/rim/device/api/i18n/ResourceBundle;
      // 60: sipush 600
      // 63: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 66: aload 3
      // 67: aconst_null
      // 68: iload 5
      // 6a: bipush 1
      // 6b: invokestatic net/rim/device/api/system/Bitmap.getPredefinedBitmap (I)Lnet/rim/device/api/system/Bitmap;
      // 6e: bipush 1
      // 6f: i2l
      // 70: invokespecial net/rim/device/api/ui/component/Dialog.<init> (Ljava/lang/String;[Ljava/lang/Object;[IILnet/rim/device/api/system/Bitmap;J)V
      // 73: astore 6
      // 75: aload 6
      // 77: invokevirtual net/rim/device/api/ui/component/Dialog.doModal ()I
      // 7a: istore 7
      // 7c: iload 7
      // 7e: bipush -1
      // 80: if_icmpeq a4
      // 83: aload 2
      // 84: new java/lang/Object
      // 87: dup
      // 88: invokespecial java/lang/StringBuffer.<init> ()V
      // 8b: aload 3
      // 8c: iload 7
      // 8e: aaload
      // 8f: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 92: ldc_w ": "
      // 95: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 98: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 9b: invokevirtual net/rim/device/api/ui/component/BasicEditField.setLabel (Ljava/lang/String;)V
      // 9e: aconst_null
      // 9f: areturn
      // a0: astore 2
      // a1: aconst_null
      // a2: areturn
      // a3: astore 2
      // a4: aconst_null
      // a5: areturn
      // try (0 -> 73): 75 null
      // try (0 -> 73): 78 null
   }
}
