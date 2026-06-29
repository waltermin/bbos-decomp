package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.addressbook.DisplayPictureModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.phone.api.CallerIDProvider;
import net.rim.device.apps.internal.phone.api.PTTKeyHandler;
import net.rim.device.apps.internal.phone.api.PhoneLogger;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.api.ui.CallDisplayListener;
import net.rim.device.apps.internal.phone.api.verbs.AnswerCallVerb;
import net.rim.device.apps.internal.phone.api.verbs.IgnoreCallVerb;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.UiSettings;
import net.rim.device.internal.ui.component.HorizontalSpacerField;
import net.rim.vm.Array;

public final class CallDisplayDialog extends PopupScreen {
   private int _callId;
   private Font _titleFont;
   private Font _callerIDFont;
   private Font _defaultFont;
   private Object _context;
   private String _longestCallerIDString;
   private DisplayPictureModel _displayPictureModel;
   private UiApplication _app;
   private CallDisplayListener _listener;
   private CallDisplayDialog$DismissDialogTimer _dismissTimer;
   private boolean _isClosed;
   private boolean _pictureAvailable;
   private IgnoreCallVerb _ignoreCallVerb;
   private AnswerCallVerb _sendKeyVerb;
   private Verb[] _menuVerbs;
   private CallDisplayDialog$CallDisplayManager _callDisplayManager = new CallDisplayDialog$CallDisplayManager(this);
   private VerticalFieldManager _callerIDContainer = new VerticalFieldManager();
   private CallerIDProvider _callerIDProvider;
   private String _customTitle;
   private String _customName;
   private String _customNumber;
   private FontFamily _fontFamily;
   private boolean _pttKeyDown;
   private boolean _ignoreKeyRepeat;
   public static final int CALLER_ID_INDENT = 4;
   public static final int CALLER_ID_BUFFER = 4;
   private static final int VERTICAL_SPACER_HEIGHT = 4;
   private static final int INDENT_WIDTH = 6;
   private static final int DEFAULT_PICTURE_WIDTH = 90;
   private static final int MIN_TEXT_SIZE = 8;
   private static final int DEFAULT_BANNER_FONT_SIZE = 18;
   private static final int _screenWidth = Display.getWidth();
   private static final int _screenHeight = Display.getHeight();
   private static boolean _smallScreen = PhoneUtilities.smallScreen();
   private static final int _baseDialogWidth = _smallScreen ? _screenWidth : _screenWidth - 4;
   private static final int MIN_COLOUR_SCREEN_CALLER_ID_FONT_SIZE = PhoneUtilities.cdmaTypeNetwork() ? 12 : 16;
   private static String _lastSelectedItem = "";

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public CallDisplayDialog(
      int callId, boolean waiting, CallerIDInfo callerIDInfo, String title, Verb[] verbs, UiApplication app, CallDisplayListener listener, Object context
   ) {
      super(new VerticalFieldManager(), 65536);
      this.setId("calldisplay");
      boolean var34 = false /* VF: Semaphore variable */;

      label181:
      try {
         var34 = true;
         this._fontFamily = FontFamily.forName("BBMillbank");
         if (_smallScreen) {
            int titleFontSize = waiting ? 10 : 12;
            int buttonFontSize = waiting ? 10 : 12;
            int callerIDFontSize = waiting ? 10 : 12;
            this._titleFont = this._fontFamily.getFont(1, titleFontSize);
            this._callerIDFont = this._fontFamily.getFont(1, callerIDFontSize);
            this._defaultFont = this._fontFamily.getFont(1, buttonFontSize);
            var34 = false;
         } else if (PhoneUtilities.isCharm240x260Screen()) {
            this._titleFont = this._fontFamily.getFont(1, 32);
            this._callerIDFont = this._fontFamily.getFont(1, 28);
            int size = verbs.length > 2 ? 18 : 20;
            this._defaultFont = this._fontFamily.getFont(1, size);
            var34 = false;
         } else {
            this._titleFont = this._fontFamily.getFont(1, 22);
            this._callerIDFont = this._fontFamily.getFont(1, 24);
            this._defaultFont = this._fontFamily.getFont(1, 16);
            var34 = false;
         }
      } finally {
         if (var34) {
            this._titleFont = Font.getDefault().derive(0, 14);
            this._callerIDFont = this._titleFont;
            break label181;
         }
      }

      this.setBorder(0, 0, 0, 0);
      this._callId = callId;
      this._context = ContextObject.clone(context);
      this._app = app;
      this._listener = listener;
      this.add(this._callDisplayManager);
      this.assignVerbs(verbs);
      if (callerIDInfo instanceof CallerIDProvider) {
         this._callerIDProvider = callerIDInfo;
      }

      this._customTitle = (String)ContextObject.get(this._context, -2385970650495354746L);
      this._customName = (String)ContextObject.get(this._context, 4313723025067987546L);
      this._customNumber = (String)ContextObject.get(this._context, 7370986089050392817L);
      if (this._customTitle != null) {
         title = this._customTitle;
      }

      LabelField altLineField = new LabelField();
      altLineField.setFont(this._defaultFont);
      if (PhoneUtilities.getAllLineIds().length > 1) {
         altLineField.setText(PhoneUtilities.getLineDescription(PhoneUtilities.getCurrentLineId(callId)));
      }

      HorizontalFieldManager titleField = new HorizontalFieldManager();
      LabelField titleLabelField = new LabelField(title);
      titleLabelField.setFont(this._titleFont);
      this.sizeFontToFitScreen(titleLabelField, title, this._titleFont);
      titleField.add(new HorizontalSpacerField((_baseDialogWidth >> 1) - (titleLabelField.getPreferredWidth() >> 1)));
      titleField.add(titleLabelField);
      ContextObject.setFlag(this._context, 58, 26);
      Object number = this._callerIDProvider.getNumber();
      Field pictureField = null;
      if (number != null || isPhoneNumberRestricted(callerIDInfo)) {
         Field callDisplayField = this.getCallDisplayField(callerIDInfo);
         if (callDisplayField != null) {
            this.updateCallerIDField(callDisplayField);
            if (this._displayPictureModel != null) {
               Bitmap picture = this._displayPictureModel.getDisplayBitmap();
               if (picture != null) {
                  pictureField = new BitmapField(picture);
               }
            }

            this._pictureAvailable = pictureField != null;
         }
      }

      Bitmap answerIgnoreBackgroundBitmap;
      if (_screenWidth == 240) {
         answerIgnoreBackgroundBitmap = PhoneResources.getBitmap("AnswerIgnoreBanner240.png");
      } else {
         answerIgnoreBackgroundBitmap = PhoneResources.getBitmap("AnswerIgnoreBanner320.png");
      }

      BitmapField answerIgnoreBackgroundField = new BitmapField(answerIgnoreBackgroundBitmap);
      LabelField answerLabelField = new LabelField(PhoneResources.getString(480));
      LabelField ignoreLabelField = new LabelField(PhoneResources.getString(482));
      HorizontalFieldManager answerIgnoreTextField = new HorizontalFieldManager(1152921504606846976L);
      boolean showMore = false;
      if (this._menuVerbs.length > 2 && Trackball.isSupported()) {
         showMore = true;
      }

      LabelField moreLabelField = null;
      int fontSize = 19;
      int space1 = 0;
      int space2 = fontSize;

      do {
         Font bannerFont = this._fontFamily.getFont(1, --fontSize);
         answerLabelField.setFont(bannerFont);
         ignoreLabelField.setFont(bannerFont);
         if (showMore) {
            moreLabelField = new LabelField(PhoneResources.getString(6320));
            moreLabelField.setFont(bannerFont);
            space1 = (_baseDialogWidth >> 1) - answerLabelField.getPreferredWidth() - (moreLabelField.getPreferredWidth() >> 1);
            space2 = (_baseDialogWidth >> 1) - ignoreLabelField.getPreferredWidth() - (moreLabelField.getPreferredWidth() >> 1);
         } else {
            space1 = _baseDialogWidth - answerLabelField.getPreferredWidth() - ignoreLabelField.getPreferredWidth();
         }
      } while ((space1 < fontSize || space2 < fontSize) && fontSize > 8);

      answerIgnoreTextField.add(answerLabelField);
      if (showMore) {
         answerIgnoreTextField.add(new HorizontalSpacerField(space1));
         answerIgnoreTextField.add(moreLabelField);
         answerIgnoreTextField.add(new HorizontalSpacerField(space2));
      } else {
         answerIgnoreTextField.add(new HorizontalSpacerField(space1));
      }

      answerIgnoreTextField.add(ignoreLabelField);
      AudioRouter ar = AudioRouter.getInstance();
      Bitmap answerIconBitmap;
      if (ar.getDefaultSink(0) == 2) {
         answerIconBitmap = PhoneResources.getBitmap("net_rim_Phone-SendBT.png");
      } else {
         answerIconBitmap = PhoneResources.getBitmap("net_rim_Phone-Send.png");
      }

      BitmapField answerIconField = new BitmapField(answerIconBitmap, 51539607552L);
      Bitmap ignoreIconBitmap = PhoneResources.getBitmap("net_rim_Phone-End.png");
      BitmapField ignoreIconField = new BitmapField(ignoreIconBitmap, 51539607552L);
      HorizontalFieldManager answerIgnoreIconsField = new HorizontalFieldManager(1152921504606846976L);
      answerIgnoreIconsField.add(answerIconField);
      if (!showMore) {
         answerIgnoreIconsField.add(new HorizontalSpacerField(_baseDialogWidth - answerIconField.getPreferredWidth() - ignoreIconField.getPreferredWidth() - 4));
      } else {
         Bitmap moreIconBitmap = PhoneResources.getBitmap("net_rim_Phone-More.png");
         BitmapField moreIconField = new BitmapField(moreIconBitmap, 51539607552L);
         int space = (_baseDialogWidth >> 1) - answerIconField.getPreferredWidth() - (moreIconField.getPreferredWidth() >> 1) - 2;
         answerIgnoreIconsField.add(new HorizontalSpacerField(space));
         answerIgnoreIconsField.add(moreIconField);
         space = (_baseDialogWidth >> 1) - ignoreIconField.getPreferredWidth() - (moreIconField.getPreferredWidth() >> 1) - 2;
         answerIgnoreIconsField.add(new HorizontalSpacerField(space));
      }

      answerIgnoreIconsField.add(ignoreIconField);
      this._callDisplayManager.add(altLineField);
      this._callDisplayManager.add(new SeparatorField());
      this._callDisplayManager.add(titleField);
      this._callDisplayManager.add(answerIgnoreBackgroundField);
      this._callDisplayManager.add(answerIgnoreIconsField);
      this._callDisplayManager.add(answerIgnoreTextField);
      this._callDisplayManager.add(this._callerIDContainer);
      if (this._pictureAvailable) {
         this._callDisplayManager.add(pictureField);
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         Backlight.setTimeout(255);
      } else {
         Backlight.setTimeout(UiSettings.getBacklightTimeout());
      }
   }

