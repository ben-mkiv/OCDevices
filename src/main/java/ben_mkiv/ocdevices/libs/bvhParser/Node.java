package ben_mkiv.ocdevices.libs.bvhParser;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.util.ArrayList;
import java.util.List;


/**
 * Node class.
 *
 * @author Leonardo Ono (ono.leo@gmail.com)
 * https://github.com/leonardo-ono/JavaBVHParser
 */
public class Node {

    public enum Type { ROOT, JOINT, END }

    private final Type type;
    private String name;
    private final Vector4d offset = new Vector4d();
    private String[] channels;
    private final Node parent;
    private final List<Node> childrens = new ArrayList<Node>();

    private static final Matrix4d transformTmp = new Matrix4d();
    private final Matrix4d transform = new Matrix4d();
    private final Vector4d position = new Vector4d();

    public Node(BVHParser parser) {
        this(parser, Type.ROOT, null);
    }

    public Node(BVHParser parser, Type type, Node parent) {
        this.type = type;
        this.parent = parent;
        switch (type) {
            case ROOT:
                parser.expect("HIERARCHY");
                name = parser.expect("ROOT")[1];
                break;
            case JOINT:
                name = parser.expect("JOINT")[1];
                break;
            case END:
                name = parser.expect("End")[1];
        }
        parser.expect("{");
        setOffset(parser.expect("OFFSET"));
        if (parser.getLine().startsWith("CHANNELS")) {
            setChannels(parser.expect("CHANNELS"));
        }
        while (parser.getLine().startsWith("JOINT")) {
            childrens.add(new Node(parser, Type.JOINT, this));
        }
        if (parser.getLine().startsWith("End")) {
            childrens.add(new Node(parser, Type.END, this));
        }
        parser.expect("}");
    }

    private void setOffset(String[] offsetStr) {
        /*
        offset.setX(Double.parseDouble(offsetStr[1]));
        offset.setY(Double.parseDouble(offsetStr[2]));
        offset.setZ(Double.parseDouble(offsetStr[3]));
        */

        //todo: figure out sane way to map to minecraft coords

        offset.setX(Double.parseDouble(offsetStr[1]));
        offset.setY(Double.parseDouble(offsetStr[3]));
        offset.setZ(-Double.parseDouble(offsetStr[2]));
        offset.setW(1);
    }

    private void setChannels(String[] channelsTmp) {
        int size = Integer.parseInt(channelsTmp[1]);
        this.channels = new String[size];
        for (int i = 0; i < size; i++) {
            this.channels[i] = channelsTmp[2 + i].toLowerCase();
        }
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Vector4d getOffset() {
        return offset;
    }

    public Vector4d getPosition() {
        return position;
    }

    public Matrix4d getTransform() {
        return transform;
    }

    public String[] getChannels() {
        return channels;
    }

    public Node getParent() {
        return parent;
    }

    public List<Node> getChildrens() {
        return childrens;
    }

    public void fillNodesList(List<Node> nodes) {
        if (type == Type.ROOT) {
            nodes.clear();
        }
        if (!nodes.contains(this)) {
            nodes.add(this);
        }
        for (Node children : childrens) {
            children.fillNodesList(nodes);
        }
    }
    private static final int[] DATA_INDEX = { 0 };

    public void setPose(double[] data) {
        transform.setIdentity();
        DATA_INDEX[0] = 0;
        setPose(data, DATA_INDEX);
    }

    private void setPose(double[] data, int[] dataIndex) {
        if (type == Type.ROOT) {
            transform.setTranslation(new Vector3d(offset.x, offset.y, offset.z));
        }
        else {
            transform.set(parent.getTransform());
            transformTmp.setTranslation(new Vector3d(offset.x, offset.y, offset.z));
            transform.mul(transformTmp);
        }

        if (channels != null && data != null) {
            for (int c = 0; c < channels.length; c++) {
                String channel = channels[c];
                double value = data[dataIndex[0]++];
                if (channel.equals("xposition")) {
                    transformTmp.setTranslation(new Vector3d(value, 0, 0));
                }
                else if (channel.equals("yposition")) {
                    transformTmp.setTranslation(new Vector3d(0, value, 0));
                }
                else if (channel.equals("zposition")) {
                    transformTmp.transform(new Vector3d(0, 0, value));
                }
                else if (channel.equals("zrotation")) {
                    transformTmp.rotX(Math.toRadians(value));
                }
                else if (channel.equals("yrotation")) {
                    transformTmp.rotY(Math.toRadians(value));
                }
                else if (channel.equals("xrotation")) {
                    transformTmp.rotZ(Math.toRadians(value));
                }
                transform.mul(transformTmp);
            }
        }

        position.set(0, 0, 0, 1);
        transform.transform(position);

        for (Node children : childrens) {
            children.setPose(data, dataIndex);
        }
    }

}