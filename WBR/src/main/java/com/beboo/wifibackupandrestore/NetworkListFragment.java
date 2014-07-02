package com.beboo.wifibackupandrestore;



import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.beboo.wifibackupandrestore.backupmanagement.Network;
import com.beboo.wifibackupandrestore.backupmanagement.NetworkDataChangedListener;
import com.beboo.wifibackupandrestore.backupmanagement.WIFIConfigurationManager;


    public abstract class NetworkListFragment extends ListFragment  implements NetworkDataChangedListener, OnItemClickListener, ActionMode.Callback {


private static final String KEYMGMT_ITEM = "keymgmt";
private static final String SSID_ITEM = "ssid";
private static final String ALIAS_ITEM = "alias";
private static final String STATE_ITEM = "state";

        protected boolean needRestore = false;

public NetworkListFragment() {

}

@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.network_list_fragment, container, false);
	}

	

	public SimpleAdapter initList(List<Network> networks) {
		ListView lv= getListView();

		// create the grid item mapping
		String[] from = new String[] {ALIAS_ITEM, SSID_ITEM, KEYMGMT_ITEM, STATE_ITEM};
		int[] to = new int[] {  R.id.alias, R.id.ssid, R.id.keymgmt,R.id.state};

		// prepare the list of all records
		List<Map<String, String>> fillMaps = new ArrayList<Map<String, String>>();
		for(Network network : networks){        	
			fillMaps.add(createItem(network));
		}

		// fill in the grid_item layout
		SimpleAdapter adapter = new NetworkListAdapter(getActivity(), fillMaps, R.layout.left_row, from, to);
		lv.setAdapter(adapter);


		registerForContextMenu(lv);
		
		lv.setOnItemClickListener(this);

		return adapter;


	}
	
	@Override
	public void onListItemClick(ListView list, View v, int position, long id) {
		// TODO
	}
	
	public SimpleAdapter refresh(List<Network> networks) {
		ListView lv= getListView();

		// create the grid item mapping
		String[] from = new String[] {ALIAS_ITEM, SSID_ITEM, KEYMGMT_ITEM, STATE_ITEM};
		int[] to = new int[] {  R.id.alias, R.id.ssid, R.id.keymgmt,R.id.state};

		// prepare the list of all records
		List<Map<String, String>> fillMaps = new ArrayList<Map<String, String>>();
		for(Network network : networks){        	
			fillMaps.add(createItem(network));
		}

		// fill in the grid_item layout
		SimpleAdapter adapter = new NetworkListAdapter(getActivity(), fillMaps, R.layout.right_row, from, to);
		lv.setAdapter(adapter);


		registerForContextMenu(lv);

		return adapter;
	}

        public abstract void restoreNetwork(Network net);


	Map<String, String> createItem(Network network) {
		Map<String,String> item = new HashMap<String, String>();
		String alias = network.getAlias();
		if (alias != null && alias.length() > 0) {
			item.put(ALIAS_ITEM, alias );
			item.put(SSID_ITEM,network.getSsid());
			item.put(KEYMGMT_ITEM, network.getKeyManagment());
			
		}
		else {
			item.put(ALIAS_ITEM, network.getSsid() );
			item.put(SSID_ITEM,network.getKeyManagment());
		}
		item.put(STATE_ITEM, network.getState());
		return item;
	}
	
	protected void messageDialog(String message) { 
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create(); 
		alertDialog.setTitle("");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {			
			public void onClick(DialogInterface dialog, int which) {
				return;				
			}
		});
		alertDialog.setMessage(message);
		alertDialog.show();
		Log.i("WBR"," MESSAGE :: "+message);

	}


        public void viewNetwork2(final Network network) {
               NetworkViewDialog.show(getActivity(),network,needRestore,this);
        }

	public void viewNetwork(final Network net) {
        android.util.Log.d("WBR","view net "+net.getAlias()+" / "+net.getSsid());

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.viewdialog);
        android.util.Log.d("WBR","view net dialog created");

        if (net.getAlias() != null) {
            TextView alias = (TextView)dialog.findViewById(R.id.view_alias);
            alias.setText(net.getAlias());
        }
        if (net.getSsid() != null) {
            TextView ssid = (TextView)dialog.findViewById(R.id.view_ssid);
            ssid.setText(net.getSsid());
        }
        if (net.getKeyManagment() != null) {
            TextView keyMgmt = (TextView)dialog.findViewById(R.id.view_keymgmt);
            keyMgmt.setText(net.getKeyManagment());
        }
        if (net.getShatedKey() != null) {
            TextView sharedkey = (TextView)dialog.findViewById(R.id.view_psk);
            sharedkey.setText(net.getShatedKey());
        }

        android.util.Log.d("WBR","view net dialog data set");

        Button okButton = (Button) dialog.findViewById(R.id.view_ok);
        // if button is clicked, close the custom dialog
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button restore = (Button) dialog.findViewById(R.id.view_restore);
        if (needRestore) {

            // if button is clicked, close the custom dialog
            restore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    NetworkListFragment.this.restoreNetwork(net);
                    Resources res = NetworkListFragment.this.getActivity().getResources();
                    String msg = res.getString(R.string.view_restoring);
                    Toast.makeText(NetworkListFragment.this.getActivity(),msg,Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            });
        }
        else {
            restore.setVisibility(View.GONE);
        }

        android.util.Log.d("WBR","view net dialog button set");

        dialog.show();

        android.util.Log.d("WBR","view net dialog shown");

