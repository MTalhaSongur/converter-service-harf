package com.converterservice;

import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@RestController
public class ConverterController {

    private final Path rootLocation = Paths.get("filestorage");

    @RequestMapping("/convert")
    public String convert( @RequestBody String req) throws Exception {
        if(req == null || req == "")
            return "ERROR : No body received!! Cannot Convert Image";

        req = req.split("=")[0];
        String decodedBody = getBase64Decoded(req);
        String extension = null;
        int i = decodedBody.lastIndexOf('.');
        if (i >= 0) {
            extension = decodedBody.substring(i+1);
            System.out.println("Extension from : " + decodedBody + " is : " + extension);
        }
        else
            return "Error: No extension detected.";

        FileConverter converter = new FileConverter();
        try {
            generateOutputFolder(rootLocation.toString(), "outputs");
            generateOutputFolder(rootLocation.resolve("outputs").toString(), "images");
            generateOutputFolder(rootLocation.resolve("outputs/images/").toString(), Long.toString(converter.getID()));
            switch (extension) {
                case "pptx":
                    converter.PPTX2PNG(decodedBody,rootLocation.resolve("outputs/images/" + converter.getID()).toAbsolutePath().toString());
                    break;
                case "pdf":
                    converter.PDF2PNG(decodedBody, rootLocation.resolve("outputs/images/" + converter.getID()).toString());
                    break;
                case "docx":
                    //Sloppy way of converting docx files to pdf and will take longer. However, Apache POI cant do this alone and other libraries requires MS word to be installed on the runner machine.
                    //Get the name of the file without extension.
                    String fileName = Paths.get(decodedBody).getFileName().toString().replaceFirst("[.][^.]+$", "");
                    generateOutputFolder(rootLocation.resolve("outputs").toString(), "pdfs");
                    generateOutputFolder(rootLocation.resolve("outputs/pdfs/").toString(), Long.toString(converter.getID()));
                    converter.DOCX2PDF(decodedBody, rootLocation.resolve("outputs/pdfs/" + converter.getID()).toString());
                    converter.PDF2PNG(rootLocation.resolve("outputs/pdfs/" + converter.getID() + "/" + fileName).toString(), rootLocation.resolve("outputs/images/" + converter.getID()).toString());
                    break;
                default:
                    return "Error: Extension : + " + extension + " is not recognized.";
            }

        }catch (Exception e){
            return e.toString();
        }

        //return "path:" + rootLocation.resolve("outputs/images").toAbsolutePath() + "/" + Long.toString(converter.getID()) + ",size:" + converter.getPageSize() + ",width:" + converter.getWidth() + ",height:" + converter.getHeight();
        return "DONE";
    }

    @GetMapping("/convertdefault")
    public String convertDefault() throws Exception {

        //constructAndSendJSON();
        FileConverter converter = new FileConverter();
        try {
            generateOutputFolder(rootLocation.toString(), "outputs");
            generateOutputFolder(rootLocation.resolve("outputs").toString(), "images");
            generateOutputFolder(rootLocation.resolve("outputs/images/").toString(), Long.toString(converter.getID()));
            generateOutputFolder(rootLocation.resolve("outputs").toString(), "pdfs");
            generateOutputFolder(rootLocation.resolve("outputs/pdfs/").toString(), Long.toString(converter.getID()));
            converter.DOCX2PDF(rootLocation.resolve("inputs/deneme.docx").toString(), rootLocation.resolve("outputs/pdfs/" + converter.getID()).toString());
            converter.PDF2PNG(rootLocation.resolve("outputs/pdfs/" + converter.getID()).toString(), rootLocation.resolve("outputs/images/" + converter.getID() + "/").toString());
            //converter.PPTX2PNG(rootLocation.resolve("inputs/deneme.pptx").toString(),rootLocation.resolve("outputs/images/" + converter.getID()).toAbsolutePath().toString());
        }catch (Exception e){
            return e.toString();
        }

        return "path:" + rootLocation.resolve("outputs/images").toAbsolutePath() + "/" + converter.getID() + ",size:" + converter.getPageSize() + ",width:" + converter.getWidth() + ",height:" + converter.getHeight();
    }

    //Utilities-------------------------------------------------------------------

    private boolean generateOutputFolder(String folder, String nameOfFolder) {
        try {
            File file = new File(folder + "/" + nameOfFolder);
            if(!file.exists())
                file.mkdir();
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    private String getBase64Decoded(String encodedString) {
        //Decode the given string encoded in Base64
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        return new String(decodedBytes);
    }

    private byte[] getBase64Decoded(byte[] encodedString) {
        return Base64.getDecoder().decode(encodedString);
    }
}
