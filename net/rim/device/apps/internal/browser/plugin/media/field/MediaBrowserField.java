package net.rim.device.apps.internal.browser.plugin.media.field;

import java.io.InputStream;
import java.util.Random;
import javax.microedition.io.InputConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemListener;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VideoControl;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.media.protocol.DataSource;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.browser.field.ContentReadEvent;
import net.rim.device.api.browser.field.Destroyable;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.browser.field.ResourceProvider;
import net.rim.device.api.browser.plugin.BrowserPageContext;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.media.control.AudioPathControl;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.Audio;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.AudioSinkCallback;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.api.utility.framework.VerbToMenuFactory;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.apps.internal.browser.plugin.media.menu.FullscreenMenuItem;
import net.rim.device.apps.internal.browser.plugin.media.menu.RepeatMenuItem;
import net.rim.device.apps.internal.browser.plugin.media.menu.ReplayMenuItem;
import net.rim.device.apps.internal.browser.plugin.media.menu.SaveMenuItem;
import net.rim.device.apps.internal.browser.plugin.media.menu.SetAsRingtoneMenuItem;
import net.rim.device.apps.internal.browser.plugin.media.menu.SkipTrackMenuItem;
import net.rim.device.apps.internal.browser.plugin.media.menu.TogglePlaylistViewMenuItem;
import net.rim.device.apps.internal.browser.plugin.media.menu.ToggleShuffleMenuItem;
import net.rim.device.apps.internal.browser.plugin.media.util.MediaDataSource;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.RTSPConnection;
import net.rim.device.apps.internal.browser.util.QuincyUtil;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.io.file.MetaDataBookmark;
import net.rim.device.internal.media.HTTPBufferingCallback;
import net.rim.device.internal.media.HTTPBufferingManager;
import net.rim.device.internal.media.HTTPDataSource;
import net.rim.device.internal.media.MediaOptionsRegistry;
import net.rim.device.internal.media.MediaPlayerStateInstance;
import net.rim.device.internal.media.MediaPlayerStateProvider;
import net.rim.device.internal.media.MediaRemoteControl;
import net.rim.device.internal.media.Playlist;
import net.rim.device.internal.media.RTSPDataSourceFactory;
import net.rim.device.internal.media.StreamDataControl;
import net.rim.device.internal.system.ActiveMedia;
import net.rim.device.internal.system.ActiveMediaObservable;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.UiSettings;
import net.rim.device.internal.vad.VADUserEvents;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.NodeImpl;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;
import net.rim.plazmic.internal.mediaengine.service.node.ImageNode;
import net.rim.plazmic.internal.mediaengine.service.node.Node;
import net.rim.plazmic.internal.mediaengine.service.node.SVGNode;
import net.rim.plazmic.internal.mediaengine.service.node.TextNode;
import net.rim.plazmic.internal.mediaengine.service.node.ViewportNode;
import net.rim.plazmic.internal.mediaengine.service.node.VisualNode;
import net.rim.plazmic.mediaengine.MediaListener;
import net.rim.plazmic.mediaengine.MediaPlayer;