   @Override
   protected final void onMenuDismissed(Menu menu) {
      super.onMenuDismissed(menu);
      if (menu != null) {
         MenuItem mi = menu.getCurrentMenuItem();
         if (mi instanceof VerbMenuItem) {
            VerbMenuItem vmi = (VerbMenuItem)mi;
            Verb verb = vmi.getVerb();
            _lastSelectedItem = verb.toString();
         }
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      MenuItem defaultItem = null;
      int len = this._menuVerbs.length;

      for (int i = 0; i < len; i++) {
         MenuItem mi = new VerbMenuItem(this._menuVerbs[i], i);
         if (this._menuVerbs[i] == this._sendKeyVerb) {
            mi.setIcon(PhoneResources.getImage(15));
            if (defaultItem == null) {
               defaultItem = mi;
            }
         } else if (this._menuVerbs[i] == this._ignoreCallVerb) {
            mi.setIcon(PhoneResources.getImage(16));
         }

         menu.add(mi);
         if (this._menuVerbs[i].toString().equals(_lastSelectedItem)) {
            defaultItem = mi;
         }
      }

      if (defaultItem != null) {
         menu.setDefault(defaultItem);
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               int activeCallID = Phone.getInstance().getActiveCallId();
               int heldCallID = Phone.getInstance().getHeldCallId();
               if (activeCallID == 0 && heldCallID == 0) {
                  return true;
               }
         }
      }

      return handled;
   }

