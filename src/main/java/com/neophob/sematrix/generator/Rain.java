/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
 *
 * This file is part of PixelController.
 *
 * PixelController is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PixelController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PixelController.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.neophob.sematrix.generator;

import com.neophob.sematrix.resize.Resize.ResizeName;
import java.util.List;

/**
 * A Generator to create a rain effect with pixels raining from the top of the
 * matrix having a fading out tail.
 * 
 * @author McGyver
 */
public class Rain extends Generator {

    private int matrixPixelCount;
    
    private final int fadeLength = 50;
    
    private int runner;
    
    private int lastnumber;

    /**
     * Instantiates a new rain
     *
     * @param controller the controller
     */
    public Rain(PixelControllerGenerator controller) {
        super(controller, GeneratorName.RAIN, ResizeName.QUALITY_RESIZE);

        runner = fadeLength;
        lastnumber = 0;
        

    }

    @Override
    public void update() {
        matrixPixelCount = this.internalBufferXSize / com.neophob.sematrix.glue.MatrixData.INTERNAL_BUFFER_SIZE;
        if(runner == fadeLength){
            int number = (int) Math.floor(Math.random()*matrixPixelCount);
            if(number == lastnumber){
                number = (number+4) % matrixPixelCount;
            }
            setPixel(number);
            lastnumber = number;
        }
        
        //reduce light of whole matrix of 1
        shiftDown();
        runner --;
        if(runner == 0){
            runner = fadeLength;
        }
    }

    private void setPixel(int x) {
       for (int j = 0; j < com.neophob.sematrix.glue.MatrixData.INTERNAL_BUFFER_SIZE; j++) {
           this.internalBuffer[x*com.neophob.sematrix.glue.MatrixData.INTERNAL_BUFFER_SIZE + j] = (255 << 16) | (255 << 8) | 255;
       }
    }
    
    private void shiftDown(){
        for (int x = 0; x < internalBufferXSize; x++) {
            for (int y = internalBufferYSize-2; y >= 0 ; y--) {
                this.internalBuffer[(y+1) * internalBufferXSize + x] = this.internalBuffer[y * internalBufferXSize + x];
            }
            int currColor = this.internalBuffer[x] & 255;
            if(currColor > 0){
                currColor--;
                if(currColor > 0){
                    currColor--;
                }
                if(currColor > 0){
                    currColor--;
                }
                this.internalBuffer[x] = (currColor << 16) | (currColor << 8) | currColor;
            }
        }
    }
}

