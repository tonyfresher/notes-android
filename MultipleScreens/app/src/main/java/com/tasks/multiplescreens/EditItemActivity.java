package com.tasks.multiplescreens;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class EditItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        ItemContent item = getIntent().hasExtra(ItemContent.NAME) ?
                (ItemContent) getIntent().getSerializableExtra(ItemContent.NAME) : null;

        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }
}
