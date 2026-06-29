package net.rim.device.apps.internal.browser.html;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.ContentReadEvent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.browser.field.SetHeaderEvent;
import net.rim.device.api.crypto.HMAC;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntStack;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.MultiMap;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.device.apps.internal.api.quincy.QuincyManager;
import net.rim.device.apps.internal.browser.api.DataModificationEvent;
import net.rim.device.apps.internal.browser.api.DeviceDataConversionEvent;
import net.rim.device.apps.internal.browser.api.LoadingStatusEvent;
import net.rim.device.apps.internal.browser.api.UIDirectionChangeEvent;
import net.rim.device.apps.internal.browser.common.RenderingUtilities;
import net.rim.device.apps.internal.browser.core.SecondaryURLNode;
import net.rim.device.apps.internal.browser.css.CSSBinaryParser;
import net.rim.device.apps.internal.browser.css.CSSParser;
import net.rim.device.apps.internal.browser.css.CSSString;
import net.rim.device.apps.internal.browser.css.CSSTextParser;
import net.rim.device.apps.internal.browser.css.DocumentHandler;
import net.rim.device.apps.internal.browser.javascript.JavaScriptInterpreter;
import net.rim.device.apps.internal.browser.javascript.JavaScriptRegistry;
import net.rim.device.apps.internal.browser.markup.HTMLBinaryConstants;
import net.rim.device.apps.internal.browser.page.PageTimer;
import net.rim.device.apps.internal.browser.page.Renderer;
import net.rim.device.apps.internal.browser.page.RendererImageContainer;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.HeaderParser;
import net.rim.device.apps.internal.browser.stack.WAPInputStream;
import net.rim.device.apps.internal.browser.ui.BrowserBitmapField;
import net.rim.device.apps.internal.browser.ui.BrowserLinkBitmapField;
import net.rim.device.apps.internal.browser.ui.BrowserListItemBitmapField;
import net.rim.device.apps.internal.browser.ui.ElementBorder;
import net.rim.device.apps.internal.browser.ui.FrameManager;
import net.rim.device.apps.internal.browser.ui.TextFlowManager;
import net.rim.device.apps.internal.browser.util.Asserts;
import net.rim.device.apps.internal.browser.util.FontCache;
import net.rim.device.apps.internal.browser.util.Frame;
import net.rim.device.apps.internal.browser.util.ImageMap;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.apps.internal.browser.verbs.EventVerb;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.device.internal.browser.util.Pipe;
import net.rim.device.internal.browser.util.PipePtr;
import net.rim.device.internal.browser.util.TimeLogger;
import net.rim.device.internal.ui.Border;
import net.rim.vm.Array;
import org.w3c.dom.Node;
import org.w3c.dom.html2.HTMLAnchorElement;
import org.w3c.dom.html2.HTMLAreaElement;
import org.w3c.dom.html2.HTMLElement;

final class HTMLRendererWithTextFlow extends Renderer implements DocumentHandler {
   private Stack _tagStack;
   private Stack _tagClasses;
   private Stack _tagIds;
   private int _lastDirection;
   private String _lastClass;
   private String _lastId;
   private String _lastName;
   private String _lastStyle;
   private int _lastListStyleType;
   private String _lastListStyleImage;
   private int _skipElementStackValue;
   private IntStack _skipPopCalls;
   private HTMLRendererContext _currentContext;
   private HTMLForm _currentForm;
   private boolean _processStyleSheets;
   private boolean _validStyleSheet;
   private String _defaultStyle;
   private CSSParser _cssParser;
   private int _styleCount;
   private int[] _elementStyles;
   private ToIntHashtable[] _classStyles;
   private ToIntHashtable[] _idStyles;
   private ToIntHashtable[] _descendantStyles;
   private ToIntHashtable[] _descendantElementStyles;
   private Hashtable[] _descendantClassStyles;
   private Hashtable[] _descendantIdStyles;
   private Vector[] _addedDescendantStyles;
   private boolean _descendantStylesEncountered;
   private Vector _styleObjects;
   private int _styleFlagCount;
   private long[] _styleFlagsValues;
   private short[] _styleTops;
   private long[] _styleSortBuffer = new long[10];
   private int _numAttributes;
   private int[] _attributeValuesInts;
   private Object[] _attributeValues;
   private byte[] _attributeItems;
   private int _currentStyleIndex = -1;
   private int _styleSheetDepth;
   private IntStack _styleStack;
   private IntStack _cellPushed;
   private Stack _tableStack;
   private HTMLBrowserField _currentBrowserField;
   private HTMLBrowserContent _browserContent;
   private HTMLDocumentImpl _document;
   private HTMLInput _currentFormControl;
   private Hashtable _currentRadioGroups;
   private String _url;
   private HTMLSelect _currentSelect;
   private long _fieldStyle = 4294967296L;
   private Hashtable _imageMaps;
   private ImageMap _currentImageMap;
   private TextUtilities _textUtilities;
   private String _onLoadScript;
   private int _inputElementCount;
   private int _numCharsAppended;
   private int _guessedHeight;
   private int _guessedHeightLimit;
   private boolean _fieldsPending;
   private Vector _scriptsToExecute;
   private HTMLBaseGenericElement _coreDocElement;
   private int _blockLevelBreakFlags;
   private boolean _breakAppended;
   private int _layoutCharAppendLimit;
   private int _imageLayoutLimit;
   private Hashtable _inlineData;
   private IntHashtable _inlineFragments;
   private int _totalInlineFragments;
   private int _inlineFramgnetsReceived;
   private boolean _loadingInlineFragments;
   private MultiMap _progressiveImageMap;
   private boolean _loadingInlineImages;
   private int _imageLayoutCount;
   private int[] _trimOffsets;
   private int[] _trimLengths;
   private JavaScriptInterpreter _scriptEngine;
   private HTMLContext _htmlContext;
   private IntHashtable _inlineDataRefs;
   private Object _appEventLock;
   private int _nextTickThreshold;
   private int _currentPriorityCount;
   private boolean _inA;
   private boolean _inFirstQ;
   private HTMLAnchor _currentAnchor;
   private ContentReadEvent _contentReadEvent;
   private LoadingStatusEvent _loadingStatusEvent;
   private boolean _framesetDocument;
   private Frame _frame;
   private int[][][] _cssTempArray = new int[3][11][];
   private HMAC _subDataMac;
   private boolean _useColor;
   DeviceDataConversionEvent _convEvent;
   private boolean _useApproximatedTicks;
   private boolean _titleSet;
   private String _iconUrl;
   private static final int PROGRESS_UPDATE_TRIGGER = 100;
   private static final int PROGRESS_UPDATE_TRIGGER_FAST = 5;
   private static final int PROGRESS_UPDATE_DATA_SIZE = 4096;
   private static final boolean RETURN_ONLY = false;
   private static final int DISPLAY_WIDTH = Display.getWidth();
   private static final int DISPLAY_HEIGHT = Display.getHeight();
   private static final int INDENT_SIZE = 10;
   private static final boolean IS_COLOR = Display.isColor();
   private static final int MAX_EMBEDDING_DEPTH = 6;
   private static final Border PLACEHOLDER_BORDER = (Border)(new Object(1, 1, 1, 1));
   private static final int CSS_CONTEXT_TEXT = 0;
   private static final int CSS_CONTEXT_BODY = 1;
   private static final int CSS_CONTEXT_TABLE = 2;
   private static final int CSS_CONTEXT_INPUT = 3;
   private static final int CSS_CONTEXT_BLOCK = 4;
   private static final int MAX_TAG_STACK_DEPTH = 200;
   private static final long ITEM_ID_MASK = 511L;
   private static final int ITEM_ID_SHIFT = 55;
   private static final int FLAG_SHIFT = 32;
   private static final int FLAG_MASK = 8388607;
   private static final long VALUE_MASK = 4294967295L;
   private static final String PSEUDO_CLASS_LINK_STRING = ":link";
   private static final CSSString PSEUDO_CLASS_LINK_CSSSTRING = new CSSString(":link", 0, 5);
   public static final int BORDER_LEFT = 1;
   public static final int BORDER_RIGHT = 2;
   public static final int BORDER_TOP = 4;
   public static final int BORDER_BOTTOM = 8;
   public static final int ALL_BORDERS = 15;
   private static String INLINE_DATA_304 = "304";
   private static String SINGLE_SPACE = " ";

   public final Frame getFrame() {
      return this._frame;
   }

   final void activateLayoutIfFieldsPending() {
      if (this._fieldsPending) {
         this.activateLayout();
      }
   }

   protected final EncodedImage findEncodedImageInCache(String url, int navigation, HTMLElement htmlElement) {
      Object object = this._inlineData.get(url);
      if (object instanceof RendererImageContainer) {
         int desiredHeight = -1;
         int desiredWidth = -1;
         RendererImageContainer container = (RendererImageContainer)object;
         if (htmlElement instanceof HTMLGenericElement) {
            HTMLGenericElement element = (HTMLGenericElement)htmlElement;
            desiredWidth = element.getAttributeValueAsInt(198);
            desiredHeight = element.getAttributeValueAsInt(124);
            if (desiredHeight == -1 && desiredWidth == -1) {
               element.setAttributeValue(124, container._height);
               element.setAttributeValue(198, container._width);
            }
         }

         return container._image;
      } else {
         boolean returnExpired = INLINE_DATA_304.equals(object);
         if ((navigation & 7) == 5) {
            navigation &= -8;
            navigation |= 1;
         }

         return super.findEncodedImageInCache(url, navigation | (returnExpired ? 32768 : 0), (SecondaryURLNode)htmlElement, this._browserContent);
      }
   }

   final void resetBlockAndBreak() {
      this._blockLevelBreakFlags = 0;
      this._breakAppended = false;
   }

   @Override
   public final void endDocument(String uri) {
      if (--this._styleSheetDepth == 0) {
         for (int i = 0; i < this._tagStack.size() - 1; i++) {
            this.findDescendantStyles(
               ((HTMLGenericElement)this._tagStack.elementAt(i)).getTagNameInt(), (String)this._tagClasses.elementAt(i), (String)this._tagIds.elementAt(i), i
            );
         }
      }

      this._currentStyleIndex = -1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void importStyle(String uri, String[] media) {
      boolean valid = true;
      if (media.length > 0) {
         valid = false;

         for (int i = 0; i < media.length; i++) {
            if (this.isValidStyleSheet(null, media[i], null)) {
               valid = true;
               break;
            }
         }
      }

      if (valid) {
         uri = this._browserContent.resolveUrl(uri);
         InlineDataRefHolder styleSheet = this.loadExternalStyleSheet(uri);
         this.loadingStatusEventOccurred(4);
         if (styleSheet != null) {
            if (StringUtilities.strEqualIgnoreCase(styleSheet._str, "text/css", 1701707776)) {
               String textStyleSheet;
               if (styleSheet._encoding == null) {
                  textStyleSheet = (String)(new Object(styleSheet._data, styleSheet._offset, styleSheet._length));
               } else {
                  boolean var8 = false /* VF: Semaphore variable */;

                  label52:
                  try {
                     var8 = true;
                     textStyleSheet = (String)(new Object(styleSheet._data, styleSheet._offset, styleSheet._length, styleSheet._encoding));
                     var8 = false;
                  } finally {
                     if (var8) {
                        textStyleSheet = (String)(new Object(styleSheet._data, styleSheet._offset, styleSheet._length));
                        break label52;
                     }
                  }
               }

               this.parseTextStyleSheet(textStyleSheet, uri);
               return;
            }

            if (StringUtilities.strEqualIgnoreCase(styleSheet._str, "application/vnd.rim.css", 1701707776)) {
               this.parseBinaryStyleSheet(Arrays.copy(styleSheet._data, styleSheet._offset, styleSheet._length), uri);
            }
         }
      }
   }

   @Override
   public final boolean startMedia(String[] media) {
      for (int i = 0; i < media.length; i++) {
         if (this.isValidStyleSheet(null, media[i], null)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final void endMedia(String[] media) {
   }

   @Override
   public final void startSelector(int[][][] selectors, int length) {
      this._currentStyleIndex = -1;
   }

   @Override
   public final void endSelector(int[][][] selectors, int selectorsLength) {
      throw new RuntimeException("cod2jar: array load: unknown element");
   }

   @Override
   public final void property(int name, int[] value, boolean important) {
      this.property(-1, name, value, value[value.length - 1], important);
   }

   @Override
   public final void startDocument(String uri) {
      this._styleSheetDepth++;
      this._currentStyleIndex = -1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void finishProcessingData() {
      this._contentReadEvent = (ContentReadEvent)(new Object(this._browserContent));
      this._browserContent.setScriptEngine(this._scriptEngine);
      if (this._scriptEngine != null) {
         this._scriptEngine.documentCreated(this._document, this._browserContent, super._renderingSession, this._frame);
      }

      synchronized (this._appEventLock) {
         this._currentBrowserField.setLayoutActive(false);
         this._currentBrowserField.pushRegion(this._coreDocElement);
      }

      if (this._iconUrl != null) {
         EncodedImage img = this.findEncodedImageInCache(this._iconUrl, super._flags & 0xFF, null);
         this._browserContent.setIconUrl(this._iconUrl);
         if (img != null) {
            this._browserContent.setIcon(img);
         }
      }

      boolean var13 = false /* VF: Semaphore variable */;

      try {
         var13 = true;
         this.parseData();
         if (this._convEvent != null && super._renderingApplication != null) {
            super._renderingApplication.eventOccurred(this._convEvent);
         }

         int numScripts = this._scriptsToExecute.size();

         for (int i = 0; i < numScripts; i++) {
            try {
               this.executeScript((JavaScriptItem)this._scriptsToExecute.elementAt(i));
            } finally {
               continue;
            }
         }

         var13 = false;
      } finally {
         if (var13) {
            synchronized (this._appEventLock) {
               this.appendCurrentString(this._coreDocElement);
               this._currentBrowserField.popRegion();
               this._currentBrowserField.setLayoutActive(true);
               this.activateLayoutInParent();
            }

            if (this._scriptEngine != null) {
               this._scriptEngine.documentLoaded(this._document, this._onLoadScript);
            }

            this.cleanup();
         }
      }

      synchronized (this._appEventLock) {
         this.appendCurrentString(this._coreDocElement);
         this._currentBrowserField.popRegion();
         this._currentBrowserField.setLayoutActive(true);
         this.activateLayoutInParent();
      }

      if (this._scriptEngine != null) {
         this._scriptEngine.documentLoaded(this._document, this._onLoadScript);
      }

      this.cleanup();
   }

   private final void parseData() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush -1
      // 002: istore 2
      // 003: aload 0
      // 004: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentContext Lnet/rim/device/apps/internal/browser/html/HTMLRendererContext;
      // 007: astore 3
      // 008: aconst_null
      // 009: astore 4
      // 00b: aload 3
      // 00c: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._in Lnet/rim/device/apps/internal/browser/stack/WAPInputStream;
      // 00f: astore 5
      // 011: aload 5
      // 013: invokevirtual java/io/DataInputStream.readUnsignedByte ()I
      // 016: bipush 16
      // 018: if_icmpeq 052
      // 01b: aload 0
      // 01c: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._appEventLock Ljava/lang/Object;
      // 01f: dup
      // 020: astore 6
      // 022: monitorenter
      // 023: aload 0
      // 024: aload 0
      // 025: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._coreDocElement Lnet/rim/device/apps/internal/browser/html/HTMLBaseGenericElement;
      // 028: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.appendCurrentString (Lnet/rim/device/apps/internal/browser/html/HTMLGenericElement;)V
      // 02b: aload 3
      // 02c: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 02f: ifle 046
      // 032: aload 0
      // 033: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentBrowserField Lnet/rim/device/apps/internal/browser/html/HTMLBrowserField;
      // 036: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowManager.popCell ()V
      // 039: aload 3
      // 03a: dup
      // 03b: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 03e: bipush 1
      // 03f: isub
      // 040: putfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 043: goto 02b
      // 046: aload 6
      // 048: monitorexit
      // 049: return
      // 04a: astore 7
      // 04c: aload 6
      // 04e: monitorexit
      // 04f: aload 7
      // 051: athrow
      // 052: bipush 0
      // 053: istore 6
      // 055: aload 5
      // 057: invokevirtual java/io/DataInputStream.read ()I
      // 05a: dup
      // 05b: istore 1
      // 05c: ifne 062
      // 05f: goto 314
      // 062: iload 1
      // 063: lookupswitch 681 10 1 102 2 117 3 186 4 240 5 301 6 340 7 444 8 625 9 643 255 89
      // 0bc: aload 5
      // 0be: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.skipByteArray ()V
      // 0c1: aload 5
      // 0c3: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.skipByteArray ()V
      // 0c6: goto 055
      // 0c9: aload 5
      // 0cb: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 0ce: pop
      // 0cf: aload 5
      // 0d1: invokevirtual java/io/DataInputStream.readInt ()I
      // 0d4: pop
      // 0d5: goto 055
      // 0d8: aload 5
      // 0da: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 0dd: pop
      // 0de: aload 5
      // 0e0: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 0e3: istore 7
      // 0e5: iload 7
      // 0e7: bipush 1
      // 0e8: iand
      // 0e9: ifeq 0f1
      // 0ec: aload 3
      // 0ed: bipush 0
      // 0ee: putfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._appendNewLine Z
      // 0f1: iload 7
      // 0f3: bipush 4
      // 0f5: iand
      // 0f6: ifeq 10a
      // 0f9: aload 3
      // 0fa: bipush 0
      // 0fb: putfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._appendNewLine Z
      // 0fe: aload 3
      // 0ff: bipush 1
      // 100: putfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._allTagsProvided Z
      // 103: aload 0
      // 104: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // 107: invokevirtual net/rim/device/apps/internal/browser/html/HTMLBrowserContent.setAllTagsProvided ()V
      // 10a: iload 7
      // 10c: bipush 8
      // 10e: iand
      // 10f: ifne 115
      // 112: goto 055
      // 115: aload 3
      // 116: bipush 1
      // 117: putfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._transcodedOnDevice Z
      // 11a: goto 055
      // 11d: aload 5
      // 11f: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 122: pop
      // 123: aload 5
      // 125: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 128: istore 8
      // 12a: aload 0
      // 12b: getfield net/rim/device/apps/internal/browser/page/Renderer._postEncoding Ljava/lang/String;
      // 12e: ifnull 134
      // 131: goto 055
      // 134: aload 0
      // 135: iload 8
      // 137: invokestatic com/fourthpass/wapstack/wsp/WSPHeaderDecoder.getCharsetName (I)Ljava/lang/String;
      // 13a: putfield net/rim/device/apps/internal/browser/page/Renderer._postEncoding Ljava/lang/String;
      // 13d: iload 8
      // 13f: bipush 11
      // 141: if_icmpeq 147
      // 144: goto 055
      // 147: aload 0
      // 148: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._coreDocElement Lnet/rim/device/apps/internal/browser/html/HTMLBaseGenericElement;
      // 14b: bipush 16
      // 14d: invokevirtual net/rim/device/internal/ui/TextFlowRegion.setAlignment (S)V
      // 150: goto 055
      // 153: aload 5
      // 155: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readByteArray ()[B
      // 158: astore 8
      // 15a: aload 0
      // 15b: getfield net/rim/device/apps/internal/browser/page/Renderer._postEncoding Ljava/lang/String;
      // 15e: ifnull 164
      // 161: goto 055
      // 164: aload 0
      // 165: new java/lang/Object
      // 168: dup
      // 169: aload 8
      // 16b: invokespecial java/lang/String.<init> ([B)V
      // 16e: putfield net/rim/device/apps/internal/browser/page/Renderer._postEncoding Ljava/lang/String;
      // 171: aload 0
      // 172: getfield net/rim/device/apps/internal/browser/page/Renderer._postEncoding Ljava/lang/String;
      // 175: ldc_w "iso-8859-8"
      // 178: ldc_w 1701707776
      // 17b: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 17e: ifne 184
      // 181: goto 055
      // 184: aload 0
      // 185: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._coreDocElement Lnet/rim/device/apps/internal/browser/html/HTMLBaseGenericElement;
      // 188: bipush 16
      // 18a: invokevirtual net/rim/device/internal/ui/TextFlowRegion.setAlignment (S)V
      // 18d: goto 055
      // 190: aload 5
      // 192: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 195: pop
      // 196: aload 5
      // 198: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 19b: istore 8
      // 19d: iload 8
      // 19f: bipush 2
      // 1a1: if_icmpeq 1a7
      // 1a4: goto 055
      // 1a7: aload 3
      // 1a8: new net/rim/device/apps/internal/browser/stack/LZSSInputStream
      // 1ab: dup
      // 1ac: aload 5
      // 1ae: invokespecial net/rim/device/apps/internal/browser/stack/LZSSInputStream.<init> (Ljava/io/InputStream;)V
      // 1b1: putfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._compressedStrings Ljava/io/InputStream;
      // 1b4: goto 055
      // 1b7: aload 5
      // 1b9: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 1bc: pop
      // 1bd: aload 5
      // 1bf: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 1c2: istore 8
      // 1c4: iload 8
      // 1c6: bipush 1
      // 1c7: if_icmpeq 1cd
      // 1ca: goto 055
      // 1cd: aconst_null
      // 1ce: astore 9
      // 1d0: aload 0
      // 1d1: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingOptions Lnet/rim/device/api/browser/field/RenderingOptions;
      // 1d4: ifnull 1eb
      // 1d7: aload 0
      // 1d8: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingOptions Lnet/rim/device/api/browser/field/RenderingOptions;
      // 1db: ldc2_w 4550690918222697397
      // 1de: bipush 33
      // 1e0: getstatic net/rim/device/api/browser/field/RenderingOptions.DEFAULT_HMAC Ljava/lang/Object;
      // 1e3: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithObjectValue (JILjava/lang/Object;)Ljava/lang/Object;
      // 1e6: checkcast [B
      // 1e9: astore 9
      // 1eb: aload 9
      // 1ed: ifnonnull 1f3
      // 1f0: goto 055
      // 1f3: aload 0
      // 1f4: getfield net/rim/device/apps/internal/browser/page/Renderer._flags I
      // 1f7: sipush 4096
      // 1fa: iand
      // 1fb: ifeq 201
      // 1fe: goto 055
      // 201: aload 0
      // 202: new java/lang/Object
      // 205: dup
      // 206: new java/lang/Object
      // 209: dup
      // 20a: aload 9
      // 20c: invokespecial net/rim/device/api/crypto/HMACKey.<init> ([B)V
      // 20f: new java/lang/Object
      // 212: dup
      // 213: invokespecial net/rim/device/api/crypto/SHA1Digest.<init> ()V
      // 216: invokespecial net/rim/device/api/crypto/HMAC.<init> (Lnet/rim/device/api/crypto/HMACKey;Lnet/rim/device/api/crypto/Digest;)V
      // 219: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._subDataMac Lnet/rim/device/api/crypto/HMAC;
      // 21c: goto 055
      // 21f: aload 5
      // 221: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.getPosition ()Lnet/rim/device/internal/browser/util/PipeContext;
      // 224: astore 8
      // 226: aload 8
      // 228: ifnull 235
      // 22b: aload 8
      // 22d: getfield net/rim/device/internal/browser/util/PipeContext._numRead I
      // 230: bipush 1
      // 231: isub
      // 232: goto 236
      // 235: bipush 0
      // 236: istore 9
      // 238: aload 5
      // 23a: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 23d: pop
      // 23e: aload 5
      // 240: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 243: istore 10
      // 245: iload 10
      // 247: ifle 26c
      // 24a: aload 5
      // 24c: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.getPosition ()Lnet/rim/device/internal/browser/util/PipeContext;
      // 24f: astore 4
      // 251: aload 4
      // 253: ifnull 26c
      // 256: aload 0
      // 257: bipush 0
      // 258: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._useApproximatedTicks Z
      // 25b: aload 0
      // 25c: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 25f: iload 10
      // 261: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsToRead (I)V
      // 264: aload 0
      // 265: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 268: bipush 1
      // 269: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsToReadInBytes (Z)V
      // 26c: aload 5
      // 26e: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 271: istore 11
      // 273: iload 11
      // 275: ifle 281
      // 278: aload 0
      // 279: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // 27c: iload 11
      // 27e: invokevirtual net/rim/device/apps/internal/browser/page/BrowserContentImpl.setImagesToLoad (I)V
      // 281: aload 5
      // 283: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.getPosition ()Lnet/rim/device/internal/browser/util/PipeContext;
      // 286: astore 8
      // 288: aload 8
      // 28a: ifnull 298
      // 28d: aload 8
      // 28f: getfield net/rim/device/internal/browser/util/PipeContext._numRead I
      // 292: iload 9
      // 294: isub
      // 295: goto 299
      // 298: bipush 0
      // 299: istore 12
      // 29b: aload 0
      // 29c: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._subDataMac Lnet/rim/device/api/crypto/HMAC;
      // 29f: ifnull 2ce
      // 2a2: iload 12
      // 2a4: ifle 2ce
      // 2a7: aload 0
      // 2a8: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimOffsets [I
      // 2ab: ifnonnull 2bc
      // 2ae: aload 0
      // 2af: bipush 0
      // 2b0: newarray 10
      // 2b2: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimOffsets [I
      // 2b5: aload 0
      // 2b6: bipush 0
      // 2b7: newarray 10
      // 2b9: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimLengths [I
      // 2bc: aload 0
      // 2bd: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimOffsets [I
      // 2c0: iload 9
      // 2c2: invokestatic net/rim/device/api/util/Arrays.add ([II)V
      // 2c5: aload 0
      // 2c6: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimLengths [I
      // 2c9: iload 12
      // 2cb: invokestatic net/rim/device/api/util/Arrays.add ([II)V
      // 2ce: bipush 1
      // 2cf: istore 6
      // 2d1: goto 055
      // 2d4: aload 5
      // 2d6: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 2d9: pop
      // 2da: aload 3
      // 2db: aload 5
      // 2dd: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 2e0: putfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._markupMinorVersion I
      // 2e3: goto 055
      // 2e6: new java/lang/Object
      // 2e9: dup
      // 2ea: aload 5
      // 2ec: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readByteArray ()[B
      // 2ef: invokespecial java/lang/String.<init> ([B)V
      // 2f2: astore 8
      // 2f4: ldc_w "application/vnd.wap.xhtml+xml"
      // 2f7: aload 8
      // 2f9: ldc_w 1701707776
      // 2fc: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 2ff: ifne 305
      // 302: goto 055
      // 305: aload 0
      // 306: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.adjustViewModeForMobileContent ()V
      // 309: goto 055
      // 30c: aload 5
      // 30e: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.skipByteArray ()V
      // 311: goto 055
      // 314: aload 3
      // 315: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pipe Lnet/rim/device/internal/browser/util/Pipe;
      // 318: ifnonnull 359
      // 31b: iload 6
      // 31d: ifne 359
      // 320: aload 5
      // 322: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.getPipe ()Lnet/rim/device/internal/browser/util/Pipe;
      // 325: astore 7
      // 327: aload 7
      // 329: ifnull 359
      // 32c: aload 7
      // 32e: invokevirtual net/rim/device/internal/browser/util/Pipe.isClosed ()Z
      // 331: ifeq 359
      // 334: aload 5
      // 336: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.getPosition ()Lnet/rim/device/internal/browser/util/PipeContext;
      // 339: astore 4
      // 33b: aload 4
      // 33d: ifnull 359
      // 340: aload 0
      // 341: bipush 0
      // 342: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._useApproximatedTicks Z
      // 345: aload 0
      // 346: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 349: aload 7
      // 34b: invokevirtual net/rim/device/internal/browser/util/Pipe.getLength ()I
      // 34e: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsToRead (I)V
      // 351: aload 0
      // 352: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 355: bipush 1
      // 356: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsToReadInBytes (Z)V
      // 359: aload 3
      // 35a: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._allTagsProvided Z
      // 35d: ifne 36e
      // 360: aload 3
      // 361: new java/lang/Object
      // 364: dup
      // 365: sipush 150
      // 368: invokespecial net/rim/device/api/util/IntHashtable.<init> (I)V
      // 36b: putfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._richTextProperties Lnet/rim/device/api/util/IntHashtable;
      // 36e: aload 0
      // 36f: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._useApproximatedTicks Z
      // 372: ifeq 37b
      // 375: aload 0
      // 376: bipush 100
      // 378: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._nextTickThreshold I
      // 37b: aload 5
      // 37d: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 380: pop
      // 381: aload 5
      // 383: invokevirtual java/io/DataInputStream.read ()I
      // 386: dup
      // 387: istore 2
      // 388: bipush -1
      // 38a: if_icmpne 390
      // 38d: goto 662
      // 390: aload 4
      // 392: ifnonnull 398
      // 395: goto 4ba
      // 398: aload 0
      // 399: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 39c: ifnonnull 3a2
      // 39f: goto 4ba
      // 3a2: aload 0
      // 3a3: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._loadingInlineFragments Z
      // 3a6: ifeq 402
      // 3a9: aload 0
      // 3aa: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._inlineFramgnetsReceived I
      // 3ad: aload 0
      // 3ae: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._nextTickThreshold I
      // 3b1: if_icmpge 3b7
      // 3b4: goto 508
      // 3b7: aload 0
      // 3b8: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentBrowserField Lnet/rim/device/apps/internal/browser/html/HTMLBrowserField;
      // 3bb: invokevirtual net/rim/device/apps/internal/browser/html/HTMLBrowserField.isDestroyed ()Z
      // 3be: ifne 3c5
      // 3c1: bipush 1
      // 3c2: goto 3c6
      // 3c5: bipush 0
      // 3c6: invokestatic net/rim/device/apps/internal/browser/util/Asserts.productionUserAbortAssert (Z)V
      // 3c9: aload 0
      // 3ca: aload 0
      // 3cb: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._inlineFramgnetsReceived I
      // 3ce: bipush 1
      // 3cf: iadd
      // 3d0: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._nextTickThreshold I
      // 3d3: aload 0
      // 3d4: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 3d7: aload 0
      // 3d8: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._totalInlineFragments I
      // 3db: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsToRead (I)V
      // 3de: aload 0
      // 3df: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 3e2: aload 0
      // 3e3: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._inlineFramgnetsReceived I
      // 3e6: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsRead (I)V
      // 3e9: aload 0
      // 3ea: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 3ed: bipush 0
      // 3ee: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsToReadInBytes (Z)V
      // 3f1: aload 0
      // 3f2: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 3f5: aload 0
      // 3f6: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 3f9: invokeinterface net/rim/device/api/browser/field/RenderingApplication.eventOccurred (Lnet/rim/device/api/browser/field/Event;)Ljava/lang/Object; 2
      // 3fe: pop
      // 3ff: goto 508
      // 402: aload 0
      // 403: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._loadingInlineImages Z
      // 406: ifeq 474
      // 409: aload 0
      // 40a: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // 40d: invokevirtual net/rim/device/apps/internal/browser/page/BrowserContentImpl.getImagesLoaded ()I
      // 410: istore 7
      // 412: iload 7
      // 414: aload 0
      // 415: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._nextTickThreshold I
      // 418: if_icmpge 41e
      // 41b: goto 508
      // 41e: aload 0
      // 41f: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentBrowserField Lnet/rim/device/apps/internal/browser/html/HTMLBrowserField;
      // 422: invokevirtual net/rim/device/apps/internal/browser/html/HTMLBrowserField.isDestroyed ()Z
      // 425: ifne 42c
      // 428: bipush 1
      // 429: goto 42d
      // 42c: bipush 0
      // 42d: invokestatic net/rim/device/apps/internal/browser/util/Asserts.productionUserAbortAssert (Z)V
      // 430: aload 0
      // 431: iload 7
      // 433: bipush 1
      // 434: iadd
      // 435: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._nextTickThreshold I
      // 438: aload 0
      // 439: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // 43c: invokevirtual net/rim/device/apps/internal/browser/page/BrowserContentImpl.getImagesToLoad ()I
      // 43f: istore 8
      // 441: iload 8
      // 443: ifgt 449
      // 446: goto 508
      // 449: aload 0
      // 44a: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 44d: iload 8
      // 44f: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsToRead (I)V
      // 452: aload 0
      // 453: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 456: iload 7
      // 458: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsRead (I)V
      // 45b: aload 0
      // 45c: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 45f: bipush 0
      // 460: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsToReadInBytes (Z)V
      // 463: aload 0
      // 464: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 467: aload 0
      // 468: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 46b: invokeinterface net/rim/device/api/browser/field/RenderingApplication.eventOccurred (Lnet/rim/device/api/browser/field/Event;)Ljava/lang/Object; 2
      // 470: pop
      // 471: goto 508
      // 474: aload 4
      // 476: getfield net/rim/device/internal/browser/util/PipeContext._numRead I
      // 479: istore 7
      // 47b: iload 7
      // 47d: aload 0
      // 47e: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._nextTickThreshold I
      // 481: if_icmplt 508
      // 484: aload 0
      // 485: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentBrowserField Lnet/rim/device/apps/internal/browser/html/HTMLBrowserField;
      // 488: invokevirtual net/rim/device/apps/internal/browser/html/HTMLBrowserField.isDestroyed ()Z
      // 48b: ifne 492
      // 48e: bipush 1
      // 48f: goto 493
      // 492: bipush 0
      // 493: invokestatic net/rim/device/apps/internal/browser/util/Asserts.productionUserAbortAssert (Z)V
      // 496: aload 0
      // 497: iload 7
      // 499: sipush 4096
      // 49c: iadd
      // 49d: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._nextTickThreshold I
      // 4a0: aload 0
      // 4a1: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 4a4: iload 7
      // 4a6: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsRead (I)V
      // 4a9: aload 0
      // 4aa: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 4ad: aload 0
      // 4ae: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 4b1: invokeinterface net/rim/device/api/browser/field/RenderingApplication.eventOccurred (Lnet/rim/device/api/browser/field/Event;)Ljava/lang/Object; 2
      // 4b6: pop
      // 4b7: goto 508
      // 4ba: aload 0
      // 4bb: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._useApproximatedTicks Z
      // 4be: ifeq 508
      // 4c1: aload 0
      // 4c2: aload 0
      // 4c3: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._nextTickThreshold I
      // 4c6: dup_x1
      // 4c7: bipush 1
      // 4c8: isub
      // 4c9: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._nextTickThreshold I
      // 4cc: ifgt 508
      // 4cf: aload 0
      // 4d0: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 4d3: ifnull 4f6
      // 4d6: aload 0
      // 4d7: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentBrowserField Lnet/rim/device/apps/internal/browser/html/HTMLBrowserField;
      // 4da: invokevirtual net/rim/device/apps/internal/browser/html/HTMLBrowserField.isDestroyed ()Z
      // 4dd: ifne 4e4
      // 4e0: bipush 1
      // 4e1: goto 4e5
      // 4e4: bipush 0
      // 4e5: invokestatic net/rim/device/apps/internal/browser/util/Asserts.productionUserAbortAssert (Z)V
      // 4e8: aload 0
      // 4e9: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 4ec: aload 0
      // 4ed: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 4f0: invokeinterface net/rim/device/api/browser/field/RenderingApplication.eventOccurred (Lnet/rim/device/api/browser/field/Event;)Ljava/lang/Object; 2
      // 4f5: pop
      // 4f6: aload 0
      // 4f7: aload 0
      // 4f8: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._loadingInlineImages Z
      // 4fb: ifeq 503
      // 4fe: bipush 5
      // 500: goto 505
      // 503: bipush 100
      // 505: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._nextTickThreshold I
      // 508: iload 2
      // 509: lookupswitch 323 5 0 128 1 51 2 58 4 315 255 121
      // 53c: aload 0
      // 53d: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.readString ()V
      // 540: goto 381
      // 543: aload 3
      // 544: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._currentStringRef I
      // 547: bipush -1
      // 549: if_icmpeq 576
      // 54c: aload 0
      // 54d: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._coreDocElement Lnet/rim/device/apps/internal/browser/html/HTMLBaseGenericElement;
      // 550: astore 7
      // 552: aload 0
      // 553: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._tagStack Ljava/util/Stack;
      // 556: invokevirtual java/util/Vector.isEmpty ()Z
      // 559: ifne 568
      // 55c: aload 0
      // 55d: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._tagStack Ljava/util/Stack;
      // 560: invokevirtual java/util/Stack.peek ()Ljava/lang/Object;
      // 563: checkcast net/rim/device/apps/internal/browser/html/HTMLGenericElement
      // 566: astore 7
      // 568: aload 7
      // 56a: invokevirtual net/rim/device/apps/internal/browser/html/HTMLGenericElement.autoAppendStrings ()Z
      // 56d: ifeq 576
      // 570: aload 0
      // 571: aload 7
      // 573: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.appendCurrentString (Lnet/rim/device/apps/internal/browser/html/HTMLGenericElement;)V
      // 576: aload 3
      // 577: aload 5
      // 579: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 57c: putfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._currentStringRef I
      // 57f: goto 381
      // 582: aload 0
      // 583: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.handleInstruction ()V
      // 586: goto 381
      // 589: aload 0
      // 58a: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._skipPopCalls Lnet/rim/device/api/util/IntStack;
      // 58d: invokevirtual net/rim/device/api/util/IntVector.isEmpty ()Z
      // 590: ifne 5af
      // 593: aload 0
      // 594: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._skipPopCalls Lnet/rim/device/api/util/IntStack;
      // 597: invokevirtual net/rim/device/api/util/IntStack.peek ()I
      // 59a: aload 0
      // 59b: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._tagStack Ljava/util/Stack;
      // 59e: invokevirtual java/util/Vector.size ()I
      // 5a1: if_icmpne 5af
      // 5a4: aload 0
      // 5a5: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._skipPopCalls Lnet/rim/device/api/util/IntStack;
      // 5a8: invokevirtual net/rim/device/api/util/IntStack.pop ()I
      // 5ab: pop
      // 5ac: goto 381
      // 5af: aload 0
      // 5b0: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._tagStack Ljava/util/Stack;
      // 5b3: invokevirtual java/util/Vector.isEmpty ()Z
      // 5b6: ifeq 5bc
      // 5b9: goto 381
      // 5bc: aload 0
      // 5bd: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._tagStack Ljava/util/Stack;
      // 5c0: invokevirtual java/util/Stack.peek ()Ljava/lang/Object;
      // 5c3: checkcast net/rim/device/apps/internal/browser/html/HTMLGenericElement
      // 5c6: astore 7
      // 5c8: aload 7
      // 5ca: invokevirtual net/rim/device/apps/internal/browser/html/HTMLGenericElement.getTagNameInt ()I
      // 5cd: istore 8
      // 5cf: iload 8
      // 5d1: bipush 75
      // 5d3: if_icmpeq 5f8
      // 5d6: iload 8
      // 5d8: bipush 81
      // 5da: if_icmpeq 5f8
      // 5dd: iload 8
      // 5df: bipush 87
      // 5e1: if_icmpeq 5f8
      // 5e4: iload 8
      // 5e6: bipush 91
      // 5e8: if_icmpeq 5f8
      // 5eb: iload 8
      // 5ed: bipush 19
      // 5ef: if_icmpeq 5f8
      // 5f2: aload 0
      // 5f3: aload 7
      // 5f5: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.appendCurrentString (Lnet/rim/device/apps/internal/browser/html/HTMLGenericElement;)V
      // 5f8: aload 0
      // 5f9: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._processStyleSheets Z
      // 5fc: ifeq 61c
      // 5ff: aload 0
      // 600: aload 0
      // 601: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._tagStack Ljava/util/Stack;
      // 604: invokevirtual java/util/Vector.size ()I
      // 607: bipush 1
      // 608: isub
      // 609: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.removeDescendantStyles (I)V
      // 60c: aload 0
      // 60d: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._tagClasses Ljava/util/Stack;
      // 610: invokevirtual java/util/Stack.pop ()Ljava/lang/Object;
      // 613: pop
      // 614: aload 0
      // 615: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._tagIds Ljava/util/Stack;
      // 618: invokevirtual java/util/Stack.pop ()Ljava/lang/Object;
      // 61b: pop
      // 61c: aload 0
      // 61d: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._tagStack Ljava/util/Stack;
      // 620: invokevirtual java/util/Stack.pop ()Ljava/lang/Object;
      // 623: pop
      // 624: aload 0
      // 625: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._tagStack Ljava/util/Stack;
      // 628: invokevirtual java/util/Vector.size ()I
      // 62b: sipush 200
      // 62e: if_icmplt 634
      // 631: goto 381
      // 634: aload 0
      // 635: aload 7
      // 637: bipush 0
      // 638: bipush 0
      // 639: bipush 0
      // 63a: bipush 0
      // 63b: bipush 0
      // 63c: bipush -1
      // 63e: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.processTag (Lnet/rim/device/apps/internal/browser/html/HTMLGenericElement;IIZZZI)V
      // 641: goto 381
      // 644: aload 0
      // 645: iload 2
      // 646: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.handleTag (I)V
      // 649: goto 381
      // 64c: iload 2
      // 64d: bipush 5
      // 64f: if_icmplt 65a
      // 652: aload 0
      // 653: iload 2
      // 654: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.handleTag (I)V
      // 657: goto 381
      // 65a: new java/lang/Object
      // 65d: dup
      // 65e: invokespecial java/io/IOException.<init> ()V
      // 661: athrow
      // 662: aload 0
      // 663: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._appEventLock Ljava/lang/Object;
      // 666: dup
      // 667: astore 4
      // 669: monitorenter
      // 66a: aload 0
      // 66b: aload 0
      // 66c: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._coreDocElement Lnet/rim/device/apps/internal/browser/html/HTMLBaseGenericElement;
      // 66f: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.appendCurrentString (Lnet/rim/device/apps/internal/browser/html/HTMLGenericElement;)V
      // 672: aload 3
      // 673: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 676: ifle 68d
      // 679: aload 0
      // 67a: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentBrowserField Lnet/rim/device/apps/internal/browser/html/HTMLBrowserField;
      // 67d: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowManager.popCell ()V
      // 680: aload 3
      // 681: dup
      // 682: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 685: bipush 1
      // 686: isub
      // 687: putfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 68a: goto 672
      // 68d: aload 4
      // 68f: monitorexit
      // 690: return
      // 691: astore 13
      // 693: aload 4
      // 695: monitorexit
      // 696: aload 13
      // 698: athrow
      // 699: astore 4
      // 69b: aload 4
      // 69d: athrow
      // 69e: astore 4
      // 6a0: bipush 0
      // 6a1: invokestatic net/rim/device/apps/internal/browser/util/Asserts.productionUserAbortAssert (Z)V
      // 6a4: aload 0
      // 6a5: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._appEventLock Ljava/lang/Object;
      // 6a8: dup
      // 6a9: astore 4
      // 6ab: monitorenter
      // 6ac: aload 0
      // 6ad: aload 0
      // 6ae: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._coreDocElement Lnet/rim/device/apps/internal/browser/html/HTMLBaseGenericElement;
      // 6b1: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.appendCurrentString (Lnet/rim/device/apps/internal/browser/html/HTMLGenericElement;)V
      // 6b4: aload 3
      // 6b5: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 6b8: ifle 6cf
      // 6bb: aload 0
      // 6bc: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentBrowserField Lnet/rim/device/apps/internal/browser/html/HTMLBrowserField;
      // 6bf: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowManager.popCell ()V
      // 6c2: aload 3
      // 6c3: dup
      // 6c4: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 6c7: bipush 1
      // 6c8: isub
      // 6c9: putfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 6cc: goto 6b4
      // 6cf: aload 4
      // 6d1: monitorexit
      // 6d2: return
      // 6d3: astore 14
      // 6d5: aload 4
      // 6d7: monitorexit
      // 6d8: aload 14
      // 6da: athrow
      // 6db: astore 4
      // 6dd: bipush 0
      // 6de: invokestatic net/rim/device/apps/internal/browser/util/Asserts.productionUserAbortAssert (Z)V
      // 6e1: aload 0
      // 6e2: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._appEventLock Ljava/lang/Object;
      // 6e5: dup
      // 6e6: astore 4
      // 6e8: monitorenter
      // 6e9: aload 0
      // 6ea: aload 0
      // 6eb: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._coreDocElement Lnet/rim/device/apps/internal/browser/html/HTMLBaseGenericElement;
      // 6ee: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.appendCurrentString (Lnet/rim/device/apps/internal/browser/html/HTMLGenericElement;)V
      // 6f1: aload 3
      // 6f2: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 6f5: ifle 70c
      // 6f8: aload 0
      // 6f9: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentBrowserField Lnet/rim/device/apps/internal/browser/html/HTMLBrowserField;
      // 6fc: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowManager.popCell ()V
      // 6ff: aload 3
      // 700: dup
      // 701: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 704: bipush 1
      // 705: isub
      // 706: putfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 709: goto 6f1
      // 70c: aload 4
      // 70e: monitorexit
      // 70f: return
      // 710: astore 15
      // 712: aload 4
      // 714: monitorexit
      // 715: aload 15
      // 717: athrow
      // 718: astore 4
      // 71a: aload 4
      // 71c: instanceof java/lang/Object
      // 71f: ifeq 75f
      // 722: ldc_w "Browser"
      // 725: invokestatic net/rim/device/apps/internal/api/quincy/QuincyManager.sendUncaughtException (Ljava/lang/String;)V
      // 728: aload 0
      // 729: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._appEventLock Ljava/lang/Object;
      // 72c: dup
      // 72d: astore 5
      // 72f: monitorenter
      // 730: aload 0
      // 731: aload 0
      // 732: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._coreDocElement Lnet/rim/device/apps/internal/browser/html/HTMLBaseGenericElement;
      // 735: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.appendCurrentString (Lnet/rim/device/apps/internal/browser/html/HTMLGenericElement;)V
      // 738: aload 3
      // 739: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 73c: ifle 753
      // 73f: aload 0
      // 740: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentBrowserField Lnet/rim/device/apps/internal/browser/html/HTMLBrowserField;
      // 743: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowManager.popCell ()V
      // 746: aload 3
      // 747: dup
      // 748: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 74b: bipush 1
      // 74c: isub
      // 74d: putfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 750: goto 738
      // 753: aload 5
      // 755: monitorexit
      // 756: return
      // 757: astore 16
      // 759: aload 5
      // 75b: monitorexit
      // 75c: aload 16
      // 75e: athrow
      // 75f: aload 4
      // 761: dup
      // 762: instanceof java/lang/Object
      // 765: ifne 76c
      // 768: pop
      // 769: goto 770
      // 76c: checkcast java/lang/Object
      // 76f: athrow
      // 770: aload 0
      // 771: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._appEventLock Ljava/lang/Object;
      // 774: dup
      // 775: astore 4
      // 777: monitorenter
      // 778: aload 0
      // 779: aload 0
      // 77a: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._coreDocElement Lnet/rim/device/apps/internal/browser/html/HTMLBaseGenericElement;
      // 77d: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.appendCurrentString (Lnet/rim/device/apps/internal/browser/html/HTMLGenericElement;)V
      // 780: aload 3
      // 781: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 784: ifle 79b
      // 787: aload 0
      // 788: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentBrowserField Lnet/rim/device/apps/internal/browser/html/HTMLBrowserField;
      // 78b: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowManager.popCell ()V
      // 78e: aload 3
      // 78f: dup
      // 790: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 793: bipush 1
      // 794: isub
      // 795: putfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 798: goto 780
      // 79b: aload 4
      // 79d: monitorexit
      // 79e: return
      // 79f: astore 17
      // 7a1: aload 4
      // 7a3: monitorexit
      // 7a4: aload 17
      // 7a6: athrow
      // 7a7: astore 18
      // 7a9: aload 0
      // 7aa: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._appEventLock Ljava/lang/Object;
      // 7ad: dup
      // 7ae: astore 19
      // 7b0: monitorenter
      // 7b1: aload 0
      // 7b2: aload 0
      // 7b3: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._coreDocElement Lnet/rim/device/apps/internal/browser/html/HTMLBaseGenericElement;
      // 7b6: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.appendCurrentString (Lnet/rim/device/apps/internal/browser/html/HTMLGenericElement;)V
      // 7b9: aload 3
      // 7ba: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 7bd: ifle 7d4
      // 7c0: aload 0
      // 7c1: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentBrowserField Lnet/rim/device/apps/internal/browser/html/HTMLBrowserField;
      // 7c4: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowManager.popCell ()V
      // 7c7: aload 3
      // 7c8: dup
      // 7c9: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 7cc: bipush 1
      // 7cd: isub
      // 7ce: putfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._pushedCellCount I
      // 7d1: goto 7b9
      // 7d4: aload 19
      // 7d6: monitorexit
      // 7d7: goto 7e2
      // 7da: astore 20
      // 7dc: aload 19
      // 7de: monitorexit
      // 7df: aload 20
      // 7e1: athrow
      // 7e2: aload 18
      // 7e4: athrow
      // try (19 -> 38): 39 null
      // try (39 -> 42): 39 null
      // try (685 -> 704): 705 null
      // try (705 -> 708): 705 null
      // try (5 -> 14): 710 net/rim/device/apps/internal/browser/common/UserAbortException
      // try (44 -> 680): 710 net/rim/device/apps/internal/browser/common/UserAbortException
      // try (5 -> 14): 713 null
      // try (44 -> 680): 713 null
      // try (721 -> 740): 741 null
      // try (741 -> 744): 741 null
      // try (5 -> 14): 746 null
      // try (44 -> 680): 746 null
      // try (754 -> 773): 774 null
      // try (774 -> 777): 774 null
      // try (5 -> 14): 779 null
      // try (44 -> 680): 779 null
      // try (790 -> 809): 810 null
      // try (810 -> 813): 810 null
      // try (828 -> 847): 848 null
      // try (848 -> 851): 848 null
      // try (5 -> 14): 853 null
      // try (44 -> 680): 853 null
      // try (710 -> 716): 853 null
      // try (746 -> 749): 853 null
      // try (779 -> 785): 853 null
      // try (815 -> 823): 853 null
      // try (859 -> 878): 879 null
      // try (879 -> 882): 879 null
      // try (853 -> 854): 853 null
   }

   private final void adjustViewModeForMobileContent() {
      if (super._renderingOptions.getPropertyWithIntValue(4550690918222697397L, 44, 0) == 2 && this._currentBrowserField.getWideViewMode()) {
         synchronized (this._appEventLock) {
            this._currentBrowserField.toggleViewMode();
         }
      }
   }

   private final void processTag(HTMLGenericElement element, int start, int end, boolean isStartTag, boolean hasAttributes, boolean hasContent, int anchorIndex) {
      int tag = element.getTagNameInt();
      HTMLRendererContext context = this._currentContext;
      if (!this._framesetDocument || tag == 37) {
         if (tag != 64 && tag != 69 && tag != 100 && tag != 62) {
            this.handleObject();
            if (this._skipElementStackValue > 0) {
               return;
            }
         }

         if (isStartTag) {
            this._document.addElement(this._lastName, this._lastId, element);
            if (tag != 66 && tag != 67) {
               synchronized (this._appEventLock) {
                  this._currentBrowserField.pushRegion(element);
               }
            }
         }

         switch (tag) {
            case 4:
            case 9:
            case 20:
            case 25:
            case 45:
            case 49:
            case 53:
            case 63:
            case 70:
            case 85:
            case 88:
            case 90:
               break;
            case 5:
            default:
               this.processElementAnchor(hasContent, isStartTag, start, end, element);
               break;
            case 6:
            case 7:
            case 52:
            case 55:
            case 78:
               this.processGenericInlineElement(hasContent, isStartTag, start, end, element);
               break;
            case 8:
            case 22:
            case 28:
            case 33:
            case 48:
            case 96:
               this.processElementAddressCiteDfnEmIVar(tag, hasContent, isStartTag, start, end, element);
               break;
            case 10:
               this.processElementArea(isStartTag, start, end, element);
               break;
            case 11:
            case 80:
               this.processElementBStrong(tag, hasContent, isStartTag, start, end, element);
               break;
            case 12:
               this.processElementBase(isStartTag, start, end);
               break;
            case 13:
               this.processElementBasefont(isStartTag, start, end);
               break;
            case 14:
               this.processElementBdo(hasContent, isStartTag, start, end, element);
               break;
            case 15:
            case 77:
               this.processElementBigSmall(tag, hasContent, isStartTag, start, end, element);
               break;
            case 16:
            case 18:
            case 26:
            case 32:
               this.processElementBlockquoteBrDdDt(tag, hasContent, isStartTag, start, end, element);
               break;
            case 17:
               this.processElementBody(hasContent, isStartTag, start, end, element);
               break;
            case 19:
               this.processElementInput(true, isStartTag, start, end, element);
               break;
            case 21:
               this.processElementCenter(hasContent, isStartTag, start, end, element);
               break;
            case 23:
            case 54:
            case 71:
            case 74:
            case 93:
               this.processElementCodeKbdPreSampTt(tag, hasContent, isStartTag, start, end, element);
               break;
            case 24:
               this.processElementColumn(isStartTag, start, end);
               break;
            case 27:
            case 73:
            case 79:
               this.processElementDelSStrike(tag, hasContent, isStartTag, start, end, element);
               break;
            case 29:
            case 60:
            case 65:
            case 95:
               this.processElementDirMenuOlUl(tag, hasContent, isStartTag, start, end, element);
               break;
            case 30:
               this.processElementDiv(hasContent, isStartTag, start, end, element);
               break;
            case 31:
               this.processElementDl(hasContent, isStartTag, start, end, element);
               break;
            case 34:
               this.processElementFieldset(hasContent, isStartTag, element);
               break;
            case 35:
               this.processElementFont(hasContent, isStartTag, start, end, element);
               break;
            case 36:
               this.processElementForm(hasContent, isStartTag, start, end, element);
               break;
            case 37:
               this.processElementFrame(isStartTag, start, end);
               break;
            case 38:
               this.processElementFrameset(isStartTag, start, end);
               break;
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
               this.processElementHn(tag, hasContent, isStartTag, start, end, element);
               break;
            case 46:
               this.processElementHr(isStartTag, start, end, element);
               break;
            case 47:
               this.processElementHTML(hasContent, isStartTag, start, end, element);
               break;
            case 50:
               this.processElementImg(isStartTag, anchorIndex, start, end, element);
               break;
            case 51:
               this.processElementInput(false, isStartTag, start, end, element);
               break;
            case 56:
               this.processElementLegend(hasContent, isStartTag, start, end, element);
               break;
            case 57:
               this.processElementLi(hasContent, isStartTag, start, end, element);
               break;
            case 58:
               this.processElementLink(isStartTag, start, end);
               break;
            case 59:
               this.processElementMap(isStartTag, start, end);
               break;
            case 61:
               this.processElementMeta(isStartTag, start, end);
               break;
            case 62:
            case 100:
               this.processElementNoembedNoframes(isStartTag);
               break;
            case 64:
               this.processElementObject(hasContent, isStartTag, start, end, element);
               break;
            case 66:
               this.processElementOptgroup(hasContent, isStartTag, start, end, element);
               break;
            case 67:
               this.processElementOption(hasContent, isStartTag, start, end, element);
               break;
            case 68:
               this.processElementP(hasContent, isStartTag, start, end, element);
               break;
            case 69:
               this.processElementParam(isStartTag, element);
               break;
            case 72:
               this.processElementQ(hasContent, isStartTag, start, end, element);
               break;
            case 75:
               this.processElementScript(isStartTag, start, end);
               break;
            case 76:
               this.processElementSelect(hasContent, isStartTag, start, end, element);
               break;
            case 81:
               this.processElementStyle(isStartTag, start, end);
               break;
            case 82:
               this.processElementSub(hasContent, isStartTag, start, end, element);
               break;
            case 83:
               this.processElementSup(hasContent, isStartTag, element);
               break;
            case 84:
               this.processElementTable(hasContent, isStartTag, start, end, element);
               break;
            case 86:
               this.processElementTdTh(false, hasContent, isStartTag, start, end, element);
               break;
            case 87:
               this.processElementTextArea(isStartTag, start, end, element);
               break;
            case 89:
               this.processElementTdTh(true, hasContent, isStartTag, start, end, element);
               break;
            case 91:
               this.processElementTitle(isStartTag, start, end);
               break;
            case 92:
               this.processElementTr(hasContent, isStartTag, start, end, element);
               break;
            case 94:
               this.processElementU(hasContent, isStartTag, element);
               break;
            case 97:
               this.processElementBlink(hasContent, isStartTag, start, end, element);
               break;
            case 98:
               this.processElementMarquee(hasContent, isStartTag, start, end, element);
               break;
            case 99:
               this.processElementEmbed(hasContent, isStartTag, start, end, element);
         }

         if (isStartTag && this._processStyleSheets) {
            this.findDescendantStyles(tag, this._lastClass, this._lastId, this._tagStack.size() - 1);
         }

         if ((!isStartTag || !hasContent) && tag != 66 && tag != 67) {
            synchronized (this._appEventLock) {
               this._currentBrowserField.popRegion();
            }
         }
      }
   }

   private final String getAttributeValue(String defaultValue, int index) {
      if (this._attributeValues[index] == null) {
         return Integer.toString(this._attributeValuesInts[index]);
      } else {
         return (String)(this._attributeValues[index] == this._attributeValues ? defaultValue : this._attributeValues[index]);
      }
   }

   private final int getAttributeValueAsPixels(int defaultValue, int index) {
      if (this._attributeValues[index] == null) {
         return this._attributeValuesInts[index];
      } else {
         return this._attributeValues[index] == this._attributeValues ? defaultValue : this.valueAsPixels((String)this._attributeValues[index], defaultValue);
      }
   }

   private final int getAttributeValueAsColor(int defaultColor, boolean throwIllegalOnParseError, int index) {
      if (this._attributeValues[index] == null) {
         return this._attributeValuesInts[index];
      }

      if (this._attributeValues[index] == this._attributeValues) {
         return defaultColor;
      }

      try {
         return HTMLBinaryConstants.resolveColor((String)this._attributeValues[index]);
      } finally {
         if (throwIllegalOnParseError) {
            ;
         } else {
            return defaultColor;
         }
      }
   }

   private final int getAttributeValueAsInt(int defaultValue, int index) {
      if (this._attributeValues[index] == null) {
         return this._attributeValuesInts[index];
      } else {
         return this._attributeValues[index] == this._attributeValues ? defaultValue : this.valueAsInt((String)this._attributeValues[index], defaultValue);
      }
   }

   private final int valueAsPercentage(String value, int defaultValue, int percentageMultiplier) {
      if (value == null) {
         return defaultValue;
      }

      int indexOfPercent = value.indexOf(37);
      if (indexOfPercent >= 0) {
         try {
            return Integer.parseInt(value.substring(0, indexOfPercent)) * percentageMultiplier / 100;
         } finally {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   private final int valueAsPixels(String value, int defaultValue) {
      if (value == null) {
         return defaultValue;
      }

      int result = this.valueAsInt(value, defaultValue);
      if (result != defaultValue) {
         return result;
      }

      int index = value.indexOf("px");
      if (index >= 0) {
         label223:
         try {
            return Integer.parseInt(value.substring(0, index));
         } finally {
            break label223;
         }
      }

      index = value.indexOf("pt");
      if (index >= 0) {
         label218:
         try {
            return Ui.convertSize(Integer.parseInt(value.substring(0, index)), 2, 0);
         } finally {
            break label218;
         }
      }

      index = value.indexOf("cm");
      if (index >= 0) {
         label213:
         try {
            return Ui.convertSize(Integer.parseInt(value.substring(0, index)), 4194308, 0);
         } finally {
            break label213;
         }
      }

      index = value.indexOf("mm");
      if (index >= 0) {
         label208:
         try {
            return Ui.convertSize(Integer.parseInt(value.substring(0, index)), 2097156, 0);
         } finally {
            break label208;
         }
      }

      index = value.indexOf("in");
      if (index >= 0) {
         try {
            return Ui.convertSize(Integer.parseInt(value.substring(0, index)) * 5, 4194308, 0) / 2;
         } finally {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   private final int valueAsInt(String value, int defaultValue) {
      if (value == null) {
         return defaultValue;
      }

      try {
         return Integer.parseInt(value);
      } finally {
         ;
      }
   }

   private final void endAnchorIfOpen() {
      if (this._inA) {
         if (this._currentContext._allTagsProvided) {
            this._textUtilities.endAnchor();
         }

         this._currentAnchor = null;
         this._inA = false;
      }
   }

   private final void processElementAnchor(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (!isStartTag) {
         if (this._styleStack.size() > 0 && this.isBlockStyle(this._styleStack.peek(), false)) {
            this.resetBlockStyle(element);
         } else {
            this.resetTextStyle();
         }

         if (this._currentContext._allTagsProvided) {
            this._textUtilities.endAnchor();
         }

         this._currentAnchor = null;
         this._inA = false;
      } else {
         this.endAnchorIfOpen();
         HTMLAnchor anchor = (HTMLAnchor)element;
         String href = null;
         String cti = null;
         boolean hrefSet = false;
         boolean hrefChanged = false;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            switch (this._attributeItems[attribute] & 0xFF) {
               case 125:
                  href = this.getAttributeValue("", attribute);
                  hrefSet = true;
                  break;
               case 199:
                  cti = this.getAttributeValue("", attribute);
            }
         }

         if (!hrefSet && cti != null) {
            href = ((StringBuffer)(new Object("cti:"))).append(cti).toString();
            hrefChanged = true;
         }

         if (this._currentContext._allTagsProvided) {
            this._textUtilities.startAnchor(anchor, href);
         }

         this._currentAnchor = anchor;
         this._inA = true;
         if (this._textUtilities.getCurrentAnchorId() != 5019899335844518230L && href != null && href.indexOf(37) != -1) {
            href = URIDecoder.decode(href, super._encoding);
            hrefChanged = true;
         }

         if (href != null && href.length() > 32768) {
            href = href.substring(0, 32768);
            hrefChanged = true;
         }

         if (href != null) {
            if (hrefChanged) {
               anchor.setAttributeValue(125, href);
            }

            element.setFontStyle(element.getFontStyle() | 4);
            element._foregroundColour = this._textUtilities.getContrastColour(element, this._textUtilities.getLinkColor());
            if (this._processStyleSheets) {
               if (this._lastClass != null) {
                  this._lastClass = ((StringBuffer)(new Object())).append(this._lastClass).append(' ').append(":link").toString();
               } else {
                  this._lastClass = ":link";
               }

               this._tagClasses.pop();
               this._tagClasses.push(this._lastClass);
            }
         }

         if (hasContent) {
            int style = this.getStyle(element.getTagNameInt(), this._lastClass, this._lastId, this._lastStyle);
            if (this.isBlockStyle(style, false)) {
               this.setBlockStyle(element, this._lastClass, this._lastId, this._lastStyle);
            } else {
               this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
            }
         }

         element.setAdditionalFlags((short)(element.getFlags() & -2049));
         this._textUtilities.setFont(element);
      }

      element.setFindMaxWidthFlag(true);
   }

   private final void processElementBase(boolean isStartTag, int startAttribute, int endAttribute) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   private final void processElementForm(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (!isStartTag) {
         this.resetBlockStyle(element);
      } else {
         HTMLForm form = (HTMLForm)element;
         String charset = form.getAcceptCharset();
         if (charset != null && charset.length() != 0 && !charset.equals("unknown")) {
            if (StringUtilities.toLowerCase(charset, 1701707776).indexOf("utf-8") != -1) {
               charset = "utf-8";
            }
         } else {
            charset = super._postEncoding != null ? super._postEncoding : super._encoding;
         }

         form.setPostCharset(charset);
         if (this._currentRadioGroups != null) {
            this._currentRadioGroups.clear();
         }

         this._currentForm = form;
         this._currentForm.setOfflineParameters(RendererControl.getOfflineQueueHeaders(super._inputConnection));
         if (hasContent) {
            this.setBlockStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.setFont(element);
      }
   }

   private final void processElementFrameset(boolean isStartTag, int startAttribute, int endAttribute) {
      if (isStartTag) {
         this._framesetDocument = true;
      }
   }

   private final void processElementFrame(boolean isStartTag, int startAttribute, int endAttribute) {
      if (isStartTag && this._framesetDocument && !this.excessiveEmbedding()) {
         String name = null;
         String src = null;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            switch (this._attributeItems[attribute] & 0xFF) {
               case 142:
                  name = this.getAttributeValue(null, attribute);
                  break;
               case 181:
                  src = this.getAttributeValue(null, attribute);
            }
         }

         if (src != null && src.length() > 0) {
            String absoluteURL = this._browserContent.resolveUrl(src);
            if (name == null) {
               name = absoluteURL;
            }

            Frame currentFrame = null;
            if (this._frame != null) {
               currentFrame = this._frame.getChild(name);
               if (currentFrame != null) {
                  absoluteURL = currentFrame.getUrl();
               } else {
                  currentFrame = new Frame(this._frame, name, absoluteURL);
                  this._frame.addFrame(currentFrame);
               }
            }

            if (super._renderingApplication != null) {
               int flags = super._flags & 0xFF | 16;
               RequestedResource resource = (RequestedResource)(new Object(absoluteURL, this.getRequestHeaders(false), flags));
               HttpConnection connection = super._renderingApplication.getResource(resource, null);
               BrowserContent browserContent = null;

               label123:
               try {
                  browserContent = RendererControl.renderBrowserContent(
                     super._renderingSession, connection, null, null, this._browserContent, flags, null, null, currentFrame
                  );
               } finally {
                  break label123;
               }

               try {
                  if (browserContent != null && browserContent.getDisplayableContent() != null) {
                     Field field = browserContent.getDisplayableContent();
                     FrameManager manager = new FrameManager(currentFrame);
                     manager.add(field);
                     this.appendField(manager, false, false);
                     browserContent.finishLoading();
                     return;
                  }
               } finally {
                  return;
               }
            }
         }
      }
   }

   private final boolean excessiveEmbedding() {
      int embeddingDepth = 0;

      for (Manager manager = this._currentBrowserField.getManager(); manager != null; manager = manager.getManager()) {
         if (manager instanceof TextFlowManager) {
            if (++embeddingDepth >= 6) {
               return true;
            }
         }
      }

      return false;
   }

   private final void processElementScript(boolean isStartTag, int startAttribute, int endAttribute) {
      if (!isStartTag) {
         JavaScriptItem currentScript = this._currentContext._currentScript;
         if (this._scriptEngine != null
            && currentScript != null
            && (currentScript._language == null || currentScript._language.startsWith("javascript"))
            && super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 2, false)) {
            currentScript._scriptRef = this._currentContext._currentStringRef;
            if (currentScript._deferred) {
               this._scriptsToExecute.addElement(currentScript);
            } else {
               this.executeScript(currentScript);
            }
         }

         this._currentContext._currentScript = null;
         this._currentContext._currentStringRef = -1;
      } else {
         JavaScriptItem currentScript = new JavaScriptItem();
         this._currentContext._currentScript = currentScript;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            switch (this._attributeItems[attribute] & 0xFF) {
               case 97:
                  currentScript._charset = this.getAttributeValue(null, attribute);
                  break;
               case 115:
                  currentScript._deferred = true;
                  break;
               case 133:
                  String value = this.getAttributeValue(null, attribute);
                  if (value != null) {
                     currentScript._language = StringUtilities.toLowerCase(value, 1701707776).trim();
                     if (StringUtilities.strEqualIgnoreCase(currentScript._language, "livescript", 1701707776)) {
                        currentScript._language = "javascript";
                     }
                  }
                  break;
               case 181:
                  currentScript._src = this.getAttributeValue(null, attribute);
                  break;
               case 190:
                  String type = this.getAttributeValue(null, attribute);
                  if (StringUtilities.strEqualIgnoreCase(type, "text/javascript", 1701707776)) {
                     currentScript._language = "javascript";
                  }
            }
         }
      }
   }

   private final void processElementHr(boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag) {
         int ruleWidth = -1;
         int size = -1;
         boolean widthPercentage = false;
         boolean filled = false;
         long fieldStyle = 12884901888L;
         int color = 0;
         int stipple = -1;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            switch (this._attributeItems[attribute] & 0xFF) {
               case 8:
                  fieldStyle = 4294967296L;
                  break;
               case 9:
                  fieldStyle = 12884901888L;
                  break;
               case 10:
                  fieldStyle = 8589934592L;
                  break;
               case 106:
                  if (this._useColor) {
                     color = this.getAttributeValueAsColor(color, false, attribute);
                  }
                  break;
               case 145:
                  filled = true;
                  break;
               case 179:
                  size = this.getAttributeValueAsPixels(1, attribute);
                  break;
               case 198:
                  String value = this.getAttributeValue(null, attribute);
                  ruleWidth = this.valueAsPercentage(value, -1, 100);
                  if (ruleWidth == -1) {
                     ruleWidth = this.valueAsPixels(value, -1);
                  } else {
                     widthPercentage = true;
                  }
            }
         }

         int style = this.getStyle(46, this._lastClass, this._lastId, this._lastStyle);
         if (style != -1) {
            short endIndex = this._styleTops[style + 1];

            for (short styleIndex = this._styleTops[style]; styleIndex < endIndex; styleIndex++) {
               int flags = (int)(this._styleFlagsValues[styleIndex] >> 32 & 8388607);
               int itemId = (int)(this._styleFlagsValues[styleIndex] >> 55 & 511);
               int value = (int)(this._styleFlagsValues[styleIndex] & 4294967295L);
               switch (itemId) {
                  case 11:
                  case 17:
                  case 21:
                  case 27:
                     switch (value) {
                        case 31:
                           stipple = -252645136;
                           continue;
                        case 37:
                           stipple = -858993460;
                           continue;
                        case 52:
                        case 97:
                           stipple = 0;
                           continue;
                        default:
                           stipple = -1;
                           continue;
                     }
                  case 34:
                     if (this._useColor) {
                        color = value;
                     }
                     break;
                  case 53:
                     size = this.interpretSizeValue(element, value, flags, false, size);
                     break;
                  case 101:
                     fieldStyle = this.mapAttributeValueAlignmentToField(value);
                     break;
                  case 113:
                     ruleWidth = this.interpretSizeValue(element, value, flags, true, ruleWidth);
                     widthPercentage = (flags & 16) != 0;
               }
            }
         }

         fieldStyle |= this._currentBrowserField.isFocusRegionOpen() ? 18014398509481984L : 36028797018963968L;
         this.appendField(new HorizontalRuleField(ruleWidth, widthPercentage, fieldStyle, size, filled, color, stipple, 2), false, false);
      }
   }

   private final void processElementHTML(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag) {
         if (this._lastDirection != -1 && super._renderingApplication != null) {
            super._renderingApplication.eventOccurred(new UIDirectionChangeEvent(this._lastDirection == 8 ? 1 : 0, this._browserContent));
         }

         if (hasContent) {
            this.setBlockStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.setFont(element);
      } else {
         this.resetBlockStyle(element);
      }
   }

   private final void processElementImg(boolean isStartTag, int anchorIndex, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag) {
         HTMLImg imgElement = (HTMLImg)element;
         long horizontalStyle = 0;
         long verticalStyle = 0;
         boolean horizontalPresent = false;
         if (this._currentContext._allTagsProvided) {
            if (this._currentAnchor != null && this._currentAnchor.getHref() != null) {
               imgElement.setLink(this._currentAnchor);
            }

            if (this._textUtilities.getAlignment() == 9) {
               horizontalStyle = 12884901888L;
               horizontalPresent = true;
            }
         } else {
            if (anchorIndex >= 0) {
               if (this._currentAnchor != null && this._currentAnchor.getHref() != null) {
                  imgElement.setLink(this._currentAnchor);
               } else {
                  imgElement.setLink(new SimpleHTMLAnchorElement(this._currentContext._richTextStrings[anchorIndex]));
               }
            }

            horizontalStyle = this._fieldStyle;
            horizontalPresent = true;
         }

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            int type = this._attributeItems[attribute] & 255;
            switch (type) {
               case 4:
               case 9:
                  break;
               case 5:
               case 6:
               case 7:
                  verticalStyle = this.mapAttributeValueAlignmentToField(type);
                  break;
               case 8:
               case 10:
               default:
                  horizontalStyle = this.mapAttributeValueAlignmentToField(type);
                  horizontalPresent = true;
            }
         }

         int style = this.getStyle(50, this._lastClass, this._lastId, this._lastStyle);
         Border border = null;
         if (style != -1) {
            border = this.getStyleBorder(element, style);
            short endIndex = this._styleTops[style + 1];

            for (short styleIndex = this._styleTops[style]; styleIndex < endIndex; styleIndex++) {
               int flags = (int)(this._styleFlagsValues[styleIndex] >> 32 & 8388607);
               int itemId = (int)(this._styleFlagsValues[styleIndex] >> 55 & 511);
               int value = (int)(this._styleFlagsValues[styleIndex] & 4294967295L);
               switch (itemId) {
                  case 46:
                     switch (value) {
                        case 69:
                           horizontalStyle = 4294967296L;
                           horizontalPresent = true;
                           continue;
                        case 97:
                           horizontalStyle = 0;
                           horizontalPresent = false;
                           continue;
                        case 117:
                           horizontalStyle = 8589934592L;
                           horizontalPresent = true;
                        default:
                           continue;
                     }
                  case 53:
                     imgElement.setHeight(this.interpretSizeValue(element, value, flags, false, imgElement.getHeight()));
                     break;
                  case 107:
                     if ((flags & 15) == 1) {
                        switch (value) {
                           case 8:
                              verticalStyle = 0;
                              break;
                           case 17:
                              verticalStyle = 34359738368L;
                              break;
                           case 88:
                              verticalStyle = 51539607552L;
                              break;
                           case 160:
                              verticalStyle = 17179869184L;
                        }
                     }
                     break;
                  case 113:
                     imgElement.setWidth(this.interpretSizeValue(element, value, flags, false, imgElement.getWidth()));
               }
            }
         }

         String mapValue = imgElement.getUseMap();
         if (mapValue != null && mapValue.length() > 0) {
            int fragmentIdentifier = mapValue.indexOf(35);
            if (fragmentIdentifier != -1) {
               mapValue = mapValue.substring(fragmentIdentifier + 1);
            }

            if (mapValue.length() > 0) {
               ImageMap map = (ImageMap)this._imageMaps.get(mapValue);
               if (map == null) {
                  map = new ImageMap(mapValue);
                  this._imageMaps.put(mapValue, map);
               }

               imgElement.setImageMap(map);
               imgElement.setLink(new SimpleHTMLAnchorElement(map.getNumAreas() > 0 ? map.getArea(0).getHref() : this._url));
            }
         }

         imgElement.setStyle(horizontalStyle | verticalStyle);
         Field bitmapField = this.makeBitmapField(imgElement, border, null);
         if (bitmapField != null) {
            this.appendField(bitmapField, !horizontalPresent, false);
         }
      }
   }

   private final void processElementInput(boolean isButton, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (!isStartTag) {
         if (isButton && this._currentFormControl != null && this._currentFormControl.getPeer() instanceof Object) {
            if (this._currentContext._currentStringRef != -1) {
               synchronized (this._appEventLock) {
                  ButtonField button = (ButtonField)this._currentFormControl.getPeer();
                  button.setLabel(this._currentContext._richTextStrings[this._currentContext._currentStringRef]);
               }

               this._currentContext._currentStringRef = -1;
            }

            this._currentFormControl = null;
         }
      } else {
         HTMLInput input = (HTMLInput)element;
         input.setUid(this._inputElementCount++);
         int type = 35;
         String value = null;
         String name = null;
         boolean checked = false;
         int maxLength = -1;
         int size = -1;
         String alt = null;
         long horizontalStyle = 4294967296L;
         long readOnlyStyle = 0;
         boolean horizontalStylePresent = false;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            switch (this._attributeItems[attribute] & 0xFF) {
               case 8:
                  if (!isButton) {
                     horizontalStyle = 4294967296L;
                     horizontalStylePresent = true;
                  }
                  break;
               case 10:
                  if (!isButton) {
                     horizontalStyle = 8589934592L;
                     horizontalStylePresent = true;
                  }
                  break;
               case 26:
               case 27:
               case 28:
               case 29:
               case 30:
               case 31:
               case 32:
               case 33:
               case 34:
               case 35:
                  type = this._attributeItems[attribute] & 255;
                  break;
               case 87:
                  alt = this.getAttributeValue(null, attribute);
                  break;
               case 98:
                  checked = true;
                  break;
               case 117:
                  readOnlyStyle = 45035996273704960L;
                  break;
               case 138:
                  maxLength = this.getAttributeValueAsInt(-1, attribute);
                  break;
               case 142:
                  name = this.getAttributeValue(null, attribute);
                  break;
               case 168:
                  if (!isButton) {
                     readOnlyStyle = 9007199254740992L;
                  }
                  break;
               case 179:
                  size = this.getAttributeValueAsPixels(-1, attribute);
                  break;
               case 193:
                  value = this.getAttributeValue(null, attribute);
            }
         }

         if (isButton) {
            if (type != 29 && type != 30 && type != 34) {
               type = 29;
            }

            alt = null;
         }

         int style = this.getStyle(isButton ? 19 : 51, this._lastClass, this._lastId, this._lastStyle);
         this.setTextStyle(style, 3);
         Border border = null;
         int backgroundColor = -1;
         int foregroundColor = -1;
         int width = -1;
         int height = -1;
         String format = null;
         boolean emptyOk = true;
         if (style != -1) {
            border = this.getStyleBorder(element, style);
            boolean emptyOkSet = false;
            short endIndex = this._styleTops[style + 1];

            for (short styleIndex = this._styleTops[style]; styleIndex < endIndex; styleIndex++) {
               int flags = (int)(this._styleFlagsValues[styleIndex] >> 32 & 8388607);
               int itemId = (int)(this._styleFlagsValues[styleIndex] >> 55 & 511);
               int styleValueInt = (int)(this._styleFlagsValues[styleIndex] & 4294967295L);
               switch (itemId) {
                  case 4:
                     if (this._useColor) {
                        backgroundColor = styleValueInt;
                        this.handleTextStyle(element, 3, itemId, styleValueInt, flags);
                     }
                     break;
                  case 34:
                     if (this._useColor) {
                        foregroundColor = styleValueInt;
                        this.handleTextStyle(element, 3, itemId, styleValueInt, flags);
                     }
                     break;
                  case 48:
                  case 49:
                  case 50:
                  case 51:
                  case 52:
                  case 101:
                  case 102:
                  case 104:
                     this.handleTextStyle(element, 3, itemId, styleValueInt, flags);
                     break;
                  case 53:
                     height = this.interpretSizeValue(element, styleValueInt, flags, false, height);
                     break;
                  case 113:
                     width = this.interpretSizeValue(element, styleValueInt, flags, false, width);
                     break;
                  case 117:
                     format = ((CSSString)this.getStyleObject(styleValueInt)).getString();
                     break;
                  case 118:
                     emptyOk = styleValueInt != 162;
                     emptyOkSet = true;
               }
            }

            if (!emptyOkSet && format != null && format.length() >= 0) {
               emptyOk = format.charAt(0) == '*';
            }

            if (foregroundColor != -1 && backgroundColor != -1) {
               foregroundColor = this._textUtilities.getContrastColour(foregroundColor, backgroundColor);
            }
         }

         this._textUtilities.setFont(element);
         Field field = null;
         long fieldStyle = (horizontalStylePresent ? horizontalStyle : this._fieldStyle) | readOnlyStyle;
         byte flags = 0;
         switch (type) {
            case 26:
               flags = (byte)(flags | 1);
            case 25:
               if ((readOnlyStyle & 9007199254740992L) != 0) {
                  readOnlyStyle |= 1024;
               }

               if (format != null && !format.equals("*M")) {
                  field = new HTMLTextInputField(
                     input,
                     this._textUtilities.deriveFont(element),
                     value,
                     size,
                     maxLength,
                     flags,
                     readOnlyStyle,
                     backgroundColor,
                     foregroundColor,
                     format,
                     emptyOk
                  );
               } else {
                  field = new HTMLTextInputField(
                     input,
                     this._textUtilities.deriveFont(element),
                     value,
                     size,
                     maxLength,
                     flags,
                     readOnlyStyle,
                     backgroundColor,
                     foregroundColor,
                     null,
                     emptyOk
                  );
               }
               break;
            case 27:
               field = new HTMLCheckboxInputField(input, null, checked, 2147483648L | readOnlyStyle);
               break;
            case 28:
               RadioButtonGroup group = null;
               if (this._currentRadioGroups != null && name != null) {
                  group = (RadioButtonGroup)this._currentRadioGroups.get(name);
               } else {
                  this._currentRadioGroups = (Hashtable)(new Object(1));
               }

               if (group == null) {
                  group = (RadioButtonGroup)(new Object());
                  if (name != null) {
                     this._currentRadioGroups.put(name, group);
                  }
               }

               field = new HTMLRadioInputField(input, null, group, checked, 2147483648L | readOnlyStyle);
               break;
            case 29:
               field = new HTMLSubmitInputField(input, value, alt, false, fieldStyle | 4, width, height, backgroundColor, foregroundColor, border);
               border = null;
               break;
            case 30:
               field = new HTMLResetInputField(input, value, fieldStyle | 4, width, height, backgroundColor, foregroundColor, border);
               border = null;
               break;
            case 31:
               field = new HTMLFileInputField(
                  input, this._textUtilities.deriveFont(element), size, flags, fieldStyle, backgroundColor, foregroundColor, format, emptyOk
               );
               break;
            case 32:
               if (RendererControl.isOfflineQueueHeader(name) && this._currentForm != null) {
                  this._currentForm.setOfflineParameter(name, value);
               }
               break;
            case 33:
            default:
               alt = alt != null ? alt : (value != null ? value : name);
               field = this.makeBitmapField(input, border, alt);
               break;
            case 34:
               field = new HTMLButtonInputField(input, value, fieldStyle, width, height, backgroundColor, foregroundColor, border);
               border = null;
         }

         if (field != null) {
            input.setPeer(field);
         }

         input.loadFrom(this._htmlContext);
         if (this._currentForm != null) {
            this._currentForm.addElement(input);
         }

         if (field != null) {
            field.setFont(this._textUtilities.deriveFont(element));
            if (border != null) {
               field.setBorder(border);
            }

            this.appendField(field, true, true);
            if (isButton) {
               this._currentFormControl = input;
            }
         }

         this.resetTextStyle(3);
      }
   }

   private final void processElementMeta(boolean isStartTag, int startAttribute, int endAttribute) {
      if (isStartTag) {
         String httpEquiv = null;
         String content = null;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            switch (this._attributeItems[attribute] & 0xFF) {
               case 97:
                  this.setEncoding(this.getAttributeValue(null, attribute));
                  break;
               case 110:
                  content = this.getAttributeValue(null, attribute);
                  if (content != null) {
                     int index = StringUtilities.toLowerCase(content, 1701707776).indexOf("charset");
                     int end = content.indexOf(59, index + 8);
                     if (index != -1) {
                        if (end != -1) {
                           this.setEncoding(content.substring(index + 8, end));
                        } else {
                           this.setEncoding(content.substring(index + 8));
                        }
                     }
                  }
                  break;
               case 128:
                  httpEquiv = this.getAttributeValue(null, attribute);
            }
         }

         if (httpEquiv != null && content != null && super._renderingApplication != null) {
            if (StringUtilities.strEqualIgnoreCase(httpEquiv, HeaderParser.CACHE_CONTROL, 1701707776)) {
               SetHeaderEvent event = (SetHeaderEvent)(new Object(this._browserContent, HeaderParser.CACHE_CONTROL, content));
               super._renderingApplication.eventOccurred(event);
            } else if (StringUtilities.strEqualIgnoreCase(httpEquiv, HeaderParser.EXPIRES, 1701707776)) {
               SetHeaderEvent event = (SetHeaderEvent)(new Object(this._browserContent, HeaderParser.EXPIRES, content));
               super._renderingApplication.eventOccurred(event);
            } else if (!StringUtilities.strEqualIgnoreCase(httpEquiv, HeaderParser.REFRESH, 1701707776)) {
               if (StringUtilities.strEqualIgnoreCase(httpEquiv, "x-rim-auto-match", 1701707776)) {
                  if (StringUtilities.strEqualIgnoreCase(content, "none", 1701707776)) {
                     this._textUtilities.setAutoMatch(false);
                  }
               } else if (StringUtilities.strEqualIgnoreCase(httpEquiv, HeaderParser.DEFAULT_STYLE, 1701707776)) {
                  if (this._processStyleSheets && content != null) {
                     this._defaultStyle = content;
                  }
               } else if (StringUtilities.strEqualIgnoreCase(httpEquiv, "Content-Type", 1701707776)
                  && StringUtilities.startsWithIgnoreCase(content, "application/vnd.wap.xhtml+xml", 1701707776)) {
                  this.adjustViewModeForMobileContent();
               }
            } else {
               String url = this._url;
               int delay = 0;
               int index = content.indexOf(59);
               int contentLength = content.length();

               try {
                  if (index == -1) {
                     index = content.indexOf(44);
                     if (index == -1) {
                        delay = Integer.parseInt(content);
                     } else {
                        delay = Integer.parseInt(content.substring(0, index));
                     }
                  } else {
                     delay = Integer.parseInt(content.substring(0, index));
                  }
               } finally {
                  ;
               }

               if (contentLength > index) {
                  index = content.indexOf(61, index);
                  if (index++ != -1 && contentLength > index) {
                     int offset = 0;
                     if (content.charAt(index) == '\'' || content.charAt(index) == '"') {
                        offset = 1;
                     }

                     url = content.substring(index + offset, contentLength - offset).trim();
                  }
               }

               if (delay >= 0 && !this._browserContent.hasTimer()) {
                  this._browserContent
                     .setOnTimer(new EventVerb((Event)(new Object(this._browserContent, url, null, 3, delay * 1000)), super._renderingApplication));
                  PageTimer timer = new PageTimer(delay, this._browserContent);
                  if (delay >= 60) {
                     timer.setPrompt(BrowserResources.getString(613));
                  }

                  this._browserContent.addTimer(timer);
               }
            }
         }

         if (StringUtilities.strEqualIgnoreCase(this._lastName, "HandheldFriendly", 1701707776)
            && StringUtilities.strEqualIgnoreCase(content, "true", 1701707776)) {
            this.adjustViewModeForMobileContent();
         }
      }
   }

   private final void addPendingOptionLabel() {
      if (this._currentSelect != null && this._currentContext._currentStringRef != -1) {
         Node lastChild = this._currentSelect.getLastChild();
         if (lastChild instanceof HTMLOption) {
            HTMLOption lastOption = (HTMLOption)lastChild;
            synchronized (this._appEventLock) {
               String text = lastOption.getText();
               String newString = this._currentContext._richTextStrings[this._currentContext._currentStringRef];
               lastOption.setText(
                  text != null && text.length() != 0 ? ((StringBuffer)(new Object())).append(text).append(newString).toString() : newString.trim()
               );
            }
         }

         this._currentContext._currentStringRef = -1;
      }
   }

   private final void processElementOption(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag) {
         if (hasContent) {
            this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.setFont(element);
      } else {
         this.resetTextStyle();
      }
   }

   private final void processElementOptgroup(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag) {
         if (hasContent) {
            this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.setFont(element);
      } else {
         this.resetTextStyle();
      }
   }

   private final void processElementSelect(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (!isStartTag) {
         if (this._currentSelect != null) {
            this._currentSelect.loadFrom(this._htmlContext);
            this._currentSelect = null;
         }
      } else {
         HTMLSelect select = (HTMLSelect)element;
         select.setUid(this._inputElementCount++);
         if (hasContent) {
            this._currentSelect = select;
         }

         boolean multiple = false;
         long fieldStyle = this._fieldStyle;
         int size = 1;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            switch (this._attributeItems[attribute] & 0xFF) {
               case 117:
                  fieldStyle |= 45035996273704960L;
                  break;
               case 141:
                  multiple = true;
                  break;
               case 168:
                  fieldStyle |= 9007199254740992L;
                  break;
               case 179:
                  size = this.getAttributeValueAsPixels(1, attribute);
                  if (size <= 0) {
                     size = 1;
                  }
            }
         }

         int style = this.getStyle(76, this._lastClass, this._lastId, this._lastStyle);
         this.setTextStyle(style, 3);
         Border border = null;
         int backgroundColor = -1;
         int foregroundColor = -1;
         if (style != -1) {
            border = this.getStyleBorder(element, style);
            short endIndex = this._styleTops[style + 1];

            for (short styleIndex = this._styleTops[style]; styleIndex < endIndex; styleIndex++) {
               int itemId = (int)(this._styleFlagsValues[styleIndex] >> 55 & 511);
               int flags = (int)(this._styleFlagsValues[styleIndex] >> 32 & 8388607);
               int value = (int)(this._styleFlagsValues[styleIndex] & 4294967295L);
               switch (itemId) {
                  case 4:
                     if (this._useColor) {
                        backgroundColor = value;
                        this.handleTextStyle(element, 3, itemId, value, flags);
                     }
                     break;
                  case 34:
                     if (this._useColor) {
                        foregroundColor = value;
                        this.handleTextStyle(element, 3, itemId, value, flags);
                     }
                     break;
                  case 48:
                  case 49:
                  case 50:
                  case 51:
                  case 52:
                  case 101:
                  case 102:
                  case 104:
                     this.handleTextStyle(element, 3, itemId, value, flags);
               }
            }

            if (foregroundColor != -1 && backgroundColor != -1) {
               foregroundColor = this._textUtilities.getContrastColour(foregroundColor, backgroundColor);
            }
         }

         this._textUtilities.setFont(element);
         HTMLSelectField field = new HTMLSelectField(this._browserContent, select, size, multiple, fieldStyle, backgroundColor, foregroundColor);
         field.setFont(element.getFont());
         select.setPeer(field);
         if (border != null) {
            field.setBorder(border);
         }

         if (this._currentForm != null) {
            this._currentForm.addElement(select);
         }

         this.appendField(field, true, true);
         this.resetTextStyle(3);
      }
   }

   private final void processGenericInlineElement(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (!isStartTag) {
         if (this._styleStack.size() > 0 && this.isBlockStyle(this._styleStack.peek(), false)) {
            this.resetBlockStyle(element);
         } else {
            this.resetTextStyle();
         }
      } else {
         if (hasContent) {
            int style = this.getStyle(element.getTagNameInt(), this._lastClass, this._lastId, this._lastStyle);
            if (style != -1 && this.isBlockStyle(style, false)) {
               this.setBlockStyle(element, this._lastClass, this._lastId, this._lastStyle);
            } else {
               this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
            }
         }

         this._textUtilities.setFont(element);
      }
   }

   private final void processElementBdo(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag) {
         short dir = 0;
         if (this._lastDirection == 0) {
            dir = 16;
         } else if (this._lastDirection == 8) {
            dir = 32;
         }

         this._lastDirection = -1;
         if (hasContent) {
            this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.setFont(element);
         element.setAlignment(dir);
         synchronized (this._appEventLock) {
            if (dir == 16) {
               this._textUtilities.appendDirectionCharacter('\u202d');
            } else if (dir == 32) {
               this._textUtilities.appendDirectionCharacter('\u202e');
            } else {
               this._textUtilities.appendDirectionCharacter('\u202a');
            }
         }
      } else {
         this.resetTextStyle();
         synchronized (this._appEventLock) {
            this._textUtilities.appendDirectionCharacter('\u202c');
         }
      }
   }

   private final void processElementTextArea(boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (!isStartTag) {
         if (this._currentFormControl != null) {
            Field peer = this._currentFormControl.getPeer();
            if (peer instanceof HTMLTextAreaField) {
               synchronized (this._appEventLock) {
                  if (this._currentContext._currentStringRef != -1) {
                     String initialValue = this._currentContext._richTextStrings[this._currentContext._currentStringRef];
                     this._currentFormControl.setDefaultValue(initialValue);
                     this._currentFormControl.setValue(initialValue);
                  }

                  this._currentFormControl.loadFrom(this._htmlContext);
               }
            } else {
               this.appendCurrentString(element);
            }
         }

         this._currentFormControl = null;
         this._currentContext._currentStringRef = -1;
      } else {
         HTMLTextArea input = (HTMLTextArea)element;
         input.setUid(this._inputElementCount++);
         int rows = 3;
         int cols = 20;
         long readOnlyStyle = 0;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            switch (this._attributeItems[attribute] & 0xFF) {
               case 107:
                  cols = this.getAttributeValueAsInt(20, attribute);
                  if (cols <= 0) {
                     cols = 20;
                  }
                  break;
               case 117:
                  readOnlyStyle = 45035996273705984L;
                  break;
               case 168:
                  readOnlyStyle = 9007199254742016L;
                  break;
               case 171:
                  rows = this.getAttributeValueAsInt(3, attribute);
                  if (rows <= 0) {
                     rows = 3;
                  }
            }
         }

         int style = this.getStyle(87, this._lastClass, this._lastId, this._lastStyle);
         this.setTextStyle(style, 3);
         Border border = null;
         int backgroundColor = -1;
         int foregroundColor = -1;
         String format = null;
         String fontFace = "monospace";
         boolean emptyOk = true;
         boolean emptyOkSet = false;
         if (style != -1) {
            border = this.getStyleBorder(element, style);
            short endIndex = this._styleTops[style + 1];

            for (short styleIndex = this._styleTops[style]; styleIndex < endIndex; styleIndex++) {
               int itemId = (int)(this._styleFlagsValues[styleIndex] >> 55 & 511);
               int flags = (int)(this._styleFlagsValues[styleIndex] >> 32 & 8388607);
               int value = (int)(this._styleFlagsValues[styleIndex] & 4294967295L);
               switch (itemId) {
                  case 4:
                     if (this._useColor) {
                        backgroundColor = value;
                        this.handleTextStyle(element, 3, itemId, value, flags);
                     }
                     break;
                  case 34:
                     if (this._useColor) {
                        foregroundColor = value;
                        this.handleTextStyle(element, 3, itemId, value, flags);
                     }
                     break;
                  case 48:
                     fontFace = (String)this.getStyleObject(value);
                     this.handleTextStyle(element, 3, itemId, value, flags);
                     break;
                  case 49:
                  case 50:
                  case 51:
                  case 52:
                  case 101:
                  case 102:
                  case 104:
                     this.handleTextStyle(element, 3, itemId, value, flags);
                     break;
                  case 117:
                     format = ((CSSString)this.getStyleObject(value)).getString();
                     break;
                  case 118:
                     emptyOk = value != 162;
                     emptyOkSet = true;
               }
            }

            if (!emptyOkSet && format != null && format.length() >= 0) {
               emptyOk = format.charAt(0) == '*';
            }

            if (foregroundColor != -1 && backgroundColor != -1) {
               foregroundColor = this._textUtilities.getContrastColour(foregroundColor, backgroundColor);
            }
         }

         element.setFontFamily(fontFace);
         this._textUtilities.setFont(element);
         Font monospaceFont = element.getFont();
         input.setAttributeValue(107, cols);
         input.setAttributeValue(171, rows);
         HTMLTextAreaField field = new HTMLTextAreaField(input, rows, cols, monospaceFont, readOnlyStyle, backgroundColor, foregroundColor, format, emptyOk);
         if (border != null) {
            field.setBorder(border);
         }

         input.setPeer(field);
         this._currentFormControl = input;
         if (this._currentForm != null) {
            this._currentForm.addElement(input);
         }

         this.resetTextStyle(3);
         this.appendField(field, true, true);
      }
   }

   private final void processElementTitle(boolean isStartTag, int startAttribute, int endAttribute) {
      if (!isStartTag) {
         if (this._currentContext._currentStringRef != -1 && !this._titleSet) {
            synchronized (this._appEventLock) {
               this._browserContent.setTitle(this._currentContext._richTextStrings[this._currentContext._currentStringRef]);
            }

            this._titleSet = true;
         }

         this._currentContext._currentStringRef = -1;
      }
   }

   private final void processElementTable(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag) {
         this._blockLevelBreakFlags = 320;
         element.setBreakingFlags((short)this._blockLevelBreakFlags);
      }

      if (this._tableStack != null) {
         if (isStartTag) {
            int tableBorderMask = -1;
            int width = -1;
            boolean widthPercentage = false;
            boolean repeatX = true;
            boolean repeatY = true;
            int backgroundXPos = 0;
            int backgroundYPos = 0;
            int borderTopColor = 0;
            int borderRightColor = 0;
            int borderBottomColor = 0;
            int borderLeftColor = 0;
            boolean borderTopColorSet = false;
            boolean borderRightColorSet = false;
            boolean borderBottomColorSet = false;
            boolean borderLeftColorSet = false;
            boolean borderWidthSet = false;
            boolean rulesSet = false;
            int dir = this._lastDirection;
            int halign = 0;
            Table currentTable;
            int availableWidth;
            int attribute;
            switch (element.getFlags() & 7) {
               case 3:
                  halign = 4096;
               default:
                  currentTable = new Table();
                  availableWidth = this._currentBrowserField.getCurrentContentWidth();
                  attribute = startAttribute;
            }

            for (; attribute < endAttribute; attribute++) {
               switch (this._attributeItems[attribute] & 0xFF) {
                  case 8:
                     halign = 2048;
                     break;
                  case 9:
                     halign = 4096;
                     break;
                  case 10:
                     halign = 6144;
                     break;
                  case 40:
                     currentTable._cellBorderMask = 0;
                     rulesSet = true;
                     break;
                  case 41:
                  case 44:
                     currentTable._cellBorderMask = 15;
                     rulesSet = true;
                     break;
                  case 42:
                     currentTable._cellBorderMask = 12;
                     rulesSet = true;
                     break;
                  case 43:
                     currentTable._cellBorderMask = 3;
                     rulesSet = true;
                     break;
                  case 45:
                     tableBorderMask = 0;
                     break;
                  case 46:
                     tableBorderMask = 4;
                     if (!borderWidthSet) {
                        currentTable._borderTopWidth = 1;
                     }
                     break;
                  case 47:
                     tableBorderMask = 8;
                     if (!borderWidthSet) {
                        currentTable._borderBottomWidth = 1;
                     }
                     break;
                  case 48:
                     tableBorderMask = 12;
                     if (!borderWidthSet) {
                        currentTable._borderTopWidth = currentTable._borderBottomWidth = 1;
                     }
                     break;
                  case 49:
                     tableBorderMask = 1;
                     if (!borderWidthSet) {
                        currentTable._borderLeftWidth = 1;
                     }
                     break;
                  case 50:
                     tableBorderMask = 2;
                     if (!borderWidthSet) {
                        currentTable._borderRightWidth = 1;
                     }
                     break;
                  case 51:
                     tableBorderMask = 3;
                     if (!borderWidthSet) {
                        currentTable._borderRightWidth = currentTable._borderLeftWidth = 1;
                     }
                     break;
                  case 52:
                  case 53:
                     tableBorderMask = 15;
                     if (!borderWidthSet) {
                        currentTable._borderTopWidth = currentTable._borderRightWidth = currentTable._borderBottomWidth = currentTable._borderLeftWidth = 1;
                     }
                     break;
                  case 90:
                     currentTable._backgroundURI = this.getAttributeValue(null, attribute);
                     break;
                  case 91:
                     if (this._useColor) {
                        element._backgroundColour = this.getAttributeValueAsColor(element._backgroundColour, false, attribute);
                     }
                     break;
                  case 92:
                     int borderWidth = this.getAttributeValueAsPixels(-1, attribute);
                     if (borderWidth == 0) {
                        tableBorderMask = 0;
                        if (!rulesSet) {
                           currentTable._cellBorderMask = 0;
                        }
                     } else {
                        if (tableBorderMask == -1) {
                           tableBorderMask = 15;
                        }

                        if (!rulesSet) {
                           currentTable._cellBorderMask = 15;
                        }

                        if (borderWidth < 0) {
                           borderWidth = 1;
                        }
                     }

                     currentTable._borderTopWidth = currentTable._borderRightWidth = currentTable._borderBottomWidth = currentTable._borderLeftWidth = borderWidth;
                     borderWidthSet = true;
                     break;
                  case 93:
                     currentTable._cellPadding = this.getAttributeValueAsPixels(0, attribute);
                     if (currentTable._cellPadding < 0) {
                        currentTable._cellPadding = 0;
                     }
                     break;
                  case 94:
                     currentTable._cellSpacing = this.getAttributeValueAsPixels(0, attribute);
                     if (currentTable._cellSpacing < 0) {
                        currentTable._cellSpacing = 2;
                     }
                     break;
                  case 198:
                     String value = this.getAttributeValue(null, attribute);
                     width = this.valueAsPercentage(value, -1, 100);
                     if (width == -1) {
                        width = this.valueAsPixels(value, -1);
                     } else {
                        widthPercentage = true;
                     }
               }
            }

            if (tableBorderMask == -1) {
               tableBorderMask = 0;
            }

            attribute = this.getStyle(84, this._lastClass, this._lastId, this._lastStyle);
            if (hasContent) {
               this.setTextStyle(attribute, 2);
            }

            if (attribute != -1) {
               short endIndex = this._styleTops[attribute + 1];

               for (short styleIndex = this._styleTops[attribute]; styleIndex < endIndex; styleIndex++) {
                  int flags = (int)(this._styleFlagsValues[styleIndex] >> 32 & 8388607);
                  int itemId = (int)(this._styleFlagsValues[styleIndex] >> 55 & 511);
                  int value = (int)(this._styleFlagsValues[styleIndex] & 4294967295L);
                  switch (itemId) {
                     case 4:
                        if (hasContent) {
                           this.handleTextStyle(element, 2, itemId, value, flags);
                        }

                        element._backgroundColour = value;
                        break;
                     case 5:
                        if ((flags & 15) == 3) {
                           currentTable._backgroundURI = (String)this.getStyleObject(value);
                        }
                        break;
                     case 6:
                        if ((flags & 15) == 1) {
                           int[] values = (int[])this.getStyleObject(value);
                           backgroundXPos = this.interpretSizeValue(element, values[1], values[0], true, 0);
                           if ((values[0] & 16) != 0) {
                              backgroundXPos |= 1073741824;
                           } else if (backgroundXPos < 0) {
                              backgroundXPos *= -1;
                              backgroundXPos |= Integer.MIN_VALUE;
                           }

                           backgroundYPos = this.interpretSizeValue(element, values[3], values[2], true, 0);
                           if ((values[2] & 16) != 0) {
                              backgroundYPos |= 1073741824;
                           } else if (backgroundYPos < 0) {
                              backgroundYPos *= -1;
                              backgroundYPos |= Integer.MIN_VALUE;
                           }
                        }
                        break;
                     case 7:
                        switch (value) {
                           case 96:
                              repeatX = false;
                              repeatY = false;
                              continue;
                           case 113:
                              repeatX = true;
                              repeatY = true;
                              continue;
                           case 114:
                              repeatX = true;
                              repeatY = false;
                              continue;
                           case 115:
                              repeatX = false;
                              repeatY = true;
                           default:
                              continue;
                        }
                     case 10:
                        borderBottomColor = value;
                        borderBottomColorSet = true;
                        break;
                     case 11:
                        if (value != 97 && value != 52) {
                           tableBorderMask |= 8;
                           if (currentTable._borderBottomWidth == 0) {
                              currentTable._borderBottomWidth = 1;
                           }
                        } else {
                           tableBorderMask &= -9;
                        }
                        break;
                     case 12:
                        currentTable._borderBottomWidth = this.interpretSizeValue(element, value, flags, false, currentTable._borderBottomWidth);
                        break;
                     case 16:
                        borderLeftColor = value;
                        borderLeftColorSet = true;
                        break;
                     case 17:
                        if (value != 97 && value != 52) {
                           tableBorderMask |= 1;
                           if (currentTable._borderLeftWidth == 0) {
                              currentTable._borderLeftWidth = 1;
                           }
                        } else {
                           tableBorderMask &= -2;
                        }
                        break;
                     case 18:
                        currentTable._borderLeftWidth = this.interpretSizeValue(element, value, flags, false, currentTable._borderLeftWidth);
                        break;
                     case 20:
                        borderRightColor = value;
                        borderRightColorSet = true;
                        break;
                     case 21:
                        if (value != 97 && value != 52) {
                           tableBorderMask |= 2;
                           if (currentTable._borderRightWidth == 0) {
                              currentTable._borderRightWidth = 1;
                           }
                        } else {
                           tableBorderMask &= -3;
                        }
                        break;
                     case 22:
                        currentTable._borderRightWidth = this.interpretSizeValue(element, value, flags, false, currentTable._borderRightWidth);
                        break;
                     case 26:
                        borderTopColor = value;
                        borderTopColorSet = true;
                        break;
                     case 27:
                        if (value != 97 && value != 52) {
                           tableBorderMask |= 4;
                           if (currentTable._borderTopWidth == 0) {
                              currentTable._borderTopWidth = 1;
                           }
                        } else {
                           tableBorderMask &= -5;
                        }
                        break;
                     case 28:
                        currentTable._borderTopWidth = this.interpretSizeValue(element, value, flags, false, currentTable._borderTopWidth);
                        break;
                     case 34:
                     case 48:
                     case 49:
                     case 50:
                     case 51:
                     case 52:
                     case 101:
                     case 102:
                     case 104:
                        if (hasContent) {
                           this.handleTextStyle(element, 2, itemId, value, flags);
                        }
                        break;
                     case 42:
                        dir = value;
                        break;
                     case 46:
                        switch (value) {
                           case 69:
                              halign = 2048;
                              continue;
                           case 97:
                              halign = 0;
                              continue;
                           case 117:
                              halign = 6144;
                           default:
                              continue;
                        }
                     case 100:
                        currentTable._fixed = value == 48;
                        break;
                     case 113:
                        width = this.interpretSizeValue(element, value, flags, true, width);
                        widthPercentage = (flags & 16) != 0;
                  }
               }
            }

            this._textUtilities.setFont(element);
            int currentTextColor = element._foregroundColour;
            if (!borderTopColorSet) {
               borderTopColor = currentTextColor;
            }

            if (!borderRightColorSet) {
               borderRightColor = currentTextColor;
            }

            if (!borderBottomColorSet) {
               borderBottomColor = currentTextColor;
            }

            if (!borderLeftColorSet) {
               borderLeftColor = currentTextColor;
            }

            if (width != -1) {
               if (widthPercentage) {
                  currentTable._specifiedTableWidth = -MathUtilities.clamp(0, width, 100);
               } else {
                  currentTable._specifiedTableWidth = width;
               }
            }

            int fullBorderWidth = currentTable._borderLeftWidth + currentTable._borderRightWidth;
            currentTable._availableWidth = availableWidth - fullBorderWidth;
            int calculatedTableWidth = Math.max(10, availableWidth);
            if (width != -1 && currentTable._fixed && !widthPercentage) {
               calculatedTableWidth = width;
            }

            if (currentTable._borderTopWidth == 0
               && currentTable._borderRightWidth == 0
               && currentTable._borderBottomWidth == 0
               && currentTable._borderLeftWidth == 0) {
               tableBorderMask = 0;
            }

            this._tableStack.push(currentTable);
            synchronized (this._appEventLock) {
               Border border = null;
               if (tableBorderMask != 0) {
                  if (!this._useColor || !borderLeftColorSet && !borderRightColorSet && !borderTopColorSet && !borderBottomColorSet) {
                     border = this.getBorder(
                        tableBorderMask,
                        currentTable._borderTopWidth,
                        currentTable._borderRightWidth,
                        currentTable._borderBottomWidth,
                        currentTable._borderLeftWidth,
                        IS_COLOR
                     );
                  } else {
                     border = this.getBorder(
                        tableBorderMask,
                        currentTable._borderTopWidth,
                        currentTable._borderRightWidth,
                        currentTable._borderBottomWidth,
                        currentTable._borderLeftWidth,
                        borderTopColor,
                        borderRightColor,
                        borderBottomColor,
                        borderLeftColor
                     );
                  }
               }

               this._currentBrowserField
                  .pushCell(calculatedTableWidth, currentTable._specifiedTableWidth, true, (currentTable._fixed ? 16 : 0) | halign, border, 0, 0, 1, 1, -1);
               this._currentContext._pushedCellCount++;
            }

            this.handleBackgroundImage(currentTable._backgroundURI, false, repeatX, repeatY, backgroundXPos, backgroundYPos);
            currentTable._dir = dir;
            currentTable._parentCell = this._currentBrowserField.getCurrentCell();
         }

         if (!isStartTag || !hasContent) {
            Table table = (Table)this._tableStack.peek();
            this._currentBrowserField.reflowCell(this._appEventLock, table._parentCell);
            synchronized (this._appEventLock) {
               this._currentBrowserField.popCell();
               this._currentContext._pushedCellCount--;
            }

            this._tableStack.pop();
            if (!isStartTag) {
               this.resetTextStyle(2);
            }
         }
      }
   }

   private final void processElementTr(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (this._tableStack == null) {
         if (isStartTag) {
            this._blockLevelBreakFlags = 320;
            element.setBreakingFlags((short)this._blockLevelBreakFlags);
         }
      } else {
         if (!isStartTag) {
            if (this._tableStack.size() > 0) {
               Table table = (Table)this._tableStack.peek();
               table.finishRow();
               synchronized (this._appEventLock) {
                  this._currentBrowserField.finishRow();
               }

               table._currentRowAlign = -1;
               table._currentRowURI = null;
               table._currentRowBgRepeatX = true;
               table._currentRowBgRepeatY = true;
               table._currentRowDir = -1;
            }

            this.resetTextStyle(2);
         } else {
            int rowAlign = -1;
            int rowValign = -1;
            int dir = this._lastDirection;
            String backgroundURI = null;
            boolean repeatX = true;
            boolean repeatY = true;
            int backgroundXPos = 0;
            int backgroundYPos = 0;

            for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
               switch (this._attributeItems[attribute] & 0xFF) {
                  case 8:
                  case 9:
                  case 10:
                     rowAlign = this._attributeItems[attribute] & 255;
                     break;
                  case 36:
                     rowValign = 0;
                     break;
                  case 37:
                     rowValign = 1536;
                     break;
                  case 38:
                     rowValign = 1024;
                     break;
                  case 39:
                     rowValign = 512;
                     break;
                  case 90:
                     backgroundURI = this.getAttributeValue(null, attribute);
                     break;
                  case 91:
                     if (this._useColor) {
                        element._backgroundColour = this.getAttributeValueAsColor(element._backgroundColour, false, attribute);
                     }
               }
            }

            int style = this.getStyle(92, this._lastClass, this._lastId, this._lastStyle);
            if (hasContent) {
               this.setTextStyle(style, 2);
            }

            if (style != -1) {
               short endIndex = this._styleTops[style + 1];

               for (short styleIndex = this._styleTops[style]; styleIndex < endIndex; styleIndex++) {
                  int flags = (int)(this._styleFlagsValues[styleIndex] >> 32 & 8388607);
                  int itemId = (int)(this._styleFlagsValues[styleIndex] >> 55 & 511);
                  int value = (int)(this._styleFlagsValues[styleIndex] & 4294967295L);
                  switch (itemId) {
                     case 4:
                        if (hasContent) {
                           this.handleTextStyle(element, 2, itemId, value, flags);
                        }

                        element._backgroundColour = value;
                        break;
                     case 5:
                        if (flags != 0 && (flags & 15) == 3) {
                           backgroundURI = (String)this.getStyleObject(value);
                        }
                        break;
                     case 6:
                        if ((flags & 15) == 1) {
                           int[] values = (int[])this.getStyleObject(value);
                           backgroundXPos = this.interpretSizeValue(element, values[1], values[0], true, 0);
                           if ((values[0] & 16) != 0) {
                              backgroundXPos |= 1073741824;
                           } else if (backgroundXPos < 0) {
                              backgroundXPos *= -1;
                              backgroundXPos |= Integer.MIN_VALUE;
                           }

                           backgroundYPos = this.interpretSizeValue(element, values[3], values[2], true, 0);
                           if ((values[2] & 16) != 0) {
                              backgroundYPos |= 1073741824;
                           } else if (backgroundYPos < 0) {
                              backgroundYPos *= -1;
                              backgroundYPos |= Integer.MIN_VALUE;
                           }
                        }
                        break;
                     case 7:
                        switch (value) {
                           case 96:
                              repeatX = false;
                              repeatY = false;
                              continue;
                           case 113:
                              repeatX = true;
                              repeatY = true;
                              continue;
                           case 114:
                              repeatX = true;
                              repeatY = false;
                              continue;
                           case 115:
                              repeatX = false;
                              repeatY = true;
                           default:
                              continue;
                        }
                     case 34:
                     case 48:
                     case 49:
                     case 50:
                     case 51:
                     case 52:
                     case 102:
                     case 104:
                        if (hasContent) {
                           this.handleTextStyle(element, 2, itemId, value, flags);
                        }
                        break;
                     case 42:
                        dir = value;
                        break;
                     case 101:
                        if (hasContent) {
                           this.handleTextStyle(element, 2, itemId, value, flags);
                        }

                        rowAlign = value;
                        break;
                     case 107:
                        if ((flags & 15) == 1) {
                           switch (value) {
                              case 8:
                                 rowValign = 512;
                                 break;
                              case 17:
                                 rowValign = 1024;
                                 break;
                              case 88:
                                 rowValign = 1536;
                                 break;
                              case 160:
                                 rowValign = 0;
                           }
                        }
                  }
               }
            }

            this._textUtilities.setFont(element);
            if (this._tableStack.size() > 0) {
               Table table = (Table)this._tableStack.peek();
               table._currentRowURI = backgroundURI;
               table._currentRowBgRepeatX = repeatX;
               table._currentRowBgRepeatY = repeatY;
               table._currentRowBgPosX = backgroundXPos;
               table._currentRowBgPosY = backgroundYPos;
               table._currentRowAlign = rowAlign;
               table._currentRowValign = rowValign;
               if (dir == -1) {
                  dir = table._dir;
               }

               table._currentRowDir = dir;
               return;
            }
         }
      }
   }

   private final void processElementTdTh(boolean isTH, boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (this._tableStack == null) {
         if (isStartTag) {
            synchronized (this._appEventLock) {
               this._textUtilities.appendText(element, SINGLE_SPACE);
            }
         }
      } else {
         if (isStartTag) {
            String backgroundURI = null;
            boolean repeatX = true;
            boolean repeatY = true;
            int width = -1;
            int height = -1;
            int dir = -1;
            boolean widthPercentage = false;
            int specifiedWidth = Integer.MIN_VALUE;
            int colspan = 1;
            int rowspan = 1;
            int textDataAlignment = -1;
            int valign = -1;
            int borderMask = 0;
            int borderTopWidth = 0;
            int borderRightWidth = 0;
            int borderBottomWidth = 0;
            int borderLeftWidth = 0;
            int borderTopColor = 0;
            int borderRightColor = 0;
            int borderBottomColor = 0;
            int borderLeftColor = 0;
            int cellPaddingLeft = 0;
            int cellPaddingTop = 0;
            int cellPaddingBottom = 0;
            int cellPaddingRight = 0;
            int cellFlags = 0;
            boolean borderLeftColorSet = false;
            boolean borderRightColorSet = false;
            boolean borderTopColorSet = false;
            boolean borderBottomColorSet = false;
            int backgroundXPos = 0;
            int backgroundYPos = 0;
            Table table = null;
            if (this._tableStack.size() > 0) {
               table = (Table)this._tableStack.peek();
               if (table != null) {
                  table.finishColumns();
                  repeatX = table._currentRowBgRepeatX;
                  repeatY = table._currentRowBgRepeatY;
                  backgroundXPos = table._currentRowBgPosX;
                  backgroundYPos = table._currentRowBgPosY;
                  borderMask = table._cellBorderMask;
                  borderTopWidth = (borderMask & 4) != 0 ? 1 : 0;
                  borderRightWidth = (borderMask & 2) != 0 ? 1 : 0;
                  borderBottomWidth = (borderMask & 8) != 0 ? 1 : 0;
                  borderLeftWidth = (borderMask & 1) != 0 ? 1 : 0;
                  cellPaddingRight = cellPaddingBottom = cellPaddingTop = cellPaddingLeft = MathUtilities.clamp(0, table._cellPadding, 255);
                  if (table._fixed) {
                     cellFlags |= 16;
                  }
               }
            }

            for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
               switch (this._attributeItems[attribute] & 0xFF) {
                  case 8:
                  case 9:
                  case 10:
                     textDataAlignment = this._attributeItems[attribute] & 255;
                     break;
                  case 36:
                     valign = 0;
                     break;
                  case 37:
                     valign = 1536;
                     break;
                  case 38:
                     valign = 1024;
                     break;
                  case 39:
                     valign = 512;
                     break;
                  case 90:
                     backgroundURI = this.getAttributeValue(null, attribute);
                     break;
                  case 91:
                     if (this._useColor) {
                        element._backgroundColour = this.getAttributeValueAsColor(element._backgroundColour, false, attribute);
                     }
                     break;
                  case 108:
                     colspan = this.getAttributeValueAsInt(1, attribute);
                     if (colspan <= 0) {
                        colspan = 1;
                     }
                     break;
                  case 124:
                     height = this.getAttributeValueAsPixels(-1, attribute);
                     break;
                  case 146:
                     cellFlags |= 128;
                     break;
                  case 172:
                     rowspan = this.getAttributeValueAsInt(1, attribute);
                     if (rowspan <= 0) {
                        rowspan = 1;
                     }
                     break;
                  case 198:
                     String value = this.getAttributeValue(null, attribute);
                     width = this.valueAsPercentage(value, -1, 100);
                     if (width == -1) {
                        width = this.valueAsPixels(value, -1);
                     } else {
                        widthPercentage = true;
                     }
               }
            }

            if (this._currentContext._allTagsProvided && isTH) {
               this._textUtilities.setFontWeight(element, 700);
            }

            int style = this.getStyle(isTH ? 89 : 86, this._lastClass, this._lastId, this._lastStyle);
            if (hasContent) {
               this.setTextStyle(style, 2);
            }

            if (style != -1) {
               short endIndex = this._styleTops[style + 1];

               for (short styleIndex = this._styleTops[style]; styleIndex < endIndex; styleIndex++) {
                  int flags = (int)(this._styleFlagsValues[styleIndex] >> 32 & 8388607);
                  int itemId = (int)(this._styleFlagsValues[styleIndex] >> 55 & 511);
                  int value = (int)(this._styleFlagsValues[styleIndex] & 4294967295L);
                  switch (itemId) {
                     case 4:
                        if (hasContent) {
                           this.handleTextStyle(element, 2, itemId, value, flags);
                        }

                        if (this._useColor) {
                           element._backgroundColour = value;
                        }
                        break;
                     case 5:
                        if ((flags & 15) == 3) {
                           backgroundURI = (String)this.getStyleObject(value);
                        }
                        break;
                     case 6:
                        if ((flags & 15) == 1) {
                           int[] values = (int[])this.getStyleObject(value);
                           backgroundXPos = this.interpretSizeValue(element, values[1], values[0], true, 0);
                           if ((values[0] & 16) != 0) {
                              backgroundXPos |= 1073741824;
                           } else if (backgroundXPos < 0) {
                              backgroundXPos *= -1;
                              backgroundXPos |= Integer.MIN_VALUE;
                           }

                           backgroundYPos = this.interpretSizeValue(element, values[3], values[2], true, 0);
                           if ((values[2] & 16) != 0) {
                              backgroundYPos |= 1073741824;
                           } else if (backgroundYPos < 0) {
                              backgroundYPos *= -1;
                              backgroundYPos |= Integer.MIN_VALUE;
                           }
                        }
                        break;
                     case 7:
                        switch (value) {
                           case 96:
                              repeatX = false;
                              repeatY = false;
                              continue;
                           case 113:
                              repeatX = true;
                              repeatY = true;
                              continue;
                           case 114:
                              repeatX = true;
                              repeatY = false;
                              continue;
                           case 115:
                              repeatX = false;
                              repeatY = true;
                           default:
                              continue;
                        }
                     case 10:
                        borderBottomColor = value;
                        borderBottomColorSet = true;
                        break;
                     case 11:
                        if (value != 97 && value != 52) {
                           borderMask |= 8;
                           if (borderBottomWidth == 0) {
                              borderBottomWidth = 1;
                           }
                        } else {
                           borderMask &= -9;
                        }
                        break;
                     case 12:
                        borderBottomWidth = this.interpretSizeValue(element, value, flags, false, borderBottomWidth);
                        break;
                     case 16:
                        borderLeftColor = value;
                        borderLeftColorSet = true;
                        break;
                     case 17:
                        if (value != 97 && value != 52) {
                           borderMask |= 1;
                           if (borderLeftWidth == 0) {
                              borderLeftWidth = 1;
                           }
                        } else {
                           borderMask &= -2;
                        }
                        break;
                     case 18:
                        borderLeftWidth = this.interpretSizeValue(element, value, flags, false, borderLeftWidth);
                        break;
                     case 20:
                        borderRightColor = value;
                        borderRightColorSet = true;
                        break;
                     case 21:
                        if (value != 97 && value != 52) {
                           borderMask |= 2;
                           if (borderRightWidth == 0) {
                              borderRightWidth = 1;
                           }
                        } else {
                           borderMask &= -3;
                        }
                        break;
                     case 22:
                        borderRightWidth = this.interpretSizeValue(element, value, flags, false, borderRightWidth);
                        break;
                     case 26:
                        borderTopColor = value;
                        borderTopColorSet = true;
                        break;
                     case 27:
                        if (value != 97 && value != 52) {
                           borderMask |= 4;
                           if (borderTopWidth == 0) {
                              borderTopWidth = 1;
                           }
                        } else {
                           borderMask &= -5;
                        }
                        break;
                     case 28:
                        borderTopWidth = this.interpretSizeValue(element, value, flags, false, borderTopWidth);
                        break;
                     case 34:
                     case 48:
                     case 49:
                     case 50:
                     case 51:
                     case 52:
                     case 102:
                     case 104:
                        if (hasContent) {
                           this.handleTextStyle(element, 2, itemId, value, flags);
                        }
                        break;
                     case 42:
                        dir = value;
                        break;
                     case 53:
                        height = this.interpretSizeValue(element, value, flags, false, height);
                        break;
                     case 77:
                        cellPaddingBottom = this.interpretSizeValue(element, value, flags, false, cellPaddingBottom);
                        break;
                     case 78:
                        cellPaddingLeft = this.interpretSizeValue(element, value, flags, false, cellPaddingLeft);
                        break;
                     case 79:
                        cellPaddingRight = this.interpretSizeValue(element, value, flags, false, cellPaddingRight);
                        break;
                     case 80:
                        cellPaddingTop = this.interpretSizeValue(element, value, flags, false, cellPaddingTop);
                        break;
                     case 101:
                        if (hasContent) {
                           this.handleTextStyle(element, 2, itemId, value, flags);
                        }

                        textDataAlignment = value;
                        break;
                     case 107:
                        if ((flags & 15) == 1) {
                           switch (value) {
                              case 8:
                                 valign = 512;
                                 break;
                              case 17:
                                 valign = 1024;
                                 break;
                              case 88:
                                 valign = 1536;
                                 break;
                              case 160:
                                 valign = 0;
                           }
                        }
                        break;
                     case 111:
                        if (value == 99) {
                           cellFlags |= 128;
                        }
                        break;
                     case 113:
                        width = this.interpretSizeValue(element, value, flags, true, width);
                        widthPercentage = (flags & 16) != 0;
                  }
               }
            }

            this._textUtilities.setFont(element);
            int currentTextColor = element._foregroundColour;
            if (!borderTopColorSet) {
               borderTopColor = currentTextColor;
            }

            if (!borderRightColorSet) {
               borderRightColor = currentTextColor;
            }

            if (!borderBottomColorSet) {
               borderBottomColor = currentTextColor;
            }

            if (!borderLeftColorSet) {
               borderLeftColor = currentTextColor;
            }

            if (table != null) {
               if (width != -1) {
                  if (widthPercentage) {
                     specifiedWidth = -MathUtilities.clamp(0, width, 100);
                  } else {
                     specifiedWidth = width;
                  }
               }

               int calculatedWidth = table._availableWidth;
               if (table._fixed) {
                  if (width != -1) {
                     if (specifiedWidth > 0) {
                        calculatedWidth = specifiedWidth;
                     } else if (table._specifiedTableWidth > 0) {
                        calculatedWidth = table._specifiedTableWidth * -1 * specifiedWidth / 100;
                     } else {
                        calculatedWidth = table._availableWidth * table._specifiedTableWidth * specifiedWidth / 10000 - table._cellPadding;
                     }
                  } else {
                     calculatedWidth = table.getNextSpecifiedWidth(colspan);
                  }
               }

               if (calculatedWidth < 0) {
                  calculatedWidth = table._availableWidth;
               }

               synchronized (this._appEventLock) {
                  if (textDataAlignment == -1) {
                     if (table._currentRowAlign != -1) {
                        textDataAlignment = table._currentRowAlign;
                     } else {
                        textDataAlignment = isTH ? 9 : 8;
                     }
                  }

                  if (valign != -1) {
                     cellFlags |= valign;
                  } else if (table._currentRowValign != -1) {
                     cellFlags |= table._currentRowValign;
                  }

                  if (dir == -1) {
                     dir = table._currentRowDir;
                  }

                  if (backgroundURI == null) {
                     backgroundURI = table._currentRowURI;
                  }

                  if (borderTopWidth == 0 && borderRightWidth == 0 && borderBottomWidth == 0 && borderLeftWidth == 0) {
                     borderMask = 0;
                  }

                  Border border = null;
                  if (borderMask != 0) {
                     if (!this._useColor || !borderLeftColorSet && !borderRightColorSet && !borderTopColorSet && !borderBottomColorSet) {
                        border = this.getBorder(borderMask, borderTopWidth, borderRightWidth, borderBottomWidth, borderLeftWidth, IS_COLOR);
                     } else {
                        border = this.getBorder(
                           borderMask,
                           borderTopWidth,
                           borderRightWidth,
                           borderBottomWidth,
                           borderLeftWidth,
                           borderTopColor,
                           borderRightColor,
                           borderBottomColor,
                           borderLeftColor
                        );
                     }
                  }

                  if (specifiedWidth == Integer.MIN_VALUE) {
                     specifiedWidth = table.getNextSpecifiedWidth(colspan);
                  }

                  int convertedPadding = cellPaddingLeft << 0 | cellPaddingRight << 8 | cellPaddingBottom << 24 | cellPaddingTop << 16;
                  this._currentBrowserField
                     .pushCell(calculatedWidth, specifiedWidth, false, cellFlags, border, table._cellSpacing, convertedPadding, colspan, rowspan, height);
                  this._textUtilities.setAlignment(element, textDataAlignment);
                  this._textUtilities.setDirection(element, dir, false);
                  this._currentContext._pushedCellCount++;
               }

               this.handleBackgroundImage(backgroundURI, false, repeatX, repeatY, 0, 0);
               table.addCell(colspan, calculatedWidth);
            }
         }

         if (!isStartTag || !hasContent) {
            if (this._tableStack.size() > 0) {
               synchronized (this._appEventLock) {
                  this._textUtilities.resetDirection(false);
                  this._currentBrowserField.popCell();
                  this._currentContext._pushedCellCount--;
               }
            }

            if (!isStartTag) {
               this.resetTextStyle(2);
            }

            if (this._currentContext._allTagsProvided && isTH) {
               this._textUtilities.resetFontWeight();
            }
         }
      }
   }

   private final void processElementColumn(boolean isStartTag, int startAttribute, int endAttribute) {
      if (this._tableStack != null) {
         if (isStartTag) {
            int cellWidth = -1;
            Table table = null;
            if (this._tableStack.size() > 0) {
               table = (Table)this._tableStack.peek();
            }

            for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
               if ((this._attributeItems[attribute] & 255) == 198) {
                  String value = this.getAttributeValue(null, attribute);
                  cellWidth = this.valueAsPercentage(value, -1, 100);
                  if (cellWidth == -1) {
                     cellWidth = this.valueAsPixels(value, 5);
                  } else {
                     cellWidth = -cellWidth;
                  }
               }
            }

            synchronized (this._appEventLock) {
               if (table != null) {
                  table.addColumn(cellWidth);
               }
            }
         }
      }
   }

   private final void processElementMap(boolean isStartTag, int startAttribute, int endAttribute) {
      if (isStartTag) {
         String name = null;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            if ((this._attributeItems[attribute] & 255) == 142) {
               name = this.getAttributeValue(null, attribute);
            }

            if (name == null || name.length() == 0) {
               name = this._lastId;
            }

            if (name != null && name.length() > 0) {
               this._currentImageMap = (ImageMap)this._imageMaps.get(name);
               if (this._currentImageMap == null) {
                  this._currentImageMap = new ImageMap(name);
               }
            }
         }
      } else if (this._currentImageMap != null) {
         this._currentImageMap.scale();
         this._imageMaps.put(this._currentImageMap.getName(), this._currentImageMap);
         this._currentImageMap = null;
      }
   }

   private final void processElementArea(boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag && this._currentImageMap != null && element instanceof HTMLAreaElement) {
         this._currentImageMap.addArea((HTMLAreaElement)element);
      }
   }

   private final void processElementBody(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (!isStartTag) {
         this.resetTextStyle(1);
         this._textUtilities.resetDirection(true);
      } else {
         int unvisitedLink = -1;
         int backgroundColor = -1;
         String backgroundURI = null;
         boolean backgroundFixed = false;
         boolean repeatX = true;
         boolean repeatY = true;
         int backgroundXPos = 0;
         int backgroundYPos = 0;
         int dir = -1;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            switch (this._attributeItems[attribute] & 0xFF) {
               case 17:
                  dir = 0;
                  break;
               case 18:
                  dir = 8;
                  break;
               case 61:
                  backgroundFixed = true;
                  break;
               case 90:
                  backgroundURI = this.getAttributeValue(null, attribute);
                  break;
               case 91:
                  if (this._useColor) {
                     backgroundColor = this.getAttributeValueAsColor(backgroundColor, false, attribute);
                  }
                  break;
               case 134:
                  if (this._useColor) {
                     unvisitedLink = this.getAttributeValueAsColor(unvisitedLink, false, attribute);
                  }
                  break;
               case 156:
                  this._onLoadScript = this.getAttributeValue(null, attribute);
                  break;
               case 188:
                  if (this._useColor) {
                     element._foregroundColour = this.getAttributeValueAsColor(element._foregroundColour, false, attribute);
                  }
            }
         }

         this._textUtilities.setDefaultColors(unvisitedLink);
         int style = this.getStyle(17, this._lastClass, this._lastId, this._lastStyle);
         if (hasContent) {
            this.setTextStyle(style, 1);
         }

         if (style != -1) {
            short endIndex = this._styleTops[style + 1];

            for (short styleIndex = this._styleTops[style]; styleIndex < endIndex; styleIndex++) {
               int flags = (int)(this._styleFlagsValues[styleIndex] >> 32 & 8388607);
               int itemId = (int)(this._styleFlagsValues[styleIndex] >> 55 & 511);
               int value = (int)(this._styleFlagsValues[styleIndex] & 4294967295L);
               switch (itemId) {
                  case 3:
                     backgroundFixed = value == 48;
                     break;
                  case 4:
                     if (hasContent) {
                        this.handleTextStyle(element, 1, itemId, value, flags);
                     }

                     backgroundColor = value;
                     break;
                  case 5:
                     if ((flags & 15) == 3) {
                        backgroundURI = (String)this.getStyleObject(value);
                     }
                     break;
                  case 6:
                     if ((flags & 15) == 1) {
                        int[] values = (int[])this.getStyleObject(value);
                        backgroundXPos = this.interpretSizeValue(element, values[1], values[0], true, 0);
                        if ((values[0] & 16) != 0) {
                           backgroundXPos |= 1073741824;
                        } else if (backgroundXPos < 0) {
                           backgroundXPos *= -1;
                           backgroundXPos |= Integer.MIN_VALUE;
                        }

                        backgroundYPos = this.interpretSizeValue(element, values[3], values[2], true, 0);
                        if ((values[2] & 16) != 0) {
                           backgroundYPos |= 1073741824;
                        } else if (backgroundYPos < 0) {
                           backgroundYPos *= -1;
                           backgroundYPos |= Integer.MIN_VALUE;
                        }
                     }
                     break;
                  case 7:
                     switch (value) {
                        case 96:
                           repeatX = false;
                           repeatY = false;
                           continue;
                        case 113:
                           repeatX = true;
                           repeatY = true;
                           continue;
                        case 114:
                           repeatX = true;
                           repeatY = false;
                           continue;
                        case 115:
                           repeatX = false;
                           repeatY = true;
                        default:
                           continue;
                     }
                  case 34:
                  case 48:
                  case 49:
                  case 50:
                  case 51:
                  case 52:
                  case 101:
                  case 102:
                  case 104:
                     if (hasContent) {
                        this.handleTextStyle(element, 1, itemId, value, flags);
                     }
                     break;
                  case 42:
                     if (hasContent) {
                        this.handleTextStyle(element, 1, itemId, value, flags);
                     }

                     dir = value;
               }
            }
         }

         if (backgroundColor != -1) {
            element._backgroundColour = backgroundColor;
            this._coreDocElement._backgroundColour = backgroundColor;
         }

         this._textUtilities.adjustToContrastColour(element);
         this._textUtilities.setFont(element);
         if (backgroundURI != null) {
            this.handleBackgroundImage(backgroundURI, backgroundFixed, repeatX, repeatY, backgroundXPos, backgroundYPos);
         }

         this._textUtilities.setDirection(element, dir, true);
         if (dir != -1 && super._renderingApplication != null) {
            super._renderingApplication.eventOccurred(new UIDirectionChangeEvent(dir == 0 ? 0 : 1, this._browserContent));
            return;
         }
      }
   }

   private final void processElementFieldset(boolean hasContent, boolean isStartTag, HTMLGenericElement element) {
      HTMLGenericElement newElement = new HTMLBaseGenericElement(4, null, 0);
      newElement.inherit(element);
      if (isStartTag) {
         if (hasContent) {
            this.setBlockStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.setFont(element);
         synchronized (this._appEventLock) {
            this._currentBrowserField.pushRegion(newElement);
            this.appendField(new HorizontalRuleField(100, true, 0, 2, true, 0, -1, 2), false, false);
            this._currentBrowserField.popRegion();
         }
      } else {
         synchronized (this._appEventLock) {
            this._currentBrowserField.pushRegion(newElement);
            this.appendField(new HorizontalRuleField(100, true, 0, 2, true, 0, -1, 2), false, false);
            this._currentBrowserField.popRegion();
         }

         this.resetBlockStyle(element);
      }
   }

   private final void processElementLegend(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag) {
         if (hasContent) {
            this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.setFont(element);
      } else {
         HTMLGenericElement newElement = new HTMLBaseGenericElement(4, null, 0);
         newElement.inherit(element);
         synchronized (this._appEventLock) {
            this._currentBrowserField.pushRegion(newElement);
            this.appendField((Field)(new Object()), false, false);
            this._currentBrowserField.popRegion();
         }

         this.resetTextStyle();
      }
   }

   private final void processElementBStrong(int tag, boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag) {
         this._textUtilities.setFontWeight(element, 700);
         if (hasContent) {
            this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.setFont(element);
      } else {
         this.resetTextStyle();
         this._textUtilities.resetFontWeight();
      }
   }

   private final void processElementBasefont(boolean isStartTag, int startAttribute, int endAttribute) {
      if (isStartTag) {
         int size = -1;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            if ((this._attributeItems[attribute] & 255) == 179) {
               size = this.getAttributeValueAsPixels(-1, attribute);
            }

            if (size != -1) {
               size = this.mapHTMLFontSizesToCSS(size);
               HTMLGenericElement parentElement = (HTMLGenericElement)this._tagStack.elementAt(this._tagStack.size() - 2);
               this._textUtilities.setFontSize(parentElement, size, false);
               this._textUtilities.setFont(parentElement);
            }
         }
      }
   }

   private final void processElementBigSmall(int tag, boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag) {
         switch (tag) {
            case 15:
               this._textUtilities.setFontSize(element, 5);
               break;
            case 77:
               this._textUtilities.setFontSize(element, 2);
         }

         if (hasContent) {
            this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.setFont(element);
      } else {
         this.resetTextStyle();
      }
   }

   private final void processElementDl(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag) {
         if (this._breakAppended) {
            element.setBreakingFlags((short)384);
         } else {
            element.setBreakingFlags((short)320);
            this._breakAppended = true;
         }

         if (hasContent) {
            this.setBlockStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.setFont(element);
      } else {
         this.resetBlockStyle(element);
      }
   }

   private final void processElementBlockquoteBrDdDt(
      int tag, boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element
   ) {
      if (isStartTag) {
         if (this._breakAppended) {
            element.setBreakingFlags((short)128);
         } else {
            element.setBreakingFlags((short)64);
            this._breakAppended = true;
         }

         if (hasContent && tag != 18) {
            this.setBlockStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         if (hasContent && (tag == 26 || tag == 16)) {
            element._margin = (short)(element._margin + 10);
         }

         this._textUtilities.setFont(element);
      } else {
         if (tag != 18) {
            this.resetBlockStyle(element);
         }
      }
   }

   private final void processElementCenter(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag) {
         this._blockLevelBreakFlags = 320;
         element.setBreakingFlags((short)this._blockLevelBreakFlags);
         this._textUtilities.setAlignment(element, 9);
         if (hasContent) {
            this.setBlockStyle(element, this._lastClass, this._lastId, this._lastStyle, 9);
         }

         this._textUtilities.setFont(element);
      } else {
         this.resetBlockStyle(element);
      }
   }

   private final void processElementAddressCiteDfnEmIVar(
      int tag, boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element
   ) {
      if (tag == 8 && isStartTag) {
         this._blockLevelBreakFlags = 320;
         element.setBreakingFlags((short)this._blockLevelBreakFlags);
      }

      if (isStartTag) {
         this._textUtilities.setFontStyle(element, 1);
         if (hasContent) {
            if (tag == 8) {
               this.setBlockStyle(element, this._lastClass, this._lastId, this._lastStyle);
            } else {
               this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
            }
         }

         this._textUtilities.setFont(element);
      } else if (tag == 8) {
         this.resetBlockStyle(element);
      } else {
         this.resetTextStyle();
      }
   }

   private final void processElementCodeKbdPreSampTt(
      int tag, boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element
   ) {
      if (tag == 71 && isStartTag) {
         this._blockLevelBreakFlags = 320;
         element.setBreakingFlags((short)this._blockLevelBreakFlags);
      }

      if (isStartTag) {
         this._textUtilities.setFontFace(element, "monospace");
         if (hasContent) {
            if (tag == 71) {
               this.setBlockStyle(element, this._lastClass, this._lastId, this._lastStyle);
            } else {
               this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
            }
         }

         this._textUtilities.setFont(element);
      } else if (tag == 71) {
         this.resetBlockStyle(element);
      } else {
         this.resetTextStyle();
      }
   }

   private final void processElementQ(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag) {
         if (hasContent) {
            this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.setFont(element);
      }

      synchronized (this._appEventLock) {
         this._textUtilities.appendText(element, this._inFirstQ ? (isStartTag ? "‘" : "”") : (isStartTag ? "“" : "’"));
      }

      this._inFirstQ = !this._inFirstQ;
      if (!isStartTag) {
         this.resetTextStyle();
      }
   }

   private final void processElementDirMenuOlUl(
      int tag, boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element
   ) {
      if (!isStartTag) {
         this._textUtilities.endList();
         this.resetBlockStyle(element);
      } else {
         int start = 1;
         int numberType = 34;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            switch (this._attributeItems[attribute] & 0xFF) {
               case 183:
                  start = this.getAttributeValueAsInt(1, attribute);
                  break;
               case 190:
                  String typeStr = this.getAttributeValue(null, attribute);
                  if (typeStr != null) {
                     if (typeStr.length() == 1) {
                        switch (typeStr.charAt(0)) {
                           case '1':
                              numberType = 32;
                              break;
                           case 'A':
                              numberType = 164;
                              break;
                           case 'I':
                              numberType = 165;
                              break;
                           case 'a':
                              numberType = 80;
                              break;
                           case 'i':
                              numberType = 81;
                        }
                     } else if (StringUtilities.strEqualIgnoreCase(typeStr, "disc", 1701707776)) {
                        numberType = 36;
                     } else if (StringUtilities.strEqualIgnoreCase(typeStr, "square", 1701707776)) {
                        numberType = 140;
                     } else if (StringUtilities.strEqualIgnoreCase(typeStr, "circle", 1701707776)) {
                        numberType = 24;
                     }
                  }
            }
         }

         this._lastListStyleType = 34;
         this._lastListStyleImage = this._textUtilities.getLastListImage();
         if (hasContent) {
            this.setBlockStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.setFont(element);
         if (this._lastListStyleType != 34) {
            numberType = this._lastListStyleType;
         }

         int minTabValue;
         switch (tag) {
            case 29:
            case 60:
               minTabValue = 0;
               break;
            default:
               minTabValue = 1;
         }

         if (this._textUtilities.startList(tag, numberType, this._lastListStyleImage, start) > minTabValue) {
            element._margin = (short)(element._margin + 10);
            this._blockLevelBreakFlags = 64;
         } else {
            this._blockLevelBreakFlags = 320;
         }

         element.setBreakingFlags((short)this._blockLevelBreakFlags);
      }
   }

   private final void processElementDiv(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag) {
         this._blockLevelBreakFlags = 320;
         element.setBreakingFlags((short)this._blockLevelBreakFlags);
         int alignment = -1;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            switch (this._attributeItems[attribute] & 0xFF) {
               case 5:
               case 6:
               case 7:
               case 8:
               case 9:
               case 10:
               case 11:
               case 12:
               default:
                  alignment = this._attributeItems[attribute] & 255;
                  break;
               case 4:
            }
         }

         this._textUtilities.setAlignment(element, alignment);
         if (hasContent) {
            this.setBlockStyle(element, this._lastClass, this._lastId, this._lastStyle, alignment);
         }

         this._textUtilities.setFont(element);
      } else {
         this.resetBlockStyle(element);
      }
   }

   private final void processElementEmbed(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag) {
         String src = null;
         String type = null;
         boolean hidden = false;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            switch (this._attributeItems[attribute] & 0xFF) {
               case 62:
                  hidden = true;
                  break;
               case 181:
                  src = this.getAttributeValue(null, attribute);
                  break;
               case 190:
                  type = this.getAttributeValue(null, attribute);
            }
         }

         if (!hidden) {
            if (type != null && StringUtilities.startsWithIgnoreCase(type, "application/", 1701707776)) {
               int typeStart = 12;
               if (StringUtilities.regionMatches(type, true, typeStart, "x-", 0, 2, 1701707776)) {
                  typeStart += 2;
               }

               if (StringUtilities.regionMatches(type, true, typeStart, "shockwave-flash", 0, 15, 1701707776)) {
                  type = "Flash";
               } else {
                  if (StringUtilities.regionMatches(type, true, typeStart, "vnd.", 0, 4, 1701707776)) {
                     typeStart += 4;
                  }

                  if (typeStart < type.length()) {
                     type = type.substring(typeStart);
                  }
               }
            }

            int typeLength = type != null ? type.length() : 0;
            String altText = this.generateAltText(src, typeLength == 0);
            int altTextLength = altText != null ? altText.length() : 0;
            if (altTextLength > 0 || typeLength > 0) {
               StringBuffer embedText = (StringBuffer)(new Object(typeLength + 4 + altTextLength));
               embedText.append('[');
               if (typeLength > 0) {
                  embedText.append(type);
                  if (altTextLength > 0) {
                     embedText.append(": ");
                  }
               }

               if (altTextLength > 0) {
                  embedText.append(altText);
               }

               embedText.append(']');
               synchronized (this._appEventLock) {
                  this._textUtilities.appendText(element, embedText.toString());
                  return;
               }
            }
         }
      }
   }

   private final void processElementFont(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (!isStartTag) {
         this.resetTextStyle();
      } else {
         int size = 0;
         int color = 0;
         String face = null;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            switch (this._attributeItems[attribute] & 0xFF) {
               case 106:
                  if (!this._useColor) {
                     break;
                  }

                  try {
                     color = this.getAttributeValueAsColor(color, true, attribute);
                     element._foregroundColour = color;
                     break;
                  } finally {
                     break;
                  }
               case 119:
                  face = this.getAttributeValue(null, attribute);
                  if (face != null) {
                     this._textUtilities.setFontFace(element, face);
                  }
                  break;
               case 179:
                  int amount = Integer.MAX_VALUE;
                  boolean isAbsolute = false;
                  if (this._attributeValues[attribute] == null) {
                     amount = this._attributeValuesInts[attribute];
                     isAbsolute = amount >= 0;
                  } else if (this._attributeValues[attribute] != this._attributeValues) {
                     String value = (String)this._attributeValues[attribute];
                     if (value != null) {
                        int length = value.length();
                        isAbsolute = length == 1;
                        if (isAbsolute || length == 2) {
                           label153:
                           try {
                              if (value.charAt(0) == '+') {
                                 amount = Integer.parseInt(value.substring(1));
                              } else {
                                 amount = Integer.parseInt(value);
                              }
                           } finally {
                              break label153;
                           }
                        }
                     }
                  }

                  if (amount != Integer.MAX_VALUE) {
                     size = isAbsolute ? amount : 3 + amount;
                     size = this.mapHTMLFontSizesToCSS(size);
                     this._textUtilities.setFontSize(element, size);
                  }
            }
         }

         if (hasContent) {
            this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.adjustToContrastColour(element);
         this._textUtilities.setFont(element);
      }
   }

   private final int mapHTMLFontSizesToCSS(int size) {
      switch (size) {
         case 0:
            return size;
         case 1:
         default:
            return 0;
         case 2:
            return 2;
         case 3:
            return 3;
         case 4:
            return 4;
         case 5:
            return 5;
         case 6:
            return 6;
         case 7:
            return 7;
      }
   }

   private final void processElementHn(int tag, boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (!isStartTag) {
         this.resetBlockStyle(element);
         this._textUtilities.resetFontWeight();
      } else {
         this._blockLevelBreakFlags = 320;
         element.setBreakingFlags((short)this._blockLevelBreakFlags);
         int alignment = -1;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            switch (this._attributeItems[attribute] & 0xFF) {
               case 5:
               case 6:
               case 7:
               case 8:
               case 9:
               case 10:
               case 11:
               case 12:
               default:
                  alignment = this._attributeItems[attribute] & 255;
                  break;
               case 4:
            }
         }

         this._textUtilities.setAlignment(element, alignment);
         this._textUtilities.setFontWeight(element, 700);
         this._browserContent.addHeading();
         switch (tag) {
            case 38:
               break;
            case 39:
            default:
               this._textUtilities.setFontSize(element, 6);
               break;
            case 40:
               this._textUtilities.setFontSize(element, 5);
               break;
            case 41:
               this._textUtilities.setFontSize(element, 4);
               break;
            case 42:
               this._textUtilities.setFontSize(element, 3);
               break;
            case 43:
               this._textUtilities.setFontSize(element, 2);
               break;
            case 44:
               this._textUtilities.setFontSize(element, 1);
         }

         if (hasContent) {
            this.setBlockStyle(element, this._lastClass, this._lastId, this._lastStyle, alignment);
         }

         this._textUtilities.setFont(element);
      }
   }

   private final void processElementLi(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (!isStartTag) {
         if (this._styleStack.size() == 0 || this.isBlockStyle(this._styleStack.peek(), true)) {
            this.resetBlockStyle(element);
            return;
         }

         this.resetTextStyle();
      } else {
         this._blockLevelBreakFlags = 64;
         element.setBreakingFlags((short)this._blockLevelBreakFlags);
         int value = Integer.MAX_VALUE;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            if ((this._attributeItems[attribute] & 255) == 193) {
               value = this.getAttributeValueAsInt(Integer.MAX_VALUE, attribute);
            }
         }

         this._lastListStyleType = 34;
         this._lastListStyleImage = this._textUtilities.getLastListImage();
         if (hasContent) {
            int style = this.getStyle(element.getTagNameInt(), this._lastClass, this._lastId, this._lastStyle);
            if (style != -1 && !this.isBlockStyle(style, true)) {
               this._lastListStyleType = 97;
               this._blockLevelBreakFlags = 0;
               this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
            } else {
               this.setBlockStyle(element, this._lastClass, this._lastId, this._lastStyle);
            }
         }

         this._textUtilities.setFont(element);
         String alt = this._textUtilities.addListItem(value, this._lastListStyleType, this._lastListStyleImage == null);
         if (this._lastListStyleImage != null) {
            HTMLLi liElement = (HTMLLi)element;
            liElement.setSrc(this._lastListStyleImage);
            liElement.setAlt(alt);
            Field field = this.makeBitmapField(liElement, null, alt);
            if (field != null) {
               HTMLGenericElement newElement = new HTMLBaseGenericElement(4, null, 0);
               newElement.inherit(element);
               synchronized (this._appEventLock) {
                  this._currentBrowserField.pushRegion(newElement);
                  this.appendField(field, true, false);
                  this._currentBrowserField.popRegion();
                  return;
               }
            }
         }
      }
   }

   private final void processElementP(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (!isStartTag) {
         this.resetBlockStyle(element);
      } else {
         int alignment = -1;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            switch (this._attributeItems[attribute] & 0xFF) {
               case 5:
               case 6:
               case 7:
               case 8:
               case 9:
               case 10:
               case 11:
               case 12:
               default:
                  alignment = this._attributeItems[attribute] & 255;
                  break;
               case 4:
            }
         }

         if (this._blockLevelBreakFlags != 0) {
            if ((this._blockLevelBreakFlags & 512) == 0) {
               element.setBreakingFlags((short)512);
            } else {
               element.setBreakingFlags((short)256);
            }

            this._blockLevelBreakFlags = 640;
         } else {
            this._blockLevelBreakFlags = 640;
            element.setBreakingFlags((short)this._blockLevelBreakFlags);
         }

         this._textUtilities.setAlignment(element, alignment);
         if (hasContent) {
            this.setBlockStyle(element, this._lastClass, this._lastId, this._lastStyle, alignment);
         }

         this._textUtilities.setFont(element);
      }
   }

   private final void processElementDelSStrike(
      int tag, boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element
   ) {
      if (isStartTag) {
         this._textUtilities.setTextDecoration(element, 4);
         if (hasContent) {
            this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.setFont(element);
      } else {
         this.resetTextStyle();
         this._textUtilities.resetTextDecoration();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void processElementStyle(boolean isStartTag, int startAttribute, int endAttribute) {
      if (!isStartTag) {
         if (this._validStyleSheet) {
            this.loadingStatusEventOccurred(4);
            this.parseTextStyleSheet();
            this.loadingStatusEventOccurred(0);
            return;
         }

         this._currentContext._currentStringRef = -1;
      } else {
         String type = null;
         String media = null;
         String title = null;
         String src = null;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            switch (this._attributeItems[attribute] & 0xFF) {
               case 139:
                  media = this.getAttributeValue(null, attribute);
                  break;
               case 181:
                  src = this.getAttributeValue(null, attribute);
                  break;
               case 189:
                  title = this.getAttributeValue(null, attribute);
                  break;
               case 190:
                  type = this.getAttributeValue(null, attribute);
            }
         }

         this._validStyleSheet = this._processStyleSheets && this.isValidStyleSheet(type, media, title);
         if (this._validStyleSheet && src != null) {
            Object object = this._inlineData.get(src);
            if (object instanceof InlineDataRefHolder) {
               InlineDataRefHolder styleSheet = (InlineDataRefHolder)object;
               this.loadingStatusEventOccurred(4);
               if (StringUtilities.strEqualIgnoreCase(styleSheet._str, "text/css", 1701707776)) {
                  String textStyleSheet;
                  if (styleSheet._encoding == null) {
                     textStyleSheet = (String)(new Object(styleSheet._data, styleSheet._offset, styleSheet._length));
                  } else {
                     boolean var13 = false /* VF: Semaphore variable */;

                     label82:
                     try {
                        var13 = true;
                        textStyleSheet = (String)(new Object(styleSheet._data, styleSheet._offset, styleSheet._length, styleSheet._encoding));
                        var13 = false;
                     } finally {
                        if (var13) {
                           textStyleSheet = (String)(new Object(styleSheet._data, styleSheet._offset, styleSheet._length));
                           break label82;
                        }
                     }
                  }

                  this.parseTextStyleSheet(textStyleSheet, null);
               } else if (StringUtilities.strEqualIgnoreCase(styleSheet._str, "application/vnd.rim.css", 1701707776)) {
                  this.parseBinaryStyleSheet(Arrays.copy(styleSheet._data, styleSheet._offset, styleSheet._length), null);
               }

               this.loadingStatusEventOccurred(0);
               this._validStyleSheet = false;
               return;
            }
         }
      }
   }

   private final void processElementSub(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag) {
         this._textUtilities.setSubscript(true);
         if (hasContent) {
            this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.setFont(element);
      } else {
         this.resetTextStyle();
         this._textUtilities.setSubscript(false);
      }
   }

   private final void processElementSup(boolean hasContent, boolean isStartTag, HTMLGenericElement element) {
      if (isStartTag) {
         this._textUtilities.setSuperscript(true);
         if (hasContent) {
            this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.setFont(element);
      } else {
         this.resetTextStyle();
         this._textUtilities.setSuperscript(false);
      }
   }

   private final void processElementU(boolean hasContent, boolean isStartTag, HTMLGenericElement element) {
      if (isStartTag) {
         this._textUtilities.setTextDecoration(element, 1);
         if (hasContent) {
            this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.setFont(element);
      } else {
         this.resetTextStyle();
         this._textUtilities.resetTextDecoration();
      }
   }

   private final void processElementMarquee(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag) {
         int delay = 85;
         int amount = 6;
         int style = 0;
         int loop = Integer.MAX_VALUE;
         int width = -1;
         boolean widthPercentage = false;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            switch (this._attributeItems[attribute] & 0xFF) {
               case 54:
                  style |= 2;
                  break;
               case 55:
                  style |= 4;
                  if (loop == Integer.MAX_VALUE) {
                     loop = 1;
                  }
                  break;
               case 56:
                  style |= 8;
                  break;
               case 57:
                  style |= 64;
                  break;
               case 58:
                  style |= 32;
                  break;
               case 91:
                  if (this._useColor) {
                     element._backgroundColour = this.getAttributeValueAsColor(-1, false, attribute);
                  }
                  break;
               case 198:
                  String value = this.getAttributeValue(null, attribute);
                  width = this.valueAsPercentage(value, -1, 100);
                  if (width == -1) {
                     width = this.valueAsPixels(value, -1);
                  } else {
                     widthPercentage = true;
                  }
                  break;
               case 202:
                  loop = this.getAttributeValueAsInt(Integer.MAX_VALUE, attribute);
                  break;
               case 203:
                  amount = this.getAttributeValueAsPixels(6, attribute);
                  break;
               case 204:
                  delay = this.getAttributeValueAsInt(85, attribute);
            }
         }

         if (width == -1) {
            width = -100;
         } else if (widthPercentage) {
            width = -width;
         }

         if ((style & 96) == 0) {
            style |= 32;
         }

         if ((style & 14) == 0) {
            style |= 2;
         }

         synchronized (this._appEventLock) {
            this._currentBrowserField.pushCell(width, style, delay, amount, loop, null);
            this._currentContext._pushedCellCount++;
         }

         if (hasContent) {
            this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.setFont(element);
      }

      if (!isStartTag || !hasContent) {
         synchronized (this._appEventLock) {
            this._currentBrowserField.popCell();
            this._currentContext._pushedCellCount--;
         }

         if (!isStartTag) {
            this.resetTextStyle();
         }
      }
   }

   private final void processElementBlink(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLGenericElement element) {
      if (isStartTag) {
         synchronized (this._appEventLock) {
            this._currentBrowserField.pushCell(-100, 16, 500, 0, Integer.MAX_VALUE, null);
            this._currentContext._pushedCellCount++;
         }

         if (hasContent) {
            this.setTextStyle(element, this._lastClass, this._lastId, this._lastStyle);
         }

         this._textUtilities.setFont(element);
      }

      if (!isStartTag || !hasContent) {
         synchronized (this._appEventLock) {
            this._currentBrowserField.popCell();
            this._currentContext._pushedCellCount--;
         }

         if (!isStartTag) {
            this.resetTextStyle();
         }
      }
   }

   private final void processElementNoembedNoframes(boolean isStartTag) {
      if (isStartTag) {
         if (this._skipElementStackValue <= 0) {
            this._skipElementStackValue = this._tagStack.size();
            return;
         }
      } else if (this._tagStack.size() < this._skipElementStackValue) {
         this._skipElementStackValue = 0;
      }
   }

   private final void processElementObject(boolean hasContent, boolean isStartTag, int startAttribute, int endAttribute, HTMLElement objectElement) {
      if (!isStartTag) {
         if (this._tagStack.size() < this._skipElementStackValue) {
            this._skipElementStackValue = 0;
         }
      } else {
         this.handleObject();
         if (this._skipElementStackValue <= 0) {
            this._currentContext._currentObject = (HTMLObject)objectElement;
            long horizontalStyle = 0;
            long verticalStyle = 0;
            if (this._textUtilities.getAlignment() == 9) {
               horizontalStyle = 12884901888L;
            }

            for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
               int type = this._attributeItems[attribute] & 255;
               switch (type) {
                  case 4:
                  case 9:
                     break;
                  case 5:
                  case 6:
                  case 7:
                     verticalStyle = this.mapAttributeValueAlignmentToField(type);
                     break;
                  case 8:
                  case 10:
                  default:
                     horizontalStyle = this.mapAttributeValueAlignmentToField(type);
               }
            }

            int style = this.getStyle(64, this._lastClass, this._lastId, this._lastStyle);
            if (style != -1) {
               short endIndex = this._styleTops[style + 1];

               for (short styleIndex = this._styleTops[style]; styleIndex < endIndex; styleIndex++) {
                  int flags = (int)(this._styleFlagsValues[styleIndex] >> 32 & 8388607);
                  int itemId = (int)(this._styleFlagsValues[styleIndex] >> 55 & 511);
                  int value = (int)(this._styleFlagsValues[styleIndex] & 4294967295L);
                  switch (itemId) {
                     case 46:
                        switch (value) {
                           case 69:
                              horizontalStyle = 4294967296L;
                              continue;
                           case 97:
                              horizontalStyle = 0;
                              continue;
                           case 117:
                              horizontalStyle = 8589934592L;
                           default:
                              continue;
                        }
                     case 107:
                        if ((flags & 15) == 1) {
                           switch (value) {
                              case 8:
                                 verticalStyle = 0;
                                 break;
                              case 17:
                                 verticalStyle = 34359738368L;
                                 break;
                              case 88:
                                 verticalStyle = 51539607552L;
                                 break;
                              case 160:
                                 verticalStyle = 17179869184L;
                           }
                        }
                  }
               }
            }

            this._currentContext._currentObject.setFieldStyle(horizontalStyle | verticalStyle);
            if (!hasContent) {
               this.handleObject();
               return;
            }

            this._skipElementStackValue = this._tagStack.size();
            return;
         }
      }
   }

   private final void handleObject() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentContext Lnet/rim/device/apps/internal/browser/html/HTMLRendererContext;
      // 004: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._currentObject Lnet/rim/device/apps/internal/browser/html/HTMLObject;
      // 007: astore 1
      // 008: aload 1
      // 009: ifnonnull 00f
      // 00c: goto 35f
      // 00f: bipush 0
      // 010: istore 2
      // 011: aload 1
      // 012: invokevirtual net/rim/device/apps/internal/browser/html/HTMLObject.getData ()Ljava/lang/String;
      // 015: astore 3
      // 016: aload 1
      // 017: invokevirtual net/rim/device/apps/internal/browser/html/HTMLObject.getType ()Ljava/lang/String;
      // 01a: astore 4
      // 01c: aload 1
      // 01d: invokevirtual net/rim/device/apps/internal/browser/html/HTMLObject.getFieldStyle ()J
      // 020: lstore 5
      // 022: aload 3
      // 023: ifnull 039
      // 026: aload 3
      // 027: ldc_w "pict://"
      // 02a: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 02d: ifeq 039
      // 030: aload 0
      // 031: aload 3
      // 032: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.handlePictogram (Ljava/lang/String;)Z
      // 035: istore 2
      // 036: goto 34e
      // 039: aload 4
      // 03b: ifnonnull 041
      // 03e: goto 1c5
      // 041: aload 4
      // 043: ldc_w "image/"
      // 046: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 049: ifne 04f
      // 04c: goto 1c5
      // 04f: aload 3
      // 050: ifnonnull 056
      // 053: goto 34e
      // 056: aload 0
      // 057: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingOptions Lnet/rim/device/api/browser/field/RenderingOptions;
      // 05a: ldc2_w 4550690918222697397
      // 05d: bipush 5
      // 05f: bipush 1
      // 060: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithBooleanValue (JIZ)Z
      // 063: ifne 069
      // 066: goto 34e
      // 069: aload 0
      // 06a: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 06d: ifnonnull 073
      // 070: goto 34e
      // 073: aload 0
      // 074: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // 077: aload 3
      // 078: invokevirtual net/rim/device/apps/internal/browser/page/BrowserContentImpl.resolveUrl (Ljava/lang/String;)Ljava/lang/String;
      // 07b: astore 3
      // 07c: aload 1
      // 07d: sipush 181
      // 080: aload 3
      // 081: invokevirtual net/rim/device/apps/internal/browser/html/HTMLGenericElement.setAttributeValue (ILjava/lang/String;)V
      // 084: new java/lang/Object
      // 087: dup
      // 088: aload 3
      // 089: aload 0
      // 08a: bipush 0
      // 08b: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.getRequestHeaders (Z)Lnet/rim/device/api/io/http/HttpHeaders;
      // 08e: aload 0
      // 08f: getfield net/rim/device/apps/internal/browser/page/Renderer._flags I
      // 092: sipush 255
      // 095: iand
      // 096: invokespecial net/rim/device/api/browser/field/RequestedResource.<init> (Ljava/lang/String;Lnet/rim/device/api/io/http/HttpHeaders;I)V
      // 099: astore 7
      // 09b: aload 0
      // 09c: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 09f: aload 7
      // 0a1: aconst_null
      // 0a2: invokeinterface net/rim/device/api/browser/field/RenderingApplication.getResource (Lnet/rim/device/api/browser/field/RequestedResource;Lnet/rim/device/api/browser/field/BrowserContent;)Ljavax/microedition/io/HttpConnection; 3
      // 0a7: astore 8
      // 0a9: aconst_null
      // 0aa: astore 9
      // 0ac: aload 8
      // 0ae: ifnonnull 0b4
      // 0b1: goto 17f
      // 0b4: aload 8
      // 0b6: invokeinterface javax/microedition/io/HttpConnection.getResponseCode ()I 1
      // 0bb: sipush 200
      // 0be: if_icmpeq 0c4
      // 0c1: goto 17f
      // 0c4: aload 8
      // 0c6: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 0cb: astore 9
      // 0cd: aload 9
      // 0cf: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.readBytesFromInputStream (Ljava/io/InputStream;)[B
      // 0d2: astore 10
      // 0d4: aload 1
      // 0d5: invokevirtual net/rim/device/apps/internal/browser/html/HTMLObject.getWidth ()Ljava/lang/String;
      // 0d8: bipush -1
      // 0da: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.valueAsPixels (Ljava/lang/String;I)I
      // 0dd: istore 11
      // 0df: aload 1
      // 0e0: invokevirtual net/rim/device/apps/internal/browser/html/HTMLObject.getHeight ()Ljava/lang/String;
      // 0e3: bipush -1
      // 0e5: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.valueAsPixels (Ljava/lang/String;I)I
      // 0e8: istore 12
      // 0ea: aload 10
      // 0ec: ifnull 163
      // 0ef: aload 10
      // 0f1: bipush 0
      // 0f2: aload 10
      // 0f4: arraylength
      // 0f5: aload 4
      // 0f7: invokestatic net/rim/device/apps/internal/browser/util/ImageConverter.convert ([BIILjava/lang/String;)Lnet/rim/device/api/system/EncodedImage;
      // 0fa: astore 13
      // 0fc: aload 13
      // 0fe: ifnull 163
      // 101: aload 0
      // 102: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentAnchor Lnet/rim/device/apps/internal/browser/html/HTMLAnchor;
      // 105: ifnull 12b
      // 108: aload 0
      // 109: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentAnchor Lnet/rim/device/apps/internal/browser/html/HTMLAnchor;
      // 10c: invokevirtual net/rim/device/apps/internal/browser/html/HTMLAnchor.getHref ()Ljava/lang/String;
      // 10f: ifnull 12b
      // 112: new net/rim/device/apps/internal/browser/ui/BrowserLinkBitmapField
      // 115: dup
      // 116: aload 0
      // 117: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // 11a: aconst_null
      // 11b: aload 3
      // 11c: lload 5
      // 11e: aload 0
      // 11f: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentAnchor Lnet/rim/device/apps/internal/browser/html/HTMLAnchor;
      // 122: aconst_null
      // 123: invokespecial net/rim/device/apps/internal/browser/ui/BrowserLinkBitmapField.<init> (Lnet/rim/device/apps/internal/browser/page/BrowserContentImpl;Lnet/rim/device/api/system/Bitmap;Ljava/lang/String;JLorg/w3c/dom/html2/HTMLAnchorElement;Lnet/rim/device/apps/internal/browser/util/ImageMap;)V
      // 126: astore 14
      // 128: goto 13c
      // 12b: new net/rim/device/apps/internal/browser/ui/BrowserBitmapField
      // 12e: dup
      // 12f: aload 0
      // 130: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // 133: aconst_null
      // 134: aload 3
      // 135: lload 5
      // 137: invokespecial net/rim/device/apps/internal/browser/ui/BrowserBitmapField.<init> (Lnet/rim/device/api/browser/field/BrowserContentBaseImpl;Lnet/rim/device/api/system/Bitmap;Ljava/lang/String;J)V
      // 13a: astore 14
      // 13c: aload 14
      // 13e: aload 13
      // 140: bipush 0
      // 141: bipush 0
      // 142: iload 11
      // 144: iload 12
      // 146: invokevirtual net/rim/device/apps/internal/browser/ui/BrowserBitmapField.setImage (Lnet/rim/device/api/system/EncodedImage;IIII)V
      // 149: bipush 1
      // 14a: istore 2
      // 14b: aload 0
      // 14c: aload 14
      // 14e: lload 5
      // 150: ldc2_w 12884901888
      // 153: land
      // 154: bipush 0
      // 155: i2l
      // 156: lcmp
      // 157: ifne 15e
      // 15a: bipush 1
      // 15b: goto 15f
      // 15e: bipush 0
      // 15f: bipush 0
      // 160: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.appendField (Lnet/rim/device/api/ui/Field;ZZ)V
      // 163: aload 9
      // 165: ifnull 17f
      // 168: aload 9
      // 16a: invokevirtual java/io/InputStream.close ()V
      // 16d: goto 17f
      // 170: astore 15
      // 172: aload 9
      // 174: ifnull 17c
      // 177: aload 9
      // 179: invokevirtual java/io/InputStream.close ()V
      // 17c: aload 15
      // 17e: athrow
      // 17f: aload 8
      // 181: ifnonnull 187
      // 184: goto 34e
      // 187: aload 8
      // 189: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 18e: goto 34e
      // 191: astore 10
      // 193: goto 34e
      // 196: astore 10
      // 198: aload 8
      // 19a: ifnonnull 1a0
      // 19d: goto 34e
      // 1a0: aload 8
      // 1a2: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1a7: goto 34e
      // 1aa: astore 10
      // 1ac: goto 34e
      // 1af: astore 16
      // 1b1: aload 8
      // 1b3: ifnull 1c2
      // 1b6: aload 8
      // 1b8: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1bd: goto 1c2
      // 1c0: astore 17
      // 1c2: aload 16
      // 1c4: athrow
      // 1c5: aload 4
      // 1c7: ifnonnull 1cd
      // 1ca: goto 34e
      // 1cd: aload 0
      // 1ce: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingOptions Lnet/rim/device/api/browser/field/RenderingOptions;
      // 1d1: ldc2_w 4550690918222697397
      // 1d4: bipush 20
      // 1d6: bipush 1
      // 1d7: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithBooleanValue (JIZ)Z
      // 1da: ifne 1e0
      // 1dd: goto 34e
      // 1e0: aload 0
      // 1e1: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 1e4: ifnonnull 1ea
      // 1e7: goto 34e
      // 1ea: aload 3
      // 1eb: ifnonnull 1f1
      // 1ee: goto 34e
      // 1f1: aload 3
      // 1f2: invokevirtual java/lang/String.length ()I
      // 1f5: ifgt 1fb
      // 1f8: goto 34e
      // 1fb: aload 0
      // 1fc: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // 1ff: aload 3
      // 200: invokevirtual net/rim/device/apps/internal/browser/page/BrowserContentImpl.resolveUrl (Ljava/lang/String;)Ljava/lang/String;
      // 203: astore 7
      // 205: aload 0
      // 206: getfield net/rim/device/apps/internal/browser/page/Renderer._flags I
      // 209: sipush 255
      // 20c: iand
      // 20d: bipush 16
      // 20f: ior
      // 210: istore 8
      // 212: new java/lang/Object
      // 215: dup
      // 216: aload 7
      // 218: aload 0
      // 219: bipush 0
      // 21a: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.getRequestHeaders (Z)Lnet/rim/device/api/io/http/HttpHeaders;
      // 21d: iload 8
      // 21f: invokespecial net/rim/device/api/browser/field/RequestedResource.<init> (Ljava/lang/String;Lnet/rim/device/api/io/http/HttpHeaders;I)V
      // 222: astore 9
      // 224: aload 0
      // 225: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 228: aload 9
      // 22a: aconst_null
      // 22b: invokeinterface net/rim/device/api/browser/field/RenderingApplication.getResource (Lnet/rim/device/api/browser/field/RequestedResource;Lnet/rim/device/api/browser/field/BrowserContent;)Ljavax/microedition/io/HttpConnection; 3
      // 230: astore 10
      // 232: aload 10
      // 234: ifnonnull 23a
      // 237: goto 34e
      // 23a: aload 10
      // 23c: invokeinterface javax/microedition/io/HttpConnection.getResponseCode ()I 1
      // 241: sipush 200
      // 244: if_icmpeq 24a
      // 247: goto 34e
      // 24a: aconst_null
      // 24b: astore 11
      // 24d: aload 0
      // 24e: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingSession Lnet/rim/device/api/browser/field/RenderingSession;
      // 251: aload 10
      // 253: aload 0
      // 254: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // 257: iload 8
      // 259: invokevirtual net/rim/device/api/browser/field/RenderingSession.getBrowserContent (Ljavax/microedition/io/HttpConnection;Lnet/rim/device/api/browser/field/RenderingApplication;I)Lnet/rim/device/api/browser/field/BrowserContent;
      // 25c: astore 11
      // 25e: goto 263
      // 261: astore 12
      // 263: aload 11
      // 265: ifnonnull 26b
      // 268: goto 34e
      // 26b: aload 11
      // 26d: invokeinterface net/rim/device/api/browser/field/BrowserContent.getDisplayableContent ()Lnet/rim/device/api/ui/Field; 1
      // 272: ifnonnull 278
      // 275: goto 34e
      // 278: aload 11
      // 27a: invokeinterface net/rim/device/api/browser/field/BrowserContent.getDisplayableContent ()Lnet/rim/device/api/ui/Field; 1
      // 27f: astore 12
      // 281: aload 12
      // 283: instanceof net/rim/device/apps/internal/browser/ui/TextFlowManager
      // 286: ifeq 293
      // 289: aload 0
      // 28a: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.excessiveEmbedding ()Z
      // 28d: ifeq 293
      // 290: goto 34e
      // 293: aload 1
      // 294: invokevirtual net/rim/device/apps/internal/browser/html/HTMLObject.getWidth ()Ljava/lang/String;
      // 297: getstatic net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.DISPLAY_WIDTH I
      // 29a: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.valueAsPixels (Ljava/lang/String;I)I
      // 29d: istore 13
      // 29f: aload 1
      // 2a0: invokevirtual net/rim/device/apps/internal/browser/html/HTMLObject.getHeight ()Ljava/lang/String;
      // 2a3: sipush 150
      // 2a6: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.valueAsPixels (Ljava/lang/String;I)I
      // 2a9: istore 14
      // 2ab: aconst_null
      // 2ac: astore 15
      // 2ae: aload 12
      // 2b0: instanceof java/lang/Object
      // 2b3: ifeq 2c9
      // 2b6: new net/rim/device/apps/internal/browser/html/DestroyableFrameLayout
      // 2b9: dup
      // 2ba: lload 5
      // 2bc: aload 12
      // 2be: checkcast java/lang/Object
      // 2c1: invokespecial net/rim/device/apps/internal/browser/html/DestroyableFrameLayout.<init> (JLnet/rim/device/api/browser/field/Destroyable;)V
      // 2c4: astore 15
      // 2c6: goto 2d4
      // 2c9: new java/lang/Object
      // 2cc: dup
      // 2cd: lload 5
      // 2cf: invokespecial net/rim/device/internal/ui/container/FrameLayout.<init> (J)V
      // 2d2: astore 15
      // 2d4: aload 11
      // 2d6: invokeinterface net/rim/device/api/browser/field/BrowserContent.getRenderingFlags ()I 1
      // 2db: sipush 1024
      // 2de: iand
      // 2df: ifne 2e6
      // 2e2: bipush 1
      // 2e3: goto 2e7
      // 2e6: bipush 0
      // 2e7: istore 16
      // 2e9: new net/rim/device/apps/internal/browser/ui/RigidManager
      // 2ec: dup
      // 2ed: iload 13
      // 2ef: iload 14
      // 2f1: iload 16
      // 2f3: ifeq 2fc
      // 2f6: ldc2_w 299067162755072
      // 2f9: goto 2ff
      // 2fc: ldc2_w 598134325510144
      // 2ff: invokespecial net/rim/device/apps/internal/browser/ui/RigidManager.<init> (IIJ)V
      // 302: astore 17
      // 304: aload 15
      // 306: aload 17
      // 308: invokevirtual net/rim/device/internal/ui/container/FrameLayout.add (Lnet/rim/device/api/ui/Field;)V
      // 30b: aload 17
      // 30d: aload 12
      // 30f: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 312: aload 0
      // 313: aload 15
      // 315: lload 5
      // 317: ldc2_w 12884901888
      // 31a: land
      // 31b: bipush 0
      // 31c: i2l
      // 31d: lcmp
      // 31e: ifne 325
      // 321: bipush 1
      // 322: goto 326
      // 325: bipush 0
      // 326: bipush 0
      // 327: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.appendField (Lnet/rim/device/api/ui/Field;ZZ)V
      // 32a: bipush 1
      // 32b: istore 2
      // 32c: aload 11
      // 32e: invokeinterface net/rim/device/api/browser/field/BrowserContent.finishLoading ()V 1
      // 333: goto 34e
      // 336: astore 12
      // 338: goto 34e
      // 33b: astore 11
      // 33d: aload 10
      // 33f: ifnull 34e
      // 342: aload 10
      // 344: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 349: goto 34e
      // 34c: astore 12
      // 34e: aload 0
      // 34f: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentContext Lnet/rim/device/apps/internal/browser/html/HTMLRendererContext;
      // 352: aconst_null
      // 353: putfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._currentObject Lnet/rim/device/apps/internal/browser/html/HTMLObject;
      // 356: iload 2
      // 357: ifne 35f
      // 35a: aload 0
      // 35b: bipush 0
      // 35c: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._skipElementStackValue I
      // 35f: return
      // try (89 -> 168): 173 null
      // try (173 -> 174): 173 null
      // try (180 -> 185): 186 null
      // try (81 -> 180): 188 null
      // try (189 -> 194): 195 null
      // try (81 -> 180): 197 null
      // try (188 -> 189): 197 null
      // try (198 -> 202): 203 null
      // try (197 -> 198): 197 null
      // try (265 -> 273): 274 null
      // try (275 -> 364): 365 null
      // try (255 -> 366): 367 null
      // try (368 -> 372): 373 null
   }

   private final void processElementParam(boolean isStartTag, HTMLElement element) {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void processElementLink(boolean isStartTag, int startAttribute, int endAttribute) {
      if (isStartTag) {
         String rel = null;
         String href = null;
         String type = null;
         String media = null;
         String title = null;

         for (int attribute = startAttribute; attribute < endAttribute; attribute++) {
            switch (this._attributeItems[attribute] & 0xFF) {
               case 125:
                  href = this.getAttributeValue(null, attribute);
                  break;
               case 139:
                  media = this.getAttributeValue(null, attribute);
                  break;
               case 169:
                  rel = this.getAttributeValue(null, attribute);
                  break;
               case 189:
                  title = this.getAttributeValue(null, attribute);
                  break;
               case 190:
                  type = this.getAttributeValue(null, attribute);
            }
         }

         if (rel != null && href != null) {
            if (!StringUtilities.strEqualIgnoreCase(rel, "stylesheet", 1701707776)) {
               if ((StringUtilities.strEqualIgnoreCase(rel, "shortcut icon", 1701707776) || StringUtilities.strEqualIgnoreCase(rel, "icon", 1701707776))
                  && IS_COLOR
                  && super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 5, true)) {
                  href = this._browserContent.resolveUrl(href);
                  EncodedImage img = this.findEncodedImageInCache(href, super._flags & 0xFF, null);
                  this._browserContent.setIconUrl(href);
                  if (img == null) {
                     this._browserContent.addSecondaryURL(href, new HTMLSecondaryURLNode(this._currentBrowserField, null), false);
                     return;
                  }

                  this._browserContent.setIcon(img);
               }
            } else if (this._processStyleSheets && this.isValidStyleSheet(type, media, title)) {
               href = this._browserContent.resolveUrl(href);
               InlineDataRefHolder styleSheet = this.loadExternalStyleSheet(href);
               if (styleSheet != null) {
                  this.loadingStatusEventOccurred(4);
                  if (StringUtilities.strEqualIgnoreCase(styleSheet._str, "text/css", 1701707776)) {
                     String textStyleSheet;
                     if (styleSheet._encoding == null) {
                        textStyleSheet = (String)(new Object(styleSheet._data, styleSheet._offset, styleSheet._length));
                     } else {
                        boolean var13 = false /* VF: Semaphore variable */;

                        label98:
                        try {
                           var13 = true;
                           textStyleSheet = (String)(new Object(styleSheet._data, styleSheet._offset, styleSheet._length, styleSheet._encoding));
                           var13 = false;
                        } finally {
                           if (var13) {
                              textStyleSheet = (String)(new Object(styleSheet._data, styleSheet._offset, styleSheet._length));
                              break label98;
                           }
                        }
                     }

                     this.parseTextStyleSheet(textStyleSheet, href);
                  } else if (StringUtilities.strEqualIgnoreCase(styleSheet._str, "application/vnd.rim.css", 1701707776)) {
                     this.parseBinaryStyleSheet(Arrays.copy(styleSheet._data, styleSheet._offset, styleSheet._length), href);
                  }
               }

               this.loadingStatusEventOccurred(0);
               return;
            }
         }
      }
   }

   private final boolean handlePictogram(String data) {
      if (data != null && super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 5, true)) {
         EncodedImage img = BrowserResources.getPictogramImage(data);
         if (img == null) {
            return false;
         }

         BrowserBitmapField field;
         if (this._currentAnchor != null && this._currentAnchor.getHref() != null) {
            field = new BrowserLinkBitmapField(this._browserContent, null, data, 0, this._currentAnchor, null);
         } else {
            field = new BrowserBitmapField(this._browserContent, null, data);
         }

         field.setImage(img);
         this.appendField(field, false, false);
         return true;
      } else {
         return false;
      }
   }

   private final String generateAltText(String src, boolean includeFileExtension) {
      if (src != null && src.length() > 0) {
         int startIndex = 0;
         int endIndex = src.length();
         int queryString = src.indexOf(63);
         if (queryString > 0) {
            endIndex = queryString;
         }

         int lastSlash = src.lastIndexOf(47, endIndex - 1);
         if (lastSlash >= 0) {
            startIndex = lastSlash + 1;
         }

         if (!includeFileExtension) {
            int fileExtension = src.lastIndexOf(46, endIndex - 1);
            if (fileExtension > startIndex) {
               endIndex = fileExtension;
            }
         }

         if (endIndex > startIndex) {
            return URIDecoder.decode(src.substring(startIndex, endIndex), super._encoding);
         }
      }

      return null;
   }

   private final Field makeBitmapField(HTMLGenericElement element, Border border, String alt) {
      boolean isSubmit = element instanceof HTMLInput;
      boolean isListItem = element instanceof HTMLLi;
      SecondaryURLNode node = (SecondaryURLNode)element;
      EncodedImage image = null;
      boolean doSecondaryRequest = false;
      String src = element.getAttribute("SRC");
      boolean useSrc = src != null && src.length() > 0;
      int background = this._currentBrowserField.isBackgroundImageSet() ? -2 : element._backgroundColour;
      if (background == -1) {
         background = 16777215;
      }

      HTMLAnchorElement link = node.getLink();
      if (alt == null && element.hasAttribute("alt")) {
         alt = node.getAlt();
      }

      int imageWidth = node.getWidth();
      int imageHeight = node.getHeight();
      if (link != null || imageWidth == -1 || imageHeight == -1 || imageWidth > 4 && imageHeight > 4) {
         if (useSrc) {
            src = this._browserContent.resolveUrl(src);
            if (super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 5, true)) {
               image = this.findEncodedImageInCache(src, super._flags & 0xFF, element);
            }

            if (image == null) {
               doSecondaryRequest = true;
            } else {
               ImageMap map = node.getImageMap();
               if (map != null) {
                  map.setOriginalSize(node.getWidth(), node.getHeight());
               }
            }
         } else {
            if (!isSubmit) {
               return null;
            }

            image = null;
            doSecondaryRequest = false;
         }

         boolean generatedAltText = false;
         if (alt == null || (link != null || isSubmit) && alt.trim().length() == 0) {
            alt = this.generateAltText(src, false);
            if (alt == null) {
               alt = link != null ? BrowserResources.getString(609) : (isSubmit ? BrowserResources.getString(537) : BrowserResources.getString(358));
            }

            generatedAltText = true;
         } else if (alt.length() == 0) {
            alt = SINGLE_SPACE;
         }

         Field field = null;
         int specifiedWidth = node.getWidth();
         int specifiedHeight = node.getHeight();
         int refTag = -1;
         Field var28;
         if (image == null) {
            if (isSubmit) {
               var28 = new HTMLSubmitInputField(
                  (HTMLInput)element, ((HTMLInput)element).getDefaultValue(), alt, true, node.getStyle() | 4, specifiedWidth, specifiedHeight, -1, -1, null
               );
            } else if (isListItem) {
               BrowserListItemBitmapField bField = new BrowserListItemBitmapField(this._browserContent, node.getSrc(), node.getStyle(), alt, element);
               bField.setFont(this._textUtilities.deriveFont(element));
               bField.setColor(this._textUtilities.getContrastColour(element, element._foregroundColour));
               var28 = bField;
            } else {
               Font font = null;
               long cookieId = -1;
               int fontStyle = 0;
               if (link == null && node.getImageMap() == null) {
                  BrowserBitmapField bField = new BrowserBitmapField(
                     this._browserContent, null, src, node.getStyle(), false, specifiedWidth, specifiedHeight, 1, alt, element
                  );
                  if (background != -2) {
                     bField.setUnderlyingBackgroundColor(background);
                  }

                  var28 = bField;
               } else {
                  BrowserLinkBitmapField lField = new BrowserLinkBitmapField(
                     this._browserContent, null, src, node.getStyle(), link, node.getImageMap(), specifiedWidth, specifiedHeight, 1, alt, element
                  );
                  if (background != -2) {
                     lField.setUnderlyingBackgroundColor(background);
                  }

                  cookieId = lField.getCookieID();
                  fontStyle = 12;
                  var28 = lField;
               }

               if (var28 != null && !isListItem) {
                  var28.setFont(this._textUtilities.getDefaultFont());
                  if (border != null) {
                     var28.setBorder(border);
                  }

                  if (!super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 5, true)
                     && !super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 7, true)) {
                     font = FontCache.getInstance().getFont(this._textUtilities.getDefaultFont(), fontStyle, this._textUtilities.getDefaultFont().getHeight(0));
                     if (generatedAltText) {
                        alt = ((StringBuffer)(new Object(alt.length() + 2))).append('[').append(alt).append(']').toString();
                     }

                     refTag = this.appendAltField(var28, alt, font, cookieId, link != null ? link.getHref() : null);
                     node.setReplaceTag(refTag);
                  }
               }
            }
         } else {
            if (isSubmit) {
               BitmapField bField = new HTMLImageInputField(this._browserContent, (HTMLInput)element, null, node.getSrc(), node.getStyle());
               bField.setImage(image);
               var28 = bField;
            } else if (isListItem) {
               BrowserListItemBitmapField bField = new BrowserListItemBitmapField(this._browserContent, node.getSrc(), node.getStyle(), alt, element);
               bField.setImage(image);
               bField.setFont(this._textUtilities.deriveFont(element));
               bField.setColor(this._textUtilities.getContrastColour(element, element._foregroundColour));
               var28 = bField;
            } else {
               ImageMap map = node.getImageMap();
               BrowserBitmapField bField = link == null && map == null
                  ? new BrowserBitmapField(this._browserContent, null, node.getSrc(), node.getStyle(), false, specifiedWidth, specifiedHeight, 1, alt, element)
                  : new BrowserLinkBitmapField(
                     this._browserContent, null, node.getSrc(), node.getStyle(), link, map, specifiedWidth, specifiedHeight, 1, alt, element
                  );
               bField.setImage(image, node.getHspace(), node.getVspace(), specifiedWidth, specifiedHeight);
               if (background != -2) {
                  bField.setUnderlyingBackgroundColor(background);
               }

               var28 = bField;
            }

            if (var28 != null && !isListItem) {
               var28.setFont(this._textUtilities.getDefaultFont());
               if (border != null) {
                  var28.setBorder(border);
               }
            }

            if (image instanceof Object) {
               if (this._progressiveImageMap == null) {
                  this._progressiveImageMap = (MultiMap)(new Object());
               }

               this._progressiveImageMap.add(image, var28);
            }
         }

         node.setUIField(var28);
         if (useSrc && doSecondaryRequest && var28 != null) {
            this._browserContent.addSecondaryURL(src, node, INLINE_DATA_304.equals(this._inlineData.get(src)));
         }

         return refTag != -1 ? null : var28;
      } else {
         this.resetBlockAndBreak();
         String onLoad = element.getAttribute("onLoad");
         if (this._scriptEngine != null && onLoad != null && onLoad.length() > 0) {
            try {
               this._scriptEngine.executeMethod(null, onLoad, null, false);
               return null;
            } finally {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   @Override
   public final void cleanup() {
      WAPInputStream in = this._currentContext._in;

      label32:
      try {
         in.close();
      } finally {
         break label32;
      }

      if (this._trimOffsets != null) {
         Pipe pipe = in.getPipe();
         if (pipe != null) {
            int adjustedOffset = pipe.removeSections(this._trimOffsets, this._trimLengths);
            if (super._renderingApplication != null) {
               super._renderingApplication.eventOccurred(new DataModificationEvent(this._browserContent, this._url, adjustedOffset));
            }
         }
      }

      super.cleanup();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void readString() {
      HTMLRendererContext context = this._currentContext;
      WAPInputStream in = context._in;
      int encodingCode = in.readCompressedInt();
      byte[] data = null;
      int offset = 0;
      int length = 0;
      if (this._currentContext._compressedStrings != null && (length = in.readCompressedInt()) > 0) {
         byte[] stringBuffer = new byte[length];
         if (this._currentContext._compressedStrings.read(stringBuffer, 0, length) != length) {
            throw new Object();
         }
      } else {
         PipePtr ptr = in.readByteArrayRef();
         length = ptr.getLength();
         if (length > 0) {
            data = ptr.getData();
            offset = ptr.getOffset();
         }
      }

      String string;
      if (data != null) {
         boolean var12 = false /* VF: Semaphore variable */;

         label55:
         try {
            var12 = true;
            string = (String)(new Object(data, offset, length, HTMLBinaryConstants.resolveStringEncoding(encodingCode)));
            var12 = false;
         } finally {
            if (var12) {
               string = (String)(new Object(data, offset, length));
               break label55;
            }
         }
      } else if (length == 0) {
         string = "";
      } else {
         string = null;
      }

      String[] richTextStrings = context._richTextStrings;
      int index = context._richTextStringsLength;
      if (++context._richTextStringsLength > richTextStrings.length) {
         Array.extend(richTextStrings, 1);
      }

      richTextStrings[index] = string;
      if (!context._allTagsProvided) {
         PipePtr ptr = in.readByteArrayRef();
         length = ptr.getLength();
         if (length > 0) {
            context._richTextProperties.put(index, Arrays.copy(ptr.getData(), ptr.getOffset(), length));
         }
      }
   }

   private final void handleInstruction() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentContext Lnet/rim/device/apps/internal/browser/html/HTMLRendererContext;
      // 004: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._in Lnet/rim/device/apps/internal/browser/stack/WAPInputStream;
      // 007: astore 1
      // 008: aload 1
      // 009: invokevirtual java/io/DataInputStream.readUnsignedByte ()I
      // 00c: istore 2
      // 00d: iload 2
      // 00e: tableswitch 54 0 9 2906 54 935 2906 150 2634 935 935 276 2857
      // 044: aload 1
      // 045: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readByteArray ()[B
      // 048: astore 3
      // 049: aload 3
      // 04a: ifnonnull 050
      // 04d: goto b6c
      // 050: aload 0
      // 051: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._skipElementStackValue I
      // 054: ifle 05a
      // 057: goto b6c
      // 05a: aload 3
      // 05b: arraylength
      // 05c: ifle 069
      // 05f: aload 3
      // 060: bipush 0
      // 061: baload
      // 062: sipush 255
      // 065: iand
      // 066: goto 06b
      // 069: bipush 8
      // 06b: istore 4
      // 06d: iload 4
      // 06f: tableswitch 29 7 10 2813 29 37 45
      // 08c: aload 0
      // 08d: ldc2_w 4294967296
      // 090: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._fieldStyle J
      // 093: return
      // 094: aload 0
      // 095: ldc2_w 12884901888
      // 098: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._fieldStyle J
      // 09b: return
      // 09c: aload 0
      // 09d: ldc2_w 8589934592
      // 0a0: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._fieldStyle J
      // 0a3: return
      // 0a4: aload 1
      // 0a5: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.skipByteArray ()V
      // 0a8: aload 0
      // 0a9: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._skipElementStackValue I
      // 0ac: ifle 0b2
      // 0af: goto b6c
      // 0b2: aload 0
      // 0b3: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentContext Lnet/rim/device/apps/internal/browser/html/HTMLRendererContext;
      // 0b6: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._currentStringRef I
      // 0b9: bipush -1
      // 0bb: if_icmpeq 0e0
      // 0be: aload 0
      // 0bf: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._coreDocElement Lnet/rim/device/apps/internal/browser/html/HTMLBaseGenericElement;
      // 0c2: astore 4
      // 0c4: aload 0
      // 0c5: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._tagStack Ljava/util/Stack;
      // 0c8: invokevirtual java/util/Vector.isEmpty ()Z
      // 0cb: ifne 0da
      // 0ce: aload 0
      // 0cf: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._tagStack Ljava/util/Stack;
      // 0d2: invokevirtual java/util/Stack.peek ()Ljava/lang/Object;
      // 0d5: checkcast net/rim/device/apps/internal/browser/html/HTMLGenericElement
      // 0d8: astore 4
      // 0da: aload 0
      // 0db: aload 4
      // 0dd: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.appendCurrentString (Lnet/rim/device/apps/internal/browser/html/HTMLGenericElement;)V
      // 0e0: new net/rim/device/apps/internal/browser/html/HTMLBaseGenericElement
      // 0e3: dup
      // 0e4: bipush 4
      // 0e6: aconst_null
      // 0e7: bipush 0
      // 0e8: invokespecial net/rim/device/apps/internal/browser/html/HTMLBaseGenericElement.<init> (ILnet/rim/device/apps/internal/browser/html/HTMLDOMInternalRepresentation;I)V
      // 0eb: astore 4
      // 0ed: aload 4
      // 0ef: aload 0
      // 0f0: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._coreDocElement Lnet/rim/device/apps/internal/browser/html/HTMLBaseGenericElement;
      // 0f3: invokevirtual net/rim/device/internal/ui/TextFlowRegion.inherit (Lnet/rim/device/internal/ui/TextFlowRegion;)V
      // 0f6: aload 4
      // 0f8: sipush 320
      // 0fb: invokevirtual net/rim/device/internal/ui/TextFlowRegion.setBreakingFlags (S)V
      // 0fe: aload 0
      // 0ff: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._appEventLock Ljava/lang/Object;
      // 102: dup
      // 103: astore 5
      // 105: monitorenter
      // 106: aload 0
      // 107: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentBrowserField Lnet/rim/device/apps/internal/browser/html/HTMLBrowserField;
      // 10a: aload 4
      // 10c: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowManager.pushRegion (Lnet/rim/device/internal/ui/TextFlowRegion;)V
      // 10f: aload 0
      // 110: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentBrowserField Lnet/rim/device/apps/internal/browser/html/HTMLBrowserField;
      // 113: invokevirtual net/rim/device/apps/internal/browser/ui/TextFlowManager.popRegion ()V
      // 116: aload 5
      // 118: monitorexit
      // 119: return
      // 11a: astore 6
      // 11c: aload 5
      // 11e: monitorexit
      // 11f: aload 6
      // 121: athrow
      // 122: aload 0
      // 123: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._loadingInlineFragments Z
      // 126: ifne 158
      // 129: ldc2_w 1907089860548946979
      // 12c: ldc_w 1114861673
      // 12f: bipush 0
      // 130: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 133: pop
      // 134: aload 0
      // 135: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 138: ifnull 14e
      // 13b: aload 0
      // 13c: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 13f: new net/rim/device/apps/internal/browser/api/InlineImageLoadingEvent
      // 142: dup
      // 143: aload 0
      // 144: bipush 1
      // 145: invokespecial net/rim/device/apps/internal/browser/api/InlineImageLoadingEvent.<init> (Ljava/lang/Object;Z)V
      // 148: invokeinterface net/rim/device/api/browser/field/RenderingApplication.eventOccurred (Lnet/rim/device/api/browser/field/Event;)Ljava/lang/Object; 2
      // 14d: pop
      // 14e: aload 0
      // 14f: bipush 1
      // 150: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._loadingInlineFragments Z
      // 153: aload 0
      // 154: bipush 0
      // 155: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._nextTickThreshold I
      // 158: aload 1
      // 159: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.getPosition ()Lnet/rim/device/internal/browser/util/PipeContext;
      // 15c: astore 4
      // 15e: aload 4
      // 160: ifnull 16b
      // 163: aload 4
      // 165: getfield net/rim/device/internal/browser/util/PipeContext._numRead I
      // 168: goto 16c
      // 16b: bipush 0
      // 16c: istore 5
      // 16e: aload 1
      // 16f: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readByteArrayRef ()Lnet/rim/device/internal/browser/util/PipePtr;
      // 172: astore 6
      // 174: aload 1
      // 175: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.getPosition ()Lnet/rim/device/internal/browser/util/PipeContext;
      // 178: astore 4
      // 17a: aload 4
      // 17c: ifnull 18a
      // 17f: aload 4
      // 181: getfield net/rim/device/internal/browser/util/PipeContext._numRead I
      // 184: iload 5
      // 186: isub
      // 187: goto 18b
      // 18a: bipush 0
      // 18b: istore 7
      // 18d: new java/lang/Object
      // 190: dup
      // 191: aload 6
      // 193: invokevirtual net/rim/device/internal/browser/util/PipePtr.getData ()[B
      // 196: aload 6
      // 198: invokevirtual net/rim/device/internal/browser/util/PipePtr.getOffset ()I
      // 19b: aload 6
      // 19d: invokevirtual net/rim/device/internal/browser/util/PipePtr.getLength ()I
      // 1a0: bipush 0
      // 1a1: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 1a4: astore 8
      // 1a6: aload 8
      // 1a8: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 1ab: istore 9
      // 1ad: aload 8
      // 1af: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 1b2: istore 10
      // 1b4: aload 8
      // 1b6: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 1b9: istore 11
      // 1bb: aload 8
      // 1bd: invokevirtual net/rim/device/api/util/DataBuffer.readByteArray ()[B
      // 1c0: astore 12
      // 1c2: aconst_null
      // 1c3: astore 13
      // 1c5: iload 9
      // 1c7: ifle 1e5
      // 1ca: aload 8
      // 1cc: aload 6
      // 1ce: invokevirtual net/rim/device/internal/browser/util/PipePtr.getLength ()I
      // 1d1: iload 9
      // 1d3: isub
      // 1d4: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 1d7: iload 9
      // 1d9: newarray 8
      // 1db: astore 13
      // 1dd: aload 8
      // 1df: aload 13
      // 1e1: invokevirtual net/rim/device/api/util/DataBuffer.read ([B)I
      // 1e4: pop
      // 1e5: aload 0
      // 1e6: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._inlineFragments Lnet/rim/device/api/util/IntHashtable;
      // 1e9: iload 10
      // 1eb: invokevirtual net/rim/device/api/util/IntHashtable.get (I)Ljava/lang/Object;
      // 1ee: checkcast net/rim/device/apps/internal/browser/html/FragmentHolder
      // 1f1: astore 14
      // 1f3: aload 14
      // 1f5: ifnonnull 1fb
      // 1f8: goto b6c
      // 1fb: aload 0
      // 1fc: aload 0
      // 1fd: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._inlineFramgnetsReceived I
      // 200: bipush 1
      // 201: iadd
      // 202: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._inlineFramgnetsReceived I
      // 205: aload 14
      // 207: iload 5
      // 209: bipush 2
      // 20b: isub
      // 20c: iload 7
      // 20e: bipush 2
      // 210: iadd
      // 211: invokevirtual net/rim/device/apps/internal/browser/html/FragmentHolder.addTrimSegment (II)V
      // 214: aload 14
      // 216: aload 12
      // 218: invokevirtual net/rim/device/apps/internal/browser/html/FragmentHolder.appendData ([B)V
      // 21b: aload 0
      // 21c: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._inlineData Ljava/util/Hashtable;
      // 21f: aload 14
      // 221: invokevirtual net/rim/device/apps/internal/browser/html/FragmentHolder.getUri ()Ljava/lang/String;
      // 224: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 227: astore 15
      // 229: aload 15
      // 22b: dup
      // 22c: instanceof net/rim/device/apps/internal/browser/page/RendererImageContainer
      // 22f: ifne 236
      // 232: pop
      // 233: goto 292
      // 236: checkcast net/rim/device/apps/internal/browser/page/RendererImageContainer
      // 239: getfield net/rim/device/apps/internal/browser/page/RendererImageContainer._image Lnet/rim/device/api/system/EncodedImage;
      // 23c: astore 16
      // 23e: aload 16
      // 240: dup
      // 241: instanceof java/lang/Object
      // 244: ifne 24b
      // 247: pop
      // 248: goto 292
      // 24b: checkcast java/lang/Object
      // 24e: aload 14
      // 250: invokevirtual net/rim/device/apps/internal/browser/html/FragmentHolder.getDataLength ()I
      // 253: invokevirtual net/rim/device/api/system/ProgressiveImage.updateLength (I)I
      // 256: pop
      // 257: aload 0
      // 258: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._progressiveImageMap Lnet/rim/device/api/util/MultiMap;
      // 25b: ifnull 292
      // 25e: aload 0
      // 25f: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._progressiveImageMap Lnet/rim/device/api/util/MultiMap;
      // 262: aload 16
      // 264: invokevirtual net/rim/device/api/util/MultiMap.elements (Ljava/lang/Object;)Ljava/util/Enumeration;
      // 267: astore 17
      // 269: aload 17
      // 26b: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 270: ifeq 292
      // 273: aload 17
      // 275: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 27a: astore 18
      // 27c: aload 18
      // 27e: dup
      // 27f: instanceof net/rim/device/apps/internal/browser/ui/BrowserBitmapField
      // 282: ifne 289
      // 285: pop
      // 286: goto 269
      // 289: checkcast net/rim/device/apps/internal/browser/ui/BrowserBitmapField
      // 28c: invokevirtual net/rim/device/apps/internal/browser/ui/BrowserBitmapField.invalidateField ()V
      // 28f: goto 269
      // 292: aload 0
      // 293: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._subDataMac Lnet/rim/device/api/crypto/HMAC;
      // 296: ifnonnull 29c
      // 299: goto b6c
      // 29c: aload 0
      // 29d: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._subDataMac Lnet/rim/device/api/crypto/HMAC;
      // 2a0: invokevirtual net/rim/device/api/crypto/HMAC.reset ()V
      // 2a3: goto 2a8
      // 2a6: astore 16
      // 2a8: aload 0
      // 2a9: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._subDataMac Lnet/rim/device/api/crypto/HMAC;
      // 2ac: aload 6
      // 2ae: invokevirtual net/rim/device/internal/browser/util/PipePtr.getData ()[B
      // 2b1: aload 6
      // 2b3: invokevirtual net/rim/device/internal/browser/util/PipePtr.getOffset ()I
      // 2b6: aload 6
      // 2b8: invokevirtual net/rim/device/internal/browser/util/PipePtr.getLength ()I
      // 2bb: iload 9
      // 2bd: isub
      // 2be: invokevirtual net/rim/device/api/crypto/HMAC.update ([BII)V
      // 2c1: aload 14
      // 2c3: invokevirtual net/rim/device/apps/internal/browser/html/FragmentHolder.getStoredMAC ()[B
      // 2c6: astore 16
      // 2c8: aload 0
      // 2c9: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._subDataMac Lnet/rim/device/api/crypto/HMAC;
      // 2cc: invokevirtual net/rim/device/api/crypto/AbstractMAC.getMAC ()[B
      // 2cf: astore 17
      // 2d1: aload 16
      // 2d3: arraylength
      // 2d4: bipush 1
      // 2d5: isub
      // 2d6: istore 18
      // 2d8: iload 18
      // 2da: iflt 2f1
      // 2dd: aload 16
      // 2df: iload 18
      // 2e1: dup2
      // 2e2: baload
      // 2e3: aload 17
      // 2e5: iload 18
      // 2e7: baload
      // 2e8: ixor
      // 2e9: i2b
      // 2ea: bastore
      // 2eb: iinc 18 -1
      // 2ee: goto 2d8
      // 2f1: iload 11
      // 2f3: bipush 1
      // 2f4: iadd
      // 2f5: aload 14
      // 2f7: invokevirtual net/rim/device/apps/internal/browser/html/FragmentHolder.getFragmentCount ()I
      // 2fa: if_icmpeq 300
      // 2fd: goto b6c
      // 300: aload 13
      // 302: ifnonnull 308
      // 305: goto b6c
      // 308: aload 13
      // 30a: aload 16
      // 30c: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 30f: ifne 315
      // 312: goto b6c
      // 315: new net/rim/device/apps/internal/browser/api/CacheSubDataEvent
      // 318: dup
      // 319: aload 0
      // 31a: aload 14
      // 31c: invokevirtual net/rim/device/apps/internal/browser/html/FragmentHolder.getUri ()Ljava/lang/String;
      // 31f: aload 14
      // 321: invokevirtual net/rim/device/apps/internal/browser/html/FragmentHolder.getHeaders ()Lnet/rim/device/api/io/http/HttpHeaders;
      // 324: aload 14
      // 326: invokevirtual net/rim/device/apps/internal/browser/html/FragmentHolder.getData ()[B
      // 329: aload 14
      // 32b: invokevirtual net/rim/device/apps/internal/browser/html/FragmentHolder.getResponseCode ()I
      // 32e: invokespecial net/rim/device/apps/internal/browser/api/CacheSubDataEvent.<init> (Ljava/lang/Object;Ljava/lang/String;Lnet/rim/device/api/io/http/HttpHeaders;[BI)V
      // 331: astore 18
      // 333: aload 0
      // 334: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 337: aload 18
      // 339: invokeinterface net/rim/device/api/browser/field/RenderingApplication.eventOccurred (Lnet/rim/device/api/browser/field/Event;)Ljava/lang/Object; 2
      // 33e: ifnonnull 344
      // 341: goto b6c
      // 344: aload 14
      // 346: invokevirtual net/rim/device/apps/internal/browser/html/FragmentHolder.getTrimSegments ()I
      // 349: istore 19
      // 34b: aload 0
      // 34c: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimOffsets [I
      // 34f: ifnonnull 365
      // 352: aload 0
      // 353: iload 19
      // 355: newarray 10
      // 357: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimOffsets [I
      // 35a: aload 0
      // 35b: iload 19
      // 35d: newarray 10
      // 35f: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimLengths [I
      // 362: goto 383
      // 365: aload 0
      // 366: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimOffsets [I
      // 369: aload 0
      // 36a: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimOffsets [I
      // 36d: arraylength
      // 36e: iload 19
      // 370: iadd
      // 371: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 374: aload 0
      // 375: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimLengths [I
      // 378: aload 0
      // 379: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimLengths [I
      // 37c: arraylength
      // 37d: iload 19
      // 37f: iadd
      // 380: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 383: aload 14
      // 385: invokevirtual net/rim/device/apps/internal/browser/html/FragmentHolder.getTrimOffsets ()[I
      // 388: bipush 0
      // 389: aload 0
      // 38a: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimOffsets [I
      // 38d: aload 0
      // 38e: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimOffsets [I
      // 391: arraylength
      // 392: iload 19
      // 394: isub
      // 395: iload 19
      // 397: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 39a: aload 14
      // 39c: invokevirtual net/rim/device/apps/internal/browser/html/FragmentHolder.getTrimLengths ()[I
      // 39f: bipush 0
      // 3a0: aload 0
      // 3a1: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimLengths [I
      // 3a4: aload 0
      // 3a5: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimLengths [I
      // 3a8: arraylength
      // 3a9: iload 19
      // 3ab: isub
      // 3ac: iload 19
      // 3ae: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 3b1: return
      // 3b2: astore 16
      // 3b4: return
      // 3b5: iload 2
      // 3b6: bipush 6
      // 3b8: if_icmpne 3bf
      // 3bb: bipush 1
      // 3bc: goto 3c0
      // 3bf: bipush 0
      // 3c0: istore 4
      // 3c2: iload 4
      // 3c4: ifeq 3f2
      // 3c7: aload 0
      // 3c8: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._loadingInlineImages Z
      // 3cb: ifne 3f2
      // 3ce: aload 0
      // 3cf: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 3d2: ifnull 3e8
      // 3d5: aload 0
      // 3d6: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 3d9: new net/rim/device/apps/internal/browser/api/InlineImageLoadingEvent
      // 3dc: dup
      // 3dd: aload 0
      // 3de: bipush 0
      // 3df: invokespecial net/rim/device/apps/internal/browser/api/InlineImageLoadingEvent.<init> (Ljava/lang/Object;Z)V
      // 3e2: invokeinterface net/rim/device/api/browser/field/RenderingApplication.eventOccurred (Lnet/rim/device/api/browser/field/Event;)Ljava/lang/Object; 2
      // 3e7: pop
      // 3e8: aload 0
      // 3e9: bipush 1
      // 3ea: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._loadingInlineImages Z
      // 3ed: aload 0
      // 3ee: bipush 0
      // 3ef: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._nextTickThreshold I
      // 3f2: aload 1
      // 3f3: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.getPosition ()Lnet/rim/device/internal/browser/util/PipeContext;
      // 3f6: astore 5
      // 3f8: aload 5
      // 3fa: ifnull 405
      // 3fd: aload 5
      // 3ff: getfield net/rim/device/internal/browser/util/PipeContext._numRead I
      // 402: goto 406
      // 405: bipush 0
      // 406: istore 6
      // 408: aload 1
      // 409: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // 40c: istore 7
      // 40e: aload 1
      // 40f: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.getPosition ()Lnet/rim/device/internal/browser/util/PipeContext;
      // 412: astore 5
      // 414: aload 5
      // 416: ifnull 421
      // 419: aload 5
      // 41b: getfield net/rim/device/internal/browser/util/PipeContext._currentReadPos I
      // 41e: goto 422
      // 421: bipush 0
      // 422: istore 8
      // 424: aload 1
      // 425: iload 7
      // 427: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readByteArrayRef (I)Lnet/rim/device/internal/browser/util/PipePtr;
      // 42a: astore 9
      // 42c: aload 1
      // 42d: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.getPosition ()Lnet/rim/device/internal/browser/util/PipeContext;
      // 430: astore 5
      // 432: aload 5
      // 434: ifnull 442
      // 437: aload 5
      // 439: getfield net/rim/device/internal/browser/util/PipeContext._numRead I
      // 43c: iload 6
      // 43e: isub
      // 43f: goto 443
      // 442: bipush 0
      // 443: istore 10
      // 445: aload 9
      // 447: invokevirtual net/rim/device/internal/browser/util/PipePtr.getData ()[B
      // 44a: astore 11
      // 44c: aload 9
      // 44e: invokevirtual net/rim/device/internal/browser/util/PipePtr.getOffset ()I
      // 451: istore 12
      // 453: aload 9
      // 455: invokevirtual net/rim/device/internal/browser/util/PipePtr.getLength ()I
      // 458: istore 13
      // 45a: aload 9
      // 45c: invokevirtual net/rim/device/internal/browser/util/PipePtr.isRef ()Z
      // 45f: istore 14
      // 461: aload 0
      // 462: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._subDataMac Lnet/rim/device/api/crypto/HMAC;
      // 465: ifnull 46c
      // 468: bipush 1
      // 469: goto 46d
      // 46c: bipush 0
      // 46d: istore 15
      // 46f: iload 13
      // 471: ifgt 477
      // 474: goto b6c
      // 477: iload 2
      // 478: bipush 7
      // 47a: if_icmpeq 480
      // 47d: goto 514
      // 480: new java/lang/Object
      // 483: dup
      // 484: aload 11
      // 486: iload 12
      // 488: iload 13
      // 48a: bipush 0
      // 48b: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 48e: astore 16
      // 490: aload 16
      // 492: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 495: istore 17
      // 497: aload 16
      // 499: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 49c: pop
      // 49d: iload 17
      // 49f: bipush 1
      // 4a0: if_icmpne 511
      // 4a3: getstatic net/rim/device/internal/browser/util/TimeLogger._loggingEnabled Z
      // 4a6: ifeq 4b6
      // 4a9: invokestatic net/rim/device/internal/browser/util/TimeLogger.getInstance ()Lnet/rim/device/internal/browser/util/TimeLogger;
      // 4ac: bipush 10
      // 4ae: aload 16
      // 4b0: invokevirtual net/rim/device/api/util/DataBuffer.hashCode ()I
      // 4b3: invokevirtual net/rim/device/internal/browser/util/TimeLogger.startTimer (II)V
      // 4b6: aload 16
      // 4b8: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 4bb: istore 18
      // 4bd: new java/lang/Object
      // 4c0: dup
      // 4c1: bipush 31
      // 4c3: invokespecial net/rim/device/internal/compress/Inflater.<init> (I)V
      // 4c6: astore 19
      // 4c8: aload 19
      // 4ca: aload 11
      // 4cc: aload 16
      // 4ce: invokevirtual net/rim/device/api/util/DataBuffer.getArrayPosition ()I
      // 4d1: iload 18
      // 4d3: invokevirtual net/rim/device/internal/compress/Inflater.decompress ([BII)[B
      // 4d6: astore 11
      // 4d8: bipush 0
      // 4d9: istore 12
      // 4db: aload 11
      // 4dd: arraylength
      // 4de: istore 13
      // 4e0: bipush 0
      // 4e1: istore 14
      // 4e3: getstatic net/rim/device/internal/browser/util/TimeLogger._loggingEnabled Z
      // 4e6: ifeq 514
      // 4e9: invokestatic net/rim/device/internal/browser/util/TimeLogger.getInstance ()Lnet/rim/device/internal/browser/util/TimeLogger;
      // 4ec: bipush 10
      // 4ee: aload 16
      // 4f0: invokevirtual net/rim/device/api/util/DataBuffer.hashCode ()I
      // 4f3: invokevirtual net/rim/device/internal/browser/util/TimeLogger.stopTimer (II)V
      // 4f6: goto 514
      // 4f9: astore 20
      // 4fb: getstatic net/rim/device/internal/browser/util/TimeLogger._loggingEnabled Z
      // 4fe: ifeq 50e
      // 501: invokestatic net/rim/device/internal/browser/util/TimeLogger.getInstance ()Lnet/rim/device/internal/browser/util/TimeLogger;
      // 504: bipush 10
      // 506: aload 16
      // 508: invokevirtual net/rim/device/api/util/DataBuffer.hashCode ()I
      // 50b: invokevirtual net/rim/device/internal/browser/util/TimeLogger.stopTimer (II)V
      // 50e: aload 20
      // 510: athrow
      // 511: aconst_null
      // 512: astore 11
      // 514: aload 11
      // 516: ifnonnull 51c
      // 519: goto b6c
      // 51c: iload 13
      // 51e: ifgt 524
      // 521: goto b6c
      // 524: aload 0
      // 525: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._subDataMac Lnet/rim/device/api/crypto/HMAC;
      // 528: ifnull 537
      // 52b: aload 0
      // 52c: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._subDataMac Lnet/rim/device/api/crypto/HMAC;
      // 52f: invokevirtual net/rim/device/api/crypto/HMAC.reset ()V
      // 532: goto 537
      // 535: astore 16
      // 537: new java/lang/Object
      // 53a: dup
      // 53b: aload 11
      // 53d: iload 12
      // 53f: iload 13
      // 541: bipush 0
      // 542: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 545: astore 16
      // 547: aconst_null
      // 548: astore 18
      // 54a: iload 4
      // 54c: ifeq 587
      // 54f: aload 0
      // 550: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._inlineDataRefs Lnet/rim/device/api/util/IntHashtable;
      // 553: aload 16
      // 555: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 558: invokevirtual net/rim/device/api/util/IntHashtable.get (I)Ljava/lang/Object;
      // 55b: checkcast net/rim/device/apps/internal/browser/html/InlineDataRefHolder
      // 55e: astore 18
      // 560: aload 18
      // 562: getfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._str Ljava/lang/String;
      // 565: astore 17
      // 567: aload 0
      // 568: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._subDataMac Lnet/rim/device/api/crypto/HMAC;
      // 56b: ifnull 5c7
      // 56e: aload 0
      // 56f: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._subDataMac Lnet/rim/device/api/crypto/HMAC;
      // 572: aload 18
      // 574: getfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._data [B
      // 577: aload 18
      // 579: getfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._offset I
      // 57c: aload 18
      // 57e: getfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._length I
      // 581: invokevirtual net/rim/device/api/crypto/HMAC.update ([BII)V
      // 584: goto 5c7
      // 587: aload 16
      // 589: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 58c: istore 19
      // 58e: aload 16
      // 590: invokevirtual net/rim/device/api/util/DataBuffer.getArrayPosition ()I
      // 593: istore 20
      // 595: aload 0
      // 596: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._subDataMac Lnet/rim/device/api/crypto/HMAC;
      // 599: ifnull 5a9
      // 59c: aload 0
      // 59d: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._subDataMac Lnet/rim/device/api/crypto/HMAC;
      // 5a0: aload 11
      // 5a2: iload 20
      // 5a4: iload 19
      // 5a6: invokevirtual net/rim/device/api/crypto/HMAC.update ([BII)V
      // 5a9: new java/lang/Object
      // 5ac: dup
      // 5ad: aload 11
      // 5af: iload 20
      // 5b1: iload 19
      // 5b3: invokespecial java/lang/String.<init> ([BII)V
      // 5b6: astore 17
      // 5b8: aload 16
      // 5ba: iload 19
      // 5bc: invokevirtual net/rim/device/api/util/DataBuffer.skipBytes (I)I
      // 5bf: pop
      // 5c0: aload 17
      // 5c2: invokestatic net/rim/device/cldc/io/utility/URIEncoder.encodeBlanks (Ljava/lang/String;)Ljava/lang/String;
      // 5c5: astore 17
      // 5c7: aload 16
      // 5c9: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 5cc: istore 19
      // 5ce: aload 16
      // 5d0: invokevirtual net/rim/device/api/util/DataBuffer.getArrayPosition ()I
      // 5d3: istore 20
      // 5d5: aload 16
      // 5d7: iload 19
      // 5d9: invokevirtual net/rim/device/api/util/DataBuffer.skipBytes (I)I
      // 5dc: pop
      // 5dd: aload 16
      // 5df: invokevirtual net/rim/device/api/util/DataBuffer.readByteArray ()[B
      // 5e2: astore 21
      // 5e4: aconst_null
      // 5e5: astore 22
      // 5e7: aconst_null
      // 5e8: astore 23
      // 5ea: aconst_null
      // 5eb: astore 24
      // 5ed: iload 15
      // 5ef: ifeq 5fb
      // 5f2: new java/lang/Object
      // 5f5: dup
      // 5f6: invokespecial net/rim/device/api/io/http/HttpHeaders.<init> ()V
      // 5f9: astore 24
      // 5fb: aload 16
      // 5fd: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 600: ifgt 606
      // 603: goto 794
      // 606: aload 16
      // 608: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 60b: istore 25
      // 60d: new java/lang/Object
      // 610: dup
      // 611: aload 11
      // 613: aload 16
      // 615: invokevirtual net/rim/device/api/util/DataBuffer.getArrayPosition ()I
      // 618: iload 25
      // 61a: bipush 0
      // 61b: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 61e: astore 26
      // 620: aload 16
      // 622: iload 25
      // 624: invokevirtual net/rim/device/api/util/DataBuffer.skipBytes (I)I
      // 627: pop
      // 628: aload 26
      // 62a: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 62d: ifgt 633
      // 630: goto 7a3
      // 633: aload 26
      // 635: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 638: istore 27
      // 63a: new java/lang/Object
      // 63d: dup
      // 63e: aload 11
      // 640: aload 26
      // 642: invokevirtual net/rim/device/api/util/DataBuffer.getArrayPosition ()I
      // 645: iload 27
      // 647: invokespecial java/lang/String.<init> ([BII)V
      // 64a: astore 28
      // 64c: aconst_null
      // 64d: astore 29
      // 64f: aload 28
      // 651: invokestatic net/rim/vm/Memory.stringIntern (Ljava/lang/String;)Ljava/lang/String;
      // 654: astore 28
      // 656: aload 26
      // 658: iload 27
      // 65a: invokevirtual net/rim/device/api/util/DataBuffer.skipBytes (I)I
      // 65d: pop
      // 65e: aload 26
      // 660: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 663: istore 27
      // 665: aload 28
      // 667: bipush 0
      // 668: invokevirtual java/lang/String.charAt (I)C
      // 66b: ldc_w 1701707776
      // 66e: invokestatic net/rim/device/api/util/CharacterUtilities.toLowerCase (CI)C
      // 671: istore 30
      // 673: iload 30
      // 675: bipush 99
      // 677: if_icmpne 6a1
      // 67a: aload 28
      // 67c: ldc_w "content-type"
      // 67f: ldc_w 1701707776
      // 682: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 685: ifeq 6a1
      // 688: new java/lang/Object
      // 68b: dup
      // 68c: aload 11
      // 68e: aload 26
      // 690: invokevirtual net/rim/device/api/util/DataBuffer.getArrayPosition ()I
      // 693: iload 27
      // 695: invokespecial java/lang/String.<init> ([BII)V
      // 698: astore 29
      // 69a: aload 29
      // 69c: astore 22
      // 69e: goto 764
      // 6a1: iload 30
      // 6a3: bipush 120
      // 6a5: if_icmpne 719
      // 6a8: aload 28
      // 6aa: ldc_w "x-rim-image-original-size"
      // 6ad: ldc_w 1701707776
      // 6b0: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 6b3: ifeq 719
      // 6b6: aload 23
      // 6b8: ifnonnull 6c4
      // 6bb: new net/rim/device/apps/internal/browser/page/RendererImageContainer
      // 6be: dup
      // 6bf: invokespecial net/rim/device/apps/internal/browser/page/RendererImageContainer.<init> ()V
      // 6c2: astore 23
      // 6c4: new java/lang/Object
      // 6c7: dup
      // 6c8: aload 11
      // 6ca: aload 26
      // 6cc: invokevirtual net/rim/device/api/util/DataBuffer.getArrayPosition ()I
      // 6cf: iload 27
      // 6d1: invokespecial java/lang/String.<init> ([BII)V
      // 6d4: astore 29
      // 6d6: aload 29
      // 6d8: ifnull 709
      // 6db: aload 29
      // 6dd: bipush 44
      // 6df: invokevirtual java/lang/String.indexOf (I)I
      // 6e2: istore 31
      // 6e4: aload 23
      // 6e6: aload 29
      // 6e8: bipush 0
      // 6e9: iload 31
      // 6eb: bipush 10
      // 6ed: invokestatic net/rim/device/api/util/NumberUtilities.parseInt (Ljava/lang/String;III)I
      // 6f0: putfield net/rim/device/apps/internal/browser/page/RendererImageContainer._width I
      // 6f3: aload 23
      // 6f5: aload 29
      // 6f7: iload 31
      // 6f9: bipush 1
      // 6fa: iadd
      // 6fb: ldc_w 2147483647
      // 6fe: bipush 10
      // 700: invokestatic net/rim/device/api/util/NumberUtilities.parseInt (Ljava/lang/String;III)I
      // 703: putfield net/rim/device/apps/internal/browser/page/RendererImageContainer._height I
      // 706: goto 764
      // 709: aload 23
      // 70b: aload 23
      // 70d: bipush -1
      // 70f: dup_x1
      // 710: putfield net/rim/device/apps/internal/browser/page/RendererImageContainer._height I
      // 713: putfield net/rim/device/apps/internal/browser/page/RendererImageContainer._width I
      // 716: goto 764
      // 719: iload 30
      // 71b: bipush 115
      // 71d: if_icmpne 764
      // 720: aload 28
      // 722: ldc_w "set-cookie"
      // 725: ldc_w 1701707776
      // 728: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 72b: ifeq 764
      // 72e: new java/lang/Object
      // 731: dup
      // 732: aload 11
      // 734: aload 26
      // 736: invokevirtual net/rim/device/api/util/DataBuffer.getArrayPosition ()I
      // 739: iload 27
      // 73b: invokespecial java/lang/String.<init> ([BII)V
      // 73e: astore 29
      // 740: aload 0
      // 741: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 744: ifnull 764
      // 747: new java/lang/Object
      // 74a: dup
      // 74b: aload 0
      // 74c: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // 74f: aload 17
      // 751: aload 29
      // 753: invokespecial net/rim/device/api/browser/field/SetHttpCookieEvent.<init> (Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V
      // 756: astore 31
      // 758: aload 0
      // 759: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 75c: aload 31
      // 75e: invokeinterface net/rim/device/api/browser/field/RenderingApplication.eventOccurred (Lnet/rim/device/api/browser/field/Event;)Ljava/lang/Object; 2
      // 763: pop
      // 764: iload 15
      // 766: ifeq 789
      // 769: aload 29
      // 76b: ifnonnull 780
      // 76e: new java/lang/Object
      // 771: dup
      // 772: aload 11
      // 774: aload 26
      // 776: invokevirtual net/rim/device/api/util/DataBuffer.getArrayPosition ()I
      // 779: iload 27
      // 77b: invokespecial java/lang/String.<init> ([BII)V
      // 77e: astore 29
      // 780: aload 24
      // 782: aload 28
      // 784: aload 29
      // 786: invokevirtual net/rim/device/api/io/http/HttpHeaders.addProperty (Ljava/lang/String;Ljava/lang/String;)V
      // 789: aload 26
      // 78b: iload 27
      // 78d: invokevirtual net/rim/device/api/util/DataBuffer.skipBytes (I)I
      // 790: pop
      // 791: goto 628
      // 794: new java/lang/Object
      // 797: dup
      // 798: aload 11
      // 79a: iload 20
      // 79c: iload 19
      // 79e: invokespecial java/lang/String.<init> ([BII)V
      // 7a1: astore 22
      // 7a3: bipush 0
      // 7a4: istore 25
      // 7a6: sipush 200
      // 7a9: istore 26
      // 7ab: aload 16
      // 7ad: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 7b0: ifle 7c1
      // 7b3: aload 16
      // 7b5: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 7b8: istore 26
      // 7ba: aload 16
      // 7bc: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 7bf: istore 25
      // 7c1: iload 26
      // 7c3: sipush 200
      // 7c6: if_icmpeq 7cc
      // 7c9: goto 8ad
      // 7cc: aload 22
      // 7ce: ifnonnull 7d4
      // 7d1: goto 8ad
      // 7d4: aload 22
      // 7d6: invokestatic net/rim/device/api/system/EncodedImage.isMIMETypeSupported (Ljava/lang/String;)Z
      // 7d9: ifne 7df
      // 7dc: goto 87c
      // 7df: aload 0
      // 7e0: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // 7e3: invokevirtual net/rim/device/apps/internal/browser/page/BrowserContentImpl.inlineImageReceived ()V
      // 7e6: aload 0
      // 7e7: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingOptions Lnet/rim/device/api/browser/field/RenderingOptions;
      // 7ea: ldc2_w 4550690918222697397
      // 7ed: bipush 5
      // 7ef: bipush 1
      // 7f0: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithBooleanValue (JIZ)Z
      // 7f3: ifne 7f9
      // 7f6: goto 8ad
      // 7f9: aload 23
      // 7fb: ifnonnull 807
      // 7fe: new net/rim/device/apps/internal/browser/page/RendererImageContainer
      // 801: dup
      // 802: invokespecial net/rim/device/apps/internal/browser/page/RendererImageContainer.<init> ()V
      // 805: astore 23
      // 807: aload 23
      // 809: aload 17
      // 80b: putfield net/rim/device/apps/internal/browser/page/RendererImageContainer._url Ljava/lang/String;
      // 80e: aload 23
      // 810: aload 21
      // 812: bipush 0
      // 813: aload 21
      // 815: arraylength
      // 816: aload 22
      // 818: invokestatic net/rim/device/apps/internal/browser/util/ImageConverter.convert ([BIILjava/lang/String;)Lnet/rim/device/api/system/EncodedImage;
      // 81b: putfield net/rim/device/apps/internal/browser/page/RendererImageContainer._image Lnet/rim/device/api/system/EncodedImage;
      // 81e: aload 0
      // 81f: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._inlineData Ljava/util/Hashtable;
      // 822: aload 17
      // 824: aload 23
      // 826: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 829: pop
      // 82a: iload 4
      // 82c: ifeq 856
      // 82f: aload 0
      // 830: aload 0
      // 831: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._imageLayoutCount I
      // 834: bipush 1
      // 835: iadd
      // 836: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._imageLayoutCount I
      // 839: aload 0
      // 83a: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // 83d: aload 23
      // 83f: invokevirtual net/rim/device/apps/internal/browser/html/HTMLBrowserContent.secondaryURLReady (Lnet/rim/device/apps/internal/browser/page/RendererImageContainer;)V
      // 842: aload 0
      // 843: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._imageLayoutCount I
      // 846: aload 0
      // 847: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._imageLayoutLimit I
      // 84a: if_icmplt 856
      // 84d: aload 0
      // 84e: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.secondaryImagesLoaded ()V
      // 851: aload 0
      // 852: bipush 0
      // 853: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._imageLayoutCount I
      // 856: aload 17
      // 858: aload 0
      // 859: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._iconUrl Ljava/lang/String;
      // 85c: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 85f: ifeq 8ad
      // 862: aload 0
      // 863: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // 866: aload 0
      // 867: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._iconUrl Ljava/lang/String;
      // 86a: invokevirtual net/rim/device/api/browser/field/BrowserContentBaseImpl.setIconUrl (Ljava/lang/String;)V
      // 86d: aload 0
      // 86e: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // 871: aload 23
      // 873: getfield net/rim/device/apps/internal/browser/page/RendererImageContainer._image Lnet/rim/device/api/system/EncodedImage;
      // 876: invokevirtual net/rim/device/apps/internal/browser/html/HTMLBrowserContent.setIcon (Lnet/rim/device/api/system/EncodedImage;)V
      // 879: goto 8ad
      // 87c: new net/rim/device/apps/internal/browser/html/InlineDataRefHolder
      // 87f: dup
      // 880: invokespecial net/rim/device/apps/internal/browser/html/InlineDataRefHolder.<init> ()V
      // 883: astore 27
      // 885: aload 27
      // 887: aload 22
      // 889: putfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._str Ljava/lang/String;
      // 88c: aload 27
      // 88e: aload 21
      // 890: putfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._data [B
      // 893: aload 27
      // 895: bipush 0
      // 896: putfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._offset I
      // 899: aload 27
      // 89b: aload 21
      // 89d: arraylength
      // 89e: putfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._length I
      // 8a1: aload 0
      // 8a2: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._inlineData Ljava/util/Hashtable;
      // 8a5: aload 17
      // 8a7: aload 27
      // 8a9: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 8ac: pop
      // 8ad: aconst_null
      // 8ae: astore 27
      // 8b0: aload 16
      // 8b2: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 8b5: iload 25
      // 8b7: if_icmple 921
      // 8ba: aload 16
      // 8bc: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 8bf: istore 28
      // 8c1: aload 16
      // 8c3: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 8c6: istore 29
      // 8c8: iload 29
      // 8ca: bipush 1
      // 8cb: if_icmplt 921
      // 8ce: aload 0
      // 8cf: aload 0
      // 8d0: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._totalInlineFragments I
      // 8d3: iload 29
      // 8d5: iadd
      // 8d6: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._totalInlineFragments I
      // 8d9: new net/rim/device/apps/internal/browser/html/FragmentHolder
      // 8dc: dup
      // 8dd: iload 29
      // 8df: aload 17
      // 8e1: aload 24
      // 8e3: aload 21
      // 8e5: iload 26
      // 8e7: invokespecial net/rim/device/apps/internal/browser/html/FragmentHolder.<init> (ILjava/lang/String;Lnet/rim/device/api/io/http/HttpHeaders;[BI)V
      // 8ea: astore 27
      // 8ec: aload 18
      // 8ee: ifnull 906
      // 8f1: aload 27
      // 8f3: aload 18
      // 8f5: getfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._rawOffset I
      // 8f8: bipush 2
      // 8fa: isub
      // 8fb: aload 18
      // 8fd: getfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._rawLength I
      // 900: bipush 2
      // 902: iadd
      // 903: invokevirtual net/rim/device/apps/internal/browser/html/FragmentHolder.addTrimSegment (II)V
      // 906: aload 27
      // 908: iload 6
      // 90a: bipush 2
      // 90c: isub
      // 90d: iload 10
      // 90f: bipush 2
      // 911: iadd
      // 912: invokevirtual net/rim/device/apps/internal/browser/html/FragmentHolder.addTrimSegment (II)V
      // 915: aload 0
      // 916: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._inlineFragments Lnet/rim/device/api/util/IntHashtable;
      // 919: iload 28
      // 91b: aload 27
      // 91d: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 920: pop
      // 921: aload 0
      // 922: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // 925: aload 17
      // 927: iload 26
      // 929: invokevirtual net/rim/device/apps/internal/browser/html/HTMLBrowserContent.addInlinedUrl (Ljava/lang/String;I)V
      // 92c: iload 25
      // 92e: ifgt 939
      // 931: aload 27
      // 933: ifnonnull 939
      // 936: goto a0f
      // 939: iload 15
      // 93b: ifne 941
      // 93e: goto a0f
      // 941: aload 0
      // 942: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 945: ifnonnull 94b
      // 948: goto a0f
      // 94b: aload 0
      // 94c: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._subDataMac Lnet/rim/device/api/crypto/HMAC;
      // 94f: aload 11
      // 951: iload 12
      // 953: iload 13
      // 955: iload 25
      // 957: isub
      // 958: invokevirtual net/rim/device/api/crypto/HMAC.update ([BII)V
      // 95b: aload 27
      // 95d: ifnull 974
      // 960: aload 27
      // 962: aload 0
      // 963: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._subDataMac Lnet/rim/device/api/crypto/HMAC;
      // 966: invokevirtual net/rim/device/api/crypto/AbstractMAC.getMAC ()[B
      // 969: invokevirtual net/rim/device/apps/internal/browser/html/FragmentHolder.setStoredMAC ([B)V
      // 96c: goto a0f
      // 96f: astore 28
      // 971: goto a0f
      // 974: aload 0
      // 975: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._subDataMac Lnet/rim/device/api/crypto/HMAC;
      // 978: aload 11
      // 97a: iload 14
      // 97c: ifeq 98a
      // 97f: iload 8
      // 981: iload 13
      // 983: iadd
      // 984: iload 25
      // 986: isub
      // 987: goto 98f
      // 98a: iload 13
      // 98c: iload 25
      // 98e: isub
      // 98f: invokevirtual net/rim/device/api/crypto/AbstractMAC.checkMAC ([BI)Z
      // 992: ifeq a0f
      // 995: new net/rim/device/apps/internal/browser/api/CacheSubDataEvent
      // 998: dup
      // 999: aload 0
      // 99a: aload 17
      // 99c: aload 24
      // 99e: aload 21
      // 9a0: iload 26
      // 9a2: invokespecial net/rim/device/apps/internal/browser/api/CacheSubDataEvent.<init> (Ljava/lang/Object;Ljava/lang/String;Lnet/rim/device/api/io/http/HttpHeaders;[BI)V
      // 9a5: astore 28
      // 9a7: aload 0
      // 9a8: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 9ab: aload 28
      // 9ad: invokeinterface net/rim/device/api/browser/field/RenderingApplication.eventOccurred (Lnet/rim/device/api/browser/field/Event;)Ljava/lang/Object; 2
      // 9b2: ifnull a0f
      // 9b5: aload 0
      // 9b6: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimOffsets [I
      // 9b9: ifnonnull 9ca
      // 9bc: aload 0
      // 9bd: bipush 0
      // 9be: newarray 10
      // 9c0: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimOffsets [I
      // 9c3: aload 0
      // 9c4: bipush 0
      // 9c5: newarray 10
      // 9c7: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimLengths [I
      // 9ca: aload 18
      // 9cc: ifnull 9ed
      // 9cf: aload 0
      // 9d0: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimOffsets [I
      // 9d3: aload 18
      // 9d5: getfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._rawOffset I
      // 9d8: bipush 2
      // 9da: isub
      // 9db: invokestatic net/rim/device/api/util/Arrays.add ([II)V
      // 9de: aload 0
      // 9df: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimLengths [I
      // 9e2: aload 18
      // 9e4: getfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._rawLength I
      // 9e7: bipush 2
      // 9e9: iadd
      // 9ea: invokestatic net/rim/device/api/util/Arrays.add ([II)V
      // 9ed: aload 0
      // 9ee: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimOffsets [I
      // 9f1: iload 6
      // 9f3: bipush 2
      // 9f5: isub
      // 9f6: invokestatic net/rim/device/api/util/Arrays.add ([II)V
      // 9f9: aload 0
      // 9fa: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._trimLengths [I
      // 9fd: iload 10
      // 9ff: bipush 2
      // a01: iadd
      // a02: invokestatic net/rim/device/api/util/Arrays.add ([II)V
      // a05: goto a0f
      // a08: astore 28
      // a0a: goto a0f
      // a0d: astore 28
      // a0f: iload 26
      // a11: sipush 304
      // a14: if_icmpeq a1a
      // a17: goto b6c
      // a1a: aload 0
      // a1b: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._inlineData Ljava/util/Hashtable;
      // a1e: aload 17
      // a20: getstatic net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.INLINE_DATA_304 Ljava/lang/String;
      // a23: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // a26: pop
      // a27: aload 0
      // a28: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // a2b: aload 17
      // a2d: invokevirtual net/rim/device/apps/internal/browser/page/BrowserContentImpl.removeInlineDataRef (Ljava/lang/String;)Z
      // a30: ifne a40
      // a33: aload 0
      // a34: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // a37: aload 17
      // a39: bipush 1
      // a3a: bipush 1
      // a3b: invokevirtual net/rim/device/apps/internal/browser/page/BrowserContentImpl.requestSecondaryURL (Ljava/lang/String;ZZ)Z
      // a3e: pop
      // a3f: return
      // a40: aload 22
      // a42: ifnull a50
      // a45: aload 22
      // a47: invokestatic net/rim/device/api/system/EncodedImage.isMIMETypeSupported (Ljava/lang/String;)Z
      // a4a: ifne a50
      // a4d: goto b6c
      // a50: aload 0
      // a51: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // a54: invokevirtual net/rim/device/apps/internal/browser/page/BrowserContentImpl.inlineImageReceived ()V
      // a57: return
      // a58: aload 1
      // a59: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.getPosition ()Lnet/rim/device/internal/browser/util/PipeContext;
      // a5c: astore 16
      // a5e: aload 16
      // a60: ifnull a6b
      // a63: aload 16
      // a65: getfield net/rim/device/internal/browser/util/PipeContext._numRead I
      // a68: goto a6c
      // a6b: bipush 0
      // a6c: istore 17
      // a6e: aload 1
      // a6f: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readByteArrayRef ()Lnet/rim/device/internal/browser/util/PipePtr;
      // a72: astore 9
      // a74: aload 16
      // a76: ifnull a84
      // a79: aload 16
      // a7b: getfield net/rim/device/internal/browser/util/PipeContext._numRead I
      // a7e: iload 17
      // a80: isub
      // a81: goto a85
      // a84: bipush 0
      // a85: istore 10
      // a87: aload 9
      // a89: invokevirtual net/rim/device/internal/browser/util/PipePtr.getLength ()I
      // a8c: ifgt a92
      // a8f: goto b6c
      // a92: aload 9
      // a94: invokevirtual net/rim/device/internal/browser/util/PipePtr.getData ()[B
      // a97: astore 3
      // a98: new java/lang/Object
      // a9b: dup
      // a9c: aload 3
      // a9d: aload 9
      // a9f: invokevirtual net/rim/device/internal/browser/util/PipePtr.getOffset ()I
      // aa2: aload 9
      // aa4: invokevirtual net/rim/device/internal/browser/util/PipePtr.getLength ()I
      // aa7: bipush 0
      // aa8: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // aab: astore 18
      // aad: aload 18
      // aaf: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // ab2: istore 19
      // ab4: aload 18
      // ab6: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // ab9: istore 20
      // abb: aload 18
      // abd: invokevirtual net/rim/device/api/util/DataBuffer.getArrayPosition ()I
      // ac0: istore 21
      // ac2: new java/lang/Object
      // ac5: dup
      // ac6: aload 3
      // ac7: iload 21
      // ac9: iload 20
      // acb: invokespecial java/lang/String.<init> ([BII)V
      // ace: astore 22
      // ad0: aload 18
      // ad2: iload 20
      // ad4: invokevirtual net/rim/device/api/util/DataBuffer.skipBytes (I)I
      // ad7: pop
      // ad8: aload 22
      // ada: invokestatic net/rim/device/cldc/io/utility/URIEncoder.encodeBlanks (Ljava/lang/String;)Ljava/lang/String;
      // add: astore 22
      // adf: aload 0
      // ae0: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingOptions Lnet/rim/device/api/browser/field/RenderingOptions;
      // ae3: ldc2_w 4550690918222697397
      // ae6: bipush 5
      // ae8: bipush 1
      // ae9: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithBooleanValue (JIZ)Z
      // aec: ifeq af8
      // aef: aload 0
      // af0: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // af3: aload 22
      // af5: invokevirtual net/rim/device/apps/internal/browser/page/BrowserContentImpl.inlineDataRefReceived (Ljava/lang/String;)V
      // af8: new net/rim/device/apps/internal/browser/html/InlineDataRefHolder
      // afb: dup
      // afc: invokespecial net/rim/device/apps/internal/browser/html/InlineDataRefHolder.<init> ()V
      // aff: astore 23
      // b01: aload 23
      // b03: iload 17
      // b05: putfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._rawOffset I
      // b08: aload 23
      // b0a: iload 10
      // b0c: putfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._rawLength I
      // b0f: aload 23
      // b11: aload 22
      // b13: putfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._str Ljava/lang/String;
      // b16: aload 23
      // b18: aload 3
      // b19: putfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._data [B
      // b1c: aload 23
      // b1e: iload 21
      // b20: putfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._offset I
      // b23: aload 23
      // b25: iload 20
      // b27: putfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._length I
      // b2a: aload 0
      // b2b: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._inlineDataRefs Lnet/rim/device/api/util/IntHashtable;
      // b2e: iload 19
      // b30: aload 23
      // b32: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // b35: pop
      // b36: return
      // b37: aload 1
      // b38: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.readCompressedInt ()I
      // b3b: istore 7
      // b3d: iload 7
      // b3f: bipush 1
      // b40: if_icmpne b60
      // b43: aload 1
      // b44: invokevirtual java/io/DataInputStream.readUnsignedByte ()I
      // b47: bipush 36
      // b49: if_icmpne b60
      // b4c: aload 0
      // b4d: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentRadioGroups Ljava/util/Hashtable;
      // b50: ifnull b5a
      // b53: aload 0
      // b54: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentRadioGroups Ljava/util/Hashtable;
      // b57: invokevirtual java/util/Hashtable.clear ()V
      // b5a: aload 0
      // b5b: aconst_null
      // b5c: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentForm Lnet/rim/device/apps/internal/browser/html/HTMLForm;
      // b5f: return
      // b60: aload 1
      // b61: iload 7
      // b63: invokevirtual java/io/DataInputStream.skipBytes (I)I
      // b66: pop
      // b67: return
      // b68: aload 1
      // b69: invokevirtual net/rim/device/apps/internal/browser/stack/WAPInputStream.skipByteArray ()V
      // b6c: return
      // try (89 -> 98): 99 null
      // try (99 -> 102): 99 null
      // try (274 -> 277): 278 null
      // try (290 -> 405): 406 null
      // try (520 -> 549): 557 null
      // try (557 -> 558): 557 null
      // try (578 -> 581): 582 null
      // try (1058 -> 1063): 1064 null
      // try (1066 -> 1135): 1136 null
      // try (1066 -> 1135): 1138 null
   }

   private final void handleTag(int tag) {
      WAPInputStream in = this._currentContext._in;
      if (tag == 4) {
         in.readCompressedInt();
      }

      int properties = in.readUnsignedByte();
      int anchorIndex = -1;
      if ((properties & 4) != 0) {
         anchorIndex = in.readCompressedInt();
      }

      this._lastId = null;
      this._lastClass = null;
      this._lastStyle = null;
      this._lastDirection = -1;
      this._lastName = null;
      boolean hasContent = (properties & 2) != 0;
      boolean hasAttributes = (properties & 1) != 0;
      HTMLGenericElement element = (HTMLGenericElement)this._document.createElement(tag);
      int start = this._numAttributes;
      if (hasAttributes) {
         for (int currentByte = in.readUnsignedByte(); currentByte != 0; currentByte = in.readUnsignedByte()) {
            boolean needToCheckForValue = false;
            if (currentByte == 4) {
               in.readCompressedInt();
               needToCheckForValue = true;
            } else {
               if (currentByte == -1) {
                  throw new Object();
               }

               if (currentByte == 0) {
                  throw new Object();
               }

               if (currentByte < 5) {
                  throw new Object();
               }

               if (currentByte < 80) {
                  if (currentByte == 17) {
                     this._lastDirection = 0;
                  } else if (currentByte == 18) {
                     this._lastDirection = 8;
                  }
               } else {
                  needToCheckForValue = true;
               }
            }

            if (this._numAttributes == this._attributeItems.length) {
               int newSize = this._attributeItems.length + Array.getSectionSize(this._attributeItems);
               Array.resize(this._attributeItems, newSize);
               Array.resize(this._attributeValuesInts, newSize);
               Array.resize(this._attributeValues, newSize);
            }

            if (!needToCheckForValue) {
               String value;
               int attrib;
               switch (currentByte) {
                  case 18:
                     value = "";
                     attrib = currentByte;
                     break;
                  case 19:
                     value = "1";
                     attrib = 122;
                     break;
                  case 20:
                     value = "0";
                     attrib = 122;
                     break;
                  case 21:
                  default:
                     value = "get";
                     attrib = 140;
                     break;
                  case 22:
                     value = "post";
                     attrib = 140;
                     break;
                  case 23:
                     value = "yes";
                     attrib = 176;
                     break;
                  case 24:
                     value = "no";
                     attrib = 176;
                     break;
                  case 25:
                     value = "auto";
                     attrib = 176;
                     break;
                  case 26:
                     value = "password";
                     attrib = 190;
                     break;
                  case 27:
                     value = "checkbox";
                     attrib = 190;
                     break;
                  case 28:
                     value = "radio";
                     attrib = 190;
                     break;
                  case 29:
                     value = "submit";
                     attrib = 190;
                     break;
                  case 30:
                     value = "reset";
                     attrib = 190;
                     break;
                  case 31:
                     value = "file";
                     attrib = 190;
                     break;
                  case 32:
                     value = "hidden";
                     attrib = 190;
                     break;
                  case 33:
                     value = "image";
                     attrib = 190;
                     break;
                  case 34:
                     value = "button";
                     attrib = 190;
                     break;
                  case 35:
                     value = "text";
                     attrib = 190;
                     break;
                  case 36:
                     value = "top";
                     attrib = 192;
                     break;
                  case 37:
                     value = "middle";
                     attrib = 192;
                     break;
                  case 38:
                     value = "bottom";
                     attrib = 192;
                     break;
                  case 39:
                     value = "baseline";
                     attrib = 192;
                     break;
                  case 40:
                     value = "none";
                     attrib = 173;
                     break;
                  case 41:
                     value = "groups";
                     attrib = 173;
                     break;
                  case 42:
                     value = "rows";
                     attrib = 173;
                     break;
                  case 43:
                     value = "cols";
                     attrib = 173;
                     break;
                  case 44:
                     value = "all";
                     attrib = 173;
                     break;
                  case 45:
                     value = "void";
                     attrib = 121;
                     break;
                  case 46:
                     value = "above";
                     attrib = 121;
                     break;
                  case 47:
                     value = "below";
                     attrib = 121;
                     break;
                  case 48:
                     value = "hsides";
                     attrib = 121;
                     break;
                  case 49:
                     value = "lhs";
                     attrib = 121;
                     break;
                  case 50:
                     value = "rhs";
                     attrib = 121;
                     break;
                  case 51:
                     value = "vsides";
                     attrib = 121;
                     break;
                  case 52:
                     value = "box";
                     attrib = 121;
                     break;
                  case 53:
                     value = "border";
                     attrib = 121;
                     break;
                  case 54:
                     value = "scroll";
                     attrib = 200;
                     break;
                  case 55:
                     value = "slide";
                     attrib = 200;
                     break;
                  case 56:
                     value = "alternate";
                     attrib = 200;
                     break;
                  case 57:
                     value = "right";
                     attrib = 201;
                     break;
                  case 58:
                     value = "left";
                     attrib = 201;
                     break;
                  case 59:
                     value = "up";
                     attrib = 201;
                     break;
                  case 60:
                     value = "down";
                     attrib = 201;
                     break;
                  case 61:
                     value = "fixed";
                     attrib = 205;
                     break;
                  case 62:
                     value = "true";
                     attrib = 206;
                     break;
                  case 63:
                     value = "false";
                     attrib = 206;
               }

               this._attributeValues[this._numAttributes] = this._attributeValues;
               this._attributeValuesInts[this._numAttributes] = 0;
               element.setAttributeValue(attrib, value);
            } else {
               int attribProperties = in.readUnsignedByte();
               if ((attribProperties & 1) == 0) {
                  this._attributeValues[this._numAttributes] = "";
                  this._attributeValuesInts[this._numAttributes] = 0;
                  element.setAttributeValue(currentByte, "");
               } else {
                  int tokenMask = attribProperties & 14;
                  int value = 0;
                  if (tokenMask != 0) {
                     switch (tokenMask) {
                        case 8:
                           value |= in.readUnsignedByte();
                           value <<= 8;
                        case 6:
                           value |= in.readUnsignedByte();
                           value <<= 8;
                        case 4:
                           value |= in.readUnsignedByte();
                           value <<= 8;
                        case 2:
                           value |= in.readUnsignedByte();
                        default:
                           this._attributeValuesInts[this._numAttributes] = value;
                           this._attributeValues[this._numAttributes] = null;
                           element.setAttributeValue(currentByte, value);
                     }
                  } else {
                     this._attributeValues[this._numAttributes] = this._currentContext._richTextStrings[in.readCompressedInt()];
                     this._attributeValuesInts[this._numAttributes] = 0;
                     element.setAttributeValue(currentByte, (String)this._attributeValues[this._numAttributes]);
                  }

                  switch (currentByte) {
                     case 100:
                        this._lastClass = this.getAttributeValue(null, this._numAttributes);
                        break;
                     case 129:
                        this._lastId = this.getAttributeValue(null, this._numAttributes);
                        break;
                     case 142:
                        this._lastName = this.getAttributeValue(null, this._numAttributes);
                        break;
                     case 184:
                        this._lastStyle = this.getAttributeValue(null, this._numAttributes);
                  }
               }
            }

            this._attributeItems[this._numAttributes] = (byte)currentByte;
            this._numAttributes++;
         }
      }

      if (hasContent) {
         switch (tag) {
            case 10:
            case 12:
            case 13:
            case 18:
            case 24:
            case 37:
            case 46:
            case 50:
            case 51:
            case 53:
            case 58:
            case 61:
            case 69:
               hasContent = false;
               this._skipPopCalls.push(this._tagStack.size());
         }
      }

      boolean max_tag_stack_depth_reached = this._tagStack.size() >= 200;
      if (this._tagStack.size() == 0) {
         this._document.appendChild(element);
         element.inherit(this._coreDocElement);
      } else if (!max_tag_stack_depth_reached) {
         HTMLNode parent = (HTMLNode)this._tagStack.peek();
         parent.appendChild(element);
         element.inherit(parent);
      }

      this.appendCurrentString(element);
      this._tagStack.push(element);
      if (this._processStyleSheets) {
         this._tagClasses.push(this._lastClass);
         this._tagIds.push(this._lastId);
      }

      if (!max_tag_stack_depth_reached) {
         this.processTag(element, start, this._numAttributes, true, hasAttributes, hasContent, anchorIndex);
      }

      if (!hasContent) {
         if (this._processStyleSheets) {
            this.removeDescendantStyles(this._tagStack.size() - 1);
            this._tagClasses.pop();
            this._tagIds.pop();
         }

         this._tagStack.pop();
      }
   }

   private final InlineDataRefHolder getTextDataWithEncoding(byte[] data, String encoding) {
      int offset = 0;
      int length = data.length;
      if (encoding == null) {
         if (length >= 3 && data[0] == -17 && data[1] == -69 && data[2] == -65) {
            offset = 3;
            encoding = "utf-8";
         } else if (length >= 2 && data[0] == -2 && data[1] == -1) {
            offset = 2;
            encoding = "utf-16be";
         } else if (length >= 2 && data[0] == -1 && data[1] == -2) {
            offset = 2;
            encoding = "utf-16le";
         }
      } else if (StringUtilities.strEqualIgnoreCase(encoding, "utf-8", 1701707776) && length >= 3) {
         if (data[0] == -17 && data[1] == -69 && data[2] == -65) {
            offset = 3;
         }
      } else if (StringUtilities.strEqualIgnoreCase(encoding, "utf-16", 1701707776) && length >= 2) {
         if (data[0] == -2 && data[1] == -1) {
            offset = 2;
            encoding = "utf-16be";
         } else if (data[0] == -1 && data[1] == -2) {
            offset = 2;
            encoding = "utf-16le";
         }
      }

      InlineDataRefHolder textData = new InlineDataRefHolder();
      textData._data = data;
      textData._offset = offset;
      textData._length = length - offset;
      textData._encoding = encoding;
      return textData;
   }

   private final void executeScript(JavaScriptItem param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: aload 1
      // 001: getfield net/rim/device/apps/internal/browser/html/JavaScriptItem._language Ljava/lang/String;
      // 004: ifnull 014
      // 007: aload 1
      // 008: aload 1
      // 009: getfield net/rim/device/apps/internal/browser/html/JavaScriptItem._language Ljava/lang/String;
      // 00c: bipush 10
      // 00e: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 011: putfield net/rim/device/apps/internal/browser/html/JavaScriptItem._language Ljava/lang/String;
      // 014: aconst_null
      // 015: astore 2
      // 016: aload 1
      // 017: getfield net/rim/device/apps/internal/browser/html/JavaScriptItem._src Ljava/lang/String;
      // 01a: ifnonnull 020
      // 01d: goto 2ad
      // 020: aload 0
      // 021: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._inlineData Ljava/util/Hashtable;
      // 024: aload 1
      // 025: getfield net/rim/device/apps/internal/browser/html/JavaScriptItem._src Ljava/lang/String;
      // 028: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 02b: astore 3
      // 02c: aconst_null
      // 02d: astore 4
      // 02f: aload 3
      // 030: ifnull 03d
      // 033: getstatic net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.INLINE_DATA_304 Ljava/lang/String;
      // 036: aload 3
      // 037: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 03a: ifeq 054
      // 03d: aload 0
      // 03e: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._browserContent Lnet/rim/device/apps/internal/browser/html/HTMLBrowserContent;
      // 041: aload 1
      // 042: getfield net/rim/device/apps/internal/browser/html/JavaScriptItem._src Ljava/lang/String;
      // 045: invokevirtual net/rim/device/apps/internal/browser/page/BrowserContentImpl.resolveUrl (Ljava/lang/String;)Ljava/lang/String;
      // 048: astore 4
      // 04a: aload 0
      // 04b: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._inlineData Ljava/util/Hashtable;
      // 04e: aload 4
      // 050: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 053: astore 3
      // 054: aload 3
      // 055: dup
      // 056: instanceof net/rim/device/apps/internal/browser/html/InlineDataRefHolder
      // 059: ifne 060
      // 05c: pop
      // 05d: goto 096
      // 060: checkcast net/rim/device/apps/internal/browser/html/InlineDataRefHolder
      // 063: astore 5
      // 065: aload 0
      // 066: bipush 2
      // 068: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.loadingStatusEventOccurred (I)V
      // 06b: aload 0
      // 06c: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._scriptEngine Lnet/rim/device/apps/internal/browser/javascript/JavaScriptInterpreter;
      // 06f: aload 5
      // 071: getfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._data [B
      // 074: aload 5
      // 076: getfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._offset I
      // 079: aload 5
      // 07b: getfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._length I
      // 07e: invokeinterface net/rim/device/apps/internal/browser/javascript/JavaScriptInterpreter.executeCompiledScript ([BII)Z 4
      // 083: pop
      // 084: aload 0
      // 085: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._scriptEngine Lnet/rim/device/apps/internal/browser/javascript/JavaScriptInterpreter;
      // 088: invokeinterface net/rim/device/apps/internal/browser/javascript/JavaScriptInterpreter.getStreamOutput ()[B 1
      // 08d: astore 2
      // 08e: goto 2e4
      // 091: astore 6
      // 093: goto 2e4
      // 096: aload 0
      // 097: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 09a: ifnonnull 0a0
      // 09d: goto 2e4
      // 0a0: aload 0
      // 0a1: bipush 1
      // 0a2: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.loadingStatusEventOccurred (I)V
      // 0a5: aload 0
      // 0a6: bipush 1
      // 0a7: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.getRequestHeaders (Z)Lnet/rim/device/api/io/http/HttpHeaders;
      // 0aa: astore 5
      // 0ac: aload 1
      // 0ad: getfield net/rim/device/apps/internal/browser/html/JavaScriptItem._charset Ljava/lang/String;
      // 0b0: astore 6
      // 0b2: aload 6
      // 0b4: ifnonnull 0bd
      // 0b7: aload 0
      // 0b8: getfield net/rim/device/apps/internal/browser/page/Renderer._postEncoding Ljava/lang/String;
      // 0bb: astore 6
      // 0bd: aload 6
      // 0bf: ifnull 0cc
      // 0c2: aload 5
      // 0c4: ldc_w "x-rim-default-charset"
      // 0c7: aload 6
      // 0c9: invokevirtual net/rim/device/api/io/http/HttpHeaders.setProperty (Ljava/lang/String;Ljava/lang/String;)V
      // 0cc: aload 0
      // 0cd: aload 0
      // 0ce: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentPriorityCount I
      // 0d1: bipush 1
      // 0d2: iadd
      // 0d3: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentPriorityCount I
      // 0d6: getstatic net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.INLINE_DATA_304 Ljava/lang/String;
      // 0d9: aload 3
      // 0da: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 0dd: istore 7
      // 0df: new java/lang/Object
      // 0e2: dup
      // 0e3: aload 4
      // 0e5: aload 5
      // 0e7: aload 0
      // 0e8: getfield net/rim/device/apps/internal/browser/page/Renderer._flags I
      // 0eb: sipush 255
      // 0ee: iand
      // 0ef: iload 7
      // 0f1: ifeq 0fa
      // 0f4: ldc_w 32768
      // 0f7: goto 0fb
      // 0fa: bipush 0
      // 0fb: ior
      // 0fc: invokespecial net/rim/device/api/browser/field/RequestedResource.<init> (Ljava/lang/String;Lnet/rim/device/api/io/http/HttpHeaders;I)V
      // 0ff: astore 8
      // 101: aload 0
      // 102: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 105: aload 8
      // 107: aconst_null
      // 108: invokeinterface net/rim/device/api/browser/field/RenderingApplication.getResource (Lnet/rim/device/api/browser/field/RequestedResource;Lnet/rim/device/api/browser/field/BrowserContent;)Ljavax/microedition/io/HttpConnection; 3
      // 10d: astore 9
      // 10f: aconst_null
      // 110: astore 10
      // 112: aload 9
      // 114: ifnonnull 11a
      // 117: goto 2a0
      // 11a: aload 9
      // 11c: invokeinterface javax/microedition/io/HttpConnection.getResponseCode ()I 1
      // 121: sipush 200
      // 124: if_icmpeq 12a
      // 127: goto 2a0
      // 12a: aload 9
      // 12c: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 131: astore 10
      // 133: aload 10
      // 135: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.readBytesFromInputStream (Ljava/io/InputStream;)[B
      // 138: astore 11
      // 13a: aload 11
      // 13c: ifnonnull 142
      // 13f: goto 204
      // 142: aload 9
      // 144: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getContentType (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 147: astore 12
      // 149: aload 12
      // 14b: ldc_w "application/vnd.rim.jscriptc"
      // 14e: ldc_w 1701707776
      // 151: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 154: ifeq 17a
      // 157: aload 0
      // 158: bipush 2
      // 15a: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.loadingStatusEventOccurred (I)V
      // 15d: aload 0
      // 15e: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._scriptEngine Lnet/rim/device/apps/internal/browser/javascript/JavaScriptInterpreter;
      // 161: aload 11
      // 163: bipush 0
      // 164: aload 11
      // 166: arraylength
      // 167: invokeinterface net/rim/device/apps/internal/browser/javascript/JavaScriptInterpreter.executeCompiledScript ([BII)Z 4
      // 16c: pop
      // 16d: aload 0
      // 16e: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._scriptEngine Lnet/rim/device/apps/internal/browser/javascript/JavaScriptInterpreter;
      // 171: invokeinterface net/rim/device/apps/internal/browser/javascript/JavaScriptInterpreter.getStreamOutput ()[B 1
      // 176: astore 2
      // 177: goto 204
      // 17a: aload 0
      // 17b: bipush 2
      // 17d: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.loadingStatusEventOccurred (I)V
      // 180: aload 0
      // 181: aload 11
      // 183: aload 9
      // 185: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getCharacterEncoding (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 188: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.getTextDataWithEncoding ([BLjava/lang/String;)Lnet/rim/device/apps/internal/browser/html/InlineDataRefHolder;
      // 18b: astore 13
      // 18d: aload 13
      // 18f: getfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._encoding Ljava/lang/String;
      // 192: ifnonnull 1a4
      // 195: aload 6
      // 197: ifnull 1a4
      // 19a: aload 0
      // 19b: aload 11
      // 19d: aload 6
      // 19f: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.getTextDataWithEncoding ([BLjava/lang/String;)Lnet/rim/device/apps/internal/browser/html/InlineDataRefHolder;
      // 1a2: astore 13
      // 1a4: aconst_null
      // 1a5: astore 14
      // 1a7: aload 13
      // 1a9: getfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._encoding Ljava/lang/String;
      // 1ac: ifnull 1d1
      // 1af: new java/lang/Object
      // 1b2: dup
      // 1b3: aload 13
      // 1b5: getfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._data [B
      // 1b8: aload 13
      // 1ba: getfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._offset I
      // 1bd: aload 13
      // 1bf: getfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._length I
      // 1c2: aload 13
      // 1c4: getfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._encoding Ljava/lang/String;
      // 1c7: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 1ca: astore 14
      // 1cc: goto 1d1
      // 1cf: astore 15
      // 1d1: aload 14
      // 1d3: ifnonnull 1e5
      // 1d6: new java/lang/Object
      // 1d9: dup
      // 1da: aload 11
      // 1dc: bipush 0
      // 1dd: aload 11
      // 1df: arraylength
      // 1e0: invokespecial java/lang/String.<init> ([BII)V
      // 1e3: astore 14
      // 1e5: aload 0
      // 1e6: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._scriptEngine Lnet/rim/device/apps/internal/browser/javascript/JavaScriptInterpreter;
      // 1e9: aload 14
      // 1eb: aload 1
      // 1ec: getfield net/rim/device/apps/internal/browser/html/JavaScriptItem._language Ljava/lang/String;
      // 1ef: invokeinterface net/rim/device/apps/internal/browser/javascript/JavaScriptInterpreter.executeScript (Ljava/lang/String;Ljava/lang/String;)Z 3
      // 1f4: pop
      // 1f5: aload 0
      // 1f6: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._scriptEngine Lnet/rim/device/apps/internal/browser/javascript/JavaScriptInterpreter;
      // 1f9: invokeinterface net/rim/device/apps/internal/browser/javascript/JavaScriptInterpreter.getStreamOutput ()[B 1
      // 1fe: astore 2
      // 1ff: goto 204
      // 202: astore 13
      // 204: aload 10
      // 206: ifnull 20e
      // 209: aload 10
      // 20b: invokevirtual java/io/InputStream.close ()V
      // 20e: aload 9
      // 210: ifnonnull 216
      // 213: goto 2a0
      // 216: aload 9
      // 218: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 21d: goto 2a0
      // 220: astore 11
      // 222: goto 2a0
      // 225: astore 11
      // 227: aload 9
      // 229: ifnull 2a0
      // 22c: aload 9
      // 22e: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 233: goto 2a0
      // 236: astore 11
      // 238: goto 2a0
      // 23b: astore 16
      // 23d: aload 9
      // 23f: ifnull 24e
      // 242: aload 9
      // 244: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 249: goto 24e
      // 24c: astore 17
      // 24e: aload 16
      // 250: athrow
      // 251: astore 18
      // 253: aload 10
      // 255: ifnull 25d
      // 258: aload 10
      // 25a: invokevirtual java/io/InputStream.close ()V
      // 25d: aload 9
      // 25f: ifnull 29d
      // 262: aload 9
      // 264: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 269: goto 29d
      // 26c: astore 19
      // 26e: goto 29d
      // 271: astore 19
      // 273: aload 9
      // 275: ifnull 29d
      // 278: aload 9
      // 27a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 27f: goto 29d
      // 282: astore 19
      // 284: goto 29d
      // 287: astore 20
      // 289: aload 9
      // 28b: ifnull 29a
      // 28e: aload 9
      // 290: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 295: goto 29a
      // 298: astore 21
      // 29a: aload 20
      // 29c: athrow
      // 29d: aload 18
      // 29f: athrow
      // 2a0: aload 0
      // 2a1: aload 0
      // 2a2: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentPriorityCount I
      // 2a5: bipush 1
      // 2a6: isub
      // 2a7: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentPriorityCount I
      // 2aa: goto 2e4
      // 2ad: aload 1
      // 2ae: getfield net/rim/device/apps/internal/browser/html/JavaScriptItem._scriptRef I
      // 2b1: bipush -1
      // 2b3: if_icmpeq 2e4
      // 2b6: aload 0
      // 2b7: bipush 2
      // 2b9: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.loadingStatusEventOccurred (I)V
      // 2bc: aload 0
      // 2bd: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._scriptEngine Lnet/rim/device/apps/internal/browser/javascript/JavaScriptInterpreter;
      // 2c0: aload 0
      // 2c1: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentContext Lnet/rim/device/apps/internal/browser/html/HTMLRendererContext;
      // 2c4: getfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._richTextStrings [Ljava/lang/String;
      // 2c7: aload 1
      // 2c8: getfield net/rim/device/apps/internal/browser/html/JavaScriptItem._scriptRef I
      // 2cb: aaload
      // 2cc: aload 1
      // 2cd: getfield net/rim/device/apps/internal/browser/html/JavaScriptItem._language Ljava/lang/String;
      // 2d0: invokeinterface net/rim/device/apps/internal/browser/javascript/JavaScriptInterpreter.executeScript (Ljava/lang/String;Ljava/lang/String;)Z 3
      // 2d5: pop
      // 2d6: aload 0
      // 2d7: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._scriptEngine Lnet/rim/device/apps/internal/browser/javascript/JavaScriptInterpreter;
      // 2da: invokeinterface net/rim/device/apps/internal/browser/javascript/JavaScriptInterpreter.getStreamOutput ()[B 1
      // 2df: astore 2
      // 2e0: goto 2e4
      // 2e3: astore 3
      // 2e4: aload 0
      // 2e5: bipush 0
      // 2e6: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.loadingStatusEventOccurred (I)V
      // 2e9: aload 2
      // 2ea: ifnonnull 2f0
      // 2ed: goto 399
      // 2f0: aload 2
      // 2f1: arraylength
      // 2f2: ifgt 2f8
      // 2f5: goto 399
      // 2f8: new java/lang/Object
      // 2fb: dup
      // 2fc: aload 2
      // 2fd: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 300: astore 3
      // 301: bipush 2
      // 303: newarray 8
      // 305: astore 4
      // 307: aload 4
      // 309: bipush 0
      // 30a: bipush 1
      // 30b: bastore
      // 30c: aload 4
      // 30e: bipush 1
      // 30f: aload 0
      // 310: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingOptions Lnet/rim/device/api/browser/field/RenderingOptions;
      // 313: ldc2_w 4550690918222697397
      // 316: bipush 18
      // 318: bipush 1
      // 319: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithBooleanValue (JIZ)Z
      // 31c: ifeq 323
      // 31f: bipush 1
      // 320: goto 324
      // 323: bipush 0
      // 324: bastore
      // 325: ldc_w "text/html"
      // 328: ldc_w "utf-8"
      // 32b: aload 0
      // 32c: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingOptions Lnet/rim/device/api/browser/field/RenderingOptions;
      // 32f: ldc2_w 4550690918222697397
      // 332: bipush 8
      // 334: ldc_w "us-ascii"
      // 337: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithStringValue (JILjava/lang/String;)Ljava/lang/String;
      // 33a: aload 3
      // 33b: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities.STATE_TABLE [I
      // 33e: aload 4
      // 340: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities._T_NAMES [Ljava/lang/String;
      // 343: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities._T_TOKENS [I
      // 346: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities._T_INDICIES [I
      // 349: getstatic net/rim/device/apps/internal/browser/markup/MarkupBinaryConstants._E_NAMES [Ljava/lang/String;
      // 34c: getstatic net/rim/device/apps/internal/browser/markup/MarkupBinaryConstants._E_TOKENS [I
      // 34f: getstatic net/rim/device/apps/internal/browser/markup/MarkupBinaryConstants._E_INDICIES [I
      // 352: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities._A_NAMES [Ljava/lang/String;
      // 355: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities._A_TOKENS [I
      // 358: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities._A_INDICIES [I
      // 35b: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities._AV_NAMES [Ljava/lang/String;
      // 35e: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities._AV_TOKENS [I
      // 361: getstatic net/rim/device/apps/internal/browser/markup/HTMLUtilities._AV_INDICIES [I
      // 364: invokestatic net/rim/device/internal/browser/markup/MarkupInputStream.getConvertedInputStream (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/io/InputStream;[I[B[Ljava/lang/String;[I[I[Ljava/lang/String;[I[I[Ljava/lang/String;[I[I[Ljava/lang/String;[I[I)Lnet/rim/device/internal/browser/markup/MarkupInputStream;
      // 367: astore 5
      // 369: aload 0
      // 36a: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentContext Lnet/rim/device/apps/internal/browser/html/HTMLRendererContext;
      // 36d: astore 6
      // 36f: aload 0
      // 370: new net/rim/device/apps/internal/browser/html/HTMLRendererContext
      // 373: dup
      // 374: aload 5
      // 376: invokevirtual net/rim/device/internal/browser/markup/MarkupInputStream.getPipe ()Lnet/rim/device/internal/browser/util/Pipe;
      // 379: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererContext.<init> (Lnet/rim/device/internal/browser/util/Pipe;)V
      // 37c: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentContext Lnet/rim/device/apps/internal/browser/html/HTMLRendererContext;
      // 37f: aload 0
      // 380: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentContext Lnet/rim/device/apps/internal/browser/html/HTMLRendererContext;
      // 383: new net/rim/device/apps/internal/browser/stack/WAPInputStream
      // 386: dup
      // 387: aload 5
      // 389: invokespecial net/rim/device/apps/internal/browser/stack/WAPInputStream.<init> (Ljava/io/InputStream;)V
      // 38c: putfield net/rim/device/apps/internal/browser/html/HTMLRendererContext._in Lnet/rim/device/apps/internal/browser/stack/WAPInputStream;
      // 38f: aload 0
      // 390: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.parseData ()V
      // 393: aload 0
      // 394: aload 6
      // 396: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentContext Lnet/rim/device/apps/internal/browser/html/HTMLRendererContext;
      // 399: return
      // try (48 -> 65): 66 null
      // try (192 -> 204): 205 null
      // try (147 -> 227): 228 null
      // try (233 -> 238): 239 null
      // try (229 -> 233): 241 null
      // try (242 -> 246): 247 null
      // try (229 -> 233): 249 null
      // try (241 -> 242): 249 null
      // try (250 -> 254): 255 null
      // try (249 -> 250): 249 null
      // try (135 -> 229): 258 null
      // try (263 -> 267): 268 null
      // try (259 -> 263): 270 null
      // try (271 -> 275): 276 null
      // try (259 -> 263): 278 null
      // try (270 -> 271): 278 null
      // try (279 -> 283): 284 null
      // try (278 -> 279): 278 null
      // try (258 -> 259): 258 null
      // try (300 -> 319): 320 null
   }

   private final void handleBackgroundImage(String value, boolean fixed, boolean repeatX, boolean repeatY, int startX, int startY) {
      if (IS_COLOR
         && this._currentContext._allTagsProvided
         && value != null
         && value.length() > 0
         && super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 4, RenderingOptions.USE_BACKGROUND_IMAGES_DEFAULT)) {
         String url = this._browserContent.resolveUrl(value);
         Object cell = this._currentBrowserField.getCurrentCell();
         EncodedImage img = this.findEncodedImageInCache(url, super._flags & 0xFF, null);
         synchronized (this._appEventLock) {
            this._currentBrowserField.setBackgroundStyle(cell, img, fixed, repeatX, repeatY, startX, startY);
         }

         if (img == null) {
            this._browserContent.addSecondaryURL(url, new HTMLSecondaryURLNode(this._currentBrowserField, cell), false);
         }
      }
   }

   private final Border getBorder(int borderMask, int borderTopWidth, int borderRightWidth, int borderBottomWidth, int borderLeftWidth, boolean is3d) {
      return (Border)(is3d
         ? new Object(
            (borderMask & 4) != 0 ? borderTopWidth : 0,
            (borderMask & 2) != 0 ? borderRightWidth : 0,
            (borderMask & 8) != 0 ? borderBottomWidth : 0,
            (borderMask & 1) != 0 ? borderLeftWidth : 0
         )
         : new Object(
            (borderMask & 4) != 0 ? borderTopWidth : 0,
            (borderMask & 2) != 0 ? borderRightWidth : 0,
            (borderMask & 8) != 0 ? borderBottomWidth : 0,
            (borderMask & 1) != 0 ? borderLeftWidth : 0
         ));
   }

   private final Border getBorder(
      int borderMask,
      int borderTopWidth,
      int borderRightWidth,
      int borderBottomWidth,
      int borderLeftWidth,
      int borderTopColor,
      int borderRightColor,
      int borderBottomColor,
      int borderLeftColor
   ) {
      return (Border)(new Object(
         (borderMask & 4) != 0 ? borderTopWidth : 0,
         (borderMask & 2) != 0 ? borderRightWidth : 0,
         (borderMask & 8) != 0 ? borderBottomWidth : 0,
         (borderMask & 1) != 0 ? borderLeftWidth : 0,
         borderTopColor,
         borderRightColor,
         borderBottomColor,
         borderLeftColor
      ));
   }

   private final void loadingStatusEventOccurred(int status) {
      if (super._renderingApplication != null) {
         if (this._loadingStatusEvent == null) {
            this._loadingStatusEvent = new LoadingStatusEvent(status, this._browserContent);
         } else {
            this._loadingStatusEvent.setStatus(status);
         }

         super._renderingApplication.eventOccurred(this._loadingStatusEvent);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void parseStyleSheet(CSSParser parser, Object styleSheet) {
      if (this._processStyleSheets) {
         CSSParser oldParser = this._cssParser;
         boolean var8 = false /* VF: Semaphore variable */;

         label130: {
            try {
               label122:
               try {
                  var8 = true;
                  this._cssParser = parser;
                  if (TimeLogger._loggingEnabled) {
                     TimeLogger.getInstance().startTimer(12, styleSheet.hashCode());
                  }

                  if (styleSheet instanceof Object) {
                     this._cssParser.parseStyleSheet((String)styleSheet);
                     var8 = false;
                  } else if (styleSheet instanceof byte[]) {
                     this._cssParser.parseStyleSheet((byte[])styleSheet);
                     var8 = false;
                  } else {
                     var8 = false;
                  }
                  break label130;
               } catch (Throwable var11) {
                  if (t instanceof Object) {
                     QuincyManager.sendJavaLogworthy("Browser:CSS");
                     var8 = false;
                  } else {
                     if (t instanceof Object) {
                        throw (Object)t;
                     }

                     var8 = false;
                  }
                  break label122;
               }
            } finally {
               if (var8) {
                  if (TimeLogger._loggingEnabled) {
                     TimeLogger.getInstance().stopTimer(12, styleSheet.hashCode());
                  }

                  this._cssParser = oldParser;
               }
            }

            if (TimeLogger._loggingEnabled) {
               TimeLogger.getInstance().stopTimer(12, styleSheet.hashCode());
            }

            this._cssParser = oldParser;
            return;
         }

         if (TimeLogger._loggingEnabled) {
            TimeLogger.getInstance().stopTimer(12, styleSheet.hashCode());
         }

         this._cssParser = oldParser;
      }
   }

   private final void parseTextStyleSheet(String styleSheet, String url) {
      if (this._processStyleSheets) {
         this.parseStyleSheet(new CSSTextParser(this, url), styleSheet);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void parseTextStyleSheet() {
      int stringRef = this._currentContext._currentStringRef;
      if (stringRef != -1) {
         boolean var5 = false /* VF: Semaphore variable */;

         try {
            var5 = true;
            if (this._processStyleSheets) {
               String styleSheet = this._currentContext._richTextStrings[stringRef];
               this.parseTextStyleSheet(styleSheet, null);
               var5 = false;
            } else {
               var5 = false;
            }
         } finally {
            if (var5) {
               this._currentContext._currentStringRef = -1;
            }
         }

         this._currentContext._currentStringRef = -1;
      }
   }

   private final void parseBinaryStyleSheet(byte[] styleSheet, String url) {
      if (this._processStyleSheets) {
         this.parseStyleSheet(new CSSBinaryParser(this, url), styleSheet);
      }
   }

   @Override
   public final BrowserContent processData() {
      boolean embeddedContent = (super._flags & 16) != 0;
      boolean bbBrowserContent = (super._flags & 32) != 0;
      long style = 0;
      style = bbBrowserContent && !embeddedContent ? 281474976710658L : 562949953421313L;
      if (super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 39, false)) {
         style |= 4;
      }

      this._currentBrowserField = new HTMLBrowserField(this._browserContent, this, this._htmlContext, style);
      this._currentBrowserField.setFont(this._textUtilities.getDefaultFont());
      this._currentBrowserField.setWideViewMode(super._renderingOptions.getPropertyWithIntValue(4550690918222697397L, 44, 0) != 0);
      this._currentBrowserField.setMobileViewCursor(super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 47, false));
      this._textUtilities.setTextFlowManager(this._currentBrowserField);
      this._browserContent.setContent(this._currentBrowserField);
      this._currentBrowserField.setBrowserContent(this._browserContent);
      this.setEncoding(null);
      return this._browserContent;
   }

   private final void appendCurrentString(HTMLGenericElement element) {
      HTMLRendererContext context = this._currentContext;
      if (context._currentStringRef != -1) {
         if (context._currentObject != null) {
            this.handleObject();
         }

         if (this._skipElementStackValue > 0 || this._framesetDocument) {
            context._currentStringRef = -1;
         } else if (this._currentSelect != null) {
            this.addPendingOptionLabel();
         } else {
            synchronized (this._appEventLock) {
               String string = context._richTextStrings[context._currentStringRef];
               if (string != null) {
                  this.resetBlockAndBreak();
                  if (context._allTagsProvided) {
                     this._textUtilities.appendText(element, string);
                  } else {
                     this._textUtilities
                        .appendText(
                           element,
                           context._currentStringRef,
                           context._richTextStrings,
                           (byte[])context._richTextProperties.get(context._currentStringRef),
                           context._appendNewLine
                        );
                  }

                  context._currentStringRef = -1;
                  this._numCharsAppended = this._numCharsAppended + string.length();
                  if (this._numCharsAppended >= this._layoutCharAppendLimit) {
                     this.activateLayout();
                  }
               }
            }
         }
      }
   }

   private final void appendField(Field field, boolean inline, boolean form) {
      if (field != null) {
         synchronized (this._appEventLock) {
            this._fieldsPending = true;
            this.resetBlockAndBreak();
            if (form && field.isFocusable()) {
               this.endAnchorIfOpen();
            }

            if (inline) {
               this._currentBrowserField.appendInlineField(field);
            } else {
               this._currentBrowserField.appendOutOfLineField(field);
            }

            int prefHeight = field.getPreferredHeight();
            if (prefHeight == 0) {
               prefHeight = 1;
            }

            this._guessedHeight += prefHeight;
            if (this._guessedHeight >= this._guessedHeightLimit || field instanceof FrameManager) {
               this.activateLayout();
            }
         }
      }
   }

   private final int appendAltField(Field field, String text, Font font, long cookieId, String link) {
      if (field == null) {
         return -1;
      }

      int tag = -1;
      synchronized (this._appEventLock) {
         this._fieldsPending = true;
         boolean focusRegionOpen = false;
         if (this._currentContext._allTagsProvided && link != null) {
            this._textUtilities.startFocusRegionIfRequired();
         } else if (field.isFocusable() && cookieId != -1 && link != null) {
            this._currentBrowserField.startFocusRegion(link, cookieId);
            focusRegionOpen = true;
         }

         tag = this._currentBrowserField.appendOutOfLineFieldAlt(field, text, 0, text.length());
         if (focusRegionOpen) {
            this._currentBrowserField.endFocusRegion();
         }

         this._numCharsAppended = this._numCharsAppended + text.length();
         if (this._numCharsAppended >= this._layoutCharAppendLimit) {
            this.activateLayout();
         }

         return tag;
      }
   }

   private final void secondaryImagesLoaded() {
      Pipe pipe = this._currentContext._pipe;
      if (pipe == null) {
         pipe = this._currentContext._in.getPipe();
      }

      if (pipe == null || !pipe.isClosed()) {
         synchronized (this._appEventLock) {
            this.activateLayout();
         }
      }
   }

   private final void activateLayoutInParent() {
      if ((super._flags & 16) != 0) {
         for (Manager manager = this._currentBrowserField.getManager(); manager != null; manager = manager.getManager()) {
            if (manager instanceof TextFlowManager) {
               TextFlowManager parentTextFlowManager = (TextFlowManager)manager;
               parentTextFlowManager.setLayoutActive(true);
               parentTextFlowManager.setLayoutActive(false);
               return;
            }
         }
      }
   }

   private final HttpHeaders getRequestHeaders(boolean addPriority) {
      HttpHeaders requestHeaders = (HttpHeaders)(new Object());
      RenderingUtilities.setReferrer(requestHeaders, this._url);
      if (addPriority) {
         requestHeaders.setProperty("x-rim-request-priority", Integer.toString(this._currentPriorityCount));
      }

      return requestHeaders;
   }

   private final void activateLayout() {
      this._currentBrowserField.setLayoutActive(true);
      this.activateLayoutInParent();
      this._currentBrowserField.setLayoutActive(false);
      this._numCharsAppended = 0;
      this._guessedHeight = 0;
      this._fieldsPending = false;
      this._guessedHeightLimit <<= 1;
      this._layoutCharAppendLimit <<= 1;
   }

   private final void property(int callDepth, int name, int[] value, int valueLength, boolean important) {
      int flags;
      int propertyValue;
      flags = 0;
      propertyValue = 0;
      int index = 0;
      callDepth++;
      Asserts.productionStateAssert(callDepth <= 2);
      int[] tempValue = (int[])this._cssTempArray[callDepth];
      label720:
      switch (name) {
         case 2:
            tempValue[0] = true;
            tempValue[1] = 161;
            this.property(callDepth, 4, tempValue, 2, important);
            tempValue[1] = 97;
            this.property(callDepth, 5, tempValue, 2, important);
            tempValue[1] = 113;
            this.property(callDepth, 7, tempValue, 2, important);
            tempValue[1] = 124;
            this.property(callDepth, 3, tempValue, 2, important);
            boolean backgroundColor = false;
            boolean backgroundImage = false;
            boolean backgroundRepeat = false;
            boolean backgroundAttachment = false;
            boolean backgroundPosition = false;
            index = 0;

            while (index < valueLength) {
               switch (value[index] & 15) {
                  case 0:
                  case 2:
                     index = valueLength;
                     break;
                  case 1:
                  default:
                     switch (value[index + 1]) {
                        case 17:
                        case 20:
                        case 69:
                        case 117:
                        case 160:
                           boolean twoValues = false;
                           if (index + 3 < valueLength) {
                              int nextType = value[index + 2] & 15;
                              if (nextType == 1) {
                                 switch (value[index + 3]) {
                                    case 17:
                                    case 20:
                                    case 69:
                                    case 117:
                                    case 160:
                                       twoValues = true;
                                 }
                              } else if (nextType == 6 || nextType == 7) {
                                 twoValues = true;
                              }
                           }

                           if (twoValues) {
                              System.arraycopy(value, index, tempValue, 0, 4);
                              this.property(callDepth, 6, tempValue, 4, important);
                              index += 4;
                           } else {
                              tempValue[0] = value[index++];
                              tempValue[1] = value[index++];
                              this.property(callDepth, 6, tempValue, 2, important);
                           }

                           backgroundPosition = true;
                           continue;
                        case 48:
                        case 124:
                           tempValue[0] = value[index++];
                           tempValue[1] = value[index++];
                           this.property(callDepth, 3, tempValue, 2, important);
                           backgroundAttachment = true;
                           continue;
                        case 58:
                           if (!backgroundColor) {
                              this.clearStyle(this._currentStyleIndex, 4);
                           }

                           if (!backgroundImage) {
                              this.clearStyle(this._currentStyleIndex, 5);
                           }

                           if (!backgroundRepeat) {
                              this.clearStyle(this._currentStyleIndex, 7);
                           }

                           if (!backgroundAttachment) {
                              this.clearStyle(this._currentStyleIndex, 3);
                           }

                           if (!backgroundPosition) {
                              this.clearStyle(this._currentStyleIndex, 6);
                           }

                           index += 2;
                           continue;
                        case 96:
                        case 113:
                        case 114:
                        case 115:
                           tempValue[0] = value[index++];
                           tempValue[1] = value[index++];
                           this.property(callDepth, 7, tempValue, 2, important);
                           backgroundRepeat = true;
                           continue;
                        case 97:
                           tempValue[0] = value[index++];
                           tempValue[1] = value[index++];
                           this.property(callDepth, 5, tempValue, 2, important);
                           backgroundImage = true;
                           continue;
                        case 161:
                           tempValue[0] = value[index++];
                           tempValue[1] = value[index++];
                           this.property(callDepth, 4, tempValue, 2, important);
                           backgroundColor = true;
                           continue;
                        default:
                           index += 2;
                           continue;
                     }
                  case 3:
                     tempValue[0] = value[index++];
                     tempValue[1] = value[index++];
                     this.property(callDepth, 5, tempValue, 2, important);
                     backgroundImage = true;
                     break;
                  case 4:
                     int remainingLength = valueLength - index;
                     if (remainingLength >= 11) {
                        System.arraycopy(value, index, tempValue, 0, 11);
                        this.property(callDepth, 4, tempValue, 11, important);
                        backgroundColor = true;
                        index += 11;
                     } else {
                        index += remainingLength;
                     }
                     break;
                  case 5:
                     tempValue[0] = value[index++];
                     tempValue[1] = value[index++];
                     this.property(callDepth, 4, tempValue, 2, important);
                     backgroundColor = true;
                     break;
                  case 6:
                  case 7:
                     boolean twoValues = false;
                     if (index + 3 < valueLength) {
                        int nextType = value[index + 2] & 15;
                        if (nextType == 1) {
                           switch (value[index + 3]) {
                              case 17:
                              case 20:
                              case 69:
                              case 117:
                              case 160:
                                 twoValues = true;
                           }
                        } else if (nextType == 6 || nextType == 7) {
                           twoValues = true;
                        }
                     }

                     if (twoValues) {
                        System.arraycopy(value, index, tempValue, 0, 4);
                        this.property(callDepth, 6, tempValue, 4, important);
                        index += 4;
                     } else {
                        tempValue[0] = value[index++];
                        tempValue[1] = value[index++];
                        this.property(callDepth, 6, tempValue, 2, important);
                     }

                     backgroundPosition = true;
               }
            }
            break;
         case 3:
            if ((value[0] & 15) == 1) {
               switch (value[1]) {
                  case 48:
                  case 124:
                     flags = 1;
                     propertyValue = value[1];
               }
            }
            break;
         case 4:
         case 10:
         case 16:
         case 20:
         case 26:
         case 34:
            int var77 = value[0] & 15;
            if (var77 != 4) {
               if (var77 == 5) {
                  flags = 5;
                  propertyValue = value[1];
               }
            } else {
               int startIndex = this._cssParser.getStringStartIndex(value[1]);
               int length = this._cssParser.getStringEndIndex(value[1]) - startIndex;
               if (length == 3
                  && StringUtilities.regionMatches(this._cssParser.getSource(), true, startIndex, "rgb", 0, length, 1701707776)
                  && valueLength == 11
                  && (value[2] & 15) == 6
                  && (value[5] & 15) == 6
                  && (value[8] & 15) == 6) {
                  int red;
                  int green;
                  int blue;
                  if ((value[2] & 16) == 0 && (value[5] & 16) == 0 && (value[8] & 16) == 0) {
                     red = MathUtilities.clamp(0, value[3], 255);
                     green = MathUtilities.clamp(0, value[6], 255);
                     blue = MathUtilities.clamp(0, value[9], 255);
                  } else {
                     red = MathUtilities.clamp(0, value[3], 100) * 255 / 100;
                     green = MathUtilities.clamp(0, value[6], 100) * 255 / 100;
                     blue = MathUtilities.clamp(0, value[9], 100) * 255 / 100;
                  }

                  flags = 5;
                  propertyValue = red << 16 | green << 8 | blue;
               }
            }
            break;
         case 5:
            int typex = value[0] & 15;
            if (typex == 1) {
               if (value[1] == 97) {
                  flags = 1;
                  propertyValue = 97;
               }
            } else if (typex == 3) {
               flags = 3;
               propertyValue = this.addStyleObject(this._cssParser.getURL(value[1]));
            }
            break;
         case 6:
            int[] myValues = new int[4];

            for (int pos = 0; pos < 4 && pos < valueLength; pos += 2) {
               int myType = value[pos] & 15;
               if (myType == 1) {
                  switch (value[pos + 1]) {
                     case 17:
                        myValues[2] = 22;
                        myValues[3] = 100;
                        break;
                     case 69:
                        myValues[0] = 22;
                        myValues[1] = 0;
                        break;
                     case 117:
                        myValues[0] = 22;
                        myValues[1] = 100;
                        break;
                     case 160:
                        myValues[2] = 22;
                        myValues[3] = 0;
                  }
               } else if (myType == 6 || myType == 7) {
                  myValues[pos] = value[pos];
                  myValues[pos + 1] = value[pos + 1];
               }
            }

            if (myValues[0] == 0) {
               myValues[0] = 22;
               myValues[1] = 50;
            }

            if (myValues[2] == 0) {
               myValues[2] = 22;
               myValues[3] = 50;
            }

            flags = 1;
            propertyValue = this.addStyleObject(myValues);
            break;
         case 7:
            if ((value[0] & 15) == 1) {
               switch (value[1]) {
                  case 96:
                  case 113:
                  case 114:
                  case 115:
                     flags = 1;
                     propertyValue = value[1];
               }
            }
            break;
         case 8:
         case 9:
         case 15:
         case 19:
         case 25:
            int borderWidthProperty = 0;
            int borderStyleProperty = 0;
            int borderColorProperty = 0;
            switch (name) {
               case 8:
                  borderWidthProperty = 29;
                  borderStyleProperty = 24;
                  borderColorProperty = 14;
                  break;
               case 9:
                  borderWidthProperty = 12;
                  borderStyleProperty = 11;
                  borderColorProperty = 10;
                  break;
               case 15:
                  borderWidthProperty = 18;
                  borderStyleProperty = 17;
                  borderColorProperty = 16;
                  break;
               case 19:
                  borderWidthProperty = 22;
                  borderStyleProperty = 21;
                  borderColorProperty = 20;
                  break;
               case 25:
                  borderWidthProperty = 28;
                  borderStyleProperty = 27;
                  borderColorProperty = 26;
            }

            tempValue[0] = true;
            tempValue[1] = 85;
            this.property(callDepth, borderWidthProperty, tempValue, 2, important);
            tempValue[1] = 97;
            this.property(callDepth, borderStyleProperty, tempValue, 2, important);
            boolean borderWidth = false;
            boolean borderStyle = false;
            boolean borderColor = false;
            index = 0;

            while (index < valueLength) {
               switch (value[index] & 15) {
                  case 0:
                  case 2:
                  case 3:
                     index = valueLength;
                     break;
                  case 1:
                  default:
                     switch (value[index + 1]) {
                        case 31:
                        case 37:
                        case 38:
                        case 50:
                        case 52:
                        case 62:
                        case 97:
                        case 104:
                        case 116:
                        case 138:
                           tempValue[0] = value[index++];
                           tempValue[1] = value[index++];
                           this.property(callDepth, borderStyleProperty, tempValue, 2, important);
                           borderStyle = true;
                           continue;
                        case 58:
                           if (!borderWidth) {
                              this.clearStyle(this._currentStyleIndex, borderWidthProperty);
                           }

                           if (!borderStyle) {
                              this.clearStyle(this._currentStyleIndex, borderStyleProperty);
                           }

                           if (!borderColor) {
                              this.clearStyle(this._currentStyleIndex, borderColorProperty);
                           }

                           index += 2;
                           continue;
                        case 85:
                        case 158:
                        case 159:
                           tempValue[0] = value[index++];
                           tempValue[1] = value[index++];
                           this.property(callDepth, borderWidthProperty, tempValue, 2, important);
                           borderWidth = true;
                           continue;
                        case 161:
                           tempValue[0] = value[index++];
                           tempValue[1] = value[index++];
                           this.property(callDepth, borderColorProperty, tempValue, 2, important);
                           borderColor = true;
                           continue;
                        default:
                           index += 2;
                           continue;
                     }
                  case 4:
                     int remainingLength = valueLength - index;
                     if (remainingLength >= 11) {
                        int[] borderColorValue = Arrays.copy(value, index, 11);
                        this.property(callDepth, borderColorProperty, borderColorValue, 11, important);
                        borderColor = true;
                        index += 11;
                     } else {
                        index += remainingLength;
                     }
                     break;
                  case 5:
                     tempValue[0] = value[index++];
                     tempValue[1] = value[index++];
                     this.property(callDepth, borderColorProperty, tempValue, 2, important);
                     borderColor = true;
                     break;
                  case 6:
                  case 7:
                     tempValue[0] = value[index++];
                     tempValue[1] = value[index++];
                     this.property(callDepth, borderWidthProperty, tempValue, 2, important);
                     borderWidth = true;
               }
            }
            break;
         case 11:
         case 17:
         case 21:
         case 27:
            if ((value[0] & 15) == 1) {
               switch (value[1]) {
                  case 31:
                  case 37:
                  case 38:
                  case 50:
                  case 52:
                  case 62:
                  case 97:
                  case 104:
                  case 116:
                  case 138:
                     flags = 1;
                     propertyValue = value[1];
               }
            }
            break;
         case 12:
         case 18:
         case 22:
         case 28:
            int var75 = value[0] & 15;
            if (var75 == 1) {
               switch (value[1]) {
                  case 85:
                     flags = 70;
                     propertyValue = 2;
                     break label720;
                  case 158:
                     flags = 70;
                     propertyValue = 3;
                     break label720;
                  case 159:
                     flags = 70;
                     propertyValue = 1;
               }
            } else if (var75 == 6 || var75 == 7) {
               switch (value[0] & 496) {
                  case 0:
                     if (value[1] == 0) {
                        flags = value[0];
                        propertyValue = value[1];
                     }
                     break label720;
                  case 16:
                  case 32:
                  case 48:
                  case 64:
                  case 80:
                  case 96:
                  case 112:
                  case 128:
                  case 144:
                     if (value[1] >= 0) {
                        flags = value[0];
                        propertyValue = value[1];
                     }
               }
            }
            break;
         case 14:
         case 24:
         case 29:
         case 76:
            int borderTopProperty = 0;
            int borderRightProperty = 0;
            int borderBottomProperty = 0;
            int borderLeftProperty = 0;
            switch (name) {
               case 14:
                  borderTopProperty = 26;
                  borderRightProperty = 20;
                  borderBottomProperty = 10;
                  borderLeftProperty = 16;
                  break;
               case 24:
                  borderTopProperty = 27;
                  borderRightProperty = 21;
                  borderBottomProperty = 11;
                  borderLeftProperty = 17;
                  break;
               case 29:
                  borderTopProperty = 28;
                  borderRightProperty = 22;
                  borderBottomProperty = 12;
                  borderLeftProperty = 18;
                  break;
               case 76:
                  borderTopProperty = 80;
                  borderRightProperty = 79;
                  borderBottomProperty = 77;
                  borderLeftProperty = 78;
            }

            switch (valueLength) {
               case 2:
                  tempValue[0] = value[0];
                  tempValue[1] = value[1];
                  this.property(callDepth, borderTopProperty, tempValue, 2, important);
                  this.property(callDepth, borderRightProperty, tempValue, 2, important);
                  this.property(callDepth, borderBottomProperty, tempValue, 2, important);
                  this.property(callDepth, borderLeftProperty, tempValue, 2, important);
                  break label720;
               case 4:
                  tempValue[0] = value[0];
                  tempValue[1] = value[1];
                  this.property(callDepth, borderTopProperty, tempValue, 2, important);
                  this.property(callDepth, borderBottomProperty, tempValue, 2, important);
                  tempValue[0] = value[2];
                  tempValue[1] = value[3];
                  this.property(callDepth, borderRightProperty, tempValue, 2, important);
                  this.property(callDepth, borderLeftProperty, tempValue, 2, important);
                  break label720;
               case 6:
                  tempValue[0] = value[0];
                  tempValue[1] = value[1];
                  this.property(callDepth, borderTopProperty, tempValue, 2, important);
                  tempValue[0] = value[2];
                  tempValue[1] = value[3];
                  this.property(callDepth, borderRightProperty, tempValue, 2, important);
                  this.property(callDepth, borderLeftProperty, tempValue, 2, important);
                  tempValue[0] = value[4];
                  tempValue[1] = value[5];
                  this.property(callDepth, borderBottomProperty, tempValue, 2, important);
                  break label720;
               case 8:
                  tempValue[0] = value[0];
                  tempValue[1] = value[1];
                  this.property(callDepth, borderTopProperty, tempValue, 2, important);
                  tempValue[0] = value[2];
                  tempValue[1] = value[3];
                  this.property(callDepth, borderRightProperty, tempValue, 2, important);
                  tempValue[0] = value[4];
                  tempValue[1] = value[5];
                  this.property(callDepth, borderBottomProperty, tempValue, 2, important);
                  tempValue[0] = value[6];
                  tempValue[1] = value[7];
                  this.property(callDepth, borderLeftProperty, tempValue, 2, important);
               default:
                  break label720;
            }
         case 42:
            if ((value[0] & 15) == 1) {
               switch (value[1]) {
                  case 83:
                     flags = 1;
                     propertyValue = 0;
                     break;
                  case 120:
                     flags = 1;
                     propertyValue = 8;
               }
            }
            break;
         case 43:
            if ((value[0] & 15) == 1) {
               switch (value[1]) {
                  case 13:
                  case 59:
                  case 97:
                  case 180:
                     flags = 1;
                     propertyValue = value[1];
               }
            }
            break;
         case 46:
            if ((value[0] & 15) == 1) {
               switch (value[1]) {
                  case 69:
                  case 97:
                  case 117:
                     flags = 1;
                     propertyValue = value[1];
               }
            }
            break;
         case 47:
            tempValue[0] = true;
            tempValue[1] = 98;
            this.property(callDepth, 50, tempValue, 2, important);
            this.property(callDepth, 51, tempValue, 2, important);
            this.property(callDepth, 52, tempValue, 2, important);
            this.property(callDepth, 56, tempValue, 2, important);
            tempValue[1] = 85;
            this.property(callDepth, 49, tempValue, 2, important);
            tempValue[1] = 58;
            this.property(callDepth, 48, tempValue, 2, important);
            int state = 47;
            boolean fontStyle = false;
            boolean fontVariant = false;
            boolean fontWeight = false;
            index = 0;

            while (state != -1 && index < valueLength) {
               switch (state) {
                  case 47:
                     int var74 = value[index] & 15;
                     if (var74 != 1) {
                        if (var74 != 6 && var74 != 7) {
                           state = 49;
                        } else if ((value[index] & 496) == 0) {
                           tempValue[0] = value[index++];
                           tempValue[1] = value[index++];
                           this.property(callDepth, 52, tempValue, 2, important);
                           fontWeight = true;
                        } else {
                           state = 49;
                        }
                     } else {
                        switch (value[index + 1]) {
                           case 14:
                           case 15:
                           case 73:
                              tempValue[0] = value[index++];
                              tempValue[1] = value[index++];
                              this.property(callDepth, 52, tempValue, 2, important);
                              fontWeight = true;
                              break;
                           case 58:
                              if (!fontStyle) {
                                 this.clearStyle(this._currentStyleIndex, 50);
                              }

                              if (!fontVariant) {
                                 this.clearStyle(this._currentStyleIndex, 51);
                              }

                              if (!fontWeight) {
                                 this.clearStyle(this._currentStyleIndex, 52);
                              }

                              index += 2;
                              break;
                           case 65:
                           case 101:
                              tempValue[0] = value[index++];
                              tempValue[1] = value[index++];
                              this.property(callDepth, 50, tempValue, 2, important);
                              fontStyle = true;
                              break;
                           case 98:
                              tempValue[0] = value[index++];
                              tempValue[1] = value[index++];
                              if (!fontStyle) {
                                 this.property(callDepth, 50, tempValue, 2, important);
                              }

                              if (!fontVariant) {
                                 this.property(callDepth, 51, tempValue, 2, important);
                              }

                              if (!fontWeight) {
                                 this.property(callDepth, 52, tempValue, 2, important);
                              }
                              break;
                           case 134:
                              tempValue[0] = value[index++];
                              tempValue[1] = value[index++];
                              this.property(callDepth, 51, tempValue, 2, important);
                              fontVariant = true;
                              break;
                           default:
                              state = 49;
                        }
                     }
                     break;
                  case 48:
                     int fontFamilyLength = valueLength - index;
                     if (fontFamilyLength > 0) {
                        int[] fontFamilyValue = Arrays.copy(value, index, fontFamilyLength);
                        this.property(callDepth, 48, fontFamilyValue, fontFamilyLength, important);
                        index += fontFamilyLength;
                     }

                     state = -1;
                     break;
                  case 49:
                     int var73 = value[index] & 15;
                     if (var73 == 1) {
                        switch (value[index + 1]) {
                           case 58:
                              this.clearStyle(this._currentStyleIndex, 49);
                              index += 2;
                              break;
                           case 67:
                           case 68:
                           case 85:
                           case 133:
                           case 136:
                           case 172:
                           case 176:
                           case 178:
                           case 179:
                              tempValue[0] = value[index++];
                              tempValue[1] = value[index++];
                              this.property(callDepth, 49, tempValue, 2, important);
                        }
                     } else if (var73 == 6 || var73 == 7) {
                        tempValue[0] = value[index++];
                        tempValue[1] = value[index++];
                        this.property(callDepth, 49, tempValue, 2, important);
                     }

                     state = 56;
                     break;
                  case 56:
                     if (value[index] == 48 && index + 1 < valueLength) {
                        int var72 = value[++index] & 15;
                        if (var72 == 1) {
                           switch (value[index + 1]) {
                              case 58:
                                 this.clearStyle(this._currentStyleIndex, 56);
                                 index += 2;
                                 break;
                              case 98:
                                 tempValue[0] = value[index++];
                                 tempValue[1] = value[index++];
                                 this.property(callDepth, 56, tempValue, 2, important);
                           }
                        } else if (var72 == 6 || var72 == 7) {
                           tempValue[0] = value[index++];
                           tempValue[1] = value[index++];
                           this.property(callDepth, 56, tempValue, 2, important);
                        }
                     }

                     state = 48;
               }
            }
            break;
         case 48:
            String face = null;

            for (int i = 0; i < valueLength && face == null; i++) {
               if (value[i] != 46) {
                  if ((value[i] & 15) == 1) {
                     face = FontCache.getFontMapping(value[++i]);
                  } else {
                     if ((value[i] & 15) != 2) {
                        break;
                     }

                     face = FontCache.getFontMapping(this._cssParser.getString(value[++i]));
                  }
               }
            }

            if (face != null) {
               FontFamily fontFamily = FontCache.getInstance().getFontFamily(this._textUtilities.getDefaultFont(), null, face);
               if (fontFamily != null) {
                  String fontName = fontFamily.getName();
                  flags = 1;
                  propertyValue = this.addStyleObject(fontName);
               }
            }
            break;
         case 49:
            int typexx = value[0] & 15;
            if (typexx == 1) {
               switch (value[1]) {
                  case 67:
                     flags = 1;
                     propertyValue = 4;
                     break label720;
                  case 68:
                     flags = 1;
                     propertyValue = 9;
                     break label720;
                  case 85:
                     flags = 1;
                     propertyValue = 3;
                     break label720;
                  case 133:
                     flags = 1;
                     propertyValue = 2;
                     break label720;
                  case 136:
                     flags = 1;
                     propertyValue = 8;
                     break label720;
                  case 172:
                     flags = 1;
                     propertyValue = 5;
                     break label720;
                  case 176:
                     flags = 1;
                     propertyValue = 1;
                     break label720;
                  case 178:
                     flags = 1;
                     propertyValue = 6;
                     break label720;
                  case 179:
                     flags = 1;
                     propertyValue = 0;
               }
            } else if (typexx == 6 || typexx == 7) {
               switch (value[0] & 496) {
                  case 0:
                     if (value[1] == 0) {
                        flags = value[0];
                        propertyValue = value[1];
                     }
                     break label720;
                  case 16:
                  case 32:
                  case 48:
                  case 64:
                  case 80:
                  case 96:
                  case 112:
                  case 128:
                  case 144:
                     if (value[1] >= 0) {
                        flags = value[0];
                        propertyValue = value[1];
                     }
               }
            }
            break;
         case 50:
            if ((value[0] & 15) == 1) {
               switch (value[1]) {
                  case 65:
                     flags = 1;
                     propertyValue = 1;
                     break;
                  case 98:
                     flags = 1;
                     propertyValue = 0;
                     break;
                  case 101:
                     flags = 1;
                     propertyValue = 2;
               }
            }
            break;
         case 51:
            if ((value[0] & 15) == 1) {
               switch (value[1]) {
                  case 98:
                  case 134:
                     flags = 1;
                     propertyValue = value[1];
               }
            }
            break;
         case 52:
            int var70 = value[0] & 15;
            if (var70 == 1) {
               switch (value[1]) {
                  case 14:
                     flags = 6;
                     propertyValue = 700;
                     break label720;
                  case 15:
                     flags = 1;
                     propertyValue = 1;
                     break label720;
                  case 73:
                     flags = 1;
                     propertyValue = 2;
                     break label720;
                  case 98:
                     flags = 6;
                     propertyValue = 400;
               }
            } else if (var70 == 6) {
               int weight = value[1];
               if (weight >= 100 && weight <= 900 && weight % 100 == 0) {
                  flags = 6;
                  propertyValue = weight;
               }
            }
            break;
         case 53:
         case 68:
         case 77:
         case 78:
         case 79:
         case 80:
         case 113:
            int var69 = value[0] & 15;
            if (var69 == 6 || var69 == 7) {
               switch (value[0] & 496) {
                  case 0:
                     if (value[1] == 0) {
                        flags = value[0];
                        propertyValue = value[1];
                     }
                     break;
                  case 16:
                  case 32:
                  case 48:
                  case 64:
                  case 80:
                  case 96:
                  case 112:
                  case 128:
                  case 144:
                     if (value[1] >= 0) {
                        flags = value[0];
                        propertyValue = value[1];
                     }
               }
            }
            break;
         case 57:
            tempValue[0] = true;
            tempValue[1] = 34;
            this.property(callDepth, 60, tempValue, 2, important);
            tempValue[1] = 97;
            this.property(callDepth, 58, tempValue, 2, important);
            tempValue[1] = 105;
            this.property(callDepth, 59, tempValue, 2, important);
            boolean listStyleType = false;
            boolean listStyleImage = false;
            boolean listStylePosition = false;
            index = 0;

            while (index < valueLength) {
               switch (value[index] & 15) {
                  case 0:
                     index = valueLength;
                     break;
                  case 1:
                  default:
                     switch (value[index + 1]) {
                        case 5:
                        case 24:
                        case 32:
                        case 33:
                        case 36:
                        case 49:
                        case 79:
                        case 80:
                        case 81:
                        case 140:
                        case 164:
                        case 165:
                        case 181:
                        case 182:
                           tempValue[0] = value[index++];
                           tempValue[1] = value[index++];
                           this.property(callDepth, 60, tempValue, 2, important);
                           listStyleType = true;
                           continue;
                        case 58:
                           if (!listStyleType) {
                              this.clearStyle(this._currentStyleIndex, 60);
                           }

                           if (!listStyleImage) {
                              this.clearStyle(this._currentStyleIndex, 58);
                           }

                           if (!listStylePosition) {
                              this.clearStyle(this._currentStyleIndex, 59);
                           }

                           index += 2;
                           continue;
                        case 63:
                        case 105:
                           tempValue[0] = value[index++];
                           tempValue[1] = value[index++];
                           this.property(callDepth, 59, tempValue, 2, important);
                           listStylePosition = true;
                           continue;
                        case 97:
                           tempValue[0] = value[index++];
                           tempValue[1] = value[index++];
                           if (!listStyleType) {
                              this.property(callDepth, 60, tempValue, 2, important);
                              listStyleType = true;
                           }

                           if (!listStyleImage) {
                              this.property(callDepth, 58, tempValue, 2, important);
                              listStyleImage = true;
                           }
                           continue;
                        default:
                           index += 2;
                           continue;
                     }
                  case 2:
                     tempValue[0] = value[index++];
                     tempValue[1] = value[index++];
                     this.property(callDepth, 60, tempValue, 2, important);
                     listStyleType = true;
                     break;
                  case 3:
                     tempValue[0] = value[index++];
                     tempValue[1] = value[index++];
                     this.property(callDepth, 58, tempValue, 2, important);
                     listStyleImage = true;
               }
            }
            break;
         case 58:
            int var68 = value[0] & 15;
            if (var68 == 1) {
               if (value[1] == 97) {
                  flags = 1;
                  propertyValue = 97;
               }
            } else if (var68 == 3) {
               flags = 3;
               propertyValue = this.addStyleObject(this._cssParser.getURL(value[1]));
            }
            break;
         case 60:
            switch (value[0] & 15) {
               case 0:
                  break label720;
               case 1:
               default:
                  flags = 1;
                  propertyValue = value[1];
                  break label720;
               case 2:
                  CSSString strValue = this._cssParser.getString(value[1]);
                  if (strValue.equalsIgnoreCase("lower-alpha")) {
                     flags = 1;
                     propertyValue = 181;
                  } else if (strValue.equalsIgnoreCase("upper-alpha")) {
                     flags = 1;
                     propertyValue = 182;
                  }
                  break label720;
            }
         case 100:
            if ((value[0] & 15) == 1) {
               switch (value[1]) {
                  case 6:
                  case 48:
                     flags = 1;
                     propertyValue = value[1];
               }
            }
            break;
         case 101:
            if ((value[0] & 15) == 1) {
               switch (value[1]) {
                  case 20:
                     flags = 1;
                     propertyValue = 9;
                     break;
                  case 66:
                  case 69:
                     flags = 1;
                     propertyValue = 8;
                     break;
                  case 117:
                     flags = 1;
                     propertyValue = 10;
               }
            }
            break;
         case 102:
            boolean valid = true;

            for (int i = 0; i < valueLength - 1 && valid; i++) {
               if ((value[i] & 15) == 1) {
                  i++;
                  switch (value[i]) {
                     case 12:
                        flags = 1;
                        propertyValue |= 8;
                        break;
                     case 74:
                        flags = 1;
                        propertyValue |= 4;
                        break;
                     case 97:
                        flags = 1;
                        propertyValue = 0;
                        break;
                     case 106:
                        flags = 1;
                        propertyValue |= 2;
                        break;
                     case 163:
                        flags = 1;
                        propertyValue |= 1;
                        break;
                     default:
                        flags = 0;
                        valid = false;
                  }
               } else {
                  flags = 0;
                  valid = false;
               }
            }
            break;
         case 104:
            if ((value[0] & 15) == 1) {
               switch (value[1]) {
                  case 18:
                  case 82:
                  case 97:
                  case 166:
                     flags = 1;
                     propertyValue = value[1];
               }
            }
            break;
         case 107:
            if ((value[0] & 15) == 1) {
               switch (value[1]) {
                  case 8:
                  case 17:
                  case 88:
                  case 160:
                     flags = 1;
                     propertyValue = value[1];
               }
            }
            break;
         case 108:
            if ((value[0] & 15) == 1) {
               switch (value[1]) {
                  case 27:
                  case 52:
                  case 167:
                     flags = 1;
                     propertyValue = value[1];
               }
            }
            break;
         case 111:
            if ((value[0] & 15) == 1) {
               switch (value[1]) {
                  case 99:
                     flags = 1;
                     propertyValue = 99;
               }
            }
            break;
         case 117:
            if ((value[0] & 15) == 2) {
               flags = 2;
               propertyValue = this.addStyleObject(this._cssParser.getString(value[1]));
            }
            break;
         case 118:
            if ((value[0] & 15) == 1) {
               switch (value[1]) {
                  case 41:
                     flags = 1;
                     propertyValue = 41;
                     break;
                  case 162:
                     flags = 1;
                     propertyValue = 162;
               }
            }
            break;
         case 119:
            if ((value[0] & 15) == 1) {
               switch (value[1]) {
                  case 83:
                     flags = 1;
                     propertyValue = 64;
                     break;
                  case 120:
                     flags = 1;
                     propertyValue = 32;
               }
            }
            break;
         case 120:
            int type = value[0] & 15;
            if (type == 1) {
               switch (value[1]) {
                  case 57:
                     flags = 6;
                     propertyValue = Integer.MAX_VALUE;
               }
            } else if (type == 6) {
               flags = 6;
               propertyValue = value[1];
            }
            break;
         case 121:
            if ((value[0] & 15) == 1) {
               switch (value[1]) {
                  case 45:
                  case 98:
                  case 131:
                     flags = 1;
                     propertyValue = value[1];
               }
            }
            break;
         case 122:
            if ((value[0] & 15) == 1) {
               switch (value[1]) {
                  case 3:
                     flags = 1;
                     propertyValue = 8;
                     break;
                  case 124:
                     flags = 1;
                     propertyValue = 2;
                     break;
                  case 130:
                     flags = 1;
                     propertyValue = 4;
               }
            }
      }

      if (flags != 0) {
         if (this._currentStyleIndex == -1) {
            this._currentStyleIndex = this.incrementStyleCount();
         }

         this.setStyle(this._currentStyleIndex, name, flags, propertyValue);
      }
   }

   private final void setBlockStyle(HTMLGenericElement element, String styleClass, String styleId, String styleValue) {
      this.setBlockStyle(element, styleClass, styleId, styleValue, -1);
   }

   private final void setBlockStyle(HTMLGenericElement element, String styleClass, String styleId, String styleValue, int alignment) {
      this._textUtilities.setDirection(element, this._lastDirection, true);
      if (this._processStyleSheets) {
         int style = this.getStyle(element.getTagNameInt(), styleClass, styleId, styleValue);
         this.setTextStyle(style, 4);
         boolean doMarquee = false;
         int backgroundColor = -1;
         boolean pushCell = false;
         if (style != -1) {
            int animation = 0;
            int delay = 85;
            int amount = 6;
            int loop = 1;
            int halign = 0;
            String backgroundURI = null;
            boolean repeatX = true;
            boolean repeatY = true;
            int backgroundXPos = 0;
            int backgroundYPos = 0;
            int width = -1;
            int height = -1;
            boolean widthSet = false;
            int blockPaddingLeft = 0;
            int blockPaddingTop = 0;
            int blockPaddingBottom = 0;
            int blockPaddingRight = 0;
            Border border = this.getStyleBorder(element, style);
            short endIndex = this._styleTops[style + 1];

            for (short styleIndex = this._styleTops[style]; styleIndex < endIndex; styleIndex++) {
               int flags = (int)(this._styleFlagsValues[styleIndex] >> 32 & 8388607);
               int itemId = (int)(this._styleFlagsValues[styleIndex] >> 55 & 511);
               int value = (int)(this._styleFlagsValues[styleIndex] & 4294967295L);
               switch (itemId) {
                  case 4:
                     backgroundColor = value;
                     this.handleTextStyle(element, 4, itemId, value, flags);
                     break;
                  case 5:
                     if ((flags & 15) == 3) {
                        backgroundURI = (String)this.getStyleObject(value);
                     }
                     break;
                  case 6:
                     if ((flags & 15) == 1) {
                        int[] values = (int[])this.getStyleObject(value);
                        backgroundXPos = this.interpretSizeValue(element, values[1], values[0], true, 0);
                        if ((values[0] & 16) != 0) {
                           backgroundXPos |= 1073741824;
                        } else if (backgroundXPos < 0) {
                           backgroundXPos *= -1;
                           backgroundXPos |= Integer.MIN_VALUE;
                        }

                        backgroundYPos = this.interpretSizeValue(element, values[3], values[2], true, 0);
                        if ((values[2] & 16) != 0) {
                           backgroundYPos |= 1073741824;
                        } else if (backgroundYPos < 0) {
                           backgroundYPos *= -1;
                           backgroundYPos |= Integer.MIN_VALUE;
                        }
                     }
                     break;
                  case 7:
                     switch (value) {
                        case 96:
                           repeatX = false;
                           repeatY = false;
                           continue;
                        case 113:
                           repeatX = true;
                           repeatY = true;
                           continue;
                        case 114:
                           repeatX = true;
                           repeatY = false;
                           continue;
                        case 115:
                           repeatX = false;
                           repeatY = true;
                        default:
                           continue;
                     }
                  case 34:
                  case 42:
                  case 48:
                  case 49:
                  case 50:
                  case 51:
                  case 52:
                  case 102:
                  case 104:
                  case 108:
                     this.handleTextStyle(element, 4, itemId, value, flags);
                     break;
                  case 43:
                     switch (value) {
                        case 180:
                           doMarquee = true;
                        default:
                           continue;
                     }
                  case 46:
                     switch (value) {
                        case 69:
                           halign = 2048;
                           element.setBreakingFlags((short)0);
                           continue;
                        case 97:
                           halign = 0;
                           continue;
                        case 117:
                           halign = 6144;
                           element.setBreakingFlags((short)0);
                        default:
                           continue;
                     }
                  case 53:
                     pushCell = true;
                     height = this.interpretSizeValue(element, value, flags, false, -1);
                     break;
                  case 58:
                     if (super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 4, RenderingOptions.USE_BACKGROUND_IMAGES_DEFAULT)) {
                        int type = flags & 15;
                        if (type == 1) {
                           if (value == 97) {
                              this._lastListStyleImage = null;
                           }
                        } else if (type == 3) {
                           this._lastListStyleImage = (String)this.getStyleObject(value);
                        }
                     }
                     break;
                  case 60:
                     this._lastListStyleType = value;
                     break;
                  case 68:
                     if (height == -1) {
                        pushCell = true;
                        height = this.interpretSizeValue(element, value, flags, false, -1);
                     }
                     break;
                  case 77:
                     blockPaddingBottom = this.interpretSizeValue(element, value, flags, false, 0);
                     break;
                  case 78:
                     blockPaddingLeft = this.interpretSizeValue(element, value, flags, false, 0);
                     break;
                  case 79:
                     blockPaddingRight = this.interpretSizeValue(element, value, flags, false, 0);
                     break;
                  case 80:
                     blockPaddingTop = this.interpretSizeValue(element, value, flags, false, 0);
                     break;
                  case 101:
                     this.handleTextStyle(element, 4, itemId, value, flags);
                     break;
                  case 113:
                     pushCell = true;
                     widthSet = true;
                     width = this.interpretSizeValue(element, value, flags, true, -1);
                     if ((flags & 16) != 0) {
                        width = -MathUtilities.clamp(0, width, 100);
                     }
                     break;
                  case 119:
                     animation |= value;
                     break;
                  case 120:
                     loop = value;
                     break;
                  case 121:
                     switch (value) {
                        case 45:
                           amount = 12;
                           continue;
                        case 131:
                           delay = 170;
                        default:
                           continue;
                     }
                  case 122:
                     animation |= value;
               }
            }

            int convertedPadding = blockPaddingLeft << 0 | blockPaddingRight << 8 | blockPaddingBottom << 24 | blockPaddingTop << 16;
            pushCell = pushCell || convertedPadding != 0;
            if (doMarquee) {
               if ((animation & 96) == 0) {
                  animation |= 32;
               }

               if ((animation & 14) == 0) {
                  animation |= 2;
               }

               synchronized (this._appEventLock) {
                  this._currentBrowserField.pushCell(-100, animation, delay, amount, loop, border);
                  this._currentContext._pushedCellCount++;
               }
            }

            if (this._currentContext._allTagsProvided) {
               boolean doBackgroundColor = backgroundColor != -1 && this._useColor;
               boolean doBackgroundImage = doBackgroundColor
                  || IS_COLOR
                     && backgroundURI != null
                     && backgroundURI.length() > 0
                     && super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 4, RenderingOptions.USE_BACKGROUND_IMAGES_DEFAULT);
               pushCell = pushCell || doBackgroundImage || doBackgroundColor || border != null;
               this._cellPushed.push(pushCell ? 1 : 0);
               if (pushCell) {
                  if (!doMarquee) {
                     int totalWidth = width > 0 ? width : Integer.MAX_VALUE;
                     int specifiedWidth;
                     if (widthSet) {
                        specifiedWidth = width;
                     } else if ((halign & 6144) != 0) {
                        specifiedWidth = Integer.MIN_VALUE;
                     } else {
                        specifiedWidth = -100;
                     }

                     synchronized (this._appEventLock) {
                        this._currentBrowserField.pushCell(totalWidth, specifiedWidth, false, halign, border, 0, convertedPadding, 1, 1, height);
                        this._currentContext._pushedCellCount++;
                     }
                  }

                  if (doBackgroundImage) {
                     this.handleBackgroundImage(backgroundURI, false, repeatX, repeatY, backgroundXPos, backgroundYPos);
                  }
               }
            }
         }

         this._textUtilities.setFont(element);
      }
   }

   private final void resetBlockStyle(HTMLGenericElement element) {
      this.resetMarqueeStyle();
      this.resetBlockBackgroundStyle(element);
      this.resetTextStyle(4);
      this._textUtilities.resetDirection(true);
   }

   private final void setTextStyle(HTMLGenericElement element, String styleClass, String styleId, String styleValue) {
      this._textUtilities.setDirection(element, this._lastDirection, true);
      if (this._processStyleSheets) {
         int style = this.getStyle(element.getTagNameInt(), styleClass, styleId, styleValue);
         this.setTextStyle(style, 0);
         if (style != -1) {
            short endIndex = this._styleTops[style + 1];

            for (short styleIndex = this._styleTops[style]; styleIndex < endIndex; styleIndex++) {
               int flags = (int)(this._styleFlagsValues[styleIndex] >> 32 & 8388607);
               int itemId = (int)(this._styleFlagsValues[styleIndex] >> 55 & 511);
               int value = (int)(this._styleFlagsValues[styleIndex] & 4294967295L);
               switch (itemId) {
                  case 4:
                  case 34:
                  case 42:
                  case 48:
                  case 49:
                  case 50:
                  case 51:
                  case 52:
                  case 101:
                  case 102:
                  case 104:
                  case 108:
                     this.handleTextStyle(element, 0, itemId, value, flags);
                     break;
               }
            }
         }
      }
   }

   private final void setTextStyle(int style, int context) {
      if (this._processStyleSheets) {
         this._styleStack.push(style);
      }
   }

   private final void handleTextStyle(HTMLGenericElement element, int context, int itemId, int value, int flags) {
      switch (itemId) {
         case 4:
            if (this._useColor) {
               element._backgroundColour = value;
               this._textUtilities.adjustToContrastColour(element);
               return;
            }
            break;
         case 34:
            if (this._useColor) {
               element._foregroundColour = value;
               this._textUtilities.adjustToContrastColour(element);
               return;
            }
            break;
         case 42:
            if (context == 4 || context == 1) {
               this._textUtilities.setDirection(element, value, true);
               return;
            }
            break;
         case 48:
            this._textUtilities.setFontFace(element, (String)this.getStyleObject(value));
            return;
         case 49:
            int type = flags & 15;
            if (type == 1) {
               this._textUtilities.setFontSize(element, value, false);
               return;
            }

            if (type == 6 || type == 7) {
               HTMLGenericElement parent = element;
               int stackSize = this._tagStack.size();
               if (stackSize > 1) {
                  parent = (HTMLGenericElement)this._tagStack.elementAt(stackSize - 2);
               }

               switch (flags & 496) {
                  case 0:
                  case 64:
                     this._textUtilities.setFontSize(element, this.scaleStyleValue(value, flags), true);
                     return;
                  case 16:
                     this._textUtilities.setFontSize(element, this.scaleStyleValue(value * parent.getFontHeight() / 100, flags), true);
                     return;
                  case 32:
                     this._textUtilities.setFontSize(element, this.scaleStyleValue(value * parent.getFontHeight(), flags), true);
                     return;
                  case 48:
                     this._textUtilities.setFontSize(element, this.scaleStyleValue(value * this._textUtilities.getCurrentFontXHeight(parent), flags), true);
                     return;
                  case 80:
                     this._textUtilities.setFontSize(element, this.scaleStyleValue(Ui.convertSize(value * 5, 4194308, 0) / 2, flags), true);
                     return;
                  case 96:
                     this._textUtilities.setFontSize(element, this.scaleStyleValue(Ui.convertSize(value, 4194308, 0), flags), true);
                     return;
                  case 112:
                     this._textUtilities.setFontSize(element, this.scaleStyleValue(Ui.convertSize(value, 2097156, 0), flags), true);
                     return;
                  case 128:
                     this._textUtilities.setFontSize(element, this.scaleStyleValue(Ui.convertSize(value, 2, 0), flags), true);
                     return;
                  case 144:
                     this._textUtilities.setFontSize(element, this.scaleStyleValue(Ui.convertSize(value * 12, 2, 0), flags), true);
                     return;
               }
            }
            break;
         case 50:
            this._textUtilities.setFontStyle(element, value);
            return;
         case 51:
            this._textUtilities.setFontVariant(element, value);
            return;
         case 52:
            this._textUtilities.setFontWeight(element, value);
            return;
         case 101:
            if (context == 0 || context == 4 || context == 1) {
               this._textUtilities.setAlignment(element, value);
               return;
            }
            break;
         case 102:
            this._textUtilities.setTextDecoration(element, value);
            return;
         case 104:
            this._textUtilities.setTextTransform(value);
            return;
         case 108:
            switch (value) {
               case 27:
               case 52:
                  element.setAdditionalFlags((short)(2048 | element.getFlags() & 1024));
                  return;
               default:
                  element.setAdditionalFlags((short)(element.getFlags() & 1024));
            }
      }
   }

   private final void clearStyle(int styleId) {
      if (styleId + 1 == this._styleCount) {
         this._styleFlagCount = this._styleTops[styleId];
         this._styleCount--;
      } else {
         int index = this._styleTops[styleId] & '\uffff';
         int nextIndex = this._styleTops[styleId + 1] & '\uffff';

         for (int i = index; i < nextIndex; i++) {
            this._styleFlagsValues[i] = 0;
         }
      }
   }

   private final void clearStyle(int styleId, int itemId) {
      int index = this._styleTops[styleId] & '\uffff';
      int nextIndex = this._styleTops[styleId + 1] & '\uffff';
      int clearIndex = -1;

      for (int i = index; i < nextIndex; i++) {
         if ((this._styleFlagsValues[i] >> 55 & 511) == itemId) {
            clearIndex = i;
            break;
         }
      }

      if (clearIndex >= index) {
         if (clearIndex > index) {
            System.arraycopy(this._styleFlagsValues, index, this._styleFlagsValues, index + 1, clearIndex - index);
         }

         this._styleFlagsValues[index] = 0;
      }
   }

   private final void setStyle(int styleId, int itemId, int value, int propertyValue) {
      Asserts.productionStateAssert(styleId + 1 == this._styleCount);
      int styleIndex = this._styleTops[styleId] & '\uffff';
      int styleNextIndex = this._styleTops[styleId + 1] & '\uffff';
      if (this._styleFlagCount + 1 >= this._styleFlagsValues.length) {
         Array.resize(this._styleFlagsValues, this._styleFlagsValues.length + Array.getSectionSize(this._styleFlagsValues));
      }

      int i;
      for (i = styleIndex; i < styleNextIndex; i++) {
         int curItemId = (int)(this._styleFlagsValues[i] >> 55 & 511);
         if (itemId == curItemId) {
            break;
         }

         if (itemId < curItemId) {
            System.arraycopy(this._styleFlagsValues, i, this._styleFlagsValues, i + 1, this._styleFlagCount - i);
            this._styleFlagCount++;
            break;
         }
      }

      if (i == styleNextIndex) {
         this._styleFlagCount++;
      }

      this._styleFlagsValues[i] = (long)itemId << 55 | (long)value << 32 | propertyValue & 4294967295L;
      this._styleTops[this._styleCount] = (short)this._styleFlagCount;
   }

   private final void cloneStyles(int newIndex, int oldIndex) {
      Asserts.productionStateAssert(newIndex + 1 == this._styleCount);
      int index = this._styleTops[oldIndex] & '\uffff';
      int nextIndex = this._styleTops[oldIndex + 1] & '\uffff';
      int length = nextIndex - index;
      if (this._styleFlagCount + length >= this._styleFlagsValues.length) {
         int sectionSize = Array.getSectionSize(this._styleFlagsValues);
         int multiplier = 1;
         if (length > sectionSize) {
            multiplier = (length + sectionSize - 1) / sectionSize;
         }

         Array.resize(this._styleFlagsValues, this._styleFlagsValues.length + multiplier * sectionSize);
      }

      System.arraycopy(this._styleFlagsValues, index, this._styleFlagsValues, this._styleFlagCount, length);
      this._styleFlagCount += length;
      this._styleTops[this._styleCount] = (short)this._styleFlagCount;
   }

   private final void mergeStyles(int newStyle, int oldStyle) {
      int newStyleIndex = this._styleTops[newStyle] & '\uffff';
      int newStyleNextIndex = this._styleTops[newStyle + 1] & '\uffff';
      int newStyleLength = newStyleNextIndex - newStyleIndex;
      int oldStyleIndex = this._styleTops[oldStyle] & '\uffff';
      int oldStyleNextIndex = this._styleTops[oldStyle + 1] & '\uffff';
      int oldStyleLength = oldStyleNextIndex - oldStyleIndex;
      if (oldStyleLength != 0) {
         if (this._styleFlagCount + oldStyleLength >= this._styleFlagsValues.length) {
            int sectionSize = Array.getSectionSize(this._styleFlagsValues);
            int multiplier = 1;
            if (oldStyleLength > sectionSize) {
               multiplier = (oldStyleLength + sectionSize - 1) / sectionSize;
            }

            Array.resize(this._styleFlagsValues, this._styleFlagsValues.length + multiplier * sectionSize);
         }

         if (this._styleSortBuffer.length < oldStyleLength + newStyleLength) {
            Array.resize(this._styleSortBuffer, oldStyleLength + newStyleLength);
         }

         int bufferLength = newStyleNextIndex - newStyleIndex;
         System.arraycopy(this._styleFlagsValues, newStyleIndex, this._styleSortBuffer, 0, bufferLength);

         for (int i = oldStyleIndex; i < oldStyleNextIndex; i++) {
            int insertItemId = (int)(this._styleFlagsValues[i] >> 55 & 511);
            int j = 0;

            for (j = 0; j < bufferLength; j++) {
               int itemId = (int)(this._styleSortBuffer[j] >> 55 & 511);
               if (insertItemId == itemId) {
                  this._styleSortBuffer[j] = this._styleFlagsValues[i];
                  break;
               }

               if (insertItemId < itemId) {
                  System.arraycopy(this._styleSortBuffer, j, this._styleSortBuffer, j + 1, bufferLength - j);
                  this._styleSortBuffer[j] = this._styleFlagsValues[i];
                  bufferLength++;
                  break;
               }
            }

            if (j == bufferLength) {
               this._styleSortBuffer[bufferLength++] = this._styleFlagsValues[i];
            }
         }

         int newLength = bufferLength;
         int numAdded = newLength - newStyleLength;
         int newOldStyleIndex = oldStyleIndex;
         if (oldStyle > newStyle) {
            newOldStyleIndex += numAdded;
         }

         System.arraycopy(
            this._styleFlagsValues, newStyleNextIndex, this._styleFlagsValues, newStyleIndex + newLength, this._styleFlagCount - newStyleNextIndex
         );
         System.arraycopy(this._styleSortBuffer, 0, this._styleFlagsValues, newStyleIndex, bufferLength);
         if (numAdded != 0) {
            this._styleFlagCount += numAdded;

            for (int j = newStyle + 1; j <= this._styleCount; j++) {
               this._styleTops[j] = (short)(this._styleTops[j] + numAdded);
            }
         }
      }
   }

   private final int incrementStyleCount() {
      int index = this._styleCount++;
      if (this._styleCount + 1 >= this._styleTops.length) {
         Array.resize(this._styleTops, this._styleCount + Array.getSectionSize(this._styleTops));
      }

      this._styleTops[index] = (short)this._styleFlagCount;
      this._styleTops[index + 1] = (short)this._styleFlagCount;
      return index;
   }

   public static final int maskedBinarySearch(long[] a, long key, int fromIndex, int toIndex, long mask) {
      int low = fromIndex;
      int high = toIndex - 1;

      while (low <= high) {
         int mid = low + high >> 1;
         if (key == (a[mid] & mask)) {
            high = mid;
            if (mid == low) {
               return low;
            }
         } else if (key < (a[mid] & mask)) {
            high = mid - 1;
         } else {
            low = mid + 1;
         }
      }

      return -(low + 1);
   }

   private final void resetTextStyle() {
      this.resetTextStyle(0);
      this._textUtilities.resetDirection(true);
   }

   private final void resetTextStyle(int context) {
      if (!this._styleStack.isEmpty()) {
         int style = this._styleStack.pop();
         if (style != -1) {
            short endIndex = this._styleTops[style + 1];

            for (short styleIndex = this._styleTops[style]; styleIndex < endIndex; styleIndex++) {
               int itemId = (int)(this._styleFlagsValues[styleIndex] >> 55 & 511);
               switch (itemId) {
                  case 42:
                     if (context == 4 || context == 1) {
                        this._textUtilities.resetDirection(true);
                     }
                     break;
                  case 51:
                     this._textUtilities.resetFontVariant();
                     break;
                  case 52:
                     this._textUtilities.resetFontWeight();
                     break;
                  case 102:
                     this._textUtilities.resetTextDecoration();
                     break;
                  case 104:
                     this._textUtilities.resetTextTransform();
               }
            }

            this.clearStyle(style);
         }
      }
   }

   private final void resetMarqueeStyle() {
      if (!this._styleStack.isEmpty()) {
         int style = this._styleStack.peek();
         if (style != -1) {
            short endIndex = this._styleTops[style + 1];

            for (short styleIndex = this._styleTops[style]; styleIndex < endIndex; styleIndex++) {
               int itemId = (int)(this._styleFlagsValues[styleIndex] >> 55 & 511);
               if (itemId == 43) {
                  int value = (int)(this._styleFlagsValues[styleIndex] & 4294967295L);
                  if (value == 180) {
                     synchronized (this._appEventLock) {
                        this._currentBrowserField.popCell();
                        this._currentContext._pushedCellCount--;
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   private final int getStyle(int element, String styleClass, String styleId, String styleValue) {
      if (!this._processStyleSheets) {
         return -1;
      }

      int style = -1;
      style = this.getElementStyle(element, style);
      style = this.getDescendantStyle(element, styleClass, styleId, style);
      style = this.getClassStyle(element, styleClass, style);
      style = this.getIdStyle(element, styleId, style);
      if (styleValue != null) {
         this._currentStyleIndex = -1;
         CSSParser oldParser = this._cssParser;
         this._cssParser = new CSSTextParser(this, null);
         this._cssParser.parseStyleDeclaration(styleValue);
         this._cssParser = oldParser;
         if (this._currentStyleIndex != -1) {
            if (style == -1) {
               style = this._currentStyleIndex;
            } else {
               this.mergeStyles(style, this._currentStyleIndex);
               this.clearStyle(this._currentStyleIndex);
            }

            this._currentStyleIndex = -1;
         }
      }

      return style;
   }

   private final int getStyleValue(int styleNumber, int style) {
      if (style != -1) {
         short endIndex = this._styleTops[style + 1];

         for (short styleIndex = this._styleTops[style]; styleIndex < endIndex; styleIndex++) {
            int itemId = (int)(this._styleFlagsValues[styleIndex] >> 55 & 511);
            int value = (int)(this._styleFlagsValues[styleIndex] & 4294967295L);
            if (itemId == styleNumber) {
               return value;
            }
         }
      }

      return 0;
   }

   private final boolean isBlockStyle(int style, boolean elementDefault) {
      if (style != -1) {
         short endIndex = this._styleTops[style + 1];
         short styleIndex = this._styleTops[style];

         while (styleIndex < endIndex) {
            int itemId = (int)(this._styleFlagsValues[styleIndex] >> 55 & 511);
            int value = (int)(this._styleFlagsValues[styleIndex] & 4294967295L);
            switch (itemId) {
               case 43:
                  switch (value) {
                     case 13:
                        return true;
                     case 59:
                        return false;
                  }
               default:
                  styleIndex++;
            }
         }
      }

      return elementDefault;
   }

   private final void addElementStyle(int element, int style) {
      if (element <= 100) {
         if (this._elementStyles == null) {
            this._elementStyles = new int[101];
            Arrays.fill(this._elementStyles, -1);
         }

         int currentStyle = this._elementStyles[element];
         if (currentStyle == -1) {
            this._elementStyles[element] = style;
         } else {
            int index = this.incrementStyleCount();
            this.cloneStyles(index, currentStyle);
            this.mergeStyles(index, style);
            this._elementStyles[element] = index;
         }
      }
   }

   private final void addClassStyle(int element, CSSString classString, int styleId) {
      if (element <= 100) {
         if (this._classStyles == null) {
            this._classStyles = new Object[101];
         }

         if (this._classStyles[element] == null) {
            this._classStyles[element] = (ToIntHashtable)(new Object());
         }

         int currentStyle = this._classStyles[element].get(classString);
         if (currentStyle == -1) {
            this._classStyles[element].put(classString, styleId);
         } else {
            int index = this.incrementStyleCount();
            this.cloneStyles(index, currentStyle);
            this.mergeStyles(index, styleId);
            this._classStyles[element].put(classString, index);
         }
      }
   }

   private final void addIdStyle(int element, CSSString id, int style) {
      if (element <= 100) {
         if (this._idStyles == null) {
            this._idStyles = new Object[101];
         }

         if (this._idStyles[element] == null) {
            this._idStyles[element] = (ToIntHashtable)(new Object());
         }

         int currentStyle = this._idStyles[element].get(id);
         if (currentStyle == -1) {
            this._idStyles[element].put(id, style);
         } else {
            int index = this.incrementStyleCount();
            this.cloneStyles(index, currentStyle);
            this.mergeStyles(index, style);
            this._idStyles[element].put(id, index);
         }
      }
   }

   private final void addDescendantStyle(Vector descendants, int style) {
      CSSSelectorNode node = (CSSSelectorNode)descendants.firstElement();
      int element = node._element;
      if (element <= 100) {
         CSSString classString = node._classString;
         CSSString id = node._idString;
         ToIntHashtable styles = null;
         if (classString == null && id == null) {
            if (this._descendantElementStyles == null) {
               this._descendantElementStyles = new Object[101];
            }

            styles = this._descendantElementStyles[element];
            if (styles == null) {
               styles = (ToIntHashtable)(new Object());
               this._descendantElementStyles[element] = styles;
            }
         } else if (id == null) {
            if (this._descendantClassStyles == null) {
               this._descendantClassStyles = new Object[101];
            }

            if (this._descendantClassStyles[element] == null) {
               this._descendantClassStyles[element] = (Hashtable)(new Object());
            }

            styles = (ToIntHashtable)this._descendantClassStyles[element].get(classString);
            if (styles == null) {
               styles = (ToIntHashtable)(new Object());
               this._descendantClassStyles[element].put(classString, styles);
            }
         } else {
            if (this._descendantIdStyles == null) {
               this._descendantIdStyles = new Object[101];
            }

            if (this._descendantIdStyles[element] == null) {
               this._descendantIdStyles[element] = (Hashtable)(new Object());
            }

            styles = (ToIntHashtable)this._descendantIdStyles[element].get(id);
            if (styles == null) {
               styles = (ToIntHashtable)(new Object());
               this._descendantIdStyles[element].put(id, styles);
            }
         }

         int currentStyle = styles.get(descendants);
         if (currentStyle == -1) {
            styles.put(descendants, style);
         } else {
            int index = this.incrementStyleCount();
            this.cloneStyles(index, currentStyle);
            this.mergeStyles(index, style);
            styles.put(descendants, index);
         }

         this._descendantStylesEncountered = true;
      }
   }

   private final int getElementStyle(int element, int style) {
      if (this._elementStyles != null && element <= 100) {
         int elementStyle = this._elementStyles[0];
         if (elementStyle != -1) {
            if (style == -1) {
               style = this.incrementStyleCount();
               this.cloneStyles(style, elementStyle);
            } else {
               this.mergeStyles(style, elementStyle);
            }
         }

         elementStyle = this._elementStyles[element];
         if (elementStyle != -1) {
            if (style == -1) {
               style = this.incrementStyleCount();
               this.cloneStyles(style, elementStyle);
               return style;
            }

            this.mergeStyles(style, elementStyle);
         }
      }

      return style;
   }

   private final int getClassStyle(int element, String classString, int style) {
      if (classString != null && this._classStyles != null && element <= 100) {
         String[] classes = null;
         if (classString.indexOf(32) != -1) {
            StringTokenizer strings = (StringTokenizer)(new Object(classString, ' '));
            classes = new Object[strings.countTokens()];

            for (int i = 0; strings.hasMoreTokens(); i++) {
               classes[i] = strings.nextToken();
            }
         } else {
            classes = new Object[]{classString};
         }

         ToIntHashtable styles = this._classStyles[0];
         if (styles != null) {
            for (int i = 0; i < classes.length; i++) {
               int classStyle = styles.get(classes[i]);
               if (classStyle != -1) {
                  if (style == -1) {
                     style = this.incrementStyleCount();
                     this.cloneStyles(style, classStyle);
                  } else {
                     this.mergeStyles(style, classStyle);
                  }
               }
            }
         }

         styles = this._classStyles[element];
         if (styles != null) {
            for (int i = 0; i < classes.length; i++) {
               int classStyle = styles.get(classes[i]);
               if (classStyle != -1) {
                  if (style == -1) {
                     style = this.incrementStyleCount();
                     this.cloneStyles(style, classStyle);
                  } else {
                     this.mergeStyles(style, classStyle);
                  }
               }
            }
         }
      }

      return style;
   }

   private final int getIdStyle(int element, String id, int style) {
      if (id != null && this._idStyles != null && element <= 100) {
         ToIntHashtable styles = this._idStyles[0];
         if (styles != null) {
            int idStyle = styles.get(id);
            if (idStyle != -1) {
               if (style == -1) {
                  style = this.incrementStyleCount();
                  this.cloneStyles(style, idStyle);
               } else {
                  this.mergeStyles(style, idStyle);
               }
            }
         }

         styles = this._idStyles[element];
         if (styles != null) {
            int idStyle = styles.get(id);
            if (idStyle != -1) {
               if (style == -1) {
                  style = this.incrementStyleCount();
                  this.cloneStyles(style, idStyle);
                  return style;
               }

               this.mergeStyles(style, idStyle);
            }
         }
      }

      return style;
   }

   private final int getDescendantStyle(int element, String classString, String id, int style) {
      if (this._descendantStyles != null && element <= 100) {
         ToIntHashtable styles = this._descendantStyles[0];
         if (styles != null) {
            style = this.getDescendantStyle(styles, element, classString, id, style);
         }

         styles = this._descendantStyles[element];
         if (styles != null) {
            style = this.getDescendantStyle(styles, element, classString, id, style);
         }
      }

      return style;
   }

   private final int getDescendantStyle(ToIntHashtable styles, int element, String classString, String id, int style) {
      Enumeration enumeration = styles.keys();

      while (enumeration.hasMoreElements()) {
         Vector descendants = (Vector)enumeration.nextElement();
         if (this.checkDescendants(descendants, element, classString, id)) {
            int descendantStyle = styles.get(descendants);
            if (descendantStyle != -1) {
               if (style == -1) {
                  style = this.incrementStyleCount();
                  this.cloneStyles(style, descendantStyle);
               } else {
                  this.mergeStyles(style, descendantStyle);
               }
            }
         }
      }

      return style;
   }

   private final boolean checkDescendants(Vector descendants, int element, String classString, String id) {
      int descendantIndex = descendants.size() - 1;
      int stackIndex = this._tagStack.size() - 1;
      if (descendantIndex <= stackIndex) {
         CSSSelectorNode node = (CSSSelectorNode)descendants.elementAt(descendantIndex);
         if (node.matches(element, classString, id)) {
            descendantIndex--;
            stackIndex--;

            while (descendantIndex <= stackIndex && descendantIndex >= 0 && stackIndex >= 0) {
               node = (CSSSelectorNode)descendants.elementAt(descendantIndex);

               while (descendantIndex <= stackIndex && stackIndex >= 0) {
                  if (this.nodeMatches(node, stackIndex)) {
                     descendantIndex--;
                     stackIndex--;
                     break;
                  }

                  stackIndex--;
               }
            }
         }
      }

      return descendantIndex < 0;
   }

   private final boolean nodeMatches(CSSSelectorNode node, int tagIndex) {
      if ((node._element == 0 || node._element == ((HTMLGenericElement)this._tagStack.elementAt(tagIndex)).getTagNameInt())
         && (node._idString == null || node._idString.equals(this._tagIds.elementAt(tagIndex)))) {
         if (node._classString == null) {
            return true;
         }

         String classString = (String)this._tagClasses.elementAt(tagIndex);
         if (classString != null) {
            if (classString.indexOf(32) != -1) {
               StringTokenizer classes = (StringTokenizer)(new Object(classString, ' '));

               while (classes.hasMoreTokens()) {
                  if (node._classString.equals(classes.nextToken())) {
                     return true;
                  }
               }
            } else if (node._classString.equals(classString)) {
               return true;
            }
         }
      }

      return false;
   }

   private final void findDescendantStyles(int element, String classString, String id, int index) {
      if (this._descendantStylesEncountered && element <= 100) {
         if (this._descendantElementStyles != null) {
            if (index == 0) {
               ToIntHashtable styles = this._descendantElementStyles[0];
               if (styles != null && index == 0) {
                  this.findDescendantStyles(styles, element, classString, id, index);
               }
            }

            ToIntHashtable styles = this._descendantElementStyles[element];
            if (styles != null) {
               this.findDescendantStyles(styles, element, classString, id, index);
            }
         }

         if (this._descendantClassStyles != null && classString != null) {
            if (this._descendantClassStyles[0] != null) {
               ToIntHashtable styles = (ToIntHashtable)this._descendantClassStyles[0].get(classString);
               if (styles != null) {
                  this.findDescendantStyles(styles, element, classString, id, index);
               }
            }

            if (this._descendantClassStyles[element] != null) {
               ToIntHashtable styles = (ToIntHashtable)this._descendantClassStyles[element].get(classString);
               if (styles != null) {
                  this.findDescendantStyles(styles, element, classString, id, index);
               }
            }
         }

         if (this._descendantIdStyles != null && id != null) {
            if (this._descendantIdStyles[0] != null) {
               ToIntHashtable styles = (ToIntHashtable)this._descendantIdStyles[0].get(id);
               if (styles != null) {
                  this.findDescendantStyles(styles, element, classString, id, index);
               }
            }

            if (this._descendantIdStyles[element] != null) {
               ToIntHashtable styles = (ToIntHashtable)this._descendantIdStyles[element].get(id);
               if (styles != null) {
                  this.findDescendantStyles(styles, element, classString, id, index);
               }
            }
         }
      }
   }

   private final void findDescendantStyles(ToIntHashtable styles, int element, String classString, String id, int index) {
      Enumeration enumeration = styles.keys();

      while (enumeration.hasMoreElements()) {
         Vector descendants = (Vector)enumeration.nextElement();
         CSSSelectorNode node = (CSSSelectorNode)descendants.firstElement();
         if (node.matches(element, classString, id)) {
            node = (CSSSelectorNode)descendants.lastElement();
            int lastElement = node._element;
            if (lastElement <= 100) {
               if (this._descendantStyles == null) {
                  this._descendantStyles = new Object[101];
               }

               if (this._descendantStyles[lastElement] == null) {
                  this._descendantStyles[lastElement] = (ToIntHashtable)(new Object());
               }

               if (this._descendantStyles[lastElement].get(descendants) == -1) {
                  int style = styles.get(descendants);
                  this._descendantStyles[lastElement].put(descendants, style);
                  if (this._addedDescendantStyles == null) {
                     this._addedDescendantStyles = new Object[index + 20];
                  }

                  if (index >= this._addedDescendantStyles.length) {
                     Array.resize(this._addedDescendantStyles, index + 20);
                  }

                  if (this._addedDescendantStyles[index] == null) {
                     this._addedDescendantStyles[index] = (Vector)(new Object());
                  }

                  this._addedDescendantStyles[index].addElement(descendants);
               }
            }
         }
      }
   }

   private final void removeDescendantStyles(int index) {
      if (this._addedDescendantStyles != null && index < this._addedDescendantStyles.length) {
         Vector styles = this._addedDescendantStyles[index];
         if (styles != null) {
            for (int i = styles.size() - 1; i >= 0; i--) {
               Vector descendants = (Vector)styles.elementAt(i);
               CSSSelectorNode node = (CSSSelectorNode)descendants.lastElement();
               this._descendantStyles[node._element].remove(descendants);
            }

            styles.removeAllElements();
         }
      }
   }

   private final int addStyleObject(Object object) {
      if (this._styleObjects == null) {
         this._styleObjects = (Vector)(new Object());
      }

      this._styleObjects.addElement(object);
      return this._styleObjects.size() - 1;
   }

   private final Object getStyleObject(int index) {
      return this._styleObjects.elementAt(index);
   }

   private final InlineDataRefHolder loadExternalStyleSheet(String param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 2
      // 002: aload 0
      // 003: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._inlineData Ljava/util/Hashtable;
      // 006: aload 1
      // 007: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 00a: astore 3
      // 00b: aload 3
      // 00c: dup
      // 00d: instanceof net/rim/device/apps/internal/browser/html/InlineDataRefHolder
      // 010: ifne 017
      // 013: pop
      // 014: goto 01d
      // 017: checkcast net/rim/device/apps/internal/browser/html/InlineDataRefHolder
      // 01a: astore 2
      // 01b: aload 2
      // 01c: areturn
      // 01d: aload 0
      // 01e: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 021: ifnonnull 027
      // 024: goto 1cd
      // 027: aload 0
      // 028: bipush 3
      // 02a: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.loadingStatusEventOccurred (I)V
      // 02d: aload 0
      // 02e: aload 0
      // 02f: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentPriorityCount I
      // 032: bipush 1
      // 033: iadd
      // 034: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentPriorityCount I
      // 037: getstatic net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.INLINE_DATA_304 Ljava/lang/String;
      // 03a: aload 3
      // 03b: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 03e: istore 4
      // 040: new java/lang/Object
      // 043: dup
      // 044: aload 1
      // 045: aload 0
      // 046: bipush 1
      // 047: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.getRequestHeaders (Z)Lnet/rim/device/api/io/http/HttpHeaders;
      // 04a: aload 0
      // 04b: getfield net/rim/device/apps/internal/browser/page/Renderer._flags I
      // 04e: sipush 255
      // 051: iand
      // 052: iload 4
      // 054: ifeq 05d
      // 057: ldc_w 32768
      // 05a: goto 05e
      // 05d: bipush 0
      // 05e: ior
      // 05f: invokespecial net/rim/device/api/browser/field/RequestedResource.<init> (Ljava/lang/String;Lnet/rim/device/api/io/http/HttpHeaders;I)V
      // 062: astore 5
      // 064: getstatic net/rim/device/internal/browser/util/TimeLogger._loggingEnabled Z
      // 067: ifeq 076
      // 06a: invokestatic net/rim/device/internal/browser/util/TimeLogger.getInstance ()Lnet/rim/device/internal/browser/util/TimeLogger;
      // 06d: bipush 11
      // 06f: aload 1
      // 070: invokevirtual java/lang/String.hashCode ()I
      // 073: invokevirtual net/rim/device/internal/browser/util/TimeLogger.startTimer (II)V
      // 076: aload 0
      // 077: getfield net/rim/device/apps/internal/browser/page/Renderer._renderingApplication Lnet/rim/device/api/browser/field/RenderingApplication;
      // 07a: aload 5
      // 07c: aconst_null
      // 07d: invokeinterface net/rim/device/api/browser/field/RenderingApplication.getResource (Lnet/rim/device/api/browser/field/RequestedResource;Lnet/rim/device/api/browser/field/BrowserContent;)Ljavax/microedition/io/HttpConnection; 3
      // 082: astore 6
      // 084: aconst_null
      // 085: astore 7
      // 087: aload 6
      // 089: ifnull 0c6
      // 08c: aload 6
      // 08e: invokeinterface javax/microedition/io/HttpConnection.getResponseCode ()I 1
      // 093: sipush 200
      // 096: if_icmpne 0c6
      // 099: aload 6
      // 09b: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 0a0: astore 7
      // 0a2: aload 7
      // 0a4: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.readBytesFromInputStream (Ljava/io/InputStream;)[B
      // 0a7: astore 8
      // 0a9: aload 8
      // 0ab: ifnull 0c6
      // 0ae: bipush 0
      // 0af: istore 9
      // 0b1: aload 0
      // 0b2: aload 8
      // 0b4: aload 6
      // 0b6: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getCharacterEncoding (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 0b9: invokespecial net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow.getTextDataWithEncoding ([BLjava/lang/String;)Lnet/rim/device/apps/internal/browser/html/InlineDataRefHolder;
      // 0bc: astore 2
      // 0bd: aload 2
      // 0be: aload 6
      // 0c0: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getContentType (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 0c3: putfield net/rim/device/apps/internal/browser/html/InlineDataRefHolder._str Ljava/lang/String;
      // 0c6: aload 7
      // 0c8: ifnull 0d0
      // 0cb: aload 7
      // 0cd: invokevirtual java/io/InputStream.close ()V
      // 0d0: aload 6
      // 0d2: ifnonnull 0d8
      // 0d5: goto 1b1
      // 0d8: aload 6
      // 0da: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0df: goto 1b1
      // 0e2: astore 8
      // 0e4: goto 1b1
      // 0e7: astore 8
      // 0e9: aload 6
      // 0eb: ifnonnull 0f1
      // 0ee: goto 1b1
      // 0f1: aload 6
      // 0f3: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0f8: goto 1b1
      // 0fb: astore 8
      // 0fd: goto 1b1
      // 100: astore 10
      // 102: aload 6
      // 104: ifnull 113
      // 107: aload 6
      // 109: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 10e: goto 113
      // 111: astore 11
      // 113: aload 10
      // 115: athrow
      // 116: astore 8
      // 118: aload 7
      // 11a: ifnull 122
      // 11d: aload 7
      // 11f: invokevirtual java/io/InputStream.close ()V
      // 122: aload 6
      // 124: ifnull 1b1
      // 127: aload 6
      // 129: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 12e: goto 1b1
      // 131: astore 8
      // 133: goto 1b1
      // 136: astore 8
      // 138: aload 6
      // 13a: ifnull 1b1
      // 13d: aload 6
      // 13f: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 144: goto 1b1
      // 147: astore 8
      // 149: goto 1b1
      // 14c: astore 12
      // 14e: aload 6
      // 150: ifnull 15f
      // 153: aload 6
      // 155: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 15a: goto 15f
      // 15d: astore 13
      // 15f: aload 12
      // 161: athrow
      // 162: astore 14
      // 164: aload 7
      // 166: ifnull 16e
      // 169: aload 7
      // 16b: invokevirtual java/io/InputStream.close ()V
      // 16e: aload 6
      // 170: ifnull 1ae
      // 173: aload 6
      // 175: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 17a: goto 1ae
      // 17d: astore 15
      // 17f: goto 1ae
      // 182: astore 15
      // 184: aload 6
      // 186: ifnull 1ae
      // 189: aload 6
      // 18b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 190: goto 1ae
      // 193: astore 15
      // 195: goto 1ae
      // 198: astore 16
      // 19a: aload 6
      // 19c: ifnull 1ab
      // 19f: aload 6
      // 1a1: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1a6: goto 1ab
      // 1a9: astore 17
      // 1ab: aload 16
      // 1ad: athrow
      // 1ae: aload 14
      // 1b0: athrow
      // 1b1: getstatic net/rim/device/internal/browser/util/TimeLogger._loggingEnabled Z
      // 1b4: ifeq 1c3
      // 1b7: invokestatic net/rim/device/internal/browser/util/TimeLogger.getInstance ()Lnet/rim/device/internal/browser/util/TimeLogger;
      // 1ba: bipush 11
      // 1bc: aload 1
      // 1bd: invokevirtual java/lang/String.hashCode ()I
      // 1c0: invokevirtual net/rim/device/internal/browser/util/TimeLogger.stopTimer (II)V
      // 1c3: aload 0
      // 1c4: aload 0
      // 1c5: getfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentPriorityCount I
      // 1c8: bipush 1
      // 1c9: isub
      // 1ca: putfield net/rim/device/apps/internal/browser/html/HTMLRendererWithTextFlow._currentPriorityCount I
      // 1cd: aload 2
      // 1ce: areturn
      // try (97 -> 102): 103 null
      // try (93 -> 97): 105 null
      // try (106 -> 111): 112 null
      // try (93 -> 97): 114 null
      // try (105 -> 106): 114 null
      // try (115 -> 119): 120 null
      // try (114 -> 115): 114 null
      // try (67 -> 93): 123 null
      // try (128 -> 132): 133 null
      // try (124 -> 128): 135 null
      // try (136 -> 140): 141 null
      // try (124 -> 128): 143 null
      // try (135 -> 136): 143 null
      // try (144 -> 148): 149 null
      // try (143 -> 144): 143 null
      // try (67 -> 93): 152 null
      // try (123 -> 124): 152 null
      // try (157 -> 161): 162 null
      // try (153 -> 157): 164 null
      // try (165 -> 169): 170 null
      // try (153 -> 157): 172 null
      // try (164 -> 165): 172 null
      // try (173 -> 177): 178 null
      // try (172 -> 173): 172 null
      // try (152 -> 153): 152 null
   }

   private final boolean isValidStyleSheet(String contentType, String mediaType, String title) {
      boolean valid = false;
      if (contentType == null
         || StringUtilities.strEqualIgnoreCase(contentType, "text/css", 1701707776)
         || StringUtilities.strEqualIgnoreCase(contentType, "application/vnd.rim.css", 1701707776)) {
         valid = true;
      }

      if (valid && mediaType != null) {
         valid = false;
         String deviceMediaType = super._renderingOptions.getPropertyWithStringValue(4550690918222697397L, 19, "handheld");
         int startIndex = 0;
         int length = mediaType.length();

         while (startIndex < length) {
            int commaIndex = mediaType.indexOf(44, startIndex);
            if (commaIndex == -1) {
               commaIndex = length;
            }

            String type = mediaType.substring(startIndex, commaIndex).trim();
            startIndex = commaIndex + 1;

            for (int i = 0; i < type.length(); i++) {
               char c = type.charAt(i);
               if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < '0' || c > '9') && c != '-') {
                  type = type.substring(0, i);
                  break;
               }
            }

            if (StringUtilities.strEqualIgnoreCase(type, "all", 1701707776) || StringUtilities.strEqualIgnoreCase(type, deviceMediaType, 1701707776)) {
               valid = true;
               break;
            }
         }
      }

      if (valid && title != null) {
         if (this._defaultStyle != null && !this._defaultStyle.equals(title)) {
            valid = false;
         } else {
            this._defaultStyle = title;
         }
      }

      return valid;
   }

   private final Border getStyleBorder(HTMLGenericElement element, int style) {
      return this.getStyleBorder(element, style, false);
   }

   private final Border getStyleBorder(HTMLGenericElement element, int style, boolean usePlaceholder) {
      Border border = null;
      int borderMask = 0;
      int borderTopWidth = 0;
      int borderRightWidth = 0;
      int borderBottomWidth = 0;
      int borderLeftWidth = 0;
      int borderTopColor = -1;
      int borderRightColor = -1;
      int borderBottomColor = -1;
      int borderLeftColor = -1;
      short endIndex = this._styleTops[style + 1];

      for (short styleIndex = this._styleTops[style]; styleIndex < endIndex; styleIndex++) {
         int flags = (int)(this._styleFlagsValues[styleIndex] >> 32 & 8388607);
         int itemId = (int)(this._styleFlagsValues[styleIndex] >> 55 & 511);
         int value = (int)(this._styleFlagsValues[styleIndex] & 4294967295L);
         switch (itemId) {
            case 9:
            case 13:
            case 14:
            case 15:
            case 19:
            case 23:
            case 24:
            case 25:
               break;
            case 10:
               borderBottomColor = value;
               break;
            case 11:
               if (value != 97 && value != 52) {
                  borderMask |= 8;
                  if (borderBottomWidth == 0) {
                     borderBottomWidth = 1;
                  }
               } else {
                  borderMask &= -9;
               }
               break;
            case 12:
               borderBottomWidth = this.interpretSizeValue(element, value, flags, false, borderBottomWidth);
               break;
            case 16:
               borderLeftColor = value;
               break;
            case 17:
               if (value != 97 && value != 52) {
                  borderMask |= 1;
                  if (borderLeftWidth == 0) {
                     borderLeftWidth = 1;
                  }
               } else {
                  borderMask &= -2;
               }
               break;
            case 18:
               borderLeftWidth = this.interpretSizeValue(element, value, flags, false, borderLeftWidth);
               break;
            case 20:
               borderRightColor = value;
               break;
            case 21:
               if (value != 97 && value != 52) {
                  borderMask |= 2;
                  if (borderRightWidth == 0) {
                     borderRightWidth = 1;
                  }
               } else {
                  borderMask &= -3;
               }
               break;
            case 22:
               borderRightWidth = this.interpretSizeValue(element, value, flags, false, borderRightWidth);
               break;
            case 26:
            default:
               borderTopColor = value;
               break;
            case 27:
               if (value != 97 && value != 52) {
                  borderMask |= 4;
                  if (borderTopWidth == 0) {
                     borderTopWidth = 1;
                  }
               } else {
                  borderMask &= -5;
               }
               break;
            case 28:
               borderTopWidth = this.interpretSizeValue(element, value, flags, false, borderTopWidth);
         }
      }

      if (borderTopWidth == 0 && borderRightWidth == 0 && borderBottomWidth == 0 && borderLeftWidth == 0) {
         borderMask = 0;
      }

      if (borderMask != 0) {
         if (usePlaceholder) {
            border = PLACEHOLDER_BORDER;
         } else if (this._useColor) {
            border = new ElementBorder(
               element,
               borderTopWidth,
               borderRightWidth,
               borderBottomWidth,
               borderLeftWidth,
               borderTopColor,
               borderRightColor,
               borderBottomColor,
               borderLeftColor
            );
         } else {
            border = this.getBorder(borderMask, borderTopWidth, borderRightWidth, borderBottomWidth, borderLeftWidth, false);
         }
      }

      return border;
   }

   private final int interpretSizeValue(HTMLGenericElement element, int value, int flags, boolean allowPercentage, int originalValue) {
      int tempValue;
      switch (flags & 496) {
         case 0:
         case 64:
            tempValue = value;
            break;
         case 16:
            if (!allowPercentage) {
               return originalValue;
            }

            tempValue = value;
            break;
         case 32:
            tempValue = value * element.getFontHeight();
            break;
         case 48:
            tempValue = value * this._textUtilities.getCurrentFontXHeight(element);
            break;
         case 80:
            tempValue = Ui.convertSize(value * 5, 4194308, 0) / 2;
            break;
         case 96:
            tempValue = Ui.convertSize(value, 4194308, 0);
            break;
         case 112:
            tempValue = Ui.convertSize(value, 2097156, 0);
            break;
         case 128:
            tempValue = Ui.convertSize(value, 2, 0);
            break;
         case 144:
            tempValue = Ui.convertSize(value * 12, 2, 0);
            break;
         default:
            return originalValue;
      }

      return (flags & 15) == 7 ? tempValue / 1000 : tempValue;
   }

   private final int scaleStyleValue(int value, long flags) {
      return (flags & 15) == 7 ? value / 1000 : value;
   }

   private final long mapAttributeValueAlignmentToField(int value) {
      switch (value) {
         case 4:
         case 8:
            return 4294967296L;
         case 5:
            return 17179869184L;
         case 6:
            return 51539607552L;
         case 7:
            return 34359738368L;
         case 9:
         default:
            return 12884901888L;
         case 10:
            return 8589934592L;
      }
   }

   private final int mapAttributeValueAlignmentToTextFlowData(int value) {
      switch (value) {
         case 8:
            return 1;
         case 9:
         default:
            return 3;
         case 10:
            return 2;
      }
   }

   private final void resetBlockBackgroundStyle(HTMLGenericElement element) {
      if (!this._styleStack.isEmpty()) {
         int style = this._styleStack.peek();
         if (style != -1) {
            int wasCellPushed = this._cellPushed.pop();
            if (wasCellPushed == 1) {
               synchronized (this._appEventLock) {
                  this._currentBrowserField.popCell();
                  this._currentContext._pushedCellCount--;
                  return;
               }
            }
         }
      }
   }

   HTMLRendererWithTextFlow(
      InputConnection connection,
      InputStream in,
      String baseUrl,
      RenderingSession renderingSession,
      RenderingApplication renderingApplication,
      String referrer,
      HTMLContext htmlContext,
      int flags,
      Frame frame,
      DeviceDataConversionEvent convEvent
   ) {
      super(connection, renderingSession, renderingApplication, referrer, flags);
      this._tagStack = (Stack)(new Object());
      this._skipPopCalls = (IntStack)(new Object());
      this._styleStack = (IntStack)(new Object());
      this._cellPushed = (IntStack)(new Object());
      this._url = baseUrl;
      this._htmlContext = htmlContext;
      this._imageMaps = (Hashtable)(new Object(0));
      this._inlineData = (Hashtable)(new Object());
      this._inlineFragments = (IntHashtable)(new Object());
      this._inlineDataRefs = (IntHashtable)(new Object());
      this._frame = frame;
      this._convEvent = convEvent;
      this._useApproximatedTicks = true;
      if (super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 17, true)) {
         this._tableStack = (Stack)(new Object());
      }

      if (HTMLBrowserField._singleLayoutMode) {
         this._guessedHeightLimit = DISPLAY_HEIGHT * 100;
         this._layoutCharAppendLimit = Integer.MAX_VALUE;
         this._imageLayoutLimit = Integer.MAX_VALUE;
      } else {
         this._guessedHeightLimit = DISPLAY_HEIGHT;
         this._layoutCharAppendLimit = 256;
         this._imageLayoutLimit = 25;
      }

      this._scriptsToExecute = (Vector)(new Object(0));
      this._appEventLock = Application.getEventLock();
      this._currentContext = new HTMLRendererContext(null);
      this._currentContext._in = new WAPInputStream(in);
      this._attributeItems = new byte[256];
      this._attributeValuesInts = new int[256];
      this._attributeValues = new Object[256];
      String referer = null;
      String host = null;
      String origEncoding = null;
      if (super._inputConnection instanceof Object) {
         HttpConnection httpConnection = (HttpConnection)super._inputConnection;
         host = httpConnection.getHost();
         referer = httpConnection.getRequestProperty(HeaderParser.REFERER);

         label65:
         try {
            origEncoding = httpConnection.getHeaderField("x-rim-original-encoding");
            this._defaultStyle = httpConnection.getHeaderField(HeaderParser.DEFAULT_STYLE);
         } finally {
            break label65;
         }
      }

      this._browserContent = new HTMLBrowserContent(referer, host, this, this._url, super._renderingApplication, super._renderingOptions, frame, super._flags);
      this._document = (HTMLDocumentImpl)this._browserContent.getDOMDocument();
      this._textUtilities = new TextUtilities(this._browserContent, this._appEventLock, super._renderingOptions);
      if (origEncoding != null) {
         this._textUtilities.setRenderingEncodingHint(origEncoding);
      }

      if (super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 2, false)) {
         this._scriptEngine = JavaScriptRegistry.getInstance();
      }

      if (super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 18, true)) {
         this._processStyleSheets = true;
         this._tagClasses = (Stack)(new Object());
         this._tagIds = (Stack)(new Object());
         this._styleFlagsValues = new long[0];
         this._styleTops = new short[1];
      }

      this._useColor = IS_COLOR
         && super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 3, RenderingOptions.USE_FOREGROUND_BACKGROUND_COLOR_DEFAULT);
      this._coreDocElement = new HTMLBaseGenericElement(4, null, 0);
      this._coreDocElement.setFontFamily(super._renderingOptions.getPropertyWithStringValue(4550690918222697397L, 31, "BBMillbank"));
      this._coreDocElement.setFontHeight(Ui.convertSize(super._renderingOptions.getPropertyWithIntValue(4550690918222697397L, 32, 8), 2, 0));
      this._coreDocElement.setFontStyle(super._renderingOptions.getPropertyWithIntValue(4550690918222697397L, 36, 0));
      this._coreDocElement._foregroundColour = 0;
      this._textUtilities.setFont(this._coreDocElement);
      this._iconUrl = this._browserContent.resolveUrl("/favicon.ico");
   }
}
