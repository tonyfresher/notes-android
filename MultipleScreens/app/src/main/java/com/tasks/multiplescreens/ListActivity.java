package com.tasks.multiplescreens;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ListView list = (ListView) findViewById(R.id.items_list);

        DataAdapter adapter = new DataAdapter(this, getDataSet());
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemContent item = (ItemContent) parent.getItemAtPosition(position);

                Intent intent = new Intent(view.getContext(), EditItemActivity.class);
                intent.putExtra(ItemContent.NAME, item);
                startActivity(intent);
            }
        });

        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.add_button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditItemActivity.class);
                startActivity(intent);
            }
        });
    }

    private ItemContent[] getDataSet() {
        DatabaseHelper helper = new DatabaseHelper(this);
        return helper.getAllItems();
    }
}
