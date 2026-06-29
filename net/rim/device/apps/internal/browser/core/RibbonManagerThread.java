package net.rim.device.apps.internal.browser.core;

import java.util.Vector;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.StringPatternContainer;
import net.rim.device.api.util.StringPatternRepository$Internal;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.ribbon.ConvenienceKeyOptionsProvider;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.internal.browser.channel.Channels;
import net.rim.device.apps.internal.browser.common.RenderingUtilities;
import net.rim.device.apps.internal.browser.img.ImageRenderingConverter;
import net.rim.device.apps.internal.browser.model.DTMFStringPattern;
import net.rim.device.apps.internal.browser.model.HTTPAddressStringPattern;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.options.DomainOverrides;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.apps.internal.browser.sbloader.SBHelper;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.stack.RawDataCache;
import net.rim.device.apps.internal.browser.util.ImageConverter;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.apps.internal.browser.util.RunnableThread;
import net.rim.device.apps.internal.browser.verbs.DownloadContentVerb;
import net.rim.device.cldc.io.waphttp.WAPConnectionRegistry;
import net.rim.device.internal.browser.wap.WAPServiceRecord;
import net.rim.device.internal.browser.wap.WPTCPServiceRecord;
import net.rim.device.internal.provisioning.ProvisioningHandler;
import net.rim.device.internal.provisioning.ProvisioningService;

public final class RibbonManagerThread extends RunnableThread implements ProvisioningHandler {
   private BrowserImpl _browserImpl;
   private boolean _processing = true;
   private Object _processingObject = new Object();
   private Object _ribbonSyncObject = new Object();
   private boolean _lastItPolicyWAPEnabledValue;
   private boolean _lastItPolicyPrivateMDSEnabledValue;
   private boolean _lastItPolicyPublicMDSEnabledValue;
   private boolean _lastItPolicyBWCEnabledValue;
   private boolean _lastItPolicyAppDownloadEnabledValue;
   private Vector _autoStartBrowserConfigs;
   private String _currentAutoStartBrowserConfigUid;
   private boolean _browserAvailable;
   private static final String BROWSER_MODULE_NAME;
   private static final String DEFAULT_BROWSER_ID;
   private static final String SINGLE_BROWSER_ICON_ID;
   private static final int BASE_RIBBON_POSITION;
   private static final int MDS_PRIVATE_RIBBON_POSITION;
   private static final int MDS_PUBLIC_RIBBON_POSITION;
   private static final int WAP_SECURE_RIBBON_POSITION;
   private static final int WAP_INSECURE_RIBBON_POSITION;
   private static final int PROV_RIBBON_POSITION;
   private static String _provMoreImagesUrl;
   private static String _provMoreTunesUrl;
   private static String _provMoreThemesUrl;
   private static String SECURE_WAP_ICON = "IconSecureWAP28.gif";
   private static String INSECURE_WAP_ICON = "IconInsecureWAP28.gif";
   private static String PRIVATE_MDS_ICON = "IconPrivateMDS28.gif";
   private static String PUBLIC_MDS_ICON = "IconPublicMDS28.gif";
   private static String BWC_ICON = "IconBWC28.gif";
   private static String TEAM_ON_ICON = "IconTeamOn28.gif";
   private static String APP_DOWNLOAD_ICON = "AppDownload28.gif";
   private static Bitmap SECURE_WAP_BITMAP;
   private static Bitmap INSECURE_WAP_BITMAP;
   private static Bitmap PRIVATE_MDS_BITMAP;
   private static Bitmap PUBLIC_MDS_BITMAP;
   private static Bitmap BWC_BITMAP;
   private static Bitmap TEAM_ON_BITMAP;
   private static Bitmap APP_DOWNLOAD_BITMAP;

   final void cleanup() {
      ProvisioningService provisioningService = ProvisioningService.getInstance();
      provisioningService.removeHandler(this);
   }

   final void removeFromRibbon(ServiceRecord serviceRecord) {
      EventLogger.logEvent(1907089860548946979L, ((StringBuffer)(new Object("RMrr \nUID: "))).append(serviceRecord.getUid()).toString().getBytes(), 5);
      this.addToQueue(new RibbonManagerThread$RibbonEvent(this, 2, serviceRecord.getId(), false, null));
      if (this._browserAvailable) {
         boolean browserAvailable = BrowserConfigRecord.getValidBrowserConfigRecords().length > 0;
         if (this._browserAvailable != browserAvailable) {
            this._browserAvailable = browserAvailable;
            this.togglePatternMatchers();
         }
      }

      BrowserConfigRecord rec = BrowserConfigRecord.getDecodedConfig(serviceRecord);
      if (rec != null) {
         this.checkAutoStart(rec, serviceRecord, false);
         checkDownloadVerbs(rec, serviceRecord, false, false);
      }
   }

   final void removeFromRibbon(int id) {
      this.addToQueue(new RibbonManagerThread$RibbonEvent(this, 2, id, false, null));
   }

   final void startProcessing() {
      synchronized (this._processingObject) {
         this._processing = true;
         this._processingObject.notify();
      }
   }

   final void stopProcessing() {
      synchronized (this._processingObject) {
         this._processing = false;
      }
   }

