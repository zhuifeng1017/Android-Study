package com.xxx.appstore.common.util;

import com.xxx.appstore.R;
import com.xxx.appstore.Session;

public class ThemeManager {

   public static final int KEY_ACTION_BAR_BG = 15;
   public static final int KEY_ACTION_BAR_BTN_BG = 37;
   public static final int KEY_ACTION_BAR_CANCEL = 41;
   public static final int KEY_ACTION_BAR_DOWN = 28;
   public static final int KEY_ACTION_BAR_DOWNLOAD = 21;
   public static final int KEY_ACTION_BAR_DOWN_CHECKED = 29;
   public static final int KEY_ACTION_BAR_FONT = 17;
   public static final int KEY_ACTION_BAR_FONT_M = 18;
   public static final int KEY_ACTION_BAR_FONT_S = 36;
   public static final int KEY_ACTION_BAR_INSTALL = 23;
   public static final int KEY_ACTION_BAR_OPEN = 24;
   public static final int KEY_ACTION_BAR_OPERATION_BG = 16;
   public static final int KEY_ACTION_BAR_PENDING = 22;
   public static final int KEY_ACTION_BAR_SPLITTER = 19;
   public static final int KEY_ACTION_BAR_START = 44;
   public static final int KEY_ACTION_BAR_UNINSTALL = 25;
   public static final int KEY_ACTION_BAR_UP = 26;
   public static final int KEY_ACTION_BAR_UP_CHECKED = 27;
   public static final int KEY_FEED_BACK_BTN = 10;
   public static final int KEY_FEED_BACK_EDIT_TEXT = 9;
   public static final int KEY_HOME_TAB_APP_MANAGER = 5;
   public static final int KEY_HOME_TAB_BACKGROUND = 31;
   public static final int KEY_HOME_TAB_BREATH = 20;
   public static final int KEY_HOME_TAB_CATEGORY = 2;
   public static final int KEY_HOME_TAB_INDEX = 1;
   public static final int KEY_HOME_TAB_INDICATOR = 43;
   public static final int KEY_HOME_TAB_RANK = 4;
   public static final int KEY_HOME_TAB_RECOMMEND = 3;
   public static final int KEY_HOME_TAB_TEXT = 11;
   public static final int KEY_PROGRESSBAR = 40;
   public static final int KEY_TAB_BG = 14;
   public static final int KEY_TAB_L = 7;
   public static final int KEY_TAB_M = 6;
   public static final int KEY_TAB_R = 8;
   public static final int KEY_TAB_TEXT = 12;
   public static final int KEY_TOP_BAR_BG = 30;
   public static final int KEY_TOP_BAR_BTN_BG = 42;
   public static final int KEY_TOP_BAR_FOLLOW = 38;
   public static final int KEY_TOP_BAR_LOGO = 33;
   public static final int KEY_TOP_BAR_SEARCH = 32;
   public static final int KEY_TOP_BAR_SHARE = 35;
   public static final int KEY_TOP_BAR_SPLITTER = 34;
   public static final int KEY_TOP_TITLE_TEXT = 13;
   public static final int THEME_TYPE_DARK = 2131361833;
   public static final int THEME_TYPE_LIGHT = 2131361832;


   private static int getDarkResourceId(int var0) {
      int var1 = R.drawable.action_bar_operation_bg;
      switch(var0) {
      case 1:
         var1 = R.drawable.main_tab_index_selector;
         break;
      case 2:
         var1 = R.drawable.main_tab_category_selector;
         break;
      case 3:
    	 var1 = R.drawable.main_tab_rank_selector;         
         break;
      case 4:
    	  var1 = R.drawable.main_tab_recommend_selector;
         break;
      case 5:
         var1 = R.drawable.main_tab_app_manager_selector;
         break;
      case 6:
         var1 = R.drawable.tab_selector;
         break;
      case 7:
         var1 = R.drawable.tab_l_bg_selector;
         break;
      case 8:
         var1 = R.drawable.tab_r_bg_selector;
         break;
      case 9:
      case 10:
      case 39:
      default:
         var1 = 1;
         break;
      case 11:
         var1 = R.style.home_tab_text_style_dark;
         break;
      case 12:
         var1 = R.style.tab_text_style_dark;
         break;
      case 13:
         var1 = R.style.text_style_3e;
         break;
      case 14:
         var1 = R.drawable.tab_bg;
      case 15:
      case 16:
         break;
      case 17:
         var1 = R.style.text_style_3e;
         break;
      case 18:
         var1 = R.style.text_style_2e;
         break;
      case 19:
         var1 = R.drawable.action_bar_splitter;
         break;
      case 20:
 //        var1 = R.drawable.master_breath_drawable;
         break;
      case 21:
         var1 = R.drawable.action_bar_download;
         break;
      case 22:
         var1 = R.drawable.action_bar_pending;
         break;
      case 23:
         var1 = R.drawable.action_bar_install;
         break;
      case 24:
         var1 = R.drawable.action_bar_open;
         break;
      case 25:
         var1 = R.drawable.action_bar_uninstall;
         break;
      case 26:
         var1 = R.drawable.action_bar_up_normal;
         break;
      case 27:
         var1 = R.drawable.action_bar_up_checked;
         break;
      case 28:
         var1 = R.drawable.action_bar_down_normal;
         break;
      case 29:
         var1 = R.drawable.action_bar_down_checked;
         break;
      case 30:
         var1 = R.drawable.topbar_bg;
         break;
      case 31:
         var1 = R.drawable.home_tab_bg;
         break;
      case 32:
         var1 = R.drawable.topbar_btn_search;
         break;
      case 33:
         var1 = R.drawable.logo;
         break;
      case 34:
         var1 = R.drawable.topbar_navigation;
         break;
      case 35:
         var1 = R.drawable.topbar_btn_share;
         break;
      case 36:
         var1 = R.style.text_style_1e;
         break;
      case 37:
         var1 = R.drawable.action_bar_btn_bg;
         break;
      case 38:
 //        var1 = R.drawable.master_follow_selector;
         break;
      case 40:
         var1 = R.drawable.progress_horizontal;
         break;
      case 41:
         var1 = R.drawable.action_bar_cancel;
         break;
      case 42:
         var1 = R.drawable.topbar_btn_bg;
         break;
      case 43:
         var1 = R.drawable.main_tab_anim;
         break;
      case 44:
         var1 = R.drawable.action_bar_start;
      }

      return var1;
   }

