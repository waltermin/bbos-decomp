package net.rim.device.apps.internal.options;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.awt.im.repository.CustomDictionary;

public class CustomWordlistScreen {
   private CustomWordlistScreen$CustomDictScreen _customDictScreen;
   private ResourceBundleFamily _rbFamily;
   private int _rbClrLearningCacheKey;
   private static CustomDictionary _customDictionary;
   private static int _screenType;
   private static int _dataType;

   public static CustomDictionary getCustomDictionary() {
      if (_customDictionary == null) {
         _customDictionary = InputContext.getInstance().getCustomDictionary(_dataType);
      }

      return _customDictionary;
   }

   public static int getScreenType() {
      return _screenType;
   }

   public static int getDataType() {
      return _dataType;
   }

   public CustomWordlistScreen(ResourceBundleFamily rbFamily, int key, int screenType, int dataType) {
      this._rbFamily = rbFamily;
      this._rbClrLearningCacheKey = key;
      _screenType = screenType;
      _dataType = dataType;
   }

   public CustomWordlistScreen$CustomDictScreen createCustomDictScreen() {
      if (this._customDictScreen == null) {
         this._customDictScreen = new CustomWordlistScreen$CustomDictScreen(this, OptionsResources.getString(1491), null);
      }

      return this._customDictScreen;
   }

   public void openCustomDictScreen() {
      this.createCustomDictScreen().showScreen();
   }

   static CustomDictionary access$002(CustomDictionary x0) {
      _customDictionary = x0;
      return x0;
   }
}
