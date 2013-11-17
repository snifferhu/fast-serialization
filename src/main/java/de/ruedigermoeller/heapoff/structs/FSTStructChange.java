package de.ruedigermoeller.heapoff.structs;

import de.ruedigermoeller.heapoff.bytez.Bytez;
import de.ruedigermoeller.serialization.util.FSTUtil;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: ruedi
 * Date: 01.11.13
 * Time: 14:17
 * To change this template use File | Settings | File Templates.
 */
// FIXME: move to long indizies
public class FSTStructChange implements Serializable {

    int changeOffsets[];
    int changeLength[];
    int curIndex;

    byte snapshot[]; // created by snapshotChanges, contains new byte values

    public FSTStructChange() {
        changeLength = new int[4];
        changeOffsets = new int[4];
    }

    public void addChange(int offset, int len) {
        addChange((long)offset,(long)len);
    }

    public void addChange(long offset, int len) {
        addChange((long)offset,(long)len);
    }

    public void addChange(long offset, long len) {
        if ( curIndex > 0 && changeOffsets[curIndex-1]+changeLength[curIndex-1] == offset ) {
            changeLength[curIndex-1]+=len;
            return;
        }
        if ( curIndex >= changeOffsets.length ) {
            int newOff[] = new int[changeOffsets.length*2];
            System.arraycopy(changeOffsets,0,newOff,0,changeOffsets.length);
            int newLen[] = new int[changeOffsets.length*2];
            System.arraycopy(changeLength,0,newLen,0,changeLength.length);
            changeOffsets = newOff;
            changeLength = newLen;
        }
        changeOffsets[curIndex] = (int) (offset);
        changeLength[curIndex] = (int) len;
        curIndex++;
    }

    public void rebase(int toSubtract) {
        for (int i = 0; i < curIndex; i++) {
            changeOffsets[i]-=toSubtract;
        }
    }

    /**
     * collects all changes and rebases.
     * @param originBase
     * @param origin
     */
    public void snapshotChanges(int originBase, Bytez origin) {
        int sumLen = 0;
        for (int i = 0; i < curIndex; i++) {
            sumLen += changeLength[i];
        }
        snapshot = new byte[sumLen];
        int targetIdx = 0;
        for (int i = 0; i < curIndex; i++) {
            int changeOffset = changeOffsets[i];
            int len = changeLength[i];
            for ( int ii = 0; ii < len; ii++) {
                snapshot[targetIdx++] = origin.get(changeOffset+ii);
            }
        }
        rebase(originBase);
    }

    public void applySnapshot(FSTStruct target) {
        Bytez arr = target.getBase();
        int baseIdx = (int) target.getOffset();
        int snapIdx = 0;
        for (int i = 0; i < curIndex; i++) {
            int changeOffset = changeOffsets[i];
            int len = changeLength[i];
            for ( int ii = 0; ii < len; ii++) {
                arr.put(baseIdx+changeOffset+ii,snapshot[snapIdx++]);
            }
        }
    }

    public void test_applyChangesTo(int originBase, byte[] origin, FSTStruct copy) {
        Bytez arr = copy.getBase();
        int baseIdx = (int) copy.getOffset();
        for (int i = 0; i < curIndex; i++) {
            int changeOffset = changeOffsets[i];
            int len = changeLength[i];
            for ( int ii = 0; ii < len; ii++) {
                arr.put(baseIdx+changeOffset-originBase+ii,origin[changeOffset+ii]);
            }
        }
    }

    public byte[] getSnapshot() {
        return snapshot;
    }
}
