package com.xxx.appstore.common.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppSecurityPermissions
  implements View.OnClickListener
{
  private static final String TAG = "AppSecurityPermissions";
  private boolean localLOGV = false;
  private Context mContext;
  private State mCurrentState;
  private Drawable mDangerousIcon;
  private LinearLayout mDangerousList;
  private Map<String, String> mDangerousMap;
  private String mDefaultGrpLabel;
  private String mDefaultGrpName = "DefaultGrp";
  private boolean mExpanded;
  private HashMap<String, CharSequence> mGroupLabelCache;
  private LayoutInflater mInflater;
  private View mNoPermsView;
  private LinearLayout mNonDangerousList;
  private Drawable mNormalIcon;
  private Map<String, String> mNormalMap;
  private String mPermFormat;
  private List<PermissionInfo> mPermsList;
  private LinearLayout mPermsView;
  private PackageManager mPm;

  public AppSecurityPermissions(Context paramContext, String[] paramArrayOfString)
  {
    this.mContext = paramContext;
    this.mPm = this.mContext.getPackageManager();
    this.mPermsList = new ArrayList();
    HashSet localHashSet = new HashSet();
    extractPerms(paramArrayOfString, localHashSet);
    Iterator localIterator = localHashSet.iterator();
    while (localIterator.hasNext())
    {
      PermissionInfo localPermissionInfo = (PermissionInfo)localIterator.next();
      this.mPermsList.add(localPermissionInfo);
    }
  }

  private void aggregateGroupDescs(Map map, Map map1)
  {
      if(map != null && map1 != null)
      {
          Iterator iterator = map.keySet().iterator();
          while(iterator.hasNext()) 
          {
              String s = null;
              String s1 = (String)iterator.next();
              List list = (List)map.get(s1);
              if(list != null)
              {
                  for(Iterator iterator1 = list.iterator(); iterator1.hasNext();)
                      s = formatPermissions(s, ((PermissionInfo)iterator1.next()).loadLabel(mPm));

                  if(s != null)
                  {
                      if(localLOGV)
                          Log.i("AppSecurityPermissions", (new StringBuilder()).append("Group:").append(s1).append(" description:").append(s.toString()).toString());
                      map1.put(s1, s.toString());
                  }
              }
          }
      }
  }

  private String canonicalizeGroupDesc(String s)
  {
      String s1;
      if(s == null || s.length() == 0)
      {
          s1 = null;
      } else
      {
          int i = s.length();
          if(s.charAt(i - 1) == '.')
              s1 = s.substring(0, i - 1);
          else
              s1 = s;
      }
      return s1;
  }
  
  private void displayNoPermissions()
  {
    this.mNoPermsView.setVisibility(0);
    this.mDangerousList.setVisibility(8);
    this.mNonDangerousList.setVisibility(8);
  }

  private void displayPermissions(boolean flag)
  {
      Map map;
      LinearLayout linearlayout;
      if(flag)
          map = mDangerousMap;
      else
          map = mNormalMap;
      if(flag)
          linearlayout = mDangerousList;
      else
          linearlayout = mNonDangerousList;
      linearlayout.removeAllViews();
      String s;
      CharSequence charsequence;
      Iterator iterator = map.keySet().iterator();
      while(iterator.hasNext())
      {
          s = (String)iterator.next();
          charsequence = getGroupLabel(s);
          if(localLOGV)
              Log.i("AppSecurityPermissions", (new StringBuilder()).append("Adding view group:").append(charsequence).append(", desc:").append((String)map.get(s)).toString());
          linearlayout.addView(getPermissionItemView(charsequence, (CharSequence)map.get(s), flag));
      }
  }

  private void extractPerms(String as[], Set set)
  {
      int i = 0;
      if(as != null && as.length != 0)
      {
          int j = as.length;
          while(i < j) 
          {
              String s = as[i];
              try
              {
                  PermissionInfo permissioninfo = mPm.getPermissionInfo(s, 0);
                  if(permissioninfo != null)
                      set.add(permissioninfo);
              }
              catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
              {
                  Log.i("AppSecurityPermissions", (new StringBuilder()).append("Ignoring unknown permission:").append(s).toString());
              }
              i++;
          }
      }
  }

  private String formatPermissions(String paramString, CharSequence paramCharSequence)
  {
    String str1;
    if (paramString == null) {
      if (paramCharSequence == null)
        str1 = null;
      else
    	  str1 = paramCharSequence.toString();
      return str1;
    }

      str1 = canonicalizeGroupDesc(paramString);
      if (paramCharSequence != null)
      {
        String str2 = this.mPermFormat;
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = str1;
        arrayOfObject[1] = paramCharSequence.toString();
        str1 = String.format(str2, arrayOfObject);
      }
      return str1;
  }

  private CharSequence getGroupLabel(String paramString)
  {
    if (paramString == null)
      return this.mDefaultGrpLabel;

    CharSequence localObject = (CharSequence)this.mGroupLabelCache.get(paramString);
      if (localObject == null)
        try
        {
          PermissionGroupInfo localPermissionGroupInfo = this.mPm.getPermissionGroupInfo(paramString, 0);
          localObject = localPermissionGroupInfo.loadLabel(this.mPm).toString();
          this.mGroupLabelCache.put(paramString, localObject);
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          Log.i("AppSecurityPermissions", "Invalid group name:" + paramString);
          localObject = null;
        }

    return localObject;
  }

  private static View getPermissionItemView(Context context, LayoutInflater layoutinflater, CharSequence charsequence, CharSequence charsequence1, boolean flag, Drawable drawable)
  {
      View view = layoutinflater.inflate(0x7f03002f, null);
      TextView textview = (TextView)view.findViewById(0x7f0c0056);
      TextView textview1 = (TextView)view.findViewById(0x7f0c0057);
      ((ImageView)view.findViewById(0x7f0c0055)).setImageDrawable(drawable);
      if(charsequence != null)
      {
          textview.setText(charsequence);
          textview1.setText(charsequence1);
      } else
      {
          textview.setText(charsequence1);
          textview1.setVisibility(8);
      }
      return view;
  }

  public static View getPermissionItemView(Context context, CharSequence charsequence, CharSequence charsequence1, boolean flag)
  {
      LayoutInflater layoutinflater = (LayoutInflater)context.getSystemService("layout_inflater");
      Resources resources = context.getResources();
      int i;
      if(flag)
          i = 0x7f02006a;
      else
          i = 0x7f02006b;
      return getPermissionItemView(context, layoutinflater, charsequence, charsequence1, flag, resources.getDrawable(i));
  }

  private View getPermissionItemView(CharSequence charsequence, CharSequence charsequence1, boolean flag)
  {
      Context context = mContext;
      LayoutInflater layoutinflater = mInflater;
      Drawable drawable;
      if(flag)
          drawable = mDangerousIcon;
      else
          drawable = mNormalIcon;
      return getPermissionItemView(context, layoutinflater, charsequence, charsequence1, flag, drawable);
  }

  private boolean isDisplayablePermission(PermissionInfo permissioninfo)
  {
      boolean flag;
      if(permissioninfo.protectionLevel == 1 || permissioninfo.protectionLevel == 0)
          flag = true;
      else
          flag = false;
      return flag;
  }

  private void setPermissions(List list)
  {
      mGroupLabelCache = new HashMap();
      mGroupLabelCache.put(mDefaultGrpName, mDefaultGrpLabel);
      mDangerousMap = new HashMap();
      mNormalMap = new HashMap();
      HashMap hashmap = new HashMap();
      HashMap hashmap1 = new HashMap();
      PermissionInfoComparator permissioninfocomparator = new PermissionInfoComparator(mPm);
      if(list != null)
      {
          Iterator iterator = list.iterator();
          do
          {
              if(!iterator.hasNext())
                  break;
              PermissionInfo permissioninfo = (PermissionInfo)iterator.next();
              if(localLOGV)
                  Log.i("AppSecurityPermissions", (new StringBuilder()).append("Processing permission:").append(permissioninfo.name).toString());
              if(!isDisplayablePermission(permissioninfo))
              {
                  if(localLOGV)
                      Log.i("AppSecurityPermissions", (new StringBuilder()).append("Permission:").append(permissioninfo.name).append(" is not displayable").toString());
              } else
              {
                  HashMap hashmap2;
                  String s;
                  List list1;
                  if(permissioninfo.protectionLevel == 1)
                      hashmap2 = hashmap;
                  else
                      hashmap2 = hashmap1;
                  if(permissioninfo.group == null)
                      s = mDefaultGrpName;
                  else
                      s = permissioninfo.group;
                  if(localLOGV)
                      Log.i("AppSecurityPermissions", (new StringBuilder()).append("Permission:").append(permissioninfo.name).append(" belongs to group:").append(s).toString());
                  list1 = (List)hashmap2.get(s);
                  if(list1 == null)
                  {
                      ArrayList arraylist = new ArrayList();
                      hashmap2.put(s, arraylist);
                      arraylist.add(permissioninfo);
                  } else
                  {
                      int i = Collections.binarySearch(list1, permissioninfo, permissioninfocomparator);
                      if(localLOGV)
                          Log.i("AppSecurityPermissions", (new StringBuilder()).append("idx=").append(i).append(", list.size=").append(list1.size()).toString());
                      if(i < 0)
                          list1.add(-i - 1, permissioninfo);
                  }
              }
          } while(true);
          aggregateGroupDescs(hashmap, mDangerousMap);
          aggregateGroupDescs(hashmap1, mNormalMap);
      }
      mCurrentState = State.NO_PERMS;
      if(mDangerousMap.size() > 0){
          if(mNormalMap.size() > 0)
        	  mCurrentState = State.BOTH;
          else
        	  mCurrentState = State.DANGEROUS_ONLY;
      }
      else
      {
    	  if(mNormalMap.size() > 0)
              mCurrentState = State.NORMAL_ONLY;
      }

      if(localLOGV)
          Log.i("AppSecurityPermissions", (new StringBuilder()).append("mCurrentState=").append(mCurrentState).toString());
      showPermissions();
  }


  private void showPermissions()
  {  
    switch (this.mCurrentState)
    {
    default:
    	break;
    case NO_PERMS:
    	displayNoPermissions();
    	break;
    case DANGEROUS_ONLY:
    	displayPermissions(true);
        this.mDangerousList.setVisibility(0);
        this.mNonDangerousList.setVisibility(8);
    	break;
    case NORMAL_ONLY:
    	displayPermissions(false);
        this.mDangerousList.setVisibility(8);
        this.mNonDangerousList.setVisibility(0);
    	break;
    case BOTH:
    	displayPermissions(true);
        displayPermissions(false);
        this.mNonDangerousList.setVisibility(0);
    	break;
    }
  }

  public int getPermissionCount()
  {
    return this.mPermsList.size();
  }

  public View getPermissionsView()
  {
    this.mInflater = ((LayoutInflater)this.mContext.getSystemService("layout_inflater"));
    this.mPermsView = ((LinearLayout)this.mInflater.inflate(2130903088, null));
    this.mDangerousList = ((LinearLayout)this.mPermsView.findViewById(2131492953));
    this.mNonDangerousList = ((LinearLayout)this.mPermsView.findViewById(2131492954));
    this.mNoPermsView = this.mPermsView.findViewById(2131492952);
    this.mDefaultGrpLabel = this.mContext.getString(2131296422);
    this.mPermFormat = this.mContext.getString(2131296423);
    this.mNormalIcon = this.mContext.getResources().getDrawable(2130837611);
    this.mDangerousIcon = this.mContext.getResources().getDrawable(2130837610);
    setPermissions(this.mPermsList);
    return this.mPermsView;
  }

  public void onClick(View view)
  {
      if(localLOGV)
          Log.i("AppSecurityPermissions", (new StringBuilder()).append("mExpanded=").append(mExpanded).toString());
      boolean flag;
      if(!mExpanded)
          flag = true;
      else
          flag = false;
      mExpanded = flag;
      showPermissions();
  }

  private static class PermissionInfoComparator
    implements Comparator<PermissionInfo>
  {
    private PackageManager mPm;
    private final Collator sCollator = Collator.getInstance();

    PermissionInfoComparator(PackageManager paramPackageManager)
    {
      this.mPm = paramPackageManager;
    }

    public final int compare(PermissionInfo paramPermissionInfo1, PermissionInfo paramPermissionInfo2)
    {
      CharSequence localCharSequence1 = paramPermissionInfo1.loadLabel(this.mPm);
      CharSequence localCharSequence2 = paramPermissionInfo2.loadLabel(this.mPm);
      return this.sCollator.compare(localCharSequence1, localCharSequence2);
    }
  }

  private static enum State
  {
	  NO_PERMS,
      DANGEROUS_ONLY,
      NORMAL_ONLY,
      BOTH
  }
}