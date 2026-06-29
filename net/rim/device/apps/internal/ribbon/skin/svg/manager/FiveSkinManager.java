package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.ribbon.EntryPointDescriptor;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.internal.ribbon.skin.svg.CustomFocusOrder;
import net.rim.device.apps.internal.ribbon.skin.svg.MediaLayout;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.NodeImpl;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;
import net.rim.plazmic.mediaengine.MediaPlayer;

public class FiveSkinManager extends SkinManager {
   private ModelInteractorImpl _modelInteractor;
   private FocusInteractor _focusInteractor;
   private MediaLayout _mediaLayout;
   private CustomFocusOrder _customFocusOrder;
   private static final String FIVE_MARKER = "TMO_Five_Theme";
   private static final String FIVE_PREFIX = "five_";
   private static final String[] FIVE_HOTSPOTS = new String[]{"five_hs1", "five_hs2", "five_hs3", "five_hs4", "five_hs5"};
   private static final String[] FIVE_ENTRIES = new String[]{
      "net_rim_tmo_five1", "net_rim_tmo_five3", "net_rim_tmo_five5", "net_rim_tmo_five4", "net_rim_tmo_five2"
   };

   @Override
   public void setServices(MediaServices services) {
      if (services != null) {
         this._focusInteractor = (FocusInteractor)services.getService("FocusInteractor");
         this._mediaLayout = (MediaLayout)((MediaPlayer)services.getService("MediaPlayer")).getUI();
      }
   }

   @Override
   public void setMedia(Object media) {
      this._modelInteractor = (ModelInteractorImpl)media;
      this._customFocusOrder = this._mediaLayout.getCustomFocusOrder();
   }

   @Override
   public void onVisibilityChange(boolean visible) {
      if (!visible) {
         this.five_FocusOnFirst();
      }
   }

   @Override
   public boolean keyDown(int keycode, int time) {
      if (Keypad.key(keycode) == 17) {
         int focus = this._focusInteractor.getItemInFocus();
         String id = NodeImpl.getId(focus, this._modelInteractor);
         if (id != null && id.startsWith("five_")) {
            int idx = Arrays.getIndex(FIVE_HOTSPOTS, id);
            if (idx != -1) {
               EntryPointDescriptor entry = RibbonLauncher.getInstance().getRegisteredAction(FIVE_ENTRIES[idx]);
               if (entry instanceof KeyListener) {
                  KeyListener listener = (KeyListener)entry;
                  return listener.keyDown(keycode, time);
               }
            }
         }
      } else if (Keypad.key(keycode) == 18) {
         boolean isFiveTheme = this._modelInteractor.getHandle("TMO_Five_Theme") != -1;
         if (isFiveTheme) {
            this.five_FocusOnFirst();
            return false;
         }
      }

      return false;
   }

   @Override
   public boolean navigationMovement(int dx, int dy, int status, int time) {
      boolean isTMOFiveTheme = this._modelInteractor.getHandle("TMO_Five_Theme") != -1;
      boolean carouselMove = false;
      if (isTMOFiveTheme && dx != 0) {
         dy = 0;
         int focus = this._focusInteractor.getItemInFocus();
         String id = NodeImpl.getId(focus, this._modelInteractor);
         if (id != null && id.startsWith("five_")) {
            carouselMove = true;
            int handle = this._modelInteractor.getHandle(id + "_focusout");
            this._modelInteractor.trigger(107, handle, null);
         }
      }

      boolean moved = this._customFocusOrder.navigationMovement(dx, dy);
      if (carouselMove) {
         int focus = this._focusInteractor.getItemInFocus();
         String id = NodeImpl.getId(focus, this._modelInteractor);
         int handle = this._modelInteractor.getHandle(id + "_focusin");
         if (handle != -1) {
            this._modelInteractor.trigger(107, handle, null);
         }

         if (!moved) {
            this.navigationMovement(0, 1, status, time);
            return true;
         }
      }

      return true;
   }

   private void five_FocusOnFirst() {
      if (this._focusInteractor != null) {
         int focus = this._focusInteractor.getItemInFocus();
         String id = NodeImpl.getId(focus, this._modelInteractor);
         if (id != null && !id.startsWith("five_")) {
            this._mediaLayout.navigationMovement(0, -1, 0, 0);
            focus = this._focusInteractor.getItemInFocus();
            id = NodeImpl.getId(focus, this._modelInteractor);
         }

         if (id != null && id.startsWith("five_")) {
            int idx = Arrays.getIndex(FIVE_HOTSPOTS, id);
            if (idx > 0) {
               if (idx <= 2) {
                  this._mediaLayout.navigationMovement(-idx, 0, 0, 0);
                  return;
               }

               this._mediaLayout.navigationMovement(5 - idx, 0, 0, 0);
            }
         }
      }
   }
}
