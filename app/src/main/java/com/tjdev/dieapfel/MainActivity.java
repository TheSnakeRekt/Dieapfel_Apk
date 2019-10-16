package com.tjdev.dieapfel;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MainActivity extends AppCompatActivity {
    private WebView myWebView;
    int PERMISSION_ALL = 1;


    private String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INSTALL_PACKAGES
    };

    public boolean CheckPermissionAndStartIntent(Context context, String... permissions)
    {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT > 23) {
            if (!CheckPermissionAndStartIntent(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        }
        myWebView=(WebView) findViewById(R.id.WebView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        webSettings.setAllowFileAccess( true );
        webSettings.setAppCacheEnabled( true );
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled( true );
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        myWebView.loadUrl("https://www.dieapfel.pt");




        myWebView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error)
            {
            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            String message = "Erro no certificado SSL";
            switch (error.getPrimaryError()) {
                case SslError.SSL_UNTRUSTED:
                    message = "A autoridade de validação nao foi verificada";
                    break;
                case SslError.SSL_EXPIRED:
                    message = "O certificado expirou.";
                    break;
                case SslError.SSL_IDMISMATCH:
                    message = "The certificate Hostname mismatch.";
                    break;
                case SslError.SSL_NOTYETVALID:
                    message = "O certificado ainda nao e valido";
                    break;
            }
            message += " Deseja Continuar?";

            builder.setTitle("Erro no certificado SSL");
            builder.setMessage(message);
            builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
            }

            public boolean shouldOverrideUrlLoading(WebView myWebView, String url) {
                if (url.endsWith(".pdf")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "application/pdf");
                    try {
                        myWebView.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        //user does not have a pdf viewer installed
                    }
                } else {
                    myWebView.loadUrl(url);
                }

                return true;
            }
        });



    }



    @Override
    public void onBackPressed()
    {
        if(myWebView.canGoBack())
        {
            myWebView.goBack();
        }
        else
        {
            super.onBackPressed();
        }
    }
}