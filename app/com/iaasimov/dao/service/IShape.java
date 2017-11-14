package com.iaasimov.dao.service;


/**
 * Created by USER on 08-07-2017.
 */

public interface IShape {
    public void drawShape();
}

class Rectangle implements IShape{
    public void drawShape(){System.out.println("drawing rectangle");}
}

class Line implements IShape{
    public void drawShape(){System.out.println("drawing Line");}
}

class noShape implements IShape{
    public void drawShape(){System.out.println("Shape not yet implimented");}
}



