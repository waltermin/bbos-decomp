package net.rim.device.apps.internal.browser.wml;

import com.fourthpass.wapstack.wsp.WSPHeaderDecoder;
import java.io.InputStream;
import java.util.Stack;
import java.util.Vector;
import javax.microedition.io.ContentConnection;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.ContentReadEvent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.field.SetHeaderEvent;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Audio;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.MMS;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.util.IntStack;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.idlescreen.IdleScreenManager;
import net.rim.device.apps.internal.browser.api.DeviceDataConversionEvent;
import net.rim.device.apps.internal.browser.page.PageTimer;
import net.rim.device.apps.internal.browser.page.Renderer;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.HeaderParser;
import net.rim.device.apps.internal.browser.stack.WAPInputStream;
import net.rim.device.apps.internal.browser.ui.BrowserBitmapField;
import net.rim.device.apps.internal.browser.ui.BrowserEditField;
import net.rim.device.apps.internal.browser.ui.BrowserPasswordEditField;
import net.rim.device.apps.internal.browser.ui.TableCell;
import net.rim.device.apps.internal.browser.ui.VerticalIndentFieldManager;
import net.rim.device.apps.internal.browser.verbs.EventVerb;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.browser.util.Pipe;
import net.rim.device.internal.browser.util.PipeContext;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.resources.Resource$Internal;

final class WMLRenderer extends Renderer {
   private WAPInputStream _in;
   private String _url;
   private WMLAttributeReader _reader;
   private WMLContextManager _wmlContextManager;
   private IntStack _tagStack;
   private Stack _currentManagerStack;
   private TextSegment _textSegment;
   private Vector _textVariables;
   private WMLBrowserContent _browserContent;
   private WMLBrowserField _currentScreen;
   private String _cardToShow;
   private String _currCardId;
   private boolean _cardLevel;
   private boolean _deckLevel;
   private Vector _currentVerbs;
   private TaskContainer _currentTaskContainer;
   private int _preTableParagraphStyle;
   private boolean _skipElement;
   private boolean _showFirstCard;
   private boolean _autoMatch = true;
   private boolean _multiSelect;
   private WMLMultiSelectOption _currentMultiOption;
   private WMLSingleSelectOption _currentSingleOption;
   private WMLSingleSelectInputField _currentSingleSelect;
   private WMLMultiSelectInputField _currentMultiSelect;
   private WMLTable _currentTable;
   private int _currentTableColumn;
   private WMLTimer _currentTimer;
   private OnEventVerb _templateOnEnterForwardVerb;
   private OnEventVerb _templateOnEnterBackwardVerb;
   private OnEventVerb _templateOnTimerVerb;
   private int _numCards;
   private WMLInputField _lastInputField;
   private boolean _lastElementForcedBr;
   private String _renderingEncoding;
   private ContentReadEvent _contentReadEvent;
   private Font _defaultFont;
   private int _minimumFontSize;
   private int _minimumFontStyle;
   private boolean _lengthAvailable;
   private boolean _minimalMenuMode;
   private boolean _ignoreTimer;
   private DeviceDataConversionEvent _convEvent;
   private static final String EMAIL_FORMAT_IDENTIFIER;
   private static final String NO_BORDER_TABLE_IDENTIFIER;
   private static final String FIRST_CARD_UNIQUE_ID;
   private static final int PROGRESS_UPDATE_TRIGGER;
   private static final int PROGRESS_UPDATE_TRIGGER_BYTES;
   private static final int[] RTF_ALIGNMENT_TRANSLATOR = new int[]{
      262144, 524288, 0, 0, 51, 1963524352, -447731706, 1704354115, 207814912, 1756490177, 1929445492, 1821066026, 712179968, -1975817147, 16806977, -716035469
   };

   private final int mapLiteralAttribute() {
      int token = -1;

      try {
         token = this._in.read();
         if (token == 4) {
            int tableOffset = this._in.readMBInt();
            String tag = this._reader.getStringFromStringTable(tableOffset);
            int map = WMLConstants.getAttributeToken(tag.hashCode());
            if (map == -1) {
               return 4;
            }

            switch (map) {
               case 27:
                  String s = this.readAttributeValue();
                  token = s.equals("get") ? 27 : 28;
                  break;
               case 29:
                  token = this.readAttributeBoolean() ? 30 : 29;
                  break;
               case 31:
                  token = this.readAttributeBoolean() ? 32 : 31;
                  break;
               case 34:
                  token = this.readAttributeBoolean() ? 35 : 34;
                  break;
               case 40:
                  token = this.readAttributeBoolean() ? 41 : 40;
                  break;
               case 47:
                  token = this.readAttributeBoolean() ? 48 : 47;
                  break;
               default:
                  token = map;
            }
         }
      } finally {
         return token;
      }

      return token;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   WMLRenderer(
      InputConnection connection,
      InputStream in,
      String baseURL,
      RenderingSession renderingSession,
      RenderingApplication renderingApplication,
      String referrer,
      WMLContext wmlContext,
      int flags,
      DeviceDataConversionEvent convEvent
   ) {
      super(connection, renderingSession, renderingApplication, referrer, flags);
      this._convEvent = convEvent;
      this._minimumFontSize = super._renderingOptions.getPropertyWithIntValue(4550690918222697397L, 35, 6);
      this._minimumFontStyle = super._renderingOptions.getPropertyWithIntValue(4550690918222697397L, 36, 0);
      boolean var14 = false /* VF: Semaphore variable */;

      label80:
      try {
         var14 = true;
         this._defaultFont = FontFamily.forName(super._renderingOptions.getPropertyWithStringValue(4550690918222697397L, 31, "BBMillbank"))
            .getFont(this._minimumFontStyle, super._renderingOptions.getPropertyWithIntValue(4550690918222697397L, 32, 8), 2);
         var14 = false;
      } finally {
         if (var14) {
            this._defaultFont = Font.getDefault();
            break label80;
         }
      }

      this._minimalMenuMode = super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 39, false);
      this._textSegment = new TextSegment(this._defaultFont, this._minimumFontSize, this._minimumFontStyle, this._minimalMenuMode);
      if (renderingSession.getContext() instanceof WMLContextManager) {
         this._wmlContextManager = (WMLContextManager)renderingSession.getContext();
      } else {
         this._wmlContextManager = new WMLContextManager();
      }

      if (wmlContext != null) {
         this._wmlContextManager.setContext(wmlContext);
         this._wmlContextManager.useSuppliedContext(true);
      } else {
         this._wmlContextManager.useSuppliedContext(false);
      }

      if ((flags & 256) != 0) {
         this._wmlContextManager.newContext();
      }

      this._url = baseURL;
      long dataLength = 0;
      if (super._inputConnection instanceof Object) {
         this._cardToShow = ((HttpConnection)super._inputConnection).getRef();
         dataLength = ((ContentConnection)super._inputConnection).getLength();
      }

      if (this._cardToShow == null || this._cardToShow.length() == 0) {
         this._showFirstCard = true;
      }

      this._in = new WAPInputStream(in);
      this._contentReadEvent = (ContentReadEvent)(new Object(super._inputConnection));
      if (dataLength <= 0) {
         Pipe pipe = this._in.getPipe();
         if (pipe != null && pipe.isClosed()) {
            dataLength = pipe.getLength();
         }
      }

      if (dataLength > 0 && this._in.getPosition() != null) {
         this._lengthAvailable = true;
         this._contentReadEvent.setItemsToRead((int)dataLength);
         this._contentReadEvent.setItemsToReadInBytes(true);
      }

      this._browserContent = new WMLBrowserContent(this, this._url, super._renderingApplication, super._renderingOptions, super._flags);
   }

   @Override
   public final void cleanup() {
      label20:
      try {
         if (this._in != null) {
            this._in.close();
         }
      } finally {
         break label20;
      }

      super.cleanup();
   }

   @Override
   public final void finishProcessingData() {
      this.cleanup();
   }

