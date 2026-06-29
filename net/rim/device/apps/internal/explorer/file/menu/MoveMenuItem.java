package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.explorer.file.FileItemField;
import net.rim.device.apps.internal.explorer.file.FileSelectionVerb;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.io.file.CopyProgressCallback;
import net.rim.device.internal.ui.component.ProgressDialog;

public final class MoveMenuItem extends MenuItem implements CopyProgressCallback {
   private FileItemField _sourceFile;
   private Integer _rootView;
   private ProgressDialog _progressDialog;
   private String _destPath;
   private boolean _copy;
   private Screen _screenToClose;
   private static final String FILE_PREFIX = "file://";
   private static String _lastMovePath;

   public MoveMenuItem(FileItemField file, Integer rootView, boolean copy, Screen screenToClose) {
      super(copy ? CommonResource.getBundle() : ExplorerResources.getResourceBundleFamily(), copy ? 3 : 37, 591108, 100);
      this._sourceFile = file;
      this._rootView = rootView;
      this._copy = copy;
      this._screenToClose = screenToClose;
   }

   @Override
   public final void run() {
      ContextObject ctx = (ContextObject)(new Object(5));
      ctx.put(424670468422402792L, this);
      if (this._rootView != null) {
         ContextObject.put(ctx, 3941043584844673548L, this._rootView);
      }

      ctx.put(-1002650280265073678L, new Object(this._sourceFile.getMediaType(), -2147476480));
      String startingPath = _lastMovePath != null ? _lastMovePath : ((StringBuffer)(new Object("file://"))).append(this._sourceFile.getPath()).toString();
      ctx.put(2765042845091913199L, startingPath);
      FileSelectionVerb selectFileVerb = new FileSelectionVerb(ctx, ExplorerResources.getResourceBundleFamily(), this._copy ? 119 : 38);
      Object filename = selectFileVerb.invoke(ctx);
      if (filename instanceof Object) {
         if (this._screenToClose != null) {
            this._screenToClose.close();
         }

         this._destPath = (String)filename;
         new MoveMenuItem$Helper(this).start();
      }
   }

   @Override
   public final void copyStarted() {
      Application.getApplication().invokeAndWait(new MoveMenuItem$1(this));
   }

   @Override
   public final void segmentCopied(long bytesCopied, long totalBytes) {
      int percentage = (int)(bytesCopied * 100 / totalBytes);
      Application.getApplication().invokeAndWait(new MoveMenuItem$2(this, percentage));
   }

   @Override
   public final void copyCompleted() {
      Application.getApplication().invokeAndWait(new MoveMenuItem$3(this));
   }

   static final String access$602(String x0) {
      _lastMovePath = x0;
      return x0;
   }
}
