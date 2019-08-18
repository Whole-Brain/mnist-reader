package com.wholebrain.mnistreader.canvas;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public final class SingleCanvas extends CustomCanvas {

    /**
     * Asks the {@link Canvas canvas} to draw the current image onto a specified {@link GraphicsContext graphics context}.
     * @param graphicsContext to draw onto.
     */
    protected void paint(GraphicsContext graphicsContext){
        double size = Double.min(canvas.getHeight(), canvas.getWidth());
        resolution = Math.floor(Double.min(size/(1.0*numberOfRows), size/(1.0*numberOfColumns)));
        xPos = Math.floor((canvas.getWidth()-resolution*numberOfColumns)/2.0);
        yPos = Math.floor((canvas.getHeight()-resolution*numberOfRows)/2.0);

        graphicsContext.setFill(backGroundColor);
        graphicsContext.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        for(int y = 0; y<numberOfRows; y++)
            for (int x = 0; x<numberOfColumns; x++){
                graphicsContext.setFill(pallet[imageBuffers[0][y*numberOfRows+x]&0xFF]);
                graphicsContext.fillRect(xPos+x*resolution, yPos+y*resolution,resolution,resolution);
            }
    }

    protected void initializeHint(Canvas canvas){
        // Hint initialization
        Tooltip pxHint = new Tooltip();
        pxHint.setShowDelay(Duration.ZERO);
        pxHint.setHideDelay(Duration.ZERO);

        canvas.setOnMouseMoved(event -> {
            if(!showHint) Tooltip.uninstall(canvas,pxHint);
            else {
                int currentXMouse = (int) Math.floor((event.getX() - xPos) / resolution);
                int currentYMouse = (int) Math.floor((event.getY() - yPos) / resolution);
                if (currentXMouse < 0 || currentXMouse >= numberOfColumns
                        || currentYMouse < 0 || currentYMouse >= numberOfRows) {
                    Tooltip.uninstall(canvas, pxHint);
                } else if (currentXMouse != xMouse || currentYMouse != yMouse) {
                    pxHint.show(canvas, event.getScreenX() + 10, event.getScreenY() + 10);
                    xMouse = currentXMouse;
                    yMouse = currentYMouse;
                    pxHint.setText(getHintText());
                    if (!pxHint.isActivated())
                        Tooltip.install(canvas, pxHint);
                }
            }

        });
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

    /**
     * Returns the text that is to show on the {@link Tooltip}.
     * @return {@link String Text}.
     */
    private String getHintText() {
        return (imageBuffers==null || imageBuffers.length == 0 )?
                "No image loaded":
                (showHintCoord? "("+xMouse+";"+yMouse+")":"")+
                        (showHintCoord&&showHintValue?"=":"")+
                        (showHintValue?(imageBuffers[0][(yMouse*numberOfColumns+xMouse)]&0xff):"");
    }

    @Override
    public int getShownImageCount() {
        return 1;
    }

    @Override
    public int getFirstShownIndex(int indexTry, int numberOfImages) {
        return indexTry;
    }

    public void loadImage(byte[] imageBuffer, int numberOfRows, int numberOfColumns, char currentChar){
        loadImages(new byte[][]{imageBuffer},numberOfRows, numberOfColumns, new char[] {currentChar});
    }
}