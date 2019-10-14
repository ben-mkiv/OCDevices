package ben_mkiv.ocdevices.libs.bvhParser;

import ben_mkiv.commons0815.utils.utilsCommon;
import ben_mkiv.ocdevices.OCDevices;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * BVHParser class.
 *
 * @author Leonardo Ono (ono.leo@gmail.com)
 * https://github.com/leonardo-ono/JavaBVHParser
 */
public class BVHParser {

    public static BVHParser INSTANCE = new BVHParser();

    private String line = "";
    private BufferedReader br;

    public void load(ResourceLocation resource) {
        InputStream is = getClass().getClassLoader().getResourceAsStream("assets/"+resource.getNamespace()+"/"+resource.getPath());
        InputStreamReader isr = new InputStreamReader(is);
        br = new BufferedReader(isr);
        nextLine();
    }

    public String getLine() {
        return line;
    }

    public void nextLine() {
        try {
            line = br.readLine();
            if (line == null) {
                line = "";
            }
            line = line.trim();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String[] expect(String token) {
        if (!line.startsWith(token)) {
            throw new RuntimeException("Expected '" + token + "' token !");
        }
        String[] tokens = line.split("\\s+");
        nextLine();
        return tokens;
    }

    public void close() {
        try {
            br.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}