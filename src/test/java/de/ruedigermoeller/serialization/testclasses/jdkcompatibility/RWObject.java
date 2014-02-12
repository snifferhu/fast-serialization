package de.ruedigermoeller.serialization.testclasses.jdkcompatibility;

import java.io.*;

/**
 * Created by ruedi on 12.02.14.
 */
public class RWObject implements Serializable {

    static class RWA extends RWObject {

        int A = 1;

        private void writeObject( ObjectOutputStream out ) {
            System.out.println("RWA:write");
        }

    }

    int root = 2;
    private void writeObject( ObjectOutputStream out ) {
        System.out.println("RWObject:write");
    }

    static class RWB extends RWA {

        int B = 3;
        private void writeObject( ObjectOutputStream out ) throws IOException {
            System.out.println("RWB:write");
            out.defaultWriteObject();
        }

    }

    static class RWC extends RWB implements Externalizable {

        int B = 3;
        private void writeObject( ObjectOutputStream out ) throws IOException {
            System.out.println("RWB:write");
            out.defaultWriteObject();
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            System.out.println("externalize RWC");
//            ((ObjectOutputStream)out).defaultWriteObject();
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        }
    }

    public static void main(String arg[]) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new ByteArrayOutputStream(50000));
        out.writeObject(new RWC());
    }

}
