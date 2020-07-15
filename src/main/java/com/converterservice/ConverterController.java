package com.converterservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.objects.JSONManifest;
import com.objects.Pages;
import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
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

        req = req.split("=")[1];
        String decodedBody = getBase64Decoded(req);

        String extension;
        int i = req.lastIndexOf('.');
        if (i >= 0) {
            extension = req.substring(i+1);
            System.out.println("Extension from : " + req + " is : " + extension);
        }

        FileConverter converter = new FileConverter();
        try {
            generateOutputFolder(rootLocation.resolve("outputs/images/").toString(), Long.toString(converter.getID()));
            converter.PPTX2PNG(new FileInputStream(decodedBody),rootLocation.resolve("outputs/images/" + converter.getID()).toAbsolutePath().toString(), decodedBody);

        }catch (Exception e){
            return e.toString();
        }

        return "path:" + rootLocation.resolve("outputs/images").toAbsolutePath() + "/" + Long.toString(converter.getID()) + ",size:" + converter.getPageSize() + ",width:" + converter.getWidth() + ",height:" + converter.getHeight();
    }

    @GetMapping("/convertdefault")
    public String convertDefault() throws Exception {

        //constructAndSendJSON();
        FileConverter converter = new FileConverter();
        try {
            generateOutputFolder(rootLocation.resolve("outputs/images/").toString(), Long.toString(converter.getID()));
            converter.PPTX2PNG(new FileInputStream(rootLocation.resolve("inputs/deneme.pptx").toString()),rootLocation.resolve("outputs/images/" + converter.getID()).toAbsolutePath().toString(), rootLocation.resolve("inputs/deneme.pptx").toString());
        }catch (Exception e){
            return e.toString();
        }

        return "path:" + rootLocation.resolve("outputs/images").toAbsolutePath() + "/" + Long.toString(converter.getID()) + ",size:" + converter.getPageSize() + ",width:" + converter.getWidth() + ",height:" + converter.getHeight();
    }

    //Utilities-------------------------------------------------------------------

    private boolean generateOutputFolder(String folder, String nameOfFolder) {
        try {
            File file = new File(folder + "/" + nameOfFolder);
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
