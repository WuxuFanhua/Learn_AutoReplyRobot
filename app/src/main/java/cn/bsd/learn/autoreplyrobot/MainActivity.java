package cn.bsd.learn.autoreplyrobot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText edit_text;
    private int Count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit_text = findViewById(R.id.edit_text);
    }

    public void btnClick(View view) {
        edit_text.postDelayed(new Runnable() {
            @Override
            public void run() {
                edit_text.setText("" + Count);
                Count++;
            }
        }, 200);
    }
}
