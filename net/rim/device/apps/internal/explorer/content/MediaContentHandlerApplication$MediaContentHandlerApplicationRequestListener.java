package net.rim.device.apps.internal.explorer.content;

import javax.microedition.content.ContentHandlerServer;
import javax.microedition.content.RequestListener;
import javax.microedition.io.InputConnection;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.RIMGlobalMessagePoster;

final class MediaContentHandlerApplication$MediaContentHandlerApplicationRequestListener implements RequestListener {
   public MediaContentHandlerApplication$MediaContentHandlerApplicationRequestListener() {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void invocationRequestNotify(ContentHandlerServer handler) {
      MediaContentHandlerApplication.access$002(handler);
      if (MediaContentHandlerApplication._invocation != null) {
         MediaContentHandlerApplication._app.finish(6);
      }

      MediaContentHandlerApplication.access$102(MediaContentHandlerApplication._handler.getRequest(false));
      if (MediaContentHandlerApplication._invocation != null) {
         InputConnection connection = null;
         String url = MediaContentHandlerApplication._invocation.getURL();
         if (url != null) {
            label312:
            try {
               connection = (InputConnection)MediaContentHandlerApplication._invocation.open(true);
            } finally {
               break label312;
            }
         }

         if (connection == null) {
            byte[] data = MediaContentHandlerApplication._invocation.getData();
            if (data != null && data.length > 0) {
               connection = new MediaContentHandlerApplication$ByteArrayInputConnection(data);
            }
         }

         if (connection != null) {
            String type = null;
            boolean var25 = false /* VF: Semaphore variable */;

            label290:
            try {
               var25 = true;
               type = MediaContentHandlerApplication._invocation.findType();
               var25 = false;
            } finally {
               if (var25) {
                  if (url != null) {
                     type = MIMETypeAssociations.getMIMEType(url);
                  }
                  break label290;
               }
            }

            synchronized (MediaContentHandlerApplication._app.getAppEventLock()) {
               if (MediaContentHandlerApplication._renderScreen != null) {
                  MediaContentHandlerApplication._app.popScreen(MediaContentHandlerApplication._renderScreen);
                  MediaContentHandlerApplication.access$302(null);
               }

               MediaContentHandlerApplication.access$302(new RenderScreen(MediaContentHandlerApplication._app, connection, type, url));
               if (MediaContentHandlerApplication._renderScreen != null) {
                  MediaContentHandlerApplication._app.pushScreen(MediaContentHandlerApplication._renderScreen);
                  MediaContentHandlerApplication._renderScreen.finishLoadingFile();
               }

               return;
            }
         }

         int explorerHandle = CodeModuleManager.getModuleHandle("net_rim_bb_file_explorer");
         if (explorerHandle == -1) {
            MediaContentHandlerApplication._app.finish(6);
         }

         ApplicationDescriptor descriptor = null;
         boolean var19 = false /* VF: Semaphore variable */;

         label305:
         try {
            var19 = true;
            ApplicationDescriptor[] ame = CodeModuleManager.getApplicationDescriptors(explorerHandle);
            int i = ame.length - 1;

            while (true) {
               if (i < 0) {
                  var19 = false;
                  break;
               }

               descriptor = ame[i];
               String[] arguments = descriptor.getArgs();
               if (arguments == null) {
                  var19 = false;
                  break;
               }

               if (arguments.length == 0) {
                  var19 = false;
                  break;
               }

               i--;
            }
         } finally {
            if (var19) {
               MediaContentHandlerApplication._app.finish(6);
               break label305;
            }
         }

         if (descriptor != null) {
            try {
               int pid = ApplicationManager.getApplicationManager().runApplication(descriptor, false);
               RIMGlobalMessagePoster.postGlobalEvent(pid, 5632215308423875952L, 0, 0, null, null);
               MediaContentHandlerApplication._app.finish(5);
               return;
            } finally {
               MediaContentHandlerApplication._app.finish(6);
               return;
            }
         }

         MediaContentHandlerApplication._app.finish(6);
      }
   }
}
