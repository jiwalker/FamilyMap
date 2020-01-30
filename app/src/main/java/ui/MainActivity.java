package ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.familymap.R;
import model.Model;

import java.net.URI;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FragmentManager fm = this.getSupportFragmentManager();
        if (Model.initialize().isLoggedIn()){
            login(true);
        }
        else {
            fm.beginTransaction()
                    .replace(R.id.frameLayout, new LoginFragment()).commit();
        }
    }
    public void switchToMap(){
        FragmentManager fm = this.getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.frameLayout, new MapFrag()).commit();

    }

    @Override
    public void login(boolean isLoggedIn) {
        if (isLoggedIn){
            switchToMap();
        }
    }

}
