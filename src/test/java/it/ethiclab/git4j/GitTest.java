package it.ethiclab.git4j;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GitTest {

    private Git g = new Git();
    private static final String TIMEZONE = "GMT+2";
    private final Calendar calendar = g.getCalendar(TIMEZONE);

    @Test
    public void testInvalidObject() {
        assertThatThrownBy(() -> g.serialize(new GitObject() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        })).isInstanceOf(GitException.class).hasMessageContaining("unsupported object it.ethiclab.git4j.GitTest$1");
    }

    @Test
    public void testHelloWorldSha1() {
        assertThat(g.binaryToHex(g.getBlobSha1("Hello World!\n\n"))).isEqualTo("ea2fd5c3fa7abbc3b05bade4a1c9ea0a5c3f1758");
    }

    @Test
    public void testTreeWith1Item() {
        assertThat(g.binaryToHex(g.getTreeSha(createInitialTree())))
                .isEqualTo("132bfb311556de7c60c34ef3d450c9e8bcc6310b");
    }

    private GitTreeEntry createInitialTree() {
        List<GitTreeEntry> objects = new ArrayList<>();
        objects.add(createFileEntry("pippo", "Hello World!\n\n"));
        return new GitTreeEntry(objects);
    }

    @Test
    public void testTreeWith2Items() {
        assertThat(g.binaryToHex(g.getTreeSha(createSecondTree())))
                .isEqualTo("a48464603b7d2519a92794f59232caeda2c829f6");
    }

    private GitTreeEntry createSecondTree() {
        List<GitTreeEntry> objects = new ArrayList<>();
        objects.add(createFileEntry("pippo", "Hello World!\n\n"));
        objects.add(createFileEntry("pluto", "Hello World!\n\n"));
        return new GitTreeEntry(objects);
    }

    private GitTreeEntry createThirdTree() {
        List<GitTreeEntry> objects = new ArrayList<>();
        objects.add(createFileEntry("pippo", "Hello World!\nHello Developer!\n\n"));
        objects.add(createFileEntry("pluto", "Hello World!\n\n"));
        return new GitTreeEntry(objects);
    }

    private GitTreeEntry createFourthTree() {
        List<GitTreeEntry> objects = new ArrayList<>();
        objects.add(createFileEntry("pippo", "Hello World!\n\n"));
        objects.add(createFileEntry("pluto", "Hello World!\nHello Master!\n\n"));
        return new GitTreeEntry(objects);
    }

    private GitTreeEntry createMergeTree() {
        List<GitTreeEntry> objects = new ArrayList<>();
        objects.add(createFileEntry("pippo", "Hello World!\nHello Developer!\n\n"));
        objects.add(createFileEntry("pluto", "Hello World!\nHello Master!\n\n"));
        return new GitTreeEntry(objects);
    }

    private GitTreeEntry createFileEntry(String name, String content) {
        return new GitTreeEntry("100644", name, g.getBlobSha1(content));
    }

    @Test
    public void testInvalidAlgorithm() {
        assertThatThrownBy(() -> new Git("Pippo", "utf-8"))
                .isInstanceOf(GitException.class)
                .hasMessage("java.security.NoSuchAlgorithmException: Pippo MessageDigest not available")
                .hasCause(new NoSuchAlgorithmException("Pippo MessageDigest not available"));
    }

    @Test
    public void testInvalidEncoding() {
        assertThatThrownBy(() -> new Git("SHA1", "Pippo").getBytes("Pluto"))
                .isInstanceOf(GitException.class)
                .hasMessage("java.io.UnsupportedEncodingException: Pippo")
                .hasCause(new UnsupportedEncodingException("Pippo"));
    }

    @Test
    public void testInitialCommit() {
        GitCommit c = createInitialCommit();
        assertThat(g.binaryToHex(g.getSha(c)))
                .isEqualTo("7f66c2dafd0ebbc4a49fc5ba5df6a18b61489faf");
    }

    private GitCommit createInitialCommit() {
        /**
         * tree 132bfb311556de7c60c34ef3d450c9e8bcc6310b
         * author Montoya Edu <montoya.edu@gmail.com> 1496830486 +0200
         * committer Montoya Edu <montoya.edu@gmail.com> 1496830486 +0200
         *
         * Add file.
         *
         */

        GitCommit c = new GitCommit();
        c.setMessage("Add file.");
        c.setCommitter("Montoya Edu <montoya.edu@gmail.com>");
        c.setAuthor("Montoya Edu <montoya.edu@gmail.com>");
        calendar.setTimeInMillis(1496830486);
        c.setAuthoringDate(calendar.getTime());
        c.setCommitDate(calendar.getTime());
        c.setAuthoringTimezone(TIMEZONE);
        c.setCommitTimezone(TIMEZONE);
        c.setTree("132bfb311556de7c60c34ef3d450c9e8bcc6310b");
        return c;
    }

    @Test
    public void testSecondCommit() {
        GitCommit c = createSecondCommit();
        assertThat(g.binaryToHex(g.getSha(c)))
                .isEqualTo("79c3366a520f9766bd4f80071370d6639dcdeadd");
    }

    @Test
    public void testThirdCommit() {
        GitCommit c = createThirdCommit();
        assertThat(g.binaryToHex(g.getSha(c)))
                .isEqualTo("6624283d61c0271418784d69d034ebb0be59ae3d");
    }

    @Test
    public void testFourthCommit() {
        GitCommit c = createFourthCommit();
        assertThat(g.binaryToHex(g.getSha(c)))
                .isEqualTo("67dcda19177750b3047b28182f5dd3e1745cab67");
    }

    @Test
    public void testMergeCommit() {
        GitCommit c = createMergeCommit();
        assertThat(g.binaryToHex(g.getSha(c)))
                .isEqualTo("afeefefbb0a4b91b5744ef77ed8efab8b20382a3");
    }

    private GitCommit createSecondCommit() {
        /**
         * tree a48464603b7d2519a92794f59232caeda2c829f6
         * parent 7f66c2dafd0ebbc4a49fc5ba5df6a18b61489faf
         * author Montoya Edu <montoya.edu@gmail.com> 1496830644 +0200
         * committer Montoya Edu <montoya.edu@gmail.com> 1496830644 +0200
         *
         * Add pluto.
         */
        GitCommit c = new GitCommit();
        c.setMessage("Add pluto.");
        c.setCommitter("Montoya Edu <montoya.edu@gmail.com>");
        c.setAuthor("Montoya Edu <montoya.edu@gmail.com>");
        calendar.setTimeInMillis(1496830644);
        c.setAuthoringDate(calendar.getTime());
        c.setCommitDate(calendar.getTime());
        c.setAuthoringTimezone(TIMEZONE);
        c.setCommitTimezone(TIMEZONE);
        c.getParentCommits().add("7f66c2dafd0ebbc4a49fc5ba5df6a18b61489faf");
        c.setTree("a48464603b7d2519a92794f59232caeda2c829f6");
        return c;
    }

    private GitCommit createThirdCommit() {
        /**
         * git4j cat-file -p 6624283d61c0271418784d69d034ebb0be59ae3d
         * tree 416a593d9cae202df6de8d6fcc15baadad977a1e
         * parent 79c3366a520f9766bd4f80071370d6639dcdeadd
         * author Montoya Edu <montoya.edu@gmail.com> 1496909667 +0200
         * committer Montoya Edu <montoya.edu@gmail.com> 1496909667 +0200
         *
         * Add line.
         */
        GitCommit c = new GitCommit();
        c.setMessage("Add line.");
        c.setCommitter("Montoya Edu <montoya.edu@gmail.com>");
        c.setAuthor("Montoya Edu <montoya.edu@gmail.com>");
        calendar.setTimeInMillis(1496909667);
        c.setAuthoringDate(calendar.getTime());
        c.setCommitDate(calendar.getTime());
        c.setAuthoringTimezone(TIMEZONE);
        c.setCommitTimezone(TIMEZONE);
        c.getParentCommits().add("79c3366a520f9766bd4f80071370d6639dcdeadd");
        c.setTree("416a593d9cae202df6de8d6fcc15baadad977a1e");
        return c;
    }

    private GitCommit createFourthCommit() {
        /**
         * git4j cat-file -p 67dcda19177750b3047b28182f5dd3e1745cab67
         * tree 01a48b0b2927ba0059295d8035fe3457298d5f9c
         * parent 79c3366a520f9766bd4f80071370d6639dcdeadd
         * author Montoya Edu <montoya.edu@gmail.com> 1496909718 +0200
         * committer Montoya Edu <montoya.edu@gmail.com> 1496909718 +0200
         *
         * Add line to pluto.
         */
        GitCommit c = new GitCommit();
        c.setMessage("Add line to pluto.");
        c.setCommitter("Montoya Edu <montoya.edu@gmail.com>");
        c.setAuthor("Montoya Edu <montoya.edu@gmail.com>");
        calendar.setTimeInMillis(1496909718);
        c.setAuthoringDate(calendar.getTime());
        c.setCommitDate(calendar.getTime());
        c.setAuthoringTimezone(TIMEZONE);
        c.setCommitTimezone(TIMEZONE);
        c.getParentCommits().add("79c3366a520f9766bd4f80071370d6639dcdeadd");
        c.setTree("01a48b0b2927ba0059295d8035fe3457298d5f9c");
        return c;
    }

    private GitCommit createMergeCommit() {
        /**
         * git4j cat-file -p afeefefbb0a4b91b5744ef77ed8efab8b20382a3
         * tree bf1d6d1bc9c9ca5295959c67222228c145e174c9
         * parent 67dcda19177750b3047b28182f5dd3e1745cab67
         * parent 6624283d61c0271418784d69d034ebb0be59ae3d
         * author Montoya Edu <montoya.edu@gmail.com> 1496909724 +0200
         * committer Montoya Edu <montoya.edu@gmail.com> 1496909724 +0200
         *
         * Merge branch 'devel'
         */
        GitCommit c = new GitCommit();
        c.setMessage("Merge branch 'devel'");
        c.setCommitter("Montoya Edu <montoya.edu@gmail.com>");
        c.setAuthor("Montoya Edu <montoya.edu@gmail.com>");
        calendar.setTimeInMillis(1496909724);
        c.setAuthoringDate(calendar.getTime());
        c.setCommitDate(calendar.getTime());
        c.setAuthoringTimezone(TIMEZONE);
        c.setCommitTimezone(TIMEZONE);
        c.getParentCommits().add("67dcda19177750b3047b28182f5dd3e1745cab67");
        c.getParentCommits().add("6624283d61c0271418784d69d034ebb0be59ae3d");
        c.setTree("bf1d6d1bc9c9ca5295959c67222228c145e174c9");
        return c;
    }

    @Test
    public void testPseudoCloneAndCheckoutMaster() throws Exception {

        List<GitObject> objects = new ArrayList<>();
        objects.add(new GitBlob("Hello World!\n\n"));
        objects.add(new GitBlob("Hello World!\nHello Developer!\n\n"));
        objects.add(new GitBlob("Hello World!\nHello Master!\n\n"));
        objects.add(createInitialTree());
        objects.add(createInitialCommit());
        objects.add(createSecondTree());
        objects.add(createSecondCommit());
        objects.add(createThirdTree());
        objects.add(createThirdCommit());
        objects.add(createFourthTree());
        objects.add(createFourthCommit());
        objects.add(createMergeTree());
        objects.add(createMergeCommit());

        System.out.println(objects);

        String headCommitSha = "afeefefbb0a4b91b5744ef77ed8efab8b20382a3";

        File folder = Paths.get( "mygit_test").toFile();
        deleteRecursively(folder);
        folder.mkdir();
        try {
            if (!folder.isDirectory()) {
                throw new RuntimeException(folder.getAbsolutePath() + " is not a folder!");
            }

            File dotGitFolder = Paths.get(folder.getAbsolutePath(), ".git").toFile();
            dotGitFolder.mkdir();

            File objectsFolder = Paths.get(dotGitFolder.getAbsolutePath(), "objects").toFile();
            objectsFolder.mkdir();

            for (GitObject object : objects) {
                g.serializeGitObjectToFile(objectsFolder, object);
            }

            File refsFolder = Paths.get(dotGitFolder.getAbsolutePath(), "refs").toFile();
            refsFolder.mkdir();

            File headsFolder = Paths.get(refsFolder.getAbsolutePath(), "heads").toFile();
            headsFolder.mkdir();

            Path HEAD = Paths.get(dotGitFolder.getAbsolutePath(), "HEAD");
            Files.write(HEAD, g.getBytes("ref: refs/heads/master\n"));

            Path MASTER = Paths.get(headsFolder.getAbsolutePath(), "master");
            Files.write(MASTER, g.getBytes(headCommitSha + "\n"));

            g.checkout(createMergeTree(), folder.toPath(), folder.toPath());
        } finally {
            //deleteRecursively(folder);
        }
    }

    private void deleteRecursively(File folder) throws IOException {
        if (!folder.exists()) {
            return;
        }
        Path rootPath = Paths.get(folder.getAbsolutePath());
        Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .peek(System.out::println)
                .forEach(File::delete);
        if (folder.exists()) {
            throw new RuntimeException(folder.getAbsolutePath() + " could not be deleted!");
        }
    }
}
