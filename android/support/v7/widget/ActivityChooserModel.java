package android.support.v7.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.DataSetObservable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class ActivityChooserModel extends DataSetObservable
{
  static final String ATTRIBUTE_ACTIVITY = "activity";
  static final String ATTRIBUTE_TIME = "time";
  static final String ATTRIBUTE_WEIGHT = "weight";
  static final boolean DEBUG = false;
  private static final int DEFAULT_ACTIVITY_INFLATION = 5;
  private static final float DEFAULT_HISTORICAL_RECORD_WEIGHT = 1.0F;
  public static final String DEFAULT_HISTORY_FILE_NAME = "activity_choser_model_history.xml";
  public static final int DEFAULT_HISTORY_MAX_LENGTH = 50;
  private static final String HISTORY_FILE_EXTENSION = ".xml";
  private static final int INVALID_INDEX = -1;
  static final String LOG_TAG = ActivityChooserModel.class.getSimpleName();
  static final String TAG_HISTORICAL_RECORD = "historical-record";
  static final String TAG_HISTORICAL_RECORDS = "historical-records";
  private static final Map<String, ActivityChooserModel> sDataModelRegistry;
  private static final Object sRegistryLock = new Object();
  private final List<ActivityResolveInfo> mActivities = new ArrayList();
  private OnChooseActivityListener mActivityChoserModelPolicy;
  private ActivitySorter mActivitySorter = new DefaultSorter();
  boolean mCanReadHistoricalData = true;
  final Context mContext;
  private final List<HistoricalRecord> mHistoricalRecords = new ArrayList();
  private boolean mHistoricalRecordsChanged = true;
  final String mHistoryFileName;
  private int mHistoryMaxSize = 50;
  private final Object mInstanceLock = new Object();
  private Intent mIntent;
  private boolean mReadShareHistoryCalled = false;
  private boolean mReloadActivities = false;

  static
  {
    sDataModelRegistry = new HashMap();
  }

  private ActivityChooserModel(Context paramContext, String paramString)
  {
    this.mContext = paramContext.getApplicationContext();
    if ((!TextUtils.isEmpty(paramString)) && (!paramString.endsWith(".xml")))
    {
      this.mHistoryFileName = (paramString + ".xml");
      return;
    }
    this.mHistoryFileName = paramString;
  }

  private boolean addHistoricalRecord(HistoricalRecord paramHistoricalRecord)
  {
    boolean bool = this.mHistoricalRecords.add(paramHistoricalRecord);
    if (bool)
    {
      this.mHistoricalRecordsChanged = true;
      pruneExcessiveHistoricalRecordsIfNeeded();
      persistHistoricalDataIfNeeded();
      sortActivitiesIfNeeded();
      notifyChanged();
    }
    return bool;
  }

  private void ensureConsistentState()
  {
    boolean bool = loadActivitiesIfNeeded() | readHistoricalDataIfNeeded();
    pruneExcessiveHistoricalRecordsIfNeeded();
    if (bool)
    {
      sortActivitiesIfNeeded();
      notifyChanged();
    }
  }

  public static ActivityChooserModel get(Context paramContext, String paramString)
  {
    synchronized (sRegistryLock)
    {
      ActivityChooserModel localActivityChooserModel = (ActivityChooserModel)sDataModelRegistry.get(paramString);
      if (localActivityChooserModel == null)
      {
        localActivityChooserModel = new ActivityChooserModel(paramContext, paramString);
        sDataModelRegistry.put(paramString, localActivityChooserModel);
      }
      return localActivityChooserModel;
    }
  }

  private boolean loadActivitiesIfNeeded()
  {
    boolean bool = this.mReloadActivities;
    int i = 0;
    if (bool)
    {
      Intent localIntent = this.mIntent;
      i = 0;
      if (localIntent != null)
      {
        this.mReloadActivities = false;
        this.mActivities.clear();
        List localList = this.mContext.getPackageManager().queryIntentActivities(this.mIntent, 0);
        int j = localList.size();
        for (int k = 0; k < j; k++)
        {
          ResolveInfo localResolveInfo = (ResolveInfo)localList.get(k);
          this.mActivities.add(new ActivityResolveInfo(localResolveInfo));
        }
        i = 1;
      }
    }
    return i;
  }

  private void persistHistoricalDataIfNeeded()
  {
    if (!this.mReadShareHistoryCalled)
      throw new IllegalStateException("No preceding call to #readHistoricalData");
    if (!this.mHistoricalRecordsChanged);
    do
    {
      return;
      this.mHistoricalRecordsChanged = false;
    }
    while (TextUtils.isEmpty(this.mHistoryFileName));
    PersistHistoryAsyncTask localPersistHistoryAsyncTask = new PersistHistoryAsyncTask();
    Executor localExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = new ArrayList(this.mHistoricalRecords);
    arrayOfObject[1] = this.mHistoryFileName;
    localPersistHistoryAsyncTask.executeOnExecutor(localExecutor, arrayOfObject);
  }

  private void pruneExcessiveHistoricalRecordsIfNeeded()
  {
    int i = this.mHistoricalRecords.size() - this.mHistoryMaxSize;
    if (i <= 0);
    while (true)
    {
      return;
      this.mHistoricalRecordsChanged = true;
      for (int j = 0; j < i; j++)
        ((HistoricalRecord)this.mHistoricalRecords.remove(0));
    }
  }

  private boolean readHistoricalDataIfNeeded()
  {
    if ((this.mCanReadHistoricalData) && (this.mHistoricalRecordsChanged) && (!TextUtils.isEmpty(this.mHistoryFileName)))
    {
      this.mCanReadHistoricalData = false;
      this.mReadShareHistoryCalled = true;
      readHistoricalDataImpl();
      return true;
    }
    return false;
  }

  private void readHistoricalDataImpl()
  {
    try
    {
      FileInputStream localFileInputStream = this.mContext.openFileInput(this.mHistoryFileName);
      try
      {
        localXmlPullParser = Xml.newPullParser();
        localXmlPullParser.setInput(localFileInputStream, "UTF-8");
        for (int i = 0; (i != 1) && (i != 2); i = localXmlPullParser.next());
        if (!"historical-records".equals(localXmlPullParser.getName()))
          throw new XmlPullParserException("Share records file does not start with historical-records tag.");
      }
      catch (XmlPullParserException localXmlPullParserException)
      {
        Log.e(LOG_TAG, "Error reading historical recrod file: " + this.mHistoryFileName, localXmlPullParserException);
        if (localFileInputStream != null)
        {
          try
          {
            localFileInputStream.close();
            return;
          }
          catch (IOException localIOException4)
          {
            return;
          }
          localList = this.mHistoricalRecords;
          localList.clear();
          while (true)
          {
            int j = localXmlPullParser.next();
            if (j == 1)
            {
              if (localFileInputStream == null)
                return;
              try
              {
                localFileInputStream.close();
                return;
              }
              catch (IOException localIOException5)
              {
                return;
              }
            }
            if ((j == 3) || (j == 4))
              continue;
            if ("historical-record".equals(localXmlPullParser.getName()))
              break;
            throw new XmlPullParserException("Share records file not well-formed.");
          }
        }
      }
      catch (IOException localIOException2)
      {
        while (true)
        {
          XmlPullParser localXmlPullParser;
          List localList;
          Log.e(LOG_TAG, "Error reading historical recrod file: " + this.mHistoryFileName, localIOException2);
          if (localFileInputStream == null)
            break;
          try
          {
            localFileInputStream.close();
            return;
          }
          catch (IOException localIOException3)
          {
            return;
          }
          localList.add(new HistoricalRecord(localXmlPullParser.getAttributeValue(null, "activity"), Long.parseLong(localXmlPullParser.getAttributeValue(null, "time")), Float.parseFloat(localXmlPullParser.getAttributeValue(null, "weight"))));
        }
      }
      finally
      {
        if (localFileInputStream != null);
        try
        {
          localFileInputStream.close();
          label314: throw localObject;
        }
        catch (IOException localIOException1)
        {
          break label314;
        }
      }
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
    }
  }

  private boolean sortActivitiesIfNeeded()
  {
    if ((this.mActivitySorter != null) && (this.mIntent != null) && (!this.mActivities.isEmpty()) && (!this.mHistoricalRecords.isEmpty()))
    {
      this.mActivitySorter.sort(this.mIntent, this.mActivities, Collections.unmodifiableList(this.mHistoricalRecords));
      return true;
    }
    return false;
  }

  public Intent chooseActivity(int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      if (this.mIntent == null)
        return null;
      ensureConsistentState();
      ActivityResolveInfo localActivityResolveInfo = (ActivityResolveInfo)this.mActivities.get(paramInt);
      ComponentName localComponentName = new ComponentName(localActivityResolveInfo.resolveInfo.activityInfo.packageName, localActivityResolveInfo.resolveInfo.activityInfo.name);
      Intent localIntent1 = new Intent(this.mIntent);
      localIntent1.setComponent(localComponentName);
      if (this.mActivityChoserModelPolicy != null)
      {
        Intent localIntent2 = new Intent(localIntent1);
        if (this.mActivityChoserModelPolicy.onChooseActivity(this, localIntent2))
          return null;
      }
      addHistoricalRecord(new HistoricalRecord(localComponentName, System.currentTimeMillis(), 1.0F));
      return localIntent1;
    }
  }

  public ResolveInfo getActivity(int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      ResolveInfo localResolveInfo = ((ActivityResolveInfo)this.mActivities.get(paramInt)).resolveInfo;
      return localResolveInfo;
    }
  }

  public int getActivityCount()
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      int i = this.mActivities.size();
      return i;
    }
  }

  public int getActivityIndex(ResolveInfo paramResolveInfo)
  {
    while (true)
    {
      int j;
      synchronized (this.mInstanceLock)
      {
        ensureConsistentState();
        List localList = this.mActivities;
        int i = localList.size();
        j = 0;
        if (j >= i)
          continue;
        if (((ActivityResolveInfo)localList.get(j)).resolveInfo == paramResolveInfo)
        {
          return j;
          return -1;
        }
      }
      j++;
    }
  }

  public ResolveInfo getDefaultActivity()
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      if (!this.mActivities.isEmpty())
      {
        ResolveInfo localResolveInfo = ((ActivityResolveInfo)this.mActivities.get(0)).resolveInfo;
        return localResolveInfo;
      }
      return null;
    }
  }

  public int getHistoryMaxSize()
  {
    synchronized (this.mInstanceLock)
    {
      int i = this.mHistoryMaxSize;
      return i;
    }
  }

  public int getHistorySize()
  {
    synchronized (this.mInstanceLock)
    {
      ensureConsistentState();
      int i = this.mHistoricalRecords.size();
      return i;
    }
  }

  public Intent getIntent()
  {
    synchronized (this.mInstanceLock)
    {
      Intent localIntent = this.mIntent;
      return localIntent;
    }
  }

  public void setActivitySorter(ActivitySorter paramActivitySorter)
  {
    synchronized (this.mInstanceLock)
    {
      if (this.mActivitySorter == paramActivitySorter)
        return;
      this.mActivitySorter = paramActivitySorter;
      if (sortActivitiesIfNeeded())
        notifyChanged();
      return;
    }
  }

  public void setDefaultActivity(int paramInt)
  {
    while (true)
    {
      synchronized (this.mInstanceLock)
      {
        ensureConsistentState();
        ActivityResolveInfo localActivityResolveInfo1 = (ActivityResolveInfo)this.mActivities.get(paramInt);
        ActivityResolveInfo localActivityResolveInfo2 = (ActivityResolveInfo)this.mActivities.get(0);
        if (localActivityResolveInfo2 != null)
        {
          f = 5.0F + (localActivityResolveInfo2.weight - localActivityResolveInfo1.weight);
          addHistoricalRecord(new HistoricalRecord(new ComponentName(localActivityResolveInfo1.resolveInfo.activityInfo.packageName, localActivityResolveInfo1.resolveInfo.activityInfo.name), System.currentTimeMillis(), f));
          return;
        }
      }
      float f = 1.0F;
    }
  }

  public void setHistoryMaxSize(int paramInt)
  {
    synchronized (this.mInstanceLock)
    {
      if (this.mHistoryMaxSize == paramInt)
        return;
      this.mHistoryMaxSize = paramInt;
      pruneExcessiveHistoricalRecordsIfNeeded();
      if (sortActivitiesIfNeeded())
        notifyChanged();
      return;
    }
  }

  public void setIntent(Intent paramIntent)
  {
    synchronized (this.mInstanceLock)
    {
      if (this.mIntent == paramIntent)
        return;
      this.mIntent = paramIntent;
      this.mReloadActivities = true;
      ensureConsistentState();
      return;
    }
  }

  public void setOnChooseActivityListener(OnChooseActivityListener paramOnChooseActivityListener)
  {
    synchronized (this.mInstanceLock)
    {
      this.mActivityChoserModelPolicy = paramOnChooseActivityListener;
      return;
    }
  }

  public static abstract interface ActivityChooserModelClient
  {
    public abstract void setActivityChooserModel(ActivityChooserModel paramActivityChooserModel);
  }

  public static final class ActivityResolveInfo
    implements Comparable<ActivityResolveInfo>
  {
    public final ResolveInfo resolveInfo;
    public float weight;

    public ActivityResolveInfo(ResolveInfo paramResolveInfo)
    {
      this.resolveInfo = paramResolveInfo;
    }

    public int compareTo(ActivityResolveInfo paramActivityResolveInfo)
    {
      return Float.floatToIntBits(paramActivityResolveInfo.weight) - Float.floatToIntBits(this.weight);
    }

    public boolean equals(Object paramObject)
    {
      if (this == paramObject);
      ActivityResolveInfo localActivityResolveInfo;
      do
      {
        return true;
        if (paramObject == null)
          return false;
        if (getClass() != paramObject.getClass())
          return false;
        localActivityResolveInfo = (ActivityResolveInfo)paramObject;
      }
      while (Float.floatToIntBits(this.weight) == Float.floatToIntBits(localActivityResolveInfo.weight));
      return false;
    }

    public int hashCode()
    {
      return 31 + Float.floatToIntBits(this.weight);
    }

    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[");
      localStringBuilder.append("resolveInfo:").append(this.resolveInfo.toString());
      localStringBuilder.append("; weight:").append(new BigDecimal(this.weight));
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }

  public static abstract interface ActivitySorter
  {
    public abstract void sort(Intent paramIntent, List<ActivityChooserModel.ActivityResolveInfo> paramList, List<ActivityChooserModel.HistoricalRecord> paramList1);
  }

  private static final class DefaultSorter
    implements ActivityChooserModel.ActivitySorter
  {
    private static final float WEIGHT_DECAY_COEFFICIENT = 0.95F;
    private final Map<ComponentName, ActivityChooserModel.ActivityResolveInfo> mPackageNameToActivityMap = new HashMap();

    public void sort(Intent paramIntent, List<ActivityChooserModel.ActivityResolveInfo> paramList, List<ActivityChooserModel.HistoricalRecord> paramList1)
    {
      Map localMap = this.mPackageNameToActivityMap;
      localMap.clear();
      int i = paramList.size();
      for (int j = 0; j < i; j++)
      {
        ActivityChooserModel.ActivityResolveInfo localActivityResolveInfo2 = (ActivityChooserModel.ActivityResolveInfo)paramList.get(j);
        localActivityResolveInfo2.weight = 0.0F;
        localMap.put(new ComponentName(localActivityResolveInfo2.resolveInfo.activityInfo.packageName, localActivityResolveInfo2.resolveInfo.activityInfo.name), localActivityResolveInfo2);
      }
      int k = -1 + paramList1.size();
      float f = 1.0F;
      for (int m = k; m >= 0; m--)
      {
        ActivityChooserModel.HistoricalRecord localHistoricalRecord = (ActivityChooserModel.HistoricalRecord)paramList1.get(m);
        ActivityChooserModel.ActivityResolveInfo localActivityResolveInfo1 = (ActivityChooserModel.ActivityResolveInfo)localMap.get(localHistoricalRecord.activity);
        if (localActivityResolveInfo1 == null)
          continue;
        localActivityResolveInfo1.weight += f * localHistoricalRecord.weight;
        f *= 0.95F;
      }
      Collections.sort(paramList);
    }
  }

  public static final class HistoricalRecord
  {
    public final ComponentName activity;
    public final long time;
    public final float weight;

    public HistoricalRecord(ComponentName paramComponentName, long paramLong, float paramFloat)
    {
      this.activity = paramComponentName;
      this.time = paramLong;
      this.weight = paramFloat;
    }

    public HistoricalRecord(String paramString, long paramLong, float paramFloat)
    {
      this(ComponentName.unflattenFromString(paramString), paramLong, paramFloat);
    }

    public boolean equals(Object paramObject)
    {
      if (this == paramObject);
      HistoricalRecord localHistoricalRecord;
      do
      {
        return true;
        if (paramObject == null)
          return false;
        if (getClass() != paramObject.getClass())
          return false;
        localHistoricalRecord = (HistoricalRecord)paramObject;
        if (this.activity == null)
        {
          if (localHistoricalRecord.activity != null)
            return false;
        }
        else if (!this.activity.equals(localHistoricalRecord.activity))
          return false;
        if (this.time != localHistoricalRecord.time)
          return false;
      }
      while (Float.floatToIntBits(this.weight) == Float.floatToIntBits(localHistoricalRecord.weight));
      return false;
    }

    public int hashCode()
    {
      if (this.activity == null);
      for (int i = 0; ; i = this.activity.hashCode())
        return 31 * (31 * (i + 31) + (int)(this.time ^ this.time >>> 32)) + Float.floatToIntBits(this.weight);
    }

    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[");
      localStringBuilder.append("; activity:").append(this.activity);
      localStringBuilder.append("; time:").append(this.time);
      localStringBuilder.append("; weight:").append(new BigDecimal(this.weight));
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }

  public static abstract interface OnChooseActivityListener
  {
    public abstract boolean onChooseActivity(ActivityChooserModel paramActivityChooserModel, Intent paramIntent);
  }

  private final class PersistHistoryAsyncTask extends AsyncTask<Object, Void, Void>
  {
    PersistHistoryAsyncTask()
    {
    }

    // ERROR //
    public Void doInBackground(Object[] paramArrayOfObject)
    {
      // Byte code:
      //   0: aload_1
      //   1: iconst_0
      //   2: aaload
      //   3: checkcast 29	java/util/List
      //   6: astore_2
      //   7: aload_1
      //   8: iconst_1
      //   9: aaload
      //   10: checkcast 31	java/lang/String
      //   13: astore_3
      //   14: aload_0
      //   15: getfield 11	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   18: getfield 37	android/support/v7/widget/ActivityChooserModel:mContext	Landroid/content/Context;
      //   21: aload_3
      //   22: iconst_0
      //   23: invokevirtual 43	android/content/Context:openFileOutput	(Ljava/lang/String;I)Ljava/io/FileOutputStream;
      //   26: astore 6
      //   28: invokestatic 49	android/util/Xml:newSerializer	()Lorg/xmlpull/v1/XmlSerializer;
      //   31: astore 7
      //   33: aload 7
      //   35: aload 6
      //   37: aconst_null
      //   38: invokeinterface 55 3 0
      //   43: aload 7
      //   45: ldc 57
      //   47: iconst_1
      //   48: invokestatic 63	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
      //   51: invokeinterface 67 3 0
      //   56: aload 7
      //   58: aconst_null
      //   59: ldc 69
      //   61: invokeinterface 73 3 0
      //   66: pop
      //   67: aload_2
      //   68: invokeinterface 77 1 0
      //   73: istore 20
      //   75: iconst_0
      //   76: istore 21
      //   78: iload 21
      //   80: iload 20
      //   82: if_icmpge +132 -> 214
      //   85: aload_2
      //   86: iconst_0
      //   87: invokeinterface 81 2 0
      //   92: checkcast 83	android/support/v7/widget/ActivityChooserModel$HistoricalRecord
      //   95: astore 22
      //   97: aload 7
      //   99: aconst_null
      //   100: ldc 85
      //   102: invokeinterface 73 3 0
      //   107: pop
      //   108: aload 7
      //   110: aconst_null
      //   111: ldc 87
      //   113: aload 22
      //   115: getfield 90	android/support/v7/widget/ActivityChooserModel$HistoricalRecord:activity	Landroid/content/ComponentName;
      //   118: invokevirtual 96	android/content/ComponentName:flattenToString	()Ljava/lang/String;
      //   121: invokeinterface 100 4 0
      //   126: pop
      //   127: aload 7
      //   129: aconst_null
      //   130: ldc 102
      //   132: aload 22
      //   134: getfield 105	android/support/v7/widget/ActivityChooserModel$HistoricalRecord:time	J
      //   137: invokestatic 108	java/lang/String:valueOf	(J)Ljava/lang/String;
      //   140: invokeinterface 100 4 0
      //   145: pop
      //   146: aload 7
      //   148: aconst_null
      //   149: ldc 110
      //   151: aload 22
      //   153: getfield 113	android/support/v7/widget/ActivityChooserModel$HistoricalRecord:weight	F
      //   156: invokestatic 116	java/lang/String:valueOf	(F)Ljava/lang/String;
      //   159: invokeinterface 100 4 0
      //   164: pop
      //   165: aload 7
      //   167: aconst_null
      //   168: ldc 85
      //   170: invokeinterface 119 3 0
      //   175: pop
      //   176: iinc 21 1
      //   179: goto -101 -> 78
      //   182: astore 4
      //   184: getstatic 123	android/support/v7/widget/ActivityChooserModel:LOG_TAG	Ljava/lang/String;
      //   187: new 125	java/lang/StringBuilder
      //   190: dup
      //   191: invokespecial 126	java/lang/StringBuilder:<init>	()V
      //   194: ldc 128
      //   196: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   199: aload_3
      //   200: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   203: invokevirtual 135	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   206: aload 4
      //   208: invokestatic 141	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   211: pop
      //   212: aconst_null
      //   213: areturn
      //   214: aload 7
      //   216: aconst_null
      //   217: ldc 69
      //   219: invokeinterface 119 3 0
      //   224: pop
      //   225: aload 7
      //   227: invokeinterface 144 1 0
      //   232: aload_0
      //   233: getfield 11	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   236: iconst_1
      //   237: putfield 148	android/support/v7/widget/ActivityChooserModel:mCanReadHistoricalData	Z
      //   240: aload 6
      //   242: ifnull +8 -> 250
      //   245: aload 6
      //   247: invokevirtual 153	java/io/FileOutputStream:close	()V
      //   250: aconst_null
      //   251: areturn
      //   252: astore 16
      //   254: getstatic 123	android/support/v7/widget/ActivityChooserModel:LOG_TAG	Ljava/lang/String;
      //   257: new 125	java/lang/StringBuilder
      //   260: dup
      //   261: invokespecial 126	java/lang/StringBuilder:<init>	()V
      //   264: ldc 128
      //   266: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   269: aload_0
      //   270: getfield 11	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   273: getfield 156	android/support/v7/widget/ActivityChooserModel:mHistoryFileName	Ljava/lang/String;
      //   276: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   279: invokevirtual 135	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   282: aload 16
      //   284: invokestatic 141	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   287: pop
      //   288: aload_0
      //   289: getfield 11	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   292: iconst_1
      //   293: putfield 148	android/support/v7/widget/ActivityChooserModel:mCanReadHistoricalData	Z
      //   296: aload 6
      //   298: ifnull -48 -> 250
      //   301: aload 6
      //   303: invokevirtual 153	java/io/FileOutputStream:close	()V
      //   306: goto -56 -> 250
      //   309: astore 18
      //   311: goto -61 -> 250
      //   314: astore 13
      //   316: getstatic 123	android/support/v7/widget/ActivityChooserModel:LOG_TAG	Ljava/lang/String;
      //   319: new 125	java/lang/StringBuilder
      //   322: dup
      //   323: invokespecial 126	java/lang/StringBuilder:<init>	()V
      //   326: ldc 128
      //   328: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   331: aload_0
      //   332: getfield 11	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   335: getfield 156	android/support/v7/widget/ActivityChooserModel:mHistoryFileName	Ljava/lang/String;
      //   338: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   341: invokevirtual 135	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   344: aload 13
      //   346: invokestatic 141	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   349: pop
      //   350: aload_0
      //   351: getfield 11	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   354: iconst_1
      //   355: putfield 148	android/support/v7/widget/ActivityChooserModel:mCanReadHistoricalData	Z
      //   358: aload 6
      //   360: ifnull -110 -> 250
      //   363: aload 6
      //   365: invokevirtual 153	java/io/FileOutputStream:close	()V
      //   368: goto -118 -> 250
      //   371: astore 15
      //   373: goto -123 -> 250
      //   376: astore 10
      //   378: getstatic 123	android/support/v7/widget/ActivityChooserModel:LOG_TAG	Ljava/lang/String;
      //   381: new 125	java/lang/StringBuilder
      //   384: dup
      //   385: invokespecial 126	java/lang/StringBuilder:<init>	()V
      //   388: ldc 128
      //   390: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   393: aload_0
      //   394: getfield 11	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   397: getfield 156	android/support/v7/widget/ActivityChooserModel:mHistoryFileName	Ljava/lang/String;
      //   400: invokevirtual 132	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   403: invokevirtual 135	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   406: aload 10
      //   408: invokestatic 141	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   411: pop
      //   412: aload_0
      //   413: getfield 11	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   416: iconst_1
      //   417: putfield 148	android/support/v7/widget/ActivityChooserModel:mCanReadHistoricalData	Z
      //   420: aload 6
      //   422: ifnull -172 -> 250
      //   425: aload 6
      //   427: invokevirtual 153	java/io/FileOutputStream:close	()V
      //   430: goto -180 -> 250
      //   433: astore 12
      //   435: goto -185 -> 250
      //   438: astore 8
      //   440: aload_0
      //   441: getfield 11	android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask:this$0	Landroid/support/v7/widget/ActivityChooserModel;
      //   444: iconst_1
      //   445: putfield 148	android/support/v7/widget/ActivityChooserModel:mCanReadHistoricalData	Z
      //   448: aload 6
      //   450: ifnull +8 -> 458
      //   453: aload 6
      //   455: invokevirtual 153	java/io/FileOutputStream:close	()V
      //   458: aload 8
      //   460: athrow
      //   461: astore 29
      //   463: goto -213 -> 250
      //   466: astore 9
      //   468: goto -10 -> 458
      //
      // Exception table:
      //   from	to	target	type
      //   14	28	182	java/io/FileNotFoundException
      //   33	75	252	java/lang/IllegalArgumentException
      //   85	176	252	java/lang/IllegalArgumentException
      //   214	232	252	java/lang/IllegalArgumentException
      //   301	306	309	java/io/IOException
      //   33	75	314	java/lang/IllegalStateException
      //   85	176	314	java/lang/IllegalStateException
      //   214	232	314	java/lang/IllegalStateException
      //   363	368	371	java/io/IOException
      //   33	75	376	java/io/IOException
      //   85	176	376	java/io/IOException
      //   214	232	376	java/io/IOException
      //   425	430	433	java/io/IOException
      //   33	75	438	finally
      //   85	176	438	finally
      //   214	232	438	finally
      //   254	288	438	finally
      //   316	350	438	finally
      //   378	412	438	finally
      //   245	250	461	java/io/IOException
      //   453	458	466	java/io/IOException
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v7.widget.ActivityChooserModel
 * JD-Core Version:    0.6.0
 */