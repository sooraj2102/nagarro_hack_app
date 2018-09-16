package android.support.v13.view.inputmethod;

import android.content.ClipDescription;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputContentInfo;

public final class InputConnectionCompat
{
  private static final InputConnectionCompatImpl IMPL;
  public static int INPUT_CONTENT_GRANT_READ_URI_PERMISSION;

  static
  {
    if (Build.VERSION.SDK_INT >= 25)
      IMPL = new InputContentInfoCompatApi25Impl(null);
    while (true)
    {
      INPUT_CONTENT_GRANT_READ_URI_PERMISSION = 1;
      return;
      IMPL = new InputContentInfoCompatBaseImpl();
    }
  }

  public static boolean commitContent(@NonNull InputConnection paramInputConnection, @NonNull EditorInfo paramEditorInfo, @NonNull InputContentInfoCompat paramInputContentInfoCompat, int paramInt, @Nullable Bundle paramBundle)
  {
    ClipDescription localClipDescription = paramInputContentInfoCompat.getDescription();
    String[] arrayOfString = EditorInfoCompat.getContentMimeTypes(paramEditorInfo);
    int i = arrayOfString.length;
    for (int j = 0; ; j++)
    {
      int k = 0;
      if (j < i)
      {
        if (!localClipDescription.hasMimeType(arrayOfString[j]))
          continue;
        k = 1;
      }
      if (k != 0)
        break;
      return false;
    }
    return IMPL.commitContent(paramInputConnection, paramInputContentInfoCompat, paramInt, paramBundle);
  }

  @NonNull
  public static InputConnection createWrapper(@NonNull InputConnection paramInputConnection, @NonNull EditorInfo paramEditorInfo, @NonNull OnCommitContentListener paramOnCommitContentListener)
  {
    if (paramInputConnection == null)
      throw new IllegalArgumentException("inputConnection must be non-null");
    if (paramEditorInfo == null)
      throw new IllegalArgumentException("editorInfo must be non-null");
    if (paramOnCommitContentListener == null)
      throw new IllegalArgumentException("onCommitContentListener must be non-null");
    return IMPL.createWrapper(paramInputConnection, paramEditorInfo, paramOnCommitContentListener);
  }

  private static abstract interface InputConnectionCompatImpl
  {
    public abstract boolean commitContent(@NonNull InputConnection paramInputConnection, @NonNull InputContentInfoCompat paramInputContentInfoCompat, int paramInt, @Nullable Bundle paramBundle);

    @NonNull
    public abstract InputConnection createWrapper(@NonNull InputConnection paramInputConnection, @NonNull EditorInfo paramEditorInfo, @NonNull InputConnectionCompat.OnCommitContentListener paramOnCommitContentListener);
  }

  @RequiresApi(25)
  private static final class InputContentInfoCompatApi25Impl
    implements InputConnectionCompat.InputConnectionCompatImpl
  {
    public boolean commitContent(@NonNull InputConnection paramInputConnection, @NonNull InputContentInfoCompat paramInputContentInfoCompat, int paramInt, @Nullable Bundle paramBundle)
    {
      return paramInputConnection.commitContent((InputContentInfo)paramInputContentInfoCompat.unwrap(), paramInt, paramBundle);
    }

    @Nullable
    public InputConnection createWrapper(@Nullable InputConnection paramInputConnection, @NonNull EditorInfo paramEditorInfo, @Nullable InputConnectionCompat.OnCommitContentListener paramOnCommitContentListener)
    {
      return new InputConnectionWrapper(paramInputConnection, false, paramOnCommitContentListener)
      {
        public boolean commitContent(InputContentInfo paramInputContentInfo, int paramInt, Bundle paramBundle)
        {
          if (this.val$listener.onCommitContent(InputContentInfoCompat.wrap(paramInputContentInfo), paramInt, paramBundle))
            return true;
          return super.commitContent(paramInputContentInfo, paramInt, paramBundle);
        }
      };
    }
  }