   final void addIconToRibbon(ServiceRecord sr, boolean refresh) {
      synchronized (this._ribbonSyncObject) {
         RibbonLauncher ribbon = RibbonLauncher.getInstance();
         if (ribbon != null) {
            String id = ((StringBuffer)(new Object("net_rim_bb_browser_daemon"))).append(sr.getId()).toString();
            EventLogger.logEvent(
               1907089860548946979L, ((StringBuffer)(new Object("RMai \nUID: "))).append(sr.getUid()).append(" ID: ").append(id).toString().getBytes(), 5
            );
            if (ribbon.getRegisteredAction(id) != null) {
               if (!refresh) {
                  return;
               }

               ribbon.unregisterAction(id);
               this.validateHotKeys();
            }

            BrowserConfigRecord rec = BrowserConfigRecord.getDecodedConfig(sr);
            if (rec != null) {
               int moduleHandle = CodeModuleManager.getModuleHandle("net_rim_bb_browser_daemon");
               if (moduleHandle > 0) {
                  rec.addLocalizedStrings(sr);
                  int configType = rec.getPropertyAsInt(12);
                  if (rec.getPropertyAsInt(30) != 1) {
                     ApplicationDescriptor[] browserDescriptors = CodeModuleManager.getApplicationDescriptors(moduleHandle);
                     int newFlags = browserDescriptors[0].getFlags() ^ 4;
                     boolean useSeparateIcon = rec.getPropertyAsBoolean(55);
                     if (useSeparateIcon) {
                        String[] args = new Object[]{"activate", sr.getUid()};
                        Bitmap iconCustom = null;
                        if (!Graphics.isColor()) {
                           byte[] iconFromServiceBook = (byte[])rec.getPropertyAsObject(14);
                           if (iconFromServiceBook != null) {
                              EncodedImage image = ImageConverter.convertAndScale(
                                 iconFromServiceBook, 0, iconFromServiceBook.length, "image/gif", -1, -1, Display.getWidth()
                              );
                              iconCustom = image != null ? image.getBitmap() : null;
                           }
                        }

                        if (iconCustom == null && rec.doesBrandingVendorIdMatch()) {
                           int brandingIcon = rec.getPropertyAsInt(21);
                           if (brandingIcon != -1) {
                              byte[] iconFromNVRam = Branding.getData(16640 + brandingIcon);
                              if (iconFromNVRam != null) {
                                 EncodedImage image = ImageConverter.convertAndScale(
                                    iconFromNVRam, 0, iconFromNVRam.length, "image/png", -1, -1, Display.getWidth()
                                 );
                                 iconCustom = image != null ? image.getBitmap() : iconCustom;
                              }
                           }
                        }

                        Bitmap iconDefault = null;
                        String type = null;
                        int positionOffset = rec.getPropertyAsInt(25);
                        int position;
                        switch (configType) {
                           case -1:
                           case 3:
                              iconDefault = loadBitmap(INSECURE_WAP_ICON);
                              type = "browser.";
                              position = 59 + positionOffset;
                              break;
                           case 0:
                           case 7:
                              String transportCid = rec.getPropertyAsString(3);
                              String transportUid = rec.getPropertyAsString(4);
                              WAPServiceRecord wapSr = configType == 0 ? WAPServiceRecord.getRecord(transportCid, transportUid) : null;
                              if (wapSr != null && wapSr.getSecureAccess() == 1) {
                                 iconDefault = loadBitmap(SECURE_WAP_ICON);
                                 type = "wap.secure.";
                                 position = 55 + positionOffset;
                              } else {
                                 iconDefault = loadBitmap(INSECURE_WAP_ICON);
                                 type = "wap.insecure.";
                                 position = 57 + positionOffset;
                              }

                              if (iconCustom == null && rec.doesBrandingVendorIdMatch()) {
                                 byte[] iconFromNVRam = Branding.getData(16384);
                                 if (iconFromNVRam != null) {
                                    EncodedImage image = ImageConverter.convertAndScale(
                                       iconFromNVRam, 0, iconFromNVRam.length, "image/png", -1, -1, Display.getWidth()
                                    );
                                    iconCustom = image != null ? image.getBitmap() : iconCustom;
                                 }
                              }
                              break;
                           case 1:
                           default:
                              iconDefault = loadBitmap(PRIVATE_MDS_ICON);
                              type = "mds.private.";
                              position = 51 + positionOffset;
                              break;
                           case 2:
                              iconDefault = loadBitmap(BWC_ICON);
                              type = "prov.bwc.";
                              position = 59 + positionOffset;
                              break;
                           case 4:
                           case 8:
                              iconDefault = loadBitmap(PUBLIC_MDS_ICON);
                              if (rec.getPropertyAsBoolean(51)) {
                                 type = "mds.public.rim.";
                                 position = 53 + positionOffset;
                              } else {
                                 type = "mds.public.";
                                 position = 54 + positionOffset;
                              }
                              break;
                           case 5:
                              iconDefault = loadBitmap(TEAM_ON_ICON);
                              type = "prov.teamon.";
                              position = 59 + positionOffset;
                              break;
                           case 6:
                              iconDefault = loadBitmap(APP_DOWNLOAD_ICON);
                              type = "appdownload.";
                              position = positionOffset;
                              if (iconCustom == null) {
                                 String iconUrl = rec.getPropertyAsString(29);
                                 if (iconUrl != null && iconUrl.length() != 0) {
                                    Object ticket = PersistentContent.waitForTicket();
                                    ticket.hashCode();
                                    BrowserImpl browserImpl = BrowserDaemonRegistry.getInstance();
                                    RawDataCache rawDataCache = browserImpl.getRawDataCache();
                                    boolean requestIcon = false;
                                    CacheResult cacheResult = rawDataCache.get(iconUrl, 1, null, null, null, false);
                                    if (cacheResult == null) {
                                       requestIcon = true;
                                       cacheResult = rawDataCache.get(iconUrl);
                                    }

                                    if (cacheResult != null) {
                                       byte[] data = RendererControl.readBytesFromInputStream(cacheResult.getStream());
                                       if (data != null) {
                                          label271:
                                          try {
                                             iconCustom = Bitmap.createBitmapFromBytes(data, 0, data.length, 1);
                                          } finally {
                                             break label271;
                                          }
                                       }
                                    }

                                    if (requestIcon) {
                                       HttpHeaders requestHeaders = (HttpHeaders)(new Object());
                                       requestHeaders.setProperty("Accept", ImageRenderingConverter.getAcceptString());
                                       RenderingUtilities.setTranscodeHeader(requestHeaders, false);
                                       RenderingSession renderingSession = RenderingSessionImpl.getNewInstance();
                                       RenderingOptions renderingOptions = renderingSession.getRenderingOptions();
                                       browserImpl.setRenderingOptions(renderingOptions, rec);
                                       browserImpl.addStandardRequestHeaders(requestHeaders, renderingSession);
                                       ModelResult modelResult = new ModelResult(iconUrl, 1, requestHeaders);
                                       String uid = sr.getUid();
                                       String cid = rec.getPropertyAsString(3);
                                       modelResult.setConfigUID(uid);
                                       modelResult.setTransportCID(cid);
                                       FetchRequest fetchRequest = new FetchRequest(modelResult, rec, 8);
                                       fetchRequest.addPendingRequest(new IconLoadListener(id));
                                    }
                                 }
                              }
                        }

                        String tempType = rec.getPropertyAsString(54);
                        if (tempType != null && tempType.length() > 0) {
                           type = ((StringBuffer)(new Object())).append(tempType).append(".").toString();
                        }

                        ApplicationDescriptor newDescriptor = (ApplicationDescriptor)(new Object(
                           browserDescriptors[0],
                           ((StringBuffer)(new Object())).append(type).append(sr.getUid()).toString(),
                           args,
                           iconDefault,
                           position,
                           "net.rim.device.apps.internal.browser.options.BrowserConfigRecord.BUNDLE_ID",
                           rec.getResourceID(11),
                           newFlags
                        ));
                        ApplicationEntryPoint newEntryPoint = (ApplicationEntryPoint)(new Object(newDescriptor));
                        if (iconCustom != null) {
                           newEntryPoint.set(4, iconCustom);
                        }

                        EventLogger.logEvent(
                           1907089860548946979L,
                           ((StringBuffer)(new Object("RMas \nUID: "))).append(sr.getUid()).append(" ID: ").append(id).toString().getBytes(),
                           5
                        );
                        ribbon.registerAction(id, newEntryPoint);
                     }

                     if (Graphics.isColor() && ribbon.getRegisteredAction("net_rim_bb_browser_daemondefault") == null) {
                        String[] defaultArgs = new Object[]{"activate", null};
                        ApplicationDescriptor defaultDescriptor = (ApplicationDescriptor)(new Object(
                           browserDescriptors[0], "default", defaultArgs, null, -1, null, -1, newFlags
                        ));
                        ApplicationEntryPoint defaultEntryPoint = (ApplicationEntryPoint)(new Object(defaultDescriptor));
                        ribbon.registerAction("net_rim_bb_browser_daemondefault", defaultEntryPoint);
                     }

                     if (!useSeparateIcon && ribbon.getRegisteredAction("net_rim_bb_browser_daemonsingle") == null) {
                        String[] defaultArgs = new Object[]{"activate", null};
                        ApplicationDescriptor defaultDescriptor = (ApplicationDescriptor)(new Object(
                           browserDescriptors[0], "single", defaultArgs, null, 50, null, -1, newFlags
                        ));
                        ApplicationEntryPoint defaultEntryPoint = (ApplicationEntryPoint)(new Object(defaultDescriptor));
                        ribbon.registerAction("net_rim_bb_browser_daemonsingle", defaultEntryPoint);
                     }
                  }

                  DomainOverrides.getInstance().addOverrides(sr.getId(), rec);
                  if (configType != 2 && configType != 6) {
                     String srUID = sr.getUid();
                     String transportCid = rec.getPropertyAsString(3);
                     if (StringUtilities.strEqualIgnoreCase(transportCid, BrowserConfigRecord.IPPP_SERVICE_CID, 1701707776)) {
                        if (StringUtilities.strEqualIgnoreCase(GeneralProperty.getPreferredMdsBrowserConfigServiceUID(), srUID, 1701707776)) {
                           GeneralProperty.setDefaultMdsBrowserConfigServiceUID(srUID);
                           GeneralProperty.setCurrentProperty(33, true);
                        } else {
                           String mdsUID = GeneralProperty.getDefaultMdsBrowserConfigServiceUID();
                           if (mdsUID.length() == 0
                              || StringUtilities.strEqualIgnoreCase(mdsUID, srUID, 1701707776)
                              || !GeneralProperty.getCurrentPropertyAsBoolean(33) && configType == 1) {
                              GeneralProperty.setDefaultMdsBrowserConfigServiceUID(srUID);
                              if (configType == 1) {
                                 GeneralProperty.setCurrentProperty(33, true);
                              }
                           }
                        }
                     } else if (StringUtilities.strEqualIgnoreCase(transportCid, WAPServiceRecord.SERVICE_CID, 1701707776)) {
                        this.checkDefaultWAPBrowser(srUID);
                     } else if (StringUtilities.strEqualIgnoreCase(transportCid, WPTCPServiceRecord.SERVICE_CID, 1701707776)) {
                        if (rec.getPropertyAsInt(12) == 7) {
                           this.checkDefaultWAPBrowser(srUID);
                        }

                        GeneralProperty.setDefaultWptcpBrowserConfigServiceUID(srUID);
                     }
                  }
               }
            }
         }
      }
   }

