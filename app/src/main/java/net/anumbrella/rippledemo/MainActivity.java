package net.anumbrella.rippledemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import net.anumbrella.ripplelibrary.RippleView;

public class MainActivity extends AppCompatActivity {


    private RippleView rippleView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rippleView = (RippleView) findViewById(R.id.listener);
        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Toast.makeText(MainActivity.this,"水波纹完成",Toast.LENGTH_SHORT).show();
            }
        });



    }


}