   public final void sendKeyPressed() {
      if (this._sendKeyVerb != null) {
         this._sendKeyVerb.invoke(null);
      }
   }

   public final void endKeyPressed() {
      if (this._ignoreCallVerb != null) {
         this._ignoreCallVerb.invoke(null);
      }

      this.dismiss();
   }

   private static final boolean isPhoneNumberRestricted(CallerIDInfo callerIDInfo) {
      switch (RadioInfo.getNetworkType()) {
         case 3:
         case 6:
            return false;
         case 4:
         case 5:
         case 7:
         default:
            return callerIDInfo.isPrivateNumber() || callerIDInfo.isUnknownNumber();
      }
   }

   private final synchronized void updateCallerIDField(Field callerIDField) {
      this._callerIDContainer.deleteAll();
      if (_screenWidth == 240) {
         this._callerIDContainer.add(callerIDField);
      } else {
         HorizontalFieldManager hfm = new HorizontalFieldManager(1152921504606846976L);
         hfm.add(new HorizontalSpacerField(6));
         hfm.add(callerIDField);
         this._callerIDContainer.add(hfm);
      }

      this.sizeFontToFitScreen(callerIDField, this._longestCallerIDString, this._callerIDFont);
   }

   private final int getAvailableCallerIDWidth() {
      int unusableWidth = this.getPaddingLeft() + this.getPaddingRight() + this.getBorderLeft() + this.getBorderRight() + 4 + 4 + 4;
      if (this._pictureAvailable) {
         unusableWidth += 90;
      }

      return _baseDialogWidth - unusableWidth;
   }

