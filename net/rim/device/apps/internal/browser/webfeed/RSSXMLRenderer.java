package net.rim.device.apps.internal.browser.webfeed;

import java.io.InputStream;
import java.util.Stack;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.ContentReadEvent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.apps.internal.browser.markup.CharacterEntitiesParser;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.page.Renderer;
import net.rim.device.internal.browser.util.PipeContext;

final class RSSXMLRenderer extends Renderer {
   private StringBuffer _buffer = (StringBuffer)(new Object());
   private WebFeedField _webFeedField;
   private Stack _itemStack = (Stack)(new Object());
   private CharacterEntitiesParser _charEntities = (CharacterEntitiesParser)(new Object());
   private WebFeedItem _currentItem;
   private BrowserContentImpl _browserContent;
   private InputStream _in;
   private ContentReadEvent _contentRead;
   private PipeContext _pipePosition;
   private boolean _append;
   private static final int CHANNEL = 0;
   private static final int ITEM = 1;
   private static final int TITLE = 2;
   private static final int LINK = 3;
   private static final int DESCRIPTION = 4;
   private static final int PUB_DATE = 5;
   private static final int DATE = 6;
   private static final int GUID = 7;
   private static final int ENCLOSURE = 8;
   private static String[] TAGS = new String[]{"channel", "item", "title", "link", "description", "pubDate", "date", "guid", "enclosure"};

   public RSSXMLRenderer(HttpConnection connection, RenderingSession renderingSession, RenderingApplication renderingApplication, String referrer, int flags) {
      super(connection, renderingSession, renderingApplication, referrer, flags);
      this._webFeedField = new WebFeedField(renderingSession);
      this._browserContent = (BrowserContentImpl)(new Object(
         this, connection.getURL(), this._webFeedField, super._renderingApplication, super._renderingOptions, super._flags
      ));
      this._webFeedField.setBrowserContent(this._browserContent);
   }

   @Override
   public final BrowserContent processData() {
      return this._browserContent;
   }

