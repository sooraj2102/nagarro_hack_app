package dubeyanurag.com.github.healthyrestaurant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dubeyanurag.com.github.healthyrestaurant.utils.DbHandler;
import java.io.PrintStream;

public class UserDetailActivity extends AppCompatActivity
{

  @BindView(2131296366)
  EditText age;

  @BindView(2131296328)
  RadioGroup diebat;
  Boolean diebatic = Boolean.valueOf(false);

  @BindView(2131296347)
  RadioGroup gend;
  Boolean gender = Boolean.valueOf(true);

  @BindView(2131296354)
  RadioGroup hear;
  Boolean heart = Boolean.valueOf(false);

  @BindView(2131296367)
  EditText height;

  @BindView(2131296368)
  EditText name;

  @BindView(2131296370)
  EditText weight;

  double getCaloriesNeeded()
  {
    double d;
    if (this.gender.booleanValue())
      d = 655.0D + 9.6D * Integer.parseInt(this.weight.getText().toString()) + 1.8D * Integer.parseInt(this.height.getText().toString()) - 4.7D * Integer.parseInt(this.age.getText().toString());
    while (true)
    {
      System.out.println(d);
      switch (1)
      {
      default:
        return 0.0D;
        d = 66.0D + 13.699999999999999D * Integer.parseInt(this.weight.getText().toString()) + 5 * Integer.parseInt(this.height.getText().toString()) - 6.8D * Integer.parseInt(this.age.getText().toString());
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      }
    }
    return d * 1.2D;
    return d * 1.375D;
    return d * 1.55D;
    return d * 1.725D;
    return d * 1.9D;
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2131427358);
    ButterKnife.bind(this);
    if (DbHandler.getBoolean(this, "data", Boolean.valueOf(false)).booleanValue())
    {
      startActivity(new Intent(this, MainActivity.class));
      finish();
    }
    System.out.print(DbHandler.getString(this, "caloriesNeeded", "qwertyh"));
    this.gend.check(0);
    this.hear.check(0);
    this.gend.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
    {
      public void onCheckedChanged(RadioGroup paramRadioGroup, int paramInt)
      {
        UserDetailActivity localUserDetailActivity = UserDetailActivity.this;
        if (!UserDetailActivity.this.gender.booleanValue());
        for (boolean bool = true; ; bool = false)
        {
          localUserDetailActivity.gender = Boolean.valueOf(bool);
          return;
        }
      }
    });
    this.diebat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
    {
      public void onCheckedChanged(RadioGroup paramRadioGroup, int paramInt)
      {
        UserDetailActivity localUserDetailActivity = UserDetailActivity.this;
        if (!UserDetailActivity.this.diebatic.booleanValue());
        for (boolean bool = true; ; bool = false)
        {
          localUserDetailActivity.diebatic = Boolean.valueOf(bool);
          return;
        }
      }
    });
    this.hear.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
    {
      public void onCheckedChanged(RadioGroup paramRadioGroup, int paramInt)
      {
        UserDetailActivity localUserDetailActivity = UserDetailActivity.this;
        if (!UserDetailActivity.this.heart.booleanValue());
        for (boolean bool = true; ; bool = false)
        {
          localUserDetailActivity.heart = Boolean.valueOf(bool);
          return;
        }
      }
    });
  }

  @OnClick({2131296296})
  public void onSubmit()
  {
    if (validate().booleanValue())
    {
      double d = getCaloriesNeeded();
      DbHandler.putString(this, "caloriesNeeded", d + "");
      DbHandler.putString(this, "name", this.name.getText().toString());
      DbHandler.putString(this, "age", this.age + "");
      DbHandler.putString(this, "height", this.height + "");
      DbHandler.putString(this, "weight", this.weight + "");
      if (!this.gender.booleanValue())
        break label209;
    }
    label209: for (String str = "male"; ; str = "female")
    {
      DbHandler.putString(this, "gender", str);
      DbHandler.putBoolean(this, "diebatic", this.diebatic);
      DbHandler.putBoolean(this, "heart", this.heart);
      DbHandler.putBoolean(this, "data", Boolean.valueOf(true));
      startActivity(new Intent(this, MainActivity.class));
      finish();
      return;
    }
  }

  Boolean validate()
  {
    if (this.name.getText().toString().equals(""))
    {
      this.name.setError("You gotta enter your name !!");
      return Boolean.valueOf(false);
    }
    if (this.age.getText().toString().equals(""))
    {
      this.age.setError("You gotta enter your age !!");
      return Boolean.valueOf(false);
    }
    if (this.height.getText().toString().equals(""))
    {
      this.height.setError("You gotta enter your height !!");
      return Boolean.valueOf(false);
    }
    if (this.weight.getText().toString().equals(""))
    {
      this.weight.setError("You gotta enter your weight !!");
      return Boolean.valueOf(false);
    }
    return Boolean.valueOf(true);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     dubeyanurag.com.github.healthyrestaurant.activities.UserDetailActivity
 * JD-Core Version:    0.6.0
 */