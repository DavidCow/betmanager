package captcha;

import java.io.File;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;

public class TessTest {
    public static void main(String[] args) {
        File imageFile = new File("blueBox.png");
        ITesseract instance = new Tesseract1();  // JNA Interface Mapping
        instance.setLanguage("deu");
        //ITesseract instance = new Tesseract1(); // JNA Direct Mapping

        try {
            String result = instance.doOCR(imageFile);
            System.out.println(result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }
}
