package it.ethiclab.git4j;

import java.util.ArrayList;
import java.util.List;

public class GitTreeEntry implements GitObject {

    private final String octalMode;
    private final String name;
    private final byte[] sha1;
    private final List<GitTreeEntry> objects;

    public GitTreeEntry(List<GitTreeEntry> objects) {
        octalMode = null;
        name = null;
        sha1 = null;
        this.objects = objects;
    }

    public GitTreeEntry(String octalMode, String name, byte[] sha1) {
        this.octalMode = octalMode;
        this.name = name;
        this.sha1 = sha1;
        this.objects = new ArrayList<>();
    }

    public String getOctalMode() {
        return octalMode;
    }

    public String getName() {
        return name;
    }

    public byte[] getSha1() {
        return sha1;
    }

    public int length() {
        return octalMode.length() + name.length() + 22;
    }

    public List<GitTreeEntry> getObjects() {
        return objects;
    }
}