   private static int getLightResourceId(int var0) {
      int var1 = R.drawable.action_bar_operation_bg_light;
      switch(var0) {
      case 1:
         var1 = R.drawable.main_tab_index_selector_light;
         break;
      case 2:
         var1 = R.drawable.main_tab_category_selector_light;
         break;
      case 3:
    	 var1 = R.drawable.main_tab_rank_selector_light;      
         break;
      case 4:
    	 var1 = R.drawable.main_tab_recommend_selector_light;
         break;
      case 5:
         var1 = R.drawable.main_tab_app_manager_selector_light;
         break;
      case 6:
         var1 = R.drawable.tab_selector_light;
         break;
      case 7:
         var1 = R.drawable.tab_l_bg_selector_light;
         break;
      case 8:
         var1 = R.drawable.tab_r_bg_selector_light;
         break;
      case 9:
      case 10:
      case 39:
      default:
         var1 = 0;
         break;
      case 11:
         var1 = R.style.home_tab_text_style_light;
         break;
      case 12:
         var1 = R.style.tab_text_style_light;
         break;
      case 13:
         var1 = R.style.app_text_style1;
         break;
      case 14:
         var1 = R.drawable.tab_bg_light;
      case 15:
      case 16:
         break;
      case 17:
         var1 = R.style.text_style_3b;
         break;
      case 18:
         var1 = R.style.text_style_2b;
         break;
      case 19:
         var1 = R.drawable.action_bar_splitter_light;
         break;
      case 20:
//         var1 = R.drawable.master_breath_drawable_light;
         break;
      case 21:
         var1 = R.drawable.action_bar_download_light;
         break;
      case 22:
         var1 = R.drawable.action_bar_pending_light;
         break;
      case 23:
         var1 = R.drawable.action_bar_install_light;
         break;
      case 24:
         var1 = R.drawable.action_bar_open_light;
         break;
      case 25:
         var1 = R.drawable.action_bar_uninstall_light;
         break;
      case 26:
         var1 = R.drawable.action_bar_up_normal_light;
         break;
      case 27:
         var1 = R.drawable.action_bar_up_checked_light;
         break;
      case 28:
         var1 = R.drawable.action_bar_down_normal_light;
         break;
      case 29:
         var1 = R.drawable.action_bar_down_checked_light;
         break;
      case 30:
         var1 = R.drawable.topbar_bg_light;
         break;
      case 31:
         var1 = R.drawable.home_tab_bg_light;
         break;
      case 32:
         var1 = R.drawable.topbar_btn_search_light;
         break;
      case 33:
         var1 = R.drawable.logo_light;
         break;
      case 34:
         var1 = R.drawable.topbar_navigation_light;
         break;
      case 35:
         var1 = R.drawable.topbar_btn_share_light;
         break;
      case 36:
         var1 = R.style.text_style_1b;
         break;
      case 37:
         var1 = R.drawable.action_bar_btn_bg;
         break;
      case 38:
//         var1 = R.drawable.master_follow_selector_light;
         break;
      case 40:
         var1 = R.drawable.progress_horizontal_light;
         break;
      case 41:
         var1 = R.drawable.action_bar_cancel_light;
         break;
      case 42:
         var1 = R.drawable.topbar_btn_bg_light;
         break;
      case 43:
         var1 = R.drawable.main_tab_anim_light;
         break;
      case 44:
         var1 = R.drawable.action_bar_start_light;
      }

      return var1;
   }

   public static int getResource(Session var0, int var1) {
      int var2;
      switch(var0.getTheme()) {
      case R.style.gfan_theme_light:
         var2 = getLightResourceId(var1);
         break;
      case R.style.gfan_theme_dark:
         var2 = getDarkResourceId(var1);
         break;
      default:
         var2 = -1;
      }

      return var2;
   }
}
