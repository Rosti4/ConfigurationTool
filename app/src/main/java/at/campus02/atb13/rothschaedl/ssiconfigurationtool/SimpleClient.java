/**
 * Prosys OPC UA Java SDK
 *
 * Copyright (c) Prosys PMS Ltd., <http://www.prosysopc.com>.
 * All rights reserved.
 */
package at.campus02.atb13.rothschaedl.ssiconfigurationtool;

import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.LocalizedText;
import org.opcfoundation.ua.core.ApplicationDescription;
import org.opcfoundation.ua.core.ApplicationType;
import org.opcfoundation.ua.core.Attributes;
import org.opcfoundation.ua.core.Identifiers;
import org.opcfoundation.ua.core.ReferenceDescription;
import org.opcfoundation.ua.transport.security.SecurityMode;

import com.prosysopc.ua.ApplicationIdentity;
import com.prosysopc.ua.SecureIdentityException;
import com.prosysopc.ua.client.UaClient;

/**
 * A very minimal client application. Connects to the server and reads one
 * variable. Works with a non-secure connection.
 */
public class SimpleClient {

	public static List<ReferenceDescription> run(View view, MyNode node) throws Exception {
		List<ReferenceDescription> references = null;
		try {
			UaClient client = new UaClient(node.getUrl());
			client.setSecurityMode(SecurityMode.NONE);
			initialize(client);
			client.setTimeout(5, TimeUnit.SECONDS);
			client.setSessionTimeout(5, TimeUnit.SECONDS);

			client.connect();
			Log.d("CLIENT", "CONNECTED");
			DataValue value = client.readValue(Identifiers.Server_ServerStatus_State);
			Log.d("CLIENT", value.toString());
			client.getAddressSpace().setMaxReferencesPerNode(1000);

			if (node.getExpandedNodeId() == null)references = client.getAddressSpace().browse(Identifiers.RootFolder);
			else references = client.getAddressSpace().browse(node.getExpandedNodeId());

			client.disconnect();
			Log.d("CLIENT", "STOP");
		} catch (Exception e) {
			Log.e("Client", "Failed", e);
		}
		return references;
	}

    public static String read(View view, MyNode node) throws Exception {
        List<ReferenceDescription> references = null;
        DataValue value2 = new DataValue();
        try {
            UaClient client = new UaClient(node.getUrl());
            client.setSecurityMode(SecurityMode.NONE);
            initialize(client);
            client.setTimeout(5, TimeUnit.SECONDS);
            client.setSessionTimeout(5, TimeUnit.SECONDS);

            client.connect();
            Log.d("CLIENT", "CONNECTED");
            DataValue value = client.readValue(Identifiers.Server_ServerStatus_State);
            Log.d("CLIENT", "General Value: " + value.toString());

            Attributes.Value.getValue();
            value2 = client.readAttribute(node.getExpandedNodeId().ID, Attributes.DataType);


            client.disconnect();
            Log.d("CLIENT", "STOP");
        } catch (Exception e) {
            Log.e("Client", "Failed", e);
        }
        return value2.toString();
    }

	/**
	 * Define a minimal ApplicationIdentity. If you use secure connections, you
	 * will also need to define the application instance certificate and manage
	 * server certificates. See the SampleConsoleClient.initialize() for a full
	 * example of that.
	 */
	protected static void initialize(UaClient client)
			throws SecureIdentityException, IOException, UnknownHostException {
		// *** Application Description is sent to the server
		ApplicationDescription appDescription = new ApplicationDescription();
		appDescription.setApplicationName(new LocalizedText("SimpleClient", Locale.ENGLISH));
		// 'localhost' (all lower case) in the URI is converted to the actual
		// host name of the computer in which the application is run
		appDescription.setApplicationUri("urn:localhost:UA:SimpleClient");
		appDescription.setProductUri("urn:prosysopc.com:UA:SimpleClient");
		appDescription.setApplicationType(ApplicationType.Client);

		final ApplicationIdentity identity = new ApplicationIdentity();
		identity.setApplicationDescription(appDescription);
		client.setApplicationIdentity(identity);
	}
	/*
	protected void write(UaClient client, NodeId nodeId) throws ServiceException, AddressSpaceException, StatusException {
		UnsignedInteger attributeId = readAttributeId();

		UaNode node = client.getAddressSpace().getNode(nodeId);
		println("Writing to node " + nodeId + " - " + node.getDisplayName().getText());

		// Find the DataType if setting Value - for other properties you must
		// find the correct data type yourself
		UaDataType dataType = null;
		if (attributeId.equals(Attributes.Value) && (node instanceof UaVariable)) {
			UaVariable v = (UaVariable) node;
			// Initialize DataType node, if it is not initialized yet
			if (v.getDataType() == null)
				v.setDataType(client.getAddressSpace().getType(v.getDataTypeId()));
			dataType = (UaDataType) v.getDataType();
			println("DataType: " + dataType.getDisplayName().getText());
		}

		print("Enter the value to write: ");
		String value = readInput(true);
		try {
			Object convertedValue = dataType != null
					? client.getAddressSpace().getDataTypeConverter().parseVariant(value, dataType) : value;
			boolean status = client.writeAttribute(nodeId, attributeId, convertedValue);
			if (status)
				println("OK");
			else
				println("OK (completes asynchronously)");
		} catch (ServiceException e) {
			printException(e);
		} catch (StatusException e) {
			printException(e);
		}

	}
*/
}
