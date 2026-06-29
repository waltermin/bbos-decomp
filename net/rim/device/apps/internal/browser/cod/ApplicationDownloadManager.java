package net.rim.device.apps.internal.browser.cod;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.DigestFactory;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.util.IntVector;
import net.rim.device.apps.api.browser.BrowserServices;
import net.rim.device.apps.api.browser.OTAStatusReportService;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.RenderingSessionImpl;
import net.rim.device.apps.internal.browser.download.DownloadManager;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.QuincyUtil;
import net.rim.device.cldc.io.utility.URL;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.content.ContentHandlerRegistrationHelper;
import net.rim.device.internal.rms.RecordStoreManagerProxy;
import net.rim.device.internal.system.CodeStore;
import net.rim.vm.Memory;

final class ApplicationDownloadManager extends DownloadManager {
   private JADAttributeParser _jad;
   private ApplicationDownloadListener _listener;
   private int _otaStatus = 906;
   private String _otaError;
   private IntVector _loadedModules;
   private CodeModuleGroup _moduleGroup;
   private Hashtable _upgradeAndDeleteGroups;
   private InputStream _jarStream;
   private BrowserConfigRecord _browserConfigRecord;
   private ApplicationPermissions _applicationPermissions;
   private boolean _allowUnsigned;
   static final int LMM_ADDITIONAL_MEMORY_CONSTANT_FACTOR = 131072;
   private static final String MODULE_UPGRADE_FLAG = "upgrade";
   private static final String MODULE_DELETE_FLAG = "delete";

   public ApplicationDownloadManager(
      RenderingApplication app, ApplicationDownloadListener listener, JADAttributeParser jad, InputStream jarStream, String configUid
   ) {
      super(app);
      this._listener = listener;
      this._jad = jad;
      this._jarStream = jarStream;
      this._upgradeAndDeleteGroups = (Hashtable)(new Object());
      if (configUid != null) {
         this._browserConfigRecord = BrowserConfigRecord.getDecodedConfig(configUid, -1, null);
      }
   }

   public final void setApplicationDownloadPermissions(ApplicationPermissions permissions) {
      this._applicationPermissions = permissions;
   }

   public final ApplicationPermissions getApplicationDownloadPermissions() {
      return this._applicationPermissions;
   }

