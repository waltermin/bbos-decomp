package net.rim.device.apps.internal.browser.model;

import java.util.Vector;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ActiveFieldCookie;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.URLProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.api.utility.framework.ControllerUtilities;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.stack.FormData;
import net.rim.device.apps.internal.browser.verbs.HttpAddressLinkVerb;
import net.rim.device.apps.internal.browser.verbs.ShowUrlVerb;
import net.rim.vm.Array;
import org.w3c.dom.html2.HTMLElement;

public final class HTTPAddressModel implements RIMModel, VerbProvider, URLProvider, ActiveFieldCookie, Persistable {
   private String _address;
   private FormData _postData;
   private Object _linkElement;
   private Object _clickedElement;
   private Object _browserContent;
   private String _configUID;

   @Override
   public final String getURL() {
      return this._address;
   }

   @Override
   public final int getURLType() {
      return 1;
   }

   public final void setBrowserContent(BrowserContentImpl browserContent) {
      this._browserContent = browserContent;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      Verb defaultVerb = null;
      int verbCount = 1;
      boolean addShowUrlVerb = false;
      if (this._browserContent != null) {
         RenderingOptions renderingOptions = ((BrowserContentImpl)this._browserContent).getRenderingOptions();
         if (renderingOptions != null) {
            addShowUrlVerb = renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 34, false);
         }
      }

      if (addShowUrlVerb) {
         verbCount++;
      }

      Array.resize(verbs, verbCount);
      String onClick = null;
      String target = null;
      HTMLElement linkElement = null;
      if (this._linkElement instanceof HTMLElement) {
         linkElement = (HTMLElement)this._linkElement;
         if (linkElement.hasAttribute("onclick")) {
            onClick = linkElement.getAttribute("onclick");
         }

         if (linkElement.hasAttribute("target")) {
            target = linkElement.getAttribute("target");
         }
      }

      if (this._clickedElement instanceof HTMLElement) {
         HTMLElement clickedElement = (HTMLElement)this._clickedElement;
         if (clickedElement.hasAttribute("onclick")) {
            String innerOnClick = clickedElement.getAttribute("onclick");
            if (innerOnClick != null) {
               if (onClick != null) {
                  onClick = innerOnClick + '\n' + onClick;
               } else {
                  onClick = innerOnClick;
               }
            }
         }
      }

      if (target == null && this._browserContent instanceof BrowserContentImpl) {
         target = ((BrowserContentImpl)this._browserContent).getBaseTarget();
      }

      verbs[0] = new HttpAddressLinkVerb((BrowserContentImpl)this._browserContent, this.getURL(), target, this._postData, onClick, linkElement, this._configUID);
      if (addShowUrlVerb) {
         ShowUrlVerb verb = new ShowUrlVerb(null, this.getURL(), (BrowserContentImpl)this._browserContent, 1);
         verbs[1] = verb;
      }

      if (ContextObject.getFlag(context, 2)) {
         defaultVerb = verbs[0];
      }

      return defaultVerb;
   }

   @Override
   public final boolean invokeApplicationKeyVerb() {
      return ControllerUtilities.invokeApplicationKeyVerb(this);
   }

   @Override
   public final MenuItem getFocusVerbs(CookieProvider provider, Object context, Vector items) {
      return CookieProviderUtilities.getFocusVerbsForActiveField(this, context, items);
   }

   public HTTPAddressModel(Object initialData) {
      Vector addressAction = (Vector)ContextObject.get(initialData, 249);
      if (addressAction != null) {
         this._address = (String)addressAction.elementAt(0);
         this._postData = (FormData)addressAction.elementAt(1);
         int size = addressAction.size();
         if (size > 2) {
            this._linkElement = addressAction.elementAt(2);
         }

         if (size > 3) {
            this._clickedElement = addressAction.elementAt(3);
         }
      } else {
         String address = (String)ContextObject.get(initialData, 253);
         this._address = address == null ? "" : address;
      }

      label47:
      try {
         this._browserContent = (BrowserContentImpl)ContextObject.get(initialData, -442409970680484936L);
      } finally {
         break label47;
      }

      if (this._browserContent != null) {
         this._address = ((BrowserContentImpl)this._browserContent).resolveUrl(this._address);
      } else {
         this._address = URI.getAbsoluteURL(this._address, null);
      }

      this._configUID = (String)ContextObject.get(initialData, 867508017068302662L);
   }
}
