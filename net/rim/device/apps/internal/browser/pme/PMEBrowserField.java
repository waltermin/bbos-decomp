package net.rim.device.apps.internal.browser.pme;

import java.io.InputStream;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.browser.field.Destroyable;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.UrlRequestedEvent;
import net.rim.device.api.browser.plugin.BrowserPageContext;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MediaField;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.browser.ui.VerticalIndentFieldManager;
import net.rim.device.apps.internal.resource.PMEPlugginResource;
import net.rim.plazmic.mediaengine.MediaException;
import net.rim.plazmic.mediaengine.MediaListener;
import net.rim.plazmic.mediaengine.MediaManager;
import net.rim.plazmic.mediaengine.MediaPlayer;

public final class PMEBrowserField
   extends VerticalIndentFieldManager
   implements PMEPlugginResource,
   VerbProvider,
   MediaListener,
   BrowserPageContext,
   HolsterListener,
   Destroyable {
   private MediaField _field;
   protected MediaPlayer _player;
   protected Object _model;
   private String _popmsg;
   private MenuItemVerb _menuItemVerb;
   private final InfoVerb _infoVerb;
   private final PauseVerb _pauseVerb;
   private final RestartVerb _restartVerb;
   private final ResumeVerb _resumeVerb;
   private final ZoomVerb _zoomVerb;
   private final PanVerb _panVerb;
   private final ZoomPanResetVerb _zoomPanResetVerb;
   private final VerbMenuItem _infoMenuItem;
   private final VerbMenuItem _pauseMenuItem;
   private final VerbMenuItem _restartMenuItem;
   private final VerbMenuItem _resumeMenuItem;
   private final VerbMenuItem _zoomMenuItem;
   private final VerbMenuItem _panMenuItem;
   private final VerbMenuItem _zoomPanResetMenuItem;
   private int _numTimesStopped;
   private BrowserContentBaseImpl _browserContent;
   private int _destroyMethod;
   private UiApplication _app;
   private boolean _standalonePage;
   private static final int DEFAULT_PREFERRED_WIDTH = Display.getWidth();
   private static final int DEFAULT_PREFERRED_HEIGHT = Display.getHeight();
   private static ResourceBundle _resources = ResourceBundle.getBundle(1239635442466553906L, "net.rim.device.apps.internal.resource.PMEPluggin");

   protected PMEBrowserField(InputConnection param1, InputStream param2, BrowserContentBaseImpl param3, long param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: lload 4
      // 003: ldc2_w 3458764513820540928
      // 006: land
      // 007: invokespecial net/rim/device/apps/internal/browser/ui/VerticalIndentFieldManager.<init> (J)V
      // 00a: aload 0
      // 00b: bipush 0
      // 00c: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._destroyMethod I
      // 00f: aload 0
      // 010: new java/lang/Object
      // 013: dup
      // 014: invokespecial net/rim/plazmic/mediaengine/MediaPlayer.<init> ()V
      // 017: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._player Lnet/rim/plazmic/mediaengine/MediaPlayer;
      // 01a: aload 0
      // 01b: aload 3
      // 01c: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._browserContent Lnet/rim/device/api/browser/field/BrowserContentBaseImpl;
      // 01f: aload 0
      // 020: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._browserContent Lnet/rim/device/api/browser/field/BrowserContentBaseImpl;
      // 023: invokevirtual net/rim/device/api/browser/field/BrowserContentBaseImpl.getRenderingApplication ()Lnet/rim/device/api/browser/field/RenderingApplication;
      // 026: astore 6
      // 028: aload 0
      // 029: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._browserContent Lnet/rim/device/api/browser/field/BrowserContentBaseImpl;
      // 02c: invokevirtual net/rim/device/api/browser/field/BrowserContentBaseImpl.getRenderingFlags ()I
      // 02f: istore 7
      // 031: new java/lang/Object
      // 034: dup
      // 035: new net/rim/device/apps/internal/browser/pme/BrowserConnector
      // 038: dup
      // 039: aload 6
      // 03b: iload 7
      // 03d: invokespecial net/rim/device/apps/internal/browser/pme/BrowserConnector.<init> (Lnet/rim/device/api/browser/field/RenderingApplication;I)V
      // 040: bipush 1
      // 041: invokespecial net/rim/plazmic/mediaengine/MediaManager.<init> (Lnet/rim/plazmic/mediaengine/io/Connector;Z)V
      // 044: astore 8
      // 046: aconst_null
      // 047: astore 9
      // 049: aload 3
      // 04a: invokevirtual net/rim/device/api/browser/field/BrowserContentBaseImpl.getRenderingOptions ()Lnet/rim/device/api/browser/field/RenderingOptions;
      // 04d: astore 10
      // 04f: aload 10
      // 051: ifnull 095
      // 054: aload 10
      // 056: ldc2_w 4550690918222697397
      // 059: bipush 49
      // 05b: bipush 0
      // 05c: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithBooleanValue (JIZ)Z
      // 05f: ifeq 095
      // 062: aload 1
      // 063: dup
      // 064: instanceof java/lang/Object
      // 067: ifne 06e
      // 06a: pop
      // 06b: goto 095
      // 06e: checkcast java/lang/Object
      // 071: astore 11
      // 073: aload 11
      // 075: ldc_w "Content-Location"
      // 078: invokeinterface javax/microedition/io/HttpConnection.getHeaderField (Ljava/lang/String;)Ljava/lang/String; 2
      // 07d: astore 9
      // 07f: aload 9
      // 081: ifnonnull 095
      // 084: aload 11
      // 086: ldc_w "Location"
      // 089: invokeinterface javax/microedition/io/HttpConnection.getHeaderField (Ljava/lang/String;)Ljava/lang/String; 2
      // 08e: astore 9
      // 090: goto 095
      // 093: astore 12
      // 095: aload 9
      // 097: ifnonnull 0a3
      // 09a: aload 1
      // 09b: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getUrl (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 09e: astore 9
      // 0a0: goto 0ae
      // 0a3: aload 9
      // 0a5: aload 1
      // 0a6: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getUrl (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 0a9: invokestatic net/rim/device/apps/api/utility/general/URI.getAbsoluteURL (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
      // 0ac: astore 9
      // 0ae: aload 3
      // 0af: aload 9
      // 0b1: invokevirtual net/rim/device/api/browser/field/BrowserContentBaseImpl.setBaseUrl (Ljava/lang/String;)V
      // 0b4: aload 8
      // 0b6: ldc_w "URI_BASE"
      // 0b9: aload 9
      // 0bb: invokevirtual net/rim/plazmic/mediaengine/MediaManager.setProperty (Ljava/lang/String;Ljava/lang/String;)V
      // 0be: aload 8
      // 0c0: aload 0
      // 0c1: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._player Lnet/rim/plazmic/mediaengine/MediaPlayer;
      // 0c4: invokestatic net/rim/device/apps/internal/browser/pme/PMEBrowserField.registerEventLogger (Lnet/rim/plazmic/mediaengine/MediaManager;Lnet/rim/plazmic/mediaengine/MediaPlayer;)V
      // 0c7: aload 2
      // 0c8: ifnonnull 0d2
      // 0cb: aload 1
      // 0cc: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 0d1: astore 2
      // 0d2: aload 1
      // 0d3: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getContentType (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 0d6: astore 11
      // 0d8: invokestatic net/rim/plazmic/internal/mediaengine/ResourceContext.createContext ()Lnet/rim/plazmic/internal/mediaengine/ResourceContext;
      // 0db: astore 12
      // 0dd: aload 12
      // 0df: ldc_w "BrowserContent"
      // 0e2: aload 0
      // 0e3: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._browserContent Lnet/rim/device/api/browser/field/BrowserContentBaseImpl;
      // 0e6: invokevirtual net/rim/plazmic/internal/mediaengine/ResourceContext.set (Ljava/lang/Object;Ljava/lang/Object;)V
      // 0e9: aload 0
      // 0ea: aload 8
      // 0ec: aload 11
      // 0ee: aload 2
      // 0ef: aload 12
      // 0f1: aconst_null
      // 0f2: invokevirtual net/rim/plazmic/mediaengine/MediaManager.createResource (Ljava/lang/String;Ljava/lang/Object;Lnet/rim/plazmic/internal/mediaengine/ResourceContext;Ljava/lang/Object;)Ljava/lang/Object;
      // 0f5: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._model Ljava/lang/Object;
      // 0f8: aload 1
      // 0f9: ifnull 102
      // 0fc: aload 1
      // 0fd: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 102: aload 2
      // 103: ifnonnull 109
      // 106: goto 19b
      // 109: aload 2
      // 10a: invokevirtual java/io/InputStream.close ()V
      // 10d: goto 19b
      // 110: astore 11
      // 112: goto 19b
      // 115: astore 11
      // 117: aload 2
      // 118: ifnull 19b
      // 11b: aload 2
      // 11c: invokevirtual java/io/InputStream.close ()V
      // 11f: goto 19b
      // 122: astore 11
      // 124: goto 19b
      // 127: astore 13
      // 129: aload 2
      // 12a: ifnull 136
      // 12d: aload 2
      // 12e: invokevirtual java/io/InputStream.close ()V
      // 131: goto 136
      // 134: astore 14
      // 136: aload 13
      // 138: athrow
      // 139: astore 11
      // 13b: new java/lang/Object
      // 13e: dup
      // 13f: aload 0
      // 140: aload 11
      // 142: invokespecial net/rim/device/apps/internal/browser/pme/PMEBrowserField.getErrorMessage (Lnet/rim/plazmic/mediaengine/MediaException;)Ljava/lang/String;
      // 145: invokespecial net/rim/device/api/browser/field/RenderingException.<init> (Ljava/lang/String;)V
      // 148: athrow
      // 149: astore 11
      // 14b: new java/lang/Object
      // 14e: dup
      // 14f: aload 11
      // 151: invokevirtual java/lang/Exception.toString ()Ljava/lang/String;
      // 154: invokespecial net/rim/device/api/browser/field/RenderingException.<init> (Ljava/lang/String;)V
      // 157: athrow
      // 158: astore 15
      // 15a: aload 1
      // 15b: ifnull 164
      // 15e: aload 1
      // 15f: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 164: aload 2
      // 165: ifnull 198
      // 168: aload 2
      // 169: invokevirtual java/io/InputStream.close ()V
      // 16c: goto 198
      // 16f: astore 16
      // 171: goto 198
      // 174: astore 16
      // 176: aload 2
      // 177: ifnull 198
      // 17a: aload 2
      // 17b: invokevirtual java/io/InputStream.close ()V
      // 17e: goto 198
      // 181: astore 16
      // 183: goto 198
      // 186: astore 17
      // 188: aload 2
      // 189: ifnull 195
      // 18c: aload 2
      // 18d: invokevirtual java/io/InputStream.close ()V
      // 190: goto 195
      // 193: astore 18
      // 195: aload 17
      // 197: athrow
      // 198: aload 15
      // 19a: athrow
      // 19b: aload 0
      // 19c: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._model Ljava/lang/Object;
      // 19f: invokestatic net/rim/plazmic/internal/mediaengine/util/MEUtilities.getMediaModel (Ljava/lang/Object;)Lnet/rim/plazmic/internal/mediaengine/MediaModel;
      // 1a2: astore 11
      // 1a4: aload 11
      // 1a6: ifnonnull 1ac
      // 1a9: goto 285
      // 1ac: aload 0
      // 1ad: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._browserContent Lnet/rim/device/api/browser/field/BrowserContentBaseImpl;
      // 1b0: aload 11
      // 1b2: ldc_w "Title"
      // 1b5: invokeinterface net/rim/plazmic/internal/mediaengine/MediaModel.getMetaInfo (Ljava/lang/String;)Ljava/lang/String; 2
      // 1ba: invokevirtual net/rim/device/api/browser/field/BrowserContentBaseImpl.setTitle (Ljava/lang/String;)V
      // 1bd: aload 11
      // 1bf: invokeinterface net/rim/plazmic/internal/mediaengine/MediaModel.getMissingExtURLs ()Ljava/lang/String; 1
      // 1c4: ldc_w ""
      // 1c7: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 1ca: ifeq 1d0
      // 1cd: goto 285
      // 1d0: getstatic net/rim/device/apps/internal/browser/pme/PMEBrowserField._resources Lnet/rim/device/api/i18n/ResourceBundle;
      // 1d3: bipush 17
      // 1d5: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 1d8: astore 12
      // 1da: new java/lang/Object
      // 1dd: dup
      // 1de: invokespecial java/lang/StringBuffer.<init> ()V
      // 1e1: aload 12
      // 1e3: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1e6: ldc_w "\n"
      // 1e9: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1ec: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 1ef: astore 12
      // 1f1: new java/lang/Object
      // 1f4: dup
      // 1f5: invokespecial java/lang/StringBuffer.<init> ()V
      // 1f8: aload 12
      // 1fa: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1fd: getstatic net/rim/device/apps/internal/browser/pme/PMEBrowserField._resources Lnet/rim/device/api/i18n/ResourceBundle;
      // 200: bipush 18
      // 202: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 205: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 208: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 20b: astore 12
      // 20d: new java/lang/Object
      // 210: dup
      // 211: aload 12
      // 213: bipush 1
      // 214: bipush 0
      // 215: invokespecial net/rim/device/apps/internal/browser/core/BrowserError.<init> (Ljava/lang/String;ZZ)V
      // 218: astore 13
      // 21a: aload 13
      // 21c: dup
      // 21d: astore 14
      // 21f: monitorenter
      // 220: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 223: aload 13
      // 225: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;)V
      // 228: aload 13
      // 22a: invokevirtual java/lang/Object.wait ()V
      // 22d: goto 232
      // 230: astore 15
      // 232: aload 14
      // 234: monitorexit
      // 235: goto 240
      // 238: astore 19
      // 23a: aload 14
      // 23c: monitorexit
      // 23d: aload 19
      // 23f: athrow
      // 240: aload 13
      // 242: invokevirtual net/rim/device/apps/internal/browser/core/BrowserError.showDetails ()Z
      // 245: ifeq 285
      // 248: aload 13
      // 24a: invokevirtual net/rim/device/api/ui/component/Dialog.close ()V
      // 24d: new java/lang/Object
      // 250: dup
      // 251: aload 11
      // 253: invokeinterface net/rim/plazmic/internal/mediaengine/MediaModel.getMissingExtURLs ()Ljava/lang/String; 1
      // 258: bipush 0
      // 259: bipush 0
      // 25a: invokespecial net/rim/device/apps/internal/browser/core/BrowserError.<init> (Ljava/lang/String;ZZ)V
      // 25d: astore 14
      // 25f: aload 14
      // 261: dup
      // 262: astore 15
      // 264: monitorenter
      // 265: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 268: aload 14
      // 26a: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;)V
      // 26d: aload 14
      // 26f: invokevirtual java/lang/Object.wait ()V
      // 272: goto 277
      // 275: astore 16
      // 277: aload 15
      // 279: monitorexit
      // 27a: goto 285
      // 27d: astore 20
      // 27f: aload 15
      // 281: monitorexit
      // 282: aload 20
      // 284: athrow
      // 285: lload 4
      // 287: ldc2_w 18014398509481984
      // 28a: lor
      // 28b: lstore 4
      // 28d: aload 0
      // 28e: lload 4
      // 290: ldc2_w 3458764513820540928
      // 293: land
      // 294: bipush 0
      // 295: i2l
      // 296: lcmp
      // 297: ifeq 29e
      // 29a: bipush 1
      // 29b: goto 29f
      // 29e: bipush 0
      // 29f: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._standalonePage Z
      // 2a2: aload 0
      // 2a3: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._standalonePage Z
      // 2a6: ifeq 2b1
      // 2a9: ldc2_w 64424509440
      // 2ac: lstore 12
      // 2ae: goto 2bf
      // 2b1: ldc2_w 21474836480
      // 2b4: lstore 12
      // 2b6: lload 4
      // 2b8: ldc_w 25690112
      // 2bb: i2l
      // 2bc: lor
      // 2bd: lstore 4
      // 2bf: aload 0
      // 2c0: new java/lang/Object
      // 2c3: dup
      // 2c4: lload 4
      // 2c6: lload 12
      // 2c8: invokespecial net/rim/device/api/ui/MediaField.<init> (JJ)V
      // 2cb: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._field Lnet/rim/device/api/ui/MediaField;
      // 2ce: aload 0
      // 2cf: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._standalonePage Z
      // 2d2: ifne 2e2
      // 2d5: aload 0
      // 2d6: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._field Lnet/rim/device/api/ui/MediaField;
      // 2d9: getstatic net/rim/device/apps/internal/browser/pme/PMEBrowserField.DEFAULT_PREFERRED_WIDTH I
      // 2dc: getstatic net/rim/device/apps/internal/browser/pme/PMEBrowserField.DEFAULT_PREFERRED_HEIGHT I
      // 2df: invokevirtual net/rim/device/api/ui/MediaField.setPreferredExtent (II)V
      // 2e2: aload 0
      // 2e3: aload 0
      // 2e4: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._field Lnet/rim/device/api/ui/MediaField;
      // 2e7: invokevirtual net/rim/device/apps/internal/browser/ui/VerticalIndentFieldManager.add (Lnet/rim/device/api/ui/Field;)V
      // 2ea: aload 0
      // 2eb: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 2ee: checkcast java/lang/Object
      // 2f1: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._app Lnet/rim/device/api/ui/UiApplication;
      // 2f4: aload 0
      // 2f5: bipush 0
      // 2f6: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._numTimesStopped I
      // 2f9: aload 0
      // 2fa: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._player Lnet/rim/plazmic/mediaengine/MediaPlayer;
      // 2fd: aload 0
      // 2fe: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._model Ljava/lang/Object;
      // 301: invokevirtual net/rim/plazmic/mediaengine/MediaPlayer.setMedia (Ljava/lang/Object;)V
      // 304: aload 0
      // 305: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._player Lnet/rim/plazmic/mediaengine/MediaPlayer;
      // 308: aload 0
      // 309: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._field Lnet/rim/device/api/ui/MediaField;
      // 30c: invokevirtual net/rim/plazmic/mediaengine/MediaPlayer.setUI (Lnet/rim/plazmic/internal/mediaengine/service/BasicService;)V
      // 30f: aload 0
      // 310: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._standalonePage Z
      // 313: ifne 32d
      // 316: aload 0
      // 317: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._field Lnet/rim/device/api/ui/MediaField;
      // 31a: bipush 0
      // 31b: invokevirtual net/rim/device/api/ui/MediaField.setWrap (Z)V
      // 31e: goto 32d
      // 321: astore 14
      // 323: aload 0
      // 324: aload 0
      // 325: aload 14
      // 327: invokespecial net/rim/device/apps/internal/browser/pme/PMEBrowserField.getErrorMessage (Lnet/rim/plazmic/mediaengine/MediaException;)Ljava/lang/String;
      // 32a: invokespecial net/rim/device/apps/internal/browser/pme/PMEBrowserField.popup (Ljava/lang/String;)V
      // 32d: aload 0
      // 32e: new net/rim/device/apps/internal/browser/pme/PauseVerb
      // 331: dup
      // 332: aload 0
      // 333: invokespecial net/rim/device/apps/internal/browser/pme/PauseVerb.<init> (Lnet/rim/device/apps/internal/browser/pme/PMEBrowserField;)V
      // 336: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._pauseVerb Lnet/rim/device/apps/internal/browser/pme/PauseVerb;
      // 339: aload 0
      // 33a: new net/rim/device/apps/internal/browser/pme/RestartVerb
      // 33d: dup
      // 33e: aload 0
      // 33f: invokespecial net/rim/device/apps/internal/browser/pme/RestartVerb.<init> (Lnet/rim/device/apps/internal/browser/pme/PMEBrowserField;)V
      // 342: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._restartVerb Lnet/rim/device/apps/internal/browser/pme/RestartVerb;
      // 345: aload 0
      // 346: new net/rim/device/apps/internal/browser/pme/ResumeVerb
      // 349: dup
      // 34a: aload 0
      // 34b: invokespecial net/rim/device/apps/internal/browser/pme/ResumeVerb.<init> (Lnet/rim/device/apps/internal/browser/pme/PMEBrowserField;)V
      // 34e: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._resumeVerb Lnet/rim/device/apps/internal/browser/pme/ResumeVerb;
      // 351: aload 0
      // 352: new net/rim/device/apps/internal/browser/pme/InfoVerb
      // 355: dup
      // 356: aload 0
      // 357: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._player Lnet/rim/plazmic/mediaengine/MediaPlayer;
      // 35a: invokespecial net/rim/device/apps/internal/browser/pme/InfoVerb.<init> (Lnet/rim/plazmic/mediaengine/MediaPlayer;)V
      // 35d: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._infoVerb Lnet/rim/device/apps/internal/browser/pme/InfoVerb;
      // 360: aload 0
      // 361: new net/rim/device/apps/internal/browser/pme/PanVerb
      // 364: dup
      // 365: aload 0
      // 366: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._field Lnet/rim/device/api/ui/MediaField;
      // 369: aload 0
      // 36a: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._player Lnet/rim/plazmic/mediaengine/MediaPlayer;
      // 36d: invokespecial net/rim/device/apps/internal/browser/pme/PanVerb.<init> (Lnet/rim/device/api/ui/Field;Ljava/lang/Object;)V
      // 370: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._panVerb Lnet/rim/device/apps/internal/browser/pme/PanVerb;
      // 373: aload 0
      // 374: new net/rim/device/apps/internal/browser/pme/ZoomVerb
      // 377: dup
      // 378: aload 0
      // 379: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._field Lnet/rim/device/api/ui/MediaField;
      // 37c: aload 0
      // 37d: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._player Lnet/rim/plazmic/mediaengine/MediaPlayer;
      // 380: invokespecial net/rim/device/apps/internal/browser/pme/ZoomVerb.<init> (Lnet/rim/device/api/ui/Field;Ljava/lang/Object;)V
      // 383: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._zoomVerb Lnet/rim/device/apps/internal/browser/pme/ZoomVerb;
      // 386: aload 0
      // 387: new net/rim/device/apps/internal/browser/pme/ZoomPanResetVerb
      // 38a: dup
      // 38b: aload 0
      // 38c: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._field Lnet/rim/device/api/ui/MediaField;
      // 38f: aload 0
      // 390: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._player Lnet/rim/plazmic/mediaengine/MediaPlayer;
      // 393: invokespecial net/rim/device/apps/internal/browser/pme/ZoomPanResetVerb.<init> (Lnet/rim/device/api/ui/Field;Ljava/lang/Object;)V
      // 396: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._zoomPanResetVerb Lnet/rim/device/apps/internal/browser/pme/ZoomPanResetVerb;
      // 399: aload 0
      // 39a: new java/lang/Object
      // 39d: dup
      // 39e: aload 0
      // 39f: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._restartVerb Lnet/rim/device/apps/internal/browser/pme/RestartVerb;
      // 3a2: ldc_w 2147483647
      // 3a5: invokespecial net/rim/device/apps/api/ui/VerbMenuItem.<init> (Lnet/rim/device/apps/api/framework/verb/Verb;I)V
      // 3a8: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._restartMenuItem Lnet/rim/device/apps/api/ui/VerbMenuItem;
      // 3ab: aload 0
      // 3ac: new java/lang/Object
      // 3af: dup
      // 3b0: aload 0
      // 3b1: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._resumeVerb Lnet/rim/device/apps/internal/browser/pme/ResumeVerb;
      // 3b4: ldc_w 2147483647
      // 3b7: invokespecial net/rim/device/apps/api/ui/VerbMenuItem.<init> (Lnet/rim/device/apps/api/framework/verb/Verb;I)V
      // 3ba: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._resumeMenuItem Lnet/rim/device/apps/api/ui/VerbMenuItem;
      // 3bd: aload 0
      // 3be: new java/lang/Object
      // 3c1: dup
      // 3c2: aload 0
      // 3c3: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._pauseVerb Lnet/rim/device/apps/internal/browser/pme/PauseVerb;
      // 3c6: ldc_w 2147483647
      // 3c9: invokespecial net/rim/device/apps/api/ui/VerbMenuItem.<init> (Lnet/rim/device/apps/api/framework/verb/Verb;I)V
      // 3cc: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._pauseMenuItem Lnet/rim/device/apps/api/ui/VerbMenuItem;
      // 3cf: aload 0
      // 3d0: new java/lang/Object
      // 3d3: dup
      // 3d4: aload 0
      // 3d5: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._infoVerb Lnet/rim/device/apps/internal/browser/pme/InfoVerb;
      // 3d8: ldc_w 2147483647
      // 3db: invokespecial net/rim/device/apps/api/ui/VerbMenuItem.<init> (Lnet/rim/device/apps/api/framework/verb/Verb;I)V
      // 3de: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._infoMenuItem Lnet/rim/device/apps/api/ui/VerbMenuItem;
      // 3e1: aload 0
      // 3e2: new java/lang/Object
      // 3e5: dup
      // 3e6: aload 0
      // 3e7: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._panVerb Lnet/rim/device/apps/internal/browser/pme/PanVerb;
      // 3ea: ldc_w 2147483647
      // 3ed: invokespecial net/rim/device/apps/api/ui/VerbMenuItem.<init> (Lnet/rim/device/apps/api/framework/verb/Verb;I)V
      // 3f0: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._panMenuItem Lnet/rim/device/apps/api/ui/VerbMenuItem;
      // 3f3: aload 0
      // 3f4: new java/lang/Object
      // 3f7: dup
      // 3f8: aload 0
      // 3f9: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._zoomVerb Lnet/rim/device/apps/internal/browser/pme/ZoomVerb;
      // 3fc: ldc_w 2147483647
      // 3ff: invokespecial net/rim/device/apps/api/ui/VerbMenuItem.<init> (Lnet/rim/device/apps/api/framework/verb/Verb;I)V
      // 402: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._zoomMenuItem Lnet/rim/device/apps/api/ui/VerbMenuItem;
      // 405: aload 0
      // 406: new java/lang/Object
      // 409: dup
      // 40a: aload 0
      // 40b: getfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._zoomPanResetVerb Lnet/rim/device/apps/internal/browser/pme/ZoomPanResetVerb;
      // 40e: ldc_w 2147483647
      // 411: invokespecial net/rim/device/apps/api/ui/VerbMenuItem.<init> (Lnet/rim/device/apps/api/framework/verb/Verb;I)V
      // 414: putfield net/rim/device/apps/internal/browser/pme/PMEBrowserField._zoomPanResetMenuItem Lnet/rim/device/apps/api/ui/VerbMenuItem;
      // 417: return
      // try (55 -> 65): 66 null
      // try (116 -> 121): 122 null
      // try (112 -> 116): 124 null
      // try (125 -> 129): 130 null
      // try (112 -> 116): 132 null
      // try (124 -> 125): 132 null
      // try (133 -> 137): 138 null
      // try (132 -> 133): 132 null
      // try (89 -> 112): 141 null
      // try (89 -> 112): 149 null
      // try (89 -> 112): 156 null
      // try (161 -> 165): 166 null
      // try (157 -> 161): 168 null
      // try (169 -> 173): 174 null
      // try (157 -> 161): 176 null
      // try (168 -> 169): 176 null
      // try (177 -> 181): 182 null
      // try (176 -> 177): 176 null
      // try (141 -> 157): 156 null
      // try (244 -> 246): 247 null
      // try (241 -> 250): 251 null
      // try (251 -> 254): 251 null
      // try (276 -> 278): 279 null
      // try (273 -> 282): 283 null
      // try (283 -> 286): 283 null
      // try (343 -> 360): 361 null
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void onDisplay() {
      super.onDisplay();
      this._numTimesStopped = 0;

      label32:
      try {
         this._player.addMediaListener(this);
         if (this._player.getState() == 0) {
            this._player.setMedia(this._model);
         }

         this._player.setMediaTime(0);
         this._player.setUI(this._field);
         if (this.isVisible() && !DeviceInfo.isInHolster()) {
            this.startPlayer();
         }
      } catch (Throwable var3) {
         this.popup(this.getErrorMessage(e));
         break label32;
      }

      this._app.addHolsterListener(this);
   }

   @Override
   protected final void sublayout(int width, int height) {
      if (this._standalonePage) {
         height = this._browserContent.getRenderingApplication().getAvailableHeight(this._browserContent);
      }

      super.sublayout(width, height);
   }

   @Override
   public final void onUndisplay() {
      super.onUndisplay();
      this._player.removeMediaListener(this);
      if (this._destroyMethod == 0) {
         this.destroy();
      } else if (this._field != null) {
         if (this._field.isPannable()) {
            this._field.setPanX(0);
            this._field.setPanY(0);
         }

         if (this._field.isZoomable()) {
            this._field.setZoomAmount(65536);
         }
      }

      this._app.removeHolsterListener(this);
      this.stopPlayer();
   }

   private final String getErrorMessage(MediaException e) {
      String message = null;
      switch (e.getCode()) {
         case 1:
            return _resources.getString(1);
         case 2:
            return _resources.getString(6);
         case 3:
            return _resources.getString(0);
         case 4:
            return _resources.getString(5);
         case 10:
            return _resources.getString(2);
         case 11:
            return _resources.getString(1);
         default:
            return ((StringBuffer)(new Object())).append(_resources.getString(9)).append(e.toString()).toString();
      }
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      if (visible) {
         this.startPlayer();
      } else {
         this.stopPlayer();
      }

      super.onVisibilityChange(visible);
   }

   public final void restartPlayer() {
      this._numTimesStopped = 0;

      try {
         this._player.stop();
         this._player.setMediaTime(0);
         if (!DeviceInfo.isInHolster()) {
            this._player.start();
            this._app.repaint();
            return;
         }
      } finally {
         return;
      }
   }

   public final void stopPlayer() {
      this._numTimesStopped++;
      if (this._player.getState() == 2) {
         this._player.stop();
      }
   }

   public final void startPlayer() {
      this._numTimesStopped--;
      if (this._numTimesStopped < 0) {
         this._numTimesStopped = 0;
      }

      if (!DeviceInfo.isInHolster()) {
         try {
            if (this._player.getState() != 2 && this._numTimesStopped == 0) {
               this._player.start();
               return;
            }
         } finally {
            return;
         }
      }
   }

   @Override
   public final void mediaEvent(Object sender, int event, int eventParam, Object data) {
      switch (event) {
         case 7:
            RenderingApplication renderingApplication = this._browserContent.getRenderingApplication();
            int flags = this._browserContent.getSharedFlags();
            if (renderingApplication == null) {
               return;
            } else {
               UrlRequestedEvent urlRequestedEvent = (UrlRequestedEvent)(new Object(this._browserContent, (String)data, null, null, false, flags | 1));
               renderingApplication.eventOccurred(urlRequestedEvent);
            }
      }
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (this._field != null && c == 27 && this._field.isScrollMode()) {
         this._field.setScrollMode(false);
      }

      return super.keyChar(c, status, time);
   }

   @Override
   protected final void onMenuDismissed(Menu menu) {
      super.onMenuDismissed(menu);
      MenuItem menuItem = menu.getSelectedItem();
      if (!(menuItem instanceof Object)) {
         this.startPlayer();
      } else {
         Verb v = ((VerbMenuItem)menuItem).getVerb();
         if (!(v instanceof Object)) {
            this.startPlayer();
            return;
         }
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      int playerState = this._player.getState();
      this.stopPlayer();
      super.makeMenu(menu, instance);
      menu.add(this._restartMenuItem);
      if (playerState == 1) {
         menu.add(this._resumeMenuItem);
      } else if (playerState == 2) {
         menu.add(this._pauseMenuItem);
      }

      menu.add(this._infoMenuItem);
      boolean addReset = false;
      if (this._field.isPannable()) {
         menu.add(this._panMenuItem);
         addReset = true;
      }

      if (this._field.isZoomable()) {
         menu.add(this._zoomMenuItem);
         addReset = true;
      }

      if ((this._field.getPanX() != 0 || this._field.getPanY() != 0 || this._field.getZoomAmount() != 65536) && addReset) {
         menu.add(this._zoomPanResetMenuItem);
      }
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (this._field == null) {
         return null;
      }

      Manager manager = this._field.getManager();
      if (manager.getFieldWithFocus() != this._field) {
         return null;
      }

      Field tempField = this._field.getLeafFieldWithFocus();
      ContextMenu menu = tempField == null ? null : tempField.getContextMenu();
      MenuItem item = null;
      if (menu != null) {
         item = menu.getDefaultItem();
      }

      if (item != null) {
         if (this._menuItemVerb == null) {
            this._menuItemVerb = new MenuItemVerb(item);
         } else {
            this._menuItemVerb.setMenuItem(item);
         }

         return this._menuItemVerb;
      } else {
         return null;
      }
   }

   private final void popup(String message) {
      this._popmsg = message;
      Application.getApplication().invokeLater(new PMEBrowserField$1(this));
   }

   private static final void registerEventLogger(MediaManager m, MediaPlayer p) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Object eventLogObj = ar.get(-6921419827463678505L);
      if (eventLogObj instanceof Object) {
         m.addMediaListener((MediaListener)eventLogObj);
         p.setInternalMediaListener((MediaListener)eventLogObj);
      }
   }

   @Override
   public final boolean getPropertyWithBooleanValue(int id, boolean defaultValue) {
      return defaultValue;
   }

   @Override
   public final int getPropertyWithIntValue(int id, int defaultValue) {
      switch (id) {
         case 2:
            return 78;
         default:
            return defaultValue;
      }
   }

   @Override
   public final Object getPropertyWithObjectValue(int id, Object defaultValue) {
      return defaultValue;
   }

   @Override
   public final String getPropertyWithStringValue(int id, String defaultValue) {
      return defaultValue;
   }

   @Override
   public final void inHolster() {
      this.stopPlayer();
   }

   @Override
   public final void outOfHolster() {
      this.startPlayer();
   }

   @Override
   public final void setDestroyMethod(int method) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void destroy() {
      this._player.close();
   }
}
