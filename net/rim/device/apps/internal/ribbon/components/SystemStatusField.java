package net.rim.device.apps.internal.ribbon.components;

import java.util.Hashtable;
import net.rim.device.api.collection.ReadableLongMap;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.TextGraphics;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.ribbon.StringRibbonComponent;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.props.PropsChangeEventSubscription;
import net.rim.device.apps.api.utility.props.PropsChangeListener;
import net.rim.device.apps.api.utility.props.StringProps;
import net.rim.device.apps.internal.ribbon.skin.svg.MediaLayout;
import net.rim.device.internal.system.RadioInternal;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.TextAttrNodeImpl;
import net.rim.plazmic.internal.mediaengine.ui.PME12GraphicsImpl;
import net.rim.vm.DebugSupport;

final class SystemStatusField extends StringRibbonComponent implements RibbonComponent$RibbonComponentChangeListener, PropsChangeListener {
   protected int _textHandle = -1;
   protected ModelInteractorImpl _model;
   private TextGraphics _textGraphics = new TextGraphics("BBMillbank", 10);
   private DrawTextParam _textParams = new DrawTextParam();
   private ResourceBundleFamily _rbf = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
   private ReadableLongMap _networkProps;
   private RibbonComponent$RibbonComponentChangeListener _listener;
   private StringProps _context;
   private boolean _hideNetworkInfo;
   private SystemStatusComponentFactory _factory;

   SystemStatusField(SystemStatusComponentFactory factory, ReadableLongMap networkProps) {
      this._factory = factory;
      this._networkProps = networkProps;
      this._hideNetworkInfo = DebugSupport.getenv("JvmHideNetworkInfo") != null;
   }

   @Override
   public final void applyTheme() {
   }

   @Override
   protected final String getDefaultTag() {
      return "system-status";
   }

   @Override
   public final String getText() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      boolean ribbonInitialized = ar.get(-863209076745204846L) != null;
      String[] multiONSData = null;
      String multiONSSuffix = null;
      boolean idleModeTextUsed = false;
      boolean cellbroadcastImmediateTextUsed = false;
      if (ribbonInitialized) {
         RibbonLauncher.getInstance().setLongIdleModeText(null);
         RibbonLauncher.getInstance().setLongImmediateCellBroadcastText(null);
      }

      if (this._factory._testValue != null) {
         return this._factory._testValue;
      }

      String defaultTitle = null;
      if (this._context != null) {
         String title = this._context.get(MediaLayout.TITLE_ID, null);
         if (title != null) {
            return title;
         }
      }

      ReadableLongMap networkProps = this._networkProps;
      String batteryMessage = (String)networkProps.get(-1986748551626928033L);
      String e911Lock = (String)networkProps.get(1812321293200299507L);
      Object activationRequired = networkProps.get(3170113883629495887L);
      int state = RadioInfo.getState();
      if (!(activationRequired instanceof Boolean) || !(Boolean)activationRequired) {
         String simStatus = (String)networkProps.get(-8960794396193289546L);
         String networkState = (String)networkProps.get(4822241500382547294L);
         String modemModeEnabled = (String)networkProps.get(2594167647522947521L);
         if (e911Lock != null) {
            defaultTitle = e911Lock;
         } else if (state == 4) {
            defaultTitle = this._rbf.getString(57);
         } else if (state == 8) {
            defaultTitle = this._rbf.getString(94);
         } else if (state == 1 && simStatus != null) {
            defaultTitle = simStatus;
         } else if (networkState != null) {
            defaultTitle = networkState;
         } else if (batteryMessage != null) {
            defaultTitle = batteryMessage;
         } else if (modemModeEnabled != null) {
            defaultTitle = modemModeEnabled;
         } else {
            int networkService = RadioInfo.getNetworkService();
            if ((networkService & 1) != 0 && (RadioInfo.getNetworkService() & 16384) == 0) {
               if (RadioInternal.getNetworkSelectionMode() == 3) {
                  defaultTitle = this._rbf.getString(72);
               } else {
                  byte[] suppressSOS = Branding.getData(17);
                  if (suppressSOS != null && suppressSOS.length > 0 && suppressSOS[0] != 0) {
                     defaultTitle = CommonResources.getString(9139);
                  }
               }
            } else if (!this._hideNetworkInfo) {
               String operatorName = null;
               Object operatorNameData = networkProps.get(-7219683504990287771L);
               if (!(operatorNameData instanceof String)) {
                  if (operatorNameData instanceof String[]) {
                     multiONSData = (String[])operatorNameData;
                     multiONSSuffix = "";
                  }
               } else {
                  operatorName = (String)operatorNameData;
               }

               defaultTitle = operatorName;
               String idleText = (String)networkProps.get(-7608742199570488450L);
               if (idleText != null && idleText.length() > 0) {
                  idleModeTextUsed = true;
                  if (defaultTitle != null && defaultTitle.length() > 0) {
                     defaultTitle = defaultTitle + " - " + idleText;
                  } else {
                     defaultTitle = idleText;
                  }

                  if (multiONSSuffix != null) {
                     multiONSSuffix = multiONSSuffix + idleText;
                  }
               }

               String immediateDisplay = (String)networkProps.get(6665563664396523075L);
               if (immediateDisplay != null && immediateDisplay.length() > 0) {
                  cellbroadcastImmediateTextUsed = true;
                  if (defaultTitle != null && defaultTitle.length() > 0) {
                     defaultTitle = defaultTitle + " - " + immediateDisplay;
                  } else {
                     defaultTitle = immediateDisplay;
                  }

                  if (multiONSSuffix != null) {
                     if (multiONSSuffix.length() > 0) {
                        multiONSSuffix = multiONSSuffix + " - ";
                     }

                     multiONSSuffix = multiONSSuffix + immediateDisplay;
                  }
               }
            }
         }
      } else if (e911Lock != null) {
         defaultTitle = e911Lock;
      } else if (state == 4) {
         defaultTitle = this._rbf.getString(57);
      } else if (batteryMessage != null) {
         defaultTitle = batteryMessage;
      } else {
         defaultTitle = this._rbf.getString(90);
      }

