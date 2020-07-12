package com.objects;

import java.util.ArrayList;
import com.objects.Pages;

public class ManifestJSON {
    private String documentName;
    private String id;
    private ArrayList<Pages> pagesArrayList = null;

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public ArrayList<Pages> getPages() {
        return pagesArrayList;
    }
}
