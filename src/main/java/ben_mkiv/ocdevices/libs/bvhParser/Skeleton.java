package ben_mkiv.ocdevices.libs.bvhParser;

import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Skeleton class.
 *
 * @author Leonardo Ono (ono.leo@gmail.com)
 * https://github.com/leonardo-ono/JavaBVHParser
 */
public class Skeleton {

    private Node rootNode;
    private Motion motion;
    private final List<Node> nodes = new ArrayList<>();

    public Skeleton(ResourceLocation resource) {
        BVHParser parser = new BVHParser();
        parser.load(resource);
        rootNode = new Node(parser);
        motion = new Motion(parser);
        rootNode.fillNodesList(nodes);
    }

    public Node getRootNode() {
        return rootNode;
    }

    public Motion getMotion() {
        return motion;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public int getFrameSize() {
        return motion.getFrameSize();
    }

    public void setPose(int frameIndex) {
        rootNode.setPose(frameIndex < 0 ? null : motion.getData(frameIndex));
    }

}