package net.rim.device.apps.internal.qm.peer.common;

import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.browser.field.ResourceProvider;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.internal.qm.resource.QmResources;

public final class QmRenderScreen extends MainScreen implements RenderingApplication, ResourceProvider {
   private InputConnection _inputConnection;
   private UiApplication _uiApp;
   private BrowserContent _content;
   private Field _field;
   private static final int RenderingOptions_IMAGE_QUALITY_VALUE;
   private static final int RenderingOptions_IMAGE_QUALITY_VALUE_HIGH;

   public QmRenderScreen(InputConnection input) {
      this._inputConnection = input;
   }

   public final void doModal() {
      QmRenderScreen thisScreen = this;
      Thread t = new QmRenderScreen$1(this, thisScreen);
      t.start();
   }

   private final Field createFieldForItem(InputConnection param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/api/browser/field/RenderingSession.getNewInstance ()Lnet/rim/device/api/browser/field/RenderingSession;
      // 03: astore 2
      // 04: aload 2
      // 05: invokevirtual net/rim/device/api/browser/field/RenderingSession.getRenderingOptions ()Lnet/rim/device/api/browser/field/RenderingOptions;
      // 08: astore 3
      // 09: aload 3
      // 0a: ldc2_w 4550690918222697397
      // 0d: bipush 41
      // 0f: bipush 0
      // 10: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JIZ)V
      // 13: aload 3
      // 14: ldc2_w 4550690918222697397
      // 17: bipush 26
      // 19: bipush 1
      // 1a: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JIZ)V
      // 1d: aload 3
      // 1e: ldc2_w 4550690918222697397
      // 21: bipush 43
      // 23: bipush 2
      // 25: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JII)V
      // 28: aload 3
      // 29: ldc2_w 4550690918222697397
      // 2c: bipush 40
      // 2e: bipush 1
      // 2f: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JIZ)V
      // 32: aload 0
      // 33: aload 2
      // 34: aload 1
      // 35: checkcast java/lang/Object
      // 38: aload 0
      // 39: bipush 0
      // 3a: invokevirtual net/rim/device/api/browser/field/RenderingSession.getBrowserContent (Ljavax/microedition/io/HttpConnection;Lnet/rim/device/api/browser/field/RenderingApplication;I)Lnet/rim/device/api/browser/field/BrowserContent;
      // 3d: putfield net/rim/device/apps/internal/qm/peer/common/QmRenderScreen._content Lnet/rim/device/api/browser/field/BrowserContent;
      // 40: new net/rim/device/apps/internal/qm/peer/common/QmRenderScreen$2
      // 43: dup
      // 44: aload 0
      // 45: invokespecial net/rim/device/apps/internal/qm/peer/common/QmRenderScreen$2.<init> (Lnet/rim/device/apps/internal/qm/peer/common/QmRenderScreen;)V
      // 48: astore 4
      // 4a: aload 4
      // 4c: invokevirtual java/lang/Thread.start ()V
      // 4f: aload 0
      // 50: aload 0
      // 51: getfield net/rim/device/apps/internal/qm/peer/common/QmRenderScreen._content Lnet/rim/device/api/browser/field/BrowserContent;
      // 54: invokeinterface net/rim/device/api/browser/field/BrowserContent.getDisplayableContent ()Lnet/rim/device/api/ui/Field; 1
      // 59: putfield net/rim/device/apps/internal/qm/peer/common/QmRenderScreen._field Lnet/rim/device/api/ui/Field;
      // 5c: aload 0
      // 5d: getfield net/rim/device/apps/internal/qm/peer/common/QmRenderScreen._field Lnet/rim/device/api/ui/Field;
      // 60: areturn
      // 61: astore 2
      // 62: aconst_null
      // 63: areturn
      // 64: astore 2
      // 65: aconst_null
      // 66: areturn
      // try (0 -> 47): 48 null
      // try (0 -> 47): 51 null
   }

   private final Field createRenderErrorField() {
      return (Field)(new Object(QmResources.getString(121), 36028797018963968L));
   }

   public final int getContentWindowWidth() {
      return Display.getWidth();
   }

   public final int getContentWindowHeight() {
      return Display.getHeight();
   }

   @Override
   protected final void sublayout(int width, int height) {
      this.setPosition(0, 0);
      this.setExtent(width, height);
      this.setPositionDelegate(0, 0);
      this.layoutDelegate(width, height);
   }

   @Override
   public final void close() {
      this._uiApp = null;
      super.close();
   }

   @Override
   public final Object eventOccurred(Event event) {
      return null;
   }

   @Override
   public final String getHTTPCookie(String url) {
      return null;
   }

   @Override
   public final HttpConnection getResource(RequestedResource resource, BrowserContent referrer) {
      return null;
   }

   @Override
   public final InputConnection getInputConnection(RequestedResource resource, BrowserContent referrer) {
      return null;
   }

   @Override
   public final int getAvailableHeight(BrowserContent browserContent) {
      return this.getContentWindowHeight();
   }

   @Override
   public final int getAvailableWidth(BrowserContent browserContent) {
      return this.getContentWindowWidth();
   }

   @Override
   public final int getHistoryPosition(BrowserContent browserContent) {
      return -1;
   }

   @Override
   public final void invokeRunnable(Runnable runnable) {
      new QmRenderScreen$4(this, runnable).start();
   }

   static final Field access$002(QmRenderScreen x0, Field x1) {
      return x0._field = x1;
   }

   static final InputConnection access$100(QmRenderScreen x0) {
      return x0._inputConnection;
   }

   static final Field access$200(QmRenderScreen x0, InputConnection x1) {
      return x0.createFieldForItem(x1);
   }

   static final Field access$000(QmRenderScreen x0) {
      return x0._field;
   }

   static final Field access$300(QmRenderScreen x0) {
      return x0.createRenderErrorField();
   }

   static final BrowserContent access$600(QmRenderScreen x0) {
      return x0._content;
   }
}
