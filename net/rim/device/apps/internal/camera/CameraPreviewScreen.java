package net.rim.device.apps.internal.camera;

import java.io.InputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.JPEGEncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MediaField;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.file.ExplorerServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.MIMEContentVerbRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.api.ui.HintPollingThread;
import net.rim.device.apps.api.ui.HintPollingThread$HintProvider;
import net.rim.device.apps.api.ui.HintPopup;
import net.rim.device.apps.api.ui.PopupStatus;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.internal.camera.Camera;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.NodeImpl;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;
import net.rim.plazmic.internal.mediaengine.service.node.TextNode;
import net.rim.plazmic.mediaengine.MediaListener;
import net.rim.plazmic.mediaengine.MediaPlayer;

final class CameraPreviewScreen extends MainScreen implements MediaListener, HintPollingThread$HintProvider {
   private Field _banner;
   private Bitmap _previewBitmap;
   private BitmapField _previewImage;
   private String _filename;
   private TextNode _filenameNode;
   private String _path;
   private String _fileExtension;
   private String _hint;
   private byte[] _imageData;
   private CameraPreviewScreen$MyMediaManager _manager;
   private ModelInteractorImpl _model;
   private MediaField _mediaField;
   private MediaPlayer _pmePlayer;
   private Verb[] _sendVerbs;
   private int _sendHandle;
   private CameraPreviewScreen$CustomFocusOrder _focusManager = new CameraPreviewScreen$CustomFocusOrder(this);
   private static final String MIME_TYPE = "image/jpeg";
   private static final String CAPTURE = "capture";
   private static final String DELETE = "delete";
   private static final String FOLDER = "folder";
   private static final String SEND = "send";
   private static final String SETAS = "setAs";
   private static final String FILENAME = "filename";
   private static final String ENABLE_SEND = "enableSend";
   private static final String DISABLE_SEND = "disableSend";
   private static final long CONTROL_STYLE = 12884967424L;
   private static final long INDICATOR_STYLE = 36028809903865856L;

   public final void updatePreview() {
      if (this._previewBitmap == null) {
         this._previewBitmap = (Bitmap)(new Object(197, 240, 180));
         this._previewImage.setBitmap(this._previewBitmap);
      }

      Camera.getPreview(this._previewBitmap);
   }

   public final void updatePreview(JPEGEncodedImage jpeg) {
      int scale = Fixed32.div(Fixed32.toFP(jpeg.getWidth()), Fixed32.toFP(240));
      jpeg.setScaleX32(scale);
      jpeg.setScaleY32(scale);
      this._previewImage.setImage(jpeg);
   }

   public final void setImageData(byte[] image) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final boolean renameImage() {
      Object obj = null;
      if (this._filename != null && !this._filename.equals("")) {
         String fileURL = FileUtilities.makeFileURL(this._path, ((StringBuffer)(new Object())).append(this._filename).append(this._fileExtension).toString());
         if (FileUtilities.checkFileExists(fileURL)) {
            obj = fileURL;
         }
      }

      if (obj == null) {
         obj = new Object(this._imageData, 0, this._imageData.length);
      }

      Verb renameVerb = this.getRenameVerb();
      if (renameVerb != null) {
         obj = renameVerb.invoke(obj);
         if (obj instanceof Object) {
            String newname = (String)obj;
            this.setFileName(FileUtilities.getPath(newname), FileUtilities.getName(newname));
            return true;
         }
      }

      return false;
   }

   public final void setFileName(String path, String filename) {
      this._path = path;
      this.setFileName(filename);
   }

   public final void setFileName(String filename) {
      String ext = null;
      String name = "";
      boolean fileExists = false;
      if (filename == null) {
         this._focusManager.enableSendButton(false);
      } else {
         fileExists = FileUtilities.checkFileExists(FileUtilities.makeFileURL(this._path, filename));
         this._focusManager.enableSendButton(fileExists && this._sendVerbs != null && this._sendVerbs.length > 0);
         int endPos = filename.lastIndexOf(46);
         if (endPos < 0) {
            endPos = filename.length();
         }

         ext = filename.substring(endPos, filename.length());
         name = filename.substring(0, endPos);
      }

      this._fileExtension = ext;
      this._filename = name;
      if (fileExists) {
         name = FileUtilities.getDisplayName(name);
         this._filenameNode.setString(name.toCharArray());
         this._mediaField.invalidate();
      }
   }