   private final void sizeFontToFitScreen(Field field, String stringToFit, Font currFont) {
      Font font = currFont;
      int currFontSize = currFont.getHeight();
      int fontSizeDecrement = 4;
      int minFontSize = MIN_COLOUR_SCREEN_CALLER_ID_FONT_SIZE;
      int widthRequired = font.getAdvance(stringToFit);
      int widthAvailable = this.getAvailableCallerIDWidth();
      if (widthRequired >= widthAvailable) {
         for (int fontSize = currFontSize - fontSizeDecrement; fontSize >= minFontSize; fontSize -= fontSizeDecrement) {
            font = font.derive(1, fontSize);
            widthRequired = font.getAdvance(stringToFit);
            if (widthRequired <= widthAvailable) {
               break;
            }
         }
      }

      PhoneUtilities.updateFont(field, font);
   }

   private final Field getCallDisplayField(CallerIDInfo cidi) {
      Field field = null;
      Field iconField = null;
      Object cidiNumber = cidi.getNumber();
      String stringToFitOnScreen = null;
      boolean havePicture = false;
      this._displayPictureModel = cidi.getDisplayPictureModel();
      if (this._displayPictureModel != null) {
         Bitmap icon = this._displayPictureModel.getDisplayIcon();
         if (icon != null) {
            iconField = new BitmapField(icon);
         }

         if (this._displayPictureModel.getDisplayBitmap() != null) {
            havePicture = true;
         }
      }

      long fieldStyle = 1152921504606847040L;
      if (havePicture && _screenWidth == 320) {
         fieldStyle = 1152921504606847040L;
      } else {
         fieldStyle = 1152921504606847044L;
      }

      if (cidi.isPrivateNumber()) {
         stringToFitOnScreen = PhoneResources.getString(156);
         field = new LabelField(stringToFitOnScreen, fieldStyle);
      } else if (cidi.isUnknownNumber()) {
         stringToFitOnScreen = PhoneResources.getString(117);
         field = new LabelField(stringToFitOnScreen, fieldStyle);
      } else if (cidiNumber != null && cidiNumber.toString().length() == 0) {
         stringToFitOnScreen = PhoneResources.getString(117);
         field = new LabelField(stringToFitOnScreen, fieldStyle);
      }

      if (field != null && stringToFitOnScreen != null) {
         String friendlyNameString = VoiceServices.getCallName(this._callId, false);
         if (friendlyNameString != null && friendlyNameString.length() > 0) {
            VerticalFieldManager vfm = new VerticalFieldManager();
            Field nameField = new LabelField(friendlyNameString, fieldStyle);
            this._longestCallerIDString = PhoneUtilities.getLongestString(friendlyNameString, stringToFitOnScreen);
            nameField.setFont(this._defaultFont);
            field.setFont(this._defaultFont);
            vfm.add(nameField);
            vfm.add(field);
            return vfm;
         } else {
            this._longestCallerIDString = stringToFitOnScreen;
            field.setFont(this._defaultFont);
            return field;
         }
      } else {
         boolean usingNetworkCallName = false;
         String friendlyNameString = cidi.getDisplayString(true);
         if (friendlyNameString == null || friendlyNameString.length() == 0) {
            String networkNameString = VoiceServices.getCallName(this._callId, false);
            if (networkNameString != null && networkNameString.length() > 0) {
               friendlyNameString = networkNameString;
               usingNetworkCallName = true;
            }
         }

         String company = cidi.getDisplayableString(2);
         String name = null;
         String number = null;
         int len = 0;
         if (friendlyNameString != null && (len = friendlyNameString.length()) > 0) {
            String var29 = null;
            String var32 = null;
            if (!usingNetworkCallName) {
               var29 = friendlyNameString;
               var32 = cidiNumber.toString();
            } else {
               int spaceIndex = friendlyNameString.lastIndexOf(32);
               int numIndex = -1;

               for (int i = spaceIndex + 1; i < len; i++) {
                  char c = friendlyNameString.charAt(i);
                  if (Character.isDigit(c) || c == '+') {
                     numIndex = i;
                     break;
                  }
               }

               String numberString = null;
               if (spaceIndex != -1) {
                  if (numIndex != -1 && Character.isDigit(friendlyNameString.charAt(spaceIndex + 1))) {
                     var29 = friendlyNameString.substring(0, numIndex - 1);
                     var32 = friendlyNameString.substring(spaceIndex + 1);
                  } else {
                     var29 = friendlyNameString;
                     var32 = cidiNumber.toString();
                  }
               } else {
                  var29 = friendlyNameString;
                  var32 = cidiNumber.toString();
               }
            }

            if (var32 != null) {
               AbstractPhoneNumberModel pnm = (AbstractPhoneNumberModel)cidi.getNumber();
               int type = pnm.getType();
               if (type != 0) {
                  String desc = cidi.getDisplayableString(6);
                  if (desc != null && desc.length() > 0) {
                     var32 = var32 + " (" + desc + ")";
                  }
               }
            }

            if (this._customName != null) {
               var29 = this._customName;
            }

            if (this._customNumber != null) {
               var32 = this._customNumber;
            }

            VerticalFieldManager vfm = new VerticalFieldManager();
            if (var29 != null) {
               Field nameField = new LabelField(var29, fieldStyle);
               if (iconField != null) {
                  HorizontalFieldManager hfm = new HorizontalFieldManager();
                  hfm.add(iconField);
                  hfm.add(nameField);
                  vfm.add(hfm);
               } else {
                  vfm.add(nameField);
               }
            }

            if (var32 != null) {
               vfm.add(new LabelField(var32, fieldStyle));
            }

            this._longestCallerIDString = PhoneUtilities.getLongestString(var29, var32);
            if (company != null && !var29.equals(company)) {
               vfm.add(new LabelField(company, fieldStyle));
               this._longestCallerIDString = PhoneUtilities.getLongestString(this._longestCallerIDString, company);
            }

            field = vfm;
         } else if (this._customName == null) {
            number = cidiNumber.toString();
            field = new LabelField(number, fieldStyle);
            this._longestCallerIDString = number;
         } else if (this._customNumber != null) {
            VerticalFieldManager vfm = new VerticalFieldManager();
            vfm.add(new LabelField(this._customName, fieldStyle));
            vfm.add(new LabelField(this._customNumber, fieldStyle));
            this._longestCallerIDString = PhoneUtilities.getLongestString(name, number);
            if (company != null && !name.equals(company)) {
               vfm.add(new LabelField(company, fieldStyle));
               this._longestCallerIDString = PhoneUtilities.getLongestString(this._longestCallerIDString, company);
            }

            field = vfm;
         } else if (company == null) {
            field = new LabelField(this._customName, fieldStyle);
            this._longestCallerIDString = this._customName;
         } else {
            VerticalFieldManager vfm = new VerticalFieldManager();
            vfm.add(new LabelField(this._customName, fieldStyle));
            vfm.add(new LabelField(company, fieldStyle));
            this._longestCallerIDString = PhoneUtilities.getLongestString(this._customName, company);
            field = vfm;
         }

         field.setFont(this._defaultFont);
         return field;
      }
   }

