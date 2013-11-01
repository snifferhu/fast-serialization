package de.ruedigermoeller.heapoff.structs;

/**
 * Created with IntelliJ IDEA.
 * User: ruedi
 * Date: 01.11.13
 * Time: 14:17
 * To change this template use File | Settings | File Templates.
 */
public class FSTChangeTracker {

    int changeOffsets[];
    int changeLength[];
    int curIndex;

    public FSTChangeTracker() {
        changeLength = new int[10];
        changeOffsets = new int[10];
    }

    public void addChange(int offset, int len) {
        addChange((long)offset,(long)len);
    }

    public void addChange(long offset, int len) {
        addChange((long)offset,(long)len);
    }

    public void addChange(long offset, long len) {
        changeOffsets[curIndex] = (int) offset;
        changeLength[curIndex] = (int) len;
        curIndex++;
    }

    public void rebase(int toSubtract) {
        for (int i = 0; i < changeOffsets.length; i++) {
            changeOffsets[i]-=toSubtract;
        }
    }
}
