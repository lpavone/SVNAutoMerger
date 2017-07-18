# SVNAutoMerger

Tool to merge branches automatically.

## Supported SVN operations

- commit, revert, update, checkout, merge, mergeinfo

## Configuration

Set up this properties in POM file:

**temp.folder:** directory used to checkout the branches and store merge result.

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

## Branches configuration

**branches.map** property must be configured in **pom.xml** or directly in already generated **config.properties** file (${project.build.outputDirectory}/config.properties).

The value must a comma separated String indicating source branch, target branch, and the Redmine task number where the changes will be committed.

i.e.: VERSION_4_4_0_0,VERSION_4_5_0_0,9999

This means branch name VERSION_4_4_0_0 will be merged into VERSION_4_5_0_0 and the changes will be commited into Redmine task #9999.

If more than one branch need to be added just separate with ";" and add another entry:

VERSION_4_4_0_0,VERSION_4_5_0_0,9999;VERSION_4_4_0_1,VERSION_4_5_0_0,9999

