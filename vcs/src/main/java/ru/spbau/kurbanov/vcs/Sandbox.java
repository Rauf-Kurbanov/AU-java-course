package ru.spbau.kurbanov.vcs;

import java.io.IOException;

public class Sandbox {

    public int test(boolean flag) {
        int res = 0;
        try {
            if (flag) {
                throw  new IOException();
            }
            System.out.println("after throw");
            res = 1;
        } catch (IOException e) {
            System.out.println("in handler");
        } finally {
            System.out.println("in finally");
        }
        System.out.println("anyway");
//        res = 3;
        return res;
    }

    public static void main(String[] args) {
        Sandbox s = new Sandbox();

        System.out.println(s.test(false));
        System.out.println();
        System.out.println(s.test(true));
    }
}
