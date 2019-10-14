package ben_mkiv.ocdevices.libs.bvhParser;

/**
 * Motion class.
 *
 * @author Leonardo Ono (ono.leo@gmail.com)
 * https://github.com/leonardo-ono/JavaBVHParser
 */
public class Motion {

    private int frameSize;
    private double frameTime;
    private double[][] data;

    public Motion(BVHParser parser) {
        parser.expect("MOTION");
        frameSize = Integer.parseInt(parser.expect("Frames:")[1]);
        frameTime = Double.parseDouble(parser.expect("Frame Time:")[2]);
        data = new double[frameSize][];
        for (int f = 0; f < frameSize; f++) {
            String[] values = parser.getLine().split("\\ ");
            data[f] = new double[values.length];
            for (int d = 0; d < values.length; d++) {
                data[f][d] = Double.parseDouble(values[d]);
            }
            parser.nextLine();
        }
    }

    public int getFrameSize() {
        return frameSize;
    }

    public double getFrameTime() {
        return frameTime;
    }

    public double[] getData(int frame) {
        return data[frame];
    }

}