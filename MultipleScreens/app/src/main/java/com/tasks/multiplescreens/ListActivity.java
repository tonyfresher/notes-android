package com.tasks.multiplescreens;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity {
    @BindView(R.id.items_list)
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemContent item = (ItemContent) parent.getItemAtPosition(position);

                Intent intent = new Intent(view.getContext(), EditItemActivity.class);
                intent.putExtra(ItemContent.NAME, (Serializable) item);
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

    @Override
    protected void onResume() {
        super.onResume();
        DataAdapter adapter = new DataAdapter(this, getDataSet());
        list.setAdapter(adapter);
    }

    private ItemContent[] getDataSet() {
        DatabaseHelper helper = new DatabaseHelper(this);
        return helper.getAllItems();
    }
}
