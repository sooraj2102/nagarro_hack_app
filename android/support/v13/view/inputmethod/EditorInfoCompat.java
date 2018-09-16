package android.support.v13.view.inputmethod;

import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.inputmethod.EditorInfo;

public final class EditorInfoCompat
{
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  public static final int IME_FLAG_FORCE_ASCII = -2147483648;
  public static final int IME_FLAG_NO_PERSONALIZED_LEARNING = 16777216;
  private static final EditorInfoCompatImpl IMPL;

  static
  {
    if (Build.VERSION.SDK_INT >= 25)
    {
      IMPL = new EditorInfoCompatApi25Impl(null);
      return;
    }
    IMPL = new EditorInfoCompatBaseImpl(null);
  }

  @NonNull
  public static String[] getContentMimeTypes(EditorInfo paramEditorInfo)
  {
    return IMPL.getContentMimeTypes(paramEditorInfo);
  }

  public static void setContentMimeTypes(@NonNull EditorInfo paramEditorInfo, @Nullable String[] paramArrayOfString)
  {
    IMPL.setContentMimeTypes(paramEditorInfo, paramArrayOfString);
  }

  @RequiresApi(25)
  private static final class EditorInfoCompatApi25Impl
    implements EditorInfoCompat.EditorInfoCompatImpl
  {
    @NonNull
    public String[] getContentMimeTypes(@NonNull EditorInfo paramEditorInfo)
    {
      String[] arrayOfString = paramEditorInfo.contentMimeTypes;
      if (arrayOfString != null)
        return arrayOfString;
      return EditorInfoCompat.EMPTY_STRING_ARRAY;
    }

    public void setContentMimeTypes(@NonNull EditorInfo paramEditorInfo, @Nullable String[] paramArrayOfString)
    {
      paramEditorInfo.contentMimeTypes = paramArrayOfString;
    }
  }

  private static final class EditorInfoCompatBaseImpl
    implements EditorInfoCompat.EditorInfoCompatImpl
  {
    private static String CONTENT_MIME_TYPES_KEY = "android.support.v13.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES";

    @NonNull
    public String[] getContentMimeTypes(@NonNull EditorInfo paramEditorInfo)
    {
      String[] arrayOfString;
      if (paramEditorInfo.extras == null)
        arrayOfString = EditorInfoCompat.EMPTY_STRING_ARRAY;
      do
      {
        return arrayOfString;
        arrayOfString = paramEditorInfo.extras.getStringArray(CONTENT_MIME_TYPES_KEY);
      }
      while (arrayOfString != null);
      return EditorInfoCompat.EMPTY_STRING_ARRAY;
    }

    public void setContentMimeTypes(@NonNull EditorInfo paramEditorInfo, @Nullable String[] paramArrayOfString)
    {
      if (paramEditorInfo.extras == null)
        paramEditorInfo.extras = new Bundle();
      paramEditorInfo.extras.putStringArray(CONTENT_MIME_TYPES_KEY, paramArrayOfString);
    }
  }

  private static abstract interface EditorInfoCompatImpl
  {
    @NonNull
    public abstract String[] getContentMimeTypes(@NonNull EditorInfo paramEditorInfo);

    public abstract void setContentMimeTypes(@NonNull EditorInfo paramEditorInfo, @Nullable String[] paramArrayOfString);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v13.view.inputmethod.EditorInfoCompat
 * JD-Core Version:    0.6.0
 */