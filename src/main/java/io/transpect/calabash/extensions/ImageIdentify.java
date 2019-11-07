package io.transpect.calabash.extensions;
/**
 * Extension for image identifying
 *
 * @author Oliver Swoboda -- le-tex publishing services GmbH
 * @date   2013-09-11
 */

import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.URI;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageManager;
import org.apache.xmlgraphics.image.loader.ImageSessionContext;
import org.apache.xmlgraphics.image.loader.impl.DefaultImageContext;
import org.apache.xmlgraphics.image.loader.impl.DefaultImageSessionContext;

import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.io.ReadablePipe;
import com.xmlcalabash.io.WritablePipe;
import com.xmlcalabash.library.DefaultStep;
import com.xmlcalabash.model.RuntimeValue;
import com.xmlcalabash.runtime.XAtomicStep;

public class ImageIdentify extends DefaultStep {
	
  private ReadablePipe mySource = null;
  private WritablePipe myResult = null;
  private WritablePipe myReport = null;
    
  private String formatDescription, mimeType, formatDetails, colorSpace, compressionAlgorithm,
    result="<c:error xmlns:c=\"http://www.w3.org/ns/xproc-step\">No file found</c:error>";
  private int height;
  private int width;
  private int density;
  private Boolean transparency = null;
    
  public ImageIdentify (XProcRuntime runtime, XAtomicStep step) {
    super(runtime, step);
  }

  @Override
  public void setInput (String port, ReadablePipe pipe) {
    mySource = pipe;
  }

  @Override
  public void setOutput (String port, WritablePipe pipe) {
    if (port.equals("result"))
      myResult = pipe;
    if (port.equals("report"))
      myReport = pipe;
  }

  @Override
  public void reset() {
    mySource.resetReader();
    myResult.resetWriter();
    myReport.resetWriter();
  }

  @Override
  public void run() throws SaxonApiException {
    super.run();
        
    RuntimeValue rValue = getOption(new QName("href"));

    while (mySource.moreDocuments()) {
      XdmNode doc = mySource.read();
      myResult.write(doc);
    }

    try {
      if (rValue != null) {
        URI uri = rValue.getBaseURI().resolve(rValue.getString());
        File file = new File(uri);
        if (file.exists()) {
          imageIdentify(file);
        }
      }

      if (result.startsWith("<c:results"))
        result += "</c:results>";

    } catch(Exception e) {
      result = "<c:error xmlns:c=\"http://www.w3.org/ns/xproc-step\">" + e.getMessage() + "</c:error>";
      e.printStackTrace();
    }

    DocumentBuilder builder = runtime.getProcessor().newDocumentBuilder();
    Source src = new StreamSource(new StringReader(result));
    XdmNode doc = builder.build(src);
	    
    myReport.write(doc);
  }
    
  private void imageIdentify(File file) throws Exception {
    try {
      org.apache.commons.imaging.ImageInfo imageInfo = Imaging.getImageInfo(file);
					
      formatDescription = imageInfo.getFormatName();
      mimeType = imageInfo.getMimeType();
      formatDetails = imageInfo.getFormatDetails();
      height = imageInfo.getHeight();
      width = imageInfo.getWidth();
      density = imageInfo.getPhysicalHeightDpi();
      compressionAlgorithm = imageInfo.getCompressionAlgorithm().toString();
      transparency = imageInfo.isTransparent();
      colorSpace = imageInfo.getColorType().toString();
    } catch (ImageReadException e) {
      ImageManager imageManager = new ImageManager(new DefaultImageContext());
      ImageSessionContext sessionContext = new DefaultImageSessionContext(imageManager.getImageContext(), null);

      ImageInfo info = imageManager.getImageInfo(file.getPath(), sessionContext);
      density = (int) info.getSize().getDpiHorizontal();
      mimeType = info.getMimeType();
      height = info.getSize().getHeightPx();
      width = info.getSize().getWidthPx();
    }
    	
    result = "<c:results xmlns:c=\"http://www.w3.org/ns/xproc-step\" name=\""+file.getName()+"\">\n";
    getInfo();
  }

  // no longer used, to be removed soon
  private void getColorSpace(File file) throws Exception {
    Integer colorSpaceType = null;
    try {
      BufferedImage image = Imaging.getBufferedImage(file);
      colorSpaceType  = image.getColorModel().getColorSpace().getType();
    } catch (ImageReadException er) {
      try {
        BufferedImage image = ImageIO.read(file);
        colorSpaceType = image.getColorModel().getColorSpace().getType();
      } catch (IIOException e) {
        ICC_Profile iccProfile = Imaging.getICCProfile(file);
        if (iccProfile != null) {
          colorSpaceType = iccProfile.getColorSpaceType();
        }
      }
    }
		
    for (Field field : ColorSpace.class.getDeclaredFields()) {
      if (field.getName().contains("TYPE") || field.getName().contains("CS")) {
        int value = field.getInt(null);
        if (colorSpaceType == value) {
          String name = field.getName();
          colorSpace = name.substring(name.indexOf("_")+1);
        }
      }
    }
  }
    
  private void getInfo() {
    	    	
    result += "\t<c:result name=\"mimetype\" value=\""+mimeType+"\"/>\n";
		
    if (formatDescription != null)
      result += "\t<c:result name=\"formatdescription\" value=\""+formatDescription+"\"/>\n";
		
    if (formatDetails != null)
      result += "\t<c:result name=\"formatdetails\" value=\""+formatDetails+"\"/>\n";
		
    result += "\t<c:result name=\"width\" value=\""+width+"px\"/>\n";
		
    result += "\t<c:result name=\"height\" value=\""+height+"px\"/>\n";
		
    if (density == -1) {
      result += "\t<c:result name=\"density\" value=\"72dpi\"/>\n";
    } else {
      result += "\t<c:result name=\"density\" value=\""+density+"dpi\"/>\n";
    }
		
    if (colorSpace != null)
      result += "\t<c:result name=\"colorspace\" value=\""+colorSpace+"\"/>\n";
		
    if (transparency != null)
      result += "\t<c:result name=\"transparency\" value=\""+transparency+"\"/>\n";
		
    if (compressionAlgorithm != null)
      result += "\t<c:result name=\"compressionalgorithm\" value=\""+compressionAlgorithm+"\"/>\n";
		
  }
}
