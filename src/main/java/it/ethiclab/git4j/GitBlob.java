package it.ethiclab.git4j;

public class GitBlob implements GitObject {
    private final String content;
    public GitBlob(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
