package com.example.final_proyect.juego1;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.final_proyect.R;

public class MainMenuActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    public static boolean mIsMainMenu = true;

    private static int mRows = 4;
    public static int getRows() { return mRows; }

    private final String BACKGROUND_COLOR_KEY = "BackgroundColor";
    public static int mBackgroundColor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mIsMainMenu = true;

        Typeface ClearSans_Bold = Typeface.createFromAsset(getResources().getAssets(), "ClearSans-Bold.ttf");

        Button bt4x4 = findViewById(R.id.btn_start_4x4);
        Button bt5x5 = findViewById(R.id.btn_start_5x5);
        Button bt6x6 = findViewById(R.id.btn_start_6x6);

        bt4x4.setTypeface(ClearSans_Bold);
        bt5x5.setTypeface(ClearSans_Bold);
        bt6x6.setTypeface(ClearSans_Bold);

        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.settings_color_picker) {
            mRows = 4;  // because of its GameView!
            startActivity(new Intent(MainMenuActivity.this, ColorPickerActivity.class));
            return true;
        }
        return false;
    }

    public void onButtonsClick(View view) {
        if (view.getId() == R.id.btn_start_4x4) {
            StartGame(4);
        } else if (view.getId() == R.id.btn_start_5x5) {
            StartGame(5);
        } else if (view.getId() == R.id.btn_start_6x6) {
            StartGame(6);
        } else if (view.getId() == R.id.btn_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.get_from_bazaar) + "\n\n" + getString(R.string.url_cafe_bazaar));
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
        } else if (view.getId() == R.id.btn_more_games) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("bazaar://collection?slug=by_author&aid=scientist_studio"));
                intent.setPackage("com.farsitel.bazaar");
                startActivity(intent);
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_cafe_bazzar_developer))));
            }
        } else if (view.getId() == R.id.btn_rate) {
            Intent bazaarIntent = new Intent(Intent.ACTION_EDIT);
            bazaarIntent.setData(Uri.parse("bazaar://details?id=com.gameditors.a2048"));
            bazaarIntent.setPackage("com.farsitel.bazaar");

            try {
                startActivity(bazaarIntent);
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_cafe_bazaar))));
            }
        } else if (view.getId() == R.id.btn_social_instagram) {
            Intent instagramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.instagram_page_uri)));
            instagramIntent.setPackage(getString(R.string.instagram_package_name));

            try {
                startActivity(instagramIntent);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.instagram_page_uri))));
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        } else if (view.getId() == R.id.btn_settings) {
            PopupMenu popup = new PopupMenu(this, view);
            popup.setOnMenuItemClickListener(this);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menus, popup.getMenu());
            popup.show();
        } else if (view.getId() == R.id.btn_send_email) {
            String[] TO = {getString(R.string.email_support_address)};
            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");

            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));

            try {
                emailIntent.setPackage("com.google.android.gm");
                startActivity(emailIntent);
            } catch (ActivityNotFoundException ex) {
                emailIntent.setPackage("");
                startActivity(Intent.createChooser(emailIntent, getString(R.string.email_send_title)));
            } catch (Exception e) {
                Toast.makeText(MainMenuActivity.this, getString(R.string.email_client_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsMainMenu = true;
        SaveColors();
        LoadColors();
    }

    private void SaveColors() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();

        if(mBackgroundColor < 0)
            editor.putInt(BACKGROUND_COLOR_KEY, mBackgroundColor);

        editor.apply();
    }

    private void LoadColors() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        if(settings.getInt(BACKGROUND_COLOR_KEY, mBackgroundColor) < 0)
            mBackgroundColor = settings.getInt(BACKGROUND_COLOR_KEY, mBackgroundColor);
        else
            mBackgroundColor = getResources().getColor(R.color.colorBackground);
    }

    private void StartGame(int rows) {
        mRows = rows;
        mIsMainMenu = false;
        startActivity(new Intent(MainMenuActivity.this, MainActivity.class));
    }
}