   public final void displayMessage(String msg) {
      if (msg == null) {
         msg = "";
      }

      this._filenameNode.setString(msg.toCharArray());
      this._mediaField.invalidate();
   }

   @Override
   public final void mediaEvent(Object sender, int event, int eventParam, Object data) {
      if (event != 105) {
         if (event == 103) {
            String id = NodeImpl.getId(eventParam, this._model);
            String hintString = null;
            if ("folder".equals(id)) {
               hintString = CameraMain._rb.getString(42);
            } else if ("capture".equals(id)) {
               hintString = CameraMain._rb.getString(41);
            } else if ("delete".equals(id)) {
               hintString = CameraMain._rb.getString(43);
            } else if ("send".equals(id)) {
               hintString = CameraMain._rb.getString(44);
            } else if ("setAs".equals(id)) {
               hintString = CameraMain._rb.getString(45);
            }

            if (hintString != null) {
               this._hint = hintString;
               HintPollingThread.reset();
            }
         }
      } else {
         String id = NodeImpl.getId(eventParam, this._model);
         if ("capture".equals(id)) {
            this.close();
         }

         if ("delete".equals(id)) {
            this.deleteImage();
            return;
         }

         if ("folder".equals(id)) {
            this.renameImage();
            return;
         }

         if (!"setAs".equals(id)) {
            if ("send".equals(id) && this._sendVerbs != null && this._sendVerbs.length > 0) {
               int idx = 0;
               if (this._sendVerbs.length > 1) {
                  String[] verbStrings = new Object[this._sendVerbs.length];
                  int i = this._sendVerbs.length;

                  while (--i >= 0) {
                     verbStrings[i] = this._sendVerbs[i].toString();
                  }

                  Dialog pickVerb = (Dialog)(new Object(CameraMain._rb.getString(19), verbStrings, 0, true));
                  pickVerb.setEscapeEnabled(true);
                  pickVerb.setIcon(ThemeManager.getThemeAwareImage("dialog_question"));
                  idx = pickVerb.doModal();
                  if (idx == -1) {
                     return;
                  }
               }

               if (this._sendVerbs[idx] != null) {
                  ContextObject contextObj = (ContextObject)(new Object());
                  if (this._filename != null && !this._filename.equals("")) {
                     contextObj.put(
                        2765042845091913199L,
                        FileUtilities.makeFileURL(
                           ((StringBuffer)(new Object())).append(this._path).append(this._filename).append(this._fileExtension).toString()
                        )
                     );
                  } else {
                     InputStream istream = (InputStream)(new Object(this._imageData, 0, this._imageData.length));
                     contextObj.put(5473606008898265655L, istream);
                  }

                  contextObj.put(-4241241545455759532L, "image/jpeg");
                  String fileDisplayName = ((StringBuffer)(new Object()))
                     .append(FileUtilities.getDisplayName(this._filename))
                     .append(this._fileExtension)
                     .toString();
                  contextObj.put(-1188330299125235844L, fileDisplayName);
                  contextObj.put(-4886909117188079897L, fileDisplayName);
                  this._sendVerbs[idx].invoke(contextObj);
                  return;
               }
            }
         } else {
            Verb[] verbs = VerbRepository.getVerbRepository(-2843135760572915788L).getVerbs(-753816125826020042L);
            if (verbs == null) {
               verbs = new Object[0];
            }

            Arrays.add(
               verbs,
               new RenderImageVerb(
                  FileUtilities.makeFileURL(this._path, ((StringBuffer)(new Object())).append(this._filename).append(this._fileExtension).toString())
               )
            );
            if (verbs != null && verbs.length > 0) {
               int idx = 0;
               if (verbs.length > 1) {
                  String[] verbStrings = new Object[verbs.length];
                  int i = verbs.length;

                  while (--i >= 0) {
                     verbStrings[i] = verbs[i].toString();
                  }

                  Dialog pickVerb = (Dialog)(new Object(CameraMain._rb.getString(18), verbStrings, 0, true));
                  pickVerb.setEscapeEnabled(true);
                  pickVerb.setIcon(ThemeManager.getThemeAwareImage("dialog_question"));
                  idx = pickVerb.doModal();
                  if (idx == -1) {
                     return;
                  }
               }

               if (verbs[idx] != null) {
                  ContextObject contextObj = (ContextObject)(new Object());
                  if (this._filename != null && !this._filename.equals("")) {
                     contextObj.put(
                        2765042845091913199L, ((StringBuffer)(new Object())).append(this._path).append(this._filename).append(this._fileExtension).toString()
                     );
                  } else {
                     contextObj.put(8849067667159082262L, this._imageData);
                  }

                  verbs[idx].invoke(contextObj);
                  return;
               }
            }
         }
      }
   }

