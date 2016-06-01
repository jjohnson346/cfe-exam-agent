# set working directory to machine learning directory.
setwd(dir = "/Users/joejohnson/Google Drive/Graduate School/Ph.D/Research/CFE Exam Agent/Code/cfe-exam-agent//machine learning")

# load data from ml input files into data frames.
passages.train = read.table(file="ml.training.5.txt", sep="|", header=TRUE)
passages.test = read.table(file="ml.test.5.txt", sep="|", header=TRUE)

# build logistic regression model for passages based on training set.
glm.fit = glm(icp~dr+nwic+llcs,data=passages.train,family=binomial)
summary(glm.fit)

# make prediction probabilities on test set using lrm from prior step.
glm.probs = predict(object=glm.fit, newdata=passages.test, type="response")

# output first few probabilities for reasonableness.
glm.probs[1:10]


# add prediction probabilities to test set data frame, passages.test.
passages.test$prob = glm.probs

# write passages.test data frame to file.
write.table(passages.test, file="ml.test.6.txt", append=FALSE, sep="|", quote=FALSE)

# make prediction vector (containing 0's and 1's) based on
# prediction probabilities. (see code below the following comments.)

# note: these tables show false negatives when cutoff is set to p > 0.5, but then 
# false positives creep in as the treshold is reduced from 0.5.
# this is an argument for taking the top 3 or 4 passages (according to predicted
# probability) and using all of them as candidate passages in the answer algorithm.

# table 1:  set prediction to 1 for p > 0.5
glm.pred=rep(0,971)
glm.pred[glm.probs>0.5] = 1

# tabulate predictions vs. observed for test set.
icp.test=passages.test$icp

print("table for p>0.5")
table(glm.pred, icp.test)

# table 2:  set prediction to 1 for p > 0.4
glm.pred=rep(0,971)
glm.pred[glm.probs>0.4] = 1
print("table for p>0.4")
table(glm.pred, icp.test)

# table 3:  set prediction to 1 for p > 0.3
glm.pred=rep(0,971)
glm.pred[glm.probs>0.3] = 1
print("table for p>0.3")
table(glm.pred, icp.test)

# table 4:  set prediction to 1 for p > 0.2
glm.pred=rep(0,971)
glm.pred[glm.probs>0.2] = 1
print("table for p>0.2")
table(glm.pred, icp.test)

# table 5:  set prediction to 1 for p > 0.1
glm.pred=rep(0,971)
glm.pred[glm.probs>0.1] = 1
print("table for p>0.1")
table(glm.pred, icp.test)

################################################################################
# code for selecting the top 3 (or 4? or 7 (which appears to be optimum)) passages 
# for each question, based on prediction
# probabilities.  Create a data frame with the top 3 passages for each question
# and then write that data to a file.

passages.best=data.frame(rid=integer(),
                         qid=character(),
                         dr=integer(),
                         nwic=integer(),
                         llcs=integer(),
                         icp=integer(),
                         opt1=character(),
                         opt2=character(),
                         opt3=character(),
                         opt4=character(),
                         pid=character(),
                         passage=character(),
                         prob=double())

qids.unique = unique(passages.test$qid)

for(qid in qids.unique) {
  passages.curr.qid = passages.test[passages.test$qid==qid,]
  passages.curr.qid = arrange(passages.curr.qid, -prob)
  # print(passages.curr.qid)
  passages.best=rbind(passages.best, passages.curr.qid[1:7,])
}

print(passages.best[c("rid","qid","dr","nwic","llcs","icp","prob")])

# print out the number of records for which we have icp (is correct passage) = 1.
# in this case, if we have all of them, the number should be 16.  With a limit 
# on the number of top passages of 7, we get 15 (thus, one is missing).  With
# a limit of 5, on the other hand, we get 13.  See table below.
# 
# limit     number correct passages captured
# -----     --------------------------------
#   9                    16
#   7                    15
#   6                    13 
#   5                    13
#   4                    12
#   3                    12

sum(passages.best$icp)

# write passages.best data frame to file.
write.table(passages.best[c("qid","dr","nwic","llcs","icp","opt1","opt2","opt3","opt4","pid","pass","prob")],
            file="ml.test.7.txt", append=FALSE, sep="|", quote=FALSE)
################################################################################