   public final IntVector getLoadedModules() {
      return this._loadedModules;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      boolean rebootRequired = false;
      boolean var209 = false /* VF: Semaphore variable */;

      label2374: {
         label2375: {
            label2376: {
               label2377: {
                  try {
                     try {
                        var209 = true;
                        this._loadedModules = (IntVector)(new Object());
                        String t = this._jad.get("MIDlet-Name");
                        String midletVendor = this._jad.get("MIDlet-Vendor");
                        String moduleGroupName = this.getModuleGroupName((String)t, (String)midletVendor);
                        String midletVersion = this._jad.get("MIDlet-Version");
                        CodeModuleGroup existingModuleGroup = CodeModuleGroupManager.load(moduleGroupName);
                        if (existingModuleGroup == null && t != null && midletVendor != null) {
                           CodeModuleGroup[] moduleGroups = CodeModuleGroupManager.loadAll();
                           if (moduleGroups != null) {
                              for (int i = moduleGroups.length - 1; i >= 0; i--) {
                                 if (((String)t).equals(moduleGroups[i].getFriendlyName()) && ((String)midletVendor).equals(moduleGroups[i].getVendor())) {
                                    existingModuleGroup = moduleGroups[i];
                                    moduleGroupName = existingModuleGroup.getName();
                                    break;
                                 }
                              }
                           }
                        }

                        this._moduleGroup = (CodeModuleGroup)(new Object(moduleGroupName));
                        this._moduleGroup.setFriendlyName((String)t);
                        this._moduleGroup.setVendor((String)midletVendor);
                        this._moduleGroup.setVersion((String)midletVersion);
                        String description = (String)this._jad.get("MIDlet-Description");
                        this._moduleGroup.setDescription(description);
                        boolean upgrade = false;
                        if (existingModuleGroup != null) {
                           if (!allowUpgrade(existingModuleGroup)) {
                              this._otaStatus = 910;
                              var209 = false;
                              break label2375;
                           }

                           boolean existingMIDletSigned = false;
                           Enumeration e = existingModuleGroup.getModules();

                           while (e.hasMoreElements()) {
                              int moduleHandle = CodeModuleManager.getModuleHandle((String)e.nextElement());
                              if (moduleHandle != 0 && CodeModuleManager.getModuleTrailer(moduleHandle, 2, 0) != null) {
                                 existingMIDletSigned = true;
                                 break;
                              }
                           }

                           if (existingMIDletSigned && !this._jad.isSigned()) {
                              this._otaStatus = 910;
                              var209 = false;
                              break label2376;
                           }

                           String existingVersion = existingModuleGroup.getVersion();
                           upgrade = this._listener.performUpgrade(existingVersion, (String)midletVersion);
                           if (!upgrade) {
                              this._otaStatus = 902;
                              this._otaError = null;
                              var209 = false;
                              break label2377;
                           }

                           this._upgradeAndDeleteGroups.put(existingModuleGroup, "upgrade");
                        }

                        this._allowUnsigned = false;
                        int transactionHandle = CodeModuleManager.beginTransaction();
                        if (this._jarStream != null) {
                           byte[] signingCert = this.requestJAR(transactionHandle, upgrade);
                           if (signingCert != null) {
                              this.addCertInformationToGroup(signingCert);
                           }
                        } else if ((this._otaStatus = this._jad.validate(upgrade)) == 900) {
                           if (this._jad.getCodUrlCount() <= 0) {
                              byte[] signingCert = this.requestJAR(transactionHandle, upgrade);
                              if (signingCert != null) {
                                 this.addCertInformationToGroup(signingCert);
                              }
                           } else {
                              this.requestCODs(transactionHandle, upgrade);
                           }
                        }

                        if (this.isAborted()) {
                           this._otaStatus = 902;
                           var209 = false;
                        } else if (this._otaStatus != 900) {
                           var209 = false;
                        } else {
                           Memory.maximizeContiguousRAM();
                           int transactionStatus = CodeModuleManager.endTransaction(transactionHandle);
                           if (transactionStatus == 15) {
                              rebootRequired = true;
                           } else if (transactionStatus == 13) {
                              this._otaStatus = 907;
                           }

                           for (int i = 0; i < this._loadedModules.size(); i++) {
                              String moduleName = CodeModuleManager.getModuleName(this._loadedModules.elementAt(i));
                              this._moduleGroup.addModule(moduleName);
                              if (upgrade && existingModuleGroup.containsModule(moduleName)) {
                                 ContentHandlerRegistrationHelper.getInstance().moduleUpgraded(moduleName, this._loadedModules.elementAt(i));
                              }
                           }

                           if (upgrade && !this.allowMIDletRecordStoreUpgrade(existingModuleGroup) && this._listener.removeRecordStores()) {
                              ((RecordStoreManagerProxy)ApplicationRegistry.getApplicationRegistry()
                                 .waitFor(6635119920104263588L))
                                 .deleteRecordStores((String)t, (String)midletVendor);
                           }

                           Enumeration groups = this._upgradeAndDeleteGroups.keys();

                           while (groups.hasMoreElements()) {
                              CodeModuleGroup currGroup = (CodeModuleGroup)groups.nextElement();
                              Enumeration e = currGroup.getModules();

                              while (e.hasMoreElements()) {
                                 String oldModuleName = (String)e.nextElement();
                                 if (!this._moduleGroup.containsModule(oldModuleName)) {
                                    int handle = CodeModuleManager.getModuleHandle(oldModuleName);
                                    if (handle != 0 && CodeModuleManager.deleteModuleEx(handle, true) == 6) {
                                       rebootRequired = true;
                                    }
                                 }
                              }

                              if (this._upgradeAndDeleteGroups.get(currGroup).equals("delete")) {
                                 currGroup.delete();
                              }
                           }

                           this.storeJadAttributes();
                           this._moduleGroup.store();
                           rebootRequired |= this.setPermissions();
                           var209 = false;
                        }
                     } catch (Throwable var236) {
                        QuincyUtil.sendQuincy(t, false);
                        this.setError(t);
                        var209 = false;
                        break label2374;
                     }
                  } finally {
                     if (var209) {
                        boolean var180 = false /* VF: Semaphore variable */;

                        label2262: {
                           label2261: {
                              try {
                                 label2259:
                                 try {
                                    var180 = true;
                                    this.sendStatusReport();
                                    var180 = false;
                                    break label2261;
                                 } catch (Throwable var224) {
                                    QuincyUtil.sendQuincy(t, false);
                                    var180 = false;
                                    break label2259;
                                 }
                              } finally {
                                 if (var180) {
                                    this._listener.finishedDownload(this._otaStatus, this._otaError, rebootRequired);
                                 }
                              }

                              this._listener.finishedDownload(this._otaStatus, this._otaError, rebootRequired);
                              break label2262;
                           }

                           this._listener.finishedDownload(this._otaStatus, this._otaError, rebootRequired);
                        }
                     }
                  }

                  boolean var151 = false /* VF: Semaphore variable */;

                  label2297: {
                     try {
                        label2295:
                        try {
                           var151 = true;
                           this.sendStatusReport();
                           var151 = false;
                           break label2297;
                        } catch (Throwable var234) {
                           QuincyUtil.sendQuincy(t, false);
                           var151 = false;
                           break label2295;
                        }
                     } finally {
                        if (var151) {
                           this._listener.finishedDownload(this._otaStatus, this._otaError, rebootRequired);
                        }
                     }

                     this._listener.finishedDownload(this._otaStatus, this._otaError, rebootRequired);
                     return;
                  }

                  this._listener.finishedDownload(this._otaStatus, this._otaError, rebootRequired);
                  return;
               }

               boolean var122 = false /* VF: Semaphore variable */;

               label2290: {
                  try {
                     label2288:
                     try {
                        var122 = true;
                        this.sendStatusReport();
                        var122 = false;
                        break label2290;
                     } catch (Throwable var232) {
                        QuincyUtil.sendQuincy(t, false);
                        var122 = false;
                        break label2288;
                     }
                  } finally {
                     if (var122) {
                        this._listener.finishedDownload(this._otaStatus, this._otaError, rebootRequired);
                     }
                  }

                  this._listener.finishedDownload(this._otaStatus, this._otaError, rebootRequired);
                  return;
               }

               this._listener.finishedDownload(this._otaStatus, this._otaError, rebootRequired);
               return;
            }

            boolean var93 = false /* VF: Semaphore variable */;

            label2283: {
               try {
                  label2281:
                  try {
                     var93 = true;
                     this.sendStatusReport();
                     var93 = false;
                     break label2283;
                  } catch (Throwable var230) {
                     QuincyUtil.sendQuincy(t, false);
                     var93 = false;
                     break label2281;
                  }
               } finally {
                  if (var93) {
                     this._listener.finishedDownload(this._otaStatus, this._otaError, rebootRequired);
                  }
               }

               this._listener.finishedDownload(this._otaStatus, this._otaError, rebootRequired);
               return;
            }

            this._listener.finishedDownload(this._otaStatus, this._otaError, rebootRequired);
            return;
         }

         boolean var64 = false /* VF: Semaphore variable */;

         label2276: {
            try {
               label2274:
               try {
                  var64 = true;
                  this.sendStatusReport();
                  var64 = false;
                  break label2276;
               } catch (Throwable var228) {
                  QuincyUtil.sendQuincy(t, false);
                  var64 = false;
                  break label2274;
               }
            } finally {
               if (var64) {
                  this._listener.finishedDownload(this._otaStatus, this._otaError, rebootRequired);
               }
            }

            this._listener.finishedDownload(this._otaStatus, this._otaError, rebootRequired);
            return;
         }

         this._listener.finishedDownload(this._otaStatus, this._otaError, rebootRequired);
         return;
      }

      boolean var35 = false /* VF: Semaphore variable */;

      label2269: {
         try {
            label2267:
            try {
               var35 = true;
               this.sendStatusReport();
               var35 = false;
               break label2269;
            } catch (Throwable var226) {
               QuincyUtil.sendQuincy(t, false);
               var35 = false;
               break label2267;
            }
         } finally {
            if (var35) {
               this._listener.finishedDownload(this._otaStatus, this._otaError, rebootRequired);
            }
         }

         this._listener.finishedDownload(this._otaStatus, this._otaError, rebootRequired);
         return;
      }

      this._listener.finishedDownload(this._otaStatus, this._otaError, rebootRequired);
   }

