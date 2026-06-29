package net.rim.wica.runtime.provisioning.internal;

import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.wica.runtime.comm.OutgoingRequest;
import net.rim.wica.runtime.comm.Response;
import net.rim.wica.runtime.comm.ResponseListener;
import net.rim.wica.runtime.lifecycle.Alert;
import net.rim.wica.runtime.lifecycle.Wiclet;
import net.rim.wica.runtime.lifecycle.WicletInfo;
import net.rim.wica.runtime.metadata.internal.def.ComponentDefStruct;
import net.rim.wica.runtime.metadata.internal.def.Definitions;
import net.rim.wica.runtime.metadata.internal.def.MsgDefAccess;
import net.rim.wica.runtime.persistence.CollectionSyncModel;
import net.rim.wica.runtime.persistence.Resource;
import net.rim.wica.runtime.persistence.WicletStore;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;
import net.rim.wica.runtime.provisioning.ProvisioningTaskInfo;
import net.rim.wica.runtime.provisioning.internal.digester.Digester;
import net.rim.wica.runtime.provisioning.internal.digester.DigesterRecipe;
import net.rim.wica.runtime.provisioning.internal.elements.ResourceElement;
import net.rim.wica.runtime.provisioning.internal.elements.WicletElement;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.util.Util;

class DefaultProvisioningService$ProvisioningTask implements ResponseListener, Runnable, ProvisioningTaskInfo {
   private boolean _cancelled;
   private int _language;
   private int _notificationFlags;
   private int _installType;
   private long _upgradedWicletId;
   private Response _response;
   private DefaultProvisioningService$WicletPackage _wicletPackage;
   private byte[] _packageContents;
   private boolean _persistPackage;
   private WicletInfo _info;
   private CollectionSyncModel[] _collections;
   private final DefaultProvisioningService this$0;

   public String getLanguage() {
      String[] languages = ((DeploymentDescriptor)this.getDD()).getLanguages();
      if (languages != null) {
         return this._language < 0 ? languages[0] : languages[this._language];
      } else {
         return Locale.get(1701707776).getLanguage();
      }
   }

   public DeploymentDescriptor getDD() {
      return this._info.getDescriptor();
   }

   public boolean isUpgradeMessageCompatible() {
      return (this._installType & 512) == 512;
   }

   public boolean isUpgradeDataCompatible() {
      return (this._installType & 256) == 256;
   }

   String getPackageLocation() {
      return this._info.getPackageLocation();
   }

   public boolean isFreshNewInstall() {
      return this._installType == 206;
   }

   public boolean isUpgradeInstall() {
      return !this.isFreshNewInstall();
   }

   @Override
   public String getApplicationUri() {
      return this._info.getUri();
   }

   @Override
   public long getApplicationId() {
      return this._info.getId();
   }

   @Override
   public synchronized void processResponse(Response response, OutgoingRequest request) {
      this._response = response;
      this.notify();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      if (this.this$0._lifecycleService.hasWiclet(this.getApplicationId())) {
         this.this$0.fireProvisioningEvent(this, 3, 195, "Application ID:" + this.getApplicationId() + " already installed");
      } else {
         this.this$0.fireProvisioningEvent(this, 1, 1);
         OutgoingRequest request = null;
         if (this._packageContents != null) {
            this.provisionInternal(this._packageContents);
         } else {
            String url = URIDecoder.decode(this.getPackageLocation(), "utf-8");
            boolean var19 = false /* VF: Semaphore variable */;

            try {
               var19 = true;
               request = this.this$0._commService.createOutgoingRequestInstance(url);
               var19 = false;
            } finally {
               if (var19) {
                  this.this$0.fireProvisioningEvent(this, 3, 192, "Application could not be downloaded, invalid URL: " + this.getPackageLocation());
                  return;
               }
            }

            request.setResponseListener(this);
            this.this$0._commService.sendRequest(request);
            long threadSleepTime = 1000;
            long expiryThreshold = 180000;
            long totalThreadSleepTime = 0;
            boolean expired = false;
            synchronized (this) {
               while (!this.hasResponseArrived() && !this._cancelled && !expired) {
                  label138:
                  try {
                     this.wait(threadSleepTime);
                  } finally {
                     break label138;
                  }

                  totalThreadSleepTime += threadSleepTime;
                  expired = totalThreadSleepTime >= expiryThreshold;
               }
            }

            if (this.hasResponseArrived()) {
               if (this._response.isSuccessful()) {
                  this.provisionInternal(this._response);
               } else if (this._response.getResponseCode() == 603) {
                  this.this$0.fireProvisioningEvent(this, 3, 901, RuntimeResources.getString(148));
               } else {
                  String errorMsg = RuntimeResources.getString(110, new Object[]{request.getUrl(), new Integer(this._response.getResponseCode())});
                  this.this$0.fireProvisioningEvent(this, 3, 954, errorMsg);
               }
            } else if (expired) {
               this.this$0.fireProvisioningEvent(this, 3, 193);
            } else {
               this.isCanceled();
            }
         }
      }
   }

