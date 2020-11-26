package com.callumbirks.gui;

import javafx.scene.image.Image;
import javafx.embed.swing.SwingFXUtils;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;

import java.io.IOException;
import java.io.InputStream;

public class SVGLoader {
    public static Image loadSVG(String path) {
        BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
        try (InputStream svgFile = App.class.getResourceAsStream(path)) {
            TranscoderInput in = new TranscoderInput(svgFile);
            transcoder.transcode(in, null);
            return SwingFXUtils.toFXImage(transcoder.getBufferedImage(), null);
        }
        catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }
        return null;
    }
}
