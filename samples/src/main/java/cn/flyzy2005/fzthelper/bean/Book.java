package cn.flyzy2005.fzthelper.bean;


import cn.flyzy2005.daoutils.anno.ColumnAlias;

/**
 * Created by Fly on 2017/5/22.
 */

public class Book extends BaseBook{
    @ColumnAlias(columnName = "name")
    private String name1;
    private String author;
    private String publisher;


    public String getName1() {
        return name1;
    }

    public void setName1(String name) {
        this.name1 = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
