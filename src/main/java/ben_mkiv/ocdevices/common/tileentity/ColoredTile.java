package ben_mkiv.ocdevices.common.tileentity;

public interface ColoredTile {
    int getColor();
    void setColor(int color);
    void onColorChanged();
}