   public DefaultProvisioningService$ProvisioningTask(
      DefaultProvisioningService this$0,
      DeploymentDescriptor descriptor,
      String packageUrl,
      long applicationId,
      int language,
      boolean ribbonVisible,
      boolean systemPrivileges,
      int notificationFlags,
      boolean persistPackage,
      byte[] packageContents,
      CollectionSyncModel[] collections
   ) {
      this.this$0 = this$0;
      this._upgradedWicletId = 0;
      this._info = new WicletInfo();
      this._info.setDescriptor(descriptor);
      this._info.setId(applicationId);
      this._info.setPackageLocation(packageUrl);
      this._info.setRibbonVisible(ribbonVisible);
      this._info.setSystemApplication(systemPrivileges);
      this._info.setState(1);
      this._collections = collections;
      this._language = language;
      this._notificationFlags = notificationFlags;
      this._packageContents = packageContents;
      this._persistPackage = persistPackage;
      this._installType = this$0._lifecycleService
         .installationType(((DeploymentDescriptor)this.getDD()).getUri(), ((DeploymentDescriptor)this.getDD()).getVersion());
      if (this.isUpgradeInstall()) {
         this._upgradedWicletId = this$0._lifecycleService.getWiclet(((DeploymentDescriptor)this.getDD()).getUri()).getId();
      }
   }

