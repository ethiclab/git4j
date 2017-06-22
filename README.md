# git4j

[![Build Status](https://travis-ci.org/ethiclab/git4j.svg?branch=master)](https://travis-ci.org/ethiclab/git4j)

[![codecov](https://codecov.io/gh/ethiclab/git4j/branch/master/graph/badge.svg)](https://codecov.io/gh/ethiclab/git4j)

git4j is an attempt to learn how git works by implementing some parts of it.

## Create Root Tree

Here we create an initial tree at root leve; with a file named pippo with the content "Hello World!\n\n"

```java
    private GitTreeEntry createInitialTree() {
        List<GitTreeEntry> objects = new ArrayList<>();
        objects.add(createFileEntry("pippo", "Hello World!\n\n"));
        return new GitTreeEntry(objects);
    }
    
    @Test
    public void testTreeWith1Item() {
        assertThat(g.binaryToHex(g.getTreeSha(createInitialTree())))
                .isEqualTo("132bfb311556de7c60c34ef3d450c9e8bcc6310b");
    }
```

## Create Initial Commit

Here we create a commit with a reference to the tree created in the previous example.

```java
        /**
         * tree 132bfb311556de7c60c34ef3d450c9e8bcc6310b
         * author Montoya Edu <montoya.edu@gmail.com> 1496830486 +0200
         * committer Montoya Edu <montoya.edu@gmail.com> 1496830486 +0200
         *
         * Add file.
         *
         */
         
        Git git = new Git();

        GitCommit c = new GitCommit();
        c.setMessage("Add file.");
        c.setCommitter("Montoya Edu <montoya.edu@gmail.com>");
        c.setAuthor("Montoya Edu <montoya.edu@gmail.com>");
        
        Calendar calendar = g.getCalendar("Europe/Rome");
        calendar.setTimeInMillis(1496830486);
        
        c.setAuthoringDate(calendar.getTime());
        c.setCommitDate(calendar.getTime());
        c.setAuthoringTimezone("Europe/Rome");
        c.setCommitTimezone("Europe/Rome");
        c.setTree("132bfb311556de7c60c34ef3d450c9e8bcc6310b");
```

## Further details

Look for file [GitTest.java](src/test/java/it/ethiclab/git4j/GitTest.java)