   @Override
   public final Object getHint() {
      LabelField hint = (LabelField)(new Object(this._hint, 51539607616L));
      hint.setTag(ThemeUtilities.TAG_CAMERA_HINT);
      return hint;
   }

   @Override
   public final void getHintPosition(XYRect rect) {
      HintPopup.transformToScreen(this._mediaField, rect);
      rect.translate(0, -rect.height);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      String fileString = ((StringBuffer)(new Object())).append(this._path).append(this._filename).append(this._fileExtension).toString();
      InputStream istream = (InputStream)(new Object(this._imageData, 0, this._imageData.length));
      ContextObject context = (ContextObject)(new Object());
      context.put(2765042845091913199L, fileString);
      context.put(8849067667159082262L, this._imageData);
      context.put(-4241241545455759532L, "image/jpeg");
      String fileDisplayName = ((StringBuffer)(new Object())).append(FileUtilities.getDisplayName(this._filename)).append(this._fileExtension).toString();
      context.put(-1188330299125235844L, fileDisplayName);
      context.put(-4886909117188079897L, fileDisplayName);
      context.put(5473606008898265655L, istream);
      MenuItem menuItem = new CameraPreviewScreen$RenameMenuItem(this);
      menu.add(menuItem);
      menu.add((MenuItem)(new Object(new CameraOptionsVerb(), Integer.MAX_VALUE)));
      Verb browseVerb = ExplorerServices.getBrowseVerb(fileString, 128, null);
      menu.add((MenuItem)(new Object(CameraMain._rb.getString(24), 591106, 0, browseVerb, null)));
      Verb[] verbs = VerbRepository.getVerbRepository(-2843135760572915788L).getVerbs(-753816125826020042L);
      if (verbs == null) {
         verbs = new Object[0];
      }

      Arrays.add(verbs, new RenderImageVerb(fileString));
      if (verbs != null) {
         for (int i = 0; i < verbs.length; i++) {
            menu.add((MenuItem)(new Object(null, verbs[i].getOrdering(), Integer.MAX_VALUE, verbs[i], context)));
         }
      }

      MenuItem[] menuItems = MIMEContentVerbRepository.getMenuItems("image/jpeg", fileString, context);
      if (menuItems != null) {
         for (int idx = 0; idx < menuItems.length; idx++) {
            menu.add(menuItems[idx]);
         }
      }

      super.makeMenu(menu, instance);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void deleteImage() {
      if (this._filename != null && !this._filename.equals("")) {
         if (Dialog.ask(2) != 3) {
            return;
         }

         boolean var3 = false /* VF: Semaphore variable */;

         label25:
         try {
            var3 = true;
            FileConnection ioe = Connector.open(
               FileUtilities.makeFileURL(this._path, ((StringBuffer)(new Object())).append(this._filename).append(this._fileExtension).toString())
            );
            ((FileConnection)ioe).delete();
            ((Connection)ioe).close();
            var3 = false;
         } finally {
            if (var3) {
               PopupStatus.show(CameraMain._rb.getString(38), 2000);
               break label25;
            }
         }
      }

      this.close();
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (key == 19) {
         this.close();
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   private final Verb getRenameVerb() {
      if (this._filename != null && !this._filename.equals("")) {
         String fileURL = FileUtilities.makeFileURL(this._path, ((StringBuffer)(new Object())).append(this._filename).append(this._fileExtension).toString());
         return FileUtilities.checkFileExists(fileURL)
            ? ExplorerServices.getRenameVerb(FileUtilities.getDisplayName(this._filename), null)
            : ExplorerServices.getSaveInputStreamVerb(fileURL, 1, true, false);
      } else {
         return ExplorerServices.getSaveInputStreamVerb(this._path, 1, true, true);
      }
   }

   @Override
   public final boolean keyChar(char c, int status, int time) {
      if (c == '\b') {
         this.deleteImage();
         return true;
      } else {
         return super.keyChar(c, status, time);
      }
   }

   CameraPreviewScreen() {
      super(2814762652205056L);
      this.setTag(ThemeUtilities.TAG_CAMERA_PREVIEW);
      this._banner = RibbonBanner.getInstance().getStatusBanner(null, 3);
      this._banner.setTag(ThemeUtilities.TAG_CAMERA_BANNER);
      this.setBanner(this._banner);
      this._previewImage = (BitmapField)(new Object(null, 12884901888L));
      this.getMainManager().setTag(ThemeUtilities.TAG_CAMERA_PREVIEW);
      this.add(this._previewImage);
      String mediaName = null;
      Theme theme = ThemeManager.getActiveTheme();
      if (theme != null) {
         ThemeAttributeSet tas = theme.getAttributeSet(ThemeUtilities.TAG_CAMERA_PREVIEW);
         if (tas != null) {
            mediaName = tas.getLayoutName();
         }
      }

      if (mediaName == null) {
         ThemeAttributeSet tas = this.getThemeAttributeSet();
         if (tas != null) {
            mediaName = tas.getLayoutName();
         }

         if (mediaName == null) {
            mediaName = "cod://net_rim_bb_camera/previewbar.pme";
         }
      }

      this._mediaField = (MediaField)(new Object(18014398512627712L));
      this._manager = new CameraPreviewScreen$MyMediaManager(this);
      this._pmePlayer = (MediaPlayer)(new Object());
      this._pmePlayer.setUI(this._mediaField);

      label48:
      try {
         this._model = (ModelInteractorImpl)this._manager.createMedia(mediaName);
         this._pmePlayer.setMedia(this._model);
         this._pmePlayer.setInternalMediaListener(this);
         Object services = this._pmePlayer.getServices();
         if (services instanceof Object) {
            MediaServices mediaServices = (MediaServices)services;
            this._focusManager.setFocusInteractor((FocusInteractor)mediaServices.getService("FocusInteractor"));
         }

         this.setStatus(this._mediaField);
         this._filenameNode = (TextNode)this._model.getNode("filename");
         this._sendHandle = this._model.getHandle("send");
      } finally {
         break label48;
      }

      this._sendVerbs = MIMEContentVerbRepository.getVerbs("image/jpeg");
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (attached) {
         this._focusManager.resetFocus();

         label22:
         try {
            this._pmePlayer.setMediaTime(this._pmePlayer.getMediaTime() + 10000);
            this._pmePlayer.start();
         } finally {
            break label22;
         }
      } else {
         this._pmePlayer.stop();
      }

      super.onUiEngineAttached(attached);
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      int adx = Math.abs(dx);
      int ady = Math.abs(dy);
      int vx = 0;
      int vy = 0;
      if (adx > ady) {
         vx = dx;
      } else {
         vy = dy;
      }

      return this._focusManager.moveFocus(vx, vy);
   }

   @Override
   public final Menu getMenu(int instance) {
      ContextObject menuContext = (ContextObject)(new Object());
      menuContext.put(244, new Object(244387));
      SystemEnabledMenu menu = (SystemEnabledMenu)(new Object(menuContext, null));
      Menu.setTargetScreen(this);
      menu.setInstance(instance);
      this.makeMenuWithContext(menu, instance);
      return menu;
   }
}
