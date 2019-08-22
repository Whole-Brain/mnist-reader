package com.wholebrain.mnistreader.canvas;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

public final class SingleCanvas extends CustomCanvas {

    private double xPos, yPos;

    /**
     * Asks the {@link Canvas canvas} to draw the current image onto a specified {@link GraphicsContext graphics context}.
     * @param graphicsContext to draw onto.
     */
    protected void paint(GraphicsContext graphicsContext){
        double size = Double.min(canvas.getHeight(), canvas.getWidth());
        resolution = (int)(Double.min(size/(1.0* imageVResolution), size/(1.0* imageHResolution)));
        xPos = Math.floor((canvas.getWidth()-resolution* imageHResolution)/2.0);
        yPos = Math.floor((canvas.getHeight()-resolution* imageVResolution)/2.0);

        graphicsContext.setFill(backGroundColor);
        graphicsContext.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        for(int y = 0; y< imageVResolution; y++)
            for (int x = 0; x< imageHResolution; x++){
                graphicsContext.setFill(pallet[imageBuffers[0][y* imageVResolution +x]&0xFF]);
                graphicsContext.fillRect(xPos+x*resolution, yPos+y*resolution,resolution,resolution);
            }
    }

    @Override
    protected void notify(SizeChangeListener listener) {
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
                int currentXMouse = (int) Math.floor((mouseEvent.getX() - xPos) / resolution);
                int currentYMouse = (int) Math.floor((mouseEvent.getY() - yPos) / resolution);
                if (currentXMouse < 0 || currentXMouse >= imageHResolution
                        || currentYMouse < 0 || currentYMouse >= imageVResolution) {
                    System.out.println("Mouse out");
                    Tooltip.uninstall(canvas, pxHint);
                } else if (currentXMouse != xMouse || currentYMouse != yMouse) {
                    System.out.println("Mouse in");
                    pxHint.show(canvas, mouseEvent.getScreenX() + 10, mouseEvent.getScreenY() + 10);
                    xMouse = currentXMouse;
                    yMouse = currentYMouse;
                    pxHint.setText(getHintText());
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
    public int getIndexFor(int scrollValue) {
        return scrollValue;
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
