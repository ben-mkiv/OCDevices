package ben_mkiv.ocdevices.utils;

public class Triangle{
    public final float a, b, c, alpha, beta, gamma = 90;

    public Triangle(float a, float b){
        this.a = a;
        this.b = b;
        this.c = (float) Math.sqrt(a*a + b*b);

        float q = a*a / c;
        float p = c - q;
        float h = (float) Math.sqrt(p*q);

        this.alpha = (float) Math.toDegrees(Math.atan(h / p));
        this.beta = 180f - this.gamma - this.alpha;
    }

    static public float B(float a, float c){
        return (float) Math.sqrt(a*a - c*c);
    }

    static public float SubB(float subLength, float factor){
        return (float) Math.sqrt(subLength*factor*subLength*factor - subLength*subLength);
    }
}