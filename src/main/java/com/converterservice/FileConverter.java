package com.converterservice;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.objects.JSONManifest;
import com.objects.Pages;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.util.FileSystemUtils;

import javax.imageio.ImageIO;

import static java.lang.System.out;

//This class is responsible for getting pptx file from sender and returning the .png file instead.
public class FileConverter {

    private InputStream inStream;
    private OutputStream outStream;
    List<XSLFSlide> slides;
    private int pageSize;
    private int width;
    private int height;
    private final long ID;

    public FileConverter() {
        ID = getRandomNumber(10000000, 99999999);
    }

    public long getID() {
        return ID;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    private void setPageSize(int size) {
        pageSize = size;
    }

    private void setWidth(int width) {
        this.width = width;
    }

    private void setHeight(int height) {
        this.height = height;
    }


    /* Commented after taking out itext as dependency. Include it again in pom.xml if you want to use this function.
    public void PPTX2PDF(InputStream inStream, OutputStream outStream) throws Exception {
        //TODO:Remnant from constructor. Remove the variables.
        this.inStream = inStream;
        this.outStream = outStream;

        Dimension pgsize = processSlides();

        double zoom = 1; // magnify it by 2 as typical slides are low res
        AffineTransform at = new AffineTransform();
        at.setToScale(zoom, zoom);

        Document document = new Document();

        PdfWriter writer = PdfWriter.getInstance(document, outStream);
        document.open();

        //Set the page size for future use outside of this class.
        setPageSize(getNumSlides());

        for (int i = 0; i < getNumSlides(); i++) {

            BufferedImage bufImg = new BufferedImage((int)Math.ceil(pgsize.width*zoom), (int)Math.ceil(pgsize.height*zoom), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = bufImg.createGraphics();
            graphics.setTransform(at);
            //clear the drawing area
            graphics.setPaint(getSlideBGColor(i));
            graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));
            try{
                drawOntoThisGraphic(i, graphics);
            } catch(Exception e){
                //Just ignore
            }

            Image image = Image.getInstance(bufImg, null);
            document.setPageSize(new Rectangle(image.getScaledWidth(), image.getScaledHeight()));
            document.newPage();
            image.setAbsolutePosition(0, 0);
            document.add(image);
        }
        document.close();
        writer.close();
    }
     */

    public void PDF2PNG(String sourceFilePath, String targetFolder) throws Exception {
        File sourceFile = new File(sourceFilePath);
        File destinationFile = new File(targetFolder);
        if(!destinationFile.exists()) {
            out.println("No folder detected at given path. Creating a new folder");
            destinationFile.mkdir();
            out.println("Folder Created  at-> "+ destinationFile.getAbsolutePath());
        }
        if(!sourceFile.exists()) {
            System.err.println("ERROR : Source folder does not exists!!!");
            return;
        }
        out.println("Images copied to Folder: "+ destinationFile.getName());
        PDDocument document = PDDocument.load(sourceFilePath);
        List<PDPage> list = document.getDocumentCatalog().getAllPages();

        //GET IMAGE WIDTH AND HEIGHT
        setWidth(list.get(0).convertToImage().getWidth());
        setHeight(list.get(0).convertToImage().getHeight());
        //-

        out.println("Total files to be converted -> "+ list.size());

        //Set the page size
        setPageSize(list.size());

        //Prepare the json file for rest response.
        JSONManifest jsonManifest = new JSONManifest();
        initiateJSONManifest(jsonManifest, ID, sourceFilePath, pageSize);
        jsonManifest.sendJSON();
        //-

        int pageNumber = 1;
        for (PDPage page : list) {
            BufferedImage image = page.convertToImage();
            File outputfile = new File(targetFolder + "/" + pageNumber +".png");
            out.println("Image Created -> "+ outputfile.getName());
            ImageIO.write(image, "png", outputfile);
            jsonManifest.updatePagePath(pageNumber - 1, outputfile.getAbsolutePath());
            jsonManifest.updateIsDone(pageNumber - 1, true);
            pageNumber++;
            jsonManifest.sendJSON();
        }
        document.close();
        out.println("Converted Images are saved at -> "+ destinationFile.getAbsolutePath());
    }

    public void PPTX2PNG(String path, String targetFolder) throws Exception {
        File sourceFile = new File(path);
        File destinationFile = new File(targetFolder);
        if(!destinationFile.exists()) {
            out.println("No folder detected at given path. Creating a new folder");
            destinationFile.mkdir();
            out.println("Folder Created  at-> "+ destinationFile.getAbsolutePath());
        }
        if(!sourceFile.exists()) {
            System.err.println("ERROR : Source folder does not exists!!!");
            return;
        }

        XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(path));
        if(inStream != null)
            inStream.close();
        double zoom = 2; // MAGNIFIER
        AffineTransform at = new AffineTransform();
        at.setToScale(zoom, zoom);

        Dimension pgsize = ppt.getPageSize();

        //Get width and height
        setWidth(pgsize.width * (int)zoom);
        setHeight(pgsize.height * (int)zoom);
        //-

        slides = ppt.getSlides();
        //get the number of slides
        setPageSize(slides.size());

        //Set the json manifest and send it before any pages are actually rendered.
        JSONManifest jsonManifest = new JSONManifest();
        initiateJSONManifest(jsonManifest, ID, path, slides.size());
        jsonManifest.sendJSON();

        for (int i = 0; i < slides.size(); i++) {
            BufferedImage img = new BufferedImage((int)Math.ceil(pgsize.width*zoom), (int)Math.ceil(pgsize.height*zoom), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = img.createGraphics();
            graphics.setTransform(at);

            graphics.setPaint(Color.white);
            graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

            //Render
            try {
                slides.get(i).draw(graphics);
            }catch (Exception e) {
                //Just Ignore. For some reason Apache POI throws an exception every time it finishes rendering a page.
            }

            File outputfile = new File(targetFolder + "/" + (i + 1) +".png");
            out.println("Image Created -> "+ outputfile.getName());
            ImageIO.write(img, "png", outputfile);
            jsonManifest.updateIsDone(i, true);
            jsonManifest.updatePagePath(i, outputfile.getAbsolutePath());
            jsonManifest.sendJSON();
        }

        out.println("Converted Images are saved at -> "+ targetFolder);
    }

    public void DOCX2PDF(String docPath, String pdfPath) {
        //Get the input file name.
        Path inputPath = Paths.get(docPath);
        String fileName = inputPath.getFileName().toString().substring(0, inputPath.getFileName().toString().lastIndexOf('.'));
        pdfPath = pdfPath + File.separator + fileName + ".pdf";
        try {
            InputStream doc = new FileInputStream(docPath);
            XWPFDocument document = new XWPFDocument(doc);
            PdfOptions options = PdfOptions.create();
            OutputStream out = new FileOutputStream(new File(pdfPath));
            PdfConverter.getInstance().convert(document, out, options);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public boolean resetFiles() {
        final Path filesLoc = Paths.get("outputs");
        boolean status = true;
        FileSystemUtils.deleteRecursively(filesLoc.toFile());
        File file = new File(filesLoc.toString() + "/outputs/images");
        status = file.mkdirs();
        file = new File(filesLoc.toString() + "/outputs/pdf");
        status = file.mkdirs();
        return status;
    }

    private void initiateJSONManifest(JSONManifest jsonManifest, long id, String path, int pageSize) {
        jsonManifest.setDocumentPath(path);
        jsonManifest.setId(id);

        for(int i = 0; i < pageSize; i++) {
            Pages page = new Pages();
            page.setPageNumber(i + 1);
            jsonManifest.addToPages(page);
        }
    }

    private Dimension processSlides() throws IOException{
        InputStream iStream = inStream;
        XMLSlideShow ppt = new XMLSlideShow(iStream);
        Dimension dimension = ppt.getPageSize();
        slides = ppt.getSlides();
        return dimension;
    }

    private int getNumSlides(){
        return slides.size();
    }

    private void drawOntoThisGraphic(int index, Graphics2D graphics){
        slides.get(index).draw(graphics);
    }

    private Color getSlideBGColor(int index){
        return slides.get(index).getBackground().getFillColor();
    }

    private long getRandomNumber(long sIndex, long fIndex) {
        return (long)(Math.random()*((fIndex-sIndex)+1))+sIndex;
    }

}