   @Override
   protected final void progressUpdate(long totalSizeDownloaded) {
      this._listener.progressUpdate((int)totalSizeDownloaded, 0);
   }

   @Override
   protected final void checkContentType(String contentType) {
   }

   @Override
   protected final void setError(String errorMessage) {
      this._otaStatus = 907;
      this._otaError = errorMessage;
   }

   @Override
   protected final void setError(int httpResponseCode) {
      if (httpResponseCode == 400) {
         this._otaStatus = 903;
         this._otaError = DownloadManager.formatHttpStatusMessage(httpResponseCode);
      } else if (httpResponseCode == 401 || httpResponseCode == 407) {
         this._otaStatus = 909;
         this._otaError = DownloadManager.formatHttpStatusMessage(httpResponseCode);
      } else if (httpResponseCode >= 900 && httpResponseCode <= 913) {
         this._otaStatus = httpResponseCode;
      } else {
         this._otaStatus = 907;
         this._otaError = DownloadManager.formatHttpStatusMessage(httpResponseCode);
      }
   }

   @Override
   protected final void setError(Throwable t) {
      if (t instanceof Object) {
         this._otaStatus = 901;
         this._otaError = BrowserResources.getString(588);
      } else {
         this.setError(t.getMessage());
      }
   }

   private final HttpHeaders getRequestHeaders(String acceptHeader, int codSelectionIndex) {
      HttpHeaders requestHeaders = (HttpHeaders)(new Object());
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      if (this._browserConfigRecord != null) {
         RenderingSession renderingSession = RenderingSessionImpl.getNewInstance();
         RenderingOptions renderingOptions = renderingSession.getRenderingOptions();
         browser.setRenderingOptions(renderingOptions, this._browserConfigRecord);
         browser.addStandardRequestHeaders(requestHeaders, renderingSession);
      } else {
         browser.addStandardRequestHeaders(requestHeaders);
      }

      if (acceptHeader != null) {
         requestHeaders.setProperty("Accept", acceptHeader);
      }

      if (codSelectionIndex != -1) {
         requestHeaders.setProperty("RIM_COD_Selection", Integer.toString(codSelectionIndex));
      }

      return requestHeaders;
   }

