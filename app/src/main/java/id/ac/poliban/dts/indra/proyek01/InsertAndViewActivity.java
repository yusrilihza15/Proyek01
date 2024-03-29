package id.ac.poliban.dts.indra.proyek01;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class InsertAndViewActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_STORAGE = 100;
    int eventID = 0;
    EditText etNama, etCatatan;
    //boolean isEditTable = false;
    String tempCatatan = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_and_view);

        Toolbar tbBar = findViewById(R.id.tbBar);
        setSupportActionBar(tbBar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        etNama = findViewById(R.id.etNama);
        etCatatan = findViewById(R.id.etCatatan);
        Button btSimpan = findViewById(R.id.btSimpan);

        String fileName;

        btSimpan.setOnClickListener(v -> {
            eventID = 2;
            if (!tempCatatan.equals(etCatatan.getText().toString())) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (periksaIzinPenyimpanan()) {
                        tampilSaveDialog();
                    }
                } else {
                    tampilSaveDialog();
                }
            }
        });

        if (getIntent().getExtras() != null && !getIntent().getExtras().isEmpty()) {
            fileName = getIntent().getExtras().getString("filename");
            etNama.setText(fileName);
            getSupportActionBar().setTitle("Ubah  Catatan");
        } else {
            getSupportActionBar().setTitle("Tambah  Catatan");
        }

        eventID = 1;
        if (Build.VERSION.SDK_INT >= 23) {
            if (periksaIzinPenyimpanan()) {
                bacaFile();
            }
        } else {
            bacaFile();
        }
    }

    public boolean periksaIzinPenyimpanan() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE);


                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            if (eventID == 1) bacaFile();
            else
                tampilSaveDialog();
    }

    void bacaFile() {
        String path = Environment.getExternalStorageDirectory().toString() + "/kominfo.proyek1";
        File file = new File(path, etNama.getText().toString());
        if (file.exists()) {

            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                while (line != null) {
                    text.append(line);
                    line = br.readLine();
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            tempCatatan = text.toString();
            etCatatan.setText(text.toString());
        }
    }

    void buatDanUbah() {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return;
        }

        String path = Environment.getExternalStorageDirectory().toString() + "/kominfo.proyek1";
        File parent = new File(path);

        if (parent.exists()) {
            File file = new File(path, etNama.getText().toString());

            try (FileOutputStream fos = new FileOutputStream(file); OutputStreamWriter osw = new OutputStreamWriter(fos)) {

                osw.append(etCatatan.getText());
                osw.flush();
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            if (parent.mkdir()) return;

            File file = new File(path, etNama.getText().toString());
            try (FileOutputStream fos = new FileOutputStream(file, false)) {
                fos.write(etCatatan.getText().toString().getBytes());
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        onBackPressed();
    }

    void tampilSaveDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Simpan  Catatan")
                .setMessage("Apakah  Anda  yakin  ingin  menyimpan  Catatan  ini?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("YES", (dialog, whichButton) -> buatDanUbah())
                .setNegativeButton("NO", null).show();
    }

    @Override
    public void onBackPressed() {
        if (!tempCatatan.equals(etCatatan.getText().toString())) {
            tampilSaveDialog();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }
}