/*
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle("d\u00e9tails du r\u00e9seau");
		alert.setMessage("Alias : "+net.getAlias()+" \nSSID : "+net.getSsid()+" \nkeymgmt : "+net.getKeyManagment()+" \nPSK : "+net.getShatedKey());

		// Set an EditText view to get user input 		
		alert.setPositiveButton(getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        unSelectRow(selectedView,selectedPosition);
                                    }
                                });
		alert.show();
		*/
	}


    /************************************************
     *
     * actionMode
     *
     ************************************************/

    protected ActionMode actionMode;

    protected Network selectedNetwork;

    protected View selectedView;

    protected int selectedPosition = -1;

    protected WIFIConfigurationManager confManager;

    public abstract Network getNetworkByView(View view);

    protected void selectRow(Network net, View view, int position) {
        Resources res = getActivity().getResources();
        //android:id="@+id/selectionImage" view.findViewById(
        ImageView image = (ImageView)view.findViewById(R.id.selectionImage);
        image.setImageDrawable(res.getDrawable(R.drawable.selected_wifi));
    }



    public void unSelectRow(View view, int position) {
        Resources res = getActivity().getResources();
        //android:id="@+id/selectionImage" view.findViewById(
        ImageView image = (ImageView)view.findViewById(R.id.selectionImage);
        image.setImageDrawable(res.getDrawable(R.drawable.wifi));
    }




    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        final Network net = getNetworkByView(view);

        Resources res = parent.getResources();

        Log.d("WBR","state : selectepos::"+selectedPosition+" / selectedNetwork ::"+selectedNetwork);

        if (position == selectedPosition) {
            //
            // deselection d'une ligne
            //
            selectedNetwork = null;
            selectedPosition = -1;
            selectedView = null;
            Log.d("WBR"," new state :: selectepos::"+selectedPosition+" / selectedNetwork ::"+selectedNetwork);
            Log.d("WBR","position is the same => selection @"+position);
            unSelectRow(view,position);

            Log.d("WBR","click on same position => hiding action mode ... "+(actionMode != null));
            if (actionMode != null) {
                Log.d("WBR","hiding action mode");
                actionMode.finish();
            }
        }
        else {
            //
            // selection d'une ligne (differente ou non)
            //
            Log.d("WBR","position is not the same => select @"+position+" && unselect @"+selectedPosition);
            if (selectedPosition >= 0) {
                Log.d("WBR","selectedPos["+selectedPosition+"] >= 0 ==> unselecting");
                unSelectRow(selectedView,selectedPosition);
            }
            Log.d("WBR","selecting @"+position);
            selectRow(net,view,position);

            selectedNetwork = net;
            selectedPosition = position;
            selectedView = view;
            Log.d("WBR"," new state :: selectepos::"+selectedPosition+" / selectedNetwork ::"+selectedNetwork);
            this.getActivity().startActionMode(this);
        }
        Log.d("WBR","########################################");


    }

}