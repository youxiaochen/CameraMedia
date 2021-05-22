package you.chen.media;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;

/**
 * Created by you on 2018-03-28.
 */
public class Test {

    public static void main(String[] args) {

        int a = 2;

        System.out.println(10 + a++ * 3);
        System.out.println(10 + a++ * 3);


    }

    static interface B {

    }


    static class A {
        B b;
        A(B b) {
            this.b = b;
        }
    }








    static class ResultPoint {
        float x, y;

        public ResultPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        @NonNull
        @Override
        public String toString() {
            return x + " " + y;
        }
    }



    /**
     * 根据扫描成功时的四个坐标点计算
     * @param rps
     * @return
     */
    public static ResultPoint centerPoint(ResultPoint[] rps) {
        if (rps == null) return null;
        if (rps.length == 2) {//条形码
            float x = (rps[0].getX() + rps[1].getX()) / 2;
            float y = (rps[0].getY() + rps[1].getY()) / 2;
            return new ResultPoint(x, y);
        }
        if (rps.length == 4) {
            ResultPoint p1 = rps[0];
            ResultPoint p2 = rps[1];
            ResultPoint p3 = rps[2];
            ResultPoint p4 = rps[3];
            try {
                float x = (p1.getX() * (p3.getY() - p1.getY()) / (p3.getX() - p1.getX())
                        - p2.getX() * (p4.getY() - p2.getY()) / (p4.getX() - p2.getX()) + p2.getY() - p1.getY())
                        / ((p3.getY() - p1.getY()) / (p3.getX() - p1.getX()) - (p4.getY() - p2.getY())
                        / (p4.getX() - p2.getX()));
                float y = (p3.getY() - p1.getY()) * (x - p1.getX()) / (p3.getX() - p1.getX()) + p1.getY();
                return new ResultPoint(x, y);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static int ss(int a) {
        return (a + 1) & ~1;
    }

    private static void test() {
        AA aa = new AA();

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    aa.add();
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    aa.div();
                    aa.print();
                }
            }
        }.start();


        new Thread(){
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("----------------  " + aa.ints.get() + "--------------------");
                }
            }
        }.start();
    }


    static class AA {

        Object look = new Object();

        AtomicInteger ints = new AtomicInteger(0);

        AtomicBoolean ising = new AtomicBoolean(false);

        public synchronized void  add() {
            if (ising.get()) return;
            System.out.println("------- put --------");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ints.set(ints.get() + 1);
            ising.set(true);
            notify();
        }

        public synchronized void div() {
            while (!ising.get()) {
                try {
                    System.out.println("wait...");
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void print() {
            System.out.println("~~ get ~~");
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ints.set(ints.get() - 1);
            ising.set(false);
        }

    }



}
