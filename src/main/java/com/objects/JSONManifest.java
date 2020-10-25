package com.objects;

import com.converterservice.FileConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.amqp.AbstractRabbitListenerContainerFactoryConfigurer;

import java.io.File;
import java.util.ArrayList;

public class JSONManifest {
    private String documentPath;
    private ArrayList<Pages> pagesArrayList = null;
    private String initialJSONResponse = null;

    public JSONManifest() {
        pagesArrayList = new ArrayList<>();
        initialJSONResponse = null;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentName) {
        documentName = documentName.indexOf('\\') < 0 ? documentName : documentName.replace('\\', '/');
        this.documentPath = documentName;
    }

    public void addToPages(Pages pages) {
        pagesArrayList.add(pages);
    }

    public String getinitialJSONResponse() {return initialJSONResponse;}

    public Pages getPage(int index) {
        try {
            return pagesArrayList.get(index);
        }
        catch (IndexOutOfBoundsException e) {
            System.err.println("Error : Index given does not exists.");
            return null;
        }
    }

    public void updateIsDone(int pageIndex, boolean isDone) {
        Pages pg = pagesArrayList.get(pageIndex);
        pg.setIsDone(isDone);
        pagesArrayList.set(pageIndex, pg);
    }

    public void updatePagePath(int pageIndex, String pagePath) {
        Pages pg = pagesArrayList.get(pageIndex);
        pg.setPagePath(pagePath);
        pagesArrayList.set(pageIndex, pg);
    }

    public ArrayList<Pages> getPages() {
        return pagesArrayList;
    }

    public void sendJSON() {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(this);
        }catch (JsonProcessingException e) {
            System.err.println(e.toString());
        }

        System.out.println(jsonString);
    }

    public void anticipatePagePaths(int pageSize, File sourceFile, String extension) {
        ArrayList<String > initialPagePaths = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
             initialPagePaths.add(sourceFile.getParent() + "/" + sourceFile.getName().replaceFirst("[.][^.]+$", "") + "_" + extension +"_converted_" + (i + 1) + ".png");
        }
        final ObjectMapper mapper = new ObjectMapper();
        try {
            initialJSONResponse = mapper.writeValueAsString(initialPagePaths);
        }
        catch (JsonProcessingException e) {
            System.err.println("Error while converting Anticipated path list to json response. Err : " + e.toString());
        }
    }
}
