package de.ruedigermoeller.heapofftest;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;

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
 * Date: 03.07.13
 * Time: 19:48
 * To change this template use File | Settings | File Templates.
 */
public class FSTGCMark {

    static class UseLessWrapper {
        Object wrapped;

        UseLessWrapper(Object wrapped) {
            this.wrapped = wrapped;
        }
    }

    static HashMap map = new HashMap();
    static int hmFillRange = 1000000 * 30; //
    static int mutatingRange = 2000000; //
    static int operationStep = 1000;

    int operCount;
    int milliDelayCount[] = new int[100];
    int hundredMilliDelayCount[] = new int[100];
    int secondDelayCount[] = new int[100];
    Random rand = new Random(1000);

    int stepCount = 0;
    public void operateStep() {
        stepCount++;

        if ( stepCount%100 == 0 ) {
            // enforce some tenuring
            for ( int i = 0; i < operationStep; i++) {
                int key = (int) (rand.nextDouble() * mutatingRange)+mutatingRange;
                map.put(key,  new UseLessWrapper(new UseLessWrapper(""+stepCount)));
            }
        }

        if ( stepCount%200 == 199 ) {
            // enforce some tenuring
            for ( int i = 0; i < operationStep; i++) {
                int key = (int) (rand.nextDouble() * mutatingRange)+mutatingRange*2;
                map.put(key,  new UseLessWrapper(new UseLessWrapper("a"+stepCount)));
            }
        }

        if ( stepCount%400 == 299 ) {
            // enforce some tenuring
            for ( int i = 0; i < operationStep; i++) {
                int key = (int) (rand.nextDouble() * mutatingRange)+mutatingRange*3;
                map.put(key,  new UseLessWrapper(new UseLessWrapper("a"+stepCount)));
            }
        }

        if ( stepCount%1000 == 999 ) {
            // enforce some tenuring
            for ( int i = 0; i < operationStep; i++) {
                int key = (int) (rand.nextDouble() * mutatingRange)+mutatingRange*4;
                map.put(key,  new UseLessWrapper(new UseLessWrapper("a"+stepCount)));
            }
        }

        for ( int i = 0; i < operationStep/2; i++) {
            int key = (int) (rand.nextDouble() * mutatingRange);
            map.put(key, new UseLessWrapper(new Dimension(key,key)));
        }
        for ( int i = 0; i < operationStep/8; i++) {
            int key = (int) (rand.nextDouble() * mutatingRange);
            map.put(key, new UseLessWrapper(new UseLessWrapper(new UseLessWrapper(new UseLessWrapper(new UseLessWrapper("pok"+i))))));
        }
        for ( int i = 0; i < operationStep/16; i++) {
            int key = (int) (rand.nextDouble() * mutatingRange);
            map.put(key, new UseLessWrapper(new int[50]));
        }
        for ( int i = 0; i < operationStep/32; i++) {
            int key = (int) (rand.nextDouble() * mutatingRange);
            map.put(key, ""+new UseLessWrapper(new int[100]));
        }
        for ( int i = 0; i < operationStep/32; i++) {
            int key = (int) (rand.nextDouble() * mutatingRange);
            Object[] wrapped = new Object[100];
            for (int j = 0; j < wrapped.length; j++) {
                wrapped[j] = ""+j;
            }
            map.put(key, new UseLessWrapper(wrapped));
        }
        for ( int i = 0; i < operationStep/64; i++) {
            int key = (int) (rand.nextDouble() * mutatingRange /64);
            map.put(key, new UseLessWrapper(new int[1000]));
        }
        for ( int i = 0; i < 4; i++) {
            int key = (int) (rand.nextDouble() * 16);
            map.put(key, new UseLessWrapper(new byte[1000000]));
        }
    }

    public void fillMap() {
        for ( int i = 0; i < hmFillRange; i++) {
            map.put(i, new UseLessWrapper(new UseLessWrapper(""+i)));
        }
    }

    public void run() {
        fillMap();
        System.gc();
        System.out.println("static alloc " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000 / 1000 + "mb");
        long time = System.currentTimeMillis();
        int count = 0;
        while ( (System.currentTimeMillis()-time) < runtime) {
            count++;
            long tim = System.currentTimeMillis();
            operateStep();
            int dur = (int) (System.currentTimeMillis()-tim);
            if ( dur < 100 )
                milliDelayCount[dur]++;
            else if ( dur < 10*100 )
                hundredMilliDelayCount[dur/100]++;
            else {
                secondDelayCount[dur/1000]++;
            }
        }
        System.out.println("Iterations "+count);
    }

    public void dumpResult() {
        for (int i = 0; i < milliDelayCount.length; i++) {
            int i1 = milliDelayCount[i];
            if ( i1 > 0 ) {
                System.out.println("["+i+"]\t"+i1);
            }
        }
        for (int i = 0; i < hundredMilliDelayCount.length; i++) {
            int i1 = hundredMilliDelayCount[i];
            if ( i1 > 0 ) {
                System.out.println("["+i*100+"]\t"+i1);
            }
        }
        for (int i = 0; i < secondDelayCount.length; i++) {
            int i1 = secondDelayCount[i];
            if ( i1 > 0 ) {
                System.out.println("["+i*1000+"]\t"+i1);
            }
        }
    }

    int runtime = 60000 * 5;

    public static void main( String arg[] ) {

        FSTGCMark fstgcMark = new FSTGCMark();
        fstgcMark.run();
        fstgcMark.dumpResult();

    }

}
