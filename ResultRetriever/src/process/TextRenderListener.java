/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package process;

/**
 *
 * @author Nikhil
 */
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import java.io.ByteArrayOutputStream;
 
public class TextRenderListener implements RenderListener {
 
    protected ByteArrayOutputStream out;

    public TextRenderListener(ByteArrayOutputStream out) {
        this.out = out;
    }
 
    @Override
    public void beginTextBlock() {
        
    }
 
    @Override
    public void endTextBlock() {
        out.write('\n');
    }
 
    @Override
    public void renderImage(ImageRenderInfo renderInfo) {
    }
 
    @Override
    public void renderText(TextRenderInfo renderInfo) {
       // out.print("<");
        String text=renderInfo.getText();
        out.write(text.getBytes(), 0, text.length());
        //out.print(">");
    }
}