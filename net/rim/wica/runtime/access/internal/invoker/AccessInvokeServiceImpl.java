package net.rim.wica.runtime.access.internal.invoker;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.PhoneArguments;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.rim.RIMMessagingService;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;
import net.rim.wica.runtime.access.data.AccessDataService;
import net.rim.wica.runtime.access.internal.data.collections.EmailCollection;
import net.rim.wica.runtime.access.invoker.AccessInvokeService;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.lifecycle.LifecycleService;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.WicletRuntime;
import net.rim.wica.runtime.metadata.component.ui.ResourceCollection;
import net.rim.wica.runtime.persistence.Resource;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;

public class AccessInvokeServiceImpl implements AccessInvokeService, Serviceable {
   private ServiceProvider _provider;
   private WicletRuntime _runtime;
   private EventService _eventService;
   private LifecycleService _lifecycleService;
   private EmailCollection _emailCollection;
   static Class class$net$rim$wica$runtime$metadata$WicletRuntime;
   static Class class$net$rim$wica$runtime$access$data$AccessDataService;
   static Class class$net$rim$wica$runtime$event$EventService;
   static Class class$net$rim$wica$runtime$lifecycle$LifecycleService;

   private Resource getResource(String name) {
      Wiclet definition = this._runtime.getWiclet();
      ResourceCollection resources = definition.getResources();
      Resource resource = null;
      int resourceId = definition.getDefHandle(name);
      if (resourceId != -1) {
         String uri = resources.getResourceURI(resourceId);
         resource = definition.getContext().getWicletStore().getResource(uri);
      }

      return resource;
   }

