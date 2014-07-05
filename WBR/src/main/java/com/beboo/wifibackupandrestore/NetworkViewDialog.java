package com.beboo.wifibackupandrestore;

import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beboo.wifibackupandrestore.backupmanagement.*;


import eu.inmite.android.lib.dialogs.BaseDialogFragment;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

/**
 * Created by olivier on 02/07/14.
 */
public class NetworkViewDialog extends SimpleDialogFragment {


    private Network network;
    private boolean isConfiguredTab;
    private NetworkListFragment fragment;
    private int selectedPosition;
    private View selectedView;

    public NetworkViewDialog() {

    }

    public NetworkViewDialog initDialog(Network network,boolean isConfiguredTab, NetworkListFragment fragment, int selectedPosition, View selectedView) {
        this.network = network;
        this.isConfiguredTab = isConfiguredTab;
        this.fragment = fragment;
        this.selectedPosition = selectedPosition;
        this.selectedView = selectedView;
        return this;
    }

    public static void show(FragmentActivity activity, Network network,boolean isConfiguredTab, NetworkListFragment fragment, int selectedPosition, View selectedView) {

        new NetworkViewDialog().initDialog(network, isConfiguredTab, fragment,selectedPosition,selectedView).show(activity.getSupportFragmentManager(), "VIEW_NETWORK");
    }




    @Override
    public BaseDialogFragment.Builder build(BaseDialogFragment.Builder builder) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.viewdialogfragment, null);
        if (network.getAlias() != null) {
            TextView alias = (TextView)view.findViewById(R.id.view_alias);
            alias.setText(network.getAlias());
        }
        if (network.getSsid() != null) {
            TextView ssid = (TextView)view.findViewById(R.id.view_ssid);
            ssid.setText(network.getSsid());
        }
        if (network.getKeyManagment() != null) {
            TextView keyMgmt = (TextView)view.findViewById(R.id.view_keymgmt);
            keyMgmt.setText(network.getKeyManagment());
        }
        if (network.getShatedKey() != null) {
            TextView sharedkey = (TextView)view.findViewById(R.id.view_psk);
            sharedkey.setText(network.getShatedKey());
        }


        builder.setView(view);


        builder.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragment.unSelectRow(selectedView,selectedPosition);
                ISimpleDialogListener listener = getDialogListener();
                if (listener != null) {
                    listener.onPositiveButtonClicked(0);
                }
                dismiss();
            }
        });

        if (isConfiguredTab) {
            builder.setNegativeButton(getString(R.string.restore), new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    fragment.restoreNetwork(network);
                    Resources res = getActivity().getResources();
                    String msg = res.getString(R.string.view_restoring);
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                    fragment.unSelectRow(selectedView,selectedPosition);
                    dismiss();

                    ISimpleDialogListener listener = getDialogListener();
                    if (listener != null) {
                        listener.onPositiveButtonClicked(0);
                    }
                    dismiss();
                }
            });
        }
        else {
            builder.setNegativeButton(getString(R.string.restore), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.util.Log.i("WBR","leaving network view dialog on negative button");
                    fragment.unSelectRow(selectedView,selectedPosition);
                    ISimpleDialogListener listener = getDialogListener();
                    if (listener != null) {
                        listener.onPositiveButtonClicked(0);
                    }

                    dismiss();
                }
            });

        }
        return builder;
    }


}
