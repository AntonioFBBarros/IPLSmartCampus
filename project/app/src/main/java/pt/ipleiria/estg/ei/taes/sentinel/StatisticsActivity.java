package pt.ipleiria.estg.ei.taes.sentinel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;


public class StatisticsActivity extends BaseActivity {

    private FirebaseFirestore db;
    private TextView textViewCurrentRoom;
    private LineChart graphTempTime, graphHumTime;
    private ScatterChart graphQualTime;
    private Local local;
    private String collectionPath;
    private EditText txtTempMin, txtTempMax, txtHumMin, txtHumMax, txtQualMin, txtQualMax;
    private Button buttonStatistics;
    private SimpleDateFormat dateFormat;
    private int numRooms, counter;
    private Random rand; // used for graph colors

    private static WeakReference<ChoseLocalActivity> mActivityRef;
    private static WeakReference<PlaceActivity> mActivityRefPlace;

    // Used to get what was selected in PlaceActivity/...
    private static final String INDICE_LOCAL = "INDICE_LOCAL";

    // Limit values for receiving notifications
    private static final int LIMITE_TEMPERATURA_SUPERIOR = 35;
    private static final int LIMITE_TEMPERATURA_INFERIOR = 19;
    private static final int LIMITE_HUMIDADE_SUPERIOR = 50;
    private static final int LIMITE_HUMIDADE_INFERIOR = 75;

    private static final String STRING_DATE_FORMAT = "dd-MM-yyyy HH:mm";

    // Data Structures
    private HashMap<String, ArrayList<Entry>> tempEntries;
    private HashMap<String, ArrayList<Entry>> humEntries;
    private HashMap<String, ArrayList<Entry>> qualEntries;

    public static void updateActivity(ChoseLocalActivity activity) {
        mActivityRef = new WeakReference<>(activity);
    }