public final class MediaBrowserField
   extends VerticalFieldManager
   implements MediaListener,
   FieldChangeListener,
   BrowserPageContext,
   PlayerListener,
   SystemListener2,
   FileSystemListener,
   Destroyable,
   HTTPBufferingCallback,
   AudioSinkCallback,
   MediaPlayerStateProvider {
   private EncodedImage PLAY_BMP;
   private EncodedImage PLAY_H_BMP;
   private EncodedImage PAUSE_BMP;
   private EncodedImage PAUSE_H_BMP;
   private String _title;
   private TextOverlayFieldManager _screenManager;
   private BitmapField _videoPauseBitmap;
   private VolumeField _volumeField;
   private SeekablePositionField _seekSliderField;
   private MediaBrowserField$MyKeyHandler _myKeyHandler;
   private MediaBrowserField$MyMediaPlayerState _myMediaPlayerState;
   private String _contentType;
   private boolean _drmForwardLock;
   private String _tuneName;
   private char[] _totaltimeString;
   private boolean _closed;
   private RequestedResource _activeResourceRequest;
   private ContentReadEvent _contentReadEvent;
   private boolean _embedded;
   private boolean _isAudio;
   private boolean _visible;
   private boolean _wasPlayingOnObscured;
   private boolean _paused;
   private boolean _playing;
   private boolean _errorOccured;
   private boolean _fileBased;
   private String _currentUrl;
   private boolean _playWhenStreamingReady;
   private boolean _wasPlaying;
   private boolean _trackForward;
   private boolean _newPlayer;
   private Player _player;
   private VideoControl _videoControl;
   private VolumeControl _volumeControl;
   private AudioRouter _audioRouter;
   private AudioPathControl _audioPathControl;
   private DataSource _ds;
   private PlaylistField _playlistField;
   private MediaBrowserField$PlaylistThread _playlistThread;
   private int[] _audioProperties;
   private int _destroyableState;
   private RenderingOptions _renderingOptions;
   private Application _application;
   private MyMediaField _mediaFieldBar;
   private MediaPlayer _pmePlayerBar;
   private MediaBrowserField$MyMediaManager _managerBar;
   private ModelInteractorImpl _modelBar;
   private CustomFocusOrder _focusManager;
   private FocusInteractor _focusInteractor;
   private StringBuffer _buffer;
   private TextNode _durationNode;
   private TextNode _totaltimeNode;
   private ImageNode _playButton;
   private ImageNode _playButtonHighlight;
   private boolean _queuedPlayback;
   private CalendarExtensions _calendar;
   private SimpleDateFormat _formatShort;
   private SimpleDateFormat _formatLong;
   private String _focus;
   private boolean _fullscreen;
   private final MediaBrowserField$FullscreenRunnable _fullscreenRunnable;
   private Playlist _playlist;
   private int[] _playlistOrder;
   private boolean _shuffled;
   private boolean _repeat;
   private int _playlistCurrentIndex;
   private boolean _requiresReinitialization;
   private boolean _singleFilePlayback;
   private BrowserContentBaseImpl _browserContent;
   private ApplicationProcess _process;
   private Runnable _cleanupRunnable;
   private char _hotkeyNext;
   private char _hotkeyPrevious;
   private long _startMediaTime;
   private static final Tag MEDIA_BAR_TAG = Tag.create("media-player-bar");
   private static final Tag SCREEN_TAG = Tag.create("media-screen");
   private static final Tag EMBEDDED_AUDIO_TAG = Tag.create("media-player-audio-embedded");
   private static Tag PROPERTY_TAG = Tag.create("media-property");
   private static Tag TRACK_TITLE_TAG = Tag.create("media-track-title");
   private static final int DESIRED_ALBUM_WIDTH;
   private static final int DESIRED_ALBUM_HEIGHT;
   private static final int TWO_SECONDS;
   private static final int DEFAULT_TRACK_INDEX;
   private static final EncodedImage _noArt = ThemeManager.getActiveTheme().getImage("noart");
   private static final String BUTTON_PLAY_IMAGE;
   private static final String BUTTON_STOP_IMAGE;
   private static final String BUTTON_PLAY;
   private static final String BUTTON_PAUSE;
   private static final String BUTTON_STOP;
   private static final String BUTTON_MENU;
   private static final String BUTTON_NEXT;
   private static final String BUTTON_PREVIOUS;
   private static final String SLIDERBAR;
   private static final String LINE_ANIM;
   private static final String VOLUMESLIDER;

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected final Field initPlayerField(boolean embedded, RenderingApplication renderApp) {
      try {
         Object mediaBar = null;
         String mediaNameBar;
         if (this._isAudio && embedded) {
            ThemeAttributeSet tas = this.getThemeAttributeSet();
            if (tas != null) {
               mediaNameBar = tas.getLayoutName();
            } else {
               mediaNameBar = "cod://net_rim_bb_browser_daemon/media_player_embed.pme";
            }
         } else {
            ThemeAttributeSet barSet = ThemeManager.getActiveTheme().getAttributeSet(MEDIA_BAR_TAG);
            if (barSet == null) {
               mediaNameBar = "cod://net_rim_bb_browser_daemon/media_player_bar.pme";
            } else {
               mediaNameBar = barSet.getLayoutName();
            }
         }

         this._focusManager = new CustomFocusOrder();
         this._mediaFieldBar = new MyMediaField(this, 18014398512627712L);
         this._managerBar = new MediaBrowserField$MyMediaManager(this);
         this._pmePlayerBar = (MediaPlayer)(new Object());
         this._pmePlayerBar.setUI(this._mediaFieldBar);
         mediaBar = this._managerBar.createMedia(mediaNameBar);
         if (!(mediaBar instanceof Object)) {
            System.out.println("Warning: Only PME 1.2 is supported");
            return null;
         }

         this._modelBar = (ModelInteractorImpl)mediaBar;
         this._pmePlayerBar.setMedia(this._modelBar);
         this._pmePlayerBar.setInternalMediaListener(this);
         Object services = this._pmePlayerBar.getServices();
         if (services instanceof Object) {
            MediaServices mediaServices = (MediaServices)services;
            this._focusInteractor = (FocusInteractor)mediaServices.getService("FocusInteractor");
            this._focusInteractor.setDefaultItem(this._modelBar.getHandle("play"));
            this._focusInteractor.setFocusToItem(this._modelBar.getHandle("play"));
         }

         if (embedded) {
            this._focusManager = null;
            this.PLAY_BMP = ThemeManager.getActiveTheme().getImage("play_embed");
            this.PLAY_H_BMP = ThemeManager.getActiveTheme().getImage("play_highlight_embed");
            this.PAUSE_BMP = ThemeManager.getActiveTheme().getImage("pause_embed");
            this.PAUSE_H_BMP = ThemeManager.getActiveTheme().getImage("pause_highlight_embed");
         } else {
            this._focusManager.setModel(this._modelBar);
            this._mediaFieldBar.setFocusManager(this._focusManager);
            this._focusManager.setFocusInteractor(this._focusInteractor);
            this._durationNode = (TextNode)this._modelBar.getNode("duration");
            this._totaltimeNode = (TextNode)this._modelBar.getNode("totaltime");
            ImageNode barNode = (ImageNode)this._modelBar.getNode("sliderbar");
            ImageNode sliderNode = (ImageNode)this._modelBar.getNode("slider");
            VisualNode streamingNode = (VisualNode)this._modelBar.getNode("slider_streaming_buffer");
            VisualNode sliderTrailNode = (VisualNode)this._modelBar.getNode("slider_trail_cover");
            ImageNode volumeHighlightNode = (ImageNode)this._modelBar.getNode("volumefocused");
            this._seekSliderField.setMediaField(this._mediaFieldBar);
            this._seekSliderField.setBarNode(barNode);
            this._seekSliderField.setSliderNode(sliderNode);
            this._seekSliderField.setStreamingNode(streamingNode);
            this._seekSliderField.setSliderTrailNode(sliderTrailNode);
            this._volumeField.setMediaField(this._mediaFieldBar);
            this._volumeField.setModelInteractor(this._modelBar);
            this._volumeField.setVolumeHighlight(volumeHighlightNode);
            this._volumeField.setChangeListener(this);
            this.PLAY_BMP = ThemeManager.getActiveTheme().getImage("play");
            this.PLAY_H_BMP = ThemeManager.getActiveTheme().getImage("play_highlight");
            this.PAUSE_BMP = ThemeManager.getActiveTheme().getImage("pause");
            this.PAUSE_H_BMP = ThemeManager.getActiveTheme().getImage("pause_highlight");
         }

         this._playButton = (ImageNode)this._modelBar.getNode("playbutton");
         this._playButtonHighlight = (ImageNode)this._modelBar.getNode("playbutton_h");
         if (!embedded) {
            this._screenManager.setTag(SCREEN_TAG);
            this.add(this._screenManager);
         }

         this.add(this._mediaFieldBar);
         return this._mediaFieldBar;
      } catch (Throwable var13) {
         QuincyUtil.sendQuincy(t, false);
         return null;
      }
   }

   public final void skipTrack(boolean forward) {
      this._wasPlayingOnObscured = false;
      this._trackForward = forward;
      if (!forward) {
         if (this._playlistCurrentIndex <= 1) {
            return;
         }

         this._playlistCurrentIndex -= 2;
      }

      this.invalidateScreen();
      this._fullscreen = false;
      this._fullscreenRunnable.halt();
      this._playlistThread.nextItem();
   }

   public final void internalFieldChanged(Object field, int context) {
      if (field == this._playlistField && context != Integer.MIN_VALUE) {
         this._playlistCurrentIndex = Arrays.getIndex(this._playlistOrder, this._playlistField.getSelectedIndex());
         this._playlistThread.nextItem();
      } else {
         if (field == this._volumeField) {
            int newVolume = this._volumeField.getVolumeLevel();
            int audioPath = this._audioPathControl.getAudioPath();
            this._volumeControl.setLevel(newVolume);
            GeneralProperty.setCurrentProperty(this._audioProperties[audioPath], newVolume);
         }
      }
   }

   public final void externalToggleFullscreen() {
      boolean fullscreen = MediaOptionsRegistry.getInstance().getBoolean(-4212305096992551720L);
      fullscreen = !fullscreen;
      MediaOptionsRegistry.getInstance().setBoolean(-4212305096992551720L, fullscreen);
      if (this._player != null && this._player.getState() == 400 && (fullscreen || !fullscreen && this.isFullscreen())) {
         this.toggleFullscreen();
      }
   }

   public final boolean isFullscreen() {
      return this._fullscreen && this._player != null && this._player.getState() == 400;
   }

   public final void toggleRepeat() {
      this._repeat = !this._repeat;
      this._modelBar.trigger(107, this._modelBar.getHandle(this._repeat ? "repeatOn" : "repeatOff"), null);
      MediaOptionsRegistry.getInstance().setBoolean(8428678949875667391L, this._repeat);
   }

   public final void replay() {
      this._queuedPlayback = false;
      this._wasPlayingOnObscured = false;
      this.stop(true);
      this.play(Application.isEventDispatchThread());
   }

   public final void toggleShuffle() {
      if (this._shuffled) {
         if (this._playlistOrder[this._playlistCurrentIndex - 1] < this._playlistOrder.length - 1 && this._playlistCurrentIndex > 0) {
            this._playlistCurrentIndex = this._playlistOrder[this._playlistCurrentIndex - 1];
            this._playlistCurrentIndex++;
         } else {
            this._playlistCurrentIndex = 0;
         }

         this.setSortedPlaylistOrder();
      } else {
         this.setShuffledPlaylistOrder();
         this._playlistCurrentIndex = 1;
      }

      this._modelBar.trigger(107, this._modelBar.getHandle(this._shuffled ? "shuffleOn" : "shuffleOff"), null);
      if (MediaOptionsRegistry.getInstance().getBoolean(-2846908971875712627L)) {
         MediaOptionsRegistry.getInstance().setBoolean(-2846908971875712627L, false);
      } else {
         MediaOptionsRegistry.getInstance().setBoolean(-7071091113559016691L, this._shuffled);
      }
   }

   public final void togglePlaylist() {
      boolean wasShown = this.isPlaylistShown();
      this._screenManager.deleteAll();
      if (wasShown) {
         this._screenManager.setActiveView(this._isAudio ? 0 : 2);
      } else {
         if (!this._isAudio && this._player != null && this._player.getState() == 400) {
            label36:
            try {
               this._player.stop();
            } finally {
               break label36;
            }
         }

         this._wasPlayingOnObscured = false;
         this._screenManager.setActiveView(1);
      }
   }

   public final boolean isPlaylistShown() {
      return this._playlistField.getManager() != null;
   }

   public final boolean isAudio() {
      return this._isAudio;
   }

   public final HTTPBufferingManager getBufferingManager() {
      return !(this._ds instanceof Object) ? null : ((HTTPDataSource)this._ds).getBufferingManager();
   }

   public final BrowserContent getBrowserContent() {
      return this._browserContent;
   }

   public final boolean isShuffled() {
      return this._shuffled;
   }

   final void updateDurationField(int milliseconds) {
      if (this._durationNode != null) {
         this._durationNode.setString(this.getTimeDisplay(milliseconds));
         this._mediaFieldBar.invalidate();
      }
   }

   @Override
   public final boolean getPropertyWithBooleanValue(int id, boolean defaultValue) {
      return defaultValue;
   }

   @Override
   public final String getPropertyWithStringValue(int id, String defaultValue) {
      return defaultValue;
   }

   @Override
   public final int getPropertyWithIntValue(int id, int defaultValue) {
      return id == 2 ? 2 : defaultValue;
   }

   @Override
   public final Object getPropertyWithObjectValue(int id, Object defaultValue) {
      return defaultValue;
   }

   @Override
   public final void playerUpdate(Player param1, String param2, Object param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: aload 0
      // 002: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 005: if_acmpeq 009
      // 008: return
      // 009: ldc_w "endOfMedia"
      // 00c: aload 2
      // 00d: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 010: ifeq 095
      // 013: aload 0
      // 014: bipush 0
      // 015: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._paused Z
      // 018: aload 0
      // 019: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.invalidateScreen ()V
      // 01c: aload 0
      // 01d: bipush 0
      // 01e: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._fullscreen Z
      // 021: aload 0
      // 022: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._seekSliderField Lnet/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField;
      // 025: bipush 0
      // 026: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField.updateTime (I)V
      // 029: aload 0
      // 02a: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.resetStreamingOnEOM ()Z
      // 02d: ifeq 03b
      // 030: aload 0
      // 031: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._seekSliderField Lnet/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField;
      // 034: nop
      // 035: ldc2_w 0
      // 038: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField.updateStreaming (D)V
      // 03b: aload 0
      // 03c: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._durationNode Lnet/rim/plazmic/internal/mediaengine/service/node/TextNode;
      // 03f: ifnull 057
      // 042: aload 0
      // 043: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._durationNode Lnet/rim/plazmic/internal/mediaengine/service/node/TextNode;
      // 046: aload 0
      // 047: bipush 0
      // 048: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.getTimeDisplay (I)[C
      // 04b: invokeinterface net/rim/plazmic/internal/mediaengine/service/node/TextNode.setString ([C)V 2
      // 050: aload 0
      // 051: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._mediaFieldBar Lnet/rim/device/apps/internal/browser/plugin/media/field/MyMediaField;
      // 054: invokevirtual net/rim/device/api/ui/MediaField.invalidate ()V
      // 057: aload 0
      // 058: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.showPlay ()V
      // 05b: aload 0
      // 05c: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._isAudio Z
      // 05f: ifne 06a
      // 062: aload 0
      // 063: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._videoPauseBitmap Lnet/rim/device/api/ui/component/BitmapField;
      // 066: aconst_null
      // 067: invokevirtual net/rim/device/api/ui/component/BitmapField.setBitmap (Lnet/rim/device/api/system/Bitmap;)V
      // 06a: aload 0
      // 06b: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlistThread Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$PlaylistThread;
      // 06e: ifnull 082
      // 071: aload 0
      // 072: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._singleFilePlayback Z
      // 075: ifne 082
      // 078: aload 0
      // 079: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlistThread Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$PlaylistThread;
      // 07c: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$PlaylistThread.nextItem ()V
      // 07f: goto 090
      // 082: aload 0
      // 083: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._repeat Z
      // 086: ifeq 090
      // 089: aload 0
      // 08a: invokestatic net/rim/device/api/system/Application.isEventDispatchThread ()Z
      // 08d: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.play (Z)V
      // 090: aload 0
      // 091: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.resetAutoBacklight ()V
      // 094: return
      // 095: ldc_w "stopped"
      // 098: aload 2
      // 099: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 09c: ifeq 0ce
      // 09f: aload 0
      // 0a0: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.invalidateScreen ()V
      // 0a3: aload 0
      // 0a4: bipush 0
      // 0a5: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._fullscreen Z
      // 0a8: aload 0
      // 0a9: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 0ac: ifnull 0c9
      // 0af: aload 0
      // 0b0: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 0b3: invokeinterface javax/microedition/media/Player.getState ()I 1
      // 0b8: sipush 400
      // 0bb: if_icmpeq 0c9
      // 0be: aload 0
      // 0bf: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.showPlay ()V
      // 0c2: aload 0
      // 0c3: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._fullscreenRunnable Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$FullscreenRunnable;
      // 0c6: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$FullscreenRunnable.halt ()V
      // 0c9: aload 0
      // 0ca: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.resetAutoBacklight ()V
      // 0cd: return
      // 0ce: ldc_w "deviceUnavailable"
      // 0d1: aload 2
      // 0d2: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 0d5: ifeq 133
      // 0d8: aload 0
      // 0d9: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._fullscreen Z
      // 0dc: ifeq 0e3
      // 0df: aload 0
      // 0e0: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.toggleFullscreen ()V
      // 0e3: aload 0
      // 0e4: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._fullscreenRunnable Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$FullscreenRunnable;
      // 0e7: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$FullscreenRunnable.halt ()V
      // 0ea: aload 0
      // 0eb: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 0ee: ifnull 102
      // 0f1: aload 0
      // 0f2: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playing Z
      // 0f5: ifeq 102
      // 0f8: aload 0
      // 0f9: bipush 1
      // 0fa: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._paused Z
      // 0fd: aload 0
      // 0fe: bipush 1
      // 0ff: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._wasPlaying Z
      // 102: aload 0
      // 103: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.showPlay ()V
      // 106: aload 0
      // 107: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.resetAutoBacklight ()V
      // 10a: aload 0
      // 10b: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.getBufferingManager ()Lnet/rim/device/internal/media/HTTPBufferingManager;
      // 10e: astore 4
      // 110: aload 4
      // 112: ifnonnull 118
      // 115: goto 4a6
      // 118: aload 4
      // 11a: invokevirtual net/rim/device/internal/media/HTTPBufferingManager.bufferContainsAllContent ()Z
      // 11d: ifeq 123
      // 120: goto 4a6
      // 123: aload 0
      // 124: bipush 1
      // 125: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.stop (Z)V
      // 128: aload 0
      // 129: bipush 1
      // 12a: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._paused Z
      // 12d: aload 0
      // 12e: bipush 1
      // 12f: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._wasPlaying Z
      // 132: return
      // 133: ldc_w "deviceAvailable"
      // 136: aload 2
      // 137: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 13a: ifeq 17c
      // 13d: aload 0
      // 13e: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._paused Z
      // 141: ifne 147
      // 144: goto 4a6
      // 147: aload 0
      // 148: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._wasPlaying Z
      // 14b: ifne 151
      // 14e: goto 4a6
      // 151: aload 0
      // 152: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.canPlay ()Z
      // 155: ifne 15b
      // 158: goto 4a6
      // 15b: aload 0
      // 15c: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._isAudio Z
      // 15f: ifeq 16a
      // 162: aload 0
      // 163: invokestatic net/rim/device/api/system/Application.isEventDispatchThread ()Z
      // 166: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.play (Z)V
      // 169: return
      // 16a: aload 0
      // 16b: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._visible Z
      // 16e: ifeq 176
      // 171: aload 0
      // 172: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.resetMediaEngineToPlay ()V
      // 175: return
      // 176: aload 0
      // 177: bipush 1
      // 178: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._wasPlayingOnObscured Z
      // 17b: return
      // 17c: ldc_w "started"
      // 17f: aload 2
      // 180: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 183: ifeq 1e7
      // 186: aload 0
      // 187: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._newPlayer Z
      // 18a: ifeq 1c8
      // 18d: invokestatic net/rim/device/api/system/Application.getEventLock ()Ljava/lang/Object;
      // 190: dup
      // 191: astore 4
      // 193: monitorenter
      // 194: invokestatic net/rim/device/internal/media/MediaOptionsRegistry.getInstance ()Lnet/rim/device/internal/media/MediaOptionsRegistry;
      // 197: astore 5
      // 199: aload 5
      // 19b: ldc2_w -4387502259448276168
      // 19e: invokevirtual net/rim/device/internal/util/OptionsRegistry.getBoolean (J)Z
      // 1a1: ifne 1b5
      // 1a4: aload 5
      // 1a6: ldc2_w 2886183832722201160
      // 1a9: invokevirtual net/rim/device/internal/util/OptionsRegistry.getBoolean (J)Z
      // 1ac: ifeq 1b5
      // 1af: invokestatic net/rim/device/internal/media/MediaOptionsUtilities.getInstance ()Lnet/rim/device/internal/media/MediaOptionsUtilities;
      // 1b2: invokevirtual net/rim/device/internal/media/MediaOptionsUtilities.showBoostVolumeWarning ()V
      // 1b5: aload 4
      // 1b7: monitorexit
      // 1b8: goto 1c3
      // 1bb: astore 6
      // 1bd: aload 4
      // 1bf: monitorexit
      // 1c0: aload 6
      // 1c2: athrow
      // 1c3: aload 0
      // 1c4: bipush 0
      // 1c5: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._newPlayer Z
      // 1c8: aload 0
      // 1c9: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 1cc: ifnull 1e2
      // 1cf: aload 0
      // 1d0: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 1d3: invokeinterface javax/microedition/media/Player.getState ()I 1
      // 1d8: sipush 400
      // 1db: if_icmpne 1e2
      // 1de: aload 0
      // 1df: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.showPause ()V
      // 1e2: aload 0
      // 1e3: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.setAutoBacklight ()V
      // 1e6: return
      // 1e7: ldc_w "error"
      // 1ea: aload 2
      // 1eb: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 1ee: ifne 1f4
      // 1f1: goto 2ce
      // 1f4: aload 0
      // 1f5: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.invalidateScreen ()V
      // 1f8: aload 0
      // 1f9: bipush 0
      // 1fa: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._fullscreen Z
      // 1fd: aload 0
      // 1fe: bipush 0
      // 1ff: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._wasPlayingOnObscured Z
      // 202: aload 0
      // 203: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._fullscreenRunnable Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$FullscreenRunnable;
      // 206: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$FullscreenRunnable.halt ()V
      // 209: aload 3
      // 20a: instanceof java/lang/Object
      // 20d: ifne 213
      // 210: goto 2c9
      // 213: bipush -1
      // 215: istore 4
      // 217: aload 3
      // 218: checkcast java/lang/Object
      // 21b: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 21e: istore 4
      // 220: goto 226
      // 223: astore 5
      // 225: return
      // 226: aconst_null
      // 227: astore 5
      // 229: iload 4
      // 22b: lookupswitch 75 2 1 25 6 36
      // 244: sipush 843
      // 247: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 24a: astore 5
      // 24c: goto 27e
      // 24f: aload 0
      // 250: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.mayFinishAfterHTTPBuffering ()Z
      // 253: ifeq 26b
      // 256: aload 0
      // 257: bipush 1
      // 258: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playWhenStreamingReady Z
      // 25b: sipush 907
      // 25e: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 261: astore 6
      // 263: aload 6
      // 265: invokestatic net/rim/device/api/ui/component/Dialog.alert (Ljava/lang/String;)V
      // 268: goto 27e
      // 26b: sipush 844
      // 26e: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 271: astore 5
      // 273: goto 27e
      // 276: sipush 845
      // 279: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 27c: astore 5
      // 27e: aload 5
      // 280: ifnull 2c9
      // 283: aload 0
      // 284: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._screenManager Lnet/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager;
      // 287: sipush 852
      // 28a: ldc_w 16777215
      // 28d: ldc_w 16711680
      // 290: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager.setOverlayText (III)V
      // 293: aload 0
      // 294: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.showPlay ()V
      // 297: aload 0
      // 298: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlistThread Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$PlaylistThread;
      // 29b: ifnull 2b4
      // 29e: aload 0
      // 29f: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._singleFilePlayback Z
      // 2a2: ifne 2b4
      // 2a5: aload 5
      // 2a7: invokestatic net/rim/device/api/ui/component/Status.show (Ljava/lang/String;)V
      // 2aa: aload 0
      // 2ab: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlistThread Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$PlaylistThread;
      // 2ae: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$PlaylistThread.nextItem ()V
      // 2b1: goto 2c9
      // 2b4: aload 5
      // 2b6: invokestatic net/rim/device/api/ui/component/Dialog.alert (Ljava/lang/String;)V
      // 2b9: aload 0
      // 2ba: aload 0
      // 2bb: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlistThread Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$PlaylistThread;
      // 2be: ifnonnull 2c5
      // 2c1: bipush 1
      // 2c2: goto 2c6
      // 2c5: bipush 0
      // 2c6: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._errorOccured Z
      // 2c9: aload 0
      // 2ca: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.resetAutoBacklight ()V
      // 2cd: return
      // 2ce: ldc_w "durationUpdated"
      // 2d1: aload 2
      // 2d2: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 2d5: ifeq 335
      // 2d8: aload 3
      // 2d9: dup
      // 2da: instanceof java/lang/Object
      // 2dd: ifne 2e4
      // 2e0: pop
      // 2e1: goto 4a6
      // 2e4: checkcast java/lang/Object
      // 2e7: invokevirtual java/lang/Long.longValue ()J
      // 2ea: lstore 4
      // 2ec: lload 4
      // 2ee: bipush -1
      // 2f0: i2l
      // 2f1: lcmp
      // 2f2: ifne 2f8
      // 2f5: goto 4a6
      // 2f8: lload 4
      // 2fa: sipush 1000
      // 2fd: i2l
      // 2fe: ldiv
      // 2ff: lstore 4
      // 301: aload 0
      // 302: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._seekSliderField Lnet/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField;
      // 305: lload 4
      // 307: l2i
      // 308: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField.setTotalTime (I)V
      // 30b: aload 0
      // 30c: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._totaltimeNode Lnet/rim/plazmic/internal/mediaengine/service/node/TextNode;
      // 30f: ifnonnull 315
      // 312: goto 4a6
      // 315: aload 0
      // 316: aload 0
      // 317: lload 4
      // 319: l2i
      // 31a: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.getTimeDisplay (I)[C
      // 31d: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._totaltimeString [C
      // 320: aload 0
      // 321: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._totaltimeNode Lnet/rim/plazmic/internal/mediaengine/service/node/TextNode;
      // 324: aload 0
      // 325: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._totaltimeString [C
      // 328: invokeinterface net/rim/plazmic/internal/mediaengine/service/node/TextNode.setString ([C)V 2
      // 32d: aload 0
      // 32e: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._mediaFieldBar Lnet/rim/device/apps/internal/browser/plugin/media/field/MyMediaField;
      // 331: invokevirtual net/rim/device/api/ui/MediaField.invalidate ()V
      // 334: return
      // 335: aload 2
      // 336: ldc_w "com.rim.seekableUpdate"
      // 339: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 33c: ifeq 35d
      // 33f: aload 3
      // 340: dup
      // 341: instanceof java/lang/Object
      // 344: ifne 34b
      // 347: pop
      // 348: goto 4a6
      // 34b: checkcast java/lang/Object
      // 34e: invokevirtual java/lang/Boolean.booleanValue ()Z
      // 351: istore 4
      // 353: aload 0
      // 354: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._seekSliderField Lnet/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField;
      // 357: iload 4
      // 359: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField.setSeekable (Z)V
      // 35c: return
      // 35d: aload 2
      // 35e: ldc_w "com.rim.timeUpdate"
      // 361: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 364: ifeq 3cd
      // 367: aload 0
      // 368: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 36b: ifnonnull 371
      // 36e: goto 4a6
      // 371: aload 0
      // 372: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 375: invokeinterface javax/microedition/media/Player.getState ()I 1
      // 37a: sipush 400
      // 37d: if_icmpeq 383
      // 380: goto 4a6
      // 383: aload 0
      // 384: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._isAudio Z
      // 387: ifne 38e
      // 38a: bipush 1
      // 38b: invokestatic net/rim/device/api/system/Backlight.enable (Z)V
      // 38e: aload 0
      // 38f: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.isFullscreen ()Z
      // 392: ifeq 396
      // 395: return
      // 396: aload 3
      // 397: dup
      // 398: instanceof java/lang/Object
      // 39b: ifne 3a2
      // 39e: pop
      // 39f: goto 4a6
      // 3a2: checkcast java/lang/Object
      // 3a5: invokevirtual java/lang/Long.longValue ()J
      // 3a8: sipush 1000
      // 3ab: i2l
      // 3ac: ldiv
      // 3ad: l2i
      // 3ae: istore 4
      // 3b0: aload 0
      // 3b1: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._seekSliderField Lnet/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField;
      // 3b4: iload 4
      // 3b6: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField.updateTime (I)V
      // 3b9: aload 0
      // 3ba: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._seekSliderField Lnet/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField;
      // 3bd: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField.isEnabled ()Z
      // 3c0: ifeq 3c6
      // 3c3: goto 4a6
      // 3c6: aload 0
      // 3c7: iload 4
      // 3c9: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.updateDurationField (I)V
      // 3cc: return
      // 3cd: aload 2
      // 3ce: ldc_w "com.rim.pauseBitmap"
      // 3d1: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 3d4: ifeq 3ff
      // 3d7: aload 0
      // 3d8: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._fullscreen Z
      // 3db: ifeq 3df
      // 3de: return
      // 3df: aload 0
      // 3e0: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._paused Z
      // 3e3: ifne 3e9
      // 3e6: goto 4a6
      // 3e9: aload 3
      // 3ea: instanceof java/lang/Object
      // 3ed: ifne 3f3
      // 3f0: goto 4a6
      // 3f3: aload 0
      // 3f4: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._videoPauseBitmap Lnet/rim/device/api/ui/component/BitmapField;
      // 3f7: aload 3
      // 3f8: checkcast java/lang/Object
      // 3fb: invokevirtual net/rim/device/api/ui/component/BitmapField.setBitmap (Lnet/rim/device/api/system/Bitmap;)V
      // 3fe: return
      // 3ff: aload 2
      // 400: ldc_w "com.rim.mediaLoaded"
      // 403: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 406: ifeq 46d
      // 409: aload 0
      // 40a: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._screenManager Lnet/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager;
      // 40d: bipush -1
      // 40f: bipush 0
      // 410: bipush 0
      // 411: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager.setOverlayText (III)V
      // 414: aload 0
      // 415: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._startMediaTime J
      // 418: bipush 0
      // 419: i2l
      // 41a: lcmp
      // 41b: ifeq 4a6
      // 41e: aload 0
      // 41f: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 422: aload 0
      // 423: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._startMediaTime J
      // 426: invokeinterface javax/microedition/media/Player.setMediaTime (J)J 3
      // 42b: pop2
      // 42c: goto 466
      // 42f: astore 4
      // 431: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 434: new java/lang/Object
      // 437: dup
      // 438: ldc_w "media error setting bookmark: "
      // 43b: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 43e: aload 4
      // 440: invokevirtual java/lang/StringBuffer.append (Ljava/lang/Object;)Ljava/lang/StringBuffer;
      // 443: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 446: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 449: goto 466
      // 44c: astore 4
      // 44e: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 451: new java/lang/Object
      // 454: dup
      // 455: ldc_w "state error setting bookmark: "
      // 458: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 45b: aload 4
      // 45d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/Object;)Ljava/lang/StringBuffer;
      // 460: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 463: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 466: aload 0
      // 467: bipush 0
      // 468: i2l
      // 469: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._startMediaTime J
      // 46c: return
      // 46d: aload 2
      // 46e: ldc_w "com.rim.playableStreams"
      // 471: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 474: ifeq 4a6
      // 477: aload 3
      // 478: dup
      // 479: instanceof java/lang/Object
      // 47c: ifne 483
      // 47f: pop
      // 480: goto 4a6
      // 483: checkcast java/lang/Object
      // 486: invokevirtual java/lang/Integer.intValue ()I
      // 489: istore 4
      // 48b: iload 4
      // 48d: bipush -1
      // 48f: if_icmpeq 4a6
      // 492: aload 0
      // 493: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._isAudio Z
      // 496: ifne 4a6
      // 499: iload 4
      // 49b: bipush 2
      // 49d: iand
      // 49e: ifne 4a6
      // 4a1: aload 0
      // 4a2: bipush 1
      // 4a3: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._isAudio Z
      // 4a6: return
      // try (186 -> 200): 201 null
      // try (201 -> 204): 201 null
      // try (244 -> 248): 249 null
      // try (466 -> 472): 473 null
      // try (466 -> 472): 484 null
   }

   @Override
   public final InputConnection requestResource(String locator) {
      this._activeResourceRequest = (RequestedResource)(new Object(locator, (HttpHeaders)(new Object()), 65536));
      RenderingApplication app = this._browserContent.getRenderingApplication();
      return !(app instanceof Object)
         ? app.getResource(this._activeResourceRequest, null)
         : ((ResourceProvider)app).getInputConnection(this._activeResourceRequest, null);
   }

   @Override
   public final void updateStreamingBufferStatus(long bytesRead, long bytesToRead) {
      this._contentReadEvent.setItemsToReadInBytes(true);
      this._contentReadEvent.setItemsToRead((int)bytesToRead);
      this._contentReadEvent.setItemsRead((int)bytesRead);
      this._browserContent.getRenderingApplication().eventOccurred(this._contentReadEvent);
      this._seekSliderField.updateStreaming((double)bytesRead / bytesToRead);
   }

   @Override
   public final void streamingDone(double percentageCompleted) {
      this._screenManager.setOverlayText(-1, 0, 0);
      this._seekSliderField.updateStreaming(percentageCompleted);
      if (this._playWhenStreamingReady) {
         this.play(false);
      }
   }

   @Override
   public final void streamingBufferReady() {
      this._screenManager.setOverlayText(-1, 0, 0);
      if (this._playWhenStreamingReady) {
         this.play(true);
      }
   }

   @Override
   public final void destroy() {
      if (this._currentUrl != null && this._player != null && this._player.getState() != 0) {
         Object bookmarkObject = this._renderingOptions.getPropertyWithObjectValue(9094571315961484757L, 2, null);
         if (bookmarkObject != null) {
            label78:
            try {
               long mediaTime = this._player.getMediaTime();
               if (mediaTime != -1) {
                  MetaDataBookmark bookmark;
                  if (!(bookmarkObject instanceof Object)) {
                     bookmark = (MetaDataBookmark)(new Object(this._currentUrl));
                  } else {
                     bookmark = (MetaDataBookmark)bookmarkObject;
                  }

                  if (mediaTime < 1000000 || this._player.getDuration() - mediaTime < 1000000) {
                     mediaTime = 0;
                  }

                  bookmark.setPosition(mediaTime);
                  bookmark.save();
               }
            } finally {
               break label78;
            }
         }
      }

      this._closed = true;
      if (this._myMediaPlayerState != null) {
         this._myMediaPlayerState.destroy();
      }

      this.stop(false);
      this.destroyPlayer();
      this.destroyDataSource();
      this.resetAutoBacklight();
      MediaRemoteControl.removeListener(this._application, this._myKeyHandler);
      this._audioRouter.setAudioSinkCallback(null);
      if (this._activeResourceRequest != null) {
         this._browserContent.getRenderingApplication().eventOccurred((Event)(new Object(this, this._activeResourceRequest)));
      }

      if (this._playlistThread != null) {
         this._playlistThread.nextItem();
      }

      this._fullscreenRunnable.halt();
      if (this._cleanupRunnable != null && this._process != null) {
         this._process.removeCleanupRunnable(this._cleanupRunnable);
         this._cleanupRunnable = null;
      }
   }

   @Override
   public final void setDestroyMethod(int method) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void mediaEvent(Object sender, int event, int eventParam, Object data) {
      String id = null;
      if (event == 103) {
         if (eventParam != 0) {
            this._focus = NodeImpl.getId(eventParam, this._modelBar);
            return;
         }
      } else if (event != 105) {
         if (event == 101) {
            id = NodeImpl.getId(eventParam, this._modelBar);
            if (this._queuedPlayback
               && "lineAnim".equals(id)
               && !this._playWhenStreamingReady
               && (this._wasPlayingOnObscured || this._playlist == null)
               && this._player.getState() != 400) {
               this.play(Application.isEventDispatchThread());
            }
         }
      } else {
         id = NodeImpl.getId(eventParam, this._modelBar);
         if ("play".equals(id) || "pause".equals(id)) {
            this.play(Application.isEventDispatchThread());
            return;
         }

         if ("stop".equals(id)) {
            this.stop(true);
            return;
         }

         if ("menu".equals(id)) {
            Runnable menuAction = new MediaBrowserField$4(this);
            this._application.invokeLater(menuAction);
            return;
         }

         if ("sliderbargroup".equals(id)) {
            if (this._seekSliderField.isEnabled()) {
               long time = this._seekSliderField.getTimePosition();
               if (this._player != null && time >= 0) {
                  label97:
                  try {
                     this._player.setMediaTime(time * 1000);
                  } finally {
                     break label97;
                  }
               }

               this._fullscreenRunnable.queue();
            } else {
               this._fullscreenRunnable.halt();
            }

            this._seekSliderField.toggleEnabled();
            return;
         }

         if ("volumecontrolgroup".equals(id)) {
            this._volumeField.toggleEnabled();
            return;
         }

         if ("previous".equals(id)) {
            this.previousTrack();
            return;
         }

         if ("next".equals(id)) {
            this.nextTrack();
            return;
         }
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      this.internalFieldChanged(field, context);
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void powerOff() {
      Screen screen = this.getScreen();
      if (screen != null) {
         screen.close();
      }
   }

   @Override
   public final void powerUp() {
   }

   @Override
   public final void powerOffRequested(int reason) {
      this.powerOff();
   }

   @Override
   public final void cradleMismatch(boolean mismatch) {
   }

   @Override
   public final void fastReset() {
   }

   @Override
   public final void backlightStateChange(boolean on) {
   }

   @Override
   public final void usbConnectionStateChange(int state) {
   }

   @Override
   public final void rootChanged(int state, String rootName) {
      if (state == 1 && rootName.equalsIgnoreCase("SDCard/") && this._fileBased) {
         String root = FileUtilities.getRoot(this._currentUrl);
         if (root.startsWith("/SDCard")) {
            this._seekSliderField.updateTime(0);
            this._durationNode.setString(this.getTimeDisplay(0));
            this.stop(false);

            label33:
            try {
               if (this._player != null) {
                  this._player.close();
               }
            } finally {
               break label33;
            }

            this._requiresReinitialization = true;
         }
      }
   }

   @Override
   public final void updateMediaSourceVolume(int audioPath) {
      int newVolume = GeneralProperty.getCurrentPropertyAsInt(this._audioProperties[audioPath]);
      if (this._volumeField != null && this._volumeControl != null) {
         if (newVolume > this._volumeField.getVolumeLevel()) {
            this._volumeField.increment(newVolume / 10);
         } else {
            this._volumeField.decrement(newVolume / 10);
         }

         this._volumeControl.setLevel(newVolume);
      }
   }

   @Override
   public final MediaPlayerStateInstance getMediaPlayerState() {
      return this._myMediaPlayerState;
   }

   @Override
   protected final synchronized void onObscured() {
      super.onObscured();
      if (!this._isAudio && this._player != null && this._player.getState() == 400) {
         this._wasPlayingOnObscured = true;
         this._queuedPlayback = false;
         this.pause();
      }

      this._visible = false;
   }

   @Override
   protected final void onExposed() {
      super.onExposed();
      this._visible = true;
      if (this._wasPlayingOnObscured) {
         this.resetMediaEngineToPlay();
      }
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      if (this.isFullscreen()) {
         this.toggleFullscreen();
         return true;
      } else {
         return super.navigationClick(status, time) ? true : this.onClick(status, time);
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      if ((status & 1) != 0) {
         return false;
      } else {
         return super.trackwheelClick(status, time) ? true : this.onClick(status, time);
      }
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      this._fullscreenRunnable.reset();
      if (this._seekSliderField.isEnabled()) {
         this._seekSliderField.navigationMovement(dx, dy, status, time);
         return true;
      } else if (this._volumeField.isEnabled()) {
         this._volumeField.navigationMovement(dx, dy, status, time);
         return true;
      } else {
         return super.navigationMovement(dx, dy, status, time);
      }
   }

   public MediaBrowserField(InputConnection param1, InputStream param2, String param3, BrowserContentBaseImpl param4, boolean param5, Event param6) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: invokespecial net/rim/device/api/ui/container/VerticalFieldManager.<init> ()V
      // 004: aload 0
      // 005: new net/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager
      // 008: dup
      // 009: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager.<init> ()V
      // 00c: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._screenManager Lnet/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager;
      // 00f: aload 0
      // 010: new java/lang/Object
      // 013: dup
      // 014: invokespecial net/rim/device/api/ui/component/BitmapField.<init> ()V
      // 017: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._videoPauseBitmap Lnet/rim/device/api/ui/component/BitmapField;
      // 01a: aload 0
      // 01b: new net/rim/device/apps/internal/browser/plugin/media/field/VolumeField
      // 01e: dup
      // 01f: bipush 40
      // 021: invokestatic net/rim/device/apps/internal/browser/options/GeneralProperty.getCurrentPropertyAsInt (I)I
      // 024: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/VolumeField.<init> (I)V
      // 027: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._volumeField Lnet/rim/device/apps/internal/browser/plugin/media/field/VolumeField;
      // 02a: aload 0
      // 02b: new net/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField
      // 02e: dup
      // 02f: aload 0
      // 030: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField.<init> (Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField;)V
      // 033: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._seekSliderField Lnet/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField;
      // 036: aload 0
      // 037: new net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$MyKeyHandler
      // 03a: dup
      // 03b: aload 0
      // 03c: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$MyKeyHandler.<init> (Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField;)V
      // 03f: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._myKeyHandler Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$MyKeyHandler;
      // 042: aload 0
      // 043: bipush 0
      // 044: newarray 5
      // 046: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._totaltimeString [C
      // 049: aload 0
      // 04a: bipush 24
      // 04c: newarray 10
      // 04e: dup
      // 04f: bipush 0
      // 050: bipush 44
      // 052: iastore
      // 053: dup
      // 054: bipush 1
      // 055: bipush 45
      // 057: iastore
      // 058: dup
      // 059: bipush 2
      // 05a: bipush 46
      // 05c: iastore
      // 05d: dup
      // 05e: bipush 3
      // 05f: bipush 47
      // 061: iastore
      // 062: dup
      // 063: bipush 4
      // 064: bipush 49
      // 066: iastore
      // 067: dup
      // 068: bipush 5
      // 069: bipush 50
      // 06b: iastore
      // 06c: dup
      // 06d: bipush 6
      // 06f: bipush 51
      // 071: iastore
      // 072: dup
      // 073: bipush 7
      // 075: ldc_w -805043272
      // 078: iastore
      // 079: dup
      // 07a: bipush 8
      // 07c: ldc_w 944130375
      // 07f: iastore
      // 080: dup
      // 081: bipush 9
      // 083: ldc_w 1335609
      // 086: iastore
      // 087: dup
      // 088: bipush 10
      // 08a: ldc_w 16187402
      // 08d: iastore
      // 08e: dup
      // 08f: bipush 11
      // 091: ldc_w -2074459648
      // 094: iastore
      // 095: dup
      // 096: bipush 12
      // 098: ldc_w 1384929858
      // 09b: iastore
      // 09c: dup
      // 09d: bipush 13
      // 09f: ldc_w 1667470418
      // 0a2: iastore
      // 0a3: dup
      // 0a4: bipush 14
      // 0a6: ldc_w -1249082212
      // 0a9: iastore
      // 0aa: dup
      // 0ab: bipush 15
      // 0ad: ldc_w -1800559228
      // 0b0: iastore
      // 0b1: dup
      // 0b2: bipush 16
      // 0b4: ldc_w -1667975788
      // 0b7: iastore
      // 0b8: dup
      // 0b9: bipush 17
      // 0bb: ldc_w -1113811787
      // 0be: iastore
      // 0bf: dup
      // 0c0: bipush 18
      // 0c2: ldc_w -1664730724
      // 0c5: iastore
      // 0c6: dup
      // 0c7: bipush 19
      // 0c9: ldc_w -1382234558
      // 0cc: iastore
      // 0cd: dup
      // 0ce: bipush 20
      // 0d0: ldc_w -968710714
      // 0d3: iastore
      // 0d4: dup
      // 0d5: bipush 21
      // 0d7: ldc_w -1513708123
      // 0da: iastore
      // 0db: dup
      // 0dc: bipush 22
      // 0de: ldc_w -1381644627
      // 0e1: iastore
      // 0e2: dup
      // 0e3: bipush 23
      // 0e5: ldc_w -826956338
      // 0e8: iastore
      // 0e9: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._audioProperties [I
      // 0ec: aload 0
      // 0ed: bipush 0
      // 0ee: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._destroyableState I
      // 0f1: aload 0
      // 0f2: new java/lang/Object
      // 0f5: dup
      // 0f6: invokespecial java/lang/StringBuffer.<init> ()V
      // 0f9: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._buffer Ljava/lang/StringBuffer;
      // 0fc: aload 0
      // 0fd: ldc_w "GMT"
      // 100: invokestatic java/util/TimeZone.getTimeZone (Ljava/lang/String;)Ljava/util/TimeZone;
      // 103: invokestatic java/util/Calendar.getInstance (Ljava/util/TimeZone;)Ljava/util/Calendar;
      // 106: checkcast java/lang/Object
      // 109: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._calendar Lnet/rim/device/cldc/util/CalendarExtensions;
      // 10c: aload 0
      // 10d: new java/lang/Object
      // 110: dup
      // 111: ldc_w "m:ss"
      // 114: invokespecial net/rim/device/api/i18n/SimpleDateFormat.<init> (Ljava/lang/String;)V
      // 117: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._formatShort Lnet/rim/device/api/i18n/SimpleDateFormat;
      // 11a: aload 0
      // 11b: new java/lang/Object
      // 11e: dup
      // 11f: ldc_w "h:mm:ss"
      // 122: invokespecial net/rim/device/api/i18n/SimpleDateFormat.<init> (Ljava/lang/String;)V
      // 125: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._formatLong Lnet/rim/device/api/i18n/SimpleDateFormat;
      // 128: aload 0
      // 129: new net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$FullscreenRunnable
      // 12c: dup
      // 12d: aload 0
      // 12e: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$FullscreenRunnable.<init> (Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField;)V
      // 131: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._fullscreenRunnable Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$FullscreenRunnable;
      // 134: aload 0
      // 135: sipush 339
      // 138: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 13b: bipush 0
      // 13c: invokevirtual java/lang/String.charAt (I)C
      // 13f: invokestatic net/rim/device/api/util/CharacterUtilities.toLowerCase (C)C
      // 142: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._hotkeyNext C
      // 145: aload 0
      // 146: sipush 876
      // 149: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 14c: bipush 0
      // 14d: invokevirtual java/lang/String.charAt (I)C
      // 150: invokestatic net/rim/device/api/util/CharacterUtilities.toLowerCase (C)C
      // 153: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._hotkeyPrevious C
      // 156: aload 1
      // 157: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getContentType (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 15a: invokestatic net/rim/device/api/io/MIMETypeAssociations.getNormalizedType (Ljava/lang/String;)Ljava/lang/String;
      // 15d: astore 7
      // 15f: aload 7
      // 161: ifnull 174
      // 164: aload 7
      // 166: ldc_w "audio"
      // 169: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 16c: ifeq 174
      // 16f: aload 0
      // 170: bipush 1
      // 171: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._isAudio Z
      // 174: aload 0
      // 175: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 178: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._application Lnet/rim/device/api/system/Application;
      // 17b: aload 0
      // 17c: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._application Lnet/rim/device/api/system/Application;
      // 17f: bipush 1
      // 180: invokevirtual net/rim/device/api/system/Application.enableKeyUpEvents (Z)V
      // 183: aload 0
      // 184: invokestatic net/rim/device/api/system/AudioRouter.getInstance ()Lnet/rim/device/api/system/AudioRouter;
      // 187: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._audioRouter Lnet/rim/device/api/system/AudioRouter;
      // 18a: aload 0
      // 18b: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._audioRouter Lnet/rim/device/api/system/AudioRouter;
      // 18e: aload 0
      // 18f: invokevirtual net/rim/device/api/system/AudioRouter.setAudioSinkCallback (Lnet/rim/device/api/system/AudioSinkCallback;)V
      // 192: aload 0
      // 193: bipush 1
      // 194: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._newPlayer Z
      // 197: aload 0
      // 198: bipush 1
      // 199: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._trackForward Z
      // 19c: aload 0
      // 19d: iload 5
      // 19f: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._embedded Z
      // 1a2: aload 0
      // 1a3: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._isAudio Z
      // 1a6: ifeq 1b8
      // 1a9: iload 5
      // 1ab: ifeq 1b8
      // 1ae: aload 0
      // 1af: getstatic net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.EMBEDDED_AUDIO_TAG Lnet/rim/device/api/ui/theme/Tag;
      // 1b2: invokevirtual net/rim/device/api/ui/Field.setTag (Lnet/rim/device/api/ui/theme/Tag;)V
      // 1b5: goto 1bf
      // 1b8: aload 0
      // 1b9: getstatic net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.MEDIA_BAR_TAG Lnet/rim/device/api/ui/theme/Tag;
      // 1bc: invokevirtual net/rim/device/api/ui/Field.setTag (Lnet/rim/device/api/ui/theme/Tag;)V
      // 1bf: aload 0
      // 1c0: aload 4
      // 1c2: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._browserContent Lnet/rim/device/api/browser/field/BrowserContentBaseImpl;
      // 1c5: aload 0
      // 1c6: aload 7
      // 1c8: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._contentType Ljava/lang/String;
      // 1cb: aload 4
      // 1cd: invokevirtual net/rim/device/api/browser/field/BrowserContentBaseImpl.getRenderingFlags ()I
      // 1d0: sipush 2048
      // 1d3: iand
      // 1d4: ifeq 1dc
      // 1d7: aload 0
      // 1d8: bipush 1
      // 1d9: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._drmForwardLock Z
      // 1dc: aload 4
      // 1de: sipush 833
      // 1e1: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 1e4: invokevirtual net/rim/device/api/browser/field/BrowserContentBaseImpl.setTitle (Ljava/lang/String;)V
      // 1e7: aload 0
      // 1e8: aload 4
      // 1ea: invokevirtual net/rim/device/api/browser/field/BrowserContentBaseImpl.getRenderingOptions ()Lnet/rim/device/api/browser/field/RenderingOptions;
      // 1ed: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._renderingOptions Lnet/rim/device/api/browser/field/RenderingOptions;
      // 1f0: aload 0
      // 1f1: new net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$MyMediaPlayerState
      // 1f4: dup
      // 1f5: aload 0
      // 1f6: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$MyMediaPlayerState.<init> (Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField;)V
      // 1f9: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._myMediaPlayerState Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$MyMediaPlayerState;
      // 1fc: aload 0
      // 1fd: aload 0
      // 1fe: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._renderingOptions Lnet/rim/device/api/browser/field/RenderingOptions;
      // 201: ldc2_w 9094571315961484757
      // 204: bipush 3
      // 206: bipush 0
      // 207: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithIntValue (JII)I
      // 20a: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlistCurrentIndex I
      // 20d: aload 0
      // 20e: aload 0
      // 20f: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._renderingOptions Lnet/rim/device/api/browser/field/RenderingOptions;
      // 212: ldc2_w 9094571315961484757
      // 215: bipush 4
      // 217: bipush 0
      // 218: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithBooleanValue (JIZ)Z
      // 21b: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._singleFilePlayback Z
      // 21e: aload 0
      // 21f: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._renderingOptions Lnet/rim/device/api/browser/field/RenderingOptions;
      // 222: ldc2_w 9094571315961484757
      // 225: bipush 2
      // 227: aconst_null
      // 228: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithObjectValue (JILjava/lang/Object;)Ljava/lang/Object;
      // 22b: astore 8
      // 22d: aload 8
      // 22f: instanceof java/lang/Object
      // 232: ifeq 244
      // 235: aload 0
      // 236: aload 8
      // 238: checkcast java/lang/Object
      // 23b: invokevirtual java/lang/Long.longValue ()J
      // 23e: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._startMediaTime J
      // 241: goto 258
      // 244: aload 8
      // 246: instanceof java/lang/Object
      // 249: ifeq 258
      // 24c: aload 0
      // 24d: aload 8
      // 24f: checkcast java/lang/Object
      // 252: invokevirtual net/rim/device/internal/io/file/MetaDataBookmark.getPosition ()J
      // 255: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._startMediaTime J
      // 258: aload 0
      // 259: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._application Lnet/rim/device/api/system/Application;
      // 25c: aload 0
      // 25d: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._myKeyHandler Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$MyKeyHandler;
      // 260: invokestatic net/rim/device/internal/media/MediaRemoteControl.addListener (Lnet/rim/device/api/system/Application;Lnet/rim/device/internal/media/MediaRemoteControlListener;)V
      // 263: aload 0
      // 264: invokevirtual net/rim/device/api/ui/Manager.applyTheme ()V
      // 267: aload 0
      // 268: iload 5
      // 26a: aload 4
      // 26c: invokevirtual net/rim/device/api/browser/field/BrowserContentBaseImpl.getRenderingApplication ()Lnet/rim/device/api/browser/field/RenderingApplication;
      // 26f: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.initPlayerField (ZLnet/rim/device/api/browser/field/RenderingApplication;)Lnet/rim/device/api/ui/Field;
      // 272: astore 9
      // 274: aload 9
      // 276: ifnonnull 28e
      // 279: aload 0
      // 27a: new java/lang/Object
      // 27d: dup
      // 27e: sipush 826
      // 281: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 284: ldc2_w 45035996273704960
      // 287: invokespecial net/rim/device/api/ui/component/RichTextField.<init> (Ljava/lang/String;J)V
      // 28a: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 28d: return
      // 28e: aload 0
      // 28f: invokestatic net/rim/vm/Process.currentProcess ()Lnet/rim/vm/Process;
      // 292: checkcast java/lang/Object
      // 295: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._process Lnet/rim/device/api/system/ApplicationProcess;
      // 298: aload 0
      // 299: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._process Lnet/rim/device/api/system/ApplicationProcess;
      // 29c: ifnull 2b6
      // 29f: aload 0
      // 2a0: new net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$1
      // 2a3: dup
      // 2a4: aload 0
      // 2a5: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$1.<init> (Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField;)V
      // 2a8: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._cleanupRunnable Ljava/lang/Runnable;
      // 2ab: aload 0
      // 2ac: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._process Lnet/rim/device/api/system/ApplicationProcess;
      // 2af: aload 0
      // 2b0: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._cleanupRunnable Ljava/lang/Runnable;
      // 2b3: invokevirtual net/rim/device/api/system/ApplicationProcess.addCleanupRunnable (Ljava/lang/Runnable;)V
      // 2b6: aload 0
      // 2b7: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._modelBar Lnet/rim/plazmic/internal/mediaengine/model/intarray/v1_2/ModelInteractorImpl;
      // 2ba: bipush 107
      // 2bc: aload 0
      // 2bd: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._modelBar Lnet/rim/plazmic/internal/mediaengine/model/intarray/v1_2/ModelInteractorImpl;
      // 2c0: ldc_w "repeatOff"
      // 2c3: invokevirtual net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/ModelInteractorImpl.getHandle (Ljava/lang/String;)I
      // 2c6: aconst_null
      // 2c7: invokevirtual net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/ModelInteractorImpl.trigger (IILjava/lang/Object;)V
      // 2ca: invokestatic net/rim/device/internal/media/MediaOptionsRegistry.getInstance ()Lnet/rim/device/internal/media/MediaOptionsRegistry;
      // 2cd: ldc2_w 8428678949875667391
      // 2d0: invokevirtual net/rim/device/internal/util/OptionsRegistry.getBoolean (J)Z
      // 2d3: ifeq 2da
      // 2d6: aload 0
      // 2d7: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.toggleRepeat ()V
      // 2da: aload 0
      // 2db: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._screenManager Lnet/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager;
      // 2de: bipush 2
      // 2e0: aload 0
      // 2e1: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._videoPauseBitmap Lnet/rim/device/api/ui/component/BitmapField;
      // 2e4: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager.addField (ILnet/rim/device/api/ui/Field;)V
      // 2e7: bipush 1
      // 2e8: istore 10
      // 2ea: aload 2
      // 2eb: ifnonnull 2f2
      // 2ee: bipush 1
      // 2ef: goto 2f3
      // 2f2: bipush 0
      // 2f3: istore 11
      // 2f5: iload 11
      // 2f7: ifeq 305
      // 2fa: aload 1
      // 2fb: aload 1
      // 2fc: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 301: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getInputStreamFromContentEncoding (Ljavax/microedition/io/InputConnection;Ljava/io/InputStream;)Ljava/io/InputStream;
      // 304: astore 2
      // 305: aload 7
      // 307: ifnonnull 30d
      // 30a: goto 3f8
      // 30d: aload 7
      // 30f: ldc_w "audio/x-mpegurl"
      // 312: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 315: ifne 326
      // 318: aload 7
      // 31a: ldc_w "audio/x-scpls"
      // 31d: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 320: ifne 326
      // 323: goto 3f8
      // 326: aload 1
      // 327: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getCharacterEncoding (Ljavax/microedition/io/InputConnection;)Ljava/lang/String;
      // 32a: astore 12
      // 32c: aload 12
      // 32e: ifnonnull 336
      // 331: invokestatic com/sun/cldc/i18n/Helper.getDefaultEncoding ()Ljava/lang/String;
      // 334: astore 12
      // 336: aload 2
      // 337: aload 12
      // 339: invokestatic com/sun/cldc/i18n/Helper.getStreamReader (Ljava/io/InputStream;Ljava/lang/String;)Ljava/io/Reader;
      // 33c: astore 13
      // 33e: aload 0
      // 33f: aload 7
      // 341: aload 13
      // 343: invokestatic net/rim/device/internal/media/Playlist.getPlaylist (Ljava/lang/String;Ljava/io/Reader;)Lnet/rim/device/internal/media/Playlist;
      // 346: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlist Lnet/rim/device/internal/media/Playlist;
      // 349: aload 0
      // 34a: aload 0
      // 34b: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlist Lnet/rim/device/internal/media/Playlist;
      // 34e: invokevirtual net/rim/device/internal/media/Playlist.getNumberOfItems ()I
      // 351: newarray 10
      // 353: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlistOrder [I
      // 356: aload 0
      // 357: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.setSortedPlaylistOrder ()V
      // 35a: aload 0
      // 35b: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlist Lnet/rim/device/internal/media/Playlist;
      // 35e: invokevirtual net/rim/device/internal/media/Playlist.getNumberOfItems ()I
      // 361: bipush 1
      // 362: if_icmple 3c0
      // 365: aload 0
      // 366: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._singleFilePlayback Z
      // 369: ifne 3c0
      // 36c: aload 0
      // 36d: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._modelBar Lnet/rim/plazmic/internal/mediaengine/model/intarray/v1_2/ModelInteractorImpl;
      // 370: bipush 107
      // 372: aload 0
      // 373: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._modelBar Lnet/rim/plazmic/internal/mediaengine/model/intarray/v1_2/ModelInteractorImpl;
      // 376: ldc_w "shuffleOff"
      // 379: invokevirtual net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/ModelInteractorImpl.getHandle (Ljava/lang/String;)I
      // 37c: aconst_null
      // 37d: invokevirtual net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/ModelInteractorImpl.trigger (IILjava/lang/Object;)V
      // 380: invokestatic net/rim/device/internal/media/MediaOptionsRegistry.getInstance ()Lnet/rim/device/internal/media/MediaOptionsRegistry;
      // 383: astore 14
      // 385: aload 14
      // 387: ldc2_w -7071091113559016691
      // 38a: invokevirtual net/rim/device/internal/util/OptionsRegistry.getBoolean (J)Z
      // 38d: istore 15
      // 38f: aload 14
      // 391: ldc2_w -2846908971875712627
      // 394: invokevirtual net/rim/device/internal/util/OptionsRegistry.getBoolean (J)Z
      // 397: istore 16
      // 399: iload 16
      // 39b: ifne 3a3
      // 39e: iload 15
      // 3a0: ifeq 3c0
      // 3a3: iload 15
      // 3a5: ifeq 3b7
      // 3a8: iload 16
      // 3aa: ifne 3b7
      // 3ad: aload 0
      // 3ae: aload 0
      // 3af: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlistCurrentIndex I
      // 3b2: bipush 1
      // 3b3: iadd
      // 3b4: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlistCurrentIndex I
      // 3b7: aload 0
      // 3b8: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.toggleShuffle ()V
      // 3bb: aload 0
      // 3bc: bipush 0
      // 3bd: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlistCurrentIndex I
      // 3c0: aload 0
      // 3c1: new net/rim/device/apps/internal/browser/plugin/media/field/PlaylistField
      // 3c4: dup
      // 3c5: aload 0
      // 3c6: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlist Lnet/rim/device/internal/media/Playlist;
      // 3c9: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/PlaylistField.<init> (Lnet/rim/device/internal/media/Playlist;)V
      // 3cc: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlistField Lnet/rim/device/apps/internal/browser/plugin/media/field/PlaylistField;
      // 3cf: aload 0
      // 3d0: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlistField Lnet/rim/device/apps/internal/browser/plugin/media/field/PlaylistField;
      // 3d3: aload 0
      // 3d4: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/PlaylistField.setChangeListener (Lnet/rim/device/api/ui/FieldChangeListener;)V
      // 3d7: aload 0
      // 3d8: new net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$PlaylistThread
      // 3db: dup
      // 3dc: aload 0
      // 3dd: aconst_null
      // 3de: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$PlaylistThread.<init> (Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField;Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$1;)V
      // 3e1: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlistThread Lnet/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField$PlaylistThread;
      // 3e4: aload 0
      // 3e5: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._screenManager Lnet/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager;
      // 3e8: bipush 1
      // 3e9: aload 0
      // 3ea: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlistField Lnet/rim/device/apps/internal/browser/plugin/media/field/PlaylistField;
      // 3ed: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager.addField (ILnet/rim/device/api/ui/Field;)V
      // 3f0: goto 5cb
      // 3f3: astore 13
      // 3f5: goto 5cb
      // 3f8: bipush 0
      // 3f9: istore 12
      // 3fb: aconst_null
      // 3fc: astore 13
      // 3fe: aload 1
      // 3ff: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.getContentLength (Ljavax/microedition/io/InputConnection;)J
      // 402: lstore 14
      // 404: aconst_null
      // 405: astore 16
      // 407: aload 1
      // 408: instanceof java/lang/Object
      // 40b: ifeq 448
      // 40e: bipush 1
      // 40f: istore 12
      // 411: iload 11
      // 413: ifne 42d
      // 416: aload 2
      // 417: invokevirtual java/io/InputStream.close ()V
      // 41a: goto 41f
      // 41d: astore 17
      // 41f: bipush 0
      // 420: istore 10
      // 422: aload 1
      // 423: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 428: astore 13
      // 42a: goto 433
      // 42d: bipush 0
      // 42e: istore 10
      // 430: aload 2
      // 431: astore 13
      // 433: aload 0
      // 434: aload 1
      // 435: checkcast java/lang/Object
      // 438: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 43d: bipush 7
      // 43f: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 442: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._tuneName Ljava/lang/String;
      // 445: goto 56a
      // 448: aload 1
      // 449: instanceof java/lang/Object
      // 44c: ifne 452
      // 44f: goto 56a
      // 452: aload 1
      // 453: instanceof java/lang/Object
      // 456: ifne 45c
      // 459: goto 564
      // 45c: bipush 0
      // 45d: istore 10
      // 45f: aload 0
      // 460: bipush 1
      // 461: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playWhenStreamingReady Z
      // 464: aload 0
      // 465: new java/lang/Object
      // 468: dup
      // 469: aload 0
      // 46a: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._browserContent Lnet/rim/device/api/browser/field/BrowserContentBaseImpl;
      // 46d: invokespecial net/rim/device/api/browser/field/ContentReadEvent.<init> (Ljava/lang/Object;)V
      // 470: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._contentReadEvent Lnet/rim/device/api/browser/field/ContentReadEvent;
      // 473: new java/lang/Object
      // 476: dup
      // 477: aload 1
      // 478: checkcast java/lang/Object
      // 47b: aload 2
      // 47c: aload 0
      // 47d: invokespecial net/rim/device/internal/media/HTTPBufferingManager.<init> (Ljavax/microedition/io/HttpConnection;Ljava/io/InputStream;Lnet/rim/device/internal/media/HTTPBufferingCallback;)V
      // 480: astore 16
      // 482: aload 0
      // 483: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._screenManager Lnet/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager;
      // 486: sipush 899
      // 489: ldc_w 16777215
      // 48c: bipush 0
      // 48d: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager.setOverlayText (III)V
      // 490: aload 16
      // 492: invokevirtual net/rim/device/internal/media/HTTPBufferingManager.getInputStream ()Ljava/io/InputStream;
      // 495: astore 13
      // 497: aload 6
      // 499: dup
      // 49a: instanceof java/lang/Object
      // 49d: ifne 4a4
      // 4a0: pop
      // 4a1: goto 56a
      // 4a4: checkcast java/lang/Object
      // 4a7: astore 17
      // 4a9: aload 17
      // 4ab: invokevirtual net/rim/device/apps/internal/browser/api/SDPRedirectEvent.getValueTable ()Lnet/rim/device/api/util/MultiMap;
      // 4ae: astore 18
      // 4b0: bipush 0
      // 4b1: istore 19
      // 4b3: aload 18
      // 4b5: ldc_w "b"
      // 4b8: invokevirtual net/rim/device/api/util/MultiMap.elements (Ljava/lang/Object;)Ljava/util/Enumeration;
      // 4bb: astore 20
      // 4bd: aload 20
      // 4bf: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 4c4: ifeq 500
      // 4c7: aload 20
      // 4c9: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 4ce: checkcast java/lang/Object
      // 4d1: astore 21
      // 4d3: aload 21
      // 4d5: ldc_w "AS:"
      // 4d8: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 4db: ifeq 4bd
      // 4de: aload 21
      // 4e0: invokevirtual java/lang/String.trim ()Ljava/lang/String;
      // 4e3: astore 21
      // 4e5: iload 19
      // 4e7: aload 21
      // 4e9: bipush 3
      // 4eb: aload 21
      // 4ed: invokevirtual java/lang/String.length ()I
      // 4f0: bipush 10
      // 4f2: invokestatic net/rim/device/api/util/NumberUtilities.parseInt (Ljava/lang/String;III)I
      // 4f5: iadd
      // 4f6: istore 19
      // 4f8: goto 4bd
      // 4fb: astore 22
      // 4fd: goto 4bd
      // 500: aload 18
      // 502: ldc_w "a"
      // 505: invokevirtual net/rim/device/api/util/MultiMap.elements (Ljava/lang/Object;)Ljava/util/Enumeration;
      // 508: astore 21
      // 50a: aload 20
      // 50c: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 511: ifeq 552
      // 514: aload 20
      // 516: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 51b: checkcast java/lang/Object
      // 51e: astore 22
      // 520: aload 22
      // 522: ldc_w "X-filesize:"
      // 525: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 528: ifeq 50a
      // 52b: aload 22
      // 52d: invokevirtual java/lang/String.trim ()Ljava/lang/String;
      // 530: astore 22
      // 532: aload 22
      // 534: bipush 3
      // 536: aload 22
      // 538: invokevirtual java/lang/String.length ()I
      // 53b: bipush 10
      // 53d: invokestatic net/rim/device/api/util/NumberUtilities.parseInt (Ljava/lang/String;III)I
      // 540: istore 23
      // 542: aload 16
      // 544: iload 23
      // 546: i2l
      // 547: invokevirtual net/rim/device/internal/media/HTTPBufferingManager.setStreamLength (J)V
      // 54a: goto 50a
      // 54d: astore 23
      // 54f: goto 50a
      // 552: iload 19
      // 554: ifle 56a
      // 557: aload 16
      // 559: iload 19
      // 55b: bipush 8
      // 55d: idiv
      // 55e: invokevirtual net/rim/device/internal/media/HTTPBufferingManager.setBandwidthRequired (I)V
      // 561: goto 56a
      // 564: bipush 0
      // 565: istore 10
      // 567: aload 2
      // 568: astore 13
      // 56a: aload 13
      // 56c: ifnonnull 5b7
      // 56f: aconst_null
      // 570: astore 17
      // 572: lload 14
      // 574: bipush 0
      // 575: i2l
      // 576: lcmp
      // 577: ifle 592
      // 57a: new java/lang/Object
      // 57d: dup
      // 57e: aload 1
      // 57f: invokespecial net/rim/device/api/browser/field/ContentReadEvent.<init> (Ljava/lang/Object;)V
      // 582: astore 17
      // 584: aload 17
      // 586: bipush 1
      // 587: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsToReadInBytes (Z)V
      // 58a: aload 17
      // 58c: lload 14
      // 58e: l2i
      // 58f: invokevirtual net/rim/device/api/browser/field/ContentReadEvent.setItemsToRead (I)V
      // 592: aload 2
      // 593: aload 17
      // 595: aload 4
      // 597: invokevirtual net/rim/device/api/browser/field/BrowserContentBaseImpl.getRenderingApplication ()Lnet/rim/device/api/browser/field/RenderingApplication;
      // 59a: lload 14
      // 59c: l2i
      // 59d: invokestatic net/rim/device/apps/internal/browser/util/RendererControl.readBytesFromInputStream (Ljava/io/InputStream;Lnet/rim/device/api/browser/field/ContentReadEvent;Lnet/rim/device/api/browser/field/RenderingApplication;I)[B
      // 5a0: astore 18
      // 5a2: aload 18
      // 5a4: ifnonnull 5ac
      // 5a7: bipush 0
      // 5a8: newarray 8
      // 5aa: astore 18
      // 5ac: new java/lang/Object
      // 5af: dup
      // 5b0: aload 18
      // 5b2: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 5b5: astore 13
      // 5b7: aload 0
      // 5b8: aload 1
      // 5b9: aload 13
      // 5bb: lload 14
      // 5bd: aload 7
      // 5bf: iload 12
      // 5c1: aload 3
      // 5c2: aload 16
      // 5c4: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.initDataSource (Ljavax/microedition/io/InputConnection;Ljava/io/InputStream;JLjava/lang/String;ZLjava/lang/String;Lnet/rim/device/internal/media/HTTPBufferingManager;)V
      // 5c7: aload 0
      // 5c8: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.initContent ()V
      // 5cb: iload 10
      // 5cd: ifeq 60c
      // 5d0: aload 1
      // 5d1: ifnull 5da
      // 5d4: aload 1
      // 5d5: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 5da: aload 2
      // 5db: ifnonnull 5e1
      // 5de: goto 6b7
      // 5e1: aload 2
      // 5e2: invokevirtual java/io/InputStream.close ()V
      // 5e5: return
      // 5e6: astore 11
      // 5e8: return
      // 5e9: astore 11
      // 5eb: aload 2
      // 5ec: ifnonnull 5f2
      // 5ef: goto 6b7
      // 5f2: aload 2
      // 5f3: invokevirtual java/io/InputStream.close ()V
      // 5f6: return
      // 5f7: astore 11
      // 5f9: return
      // 5fa: astore 24
      // 5fc: aload 2
      // 5fd: ifnull 609
      // 600: aload 2
      // 601: invokevirtual java/io/InputStream.close ()V
      // 604: goto 609
      // 607: astore 25
      // 609: aload 24
      // 60b: athrow
      // 60c: invokestatic java/lang/Thread.currentThread ()Ljava/lang/Thread;
      // 60f: astore 11
      // 611: aload 11
      // 613: dup
      // 614: instanceof java/lang/Object
      // 617: ifne 61e
      // 61a: pop
      // 61b: goto 6b7
      // 61e: checkcast java/lang/Object
      // 621: aconst_null
      // 622: invokevirtual net/rim/device/apps/internal/browser/page/RenderThread.setInput (Ljava/io/InputStream;)V
      // 625: aload 11
      // 627: checkcast java/lang/Object
      // 62a: aconst_null
      // 62b: invokevirtual net/rim/device/apps/internal/browser/page/RenderThread.setConnection (Ljavax/microedition/io/Connection;)V
      // 62e: return
      // 62f: astore 11
      // 631: new java/lang/Object
      // 634: dup
      // 635: aload 11
      // 637: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 63a: invokespecial net/rim/device/api/browser/field/RenderingException.<init> (Ljava/lang/String;)V
      // 63d: athrow
      // 63e: astore 11
      // 640: new java/lang/Object
      // 643: dup
      // 644: aload 11
      // 646: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 649: invokespecial net/rim/device/api/browser/field/RenderingException.<init> (Ljava/lang/String;)V
      // 64c: athrow
      // 64d: astore 26
      // 64f: iload 10
      // 651: ifeq 692
      // 654: aload 1
      // 655: ifnull 65e
      // 658: aload 1
      // 659: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 65e: aload 2
      // 65f: ifnull 6b4
      // 662: aload 2
      // 663: invokevirtual java/io/InputStream.close ()V
      // 666: goto 6b4
      // 669: astore 27
      // 66b: goto 6b4
      // 66e: astore 27
      // 670: aload 2
      // 671: ifnull 6b4
      // 674: aload 2
      // 675: invokevirtual java/io/InputStream.close ()V
      // 678: goto 6b4
      // 67b: astore 27
      // 67d: goto 6b4
      // 680: astore 28
      // 682: aload 2
      // 683: ifnull 68f
      // 686: aload 2
      // 687: invokevirtual java/io/InputStream.close ()V
      // 68a: goto 68f
      // 68d: astore 29
      // 68f: aload 28
      // 691: athrow
      // 692: invokestatic java/lang/Thread.currentThread ()Ljava/lang/Thread;
      // 695: astore 27
      // 697: aload 27
      // 699: dup
      // 69a: instanceof java/lang/Object
      // 69d: ifne 6a4
      // 6a0: pop
      // 6a1: goto 6b4
      // 6a4: checkcast java/lang/Object
      // 6a7: aconst_null
      // 6a8: invokevirtual net/rim/device/apps/internal/browser/page/RenderThread.setInput (Ljava/io/InputStream;)V
      // 6ab: aload 27
      // 6ad: checkcast java/lang/Object
      // 6b0: aconst_null
      // 6b1: invokevirtual net/rim/device/apps/internal/browser/page/RenderThread.setConnection (Ljavax/microedition/io/Connection;)V
      // 6b4: aload 26
      // 6b6: athrow
      // 6b7: return
      // try (394 -> 481): 482 null
      // try (500 -> 502): 503 null
      // try (590 -> 599): 600 null
      // try (620 -> 631): 632 null
      // try (705 -> 707): 708 null
      // try (698 -> 702): 710 null
      // try (714 -> 716): 717 null
      // try (698 -> 702): 719 null
      // try (710 -> 711): 719 null
      // try (722 -> 724): 725 null
      // try (719 -> 720): 719 null
      // try (362 -> 696): 744 null
      // try (362 -> 696): 751 null
      // try (362 -> 696): 758 null
      // try (767 -> 769): 770 null
      // try (761 -> 765): 772 null
      // try (775 -> 777): 778 null
      // try (761 -> 765): 780 null
      // try (772 -> 773): 780 null
      // try (783 -> 785): 786 null
      // try (780 -> 781): 780 null
      // try (744 -> 759): 758 null
   }

   private final boolean mayFinishAfterHTTPBuffering() {
      HTTPBufferingManager bufferingManager = this.getBufferingManager();
      return bufferingManager != null && bufferingManager.bufferWillContainAllContent() && !bufferingManager.bufferContainsAllContent();
   }

   private final boolean resetStreamingOnEOM() {
      if (this._ds instanceof Object) {
         HTTPBufferingManager bufferingManager = this.getBufferingManager();
         return bufferingManager == null || !bufferingManager.bufferContainsAllContent();
      } else {
         return true;
      }
   }

   private final void initContent() {
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
      // 001: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._seekSliderField Lnet/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField;
      // 004: bipush 0
      // 005: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField.updateTime (I)V
      // 008: aload 0
      // 009: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._seekSliderField Lnet/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField;
      // 00c: nop
      // 00d: ldc2_w 0
      // 010: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField.updateStreaming (D)V
      // 013: aload 0
      // 014: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.destroyPlayer ()V
      // 017: aload 0
      // 018: aconst_null
      // 019: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 01c: aload 0
      // 01d: aload 0
      // 01e: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._ds Ljavax/microedition/media/protocol/DataSource;
      // 021: invokestatic javax/microedition/media/Manager.createPlayer (Ljavax/microedition/media/protocol/DataSource;)Ljavax/microedition/media/Player;
      // 024: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 027: aload 0
      // 028: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 02b: aload 0
      // 02c: invokeinterface javax/microedition/media/Player.addPlayerListener (Ljavax/microedition/media/PlayerListener;)V 2
      // 031: aload 0
      // 032: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 035: invokeinterface javax/microedition/media/Player.realize ()V 1
      // 03a: aload 0
      // 03b: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 03e: instanceof java/lang/Object
      // 041: ifeq 099
      // 044: aload 0
      // 045: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._playlist Lnet/rim/device/internal/media/Playlist;
      // 048: ifnull 05d
      // 04b: aload 0
      // 04c: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 04f: checkcast java/lang/Object
      // 052: ldc_w "big_session"
      // 055: getstatic java/lang/Boolean.TRUE Ljava/lang/Boolean;
      // 058: invokeinterface net/rim/device/internal/media/StreamDataControl.setKeyValue (Ljava/lang/String;Ljava/lang/Object;)V 3
      // 05d: aload 0
      // 05e: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._isAudio Z
      // 061: ifeq 069
      // 064: bipush 4
      // 066: goto 06b
      // 069: bipush 3
      // 06b: istore 1
      // 06c: aload 0
      // 06d: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._renderingOptions Lnet/rim/device/api/browser/field/RenderingOptions;
      // 070: ldc2_w 9094571315961484757
      // 073: bipush 1
      // 074: bipush -1
      // 076: invokevirtual net/rim/device/api/browser/field/RenderingOptions.getPropertyWithIntValue (JII)I
      // 079: istore 2
      // 07a: iload 2
      // 07b: bipush -1
      // 07d: if_icmpeq 082
      // 080: iload 2
      // 081: istore 1
      // 082: aload 0
      // 083: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 086: checkcast java/lang/Object
      // 089: ldc_w "audiosource"
      // 08c: new java/lang/Object
      // 08f: dup
      // 090: iload 1
      // 091: invokespecial java/lang/Integer.<init> (I)V
      // 094: invokeinterface net/rim/device/internal/media/StreamDataControl.setKeyValue (Ljava/lang/String;Ljava/lang/Object;)V 3
      // 099: aload 0
      // 09a: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 09d: ldc_w "javax.microedition.media.control.MetaDataControl"
      // 0a0: invokeinterface javax/microedition/media/Controllable.getControl (Ljava/lang/String;)Ljavax/microedition/media/Control; 2
      // 0a5: checkcast java/lang/Object
      // 0a8: astore 1
      // 0a9: aload 0
      // 0aa: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 0ad: ldc_w "net.rim.device.api.media.control.BinaryMetaDataControl"
      // 0b0: invokeinterface javax/microedition/media/Controllable.getControl (Ljava/lang/String;)Ljavax/microedition/media/Control; 2
      // 0b5: checkcast java/lang/Object
      // 0b8: astore 2
      // 0b9: aload 0
      // 0ba: aload 0
      // 0bb: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 0be: ldc_w "javax.microedition.media.control.VideoControl"
      // 0c1: invokeinterface javax/microedition/media/Controllable.getControl (Ljava/lang/String;)Ljavax/microedition/media/Control; 2
      // 0c6: checkcast java/lang/Object
      // 0c9: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._videoControl Ljavax/microedition/media/control/VideoControl;
      // 0cc: aload 0
      // 0cd: aload 0
      // 0ce: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 0d1: ldc_w "net.rim.device.api.media.control.AudioPathControl"
      // 0d4: invokeinterface javax/microedition/media/Controllable.getControl (Ljava/lang/String;)Ljavax/microedition/media/Control; 2
      // 0d9: checkcast java/lang/Object
      // 0dc: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._audioPathControl Lnet/rim/device/api/media/control/AudioPathControl;
      // 0df: aload 0
      // 0e0: aload 0
      // 0e1: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 0e4: ldc_w "javax.microedition.media.control.VolumeControl"
      // 0e7: invokeinterface javax/microedition/media/Controllable.getControl (Ljava/lang/String;)Ljavax/microedition/media/Control; 2
      // 0ec: checkcast java/lang/Object
      // 0ef: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._volumeControl Ljavax/microedition/media/control/VolumeControl;
      // 0f2: aload 0
      // 0f3: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._volumeControl Ljavax/microedition/media/control/VolumeControl;
      // 0f6: ifnull 10a
      // 0f9: aload 0
      // 0fa: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._volumeControl Ljavax/microedition/media/control/VolumeControl;
      // 0fd: aload 0
      // 0fe: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._volumeField Lnet/rim/device/apps/internal/browser/plugin/media/field/VolumeField;
      // 101: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/VolumeField.getVolumeLevel ()I
      // 104: invokeinterface javax/microedition/media/control/VolumeControl.setLevel (I)I 2
      // 109: pop
      // 10a: aload 1
      // 10b: ifnull 135
      // 10e: aload 0
      // 10f: aload 1
      // 110: ldc_w "title"
      // 113: invokeinterface javax/microedition/media/control/MetaDataControl.getKeyValue (Ljava/lang/String;)Ljava/lang/String; 2
      // 118: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 11b: aload 0
      // 11c: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 11f: ifnull 135
      // 122: aload 0
      // 123: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 126: invokevirtual java/lang/String.length ()I
      // 129: ifne 135
      // 12c: aload 0
      // 12d: aconst_null
      // 12e: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 131: goto 135
      // 134: astore 3
      // 135: aload 0
      // 136: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 139: ifnonnull 19f
      // 13c: aload 0
      // 13d: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._currentUrl Ljava/lang/String;
      // 140: ifnull 19f
      // 143: aload 0
      // 144: aload 0
      // 145: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._currentUrl Ljava/lang/String;
      // 148: ldc_w "utf-8"
      // 14b: invokestatic net/rim/device/cldc/io/utility/URIDecoder.decode (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
      // 14e: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 151: aload 0
      // 152: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 155: ifnull 19f
      // 158: aload 0
      // 159: aload 0
      // 15a: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 15d: invokestatic net/rim/device/internal/io/file/FileUtilities.getFileNameAndStripEncryptionExt (Ljava/lang/String;)Ljava/lang/String;
      // 160: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 163: aload 0
      // 164: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 167: bipush 47
      // 169: invokevirtual java/lang/String.lastIndexOf (I)I
      // 16c: istore 3
      // 16d: iload 3
      // 16e: bipush -1
      // 170: if_icmpeq 181
      // 173: aload 0
      // 174: aload 0
      // 175: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 178: iload 3
      // 179: bipush 1
      // 17a: iadd
      // 17b: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 17e: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 181: aload 0
      // 182: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 185: bipush 46
      // 187: invokevirtual java/lang/String.lastIndexOf (I)I
      // 18a: istore 4
      // 18c: iload 4
      // 18e: ifle 19f
      // 191: aload 0
      // 192: aload 0
      // 193: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 196: bipush 0
      // 197: iload 4
      // 199: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 19c: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 19f: aload 0
      // 1a0: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._isAudio Z
      // 1a3: ifne 1a9
      // 1a6: goto 3b0
      // 1a9: aload 0
      // 1aa: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._screenManager Lnet/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager;
      // 1ad: bipush 0
      // 1ae: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager.clearViewFields (I)V
      // 1b1: aconst_null
      // 1b2: astore 3
      // 1b3: new net/rim/device/apps/internal/browser/plugin/media/field/AudioBottomUpManager
      // 1b6: dup
      // 1b7: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/AudioBottomUpManager.<init> ()V
      // 1ba: astore 4
      // 1bc: aload 2
      // 1bd: ifnull 232
      // 1c0: aload 2
      // 1c1: invokeinterface net/rim/device/api/media/control/BinaryMetaDataControl.getMetaDataObjects ()[Lnet/rim/device/api/media/MetaDataObject; 1
      // 1c6: astore 5
      // 1c8: bipush 0
      // 1c9: istore 6
      // 1cb: iload 6
      // 1cd: aload 5
      // 1cf: arraylength
      // 1d0: if_icmpge 232
      // 1d3: aload 5
      // 1d5: iload 6
      // 1d7: aaload
      // 1d8: ifnull 22c
      // 1db: aload 5
      // 1dd: iload 6
      // 1df: aaload
      // 1e0: invokevirtual net/rim/device/api/media/MetaDataObject.getPictureType ()I
      // 1e3: bipush -1
      // 1e5: if_icmpeq 22c
      // 1e8: aload 5
      // 1ea: iload 6
      // 1ec: aaload
      // 1ed: invokevirtual net/rim/device/api/media/MetaDataObject.getData ()[B
      // 1f0: astore 7
      // 1f2: aload 7
      // 1f4: ifnonnull 1fa
      // 1f7: goto 22c
      // 1fa: aload 7
      // 1fc: bipush 0
      // 1fd: aload 7
      // 1ff: arraylength
      // 200: invokestatic net/rim/device/api/system/EncodedImage.createEncodedImage ([BII)Lnet/rim/device/api/system/EncodedImage;
      // 203: astore 3
      // 204: aload 3
      // 205: ifnonnull 20b
      // 208: goto 22c
      // 20b: aload 3
      // 20c: invokevirtual net/rim/device/api/system/EncodedImage.getWidth ()I
      // 20f: istore 8
      // 211: aload 3
      // 212: invokevirtual net/rim/device/api/system/EncodedImage.getHeight ()I
      // 215: istore 9
      // 217: iload 8
      // 219: bipush 5
      // 21b: if_icmplt 225
      // 21e: iload 9
      // 220: bipush 5
      // 222: if_icmpge 232
      // 225: aconst_null
      // 226: astore 3
      // 227: goto 22c
      // 22a: astore 8
      // 22c: iinc 6 1
      // 22f: goto 1cb
      // 232: aload 3
      // 233: ifnull 239
      // 236: goto 2e1
      // 239: aload 0
      // 23a: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._fileBased Z
      // 23d: ifne 243
      // 240: goto 2e1
      // 243: ldc_w "folder.jpg"
      // 246: aload 0
      // 247: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._currentUrl Ljava/lang/String;
      // 24a: invokestatic net/rim/device/apps/api/utility/general/URI.getAbsoluteURL (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
      // 24d: astore 5
      // 24f: new java/lang/Object
      // 252: dup
      // 253: aload 5
      // 255: invokespecial net/rim/device/api/browser/field/RequestedResource.<init> (Ljava/lang/String;)V
      // 258: astore 6
      // 25a: aload 0
      // 25b: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._browserContent Lnet/rim/device/api/browser/field/BrowserContentBaseImpl;
      // 25e: invokevirtual net/rim/device/api/browser/field/BrowserContentBaseImpl.getRenderingApplication ()Lnet/rim/device/api/browser/field/RenderingApplication;
      // 261: astore 7
      // 263: aconst_null
      // 264: astore 8
      // 266: aload 7
      // 268: dup
      // 269: instanceof java/lang/Object
      // 26c: ifne 273
      // 26f: pop
      // 270: goto 280
      // 273: checkcast java/lang/Object
      // 276: aload 6
      // 278: aconst_null
      // 279: invokeinterface net/rim/device/api/browser/field/ResourceProvider.getInputConnection (Lnet/rim/device/api/browser/field/RequestedResource;Lnet/rim/device/api/browser/field/BrowserContent;)Ljavax/microedition/io/InputConnection; 3
      // 27e: astore 8
      // 280: aload 8
      // 282: ifnull 2e1
      // 285: aconst_null
      // 286: astore 9
      // 288: aload 8
      // 28a: invokeinterface javax/microedition/io/InputConnection.openInputStream ()Ljava/io/InputStream; 1
      // 28f: astore 9
      // 291: aload 9
      // 293: invokestatic net/rim/device/api/io/IOUtilities.streamToBytes (Ljava/io/InputStream;)[B
      // 296: astore 10
      // 298: aload 10
      // 29a: ifnull 2a7
      // 29d: aload 10
      // 29f: bipush 0
      // 2a0: aload 10
      // 2a2: arraylength
      // 2a3: invokestatic net/rim/device/api/system/EncodedImage.createEncodedImage ([BII)Lnet/rim/device/api/system/EncodedImage;
      // 2a6: astore 3
      // 2a7: aload 9
      // 2a9: ifnull 2e1
      // 2ac: aload 9
      // 2ae: invokevirtual java/io/InputStream.close ()V
      // 2b1: goto 2e1
      // 2b4: astore 10
      // 2b6: goto 2e1
      // 2b9: astore 10
      // 2bb: aload 9
      // 2bd: ifnull 2e1
      // 2c0: aload 9
      // 2c2: invokevirtual java/io/InputStream.close ()V
      // 2c5: goto 2e1
      // 2c8: astore 10
      // 2ca: goto 2e1
      // 2cd: astore 11
      // 2cf: aload 9
      // 2d1: ifnull 2de
      // 2d4: aload 9
      // 2d6: invokevirtual java/io/InputStream.close ()V
      // 2d9: goto 2de
      // 2dc: astore 12
      // 2de: aload 11
      // 2e0: athrow
      // 2e1: aload 3
      // 2e2: ifnull 2f3
      // 2e5: new net/rim/device/apps/internal/browser/plugin/media/field/AlbumArtField
      // 2e8: dup
      // 2e9: aload 3
      // 2ea: bipush 1
      // 2eb: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/AlbumArtField.<init> (Lnet/rim/device/api/system/EncodedImage;Z)V
      // 2ee: astore 5
      // 2f0: goto 300
      // 2f3: new net/rim/device/apps/internal/browser/plugin/media/field/AlbumArtField
      // 2f6: dup
      // 2f7: getstatic net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._noArt Lnet/rim/device/api/system/EncodedImage;
      // 2fa: bipush 0
      // 2fb: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/AlbumArtField.<init> (Lnet/rim/device/api/system/EncodedImage;Z)V
      // 2fe: astore 5
      // 300: aload 4
      // 302: aload 5
      // 304: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 307: aload 0
      // 308: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 30b: ifnull 32f
      // 30e: new net/rim/device/apps/internal/browser/plugin/media/field/MarqueeDetailsField
      // 311: dup
      // 312: aload 0
      // 313: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 316: ldc2_w 36028797018963968
      // 319: bipush 1
      // 31a: bipush 0
      // 31b: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MarqueeDetailsField.<init> (Ljava/lang/String;JZZ)V
      // 31e: astore 6
      // 320: aload 6
      // 322: getstatic net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.TRACK_TITLE_TAG Lnet/rim/device/api/ui/theme/Tag;
      // 325: invokevirtual net/rim/device/api/ui/Field.setTag (Lnet/rim/device/api/ui/theme/Tag;)V
      // 328: aload 4
      // 32a: aload 6
      // 32c: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 32f: aload 1
      // 330: ifnull 391
      // 333: bipush 1
      // 334: anewarray 6512
      // 337: dup
      // 338: bipush 0
      // 339: ldc_w "author"
      // 33c: aastore
      // 33d: astore 7
      // 33f: bipush 0
      // 340: istore 8
      // 342: iload 8
      // 344: aload 7
      // 346: arraylength
      // 347: if_icmpge 391
      // 34a: aconst_null
      // 34b: astore 9
      // 34d: aload 1
      // 34e: aload 7
      // 350: iload 8
      // 352: aaload
      // 353: invokeinterface javax/microedition/media/control/MetaDataControl.getKeyValue (Ljava/lang/String;)Ljava/lang/String; 2
      // 358: astore 9
      // 35a: goto 35f
      // 35d: astore 10
      // 35f: aload 9
      // 361: ifnull 38b
      // 364: aload 9
      // 366: invokevirtual java/lang/String.length ()I
      // 369: ifle 38b
      // 36c: new net/rim/device/apps/internal/browser/plugin/media/field/MarqueeDetailsField
      // 36f: dup
      // 370: aload 9
      // 372: ldc2_w 36028797018963968
      // 375: bipush 0
      // 376: bipush 1
      // 377: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MarqueeDetailsField.<init> (Ljava/lang/String;JZZ)V
      // 37a: astore 6
      // 37c: aload 6
      // 37e: getstatic net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.PROPERTY_TAG Lnet/rim/device/api/ui/theme/Tag;
      // 381: invokevirtual net/rim/device/api/ui/Field.setTag (Lnet/rim/device/api/ui/theme/Tag;)V
      // 384: aload 4
      // 386: aload 6
      // 388: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 38b: iinc 8 1
      // 38e: goto 342
      // 391: aload 0
      // 392: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._screenManager Lnet/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager;
      // 395: bipush 0
      // 396: aload 4
      // 398: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager.addField (ILnet/rim/device/api/ui/Field;)V
      // 39b: aload 0
      // 39c: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._screenManager Lnet/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager;
      // 39f: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager.isPlaylistActive ()Z
      // 3a2: ifne 3d6
      // 3a5: aload 0
      // 3a6: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._screenManager Lnet/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager;
      // 3a9: bipush 0
      // 3aa: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager.setActiveView (I)V
      // 3ad: goto 3d6
      // 3b0: aload 0
      // 3b1: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._screenManager Lnet/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager;
      // 3b4: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager.getOverlayTextId ()I
      // 3b7: sipush 899
      // 3ba: if_icmpeq 3cd
      // 3bd: aload 0
      // 3be: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._screenManager Lnet/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager;
      // 3c1: sipush 851
      // 3c4: ldc_w 16777215
      // 3c7: ldc_w 2263842
      // 3ca: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager.setOverlayText (III)V
      // 3cd: aload 0
      // 3ce: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._screenManager Lnet/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager;
      // 3d1: bipush 2
      // 3d3: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/TextOverlayFieldManager.setActiveView (I)V
      // 3d6: invokestatic net/rim/device/api/system/Application.getEventLock ()Ljava/lang/Object;
      // 3d9: dup
      // 3da: astore 3
      // 3db: monitorenter
      // 3dc: aload 0
      // 3dd: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 3e0: ifnull 40d
      // 3e3: aload 0
      // 3e4: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._browserContent Lnet/rim/device/api/browser/field/BrowserContentBaseImpl;
      // 3e7: new java/lang/Object
      // 3ea: dup
      // 3eb: invokespecial java/lang/StringBuffer.<init> ()V
      // 3ee: sipush 833
      // 3f1: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 3f4: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 3f7: ldc_w " - "
      // 3fa: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 3fd: aload 0
      // 3fe: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._title Ljava/lang/String;
      // 401: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 404: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 407: invokevirtual net/rim/device/api/browser/field/BrowserContentBaseImpl.setTitle (Ljava/lang/String;)V
      // 40a: goto 41a
      // 40d: aload 0
      // 40e: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._browserContent Lnet/rim/device/api/browser/field/BrowserContentBaseImpl;
      // 411: sipush 833
      // 414: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 417: invokevirtual net/rim/device/api/browser/field/BrowserContentBaseImpl.setTitle (Ljava/lang/String;)V
      // 41a: aload 0
      // 41b: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._player Ljavax/microedition/media/Player;
      // 41e: invokeinterface javax/microedition/media/Player.getDuration ()J 1
      // 423: lstore 4
      // 425: lload 4
      // 427: bipush -1
      // 429: i2l
      // 42a: lcmp
      // 42b: ifeq 47e
      // 42e: lload 4
      // 430: sipush 1000
      // 433: i2l
      // 434: ldiv
      // 435: lstore 6
      // 437: aload 0
      // 438: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._seekSliderField Lnet/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField;
      // 43b: lload 6
      // 43d: l2i
      // 43e: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/SeekablePositionField.setTotalTime (I)V
      // 441: aload 0
      // 442: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._ds Ljavax/microedition/media/protocol/DataSource;
      // 445: dup
      // 446: instanceof java/lang/Object
      // 449: ifne 450
      // 44c: pop
      // 44d: goto 458
      // 450: checkcast java/lang/Object
      // 453: lload 6
      // 455: invokevirtual net/rim/device/internal/media/HTTPDataSource.setEstimatedTime (J)V
      // 458: aload 0
      // 459: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._totaltimeNode Lnet/rim/plazmic/internal/mediaengine/service/node/TextNode;
      // 45c: ifnull 47e
      // 45f: aload 0
      // 460: aload 0
      // 461: lload 6
      // 463: l2i
      // 464: invokespecial net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.getTimeDisplay (I)[C
      // 467: putfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._totaltimeString [C
      // 46a: aload 0
      // 46b: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._totaltimeNode Lnet/rim/plazmic/internal/mediaengine/service/node/TextNode;
      // 46e: aload 0
      // 46f: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._totaltimeString [C
      // 472: invokeinterface net/rim/plazmic/internal/mediaengine/service/node/TextNode.setString ([C)V 2
      // 477: aload 0
      // 478: getfield net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField._mediaFieldBar Lnet/rim/device/apps/internal/browser/plugin/media/field/MyMediaField;
      // 47b: invokevirtual net/rim/device/api/ui/MediaField.invalidate ()V
      // 47e: aload 0
      // 47f: bipush 0
      // 480: invokevirtual net/rim/device/apps/internal/browser/plugin/media/field/MediaBrowserField.updateDurationField (I)V
      // 483: aload 3
      // 484: monitorexit
      // 485: return
      // 486: astore 13
      // 488: aload 3
      // 489: monitorexit
      // 48a: aload 13
      // 48c: athrow
      // try (112 -> 127): 128 null
      // try (222 -> 230): 246 null
      // try (231 -> 245): 246 null
      // try (303 -> 305): 306 null
      // try (287 -> 301): 308 null
      // try (311 -> 313): 314 null
      // try (287 -> 301): 316 null
      // try (308 -> 309): 316 null
      // try (319 -> 321): 322 null
      // try (316 -> 317): 316 null
      // try (378 -> 384): 385 null
      // try (440 -> 515): 516 null
      // try (516 -> 519): 516 null
   }

   private final void play(boolean startNewThread) {
      try {
         if (this._player != null && this._player.getState() == 400) {
            this.pause();
         } else {
            this._playWhenStreamingReady = false;
            if (this.restartHTTPBuffering()) {
               return;
            }

            if (!this.canPlay()) {
               return;
            }

            if (!this._isAudio && !this._visible) {
               return;
            }

            if (this._errorOccured) {
               return;
            }

            this._audioRouter.setAudioSinkCallback(this);
            if (this._requiresReinitialization) {
               this.reinitInputConnection();
               if (this._player == null) {
                  String errorMsg = BrowserResources.getString(845);
                  if (this._playlistThread != null) {
                     Status.show(errorMsg);
                     this._playlistThread.nextItem();
                  } else {
                     Dialog.alert(errorMsg);
                     this._errorOccured = true;
                  }

                  return;
               }
            }

            this.updateScreenCoords();
            if (MediaOptionsRegistry.getInstance().getBoolean(-4212305096992551720L)) {
               this.toggleFullscreen();
            }

            if (!this._isAudio && this._screenManager != null && this._screenManager.isPlaylistActive()) {
               this._screenManager.setActiveView(2);
            }

            this._paused = false;
            synchronized (Application.getEventLock()) {
               this._videoPauseBitmap.setBitmap(null);
            }

            if (startNewThread) {
               MediaBrowserField mbf = this;
               new MediaBrowserField$3(this, mbf).start();
            } else if (this._player != null) {
               this._player.start();
               this._wasPlaying = false;
               this._wasPlayingOnObscured = false;
               this._queuedPlayback = false;
               this.showPause();
               return;
            }
         }
      } finally {
         return;
      }
   }

   private final void invalidateScreen() {
      if (this._fullscreen) {
         Manager screen = this.getManager();
         if (screen != null) {
            screen.invalidate();
         }
      }
   }

   private final void showPause() {
      this._playing = true;
      this._playButton.setImage(this.PAUSE_BMP);
      this._playButtonHighlight.setImage(this.PAUSE_H_BMP);
      this._mediaFieldBar.invalidate();
   }

   private final void showPlay() {
      this._playing = false;
      this._playButton.setImage(this.PLAY_BMP);
      this._playButtonHighlight.setImage(this.PLAY_H_BMP);
      this._mediaFieldBar.invalidate();
   }

   private final void resetMediaEngineToPlay() {
      this._queuedPlayback = true;
      if (this._pmePlayerBar != null) {
         try {
            this._pmePlayerBar.stop();
            this._pmePlayerBar.setMediaTime(0);
            if (this._totaltimeNode != null) {
               this._totaltimeNode.setString(this._totaltimeString);
            }

            if (this._volumeField != null) {
               this._volumeField.invalidate();
            }

            if (this._seekSliderField != null && this._seekSliderField.isEnabled()) {
               this._seekSliderField.toggleEnabled();
            }

            if (this._player != null) {
               int milliseconds = (int)(this._player.getMediaTime() / 1000);
               if (this._seekSliderField != null) {
                  this._seekSliderField.updateTime(milliseconds);
                  if (!this._seekSliderField.isEnabled()) {
                     this.updateDurationField(milliseconds);
                  }

                  this._seekSliderField.resetAspectRatio();
               }
            }

            if (this._modelBar != null) {
               this._modelBar.trigger(107, this._modelBar.getHandle(this._repeat ? "repeatOn" : "repeatOff"), null);
               if (this._playlist != null && this._playlist.getNumberOfItems() > 1 && !this._singleFilePlayback) {
                  this._modelBar.trigger(107, this._modelBar.getHandle(this._shuffled ? "shuffleOn" : "shuffleOff"), null);
               }
            }

            if (this._visible) {
               this._pmePlayerBar.start();
            }

            if (this._focusManager != null) {
               this._focusManager.resetFocus();
               return;
            }
         } finally {
            return;
         }
      }
   }

   private final void setAutoBacklight() {
      if (!this.isAudio() && MediaOptionsRegistry.getInstance().getBoolean(-1314075862077144981L)) {
         InternalServices.setLightSensorMode(4);
      }
   }

   private final void resetAutoBacklight() {
      boolean defaultMode = UiSettings.getAutomaticBacklightEnabled();
      InternalServices.setLightSensorMode(defaultMode ? 1 : 3);
   }

   private final void pause() {
      try {
         if (this._errorOccured) {
            return;
         }

         if (this._player.getState() == 400) {
            this._paused = true;
            this._audioRouter.setAudioSinkCallback(null);
            if (this._fullscreen) {
               this.toggleFullscreen();
            }

            this._player.stop();
            this.showPlay();
            return;
         }
      } finally {
         return;
      }
   }

   private final void stop(boolean seekToZero) {
      try {
         if (!this._errorOccured) {
            this._audioRouter.setAudioSinkCallback(null);
            this._paused = false;
            this._wasPlaying = false;
            if (this._fullscreen) {
               this.toggleFullscreen();
            }

            this._player.stop();
            this.showPlay();
            if (!this._isAudio) {
               this._videoPauseBitmap.setBitmap(null);
            }

            if (seekToZero) {
               label61:
               try {
                  this._player.setMediaTime(0);
               } finally {
                  break label61;
               }

               this._seekSliderField.updateTime(0);
               if (this._durationNode != null) {
                  this._durationNode.setString(this.getTimeDisplay(0));
                  this._mediaFieldBar.invalidate();
               }
            }

            this.stopHTTPBuffer();
         }
      } finally {
         return;
      }
   }

   private final void stopHTTPBuffer() {
      if (this._ds instanceof Object) {
         HTTPBufferingManager bufferingManager = this.getBufferingManager();
         if (bufferingManager != null && !bufferingManager.bufferContainsAllContent()) {
            this._playWhenStreamingReady = false;
            bufferingManager.setHTTPBufferingCallback(null);
            this._ds.disconnect();
            this._seekSliderField.updateStreaming((double)0L);
            return;
         }

         if (bufferingManager == null) {
            this._seekSliderField.updateStreaming((double)0L);
         }
      }
   }

   private final void toggleFullscreen() {
      if (!this._isAudio && this._videoControl != null && this._player != null) {
         label59:
         try {
            this._videoControl.setDisplayFullScreen(!this._fullscreen);
         } finally {
            break label59;
         }

         this._fullscreen = !this._fullscreen;
         if (!this._fullscreen) {
            Manager screen = this.getManager();
            if (screen != null) {
               synchronized (Application.getEventLock()) {
                  screen.invalidate();
               }
            }

            this._fullscreenRunnable.queue(4000);
         }
      }
   }

   private final void updateScreenCoords() {
      if (this._videoControl != null && this._screenManager != null) {
         int videoWidth = this._screenManager.getWidth();
         int videoHeight = this._screenManager.getHeight();
         XYRect rect = (XYRect)(new Object(this._screenManager.getExtent()));
         this._screenManager.getManager().transformToScreen(rect);
         int videoX = rect.x;
         int videoY = rect.y;

         label151:
         try {
            if (videoWidth == this._videoControl.getDisplayWidth()
               && videoHeight == this._videoControl.getDisplayHeight()
               && videoX == this._videoControl.getDisplayX()
               && videoY == this._videoControl.getDisplayY()) {
               return;
            }

            if (videoWidth <= 0 || videoHeight <= 0 || videoX < 0 || videoY < 0) {
               return;
            }
         } finally {
            break label151;
         }

         label133:
         try {
            this._videoControl.initDisplayMode(1, this);
         } finally {
            break label133;
         }

         try {
            this._videoControl.setDisplayLocation(videoX, videoY);
            this._videoControl.setDisplaySize(videoWidth, videoHeight);
         } finally {
            return;
         }
      }
   }

   private final boolean canPlay() {
      if (Phone.getInstance().isActive()) {
         return false;
      }

      if (AudioRouter.getInstance().getActiveSource() < 3) {
         return false;
      }

      ActiveMedia active = ActiveMediaObservable.getActiveMedia();
      if (active != null && active != this.getScreen()) {
         String logEvent = ((StringBuffer)(new Object("AVM,"))).append(active.toString()).toString();
         EventLogger.logEvent(-6484753878348010781L, logEvent.getBytes(), 2);
         return false;
      }

      if (active != null && this._player != null && this._player instanceof Object) {
         try {
            ((StreamDataControl)this._player).setKeyValue("active_media_source", active);
            return true;
         } finally {
            return true;
         }
      } else {
         return true;
      }
   }

   private final boolean restartHTTPBuffering() {
      if (this._ds instanceof Object) {
         HTTPBufferingManager bufferingManager = this.getBufferingManager();
         if (bufferingManager == null) {
            Object lock = Application.getEventLock();
            Thread thread = new MediaBrowserField$2(this, lock);
            synchronized (lock) {
               thread.start();
               lock.wait();
            }

            this._playWhenStreamingReady = true;
            return true;
         }
      }

      return false;
   }

   private final char[] getTimeDisplay(int time) {
      this._buffer.setLength(0);
      this._calendar.setTimeLong(time);
      if (time < 3600000) {
         this._formatShort.format(this._calendar, this._buffer, null);
      } else {
         this._formatLong.format(this._calendar, this._buffer, null);
      }

      return this._buffer.toString().toCharArray();
   }

   private final void initDataSource(
      InputConnection conn, InputStream input, long size, String contentType, boolean fileBased, String currentUrl, HTTPBufferingManager bufferingManager
   ) {
      this.destroyDataSource();
      if (contentType != null && contentType.startsWith("audio")) {
         this._isAudio = true;
      } else {
         this._isAudio = false;
      }

      this._fileBased = fileBased;
      this._currentUrl = currentUrl;
      if (bufferingManager != null) {
         this._ds = (DataSource)(new Object(this, bufferingManager, currentUrl, contentType));
      } else if (input instanceof Object) {
         this._ds = (DataSource)(new Object(input, contentType, size));
      } else if (!(conn instanceof Object)) {
         this._ds = new MediaDataSource(input, size, contentType);
      } else {
         RTSPConnection rtsp = (RTSPConnection)conn;
         this._ds = RTSPDataSourceFactory.createNewDataSource(rtsp.getURL(), rtsp.getUserAgent());
      }
   }

   @Override
   protected final boolean keyControl(char character, int status, int time) {
      if (super.keyControl(character, status, time)) {
         return true;
      } else if (character == 128 && !this.isAudio() && this._player != null && this._player.getState() == 400) {
         this.externalToggleFullscreen();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final void sublayout(int width, int height) {
      if (this._modelBar != null && !this._embedded) {
         synchronized (this._modelBar) {
            int availHeight = this._browserContent.getRenderingApplication().getAvailableHeight(this._browserContent);
            SVGNode barRoot = this._modelBar.getRoot();
            int barHeight = barRoot.getHeight() >> 16;
            int screenHeight = availHeight - barHeight;
            this._screenManager.setDimensions(Display.getWidth(), screenHeight);
            this.updateScreenCoords();
         }
      }

      super.sublayout(width, height);
      if (this._embedded) {
         this.setExtent(this.getPreferredWidth(), this.getPreferredHeight());
         this.setVirtualExtent(this.getPreferredWidth(), this.getPreferredHeight());
      }
   }

   @Override
   public final int getPreferredHeight() {
      return this._embedded ? this._mediaFieldBar.getHeight() : this._browserContent.getRenderingApplication().getAvailableHeight(this._browserContent);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      if (this._playlist != null) {
         if (this._playlist.getNumberOfItems() > 1) {
            menu.add(new SkipTrackMenuItem(false, this));
            menu.add(new SkipTrackMenuItem(true, this));
            if (!this._singleFilePlayback) {
               menu.add(new ToggleShuffleMenuItem(this));
            }

            MenuItem toggleMenuItem = new TogglePlaylistViewMenuItem(this);
            menu.add(toggleMenuItem);
            menu.setDefault(toggleMenuItem);
         }
      } else if (this._tuneName == null
         && this.getBufferingManager() != null
         && this._renderingOptions != null
         && (this._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 27, true) || this._drmForwardLock)) {
         menu.add(new SaveMenuItem(this, this._drmForwardLock, this._currentUrl, this._contentType));
      }

      if (!Phone.getInstance().isActive() && this._audioPathControl != null) {
         VerbToMenu verbToMenu = VerbToMenuFactory.createInstance();
         VerbFactory[] factories = VerbFactoryRepository.getVerbFactories(-5280468186386428176L);
         if (factories != null) {
            for (int i = factories.length - 1; i >= 0; i--) {
               verbToMenu.addVerbs(factories[i].getVerbs(this._audioPathControl));
            }
         }

         verbToMenu.getMenu(menu, null);
      }

      if (!this.isAudio()) {
         MenuItem fullscreen = new FullscreenMenuItem(this, MediaOptionsRegistry.getInstance().getBoolean(-4212305096992551720L));
         menu.add(fullscreen);
         menu.setDefault(fullscreen);
      } else if (this._tuneName != null) {
         menu.add(new SetAsRingtoneMenuItem(this._tuneName));
      }

      menu.add(new RepeatMenuItem(this, this._repeat));
      menu.add(new ReplayMenuItem(this));
      super.makeMenu(menu, instance);
   }

   private final void setSortedPlaylistOrder() {
      this._shuffled = false;
      int i = this._playlistOrder.length - 1;

      while (i >= 0) {
         this._playlistOrder[i] = i--;
      }
   }

   private final void reinitInputConnection() {
      this._requiresReinitialization = false;
      InputConnection inputConnection = this.requestResource(this._currentUrl);
      if (inputConnection != null) {
         boolean fileBased = false;
         if (inputConnection instanceof Object) {
            fileBased = true;
            if (!((FileConnection)inputConnection).exists()) {
               try {
                  inputConnection.close();
                  return;
               } finally {
                  return;
               }
            }
         }

         String contentType = RendererControl.getContentType(inputConnection);
         InputStream in = null;

         try {
            in = RendererControl.getInputStreamFromContentEncoding(inputConnection, inputConnection.openInputStream());
            this.initDataSource(
               inputConnection, in, RendererControl.getContentLength(inputConnection), contentType, fileBased, RendererControl.getUrl(inputConnection), null
            );
            this.initContent();
         } finally {
            if (in != null) {
               try {
                  in.close();
                  return;
               } finally {
                  return;
               }
            } else {
               return;
            }
         }
      }
   }

   private final void setShuffledPlaylistOrder() {
      this._shuffled = true;
      if (this._playlistOrder.length != 0) {
         Random random = (Random)(new Object(System.currentTimeMillis()));
         int currentIndex = this._playlistCurrentIndex - 1;
         int numEntries = this._playlistOrder.length;

         for (int i = 0; i < 100; i++) {
            for (int j = 0; j < numEntries; j++) {
               int swapIndex;
               do {
                  swapIndex = random.nextInt(numEntries);
               } while (swapIndex == j);

               int temp = this._playlistOrder[j];
               this._playlistOrder[j] = this._playlistOrder[swapIndex];
               this._playlistOrder[swapIndex] = temp;
            }
         }

         if (currentIndex >= 0 && currentIndex < this._playlistOrder.length) {
            for (int j = 0; j < numEntries; j++) {
               if (this._playlistOrder[j] == currentIndex) {
                  this._playlistOrder[j] = this._playlistOrder[0];
                  this._playlistOrder[0] = currentIndex;
                  return;
               }
            }
         }
      }
   }

   private final boolean getCoords(int handle, XYRect rect) {
      if (handle == -1) {
         return false;
      }

      Node node = this._modelBar.getNodeObject(handle);
      if (!(node instanceof Object)) {
         return false;
      }

      ViewportNode viewport = (ViewportNode)node;
      rect.set(
         Fixed32.toInt(viewport.getX()), Fixed32.toInt(viewport.getY()), Fixed32.toInt(viewport.getActualWidth()), Fixed32.toInt(viewport.getActualHeight())
      );
      if (handle == this._modelBar.getHandle("playbutton")) {
         rect.width >>= 1;
         return true;
      }

      if (handle == this._modelBar.getHandle("stopbutton")) {
         rect.width >>= 1;
         rect.x = rect.x + rect.width;
      }

      return true;
   }

   @Override
   public final void getFocusRect(XYRect rect) {
      int focusItem = this._focusInteractor.getItemInFocus();
      if (!this.getCoords(focusItem, rect)) {
         super.getFocusRect(rect);
      }
   }

   @Override
   protected final boolean stylusTap(int x, int y, int status, int time) {
      int hitFocus = this.hitTest(x, y);
      return hitFocus == -1 || this._focusInteractor.getItemInFocus() != hitFocus;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final int hitTest(int x, int y) {
      XYRect rect = Ui.getTmpXYRect();
      boolean var9 = false /* VF: Semaphore variable */;

      int var6;
      label56: {
         try {
            var9 = true;

            for (int i = 0; i < 2; i++) {
               int id = this._modelBar.getHandle(i == 0 ? "playbutton" : "stopbutton");
               if (this.getCoords(id, rect) && rect.contains(x, y)) {
                  var6 = id;
                  var9 = false;
                  break label56;
               }
            }

            var9 = false;
         } finally {
            if (var9) {
               Ui.returnTmpXYRect(rect);
            }
         }

         Ui.returnTmpXYRect(rect);
         return -1;
      }

      Ui.returnTmpXYRect(rect);
      return var6;
   }

   @Override
   public final boolean onCursorHover(int x, int y) {
      if (this._embedded) {
         int focusItem = this._focusInteractor.getItemInFocus();
         int newFocus = this.hitTest(x, y);
         if (focusItem != newFocus) {
            this._focusInteractor.setFocusToItem(newFocus);
            return true;
         } else {
            return false;
         }
      } else {
         return super.onCursorHover(x, y);
      }
   }

   @Override
   public final int getPreferredWidth() {
      return this._embedded ? this._mediaFieldBar.getWidth() : Display.getWidth();
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      char key = Keypad.map(keycode);
      if (key == '$') {
         this._audioPathControl.toggleSpeakerphone();
      }

      if (this._myKeyHandler.keyDown(keycode, time)) {
         this._fullscreenRunnable.reset();
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   @Override
   protected final boolean keyChar(char character, int status, int time) {
      if (super.keyChar(character, status, time)) {
         this._fullscreenRunnable.reset();
         return true;
      }

      int PREVIOUS = 1;
      int NEXT = 2;
      int action = -1;
      if (InternalServices.isReducedFormFactor()) {
         int keycode = Keypad.getKeyCode(character, status);
         switch (keycode) {
            case 68:
               action = PREVIOUS;
               break;
            case 74:
               action = NEXT;
         }
      } else if (character == this._hotkeyPrevious) {
         action = PREVIOUS;
      } else if (character == this._hotkeyNext) {
         action = NEXT;
      }

      if (action == PREVIOUS) {
         if (this.previousTrack()) {
            return true;
         }
      } else if (action == NEXT) {
         if (this.nextTrack()) {
            return true;
         }
      } else {
         if (character == 27) {
            if (this._seekSliderField != null && this._seekSliderField.isEnabled()) {
               this._seekSliderField.cancelSeek();
               this._fullscreenRunnable.queue();
               return true;
            }

            if (this._volumeField != null && this._volumeField.isEnabled()) {
               this._volumeField.toggleEnabled();
               return true;
            }

            if (this.isFullscreen()) {
               this.toggleFullscreen();
               return true;
            }

            return false;
         }

         if (character == ' ' && this._mediaFieldBar != null && this.getLeafFieldWithFocus() != this._mediaFieldBar) {
            this._mediaFieldBar.setFocus();
            return true;
         }
      }

      this._fullscreenRunnable.reset();
      return false;
   }

   @Override
   protected final boolean keyUp(int keycode, int time) {
      if (this._myKeyHandler.keyUp(keycode, time)) {
         this._fullscreenRunnable.reset();
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   private final boolean previousTrack() {
      long mediaTime = this._player != null ? this._player.getMediaTime() : -1;
      if (mediaTime != -1 && mediaTime > 2000000) {
         new ReplayMenuItem(this).run();
         this._audioRouter.setAudioSinkCallback(null);
         return true;
      } else if (this._playlist != null && this._playlist.getNumberOfItems() > 1) {
         this._audioRouter.setAudioSinkCallback(null);
         this.skipTrack(false);
         return true;
      } else {
         return false;
      }
   }

   private final boolean nextTrack() {
      if (this._playlist != null && this._playlist.getNumberOfItems() > 1) {
         this._audioRouter.setAudioSinkCallback(null);
         this.skipTrack(true);
         return true;
      } else {
         return false;
      }
   }

   private final void destroyDataSource() {
      if (this._ds != null) {
         if (!(this._ds instanceof MediaDataSource)) {
            if (this._ds instanceof Object) {
               this._ds.disconnect();
            } else {
               label30:
               try {
                  this._ds.stop();
               } finally {
                  break label30;
               }
            }
         } else {
            ((MediaDataSource)this._ds).close();
         }

         this._ds = null;
      }
   }

   private final void destroyPlayer() {
      if (this._player != null) {
         label21:
         try {
            this._player.removePlayerListener(this);
            this._player.close();
         } finally {
            break label21;
         }

         this._player = null;
      }
   }

   private final boolean onClick(int status, int time) {
      return this._focus != null && !"menu".equals(this._focus);
   }

   @Override
   protected final boolean navigationUnclick(int status, int time) {
      return true;
   }

   @Override
   protected final void onUndisplay() {
      super.onUndisplay();
      if (this._destroyableState == 0) {
         this.destroy();
      } else {
         this.pause();
      }

      this._application.removeKeyListener(this._myKeyHandler);
      this._application.removeSystemListener(this);
      this._application.removeFileSystemListener(this);
      Audio.removeListener(this._application, this._myKeyHandler);
      VADUserEvents.removeListener(this._application, this._myKeyHandler);
   }

   @Override
   protected final void onDisplay() {
      super.onDisplay();
      if (this._focusManager != null) {
         this._focusManager.resetFocus();
      }

      this._application.addKeyListener(this._myKeyHandler);
      this._application.addSystemListener(this);
      this._application.addFileSystemListener(this);
      Audio.addListener(this._application, this._myKeyHandler);
      VADUserEvents.addListener(this._application, this._myKeyHandler);
      this.resetMediaEngineToPlay();
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      super.onVisibilityChange(visible);
      this._visible = visible;
      if (visible) {
         if (this._pmePlayerBar != null) {
            label44:
            try {
               this._pmePlayerBar.start();
            } finally {
               break label44;
            }
         }

         if (this._playlistThread != null && !this._playlistThread.isAlive()) {
            this._playlistThread.start();
            return;
         }
      } else {
         if (this._pmePlayerBar != null) {
            this._pmePlayerBar.stop();
         }

         if (!this._isAudio) {
            this.pause();
         }

         this._wasPlayingOnObscured = false;
         this._queuedPlayback = false;
      }
   }

   static final int access$2408(MediaBrowserField x0) {
      return x0._playlistCurrentIndex++;
   }

   static final int access$2420(MediaBrowserField x0, int x1) {
      return x0._playlistCurrentIndex -= x1;
   }
}
