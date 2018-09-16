package android.support.v4.view;

import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeProviderCompat;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;

public class AccessibilityDelegateCompat
{
  private static final View.AccessibilityDelegate DEFAULT_DELEGATE;
  private static final AccessibilityDelegateBaseImpl IMPL;
  final View.AccessibilityDelegate mBridge = IMPL.newAccessibilityDelegateBridge(this);

  static
  {
    if (Build.VERSION.SDK_INT >= 16)
      IMPL = new AccessibilityDelegateApi16Impl();
    while (true)
    {
      DEFAULT_DELEGATE = new View.AccessibilityDelegate();
      return;
      IMPL = new AccessibilityDelegateBaseImpl();
    }
  }

  public boolean dispatchPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    return DEFAULT_DELEGATE.dispatchPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
  }

  public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View paramView)
  {
    return IMPL.getAccessibilityNodeProvider(DEFAULT_DELEGATE, paramView);
  }

  View.AccessibilityDelegate getBridge()
  {
    return this.mBridge;
  }

  public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    DEFAULT_DELEGATE.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
  }

  public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    DEFAULT_DELEGATE.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat.unwrap());
  }

  public void onPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    DEFAULT_DELEGATE.onPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
  }

  public boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    return DEFAULT_DELEGATE.onRequestSendAccessibilityEvent(paramViewGroup, paramView, paramAccessibilityEvent);
  }

  public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
  {
    return IMPL.performAccessibilityAction(DEFAULT_DELEGATE, paramView, paramInt, paramBundle);
  }

  public void sendAccessibilityEvent(View paramView, int paramInt)
  {
    DEFAULT_DELEGATE.sendAccessibilityEvent(paramView, paramInt);
  }

  public void sendAccessibilityEventUnchecked(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    DEFAULT_DELEGATE.sendAccessibilityEventUnchecked(paramView, paramAccessibilityEvent);
  }

  @RequiresApi(16)
  static class AccessibilityDelegateApi16Impl extends AccessibilityDelegateCompat.AccessibilityDelegateBaseImpl
  {
    public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View.AccessibilityDelegate paramAccessibilityDelegate, View paramView)
    {
      AccessibilityNodeProvider localAccessibilityNodeProvider = paramAccessibilityDelegate.getAccessibilityNodeProvider(paramView);
      if (localAccessibilityNodeProvider != null)
        return new AccessibilityNodeProviderCompat(localAccessibilityNodeProvider);
      return null;
    }

    public View.AccessibilityDelegate newAccessibilityDelegateBridge(AccessibilityDelegateCompat paramAccessibilityDelegateCompat)
    {
      return new View.AccessibilityDelegate(paramAccessibilityDelegateCompat)
      {
        public boolean dispatchPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
        {
          return this.val$compat.dispatchPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
        }

        public AccessibilityNodeProvider getAccessibilityNodeProvider(View paramView)
        {
          AccessibilityNodeProviderCompat localAccessibilityNodeProviderCompat = this.val$compat.getAccessibilityNodeProvider(paramView);
          if (localAccessibilityNodeProviderCompat != null)
            return (AccessibilityNodeProvider)localAccessibilityNodeProviderCompat.getProvider();
          return null;
        }

        public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
        {
          this.val$compat.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
        }

        public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfo paramAccessibilityNodeInfo)
        {
          this.val$compat.onInitializeAccessibilityNodeInfo(paramView, AccessibilityNodeInfoCompat.wrap(paramAccessibilityNodeInfo));
        }

        public void onPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
        {
          this.val$compat.onPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
        }

        public boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
        {
          return this.val$compat.onRequestSendAccessibilityEvent(paramViewGroup, paramView, paramAccessibilityEvent);
        }

        public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
        {
          return this.val$compat.performAccessibilityAction(paramView, paramInt, paramBundle);
        }

        public void sendAccessibilityEvent(View paramView, int paramInt)
        {
          this.val$compat.sendAccessibilityEvent(paramView, paramInt);
        }

        public void sendAccessibilityEventUnchecked(View paramView, AccessibilityEvent paramAccessibilityEvent)
        {
          this.val$compat.sendAccessibilityEventUnchecked(paramView, paramAccessibilityEvent);
        }
      };
    }

    public boolean performAccessibilityAction(View.AccessibilityDelegate paramAccessibilityDelegate, View paramView, int paramInt, Bundle paramBundle)
    {
      return paramAccessibilityDelegate.performAccessibilityAction(paramView, paramInt, paramBundle);
    }
  }

  static class AccessibilityDelegateBaseImpl
  {
    public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View.AccessibilityDelegate paramAccessibilityDelegate, View paramView)
    {
      return null;
    }

    public View.AccessibilityDelegate newAccessibilityDelegateBridge(AccessibilityDelegateCompat paramAccessibilityDelegateCompat)
    {
      return new View.AccessibilityDelegate(paramAccessibilityDelegateCompat)
      {
        public boolean dispatchPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
        {
          return this.val$compat.dispatchPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
        }

        public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
        {
          this.val$compat.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
        }

        public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfo paramAccessibilityNodeInfo)
        {
          this.val$compat.onInitializeAccessibilityNodeInfo(paramView, AccessibilityNodeInfoCompat.wrap(paramAccessibilityNodeInfo));
        }

        public void onPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
        {
          this.val$compat.onPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
        }

        public boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
        {
          return this.val$compat.onRequestSendAccessibilityEvent(paramViewGroup, paramView, paramAccessibilityEvent);
        }

        public void sendAccessibilityEvent(View paramView, int paramInt)
        {
          this.val$compat.sendAccessibilityEvent(paramView, paramInt);
        }

        public void sendAccessibilityEventUnchecked(View paramView, AccessibilityEvent paramAccessibilityEvent)
        {
          this.val$compat.sendAccessibilityEventUnchecked(paramView, paramAccessibilityEvent);
        }
      };
    }

    public boolean performAccessibilityAction(View.AccessibilityDelegate paramAccessibilityDelegate, View paramView, int paramInt, Bundle paramBundle)
    {
      return false;
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.v4.view.AccessibilityDelegateCompat
 * JD-Core Version:    0.6.0
 */