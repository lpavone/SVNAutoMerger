# SVNAutoMerger

Tool to merge branches automatically.

## Supported SVN operations

- commit, revert, update, checkout, merge, mergeinfo

## Configuration

Set up this properties in POM file:

**temp.folder:** directory used to checkout the branches.

**tmp.commit.message.file:** path to temp file created with commit message content.

**base.repository.path:** base path of remote SVN repository.

## Execution flow

When a merge is performed between B1 (source branch) and B2 (target branch):

```
eligibleRevisions = get this using mergeinfo command;
if (eligibleRevisions is not empty){
    checkoutOrUpdateTargetBranch;
    mergeEligibleRevisions;
    if( succesfulMerge){
        commitChangesIntoTargetBranch;
        if( succesfulCommit){
            logRevisionsMerged;
        } else{
            notifyDevelopers;
        }
    } else{
        notifyDevelopers;
    }
} else{
    endOfExecution;
}
```

A **succesfulMerge** means there were no conflicts at all.