   @Override
   public final void finishProcessingData() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/api/xml/parsers/SAXParserFactory.newInstance ()Lnet/rim/device/api/xml/parsers/SAXParserFactory;
      // 03: astore 1
      // 04: aload 1
      // 05: invokevirtual net/rim/device/api/xml/parsers/SAXParserFactory.newSAXParser ()Lnet/rim/device/api/xml/parsers/SAXParser;
      // 08: astore 2
      // 09: aload 2
      // 0a: bipush 1
      // 0b: invokevirtual net/rim/device/api/xml/parsers/SAXParser.setAllowUndefinedNamespaces (Z)V
      // 0e: aload 0
      // 0f: aload 0
      // 10: getfield net/rim/device/apps/internal/browser/page/Renderer._inputConnection Ljavax/microedition/io/InputConnection;
      // 13: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 18: putfield net/rim/device/apps/internal/browser/webfeed/RSSXMLRenderer._in Ljava/io/InputStream;
      // 1b: aload 0
      // 1c: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 1f: ifnull a1
      // 22: aload 0
      // 23: getfield net/rim/device/apps/internal/browser/webfeed/RSSXMLRenderer._in Ljava/io/InputStream;
      // 26: instanceof java/lang/Object
      // 29: ifeq a1
      // 2c: bipush 0
      // 2d: i2l
      // 2e: lstore 3
      // 2f: aload 0
      // 30: getfield net/rim/device/apps/internal/browser/page/Renderer._inputConnection Ljavax/microedition/io/InputConnection;
      // 33: dup
      // 34: instanceof java/lang/Object
      // 37: ifne 3e
      // 3a: pop
      // 3b: goto 47
      // 3e: checkcast java/lang/Object
      // 41: invokeinterface javax/microedition/io/ContentConnection.getLength ()J 1
      // 46: lstore 3
      // 47: aload 0
      // 48: aload 0
      // 49: getfield net/rim/device/apps/internal/browser/webfeed/RSSXMLRenderer._in Ljava/io/InputStream;
      // 4c: checkcast java/lang/Object
      // 4f: invokeinterface net/rim/device/internal/browser/util/PipeInput.getPosition ()Lnet/rim/device/internal/browser/util/PipeContext; 1
      // 54: putfield net/rim/device/apps/internal/browser/webfeed/RSSXMLRenderer._pipePosition Lnet/rim/device/internal/browser/util/PipeContext;
      // 57: lload 3
      // 58: bipush 0
      // 59: i2l
      // 5a: lcmp
      // 5b: ifle a1
      // 5e: aload 0
      // 5f: getfield net/rim/device/apps/internal/browser/webfeed/RSSXMLRenderer._pipePosition Lnet/rim/device/internal/browser/util/PipeContext;
      // 62: ifnull a1
      // 65: aload 0
      // 66: new java/lang/Object
      // 69: dup
      // 6a: aload 0
      // 6b: getfield net/rim/device/apps/internal/browser/page/Renderer._inputConnection Ljavax/microedition/io/InputConnection;
      // 6e: invokespecial net/rim/device/api/browser/field/ContentReadEvent.<init> (Ljava/lang/Object;)V
      // 71: putfield net/rim/device/apps/internal/browser/webfeed/RSSXMLRenderer._contentRead Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 74: aload 0
      // 75: getfield net/rim/device/apps/internal/browser/webfeed/RSSXMLRenderer._contentRead Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 78: lload 3
      // 79: l2i
      // 7a: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsToRead (I)V
      // 7d: aload 0
      // 7e: getfield net/rim/device/apps/internal/browser/webfeed/RSSXMLRenderer._contentRead Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 81: bipush 1
      // 82: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsToReadInBytes (Z)V
      // 85: aload 0
      // 86: getfield net/rim/device/apps/internal/browser/webfeed/RSSXMLRenderer._contentRead Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 89: aload 0
      // 8a: getfield net/rim/device/apps/internal/browser/webfeed/RSSXMLRenderer._pipePosition Lnet/rim/device/internal/browser/util/PipeContext;
      // 8d: getfield net/rim/device/internal/browser/util/PipeContext._numRead I
      // 90: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsRead (I)V
      // 93: aload 0
      // 94: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 97: aload 0
      // 98: getfield net/rim/device/apps/internal/browser/webfeed/RSSXMLRenderer._contentRead Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 9b: invokeinterface net/rim/device/api/browser/field/RenderingApplication.eventOccurred (Lnet/rim/device/api/browser/field/Event;)Ljava/lang/Object; 2
      // a0: pop
      // a1: aload 2
      // a2: aload 0
      // a3: getfield net/rim/device/apps/internal/browser/webfeed/RSSXMLRenderer._in Ljava/io/InputStream;
      // a6: new net/rim/device/apps/internal/browser/webfeed/RSSXMLRenderer$HandlerHelper
      // a9: dup
      // aa: aload 0
      // ab: aconst_null
      // ac: invokespecial net/rim/device/apps/internal/browser/webfeed/RSSXMLRenderer$HandlerHelper.<init> (Lnet/rim/device/apps/internal/browser/webfeed/RSSXMLRenderer;Lnet/rim/device/apps/internal/browser/webfeed/RSSXMLRenderer$1;)V
      // af: invokevirtual net/rim/device/api/xml/parsers/SAXParser.parse (Ljava/io/InputStream;Lorg/xml/sax/helpers/DefaultHandler;)V
      // b2: invokestatic net/rim/device/apps/internal/browser/webfeed/WebFeedStatusStore.getInstance ()Lnet/rim/device/apps/internal/browser/webfeed/WebFeedStatusStore;
      // b5: aload 0
      // b6: getfield net/rim/device/apps/internal/browser/page/Renderer._inputConnection Ljavax/microedition/io/InputConnection;
      // b9: checkcast java/lang/Object
      // bc: invokeinterface javax/microedition/io/HttpConnection.getURL ()Ljava/lang/String; 1
      // c1: aload 0
      // c2: getfield net/rim/device/apps/internal/browser/webfeed/RSSXMLRenderer._webFeedField Lnet/rim/device/apps/internal/browser/webfeed/WebFeedField;
      // c5: invokevirtual net/rim/device/apps/internal/browser/webfeed/WebFeedField.getItems ()[Lnet/rim/device/apps/internal/browser/webfeed/WebFeedItem;
      // c8: invokevirtual net/rim/device/apps/internal/browser/webfeed/WebFeedStatusStore.setWebFeedGuids (Ljava/lang/String;[Lnet/rim/device/apps/internal/browser/webfeed/WebFeedItem;)V
      // cb: return
      // cc: astore 1
      // cd: return
      // ce: astore 1
      // cf: return
      // d0: astore 1
      // d1: return
      // try (0 -> 93): 94 null
      // try (0 -> 93): 96 null
      // try (0 -> 93): 98 null
   }

   private final int top() {
      return this._itemStack.size() == 0 ? -1 : this.getTag((String)this._itemStack.peek());
   }

   private final int getTag(String name) {
      for (int i = TAGS.length - 1; i >= 0; i--) {
         if (TAGS[i].equals(name)) {
            return i;
         }
      }

      return -1;
   }

   private final String getCurrentString() {
      String result = this._buffer.toString();
      this._buffer.setLength(0);
      return result;
   }

   static final RenderingApplication access$500(RSSXMLRenderer x0) {
      return x0._renderingApplication;
   }

   static final RenderingApplication access$1400(RSSXMLRenderer x0) {
      return x0._renderingApplication;
   }

   static final RenderingApplication access$1500(RSSXMLRenderer x0) {
      return x0._renderingApplication;
   }
}
