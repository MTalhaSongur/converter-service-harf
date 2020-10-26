package com.converterservice;

import com.objects.JSONManifest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.Base64;

@RestController
public class ConverterController {

    @Autowired
    private FileConverter converter;

    @RequestMapping(
            value = "/convert",
            method = RequestMethod.POST,
            produces = "application/json"
    )
    @ResponseBody
    public ResponseEntity<String> convert(@RequestParam("filePath") String filePath) {
        if(filePath == null || filePath.equals(""))
            return ResponseEntity.badRequest().body("No body received, cannot convert file!!");

        filePath = filePath.split("=")[0];
        String decodedBody = getBase64Decoded(filePath);
        String extension = null;
        int i = decodedBody.lastIndexOf('.');
        if (i >= 0) {
            extension = decodedBody.substring(i+1);
            System.out.println("Extension from : " + decodedBody + " is : " + extension);
        }
        else
            return ResponseEntity.badRequest().body("Extension from " + decodedBody + " not found");

        try {
            JSONManifest jsonManifest = new JSONManifest();
            //Note: These conversion methods used are working async to the main thread.
            switch (extension) {
                case "pptx":
                    converter.PPTX2PNG(decodedBody, jsonManifest);
                    break;
                case "pdf":
                    converter.PDF2PNG(decodedBody, "pdf", jsonManifest);
                    break;
                case "docx":
                    //Get the name of the file without extension.
                    String parentFolder = new File(decodedBody).getParent();
                    String fileName = Paths.get(decodedBody).getFileName().toString().replaceFirst("[.][^.]+$", "");
                    converter.DOCX2PDF(decodedBody, parentFolder);
                    converter.PDF2PNG(parentFolder + "/" + fileName + ".pdf", "docx", jsonManifest);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Error: Extension : + " + extension + " is not recognized.");
            }
            while (true) {
                String jsonResponse = jsonManifest.getinitialJSONResponse();
                if(jsonResponse != null)
                    return ResponseEntity.ok(jsonResponse);
                Thread.sleep(100);
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while in conversion. Err : " + e.toString());
        }
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
