package com.wholebrain.mnistreader.canvas;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

public final class SingleCanvas extends CustomCanvas {

    private double xPos, yPos;
    public SingleCanvas(){
        super();
        canvas.setOnScroll(event -> {if(event.isShiftDown())
                forceDeltaPosition(event.getDeltaX() > 0 ? -1 : 1);
            else if(event.isAltDown())
                forceDeltaPosition((int)(-event.getDeltaY()*20));
            else
                forceDeltaPosition(-(int) event.getDeltaY());
        });
    }

    /**
     * Asks the {@link Canvas canvas} to draw the current image onto a specified {@link GraphicsContext graphics context}.
     * @param graphicsContext to draw onto.
     */
    protected void paint(GraphicsContext graphicsContext){
        double size = Double.min(canvas.getHeight(), canvas.getWidth());
        setResolution((int)(Double.min(size/(1.0* imageVDefinition), size/(1.0* imageHDefinition))));
        xPos = Math.floor((canvas.getWidth()-getHorizontalDefinition())/2.0);
        yPos = Math.floor((canvas.getHeight()-getVerticalDefinition())/2.0);

        graphicsContext.setFill(backGroundColor);
        graphicsContext.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        int resolution = getResolution();
        for(int y = 0; y< imageVDefinition; y++)
            for (int x = 0; x< imageHDefinition; x++){
                graphicsContext.setFill(pallet[imageBuffers[0][y* imageVDefinition +x]&0xFF]);
                graphicsContext.fillRect(xPos+x*resolution, yPos+y*resolution,resolution,resolution);
            }
    }

    @Override
    protected void notify(ImageBufferProvider listener) {
        // Since the ScrollBar value is not affected by the size of the canvas,
        // the listener is not notified.
    }

    @Override
    public DIRECTION getScrollBarPosition() {
        return DIRECTION._BOTTOM;
    }

    /**
     * Make the canvas draw the label.
     * @param gc The canvas' {@link GraphicsContext}.
     */
    protected void paintLabels(GraphicsContext gc) {
        gc.setFill(pallet[255]);
        String toPrint = String.valueOf(currentChars.length>0?currentChars[0]:' ');
        double x = (1+4*currentLabelPosition.getHPosition())/6d*canvas.getWidth();
        double y = (1+4*currentLabelPosition.getVPosition())/6d*canvas.getHeight();
        gc.fillText(toPrint,x,y);
    }

    @Override
    protected EventHandler<MouseEvent> getHintEvent() {
        return mouseEvent -> {
            if(!showHint) Tooltip.uninstall(canvas,pxHint);
            else {
                int currentXMouse = (int) Math.floor((mouseEvent.getX() - xPos) / getResolution());
                int currentYMouse = (int) Math.floor((mouseEvent.getY() - yPos) / getResolution());
                if (currentXMouse < 0 || currentXMouse >= imageHDefinition
                        || currentYMouse < 0 || currentYMouse >= imageVDefinition) {
                    Tooltip.uninstall(canvas, pxHint);
                } else if (currentXMouse != xMouse || currentYMouse != yMouse) {
//                    pxHint.show(canvas, mouseEvent.getScreenX() + 10, mouseEvent.getScreenY() + 10);
                    pxHint.setX(mouseEvent.getScreenX()+10);
                    pxHint.setY(mouseEvent.getScreenY()+10);
                    xMouse = currentXMouse;
                    yMouse = currentYMouse;
                    pxHint.setText(getHintText(0));
                    if (!pxHint.isActivated())
                        Tooltip.install(canvas, pxHint);
                }
            }
        };
    }


    @Override
    public int getShownImageCount() {
        return 1;
    }

    @Override
    public int getIndexFor(int position) {
        return position;
    }

    @Override
    public int getScrollValueForIndex(int index) {
        return index;
    }

    @Override
    public int getScrollBarMaxValueFor(int elementCount) {
        return elementCount-1;
    }

    @Override
    public double getScrollBarUnitIncrement() {
        return 1;
    }

    public void loadImage(byte[] imageBuffer, char currentChar){
        loadImages(new byte[][]{imageBuffer}, new char[] {currentChar});
    }
}
