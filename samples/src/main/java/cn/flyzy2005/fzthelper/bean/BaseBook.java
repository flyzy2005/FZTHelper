package cn.flyzy2005.fzthelper.bean;


import cn.flyzy2005.daoutils.anno.Ignore;
import cn.flyzy2005.daoutils.anno.PrimaryKey;

/**
 * Created by Fly on 2017/9/4.
 */

public class BaseBook {
    @PrimaryKey
    private int id;
    @Ignore
    private String test;
    @Ignore
    private String test1;
    @Ignore
    private String test2;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getTest1() {
        return test1;
    }

    public void setTest1(String test1) {
        this.test1 = test1;
    }

    public String getTest2() {
        return test2;
    }

    public void setTest2(String test2) {
        this.test2 = test2;
    }
}
