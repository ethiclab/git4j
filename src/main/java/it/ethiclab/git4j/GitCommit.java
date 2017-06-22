package it.ethiclab.git4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GitCommit implements GitObject {
    private String message;
    private String committer;
    private Date commitDate;
    private String author;
    private Date authoringDate;
    private String tree;
    private final List<String> parentCommits = new ArrayList<>();
    private String commitTimezone;
    private String authoringTimezone;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getAuthoringDate() {
        return authoringDate;
    }

    public void setAuthoringDate(Date authoringDate) {
        this.authoringDate = authoringDate;
    }

    public String getTree() {
        return tree;
    }

    public void setTree(String tree) {
        this.tree = tree;
    }

    public List<String> getParentCommits() {
        return parentCommits;
    }

    public String getCommitTimezone() {
        return commitTimezone;
    }

    public void setCommitTimezone(String commitTimezone) {
        this.commitTimezone = commitTimezone;
    }

    public String getAuthoringTimezone() {
        return authoringTimezone;
    }

    public void setAuthoringTimezone(String authoringTimezone) {
        this.authoringTimezone = authoringTimezone;
    }
}
