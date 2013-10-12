package com.mappn.gfan.common.util;

import com.mappn.gfan.Session;

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
      int var1 = 2130837532;
      switch(var0) {
      case 1:
         var1 = 2130837669;
         break;
      case 2:
         var1 = 2130837662;
         break;
      case 3:
         var1 = 2130837683;
         break;
      case 4:
         var1 = 2130837676;
         break;
      case 5:
         var1 = 2130837647;
         break;
      case 6:
         var1 = 2130837790;
         break;
      case 7:
         var1 = 2130837774;
         break;
      case 8:
         var1 = 2130837782;
         break;
      case 9:
      case 10:
      case 39:
      default:
         var1 = 1;
         break;
      case 11:
         var1 = 2131361815;
         break;
      case 12:
         var1 = 2131361818;
         break;
      case 13:
         var1 = 2131361805;
         break;
      case 14:
         var1 = 2130837772;
      case 15:
      case 16:
         break;
      case 17:
         var1 = 2131361805;
         break;
      case 18:
         var1 = 2131361801;
         break;
      case 19:
         var1 = 2130837536;
         break;
      case 20:
         var1 = 2130837710;
         break;
      case 21:
         var1 = 2130837526;
         break;
      case 22:
         var1 = 2130837534;
         break;
      case 23:
         var1 = 2130837528;
         break;
      case 24:
         var1 = 2130837530;
         break;
      case 25:
         var1 = 2130837540;
         break;
      case 26:
         var1 = 2130837544;
         break;
      case 27:
         var1 = 2130837542;
         break;
      case 28:
         var1 = 2130837524;
         break;
      case 29:
         var1 = 2130837522;
         break;
      case 30:
         var1 = 2130837800;
         break;
      case 31:
         var1 = 2130837606;
         break;
      case 32:
         var1 = 2130837806;
         break;
      case 33:
         var1 = 2130837641;
         break;
      case 34:
         var1 = 2130837810;
         break;
      case 35:
         var1 = 2130837808;
         break;
      case 36:
         var1 = 2131361796;
         break;
      case 37:
         var1 = 2130837515;
         break;
      case 38:
         var1 = 2130837712;
         break;
      case 40:
         var1 = 2130837745;
         break;
      case 41:
         var1 = 2130837520;
         break;
      case 42:
         var1 = 2130837802;
         break;
      case 43:
         var1 = 2130837644;
         break;
      case 44:
         var1 = 2130837538;
      }

      return var1;
   }

   private static int getLightResourceId(int var0) {
      int var1 = 2130837533;
      switch(var0) {
      case 1:
         var1 = 2130837670;
         break;
      case 2:
         var1 = 2130837663;
         break;
      case 3:
         var1 = 2130837684;
         break;
      case 4:
         var1 = 2130837677;
         break;
      case 5:
         var1 = 2130837648;
         break;
      case 6:
         var1 = 2130837791;
         break;
      case 7:
         var1 = 2130837775;
         break;
      case 8:
         var1 = 2130837783;
         break;
      case 9:
      case 10:
      case 39:
      default:
         var1 = 0;
         break;
      case 11:
         var1 = 2131361816;
         break;
      case 12:
         var1 = 2131361817;
         break;
      case 13:
         var1 = 2131361806;
         break;
      case 14:
         var1 = 2130837773;
      case 15:
      case 16:
         break;
      case 17:
         var1 = 2131361809;
         break;
      case 18:
         var1 = 2131361808;
         break;
      case 19:
         var1 = 2130837537;
         break;
      case 20:
         var1 = 2130837711;
         break;
      case 21:
         var1 = 2130837527;
         break;
      case 22:
         var1 = 2130837535;
         break;
      case 23:
         var1 = 2130837529;
         break;
      case 24:
         var1 = 2130837531;
         break;
      case 25:
         var1 = 2130837541;
         break;
      case 26:
         var1 = 2130837545;
         break;
      case 27:
         var1 = 2130837543;
         break;
      case 28:
         var1 = 2130837525;
         break;
      case 29:
         var1 = 2130837523;
         break;
      case 30:
         var1 = 2130837801;
         break;
      case 31:
         var1 = 2130837607;
         break;
      case 32:
         var1 = 2130837807;
         break;
      case 33:
         var1 = 2130837642;
         break;
      case 34:
         var1 = 2130837811;
         break;
      case 35:
         var1 = 2130837809;
         break;
      case 36:
         var1 = 2131361793;
         break;
      case 37:
         var1 = 2130837515;
         break;
      case 38:
         var1 = 2130837713;
         break;
      case 40:
         var1 = 2130837746;
         break;
      case 41:
         var1 = 2130837521;
         break;
      case 42:
         var1 = 2130837803;
         break;
      case 43:
         var1 = 2130837645;
         break;
      case 44:
         var1 = 2130837539;
      }

      return var1;
   }

   public static int getResource(Session var0, int var1) {
      int var2;
      switch(var0.getTheme()) {
      case 2131361832:
         var2 = getLightResourceId(var1);
         break;
      case 2131361833:
         var2 = getDarkResourceId(var1);
         break;
      default:
         var2 = -1;
      }

      return var2;
   }
}