   final void removeIconFromRibbon(int srId) {
      synchronized (this._ribbonSyncObject) {
         String id = ((StringBuffer)(new Object("net_rim_bb_browser_daemon"))).append(srId).toString();
         RibbonLauncher ribbon = RibbonLauncher.getInstance();
         if (ribbon != null) {
            ribbon.unregisterAction(id);
            this.validateHotKeys();
         }

         DomainOverrides.getInstance().removeOverrides(srId);
      }
   }

   final void validateHotKeys() {
      BrowserConfigRecord[] configs = BrowserConfigRecord.getValidBrowserConfigRecords();
      String mdsUid = GeneralProperty.getDefaultMdsBrowserConfigServiceUID();
      if (mdsUid.length() != 0) {
         boolean valid = false;
         String newUid = null;
         int numConfigs = configs.length;

         for (int i = 0; i < numConfigs; i++) {
            String configUid = configs[i].getUid();
            if (StringUtilities.strEqualIgnoreCase(mdsUid, configUid, 1701707776)) {
               valid = true;
               break;
            }

            if (newUid == null) {
               int configType = configs[i].getPropertyAsInt(12);
               if (configType != 2
                  && configType != 6
                  && StringUtilities.strEqualIgnoreCase(configs[i].getPropertyAsString(3), BrowserConfigRecord.IPPP_SERVICE_CID, 1701707776)) {
                  newUid = configUid;
               }
            }
         }

         if (!valid) {
            GeneralProperty.setDefaultMdsBrowserConfigServiceUID(newUid);
            GeneralProperty.setCurrentProperty(33, false);
         }
      }

      String wapUid = GeneralProperty.getDefaultWapBrowserConfigServiceUID();
      if (wapUid.length() != 0) {
         boolean valid = false;
         String newUid = null;
         int numConfigs = configs.length;

         for (int i = 0; i < numConfigs; i++) {
            String configUid = configs[i].getUid();
            if (StringUtilities.strEqualIgnoreCase(wapUid, configUid, 1701707776)) {
               valid = true;
               break;
            }

            if (newUid == null) {
               int configType = configs[i].getPropertyAsInt(12);
               if (configType != 2
                  && configType != 6
                  && (configType == 7 || StringUtilities.strEqualIgnoreCase(configs[i].getPropertyAsString(3), WAPServiceRecord.SERVICE_CID, 1701707776))) {
                  newUid = configUid;
               }
            }
         }

         if (!valid) {
            GeneralProperty.setDefaultWapBrowserConfigServiceUID(newUid);
            GeneralProperty.setCurrentProperty(32, false);
         }
      }

      boolean valid = false;
      String defaultUid = GeneralProperty.getDefaultBrowserConfigServiceUID();
      if (defaultUid.length() != 0) {
         int numConfigs = configs.length;

         for (int i = 0; i < numConfigs; i++) {
            String configUid = configs[i].getUid();
            if (StringUtilities.strEqualIgnoreCase(defaultUid, configUid, 1701707776)) {
               valid = true;
               break;
            }
         }

         if (!valid) {
            GeneralProperty.setDefaultBrowserConfigServiceUID(null);
         }
      }

      if (Graphics.isColor() && !valid) {
         String newUid = GeneralProperty.determineDefaultBrowserConfigServiceUID();
         if (newUid.length() == 0) {
            RibbonLauncher ribbon = RibbonLauncher.getInstance();
            if (ribbon != null) {
               ribbon.unregisterAction("net_rim_bb_browser_daemondefault");
            }
         }
      }

      boolean useSingleIcon = false;
      int numConfigs = configs.length;

      for (int i = 0; i < numConfigs; i++) {
         if (configs[i].getPropertyAsInt(30) != 1 && !configs[i].getPropertyAsBoolean(55)) {
            useSingleIcon = true;
            break;
         }
      }

      if (!useSingleIcon) {
         RibbonLauncher ribbon = RibbonLauncher.getInstance();
         if (ribbon != null) {
            ribbon.unregisterAction("net_rim_bb_browser_daemonsingle");
         }
      }
   }

   final void handleAddedServiceRecord(ServiceRecord sr, boolean refresh) {
      if (sr != null) {
         ServiceBook sb = ServiceBook.getSB();
         String cid = sr.getCid();
         EventLogger.logEvent(
            1907089860548946979L, ((StringBuffer)(new Object("RMsb \nCID: "))).append(cid).append(" UID: ").append(sr.getUid()).toString().getBytes(), 5
         );
         if (!StringUtilities.strEqualIgnoreCase(cid, BrowserConfigRecord.SERVICE_CID, 1701707776)) {
            if (StringUtilities.strEqualIgnoreCase(cid, WAPServiceRecord.SERVICE_CID, 1701707776)
               || StringUtilities.strEqualIgnoreCase(cid, BrowserConfigRecord.IPPP_SERVICE_CID, 1701707776)
               || StringUtilities.strEqualIgnoreCase(cid, BrowserConfigRecord.TCP_SERVICE_CID, 1701707776)) {
               EventLogger.logEvent(1907089860548946979L, "RMbt".getBytes(), 5);
               boolean desktopMdsTransport = isDesktopMdsTransport(sr);
               boolean desktopMdsConfig = false;
               String uid = sr.getUid();
               ServiceRecord[] configs = sb.findRecordsByCid(BrowserConfigRecord.SERVICE_CID);

               for (int i = 0; i < configs.length; i++) {
                  ServiceRecord configSr = configs[i];
                  BrowserConfigRecord rec = BrowserConfigRecord.getDecodedConfig(configSr);
                  if (rec != null) {
                     String transportCid = rec.getPropertyAsString(3);
                     String transportUid = rec.getPropertyAsString(4);
                     if (transportCid != null
                        && transportUid != null
                        && StringUtilities.strEqualIgnoreCase(cid, transportCid, 1701707776)
                        && StringUtilities.strEqualIgnoreCase(uid, transportUid, 1701707776)) {
                        EventLogger.logEvent(
                           1907089860548946979L,
                           ((StringBuffer)(new Object("RMcf \nCCID: ")))
                              .append(configSr.getCid())
                              .append(" CUID: ")
                              .append(configSr.getUid())
                              .toString()
                              .getBytes(),
                           5
                        );
                        int configType = rec.getPropertyAsInt(12);
                        if (configType == 0 && !WAPConnectionRegistry.isWAPInstalled()) {
                           EventLogger.logEvent(
                              1907089860548946979L,
                              ((StringBuffer)(new Object("RMwn \nTCID: "))).append(transportCid).append(" TUID: ").append(transportUid).toString().getBytes(),
                              5
                           );
                           return;
                        }

                        if (rec.isITEnabled()) {
                           if (configType == 6) {
                              String moduleName = rec.getPropertyAsString(28);
                              if (moduleName != null && moduleName.length() != 0) {
                                 int handle = CodeModuleManager.getModuleHandle(moduleName);
                                 if (handle == 0) {
                                    this.addToRibbon(rec, configSr, refresh);
                                 } else {
                                    String newVersion = rec.getPropertyAsString(49);
                                    if (newVersion != null && newVersion.length() != 0) {
                                       String currentVersion = CodeModuleManager.getModuleVersion(handle);
                                       if (this.compareModuleVersions(newVersion, currentVersion) > 0) {
                                          this.addToRibbon(rec, configSr, refresh);
                                       }
                                    }
                                 }
                              }
                           } else if (configType != BrowserConfigRecord.INVALID_VALUE) {
                              this.addToRibbon(rec, configSr, refresh);
                           }
                        }

                        if (isDesktopMdsConfig(rec, configSr)) {
                           desktopMdsConfig = true;
                           if (refresh) {
                              this.updateDesktopMdsConfig(configSr, sr);
                           }
                        }
                     } else if (refresh && BrowserConfigRecord.getTransportServiceRecord(transportCid, transportUid) == null) {
                        this.removeFromRibbon(configSr);
                     }
                  }
               }

               if (desktopMdsTransport && !desktopMdsConfig) {
                  this.updateDesktopMdsConfig(null, sr);
               }
            }
         } else {
            BrowserConfigRecord rec = BrowserConfigRecord.getDecodedConfig(sr);
            if (rec == null) {
               return;
            }

            String transportCid = rec.getPropertyAsString(3);
            String transportUid = rec.getPropertyAsString(4);
            EventLogger.logEvent(
               1907089860548946979L,
               ((StringBuffer)(new Object("RMbc \nTCID: "))).append(transportCid).append(" TUID: ").append(transportUid).toString().getBytes(),
               5
            );
            ServiceRecord transportSr = BrowserConfigRecord.getTransportServiceRecord(transportCid, transportUid);
            if (transportSr == null) {
               if (refresh) {
                  this.removeFromRibbon(sr);
                  return;
               }
            } else {
               int configType = rec.getPropertyAsInt(12);
               if (configType == 0 && !WAPConnectionRegistry.isWAPInstalled()) {
                  EventLogger.logEvent(
                     1907089860548946979L,
                     ((StringBuffer)(new Object("RMwn \nTCID: "))).append(transportCid).append(" TUID: ").append(transportUid).toString().getBytes(),
                     5
                  );
                  return;
               }

               EventLogger.logEvent(
                  1907089860548946979L,
                  ((StringBuffer)(new Object("RMtf \nTCID: "))).append(transportCid).append(" TUID: ").append(transportUid).toString().getBytes(),
                  5
               );
               if (rec.isITEnabled()) {
                  if (configType == 6) {
                     String moduleName = rec.getPropertyAsString(28);
                     if (moduleName != null && moduleName.length() != 0) {
                        int handle = CodeModuleManager.getModuleHandle(moduleName);
                        if (handle == 0) {
                           this.addToRibbon(rec, sr, refresh);
                        } else {
                           String newVersion = rec.getPropertyAsString(49);
                           if (newVersion != null && newVersion.length() != 0) {
                              String currentVersion = CodeModuleManager.getModuleVersion(handle);
                              if (this.compareModuleVersions(newVersion, currentVersion) > 0) {
                                 this.addToRibbon(rec, sr, refresh);
                              }
                           }
                        }
                     }
                  } else if (configType != BrowserConfigRecord.INVALID_VALUE) {
                     this.addToRibbon(rec, sr, refresh);
                  }
               }

               if (isDesktopMdsConfig(rec, sr)) {
                  this.updateDesktopMdsConfig(sr, transportSr);
               }

               if (refresh) {
                  BrowserSession.refresh(sr.getUid());
                  return;
               }
            }
         }
      }
   }

