package android.support.design.internal;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.support.annotation.RestrictTo;
import android.util.SparseArray;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class ParcelableSparseArray extends SparseArray<Parcelable>
  implements Parcelable
{
  public static final Parcelable.Creator<ParcelableSparseArray> CREATOR = new Parcelable.ClassLoaderCreator()
  {
    public ParcelableSparseArray createFromParcel(Parcel paramParcel)
    {
      return new ParcelableSparseArray(paramParcel, null);
    }

    public ParcelableSparseArray createFromParcel(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      return new ParcelableSparseArray(paramParcel, paramClassLoader);
    }

    public ParcelableSparseArray[] newArray(int paramInt)
    {
      return new ParcelableSparseArray[paramInt];
    }
  };

  public ParcelableSparseArray()
  {
  }

  public ParcelableSparseArray(Parcel paramParcel, ClassLoader paramClassLoader)
  {
    int i = paramParcel.readInt();
    int[] arrayOfInt = new int[i];
    paramParcel.readIntArray(arrayOfInt);
    Parcelable[] arrayOfParcelable = paramParcel.readParcelableArray(paramClassLoader);
    for (int j = 0; j < i; j++)
      put(arrayOfInt[j], arrayOfParcelable[j]);
  }

  public int describeContents()
  {
    return 0;
  }

  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = size();
    int[] arrayOfInt = new int[i];
    Parcelable[] arrayOfParcelable = new Parcelable[i];
    for (int j = 0; j < i; j++)
    {
      arrayOfInt[j] = keyAt(j);
      arrayOfParcelable[j] = ((Parcelable)valueAt(j));
    }
    paramParcel.writeInt(i);
    paramParcel.writeIntArray(arrayOfInt);
    paramParcel.writeParcelableArray(arrayOfParcelable, paramInt);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.design.internal.ParcelableSparseArray
 * JD-Core Version:    0.6.0
 */