package com.iaasimov.dao.service;

/**
 * Created by USER on 08-07-2017.
 */

public class Canvas {
    public void setcolor(String color)
    {
        mycolor= color;
    }
    private void createcanvas()
    {
        {System.out.println("Shape not yet implimented");}
    }
    public IShape createShape(String type)
    {
        //I am not sure whether you want to draw the canvas at the start or when the request for drawing comes
        createcanvas();

        if (type == "horizontalline")
            return new Line();
        else if (type == "rectangle")
            return  new Rectangle();
        else
            return new noShape();
    }
    public void drawshape()
    {

    }

    private
    String mycolor;
}