   public DefaultProvisioningService$ProvisioningTask(DefaultProvisioningService this$0) {
      this.this$0 = this$0;
      this._upgradedWicletId = 0;
   }

   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer(64);
      sb.append("[ProvisioningTask[packageURL=").append(this.getPackageLocation()).append(']');
      return sb.toString();
   }

   private WicletElement buildTree(DigesterRecipe recipe, CodeBook codebook, byte[] wicletData, DefaultProvisioningService$WicletPackage appPackage) {
      Digester d = new Digester();
      d.setCodebook(codebook);
      String language = Locale.get(1701707776).getLanguage();
      if (this._language >= 0) {
         language = ((DeploymentDescriptor)this.getDD()).getLanguages()[this._language];
      }

      d.setEntityResolver(new DefaultProvisioningService$ExternalEntityResolver(appPackage, language));
      d = recipe.prepare(d);
      d.parse(new ByteArrayInputStream(wicletData));
      return (WicletElement)d.getRoot();
   }

   private void cancel() {
      synchronized (this) {
         this._cancelled = true;
         this.notify();
      }
   }

   private boolean isCanceled() {
      if (this._cancelled) {
         this.this$0.fireProvisioningEvent(this, 4, 5);
      }

      return this._cancelled;
   }

   private void createMessageCodeMap(Wiclet installedWiclet, WicletElement newWicletElement, WicletInfo newWicletInfo) {
      MsgDefAccess msgDefs = ((Definitions)installedWiclet.getWicletStore().loadDefinitions()).getMsgDefs();
      MessageCodeMapVisitor mv = new MessageCodeMapVisitor(msgDefs);
      newWicletElement.accept(mv);
      this.this$0._persistenceService.storeMessageUpgradeMap(newWicletInfo.getId(), mv.getMessageCodeMap());
   }

   private void populateWicletInfo(WicletInfo info, WicletElement wiclet, Alert[] alerts) {
      info.setLanguageIndex(this._language);
      info.setAlerts(alerts);
      String entry = wiclet.getEntry();
      info.setEntryPoint(entry == null ? -1 : this.this$0._uniqueCodeGenerator.generateCode(entry));
      info.setIconUri(wiclet.getIcon());
      info.setHoverIcon(wiclet.getHoverIcon() != null ? wiclet.getUri() + "/" + wiclet.getHoverIcon() : null);
      String msgDelivery = wiclet.getMessageDelivery();
      int msgDeliveryMode = 0;
      if (msgDelivery.equalsIgnoreCase("bestEffort")) {
         msgDeliveryMode = 2;
      } else if (msgDelivery.equalsIgnoreCase("reliable")) {
         msgDeliveryMode = 1;
      }

      info.setMessageDelivery(msgDeliveryMode);
      info.setPersistenceMode(wiclet.getPersistence().equalsIgnoreCase("reliable"));
      info.setName(wiclet.getName());
      info.setDescription(wiclet.getDescription());
      info.setUri(wiclet.getUri());
      info.setVendor(wiclet.getVendor());
      info.setVersion(wiclet.getVersion());
   }

   private Resource[] getImmediateResources(WicletElement wiclet) {
      Hashtable resources = wiclet.getResourceElements();
      Enumeration resEnum = resources.elements();
      Vector immediateResources = new Vector();
      Resource[] ires = new Resource[0];

      while (resEnum.hasMoreElements()) {
         ResourceElement re = (ResourceElement)resEnum.nextElement();
         String resourceURL = re.getUrl();
         if (Util.isNonEmptyString(resourceURL)) {
            String language = null;
            if (this._language > -1) {
               language = ((DeploymentDescriptor)this.getDD()).getLanguages()[this._language];
            }

            Resource resource = this._wicletPackage.getImmediateResource(resourceURL, language);
            if (resource != null) {
               resource.setContentType(re.getMimeType());
               immediateResources.addElement(resource);
            }
         }
      }

      Resource scriptResource = this._wicletPackage.getScriptResource();
      if (scriptResource != null) {
         immediateResources.addElement(scriptResource);
      }

      Resource icon = this.getWicletIcon(wiclet.getIcon());
      if (icon != null) {
         immediateResources.addElement(icon);
      }

      Resource hoverIcon = this.getWicletIcon(wiclet.getHoverIcon());
      if (hoverIcon != null) {
         immediateResources.addElement(hoverIcon);
      }

      if (!immediateResources.isEmpty()) {
         ires = new Resource[immediateResources.size()];
         immediateResources.copyInto(ires);
      }

      return ires;
   }

   private Resource getWicletIcon(String iconName) {
      Resource resource = null;
      if (Util.isNonEmptyString(iconName)) {
         String language = null;
         if (this._language > -1) {
            language = ((DeploymentDescriptor)this.getDD()).getLanguages()[this._language];
         }

         resource = this._wicletPackage.getImmediateResource(iconName, language);
      }

      return resource;
   }

   private boolean hasResponseArrived() {
      return this._response != null;
   }

   private void provisionInternal(Response response) {
      this.provisionInternal(response.getData());
   }

   private void provisionInternal(byte[] param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: arraylength
      // 002: bipush 3
      // 004: imul
      // 005: invokestatic net/rim/wica/runtime/util/Util.ensureAvailableFlash (I)Z
      // 008: ifne 01f
      // 00b: aload 0
      // 00c: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 00f: aload 0
      // 010: bipush 3
      // 012: sipush 901
      // 015: sipush 148
      // 018: invokestatic net/rim/wica/runtime/resources/RuntimeResources.getString (I)Ljava/lang/String;
      // 01b: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.fireProvisioningEvent (Lnet/rim/wica/runtime/provisioning/ProvisioningTaskInfo;IILjava/lang/String;)V
      // 01e: return
      // 01f: aload 0
      // 020: aload 0
      // 021: aload 1
      // 022: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.unpackZip ([B)Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$WicletPackage;
      // 025: putfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask._wicletPackage Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$WicletPackage;
      // 028: aload 0
      // 029: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask._wicletPackage Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$WicletPackage;
      // 02c: ifnull 036
      // 02f: aload 0
      // 030: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.isCanceled ()Z
      // 033: ifeq 043
      // 036: aload 0
      // 037: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 03a: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.getUniqueCodeGenerator ()Lnet/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator;
      // 03d: astore 2
      // 03e: aload 2
      // 03f: invokevirtual net/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator.clear ()V
      // 042: return
      // 043: aload 0
      // 044: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 047: aload 0
      // 048: bipush 4
      // 04a: bipush 2
      // 04c: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.fireProvisioningEvent (Lnet/rim/wica/runtime/provisioning/ProvisioningTaskInfo;II)V
      // 04f: aload 0
      // 050: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.buildCustomDOMTree ()Lnet/rim/wica/runtime/provisioning/internal/elements/WicletElement;
      // 053: astore 2
      // 054: aload 2
      // 055: ifnull 05f
      // 058: aload 0
      // 059: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.isCanceled ()Z
      // 05c: ifeq 06c
      // 05f: aload 0
      // 060: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 063: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.getUniqueCodeGenerator ()Lnet/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator;
      // 066: astore 3
      // 067: aload 3
      // 068: invokevirtual net/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator.clear ()V
      // 06b: return
      // 06c: aload 2
      // 06d: aload 0
      // 06e: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 071: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService._uniqueCodeGenerator Lnet/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator;
      // 074: invokevirtual net/rim/wica/runtime/provisioning/internal/elements/WicletElement.attachStandardComponentResolver (Lnet/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator;)V
      // 077: new net/rim/wica/runtime/provisioning/internal/DataVisitor
      // 07a: dup
      // 07b: aload 0
      // 07c: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 07f: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService._uniqueCodeGenerator Lnet/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator;
      // 082: invokespecial net/rim/wica/runtime/provisioning/internal/DataVisitor.<init> (Lnet/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator;)V
      // 085: astore 3
      // 086: aload 2
      // 087: aload 3
      // 088: invokevirtual net/rim/wica/runtime/provisioning/internal/elements/WicletElement.accept (Lnet/rim/wica/runtime/provisioning/internal/DefinitionVisitor;)V
      // 08b: aload 3
      // 08c: invokevirtual net/rim/wica/runtime/provisioning/internal/DataVisitor.getGlobalDefId ()I
      // 08f: istore 4
      // 091: new net/rim/wica/runtime/provisioning/internal/MessageVisitor
      // 094: dup
      // 095: aload 0
      // 096: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 099: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService._uniqueCodeGenerator Lnet/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator;
      // 09c: iload 4
      // 09e: aload 2
      // 09f: invokespecial net/rim/wica/runtime/provisioning/internal/MessageVisitor.<init> (Lnet/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator;ILnet/rim/wica/runtime/provisioning/internal/elements/WicletElement;)V
      // 0a2: astore 5
      // 0a4: aload 2
      // 0a5: aload 5
      // 0a7: invokevirtual net/rim/wica/runtime/provisioning/internal/elements/WicletElement.accept (Lnet/rim/wica/runtime/provisioning/internal/DefinitionVisitor;)V
      // 0aa: new net/rim/wica/runtime/provisioning/internal/ScriptVisitor
      // 0ad: dup
      // 0ae: aload 0
      // 0af: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 0b2: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService._uniqueCodeGenerator Lnet/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator;
      // 0b5: invokespecial net/rim/wica/runtime/provisioning/internal/ScriptVisitor.<init> (Lnet/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator;)V
      // 0b8: astore 6
      // 0ba: aload 2
      // 0bb: aload 6
      // 0bd: invokevirtual net/rim/wica/runtime/provisioning/internal/elements/WicletElement.accept (Lnet/rim/wica/runtime/provisioning/internal/DefinitionVisitor;)V
      // 0c0: new net/rim/wica/runtime/provisioning/internal/ScreenVisitor
      // 0c3: dup
      // 0c4: aload 0
      // 0c5: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 0c8: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService._uniqueCodeGenerator Lnet/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator;
      // 0cb: iload 4
      // 0cd: aload 2
      // 0ce: invokespecial net/rim/wica/runtime/provisioning/internal/ScreenVisitor.<init> (Lnet/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator;ILnet/rim/wica/runtime/provisioning/internal/elements/WicletElement;)V
      // 0d1: astore 7
      // 0d3: aload 2
      // 0d4: aload 7
      // 0d6: invokevirtual net/rim/wica/runtime/provisioning/internal/elements/WicletElement.accept (Lnet/rim/wica/runtime/provisioning/internal/DefinitionVisitor;)V
      // 0d9: aload 0
      // 0da: aload 0
      // 0db: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask._info Lnet/rim/wica/runtime/lifecycle/WicletInfo;
      // 0de: aload 2
      // 0df: aload 5
      // 0e1: invokevirtual net/rim/wica/runtime/provisioning/internal/MessageVisitor.getAlerts ()[Lnet/rim/wica/runtime/lifecycle/Alert;
      // 0e4: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.populateWicletInfo (Lnet/rim/wica/runtime/lifecycle/WicletInfo;Lnet/rim/wica/runtime/provisioning/internal/elements/WicletElement;[Lnet/rim/wica/runtime/lifecycle/Alert;)V
      // 0e7: aload 0
      // 0e8: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.isCanceled ()Z
      // 0eb: ifeq 0fd
      // 0ee: aload 0
      // 0ef: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 0f2: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.getUniqueCodeGenerator ()Lnet/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator;
      // 0f5: astore 8
      // 0f7: aload 8
      // 0f9: invokevirtual net/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator.clear ()V
      // 0fc: return
      // 0fd: aload 0
      // 0fe: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 101: aload 0
      // 102: bipush 4
      // 104: bipush 3
      // 106: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.fireProvisioningEvent (Lnet/rim/wica/runtime/provisioning/ProvisioningTaskInfo;II)V
      // 109: aload 0
      // 10a: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 10d: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService._persistenceService Lnet/rim/wica/runtime/persistence/PersistenceService;
      // 110: aload 0
      // 111: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask._info Lnet/rim/wica/runtime/lifecycle/WicletInfo;
      // 114: invokevirtual net/rim/wica/runtime/lifecycle/WicletInfo.getId ()J
      // 117: invokeinterface net/rim/wica/runtime/persistence/PersistenceService.createApplication (J)Lnet/rim/wica/runtime/persistence/WicletStore; 3
      // 11c: astore 8
      // 11e: aload 0
      // 11f: aload 8
      // 121: aload 2
      // 122: aload 3
      // 123: invokevirtual net/rim/wica/runtime/provisioning/internal/DataVisitor.getDataDefs ()Lnet/rim/wica/runtime/metadata/internal/def/ComponentDefStruct;
      // 126: aload 5
      // 128: invokevirtual net/rim/wica/runtime/provisioning/internal/MessageVisitor.getMessageDefs ()Lnet/rim/wica/runtime/metadata/internal/def/ComponentDefStruct;
      // 12b: aload 6
      // 12d: invokevirtual net/rim/wica/runtime/provisioning/internal/ScriptVisitor.getScriptDefs ()Lnet/rim/wica/runtime/metadata/internal/def/ComponentDefStruct;
      // 130: aload 7
      // 132: invokevirtual net/rim/wica/runtime/provisioning/internal/ScreenVisitor.getScreenDefs ()Lnet/rim/wica/runtime/metadata/internal/def/ComponentDefStruct;
      // 135: iload 4
      // 137: aload 0
      // 138: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask._info Lnet/rim/wica/runtime/lifecycle/WicletInfo;
      // 13b: aload 0
      // 13c: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask._persistPackage Z
      // 13f: ifeq 146
      // 142: aload 1
      // 143: goto 147
      // 146: aconst_null
      // 147: aload 0
      // 148: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask._collections [Lnet/rim/wica/runtime/persistence/CollectionSyncModel;
      // 14b: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.persistApplicationDefinition (Lnet/rim/wica/runtime/persistence/WicletStore;Lnet/rim/wica/runtime/provisioning/internal/elements/WicletElement;Lnet/rim/wica/runtime/metadata/internal/def/ComponentDefStruct;Lnet/rim/wica/runtime/metadata/internal/def/ComponentDefStruct;Lnet/rim/wica/runtime/metadata/internal/def/ComponentDefStruct;Lnet/rim/wica/runtime/metadata/internal/def/ComponentDefStruct;ILnet/rim/wica/runtime/lifecycle/WicletInfo;[B[Lnet/rim/wica/runtime/persistence/CollectionSyncModel;)V
      // 14e: aload 0
      // 14f: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.isUpgradeInstall ()Z
      // 152: ifeq 195
      // 155: aload 0
      // 156: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 159: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService._lifecycleService Lnet/rim/wica/runtime/lifecycle/LifecycleService;
      // 15c: aload 0
      // 15d: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.getDD ()Ljava/lang/Object;
      // 160: invokevirtual net/rim/wica/runtime/provisioning/DeploymentDescriptor.getUri ()Ljava/lang/String;
      // 163: invokeinterface net/rim/wica/runtime/lifecycle/LifecycleService.getWiclet (Ljava/lang/String;)Lnet/rim/wica/runtime/lifecycle/Wiclet; 2
      // 168: astore 9
      // 16a: aload 0
      // 16b: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.isUpgradeDataCompatible ()Z
      // 16e: ifeq 183
      // 171: aload 9
      // 173: invokeinterface net/rim/wica/runtime/metadata/WicletContext.getWicletStore ()Lnet/rim/wica/runtime/persistence/WicletStore; 1
      // 178: astore 10
      // 17a: aload 8
      // 17c: aload 10
      // 17e: invokeinterface net/rim/wica/runtime/persistence/WicletStore.copyData (Lnet/rim/wica/runtime/persistence/WicletStore;)V 2
      // 183: aload 0
      // 184: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.isUpgradeMessageCompatible ()Z
      // 187: ifeq 195
      // 18a: aload 0
      // 18b: aload 9
      // 18d: aload 2
      // 18e: aload 0
      // 18f: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask._info Lnet/rim/wica/runtime/lifecycle/WicletInfo;
      // 192: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.createMessageCodeMap (Lnet/rim/wica/runtime/lifecycle/Wiclet;Lnet/rim/wica/runtime/provisioning/internal/elements/WicletElement;Lnet/rim/wica/runtime/lifecycle/WicletInfo;)V
      // 195: aload 0
      // 196: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 199: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService._lifecycleService Lnet/rim/wica/runtime/lifecycle/LifecycleService;
      // 19c: aload 8
      // 19e: invokeinterface net/rim/wica/runtime/lifecycle/LifecycleService.installApplication (Lnet/rim/wica/runtime/persistence/WicletStore;)Lnet/rim/wica/runtime/lifecycle/Wiclet; 2
      // 1a3: pop
      // 1a4: goto 1b5
      // 1a7: astore 9
      // 1a9: aload 0
      // 1aa: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 1ad: ldc_w "ProvisioningService: Failed to store application during install."
      // 1b0: aload 9
      // 1b2: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.logException (Ljava/lang/String;Ljava/lang/Throwable;)V
      // 1b5: aload 0
      // 1b6: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.isFreshNewInstall ()Z
      // 1b9: ifeq 1cb
      // 1bc: aload 0
      // 1bd: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 1c0: aload 0
      // 1c1: bipush 2
      // 1c3: bipush 4
      // 1c5: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.fireProvisioningEvent (Lnet/rim/wica/runtime/provisioning/ProvisioningTaskInfo;II)V
      // 1c8: goto 1e4
      // 1cb: aload 0
      // 1cc: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 1cf: aload 0
      // 1d0: new java/lang/Long
      // 1d3: dup
      // 1d4: aload 0
      // 1d5: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask._upgradedWicletId J
      // 1d8: invokespecial java/lang/Long.<init> (J)V
      // 1db: bipush 5
      // 1dd: bipush 4
      // 1df: aconst_null
      // 1e0: aconst_null
      // 1e1: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.fireProvisioningEvent (Lnet/rim/wica/runtime/provisioning/ProvisioningTaskInfo;Ljava/lang/Object;IILjava/lang/String;Ljava/lang/Throwable;)V
      // 1e4: aload 0
      // 1e5: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 1e8: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.getUniqueCodeGenerator ()Lnet/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator;
      // 1eb: astore 2
      // 1ec: aload 2
      // 1ed: invokevirtual net/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator.clear ()V
      // 1f0: return
      // 1f1: astore 2
      // 1f2: aload 0
      // 1f3: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 1f6: aload 0
      // 1f7: bipush 3
      // 1f9: sipush 194
      // 1fc: aload 2
      // 1fd: invokevirtual net/rim/wica/runtime/provisioning/internal/ProvisioningEncodingException.getMessage ()Ljava/lang/String;
      // 200: aload 2
      // 201: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.fireProvisioningEvent (Lnet/rim/wica/runtime/provisioning/ProvisioningTaskInfo;IILjava/lang/String;Ljava/lang/Throwable;)V
      // 204: aload 0
      // 205: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 208: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.getUniqueCodeGenerator ()Lnet/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator;
      // 20b: astore 2
      // 20c: aload 2
      // 20d: invokevirtual net/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator.clear ()V
      // 210: return
      // 211: astore 2
      // 212: aload 0
      // 213: aconst_null
      // 214: putfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask._wicletPackage Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$WicletPackage;
      // 217: invokestatic net/rim/vm/Memory.emergencyGC ()V
      // 21a: aload 0
      // 21b: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 21e: aload 0
      // 21f: bipush 3
      // 221: sipush 901
      // 224: sipush 148
      // 227: invokestatic net/rim/wica/runtime/resources/RuntimeResources.getString (I)Ljava/lang/String;
      // 22a: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.fireProvisioningEvent (Lnet/rim/wica/runtime/provisioning/ProvisioningTaskInfo;IILjava/lang/String;)V
      // 22d: aload 0
      // 22e: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 231: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.getUniqueCodeGenerator ()Lnet/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator;
      // 234: astore 2
      // 235: aload 2
      // 236: invokevirtual net/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator.clear ()V
      // 239: return
      // 23a: astore 2
      // 23b: aload 0
      // 23c: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 23f: aload 0
      // 240: bipush 3
      // 242: sipush 190
      // 245: ldc_w "Provisioning task failed unexpectedly"
      // 248: aload 2
      // 249: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.fireProvisioningEvent (Lnet/rim/wica/runtime/provisioning/ProvisioningTaskInfo;IILjava/lang/String;Ljava/lang/Throwable;)V
      // 24c: aload 0
      // 24d: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 250: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.getUniqueCodeGenerator ()Lnet/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator;
      // 253: astore 2
      // 254: aload 2
      // 255: invokevirtual net/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator.clear ()V
      // 258: return
      // 259: astore 11
      // 25b: aload 0
      // 25c: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 25f: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.getUniqueCodeGenerator ()Lnet/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator;
      // 262: astore 12
      // 264: aload 12
      // 266: invokevirtual net/rim/wica/runtime/provisioning/internal/UniqueCodeGenerator.clear ()V
      // 269: aload 11
      // 26b: athrow
      // try (189 -> 195): 196 net/rim/wica/runtime/lifecycle/LifecycleException
      // try (15 -> 26): 232 net/rim/wica/runtime/provisioning/internal/ProvisioningEncodingException
      // try (33 -> 47): 232 net/rim/wica/runtime/provisioning/internal/ProvisioningEncodingException
      // try (54 -> 116): 232 net/rim/wica/runtime/provisioning/internal/ProvisioningEncodingException
      // try (123 -> 225): 232 net/rim/wica/runtime/provisioning/internal/ProvisioningEncodingException
      // try (15 -> 26): 249 null
      // try (33 -> 47): 249 null
      // try (54 -> 116): 249 null
      // try (123 -> 225): 249 null
      // try (15 -> 26): 269 null
      // try (33 -> 47): 269 null
      // try (54 -> 116): 269 null
      // try (123 -> 225): 269 null
      // try (15 -> 26): 285 null
      // try (33 -> 47): 285 null
      // try (54 -> 116): 285 null
      // try (123 -> 225): 285 null
      // try (232 -> 242): 285 null
      // try (249 -> 262): 285 null
      // try (269 -> 278): 285 null
      // try (285 -> 286): 285 null
   }

   private DefaultProvisioningService$WicletPackage unpackZip(byte[] param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 2
      // 02: new net/rim/wica/runtime/util/zip/ZipFile
      // 05: dup
      // 06: aload 0
      // 07: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.getPackageLocation ()Ljava/lang/String;
      // 0a: aload 1
      // 0b: invokespecial net/rim/wica/runtime/util/zip/ZipFile.<init> (Ljava/lang/String;[B)V
      // 0e: astore 3
      // 0f: new net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$WicletPackage
      // 12: dup
      // 13: aload 3
      // 14: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$WicletPackage.<init> (Lnet/rim/wica/runtime/util/zip/ZipFile;)V
      // 17: astore 2
      // 18: aload 2
      // 19: areturn
      // 1a: astore 3
      // 1b: new java/lang/StringBuffer
      // 1e: dup
      // 1f: ldc_w "Invalid or damaged application package: "
      // 22: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 25: aload 0
      // 26: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.getPackageLocation ()Ljava/lang/String;
      // 29: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2c: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 2f: astore 4
      // 31: aload 0
      // 32: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 35: aload 0
      // 36: bipush 3
      // 38: sipush 955
      // 3b: aload 4
      // 3d: aload 3
      // 3e: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.fireProvisioningEvent (Lnet/rim/wica/runtime/provisioning/ProvisioningTaskInfo;IILjava/lang/String;Ljava/lang/Throwable;)V
      // 41: aload 2
      // 42: areturn
      // 43: astore 3
      // 44: new java/lang/StringBuffer
      // 47: dup
      // 48: ldc_w "Unable to compile script code from application package: "
      // 4b: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 4e: aload 0
      // 4f: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.getPackageLocation ()Ljava/lang/String;
      // 52: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 55: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 58: astore 4
      // 5a: aload 0
      // 5b: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 5e: aload 0
      // 5f: bipush 3
      // 61: sipush 955
      // 64: aload 4
      // 66: aload 3
      // 67: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.fireProvisioningEvent (Lnet/rim/wica/runtime/provisioning/ProvisioningTaskInfo;IILjava/lang/String;Ljava/lang/Throwable;)V
      // 6a: aload 2
      // 6b: areturn
      // 6c: astore 3
      // 6d: aload 0
      // 6e: aconst_null
      // 6f: putfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask._wicletPackage Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$WicletPackage;
      // 72: invokestatic net/rim/vm/Memory.emergencyGC ()V
      // 75: aload 0
      // 76: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 79: aload 0
      // 7a: bipush 3
      // 7c: sipush 901
      // 7f: sipush 147
      // 82: invokestatic net/rim/wica/runtime/resources/RuntimeResources.getString (I)Ljava/lang/String;
      // 85: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.fireProvisioningEvent (Lnet/rim/wica/runtime/provisioning/ProvisioningTaskInfo;IILjava/lang/String;)V
      // 88: aload 2
      // 89: areturn
      // try (2 -> 14): 16 null
      // try (2 -> 14): 36 net/rim/wica/runtime/script/CompilerException
      // try (2 -> 14): 56 null
   }

   private WicletElement buildCustomDOMTree() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 1
      // 02: aload 0
      // 03: new net/rim/wica/runtime/provisioning/internal/digester/WicaDigesterRecipe
      // 06: dup
      // 07: invokespecial net/rim/wica/runtime/provisioning/internal/digester/WicaDigesterRecipe.<init> ()V
      // 0a: aload 0
      // 0b: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask._wicletPackage Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$WicletPackage;
      // 0e: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$WicletPackage.hasBinaryWicletDefinition ()Z
      // 11: ifeq 1e
      // 14: new net/rim/wica/runtime/provisioning/internal/CodeBook
      // 17: dup
      // 18: invokespecial net/rim/wica/runtime/provisioning/internal/CodeBook.<init> ()V
      // 1b: goto 1f
      // 1e: aconst_null
      // 1f: aload 0
      // 20: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask._wicletPackage Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$WicletPackage;
      // 23: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$WicletPackage.getWicletDefinition ()[B
      // 26: aload 0
      // 27: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask._wicletPackage Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$WicletPackage;
      // 2a: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.buildTree (Lnet/rim/wica/runtime/provisioning/internal/digester/DigesterRecipe;Lnet/rim/wica/runtime/provisioning/internal/CodeBook;[BLnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$WicletPackage;)Lnet/rim/wica/runtime/provisioning/internal/elements/WicletElement;
      // 2d: astore 1
      // 2e: aload 1
      // 2f: areturn
      // 30: astore 2
      // 31: aload 0
      // 32: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 35: aload 0
      // 36: bipush 3
      // 38: sipush 955
      // 3b: ldc_w "I/O exception while reading application: "
      // 3e: aload 2
      // 3f: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.fireProvisioningEvent (Lnet/rim/wica/runtime/provisioning/ProvisioningTaskInfo;IILjava/lang/String;Ljava/lang/Throwable;)V
      // 42: aload 1
      // 43: areturn
      // 44: astore 2
      // 45: aload 0
      // 46: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 49: aload 0
      // 4a: bipush 3
      // 4c: sipush 955
      // 4f: ldc_w "Parser exception while constructing application definition: "
      // 52: aload 2
      // 53: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.fireProvisioningEvent (Lnet/rim/wica/runtime/provisioning/ProvisioningTaskInfo;IILjava/lang/String;Ljava/lang/Throwable;)V
      // 56: aload 1
      // 57: areturn
      // 58: astore 2
      // 59: aload 0
      // 5a: aconst_null
      // 5b: putfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask._wicletPackage Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$WicletPackage;
      // 5e: aconst_null
      // 5f: astore 1
      // 60: invokestatic net/rim/vm/Memory.emergencyGC ()V
      // 63: aload 0
      // 64: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 67: aload 0
      // 68: bipush 3
      // 6a: sipush 901
      // 6d: sipush 148
      // 70: invokestatic net/rim/wica/runtime/resources/RuntimeResources.getString (I)Ljava/lang/String;
      // 73: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.fireProvisioningEvent (Lnet/rim/wica/runtime/provisioning/ProvisioningTaskInfo;IILjava/lang/String;)V
      // 76: aload 1
      // 77: areturn
      // try (2 -> 22): 24 null
      // try (2 -> 22): 35 null
      // try (2 -> 22): 46 null
   }

   private void persistApplicationDefinition(
      WicletStore store,
      WicletElement wiclet,
      ComponentDefStruct datas,
      ComponentDefStruct messages,
      ComponentDefStruct scripts,
      ComponentDefStruct screens,
      int globalDef,
      WicletInfo info,
      byte[] applicationPackage,
      CollectionSyncModel[] collections
   ) {
      store.storeApplication(
         info,
         this.this$0._uniqueCodeGenerator.getMappingTable(),
         this.this$0._uniqueCodeGenerator.getCollisionCodes(),
         globalDef,
         datas,
         messages,
         scripts,
         screens,
         this.getImmediateResources(wiclet),
         applicationPackage
      );
      if (collections != null) {
         for (int i = collections.length - 1; i >= 0; i--) {
            store.storeData(collections[i].getDefId(), collections[i].getData());
         }
      }
   }

   private boolean shouldAGBeNotified() {
      return (this._notificationFlags & 1) == 1;
   }

   private boolean shouldProvWicletBeNotified() {
      return (this._notificationFlags & 2) == 2;
   }
}
