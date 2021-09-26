package ru.i_fighter.makiwara1;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.Series;
import com.jjoe64.graphview.series.LineGraphSeries;

import static ru.i_fighter.makiwara1.PowerActivity.KickPowerGraf;
import static ru.i_fighter.makiwara1.PowerActivity.KickTimePowerGraf;
import static ru.i_fighter.makiwara1.PowerActivity.maxValueGraf;
//import static ru.i_fighter.makiwara1.PowerActivity.maxValueGraf;


public class ResActivity extends AppCompatActivity {
    private static final int CM_DELETE_ID = 1;
    ListView lvData;
    DB db;
    SimpleCursorAdapter scAdapter;
    Cursor cursor;
    int k = 0;

    int kick[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res);

        //Toast.makeText(getApplicationContext(), "KickPowerGraf = "+ KickPowerGraf[0], Toast.LENGTH_SHORT).show();

        GraphView graph = (GraphView) findViewById(R.id.graph);
        /*LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {

               new DataPoint(0.5, KickPowerGraf[0]),
                new DataPoint(0.5, KickPowerGraf[0]),
                new DataPoint(0.5, KickPowerGraf[0]),
                new DataPoint(0.5, KickPowerGraf[0]),
                /*new DataPoint(0.5, 5),
                new DataPoint(0.8, 3),
                new DataPoint(0.9, 2),
                new DataPoint(1.2, 6),
                new DataPoint(1.6, 12),
                new DataPoint(2.4, 5),
                new DataPoint(3.1, 3),
                new DataPoint(4.6, 2),
                new DataPoint(5.5, 6)
        });*/
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(generateData());
        graph.addSeries(series);


         // set manual X bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(1000);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(100);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.addSeries(series);

// styling series

        //graph.setTitle("Kick Power");
        series.setTitle("Kick Power, kGf");
        graph.getLegendRenderer().setVisible(true);
        series.setColor(Color.GREEN);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);



        // открываем подключение к БД
        db = new DB(this);
        db.open();

        // получаем курсор
        cursor = db.getAllData();
        startManagingCursor(cursor);

        // формируем столбцы сопоставления
        String[] from = new String[] { DB.COLUMN_IMG, DB.COLUMN_TXT };
        //int[] to = new int[] { R.id.ivImg, R.id.tvText };
        int[] to = new int[] { R.id.textViewDate, R.id.tvTextResults };

        // создааем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, from, to);
        lvData = (ListView) findViewById(R.id.lvData);
        lvData.setAdapter(scAdapter);

        // добавляем контекстное меню к списку
        registerForContextMenu(lvData);

    }
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, "Delete record");
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД
            db.delRec(acmi.id);
            // обновляем курсор
            cursor.requery();
            return true;
        }
        return super.onContextItemSelected(item);
    }
    private DataPoint[] generateData() {
        //int count = maxValueGraf;
        DataPoint[] values = new DataPoint[maxValueGraf];
        for (int k=0; k<maxValueGraf; k++) {

            DataPoint v = new DataPoint(k, KickPowerGraf[k]);
            values[k] = v;
        }
        return values;
    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }

}