      String wifiText = (String)networkProps.get(-1839664706744174700L);
      if (wifiText != null && wifiText.length() > 0) {
         if (defaultTitle != null && defaultTitle.length() > 0) {
            defaultTitle = defaultTitle + " - " + wifiText;
         } else {
            defaultTitle = wifiText;
         }

         if (multiONSSuffix != null) {
            multiONSSuffix = multiONSSuffix + wifiText;
         }
      }

      boolean textGraphicsFilled = false;
      if (multiONSData != null || idleModeTextUsed) {
         if (this._textHandle != -1 && this._model != null) {
            int fontSize = TextAttrNodeImpl.getResolvedFontSize(this._textHandle, this._model);
            String fontFamily = TextAttrNodeImpl.getResolvedFontFamily(this._textHandle, this._model);
            int fontWeight = TextAttrNodeImpl.getResolvedFontWeight(this._textHandle, this._model);
            int fontStyle = TextAttrNodeImpl.getResolvedFontStyle(this._textHandle, this._model);
            fontStyle = PME12GraphicsImpl.getFontStyle(fontStyle);
            fontStyle |= PME12GraphicsImpl.getFontWeight(fontWeight);
            this._textGraphics.setTypefaceName(fontFamily);
            this._textGraphics.setHeightWithLeading(Fixed32.toInt(fontSize));
            this._textGraphics.setStyle(fontStyle);
            this._textGraphics.setAntialiasingMode(1);
            textGraphicsFilled = true;
         } else if (super._font != null) {
            this._textGraphics.setFontSpec(super._font);
            textGraphicsFilled = true;
         }

         if (multiONSData != null) {
            if (textGraphicsFilled) {
               int suffixAdvance = multiONSSuffix != null && multiONSSuffix.length() != 0
                  ? this._textGraphics.measureText(multiONSSuffix, 0, multiONSSuffix.length(), this._textParams, null)
                  : 0;
               defaultTitle = multiONSData[0];

               for (int i = 0; i < multiONSData.length; i++) {
                  String onsName = multiONSData[i];
                  int advance = onsName != null && onsName.length() != 0
                     ? this._textGraphics.measureText(onsName, 0, onsName.length(), this._textParams, null)
                     : 0;
                  if (advance + suffixAdvance < super._width) {
                     defaultTitle = onsName;
                     break;
                  }
               }
            } else {
               defaultTitle = multiONSData[0];
            }

            if (multiONSSuffix != null && multiONSSuffix.length() > 0) {
               defaultTitle = defaultTitle + " - " + multiONSSuffix;
            }
         }

         if (idleModeTextUsed) {
            int advance = Integer.MAX_VALUE;
            if (textGraphicsFilled) {
               advance = this._textGraphics.measureText(defaultTitle, 0, defaultTitle.length(), this._textParams, null);
            }

            if (advance >= super._width && ribbonInitialized) {
               String idleText = (String)this._networkProps.get(-7608742199570488450L);
               RibbonLauncher.getInstance().setLongIdleModeText(idleText);
            }
         }
      }

      if (cellbroadcastImmediateTextUsed && ribbonInitialized) {
         String cbText = (String)this._networkProps.get(6665563664396523075L);
         RibbonLauncher.getInstance().setLongImmediateCellBroadcastText(cbText);
      }

      return defaultTitle;
   }

   @Override
   public final void setTargetNode(int node) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void initialize(Hashtable params, Object context) {
      if (context instanceof StringProps) {
         this._context = (StringProps)context;
         if (context instanceof PropsChangeEventSubscription) {
            PropsChangeEventSubscription subscription = (PropsChangeEventSubscription)context;
            subscription.addPropsChangeListener(this);
         }
      }

      this._model = (ModelInteractorImpl)params.get("Media");
      super.initialize(params, context);
   }

   @Override
   public final void propChanged(long propID) {
      if (propID == MediaLayout.TITLE_ID) {
         this.ribbonComponentChanged(this);
      }
   }

   @Override
   public final synchronized void ribbonComponentChanged(RibbonComponent component) {
      RibbonComponent$RibbonComponentChangeListener listener = this._listener;
      if (listener != null) {
         listener.ribbonComponentChanged(this);
      }
   }

   @Override
   public final synchronized void setChangeListener(RibbonComponent$RibbonComponentChangeListener listener) {
      this._listener = listener;
   }
}