   final void handleRemovedServiceRecord(ServiceRecord sr) {
      if (sr != null) {
         ServiceBook sb = ServiceBook.getSB();
         String cid = sr.getCid();
         if (StringUtilities.strEqualIgnoreCase(cid, BrowserConfigRecord.SERVICE_CID, 1701707776)) {
            BrowserConfigRecord rec = BrowserConfigRecord.getDecodedConfig(sr);
            if (rec == null) {
               return;
            }

            this.removeFromRibbon(sr);
            if (isDesktopMdsConfig(rec, sr)) {
               String transportCid = rec.getPropertyAsString(3);
               String transportUid = rec.getPropertyAsString(4);
               ServiceRecord transportSr = BrowserConfigRecord.getTransportServiceRecord(transportCid, transportUid);
               if (transportSr != null) {
                  this.updateDesktopMdsConfig(null, transportSr);
                  return;
               }
            }
         } else if (StringUtilities.strEqualIgnoreCase(cid, WAPServiceRecord.SERVICE_CID, 1701707776)
            || StringUtilities.strEqualIgnoreCase(cid, BrowserConfigRecord.IPPP_SERVICE_CID, 1701707776)
            || StringUtilities.strEqualIgnoreCase(cid, BrowserConfigRecord.TCP_SERVICE_CID, 1701707776)) {
            boolean desktopMdsTransport = isDesktopMdsTransport(sr);
            String uid = sr.getUid();
            ServiceRecord[] records = sb.findRecordsByCid(BrowserConfigRecord.SERVICE_CID);

            for (int i = 0; i < records.length; i++) {
               ServiceRecord configSr = records[i];
               BrowserConfigRecord rec = BrowserConfigRecord.getDecodedConfig(configSr);
               if (rec != null) {
                  String transportCid = rec.getPropertyAsString(3);
                  String transportUid = rec.getPropertyAsString(4);
                  if (transportCid != null
                     && transportUid != null
                     && StringUtilities.strEqualIgnoreCase(cid, transportCid, 1701707776)
                     && StringUtilities.strEqualIgnoreCase(uid, transportUid, 1701707776)) {
                     this.removeFromRibbon(configSr);
                     if (desktopMdsTransport && isDesktopMdsConfig(rec, configSr)) {
                        sb.removeRecord(configSr);
                     }
                  }
               }
            }
         }
      }
   }

   final boolean isBrowserAvailable() {
      return this._browserAvailable;
   }

   final void disableAutoStart() {
      synchronized (this._autoStartBrowserConfigs) {
         if (!this._autoStartBrowserConfigs.isEmpty()) {
            label32:
            try {
               ServiceRecord configSr = (ServiceRecord)this._autoStartBrowserConfigs.elementAt(0);
               BrowserConfigRecord browserConfig = BrowserConfigRecord.getDecodedConfig(configSr);
               if (browserConfig.getPropertyAsInt(20) == 1) {
                  browserConfig.setPropertyAsInt(20, 0);
                  byte[] appData = browserConfig.getEncodedData();
                  configSr.setApplicationData(appData);
                  ServiceBook.getSB().commit();
               }
            } finally {
               break label32;
            }

            this._autoStartBrowserConfigs.removeElementAt(0);
            this._currentAutoStartBrowserConfigUid = null;
         }
      }
   }

   final void autoStartIfRequired(boolean updateLastCoverage) {
      synchronized (this._autoStartBrowserConfigs) {
         if (!this._autoStartBrowserConfigs.isEmpty() && this._currentAutoStartBrowserConfigUid == null) {
            ServiceRecord autoStartRecord = (ServiceRecord)this._autoStartBrowserConfigs.elementAt(0);
            BrowserConfigRecord autoStartBrowserConfig = BrowserConfigRecord.getDecodedConfig(autoStartRecord);
            if (autoStartBrowserConfig != null && !this._browserImpl.isForeground()) {
               Object ticket = PersistentContent.getTicket();
               if (ticket == null) {
                  return;
               }

               if (updateLastCoverage) {
                  this._browserImpl.updateLastCoverage();
               }

               String transportCid = autoStartBrowserConfig.getPropertyAsString(3);
               if (this._browserImpl.isCoverageSufficient(transportCid)) {
                  this._currentAutoStartBrowserConfigUid = autoStartRecord.getUid();
                  this._browserImpl.activateBrowser(autoStartRecord.getUid());
               }

               ticket = null;
            }
         }
      }
   }

   final void updateDesktopMdsConfigs(boolean refresh) {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] records = sb.findRecordsByCid(BrowserConfigRecord.IPPP_SERVICE_CID);