   private final byte[] requestJAR(int param1, boolean param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._jad Lnet/rim/device/apps/internal/browser/cod/JADAttributeParser;
      // 004: invokevirtual net/rim/device/apps/internal/browser/cod/JADAttributeParser.isSigned ()Z
      // 007: ifne 024
      // 00a: aload 0
      // 00b: getfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._listener Lnet/rim/device/apps/internal/browser/cod/ApplicationDownloadListener;
      // 00e: invokeinterface net/rim/device/apps/internal/browser/cod/ApplicationDownloadListener.installUnsigned ()Z 1
      // 013: ifne 024
      // 016: aload 0
      // 017: sipush 902
      // 01a: putfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._otaStatus I
      // 01d: aload 0
      // 01e: aconst_null
      // 01f: putfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._otaError Ljava/lang/String;
      // 022: aconst_null
      // 023: areturn
      // 024: aload 0
      // 025: bipush 1
      // 026: putfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._allowUnsigned Z
      // 029: aconst_null
      // 02a: astore 3
      // 02b: ldc_w "net.rim.device.apps.internal.browser.cod.Harness"
      // 02e: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 031: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 034: checkcast net/rim/device/apps/internal/browser/cod/JARCompiler
      // 037: astore 3
      // 038: goto 047
      // 03b: astore 4
      // 03d: goto 047
      // 040: astore 4
      // 042: goto 047
      // 045: astore 4
      // 047: aload 3
      // 048: ifnonnull 054
      // 04b: aload 0
      // 04c: sipush 908
      // 04f: putfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._otaStatus I
      // 052: aconst_null
      // 053: areturn
      // 054: aload 0
      // 055: getfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._jad Lnet/rim/device/apps/internal/browser/cod/JADAttributeParser;
      // 058: invokevirtual net/rim/device/apps/internal/browser/cod/JADAttributeParser.getJarURL ()Ljava/lang/String;
      // 05b: astore 4
      // 05d: aconst_null
      // 05e: astore 5
      // 060: aload 0
      // 061: getfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._jarStream Ljava/io/InputStream;
      // 064: ifnonnull 07e
      // 067: aload 0
      // 068: aload 0
      // 069: aload 4
      // 06b: aload 0
      // 06c: ldc_w "application/java-archive,application/java,application/x-java-archive,application/vnd.oma.drm.message;q=0.9"
      // 06f: bipush -1
      // 071: invokespecial net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager.getRequestHeaders (Ljava/lang/String;I)Lnet/rim/device/api/io/http/HttpHeaders;
      // 074: aload 0
      // 075: getfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._browserConfigRecord Lnet/rim/device/apps/internal/browser/options/BrowserConfigRecord;
      // 078: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.requestResourceStream (Ljava/lang/String;Lnet/rim/device/api/io/http/HttpHeaders;Ljava/lang/Object;)Ljava/io/InputStream;
      // 07b: putfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._jarStream Ljava/io/InputStream;
      // 07e: aload 0
      // 07f: getfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._jarStream Ljava/io/InputStream;
      // 082: ifnull 0bc
      // 085: aload 0
      // 086: getfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._jad Lnet/rim/device/apps/internal/browser/cod/JADAttributeParser;
      // 089: invokevirtual net/rim/device/apps/internal/browser/cod/JADAttributeParser.totalSize ()I
      // 08c: istore 6
      // 08e: aload 0
      // 08f: getfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._listener Lnet/rim/device/apps/internal/browser/cod/ApplicationDownloadListener;
      // 092: iload 6
      // 094: bipush 1
      // 095: invokeinterface net/rim/device/apps/internal/browser/cod/ApplicationDownloadListener.progressUpdate (II)V 3
      // 09a: aload 4
      // 09c: invokestatic net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager.getNameFromJarURL (Ljava/lang/String;)Ljava/lang/String;
      // 09f: astore 7
      // 0a1: aload 3
      // 0a2: aload 7
      // 0a4: aload 0
      // 0a5: getfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._jad Lnet/rim/device/apps/internal/browser/cod/JADAttributeParser;
      // 0a8: invokevirtual net/rim/device/apps/internal/browser/cod/JADAttributeParser.getJADString ()Ljava/lang/String;
      // 0ab: aload 0
      // 0ac: getfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._jarStream Ljava/io/InputStream;
      // 0af: iload 6
      // 0b1: aload 0
      // 0b2: getfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._listener Lnet/rim/device/apps/internal/browser/cod/ApplicationDownloadListener;
      // 0b5: invokeinterface net/rim/device/apps/internal/browser/cod/JARCompiler.compile (Ljava/lang/String;Ljava/lang/String;Ljava/io/InputStream;ILnet/rim/device/apps/internal/browser/cod/ApplicationDownloadListener;)Ljava/util/Vector; 6
      // 0ba: astore 5
      // 0bc: aload 0
      // 0bd: getfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._jarStream Ljava/io/InputStream;
      // 0c0: ifnull 0cf
      // 0c3: aload 0
      // 0c4: getfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._jarStream Ljava/io/InputStream;
      // 0c7: invokevirtual java/io/InputStream.close ()V
      // 0ca: goto 0cf
      // 0cd: astore 6
      // 0cf: aload 0
      // 0d0: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.closeInputConnection ()V
      // 0d3: goto 124
      // 0d6: astore 6
      // 0d8: aload 6
      // 0da: bipush 0
      // 0db: invokestatic net/rim/device/apps/internal/browser/util/QuincyUtil.sendQuincy (Ljava/lang/Throwable;Z)V
      // 0de: aload 0
      // 0df: sipush 907
      // 0e2: putfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._otaStatus I
      // 0e5: aload 0
      // 0e6: aload 6
      // 0e8: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 0eb: putfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._otaError Ljava/lang/String;
      // 0ee: aload 0
      // 0ef: getfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._jarStream Ljava/io/InputStream;
      // 0f2: ifnull 101
      // 0f5: aload 0
      // 0f6: getfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._jarStream Ljava/io/InputStream;
      // 0f9: invokevirtual java/io/InputStream.close ()V
      // 0fc: goto 101
      // 0ff: astore 6
      // 101: aload 0
      // 102: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.closeInputConnection ()V
      // 105: goto 124
      // 108: astore 8
      // 10a: aload 0
      // 10b: getfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._jarStream Ljava/io/InputStream;
      // 10e: ifnull 11d
      // 111: aload 0
      // 112: getfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._jarStream Ljava/io/InputStream;
      // 115: invokevirtual java/io/InputStream.close ()V
      // 118: goto 11d
      // 11b: astore 9
      // 11d: aload 0
      // 11e: invokevirtual net/rim/device/apps/internal/browser/download/DownloadManager.closeInputConnection ()V
      // 121: aload 8
      // 123: athrow
      // 124: aload 5
      // 126: ifnull 1a0
      // 129: aload 5
      // 12b: invokevirtual java/util/Vector.size ()I
      // 12e: istore 6
      // 130: iload 6
      // 132: ifne 16a
      // 135: aload 3
      // 136: invokeinterface net/rim/device/apps/internal/browser/cod/JARCompiler.getResultCode ()I 1
      // 13b: istore 7
      // 13d: iload 7
      // 13f: sipush 904
      // 142: if_icmplt 156
      // 145: iload 7
      // 147: sipush 910
      // 14a: if_icmpgt 156
      // 14d: aload 0
      // 14e: iload 7
      // 150: putfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._otaStatus I
      // 153: goto 15d
      // 156: aload 0
      // 157: sipush 907
      // 15a: putfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._otaStatus I
      // 15d: aload 0
      // 15e: aload 3
      // 15f: invokeinterface net/rim/device/apps/internal/browser/cod/JARCompiler.getError ()Ljava/lang/String; 1
      // 164: putfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._otaError Ljava/lang/String;
      // 167: goto 1a0
      // 16a: bipush 0
      // 16b: istore 7
      // 16d: iload 7
      // 16f: iload 6
      // 171: if_icmpge 1a0
      // 174: aload 5
      // 176: iload 7
      // 178: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 17b: checkcast [B
      // 17e: astore 8
      // 180: aload 0
      // 181: aload 8
      // 183: iload 1
      // 184: iload 2
      // 185: invokespecial net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager.installModule ([BIZ)V
      // 188: aload 0
      // 189: getfield net/rim/device/apps/internal/browser/cod/ApplicationDownloadManager._otaStatus I
      // 18c: sipush 900
      // 18f: if_icmpeq 19a
      // 192: iload 1
      // 193: invokestatic net/rim/device/api/system/CodeModuleManager.cancelTransaction (I)I
      // 196: pop
      // 197: goto 1a0
      // 19a: iinc 7 1
      // 19d: goto 16d
      // 1a0: aload 3
      // 1a1: invokeinterface net/rim/device/apps/internal/browser/cod/JARCompiler.getSignerCertEncoding ()[B 1
      // 1a6: areturn
      // try (21 -> 26): 27 null
      // try (21 -> 26): 29 null
      // try (21 -> 26): 31 null
      // try (89 -> 92): 93 null
      // try (45 -> 86): 97 null
      // try (111 -> 114): 115 null
      // try (45 -> 86): 119 null
      // try (97 -> 108): 119 null
      // try (123 -> 126): 127 null
      // try (119 -> 120): 119 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void addCertInformationToGroup(byte[] certEncoding) {
      X509Certificate cert = null;
      Digest digest = null;
      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         var8 = new Object(certEncoding);
         digest = DigestFactory.getInstance("SHA256");
         var6 = false;
      } finally {
         if (var6) {
            return;
         }
      }

