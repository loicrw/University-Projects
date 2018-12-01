#determine where files are read and written
setwd("C:/Users/Loic RW/Google Drive/Big Data and Business Analytics/Workshops/Session 2")

#read all the data
library(readr)
test1 <- read_csv("C:/Users/Loic RW/Google Drive/Big Data and Business Analytics/Assignments/Assignment 2/adult.data.csv")

test2 <- read_csv("C:/Users/Loic RW/Google Drive/Big Data and Business Analytics/Assignments/Assignment 2/adult.test.csv")

#clean data
#merge datasets
alltest = rbind(adult_data, adult_test)

#transform the salary into a binomial variable
alltest$target[alltest$salary == "<=50K" | alltest$salary == "<=50K."] <- 0
alltest$target[alltest$salary == ">50K" | alltest$salary == ">50K."] <- 1
alltest$salary <- NULL
alltest$target <- as.factor(alltest$target)

#get rid of unreasonable values
alltest$capitalGain[alltest$capitalGain > 50000] <- NA

summary(as.factor(alldata$workclass))

#scale all int data
alltest$age <- scale(alltest$age)
alltest$fnlwgt <- scale(alltest$fnlwgt)
alltest$capitalGain <- scale(alltest$capitalGain)
alltest$capitalLoss <- scale(alltest$capitalLoss)
alltest$hoursPerWeek <- scale(alltest$hoursPerWeek)

#get rid of variables with few observations
alltest$workclass[alltest$workclass == "Without-pay"] <- NA
alltest$education[alltest$education == "Preschool"] <- NA
alltest$maritalStatus[alltest$maritalStatus == "Married-AF-spouse"] <- NA
alltest$occupation[alltest$occupation == "Armed-Forces"] <- NA


#delete all missing values
alltest <- alltest[complete.cases(alltest),]



# summary(as.factor(train$workclass))
# summary(as.factor(test$workclass))
# 
# summary(as.factor(train$education))
# summary(as.factor(test$education))
# 
# summary(as.factor(train$maritalStatus))
# summary(as.factor(test$maritalStatus))
# 
# summary(as.factor(train$occupation))
# summary(as.factor(test$occupation))
# 
# summary(as.factor(train$relationship))
# summary(as.factor(test$relationship))
# 
# summary(as.factor(train$nativeCountry))
# summary(as.factor(test$nativeCountry))




library("rpart")
library("rpart.plot")
library("caret")
library("e1071")


set.seed(1)

library(caTools)
# 
# sample <- sample.split(alldata$target, SplitRatio = 0.8)
# 
# train = subset(alldata, sample == TRUE)
# test = subset(alldata, sample == FALSE)

library(C50)
library(nnet)
library(e1071)
library(stargazer)
library(caret)
modelA <- target ~ age + workclass + fnlwgt + education + educationNum + maritalStatus + occupation + relationship + race + sex +
  capitalGain + capitalLoss + hoursPerWeek


# rsltLogit <- glm(modelA, data = train, family = "binomial")
# rsltSVM <- svm(modelA, data = train)
# rsltNN <- nnet(modelA, data = train, maxit = 300, size = 10)
# 
# #stargazer(rsltLogit, type = "html", out = "test.doc")
# 
# pdLogit = predict(rsltLogit, test, type = "response")
# pdLogit[pdLogit > 0.5] <- 1
# pdLogit[pdLogit <= 0.5] <- 0
# accLogit = mean(pdLogit == test$target)
# accLogit
# sd(pdLogit)
# 
# 
# pdSVM = predict(rsltSVM, test)
# pdSVM[pdSVM > 0.5] <- 1
# pdSVM[pdSVM <= 0.5] <- 0
# accSVM = mean(pdSVM == test$target)
# accSVM
# sd(pdSVM)
# 
# 
# pdNN = predict(rsltNN, test, type = "raw")
# pdNN[pdNN > 0.5] <- 1
# pdNN[pdNN <= 0.5] <- 0
# pdNN = as.factor(pdNN)
# accNN = mean(pdNN == test$target)
# accNN
# 
# summary(as.factor(test$target))
# summary(as.factor(pdNN))
# 




#cross validation
nFolds = 10
myFolds <- cut(seq(1,nrow(alldata)),
               breaks = nFolds,
               labels = FALSE)

accLogit <- rep(NA, nFolds)
accSVM <- rep(NA, nFolds)
accNN <- rep(NA, nFolds)

for (i in 1:nFolds) {
  cat("Analysis of fold", i, "\n")
  
  #define training and test set
  testIndex <- which(myFolds == i, arr.ind = TRUE)
  print("index done")
  crossTest <- alldata[testIndex, ]
  crossTrain <- alldata[-testIndex, ]
  print("data allocated")
  
  #train the models
  rsltLogit <- glm(modelA, data = crossTrain, family = "binomial")
  print("logit trained")
  rsltSVM <- svm(modelA, data = crossTrain)
  print("svm trained")
  rsltNN <- nnet(modelA, data = crossTrain, maxit = 300, size = 10)
  print("nn trained")
  
  #predict values
  pdLogit = predict(rsltLogit, crossTest, type = "response")
  pdLogit[pdLogit > 0.5] <- 1
  pdLogit[pdLogit <= 0.5] <- 0
  print("logit predicted")
  
  pdSVM = predict(rsltSVM, crossTest)
  pdSVM
  print("svm predicted")
  
  pdNN = predict(rsltNN, crossTest, type = "raw")
  pdNN[pdNN > 0.5] <- 1
  pdNN[pdNN <= 0.5] <- 0
  print("nn predicted")
  
  #measure accuracy
  
  accLogit[i] = mean(pdLogit == crossTest$target)
  accSVM[i] = mean(pdSVM == crossTest$target)
  accNN[i] = mean(pdNN == crossTest$target)
  
  
  accNN[i]
  accLogit
  accSVM
  accNN
}


avgAccLogit = mean(accLogit)
avgAccSVM = mean(accSVM)
avgAccNN = mean(accNN)

sdLogit = sd(accLogit)
sdSVM = sd(accSVM)
sdNN = sd(accNN)



```

#results#

The average for logit was 0.84688
The average for SVM was 0.84798
The average for NN was 0.85168