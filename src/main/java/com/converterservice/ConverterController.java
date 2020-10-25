package com.converterservice;

import com.objects.JSONManifest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;

@RestController
public class ConverterController {

    @Autowired
    private FileConverter converter;

    private final Path rootLocation = Paths.get("filestorage");

    @RequestMapping(
            value = "/convert",
            method = RequestMethod.POST,
            produces = "application/json"
    )
    @ResponseBody
    public String convert(@RequestParam("filePath") String filePath) {
        if(filePath == null || filePath.equals(""))
            return "ERROR : No body received!! Cannot Convert Image";

        filePath = filePath.split("=")[0];
        String decodedBody = getBase64Decoded(filePath);
        String extension = null;
        int i = decodedBody.lastIndexOf('.');
        if (i >= 0) {
            extension = decodedBody.substring(i+1);
            System.out.println("Extension from : " + decodedBody + " is : " + extension);
        }
        else
            return "Error: No extension detected.";

        try {
            switch (extension) {
                case "pptx":
                    JSONManifest jsonManifest = new JSONManifest();
                    converter.PPTX2PNG(decodedBody, jsonManifest);
                    while (true) {
                        String jsonResponse = jsonManifest.getinitialJSONResponse();
                        if(jsonManifest != null)
                            return jsonResponse;
                    }
                case "pdf":
                    converter.PDF2PNG(decodedBody, "pdf");
                    break;
                case "docx":
                    //Sloppy way of converting docx files to pdf and will take longer. However, Apache POI cant do this alone and other libraries requires MS word to be installed on the runner machine.
                    //Get the name of the file without extension.
                    String parentFolder = new File(decodedBody).getParent();
                    String fileName = Paths.get(decodedBody).getFileName().toString().replaceFirst("[.][^.]+$", "");
                    converter.DOCX2PDF(decodedBody, parentFolder);
                    converter.PDF2PNG(parentFolder + "/" + fileName + ".pdf", "docx");
                    break;
                default:
                    return "Error: Extension : + " + extension + " is not recognized.";
            }

        }catch (Exception e){
            return e.toString();
        }

        //return "path:" + rootLocation.resolve("outputs/images").toAbsolutePath() + "/" + Long.toString(converter.getID()) + ",size:" + converter.getPageSize() + ",width:" + converter.getWidth() + ",height:" + converter.getHeight();
        converter = null;
        return "DONE";
    }
    //Utilities-------------------------------------------------------------------

    private void generateOutputFolder(String folder, String nameOfFolder) {
        try {
            File file = new File(folder + "/" + nameOfFolder);
            if(!file.exists())
                file.mkdir();
        }catch (Exception e) {
            System.err.println("Error while creating " + nameOfFolder + ". Err : " + e.toString());
        }
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
