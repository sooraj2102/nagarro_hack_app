package android.support.v4.app;

import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@RequiresApi(16)
class RemoteInputCompatJellybean
{
  private static final String EXTRA_DATA_TYPE_RESULTS_DATA = "android.remoteinput.dataTypeResultsData";
  private static final String KEY_ALLOWED_DATA_TYPES = "allowedDataTypes";
  private static final String KEY_ALLOW_FREE_FORM_INPUT = "allowFreeFormInput";
  private static final String KEY_CHOICES = "choices";
  private static final String KEY_EXTRAS = "extras";
  private static final String KEY_LABEL = "label";
  private static final String KEY_RESULT_KEY = "resultKey";

  public static void addDataResultToIntent(RemoteInput paramRemoteInput, Intent paramIntent, Map<String, Uri> paramMap)
  {
    Intent localIntent = getClipDataIntentFromIntent(paramIntent);
    if (localIntent == null)
      localIntent = new Intent();
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      Uri localUri = (Uri)localEntry.getValue();
      if (str == null)
        continue;
      Bundle localBundle = localIntent.getBundleExtra(getExtraResultsKeyForData(str));
      if (localBundle == null)
        localBundle = new Bundle();
      localBundle.putString(paramRemoteInput.getResultKey(), localUri.toString());
      localIntent.putExtra(getExtraResultsKeyForData(str), localBundle);
    }
    paramIntent.setClipData(ClipData.newIntent("android.remoteinput.results", localIntent));
  }

  static void addResultsToIntent(RemoteInputCompatBase.RemoteInput[] paramArrayOfRemoteInput, Intent paramIntent, Bundle paramBundle)
  {
    Intent localIntent = getClipDataIntentFromIntent(paramIntent);
    if (localIntent == null)
      localIntent = new Intent();
    Bundle localBundle = localIntent.getBundleExtra("android.remoteinput.resultsData");
    if (localBundle == null)
      localBundle = new Bundle();
    int i = paramArrayOfRemoteInput.length;
    for (int j = 0; j < i; j++)
    {
      RemoteInputCompatBase.RemoteInput localRemoteInput = paramArrayOfRemoteInput[j];
      Object localObject = paramBundle.get(localRemoteInput.getResultKey());
      if (!(localObject instanceof CharSequence))
        continue;
      localBundle.putCharSequence(localRemoteInput.getResultKey(), (CharSequence)localObject);
    }
    localIntent.putExtra("android.remoteinput.resultsData", localBundle);
    paramIntent.setClipData(ClipData.newIntent("android.remoteinput.results", localIntent));
  }

  static RemoteInputCompatBase.RemoteInput fromBundle(Bundle paramBundle, RemoteInputCompatBase.RemoteInput.Factory paramFactory)
  {
    ArrayList localArrayList = paramBundle.getStringArrayList("allowedDataTypes");
    HashSet localHashSet = new HashSet();
    if (localArrayList != null)
    {
      Iterator localIterator = localArrayList.iterator();
      while (localIterator.hasNext())
        localHashSet.add((String)localIterator.next());
    }
    return paramFactory.build(paramBundle.getString("resultKey"), paramBundle.getCharSequence("label"), paramBundle.getCharSequenceArray("choices"), paramBundle.getBoolean("allowFreeFormInput"), paramBundle.getBundle("extras"), localHashSet);
  }

  static RemoteInputCompatBase.RemoteInput[] fromBundleArray(Bundle[] paramArrayOfBundle, RemoteInputCompatBase.RemoteInput.Factory paramFactory)
  {
    RemoteInputCompatBase.RemoteInput[] arrayOfRemoteInput;
    if (paramArrayOfBundle == null)
      arrayOfRemoteInput = null;
    while (true)
    {
      return arrayOfRemoteInput;
      arrayOfRemoteInput = paramFactory.newArray(paramArrayOfBundle.length);
      for (int i = 0; i < paramArrayOfBundle.length; i++)
        arrayOfRemoteInput[i] = fromBundle(paramArrayOfBundle[i], paramFactory);
    }
  }

  private static Intent getClipDataIntentFromIntent(Intent paramIntent)
  {
    ClipData localClipData = paramIntent.getClipData();
    if (localClipData == null);
    ClipDescription localClipDescription;
    do
    {
      return null;
      localClipDescription = localClipData.getDescription();
    }
    while ((!localClipDescription.hasMimeType("text/vnd.android.intent")) || (!localClipDescription.getLabel().equals("android.remoteinput.results")));
    return localClipData.getItemAt(0).getIntent();
  }

  static Map<String, Uri> getDataResultsFromIntent(Intent paramIntent, String paramString)
  {
    Intent localIntent = getClipDataIntentFromIntent(paramIntent);
    if (localIntent == null)
      return null;
    HashMap localHashMap = new HashMap();
    Iterator localIterator = localIntent.getExtras().keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      if (!str1.startsWith("android.remoteinput.dataTypeResultsData"))
        continue;
      String str2 = str1.substring("android.remoteinput.dataTypeResultsData".length());
      if ((str2 == null) || (str2.isEmpty()))
        continue;
      String str3 = localIntent.getBundleExtra(str1).getString(paramString);
      if ((str3 == null) || (str3.isEmpty()))
        continue;
      localHashMap.put(str2, Uri.parse(str3));
    }
    if (localHashMap.isEmpty())
      localHashMap = null;
    return localHashMap;
  }

  private static String getExtraResultsKeyForData(String paramString)
  {
    return "android.remoteinput.dataTypeResultsData" + paramString;
  }

  static Bundle getResultsFromIntent(Intent paramIntent)
  {
    Intent localIntent = getClipDataIntentFromIntent(paramIntent);
    if (localIntent == null)
      return null;
    return (Bundle)localIntent.getExtras().getParcelable("android.remoteinput.resultsData");
  }

  static Bundle toBundle(RemoteInputCompatBase.RemoteInput paramRemoteInput)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("resultKey", paramRemoteInput.getResultKey());
    localBundle.putCharSequence("label", paramRemoteInput.getLabel());
    localBundle.putCharSequenceArray("choices", paramRemoteInput.getChoices());
    localBundle.putBoolean("allowFreeFormInput", paramRemoteInput.getAllowFreeFormInput());
    localBundle.putBundle("extras", paramRemoteInput.getExtras());
    Set localSet = paramRemoteInput.getAllowedDataTypes();
    if ((localSet != null) && (!localSet.isEmpty()))
    {
      ArrayList localArrayList = new ArrayList(localSet.size());
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
        localArrayList.add((String)localIterator.next());
      localBundle.putStringArrayList("allowedDataTypes", localArrayList);
    }
    return localBundle;
  }

  static Bundle[] toBundleArray(RemoteInputCompatBase.RemoteInput[] paramArrayOfRemoteInput)
  {
    Bundle[] arrayOfBundle;
    if (paramArrayOfRemoteInput == null)
      arrayOfBundle = null;
    while (true)
    {
      return arrayOfBundle;
      arrayOfBundle = new Bundle[paramArrayOfRemoteInput.length];
      for (int i = 0; i < paramArrayOfRemoteInput.length; i++)
        arrayOfBundle[i] = toBundle(paramArrayOfRemoteInput[i]);
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.app.RemoteInputCompatJellybean
 * JD-Core Version:    0.6.0
 */