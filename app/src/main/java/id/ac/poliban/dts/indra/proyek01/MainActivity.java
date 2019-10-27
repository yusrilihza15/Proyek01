package id.ac.poliban.dts.indra.proyek01;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import java.io.File;
import java.lang.Object;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_STORAGE = 100;
    ListView lvView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar tbBar = findViewById(R.id.tbBar);
        setSupportActionBar(tbBar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("aplikasi catatan harian yg membosankan");

        lvView = findViewById(R.id.lvView);

        lvView.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, Object> data = (Map<String, Object>) parent.getAdapter().getItem(position);

            Intent intent = new Intent(this, InsertAndViewActivity.class);
            intent.putExtra("filename ", data.get("name").toString());
            startActivity(intent);
        });

        lvView.setOnItemLongClickListener((parent, view, position, id) -> {
            Map<String, Object> data = (Map<String, Object>) parent.getAdapter().getItem(position);
            String namaFile = data.get("nama").toString();
            new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi Hapus")
                    .setMessage(String.format("Hapus Catatan %s", namaFile))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("yes", (dialog, which) -> hapusFile(namaFile))
                    .setNegativeButton("no", null).show();
            return true;
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23) {
            if (periksaIzinPenyimpanan()) {
                showListFiles();
            }
        } else {
            showListFiles();
        }
    }

    public boolean periksaIzinPenyimpanan() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showListFiles();
            }

    }

    void showListFiles() {
        String path = Environment.getExternalStorageDirectory() + "/kominfo.proyek1";
        File directory = new File(path);

        if (directory.exists()) {
            File[] files = directory.listFiles();
            String[] filenames = new String[files.length];
            String[] dateCreated = new String[files.length];
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd  MMM  YYYY  HH:mm:ss");
            ArrayList<Map<String, Object>> itemDataList = new ArrayList<Map<String, Object>>();

            for (int i = 0; i < files.length; i++) {
                filenames[i] = files[i].getName();
                Date lastModDate = new Date(files[i].lastModified());
                dateCreated[i] = simpleDateFormat.format(lastModDate);
                Map<String, Object> listItemMap = new HashMap<>();
                listItemMap.put("name", filenames[i]);
                listItemMap.put("date", dateCreated[i]);
                itemDataList.add(listItemMap);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, itemDataList, android.R.layout.simple_list_item_2, new String[]{"name", "date"}, new int[]{android.R.id.text1, android.R.id.text2});
            lvView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.miTambah)
            startActivity(new Intent(this, InsertAndViewActivity.class));

        return super.onOptionsItemSelected(item);
    }

    void hapusFile(String filename) {
        String path = Environment.getExternalStorageDirectory().toString() + "/kominfo.proyek1";
        File file = new File(path, filename);
        if (file.exists() && file.delete()) System.out.println("file  telah  dihapus");

        showListFiles();

    }
}

