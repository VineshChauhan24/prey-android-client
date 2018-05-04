package com.prey.activities;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prey.PreyLogger;
import com.prey.PreyStatus;
import com.prey.PreyUtils;
import com.prey.contacts.ContactAccessor;
import com.prey.contacts.ContactInfo;
import com.prey.exceptions.SMSNotSendException;
import com.prey.sms.SMSSupport;
import com.prey.R;
public class HeroActivity extends PreyActivity {

    private static final int PICK_CONTACT_REQUEST = 0;
    ContactAccessor contactAccesor = new ContactAccessor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hero);
        fillScreenInfo(getPreyConfig().getDestinationHeroName(), getPreyConfig().getDestinationHeroNumber(),null);

        View.OnClickListener launchContactPicker = new View.OnClickListener() {
            public void onClick(View v) {
                doLaunchContactPicker(getCurrentFocus());
            }
        };

        Button change = (Button) findViewById(R.id.hero_btn_change);
        change.setOnClickListener(launchContactPicker);

        ImageView picture = (ImageView) findViewById(R.id.hero_sheriff);
        picture.setOnClickListener(launchContactPicker);

        Button ok = (Button) findViewById(R.id.hero_btn_accept);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                close();
            }
        });

        Button remove = (Button) findViewById(R.id.hero_btn_remove);
        remove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getPreyConfig().saveDestinationHeroNumber("");
                getPreyConfig().saveDestinationHeroName("");
                getPreyConfig().deleteSmsPicture();
                fillScreenInfo("","",null);
            }
        });
        requestPermission();
    }


    private void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(getApplicationContext().checkSelfPermission( Manifest.permission.READ_CONTACTS ) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, INITIAL_PERMS, REQUEST_PERMISSIONS);
            }
        }
    }
    private static final int REQUEST_PERMISSIONS = 1;

    private static final String[] INITIAL_PERMS={
            Manifest.permission.READ_CONTACTS
    };


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
         PreyLogger.i("requestCode:"+requestCode+" permissions:"+permissions+" grantResults:"+grantResults);
    }


    @Override
    public void onBackPressed(){
        this.close();
    }

    private void close(){
        Intent intent = new Intent(HeroActivity.this, PreyConfigurationActivity.class);
        PreyStatus.getInstance().setPreyConfigurationActivityResume(true);
        startActivity(intent);
        finish();
    }

    public void doLaunchContactPicker(View view) {
        startActivityForResult(contactAccesor.getPickContactIntent(), PICK_CONTACT_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        PreyLogger.d("Activity returned");
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == RESULT_OK)
            loadContactInfo(data.getData());
    }



    private void loadContactInfo(Uri contactUri) {

        /*
         * We should always run database queries on a background thread. The
         * database may be locked by some process for a long time. If we locked
         * up the UI thread while waiting for the query to come back, we might
         * get an "Application Not Responding" dialog.
         */
        AsyncTask<Uri, Void, ContactInfo> task = new AsyncTask<Uri, Void, ContactInfo>() {

            @Override
            protected ContactInfo doInBackground(Uri... uris) {
                return contactAccesor.loadContact(getContentResolver(), uris[0]);
            }

            @Override
            protected void onPostExecute(ContactInfo result) {
                bindView(result);

            }
        };
        task.execute(contactUri);
    }

    protected void bindView(ContactInfo contactInfo) {
        String contactNumber = contactInfo.getPhoneNumber();
        String contactName = contactInfo.getDisplayName();
        Bitmap contactPhoto = contactInfo.getPicture();

        if (contactNumber != null && PhoneNumberUtils.isWellFormedSmsAddress(contactNumber)) {
            getPreyConfig().saveDestinationHeroNumber(contactNumber);
            getPreyConfig().saveDestinationHeroName(contactName);
            getPreyConfig().saveDestinationHeroPicture(contactPhoto);
            fillScreenInfo(contactName, contactNumber,contactPhoto);
            PreyLogger.d("Hero contact stored: " + contactInfo.getDisplayName() + " - " + contactInfo.getPhoneNumber());
        }
        else {
            Toast.makeText(HeroActivity.this, R.string.preferences_destination_hero_not_valid, Toast.LENGTH_LONG).show();
        }
    }

    private void fillScreenInfo(String name, String number, Bitmap photo){
        if (name == null || name.equals(""))
            name = getString(R.string.no_hero_selected);
        ((TextView) findViewById(R.id.hero_contact_text)).setText(name);
        ((TextView) findViewById(R.id.hero_contact_number)).setText(PhoneNumberUtils.formatNumber(number));
        Bitmap b = getPreyConfig().getDestinationSmsPicture();
        if (b!= null)
            ((ImageView) findViewById(R.id.hero_sheriff)).setImageBitmap(b);
        else
            ((ImageView) findViewById(R.id.hero_sheriff)).setImageResource(R.drawable.sheriff);
    }

}