   @Override
   public final BrowserContent processData() {
      if (this._in == null) {
         throw new Object(BrowserResources.getString(214));
      }

      if (!this._showFirstCard) {
         this._in.save();
      }

      this._numCards = 0;
      this._tagStack = (IntStack)(new Object());
      this._currentManagerStack = (Stack)(new Object());
      this._in.read();
      int publicIdentifierId = this._in.readMBInt();
      if (publicIdentifierId == 0) {
         this._in.readMBInt();
      }

      int charSet = this._in.readMBInt();
      if (charSet == 0) {
         charSet = 106;
      }

      this.setEncoding(WSPHeaderDecoder.getCharsetName(charSet), this._convEvent == null);
      if (super._inputConnection instanceof Object) {
         this.setPostEncoding(
            this._convEvent != null ? this._convEvent.getEncoding() : ((HttpConnection)super._inputConnection).getHeaderField("x-rim-proxy-encoding")
         );
         this._renderingEncoding = ((HttpConnection)super._inputConnection).getHeaderField("x-rim-original-encoding");
      }

      if (super._postEncoding == null) {
         super._postEncoding = super._encoding;
      }

      int stringTableSize = this._in.readMBInt();
      if (stringTableSize < 0) {
         throw new Object(BrowserResources.getString(235));
      }

      byte[] stringTable = new byte[stringTableSize];
      this._in.read(stringTable);
      this._reader = new WMLAttributeReader(stringTable, super._encoding, super._postEncoding);
      int _id = 0;
      int countTrigger = this._lengthAvailable ? 0 : 25;
      PipeContext position = this._in.getPosition();

      while (true) {
         _id = this._in.read();
         if (_id == -1) {
            if (this._browserContent != null && !this._showFirstCard) {
               this._showFirstCard = true;
               this._in = new WAPInputStream(this._in.getSaved());
               return this.processData();
            }

            throw new Object(BrowserResources.getString(548));
         }

         if (super._renderingApplication != null) {
            if (this._lengthAvailable) {
               int numRead = position._numRead;
               if (numRead > countTrigger) {
                  countTrigger = numRead + 512;
                  this._contentReadEvent.setItemsRead(numRead);
                  super._renderingApplication.eventOccurred(this._contentReadEvent);
               }
            } else if (countTrigger-- <= 0) {
               super._renderingApplication.eventOccurred(this._contentReadEvent);
               countTrigger = 25;
            }
         }

         switch ((short)_id) {
            case 0:
               if (this._in.read() != 0) {
                  throw new Object(WMLConstants.STRING_ONLY_PAGE_ZERO_SUPPORTED);
               }
               break;
            case 1:
               if (this._tagStack.isEmpty()) {
                  break;
               }

               int tag = this._tagStack.peek();
               this.processTag((byte)tag, 0, 0, false);
               if (tag != 39 && tag != 63 || !this._showFirstCard && (this._currCardId == null || !this._currCardId.equals(this._cardToShow))) {
                  break;
               }

               if (this._browserContent != null && this._currentScreen != null) {
                  Verb verb = null;
                  switch (super._flags & 7) {
                     case 0:
                        break;
                     case 1:
                     case 3:
                     case 4:
                     case 5:
                     default:
                        verb = this._currentScreen.getOnEnterForward();
                        break;
                     case 2:
                        verb = this._currentScreen.getOnEnterBackward();
                  }

                  if (verb != null) {
                     if (((OnEventVerb)verb).getTask() instanceof Refresh) {
                        ContextObject contextObject = (ContextObject)(new Object(64));
                        Application.getApplication().invokeLater((Runnable)(new Object(null, verb, contextObject, false)));
                        return this._browserContent;
                     }

                     if (super._renderingApplication != null) {
                        super._renderingApplication.eventOccurred((Event)(new Object(super._inputConnection, this._url, 1)));
                     }

                     ContextObject contextObject = (ContextObject)(new Object(64));
                     Application.getApplication().invokeLater((Runnable)(new Object(null, verb, contextObject, false)));
                     return null;
                  }

                  return this._browserContent;
               }

               throw new Object(BrowserResources.getString(548));
            case 2:
               char c = (char)this._in.readMBInt();
               if (this._textSegment != null) {
                  this._textSegment.renderText(String.valueOf(c));
               }
               break;
            case 3:
               String strx = this._in.readInlineString(super._encoding);
               if (this._textSegment != null) {
                  this._textSegment.renderText(strx);
               }
               break;
            case 4:
            case 68:
            case 132:
            case 196:
               int tableOffset = this._in.readMBInt();
               int isContent = 0;
               int isAttr = 0;
               if (68 == (short)_id || 196 == (short)_id) {
                  isContent = 1;
               }

               if (132 == (short)_id || 196 == (short)_id) {
                  isAttr = 1;
               }

               String tag = this._reader.getStringFromStringTable(tableOffset);
               int tagValue = WMLConstants.getTagToken(tag.hashCode());
               int tagToProcess = _id;
               if (tagValue != -1) {
                  tagToProcess = tagValue;
               }

               this.processTag((byte)tagToProcess, isAttr, isContent, true);
               if (this._skipElement) {
                  this.skipElement(tagToProcess);
                  if (tagToProcess == 39) {
                     this._in.save();
                  }

                  this._skipElement = false;
               }
               break;
            case 64:
               String var31 = this._in.readInlineString(super._encoding);
               WMLVariable var43 = new WMLVariable(var31, this._wmlContextManager, "Esc", super._postEncoding);
               if (this._textSegment != null) {
                  this._textSegment.addVariable(var43);
               }
               break;
            case 65:
               String var30 = this._in.readInlineString(super._encoding);
               WMLVariable variablex = new WMLVariable(var30, this._wmlContextManager, "UnEsc", super._postEncoding);
               if (this._textSegment != null) {
                  this._textSegment.addVariable(variablex);
               }
               break;
            case 66:
               String var29 = this._in.readInlineString(super._encoding);
               WMLVariable variablexx = new WMLVariable(var29, this._wmlContextManager, "NoEsc", super._postEncoding);
               if (this._textSegment != null) {
                  this._textSegment.addVariable(variablexx);
               }
               break;
            case 67:
               int next = -1;

               while (next != 1) {
                  next = this.mapLiteralAttribute();
                  if (next == -1) {
                     break;
                  }
               }
               break;
            case 128:
               int var38 = this._in.readMBInt();
               String var28 = this._reader.getStringFromStringTable(var38);
               WMLVariable var40 = new WMLVariable(var28, this._wmlContextManager, "Esc", super._postEncoding);
               if (this._textSegment != null) {
                  this._textSegment.addVariable(var40);
               }
               break;
            case 129:
               int var37 = this._in.readMBInt();
               String var27 = this._reader.getStringFromStringTable(var37);
               WMLVariable var39 = new WMLVariable(var27, this._wmlContextManager, "UnEsc", super._postEncoding);
               if (this._textSegment != null) {
                  this._textSegment.addVariable(var39);
               }
               break;
            case 130:
               int var36 = this._in.readMBInt();
               String var26 = this._reader.getStringFromStringTable(var36);
               WMLVariable variable = new WMLVariable(var26, this._wmlContextManager, "NoEsc", super._postEncoding);
               if (this._textSegment != null) {
                  this._textSegment.addVariable(variable);
               }
               break;
            case 131:
               int offset = this._in.readMBInt();
               String str = this._reader.getStringFromStringTable(offset);
               if (this._textSegment != null) {
                  this._textSegment.renderText(str);
               }
               break;
            case 192:
            case 193:
            case 194:
               throw new Object(WMLConstants.STRING_RESERVED_EXT_BYTE);
            case 195:
               int len = this._in.readMBInt();
               int i = 0;
               if (i < len && this._in.available() > 0) {
               }
            default:
               int tagId = _id & 63;
               int attr = (_id & 128) >> 7;
               int content = (_id & 64) >> 6;
               this.processTag((byte)tagId, attr, content, true);
               if (this._skipElement) {
                  this.skipElement(tagId);
                  if (tagId == 39) {
                     this._in.save();
                  }

                  this._skipElement = false;
               }
         }
      }
   }

   final long getCurrentParagraphStyle() {
      long style = 0;
      int paragraphStyle = this._textSegment.getParagraphStyle();
      switch (paragraphStyle) {
         case 0:
            style = 4294967296L;
         default:
            return style;
         case 262144:
            return 12884901888L;
         case 524288:
            return 8589934592L;
      }
   }

   private final void removeDoVerb(String name) {
      if (this._cardLevel && this._currentVerbs != null) {
         for (int i = 0; i < this._currentVerbs.size(); i++) {
            if (((DoVerb)this._currentVerbs.elementAt(i)).getName().equals(name) && ((DoVerb)this._currentVerbs.elementAt(i)).isTemplateLevel()) {
               this._currentVerbs.removeElementAt(i);
            }
         }
      }
   }

   private final void removeOnEvent(String type) {
      if (type != null) {
         if (this._cardLevel && this._browserContent != null) {
            if (type.equals(WMLConstants.STRING_ON_TIMER)) {
               this._browserContent.removeOnTimer();
               return;
            }

            if (type.equals(WMLConstants.STRING_ON_ENTER_F)) {
               this._currentScreen.setOnEnterForward(null);
               return;
            }

            if (type.equals(WMLConstants.STRING_ON_ENTER_B)) {
               this._currentScreen.setOnEnterBackward(null);
            }
         }
      }
   }

   final void addField(Field f, int indent) {
      Manager topManager = (Manager)this._currentManagerStack.peek();
      if (!(topManager instanceof WMLBrowserField)) {
         if (topManager instanceof VerticalIndentFieldManager) {
            ((VerticalIndentFieldManager)topManager).add(f, indent);
         }
      } else {
         ((WMLBrowserField)topManager).add(f, indent);
      }
   }

   private final void pushDoVerb(DoVerb doVerb) {
      if (this._deckLevel) {
         this._currentVerbs.addElement(doVerb);
      } else {
         if (this._cardLevel) {
            boolean shadowed = false;

            for (int i = 0; i < this._currentVerbs.size(); i++) {
               DoVerb verb = (DoVerb)this._currentVerbs.elementAt(i);
               if (verb.getName().equals(doVerb.getName()) && verb.isTemplateLevel()) {
                  this._currentVerbs.removeElementAt(i);

                  for (int j = i; j < this._currentVerbs.size(); j++) {
                     if (((DoVerb)this._currentVerbs.elementAt(j)).getName().equals(doVerb.getName())
                        && ((DoVerb)this._currentVerbs.elementAt(j)).isTemplateLevel()) {
                        this._currentVerbs.removeElementAt(j);
                        j--;
                     }
                  }

                  this._currentVerbs.insertElementAt(doVerb, i);
                  shadowed = true;
               }
            }

            if (!shadowed) {
               this._currentVerbs.addElement(doVerb);
            }
         }
      }
   }

   private final void pushOnEventVerb(OnEventVerb onEventVerb) {
      String type = onEventVerb.getType();
      if (type != null) {
         if (this._deckLevel && this._tagStack.search(59) == 2) {
            if (type.equals(WMLConstants.STRING_ON_TIMER)) {
               this._templateOnTimerVerb = onEventVerb;
               return;
            }

            if (type.equals(WMLConstants.STRING_ON_ENTER_F)) {
               this._templateOnEnterForwardVerb = onEventVerb;
               return;
            }

            if (type.equals(WMLConstants.STRING_ON_ENTER_B)) {
               this._templateOnEnterBackwardVerb = onEventVerb;
               return;
            }
         } else if (this._cardLevel) {
            if (this._tagStack.search(39) == 2) {
               if (type.equals(WMLConstants.STRING_ON_TIMER)) {
                  this._browserContent.setOnTimer(onEventVerb);
                  return;
               }

               if (type.equals(WMLConstants.STRING_ON_ENTER_F)) {
                  this._currentScreen.setOnEnterForward(onEventVerb);
                  return;
               }

               if (type.equals(WMLConstants.STRING_ON_ENTER_B)) {
                  this._currentScreen.setOnEnterBackward(onEventVerb);
                  return;
               }
            } else if (type.equals(WMLConstants.STRING_ON_PICK)) {
               if (this._multiSelect && this._currentMultiOption != null) {
                  this._currentMultiOption.addOnPick(onEventVerb);
                  return;
               }

               if (!this._multiSelect && this._currentSingleOption != null) {
                  this._currentSingleOption.addOnPick(onEventVerb);
               }
            }
         }
      }
   }

