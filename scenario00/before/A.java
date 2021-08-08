package scenario00.before;

import java.io.File;

public class A {

    StringBuffer sb = new StringBuffer();
    File f = new File("");

    B b = new B();

    private int a(){
        return 0;
    }

    private int b(){
        return a() + 1;
    }

    public int c(){
        return a() + b() + 2;
    }
}
