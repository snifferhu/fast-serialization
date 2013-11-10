package de.ruedigermoeller.serialization.serializers;

import de.ruedigermoeller.heapoff.structs.FSTStruct;
import de.ruedigermoeller.heapoff.structs.unsafeimpl.FSTStructFactory;
import de.ruedigermoeller.serialization.FSTBasicObjectSerializer;
import de.ruedigermoeller.serialization.FSTClazzInfo;
import de.ruedigermoeller.serialization.FSTObjectInput;
import de.ruedigermoeller.serialization.FSTObjectOutput;
import de.ruedigermoeller.serialization.util.FSTUtil;

import java.io.IOException;

/**
 * Copyright (c) 2012, Ruediger Moeller. All rights reserved.
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 * <p/>
 * Date: 09.11.13
 * Time: 22:06
 * To change this template use File | Settings | File Templates.
 */
public class FSTStructSerializer extends FSTBasicObjectSerializer {
    /**
     * write the contents of a given object
     */
    @Override
    public void writeObject(FSTObjectOutput out, Object toWrite, FSTClazzInfo clzInfo, FSTClazzInfo.FSTFieldInfo referencedBy, int streamPosition) throws IOException {
        FSTStruct str = (FSTStruct) toWrite;
        if ( ! str.isOffHeap() ) {
            str = str.toOffHeap();
        }
        int byteSize = str.getByteSize();
        out.writeFInt(byteSize);
        out.write( str.getBase(), str.getOffset(), byteSize);
    }

    @Override
    public Object instantiate(Class objectClass, FSTObjectInput in, FSTClazzInfo serializationInfo, FSTClazzInfo.FSTFieldInfo referencee, int streamPositioin) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        int len = in.readFInt();
        byte b[] = new byte[len];
        in.read(b);

        // need to patch clzId as it may differ from clz id in remote vm
        int clzId = FSTStructFactory.getInstance().getClzId(serializationInfo.getClazz());
        FSTUtil.getUnsafe().putInt(b, FSTStruct.bufoff + 4, clzId);

        return FSTStructFactory.getInstance().createStructWrapper(b, 0);
    }
}
