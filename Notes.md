# Notes for testing

## Test cases

1) full merge and commit attempt with correct build
2) full merge and commit attempt with broken build
3) no eligible revisions during merge
4) merge with conflicts

## Misc

**CHANGE TO BREAK BUILD**
```
find ejb/src/com/merchant/ejb/SubscriptionBean.java -type f -print0 | xargs -0 sed -i 's/public class/PUBLIC CLASS/g'
svn commit -m "breaking build"
```
Run that command in source branch, commit and perfom full merge (Test case 2).

**CHANGE TO FIX BUILD**
```
find ejb/src/com/merchant/ejb/SubscriptionBean.java -type f -print0 | xargs -0 sed -i 's/PUBLIC CLASS/public class/g'
svn commit -m "fixing build"
```
Run that command in source branch, commit and perfom full merge (Test case 1).

**INTRODUCE CONFLICT**\
An example to introduce conflicts:
Run this in source branch:
```
find ejb/src/com/merchant/ejb/SubscriptionBean.java -type f -print0 | xargs -0 sed -i 's/public class/public clazz AAA/g'
svn commit -m "introducing conflict"
```
then run this in target branch:
```
find ejb/src/com/merchant/ejb/SubscriptionBean.java -type f -print0 | xargs -0 sed -i 's/public class/public clazz BBB/g'
svn commit -m "introducing conflict"
```
Same file is modified in the same line with different content and then committed causing the conflict during merge.

**FIX CONFLICT**\
To revert the conflict run in source branch:
```
find ejb/src/com/merchant/ejb/SubscriptionBean.java -type f -print0 | xargs -0 sed -i 's/public clazz AAA/public class/g'
svn commit -m "removing conflict"
```
and then in target branch:
```
svn revert -R .
find ejb/src/com/merchant/ejb/SubscriptionBean.java -type f -print0 | xargs -0 sed -i 's/public clazz BBB/public class/g'
svn commit -m "removing conflict"
```