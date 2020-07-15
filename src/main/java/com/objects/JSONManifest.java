package com.objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class JSONManifest {
    private String documentPath;
    private long id;
    private ArrayList<Pages> pagesArrayList = null;

    public JSONManifest() {
        pagesArrayList = new ArrayList<>();
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentName) {
        this.documentPath = documentName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void addToPages(Pages pages) {
        pagesArrayList.add(pages);
    }

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
            System.out.println(jsonString);
        }catch (JsonProcessingException e) {
            System.err.println(e.toString());
        }


    }

}
