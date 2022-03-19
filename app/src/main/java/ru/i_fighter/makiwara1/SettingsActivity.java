package ru.i_fighter.makiwara1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
//-------------------------------------------------------------------
//ОБЪЯВЛЯЕМ ПЕРЕМЕННЫЕ
//--------------------------------------------------------------------
    Button btnPaired;
    ListView devicelist;
    Animation anim;
    TextView txtViewPairDev;
//--------------------------------------------------------------------------
//Bluetooth
//----------------------------------------------------------------------------
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";
//=========================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //------------------------------------------------------------------------
        //Calling widgets
        //-----------------------------------------------------------------------
        btnPaired = (Button)findViewById(R.id.button);
        devicelist = (ListView)findViewById(R.id.listView);
        txtViewPairDev = (TextView) findViewById(R.id.txtViewPairDev);

        //------------------------------------------------------------------------------------
        //if the device has bluetooth
        //-------------------------------------------------------------------------------------

        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth == null)
        {
            //Show a mensag. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        }else if(!myBluetooth.isEnabled()){
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }
        //----------------------------------------------------------------
        //
        //------------------------------------------------------------------
        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                anim = AnimationUtils.loadAnimation(SettingsActivity.this,R.anim.myscale);
                btnPaired.startAnimation(anim);

                //txtViewPairDev.setText("Click the device to communicate with the simulator");
                txtViewPairDev.setText(R.string.txtClickSimulator);
                pairedDevicesList();
            }
        });
             //pairedDevicesList();
    }
    //------------------------------------------------------------------------------------------------
//
//------------------------------------------------------------------------------------------------
    private void pairedDevicesList()
    {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                Toast.makeText(getApplicationContext(), "Bluetooth Device"+ bt.getAddress(), Toast.LENGTH_LONG).show();

                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address

            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
       final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
       /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, list){

            @Override
            public View getView(int position, View convertView, ViewGroup parent){

                View view = super.getView(position, convertView, parent);

                TextView ListItemShow = (TextView) view.findViewById(android.R.id.text1);

                ListItemShow.setTextColor(Color.parseColor("#ffffff"));

                return view;
            }

        };*/

        //listView.setAdapter(adapter);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }


        //devicelist.setAdapter(adapter);



    //------------------------------------------------------------------------------------------
    //
    //---------------------------------------------------------------------------------------------
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View

            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            //Toast.makeText(getApplicationContext(), "Bluetooth Device"+ address, Toast.LENGTH_LONG).show();


            // Make an intent to start next activity.
            Intent i = new Intent(SettingsActivity.this, PowerActivity.class);
            //Change the activity.
            i.putExtra(EXTRA_ADDRESS, address); //this will be received at ledControl (class) Activity
            //startActivity(i);
            setResult(RESULT_OK,i);
            finish();
        }
    };
}