  static final class InputContentInfoCompatBaseImpl
    implements InputConnectionCompat.InputConnectionCompatImpl
  {
    private static String COMMIT_CONTENT_ACTION = "android.support.v13.view.inputmethod.InputConnectionCompat.COMMIT_CONTENT";
    private static String COMMIT_CONTENT_CONTENT_URI_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_URI";
    private static String COMMIT_CONTENT_DESCRIPTION_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_DESCRIPTION";
    private static String COMMIT_CONTENT_FLAGS_KEY;
    private static String COMMIT_CONTENT_LINK_URI_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_LINK_URI";
    private static String COMMIT_CONTENT_OPTS_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_OPTS";
    private static String COMMIT_CONTENT_RESULT_RECEIVER;

    static
    {
      COMMIT_CONTENT_FLAGS_KEY = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_FLAGS";
      COMMIT_CONTENT_RESULT_RECEIVER = "android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_RESULT_RECEIVER";
    }

    static boolean handlePerformPrivateCommand(@Nullable String paramString, @NonNull Bundle paramBundle, @NonNull InputConnectionCompat.OnCommitContentListener paramOnCommitContentListener)
    {
      int i = 1;
      if (!TextUtils.equals(COMMIT_CONTENT_ACTION, paramString));
      do
        return false;
      while (paramBundle == null);
      ResultReceiver localResultReceiver = null;
      label166: 
      try
      {
        localResultReceiver = (ResultReceiver)paramBundle.getParcelable(COMMIT_CONTENT_RESULT_RECEIVER);
        Uri localUri1 = (Uri)paramBundle.getParcelable(COMMIT_CONTENT_CONTENT_URI_KEY);
        ClipDescription localClipDescription = (ClipDescription)paramBundle.getParcelable(COMMIT_CONTENT_DESCRIPTION_KEY);
        Uri localUri2 = (Uri)paramBundle.getParcelable(COMMIT_CONTENT_LINK_URI_KEY);
        int j = paramBundle.getInt(COMMIT_CONTENT_FLAGS_KEY);
        Bundle localBundle = (Bundle)paramBundle.getParcelable(COMMIT_CONTENT_OPTS_KEY);
        boolean bool = paramOnCommitContentListener.onCommitContent(new InputContentInfoCompat(localUri1, localClipDescription, localUri2), j, localBundle);
        if (localResultReceiver != null);
        return bool;
        int k = 0;
      }
      finally
      {
        if (localResultReceiver != null)
        {
          if (0 == 0)
            break label166;
          localResultReceiver.send(i, null);
        }
      }
    }

    public boolean commitContent(@NonNull InputConnection paramInputConnection, @NonNull InputContentInfoCompat paramInputContentInfoCompat, int paramInt, @Nullable Bundle paramBundle)
    {
      Bundle localBundle = new Bundle();
      localBundle.putParcelable(COMMIT_CONTENT_CONTENT_URI_KEY, paramInputContentInfoCompat.getContentUri());
      localBundle.putParcelable(COMMIT_CONTENT_DESCRIPTION_KEY, paramInputContentInfoCompat.getDescription());
      localBundle.putParcelable(COMMIT_CONTENT_LINK_URI_KEY, paramInputContentInfoCompat.getLinkUri());
      localBundle.putInt(COMMIT_CONTENT_FLAGS_KEY, paramInt);
      localBundle.putParcelable(COMMIT_CONTENT_OPTS_KEY, paramBundle);
      return paramInputConnection.performPrivateCommand(COMMIT_CONTENT_ACTION, localBundle);
    }

    @NonNull
    public InputConnection createWrapper(@NonNull InputConnection paramInputConnection, @NonNull EditorInfo paramEditorInfo, @NonNull InputConnectionCompat.OnCommitContentListener paramOnCommitContentListener)
    {
      if (EditorInfoCompat.getContentMimeTypes(paramEditorInfo).length == 0)
        return paramInputConnection;
      return new InputConnectionWrapper(paramInputConnection, false, paramOnCommitContentListener)
      {
        public boolean performPrivateCommand(String paramString, Bundle paramBundle)
        {
          if (InputConnectionCompat.InputContentInfoCompatBaseImpl.handlePerformPrivateCommand(paramString, paramBundle, this.val$listener))
            return true;
          return super.performPrivateCommand(paramString, paramBundle);
        }
      };
    }
  }

  public static abstract interface OnCommitContentListener
  {
    public abstract boolean onCommitContent(InputContentInfoCompat paramInputContentInfoCompat, int paramInt, Bundle paramBundle);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v13.view.inputmethod.InputConnectionCompat
 * JD-Core Version:    0.6.0
 */