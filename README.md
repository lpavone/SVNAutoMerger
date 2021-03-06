# SVNAutoMerger

Tool to merge branches automatically.

## Supported SVN operations

- commit, revert, update, checkout, merge, mergeinfo

## Configuration

Set up this properties in POM file (pom.xml.template must be update, merge properties if needed):

**temp.folder:** directory used to checkout the branches and store merge result.

**tmp.commit.message.file:** path to temp file created with commit message content.

**base.repository.path:** base path of remote SVN repository.

**svn.enable.password.auth:** "true" if SVN authentication is done using user/password. If "false" it's
authenticated using a public key.

**branches.map:** set the branches to be merged.

**enable.commit.mode:** if set to false will perform merge but will not commit changes (simulation mode).

**compiled.css.path:** path to identify pre-compiled CSS files and resolve conflicts automatically only on these files.

**appserver.dir:** path to application server. Used to check build and compile after merge.

## Execution flow

When a merge is performed between a source and target branch, this is the pseudo-code of the process:

```
eligibleRevisions = get this using mergeinfo command;
if (eligibleRevisions is empty){
  notify and abort;
}
checkout or update source branch;
checkout or update target branch;
perform merge of eligible revisions;
if( there were conflicts during merge){
  resolve CSS conflicts;
  if ( still have conflicts){
    notify and abort;
  } else{
    recompile CSS files;
    if (CSS compilation failed){
      notify and abort;
    }
  }
}
if( not successful build){
  notify and abort;
}
if( is commit mode enabled){
  commit changes into target branch;
  if( successful commit){
    log merged revisions;
    notify;
  } else{
    notify;
  }
}
```

## Branches configuration

Branches to be merged are being read from a document with CSV format stored in Team Drive.
The values must a comma separated String indicating source branch, target branch, and the Redmine task number where the changes will be committed.

i.e.: VERSION_4_4_0_0,VERSION_4_5_0_0,9999

Multiple lines in the document will trigger multiple merges.