   public final void updateCallerIDInfo(RIMModel callDisplayInfo) {
      Field callerIDField = this.getCallDisplayField((CallerIDInfo)callDisplayInfo);
      if (callerIDField != null) {
         this.updateCallerIDField(callerIDField);
      }
   }

   public final void setAutomaticTimeout(long delayMillis, Runnable timeoutRunnable) {
      if (this._dismissTimer != null) {
         this._dismissTimer.cancel();
      }

      this._dismissTimer = new CallDisplayDialog$DismissDialogTimer(this, delayMillis, timeoutRunnable);
   }

   private final void cancelAutomaticTimeout() {
      if (this._dismissTimer != null) {
         this._dismissTimer.cancel();
         this._dismissTimer = null;
      }
   }

   private final void assignVerbs(Verb[] verbs) {
      int[] verbAnsweringOptions = new int[verbs.length - 1];
      int answeringOptionsCount = 0;
      int menuVerbIndex = 0;
      this._menuVerbs = new Verb[verbs.length];

      for (int i = 0; i < verbs.length; i++) {
         Verb verb = verbs[i];
         this._menuVerbs[menuVerbIndex++] = verb;
         if (verb != null) {
            if (verb instanceof IgnoreCallVerb) {
               if (this._ignoreCallVerb == null) {
                  this._ignoreCallVerb = (IgnoreCallVerb)verb;
               }
            } else if (verb instanceof AnswerCallVerb) {
               AnswerCallVerb acv = (AnswerCallVerb)verb;
               int answeringOption = acv.getAnsweringOption();
               verbAnsweringOptions[answeringOptionsCount++] = answeringOption;
               if (answeringOption == 0 || answeringOption == 1 || answeringOption == 2) {
                  this._sendKeyVerb = acv;
               }
            }
         }
      }

      Array.resize(this._menuVerbs, menuVerbIndex);
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      boolean isTrackball = (status & 536870912) != 0;
      if (this._listener != null && !isTrackball) {
         this._listener.callDisplayStopRepeatNotification();
      }

      boolean result = super.navigationMovement(dx, dy, status, time);
      return isTrackball ? true : result;
   }

