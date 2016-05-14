package at.campus02.atb13.rothschaedl.ssiconfigurationtool;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

//import com.prosysopc.ua.ApplicationIdentity;
//import com.prosysopc.ua.client.UaClient;
//
//import org.opcfoundation.ua.builtintypes.DataValue;
//import org.opcfoundation.ua.builtintypes.LocalizedText;
//import org.opcfoundation.ua.core.ApplicationDescription;
//import org.opcfoundation.ua.core.ApplicationType;
//import org.opcfoundation.ua.core.Identifiers;
//import org.opcfoundation.ua.transport.security.SecurityMode;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.opcfoundation.ua.core.ReferenceDescription;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    //List of Variables
    ListView mainListView;
    List<ReferenceDescription> actualNodeList;
    MyNode actNode;
    MyNode prevNode;
    private ProgressDialog progressBar;
    boolean firstLoadDone;
    FloatingActionButton fab;
    String pressedItemName;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Reference Variables ---------------------------------------------------------------------
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mainListView = (ListView) findViewById(R.id.list);

        actNode = new MyNode("opc.tcp://192.168.0.120:4840");
        prevNode = new MyNode();
        firstLoadDone = false;
        actualNodeList = null;
        pressedItemName = "Data";

        // define Listener -------------------------------------------------------------------------
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Save Previus Node
                prevNode.setExpandedNodeId(actNode.getExpandedNodeId());
                prevNode.setName(actNode.getName());

                actNode.setExpandedNodeId(actualNodeList.get(position).getNodeId());
                actNode.setName(actualNodeList.get(position).getBrowseName().toString());
                pressedItemName = actNode.getName();
                actNode.setCmd("g");
                new OPCTask().execute(actNode);
            }

        });
        mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if( actualNodeList.get(position).getNodeClass().toString() == "Variable"){
                    MyNode tempNodeid = actNode;
                    tempNodeid.setExpandedNodeId(actualNodeList.get(position).getNodeId());
                    tempNodeid.setCmd("r");
                    new OPCTask().execute(tempNodeid);
                }
                return false;
            }
        });

        setSupportActionBar(toolbar);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstLoadDone) {
                    // Go to the previous Node
                    actNode.setExpandedNodeId(prevNode.getExpandedNodeId());
                    actNode.setName(prevNode.getName());
                }
                actNode.setCmd("g");
                pressedItemName = actNode.getName();
                new OPCTask().execute(actNode);
                //Snackbar.make(view, "Connecting", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://at.campus02.atb13.rothschaedl.ssiconfigurationtool/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://at.campus02.atb13.rothschaedl.ssiconfigurationtool/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    class OPCTask extends AsyncTask<MyNode, Integer, String> {

        @Override
        protected void onPreExecute() {

            progressBar = new ProgressDialog(getWindow().getDecorView().getRootView().findViewById(R.id.list).getContext());
            progressBar.setTitle("Load " + pressedItemName);
            progressBar.setMessage("Please wait.");
            progressBar.setCancelable(false);
            progressBar.setIndeterminate(true);
            progressBar.show();
        }

        @Override
        protected String doInBackground(MyNode... nodes) {
            try {
                if (nodes[0].getCmd() == "g"){
                    Log.d("CLIENT","GET from " + nodes[0].getName());
                    actualNodeList = SimpleClient.run(null, nodes[0]);
                    for (ReferenceDescription referenceDescription : actualNodeList) {
                        //listArray.add(referenceDescription.getDisplayName().toString());
                        Log.d("CLIENT", referenceDescription.getBrowseName().toString());
                    }
                }else if (nodes[0].getCmd()== "r"){
                    Log.d("CLIENT","READ from " + nodes[0].getName());
                    String value = SimpleClient.read(null, nodes[0]);
                    Log.d("CLIENT", "-------Return Value: " + value);

                }else if (nodes[0].getCmd() == "w"){
                    Log.d("CLIENT","WRITE from " + nodes[0].getName());

                }else{

                }
            } catch (Exception e) {
                Log.e("EXCEPTION", e.getMessage(), e);
            }
            Log.d("DONE", "DONE DONE DONE");
            return "DONE";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(String s) {
            updateListView(actualNodeList, getWindow().getDecorView().getRootView().findViewById(R.id.list));
            if (progressBar != null) {
                progressBar.dismiss();
            }
            if (!firstLoadDone) {
                fab.setImageDrawable(ContextCompat.getDrawable(
                        getWindow().getDecorView().getRootView().findViewById(R.id.list).getContext(), R.drawable.ic_undo_white_36dp));
                firstLoadDone = true;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateListView(final List<ReferenceDescription> list, View view) {
        //ArrayAdapter<String> adapter;
        //String[] simplelistItem1 = new String[ listItem1.size() ];
        //listItem1.toArray( simplelistItem1 );
        try {
            //adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,simplelistItem1);
            ArrayAdapter adapter = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, list) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                    text1.setText(list.get(position).getBrowseName().toString());
                    text2.setText(list.get(position).getNodeClass().toString());
                    return view;
                }
            };
            mainListView.setAdapter(adapter);
        } catch (Exception e) {
            Log.e("EXCEPTION", e.getMessage(), e);
            Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (progressBar != null) {
            progressBar.dismiss();
        }
        super.onDestroy();
    }
}