      this._moduleGroup.setMIDletSigner(((X509Certificate)var8).getSubjectFriendlyName());
      digest.update(certEncoding);
      this._moduleGroup.setMIDletSignerHash(digest.getDigest());
   }

   static final String getNameFromJarURL(String url) {
      int urlLen = url.length();
      int ind1 = url.lastIndexOf(47);
      if (ind1 == urlLen - 1) {
         url = url.substring(0, ind1);
         ind1 = url.lastIndexOf(47);
      }

      int ind2 = url.indexOf(46, ind1 + 1);
      if (ind2 == -1) {
         ind2 = urlLen;
      }

      return url.substring(ind1 + 1, ind2);
   }

   private final void requestCODs(int transactionHandle, boolean upgrade) {
      if (this._jad.isTranscodedJar()) {
         if (!this._listener.installUnsigned()) {
            this._otaStatus = 902;
            this._otaError = null;
            return;
         }

         this._allowUnsigned = true;
      }

      int urlCount = this._jad.getCodUrlCount();

      for (int i = 0; i < urlCount; i++) {
         if (this.isAborted()) {
            return;
         }

         String url = this._jad.getURLbyNumber(i);
         byte[] inputBytes = this.requestResource(
            url,
            this.getRequestHeaders("application/vnd.rim.cod,application/vnd.oma.drm.message;q=0.9,application/octet-stream;q=0.9,text/plain;q=0.9", i),
            this._browserConfigRecord
         );
         if (inputBytes != null) {
            this.installModule(inputBytes, transactionHandle, upgrade);
         }

         if (this._otaStatus != 900) {
            CodeModuleManager.cancelTransaction(transactionHandle);
            return;
         }
      }
   }

   private final void installModule(byte[] inputBytes, int transactionHandle, boolean upgrade) {
      if (this.isDrmProtected()) {
         inputBytes = CodeStore.addDRMTrailers(inputBytes);
      }

      int length = inputBytes.length;
      int moduleHandle = 0;
      if (!DownloadManager.ensureAvailableFlash(length + 131072)) {
         this._otaStatus = 901;
      } else {
         if (length > 4 && inputBytes[0] == 80 && inputBytes[1] == 75 && inputBytes[2] == 3 && inputBytes[3] == 4) {
            this._otaStatus = 907;
            this._otaError = BrowserResources.getString(727);
            return;
         }

         String moduleName = this.getModuleNameFromBytes(inputBytes);
         if (upgrade) {
            if (moduleName != null) {
               String midletName = this._moduleGroup.getFriendlyName();
               String vendorName = this._moduleGroup.getVendor();
               CodeModuleGroup[] cmgs = CodeModuleGroupManager.loadAll();
               if (cmgs != null) {
                  for (int i = 0; i < cmgs.length; i++) {
                     if ((cmgs[i].getName() == null || !cmgs[i].getName().equals(this._moduleGroup.getName()))
                        && (
                           midletName == null
                              || !midletName.equals(cmgs[i].getFriendlyName())
                              || vendorName == null
                              || !vendorName.equals(cmgs[i].getFriendlyName())
                        )
                        && cmgs[i].containsModule(moduleName)) {
                        if (!allowUpgrade(cmgs[i])) {
                           this._otaStatus = 910;
                           return;
                        }

                        byte[] deviceHash = null;

                        try {
                           deviceHash = CodeModuleManager.getModuleHash(CodeModuleManager.getModuleHandle(moduleName));
                        } finally {
                           break;
                        }

                        byte[] downloadHash = this.getHashFromBytes(inputBytes);
                        if (!this.identicalHash(downloadHash, deviceHash)
                           && !this._listener.newerModule(this._moduleGroup.getName(), moduleName, cmgs[i].getFriendlyName())) {
                           this._otaStatus = 902;
                           this._otaError = null;
                           return;
                        }
                     }
                  }
               }
            }
         } else if (moduleName != null) {
            int[] handles = CodeModuleManager.getModuleHandles();

            for (int i = 0; i < handles.length; i++) {
               if (moduleName.equals(CodeModuleManager.getModuleName(handles[i]))) {
                  byte[] downloadHash = this.getHashFromBytes(inputBytes);
                  byte[] deviceHash = CodeModuleManager.getModuleHash(handles[i]);
                  if (!this.identicalHash(downloadHash, deviceHash)) {
                     CodeModuleGroup dupGroup = null;
                     CodeModuleGroup[] cmgs = CodeModuleGroupManager.loadAll();
                     if (cmgs != null) {
                        for (int j = 0; j < cmgs.length; j++) {
                           if (cmgs[j].containsModule(moduleName)) {
                              if (!allowUpgrade(cmgs[j])) {
                                 this._otaStatus = 910;
                                 return;
                              }

                              dupGroup = cmgs[j];
                              if (!this._listener.overwriteModule(this._moduleGroup.getName(), moduleName, dupGroup)) {
                                 this._otaStatus = 902;
                                 this._otaError = null;
                                 return;
                              }

                              this._upgradeAndDeleteGroups.put(dupGroup, "delete");
                           }
                        }
                     }

                     if (dupGroup == null && !this._listener.overwriteModule(this._moduleGroup.getName(), moduleName, null)) {
                        this._otaStatus = 902;
                        this._otaError = null;
                        return;
                     }
                     break;
                  }
               }
            }
         }

         moduleHandle = CodeModuleManager.createNewModule(length, inputBytes, length);
         if (moduleHandle == 0) {
            this._otaStatus = 907;
         } else {
            if (!this._allowUnsigned) {
               label337:
               try {
                  if (CodeModuleManager.isMidlet(moduleHandle) && CodeModuleManager.getModuleSignerId(moduleHandle, 0) == -1) {
                     if (!this._listener.installUnsigned()) {
                        this._otaStatus = 902;
                        this._otaError = null;
                        CodeModuleManager.deleteNewModule(moduleHandle);
                        return;
                     }

                     this._allowUnsigned = true;
                  }
               } finally {
                  break label337;
               }
            }

            int status = CodeModuleManager.saveNewModule(moduleHandle, upgrade, transactionHandle);
            if (!upgrade && status == 5) {
               if (!this._listener.performUpgrade()) {
                  this._otaStatus = 902;
                  CodeModuleManager.deleteNewModule(moduleHandle);
                  return;
               }

               status = CodeModuleManager.saveNewModule(moduleHandle, true, transactionHandle);
            }

            switch (status) {
               case 0:
               case 1:
                  this._loadedModules.addElement(moduleHandle);
                  this._otaStatus = 900;
                  break;
               case 2:
                  this._otaStatus = 909;
                  break;
               case 3:
                  this._otaStatus = 910;
                  break;
               case 4:
                  this._otaStatus = 907;
                  break;
               case 12:
                  this._otaStatus = 910;
            }
         }
      }

      if (this._otaStatus != 900 && moduleHandle != 0) {
         CodeModuleManager.deleteNewModule(moduleHandle);
      }
   }

   private final boolean allowMIDletRecordStoreUpgrade(CodeModuleGroup existingModuleGroup) {
      RecordStoreManagerProxy rsmp = (RecordStoreManagerProxy)ApplicationRegistry.getApplicationRegistry().waitFor(6635119920104263588L);
      if (!rsmp.recordStoresExistForSuite(existingModuleGroup.getFriendlyName(), existingModuleGroup.getVendor())) {
         return true;
      }

      String newSigner = this._moduleGroup.getMIDletSigner();
      String existingSigner = existingModuleGroup.getProperty("RIM-MIDlet-Signer");
      boolean signersMatch = newSigner != null && existingSigner != null && newSigner.equals(existingSigner);
      boolean jadURLsMatch = this.urlSchemeHostAndPathMatch(this._jad.getJadURL(), existingModuleGroup.getProperty("RIM-JAD-URL"));
      boolean jarURLsMatch = true;
      int codCount = this._jad.getCodUrlCount();
      if (codCount > 0) {
         for (int i = 0; i < codCount; i++) {
            String codURLKey = i == 0 ? "RIM-COD-URL" : ((StringBuffer)(new Object("RIM-COD-URL-"))).append(i).toString();
            if (!this.urlSchemeHostAndPathMatch(this._jad.getURLbyNumber(i), existingModuleGroup.getProperty(codURLKey))) {
               jarURLsMatch = false;
            }
         }

         if (existingModuleGroup.getProperty(((StringBuffer)(new Object("RIM-COD-URL-"))).append(codCount).toString()) != null) {
            jarURLsMatch = false;
         }
      } else {
         jarURLsMatch = this.urlSchemeHostAndPathMatch((String)this._jad.get("MIDlet-Jar-URL"), existingModuleGroup.getProperty("MIDlet-Jar-URL"));
      }

      return signersMatch || jadURLsMatch || jarURLsMatch;
   }

   private final boolean urlSchemeHostAndPathMatch(String url1, String url2) {
      if (url1 != null && url2 != null) {
         URL parsedURL1 = null;
         URL parsedURL2 = null;

         try {
            var8 = new Object(url1);
            var9 = new Object(url2);
         } finally {
            ;
         }

         return this.partsMatch(((URL)var8).getScheme(), ((URL)var9).getScheme())
            && this.partsMatch(((URL)var8).getHost(), ((URL)var9).getHost())
            && this.partsMatch(((URL)var8).getPath(), ((URL)var9).getPath());
      } else {
         return false;
      }
   }

   private final boolean partsMatch(String string1, String string2) {
      if (string1 == null && string2 == null) {
         return true;
      } else {
         return string1 != null && string2 != null ? string1.equals(string2) : false;
      }
   }

   private final void storeJadAttributes() {
      this._moduleGroup.clearProperties();
      Enumeration e = this._jad.keys();

      while (e.hasMoreElements()) {
         String key = (String)e.nextElement();
         this._moduleGroup.setProperty(key, (String)this._jad.get(key));
      }

      String midletSigner = this._moduleGroup.getMIDletSigner();
      if (midletSigner != null) {
         this._moduleGroup.setProperty("RIM-MIDlet-Signer", midletSigner);
      }

      this._moduleGroup.setProperty("RIM-JAD-URL", this._jad.getJadURL());
   }

   private static final boolean allowUpgrade(CodeModuleGroup cmg) {
      return !cmg.containsModule("net_rim_os")
         && !cmg.containsModule("net_rim_cldc")
         && !cmg.containsModule("net_rim_bb_browser_lib")
         && !cmg.containsModule("net_rim_bb_phone_app");
   }

   private final String getModuleGroupName() {
      return this.getModuleGroupName((String)this._jad.get("MIDlet-Name"), (String)this._jad.get("MIDlet-Vendor"));
   }

   private final String getModuleGroupName(String midletName, String midletVendor) {
      return ((StringBuffer)(new Object())).append(midletName).append(':').append(midletVendor).toString();
   }

   private final String getModuleNameFromBytes(byte[] bytes) {
      try {
         int dataOffset = 44 + (bytes[38] & 255) + ((bytes[39] & 255) << 8);
         int classOffset = 52 + (bytes[dataOffset + 5] & 255) * 2;
         int nameOffset = (bytes[dataOffset + classOffset] & 255) + ((bytes[dataOffset + classOffset + 1] & 255) << 8);
         StringBuffer moduleName = (StringBuffer)(new Object());

         for (int i = dataOffset + nameOffset; bytes[i] != 0; i++) {
            moduleName.append((char)bytes[i]);
         }

         return moduleName.toString();
      } finally {
         ;
      }
   }

   private final byte[] getHashFromBytes(byte[] bytes) {
      SHA1Digest digest = (SHA1Digest)(new Object());

      try {
         int codeSize = (bytes[38] & 255) + ((bytes[39] & 255) << 8);
         int dataSize = (bytes[40] & 255) + ((bytes[41] & 255) << 8);
         digest.update(bytes, 0, 44 + codeSize + dataSize);
      } finally {
         ;
      }

      return digest.getDigest();
   }

   private final boolean identicalHash(byte[] hash1, byte[] hash2) {
      boolean duplicate = hash1 != null && hash2 != null && hash1.length == hash2.length;
      if (duplicate) {
         for (int i = 0; i < hash1.length; i++) {
            if (hash1[i] != hash2[i]) {
               return false;
            }
         }
      }

      return duplicate;
   }

   private final void sendStatusReport() {
      this.sendStatusReport(this._otaStatus);
   }

   final void sendStatusReport(int statusCode) {
      OTAStatusReportService ota = BrowserServices.getOTAStatusReportService();
      if (ota != null) {
         String midletInstallNotify = (String)this._jad.get("MIDlet-Install-Notify");
         String moduleGroupName = this._moduleGroup != null ? this._moduleGroup.getName() : this.getModuleGroupName();
         if (midletInstallNotify != null) {
            ota.sendReport(moduleGroupName, midletInstallNotify, statusCode, null);
         }

         if (statusCode == 900) {
            String midletDeleteNotify = (String)this._jad.get("MIDlet-Delete-Notify");
            if (midletDeleteNotify != null && this._loadedModules != null && this._loadedModules.size() > 0) {
               ota.addDeleteNotifyApp(moduleGroupName, midletDeleteNotify, null);
            }
         }
      }
   }

   private final boolean setPermissions() {
      boolean resetRequired = false;
      if (this._applicationPermissions != null) {
         Enumeration moduleNames = this._moduleGroup.getModules();
         String currModuleName = null;
         int currModuleHandle = 0;
         byte[] currModuleHash = null;

         while (moduleNames.hasMoreElements()) {
            currModuleName = (String)moduleNames.nextElement();
            currModuleHandle = CodeModuleManager.getModuleHandle(currModuleName);
            currModuleHash = CodeModuleManager.getModuleHash(currModuleHandle);
            resetRequired |= ApplicationControl.setModuleUserPermission(currModuleHash, currModuleHandle, this._applicationPermissions);
         }
      }

      return resetRequired;
   }
}