   @Override
   protected final boolean trackwheelRoll(int amount, int status, int time) {
      return false;
   }

   private static final PTTKeyHandler getPTTKeyHandler() {
      return ActivePhoneScreen.getPTTKeyHandler();
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      PTTKeyHandler pttKeyHandler = getPTTKeyHandler();
      if (pttKeyHandler != null && pttKeyHandler.isPTTKey(keycode)) {
         PhoneLogger.log("CDD.keydown()");
         this._pttKeyDown = true;
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   @Override
   protected final boolean keyUp(int keycode, int time) {
      PTTKeyHandler pttKeyHandler = getPTTKeyHandler();
      if (pttKeyHandler != null && pttKeyHandler.isPTTKey(keycode) && this._pttKeyDown) {
         PhoneLogger.log("CDD.keyup()");
         this._pttKeyDown = false;
         this._ignoreKeyRepeat = false;
         pttKeyHandler.onPTTKeyReleased();
         return true;
      } else {
         return super.keyUp(keycode, time);
      }
   }

   @Override
   public final boolean dispatchKeyEvent(int event, char key, int keycode, int time) {
      if (event == 515) {
         PTTKeyHandler hdlr = getPTTKeyHandler();
         if (hdlr == null || !hdlr.isPTTKey(keycode)) {
            return true;
         }
      }

      if (!PhoneUtilities.isMuteKey(keycode) && key != 150 && key != 151) {
         switch (Keypad.key(keycode)) {
            case 16:
               break;
            case 17:
               if (this._sendKeyVerb != null) {
                  this._sendKeyVerb.invoke(null);
                  this._sendKeyVerb = null;
                  return true;
               }
               break;
            case 18:
            default:
               if (this._ignoreCallVerb != null) {
                  this._ignoreCallVerb.invoke(null);
                  this.dismiss();
                  return true;
               }
         }

         return super.dispatchKeyEvent(event, key, keycode, time);
      } else {
         if (this._listener != null) {
            this._listener.callDisplayStopRepeatNotification();
         }

         return true;
      }
   }

   @Override
   protected final boolean keyRepeat(int keycode, int time) {
      PTTKeyHandler pttKeyHandler = getPTTKeyHandler();
      if (pttKeyHandler != null && pttKeyHandler.isPTTKey(keycode) && !this._ignoreKeyRepeat) {
         this._ignoreKeyRepeat = true;
         PhoneLogger.log("CDD.keyrepeat()");
         LiveCall currCall = (LiveCall)CallManager.getInstance().getCurrentCall();
         pttKeyHandler.onPTTKeyPressedAndHeld(currCall);
         return true;
      } else {
         return super.keyRepeat(keycode, time);
      }
   }

   @Override
   public final void close() {
      this.closeCallDisplay(true);
   }

   private final void closeCallDisplay(boolean invokeButtonVerb) {
      if (this._listener != null) {
         this._listener.callDisplayClosed();
      }

      this.cancelAutomaticTimeout();
      if (invokeButtonVerb) {
         Field selectedButton = this.getLeafFieldWithFocus();
         if (selectedButton != null) {
            Object cookie = selectedButton.getCookie();
            if (cookie instanceof Verb) {
               if (cookie != this._ignoreCallVerb) {
                  ((VoiceApp)this._app).preCallAnswer();
               }

               ((Verb)cookie).invoke(this._context);
            }
         }
      }

      this._isClosed = true;
      this._app.dismissStatus(this);
   }

   public final void show() {
      Screen screen = this;
      this._app.invokeLater(new CallDisplayDialog$1(this, screen));
   }

   public final void dismiss() {
      this.closeCallDisplay(false);
   }

   @Override
   protected final void sublayout(int width, int height) {
      this.setPositionDelegate(0, 0);
      int x = 0;
      int y = 0;
      int dlgHeight = _screenHeight - this.getBorderTop() - this.getBorderBottom() - this.getPaddingTop() - this.getPaddingBottom();
      int dlgWidth = _screenWidth - this.getBorderLeft() - this.getBorderRight() - this.getPaddingLeft() - this.getPaddingRight();
      this.layoutDelegate(dlgWidth, dlgHeight);
      this.setPosition(x, y);
      this.setExtent(this.getDelegate().getWidth(), this.getDelegate().getHeight());
   }
}
