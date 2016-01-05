package sintu.com.pidroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sintu.com.pidroid.p2p.P2P;

public class MainActivity extends AppCompatActivity {
    private P2P p2p;
    private ListView lvDevices;
    private List peers = new ArrayList();
    private List peerNames = new ArrayList();
    private Button btnPhoto;
    private ArrayAdapter<String> peersAdapter = null;
    private Handler peerHandler;
    private static final int ACCESS_STORAGE_PERMISSION = 1000;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String APP_TAG = "SANKOFA";
    private String photoFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createView();
        setHandler();
        setListeners();
        getPermissions();
        p2p = new P2P(this, peerHandler);

    }

    private void createView() {
        setContentView(R.layout.activity_main);
        lvDevices = (ListView) findViewById(R.id.lvDevices);
        peersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, peerNames);
        lvDevices.setAdapter(peersAdapter);
        btnPhoto = (Button) findViewById(R.id.btnPhoto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setHandler() {
        peerHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                peers.clear();
                peers.addAll((List <WifiP2pDevice>) msg.obj );
                peerNames.clear();
                for (int i = 0; i < peers.size(); i++) {
                    WifiP2pDevice device = (WifiP2pDevice) peers.get(i);
                    peersAdapter.add(device.deviceName);
                }

                return true;
            }
        });
    }

    private void setListeners() {
        lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                WifiP2pDevice device = (WifiP2pDevice) peers.get(pos);
                p2p.connect(device);
            }
        });

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });


    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        p2p.registerReceivers();
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        p2p.unregisterReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {

            if (shouldShowRequestPermissionRationale( android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Why we need to write to storage: Camera needs to save picture locally
            }

            requestPermissions(new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    ACCESS_STORAGE_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ) {

            if (shouldShowRequestPermissionRationale( Manifest.permission.CAMERA)) {
            }

            requestPermissions(new String[] {Manifest.permission.CAMERA},
                    ACCESS_STORAGE_PERMISSION);
        }

    }


    private void dispatchTakePictureIntent() {

        getPermissions();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFileName  = APP_TAG + "_" + Integer.toString( Calendar.getInstance().get(Calendar.SECOND) );
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName));
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Uri takenPhotoUri = getPhotoFileUri(photoFileName);
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                // Load the taken image into a preview
                ImageView ivPreview = (ImageView) findViewById(R.id.ivPreview);
                ivPreview.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }
}
