package org.devathon.contest2016.util;

/**
 * Created by Florian on 06.11.16 in org.devathon.contest2016.util
 */
public class IntRedirecter {
    private final int[] redirects;

    public IntRedirecter(int size) {
        this.redirects = new int[size];
    }

    public void setMap(int... ints) {
        if (ints.length % 2 != 0) throw new IllegalArgumentException();

        boolean setNext = false;
        int key = -1;
        for (int anInt : ints) {
            if (setNext)
                redirects[key] = anInt;
            else
                key = anInt;

            setNext = !setNext;
        }
    }

    public int redirect(int i) {
        return redirects[i];
    }

    public int reverseRedirect(int i) {
        for (int i1 = 0; i1 < redirects.length; i1++) {
            if (redirects[i1] == i)
                return i1;
        }
        return -1;
    }

}