   private boolean verifyAccessGranted() {
      if (this._runtime.getWiclet().getContext().getExternalAccessType() == 0) {
         this._eventService.dispatchEvent(this, 605, 106);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public boolean browserLoadUrl(String url) {
      if (!this.verifyAccessGranted()) {
         return false;
      }

      boolean retValue = false;
      BrowserSession session = Browser.getDefaultSession();
      if (session != null) {
         session.displayPage(url);
         retValue = true;
      }

      return retValue;
   }

   @Override
   public void play(String param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokespecial net/rim/wica/runtime/access/internal/invoker/AccessInvokeServiceImpl.verifyAccessGranted ()Z
      // 04: ifne 08
      // 07: return
      // 08: aconst_null
      // 09: astore 2
      // 0a: aload 1
      // 0b: ldc_w "http"
      // 0e: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 11: ifne 50
      // 14: aload 1
      // 15: ldc_w "https"
      // 18: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 1b: ifne 50
      // 1e: aload 0
      // 1f: aload 1
      // 20: invokespecial net/rim/wica/runtime/access/internal/invoker/AccessInvokeServiceImpl.getResource (Ljava/lang/String;)Lnet/rim/wica/runtime/persistence/Resource;
      // 23: astore 3
      // 24: aload 3
      // 25: ifnull 55
      // 28: aload 3
      // 29: invokevirtual net/rim/wica/runtime/persistence/Resource.getData ()[B
      // 2c: ifnull 45
      // 2f: new java/io/ByteArrayInputStream
      // 32: dup
      // 33: aload 3
      // 34: invokevirtual net/rim/wica/runtime/persistence/Resource.getData ()[B
      // 37: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 3a: aload 3
      // 3b: invokevirtual net/rim/wica/runtime/persistence/Resource.getContentType ()Ljava/lang/String;
      // 3e: invokestatic javax/microedition/media/Manager.createPlayer (Ljava/io/InputStream;Ljava/lang/String;)Ljavax/microedition/media/Player;
      // 41: astore 2
      // 42: goto 55
      // 45: aload 3
      // 46: invokevirtual net/rim/wica/runtime/persistence/Resource.getUri ()Ljava/lang/String;
      // 49: invokestatic javax/microedition/media/Manager.createPlayer (Ljava/lang/String;)Ljavax/microedition/media/Player;
      // 4c: astore 2
      // 4d: goto 55
      // 50: aload 1
      // 51: invokestatic javax/microedition/media/Manager.createPlayer (Ljava/lang/String;)Ljavax/microedition/media/Player;
      // 54: astore 2
      // 55: aload 2
      // 56: ifnull 63
      // 59: aload 2
      // 5a: invokeinterface javax/microedition/media/Player.start ()V 1
      // 5f: return
      // 60: astore 3
      // 61: return
      // 62: astore 3
      // 63: return
      // try (6 -> 45): 46 null
      // try (6 -> 45): 48 null
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void executeApplication(String applicationUri) {
      if (this.verifyAccessGranted()) {
         try {
            this._lifecycleService.startWiclet(applicationUri);
         } catch (Throwable var4) {
            throw new RuntimeException(e.toString());
         }
      }
   }

   @Override
   public void startCall(String number) {
      if (this.verifyAccessGranted()) {
         try {
            PhoneArguments phoneArgs = new PhoneArguments("call", number);
            if (phoneArgs != null) {
               Invoke.invokeApplication(4, phoneArgs);
               return;
            }
         } finally {
            return;
         }
      }
   }

   private EmailCollection getEmailCollection() {
      if (this._emailCollection == null) {
         WicletRuntime wr = (WicletRuntime)this._provider
            .getService(
               class$net$rim$wica$runtime$metadata$WicletRuntime == null
                  ? (class$net$rim$wica$runtime$metadata$WicletRuntime = class$("net.rim.wica.runtime.metadata.WicletRuntime"))
                  : class$net$rim$wica$runtime$metadata$WicletRuntime
            );
         AccessDataService ads = (AccessDataService)this._provider
            .getService(
               class$net$rim$wica$runtime$access$data$AccessDataService == null
                  ? (class$net$rim$wica$runtime$access$data$AccessDataService = class$("net.rim.wica.runtime.access.data.AccessDataService"))
                  : class$net$rim$wica$runtime$access$data$AccessDataService
            );
         if (ads != null && wr != null) {
            this._emailCollection = (EmailCollection)ads.getDataCollection(7, wr.getWiclet());
         }
      }

      return this._emailCollection;
   }

   @Override
   public int sendEmail(long emailID) {
      int retValue = 0;
      if (!this.verifyAccessGranted()) {
         return retValue;
      }

      EmailMessageModel emm = this.getMessageToSend(emailID);
      if (emm != null) {
         RIMMessagingService service = (RIMMessagingService)TransmissionServiceManager.get(8399767144006445082L);
         if (service != null) {
            int caps = service.getStatus();
            ServiceRecord sr = service.getOutgoingServiceRecord();
            if ((caps & 1) != 0 && sr != null) {
               EmailSendUtility.sendMessage(emm, sr, new ContextObject());
               retValue = 1;
            } else {
               this.saveMessageAsDraftInternal(emm);
               retValue = 2;
            }

            EmailCollection ec = this.getEmailCollection();
            if (ec != null) {
               ec.messageSent(emailID, emm);
            }
         }
      }

      return retValue;
   }

   private EmailMessageModel getMessageToSend(long emailID) {
      EmailCollection ec = this.getEmailCollection();
      return ec != null ? ec.getMessageToSend(emailID) : null;
   }

   @Override
   public int saveMessageAsDraft(long emailID) {
      int retValue = 0;
      if (this.verifyAccessGranted()) {
         EmailMessageModel emm = this.getMessageToSend(emailID);
         if (emm != null) {
            this.saveMessageAsDraftInternal(emm);
            retValue = 2;
         }
      }

      return retValue;
   }

   private void saveMessageAsDraftInternal(EmailMessageModel emm) {
      ContextObject co = new ContextObject();
      long folderToPutMessageIn = EmailSendUtility.assignFolderToMessage(emm, co);
      EmailSendUtility.fileOutgoingMessageInFolder(emm, folderToPutMessageIn, co);
   }

   @Override
   public void setServices(ServiceProvider provider) {
      this._provider = provider;
      this._runtime = (WicletRuntime)provider.getService(
         class$net$rim$wica$runtime$metadata$WicletRuntime == null
            ? (class$net$rim$wica$runtime$metadata$WicletRuntime = class$("net.rim.wica.runtime.metadata.WicletRuntime"))
            : class$net$rim$wica$runtime$metadata$WicletRuntime
      );
      this._eventService = (EventService)provider.getService(
         class$net$rim$wica$runtime$event$EventService == null
            ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
            : class$net$rim$wica$runtime$event$EventService
      );
      this._lifecycleService = (LifecycleService)provider.getService(
         class$net$rim$wica$runtime$lifecycle$LifecycleService == null
            ? (class$net$rim$wica$runtime$lifecycle$LifecycleService = class$("net.rim.wica.runtime.lifecycle.LifecycleService"))
            : class$net$rim$wica$runtime$lifecycle$LifecycleService
      );
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }
}
