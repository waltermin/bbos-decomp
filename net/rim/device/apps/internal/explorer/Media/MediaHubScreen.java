package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.ui.MediaField;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.explorer.Media.verbs.MediaVerb;
import net.rim.device.apps.internal.explorer.MediaLibrary.ContextInfo;
import net.rim.device.apps.internal.explorer.file.menu.OptionsMenuItem;
import net.rim.device.apps.internal.explorer.file.menu.ReceiveBluetooth;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.system.InternalServices;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.NodeImpl;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;
import net.rim.plazmic.internal.mediaengine.service.node.TextNode;
import net.rim.plazmic.mediaengine.MediaListener;
import net.rim.plazmic.mediaengine.MediaManager;
import net.rim.plazmic.mediaengine.MediaPlayer;

public final class MediaHubScreen extends AppsMainScreen implements MediaListener, ActionProvider {
   private MediaField _mediaField;
   private MediaPlayer _mediaPlayer;
   private MediaManager _mediaManager;
   private ModelInteractorImpl _model;
   private FocusInteractor _focusInteractor;
   private TextNode _text;
   private String _focus;
   private ContextInfo _context;
   private static final String MUSIC = "hs1";
   private static final String VIDEO = "hs2";
   private static final String RINGTONES = "hs3";
   private static final String PICTURES = "hs4";
   private static final String VOICENOTES = "hs5";

   public MediaHubScreen(ContextInfo context) {
      super(0);
      this._context = context == null ? new ContextInfo() : context;
      this.initScreen();
      this.setHelp(32247);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void initScreen() {
      this._mediaField = new MediaField(18014398512627712L);
      this._mediaManager = new MediaManager();
      this._mediaPlayer = new MediaPlayer();
      ThemeAttributeSet svg = ThemeManager.getActiveTheme().getAttributeSet(ThemeUtilities.MEDIA_HUB_TAG);
      String content = null;
      if (svg != null) {
         content = svg.getLayoutName();
         boolean var6 = false /* VF: Semaphore variable */;

         label36:
         try {
            var6 = true;
            this._mediaPlayer.setUI(this._mediaField);
            Object e = this._mediaManager.createMedia(content);
            if (e instanceof ModelInteractorImpl) {
               this._model = (ModelInteractorImpl)e;
               this._mediaPlayer.setMedia(this._model);
               this._mediaPlayer.setInternalMediaListener(this);
               this._text = (TextNode)this._model.getNode("title");
               MediaServices services = (MediaServices)this._mediaPlayer.getServices();
               this._focusInteractor = (FocusInteractor)services.getService("FocusInteractor");
               this._focusInteractor.setDefaultItem(this._model.getHandle("hs1"));
            }

            this._mediaPlayer.start();
            if (this._focusInteractor != null) {
               if (this._model != null) {
                  this._focusInteractor.setFocusToItem(this._model.getHandle("hs1"));
                  this.setTextForFocus("hs1");
                  var6 = false;
               } else {
                  var6 = false;
               }
            } else {
               var6 = false;
            }
         } finally {
            if (var6) {
               System.out.println("Error in mediaEngine");
               break label36;
            }
         }

         this.add(this._mediaField);
      }
   }

   @Override
   public final void mediaEvent(Object sender, int event, int eventParam, Object data) {
      if (event == 103) {
         if (eventParam != 0) {
            this._focus = NodeImpl.getId(eventParam, this._model);
         }

         this.setTextForFocus(this._focus);
      }
   }

   private final void setTextForFocus(String focus) {
      if (focus != null) {
         char[] charArray = null;
         if (focus.equals("hs1")) {
            if (this.isSDCardSupported()) {
               charArray = ExplorerResources.getString(143).toCharArray();
            } else {
               charArray = ExplorerResources.getString(138).toCharArray();
            }
         } else if (focus.equals("hs2")) {
            if (this.isSDCardSupported()) {
               charArray = ExplorerResources.getString(139).toCharArray();
            } else {
               charArray = ExplorerResources.getString(150).toCharArray();
            }
         } else if (focus.equals("hs3")) {
            charArray = ExplorerResources.getString(138).toCharArray();
         } else if (focus.equals("hs4")) {
            charArray = ExplorerResources.getString(150).toCharArray();
         } else if (focus.equals("hs5")) {
            charArray = ExplorerResources.getString(174).toCharArray();
         }

         if (charArray != null) {
            this._text.setString(charArray);
            this._mediaField.invalidate();
         }
      }
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      if (super.navigationClick(status, time)) {
         return true;
      } else {
         return (status & 1073741824) != 0 ? false : this.invoke();
      }
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (c == 27) {
         this.close();
         return true;
      } else if (c == '\n') {
         this.invoke();
         return true;
      } else {
         return super.keyChar(c, status, time);
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      MediaVerb view = new MediaVerb(6099736323056465049L, this, 569345, ExplorerResources.getResourceBundleFamily(), 157);
      menu.add(view);
      menu.add(new OptionsMenuItem());
      if (MediaUtilities.checkBluetoothPolicy()) {
         menu.add(new ReceiveBluetooth());
      }

      Verb explore = new MediaHubScreen$BrowseMediaVerb();
      menu.add(explore);
      if (Trackball.isSupported()) {
         menu.setDefault(explore);
      } else {
         menu.setDefault(view);
      }
   }

   @Override
   public final boolean perform(long actionId, Object context) {
      if (actionId == 6099736323056465049L) {
         this.invoke();
         return true;
      } else {
         return false;
      }
   }

   private final boolean invoke() {
      if (this._focus == null && !this.isMusicInFocus()) {
         return false;
      }

      Screen screen = null;
      if (!"hs1".equals(this._focus) && !this.isMusicInFocus()) {
         if ("hs2".equals(this._focus)) {
            if (this.isSDCardSupported()) {
               screen = new VideoLibraryScreen(this._context);
            } else {
               screen = new PictureLibraryScreen(this._context);
            }
         } else if ("hs3".equals(this._focus)) {
            screen = new RingtoneLibraryScreen(this._context);
         } else if ("hs4".equals(this._focus)) {
            screen = new PictureLibraryScreen(this._context);
         } else if ("hs5".equals(this._focus)) {
            ContextInfo contextInfo = ContextInfo.createCopy(this._context);
            contextInfo.setType(256);
            screen = new TrackListScreen(contextInfo);
         }
      } else if (this.isSDCardSupported()) {
         screen = new MusicLibraryScreen(this._context);
      } else {
         screen = new RingtoneLibraryScreen(this._context);
      }

      if (screen != null) {
         UiApplication.getUiApplication().pushScreen(screen);
      }

      return true;
   }

   private final boolean isMusicInFocus() {
      if (this._focusInteractor != null && this._model != null) {
         int musicHandle = this._model.getHandle("hs1");
         int focus = this._focusInteractor.getItemInFocus();
         return musicHandle == focus;
      } else {
         return false;
      }
   }

   private final boolean isSDCardSupported() {
      return InternalServices.isDeviceCapable(19);
   }
}