   private final void setCardId(String cardId) {
      this._currCardId = cardId;
      this._currentScreen.setCurrentCardId(cardId);
      if (this._cardToShow == null || this._cardToShow.length() == 0) {
         this._cardToShow = cardId;
      }
   }

   private final String readAttributeValue() {
      try {
         return this._reader.read(this._in, this._wmlContextManager);
      } finally {
         ;
      }
   }

   private final boolean readAttributeBoolean() {
      return this.readAttributeValue().equals("true");
   }

   private final void skipAttributeValue() {
      try {
         this._reader.skip(this._in);
      } finally {
         return;
      }
   }

   private final void processTag(byte tag, int attr, int content, boolean isStartTag) {
      if (isStartTag && content == 1) {
         this._tagStack.push(tag);
      }

      switch (tag) {
         case 26:
         case 58:
            break;
         case 27:
            this.processElementPre(attr, content, isStartTag);
            break;
         case 28:
         default:
            this.processElementA(attr, content, isStartTag);
            break;
         case 29:
            this.processElementTd(attr, content, isStartTag);
            break;
         case 30:
            this.processElementTr(attr, content, isStartTag);
            break;
         case 31:
            this.processElementTable(attr, content, isStartTag);
            break;
         case 32:
            this.processElementP(attr, content, isStartTag);
            break;
         case 33:
            this.processElementPostField(attr, content, isStartTag);
            break;
         case 34:
            this.processElementAnchor(attr, content, isStartTag);
            break;
         case 35:
            this.processElementAccess(attr, content, isStartTag);
            break;
         case 36:
            this.processElementB(attr, content, isStartTag);
            break;
         case 37:
            this.processElementBig(attr, content, isStartTag);
            break;
         case 38:
            this.processElementBr(attr, content, isStartTag);
            break;
         case 39:
            this.processElementCard(attr, content, isStartTag);
            break;
         case 40:
            this.processElementDo(attr, content, isStartTag);
            break;
         case 41:
            this.processElementEm(attr, content, isStartTag);
            break;
         case 42:
            this.processElementFieldSet(attr, content, isStartTag);
            break;
         case 43:
            this.processElementGo(attr, content, isStartTag);
            break;
         case 44:
            this.processElementHead(attr, content, isStartTag);
            break;
         case 45:
            this.processElementI(attr, content, isStartTag);
            break;
         case 46:
            this.processElementImg(attr, content, isStartTag);
            break;
         case 47:
            this.processElementInput(attr, content, isStartTag);
            break;
         case 48:
            this.processElementMeta(attr, content, isStartTag);
            break;
         case 49:
            this.processElementNoop(attr, content, isStartTag);
            break;
         case 50:
            this.processElementPrev(attr, content, isStartTag);
            break;
         case 51:
            this.processElementOnEvent(attr, content, isStartTag);
            break;
         case 52:
            this.processElementOptGroup(attr, content, isStartTag);
            break;
         case 53:
            this.processElementOption(attr, content, isStartTag);
            break;
         case 54:
            this.processElementRefresh(attr, content, isStartTag);
            break;
         case 55:
            this.processElementSelect(attr, content, isStartTag);
            break;
         case 56:
            this.processElementSmall(attr, content, isStartTag);
            break;
         case 57:
            this.processElementStrong(attr, content, isStartTag);
            break;
         case 59:
            this.processElementTemplate(attr, content, isStartTag);
            break;
         case 60:
            this.processElementTimer(attr, content, isStartTag);
            break;
         case 61:
            this.processElementU(attr, content, isStartTag);
            break;
         case 62:
            this.processElementSetVar(attr, content, isStartTag);
            break;
         case 63:
            this.processElementWml(attr, content, isStartTag);
      }

      if (!isStartTag) {
         this._tagStack.pop();
      }

      if (tag != 47 && tag != 46) {
         this._lastElementForcedBr = false;
      } else {
         this._lastElementForcedBr = true;
      }
   }

   private final void processElementA(int attr, int content, boolean isStartTag) {
      if (!isStartTag) {
         this._textSegment.anchorOff();
         this._lastInputField = null;
      } else {
         this._textSegment.anchorOn();
         int next = -1;
         Go go = null;
         WMLVariable composedLink = null;

         while (next != 1) {
            if (attr == 0) {
               return;
            }

            next = this.mapLiteralAttribute();
            if (next == -1) {
               return;
            }

            switch (next) {
               case 54:
                  byte[] bytes = this._reader.getEncodedVariableName(this._in);
                  WMLVariable title = new WMLVariable(bytes, this._reader, this._wmlContextManager);
                  this._textSegment.setAnchorTitle(title, false);
                  break;
               case 74:
                  go = new Go(this._browserContent, super._renderingApplication, this._wmlContextManager, null);
                  composedLink = new WMLVariable(this._reader.getEncodedVariableName(this._in), this._reader, this._wmlContextManager);
                  go.setHref(composedLink);
                  this._textSegment.setTask(go);
                  composedLink = null;
                  go = null;
                  break;
               case 75:
                  go = new Go(this._browserContent, super._renderingApplication, this._wmlContextManager, null);
                  composedLink = new WMLVariable(this._reader.getEncodedVariableName(this._in), this._reader, this._wmlContextManager);
                  composedLink.setPrefix(WMLConstants.STRING_HTTP);
                  go.setHref(composedLink);
                  this._textSegment.setTask(go);
                  composedLink = null;
                  go = null;
                  break;
               case 76:
                  go = new Go(this._browserContent, super._renderingApplication, this._wmlContextManager, null);
                  composedLink = new WMLVariable(this._reader.getEncodedVariableName(this._in), this._reader, this._wmlContextManager);
                  composedLink.setPrefix(WMLConstants.STRING_HTTPS);
                  go.setHref(composedLink);
                  this._textSegment.setTask(go);
                  composedLink = null;
                  go = null;
                  break;
               case 84:
               case 85:
                  this.readAttributeValue();
                  break;
               case 94:
                  String accesskey = this.readAttributeValue();
                  if (accesskey.length() == 1) {
                     this._textSegment.setAnchorAccess(accesskey.charAt(0));
                  }
            }
         }
      }
   }

   private final void processElementAnchor(int attr, int content, boolean isStartTag) {
      if (!isStartTag) {
         this._textSegment.anchorOff();
         this._currentTaskContainer = null;
         this._lastInputField = null;
      } else {
         this._textSegment.anchorOn();
         int next = -1;

         while (next != 1 && attr != 0) {
            next = this.mapLiteralAttribute();
            if (next == -1) {
               break;
            }

            switch (next) {
               case 54:
                  byte[] bytes = this._reader.getEncodedVariableName(this._in);
                  WMLVariable title = new WMLVariable(bytes, this._reader, this._wmlContextManager);
                  this._textSegment.setAnchorTitle(title, false);
                  break;
               case 84:
               case 85:
                  this.readAttributeValue();
                  break;
               case 94:
                  String accesskey = this.readAttributeValue();
                  if (accesskey.length() == 1) {
                     this._textSegment.setAnchorAccess(accesskey.charAt(0));
                  }
            }
         }

         this._currentTaskContainer = this._textSegment;
      }
   }

