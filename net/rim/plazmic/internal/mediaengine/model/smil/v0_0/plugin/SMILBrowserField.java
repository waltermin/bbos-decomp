package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.plugin;

import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.mms.ui.MMSPresentationField;
import net.rim.plazmic.internal.mediaengine.event.EventEngine;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.RegionManager;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.SMILModel;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.SMILPlayer;

final class SMILBrowserField extends VerticalFieldManager {
   private SMILModel _model;
   private EventEngine _engine;
   private RegionManager _regionManager;
   private SMILPlayer _player;
   private boolean _restartable;
   private int _isTemplate;
   private final VerbMenuItem _pauseMenuItem;
   private final VerbMenuItem _resumeMenuItem;
   private final VerbMenuItem _restartMenuItem;

   public SMILBrowserField(BrowserContent param1, HttpConnection param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: ldc2_w 1407374883553280
      // 004: invokespecial net/rim/device/api/ui/container/VerticalFieldManager.<init> (J)V
      // 007: aload 0
      // 008: bipush -1
      // 00a: putfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._isTemplate I
      // 00d: aconst_null
      // 00e: astore 3
      // 00f: aload 0
      // 010: bipush 1
      // 011: putfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._restartable Z
      // 014: aload 1
      // 015: putstatic net/rim/plazmic/internal/mediaengine/model/smil/v0_0/contentregistry/ContentRegistry._browserContent Lnet/rim/device/api/browser/field/BrowserContent;
      // 018: aload 2
      // 019: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 01e: astore 3
      // 01f: aload 2
      // 020: ldc_w "smil-context"
      // 023: invokeinterface javax/microedition/io/HttpConnection.getRequestProperty (Ljava/lang/String;)Ljava/lang/String; 2
      // 028: astore 5
      // 02a: ldc_w "MMS"
      // 02d: aload 5
      // 02f: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 032: ifeq 047
      // 035: new net/rim/plazmic/internal/mediaengine/model/smil/v0_0/MMSParser
      // 038: dup
      // 039: invokespecial net/rim/plazmic/internal/mediaengine/model/smil/v0_0/MMSParser.<init> ()V
      // 03c: astore 4
      // 03e: aload 4
      // 040: bipush 1
      // 041: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILParser.setConfiguration (I)V
      // 044: goto 050
      // 047: new net/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILParser
      // 04a: dup
      // 04b: invokespecial net/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILParser.<init> ()V
      // 04e: astore 4
      // 050: aload 4
      // 052: aload 3
      // 053: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILParser.parseDocument (Ljava/io/InputStream;)Lorg/w3c/dom/Document;
      // 056: astore 6
      // 058: goto 096
      // 05b: astore 7
      // 05d: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 060: ldc_w "Caught SAXException"
      // 063: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 066: getstatic java/lang/System.err Ljava/io/PrintStream;
      // 069: aload 7
      // 06b: invokevirtual org/xml/sax/SAXException.getMessage ()Ljava/lang/String;
      // 06e: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 071: new java/io/IOException
      // 074: dup
      // 075: aload 7
      // 077: invokevirtual org/xml/sax/SAXException.getMessage ()Ljava/lang/String;
      // 07a: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 07d: athrow
      // 07e: astore 7
      // 080: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 083: ldc_w "Caught ParserConfigException"
      // 086: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 089: new java/io/IOException
      // 08c: dup
      // 08d: aload 7
      // 08f: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 092: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 095: athrow
      // 096: aload 0
      // 097: aload 4
      // 099: aload 6
      // 09b: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILParser.parse (Lorg/w3c/dom/Document;)Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel;
      // 09e: putfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._model Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel;
      // 0a1: goto 0ba
      // 0a4: astore 7
      // 0a6: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 0a9: ldc_w "Runtime Exception in SMIL parser"
      // 0ac: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 0af: new java/io/IOException
      // 0b2: dup
      // 0b3: ldc_w "Invalid SMIL attachment."
      // 0b6: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 0b9: athrow
      // 0ba: aload 0
      // 0bb: aload 0
      // 0bc: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._model Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel;
      // 0bf: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel.getRootLayout ()Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/Region;
      // 0c2: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 0c5: aload 0
      // 0c6: new net/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILPlayer
      // 0c9: dup
      // 0ca: invokespecial net/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILPlayer.<init> ()V
      // 0cd: putfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._player Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILPlayer;
      // 0d0: aload 0
      // 0d1: new net/rim/plazmic/internal/mediaengine/event/EventEngine
      // 0d4: dup
      // 0d5: invokespecial net/rim/plazmic/internal/mediaengine/event/EventEngine.<init> ()V
      // 0d8: putfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._engine Lnet/rim/plazmic/internal/mediaengine/event/EventEngine;
      // 0db: new net/rim/plazmic/internal/mediaengine/event/EventResolverImpl
      // 0de: dup
      // 0df: invokespecial net/rim/plazmic/internal/mediaengine/event/EventResolverImpl.<init> ()V
      // 0e2: astore 7
      // 0e4: aload 7
      // 0e6: aload 0
      // 0e7: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._engine Lnet/rim/plazmic/internal/mediaengine/event/EventEngine;
      // 0ea: invokeinterface net/rim/plazmic/internal/mediaengine/service/EventResolver.setEngine (Lnet/rim/plazmic/internal/mediaengine/event/EventEngine;)V 2
      // 0ef: aload 0
      // 0f0: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._model Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel;
      // 0f3: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel.getEventLogic ()Lnet/rim/plazmic/internal/mediaengine/event/EventLogic;
      // 0f6: astore 8
      // 0f8: aload 0
      // 0f9: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._model Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel;
      // 0fc: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel.getStartEvent ()Lnet/rim/plazmic/internal/mediaengine/event/Event;
      // 0ff: astore 9
      // 101: aload 0
      // 102: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._engine Lnet/rim/plazmic/internal/mediaengine/event/EventEngine;
      // 105: invokevirtual net/rim/plazmic/internal/mediaengine/event/EventEngine.getEventInstance ()Lnet/rim/plazmic/internal/mediaengine/event/Event;
      // 108: astore 10
      // 10a: aload 10
      // 10c: bipush 2
      // 10e: putfield net/rim/plazmic/internal/mediaengine/event/Event._event I
      // 111: aload 10
      // 113: aload 9
      // 115: getfield net/rim/plazmic/internal/mediaengine/event/Event._eventParam I
      // 118: putfield net/rim/plazmic/internal/mediaengine/event/Event._eventParam I
      // 11b: aload 0
      // 11c: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._engine Lnet/rim/plazmic/internal/mediaengine/event/EventEngine;
      // 11f: invokevirtual net/rim/plazmic/internal/mediaengine/event/EventEngine.getEventInstance ()Lnet/rim/plazmic/internal/mediaengine/event/Event;
      // 122: astore 11
      // 124: aload 11
      // 126: bipush 4
      // 128: putfield net/rim/plazmic/internal/mediaengine/event/Event._event I
      // 12b: aload 11
      // 12d: bipush -1
      // 12f: putfield net/rim/plazmic/internal/mediaengine/event/Event._eventParam I
      // 132: aload 8
      // 134: aload 10
      // 136: aload 11
      // 138: bipush 0
      // 139: i2l
      // 13a: invokeinterface net/rim/plazmic/internal/mediaengine/event/EventLogic.addEventDependancy (Lnet/rim/plazmic/internal/mediaengine/event/Event;Lnet/rim/plazmic/internal/mediaengine/event/Event;J)V 5
      // 13f: aload 7
      // 141: bipush 4
      // 143: aload 0
      // 144: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._player Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILPlayer;
      // 147: invokeinterface net/rim/plazmic/internal/mediaengine/service/EventResolver.setEventListener (ILjava/lang/Object;)V 3
      // 14c: aload 7
      // 14e: aload 8
      // 150: invokeinterface net/rim/plazmic/internal/mediaengine/service/EventResolver.setEventLogic (Ljava/lang/Object;)V 2
      // 155: aload 0
      // 156: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._engine Lnet/rim/plazmic/internal/mediaengine/event/EventEngine;
      // 159: aload 10
      // 15b: invokevirtual net/rim/plazmic/internal/mediaengine/event/EventEngine.releaseEventInstance (Lnet/rim/plazmic/internal/mediaengine/event/Event;)V
      // 15e: aload 0
      // 15f: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._engine Lnet/rim/plazmic/internal/mediaengine/event/EventEngine;
      // 162: aload 11
      // 164: invokevirtual net/rim/plazmic/internal/mediaengine/event/EventEngine.releaseEventInstance (Lnet/rim/plazmic/internal/mediaengine/event/Event;)V
      // 167: aload 0
      // 168: new net/rim/plazmic/internal/mediaengine/model/smil/v0_0/RegionManager
      // 16b: dup
      // 16c: invokespecial net/rim/plazmic/internal/mediaengine/model/smil/v0_0/RegionManager.<init> ()V
      // 16f: putfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._regionManager Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/RegionManager;
      // 172: aload 0
      // 173: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._regionManager Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/RegionManager;
      // 176: aload 0
      // 177: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._engine Lnet/rim/plazmic/internal/mediaengine/event/EventEngine;
      // 17a: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/RegionManager.setEngine (Lnet/rim/plazmic/internal/mediaengine/event/EventEngine;)V
      // 17d: aload 0
      // 17e: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._regionManager Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/RegionManager;
      // 181: aload 7
      // 183: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/RegionManager.setEventResolver (Lnet/rim/plazmic/internal/mediaengine/service/EventResolver;)V
      // 186: aload 0
      // 187: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._regionManager Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/RegionManager;
      // 18a: aload 0
      // 18b: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._model Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel;
      // 18e: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/RegionManager.setModel (Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel;)V
      // 191: aload 0
      // 192: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._regionManager Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/RegionManager;
      // 195: aload 0
      // 196: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._player Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILPlayer;
      // 199: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/RegionManager.setPlayer (Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILPlayer;)V
      // 19c: ldc_w "MMS"
      // 19f: aload 5
      // 1a1: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 1a4: ifeq 209
      // 1a7: aload 0
      // 1a8: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._model Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel;
      // 1ab: checkcast net/rim/plazmic/internal/mediaengine/model/smil/v0_0/MMSModel
      // 1ae: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/MMSModel.getSlideManager ()Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/ui/SlideManager;
      // 1b1: astore 12
      // 1b3: aload 12
      // 1b5: aload 0
      // 1b6: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._player Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILPlayer;
      // 1b9: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/ui/SlideManager.setPlayer (Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILPlayer;)V
      // 1bc: new net/rim/plazmic/internal/mediaengine/model/smil/v0_0/EventInterceptor
      // 1bf: dup
      // 1c0: aload 0
      // 1c1: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._regionManager Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/RegionManager;
      // 1c4: aload 0
      // 1c5: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._model Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel;
      // 1c8: checkcast net/rim/plazmic/internal/mediaengine/model/smil/v0_0/MMSModel
      // 1cb: invokespecial net/rim/plazmic/internal/mediaengine/model/smil/v0_0/EventInterceptor.<init> (Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/RegionManager;Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/MMSModel;)V
      // 1ce: astore 13
      // 1d0: aload 7
      // 1d2: bipush 1
      // 1d3: aload 13
      // 1d5: invokeinterface net/rim/plazmic/internal/mediaengine/service/EventResolver.setEventListener (ILjava/lang/Object;)V 3
      // 1da: aload 0
      // 1db: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._model Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel;
      // 1de: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel.hasAudio ()Z
      // 1e1: ifne 215
      // 1e4: aload 0
      // 1e5: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._model Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel;
      // 1e8: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel.hasAnimation ()Z
      // 1eb: ifne 215
      // 1ee: aload 0
      // 1ef: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._model Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel;
      // 1f2: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel.hasVideo ()Z
      // 1f5: ifne 215
      // 1f8: aload 12
      // 1fa: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/ui/SlideManager.getSlideCount ()I
      // 1fd: bipush 1
      // 1fe: if_icmpgt 215
      // 201: aload 0
      // 202: bipush 0
      // 203: putfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._restartable Z
      // 206: goto 215
      // 209: aload 7
      // 20b: bipush 1
      // 20c: aload 0
      // 20d: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._regionManager Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/RegionManager;
      // 210: invokeinterface net/rim/plazmic/internal/mediaengine/service/EventResolver.setEventListener (ILjava/lang/Object;)V 3
      // 215: aload 7
      // 217: bipush 2
      // 219: aload 0
      // 21a: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._regionManager Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/RegionManager;
      // 21d: invokeinterface net/rim/plazmic/internal/mediaengine/service/EventResolver.setEventListener (ILjava/lang/Object;)V 3
      // 222: aload 0
      // 223: new net/rim/device/apps/api/ui/VerbMenuItem
      // 226: dup
      // 227: new net/rim/plazmic/internal/mediaengine/model/smil/v0_0/verbs/SMILPauseVerb
      // 22a: dup
      // 22b: aload 0
      // 22c: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._player Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILPlayer;
      // 22f: invokespecial net/rim/plazmic/internal/mediaengine/model/smil/v0_0/verbs/SMILPauseVerb.<init> (Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILPlayer;)V
      // 232: bipush 101
      // 234: invokespecial net/rim/device/apps/api/ui/VerbMenuItem.<init> (Lnet/rim/device/apps/api/framework/verb/Verb;I)V
      // 237: putfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._pauseMenuItem Lnet/rim/device/apps/api/ui/VerbMenuItem;
      // 23a: aload 0
      // 23b: new net/rim/device/apps/api/ui/VerbMenuItem
      // 23e: dup
      // 23f: new net/rim/plazmic/internal/mediaengine/model/smil/v0_0/verbs/SMILResumeVerb
      // 242: dup
      // 243: aload 0
      // 244: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._player Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILPlayer;
      // 247: invokespecial net/rim/plazmic/internal/mediaengine/model/smil/v0_0/verbs/SMILResumeVerb.<init> (Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILPlayer;)V
      // 24a: bipush 101
      // 24c: invokespecial net/rim/device/apps/api/ui/VerbMenuItem.<init> (Lnet/rim/device/apps/api/framework/verb/Verb;I)V
      // 24f: putfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._resumeMenuItem Lnet/rim/device/apps/api/ui/VerbMenuItem;
      // 252: aload 0
      // 253: new net/rim/device/apps/api/ui/VerbMenuItem
      // 256: dup
      // 257: new net/rim/plazmic/internal/mediaengine/model/smil/v0_0/verbs/SMILRestartVerb
      // 25a: dup
      // 25b: aload 0
      // 25c: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._player Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILPlayer;
      // 25f: invokespecial net/rim/plazmic/internal/mediaengine/model/smil/v0_0/verbs/SMILRestartVerb.<init> (Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILPlayer;)V
      // 262: bipush 102
      // 264: invokespecial net/rim/device/apps/api/ui/VerbMenuItem.<init> (Lnet/rim/device/apps/api/framework/verb/Verb;I)V
      // 267: putfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._restartMenuItem Lnet/rim/device/apps/api/ui/VerbMenuItem;
      // 26a: aload 0
      // 26b: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._player Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILPlayer;
      // 26e: aload 0
      // 26f: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._model Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel;
      // 272: aload 0
      // 273: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._engine Lnet/rim/plazmic/internal/mediaengine/event/EventEngine;
      // 276: aload 0
      // 277: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._regionManager Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/RegionManager;
      // 27a: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILPlayer.realize (Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILModel;Lnet/rim/plazmic/internal/mediaengine/event/EventEngine;Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/RegionManager;)V
      // 27d: aload 0
      // 27e: getfield net/rim/plazmic/internal/mediaengine/model/smil/v0_0/plugin/SMILBrowserField._player Lnet/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILPlayer;
      // 281: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/SMILPlayer.startPlayback ()V
      // 284: aload 2
      // 285: ifnull 28e
      // 288: aload 2
      // 289: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 28e: aload 3
      // 28f: ifnull 30c
      // 292: aload 3
      // 293: invokevirtual java/io/InputStream.close ()V
      // 296: return
      // 297: astore 4
      // 299: return
      // 29a: astore 4
      // 29c: aload 3
      // 29d: ifnull 30c
      // 2a0: aload 3
      // 2a1: invokevirtual java/io/InputStream.close ()V
      // 2a4: return
      // 2a5: astore 4
      // 2a7: return
      // 2a8: astore 14
      // 2aa: aload 3
      // 2ab: ifnull 2b7
      // 2ae: aload 3
      // 2af: invokevirtual java/io/InputStream.close ()V
      // 2b2: goto 2b7
      // 2b5: astore 15
      // 2b7: aload 14
      // 2b9: athrow
      // 2ba: astore 4
      // 2bc: new net/rim/device/api/browser/field/RenderingException
      // 2bf: dup
      // 2c0: aload 4
      // 2c2: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 2c5: invokespecial net/rim/device/api/browser/field/RenderingException.<init> (Ljava/lang/String;)V
      // 2c8: athrow
      // 2c9: astore 16
      // 2cb: aload 2
      // 2cc: ifnull 2d5
      // 2cf: aload 2
      // 2d0: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2d5: aload 3
      // 2d6: ifnull 309
      // 2d9: aload 3
      // 2da: invokevirtual java/io/InputStream.close ()V
      // 2dd: goto 309
      // 2e0: astore 17
      // 2e2: goto 309
      // 2e5: astore 17
      // 2e7: aload 3
      // 2e8: ifnull 309
      // 2eb: aload 3
      // 2ec: invokevirtual java/io/InputStream.close ()V
      // 2ef: goto 309
      // 2f2: astore 17
      // 2f4: goto 309
      // 2f7: astore 18
      // 2f9: aload 3
      // 2fa: ifnull 306
      // 2fd: aload 3
      // 2fe: invokevirtual java/io/InputStream.close ()V
      // 301: goto 306
      // 304: astore 19
      // 306: aload 18
      // 308: athrow
      // 309: aload 16
      // 30b: athrow
      // 30c: return
      // try (36 -> 40): 41 null
      // try (36 -> 40): 55 null
      // try (65 -> 70): 71 null
      // try (283 -> 287): 288 null
      // try (279 -> 283): 290 null
      // try (291 -> 295): 296 null
      // try (279 -> 283): 298 null
      // try (290 -> 291): 298 null
      // try (299 -> 303): 304 null
      // try (298 -> 299): 298 null
      // try (11 -> 279): 307 null
      // try (11 -> 279): 314 null
      // try (319 -> 323): 324 null
      // try (315 -> 319): 326 null
      // try (327 -> 331): 332 null
      // try (315 -> 319): 334 null
      // try (326 -> 327): 334 null
      // try (335 -> 339): 340 null
      // try (334 -> 335): 334 null
      // try (307 -> 315): 314 null
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      switch (this._player.getState()) {
         case 0:
            break;
         case 1:
         default:
            menu.add(this._resumeMenuItem);
            break;
         case 2:
            menu.add(this._pauseMenuItem);
      }

      if (this._restartable && this._player.getState() != 0) {
         menu.add(this._restartMenuItem);
      }

      this._player.pausePlayback();
   }

