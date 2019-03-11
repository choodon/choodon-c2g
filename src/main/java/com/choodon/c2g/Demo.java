/*
 * Copyright 2018 The Choodon-C2G Project
 *
 *  The Choodon-C2G Project licenses this file to you under the Apache License,
 *  version 2.0 (the "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License
 */

package com.choodon.c2g;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Demo
 *
 * @author michael
 * @since 2019-03-05
 */
public class Demo {
    public int a = 1;
    private Integer b = 2;
    private String c;
    private String d;
    private String e;

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public Integer getB() {
        return b;
    }

    public void setB(Integer b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Demo{");
        sb.append("a=").append(a);
        sb.append(", b=").append(b);
        sb.append(", c='").append(c).append('\'');
        sb.append(", d='").append(d).append('\'');
        sb.append(", e='").append(e).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        int length = 1000000;
        int threads = 10;
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                Long now0 = System.currentTimeMillis();
                for (int j = 0; j < length; j++) {
                    new Demo();
                }
                Long now1 = System.currentTimeMillis();
                System.out.println("new:" + (now1 - now0));
            }).start();

            new Thread(() -> {
                Long now0 = System.currentTimeMillis();
                for (int j = 0; j < length; j++) {
                    DefaultC2G.allocate(Demo.class);
                }
                Long now1 = System.currentTimeMillis();
                System.out.println("c2g:" + (now1 - now0));
            }).start();
        }
    }
}