   private final void processElementAccess(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         if (attr == 0) {
            return;
         }

         int next = -1;
         String path = "/";
         String domain;
         String referringDomain;
         String referringPath;
         if (super._referrer == null) {
            referringDomain = "no domain";
            referringPath = "no path";
            domain = referringDomain;
         } else {
            referringDomain = super._referrer;
            referringPath = super._referrer;
            domain = this.getDomainFromUrl(referringDomain);
         }

         boolean domainMatches = false;
         boolean pathMatches = false;

         while (next != 1) {
            next = this.mapLiteralAttribute();
            if (next == -1) {
               break;
            }

            switch (next) {
               case 15:
                  domain = this.readAttributeValue();
                  break;
               case 42:
                  path = this.readAttributeValue();
                  break;
               case 84:
               case 85:
                  this.readAttributeValue();
            }
         }

         if (referringDomain.indexOf(58) > 0) {
            referringDomain = this.getDomainFromUrl(referringDomain);
            if (referringDomain.endsWith(domain) && (referringDomain.equals(domain) || referringDomain.charAt(referringDomain.indexOf(domain) - 1) == '.')) {
               domainMatches = true;
            }
         }

         if (referringPath.indexOf(58) > 0) {
            if (path.length() == 0) {
               path = "/";
            }

            if (path.equals("/") && domainMatches) {
               pathMatches = true;
            } else {
               if (path.charAt(0) != '/') {
                  path = ((StringBuffer)(new Object())).append(this.getPathFromUrl(this._url)).append('/').append(path).toString();
               }

               referringPath = this.getPathFromUrl(referringPath);
               if (referringPath.startsWith(path) && (referringPath.equals(path) || referringPath.charAt(path.length()) == '/')) {
                  pathMatches = true;
               }
            }
         }

         if (!domainMatches || !pathMatches) {
            StringBuffer denialBuff = (StringBuffer)(new Object(BrowserResources.getString(555)));
            if (!pathMatches) {
               denialBuff.append(BrowserResources.getString(556));
               denialBuff.append(referringPath);
            }

            if (!domainMatches) {
               denialBuff.append(BrowserResources.getString(557));
               denialBuff.append(referringDomain);
            }

            throw new Object(denialBuff.toString());
         }
      }
   }

   private final String getDomainFromUrl(String Url) {
      String tmpString = Url.substring(Url.indexOf(58) + 3);
      if (tmpString.indexOf(47) > 0) {
         tmpString = tmpString.substring(0, tmpString.indexOf(47));
      }

      return tmpString;
   }

   private final String getPathFromUrl(String Url) {
      String tmpString = Url.substring(Url.indexOf(58) + 3);
      return tmpString.indexOf(47) > 0 ? tmpString.substring(tmpString.indexOf(47), tmpString.lastIndexOf(47)) : "";
   }

   private final void processElementB(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         this._textSegment.boldOn();
         if (attr != 0) {
            this.skipAllAttributes();
            return;
         }
      } else {
         this._textSegment.boldOff();
      }
   }

   private final void processElementBig(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         this._textSegment.increaseHeight();
         if (attr != 0) {
            this.skipAllAttributes();
            return;
         }
      } else {
         this._textSegment.decreaseHeight();
      }
   }

   private final void processElementBr(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         if (!this._lastElementForcedBr) {
            this._textSegment.appendNewLine();
         }

         if (attr != 0) {
            this.skipAllAttributes();
         }
      }
   }

   private final void processElementCard(int attr, int content, boolean isStartTag) {
      if (!isStartTag) {
         this._currentScreen.setTextVariables(this._textVariables.size() == 0 ? null : this._textVariables);
         if (this._currentVerbs != null) {
            for (int j = 0; j < this._currentVerbs.size(); j++) {
               DoVerb doVerb = (DoVerb)this._currentVerbs.elementAt(j);
               doVerb.setBrowserContent(this._browserContent);
               doVerb.setCardId(this._currCardId);
               boolean prevTask = doVerb.getTask() instanceof Prev;
               if ((StringUtilities.strEqualIgnoreCase(doVerb.getType(), "prev", 1701707776) || prevTask) && super._renderingApplication != null) {
                  int position = super._renderingApplication.getHistoryPosition(this._browserContent);
                  if (position < 1 && prevTask) {
                     this._currentVerbs.removeElementAt(j);
                     j--;
                  } else {
                     this._browserContent.setPrevVerb(doVerb);
                  }
               }
            }

            if (this._lastInputField != null) {
               int length = this._currentVerbs.size();
               Verb autoVerb = null;

               for (int z = 0; z < length; z++) {
                  DoVerb verb = (DoVerb)this._currentVerbs.elementAt(z);
                  if (verb.getType().equals(WMLConstants.STRING_ACCEPT)) {
                     autoVerb = verb;
                     break;
                  }
               }

               if (autoVerb != null) {
                  this._lastInputField.setAutoExecVerb(autoVerb);
               }
            }

            int length = this._currentVerbs.size();

            for (int z = 0; z < length; z++) {
               DoVerb verb = (DoVerb)this._currentVerbs.elementAt(z);
               String name = verb.getLabel();
               if (name == null || name.trim().length() == 0) {
                  this._currentVerbs.removeElementAt(z);
                  length--;
                  z--;
               }
            }
         }

         this._browserContent.setVerbs(this._currentVerbs);
         if (!this._currentManagerStack.isEmpty()) {
            this._currentManagerStack.pop();
         }

         this._cardLevel = false;
      } else {
         this._cardLevel = true;
         this._numCards++;
         boolean idSet = false;
         boolean newContext = false;
         this._currentScreen = new WMLBrowserField(this._browserContent, this._wmlContextManager, this._url, this);
         this._browserContent.setContent(this._currentScreen);
         if (this._currentVerbs == null) {
            this._currentVerbs = (Vector)(new Object());
         }

         if (this._templateOnEnterForwardVerb != null) {
            OnEventVerb newVerb = (OnEventVerb)this._templateOnEnterForwardVerb.clone();
            newVerb.setBrowserContent(this._browserContent);
            this._currentScreen.setOnEnterForward(newVerb);
         }

         if (this._templateOnEnterBackwardVerb != null) {
            OnEventVerb newVerb = (OnEventVerb)this._templateOnEnterBackwardVerb.clone();
            newVerb.setBrowserContent(this._browserContent);
            this._currentScreen.setOnEnterBackward(newVerb);
         }

         if (this._templateOnTimerVerb != null) {
            OnEventVerb newVerb = (OnEventVerb)this._templateOnTimerVerb.clone();
            newVerb.setBrowserContent(this._browserContent);
            this._browserContent.setOnTimer(newVerb);
         }

         this._textVariables = (Vector)(new Object());
         int next = -1;
         String cardId = null;
         WMLVariable title = null;

         while (next != 1 && attr != 0) {
            next = this.mapLiteralAttribute();
            if (next == -1) {
               break;
            }

            switch (next) {
               case 34:
                  newContext = false;
                  break;
               case 35:
                  newContext = true;
                  break;
               case 37: {
                  byte[] value = this._reader.getEncodedVariableName(this._in);
                  Go go = new Go(this._browserContent, super._renderingApplication, this._wmlContextManager, null);
                  go.setHref(new WMLVariable(value, this._reader, this._wmlContextManager));
                  this._currentScreen.setOnEnterBackward(new OnEventVerb(WMLConstants.STRING_ON_ENTER_B, go));
                  break;
               }
               case 38: {
                  byte[] value = this._reader.getEncodedVariableName(this._in);
                  Go go = new Go(this._browserContent, super._renderingApplication, this._wmlContextManager, null);
                  go.setHref(new WMLVariable(value, this._reader, this._wmlContextManager));
                  this._currentScreen.setOnEnterForward(new OnEventVerb(WMLConstants.STRING_ON_ENTER_F, go));
                  break;
               }
               case 39: {
                  byte[] value = this._reader.getEncodedVariableName(this._in);
                  Go go = new Go(this._browserContent, super._renderingApplication, this._wmlContextManager, null);
                  go.setHref(new WMLVariable(value, this._reader, this._wmlContextManager));
                  this._browserContent.setOnTimer(new OnEventVerb(WMLConstants.STRING_ON_TIMER, go));
                  break;
               }
               case 54: {
                  byte[] value = this._reader.getEncodedVariableName(this._in);
                  title = new WMLVariable(value, this._reader, this._wmlContextManager);
                  break;
               }
               case 84:
                  this.readAttributeValue();
                  break;
               case 85:
                  idSet = true;
                  cardId = this.readAttributeValue();
                  this.setCardId(cardId);
            }
         }

         this._currentScreen.setTitle(title);
         if (!idSet && this._numCards == 1) {
            this.setCardId("UD123456789");
         }

         if (!this._showFirstCard && this._cardToShow != null && !this._currCardId.equals(this._cardToShow)) {
            this._skipElement = true;
         } else {
            if (this._currCardId.equals(this._cardToShow) && newContext && (super._flags & 1) != 0) {
               this._wmlContextManager.newContext();
            }

            this._currentManagerStack.push(this._currentScreen);
            this._textSegment = new TextSegment(
               this._defaultFont,
               this._currentManagerStack,
               this._textVariables,
               this._renderingEncoding,
               this._autoMatch,
               this._minimumFontSize,
               this._minimumFontStyle,
               this._minimalMenuMode
            );
            this._textSegment.setBrowserContent(this._browserContent);
         }
      }
   }

   private final void processElementDo(int attr, int content, boolean isStartTag) {
      if (!isStartTag) {
         if (this._currentTaskContainer != null) {
            this.pushDoVerb((DoVerb)this._currentTaskContainer);
            this._currentTaskContainer = null;
         }
      } else {
         DoVerb doVerb = new DoVerb(this._currCardId, this._browserContent, !this._cardLevel);
         int next = -1;

         while (next != 1 && attr != 0) {
            next = this.mapLiteralAttribute();
            if (next == -1) {
               break;
            }

            String tmpStr = null;
            switch (next) {
               case 24:
                  byte[] value = this._reader.getEncodedVariableName(this._in);
                  doVerb.setLabel(new WMLVariable(value, this._reader, this._wmlContextManager));
                  break;
               case 33:
                  doVerb.setName(this.readAttributeValue());
                  break;
               case 40:
                  doVerb.setOptional(false);
                  break;
               case 41:
                  doVerb.setOptional(true);
                  break;
               case 55:
                  doVerb.setType(this.readAttributeValue());
                  break;
               case 56:
                  tmpStr = this.readAttributeValue();
                  if (tmpStr.length() == 0) {
                     doVerb.setType(WMLConstants.STRING_ACCEPT);
                  } else {
                     doVerb.setType(((StringBuffer)(new Object())).append(WMLConstants.STRING_ACCEPT).append(tmpStr).toString());
                  }
                  break;
               case 57:
                  tmpStr = this.readAttributeValue();
                  if (tmpStr.length() == 0) {
                     doVerb.setType(WMLConstants.STRING_DELETE);
                  } else {
                     doVerb.setType(((StringBuffer)(new Object())).append(WMLConstants.STRING_DELETE).append(tmpStr).toString());
                  }
                  break;
               case 58:
                  tmpStr = this.readAttributeValue();
                  if (tmpStr.length() == 0) {
                     doVerb.setType(WMLConstants.STRING_HELP);
                  } else {
                     doVerb.setType(((StringBuffer)(new Object())).append(WMLConstants.STRING_HELP).append(tmpStr).toString());
                  }
                  break;
               case 69:
                  tmpStr = this.readAttributeValue();
                  if (tmpStr.length() == 0) {
                     doVerb.setType(WMLConstants.STRING_OPTIONS);
                  } else {
                     doVerb.setType(((StringBuffer)(new Object())).append(WMLConstants.STRING_OPTIONS).append(tmpStr).toString());
                  }
                  break;
               case 70:
                  tmpStr = this.readAttributeValue();
                  if (tmpStr.length() == 0) {
                     doVerb.setType(WMLConstants.STRING_PREV);
                  } else {
                     doVerb.setType(((StringBuffer)(new Object())).append(WMLConstants.STRING_PREV).append(tmpStr).toString());
                  }
                  break;
               case 71:
                  tmpStr = this.readAttributeValue();
                  if (tmpStr.length() == 0) {
                     doVerb.setType(WMLConstants.STRING_RESET);
                  } else {
                     doVerb.setType(((StringBuffer)(new Object())).append(WMLConstants.STRING_RESET).append(tmpStr).toString());
                  }
                  break;
               case 84:
               case 85:
                  this.readAttributeValue();
            }
         }

         this._currentTaskContainer = doVerb;
      }
   }

   private final void processElementEm(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         this._textSegment.italicOn();
         if (attr != 0) {
            this.skipAllAttributes();
            return;
         }
      } else {
         this._textSegment.italicOff();
      }
   }

   private final void processElementFieldSet(int attr, int content, boolean isStartTag) {
      if (isStartTag && attr != 0) {
         this.skipAllAttributes();
      }
   }

   private final void processElementGo(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         Go go = new Go(this._browserContent, super._renderingApplication, this._wmlContextManager, super._inputConnection);
         int next = -1;

         while (next != 1 && attr != 0) {
            next = this.mapLiteralAttribute();
            if (next == -1) {
               break;
            }

            byte[] value = null;
            switch (next) {
               case 5:
                  String charset = this.readAttributeValue();
                  if (StringUtilities.toLowerCase(charset, 1701707776).indexOf("utf-8") != -1) {
                     charset = "utf-8";
                     go.setAcceptCharset(charset);
                  }
                  break;
               case 27:
                  go.setMethod(WMLConstants.STRING_GET);
                  break;
               case 28:
                  go.setMethod(WMLConstants.STRING_POST);
                  break;
               case 47:
                  go.setSendReferrer(false);
                  break;
               case 48:
                  go.setSendReferrer(true);
                  break;
               case 74:
                  value = this._reader.getEncodedVariableName(this._in);
                  go.setHref(new WMLVariable(value, this._reader, this._wmlContextManager));
                  break;
               case 75: {
                  value = this._reader.getEncodedVariableName(this._in);
                  WMLVariable composedLink = new WMLVariable(value, this._reader, this._wmlContextManager);
                  composedLink.setPrefix(WMLConstants.STRING_HTTP);
                  go.setHref(composedLink);
                  break;
               }
               case 76: {
                  value = this._reader.getEncodedVariableName(this._in);
                  WMLVariable composedLink = new WMLVariable(value, this._reader, this._wmlContextManager);
                  composedLink.setPrefix(WMLConstants.STRING_HTTPS);
                  go.setHref(composedLink);
                  break;
               }
               case 84:
               case 85:
                  this.readAttributeValue();
                  break;
               case 95:
                  go.setEnctype(this.readAttributeValue());
                  break;
               case 96:
                  go.setEnctype(0);
                  break;
               case 97:
                  go.setEnctype(1);
                  break;
               case 100:
                  go.setCacheControl(HeaderParser.CACHE_DIRECTIVE_NO_CACHE);
            }
         }

         if (go.getAcceptCharset() == null) {
            go.setAcceptCharset(super._postEncoding);
         }

         if (this._currentTaskContainer == null) {
            throw new Object(BrowserResources.getString(558));
         }

         this._currentTaskContainer.setTask(go);
      }
   }

   private final void processElementHead(int attr, int content, boolean isStartTag) {
      if (isStartTag && attr != 0) {
         this.skipAllAttributes();
      }
   }

   private final void processElementI(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         this._textSegment.italicOn();
         if (attr != 0) {
            this.skipAllAttributes();
            return;
         }
      } else {
         this._textSegment.italicOff();
      }
   }

   private final void processElementImg(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         int next = -1;
         String localSrc = null;
         String src = null;
         WMLVariable alt = null;
         WMLSecondaryURLNode node = new WMLSecondaryURLNode();

         while (next != 1 && attr != 0) {
            next = this.mapLiteralAttribute();
            if (next == -1) {
               break;
            }

            switch (next) {
               case 12:
                  alt = new WMLVariable(this._reader.getEncodedVariableName(this._in), this._reader, this._wmlContextManager);
                  break;
               case 19:
                  String heightStr = this.readAttributeValue();

                  try {
                     node.setHeight(Integer.parseInt(heightStr));
                     break;
                  } finally {
                     break;
                  }
               case 20:
                  String hSpaceStr = this.readAttributeValue();

                  try {
                     if (hSpaceStr.charAt(hSpaceStr.length() - 1) == '%') {
                        Integer.parseInt(hSpaceStr.substring(0, hSpaceStr.length() - 1));
                     } else {
                        node.setHspace(Integer.parseInt(hSpaceStr));
                     }
                     break;
                  } finally {
                     break;
                  }
               case 25:
                  localSrc = this.readAttributeValue();
                  break;
               case 50:
                  src = this.readAttributeValue();
                  break;
               case 78:
                  String vSpaceStr = this.readAttributeValue();

                  try {
                     if (vSpaceStr.charAt(vSpaceStr.length() - 1) == '%') {
                        Integer.parseInt(vSpaceStr.substring(0, vSpaceStr.length() - 1));
                     } else {
                        node.setVspace(Integer.parseInt(vSpaceStr));
                     }
                     break;
                  } finally {
                     break;
                  }
               case 79:
                  String widthStr = this.readAttributeValue();

                  try {
                     node.setWidth(Integer.parseInt(widthStr));
                     break;
                  } finally {
                     break;
                  }
               case 82:
                  this.readAttributeValue();
                  break;
               case 84:
               case 85:
                  this.readAttributeValue();
                  break;
               case 88:
                  src = ((StringBuffer)(new Object())).append(WMLConstants.STRING_HTTP).append(this.readAttributeValue()).toString();
                  break;
               case 89:
                  src = ((StringBuffer)(new Object())).append(WMLConstants.STRING_HTTPS).append(this.readAttributeValue()).toString();
            }
         }

         if (localSrc == null && src == null) {
            return;
         }

         if (src != null && src.length() > 0 && this._browserContent != null) {
            node.setSrc(this._browserContent.resolveUrl(src));
         }

         node.setCookie(this._textSegment.getCurrentAnchorVerb());
         node.setStyle(this.getCurrentParagraphStyle());
         if (alt == null) {
            String defaultAltTextString = node.getCookie() != null ? BrowserResources.getString(609) : BrowserResources.getString(358);
            byte[] defaultAltTextBytes = defaultAltTextString.getBytes();
            byte[] realDefaultAltTextBytes = new byte[defaultAltTextBytes.length + 2];
            realDefaultAltTextBytes[0] = 3;
            System.arraycopy(defaultAltTextBytes, 0, realDefaultAltTextBytes, 1, defaultAltTextBytes.length);
            realDefaultAltTextBytes[realDefaultAltTextBytes.length - 1] = 0;
            alt = new WMLVariable(realDefaultAltTextBytes, this._reader, this._wmlContextManager);
         }

         EncodedImage image = null;
         if (super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 6, true)) {
            if (localSrc != null && localSrc.startsWith("pict://")) {
               image = BrowserResources.getPictogramImage(localSrc);
            }

            src = node.getSrc();
            if (image == null && src != null && src.length() > 0) {
               image = this.findEncodedImageInCache(src, super._flags & 0xFF, node, this._browserContent);
            }
         }

         Field field = null;
         this._textSegment.flush(false);
         if (image == null) {
            this._textSegment.renderText(" [");
            this._textSegment.addVariable(alt);
            this._textSegment.renderText("] ");
            field = this._textSegment.createTextField(false);
            if (node.getSrc() != null) {
               node.setUIField(field);
               this._browserContent.addSecondaryURL(node.getSrc(), node, false);
            }
         } else {
            BrowserBitmapField bitmapField = node.getCookie() != null
               ? new WMLAnchoredBitmapField(this._browserContent, null, node.getSrc(), node.getStyle(), (WMLAnchorVerb)node.getCookie())
               : new BrowserBitmapField(this._browserContent, null, node.getSrc(), node.getStyle());
            bitmapField.setLimitVHSpace(false);
            bitmapField.setImage(image, node.getHspace(), node.getVspace(), node.getWidth(), node.getHeight());
            field = bitmapField;
         }

         Manager manager = (Manager)this._currentManagerStack.peek();
         manager.add(field);
      }
   }

   private final void processElementInput(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         int next = -1;
         this._textSegment.flush(false);
         String name = "";
         WMLVariable value = null;
         WMLVariable title = null;
         String format = "*M";
         boolean emptyOkPresent = false;
         boolean emptyOk = true;
         int maxLength = -1;
         int size = -1;
         int tabIndex = 0;
         byte flags = 0;

         while (next != 1 && attr != 0) {
            next = this.mapLiteralAttribute();
            if (next == -1) {
               break;
            }

            switch (next) {
               case 16:
                  emptyOk = false;
                  emptyOkPresent = true;
                  break;
               case 17:
                  emptyOk = true;
                  emptyOkPresent = true;
                  break;
               case 18:
                  format = this.readAttributeValue();
                  break;
               case 26:
                  try {
                     maxLength = Integer.parseInt(this.readAttributeValue());
                     maxLength = maxLength > 0 ? maxLength : -1;
                     break;
                  } finally {
                     break;
                  }
               case 33:
                  name = this.readAttributeValue();
                  break;
               case 49:
                  try {
                     size = Integer.parseInt(this.readAttributeValue());
                     break;
                  } finally {
                     break;
                  }
               case 53:
                  try {
                     tabIndex = Integer.parseInt(this.readAttributeValue());
                     tabIndex = tabIndex > -1 ? tabIndex : 0;
                     break;
                  } finally {
                     break;
                  }
               case 54:
                  byte[] var34 = this._reader.getEncodedVariableName(this._in);
                  title = new WMLVariable(var34, this._reader, this._wmlContextManager);
                  break;
               case 59:
                  flags = (byte)(flags | 1);
                  break;
               case 77:
                  byte[] bytes = this._reader.getEncodedVariableName(this._in);
                  value = new WMLVariable(bytes, this._reader, this._wmlContextManager);
                  break;
               case 84:
               case 85:
                  this.readAttributeValue();
                  break;
               case 94:
                  this.readAttributeValue();
            }
         }

         if (!emptyOkPresent && format != null && format.length() >= 0) {
            emptyOk = format.charAt(0) == '*';
         }

         BasicEditField edit = null;
         long style = this.getCurrentParagraphStyle() | 137438953472L;
         boolean wrap = false;
         if (size >= 0 || format == null || !format.equals("*m") && !format.equals("*M")) {
            if ((flags & 1) != 0) {
               edit = new BrowserPasswordEditField(maxLength < 0 ? 1000000 : maxLength, style, format);
            } else if (format != null && format.equals("XXX_RIM_EMAIL_INPUT")) {
               edit = (BasicEditField)(new Object(null, null, maxLength < 0 ? 1000000 : maxLength));
            } else {
               edit = new BrowserEditField(maxLength < 0 ? 1000000 : maxLength, style | 2147483648L | 1073741824, format);
            }
         } else if ((flags & 1) != 0) {
            edit = (BasicEditField)(new Object(null, "", maxLength < 0 ? 1000000 : maxLength, style));
            ((TextField)edit).setAllowUnicodeInput(true);
         } else {
            if (format.equals("*m")) {
               style |= 524288;
            }

            edit = (BasicEditField)(new Object(null, "", maxLength < 0 ? 1000000 : maxLength, style | 8388608));
            wrap = true;
         }

         edit.setFont(this._defaultFont);
         if (!this._skipElement) {
            WMLInputField inputField = new WMLTextInputField(
               this._currentScreen,
               this._currentScreen.getInputFieldCount(),
               size,
               this._wmlContextManager,
               title,
               name,
               value,
               edit,
               emptyOk,
               wrap,
               this.getCurrentParagraphStyle()
            );
            this.addField((Field)inputField, 0);
            this._currentScreen.addInputField(inputField);
            this._lastInputField = inputField;
         }
      }
   }

   private final void processElementMeta(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         int next = -1;
         String name = null;
         String value = null;

         while (next != 1 && attr != 0) {
            next = this.mapLiteralAttribute();
            if (next == -1) {
               break;
            }

            switch (next) {
               case 13:
                  value = this.readAttributeValue();
                  break;
               case 33:
                  this.readAttributeValue();
                  break;
               case 46:
                  this.readAttributeValue();
                  break;
               case 84:
               case 85:
                  this.readAttributeValue();
                  break;
               case 90:
                  name = this.readAttributeValue();
                  break;
               case 91:
                  name = "Content-Type";
                  break;
               case 93:
                  name = HeaderParser.EXPIRES;
            }
         }

         if (value != null && name != null && super._renderingApplication != null) {
            if (StringUtilities.strEqualIgnoreCase(name, HeaderParser.CACHE_CONTROL, 1701707776)) {
               SetHeaderEvent event = (SetHeaderEvent)(new Object(super._inputConnection, HeaderParser.CACHE_CONTROL, value));
               super._renderingApplication.eventOccurred(event);
            } else if (StringUtilities.strEqualIgnoreCase(name, HeaderParser.EXPIRES, 1701707776)) {
               SetHeaderEvent event = (SetHeaderEvent)(new Object(super._inputConnection, HeaderParser.EXPIRES, value));
               super._renderingApplication.eventOccurred(event);
            } else if (StringUtilities.strEqualIgnoreCase(name, "x-rim-auto-match", 1701707776)
               && StringUtilities.strEqualIgnoreCase(value, "none", 1701707776)) {
               this._textSegment.setAutoMatch(false);
               this._autoMatch = false;
            }
         }

         this.attachTimerIfRequired(name, value);
      }
   }

   private final void attachTimerIfRequired(String name, String value) {
      if (!this._browserContent.hasTimer() && value != null && StringUtilities.strEqualIgnoreCase(name, HeaderParser.REFRESH, 1701707776)) {
         int delay = getDelay(value);
         String url = getURL(value);
         if (url == null) {
            return;
         }

         this._ignoreTimer = true;
         this._browserContent.setOnTimer(new EventVerb((Event)(new Object(this._browserContent, url, null, 3, delay * 1000)), super._renderingApplication));
         PageTimer timer = new PageTimer(delay, this._browserContent);
         this._browserContent.addTimer(timer);
      }
   }

   private static final int getDelay(String content) {
      int delay = 0;
      int index = content.indexOf(59);

      try {
         if (index != -1) {
            return Integer.parseInt(content.substring(0, index));
         }

         index = content.indexOf(44);
         if (index == -1) {
            delay = Integer.parseInt(content);
         } else {
            delay = Integer.parseInt(content.substring(0, index));
         }
      } finally {
         return delay;
      }

      return delay;
   }

   private static final String getURL(String content) {
      String url = null;
      int index = content.indexOf(59);
      int contentLength = content.length();
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

      return url;
   }

   private final void processElementNoop(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         if (this._cardLevel) {
            if (this._currentTaskContainer instanceof DoVerb) {
               this.removeDoVerb(((DoVerb)this._currentTaskContainer).getName());
            } else if (this._currentTaskContainer instanceof OnEventVerb) {
               this.removeOnEvent(((OnEventVerb)this._currentTaskContainer).getType());
            }

            this._currentTaskContainer = null;
         } else if (this._currentTaskContainer != null
            && StringUtilities.strEqualIgnoreCase(((DoVerb)this._currentTaskContainer).getType(), "prev", 1701707776)) {
            this._browserContent.setPrevVerb(null);
         }

         if (attr != 0) {
            this.skipAllAttributes();
         }
      }
   }

   private final void processElementP(int attr, int content, boolean isStartTag) {
      if (this._textSegment != null) {
         if (!isStartTag) {
            this._textSegment.endP();
         } else {
            this._textSegment.startP();
            boolean wrappingStyle = true;
            int alignment = 0;
            int next = -1;

            while (next != 1 && attr != 0) {
               next = this.mapLiteralAttribute();
               if (next == -1) {
                  break;
               }

               switch (next) {
                  case 7:
                     alignment = 262144;
                     break;
                  case 8:
                     alignment = 0;
                     break;
                  case 10:
                     alignment = 524288;
                     break;
                  case 29:
                     wrappingStyle = false;
                     break;
                  case 30:
                     wrappingStyle = true;
                     break;
                  case 84:
                  case 85:
                     String classOrID = this.readAttributeValue();
                     if (classOrID != null && content > 0) {
                        this.setSkipElement(classOrID);
                     }
               }
            }

            if (content > 0 && !this._skipElement) {
               this._textSegment.setWrappingStyle(wrappingStyle);
               this._textSegment.setParagraphStyle(alignment);
               return;
            }
         }
      }
   }

   private final void processElementPostField(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         int next = -1;
         WMLVariable name = null;
         WMLVariable value = null;

         while (next != 1 && attr != 0) {
            next = this.mapLiteralAttribute();
            if (next == -1) {
               break;
            }

            switch (next) {
               case 33:
                  name = new WMLVariable(this._reader.getEncodedVariableName(this._in), this._reader, this._wmlContextManager);
                  break;
               case 77:
                  value = new WMLVariable(this._reader.getEncodedVariableName(this._in), this._reader, this._wmlContextManager);
                  break;
               case 84:
               case 85:
                  this.readAttributeValue();
            }
         }

         if (this._currentTaskContainer.getTask() instanceof Go) {
            ((Go)this._currentTaskContainer.getTask()).setPostField(name, value);
         }
      }
   }

   private final void processElementPre(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         if (attr != 0) {
            this.skipAllAttributes();
         }

         if (this._textSegment != null) {
            this._textSegment.startP();
            if (content > 0) {
               this._textSegment.setWrappingStyle(false);
               return;
            }
         }
      } else if (this._textSegment != null) {
         this._textSegment.endP();
      }
   }

   private final void processElementPrev(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         if (this._currentTaskContainer == null) {
            throw new Object(BrowserResources.getString(558));
         }

         Prev prev = new Prev(this._browserContent, this._wmlContextManager);
         this._currentTaskContainer.setTask(prev);
         if (attr != 0) {
            this.skipAllAttributes();
         }
      }
   }

   private final void processElementOnEvent(int attr, int content, boolean isStartTag) {
      if (!isStartTag) {
         if (this._currentTaskContainer != null) {
            this.pushOnEventVerb((OnEventVerb)this._currentTaskContainer);
            this._currentTaskContainer = null;
         }
      } else {
         int next = -1;
         String type = null;

         while (next != 1 && attr != 0) {
            next = this.mapLiteralAttribute();
            if (next == -1) {
               break;
            }

            switch (next) {
               case 55:
                  type = this.readAttributeValue();
                  break;
               case 60:
                  type = WMLConstants.STRING_ON_PICK;
                  break;
               case 61:
                  type = WMLConstants.STRING_ON_ENTER_B;
                  break;
               case 62:
                  type = WMLConstants.STRING_ON_ENTER_F;
                  break;
               case 63:
                  type = WMLConstants.STRING_ON_TIMER;
                  break;
               case 84:
               case 85:
                  this.readAttributeValue();
            }
         }

         this._currentTaskContainer = new OnEventVerb(type);
      }
   }

   private final void processElementOptGroup(int attr, int content, boolean isStartTag) {
      if (isStartTag && attr != 0) {
         this.skipAllAttributes();
      }
   }

   private final void processElementOption(int attr, int content, boolean isStartTag) {
      if (!isStartTag) {
         if (this._multiSelect) {
            this._currentMultiOption.setLabel(this._textSegment.getCurrentText());
            if (this._textSegment.getVarStartsLength() != 0) {
               this._currentMultiOption.setVariables(this._textSegment.getVars(), this._textSegment.getVarStarts());
            }
         } else {
            this._currentSingleOption.setLabel(this._textSegment.getCurrentText());
            if (this._textSegment.getVarStartsLength() != 0) {
               this._currentSingleOption.setVariables(this._textSegment.getVars(), this._textSegment.getVarStarts());
            }
         }

         this._textSegment.clear();
      } else {
         int next = -1;
         WMLVariable value = null;
         WMLVariable title = null;
         TaskContainer onPickVerb = null;
         byte[] url = null;

         while (next != 1 && attr != 0) {
            next = this.mapLiteralAttribute();
            if (next == -1) {
               break;
            }

            switch (next) {
               case 36:
                  url = this._reader.getEncodedVariableName(this._in);
                  Go go = new Go(this._browserContent, super._renderingApplication, this._wmlContextManager, null);
                  go.setHref(new WMLVariable(url, this._reader, this._wmlContextManager));
                  onPickVerb = new OnEventVerb(WMLConstants.STRING_ON_PICK, go);
                  break;
               case 54:
                  byte[] var15 = this._reader.getEncodedVariableName(this._in);
                  title = new WMLVariable(var15, this._reader, this._wmlContextManager);
                  break;
               case 77:
                  byte[] bytes = this._reader.getEncodedVariableName(this._in);
                  value = new WMLVariable(bytes, this._reader, this._wmlContextManager);
                  break;
               case 84:
               case 85:
                  String classOrID = this.readAttributeValue();
                  if (classOrID != null && content > 0) {
                     this.setSkipElement(classOrID);
                  }
            }
         }

         if (!this._skipElement) {
            if (this._multiSelect) {
               this._currentMultiOption = new WMLMultiSelectOption(this._currentMultiSelect, value, title, onPickVerb);
               WMLCheckboxField field = this._currentMultiOption.getCheckbox();
               field.setFont(this._defaultFont);
               this._currentMultiSelect.addOption(this._currentMultiOption);
               this.addField(field, 0);
               return;
            }

            this._currentSingleOption = new WMLSingleSelectOption(this._currentSingleSelect, value, title, onPickVerb);
            WMLRadioButtonField field = this._currentSingleOption.getRadioButton();
            field.setFont(this._defaultFont);
            this._currentSingleSelect.addOption(this._currentSingleOption);
            this.addField(field, 0);
            return;
         }
      }
   }

   private final void processElementRefresh(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         if (this._currentTaskContainer == null) {
            throw new Object(BrowserResources.getString(558));
         }

         Refresh refresh = new Refresh(this._browserContent, this._wmlContextManager);
         this._currentTaskContainer.setTask(refresh);
         if (attr != 0) {
            this.skipAllAttributes();
         }
      }
   }

   private final void processElementSelect(int attr, int content, boolean isStartTag) {
      if (!isStartTag) {
         if (this._multiSelect) {
            this._currentMultiSelect.doneAddingChoices();
         } else {
            this._currentSingleSelect.doneAddingChoices();
         }
      } else {
         this._textSegment.flush(false);
         int next = -1;
         String title = null;
         String name = null;
         String iname = null;
         this._multiSelect = false;
         WMLVariable ivalue = null;
         WMLVariable value = null;

         while (next != 1 && attr != 0) {
            next = this.mapLiteralAttribute();
            if (next == -1) {
               break;
            }

            switch (next) {
               case 21:
                  byte[] var11 = this._reader.getEncodedVariableName(this._in);
                  ivalue = new WMLVariable(var11, this._reader, this._wmlContextManager);
                  break;
               case 22:
                  iname = this.readAttributeValue();
                  break;
               case 31:
                  this._multiSelect = false;
                  break;
               case 32:
                  this._multiSelect = true;
                  break;
               case 33:
                  name = this.readAttributeValue();
                  break;
               case 53:
                  this.readAttributeValue();
                  break;
               case 54:
                  title = this.readAttributeValue();
                  break;
               case 77:
                  byte[] bytes = this._reader.getEncodedVariableName(this._in);
                  value = new WMLVariable(bytes, this._reader, this._wmlContextManager);
                  break;
               case 84:
               case 85:
                  this.readAttributeValue();
            }
         }

         if (!this._multiSelect) {
            this._currentSingleSelect = new WMLSingleSelectInputField(this._wmlContextManager, name, title, value, iname, ivalue);
            this._currentScreen.addInputField(this._currentSingleSelect);
            this._lastInputField = this._currentSingleSelect;
         } else {
            this._currentMultiSelect = new WMLMultiSelectInputField(this._wmlContextManager, name, title, value, iname, ivalue);
            this._currentScreen.addInputField(this._currentMultiSelect);
            this._lastInputField = null;
         }
      }
   }

   private final void processElementSetVar(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         WMLVariable name = null;
         WMLVariable value = null;
         int next = -1;

         while (next != 1 && attr != 0) {
            next = this.mapLiteralAttribute();
            if (next == -1) {
               break;
            }

            switch (next) {
               case 33:
                  name = new WMLVariable(this._reader.getEncodedVariableName(this._in), this._reader, this._wmlContextManager);
                  break;
               case 77:
                  value = new WMLVariable(this._reader.getEncodedVariableName(this._in), this._reader, this._wmlContextManager);
                  break;
               case 84:
               case 85:
                  this.readAttributeValue();
            }
         }

         if (this._currentTaskContainer != null) {
            if (value == null) {
               value = new WMLVariable("", this._wmlContextManager, "NoEsc", this._reader.getEncoding());
            }

            this._currentTaskContainer.addSetVar(name, value);
         }
      }
   }

   private final void processElementSmall(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         this._textSegment.decreaseHeight();
         if (attr != 0) {
            this.skipAllAttributes();
            return;
         }
      } else {
         this._textSegment.increaseHeight();
      }
   }

   private final void processElementStrong(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         this._textSegment.boldOn();
         if (attr != 0) {
            this.skipAllAttributes();
            return;
         }
      } else {
         this._textSegment.boldOff();
      }
   }

   private final void processElementTable(int attr, int content, boolean isStartTag) {
      if (!isStartTag) {
         this._currentScreen.add(this._currentTable);
         this._currentTable = null;
         this._textSegment.setParagraphStyle(this._preTableParagraphStyle);
      } else {
         this._preTableParagraphStyle = this._textSegment.getParagraphStyle();
         this._textSegment.flush(false);
         int numColumns = 1;
         String columnAlignment = null;
         int next = -1;
         String id = null;
         String title = null;

         while (next != 1 && attr != 0) {
            next = this.mapLiteralAttribute();
            if (next == -1) {
               break;
            }

            switch (next) {
               case 54:
                  title = this.readAttributeValue();
                  break;
               case 82:
                  columnAlignment = this.readAttributeValue();
                  break;
               case 83:
                  try {
                     int numColumnsTmp = Integer.parseInt(this.readAttributeValue());
                     numColumns = numColumnsTmp > numColumns ? numColumnsTmp : numColumns;
                     break;
                  } finally {
                     break;
                  }
               case 84:
                  this.readAttributeValue();
                  break;
               case 85:
                  id = this.readAttributeValue();
            }
         }

         if (title != null) {
            this._textSegment.renderText(title);
            this._textSegment.flush(false);
         }

         this._textSegment.setRemoveLeadingSpaces();
         if (id != null && id.startsWith("XXX_RIM_TABLE_NO_BORDER_UID")) {
            this._currentTable = new WMLTable((byte)0, numColumns, columnAlignment);
         } else {
            this._currentTable = new WMLTable((byte)63, numColumns, columnAlignment);
         }
      }
   }

   private final void processElementTd(int attr, int content, boolean isStartTag) {
      if (!isStartTag) {
         this._textSegment.flush(false);
         if (this._currentManagerStack.peek() instanceof TableCell) {
            this._currentManagerStack.pop();
         }

         this._currentTableColumn++;
      } else {
         if (this._currentTableColumn >= this._currentTable.getNumColumns()) {
            Manager tableCell = this._currentTable.getCornerCell();
            this._currentManagerStack.push(tableCell);
            return;
         }

         int columnAlignment = this._currentTable.getParagraphStyle(this._currentTableColumn) & 7;
         int translatorIndex = columnAlignment - 4;
         Manager tableCell = null;
         Object var10;
         if (columnAlignment == 12884901888L) {
            var10 = this._currentTable.addCell(1, 1, 1);
         } else if (columnAlignment == 8589934592L) {
            var10 = this._currentTable.addCell(1, 1, 2);
         } else {
            var10 = this._currentTable.addCell(1, 1, 0);
         }

         if (content == 1) {
            this._currentManagerStack.push(var10);
         }

         int style = RTF_ALIGNMENT_TRANSLATOR[translatorIndex];
         this._textSegment.setParagraphStyle(style);
         int next = -1;

         while (next != 1 && attr != 0) {
            next = this.mapLiteralAttribute();
            if (next == -1) {
               break;
            }

            switch (next) {
               case 83:
                  break;
               case 84:
               case 85:
               default:
                  this.readAttributeValue();
            }
         }
      }

      this._textSegment.setRemoveLeadingSpaces();
   }

   private final void processElementTemplate(int attr, int content, boolean isStartTag) {
      if (!isStartTag) {
         this._deckLevel = false;
      } else {
         this._deckLevel = true;
         this._currentVerbs = (Vector)(new Object());
         int next = -1;

         while (next != 1 && attr != 0) {
            next = this.mapLiteralAttribute();
            if (next == -1) {
               break;
            }

            switch (next) {
               case 37: {
                  byte[] value = this._reader.getEncodedVariableName(this._in);
                  Go go = new Go(this._browserContent, super._renderingApplication, this._wmlContextManager, null);
                  go.setHref(new WMLVariable(value, this._reader, this._wmlContextManager));
                  this._templateOnEnterBackwardVerb = new OnEventVerb(WMLConstants.STRING_ON_ENTER_B, go);
                  break;
               }
               case 38: {
                  byte[] value = this._reader.getEncodedVariableName(this._in);
                  Go go = new Go(this._browserContent, super._renderingApplication, this._wmlContextManager, null);
                  go.setHref(new WMLVariable(value, this._reader, this._wmlContextManager));
                  this._templateOnEnterForwardVerb = new OnEventVerb(WMLConstants.STRING_ON_ENTER_F, go);
                  break;
               }
               case 39: {
                  byte[] value = this._reader.getEncodedVariableName(this._in);
                  Go go = new Go(this._browserContent, super._renderingApplication, this._wmlContextManager, null);
                  go.setHref(new WMLVariable(value, this._reader, this._wmlContextManager));
                  this._templateOnTimerVerb = new OnEventVerb(WMLConstants.STRING_ON_TIMER, go);
                  break;
               }
               case 84:
               case 85:
                  this.readAttributeValue();
            }
         }

         if (content == 0) {
            this._deckLevel = false;
            return;
         }
      }
   }

   private final void processElementTimer(int attr, int content, boolean isStartTag) {
      if (isStartTag && !this._ignoreTimer) {
         int next = -1;
         WMLVariable name = null;
         WMLVariable value = null;

         while (next != 1 && attr != 0) {
            next = this.mapLiteralAttribute();
            if (next == -1) {
               break;
            }

            switch (next) {
               case 33: {
                  byte[] enc = this._reader.getEncodedVariableName(this._in);
                  name = new WMLVariable(enc, this._reader, this._wmlContextManager);
                  break;
               }
               case 77: {
                  byte[] enc = this._reader.getEncodedVariableName(this._in);
                  value = new WMLVariable(enc, this._reader, this._wmlContextManager);
               }
            }
         }

         this._currentTimer = new WMLTimer(name, value, this._browserContent, this._wmlContextManager);
         this._browserContent.addTimer(this._currentTimer);
      }
   }

   private final void processElementTr(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         int next = -1;

         while (next != 1 && attr != 0) {
            next = this.mapLiteralAttribute();
            if (next == -1) {
               break;
            }

            switch (next) {
               case 83:
                  break;
               case 84:
               case 85:
               default:
                  this.readAttributeValue();
            }
         }

         this._currentTableColumn = 0;
         this._currentTable.addRow();
      }
   }

   private final void processElementU(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         this._textSegment.underlinedOn();
         if (attr != 0) {
            this.skipAllAttributes();
            return;
         }
      } else {
         this._textSegment.underlinedOff();
      }
   }

   private final void processElementWml(int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         int next = -1;

         while (next != 1) {
            if (attr == 0) {
               return;
            }

            next = this.mapLiteralAttribute();
            if (next == -1) {
               return;
            }

            switch (next) {
               case 83:
                  break;
               case 84:
               case 85:
               default:
                  this.readAttributeValue();
            }
         }
      }
   }

   private final void skipTag(byte tag, int attr, int content, boolean isStartTag) {
      if (isStartTag) {
         if (content == 1) {
            this._tagStack.push(tag);
         }

         if (attr != 0) {
            this.skipAllAttributes();
            return;
         }
      } else {
         this._tagStack.pop();
      }
   }

   private final void skipAllAttributes() {
      int next = -1;

      while (next != 1) {
         next = this.mapLiteralAttribute();
         if (next == -1) {
            break;
         }

         if (next == 1) {
            return;
         }

         this.skipAttributeValue();
      }
   }

   private final void skipElement(int endElement) {
      while (true) {
         int _id = this._in.read();
         if (_id == -1) {
            return;
         }

         switch ((short)_id) {
            case 0:
               if (this._in.read() == 0) {
                  break;
               }

               throw new Object(WMLConstants.STRING_ONLY_PAGE_ZERO_SUPPORTED);
            case 1:
               if (this._tagStack.isEmpty()) {
                  break;
               }

               int tag = this._tagStack.peek();
               this.skipTag((byte)tag, 0, 0, false);
               if (tag != endElement && tag != 63) {
                  break;
               }

               return;
            case 2:
               this._in.readMBInt();
               break;
            case 3:
               this._in.skipInlineString();
               break;
            case 4:
            case 68:
            case 132:
            case 196:
               int tableOffset = this._in.readMBInt();
               int isContent = 0;
               int isAttr = 0;
               if (68 == (short)_id || 196 == (short)_id) {
                  isContent = 1;
               }

               if (132 == (short)_id || 196 == (short)_id) {
                  isAttr = 1;
               }

               String tag = this._reader.getStringFromStringTable(tableOffset);
               int tagValue = WMLConstants.getTagToken(tag.hashCode());
               int tagToProcess = _id;
               if (tagValue != -1) {
                  tagToProcess = tagValue;
               }

               this.skipTag((byte)tagToProcess, isAttr, isContent, true);
               break;
            case 64:
            case 65:
            case 66:
               this._in.skipInlineString();
               break;
            case 67:
               int next = -1;

               while (next != 1) {
                  next = this.mapLiteralAttribute();
                  if (next == -1) {
                     break;
                  }
               }
               break;
            case 128:
            case 129:
            case 130:
               this._in.readMBInt();
               break;
            case 131:
               this._in.readMBInt();
               break;
            case 192:
            case 193:
            case 194:
               throw new Object(WMLConstants.STRING_RESERVED_EXT_BYTE);
            case 195:
               int len = this._in.readMBInt();
               this._in.skip(len);
               break;
            default:
               int tagId = _id & 63;
               int attr = (_id & 128) >> 7;
               int content = (_id & 64) >> 6;
               this.skipTag((byte)tagId, attr, content, true);
         }
      }
   }

   private final void setSkipElement(String classOrID) {
      if (classOrID != null && classOrID.length() >= 11 && classOrID.charAt(0) == 'X') {
         switch (classOrID.charAt(8)) {
            case 'A':
               if (classOrID.equals("XXX_RIM_ASCENDENT") && Resource$Internal.getResourceClass("net_rim_bb_phone_ascendent") == null) {
                  this._skipElement = true;
                  return;
               }
               break;
            case 'B':
               if (classOrID.equals("XXX_RIM_BLUETOOTH")) {
                  if (!BluetoothME.isSupported()) {
                     this._skipElement = true;
                     return;
                  }
               } else if (classOrID.equals("XXX_RIM_BACKLIGHT_BRIGHTNESS") && !Backlight.isBrightnessConfigurable()) {
                  this._skipElement = true;
                  return;
               }
               break;
            case 'C':
               if (classOrID.equals("XXX_RIM_CONVENIENCE_KEY")) {
                  if (!Keypad.hasFrontConvenienceKey()) {
                     this._skipElement = true;
                     return;
                  }
               } else if (classOrID.equals("XXX_RIM_CURRENCY_KEY") && Keypad.getHardwareLayout() != 1364669234) {
                  this._skipElement = true;
                  return;
               }
               break;
            case 'L':
               if (classOrID.equals("XXX_RIM_LIGHT_SENSOR") && !InternalServices.isDeviceCapable(16)) {
                  this._skipElement = true;
                  return;
               }
               break;
            case 'M':
               if (classOrID.equals("XXX_RIM_MAPS")) {
                  if (CodeModuleManager.getModuleHandle("net_rim_bb_lbs") == 0) {
                     this._skipElement = true;
                     return;
                  }
               } else if (classOrID.equals("XXX_RIM_MMS")) {
                  if (!MMS.isEnabled()) {
                     this._skipElement = true;
                     return;
                  }
               } else if (classOrID.equals("XXX_RIM_MUTE_BUTTON")) {
                  if (!Keypad.hasMuteKey()) {
                     this._skipElement = true;
                     return;
                  }
               } else if (classOrID.equals("XXX_RIM_MP3") && !Audio.isCodecSupported(3)) {
                  this._skipElement = true;
                  return;
               }
               break;
            case 'N':
               if (classOrID.equals("XXX_RIM_NOT_SEND_END")) {
                  if (Keypad.hasSendEndKeys()) {
                     this._skipElement = true;
                     return;
                  }
               } else if (classOrID.equals("XXX_RIM_NOT_MUTE_BUTTON")) {
                  if (Keypad.hasMuteKey()) {
                     this._skipElement = true;
                     return;
                  }
               } else if (classOrID.equals("XXX_RIM_NOT_MP3")) {
                  if (Audio.isCodecSupported(3)) {
                     this._skipElement = true;
                     return;
                  }
               } else if (classOrID.equals("XXX_RIM_NOT_STANDBY_SCREEN") && IdleScreenManager.getInstance() != null) {
                  this._skipElement = true;
                  return;
               }
               break;
            case 'P':
               if (classOrID.equals("XXX_RIM_PTT")) {
                  if (CodeModuleManager.getModuleHandle("net_rim_bb_phone_ptt_app") == 0) {
                     this._skipElement = true;
                     return;
                  }
               } else if (classOrID.equals("XXX_RIM_PGP") && Resource$Internal.getResourceClass("net_rim_bb_pgp") == null) {
                  this._skipElement = true;
                  return;
               }
               break;
            case 'S':
               if (classOrID.equals("XXX_RIM_SEND_END")) {
                  if (!Keypad.hasSendEndKeys()) {
                     this._skipElement = true;
                     return;
                  }
               } else if (classOrID.equals("XXX_RIM_SPEAKERPHONE_KEY")) {
                  if (Keypad.getHardwareLayout() != 1364669234) {
                     this._skipElement = true;
                     return;
                  }
               } else if (classOrID.equals("XXX_RIM_STANDBY_SCREEN")) {
                  if (IdleScreenManager.getInstance() == null) {
                     this._skipElement = true;
                     return;
                  }
               } else if (classOrID.equals("XXX_RIM_SSP") && Resource$Internal.getResourceClass("net_rim_bb_smime") == null) {
                  this._skipElement = true;
                  return;
               }
               break;
            case 'T':
               if (classOrID.equals("XXX_RIM_TRACKBALL")) {
                  if (!Trackball.isSupported()) {
                     this._skipElement = true;
                     return;
                  }
               } else if (classOrID.equals("XXX_RIM_TRACKWHEEL") && Trackball.isSupported()) {
                  this._skipElement = true;
                  return;
               }
               break;
            case 'V':
               if (classOrID.equals("XXX_RIM_VENDOR_HELP") && Branding.getData(20480) == null) {
                  this._skipElement = true;
               }
         }
      }
   }
}