   @Override
   protected final void onMenuDismissed(Menu menu) {
      if (this.isVisible()) {
         this._player.resumePlayback();
      }
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      if ((Keypad.status(keycode) & 1) == 1) {
         char c = (char)Keypad.key(keycode);
         switch (c) {
            case '\n':
               if (this._player.getState() == 1) {
                  this._player.resumePlayback();
                  return true;
               }

               if (this._player.getState() == 2) {
                  this._player.pausePlayback();
               }

               return true;
            case ' ':
               this._player.restartPlayback();
               return true;
         }
      }

      return super.keyDown(keycode, time);
   }

   @Override
   public final void onDisplay() {
      super.onDisplay();
      if (this._engine != null && this._engine.getTime() != 0) {
         this._player.restartPlayback();
         if (this.determineIfTemplate()) {
            this._player.pausePlayback();
         }
      }
   }

   private final boolean determineIfTemplate() {
      if (this._isTemplate != -1) {
         return this._isTemplate == 1;
      }

      for (Field f = this.getManager(); f != null; f = f.getManager()) {
         if (f instanceof MMSPresentationField) {
            MMSPresentationField mpf = (MMSPresentationField)f;
            if (mpf.hasAttachment("net_rim_Template")) {
               this._isTemplate = 1;
               return true;
            }
         }
      }

      this._isTemplate = 0;
      return false;
   }

   @Override
   public final void onUndisplay() {
      super.onUndisplay();
      this._player.close();
   }
}
