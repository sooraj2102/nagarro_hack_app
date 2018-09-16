package android.support.v4.app;

import android.app.RemoteInput;
import android.app.RemoteInput.Builder;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@RequiresApi(20)
class RemoteInputCompatApi20
{
  private static final String EXTRA_DATA_TYPE_RESULTS_DATA = "android.remoteinput.dataTypeResultsData";

  public static void addDataResultToIntent(RemoteInputCompatBase.RemoteInput paramRemoteInput, Intent paramIntent, Map<String, Uri> paramMap)
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
    Bundle localBundle = getResultsFromIntent(paramIntent);
    if (localBundle == null)
      localBundle = paramBundle;
    while (true)
    {
      int i = paramArrayOfRemoteInput.length;
      for (int j = 0; j < i; j++)
      {
        RemoteInputCompatBase.RemoteInput localRemoteInput = paramArrayOfRemoteInput[j];
        Map localMap = getDataResultsFromIntent(paramIntent, localRemoteInput.getResultKey());
        RemoteInput.addResultsToIntent(fromCompat(new RemoteInputCompatBase.RemoteInput[] { localRemoteInput }), paramIntent, localBundle);
        if (localMap == null)
          continue;
        addDataResultToIntent(localRemoteInput, paramIntent, localMap);
      }
      localBundle.putAll(paramBundle);
    }
  }

  static RemoteInput[] fromCompat(RemoteInputCompatBase.RemoteInput[] paramArrayOfRemoteInput)
  {
    RemoteInput[] arrayOfRemoteInput;
    if (paramArrayOfRemoteInput == null)
      arrayOfRemoteInput = null;
    while (true)
    {
      return arrayOfRemoteInput;
      arrayOfRemoteInput = new RemoteInput[paramArrayOfRemoteInput.length];
      for (int i = 0; i < paramArrayOfRemoteInput.length; i++)
      {
        RemoteInputCompatBase.RemoteInput localRemoteInput = paramArrayOfRemoteInput[i];
        arrayOfRemoteInput[i] = new RemoteInput.Builder(localRemoteInput.getResultKey()).setLabel(localRemoteInput.getLabel()).setChoices(localRemoteInput.getChoices()).setAllowFreeFormInput(localRemoteInput.getAllowFreeFormInput()).addExtras(localRemoteInput.getExtras()).build();
      }
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
    return RemoteInput.getResultsFromIntent(paramIntent);
  }

  static RemoteInputCompatBase.RemoteInput[] toCompat(RemoteInput[] paramArrayOfRemoteInput, RemoteInputCompatBase.RemoteInput.Factory paramFactory)
  {
    if (paramArrayOfRemoteInput == null)
      return null;
    RemoteInputCompatBase.RemoteInput[] arrayOfRemoteInput = paramFactory.newArray(paramArrayOfRemoteInput.length);
    for (int i = 0; i < paramArrayOfRemoteInput.length; i++)
    {
      RemoteInput localRemoteInput = paramArrayOfRemoteInput[i];
      arrayOfRemoteInput[i] = paramFactory.build(localRemoteInput.getResultKey(), localRemoteInput.getLabel(), localRemoteInput.getChoices(), localRemoteInput.getAllowFreeFormInput(), localRemoteInput.getExtras(), null);
    }
    return arrayOfRemoteInput;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.app.RemoteInputCompatApi20
 * JD-Core Version:    0.6.0
 */