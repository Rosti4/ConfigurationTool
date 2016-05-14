package at.campus02.atb13.rothschaedl.ssiconfigurationtool;

import com.prosysopc.ua.UaAddress;

import org.opcfoundation.ua.builtintypes.ExpandedNodeId;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.core.Identifiers;

import java.net.URISyntaxException;

/**
 * Created by Stephan on 14.05.2016.
 */
public class MyNode{
    public MyNode(ExpandedNodeId expandedNodeId, String url) {
        this.expandedNodeId = expandedNodeId;
        this.url = url;
        this.cmd = "g";
        this.name = "ROOT";
    }

    public MyNode(String url) {
        this.url = url;
        this.cmd = "g";
        this.name = "ROOT";
    }
    public MyNode() {
        this.cmd = "g";
        this.url = "";
        this.name = "ROOT";
    }

    public ExpandedNodeId getExpandedNodeId() {
        return expandedNodeId;
    }

    public void setExpandedNodeId(ExpandedNodeId expandedNodeId) {
        this.expandedNodeId = expandedNodeId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    private ExpandedNodeId expandedNodeId;
    private String url;
    private String cmd;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
}
