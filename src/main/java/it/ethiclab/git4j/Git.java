package it.ethiclab.git4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class Git {

    private final String encoding;
    private final MessageDigest digest;

    public Git() {
        this("SHA1", "utf-8");
    }

    public Git(String algorithm, String encoding) {
        this.encoding = encoding;
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new GitException(e);
        }
    }

    public byte[] getBlobSha1(String input) {
        byte[] blobString = serialize(new GitBlob(input));
        return getBinarySha1(blobString);
    }

    public byte[] getBytes(String input) {
        try {
            return input.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            throw new GitException(e);
        }
    }

    public byte[] getBinarySha1(byte[] input) {
        return digest.digest(input);
    }

    public String binaryToHex(byte[] hashBytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hashBytes.length; i++) {
            sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public byte[] getTreeSha(GitTreeEntry tree) {
        return getBinarySha1(serialize(tree));
    }

    public Calendar getCalendar(String timezone) {
        Calendar zero = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        long gmtTime = zero.getTime().getTime();

        long offset = gmtTime + TimeZone.getTimeZone(timezone).getRawOffset();
        Calendar r = Calendar.getInstance(TimeZone.getTimeZone(timezone));
        r.setTimeInMillis(offset);

        return r;
    }

    private String format(String timezone) {
        Calendar calendar = getCalendar(timezone);
        TimeZone tz = calendar.getTimeZone();
        SimpleDateFormat sdf = new SimpleDateFormat("ZZ");
        sdf.setTimeZone(tz);
        return sdf.format(calendar.getTime());
    }

    public byte[] getSha(GitObject object) {
        return getBinarySha1(serialize(object));
    }

    public byte[] serialize(GitObject object) {
        if (object instanceof GitCommit) {
            return serialize((GitCommit) object);
        } else if (object instanceof GitBlob) {
            return serialize((GitBlob) object);
        } else if (object instanceof GitTreeEntry) {
            return serialize((GitTreeEntry) object);
        }
        throw new GitException("unsupported object " + object);
    }

    public byte[] serialize(GitCommit commit) {
        StringBuilder sb = new StringBuilder();

        sb.append("tree");
        sb.append(' ');
        sb.append(commit.getTree());
        sb.append('\n');

        if (!commit.getParentCommits().isEmpty()) {
            for(String parent : commit.getParentCommits()) {
                sb.append("parent");
                sb.append(" ");
                sb.append(parent);
                sb.append("\n");
            }
        }

        sb.append("author " + commit.getAuthor() + " " + commit.getAuthoringDate().getTime() + " " + format(commit.getAuthoringTimezone()));
        sb.append('\n');
        sb.append("committer " + commit.getCommitter() + " " + commit.getCommitDate().getTime() + " " + format(commit.getCommitTimezone()));
        sb.append("\n");
        sb.append("\n");
        sb.append(commit.getMessage());
        sb.append("\n");

        StringBuilder commitBuilder = new StringBuilder();
        commitBuilder.append("commit");
        commitBuilder.append(' ');
        commitBuilder.append(sb.length());
        commitBuilder.append('\0');
        commitBuilder.append(sb.toString());

        System.out.println(commitBuilder.toString());

        return getBytes(commitBuilder.toString());
    }

    public byte[] serialize(GitBlob commit) {
        String content = commit.getContent();
        return getBytes("blob " + content.length() + "\000" + content);
    }

    public byte[] serialize(GitTreeEntry tree) {
        int length = 0;

        for (GitTreeEntry object : tree.getObjects()) {
            length+=object.length();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("tree");
        sb.append(' ');
        sb.append(length);
        ByteBuffer bb = ByteBuffer.allocate(8 + length);
        bb.put(getBytes(sb.toString()));
        bb.put((byte) 0);

        for (GitTreeEntry object : tree.getObjects()) {
            String strObject = object.getOctalMode() + " " + object.getName();
            bb.put(getBytes(strObject));
            bb.put((byte) 0);
            bb.put(object.getSha1());
        }

        return bb.array();
    }

    private void doCopy(InputStream is, OutputStream os, int skip) throws IOException {
        int oneByte;
        is.skip(skip);
        StringBuilder sb = new StringBuilder();
        while ((oneByte = is.read()) != 0) {
            sb.append((char) oneByte);
        }
        int length = Integer.parseInt(sb.toString());
        byte[] buf = new byte[length];
        is.read(buf);
        os.write(buf);
        os.flush();
        os.close();
        is.close();
    }

    public byte[] compress(byte[] input) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DeflaterOutputStream compressor = new DeflaterOutputStream(out);
        try {
            compressor.write(input);
            compressor.flush();
            compressor.close();
        } catch (IOException e) {
            throw new GitException(e);
        }
        return out.toByteArray();
    }

    public void uncompress(File dest, File file) {
        try {
            InflaterInputStream zip = new InflaterInputStream(new FileInputStream(file));
            FileOutputStream out = new FileOutputStream(dest);
            doCopy(zip, out, 5);
        }
        catch (Exception e) {
            throw new GitException(e);
        }
    }

    public void checkout(GitTreeEntry tree, Path root, Path path) {
        path.toFile().mkdir();
        for (GitTreeEntry entry : tree.getObjects()) {
            Path itemPath = Paths.get(path.toFile().getAbsolutePath(), entry.getName());
            if (!entry.getObjects().isEmpty()) {
                checkout(entry, root, itemPath);
            } else {
                String sha = binaryToHex(entry.getSha1());
                Path blob = Paths.get(root.toFile().getAbsolutePath(), ".git4j", "objects", sha.substring(0, 2), sha.substring(2));
                uncompress(itemPath.toFile(), blob.toFile());
            }
        }
    }
}
