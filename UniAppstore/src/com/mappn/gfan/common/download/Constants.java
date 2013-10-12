package com.mappn.gfan.common.download;


public class Constants {

   public static final String ACTION_HIDE = "gfan.intent.action.DOWNLOAD_HIDE";
   public static final String ACTION_LIST = "gfan.intent.action.DOWNLOAD_LIST";
   public static final String ACTION_OPEN = "gfan.intent.action.DOWNLOAD_OPEN";
   public static final String ACTION_RETRY = "gfan.intent.action.DOWNLOAD_WAKEUP";
   public static final int BUFFER_SIZE = 4096;
   public static final String DEFAULT_BBS_SUBDIR = "gfan/bbs";
   public static final String DEFAULT_CLOUD_SUBDIR = "gfan/cloud";
   public static final String DEFAULT_DL_BINARY_EXTENSION = ".bin";
   public static final String DEFAULT_DL_FILENAME = "downloadfile";
   public static final String DEFAULT_DL_HTML_EXTENSION = ".html";
   public static final String DEFAULT_DL_TEXT_EXTENSION = ".txt";
   public static final String DEFAULT_MARKET_SUBDIR = "gfan/market";
   public static final String DEFAULT_SUBDIR = "gfan/others";
   public static final int DESTINATION_CACHE_PARTITION = 1;
   public static final int DESTINATION_EXTERNAL = 0;
   public static final int DOWNLOAD_FROM_BBS = 1;
   public static final int DOWNLOAD_FROM_CLOUD = 2;
   public static final int DOWNLOAD_FROM_MARKET = 0;
   public static final int DOWNLOAD_FROM_OTA = 3;
   public static final String FILENAME_SEQUENCE_SEPARATOR = "-";
   public static final String KNOWN_SPURIOUS_FILENAME = "lost+found";
   public static final int MAX_DOWNLOADS = 1000;
   public static final int MAX_REDIRECTS = 5;
   public static final int MAX_RETRIES = 5;
   public static final int MAX_RETRY_AFTER = 86400;
   public static final String MIMETYPE_APK = "application/vnd.android.package-archive";
   public static final String MIMETYPE_IMAGE = "image/*";
   public static final int MIN_PROGRESS_STEP = 4096;
   public static final long MIN_PROGRESS_TIME = 1500L;
   public static final int MIN_RETRY_AFTER = 30;
   public static final String RECOVERY_DIRECTORY = "recovery";
   public static final int RETRY_FIRST_DELAY = 30;


}
