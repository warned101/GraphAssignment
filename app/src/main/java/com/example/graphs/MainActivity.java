package com.example.graphs;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity {

    EditText xValue, yValue;
    Button btnInsert;
    GraphView graphView;
    LineGraphSeries lineGraphSeries;


    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xValue = findViewById(R.id.x_value);
        yValue = findViewById(R.id.y_value);

        btnInsert = findViewById(R.id.btn_insert);
        graphView = findViewById(R.id.graph);
        lineGraphSeries = new LineGraphSeries();
        graphView.addSeries(lineGraphSeries);

        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScalableY(true);

        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScrollableY(true);

        FirebaseApp.initializeApp(this);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("chartTable");

        setListeners();

    }

    private void setListeners() {
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = reference.push().getKey();

                int x  = Integer.parseInt(xValue.getText().toString());
                int y  = Integer.parseInt(yValue.getText().toString());

                PointValue pointValue = new PointValue(x, y);

                reference.child(id).setValue(pointValue);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataPoint[] dp = new DataPoint[(int) dataSnapshot.getChildrenCount()];

                int index = 0;

                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    PointValue pointValue = dataSnap.getValue(PointValue.class);
                    dp[index] = new DataPoint(pointValue.getxValue(), pointValue.getyValue());
                    index++;
                }

                lineGraphSeries.resetData(dp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}


