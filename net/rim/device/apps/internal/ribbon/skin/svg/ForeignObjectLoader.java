package net.rim.device.apps.internal.ribbon.skin.svg;

import java.util.Hashtable;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.ImageProviderRibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.apps.api.ribbon.SimpleRibbonComponent;
import net.rim.device.apps.api.ribbon.TextProviderRibbonComponent;
import net.rim.device.apps.internal.ribbon.skin.svg.manager.SkinManager;
import net.rim.plazmic.internal.mediaengine.ResourceContext;
import net.rim.plazmic.internal.mediaengine.ResourceProvider;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ViewportNodeImpl;

public class ForeignObjectLoader implements ResourceProvider {
   private Hashtable _parameters = (Hashtable)(new Object());
   private ComponentLinkForeignObject[] _links = new ComponentLinkForeignObject[0];
   Object _context;
   CustomFocusOrder _customFocusOrder;
   private RibbonComponent$RibbonComponentChangeListener _changeListener;

   public void setChangeListener(RibbonComponent$RibbonComponentChangeListener changeListener) {
      this._changeListener = changeListener;
   }

   private Object loadComponent(String type, String data, ResourceContext context) {
      String name = type.substring(type.indexOf(47) + 1);
      if (name.equals("SkinManager")) {
         this.loadSkinManager(data, context);
      } else if (name.equals("CustomFocusOrder")) {
         this.loadCustomFocusOrder(data, context);
      }

      return this.loadComponent(data, context);
   }

   private void loadCustomFocusOrder(String xml, ResourceContext context) {
      ModelInteractorImpl modelInteractor = (ModelInteractorImpl)context.get("Media");
      this._customFocusOrder = new CustomFocusOrder(modelInteractor);
      this._customFocusOrder.parseXml(xml);
   }

   private void loadSkinManager(String xml, ResourceContext context) {
      if (this._context instanceof MediaLayout) {
         MediaLayout mediaLayout = (MediaLayout)this._context;
         FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(3856995967469138522L);
         Factory factory = repos.getFactory(xml.trim());
         if (factory != null) {
            SkinManager skinManager = (SkinManager)factory.createInstance(null);
            mediaLayout.setSkinManager(skinManager);
         }
      }
   }

   private Object loadComponent(String url, ResourceContext context) {
      int start = url.indexOf(47) + 1;
      int query = url.indexOf(63);
      String name;
      if (query == -1) {
         name = url.substring(start);
      } else {
         name = url.substring(start, query);
      }

      if (name.equals("DayNight")) {
         if (query == -1) {
            return null;
         }

         ModelInteractorImpl model = (ModelInteractorImpl)context.get("Media");
         return DayNightCurveForeignObjectFactory.getFactory().createInstance(model, url.substring(query + 1));
      } else {
         if (name.equals("AlarmIndicator") && CodeModuleManager.getModuleHandle("net_rim_bb_alarm_app") != 0) {
            ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
            appReg.waitFor(-7118281301835656932L);
         }

         Factory factory = null;
         if (this._context != null && this._context instanceof Object) {
            FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
            factory = repos.getFactory(name);
         }

         if (factory == null) {
            FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
            factory = repos.getFactory(name);
         }

         if (factory == null) {
            return null;
         }

         RibbonComponent component = (RibbonComponent)factory.createInstance(null);
         this.initializeComponent(component, query == -1 ? null : url.substring(query + 1), context);
         String target = (String)this._parameters.get("target");
         ModelInteractorImpl mi = (ModelInteractorImpl)context.get("Media");
         if (target != null && mi != null) {
            if (component instanceof Object) {
               ImageRibbonComponentLink newComponent = new ImageRibbonComponentLink((ImageProviderRibbonComponent)component, mi, target);
               Arrays.add(this._links, newComponent);
               newComponent.setChangeListener(this._changeListener);
               return newComponent;
            }

            if (component instanceof Object) {
               TextRibbonComponentLink newComponent = new TextRibbonComponentLink((TextProviderRibbonComponent)component, mi, target);
               Arrays.add(this._links, newComponent);
               newComponent.setChangeListener(this._changeListener);
               return newComponent;
            }
         }

         SimpleRibbonComponentForeignObject componentToReturn = null;
         if (component instanceof Object) {
            componentToReturn = new SimpleRibbonComponentForeignObject((SimpleRibbonComponent)component);
            componentToReturn.setChangeListener(this._changeListener);
         }

         return componentToReturn;
      }
   }

   void resolveIds() {
      int count = this._links.length;

      for (int i = 0; i < count; i++) {
         this._links[i].resolveId();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void initializeComponent(RibbonComponent component, String query, ResourceContext context) {
      int height = -1;
      int width = -1;
      Util.fillParameters(query, this._parameters);
      ModelInteractorImpl model = (ModelInteractorImpl)context.get("Media");
      if (model != null) {
         this._parameters.put("Media", model);
      }

      String value = (String)this._parameters.get("height");
      if (value != null) {
         boolean var15 = false /* VF: Semaphore variable */;

         label96:
         try {
            var15 = true;
            height = Integer.parseInt(value);
            var15 = false;
         } finally {
            if (var15) {
               System.out.println("Invalid number format parsing height in SVGRibbonScreen");
               break label96;
            }
         }
      }

      value = (String)this._parameters.get("width");
      if (value != null) {
         boolean var12 = false /* VF: Semaphore variable */;

         label91:
         try {
            var12 = true;
            width = Integer.parseInt(value);
            var12 = false;
         } finally {
            if (var12) {
               System.out.println("Invalid number format parsing width in SVGRibbonScreen");
               break label91;
            }
         }
      }

      if (model != null) {
         Integer iHandle = (Integer)context.get("Handle");
         if (iHandle != null) {
            int handle = iHandle;
            if (width == -1) {
               width = Fixed32.toInt(ViewportNodeImpl.getWidth(handle, model));
               this._parameters.put("width", Integer.toString(width));
            }

            if (height == -1) {
               height = Fixed32.toInt(ViewportNodeImpl.getHeight(handle, model));
               this._parameters.put("height", Integer.toString(height));
            }
         }
      }

      if (component instanceof Object) {
         RibbonComponentInitializer init = (RibbonComponentInitializer)component;
         init.initialize(this._parameters, this._context);
      }

      if (component instanceof Object) {
         SimpleRibbonComponent simpleComponent = (SimpleRibbonComponent)component;
         simpleComponent.setDimensionsAvailable(width, height);
      }
   }

   @Override
   public Object createResourceFromURI(String url, String suggestedType, ResourceContext context, Object referrer) {
      Object resource = null;
      if (url != null) {
         if (url.startsWith("x-object:")) {
            resource = this.loadComponent(url, context);
         } else if (suggestedType != null && suggestedType.equals("image/png")) {
            int extensionIdx = url.lastIndexOf(46);
            if (extensionIdx > 0) {
               resource = ThemeManager.getActiveTheme().getImage(url.substring(0, extensionIdx), true);
            }
         }
      }

      return resource;
   }

   @Override
   public Object createResource(String type, Object data, ResourceContext context, Object referer) {
      Object foreignObject = null;
      if (type != null && type.startsWith("x-object:") && context != null) {
         foreignObject = this.loadComponent(type, (String)data, context);
      }

      return foreignObject;
   }
}
