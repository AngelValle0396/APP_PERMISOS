package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ImageView img;
    Intent i;
    final static int cons=1;
    Bitmap btm;
    Button btfoto;
    Button btfile;

    Uri uriTree;
    final static int RQS_OPEN_DOCUMENT_TREE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> permisos = new ArrayList<String>();
        permisos.add(Manifest.permission.CAMERA);
        permisos.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permisos.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permisos.add(Manifest.permission.WRITE_CALENDAR);


        getPermission(permisos);

        btfoto=findViewById(R.id.button3);
        btfile=findViewById(R.id.button);
        btfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarFoto(v);
            }
        });
        btfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BajarDoc(v);
            }
        });
    }

    public void guardarFoto(View view){
        i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        img=(ImageView)findViewById(R.id.imageView);
        if(i.resolveActivity(getPackageManager())!=null){
            startActivityForResult(i,cons);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == RQS_OPEN_DOCUMENT_TREE)
        { uriTree = data.getData();}

        if(requestCode==cons && resultCode== Activity.RESULT_OK){
            Bundle bd=data.getExtras();
            btm=(Bitmap)bd.get("data");
            img.setImageBitmap(btm);
        }
    }

    public void getPermission(ArrayList<String> permisosSolicitados){

        ArrayList<String> listPermisosNOAprob = getPermisosNoAprobados(permisosSolicitados);
        if (listPermisosNOAprob.size()>0)
             if (Build.VERSION.SDK_INT >= 23)
                  requestPermissions(listPermisosNOAprob.toArray(new String[listPermisosNOAprob.size()]), 1);

    }


    public ArrayList<String> getPermisosNoAprobados(ArrayList<String>  listaPermisos) {
        ArrayList<String> list = new ArrayList<String>();
        for(String permiso: listaPermisos) {
            if (Build.VERSION.SDK_INT >= 23)
                 if(checkSelfPermission(permiso) != PackageManager.PERMISSION_GRANTED)
                     list.add(permiso);

        }
        return list;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String s="";
        if(requestCode==1)    {
            for(int i =0; i<permissions.length;i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    s=s + "OK " + permissions[i] + "\n";
                else
                    s=s + "NO  " + permissions[i] + "\n";
            }
            Toast.makeText(this.getApplicationContext(), s, Toast.LENGTH_LONG).show();
        }
    }

    public void MostrarDescargas(View view){

        Intent intent = new Intent();
        intent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(intent);

    }
    public void BajarDoc(View view){
        i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(i, RQS_OPEN_DOCUMENT_TREE);

        String url = "https://www.uteq.edu.ec/revistacyt/archivositio/instrucciones_arbitros.pdf";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("PDF");
        request.setTitle("Pdf");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
       String g= Environment.DIRECTORY_DOWNLOADS;
            g=uriTree.toString();
        request.setDestinationInExternalPublicDir(uriTree.toString(), "filedownload.pdf");
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        try {
            manager.enqueue(request);
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(),"Error: "  + e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }


}