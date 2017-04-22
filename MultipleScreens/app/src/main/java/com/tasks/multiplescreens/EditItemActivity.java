package com.tasks.multiplescreens;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditItemActivity extends AppCompatActivity {
    @BindView(R.id.edit_app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.edit_toolbar)
    Toolbar toolbar;
    @BindView(R.id.edit_name)
    EditText itemName;
    @BindView(R.id.edit_description)
    EditText itemDescription;

    private boolean isNewItem = true;
    private ItemContent item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getIntent().hasExtra(ItemContent.NAME)) {
            isNewItem = false;

            item = (ItemContent) getIntent().getSerializableExtra(ItemContent.NAME);
            initFromItem();

            findViewById(R.id.edit_delete).setVisibility(View.VISIBLE);
        } else {
            item = new ItemContent();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        item.name = getName();
        item.description = getDescription();
        outState.putParcelable(ItemContent.NAME, item);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        item = savedInstanceState.getParcelable(ItemContent.NAME);
    }

    private void initFromItem() {
        itemName.setText(item.name);
        if (item.description != null)
            itemDescription.setText(item.description);
        if (item.color != 0)
            appBarLayout.setBackgroundColor(item.color);
    }

    @OnClick(R.id.edit_exit)
    public void exit() {
        finish();
    }

    @OnClick(R.id.edit_save)
    public void trySaveAndExit() {
        if (getName().equals("")) {
            Toast.makeText(this, "Please enter item name", Toast.LENGTH_SHORT).show();
            return;
        }
        saveItem();
        finish();
    }

    @OnClick(R.id.edit_save)
    public void deleteAndExit() {
        deleteItem();
        finish();
    }

    private void saveItem() {
        item.name = getName();
        item.description = getDescription();

        DatabaseHelper helper = new DatabaseHelper(this);
        if (isNewItem) {
            helper.insert(item);
        } else {
            helper.replace(item.id, item);
        }
    }

    private void deleteItem() {
        DatabaseHelper helper = new DatabaseHelper(this);
        helper.delete(item.id);
    }

    private String getDescription() {
        return itemDescription.getText().toString();
    }

    private String getName() {
        return itemName.getText().toString();
    }
}