    public static void updateActivity(PlaceActivity activity) {
        mActivityRefPlace = new WeakReference<>(activity);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_statistics;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textViewCurrentRoom = findViewById(R.id.textViewCurrentRoom);
        graphTempTime = findViewById(R.id.temperature_time);
        graphHumTime = findViewById(R.id.humidity_time);
        graphQualTime = findViewById(R.id.quality_time);
        buttonStatistics = findViewById(R.id.buttonStatistics);
        rand = new Random();

        final Button btnTempLimits, btnHumLimits, btnQualLimits, btnResetLimits;

        txtTempMin = findViewById(R.id.txtTempMin);
        txtTempMax = findViewById(R.id.txtTempMax);
        txtHumMin = findViewById(R.id.txtHumMin);
        txtHumMax = findViewById(R.id.txtHumMax);
        txtQualMin = findViewById(R.id.txtQualMin);
        txtQualMax = findViewById(R.id.txtQualMax);
        dateFormat = new SimpleDateFormat(STRING_DATE_FORMAT);
        dateFormat.setLenient(false);
        btnTempLimits = findViewById(R.id.btnTempLimits);
        btnHumLimits = findViewById(R.id.btnHumLimits);
        btnQualLimits = findViewById(R.id.btnQualLimits);
        btnResetLimits = findViewById(R.id.btnResetLimits);

        btnResetLimits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphTempTime.getXAxis().resetAxisMinimum();
                graphTempTime.getXAxis().resetAxisMaximum();
                graphTempTime.notifyDataSetChanged();
                graphTempTime.invalidate();

                graphHumTime.getXAxis().resetAxisMinimum();
                graphHumTime.getXAxis().resetAxisMaximum();
                graphHumTime.notifyDataSetChanged();
                graphHumTime.invalidate();

                graphQualTime.getXAxis().resetAxisMinimum();
                graphQualTime.getXAxis().resetAxisMaximum();
                graphQualTime.notifyDataSetChanged();
                graphQualTime.invalidate();

                refreshTexts();
            }
        });

        btnTempLimits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Date min = dateFormat.parse(txtTempMin.getText().toString());
                    Date max = dateFormat.parse(txtTempMax.getText().toString());
                    if (min.after(max)) {
                        createErrorDialog("Make sure your minimum date is before your maximum date");
                        return;
                    }
                    graphTempTime.getXAxis().setAxisMinimum(min.getTime());
                    graphTempTime.getXAxis().setAxisMaximum(max.getTime());
                    graphTempTime.notifyDataSetChanged();
                    graphTempTime.invalidate();
                } catch (ParseException ex) {
                    createErrorDialog("Please insert valid dates (dd-mm-yyyy hh:mm)");
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });

        btnHumLimits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Date min = dateFormat.parse(txtHumMin.getText().toString());
                    Date max = dateFormat.parse(txtHumMax.getText().toString());
                    if (min.after(max)) {
                        createErrorDialog("Make sure your minimum date is before your maximum date");
                        return;
                    }
                    graphHumTime.getXAxis().setAxisMinimum(min.getTime());
                    graphHumTime.getXAxis().setAxisMaximum(max.getTime());
                    graphHumTime.notifyDataSetChanged();
                    graphHumTime.invalidate();
                } catch (ParseException ex) {
                    createErrorDialog("Please insert valid dates (dd-mm-yyyy)");
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });

        btnQualLimits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Date min = dateFormat.parse(txtQualMin.getText().toString());
                    Date max = dateFormat.parse(txtQualMax.getText().toString());
                    if (min.after(max)) {
                        createErrorDialog("Make sure your minimum date is before your maximum date");
                        return;
                    }
                    graphQualTime.getXAxis().setAxisMinimum(min.getTime());
                    graphQualTime.getXAxis().setAxisMaximum(max.getTime());
                    graphQualTime.notifyDataSetChanged();
                    graphQualTime.invalidate();
                } catch (ParseException ex) {
                    createErrorDialog("Please insert valid dates (dd-mm-yyyy)");
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });

        formatGraphs();
        db = FirebaseFirestore.getInstance();
        local = new Local("ESTG", "A");
        setCollectionPath(local);
    }

    private void createErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StatisticsActivity.this);
        builder.setTitle("Error").setMessage(message);
        builder.setNeutralButton("Ok", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        buttonStatistics.setBackgroundResource(R.drawable.statistics_selected);
        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        if (intent != null) {
            int position = intent.getIntExtra(INDICE_LOCAL, -1);
            if (position != -1){
                if(bundle.containsKey("caller")){
                    if(bundle.getString("caller").equals(ChoseLocalActivity.class.toString())){
                        local = mActivityRef.get().getLocal(position);
                    }else{
                        if(bundle.getString("caller").equals(PlaceActivity.class.toString())){
                            local = mActivityRefPlace.get().getLocal(position);
                        }
                    }
                }

                if (local != null){
                    setCollectionPath(local);
                    System.out.println("\n\n\n\n************* COLLECTION_PATH: " + collectionPath + "\n\n\n");
                }
            }
        }

        getData();


    }

    // Formats all of the graphs, calls each format<name>Graph method
    private void formatGraphs() {
        formatTempTimeGraph();
        formatHumTimeGraph();
        formatQualTimeGraph();
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(graphTempTime);
        mv.setChartView(graphHumTime);
        // mv.setChartView(graphQualTime);
        graphTempTime.setMarker(mv);
        graphHumTime.setMarker(mv);
        //graphQualTime.setMarker(mv);
    }

    private void formatTempTimeGraph() {
        graphTempTime.setBackgroundColor(Color.TRANSPARENT);
        graphTempTime.setTouchEnabled(true);
        graphTempTime.getDescription().setText("");
        graphTempTime.setDragEnabled(true);
        graphTempTime.setScaleEnabled(true);
        graphTempTime.setScaleYEnabled(false);
        graphTempTime.setPinchZoom(true);
        graphTempTime.setDrawGridBackground(false);
        graphTempTime.setNoDataText("No values from this/these classrooms");

        LimitLine limTempInf = new LimitLine(LIMITE_TEMPERATURA_INFERIOR, "Limite Notificações Inferior");
        limTempInf.setLineWidth(2f);
        limTempInf.enableDashedLine(15f, 10f, 1f);
        limTempInf.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limTempInf.setTextSize(5f);
        limTempInf.setLineColor(Color.LTGRAY);

        LimitLine limTempSup = new LimitLine(LIMITE_TEMPERATURA_SUPERIOR, "Limite Notificações Superior");
        limTempSup.setLineWidth(2f);
        limTempSup.enableDashedLine(15f, 10f, 1f);
        limTempSup.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limTempSup.setTextSize(5f);
        limTempSup.setLineColor(Color.LTGRAY);

        ValueFormatter xAxisFormatter = new DateAxisValueFormatter();
        XAxis xAxis = graphTempTime.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setGranularity(43200f);
        xAxis.setLabelCount(2);
        xAxis.setDrawGridLines(false);

        graphTempTime.getAxisRight().setEnabled(false);

        YAxis yAxis = graphTempTime.getAxisLeft();
        yAxis.setDrawLimitLinesBehindData(true);
        yAxis.addLimitLine(limTempSup);
        yAxis.addLimitLine(limTempInf);
        //yAxis.setAxisMinimum(0f);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawZeroLine(true);
        yAxis.setZeroLineWidth(1.5f);
        yAxis.setZeroLineColor(Color.BLACK);
    }

    private void formatHumTimeGraph() {
        graphHumTime.setBackgroundColor(Color.TRANSPARENT);
        graphHumTime.setTouchEnabled(true);
        graphHumTime.getDescription().setText("");
        graphHumTime.setDragEnabled(true);
        graphHumTime.setScaleEnabled(true);
        graphHumTime.setScaleYEnabled(false);
        graphHumTime.setPinchZoom(true);
        graphHumTime.setDrawGridBackground(false);
        graphHumTime.setNoDataText("No values from this/these classrooms");

        LimitLine limHumInf = new LimitLine(LIMITE_HUMIDADE_INFERIOR, "Limite Notificações Inferior");
        limHumInf.setLineWidth(2f);
        limHumInf.enableDashedLine(15f, 10f, 1f);
        limHumInf.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limHumInf.setTextSize(5f);
        limHumInf.setLineColor(Color.LTGRAY);

        LimitLine limHumSup = new LimitLine(LIMITE_HUMIDADE_SUPERIOR, "Limite Notificações Superior");
        limHumSup.setLineWidth(2f);
        limHumSup.enableDashedLine(15f, 10f, 1f);
        limHumSup.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limHumSup.setTextSize(5f);
        limHumSup.setLineColor(Color.LTGRAY);

        ValueFormatter xAxisFormatter = new DateAxisValueFormatter();
        XAxis xAxis = graphHumTime.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setGranularity(43200f);
        xAxis.setLabelCount(2);
        xAxis.setDrawGridLines(false);

        graphHumTime.getAxisRight().setEnabled(false);

        YAxis yAxis = graphHumTime.getAxisLeft();
        yAxis.setDrawLimitLinesBehindData(true);
        yAxis.addLimitLine(limHumSup);
        yAxis.addLimitLine(limHumInf);
        yAxis.setAxisMinimum(0f);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawZeroLine(true);
        yAxis.setZeroLineWidth(1.5f);
        yAxis.setZeroLineColor(Color.BLACK);
    }

    private void formatQualTimeGraph() {
        graphQualTime.setBackgroundColor(Color.TRANSPARENT);
        graphQualTime.getDescription().setText("");
        graphQualTime.setTouchEnabled(true);
        graphQualTime.setDragEnabled(true);
        graphQualTime.setScaleEnabled(true);
        graphQualTime.setPinchZoom(true);
        graphQualTime.setScaleYEnabled(false);
        graphQualTime.setDrawGridBackground(false);
        graphQualTime.setNoDataText("No values from this/these classrooms");
        graphQualTime.setPadding(0, 5, 0, 5);

        ValueFormatter xAxisFormatter = new DateAxisValueFormatter();
        XAxis xAxis = graphQualTime.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setGranularity(43200f);
        xAxis.setLabelCount(2);
        xAxis.setDrawGridLines(false);

        graphQualTime.getAxisRight().setEnabled(false);

        YAxis yAxis = graphQualTime.getAxisLeft();
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(true);
        yAxis.setValueFormatter(new QualidadeValueFormatter());
        yAxis.setDrawZeroLine(false);
        yAxis.setZeroLineWidth(1.5f);
        yAxis.setZeroLineColor(Color.BLACK);
        yAxis.setGranularity(1f);

    }

    // Fills data structures, calls getBuildingInformation or getSingelRoomInformation
    public void getData() {
        tempEntries = new HashMap<>();
        humEntries = new HashMap<>();
        qualEntries = new HashMap<>();
        numRooms = -1;


        if (local.getSala() != null) { // IF HE SELECTED A SINGLE ROOM
            textViewCurrentRoom.setText(local.getEscola() + " > Edificio " + local.getEdificio() + " > " + local.getSala());
            numRooms = 1;
            final String key = local.getSala();
            tempEntries.put(key, new ArrayList<Entry>());
            humEntries.put(key, new ArrayList<Entry>());
            qualEntries.put(key, new ArrayList<Entry>());
            getSingleRoomInformation(collectionPath, key);
        } else { // SELECTED A BUILDING
            textViewCurrentRoom.setText(local.getEscola() + " > " + "Edificio " + local.getEdificio());
            getBuildingInformation();
        }
    }

    // Gets whole building information (X rooms), calls getSingleRoomInformation
    public void getBuildingInformation() {
        db.collection(collectionPath).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    numRooms = task.getResult().size();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        tempEntries.put(document.getId(), new ArrayList<Entry>());
                        humEntries.put(document.getId(), new ArrayList<Entry>());
                        qualEntries.put(document.getId(), new ArrayList<Entry>());
                        getSingleRoomInformation(collectionPath + "/" + document.getId() + "/Historico", document.getId());
                    }
                } else {
                    createErrorDialog("Error " + task.getException());
                }
            }
        });
    }

    /*
    *   Gets single room information and then calls populateGraphs
    *       String path: collection path of the room
    *       String key: key of the data structures, also name of the room
    * */
    public void getSingleRoomInformation(String path, final String key) {
        db.collection(path).orderBy("Timestamp", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.contains("Humidade") && document.contains("Temperatura") && document.contains("Timestamp")){
                            try {
                                Long temperatura, humidade;
                                Date timestamp;
                                temperatura = document.getLong("Temperatura");
                                humidade = document.getLong("Humidade");
                                timestamp = document.getTimestamp("Timestamp").toDate();
                                tempEntries.get(key).add(new Entry(timestamp.getTime(), temperatura));
                                humEntries.get(key).add(new Entry(timestamp.getTime(), humidade));
                                qualEntries.get(key).add(new Entry(timestamp.getTime(), calcularQualidade(temperatura,humidade)));
                            } catch (NullPointerException ex) {
                                System.out.println("Ignored bad values");
                            }
                        }
                    }
                    if (++counter == numRooms) {
                        populateGraphs();
                    }

                } else {
                    createErrorDialog("Error " + task.getException());
                }
            }
        });
    }

    // Clears all of the graphs, and populates each one (calls each populate method)
    public void populateGraphs(){
        graphTempTime.clear();
        graphHumTime.clear();
        graphQualTime.clear();

        populateTempTimeGraph();
        populateHumTimeGraph();
        populateQualTimeGraph();
    }

    public void populateTempTimeGraph() {
        LineData tempData = new LineData();

        for (String key: tempEntries.keySet()) {
            LineDataSet dataSet = new LineDataSet(tempEntries.get(key), key);
            int color = Color.rgb(rand.nextInt(215), rand.nextInt(215), rand.nextInt(215));
            dataSet.setColors(color);
            dataSet.setCircleColor(color);
            dataSet.setLineWidth(2.5f);
            dataSet.setCircleRadius(3.5f);
            dataSet.setCircleHoleRadius(1.5f);
            dataSet.setValueTextSize(8f);

            dataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return super.getFormattedValue(value) + "ºC";
                }
            });
            graphTempTime.getAxisLeft().setAxisMinimum(0f);
            tempData.addDataSet(dataSet);
        }

        graphTempTime.setData(tempData);

        refreshTexts();

        Legend l = graphTempTime.getLegend();
        l.setEnabled(true);
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(8f);
        l.setTextColor(Color.BLACK);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setWordWrapEnabled(true);
        l.setDrawInside(false);
    }

    public void populateHumTimeGraph() {
        LineData humData = new LineData();

        for (String key: humEntries.keySet()) {
            LineDataSet dataSet = new LineDataSet(humEntries.get(key), key);
            int color = Color.rgb(rand.nextInt(215), rand.nextInt(215), rand.nextInt(215));
            dataSet.setColors(color);
            dataSet.setCircleColor(color);
            dataSet.setLineWidth(2.5f);
            dataSet.setCircleRadius(3.5f);
            dataSet.setCircleHoleRadius(1.5f);
            dataSet.setValueTextSize(8f);

            dataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return super.getFormattedValue(value) + "%";
                }
            });
            graphHumTime.getAxisLeft().setAxisMinimum(0f);
            humData.addDataSet(dataSet);
        }

        graphHumTime.setData(humData);

        refreshTexts();

        Legend l = graphHumTime.getLegend();
        l.setEnabled(true);
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(8f);
        l.setTextColor(Color.BLACK);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setWordWrapEnabled(true);
        l.setDrawInside(false);
    }

    public void populateQualTimeGraph() {
        ScatterData qualData = new ScatterData();
        for (String key: humEntries.keySet()) {
            ScatterDataSet dataSet = new ScatterDataSet(qualEntries.get(key), key);
            int color = Color.rgb(rand.nextInt(215), rand.nextInt(215), rand.nextInt(215));
            dataSet.setColor(color);
            dataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
            dataSet.setScatterShapeSize(25f);
            dataSet.setScatterShapeHoleColor(Color.WHITE);
            dataSet.setScatterShapeHoleRadius(1.5f);
            dataSet.setValueTextSize(8f);

            dataSet.setValueFormatter(new QualidadeValueFormatter());
            qualData.addDataSet(dataSet);
        }

        graphQualTime.setData(qualData);
        graphQualTime.getAxisLeft().setAxisMinimum(-1.2f);
        graphQualTime.getAxisLeft().setAxisMaximum(1.2f);
        graphQualTime.getXAxis().setDrawAxisLine(false);
        //graphQualTime.getAxisLeft().setLabelCount(3);
        refreshTexts();

        Legend l = graphQualTime.getLegend();
        l.setEnabled(true);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTextSize(8f);
        l.setTextColor(Color.BLACK);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setWordWrapEnabled(true);
        l.setDrawInside(false);
    }

    private void refreshTexts() {
        long minValue = (long) graphTempTime.getXAxis().getAxisMinimum();
        Date minDate = new Timestamp(minValue);
        txtTempMin.setText(dateFormat.format(minDate));
    

        long maxValue = (long) graphTempTime.getXAxis().getAxisMaximum();
        Date maxDate = new Timestamp(maxValue);
        txtTempMax.setText(dateFormat.format(maxDate));

        minValue = (long) graphHumTime.getXAxis().getAxisMinimum();
        minDate = new Timestamp(minValue);
        txtHumMin.setText(dateFormat.format(minDate));

        maxValue = (long) graphHumTime.getXAxis().getAxisMaximum();
        maxDate = new Timestamp(maxValue);
        txtHumMax.setText(dateFormat.format(maxDate));

        minValue = (long) graphQualTime.getXAxis().getAxisMinimum();
        minDate = new Timestamp(minValue);
        txtQualMin.setText(dateFormat.format(minDate));

        maxValue = (long) graphQualTime.getXAxis().getAxisMaximum();
        maxDate = new Timestamp(maxValue);
        txtQualMax.setText(dateFormat.format(maxDate));
    }

    public void setCollectionPath(Local local) {
        this.local = local;
        if (local.getSala() == null){
            this.collectionPath = "/Escolas/" + local.getEscola() + "/Edificios/" + local.getEdificio() + "/Salas";
        } else {
            this.collectionPath = "/Escolas/" + local.getEscola() + "/Edificios/" + local.getEdificio() + "/Salas/" + local.getSala() + "/Historico";
        }
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, StatisticsActivity.class);
    }
    public static Intent createIntent(Context context, int position) {
        return new Intent(context, StatisticsActivity.class).putExtra(INDICE_LOCAL, position);
    }

    private class DateAxisValueFormatter extends ValueFormatter {

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            Date d = new Date(Float.valueOf(value).longValue());
            return new SimpleDateFormat("dd-MM-yy HH:mm").format(d);
        }
    }
    private class QualidadeValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return getQualidadeNome((int) value);
        }
    }

    public int calcularQualidade(long temperatura, long humidade) {
        if ((temperatura >= 19 && temperatura <= 35) && (humidade >= 50 && humidade <= 75))
            return 1;
        else if ((temperatura < 19 || temperatura > 35) && (humidade < 50 || humidade > 75))
            return -1;
        else
            return 0;
    }

    public String getQualidadeNome(int qualidade) {
        if (qualidade == -1)
            return "Mau";
        else if(qualidade == 0)
            return "Médio";
        else
            return "Bom";
    }
}