      for (int i = 0; i < records.length; i++) {
         ServiceRecord transportSr = records[i];
         if (isDesktopMdsTransport(transportSr)) {
            String transportUid = transportSr.getUid();
            ServiceRecord configSr = sb.getRecordByUidAndCid(transportUid, BrowserConfigRecord.SERVICE_CID);
            if (configSr == null || refresh) {
               this.updateDesktopMdsConfig(configSr, transportSr);
            }
         }
      }
   }

   final void updateDesktopMdsConfig(ServiceRecord configSr, ServiceRecord transportSr) {
      if (isDesktopMdsTransport(transportSr)) {
         String transportUid = transportSr.getUid();
         String transportName = transportSr.getName();
         String homePage = ITPolicy.getString(17);
         if (homePage == null) {
            homePage = "http://mobile.blackberry.com/";
         }

         String ribbonTitle = ITPolicy.getString(30, 1);
         if (ribbonTitle == null) {
            ribbonTitle = "BlackBerry Browser";
         }

         int editable;
         if (ITPolicy.getBoolean(18, false)) {
            editable = 0;
         } else {
            editable = 2;
         }

         int type = 1;
         boolean javascriptEnabledDefault = BrowserConfigRecord.getDefaultPropertyAsBoolean(type, 38);
         boolean cssEnabledDefault = BrowserConfigRecord.getDefaultPropertyAsBoolean(type, 33);
         boolean htmlTablesEnabledDefault = BrowserConfigRecord.getDefaultPropertyAsBoolean(type, 36);
         boolean bsmEnabledDefault = BrowserConfigRecord.getDefaultPropertyAsBoolean(type, 43);
         boolean javascriptEnabled = ITPolicy.getBoolean(30, 4, javascriptEnabledDefault);
         boolean stylesheetsEnabled = ITPolicy.getBoolean(30, 5, cssEnabledDefault);
         boolean htmlTablesEnabled = ITPolicy.getBoolean(30, 6, htmlTablesEnabledDefault);
         boolean bsmEnabled = ITPolicy.getBoolean(30, 7, bsmEnabledDefault);
         String moreImagesUrl = ITPolicy.getString(30, 8);
         String moreTunesUrl = ITPolicy.getString(30, 10);
         String moreThemesUrl = ITPolicy.getString(30, 9);
         if (moreImagesUrl != null && moreImagesUrl.length() == 0) {
            moreImagesUrl = null;
         }

         if (moreTunesUrl != null && moreTunesUrl.length() == 0) {
            moreTunesUrl = null;
         }

         if (moreThemesUrl != null && moreThemesUrl.length() == 0) {
            moreThemesUrl = null;
         }

         boolean useSeparateIcon = ITPolicy.getBoolean(30, 12, false);
         String domains = ITPolicy.getString(30, 13);
         if (domains != null && domains.length() == 0) {
            domains = null;
         }

         try {
            if (configSr != null) {
               BrowserConfigRecord browserConfig = BrowserConfigRecord.getDecodedConfig(configSr.getUid(), configSr.getApplicationData());
               if (browserConfig == null) {
                  return;
               }

               boolean configChanged = false;
               if (!configSr.getName().equals(transportName)) {
                  configSr.setName(transportName);
                  configChanged = true;
               }

               if (!homePage.equals(browserConfig.getPropertyAsString(1))) {
                  browserConfig.setPropertyAsString(1, homePage);
                  configChanged = true;
               }

               if (!ribbonTitle.equals(browserConfig.getPropertyAsString(11))) {
                  browserConfig.setPropertyAsString(11, ribbonTitle);
                  configChanged = true;
               }

               if (editable != browserConfig.getPropertyAsInt(5)) {
                  browserConfig.setPropertyAsInt(5, editable);
                  configChanged = true;
               }

               if (browserConfig.getPropertyAsInt(12) != 1) {
                  browserConfig.setPropertyAsInt(12, 1);
                  configChanged = true;
               }

               if (javascriptEnabled != browserConfig.getPropertyAsBoolean(38)) {
                  browserConfig.setPropertyAsBoolean(38, javascriptEnabled);
                  configChanged = true;
               }

               if (stylesheetsEnabled != browserConfig.getPropertyAsBoolean(33)) {
                  browserConfig.setPropertyAsBoolean(33, stylesheetsEnabled);
                  configChanged = true;
               }

               if (htmlTablesEnabled != browserConfig.getPropertyAsBoolean(36)) {
                  browserConfig.setPropertyAsBoolean(36, htmlTablesEnabled);
                  configChanged = true;
               }

               if (bsmEnabled != browserConfig.getPropertyAsBoolean(43)) {
                  browserConfig.setPropertyAsBoolean(43, bsmEnabled);
                  configChanged = true;
               }

               String oldMoreImagesUrl = browserConfig.getPropertyAsString(46);
               if (moreImagesUrl != null && !moreImagesUrl.equals(oldMoreImagesUrl) || moreImagesUrl == null && oldMoreImagesUrl != null) {
                  browserConfig.setPropertyAsString(46, moreImagesUrl);
                  configChanged = true;
               }

               String oldMoreTunesUrl = browserConfig.getPropertyAsString(48);
               if (moreTunesUrl != null && !moreTunesUrl.equals(oldMoreTunesUrl) || moreTunesUrl == null && oldMoreTunesUrl != null) {
                  browserConfig.setPropertyAsString(48, moreTunesUrl);
                  configChanged = true;
               }

               String oldMoreThemesUrl = browserConfig.getPropertyAsString(47);
               if (moreThemesUrl != null && !moreThemesUrl.equals(oldMoreThemesUrl) || moreThemesUrl == null && oldMoreThemesUrl != null) {
                  browserConfig.setPropertyAsString(47, moreThemesUrl);
                  configChanged = true;
               }

               if (useSeparateIcon != browserConfig.getPropertyAsBoolean(55)) {
                  browserConfig.setPropertyAsBoolean(55, useSeparateIcon);
                  configChanged = true;
               }

               if (!StringUtilities.strEqual(domains, browserConfig.getPropertyAsString(56))) {
                  browserConfig.setPropertyAsString(56, domains);
                  configChanged = true;
               }

               if (configChanged) {
                  byte[] appData = browserConfig.getEncodedData();
                  configSr.setApplicationData(appData);
                  ServiceBook.getSB().commit();
                  BrowserDaemonRegistry.notifyBrowserConfigChangeListeners(true);
                  return;
               }
            } else {
               ServiceRecord sr = (ServiceRecord)(new Object());
               sr.setType(0);
               sr.setName(transportName);
               sr.setUid(transportUid);
               sr.setCid(BrowserConfigRecord.SERVICE_CID);
               sr.setDescription("RIM BlackBerry Browser Config");
               BrowserConfigRecord tempRecord = BrowserConfigRecord.getNewConfig(BrowserConfigRecord.IPPP_SERVICE_CID, transportUid, 1);
               tempRecord.setPropertyAsString(1, homePage);
               tempRecord.setPropertyAsInt(5, editable);
               tempRecord.setPropertyAsInt(10, 1);
               tempRecord.setPropertyAsString(11, ribbonTitle);
               tempRecord.setPropertyAsBoolean(33, stylesheetsEnabled);
               tempRecord.setPropertyAsBoolean(36, htmlTablesEnabled);
               tempRecord.setPropertyAsBoolean(38, javascriptEnabled);
               tempRecord.setPropertyAsBoolean(43, bsmEnabled);
               tempRecord.setPropertyAsString(46, moreImagesUrl);
               tempRecord.setPropertyAsString(47, moreThemesUrl);
               tempRecord.setPropertyAsString(48, moreTunesUrl);
               tempRecord.setPropertyAsBoolean(55, useSeparateIcon);
               tempRecord.setPropertyAsString(56, domains);
               sr.setApplicationData(tempRecord.getEncodedData());
               ServiceBook sb = ServiceBook.getSB();
               if (sb.addRecord(sr) != null) {
                  sb.commit();
                  BrowserDaemonRegistry.notifyBrowserConfigChangeListeners(true);
               }
            }
         } finally {
            return;
         }
      }
   }

   final void handleITPolicyChanged() {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] records = sb.findRecordsByCid(BrowserConfigRecord.SERVICE_CID);
      boolean validateChannels = false;
      boolean itPolicyWAPEnabledCurrentValue = ITPolicy.getBoolean(19, true);
      boolean itPolicyPrivateMDSEnabledCurrentValue = ITPolicy.getBoolean(2, true);
      boolean itPolicyPublicMDSEnabledCurrentValue = ITPolicy.getBoolean(30, 3, true);
      boolean itPolicyBWCEnabledCurrentValue = sb.isAllowedRecord(null, 0, null, "*", "CMIME");
      boolean itPolicyAppDownloadEnabledCurrentValue = ITPolicy.getBoolean(30, 14, true);

      for (int i = 0; i < records.length; i++) {
         ServiceRecord configSr = records[i];
         BrowserConfigRecord rec = BrowserConfigRecord.getDecodedConfig(configSr);
         if (rec != null) {
            int configType = rec.getPropertyAsInt(12);
            if (configType != 0 && configType != 7) {
               if (configType == 1) {
                  if (itPolicyPrivateMDSEnabledCurrentValue) {
                     this.handleAddedServiceRecord(configSr, true);
                     if (!this._lastItPolicyPrivateMDSEnabledValue) {
                        validateChannels = true;
                     }
                  } else {
                     this.removeFromRibbon(configSr);
                     if (GeneralProperty.getDefaultBrowserConfigServiceUID().equals(configSr.getUid())) {
                        GeneralProperty.setDefaultBrowserConfigServiceUID(null);
                     }

                     if (this._lastItPolicyPrivateMDSEnabledValue) {
                        validateChannels = true;
                     }
                  }
               } else if (configType != 4 && configType != 8) {
                  if (configType == 2) {
                     if (itPolicyBWCEnabledCurrentValue) {
                        this.handleAddedServiceRecord(configSr, true);
                        if (!this._lastItPolicyBWCEnabledValue) {
                           validateChannels = true;
                        }
                     } else {
                        this.removeFromRibbon(configSr);
                        if (GeneralProperty.getDefaultBrowserConfigServiceUID().equals(configSr.getUid())) {
                           GeneralProperty.setDefaultBrowserConfigServiceUID(null);
                        }

                        if (this._lastItPolicyBWCEnabledValue) {
                           validateChannels = true;
                        }
                     }
                  } else if (configType == 6) {
                     if (itPolicyAppDownloadEnabledCurrentValue) {
                        this.handleAddedServiceRecord(configSr, true);
                        if (!this._lastItPolicyAppDownloadEnabledValue) {
                           validateChannels = true;
                        }
                     } else {
                        this.removeFromRibbon(configSr);
                        if (GeneralProperty.getDefaultBrowserConfigServiceUID().equals(configSr.getUid())) {
                           GeneralProperty.setDefaultBrowserConfigServiceUID(null);
                        }

                        if (this._lastItPolicyAppDownloadEnabledValue) {
                           validateChannels = true;
                        }
                     }
                  }
               } else if (itPolicyPublicMDSEnabledCurrentValue) {
                  this.handleAddedServiceRecord(configSr, true);
                  if (!this._lastItPolicyPublicMDSEnabledValue) {
                     validateChannels = true;
                  }
               } else {
                  this.removeFromRibbon(configSr);
                  if (GeneralProperty.getDefaultBrowserConfigServiceUID().equals(configSr.getUid())) {
                     GeneralProperty.setDefaultBrowserConfigServiceUID(null);
                  }

                  if (this._lastItPolicyPublicMDSEnabledValue) {
                     validateChannels = true;
                  }
               }
            } else if (itPolicyWAPEnabledCurrentValue) {
               this.handleAddedServiceRecord(configSr, true);
               if (!this._lastItPolicyWAPEnabledValue) {
                  validateChannels = true;
               }
            } else {
               this.removeFromRibbon(configSr);
               if (GeneralProperty.getDefaultBrowserConfigServiceUID().equals(configSr.getUid())) {
                  GeneralProperty.setDefaultBrowserConfigServiceUID(null);
               }

               if (this._lastItPolicyWAPEnabledValue) {
                  validateChannels = true;
               }
            }
         }
      }

      this._lastItPolicyWAPEnabledValue = itPolicyWAPEnabledCurrentValue;
      this._lastItPolicyPrivateMDSEnabledValue = itPolicyPrivateMDSEnabledCurrentValue;
      this._lastItPolicyPublicMDSEnabledValue = itPolicyPublicMDSEnabledCurrentValue;
      this._lastItPolicyBWCEnabledValue = itPolicyBWCEnabledCurrentValue;
      this._lastItPolicyAppDownloadEnabledValue = itPolicyAppDownloadEnabledCurrentValue;
      if (validateChannels) {
         EventLogger.logEvent(1907089860548946979L, 1128822369);
         ((Thread)(new Object(new Channels()))).start();
      }

      this._browserAvailable = BrowserConfigRecord.getValidBrowserConfigRecords().length > 0;
      this.togglePatternMatchers();
   }

   final void moduleAdded(int moduleHandle) {
      ServiceRecord[] configs = ServiceBook.getSB().findRecordsByCid(BrowserConfigRecord.SERVICE_CID);

      for (int i = configs.length - 1; i >= 0; i--) {
         ServiceRecord configSr = configs[i];
         BrowserConfigRecord rec = BrowserConfigRecord.getDecodedConfig(configSr);
         if (rec != null
            && rec.getPropertyAsInt(12) == 6
            && StringUtilities.strEqual(rec.getPropertyAsString(28), CodeModuleManager.getModuleName(moduleHandle))) {
            String requiredVersion = rec.getPropertyAsString(49);
            if (requiredVersion != null && requiredVersion.length() != 0) {
               String currentVersion = CodeModuleManager.getModuleVersion(moduleHandle);
               if (this.compareModuleVersions(requiredVersion, currentVersion) <= 0) {
                  this.removeFromRibbon(configSr);
               }
            } else {
               this.removeFromRibbon(configSr);
            }
         }
      }
   }

   final void moduleDeleted() {
      ServiceRecord[] configs = ServiceBook.getSB().findRecordsByCid(BrowserConfigRecord.SERVICE_CID);

      for (int i = configs.length - 1; i >= 0; i--) {
         ServiceRecord configSr = configs[i];
         BrowserConfigRecord rec = BrowserConfigRecord.getDecodedConfig(configSr);
         if (rec != null && rec.getPropertyAsInt(12) == 6) {
            this.handleAddedServiceRecord(configSr, false);
         }
      }
   }

   @Override
   public final void updateProvisioningData(IntIntHashtable chunks, DataBuffer db) {
      if (chunks.isEmpty()) {
         _provMoreImagesUrl = null;
         _provMoreThemesUrl = null;
         _provMoreTunesUrl = null;
         checkDownloadVerbRegistration(null, -2145255983166432762L, null, false, true, false);
         checkDownloadVerbRegistration(null, -4064892972611285119L, null, false, true, false);
         checkDownloadVerbRegistration(null, -201905085362485851L, null, false, true, false);
      } else {
         this.registerProvisioningDownloadVerbs(4, chunks, db);
         this.registerProvisioningDownloadVerbs(5, chunks, db);
         this.registerProvisioningDownloadVerbs(6, chunks, db);
      }
   }

   private final void registerProvisioningDownloadVerbs(int type, IntIntHashtable chunks, DataBuffer db) {
      if (chunks.containsKey(type)) {
         db.setPosition(chunks.get(type));
         int length = db.readCompressedInt();
         byte[] chars = new byte[length];

         for (int i = 0; i < length; i++) {
            chars[i] = db.readByte();
         }

         String url = (String)(new Object(chars));
         long registrationType = 0;
         switch (type) {
            case 3:
               break;
            case 4:
            default:
               _provMoreImagesUrl = url;
               registrationType = -2145255983166432762L;
               break;
            case 5:
               _provMoreTunesUrl = url;
               registrationType = -201905085362485851L;
               break;
            case 6:
               _provMoreThemesUrl = url;
               registrationType = -4064892972611285119L;
         }

         checkDownloadVerbRegistration(null, registrationType, url, true, true, true);
      }
   }

   public static final void checkDownloadVerbRegistration(
      BrowserConfigRecord rec, long registrationType, String url, boolean validSr, boolean provisioning, boolean refresh
   ) {
      VerbRepository verbRepository = VerbRepository.getVerbRepository(8855791131633157813L);
      if (verbRepository != null) {
         if (validSr && (url == null || url.length() == 0)) {
            if (!refresh) {
               return;
            }

            validSr = false;
         }

         if (!validSr) {
            Verb[] verbs = verbRepository.getVerbs(registrationType);
            DownloadContentVerb downloadContentVerb = null;
            if (verbs != null && verbs.length > 0) {
               Verb var23 = verbs[0];
               if (verbs[0] instanceof DownloadContentVerb) {
                  downloadContentVerb = (DownloadContentVerb)var23;
               }
            }

            if (downloadContentVerb != null) {
               boolean deregistered = false;
               BrowserConfigRecord verbConfig = downloadContentVerb.getBrowserConfigRecord();
               String transportCid = rec != null ? rec.getPropertyAsString(3) : null;
               String transportUid = rec != null ? rec.getPropertyAsString(4) : null;
               String cid = verbConfig != null ? verbConfig.getPropertyAsString(3) : null;
               String uid = verbConfig != null ? verbConfig.getPropertyAsString(4) : null;
               if (provisioning && downloadContentVerb.isProvisioningSR()
                  || transportCid != null
                     && transportUid != null
                     && cid != null
                     && uid != null
                     && StringUtilities.strEqualIgnoreCase(cid, transportCid, 1701707776)
                     && StringUtilities.strEqualIgnoreCase(uid, transportUid, 1701707776)) {
                  verbRepository.deregister(downloadContentVerb, downloadContentVerb.getType());
                  deregistered = true;
               }

               if (deregistered) {
                  pickContentDownloadVerbFromSB(registrationType);
               }
            }
         } else {
            Verb[] verbs = verbRepository.getVerbs(registrationType);
            DownloadContentVerb downloadContentVerb = null;
            if (verbs != null && verbs.length > 0) {
               Verb var10000 = verbs[0];
               if (verbs[0] instanceof DownloadContentVerb) {
                  downloadContentVerb = (DownloadContentVerb)var10000;
                  if (provisioning) {
                     if (!downloadContentVerb.isProvisioningSR()) {
                        return;
                     }
                  } else {
                     int newType = Integer.MIN_VALUE;
                     if (rec != null) {
                        newType = rec.getPropertyAsInt(12);
                     }

                     BrowserConfigRecord browserConfigRecord = downloadContentVerb.getBrowserConfigRecord();
                     if (!downloadContentVerb.isProvisioningSR() && browserConfigRecord != null) {
                        int type = browserConfigRecord.getPropertyAsInt(12);
                        if (compareBrowserConfigsForDownloadVerbs(newType, type) < 0) {
                           return;
                        }
                     }
                  }

                  verbRepository.deregister(downloadContentVerb, downloadContentVerb.getType());
               }
            }

            DownloadContentVerb verb = new DownloadContentVerb(rec, url, registrationType, provisioning);
            verbRepository.register(verb, verb.getType());
         }
      }
   }

   private static final void pickContentDownloadVerbFromSB(long registrationType) {
      VerbRepository verbRepository = VerbRepository.getVerbRepository(8855791131633157813L);
      if (verbRepository != null) {
         int encodedType = registrationType == -2145255983166432762L
            ? 46
            : (registrationType == -201905085362485851L ? 48 : (registrationType == -4064892972611285119L ? 47 : 0));
         BrowserConfigRecord[] records = BrowserConfigRecord.getValidBrowserConfigRecords();
         BrowserConfigRecord newRecord = null;
         int newType = Integer.MIN_VALUE;

         for (int j = records.length - 1; j >= 0; j--) {
            String url = records[j].getPropertyAsString(encodedType);
            if (url != null && url.length() != 0) {
               if (newRecord == null) {
                  newRecord = records[j];
                  newType = newRecord.getPropertyAsInt(12);
               } else {
                  int currentType = records[j].getPropertyAsInt(12);
                  if (compareBrowserConfigsForDownloadVerbs(newType, currentType) < 0) {
                     newRecord = records[j];
                     newType = currentType;
                  }
               }

               if (newType == 1) {
                  break;
               }
            }
         }

         if (newRecord != null) {
            DownloadContentVerb newVerb = new DownloadContentVerb(newRecord, null, registrationType, false);
            verbRepository.register(newVerb, newVerb.getType());
         } else {
            String url = getProvisioningContentUrl(registrationType);
            if (url != null) {
               DownloadContentVerb newVerb = new DownloadContentVerb(null, url, registrationType, true);
               verbRepository.register(newVerb, newVerb.getType());
            }
         }
      }
   }

   private static final int compareBrowserConfigsForDownloadVerbs(int type1, int type2) {
      if (type1 == type2) {
         return 0;
      } else if (type1 == 1) {
         return 1;
      } else if (type2 == 1) {
         return -1;
      } else if (type1 == 4) {
         return 1;
      } else if (type2 == 4) {
         return -1;
      } else if (type1 == 0) {
         return 1;
      } else {
         return type2 == 0 ? -1 : 0;
      }
   }

   private static final String getProvisioningContentUrl(long type) {
      if (type == -2145255983166432762L) {
         return _provMoreImagesUrl;
      } else if (type == -4064892972611285119L) {
         return _provMoreThemesUrl;
      } else {
         return type == -201905085362485851L ? _provMoreTunesUrl : null;
      }
   }

   private final void checkAutoStart(BrowserConfigRecord rec, ServiceRecord configSr, boolean validSr) {
      if (rec == null) {
         rec = BrowserConfigRecord.getDecodedConfig(configSr);
      }

      synchronized (this._autoStartBrowserConfigs) {
         if (rec != null && validSr && rec.getPropertyAsInt(20) == 1) {
            if (!this._autoStartBrowserConfigs.contains(configSr)) {
               this._autoStartBrowserConfigs.addElement(configSr);
               if (this._autoStartBrowserConfigs.size() == 1) {
                  this.autoStartIfRequired(true);
               }
            }
         } else if (this._autoStartBrowserConfigs.contains(configSr)) {
            this._autoStartBrowserConfigs.removeElement(configSr);
            if (StringUtilities.strEqualIgnoreCase(configSr.getUid(), this._currentAutoStartBrowserConfigUid, 1701707776)) {
               this._currentAutoStartBrowserConfigUid = null;
            }

            if (!this._autoStartBrowserConfigs.isEmpty()) {
               this.autoStartIfRequired(true);
            }
         }
      }
   }

   private final void checkDefaultWAPBrowser(String srUID) {
      if (StringUtilities.strEqualIgnoreCase(GeneralProperty.getPreferredWapBrowserConfigServiceUID(), srUID, 1701707776)) {
         GeneralProperty.setDefaultWapBrowserConfigServiceUID(srUID);
         GeneralProperty.setCurrentProperty(32, true);
      } else {
         String wapUID = GeneralProperty.getDefaultWapBrowserConfigServiceUID();
         if (wapUID.length() == 0
            || StringUtilities.strEqualIgnoreCase(wapUID, srUID, 1701707776)
            || !GeneralProperty.getCurrentPropertyAsBoolean(32) && StringUtilities.compareToIgnoreCase(srUID, wapUID, 1701707776) < 0) {
            GeneralProperty.setDefaultWapBrowserConfigServiceUID(srUID);
         }
      }
   }

   private final void addToRibbon(BrowserConfigRecord browserRecord, ServiceRecord serviceRecord, boolean refresh) {
      EventLogger.logEvent(1907089860548946979L, ((StringBuffer)(new Object("RMar \nUID: "))).append(serviceRecord.getUid()).toString().getBytes(), 5);
      this.addToQueue(new RibbonManagerThread$RibbonEvent(this, 1, 0, refresh, serviceRecord));
      if (!this._browserAvailable) {
         this._browserAvailable = true;
         this.togglePatternMatchers();
      }

      this.checkAutoStart(browserRecord, serviceRecord, true);
      checkDownloadVerbs(browserRecord, serviceRecord, true, refresh);
   }

   static final boolean isDesktopMdsTransport(ServiceRecord transportSr) {
      if (StringUtilities.strEqualIgnoreCase(transportSr.getCid(), BrowserConfigRecord.IPPP_SERVICE_CID, 1701707776)) {
         if (transportSr.getSource() == 1 || transportSr.getSource() == 5) {
            return true;
         }

         if (transportSr.getSource() == 0 && transportSr.getUid().indexOf(32) == -1) {
            return true;
         }
      }

      return false;
   }

   static final boolean isDesktopMdsConfig(BrowserConfigRecord rec, ServiceRecord configSr) {
      return StringUtilities.strEqualIgnoreCase(configSr.getCid(), BrowserConfigRecord.SERVICE_CID, 1701707776)
         && configSr.getSource() == 3
         && StringUtilities.strEqualIgnoreCase(rec.getPropertyAsString(3), BrowserConfigRecord.IPPP_SERVICE_CID, 1701707776)
         && StringUtilities.strEqualIgnoreCase(rec.getPropertyAsString(4), configSr.getUid(), 1701707776);
   }

   @Override
   protected final void runItem(Object obj) {
      if (obj instanceof RibbonManagerThread$RibbonEvent) {
         synchronized (this._processingObject) {
            if (!this._processing) {
               label35:
               try {
                  this._processingObject.wait();
               } finally {
                  break label35;
               }
            }
         }

         ((RibbonManagerThread$RibbonEvent)obj).run();
      }
   }

   public static final boolean browserOwnsConvenienceKey(int key) {
      ConvenienceKeyOptionsProvider convKeyProvider = ConvenienceKeyOptionsProvider.getInstance();
      String keyOwner = null;
      if (convKeyProvider != null) {
         switch (key) {
            case 19:
               keyOwner = convKeyProvider.getConvenienceKey1Owner();
               break;
            case 21:
               keyOwner = convKeyProvider.getConvenienceKey2Owner();
               break;
            default:
               return false;
         }
      }

      return keyOwner == null ? key == 19 : keyOwner.startsWith("net_rim_bb_browser_daemon");
   }

   private final int compareModuleVersions(String version1, String version2) {
      if (StringUtilities.strEqual(version1, version2)) {
         return 0;
      }

      StringTokenizer tokens1 = (StringTokenizer)(new Object(version1, '.'));
      StringTokenizer tokens2 = (StringTokenizer)(new Object(version2, '.'));
      int count = Math.max(tokens1.countTokens(), tokens2.countTokens());
      int[] components1 = new int[count];
      int[] components2 = new int[count];

      for (int i = 0; tokens1.hasMoreTokens(); i++) {
         try {
            components1[i] = Integer.parseInt(tokens1.nextToken());
         } finally {
            break;
         }
      }

      for (int i = 0; tokens2.hasMoreTokens(); i++) {
         try {
            components2[i] = Integer.parseInt(tokens2.nextToken());
         } finally {
            break;
         }
      }

      for (int i = 0; i < count; i++) {
         if (components1[i] < components2[i]) {
            return -1;
         }

         if (components1[i] > components2[i]) {
            return 1;
         }
      }

      return 0;
   }

   public static final void setHotkey(String uid, char hotkey) {
      RibbonLauncher ribbon = RibbonLauncher.getInstance();
      if (ribbon != null) {
         ServiceBook sb = ServiceBook.getSB();
         ServiceRecord record = sb.getRecordByUidAndCid(uid, BrowserConfigRecord.SERVICE_CID);
         if (record != null) {
            int configId = record.getId();
            ApplicationEntryPoint point = null;
            point = (ApplicationEntryPoint)ribbon.getRegisteredAction(((StringBuffer)(new Object("net_rim_bb_browser_daemon"))).append(configId).toString());
            if (point != null) {
               point.set(8, CharacterUtilities.toLowerCase(hotkey, 1701707776));
            }
         }
      }
   }

   private static final void checkDownloadVerbs(BrowserConfigRecord rec, ServiceRecord configSr, boolean validSr, boolean refresh) {
      if (rec == null) {
         rec = BrowserConfigRecord.getDecodedConfig(configSr);
      }

      String url = rec.getPropertyAsString(46);
      checkDownloadVerbRegistration(rec, -2145255983166432762L, url, validSr, false, refresh);
      url = rec.getPropertyAsString(47);
      checkDownloadVerbRegistration(rec, -4064892972611285119L, url, validSr, false, refresh);
      url = rec.getPropertyAsString(48);
      checkDownloadVerbRegistration(rec, -201905085362485851L, url, validSr, false, refresh);
   }

   static final void deleteBrowserServiceRecords() {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] records = sb.findRecordsByType(0);

      for (int i = 0; i < records.length; i++) {
         ServiceRecord sr = records[i];
         String cid = sr.getCid();
         if (StringUtilities.strEqualIgnoreCase(cid, BrowserConfigRecord.SERVICE_CID, 1701707776)) {
            BrowserConfigRecord rec = BrowserConfigRecord.getDecodedConfig(sr);
            if (rec == null || !isDesktopMdsConfig(rec, sr)) {
               sb.removeRecord(sr);
            }
         } else if (StringUtilities.strEqualIgnoreCase(cid, BrowserConfigRecord.IPPP_SERVICE_CID, 1701707776)) {
            if (!isDesktopMdsTransport(sr)) {
               sb.removeRecord(sr);
            }
         } else if (StringUtilities.strEqualIgnoreCase(cid, BrowserConfigRecord.WAP_SERVICE_CID, 1701707776)
            || StringUtilities.strEqualIgnoreCase(cid, BrowserConfigRecord.TCP_SERVICE_CID, 1701707776)
            || StringUtilities.strEqualIgnoreCase(cid, "WAPPushConfig", 1701707776)) {
            sb.removeRecord(sr);
         }
      }
   }

   static final void sendRegistrationRequest() {
      SBHelper.getInstance().injectServiceBooks();
      HRUtils.getThunks().sendRegistrationRequest();
   }

   RibbonManagerThread(BrowserImpl browserImpl) {
      this._browserImpl = browserImpl;
      this._autoStartBrowserConfigs = (Vector)(new Object(0, 1));
      this._lastItPolicyWAPEnabledValue = ITPolicy.getBoolean(19, true);
      this._lastItPolicyPrivateMDSEnabledValue = ITPolicy.getBoolean(2, true);
      this._lastItPolicyPublicMDSEnabledValue = ITPolicy.getBoolean(30, 3, true);
      this._lastItPolicyBWCEnabledValue = ServiceBook.getSB().isAllowedRecord(null, 0, null, "*", "CMIME");
      this._lastItPolicyAppDownloadEnabledValue = ITPolicy.getBoolean(30, 14, true);
      ProvisioningService provisioningService = ProvisioningService.getInstance();

      try {
         provisioningService.addHandler(this);
      } finally {
         return;
      }
   }

   private final void togglePatternMatchers() {
      boolean useThirdPartyBrowser = false;
      StringPatternContainer c = StringPatternRepository$Internal.getStringPatterns();

      for (int i = c.size() - 1; i >= 0; i--) {
         Object pattern = c.getAt(i);
         if (!(pattern instanceof HTTPAddressStringPattern)) {
            if (pattern instanceof DTMFStringPattern) {
               ((DTMFStringPattern)pattern).setEnabled(this._browserAvailable);
            }
         } else {
            ((HTTPAddressStringPattern)pattern).setEnabled(this._browserAvailable || useThirdPartyBrowser);
         }
      }
   }

   private static final Bitmap loadBitmap(String type) {
      if (SECURE_WAP_ICON.equals(type)) {
         if (SECURE_WAP_BITMAP == null) {
            SECURE_WAP_BITMAP = Bitmap.getBitmapResource(type);
         }

         return SECURE_WAP_BITMAP;
      } else if (INSECURE_WAP_ICON.equals(type)) {
         if (INSECURE_WAP_BITMAP == null) {
            INSECURE_WAP_BITMAP = Bitmap.getBitmapResource(type);
         }

         return INSECURE_WAP_BITMAP;
      } else if (PRIVATE_MDS_ICON.equals(type)) {
         if (PRIVATE_MDS_BITMAP == null) {
            PRIVATE_MDS_BITMAP = Bitmap.getBitmapResource(type);
         }

         return PRIVATE_MDS_BITMAP;
      } else if (PUBLIC_MDS_ICON.equals(type)) {
         if (PUBLIC_MDS_BITMAP == null) {
            PUBLIC_MDS_BITMAP = Bitmap.getBitmapResource(type);
         }

         return PUBLIC_MDS_BITMAP;
      } else if (BWC_ICON.equals(type)) {
         if (BWC_BITMAP == null) {
            BWC_BITMAP = Bitmap.getBitmapResource(type);
         }

         return BWC_BITMAP;
      } else if (TEAM_ON_ICON.equals(type)) {
         if (TEAM_ON_BITMAP == null) {
            TEAM_ON_BITMAP = Bitmap.getBitmapResource(type);
         }

         return TEAM_ON_BITMAP;
      } else if (APP_DOWNLOAD_ICON.equals(type)) {
         if (APP_DOWNLOAD_BITMAP == null) {
            APP_DOWNLOAD_BITMAP = Bitmap.getBitmapResource(type);
         }

         return APP_DOWNLOAD_BITMAP;
      } else {
         return null;
      }
   }
}
